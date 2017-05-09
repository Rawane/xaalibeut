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
import java.security.PublicKey;
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
public class XoolibeutEnCryptS {
	/**
	 * 
	 */
	private static final String ADD_FILE_CRYPT_XOOL = "_xool";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutEnCryptS.class);
	private Cipher cipher;
	private int countFileEncrpypt = 0;
	private int countAllFileModeEnCrypt = 0;
	private long totalSize = 0;

	/**
	 * 
	 */
	public XoolibeutEnCryptS() {
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Permet de charger la clès public.
	 * 
	 * @param fileNameKey
	 *            nom de la clès public
	 * @return
	 * @throws RSAException
	 */
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

	public void writeToFile(String path, byte[] ouputBytes) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(ouputBytes);
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
		RSAPublicKey publicKey = getPublicKeyFromFile(fileKeyPublic);
		int maxSizeByteEncrypt = publicKey.getModulus().bitLength() / 8 - 11;
		//LOGGER.debug("nombre de bloc à crypter " + maxSizeByteEncrypt);
		if (Files.size(Paths.get(inputFile)) <= maxSizeByteEncrypt) {
			this.encryptFile(Files.readAllBytes(Paths.get(inputFile)),
					pathOutput, publicKey);
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
					this.encryptFile(inputRead, fos, publicKey);
				} else {
					this.encryptFile(input, fos, publicKey);
				}

			}
			fileInputStream.close();
			fos.close();
		}

	}

	public void encryptProjetJava(String pathDirectory,
			final String fileKeyPublic, String... noCryprFolder)
			throws IOException {
		long start = System.currentTimeMillis();
		encryptProjetJ(pathDirectory, fileKeyPublic, noCryprFolder);
		LOGGER.info("nombre de fichier traité " + countAllFileModeEnCrypt);
		LOGGER.info("nombre de fichier encrypté " + countFileEncrpypt);
		LOGGER.info("Toal data crypté " + formatTotalSize());
		long end = System.currentTimeMillis() - start;
		LOGGER.info("Encrypt Durée en minutes  "
				+ Duration.ofMillis(end).toMinutes());		
	}

	public void encryptDirectoryComplet(String pathDirectory,
			final String fileKeyPublic) throws IOException {
		long start = System.currentTimeMillis();
		encryptDirectoryWithDelete(pathDirectory, fileKeyPublic, null);
		LOGGER.info("nombre de fichier traité " + countAllFileModeEnCrypt);
		LOGGER.info("nombre de fichier encrypté " + countFileEncrpypt);
		LOGGER.info("Toal data crypté " + formatTotalSize());
		long end = System.currentTimeMillis() - start;
		LOGGER.info("Encrypt Durée en minutes  "
				+ Duration.ofMillis(end).toMinutes());
	}

	public void encryptDirectoryWithDelete(String pathDirectory,
			final String fileKeyPublic, final Predicate<Path> predicate)
			throws IOException {
		this.encryptDirectory(pathDirectory, fileKeyPublic, predicate, true);
	}

	/**
	 * crypter un répertoire avec une clès public. *
	 * 
	 * @param pathDirectory
	 * @param fileKeyPublic
	 * @param predicate
	 * @throws IOException
	 */
	private void encryptDirectory(String pathDirectory,
			final String fileKeyPublic, final Predicate<Path> predicate,
			final boolean deleteFile) throws IOException {
		Stream<Path> pathStream;
		if (predicate != null) {
			pathStream = Files.list(Paths.get(pathDirectory)).filter(predicate);
		} else {
			Predicate<Path> predicateXool = new Predicate<Path>() {
				@Override
				public boolean test(Path path) {
					countAllFileModeEnCrypt++;
					return !path.toAbsolutePath().toString()
							.contains(ADD_FILE_CRYPT_XOOL);
				}

			};
			pathStream = Files.list(Paths.get(pathDirectory)).filter(
					predicateXool);
		}
		Consumer<Path> action = new Consumer<Path>() {
			@Override
			public void accept(Path path) {
				try {
					String pathAbsoluteFile = path.toAbsolutePath().toString();
					if (path.toFile().isDirectory()) {
						LOGGER.info("encryptDirectory répertpoire  "
								+ pathAbsoluteFile);
						encryptDirectory(pathAbsoluteFile, fileKeyPublic,
								predicate, deleteFile);
					} else {
						LOGGER.info("encryptDirectory file name "
								+ path.toAbsolutePath().toString());

						int lastIndex = pathAbsoluteFile.lastIndexOf(".");
						String nomFileCrypte;
						if (lastIndex > 0) {
							nomFileCrypte = pathAbsoluteFile.substring(0,
									lastIndex)
									+ ADD_FILE_CRYPT_XOOL
									+ pathAbsoluteFile.substring(lastIndex);
						} else {
							nomFileCrypte = pathAbsoluteFile
									+ ADD_FILE_CRYPT_XOOL;
						}
						encryptFile(pathAbsoluteFile, nomFileCrypte,
								fileKeyPublic);
						countFileEncrpypt++;
						totalSize = totalSize + Files.size(path);
						if (deleteFile) {
							Files.delete(path);
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

			}

		};

		pathStream.forEach(action);
	}

	private void encryptProjetJ(String pathDirectory,
			final String fileKeyPublic, String... noCryprFolder)
			throws IOException {
		final List<String> listNoCrypt = Arrays.asList(noCryprFolder);
		Predicate<Path> predicate = new Predicate<Path>() {
			@Override
			public boolean test(Path path) {
				countAllFileModeEnCrypt++;
				String absolutePath = path.toAbsolutePath().toString();
				return !absolutePath.contains(ADD_FILE_CRYPT_XOOL)
						&& ((path.toFile().isDirectory() && !listNoCrypt
								.contains(path.getFileName().toString())) || (absolutePath
								.endsWith(".java")
								|| absolutePath.endsWith(".properties")
								|| absolutePath.endsWith(".css")
								|| absolutePath.endsWith(".js")
								|| absolutePath.endsWith(".html")
								|| absolutePath.endsWith(".sql")
								|| absolutePath.endsWith(".xml")
								|| absolutePath.endsWith(".png") || absolutePath
									.endsWith(".jpg")));
			}

		};

		encryptDirectory(pathDirectory, fileKeyPublic, predicate, false);
	}

	private void encryptFile(byte[] input, FileOutputStream fos,
			PublicKey publicKey) throws IOException, GeneralSecurityException {
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		writeTempToFile(fos, cipher.doFinal(input));
	}

	public String encryptText(String msg, PublicKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.getEncoder().encodeToString(
				cipher.doFinal(msg.getBytes("UTF-8")));
	}

	private String formatTotalSize() {
		if (totalSize < 1024) {
			return totalSize + " Octet";
		}
		if (totalSize < 1024 * 1024) {
			return (totalSize / (1024)) + " KO";
		}
		if (totalSize < 1024 * 1024 * 1024) {
			return (totalSize / (1024 * 1024)) + " MO";
		} else {
			return (totalSize / (1024 * 1024)) + " GO";
		}
	}

}
