package com.gravypod.wrapper.server;

import java.util.Collections;
import java.util.List;

import com.gravypod.starmadewrapper.Sector;
import com.gravypod.wrapper.LocationUtils;

public class User implements com.gravypod.starmadewrapper.User {
	
	public int x, y, z;
	
	public String name;
	
	public List<String> factions;
	
	public String leads;
	
	public int lastVotes;
	
	public void setLocation(final int x, final int y, final int z) {
	
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
	
		return x;
	}
	
	public int getY() {
	
		return y;
	}
	
	public int getZ() {
	
		return z;
	}
	
	public String getLocation() {
	
		return x + ", " + y + ", " + z;
	}
	
	public String getCommandLocation() {
	
		return LocationUtils.locationToString(x, y, z);
	}
	
	public synchronized void add(final String item) {
	
		factions.add(item);
	}
	
	public synchronized void remove(final String item) {
	
		factions.remove(item);
	}
	
	public synchronized List<String> getFactions() {
	
		return Collections.unmodifiableList(factions);
	}
	
	public synchronized int getLastVotes() {
	
		return lastVotes;
	}
	
	public synchronized String getLeads() {
	
		return leads;
	}
	
	public synchronized String getName() {
	
		return name;
	}

	@Override
	public String getUsername() {
	
		return getName();
	}

	@Override
	public Sector getSector() {
	
		return new Sector(x, y, z);
	}

	@Override
	public String getFactionOwned() {
	
		return getLeads();
	}
	
}
