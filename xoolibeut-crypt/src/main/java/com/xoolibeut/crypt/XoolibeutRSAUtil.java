/**
 * 
 */
package com.xoolibeut.crypt;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cer4495267
 * 
 */
public class XoolibeutRSAUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutRSAUtil.class);

	public static String formatPublicKey(String pubKey) throws RSAException {
		LOGGER.debug(" > formatPublicKey");

		// Convert to RSAPublicKey
		RSAPublicKey rsaPubKey = RSAPublicKeyUtility
				.convertToRSAPublicKey(pubKey);
		// Conversion en String (format PEM)
		return RSAPublicKeyUtility.convertKeyToPEMPublicKey(rsaPubKey
				.getEncoded());

	}

	public static String formatPrivateKey(String privKey) throws RSAException {
		LOGGER.debug(" > formatPrivateKey");

		// Convert to RSAPrivateKey
		RSAPrivateKey rsaPrivKey = RSAPrivateKeyUtility
				.convertToRSAPrivateKey(privKey);
		// Conversion en String (format PEM)
		return RSAPrivateKeyUtility.convertKeyToPEMPrivateKey(rsaPrivKey
				.getEncoded());

	}

	public static void generateKey(String rep) {

		try {
			// Generation keyPair
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			final int keyBitSize = 1024;
			keyGen.initialize(keyBitSize);
			KeyPair keyPair = keyGen.genKeyPair();
			// Generation clés privée et publique
			RSAPrivateKey priv = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();
			// Conversion application			
			Files.write(Paths.get(rep + File.separator + "PrivateKey.pem"),
					formatPrivateKey(RSAPrivateKeyUtility.convertRSAPrivateKey(priv)).getBytes());
			Files.write(Paths.get(rep + File.separator + "PublicKey.pem"),
					formatPublicKey(RSAPublicKeyUtility.convertRSAPublicKey(pub)).getBytes());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		generateKey("D:\\devs\\tmp\\xoolibeut");
	}
}
