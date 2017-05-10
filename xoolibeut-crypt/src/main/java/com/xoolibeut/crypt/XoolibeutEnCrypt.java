package com.xoolibeut.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XoolibeutEnCrypt {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutEnCrypt.class);
	private static final String ADD_FILE_CRYPT_XOOL = "_xool";
	private Predicate<Path> predicate;
	private Cipher cipher;
	private String source;
	private Metric metric;
	private RSAPublicKey publicKey;
	private TypeProjet typeProjet;

	/**
	 * XoolibeutEncrypt.
	 * 
	 * @param builder
	 */
	public XoolibeutEnCrypt(final Builder builder) {
		this.cipher = builder.cipher;
		this.source = builder.source;
		this.predicate = builder.predicate;
		this.publicKey = builder.publicKey;
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
	public void writeToFile(String pathSource, byte[] ouputBytes)
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
	public void writeTempToFile(FileOutputStream fos, byte[] ouputBytes)
			throws IOException {
		fos.write(ouputBytes);

	}

	/**
	 * finalise l'actionde cryptage.
	 * 
	 * @throws IOException
	 */
	public void doFinal() throws IOException {
		long start = System.currentTimeMillis();
		encryptDirectory(this.source);
		LOGGER.info("nombre de fichier traité "
				+ metric.getCountAllFilInFolder());
		LOGGER.info("nombre de fichier encrypté " + metric.getCountFileTraite());
		LOGGER.info("Toal data crypté " + metric.formatTotalSize());
		long end = System.currentTimeMillis() - start;
		LOGGER.info("Encrypt Durée en minutes  "
				+ Duration.ofMillis(end).toMinutes());
	}

	/**
	 * crypter un répertoire passé en paramètre.
	 * 
	 * @param pathDirectory
	 * @throws IOException
	 */
	private void encryptDirectory(String pathDirectory) throws IOException {

		Consumer<Path> action = new Consumer<Path>() {
			@Override
			public void accept(Path path) {
				try {
					String pathAbsoluteFile = path.toAbsolutePath().toString();
					if (path.toFile().isDirectory()) {
						LOGGER.info("encryptDirectory répertoire  "
								+ pathAbsoluteFile);
						encryptDirectory(pathAbsoluteFile);
					} else {
						LOGGER.info("encryptDirectory nom du fichier cripté "
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
						encryptFile(pathAbsoluteFile, nomFileCrypte);
						metric.setCountFileTraite(metric.getCountFileTraite() + 1);
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
		Stream<Path> pathStream = Files.list(Paths.get(pathDirectory)).filter(
				predicate);
		pathStream.forEach(action);
	}

	/**
	 * crypter un fichier en entier. le fichier de sortie est nommé
	 * filename_xool.extension
	 * 
	 * @param inputFile
	 * @param pathOutput
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws RSAException
	 */
	private void encryptFile(String inputFile, String pathOutput)
			throws IOException, GeneralSecurityException, RSAException {

		int maxSizeByteEncrypt = publicKey.getModulus().bitLength() / 8 - 11;
		// LOGGER.debug("nombre de bloc à crypter " + maxSizeByteEncrypt);
		if (Files.size(Paths.get(inputFile)) <= maxSizeByteEncrypt) {
			this.encryptLittleFile(Files.readAllBytes(Paths.get(inputFile)),
					pathOutput);
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
					this.encryptByte(inputRead, fos);
				} else {
					this.encryptByte(input, fos);
				}

			}
			fileInputStream.close();
			fos.close();
		}

	}

	/**
	 * cypter un petit fichier. *
	 * 
	 * @param input
	 * @param pathOutput
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private void encryptLittleFile(byte[] input, String pathOutput)
			throws IOException, GeneralSecurityException {
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		writeToFile(pathOutput, cipher.doFinal(input));
	}

	/**
	 * encrypter un tableau de byte .
	 * 
	 * @param input
	 * @param fos
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private void encryptByte(byte[] input, FileOutputStream fos)
			throws IOException, GeneralSecurityException {
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
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
		private RSAPublicKey publicKey;
		private TypeProjet typeProjet;
		private String noCryptFolder;
		private Metric metric = new Metric();

		public Builder() {

		}

		public Builder(String publicKeyFile) {
			try {
				this.publicKey = buildKeyPublic(publicKeyFile);
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
				this.publicKey = buildKeyPublic(publicKeyFile);
			} catch (RSAException exception) {
				LOGGER.error("erreur ", exception);
			}
			return this;
		}

		public Builder predicate() {
			if (TypeProjet.DOSSIER.equals(typeProjet)) {
				this.predicate = new Predicate<Path>() {
					@Override
					public boolean test(Path path) {
						metric.setCountAllFilInFolder(metric
								.getCountAllFilInFolder() + 1);
						return !path.toAbsolutePath().toString()
								.contains(ADD_FILE_CRYPT_XOOL);
					}

				};
			} else {
				if (TypeProjet.JAVA.equals(typeProjet)) {
					final List<String> listNoCrypt;
					if (noCryptFolder != null) {
						listNoCrypt = Arrays.asList(this.noCryptFolder
								.split(";"));
					} else {
						listNoCrypt = Collections.emptyList();
					}
					this.predicate = new Predicate<Path>() {
						@Override
						public boolean test(Path path) {
							metric.setCountAllFilInFolder(metric
									.getCountAllFilInFolder() + 1);
							String absolutePath = path.toAbsolutePath()
									.toString();
							return !absolutePath.contains(ADD_FILE_CRYPT_XOOL)
									&& ((path.toFile().isDirectory() && !listNoCrypt
											.contains(path.getFileName()
													.toString())) || (absolutePath
											.endsWith(".java")
											|| absolutePath
													.endsWith(".properties")
											|| absolutePath.endsWith(".css")
											|| absolutePath.endsWith(".js")
											|| absolutePath.endsWith(".html")
											|| absolutePath.endsWith(".sql")
											|| absolutePath.endsWith(".xml")
											|| absolutePath.endsWith(".png") || absolutePath
												.endsWith(".jpg")));
						}

					};
				}
			}
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

		/**
		 * ne cripte pas ce dossier.*
		 * 
		 * @param noCryptFolder
		 */
		public Builder noCrypt(String noCryptFolder) {
			this.noCryptFolder = noCryptFolder;
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

		public XoolibeutEnCrypt build() {
			return new XoolibeutEnCrypt(this);
		}

		/**
		 * 
		 * @param publicKeyFile
		 * @return
		 * @throws RSAException
		 */
		private RSAPublicKey buildKeyPublic(String publicKeyFile)
				throws RSAException {
			RSAPublicKey publicKey = null;
			try {
				publicKey = RSAPublicKeyUtility
						.convertToRSAPublicKey(new String(Files
								.readAllBytes(Paths.get(publicKeyFile))));
			} catch (IOException exception) {
				throw new RSAException("erreur buildKeyPublic ", exception);
			}
			return publicKey;
		}

	}

	/**
	 * @return the publicKey
	 */
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

	/**
	 * @param publicKey
	 *            the publicKey to set
	 */
	public void setPublicKey(RSAPublicKey publicKey) {
		this.publicKey = publicKey;
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
