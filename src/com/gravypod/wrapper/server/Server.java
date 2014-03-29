package com.gravypod.wrapper.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import com.gravypod.wrapper.VersionManager;
import com.gravypod.wrapper.monitors.ShutdownMonitor;
import com.gravypod.wrapper.processing.MessageProcessor;
import com.gravypod.wrapper.server.commands.AddWarpCommand;
import com.gravypod.wrapper.server.commands.ClaimCommand;
import com.gravypod.wrapper.server.commands.DelWarpCommand;
import com.gravypod.wrapper.server.commands.FactionOwnerCommand;
import com.gravypod.wrapper.server.commands.HelpCommand;
import com.gravypod.wrapper.server.commands.LocationCommand;
import com.gravypod.wrapper.server.commands.PermitCommand;
import com.gravypod.wrapper.server.commands.ReloadCommand;
import com.gravypod.wrapper.server.commands.StuckCommand;
import com.gravypod.wrapper.server.commands.TpCommand;
import com.gravypod.wrapper.server.commands.WarpCommand;
import com.gravypod.wrapper.server.scripting.ScriptManager;
import com.gravypod.wrapper.warps.WarpList;

public class Server implements Runnable {
	
	private PrintWriter writer;
	
	private final Map<String, User> users;
	
	private final Map<String, Command> commands = new HashMap<String, Command>();
	
	private final File dataFolder;
	
	private final WarpList warpList;
	
	private final Config config;
	
	private final DataSaver dataSaver;
	
	private final FileInfo fileInfo;
	
	private final ProcessBuilder builder;
	
	private static final BlockingQueue<String> messages = new PriorityBlockingQueue<String>();
	
	private final AtomicReference<Process> starmade = new AtomicReference<Process>();
	
	private final MessageProcessor messageProcessor = new MessageProcessor(this, Server.messages);
	
	private final ScriptManager scriptManager = new ScriptManager(this);
	
	private final ReentrantLock lock = new ReentrantLock();
	
	public Server(final File directory) {
	
		dataFolder = directory; // Set data folder
		
		if (!dataFolder.exists() || !dataFolder.isDirectory()) {
			dataFolder.mkdirs();// TODO: Check if it worked
		}
		
		final DataSaver dataSaver = new DataSaver(directory);
		
		addShutdownHooks();
		
		users = new ConcurrentHashMap<String, User>(); // initialize user map
		
		config = dataSaver.loadConfig();
		
		final FileInfo fileInfo = new FileInfo(directory, getStarmadeDirectory());
		
		warpList = dataSaver.loadWarpList();
		
		this.fileInfo = fileInfo;
		
		this.dataSaver = dataSaver;
		
		builder = creatBuilder();
		
		try {
			final File starmadeDirectory = getStarmadeDirectory();
			final VersionManager versionManager = new VersionManager(starmadeDirectory);
			if (config.update && versionManager.needsUpdate()) {
				versionManager.downloadUpdate(config.backup);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		messageProcessor.start();
		
	}
	
	@Override
	public void run() {
	
		try {
			
			registerCommands();
			
			starmade.set(builder.start());
			
			setWriter(starmade.get().getOutputStream());
			
			final Scanner sc = new Scanner(starmade.get().getInputStream());
			
			while (sc.hasNextLine()) {
				final String line = sc.nextLine();
				Server.messages.add(line);
			}
			sc.close();
			
			try {
				starmade.get().waitFor(); // Wait for the server to exit
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			starmade.get().destroy();
			
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void exec(final String command) {
	
		lock.lock();
		writer.println(command);
		writer.flush();
		lock.unlock();
	}
	
	public synchronized void masExec(final String ... commands) {
	
		lock.lock();
		for (final String s : commands) {
			writer.println(s);
		}
		writer.flush();
		lock.unlock();
	}
	
	/**
	 * Warning, will alter array that was input
	 * 
	 * @param user
	 * @param messages
	 */
	public void pm(final String user, final String ... messages) {
	
		final String[] ms = new String[messages.length];
		for (int i = 0; i < messages.length; i++) {
			ms[i] = "/pm " + user + " " + messages[i];
		}
		
		masExec(ms);
	}
	
	public synchronized void pm(final String user, final String message) {
	
		exec("/pm " + user + " " + message);
	}
	
	public synchronized void tp(final String user, final int x, final int y, final int z) {
	
		exec("/change_sector_for " + user + " " + x + " " + y + " " + z);
	}
	
	public void tp(final String sender, final String x, final String y, final String z) {
	
		exec("/change_sector_for " + sender + " " + x + " " + y + " " + z);
	}
	
	public synchronized void give(final String user, final int item, final int amount) {
	
		exec("/giveid " + user + " " + item + " " + amount);
	}
	
	public synchronized PrintWriter getWriter() {
	
		return writer;
	}
	
	public synchronized void setWriter(final OutputStream os) {
	
		writer = new PrintWriter(os);
	}
	
	public synchronized Map<String, Command> getCommands() {
	
		return commands;
	}
	
	public synchronized boolean logoutUser(final String name, final int x, final int y, final int z) {
	
		if (!users.containsKey(name)) {
			return false;
		}
		
		final User u = users.remove(name);
		
		u.setLocation(x, y, z);
		
		dataSaver.save(u);
		
		return true;
		
	}
	
	public synchronized User getUser(final String name) {
	
		if (!users.containsKey(name)) {
			users.put(name, dataSaver.load(name));
		}
		
		return users.get(name);
		
	}
	
	public Map<String, User> getUsers() {
	
		return users;
	}
	
	public WarpList getWarpList() {
	
		return warpList;
	}
	
	public void logoutAll() {
	
		for (final User user : users.values()) {
			logoutUser(user.name, user.x, user.y, user.z);
		}
	}
	
	public File getDataFolder() {
	
		return dataFolder;
	}
	
	public void registerCommands() {
	
		commands.clear();
		
		commands.put("help", new HelpCommand());
		commands.put("location", new LocationCommand());
		commands.put("warp", new WarpCommand());
		commands.put("addwarp", new AddWarpCommand());
		commands.put("delwarp", new DelWarpCommand());
		commands.put("setfactionowner", new FactionOwnerCommand());
		commands.put("permit", new PermitCommand());
		commands.put("stuck", new StuckCommand());
		commands.put("tp", new TpCommand());
		commands.put("claim", new ClaimCommand());
		commands.put("reload", new ReloadCommand());
		
		scriptManager.loadScripts(commands, new File(getDataFolder(), "scripts"));
		
		for (final Command c : commands.values()) {
			c.setServer(this);
		}
		
		for (final Command c : commands.values()) {
			c.init();
		}
		
	}
	
	public Config getConfig() {
	
		return config;
	}
	
	public File getStarmadeDirectory() {
	
		return new File(dataFolder, config.starmadeDirectory);
	}
	
	public DataSaver getDataSaver() {
	
		return dataSaver;
	}
	
	public FileInfo getFileInfo() {
	
		return fileInfo;
	}
	
	private ProcessBuilder creatBuilder() {
	
		final ProcessBuilder builder = new ProcessBuilder(config.launchCommand.split(" "));
		builder.redirectErrorStream(true);
		builder.directory(getStarmadeDirectory());
		return builder;
	}
	
	private void addShutdownHooks() {
	
		Runtime.getRuntime().addShutdownHook(new ShutdownMonitor(this));
	}
	
	public void fireLogout(final String user) {
	
		for (final Command c : commands.values()) {
			c.onLogout(user);
		}
	}
	
	public void fireLogin(final String username) {
	
		for (final Command c : commands.values()) {
			c.onLogin(username);
		}
	}
	
	public AtomicReference<Process> getStarmade() {
	
		return starmade;
	}
	
}
