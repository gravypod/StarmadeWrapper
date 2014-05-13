package com.gravypod.wrapper.server.scripting;

import java.io.File;
import java.io.FileFilter;

import sleep.runtime.ScriptInstance;
import sleep.runtime.ScriptLoader;

import com.gravypod.starmadewrapper.plugins.Plugin;
import com.gravypod.starmadewrapper.plugins.commands.CommandManager;
import com.gravypod.wrapper.SSWPlugin;
import com.gravypod.wrapper.ServerWapper;
import com.gravypod.wrapper.server.Server;

public class ScriptManager {
	
	private final ScriptLoader loader = new ScriptLoader();
	private final Server server;
	private final FileFilter scriptFileFilter = new FileFilter() {
		
		@Override
		public boolean accept(final File pathname) {
		
			return pathname.getName().endsWith(".sl");
		}
	};
	
	public ScriptManager(final Server server) {
		this.server = server;
		loader.addGlobalBridge(new StarmadeScriptBridge(server));
	}
	
	/**
	 * @param commandManager
	 * @param scriptDirectory
	 */
	public void loadScripts(final CommandManager commandManager, final File scriptDirectory) {
	
		if (!scriptDirectory.exists() || !scriptDirectory.isDirectory()) {
			scriptDirectory.mkdirs();
		}

		Plugin plugin = SSWPlugin.PLUGIN;
		for (final File f : scriptDirectory.listFiles(scriptFileFilter)) {
			ServerWapper.getLogger().info("Found the script: " + f.getName() + ". It is being loaded as a command.");
			ScriptCommand command;
			
			final String name = stripExtension(f.getName());
			
			try {
				final ScriptInstance script = loader.loadScript(f);
				
				if (commandManager.isRegistered(name)) {
					commandManager.unregisterCommand(name);
				}
				
				script.runScript();
				command = new ScriptCommand(script);
				server.getPluginManager().getEventManager().registerEvents(plugin, command);
			} catch (final Exception e) {
				continue;
			}
			
			if (command != null) {
				commandManager.registerCommand(name, command);
			}
			
		}
	}
	
	/**
	 * Strip the extension of a file name
	 * 
	 * @param str
	 *            - The file of the string
	 * @return The file name without the extension, "text.txt" changes to "test"
	 */
	private String stripExtension(final String str) {
	
		if (str == null) {
			return null;
		}
		
		final int pos = str.lastIndexOf(".");
		
		if (pos == -1) {
			return str;
		}
		
		return str.substring(0, pos);
	}
}
