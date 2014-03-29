package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.server.AdminCommand;

public class ReloadCommand extends AdminCommand {
	
	@Override
	public void runAdmin(final String user, final String ... args) {
	
		getServer().registerCommands();
		pm(user, "All known commands and scripts have been reloaded.");
	}
}
