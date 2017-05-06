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
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
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
public class XoolibeutCryptUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutCryptUtil.class);
	private Cipher cipher;
	private int maxSizeByteEncrypt = 117;
	private int maxSizeByteDecrypt = 128;
	private int countFileDecrpypt = 0;
	private int countFileEncrpypt = 0;
	private int countAllFileModeDeCrypt = 0;
	private int countAllFileModeEnCrypt = 0;

	/**
	 * 
	 */
	public XoolibeutCryptUtil() {
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XoolibeutCryptUtil xool = new XoolibeutCryptUtil();
		try {
			xool.encryptProjetJava("D:\\devs\\tmp\\xoolibeut\\test_java",
					"D:\\devs\\tmp\\xoolibeut\\PublicKey.pem");
			xool.decryptProjetJava("D:\\devs\\tmp\\xoolibeut\\test_java",
					"D:\\devs\\tmp\\xoolibeut\\PrivateKey.pem");
			// xool.decryptFile("D:\\devs\\tmp\\xoolibeut\\test_crypt",
			// "D:\\devs\\tmp\\xoolibeut\\test_2.txt",
			// "D:\\devs\\tmp\\xoolibeut\\PrivateKey.pem");
		} catch (Exception exception) {
			LOGGER.error("erreur", exception);
		}
	}

	public RSAPrivateKey getPrivateKeyFromFile(String fileNameKey)
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

	public static RSAPublicKey getPublicKeyFromFile(String fileNameKey)
			throws RSAException {
		RSAPublicKey publicKey = null;
		try {
			publicKey = RSAPublicKeyUtility.convertToRSAPublicKey(new String(
					Files.readAllBytes(Paths.get(fileNameKey))));
		} catch (IOException exception) {

			exception.printStackTrace();
		}
		return publicKey;
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

	public void encryptFile(byte[] input, String pathOutput, PublicKey publicKey)
			throws IOException, GeneralSecurityException {
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		writeToFile(pathOutput, cipher.doFinal(input));
	}

	public void encryptFile(byte[] input, String pathOutput,
			String fileKeyPublic) throws IOException, GeneralSecurityException,
			RSAException {
		this.encryptFile(input, pathOutput, getPublicKeyFromFile(fileKeyPublic));
	}

	public void encryptFile(String inputFile, String pathOutput,
			String fileKeyPublic) throws IOException, GeneralSecurityException,
			RSAException {
		if (Paths.get(inputFile).toFile().length() <= maxSizeByteEncrypt) {
			this.encryptFile(Files.readAllBytes(Paths.get(inputFile)),
					pathOutput, fileKeyPublic);
		} else {
			File fileOuput = new File(pathOutput);
			fileOuput.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(fileOuput);
			byte[] input = new byte[maxSizeByteEncrypt];
			FileInputStream fileInputStream = new FileInputStream(Paths.get(
					inputFile).toFile());
			int nbRead = 0;
			while ((nbRead = fileInputStream.read(input)) > 0) {
				byte[] inputRead = new byte[nbRead];
				if (nbRead < maxSizeByteEncrypt) {
					for (int i = 0; i < nbRead; i++) {
						inputRead[i] = input[i];
					}
					this.encryptFile(inputRead, fos,
							getPublicKeyFromFile(fileKeyPublic));
				} else {
					this.encryptFile(input, fos,
							getPublicKeyFromFile(fileKeyPublic));
				}

			}
			fileInputStream.close();
			fos.close();
		}

	}

	public void encryptProjetJava(String pathDirectory,
			final String fileKeyPublic,String... noCryprFolder) throws IOException {
		long start = System.currentTimeMillis();
		encryptProjetJ(pathDirectory, fileKeyPublic);
		LOGGER.info("nombre de fichier traité " + countAllFileModeEnCrypt);
		LOGGER.info("nombre de fichier encrypté " + countFileEncrpypt);
		long end = System.currentTimeMillis() - start;
		LOGGER.info("Encrypt Durée en minutes  "
				+ Duration.ofMillis(end).toMinutes());
	}

	public void encryptDirectory(String pathDirectory,
			final String fileKeyPublic, final Predicate<Path> predicate)
			throws IOException {
		Stream<Path> pathStream;
		if (predicate != null) {
			pathStream = Files.list(Paths.get(pathDirectory)).filter(predicate);
		} else {
			pathStream = Files.list(Paths.get(pathDirectory));
		}
		Consumer<Path> action = new Consumer<Path>() {
			@Override
			public void accept(Path path) {
				try {

					if (path.toFile().isDirectory()) {
						LOGGER.info("encryptDirectory répertpoire  "
								+ path.toAbsolutePath().toString());
						encryptDirectory(path.toAbsolutePath().toString(),
								fileKeyPublic, predicate);
					} else {
						LOGGER.info("encryptDirectory file name "
								+ path.toAbsolutePath().toString());
						encryptFile(path.toAbsolutePath().toString(), path
								.toAbsolutePath().toString() + "_xool",
								fileKeyPublic);
						countFileEncrpypt++;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

			}

		};

		pathStream.forEach(action);
	}

	public void encryptProjetJ(String pathDirectory, final String fileKeyPublic,String... noCryprFolder)
			throws IOException {
		final List<String> listNoCrypt = Arrays.asList(noCryprFolder);
		Predicate<Path> predicate = new Predicate<Path>() {		
			@Override
			public boolean test(Path path) {
				countAllFileModeEnCrypt++;
				return (path.toFile().isDirectory() && !listNoCrypt.contains(path.toAbsolutePath().toString()))
						|| path.toAbsolutePath().toString().endsWith(".java")
						|| path.toAbsolutePath().toString()
								.endsWith(".properties")
						|| path.toAbsolutePath().toString().endsWith(".css")
						|| path.toAbsolutePath().toString().endsWith(".js")
						|| path.toAbsolutePath().toString().endsWith(".html")
						|| path.toAbsolutePath().toString().endsWith(".sql")
						|| path.toAbsolutePath().toString().endsWith(".xml")
						|| path.toAbsolutePath().toString().endsWith(".png")
						|| path.toAbsolutePath().toString().endsWith(".jpg");
			}

		};

		encryptDirectory(pathDirectory, fileKeyPublic, predicate);
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
						|| path.toAbsolutePath().toString().endsWith("_xool");
			}

		};
		decryptDirectory(pathDirectory, fileKeyPrivate, predicate);
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
										.replace("_xool", ""), fileKeyPrivate);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

			}

		};

		pathStream.forEach(actionDecrypt);
	}

	public void encryptFile(byte[] input, FileOutputStream fos,
			PublicKey publicKey) throws IOException, GeneralSecurityException {
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		writeTempToFile(fos, cipher.doFinal(input));
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
		if (Paths.get(inputFile).toFile().length() <= maxSizeByteDecrypt) {
			this.decryptFile(Files.readAllBytes(Paths.get(inputFile)),
					pathOutput, fileKeyPrive);
		} else {
			File fileOuput = new File(pathOutput);
			fileOuput.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(fileOuput);
			byte[] input = new byte[maxSizeByteDecrypt];
			FileInputStream fileInputStream = new FileInputStream(Paths.get(
					inputFile).toFile());
			while (fileInputStream.read(input) > 0) {
				this.decryptFile(input, fos,
						getPrivateKeyFromFile(fileKeyPrive));
			}
			int nbRead = 0;
			while ((nbRead = fileInputStream.read(input)) > 0) {
				byte[] inputRead = new byte[nbRead];
				if (nbRead < maxSizeByteDecrypt) {
					for (int i = 0; i < nbRead; i++) {
						inputRead[i] = input[i];
					}
					this.decryptFile(inputRead, fos,
							getPrivateKeyFromFile(fileKeyPrive));
				} else {
					this.decryptFile(input, fos,
							getPrivateKeyFromFile(fileKeyPrive));
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

	public String encryptText(String msg, PublicKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.getEncoder().encodeToString(
				cipher.doFinal(msg.getBytes("UTF-8")));
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
