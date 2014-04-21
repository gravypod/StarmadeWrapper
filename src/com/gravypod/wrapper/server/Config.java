package com.gravypod.wrapper.server;


public class Config {
	
	/**
	 * The API key for Starmade-servers
	 */
	public String apiKey;
	
	/**
	 * Starmade directory
	 */
	public String starmadeDirectory;
	
	/**
	 * Launch command to start the starmade server
	 */
	public String launchCommand;
	
	/**
	 * Should we update the server?
	 */
	public boolean update;
	
	/**
	 * Should we backup the server on updates?
	 */
	public boolean backup;
	
	public boolean donorsTpOthers;
	
	public int adminPanelPort;
	
	public String adminPanelPass;

	public boolean usePanel;
	
}
