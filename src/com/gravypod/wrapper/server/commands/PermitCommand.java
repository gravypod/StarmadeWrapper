package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.server.Command;
import com.gravypod.wrapper.server.User;

public class PermitCommand extends Command {
	
	@Override
	public void run(final String user, final String ... args) {
	
		if (args.length != 1) {
			pm(user, "Error, incorrect number of arguments!");
			pm(user, getHelp());
			return;
		}
		
		final User u = getServer().getUser(user);
		
		if (u.leads == null || u.leads.trim().isEmpty()) {
			pm(user, "You are not the owner of a faciton!");
			return;
		}
		
		final User otherUser = getServer().getUser(args[0]);
		
		otherUser.add(u.leads);
		
		pm(args[0], "You can now use warp gates owned by " + u.leads);
		
		pm(user, "You have allowed " + args[0] + " to use your warp gates");
	}
	
	@Override
	public String getHelp() {
	
		return "!permit username: Permit a user to use warp gates from your faction";
	}
	
}
