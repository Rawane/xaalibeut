/**
 * 
 */
package com.xoolibeut.crypt;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

	public static void generateKey(String rep, final int keyBitSize) {

		try {
			LOGGER.info("généré une clés de taille " + keyBitSize);
			// Generation keyPair 				
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(keyBitSize);
			KeyPair keyPair = keyGen.genKeyPair();
			// Generation clés privée et publique
			RSAPrivateKey priv = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();
			LOGGER.info("clés généré avec la taille " + priv.getModulus().bitLength()+" "+priv.getModulus().bitCount());
			// Conversion application
			Files.write(
					Paths.get(rep + File.separator + "PrivateKey.pem"),
					formatPrivateKey(
							RSAPrivateKeyUtility.convertRSAPrivateKey(priv))
							.getBytes());
			Files.write(
					Paths.get(rep + File.separator + "PublicKey.pem"),
					formatPublicKey(
							RSAPublicKeyUtility.convertRSAPublicKey(pub))
							.getBytes());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	public static void generateSymetricKey(String rep) {

		try {
			
			// Generation keyPair 				
			  KeyGenerator keyGen = KeyGenerator.getInstance("AES");			
			keyGen.init(256);
			//keyGen.init(SecureRandom.getInstance("SHA1PRNG"));
			

	        MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update("FamilleGayeFranceRawaneNdeyeAidaSalimata!2307".getBytes());
			//LOGGER.info("clés généré avec la taille " +RSAUtility.convertKeyToPEM(secretKey.getEncoded(), "", ""));
			LOGGER.info("clés généré " +Base64.getEncoder().encodeToString(md.digest()));
			SecretKey secretKey = new SecretKeySpec("WoorAEIGAYE!2307".getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			
			cipher.doFinal(Files.readAllBytes(Paths.get("D:\\devs\\test\\ManagementController.java")));
			File f = new File("D:\\devs\\test\\ManagementController.java.cypt");
			f.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(cipher.doFinal(Files.readAllBytes(Paths.get("D:\\devs\\test\\ManagementController.java"))));
			fos.flush();
			fos.close();
			Cipher cipherD = Cipher.getInstance("AES");
			cipherD.init(Cipher.DECRYPT_MODE, secretKey);
			File file = new File("D:\\devs\\test\\ManagementController.decypt.java");
			file.getParentFile().mkdirs();
			FileOutputStream fosOutputStream = new FileOutputStream(file);
			fosOutputStream.write(cipherD.doFinal(Files.readAllBytes(Paths.get("D:\\devs\\test\\ManagementController.java.cypt"))));
			fosOutputStream.flush();
			fosOutputStream.close();
//			// Conversion application
//			Files.write(
//					Paths.get(rep + File.separator + "PrivateKey.pem"),
//					formatPrivateKey(
//							RSAPrivateKeyUtility.convertRSAPrivateKey(priv))
//							.getBytes());
//			Files.write(
//					Paths.get(rep + File.separator + "PublicKey.pem"),
//					formatPublicKey(
//							RSAPublicKeyUtility.convertRSAPublicKey(pub))
//							.getBytes());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		generateSymetricKey("xoolibeut");
	}
}
