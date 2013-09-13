package com.quui.utils.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

/**
 * Simply writes a file to disk
 *
 * @author maxmc
 *
 */
public class FileSaver {
	public static final boolean write(final CharSequence buffer, final File file, final boolean compress) {
		OutputStream out = null;
		BufferedWriter writer = null;
		try {
			out = compress
					? new GZIPOutputStream(new FileOutputStream(new File(file.getAbsolutePath()+".gz")))
					: new FileOutputStream(file);

			writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.append(buffer);
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {}
			try {
				out.close();
			} catch (Exception ex) {}
		}
	}
}
