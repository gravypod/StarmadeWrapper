package com.gravypod.wrapper;

import com.gravypod.wrapper.server.Server;


public class StarmadeServerThread extends Thread {
	
	private final Server server;
	public StarmadeServerThread(Server server) {
		this.server = server;
	}
	
	@Override
	public void run() {

		
		server.getRunning().set(true);
		while (true) {
			while (server.getRunning().get()) {
				ServerWapper.getLogger().info("Starting server");
				server.getServerConfig().saveServerConfig();
				server.run();
			}
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
