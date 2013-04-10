package com.quui.utils.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simply writes a file to disk
 *
 * @author maxmc
 *
 */
public class FileSaver {
	private String _content;
	private String _filename;

	/**
	 * @param content
	 *            the content of the file
	 * @param filename
	 *            the fully qualified filename
	 */
	public FileSaver(String content, String filename) {
		_content = content;
		_filename = filename;

		writeToFile();
	}

	private void writeToFile() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(_filename)));
			writer.write(_content.toCharArray());
		} catch (IOException e) {
			System.err.println("fail to write " +  _filename);
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {}
		}
	}
}
