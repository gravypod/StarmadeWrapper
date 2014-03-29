package com.gravypod.wrapper.monitors;

import com.gravypod.wrapper.server.Server;

public class ShutdownMonitor extends Thread {
	
	private final Server server;
	
	public ShutdownMonitor(final Server server) {
	
		this.server = server;
	}
	
	@Override
	public void run() {
	
		server.logoutAll();
		server.getStarmade().get().destroy();
	}
}
