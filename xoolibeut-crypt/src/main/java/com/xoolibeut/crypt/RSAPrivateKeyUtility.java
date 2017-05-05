package com.xoolibeut.crypt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * RSAPrivateKeyUtility
 */
public class RSAPrivateKeyUtility extends RSAUtility {

	private static String beginPEM = "-----BEGIN RSA PRIVATE KEY-----";

	private static String endPEM = "-----END RSA PRIVATE KEY-----";

	/**
	 * convertKeyToPEMPrivateKey
	 * 
	 * @param data
	 *            byte
	 * @return String
	 */
	public static String convertKeyToPEMPrivateKey(byte[] data) {

		return convertKeyToPEM(data, beginPEM, endPEM);
	}

	/**
	 * @param privString
	 *            String
	 * @return String
	 */
	public static String cleanPEMPrivateKey(String privString) {

		return cleanPEM(privString);
	}

	/**
	 * @param privateKey
	 *            RSAPrivateKey
	 * @return String
	 * @throws PrismeInvalidTokenException
	 *             exception
	 */
	public static String convertRSAPrivateKey(RSAPrivateKey privateKey)
			throws RSAException {

		return cleanPEM(convertKeyToPEMPrivateKey(privateKey.getEncoded()));
	}

	/**
	 * @param privString
	 *            String
	 * @return PrismeRSAPrivateKey
	 * @throws PrismeInvalidTokenException
	 *             exception
	 */
	public static RSAPrivateKey convertToRSAPrivateKey(String privString)
			throws RSAException {

		privString = cleanPEM(privString);
	
		return convertToRSAPrivateKey( Base64.getDecoder().decode(privString));
	}

	/**
	 * convertToRSAPriv
	 * 
	 * @param privBytes
	 *            byte
	 * @return PrismeRSAPrivateKey
	 * @throws PrismeInvalidTokenException
	 *             exception
	 */
	public static RSAPrivateKey convertToRSAPrivateKey(byte[] privBytes)
			throws RSAException {

		RSAPrivateKey result = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			result = (RSAPrivateKey) keyFactory
					.generatePrivate(new PKCS8EncodedKeySpec(privBytes));
		} catch (NoSuchAlgorithmException e) {
			throw new RSAException("L'algorithme renseigné est inconnu");
		} catch (InvalidKeySpecException e) {
			throw new RSAException(
					"Erreur   lors de la génération de la private key");
		}
		return result;
	}
}
