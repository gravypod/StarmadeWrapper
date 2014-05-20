package com.gravypod.wrapper.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
	
	/**
	 * Create a {@link DataSaver} that will load and save data to
	 * <code>directory</code>
	 * 
	 * @param directory
	 */
	public DataSaver(final File directory) {
	
		userDataFolder = new File(directory, "users");
		
		if (!userDataFolder.exists() || !userDataFolder.isDirectory()) {
			userDataFolder.mkdirs();
		}
		
		config = new File(directory, "config.yml");
		
	}
	
	public Config loadConfig() {
	
		Config c = null;
		
		if (!config.exists()) {
			c = new Config();
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
				e.printStackTrace();
				System.out.println("Could not write config, check to see if you have permission for the directory " + new File(".").getAbsolutePath());
			}
		}
		try {
			final YamlReader reader = new YamlReader(new FileReader(config));
			reader.getConfig().setClassTag("Config", Config.class);
			c = reader.read(Config.class);
		} catch (final Exception e) {
			e.printStackTrace();
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
