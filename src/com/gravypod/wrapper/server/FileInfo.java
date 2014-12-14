package com.gravypod.wrapper.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class FileInfo {
	
	private final File adminFile, donorFile, blocksFile;
	
	private final Set<String> admins = new HashSet<String>();
	
	private final Set<String> donors = new HashSet<String>();
	
	private final Set<String> blocks = new HashSet<String>();
	
	private final ReentrantLock lock = new ReentrantLock();
	
	public FileInfo(final File dataFolder, final File starmadeDirectory, ScheduledExecutorService executor) {
	
		blocksFile = new File(dataFolder, "blocks.txt");
		donorFile = new File(dataFolder, "donors.txt");
		adminFile = new File(starmadeDirectory, "admins.txt");
		updateInfo();
		if (donorFile.exists()) {
			executor.scheduleAtFixedRate(new FileWatcher(donorFile, new Runnable() {
				@Override
				public void run() {
					lock.lock();
					System.out.println("Updating donors");
					findDonors();
					lock.unlock();
				}
			}), 5, 5, TimeUnit.SECONDS);
		}
		
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
		
		try {
			final Scanner sc = new Scanner(donorFile);
			while (sc.hasNextLine()) {
				String next = sc.nextLine();
				donors.add(next);
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
	
	public boolean isDonor(String username) {
		lock.lock();
		boolean is = donors.contains(username);
		lock.unlock();
		return is;
	}
	public boolean isAdmin(String username) {
		lock.lock();
		boolean is = admins.contains(username);
		lock.unlock();
		return is;
	}
	
	public List<String> readTextFromJar(final String path) throws IOException {
	
		final List<String> out = new ArrayList<String>();
		
		final Scanner sc = new Scanner(getClass().getResourceAsStream(path));
		
		while (sc.hasNextLine()) {
			out.add(sc.nextLine());
		}
		
		sc.close();
		
		return out;
	}
	
}
