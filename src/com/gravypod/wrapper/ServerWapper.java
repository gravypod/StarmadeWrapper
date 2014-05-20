package com.gravypod.wrapper;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.gravypod.wrapper.monitors.ConsoleMonitor;
import com.gravypod.wrapper.server.Server;

public class ServerWapper {
	
	private static final Logger logger = Logger.getLogger(ServerWapper.class.getName());
	
	private static StarmadeServerThread serverThread;
	
	public static void main(final String[] args) {
	
		ServerWapper.addLogHandler();
		
		final File directory = ServerWapper.getDirectory();
		
		final Server server = new Server(directory);
		
		final ConsoleMonitor consoleMonitor = new ConsoleMonitor(server);
		consoleMonitor.start();
		
		serverThread = new StarmadeServerThread(server);
		
		ServerWapper.getServerThread().start();
		
	}
	
	private static void addLogHandler() {
	
		try {
			
			final FileHandler logHandler = new FileHandler("wrapper.log");
			final SimpleFormatter formatter = new SimpleFormatter();
			logHandler.setFormatter(formatter);
			ServerWapper.getLogger().addHandler(logHandler);
			
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
	
		return ServerWapper.logger;
	}
	
	public static Thread getServerThread() {
	
		return ServerWapper.serverThread;
	}
	
}
