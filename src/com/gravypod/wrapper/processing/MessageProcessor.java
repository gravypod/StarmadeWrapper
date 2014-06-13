package com.gravypod.wrapper.processing;

import java.util.concurrent.BlockingQueue;

import com.gravypod.starmadewrapper.Sector;
import com.gravypod.starmadewrapper.plugins.events.Event;
import com.gravypod.starmadewrapper.plugins.events.Events;
import com.gravypod.starmadewrapper.plugins.events.players.ChatEvent;
import com.gravypod.starmadewrapper.plugins.events.players.LoginEvent;
import com.gravypod.starmadewrapper.plugins.events.players.LogoutEvent;
import com.gravypod.starmadewrapper.plugins.events.players.SectorChangeEvent;
import com.gravypod.wrapper.LocationUtils;
import com.gravypod.wrapper.ServerWrapper;
import com.gravypod.wrapper.server.Server;

public class MessageProcessor extends Thread {
	
	private final BlockingQueue<String> messages;
	
	private final Server server;
	
	public MessageProcessor(final Server server, final BlockingQueue<String> messages) {
	
		this.server = server;
		this.messages = messages;
		setName("MessagePrwocessor-Thread");
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
	}
	
	@Override
	public void run() {
	
		while (!isInterrupted()) {
			
			try {
				
				final String line = messages.take();
				if (line.startsWith(IdentifierConstants.fullyStarted)) {
					server.getServerConfig().reloadConfig(); // Reload Config once StarMade has updated it for use.
				} else if (line.startsWith(IdentifierConstants.loginIdentifier)) {
					login(line);
					continue;
				} else if (line.contains(IdentifierConstants.movementIdentifier)) {
					move(line);
					continue;
				} else if (line.contains(IdentifierConstants.logoutMessageIdentifier)) {
					logout(line);
					continue;
				} else if (line.startsWith(IdentifierConstants.chatMessageIdentifier)) {
					chat(line);
					continue;
				}
				
			} catch (final InterruptedException e) {
				continue;
			}
			
		}
		ServerWrapper.getLogger().info("Closing out of " + getClass().getName());
	}
	
	private void login(final String line) {
	
		final String newMessage = line.replace(IdentifierConstants.loginIdentifier, "");
		
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
	
	private void logout(String line) {
	
		line = line.replace(IdentifierConstants.logoutMessageIdentifier, "");
		final String user = line.substring(0, line.indexOf(' '));
		final Sector location = LocationUtils.sectorFromString(line);
		try {
			if (server.logoutUser(user, location.getX(), location.getY(), location.getZ())) {
				Events.fireEvent(new LogoutEvent(user));
				ServerWrapper.getLogger().info("Logging " + user + " out. He is in sector (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")");
			}
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void move(final String line) {
	
		final int playerCharLoc = line.indexOf(IdentifierConstants.playerCharacterID);
		final int playerCharEnd = playerCharLoc + IdentifierConstants.playerCharacterID.length();
		final String player = line.substring(playerCharEnd, line.indexOf(')', playerCharEnd));
		final Sector from = LocationUtils.sectorsFromString(line)[0];
        final Sector to = LocationUtils.sectorsFromString(line)[1];

        final Event event = Events.fireEvent(new SectorChangeEvent(player, from, to));

        if (event.isCancelled()) {
            server.getUser(player).setLocation(from);
            return;
        }

		server.getUser(player).setLocation(to);
		
	}
	
	private void chat(String line) {
	
		line = line.replace(IdentifierConstants.chatMessageIdentifier, "").trim(); // Remove
																					// the
																					// chat
																					// ID
		final int colonIndex = line.indexOf(':'); // find the colon
		
		if (colonIndex == -1) {
			return;
		}
		
		final String user = line.substring(0, colonIndex); // Everything before
															// colon
		final String message = line.substring(colonIndex + 1).trim(); // Everything
																		// after
																		// colon
		
		final Event event = Events.fireEvent(new ChatEvent(user, message));
		if (event.isCancelled()) {
			return;
		}
		
		if (!message.startsWith("!")) {
			return;
		}
		
		int firstSpace = message.indexOf(' ');
		
		if (firstSpace == -1) {
			firstSpace = message.length();
		}
		
		final String command = message.substring(1, firstSpace).trim();
		
		if (!server.getCommandManager().isRegistered(command)) {
			server.pm(user, "The command " + command + " is unknown.");
			return;
		}
		
		final String[] args = message.substring(firstSpace).trim().split(" ");
		
		try {
			server.getCommandManager().execute(user, command, args);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
