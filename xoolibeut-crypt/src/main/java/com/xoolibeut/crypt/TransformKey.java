package com.xoolibeut.crypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformKey {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformKey.class);
	private static final String ALPHA = "wazbcfeskolmpkdlnsklyeABMBWcsmsddjbzrezywlUXrHoCUDEFwjGzI9/OVtRkBOSYf1VTAJCtwTON0rsNXlQ0pF5tw3yueu/xjK3NFcocOAH7PR9pdmBjGjGjUxz3AOCE4I5BXFCdx1SzqrZqh70ymMxo3mawzPXAICs5Bmtmm4vI7PN+ptCr7mkr083prcbtUiuFM8nUqQIDAQABAoICAAhdp89L2QEG2~ABCDEFGHIJKLMNOPQRSTUVWXY!Z9812375640-_^çà@)]=}+";

	public static int getOrdre(String key, String chaine) {
		return chaine.indexOf(key);
	}

	/**
	 * 
	 * @param line
	 * @param key
	 * @return
	 */
	public static String transformLine(String line, String key) {
		// System.out.println(line);
		int ordre = getOrdre(key.toUpperCase(), ALPHA.substring(ALPHA.indexOf("~")));
		if (ordre > 0 && ordre < line.length()) {
			String replace = line.substring(line.length() - 1);
			return line.substring(0, ordre - 1) + replace + line.substring(ordre - 1, line.length() - 1);
		}
		return line;
	}

	/**
	 * Transformer pluseurs fichier pem en seule clé privé.
	 * 
	 * @param line
	 * @param key
	 * @return
	 */
	public static String transformFile(String password, String... paths) {
		List<String> lines = new ArrayList<>(50);
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

				} else {
					transformBuilder.append(lines.get(i)).append("\n");
					String trans = transformLine(lines.get(i + 1), String.valueOf(carac));
					transformBuilder.append(trans).append("\n");
					i++;
				}
				i++;
			}

			for (int k = i; k < lines.size(); k++) {
				String trans = lines.get(k);
				transformBuilder.append(trans);
				if (k < lines.size() - 1) {
					transformBuilder.append("\n");
				}
			}

			return transformBuilder.toString();
		} catch (IOException ioException) {
			LOGGER.error("erreur", ioException);
		}

		return null;
	}
}
