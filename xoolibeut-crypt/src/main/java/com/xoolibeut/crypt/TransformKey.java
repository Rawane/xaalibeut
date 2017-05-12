package com.xoolibeut.crypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TransformKey {
	private static final String ALPHA = "~ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static int getOrdre(String key) {

		return ALPHA.indexOf(key);
	}

	public static void main(String[] args) {
		
	}

	/**
	 * 
	 * @param line
	 * @param key
	 * @return
	 */
	public static String transformLine(String line, String key) {
		//System.out.println(line);
		int ordre = getOrdre(key.toUpperCase());
		if (ordre > 0 && ordre < line.length()) {
			String replace = line.substring(line.length() - 1);
			return line.substring(0, ordre - 1) + replace
					+ line.substring(ordre - 1, line.length() - 1);
		}
		return line;
	}

	/**
	 * 
	 * @param line
	 * @param key
	 * @return
	 */
	public static String transformFile(String password, String... paths) {
		List<String> lines = new ArrayList<>(5);
		try {
			StringBuilder transformBuilder = new StringBuilder(2048);
			for (String path : paths) {
				lines.addAll(Files.readAllLines(Paths.get(path)));
			}
			char[] charArray = password.toCharArray();
			int i = 0;
			for (char carac : charArray) {
				if (!lines.get(i).startsWith("----")) {
					transformBuilder.append(transformLine(lines.get(i),
							String.valueOf(carac))).append("\n");
				} else {
					transformBuilder.append(lines.get(i)).append("\n");
					transformBuilder.append(transformLine(lines.get(i+1),
							String.valueOf(carac))).append("\n");
					i++;
				}
				i++;
			}
			for (int k = i; k < lines.size(); k++) {
				transformBuilder.append(lines.get(k)).append("\n");
			}
			return transformBuilder.toString();
		} catch (IOException ioException) {

			ioException.printStackTrace();
		}

		return null;
	}

}
