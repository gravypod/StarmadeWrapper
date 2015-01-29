package com.gravypod.wrapper.server;

import java.io.File;

public class FileWatcher implements Runnable {

	private final File file;
	private final Runnable callback;
	private long lastLength, lastTimestamp;

	public FileWatcher(File file, Runnable callback) {
		this.file = file;
		this.callback = callback;

	}

	@Override
	public void run() {

		if (!hasMetadataChanged()) { // Check metadata
			return;
		}

		updateMetadata(); // Update state to match metadata on disk

		callback.run(); // Run update

	}

	private void updateMetadata() {
		this.lastLength = file.length();
		this.lastTimestamp = file.lastModified();
	}

	private boolean hasMetadataChanged() {
		return lastLength != file.length() || lastTimestamp != file.lastModified();
	}

}
