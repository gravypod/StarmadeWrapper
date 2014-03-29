package com.gravypod.wrapper.server;

public abstract class DonorCommand extends Command {
	
	@Override
	public boolean canUseCommand(final String user) {
	
		return getServer().getFileInfo().getDonors().contains(user) || getServer().getFileInfo().getAdmins().contains(user);
	}
	
	@Override
	public void run(final String user, final String ... args) {
	
		if (!canUseCommand(user)) {
			pm(user, "You cannot use this command! You are not a donor");
			return;
		}
		runDonor(user, args);
	}
	
	public abstract void runDonor(final String user, final String ... args);
	
}
