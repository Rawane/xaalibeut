package com.xoolibeut.crypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TransformKey {
	private static final String ALPHA = "~ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static int getOrdre(String key) {

		return ALPHA.indexOf(key);
	}

	public static void main(String[] args) {
		System.out.println(transformLine("+z8NfES3F2nnAQcp0QaCMXTBQDMXzqHAZv+Q/3LFOxhZ1BWsrSpGy1VzMdbp60h", "w")
				.equals("+z8NfES3F2nnAQcp0QaCMXhTBQDMXzqHAZv+Q/3LFOxhZ1BWsrSpGy1VzMdbp60"));
		System.out.println(transformLine("tIjOVnYrJu3KJfDKgoII91ilzoMciBcxQ0zkf93+1blIqQpcY8ipSBh6p8P4hbV", "A"));
	}

	/**
	 * 
	 * @param line
	 * @param key
	 * @return
	 */
	public static String transformLine(String line, String key) {
		System.out.println(line);
		int ordre = getOrdre(key.toUpperCase());
		if (ordre > 0 && ordre < line.length()) {
			String replace = line.substring(line.length() - 1);
			return line.substring(0, ordre - 1) + replace + line.substring(ordre - 1, line.length() - 1);
		}
		return line;
	}

	/**
	 * 
	 * @param line
	 * @param key
	 * @return
	 */
	public static String transformFile(String path, String password) {
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(path));
			char[] charArray = password.toCharArray();
			int i = 1;
			for (char carac : charArray) {
				String transform = transformLine(lines.get(i), String.valueOf(carac));
				lines.set(i, transform);
			}
		} catch (IOException ioException) {

			ioException.printStackTrace();
		}

		return null;
	}

}
