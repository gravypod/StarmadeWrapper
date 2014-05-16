package com.gravypod.wrapper.server.commands;

import com.gravypod.starmadewrapper.User;
import com.gravypod.starmadewrapper.plugins.PluginManager;
import com.gravypod.starmadewrapper.plugins.commands.AdminCommand;

public class ReloadCommand extends AdminCommand {
	
	private final PluginManager manager;
	
	public ReloadCommand(PluginManager manager) {
	
		this.manager = manager;
	}
	
	@Override
	public void runAdmin(String username, User user, String... args) {
	
		manager.reloadPlugins();
		pm(username, "Plugins reloaded");
		
	}
	
}
