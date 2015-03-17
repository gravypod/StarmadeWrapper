package com.gravypod.wrapper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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
			final List<String> mirrors = getMirrors();
			
			mirrorUsed = mirrors.get((int) (Math.random() * mirrors.size()));
		} catch (final IOException e) {
			mirrorUsed = VersionManager.STARMADE_BUILD_SERVER;
		}
		
		mirror = mirrorUsed;
		
		starmadeDirectory = dir;
		builds = getBuilds();
		
	}
	
	public boolean isInstalled() {
	
		return new File(starmadeDirectory, "StarMade.jar").exists();
	}
	
	public void downloadUpdate(final boolean backup) throws IOException {
	
		final String remotePath = mirror + desanitizeFile(latestBuild());
		BufferedInputStream inputStream = null;
		FileOutputStream outputStream = null;
		
		final URL url = new URL(remotePath);
		final URLConnection connection = url.openConnection();
		final int size = connection.getContentLength();
		
		if (size < 0) {
			ServerWrapper.getLogger().info("Unable to get the latest version of StarMade!");
		} else {
			ServerWrapper.getLogger().info("Downloading the latest version of StarMade (length: " + size + " bytes, URL: " + remotePath + ")...");
		}
		
		inputStream = new BufferedInputStream(url.openStream());
		outputStream = new FileOutputStream("StarMade-latest.zip");
		
		final byte data[] = new byte[1024];
		int count;
		double sumCount = 0.0;
		int percentage;
		int lastPercentage = 0;
		
		while ((count = inputStream.read(data, 0, 1024)) != -1) {
			outputStream.write(data, 0, count);
			
			sumCount += count;
			
			percentage = (int) Math.ceil(sumCount / size * 100);
			
			if (percentage != lastPercentage) {
				ServerWrapper.getLogger().info(percentage + "%");
			}
			
			lastPercentage = percentage;
		}
		
		if (inputStream != null) {
			inputStream.close();
		}
		if (outputStream != null) {
			outputStream.close();
		}
		
		ServerWrapper.getLogger().info("Download finished. ");
		
		if (isInstalled() && backup) {
			ServerWrapper.getLogger().info("Backing up server.");
			final File f = new File(new SimpleDateFormat("'backup-'MM-dd hh-mm-ss-SS'.zip'").format(new Date()));
			if (!f.exists()) {
				f.createNewFile();
			}
			ZipUtils.zipDirectory(starmadeDirectory, f);
		}
		
		if (!starmadeDirectory.exists()) {
			starmadeDirectory.mkdirs();
		}
		ServerWrapper.getLogger().info("Installing update.");
		
		final File starmadeUpdate = new File("starmade-latest.zip");
		
		if (starmadeUpdate.length() != size) {
			throw new IOException("File downloaded is the incorrect size!");
		}
		
		ZipUtils.extract(starmadeUpdate, starmadeDirectory);
		ServerWrapper.getLogger().info("Update has finished.");
	}
	
	/**
	 * Check to see if an update is needed
	 * 
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
			final String build = sanitizeFile(buildInfo[0]).trim();
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
	
	private String sanitizeFile(final String name) {
	
		return name.replace("starmade-build_", "").replace(".zip", "");
	}
	
	private String desanitizeFile(final String newName) {
	
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
