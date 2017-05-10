package com.xoolibeut.crypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPublicKey;
import java.util.function.Predicate;

import javax.crypto.Cipher;

public class XoolibeutEnCrypt {
	private Predicate<Path> predicate;
	private String publicKeyFile;
	private Cipher cipher;
	private String source;
	private int countFileEncrpypt = 0;
	private int countAllFileModeEnCrypt = 0;

	/**
	 * XoolibeutEncrypt.
	 * 
	 * @param builder
	 */
	public XoolibeutEnCrypt(final Builder builder) {
		this.publicKeyFile = builder.publicKeyFile;
		this.cipher = builder.cipher;
		this.source = builder.source;
		this.predicate = builder.predicate;
	}

	public RSAPublicKey buildKeyPublic() throws RSAException {
		RSAPublicKey publicKey = null;
		try {
			publicKey = RSAPublicKeyUtility
					.convertToRSAPublicKey(new String(Files.readAllBytes(Paths.get(publicKeyFile))));
		} catch (IOException exception) {
			throw new RSAException("erreur buildKeyPublic ", exception);
		}
		return publicKey;
	}

	public void writeToFile(byte[] ouputBytes) throws IOException {
		File f = new File(source);
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(ouputBytes);
		fos.flush();
		fos.close();
	}

	public void writeTempToFile(FileOutputStream fos, byte[] ouputBytes) throws IOException {
		fos.write(ouputBytes);

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
		private String publicKeyFile;
		private Cipher cipher;
		private String source;

		public Builder() {

		}

		public Builder(String key) {
			this.publicKeyFile = key;
		}

		public void predicate(Predicate<Path> predicate) {
			this.predicate = predicate;
		}

		public void withKey(String publicKeyFile) {
			this.publicKeyFile = publicKeyFile;
		}

		public void cipher(Cipher cipher) {
			this.cipher = cipher;
		}

		public void source(String source) {
			this.source = source;
		}

		public XoolibeutEnCrypt build() {
			return new XoolibeutEnCrypt(this);
		}
	}

	public Predicate<Path> getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate<Path> predicate) {
		this.predicate = predicate;
	}

	public String getPublicKeyFile() {
		return publicKeyFile;
	}

	public void setPublicKeyFile(String publicKeyFile) {
		this.publicKeyFile = publicKeyFile;
	}

	public Cipher getCipher() {
		return cipher;
	}

	public void setCipher(Cipher cipher) {
		this.cipher = cipher;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
