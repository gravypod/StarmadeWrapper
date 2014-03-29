package com.gravypod.wrapper.server.commands;

import java.util.ArrayList;

import com.gravypod.wrapper.server.Command;

public class HelpCommand extends Command {
	
	private final ArrayList<String> helpMessages = new ArrayList<String>();
	
	private final String[] GENERIC_STRING_ARRAY = new String[0];
	
	@Override
	public void run(final String user, final String ... args) {
	
		pm(user, helpMessages.toArray(GENERIC_STRING_ARRAY));
	}
	
	@Override
	public void init() {
	
		for (final Command c : getServer().getCommands().values()) {
			final String help = c.getHelp();
			if (help == null) {
				continue;
			}
			helpMessages.add(help);
		}
	}
	
	@Override
	public String getHelp() {
	
		return "!help: Get a list of commands and their usage";
	}
	
}
