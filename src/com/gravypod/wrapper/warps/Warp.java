package com.gravypod.wrapper.warps;

import com.gravypod.wrapper.LocationUtils;

public class Warp {
	
	public int locationOneX, locationOneY, locationOneZ;
	
	public int locationTwoX, locationTwoY, locationTwoZ;
	
	public String faction;
	
	public String locationOneString() {
	
		return LocationUtils.locationToString(locationOneX, locationOneY, locationOneZ);
	}
	
	public String locationTwoString() {
	
		return LocationUtils.locationToString(locationTwoX, locationTwoY, locationTwoZ);
		
	}
	
	@Override
	public String toString() {
		return locationOneString() + " => " + locationTwoString();
	}
	
}
