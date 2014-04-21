package com.gravypod.wrapper.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.gravypod.wrapper.warps.Warp;
import com.gravypod.wrapper.warps.WarpList;

/**
 * Save and load data
 * 
 * @author gravypod
 */
public class DataSaver {
	
	/**
	 * Files
	 */
	private final File userDataFolder, warpList, config;
	
	/**
	 * Create a {@link DataSaver} that will load and save data to <code>directory</code>
	 * 
	 * @param directory
	 */
	public DataSaver(final File directory) {
	
		userDataFolder = new File(directory, "users");
		
		if (!userDataFolder.exists() || !userDataFolder.isDirectory()) {
			userDataFolder.mkdirs();
		}
		
		warpList = new File(directory, "warps.yml");
		config = new File(directory, "config.yml");
		
	}
	
	public Config loadConfig() {
	
		Config c = null;
		
		if (!config.exists()) {
			c = new Config();
			c.apiKey = "a";
			c.starmadeDirectory = "." + File.separator + "starmade" + File.separator;
			c.launchCommand = "java -Xms512m -Xmx1024m -Xincgc -Xshare:off -jar StarMade.jar -server";
			c.backup = true;
			c.update = true;
			c.donorsTpOthers = false;
			c.usePanel = true;
			c.adminPanelPort = 453;
			c.adminPanelPass = UUID.randomUUID().toString();
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
	
	public void saveWarpList(final WarpList list) {
	
		YamlWriter writer = null;
		try {
			writer = new YamlWriter(new FileWriter(warpList));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		writer.getConfig().setClassTag("WarpList", WarpList.class);
		writer.getConfig().setClassTag("Warp", Warp.class);
		try {
			writer.write(list);
			writer.close();
		} catch (final YamlException e) {
			e.printStackTrace();
		}
	}
	
	public WarpList loadWarpList() {
	
		if (!warpList.exists()) {
			return defaultWarpList();
		}
		
		final WarpList defaultWarpList = new WarpList();
		
		defaultWarpList.warps = new HashMap<String, Warp>();
		defaultWarpList.setSaver(this);
		final WarpList list;
		try {
			final YamlReader reader = new YamlReader(new FileReader(warpList));
			reader.getConfig().setClassTag("WarpList", WarpList.class);
			reader.getConfig().setClassTag("Warp", Warp.class);
			list = reader.read(WarpList.class);
			list.setSaver(this);
		} catch (final Exception e) {
			e.printStackTrace();
			return defaultWarpList();
		}
		
		if (list != null && list.warps != null) {
			defaultWarpList.warps.putAll(list.warps);
		}
		
		return defaultWarpList;
		
	}
	
	private WarpList defaultWarpList() {
	
		final WarpList list = new WarpList();
		list.setSaver(this);
		list.warps = new HashMap<String, Warp>();
		return list;
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
