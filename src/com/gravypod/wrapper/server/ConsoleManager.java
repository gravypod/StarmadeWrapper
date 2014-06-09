package com.gravypod.wrapper.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class ConsoleManager implements Runnable {
	
	private PrintWriter writer;
	
	private final ProcessBuilder builder;
	
	private final BlockingQueue<String> messages;
	
	private final AtomicReference<Process> starmade = new AtomicReference<Process>();
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private final File starmadeDir;
	private final Config config;
	
	public ConsoleManager(final Config config, final File starmadeDir, final BlockingQueue<String> messages) {
	
		this.messages = messages;
		this.config = config;
		this.starmadeDir = starmadeDir;
		builder = creatBuilder();
	}
	
	@Override
	public void run() {
	
		try {
			starmade.set(builder.start());
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		
		setWriter(starmade.get().getOutputStream());
		final Scanner sc = new Scanner(starmade.get().getInputStream());
		
		while (sc.hasNextLine()) {
			final String line = sc.nextLine();
			messages.add(line);
		}
		sc.close();
		
		try {
			starmade.get().waitFor(); // Wait for the server to exit
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		starmade.get().destroy();
	}
	
	public synchronized void exec(final String command) {
	
		lock.lock();
		writer.println(command);
		writer.flush();
		lock.unlock();
	}
	
	public synchronized void masExec(final String... commands) {
	
		lock.lock();
		for (final String s : commands) {
			writer.println(s);
		}
		writer.flush();
		lock.unlock();
	}
	
	/**
	 * Warning, will alter array that was input
	 * 
	 * @param user
	 * @param messages
	 */
	public void pm(final String user, final String... messages) {
	
		final String[] ms = new String[messages.length];
		for (int i = 0; i < messages.length; i++) {
			ms[i] = "/pm " + user + " " + messages[i];
		}
		
		masExec(ms);
	}
	
	public synchronized void pm(final String user, final String message) {
	
		exec("/pm " + user + " " + message);
	}
	
	public synchronized void tp(final String user, final int x, final int y, final int z) {
	
		exec("/change_sector_for " + user + " " + x + " " + y + " " + z);
	}
	
	public void tp(final String sender, final String x, final String y, final String z) {
	
		exec("/change_sector_for " + sender + " " + x + " " + y + " " + z);
	}
	
	public synchronized void give(final String user, final int item, final int amount) {
	
		exec("/giveid " + user + " " + item + " " + amount);
	}
	
	public synchronized void ban(final String user) {
	
		exec("/ban_name " + user);
	}
	
	public synchronized void setGodMode(final String user, final boolean enabled) {
	
		exec("/god_mode " + user + " " + enabled);
	}

    public synchronized void setWhitelist(final boolean whitelist) {

        exec("/whitelist_activate " + whitelist);
    }

    public synchronized void whitelist(final String username, boolean auth) {

        if (auth) {
            exec("/whitelist_account " + username);
        } else {
            exec("/whitelist_name " + username);
        }
    }
	
	public synchronized PrintWriter getWriter() {
	
		return writer;
	}
	
	public synchronized void setWriter(final OutputStream os) {
	
		writer = new PrintWriter(os);
	}
	
	private ProcessBuilder creatBuilder() {
	
		final ProcessBuilder builder = new ProcessBuilder(config.launchCommand.split(" "));
		builder.redirectErrorStream(true);
		builder.directory(starmadeDir);
		return builder;
	}
	
	public void kill() {
	
		starmade.get().destroy();
	}
	
}
