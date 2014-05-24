package com.gravypod.wrapper.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

/**
 * Save and load data
 * 
 * @author gravypod
 */
public class DataSaver {
	
	/**
	 * Files
	 */
	private final File userDataFolder, config;
	
	private final Logger logger;
	
	/**
	 * Create a {@link DataSaver} that will load and save data to
	 * <code>directory</code>
	 * 
	 * @param logger
	 * 
	 * @param directory
	 */
	public DataSaver(Logger logger, final File directory) {
	
		this.logger = logger;
		userDataFolder = new File(directory, "users");
		
		if (!userDataFolder.exists() || !userDataFolder.isDirectory()) {
			userDataFolder.mkdirs();
		}
		
		config = new File(directory, "config.yml");
		
	}
	
	private Config defaultConfig() {
	
		Config c = new Config();
		c.starmadeDirectory = "." + File.separator + "starmade" + File.separator;
		c.launchCommand = "java -Xms512m -Xmx1024m -Xincgc -Xshare:off -jar StarMade.jar -server";
		c.backup = true;
		c.update = true;
		
		try {
			final YamlWriter writer = new YamlWriter(new FileWriter(config));
			
			writer.getConfig().setClassTag("Config", Config.class);
			writer.write(c);
			writer.close();
		} catch (final IOException e) {
			logger.log(Level.SEVERE, "Could not write config!", e);
		}
		return c;
	}
	
	public Config loadConfig() {
	
		Config c = null;
		
		if (!config.exists()) {
			c = defaultConfig();
		}
		YamlReader reader = null;
		try {
			reader = new YamlReader(new FileReader(config));
			reader.getConfig().setClassTag("Config", Config.class);
			c = reader.read(Config.class);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "Could not load config, backing up old and useing a default config!");
			try {
				if (reader != null) {
					reader.close();
					reader = null;
				}
				Files.move(config.toPath(), new File(config.getParentFile(), "config.yml.broken").toPath());
			} catch (IOException e1) {
				logger.log(Level.SEVERE, "Could not backup config!");
			}
			c = defaultConfig();
		}
		
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return c;
	}
	
	public boolean exists(final String username) {
	
		return new File(userDataFolder, username + ".yml").exists();
	}
	
	public void save(final User u) {
	
		try {
			final YamlWriter writer = new YamlWriter(new FileWriter(new File(userDataFolder, u.name + ".yml")));
			writer.getConfig().setClassTag("User", User.class);
			writer.write(u);
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public User load(final String name) {
	
		if (!exists(name)) {
			return defaultUser(name);
		}
		try {
			final YamlReader reader = new YamlReader(new FileReader(new File(userDataFolder, name + ".yml")));
			reader.getConfig().setClassTag("User", User.class);
			final User u = reader.read(User.class);
			u.factions = new ArrayList<String>(u.getFactions());
			u.name = u.name.toLowerCase();
			return u;
		} catch (final Exception e) {
			return defaultUser(name);
		}
		
	}
	
	private User defaultUser(final String name) {
	
		final User u = new User();
		u.name = name.toLowerCase();
		u.setLocation(2, 2, 2);
		u.factions = new ArrayList<String>();
		u.add("all");
		u.leads = "";
		u.lastVotes = 0;
		return u;
	}
	
}
