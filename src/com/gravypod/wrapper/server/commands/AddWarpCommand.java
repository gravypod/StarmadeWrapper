package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.server.AdminCommand;

public class AddWarpCommand extends AdminCommand {
	
	@Override
	public void runAdmin(final String user, final String ... args) {
	
		if (args.length != 7) {
			pm(user, "Incorrect arguments used!");
			pm(user, getHelp());
			return;
		}
		
		final String faction = args[0];
		final int sx, sy, sz;
		final int ex, ey, ez;
		
		try {
			
			sx = Integer.parseInt(args[1]);
			sy = Integer.parseInt(args[2]);
			sz = Integer.parseInt(args[3]);
			
			ex = Integer.parseInt(args[4]);
			ey = Integer.parseInt(args[5]);
			ez = Integer.parseInt(args[6]);
			
		} catch (final NumberFormatException e) {
			pm(user, "One of the numbers input was unable tp be parsed! Check your arguments");
			return;
		}
		
		getServer().getWarpList().addWarp(sx, sy, sz, ex, ey, ez, faction);
		
		pm(user, "The warp was added");
		
	}
	
	@Override
	public String getHelp() {
	
		return "!addwarp faction startX startY startZ endX endY endZ: Create a warp between two sectors (Admins only)";
	}
	
}
