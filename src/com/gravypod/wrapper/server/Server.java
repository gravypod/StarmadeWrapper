package com.gravypod.wrapper.server;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.gravypod.starmadewrapper.plugins.PluginManager;
import com.gravypod.starmadewrapper.plugins.commands.CommandManager;
import com.gravypod.wrapper.VersionManager;
import com.gravypod.wrapper.monitors.ShutdownMonitor;
import com.gravypod.wrapper.processing.MessageProcessor;
import com.gravypod.wrapper.server.commands.HelpCommand;
import com.gravypod.wrapper.server.commands.LocationCommand;
import com.gravypod.wrapper.server.scripting.ScriptManager;

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
	
	private final ScriptManager scriptManager = new ScriptManager(this);
	
	private final CommandManager commandManager = new CommandManager(this);
	
	private final PluginManager pluginManager = new PluginManager(logger, this, commandManager);
	
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
	
		registerCommands();
		pluginManager.loadPlugins();
		consoleData.run();
		pluginManager.disablePlugins();
		
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
		
		scriptManager.loadScripts(getCommandManager(), new File(getDataFolder(), "scripts"));
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

    public ServerConfig getServerConfig() {

        return serverConfig;
    }

    public int getMaxClients() {

        return serverConfig.getInt(ServerConfig.ConfigItem.MAX_CLIENTS);
    }

    public int getThrustSpeedLimit() {

        return serverConfig.getInt(ServerConfig.ConfigItem.THRUST_SPEED_LIMIT);
    }

    public int getStartingCredits() {

        return serverConfig.getInt(ServerConfig.ConfigItem.STARTING_CREDITS);
    }

    public long getUniverseDay() {

        return serverConfig.getLong(ServerConfig.ConfigItem.UNIVERSE_DAY_IN_MS);
    }

    public boolean hasWhitelist() {

        return serverConfig.getBoolean(ServerConfig.ConfigItem.USE_WHITELIST);
    }

    public boolean hasEnemySpawning() {

        return serverConfig.getBoolean(ServerConfig.ConfigItem.ENEMY_SPAWNING);
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
	
	public CommandManager getCommandManager() {
	
		return commandManager;
	}
	
	public PluginManager getPluginManager() {
	
		return pluginManager;
	}
	
	@Override
	public List<String> getDonors() {
	
		return getFileInfo().getDonors();
	}
	
	@Override
	public List<String> getAdmins() {
	
		return getFileInfo().getAdmins();
	}
	
	@Override
	public void exec(String command) {
	
		consoleData.exec(command);
	}
	
	@Override
	public void tp(String username, int x, int y, int z) {
	
		consoleData.tp(username, x, y, z);
	}
	
	@Override
	public void tp(String username, String x, String y, String z) {
	
		consoleData.tp(username, x, y, z);
	}
	
	@Override
	public void pm(String user, String... message) {
	
		consoleData.pm(user, message);
		
	}
	
	@Override
	public void give(String user, int item, int amount) {
	
		consoleData.give(user, item, amount);
		
	}
	
	@Override
	public void pm(String username, String message) {
	
		consoleData.pm(username, message);
		
	}
	
	public ConsoleManager getConsoleData() {
	
		return consoleData;
	}
	
	@Override
	public void stopServer() {
		getRunning().set(false);
		exec("/shutdown 0");
	}
	
	@Override
	public void restart(int time) {
	
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
}
