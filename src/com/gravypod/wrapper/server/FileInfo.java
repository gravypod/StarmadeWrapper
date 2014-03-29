package com.gravypod.wrapper.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileInfo {
	
	private final File adminFile, donorFile, blocksFile;
	
	private final ArrayList<String> admins = new ArrayList<String>();
	
	private final ArrayList<String> donors = new ArrayList<String>();
	
	private final ArrayList<String> blocks = new ArrayList<String>();
	
	public FileInfo(final File dataFolder, final File starmadeDirectory) {
	
		blocksFile = new File(dataFolder, "blocks.txt");
		donorFile = new File(dataFolder, "donors.txt");
		adminFile = new File(starmadeDirectory, "admins.txt");
		updateInfo();
	}
	
	protected void updateInfo() {
	
		findAdmins();
		findBlocks();
		findDonors();
	}
	
	private void findBlocks() {
	
		if (!blocksFile.exists()) {
			try {
				blocks.addAll(readTextFromJar("/data/blocks.txt"));
			} catch (final IOException e) {
				e.printStackTrace();
			}
			return;
		}
		blocks.clear();
		try {
			final Scanner sc = new Scanner(blocksFile);
			while (sc.hasNextLine()) {
				blocks.add(sc.nextLine());
			}
			sc.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void findDonors() {
	
		if (!donorFile.exists()) {
			return;
		}
		donors.clear();
		try {
			final Scanner sc = new Scanner(donorFile);
			while (sc.hasNextLine()) {
				donors.add(sc.nextLine());
			}
			sc.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void findAdmins() {
	
		if (!adminFile.exists()) {
			return;
		}
		admins.clear();
		try {
			final Scanner sc = new Scanner(adminFile);
			while (sc.hasNextLine()) {
				admins.add(sc.nextLine());
			}
			sc.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public File getAdminfile() {
	
		return adminFile;
	}
	
	public File getBlocksfile() {
	
		return blocksFile;
	}
	
	public File getDonorfile() {
	
		return donorFile;
	}
	
	public ArrayList<String> getAdmins() {
	
		return admins;
	}
	
	public ArrayList<String> getBlocks() {
	
		return blocks;
	}
	
	public ArrayList<String> getDonors() {
	
		return donors;
	}
	
	public List<String> readTextFromJar(final String path) throws IOException {
	
		final List<String> out = new ArrayList<String>();
		
		final Scanner sc = new Scanner(getClass().getResourceAsStream(path));
		
		while (sc.hasNextLine()) {
			out.add(sc.nextLine());
		}
		
		return out;
	}
	
}
