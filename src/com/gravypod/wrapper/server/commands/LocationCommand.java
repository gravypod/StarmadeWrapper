package com.gravypod.wrapper.server.commands;

import com.gravypod.starmadewrapper.User;
import com.gravypod.starmadewrapper.plugins.commands.Command;

public class LocationCommand extends Command {
	
	@Override
	public void run(String username, User user, String... args) {
	
		pm(username, "You are located within sector " + user.getSector().toString());
	}
	
	@Override
	public String getHelp() {
	
		return "!location: Returns your current location as known by the server (May be incorrect)";
	}
	
}
