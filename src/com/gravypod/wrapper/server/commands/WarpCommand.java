package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.server.Command;
import com.gravypod.wrapper.server.User;
import com.gravypod.wrapper.warps.Warp;
import com.gravypod.wrapper.warps.WarpList;

public class WarpCommand extends Command {
	
	@Override
	public void run(final String user, final String ... args) {
	
		final WarpList list = getServer().getWarpList();
		final User u = getServer().getUser(user);
		final Warp warp = list.getWarp(u.x, u.y, u.z);
		
		if (warp == null) {
			pm(user, "You are out of range of a warp station!");
			return;
		}
		
		if (!u.getFactions().contains(warp.faction)) {
			pm(user, "You do not have permission to use this warp station!");
			return;
		}
		
		if (u.x == warp.locationOneX && u.y == warp.locationOneY && u.z == warp.locationOneZ) {
			tp(user, warp.locationTwoX, warp.locationTwoY, warp.locationTwoZ);
		} else {
			tp(user, warp.locationOneX, warp.locationOneY, warp.locationOneZ);
		}
		
		pm(user, "You have been teleported to your destination. Have a nice day!");
		
	}
	
	@Override
	public String getHelp() {
	
		return "!warp: Teleport to the other endpoint of the warp station you are near.";
	}
	
}
