package com.xoolibeut.crypt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSAPublicKeyUtility
 */
public class RSAPublicKeyUtility extends RSAUtility {

	private static String beginPEM = "-----BEGIN RSA PUBLIC KEY-----";

	private static String endPEM = "-----END RSA PUBLIC KEY-----";

	/**
	 * convertKeyToPEMPublicKey
	 * 
	 * @param data
	 *            byte
	 * @return String
	 */
	public static String convertKeyToPEMPublicKey(byte[] data) {

		return convertKeyToPEM(data, beginPEM, endPEM);
	}

	/**
	 * @param pubString
	 *            String
	 * @return String
	 */
	public static String cleanPEMPublicKey(String pubString) {

		return cleanPEM(pubString);
	}

	/**
	 * @param publicKey
	 *            RSAPublicKey
	 * @return String
	 * @throws PrismeInvalidTokenException
	 *             exception
	 */
	public static String convertRSAPublicKey(RSAPublicKey publicKey)
			throws RSAException {

		return cleanPEM(convertKeyToPEMPublicKey(publicKey.getEncoded()));
	}

	/**
	 * @param pubString
	 *            String
	 * @return PrismeRSAPublicKey
	 * @throws PrismeInvalidTokenException
	 *             exception
	 */
	public static RSAPublicKey convertToRSAPublicKey(String pubString)
			throws RSAException {

		pubString = cleanPEM(pubString);
		return convertToRSAPublicKey(Base64.getDecoder().decode(pubString));
	}

	/**
	 * convertToRSAPub
	 * 
	 * @param pubBytes
	 *            byte
	 * @return PrismeRSAPublicKey
	 * @throws PrismeInvalidTokenException
	 *             exception
	 */
	public static RSAPublicKey convertToRSAPublicKey(byte[] pubBytes)
			throws RSAException {

		RSAPublicKey result = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			result = (RSAPublicKey) keyFactory
					.generatePublic(new X509EncodedKeySpec(pubBytes));
		} catch (NoSuchAlgorithmException e) {
			throw new RSAException("L'algorithme renseigné est inconnu", e);
		} catch (InvalidKeySpecException e) {
			throw new RSAException(
					"Erreur lors de la génération de la public key", e);
		}
		return result;
	}
}
