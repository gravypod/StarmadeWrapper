package com.gravypod.wrapper;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import com.gravypod.wrapper.monitors.ConsoleMonitor;
import com.gravypod.wrapper.server.Server;

public class ServerWrapper {
	
	private static final Logger logger = Logger.getLogger(ServerWrapper.class.getName());
	
	
	public static void main(final String[] args) {
	
		ServerWrapper.addLogHandler();
		
		final File directory = ServerWrapper.getDirectory();
		
		final Server server = new Server(directory);
		
		final ConsoleMonitor consoleMonitor = new ConsoleMonitor(server);
		consoleMonitor.start();
		
		server.getRunning().set(true);
		
		while (true) {
			while (server.getRunning().get()) {
				ServerWrapper.getLogger().info("Starting server");
				server.run();
			}
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static void addLogHandler() {
	
		try {
			
			final FileHandler logHandler = new FileHandler("wrapper.log");
			logHandler.setFormatter(new LogFormatter());
			
			ServerWrapper.getLogger().addHandler(logHandler);
			
			for (Handler h : logger.getHandlers()) {
				h.setFormatter(new LogFormatter());
			}
			
		} catch (final Exception e) {
			
			e.printStackTrace();
			
			System.out.println("Could not setup logger");
			System.exit(0);
			
		}
	}
	
	private static File getDirectory() { // Get directory we want to use (This
											// will be used later for multi
											// server hosting)
	
		final File directory = new File("./");
		if (!directory.exists() || !directory.isDirectory()) {
			directory.mkdirs();
		}
		return directory;
	}
	
	public static Logger getLogger() {
	
		return ServerWrapper.logger;
	}
	
}
