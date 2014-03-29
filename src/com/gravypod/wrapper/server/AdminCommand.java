package com.gravypod.wrapper.server;

public abstract class AdminCommand extends Command {
	
	@Override
	public boolean canUseCommand(final String user) {
	
		return getServer().getFileInfo().getAdmins().contains(user);
	}
	
	@Override
	public void run(final String user, final String ... args) {
	
		if (!canUseCommand(user)) {
			pm(user, "You cannot use this command! You are not an admin");
			return;
		}
		runAdmin(user, args);
	}
	
	public abstract void runAdmin(final String user, final String ... args);
	
}
