package com.xoolibeut.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XoolibeutDeCrypt {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutDeCrypt.class);
	private static final String ADD_FILE_CRYPT_XOOL = "_xool";
	private Predicate<Path> predicate;
	private Cipher cipher;
	private String source;
	private Metric metric;
	private RSAPrivateKey privateKey;
	private TypeProjet typeProjet;

	/**
	 * XoolibeutEncrypt.
	 * 
	 * @param builder
	 */
	public XoolibeutDeCrypt(final Builder builder) {
		this.cipher = builder.cipher;
		this.source = builder.source;
		this.predicate = builder.predicate;
		this.privateKey = builder.privateKey;
		this.typeProjet = builder.typeProjet;
		this.metric = builder.metric;
	}

	/**
	 * Ecrit et ferme le fichier. *
	 * 
	 * @param pathSource
	 * @param ouputBytes
	 * @throws IOException
	 */
	private void writeToFile(String pathSource, byte[] ouputBytes)
			throws IOException {
		File f = new File(pathSource);
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(ouputBytes);
		fos.flush();
		fos.close();
	}

	/**
	 * Ecrit et lasse le fichier ouvert.
	 * 
	 * @param fos
	 * @param ouputBytes
	 * @throws IOException
	 */
	private void writeTempToFile(FileOutputStream fos, byte[] ouputBytes)
			throws IOException {
		fos.write(ouputBytes);

	}

	/**
	 * Finalise l'action de décryptage.
	 * 
	 * @throws IOException
	 */
	public void doFinal() throws IOException {
		long start = System.currentTimeMillis();
		decryptDirectory(this.source);
		LOGGER.info("nombre de fichier traité "
				+ metric.getCountAllFilInFolder());
		LOGGER.info("nombre de fichier decrypté " + metric.getCountFileTraite());
		LOGGER.info("Toal data décrypté " + metric.formatTotalSize());
		long end = System.currentTimeMillis() - start;
		LOGGER.info("Durée en minutes  " + Duration.ofMillis(end).toMinutes());
	}

	/**
	 * décrypte un répertoire. les fichier dont les noms contiennent le pattern
	 * _xool.
	 * 
	 * @param pathDirectory
	 * @throws IOException
	 */
	private void decryptDirectory(String pathDirectory) throws IOException {
		Stream<Path> pathStream = Files.list(Paths.get(pathDirectory)).filter(
				predicate);
		Consumer<Path> actionDecrypt = new Consumer<Path>() {
			@Override
			public void accept(Path path) {
				try {
					LOGGER.info(" decryptProjetJava file name "
							+ path.toAbsolutePath().toString());
					if (path.toFile().isDirectory()) {
						LOGGER.info(" decryptProjetJava répertoire name "
								+ path.toAbsolutePath().toString());
						decryptDirectory(path.toAbsolutePath().toString());
					} else {
						LOGGER.info(" decryptProjetJava file name "
								+ path.toAbsolutePath().toString());
						metric.setCountFileTraite(metric.getCountFileTraite() + 1);
						decryptFile(
								path.toAbsolutePath().toString(),
								path.toAbsolutePath().toString()
										.replace(ADD_FILE_CRYPT_XOOL, ""));
						metric.setTotalSize(metric.getTotalSize()
								+ Files.size(path));
						if (TypeProjet.DOSSIER.equals(typeProjet)) {
							Files.delete(path);
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

			}

		};

		pathStream.forEach(actionDecrypt);
	}

	/**
	 * Décripte un fichier en entier. un fichier dont le nom contient _xool
	 * 
	 * @param inputFile
	 * @param pathOutput
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws RSAException
	 */
	private void decryptFile(String inputFile, String pathOutput)
			throws IOException, GeneralSecurityException, RSAException {

		int maxSizeByteDecrypt = privateKey.getModulus().bitLength() / 8;
		// LOGGER.info("size clé privé " + privateKey.getModulus().bitLength());
		if (Files.size(Paths.get(inputFile)) <= maxSizeByteDecrypt) {
			this.decryptLittleFile(Files.readAllBytes(Paths.get(inputFile)),
					pathOutput);
		} else {
			File fileOuput = new File(pathOutput);
			fileOuput.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(fileOuput);
			byte[] input = new byte[maxSizeByteDecrypt];
			FileInputStream fileInputStream = new FileInputStream(Paths.get(
					inputFile).toFile());
			int nbRead = 0;
			while ((nbRead = fileInputStream.read(input)) > 0) {
				byte[] inputRead = new byte[nbRead];
				if (nbRead < maxSizeByteDecrypt) {
					for (int i = 0; i < nbRead; i++) {
						inputRead[i] = input[i];
					}
					this.decryptByte(inputRead, fos);
				} else {
					this.decryptByte(input, fos);
				}

			}
			fileInputStream.close();
			fos.close();
		}

	}

	/**
	 * décrypte un petit fichier.
	 * 
	 * @param input
	 * @param pathOutput
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private void decryptLittleFile(byte[] input, String pathOutput)
			throws IOException, GeneralSecurityException {
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		writeToFile(pathOutput, cipher.doFinal(input));
	}

	/**
	 * décypter un tableau de byte.
	 * 
	 * @param input
	 * @param fos
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws RSAException
	 */
	private void decryptByte(byte[] input, FileOutputStream fos)
			throws IOException, GeneralSecurityException, RSAException {
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		writeTempToFile(fos, cipher.doFinal(input));
	}

	/**
	 * builder clé privé.
	 * 
	 * @param key
	 * @return
	 */
	public static Builder builder(final String key) {
		return new Builder(key);
	}

	/**
	 * Classe Builder.
	 * 
	 * @author rawane
	 * 
	 */
	public static final class Builder {
		private Predicate<Path> predicate;
		private Cipher cipher;
		private String source;
		private RSAPrivateKey privateKey;
		private TypeProjet typeProjet;

		private Metric metric = new Metric();

		public Builder() {

		}

		public Builder(String privateKeyFile) {
			try {
				this.privateKey = buildPrivateKeyFromFile(privateKeyFile);
			} catch (RSAException exception) {
				LOGGER.error("Builder erreur ", exception);
			}
		}

		public Builder withPredicate(Predicate<Path> predicate) {
			this.predicate = predicate;
			return this;
		}

		public Builder withKey(String publicKeyFile) {
			try {
				this.privateKey = buildPrivateKeyFromFile(publicKeyFile);
			} catch (RSAException exception) {
				LOGGER.error("erreur ", exception);
			}
			return this;
		}

		public Builder predicate() {
			predicate = new Predicate<Path>() {
				@Override
				public boolean test(Path path) {
					metric.setCountAllFilInFolder(metric
							.getCountAllFilInFolder() + 1);
					return path.toFile().isDirectory()
							|| path.toAbsolutePath().toString()
									.contains(ADD_FILE_CRYPT_XOOL);
				}

			};
			return this;

		}

		/**
		 * 
		 * @param cipher
		 */
		public Builder cipher(Cipher cipher) {
			this.cipher = cipher;
			return this;
		}

		/**
		 * 
		 * @param projet
		 */
		public Builder type(TypeProjet projet) {
			this.typeProjet = projet;
			return this;
		}

		/**
		 * 
		 * @param source
		 */
		public Builder source(String source) {
			this.source = source;
			return this;
		}

		public Builder algo(String algo) {
			try {
				cipher = Cipher.getInstance(algo);
			} catch (Exception exception) {
				LOGGER.error("Builder erreur ", exception);
			}
			return this;
		}

		public Builder algoRSA() {
			algo("RSA");
			return this;
		}

		public XoolibeutDeCrypt build() {
			return new XoolibeutDeCrypt(this);
		}

		private RSAPrivateKey buildPrivateKeyFromFile(String fileNameKey)
				throws RSAException {
			RSAPrivateKey privateKey = null;
			try {
				privateKey = RSAPrivateKeyUtility
						.convertToRSAPrivateKey(new String(Files
								.readAllBytes(Paths.get(fileNameKey))));
			} catch (IOException exception) {

				exception.printStackTrace();
			}
			return privateKey;
		}
	}

	/**
	 * @return the typeProjet
	 */
	public TypeProjet getTypeProjet() {
		return typeProjet;
	}

	/**
	 * @param typeProjet
	 *            the typeProjet to set
	 */
	public void setTypeProjet(TypeProjet typeProjet) {
		this.typeProjet = typeProjet;
	}

	/**
	 * @return the cipher
	 */
	public Cipher getCipher() {
		return cipher;
	}

	/**
	 * @param cipher
	 *            the cipher to set
	 */
	public void setCipher(Cipher cipher) {
		this.cipher = cipher;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

}
