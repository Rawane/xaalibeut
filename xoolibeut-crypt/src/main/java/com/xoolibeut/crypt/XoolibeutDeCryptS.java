/**
 * 
 */
package com.xoolibeut.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cer4495267
 * 
 */
public class XoolibeutDeCryptS {
	/**
	 * 
	 */
	private static final String ADD_FILE_CRYPT_XOOL = "_xool";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutDeCryptS.class);
	private Cipher cipher;
	private int countFileDecrpypt = 0;
	private int countAllFileModeDeCrypt = 0;

	/**
	 * 
	 */
	public XoolibeutDeCryptS() {
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {

			e.printStackTrace();
		}
	}

	public XoolibeutDeCryptS(int rsaSize) {
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {

			e.printStackTrace();
		}
	}

	private RSAPrivateKey getPrivateKeyFromFile(String fileNameKey)
			throws RSAException {
		RSAPrivateKey privateKey = null;
		try {
			privateKey = RSAPrivateKeyUtility
					.convertToRSAPrivateKey(new String(Files.readAllBytes(Paths
							.get(fileNameKey))));
		} catch (IOException exception) {

			exception.printStackTrace();
		}
		return privateKey;
	}

	public void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}

	public void writeTempToFile(FileOutputStream fos, byte[] key)
			throws IOException {
		fos.write(key);

	}

	public void decryptProjetJava(String pathDirectory,
			final String fileKeyPrivate) throws IOException {
		long start = System.currentTimeMillis();
		decryptProjetJ(pathDirectory, fileKeyPrivate);
		LOGGER.info("nombre de fichier traité " + countAllFileModeDeCrypt);
		LOGGER.info("nombre de fichier décrypté " + countFileDecrpypt);
		long end = System.currentTimeMillis() - start;
		LOGGER.info("decrypt Durée en minutes  "
				+ Duration.ofMillis(end).toMinutes());
	}

	public void decryptProjetJ(String pathDirectory, final String fileKeyPrivate)
			throws IOException {
		Predicate<Path> predicate = new Predicate<Path>() {
			@Override
			public boolean test(Path path) {
				countAllFileModeDeCrypt++;
				return path.toFile().isDirectory()
						|| path.toAbsolutePath().toString()
								.contains(ADD_FILE_CRYPT_XOOL);
			}

		};
		decryptDirectory(pathDirectory, fileKeyPrivate, predicate);
	}

	public void decryptDirectoryComplet(String pathDirectory,
			final String fileKeyPrivate) throws IOException {
		long start = System.currentTimeMillis();
		Predicate<Path> predicate = new Predicate<Path>() {
			@Override
			public boolean test(Path path) {
				countAllFileModeDeCrypt++;
				return path.toFile().isDirectory()
						|| path.toAbsolutePath().toString()
								.contains(ADD_FILE_CRYPT_XOOL);
			}

		};
		decryptDirectoryWithDelete(pathDirectory, fileKeyPrivate, predicate);
		LOGGER.info("nombre de fichier traité " + countAllFileModeDeCrypt);
		LOGGER.info("nombre de fichier décrypté " + countFileDecrpypt);
		long end = System.currentTimeMillis() - start;
		LOGGER.info("decrypt Durée en minutes  "
				+ Duration.ofMillis(end).toMinutes());
	}

	public void decryptDirectoryWithDelete(String pathDirectory,
			final String fileKeyPrivate, final Predicate<Path> predicate)
			throws IOException {
		Stream<Path> pathStream = Files.list(Paths.get(pathDirectory)).filter(
				predicate);
		if (predicate != null) {
			pathStream = Files.list(Paths.get(pathDirectory)).filter(predicate);
		} else {
			pathStream = Files.list(Paths.get(pathDirectory));
		}

		Consumer<Path> actionDecrypt = new Consumer<Path>() {
			@Override
			public void accept(Path path) {
				try {
					LOGGER.info(" decryptProjetJava file name "
							+ path.toAbsolutePath().toString());
					if (path.toFile().isDirectory()) {
						LOGGER.info(" decryptProjetJava répertoire name "
								+ path.toAbsolutePath().toString());
						decryptDirectoryWithDelete(path.toAbsolutePath()
								.toString(), fileKeyPrivate, predicate);
					} else {
						LOGGER.info(" decryptProjetJava file name "
								+ path.toAbsolutePath().toString());

						decryptFile(
								path.toAbsolutePath().toString(),
								path.toAbsolutePath().toString()
										.replace(ADD_FILE_CRYPT_XOOL, ""),
								fileKeyPrivate);
						countFileDecrpypt++;
						Files.delete(path);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

			}

		};

		pathStream.forEach(actionDecrypt);
	}

	public void decryptDirectory(String pathDirectory,
			final String fileKeyPrivate, final Predicate<Path> predicate)
			throws IOException {
		Stream<Path> pathStream = Files.list(Paths.get(pathDirectory)).filter(
				predicate);
		if (predicate != null) {
			pathStream = Files.list(Paths.get(pathDirectory)).filter(predicate);
		} else {
			pathStream = Files.list(Paths.get(pathDirectory));
		}

		Consumer<Path> actionDecrypt = new Consumer<Path>() {
			@Override
			public void accept(Path path) {
				try {
					LOGGER.info(" decryptProjetJava file name "
							+ path.toAbsolutePath().toString());
					if (path.toFile().isDirectory()) {
						LOGGER.info(" decryptProjetJava répertoire name "
								+ path.toAbsolutePath().toString());
						decryptDirectory(path.toAbsolutePath().toString(),
								fileKeyPrivate, predicate);
					} else {
						LOGGER.info(" decryptProjetJava file name "
								+ path.toAbsolutePath().toString());
						countFileDecrpypt++;
						decryptFile(
								path.toAbsolutePath().toString(),
								path.toAbsolutePath().toString()
										.replace(ADD_FILE_CRYPT_XOOL, ""),
								fileKeyPrivate);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

			}

		};

		pathStream.forEach(actionDecrypt);
	}

	public void decryptFile(byte[] input, String pathOutput, PrivateKey key)
			throws IOException, GeneralSecurityException {
		cipher.init(Cipher.DECRYPT_MODE, key);
		writeToFile(pathOutput, cipher.doFinal(input));
	}

	public void decryptFile(byte[] input, String pathOutput, String fileKeyPrive)
			throws IOException, GeneralSecurityException, RSAException {
		this.decryptFile(input, pathOutput, getPrivateKeyFromFile(fileKeyPrive));
	}

	public void decryptFile(String inputFile, String pathOutput,
			String fileKeyPrive) throws IOException, GeneralSecurityException,
			RSAException {
		RSAPrivateKey privateKey = getPrivateKeyFromFile(fileKeyPrive);
		int maxSizeByteDecrypt = privateKey.getModulus().bitLength() / 8;
		LOGGER.info("size clé privé " + privateKey.getModulus().bitLength());
		if (Paths.get(inputFile).toFile().length() <= maxSizeByteDecrypt) {
			this.decryptFile(Files.readAllBytes(Paths.get(inputFile)),
					pathOutput, privateKey);
		} else {
			File fileOuput = new File(pathOutput);
			fileOuput.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(fileOuput);
			byte[] input = new byte[maxSizeByteDecrypt];
			FileInputStream fileInputStream = new FileInputStream(Paths.get(
					inputFile).toFile());
			while (fileInputStream.read(input) > 0) {
				this.decryptFile(input, fos, privateKey);
			}
			int nbRead = 0;
			while ((nbRead = fileInputStream.read(input)) > 0) {
				byte[] inputRead = new byte[nbRead];
				if (nbRead < maxSizeByteDecrypt) {
					for (int i = 0; i < nbRead; i++) {
						inputRead[i] = input[i];
					}
					this.decryptFile(inputRead, fos, privateKey);
				} else {
					this.decryptFile(input, fos, privateKey);
				}

			}
			fileInputStream.close();
			fos.close();
		}

	}

	public void decryptFile(byte[] input, FileOutputStream fos,
			PrivateKey privateKey) throws IOException,
			GeneralSecurityException, RSAException {
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		writeTempToFile(fos, cipher.doFinal(input));
	}

	public String decryptText(String msg, PrivateKey key)
			throws InvalidKeyException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.getDecoder().decode(msg)),
				"UTF-8");
	}

}
