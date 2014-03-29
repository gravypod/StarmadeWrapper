package com.gravypod.wrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class VersionManager {
	
	public static final String VERSION_FILE_NAME = "version.txt";
	
	public static final String STARMADE_MIRRORS_SITE = "http://files.star-made.org/mirrors";
	
	public static final String STARMADE_BUILD_SERVER = "http://files.star-made.org/build/";
	
	public static final String STARMADE_CHECKSUM_SITE = "http://files.star-made.org/checksum";
	
	private final List<String> builds;
	
	private final String mirror;
	
	private final HashMap<String, Integer> fileSizes = new HashMap<String, Integer>();
	
	private String version;
	
	private String build;
	
	private final File starmadeDirectory;
	
	public VersionManager(final File dir) throws IOException {
	
		String mirrorUsed;
		
		try {
			List<String> mirrors = getMirrors();
			
			mirrorUsed = mirrors.get((int) (Math.random() * mirrors.size()));
		} catch (IOException e) {
			mirrorUsed = STARMADE_BUILD_SERVER;
		}
		
		mirror = mirrorUsed;
		
		starmadeDirectory = dir;
		builds = getBuilds();
		
	}
	
	public boolean isInstalled() {
	
		return new File(starmadeDirectory, "StarMade.jar").exists();
	}
	
	public void downloadUpdate(final boolean backup) throws IOException {
	
		final String buildURL = mirror + desanatizeFile(latestBuild());
		final URL website = new URL(buildURL);
		final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		final int fileSize = fileSizes.get(latestBuild());
		ServerWapper.getLogger().info("Downloading downloading the latest version of starmade. This build can be found here: " + buildURL + ". The file is expected to be " + fileSize + " bytes.");
		final FileOutputStream fos = new FileOutputStream("starmade-latest.zip");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
		
		
		ServerWapper.getLogger().info("Download finished. ");
		
		if (isInstalled() && backup) {
			ServerWapper.getLogger().info("Backing up server.");
			final File f = new File(new SimpleDateFormat("'backup-'MM-dd hh-mm-ss-SS'.zip'").format(new Date()));
			if (!f.exists()) {
				f.createNewFile();
			}
			ZipUtils.zipDirectory(starmadeDirectory, f);
		}
		
		if (!starmadeDirectory.exists()) {
			starmadeDirectory.mkdirs();
		}
		ServerWapper.getLogger().info("Installing update.");
		
		File starmadeUpdate = new File("starmade-latest.zip");
		
		if (starmadeUpdate.length() != fileSize) {
			throw new IOException("File downloaded is the incorrect size!");
		}
		
		
		ZipUtils.extract(starmadeUpdate, starmadeDirectory);
		ServerWapper.getLogger().info("Update has finished.");
	}
	
	/**
	 * Check to see if an update is needed
	 * 
	 * @param dir
	 *            - The directory where starmade is installed
	 * @return
	 */
	public boolean needsUpdate() {
	
		if (!isInstalled()) {
			return true;
		}
		final File versionFile = new File(starmadeDirectory, VersionManager.VERSION_FILE_NAME);
		
		if (!versionFile.exists()) {
			return true;
		}
		
		try {
			final String data = readFile(versionFile);
			
			final String[] info = data.split("#");
			version = info[0].trim();
			build = info[1].trim();
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return !latestBuild().equals(build);
		
	}
	
	private List<String> getMirrors() throws IOException {
	
		final ArrayList<String> mirrors = new ArrayList<String>();
		final URL url = new URL(VersionManager.STARMADE_MIRRORS_SITE);
		final Scanner sc = new Scanner(url.openStream());
		
		while (sc.hasNext()) {
			final String line = sc.nextLine();
			mirrors.add(line);
		}
		
		sc.close();
		
		return mirrors;
	}
	
	private List<String> getBuilds() throws IOException {
	
		final ArrayList<String> builds = new ArrayList<String>();
		final URL url = new URL(VersionManager.STARMADE_CHECKSUM_SITE);
		final Scanner sc = new Scanner(url.openStream());
		
		while (sc.hasNext()) {
			final String line = sc.nextLine();
			if (!line.startsWith("starmade-build")) {
				continue;
			}
			final String[] buildInfo = line.split(" ");
			final String build = sanatizeFile(buildInfo[0]).trim();
			final int fileSize = Integer.parseInt(buildInfo[1]);
			fileSizes.put(build, fileSize);
			builds.add(build);
		}
		
		sc.close();
		
		return builds;
	}
	
	// starmade-build_20130809_195544.zip
	
	public String latestBuild() {
	
		return builds.get(builds.size() - 1);
	}
	
	private String sanatizeFile(final String name) {
	
		return name.replace("starmade-build_", "").replace(".zip", "");
	}
	
	private String desanatizeFile(final String newName) {
	
		return "starmade-build_" + newName + ".zip";
	}
	
	private String readFile(final File file) throws IOException {
	
		final StringBuilder fileContents = new StringBuilder((int) file.length());
		final Scanner scanner = new Scanner(file);
		final String lineSeparator = System.getProperty("line.separator");
		
		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}
	
	public String getBuild() {
	
		return build;
	}
	
	public String getVersion() {
	
		return version;
	}
}
