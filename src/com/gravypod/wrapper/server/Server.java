package com.gravypod.wrapper.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gravypod.starmadewrapper.Material;
import com.gravypod.starmadewrapper.plugins.PluginManager;
import com.gravypod.starmadewrapper.plugins.commands.CommandManager;
import com.gravypod.wrapper.VersionManager;
import com.gravypod.wrapper.monitors.ShutdownMonitor;
import com.gravypod.wrapper.processing.MessageProcessor;
import com.gravypod.wrapper.server.commands.HelpCommand;
import com.gravypod.wrapper.server.commands.LocationCommand;
import com.gravypod.wrapper.server.commands.ReloadCommand;

public class Server implements Runnable, com.gravypod.starmadewrapper.Server {
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private final Map<String, User> users;
	
	private final File dataFolder;
	
	private final AtomicBoolean running = new AtomicBoolean(true);
	
	private final Config config;
	
	private final DataSaver dataSaver;
	
	private final ServerConfig serverConfig;
	
	private final FileInfo fileInfo;
	
	private final BlockingQueue<String> messages = new PriorityBlockingQueue<String>();
	
	private final MessageProcessor messageProcessor = new MessageProcessor(this, messages);
	
	private final ConsoleManager consoleData;
	
	private final CommandManager commandManager = new CommandManager(this);
	
	private final PluginManager pluginManager = new PluginManager(logger, this, commandManager);
	
	private ScheduledExecutorService schedulePool;
	private ExecutorService threadPool;
	
	public Server(final File directory) {
	
		dataFolder = directory; // Set data folder
		
		if (!dataFolder.exists() || !dataFolder.isDirectory()) {
			dataFolder.mkdirs();// TODO: Check if it worked
		}
		
		final DataSaver dataSaver = new DataSaver(logger, directory);
		
		addShutdownHooks();
		
		users = new ConcurrentHashMap<String, User>(); // initialize user map
		
		config = dataSaver.loadConfig();
		
		final FileInfo fileInfo = new FileInfo(directory, getStarmadeDirectory(), Executors.newScheduledThreadPool(1));
		
		this.fileInfo = fileInfo;
		
		this.dataSaver = dataSaver;
		
		serverConfig = new ServerConfig(this);
		
		final File starmadeDirectory = getStarmadeDirectory();
		
		consoleData = new ConsoleManager(config, starmadeDirectory, messages);
		try {
			
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
			Material.load(new File(getStarmadeDirectory(), "data" + File.separator + "config" + File.separator + "BlockTypes.properties"));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Could not find starmade's BlockTypes.properties. No block data will be usabel by plugins", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not read BlockTypes.properties. No block data will be usabel by plugins", e);
		}
		
		
		schedulePool = Executors.newScheduledThreadPool(6);
		threadPool = Executors.newCachedThreadPool();
		
		registerCommands();
		pluginManager.loadPlugins();
		consoleData.run();
		pluginManager.disablePlugins();
		getThreadPool().shutdownNow();
		getSchedulerPool().shutdownNow();
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
	
	@Override
	public synchronized User getUser(final String name) {
	
		if (!users.containsKey(name)) {
			users.put(name, dataSaver.load(name));
		}
		
		return users.get(name);
		
	}
	
	public Map<String, User> getUsers() {
	
		return users;
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
	
		getCommandManager().registerCommand("help", new HelpCommand());
		getCommandManager().registerCommand("location", new LocationCommand());
		getCommandManager().registerCommand("reload", new ReloadCommand(getPluginManager()));
		
	}
	
	public Config getConfig() {
	
		return config;
	}
	
	@Override
	public File getStarmadeDirectory() {
	
		return new File(dataFolder, config.starmadeDirectory);
	}
	
	public DataSaver getDataSaver() {
	
		return dataSaver;
	}
	
	public ServerConfig getServerConfig() {
	
		return serverConfig;
	}
	
	@Override
	public int getMaxClients() {
	
		return serverConfig.getInt(ServerConfig.ConfigItem.MAX_CLIENTS);
	}
	
	@Override
	public int getThrustSpeedLimit() {
	
		return serverConfig.getInt(ServerConfig.ConfigItem.THRUST_SPEED_LIMIT);
	}
	
	@Override
	public int getStartingCredits() {
	
		return serverConfig.getInt(ServerConfig.ConfigItem.STARTING_CREDITS);
	}
	
	@Override
	public long getUniverseDay() {
	
		return serverConfig.getLong(ServerConfig.ConfigItem.UNIVERSE_DAY_IN_MS);
	}
	
	@Override
	public boolean hasWhitelist() {
	
		return serverConfig.getBoolean(ServerConfig.ConfigItem.USE_WHITELIST);
	}
	
	@Override
	public boolean hasEnemySpawning() {
	
		return serverConfig.getBoolean(ServerConfig.ConfigItem.ENEMY_SPAWNING);
	}

    @Override
    public boolean useStarMadeAuth() {

        return serverConfig.getBoolean(ServerConfig.ConfigItem.USE_STARMADE_AUTHENTICATION);
    }

    public FileInfo getFileInfo() {
	
		return fileInfo;
	}
	
	private void addShutdownHooks() {
	
		Runtime.getRuntime().addShutdownHook(new ShutdownMonitor(this));
	}
	
	public AtomicBoolean getRunning() {
	
		return running;
	}
	
	@Override
	public CommandManager getCommandManager() {
	
		return commandManager;
	}
	
	public PluginManager getPluginManager() {
	
		return pluginManager;
	}
	
	@Override
	public boolean isDonor(String username) {
	
		return getFileInfo().isDonor(username);
	}
	
	@Override
	public boolean isAdmin(String username) {
	
		return getFileInfo().isAdmin(username);
	}
	
	@Override
	public void exec(final String command) {
	
		consoleData.exec(command);
	}
	
	@Override
	public void tp(final String username, final int x, final int y, final int z) {
	
		consoleData.tp(username, x, y, z);
	}
	
	@Override
	public void tp(final String username, final String x, final String y, final String z) {
	
		consoleData.tp(username, x, y, z);
	}
	
	@Override
	public void pm(final String user, final String... message) {
	
		consoleData.pm(user, message);
		
	}
	
	@Override
	public void give(final String user, final int item, final int amount) {
	
		consoleData.give(user, item, amount);
		
	}
	
	@Override
	public void pm(final String username, final String message) {
	
		consoleData.pm(username, message);
		
	}
	
	@Override
	public void ban(final String user) {
	
		consoleData.ban(user);
	}
	
	public ConsoleManager getConsoleData() {
	
		return consoleData;
	}

    @Override
    public void setWhitelist(boolean whitelist) {

        consoleData.setWhitelist(whitelist);
    }

    @Override
    public void whitelist(String whitelist) {

        consoleData.whitelist(whitelist, useStarMadeAuth());
    }

    @Override
	public void stopServer() {
	
		getRunning().set(false);
		exec("/shutdown 0");
	}
	
	@Override
	public void restart(final int time) {
	
		getRunning().set(true);
		exec("/shutdown 60");
	}
	
	@Override
	public void startServer() {
	
		if (!isRunning()) {
			getRunning().set(true);
		}
	}
	
	@Override
	public boolean isRunning() {
	
		return getRunning().get();
	}
	
	@Override
	public Logger getLogger() {
	
		return logger;
	}
	
	
	@Override
	public ExecutorService getThreadPool() {
		return threadPool;
	}
	
	@Override
	public ScheduledExecutorService getSchedulerPool() {
		return schedulePool;
	}
	
	
}
