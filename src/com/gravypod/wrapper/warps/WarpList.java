package com.gravypod.wrapper.warps;

import java.util.HashMap;

import com.gravypod.wrapper.LocationUtils;
import com.gravypod.wrapper.server.DataSaver;

public class WarpList {
	
	public HashMap<String, Warp> warps;
	
	protected DataSaver saver;
	
	public void setSaver(final DataSaver saver) {
	
		this.saver = saver;
	}
	
	public void addWarp(final int sx, final int sy, final int sz, final int ex, final int ey, final int ez, final String faction) {
	
		final Warp w = new Warp();
		
		w.faction = faction.trim().toLowerCase();
		w.locationOneX = sx;
		w.locationOneY = sy;
		w.locationOneZ = sz;
		w.locationTwoX = ex;
		w.locationTwoY = ey;
		w.locationTwoZ = ez;
		
		final String start = w.locationOneString();
		final String end = w.locationTwoString();
		
		warps.put(start, w);
		warps.put(end, w);
		saver.saveWarpList(this);
	}
	
	public void removeWarp(final String key) {
	
		if (!warps.containsKey(key)) {
			return;
		}
		
		final Warp w = warps.get(key);
		
		warps.remove(w.locationTwoString());
		warps.remove(w.locationOneString());
		saver.saveWarpList(this);
	}
	
	public boolean warpExists(final int x, final int y, final int z) {
	
		final String location = LocationUtils.locationToString(x, y, z);
		return warps.containsKey(location);
	}
	
	public Warp getWarp(final int x, final int y, final int z) {
	
		if (!warpExists(x, y, z)) {
			return null;
		}
		
		return warps.get(LocationUtils.locationToString(x, y, z));
		
	}
	
}
