package com.gravypod.wrapper;

import com.gravypod.starmadewrapper.Sector;

import java.util.ArrayList;
import java.util.List;

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
     * Parse a Sector from String.
     *
     * @param line from the logger
     * @return sector parsed
     */
    public static Sector sectorFromString(final String line) {

        String sector = line.substring(line.indexOf("SECTOR", 0), line.lastIndexOf(")") + 1);
        String[] coords = sector.substring(sector.indexOf("(") + 1, sector.indexOf(")")).split(", ");

        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        int z = Integer.parseInt(coords[2]);

        return new Sector(x, y, z);
    }

    /**
     * Parse a array of Sectors for sector changing.
     *
     * @param line from the logger
     * @return array of sectors parsed
     */
	public static Sector[] sectorsFromString(final String line) {

        List<Sector> sectorList = new ArrayList<Sector>();
        String[] sectors = line.substring(line.indexOf("Sector[", 0), line.lastIndexOf(")") + 1).split(" -> ");

        for (String sector : sectors) {
            String[] coords = sector.substring(sector.indexOf("(") + 1, sector.indexOf(")")).split(", ");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int z = Integer.parseInt(coords[2]);
            sectorList.add(new Sector(x, y ,z));
        }

        return sectorList.toArray(new Sector[sectorList.size()]);
    }
	
}
