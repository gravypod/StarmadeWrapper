package com.gravypod.wrapper.server;

public abstract class Command {
	
	private Server server;
	
	public boolean canUseCommand(final String user) {
	
		return true;
	}
	
	public void onLogin(final String username) {
	
	}
	
	public void onLogout(final String username) {
	
	}
	
	public abstract void run(final String user, final String ... args);
	
	protected void setServer(final Server server) {
	
		this.server = server;
	}
	
	public Server getServer() {
	
		return server;
	}
	
	protected void pm(final String user, final String ... message) {
	
		getServer().pm(user, message);
	}
	
	protected void pm(final String user, final String message) {
	
		getServer().pm(user, message);
	}
	
	protected void exec(final String command) {
	
		getServer().exec(command);
	}
	
	protected void tp(final String user, final int x, final int y, final int z) {
	
		getServer().tp(user, x, y, z);
	}
	
	protected void tp(final String sender, final String x, final String y, final String z) {
	
		getServer().tp(sender, x, y, z);
	}
	
	public String getHelp() {
	
		return null;
	}
	
	public void init() {
	
	}
}
