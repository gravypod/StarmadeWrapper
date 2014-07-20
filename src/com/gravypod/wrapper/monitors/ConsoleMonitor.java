package com.gravypod.wrapper.monitors;

import java.util.Scanner;

import com.gravypod.wrapper.ServerWrapper;
import com.gravypod.wrapper.server.Server;

public class ConsoleMonitor extends Thread {
	
	private final Server server;
	
	public ConsoleMonitor(final Server server) {
	
		this.server = server;
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
			
			if (line.equalsIgnoreCase("stop")) {
				server.logoutAll();
				System.exit(0);
			}
			
		}
		
		sc.close();
		
		ServerWrapper.getLogger().info("Closing out of " + getClass().getName());
		
	}
	
}
