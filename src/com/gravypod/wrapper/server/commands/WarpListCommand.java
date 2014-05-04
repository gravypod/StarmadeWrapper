package com.gravypod.wrapper.server.commands;

import java.util.HashMap;

import com.gravypod.wrapper.server.Command;
import com.gravypod.wrapper.warps.Warp;

public class WarpListCommand extends Command {
	
	@Override
	public void run(String user, String ... args) {
		
		HashMap<String, Warp> warps = new HashMap<String, Warp>(getServer().getWarpList().warps);
		
		for (String w : warps.keySet()) {
			Warp warp = warps.get(warps.get(w));
			
			warps.remove(warp.locationOneString());
			warps.remove(warp.locationTwoString());
			
			pm(user, warps.toString());
			
		}
		
		
	}
	
}
