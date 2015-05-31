package com.gravypod.wrapper.processing;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gravypod.starmadewrapper.LocationUtils;
import com.gravypod.starmadewrapper.Sector;
import com.gravypod.starmadewrapper.plugins.events.Event;
import com.gravypod.starmadewrapper.plugins.events.Events;
import com.gravypod.starmadewrapper.plugins.events.players.ChatEvent;
import com.gravypod.starmadewrapper.plugins.events.players.EnterShip;
import com.gravypod.starmadewrapper.plugins.events.players.LeaveShip;
import com.gravypod.starmadewrapper.plugins.events.players.LoginEvent;
import com.gravypod.starmadewrapper.plugins.events.players.LogoutEvent;
import com.gravypod.starmadewrapper.plugins.events.players.PlayerKillPlayer;
import com.gravypod.starmadewrapper.plugins.events.players.SectorChangeEvent;
import com.gravypod.starmadewrapper.plugins.events.players.ShipKillPlayer;
import com.gravypod.starmadewrapper.plugins.events.players.ShopBuyEvent;
import com.gravypod.starmadewrapper.plugins.events.players.WisperEvent;
import com.gravypod.wrapper.ServerWrapper;
import com.gravypod.wrapper.server.Server;
import com.gravypod.wrapper.server.User;

public class MessageProcessor extends Thread {
	
	private final BlockingQueue<String> messages;
	private final String[] EMPTY_ARGS = new String[0];
	private final Server server;

	private static final Pattern chatRegex = Pattern.compile("\\[CHANNELROUTER\\] RECEIVED MESSAGE ON Server\\([0-9]*\\): \\[CHAT\\]\\[sender=(?<sender>.*)\\]\\[receiverType=(?<receiverType>.*)\\]\\[receiver=(?<receiver>.*)\\]\\[message=(?<message>.*)\\]");

	private static final String PLAYER_CHARACTER_ID = "ENTITY_PLAYERCHARACTER_";

	public MessageProcessor(final Server server, final BlockingQueue<String> messages) {
	
		this.server = server;
		this.messages = messages;
		setName("MessageProcessor-Thread");
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
	}
	
	@Override
	public void run() {
	
		while (!isInterrupted()) {
			
			try {
				
				final String line = messages.take();
				
				Matcher m = chatRegex.matcher(line);
				if (m.matches()) {
					chat(m);
				}
				
				try {
					Identifier identifier = Identifier.findMatch(line);
					if (identifier != null) {
						identifier.invoke(this, line);
					}
				} catch (Exception e) {
					System.out.println("Error parsing starmade message " + line);
					e.printStackTrace(); // Prevent 1 exception from killing the message processor thread.
					continue;
				}
			} catch (final InterruptedException e) {
				continue;
			}
			
		}
		ServerWrapper.getLogger().info("Closing out of " + getClass().getName());
	}

	public void fullyStarted(String ignored) {
		this.server.getServerConfig().reloadConfig();
	}

	public void playerKillPlayer(String line) {
		String trimmed = line.substring(line.indexOf(":"));
		
		String username = trimmed.substring(trimmed.indexOf("(") + 1, trimmed.indexOf(")"));
		username = username.substring(username.lastIndexOf("_") + 1);
		String killed = trimmed.substring(trimmed.lastIndexOf("[") + 1, trimmed.lastIndexOf(";"));
		Events.fireEvent(new PlayerKillPlayer(killed.trim(), username.trim()));
		
	}

	public void shipKillPlayer(String line) {
		String trimmed = line.substring(line.indexOf(": "));
		String ship = trimmed.substring(trimmed.indexOf("[") + 1, trimmed.indexOf("]")).trim();
		String username = trimmed.substring(trimmed.lastIndexOf("[") + 1, trimmed.lastIndexOf(";")).trim();
		Events.fireEvent(new ShipKillPlayer(ship.trim(), username.trim()));
	}

	public void shipChange(String line) {
		String s = line.replace("[CONTROLLER][ADD-UNIT] (Server(0)): PlS[", "");
		String username = s.substring(0, s.indexOf(";")).trim();
		String part = s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')).trim();
		
		if (part.startsWith("(ENTITY_PLAYERCHARACTER_")) {
			Events.fireEvent(new LeaveShip(username.trim()));
		} else {
			Events.fireEvent(new EnterShip(username.trim(), part.trim()));
		}
		
	}

	public void whisper(String line) {
		
		// [SERVER][CHAT][WISPER] gravypod2: [PM][gravypod1] Hello World
		
		line = line.replace(Identifier.WHISPER.getPattern(), "").trim(); // message start
		
		final int colonIndex = line.indexOf(':'); // find the colon
		
		if (colonIndex == -1) {
			return;
		}
		
		final String receiver = line.substring(0, colonIndex); // Everything before colon
		
		line = line.substring(colonIndex + 1).trim(); // Remove the user from tyhe start of the string.
		
		final int pmStart = line.indexOf("[PM]");
		final int pmEnd = line.indexOf(']', pmStart + 4);
		
		final String user = line.substring(pmStart + 5, pmEnd);

		final String message = line.substring(line.indexOf(']', pmEnd) + 1).trim(); // Everything after colon
		
		final Event event = Events.fireEvent(new WisperEvent(user, receiver, message));
		
		if (event.isCancelled()) {
			return;
		}
		
		if (!message.startsWith("!")) {
			return;
		}
		
		int firstSpace = message.indexOf(' ');
		
		final String command, args[];
		
		if (firstSpace == -1) {
			firstSpace = message.length();
			command = message.substring(1).trim();
			args = EMPTY_ARGS;
		} else {
			command = message.substring(1, firstSpace);
			args = message.substring(firstSpace).trim().split(" ");
		}
		
		if (!server.getCommandManager().isRegistered(command)) {
			server.pm(user, "The command " + command + " is unknown.");
			return;
		}
		
		try {
			server.getCommandManager().execute(user, command, args);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
	}

	public void login(final String line) {
	
		final String newMessage = line.replace(Identifier.LOGIN.getPattern(), "");
		
		final int firstString = newMessage.indexOf(' ');
		
		if (firstString == -1) {
			return;
		}
		
		final String username = newMessage.substring(0, firstString).trim();
		
		final Event e = Events.fireEvent(new LoginEvent(username));

		if (e.isCancelled()) {
			server.exec("/kick " + username);
		}
		
	}

	public void logout(String line) {
	
		line = line.replace(Identifier.LOGOUT.getPattern(), "");
		final String user = line.substring(0, line.indexOf(' '));
		final Sector location = LocationUtils.sectorFromString(line);
		try {
			if (server.logoutUser(user, location.getX(), location.getY(), location.getZ())) {
				Events.fireEvent(new LogoutEvent(user));
				ServerWrapper.getLogger().info("Logging " + user + " out. He is in sector (" + location + ") because " + line);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void move(final String line) {
		
		final Sector[] movement = LocationUtils.sectorsFromString(line);
		
		if (movement.length != 2) {
			return;
		}
		
		final int playerCharLoc = line.indexOf(PLAYER_CHARACTER_ID);
		final int playerCharEnd = playerCharLoc + PLAYER_CHARACTER_ID.length();
		final String player = line.substring(playerCharEnd, line.indexOf(')', playerCharEnd));
		
		final Sector from = movement[0];
		final Sector to = movement[1];
		
		User user = server.getUser(player);
		
		if (user.getSector().equals(to)) {
			return;
		}
		
		final Event event = Events.fireEvent(new SectorChangeEvent(player, from, to));
		
		if (event.isCancelled()) {
			user.setLocation(from);
			return;
		}
		
		user.setLocation(to);
		
	}

	public void chat(Matcher matcher) {
	
		
		final String user = matcher.group("sender"); // Everything before colon
		final String message = matcher.group("message"); // Everything after colon
		
		final Event event = Events.fireEvent(new ChatEvent(user, message));
		if (event.isCancelled()) {
			return;
		}
		
		if (!message.startsWith("!")) {
			return;
		}
		
		int firstSpace = message.indexOf(' ');
		
		final String command, args[];
		
		if (firstSpace == -1) {
			firstSpace = message.length();
			command = message.substring(1).trim();
			args = EMPTY_ARGS;
		} else {
			command = message.substring(1, firstSpace);
			args = message.substring(firstSpace).trim().split(" ");
		}
		
		if (!server.getCommandManager().isRegistered(command)) {
			server.pm(user, "The command " + command + " is unknown.");
			return;
		}
		
		try {
			server.getCommandManager().execute(user, command, args);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
	}

	public void shopBuy(String line) {

		line = line.replace(Identifier.SHOP_BUY.getPattern(), "");

		final int ofIndex = line.indexOf(" of ");
		final int quantity = Integer.valueOf(line.substring(0, ofIndex));
		line = line.substring(ofIndex + 4);

		final int forIndex = line.indexOf(" for ");
		final String item = line.substring(0, forIndex);
		line = line.substring(forIndex + 5 + 4);

		final int semiColonIndex = line.indexOf(" ; ");
		final User player = server.getUser(line.substring(0, semiColonIndex));

		Events.fireEvent(new ShopBuyEvent(quantity, item, player));
	}
	
}
