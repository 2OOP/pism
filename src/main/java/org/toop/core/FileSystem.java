package org.toop.core;

import java.io.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class FileSystem {
	public record File(String path, CharSequence buffer) {};

    private static final Logger logger = LogManager.getLogger(FileSystem.class);

	public static File read(String path) {
		File file;

		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			StringBuilder buffer = new StringBuilder();
			String line = reader.readLine();

			while (line != null) {
				buffer.append(line);
				buffer.append(System.lineSeparator());
				line = reader.readLine();
			}

			file = new File(path, buffer);
		} catch (IOException e) {
			logger.error("{}", e.getMessage());
			return null;
		}

		return file;
	}
}
