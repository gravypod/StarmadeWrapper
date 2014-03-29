package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.server.Command;

public class StuckCommand extends Command {
	
	@Override
	public void run(final String user, final String ... args) {
	
		tp(user, 2, 2, 2);
		pm(user, "You are now unstuck!");
	}
	
}
