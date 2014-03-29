package com.gravypod.wrapper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	
	private static final int BUFFER_SIZE = 4096;
	
	private static void extractFile(final ZipInputStream in, final File outdir, final String name) throws IOException {
	
		final byte[] buffer = new byte[ZipUtils.BUFFER_SIZE];
		final File endFile = new File(outdir, name);
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(endFile));
		int count = -1;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
		out.close();
	}
	
	private static void mkdirs(final File outdir, final String path) {
	
		final File d = new File(outdir, path);
		if (!d.exists()) {
			d.mkdirs();
		}
	}
	
	private static String dirpart(final String name) {
	
		final int s = name.lastIndexOf(File.separatorChar);
		return s == -1 ? null : name.substring(0, s);
	}
	
	/***
	 * Extract zipfile to outdir with complete directory structure
	 * 
	 * @param zipfile
	 *            Input .zip file
	 * @param outdir
	 *            Output directory
	 */
	public static void extract(final File zipfile, final File outdir) {
	
		try {
			final ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
			ZipEntry entry;
			String name, dir;
			while ((entry = zin.getNextEntry()) != null) {
				name = entry.getName();
				if (entry.isDirectory()) {
					ZipUtils.mkdirs(outdir, name);
					continue;
				}
				dir = ZipUtils.dirpart(name);
				if (dir != null) {
					ZipUtils.mkdirs(outdir, dir);
				}
				
				ZipUtils.extractFile(zin, outdir, name);
			}
			zin.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void zipDirectory(final File sourceFolder, final File destination) throws IOException {
	
		final ZipOutputStream output = new ZipOutputStream(new FileOutputStream(destination));
		ZipUtils.recursiveZipDirectory(sourceFolder, output);
		output.close();
	}
	
	public static void zipDirectories(final File[] sourceFolders, final File destination) throws IOException {
	
		final ZipOutputStream output = new ZipOutputStream(new FileOutputStream(destination));
		for (final File sourceFolder : sourceFolders) {
			ZipUtils.recursiveZipDirectory(sourceFolder, output);
		}
		output.close();
	}
	
	public static void recursiveZipDirectory(final File sourceFolder, final ZipOutputStream zipStream) throws IOException {
	
		final String[] dirList = sourceFolder.list();
		final byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		for (final String element : dirList) {
			final File f = new File(sourceFolder, element);
			if (f.isDirectory()) {
				ZipUtils.recursiveZipDirectory(f, zipStream);
				continue;
			} else if (f.isFile() && f.canRead()) {
				final FileInputStream input = new FileInputStream(f);
				final ZipEntry anEntry = new ZipEntry(f.getPath());
				zipStream.putNextEntry(anEntry);
				while ((bytesIn = input.read(readBuffer)) != -1) {
					zipStream.write(readBuffer, 0, bytesIn);
				}
				input.close();
			}
		}
	}
}
