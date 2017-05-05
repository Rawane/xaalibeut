package com.xoolibeut.crypt;

import java.util.Base64;

/**
 * RSAUtility
 */
public class RSAUtility {

	/**
	 * Convertit une RSAKey en contenu de fichier PEM.
	 * 
	 * @param data
	 *            byte
	 * @param beginPEM
	 *            String
	 * @param endPEM
	 *            String
	 * @return String
	 */
	protected static String convertKeyToPEM(final byte[] data,
			final String beginPEM, final String endPEM) {

		String base64Encoded = Base64.getEncoder().encodeToString(data);
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(beginPEM + "\n");
		boolean done = false;
		int start = 0;
		int end = 63;
		while (!done) {
			if (end > base64Encoded.length()) {
				end = base64Encoded.length();
				done = true;
			}
			strBuff.append(base64Encoded.substring(start, end) + "\n");
			start = end;
			end = end + 63;
		}
		strBuff.append(endPEM);
		return strBuff.toString();
	}

	/**
	 * Nettoie une Key au format PEM en supprimant l'entete, la fin et les sauts
	 * de ligne
	 * 
	 * @param pemString
	 *            String
	 * @return String
	 */
	protected static String cleanPEM(String pemString) {

		// Suppression header
		int posHeader = pemString.indexOf("\n");
		if (posHeader != -1) {
			pemString = pemString.substring(posHeader);
		}
		// Suppression footer
		int posFooter = pemString.lastIndexOf("\n");
		if (posFooter != -1) {
			pemString = pemString.substring(0, posFooter);
		}
		// Trim
		pemString = pemString.replaceAll("\n", "");
		pemString = pemString.replaceAll("\r", "");
		return pemString;
	}
}
