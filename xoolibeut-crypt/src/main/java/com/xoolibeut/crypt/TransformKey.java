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
		try {
			System.out.println(transformFile("RawaneNdeyeAidaSalimata", "/home/rawane/devs/tmp/key/PrivateKeyM_T_1.pem",
					"/home/rawane/devs/tmp/key/PrivateKeyM_T_2.pem"));
			// RSAPrivateKeyUtility.convertToRSAPrivateKey(transformFile("RawaneNdeyeAidaSalimata",
			// "/home/rawane/devs/tmp/key/PrivateKeyM_T_1.pem","/home/rawane/devs/tmp/key/PrivateKeyM_T_2.pem"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	/**
	 * 
	 * @param line
	 * @param key
	 * @return
	 */
	public static String transformLine(String line, String key) {
		// System.out.println(line);
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
	public static String transformFile(String password, String... paths) {
		List<String> lines = new ArrayList<>(50);
		List<String> linesTransform = new ArrayList<>(50);
		try {
			StringBuilder transformBuilder = new StringBuilder(2048);
			for (String path : paths) {
				lines.addAll(Files.readAllLines(Paths.get(path)));
			}
			char[] charArray = password.toCharArray();
			int i = 0;
			for (char carac : charArray) {
				if (!lines.get(i).startsWith("----")) {
					String trans = transformLine(lines.get(i), String.valueOf(carac));
					transformBuilder.append(trans).append("\n");
					linesTransform.add(trans);
					System.out.println(" key " + carac);
					System.out.println(" entrée  " + lines.get(i));
					System.out.println(" trans  " + trans);
				} else {
					transformBuilder.append(lines.get(i)).append("\n");
					linesTransform.add(lines.get(i));
					String trans = transformLine(lines.get(i + 1), String.valueOf(carac));
					linesTransform.add(trans);
					transformBuilder.append(trans).append("\n");
					i++;
				}
				i++;
			}
			int count = 0;
			for (int k = i; k < lines.size(); k++) {
				transformBuilder.append(lines.get(k)).append("\n");
				count = k;
			}
			System.out.println("tansform i  " + i);
			System.out.println("lines " + lines.size());
			System.out.println("transform lines " + count);
			List<String> lines1 = Files.readAllLines(Paths.get("/home/rawane/devs/tmp/key/PrivateKeyM.pem"));
			for (int l = 0; l < lines1.size(); l++) {
				if (!lines1.get(l).equals(linesTransform.get(l))) {
					System.out.println("ligne  " + l + "  " + linesTransform.get(l));
					System.out.println("ligne  " + l + "  " + lines1.get(l));
					System.exit(1);
				}
			}

			return transformBuilder.toString();
		} catch (IOException ioException) {

			ioException.printStackTrace();
		}

		return null;
	}

}
