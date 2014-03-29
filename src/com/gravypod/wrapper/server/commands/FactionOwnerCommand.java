package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.server.AdminCommand;
import com.gravypod.wrapper.server.User;

public class FactionOwnerCommand extends AdminCommand {
	
	@Override
	public void runAdmin(final String user, final String ... args) {
	
		if (args.length != 2) {
			pm(user, "Error, incorrect number of arguments");
			pm(user, getHelp());
			return;
		}
		
		final String username = args[0];
		final String faction = args[1];
		
		final User u = getServer().getUser(username);
		u.add(faction);
		u.leads = faction;
		pm(username, "You are now an owner of " + faction);
		pm(user, "You have added " + username + " as the owner of " + faction);
	}
	
	@Override
	public String getHelp() {
	
		return "!setfactionowner username faction: Set a user as the owner of a faction. Allows them to permission users to use warp gates. (Admin Only)";
	}
	
}
