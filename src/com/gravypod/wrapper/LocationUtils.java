package com.gravypod.wrapper;

public class LocationUtils {
	
	/**
	 * Change a location to a string
	 * 
	 * @param x
	 *            - The x coord
	 * @param y
	 *            - The y coord
	 * @param z
	 *            - The z coord
	 * @return - A string representing the location
	 */
	public static String locationToString(final int x, final int y, final int z) {
	
		return x + " " + y + " " + z;
	}
	
	/**
	 * Extract a location from a string
	 * 
	 * @param line
	 *            - Line to extract data from
	 * @return - X, Y, Z locations as strings
	 */
	public static String[] extractLocationString(final String line) {
	
		return line.substring(line.lastIndexOf('(') + 1, line.lastIndexOf(')')).split(", ");
		
	}
	
}
