package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.server.Command;
import com.gravypod.wrapper.server.User;

public class LocationCommand extends Command {
	
	@Override
	public void run(final String user, final String ... args) {
	
		final User u = getServer().getUser(user);
		pm(user, "You are located within sector " + u.getLocation());
	}
	
	@Override
	public String getHelp() {
	
		return "!location: Returns your current location as known by the server (May be incorrect)";
	}
	
}
