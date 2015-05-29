package com.mtgames.jspr;

import com.mtgames.utils.Debug;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class Packer {
	public static void main(String[] arg) {
//		Init code
		System.setProperty("com.mtgames.debug", "0");

		File path = new File(arg[0]);

		File[] directories = path.listFiles((current, name) -> new File(current, name).isDirectory());

		for (File directory : directories) {
			pack(directory);
		}
	}

	private static void pack(File directory) {
		Debug.log(directory + " " + directory, Debug.DEBUG);

		try {
			File tar = new File(directory.getParent() + "/" + directory.getName() + ".tar");
			if (tar.exists() && !tar.isDirectory()) {
				tar.delete();
			}
			File jsp = new File(directory.getParent() + "/" + directory.getName() + ".jsp");
			if (jsp.exists() && !jsp.isDirectory()) {
				jsp.delete();
			}

			File[] filesToTar=directory.listFiles((dir, name) -> name.endsWith(".json"));

			TarOutputStream tarOut = new TarOutputStream(new FileOutputStream(directory.getParent() + "/" + directory.getName() + ".tar"));

			assert filesToTar != null;
			for (File f:filesToTar) {
				try {
					tarOut.putNextEntry(new TarEntry(f, f.getName()));
					BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f));

					int count;
					byte data[] = new byte[2048];
					while((count = origin.read(data)) != -1) {
						tarOut.write(data, 0, count);
					}

					tarOut.flush();
					origin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			InputStream archive = new FileInputStream(new File(directory.getParent() + "/" + directory.getName() + ".tar"));
			GZIPOutputStream gzOut = new GZIPOutputStream(new FileOutputStream(directory.getParent() + "/" + directory.getName() + ".jsp"));

			byte[] buffer = new byte[1024];
			int len;
			while ((len = archive.read(buffer)) != -1) {
				gzOut.write(buffer, 0, len);
			}
			gzOut.close();
			archive.close();

			new File(directory.getParent() + "/" + directory.getName() + ".tar").delete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
