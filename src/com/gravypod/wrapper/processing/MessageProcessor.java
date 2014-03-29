package com.gravypod.wrapper.processing;

import java.util.concurrent.BlockingQueue;

import com.gravypod.wrapper.LocationUtils;
import com.gravypod.wrapper.ServerWapper;
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
				
				if (line.startsWith(IdentifierConstants.loginIdentifier)) {
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
				return;
			}
			
		}
		ServerWapper.getLogger().info("Closing out of " + getClass().getName());
	}
	
	private void login(final String line) {
	
		final String newMessage = line.replace(IdentifierConstants.loginIdentifier, "");
		
		final int firstString = newMessage.indexOf(' ');
		
		if (firstString == -1) {
			return;
		}
		
		final String username = newMessage.substring(0, firstString).trim();
		System.out.println("Login " + username);
		server.fireLogin(username);
		
	}
	
	private void logout(String line) {
	
		line = line.replace(IdentifierConstants.logoutMessageIdentifier, "");
		final String user = line.substring(0, line.indexOf(' '));
		final String[] location = LocationUtils.extractLocationString(line);
		try {
			final int x = Integer.parseInt(location[0]);
			final int y = Integer.parseInt(location[1]);
			final int z = Integer.parseInt(location[2]);
			if (server.logoutUser(user, x, y, z)) {
				server.fireLogout(user);
				ServerWapper.getLogger().info("Logging " + user + " out. He is in sector " + x + ", " + y + ", " + z);
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
		final String[] coords = LocationUtils.extractLocationString(line);
		
		final int x = Integer.parseInt(coords[0].trim());
		final int y = Integer.parseInt(coords[1].trim());
		final int z = Integer.parseInt(coords[2].trim());
		
		server.getUser(player).setLocation(x, y, z);
		
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
		
		if (!message.startsWith("!")) {
			return;
		}
		
		int firstSpace = message.indexOf(' ');
		
		if (firstSpace == -1) {
			firstSpace = message.length();
		}
		
		final String command = message.substring(1, firstSpace).trim();
		
		if (!server.getCommands().containsKey(command)) {
			server.pm(user, "The command " + command + " is unknown.");
			return;
		}
		
		final String[] args = message.substring(firstSpace).trim().split(" ");
		
		try {
			server.getCommands().get(command).run(user, args);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
