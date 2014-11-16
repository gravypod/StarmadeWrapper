package com.gravypod.wrapper.monitors;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import com.gravypod.starmadewrapper.plugins.commands.CommandManager;
import com.gravypod.wrapper.ServerWrapper;
import com.gravypod.wrapper.server.Server;

public class ConsoleMonitor extends Thread {
	
	private final Server server;
	private final AtomicReference<CommandManager> commandManager;
	public ConsoleMonitor(final Server server, AtomicReference<CommandManager> commandManager) {
	
		this.server = server;
		this.commandManager = commandManager;
		setName("ConsoleMonitor");
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
	}
	
	@Override
	public void run() {
	
		final Scanner sc = new Scanner(System.in);
		
		while (sc.hasNext()) {
			
			final String line = sc.nextLine().trim();
			
			if (line.startsWith("/")) {
				System.out.println("Executing " + line);
				server.exec(line);
			}
			
			if (line.startsWith("!")) {
				CommandManager cm = commandManager.get();
				
				
				String trimmed = line.substring(1).trim();
				int commandEnd = trimmed.indexOf(' ');
				
				String cmd = trimmed.substring(0, line.indexOf(' ')); // Command.
				String[] args = trimmed.substring(commandEnd).trim().split(" ");
				
				cm.execute("[COMMAND]", cmd, args);
			}
			
			if (line.equalsIgnoreCase("stop")) {
				server.logoutAll();
				System.exit(0);
			}
			
		}
		
		sc.close();
		
		ServerWrapper.getLogger().info("Closing out of " + getClass().getName());
		
	}
	
}
