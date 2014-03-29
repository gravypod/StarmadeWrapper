package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.LocationUtils;
import com.gravypod.wrapper.server.AdminCommand;

public class DelWarpCommand extends AdminCommand {
	
	@Override
	public void runAdmin(final String user, final String ... args) {
	
		if (args.length != 3) {
			pm(user, "Incorrect arguments used!");
			pm(user, getHelp());
			return;
		}
		
		final int sx, sy, sz;
		
		try {
			
			sx = Integer.parseInt(args[0]);
			sy = Integer.parseInt(args[1]);
			sz = Integer.parseInt(args[2]);
			
		} catch (final NumberFormatException e) {
			pm(user, "One of the numbers input was unable tp be parsed! Check your arguments");
			return;
		}
		
		getServer().getWarpList().removeWarp(LocationUtils.locationToString(sx, sy, sz));
		
		pm(user, "The warp was removed");
		
	}
	
	@Override
	public String getHelp() {
	
		return "!delwarp x y z: Delete the warp, and both endpoints, located at the coordinates specified (Admins only)";
	}
	
}
