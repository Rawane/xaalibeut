/**
 * 
 */
package com.xoolibeut.crypt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cer4495267
 * 
 */
public class XoolibeutFiles {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutFiles.class);
	private String repSource;
	private long sizeSousRep;
	private String prefixRepertoire;

	/**
 * 
 */
	public XoolibeutFiles(Builder builder) {
		this.repSource = builder.repSource;
		this.sizeSousRep = builder.size;
		this.prefixRepertoire = builder.prefix;
	}

	public static Builder builder(final String source) {
		return new Builder(source);
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void arrangeFiles(String fileName) throws IOException {
		LOGGER.info("arrange le répertoire " + fileName);
		Path path = Paths.get(fileName);
		final List<Path> childDirectories = new ArrayList<Path>();
		final List<Path> files = new ArrayList<Path>();
		if (path.toFile().isDirectory()) {
			LOGGER.info("arrange le répertoire taille fichier  "
					+ Files.size(path) + " Octet");
			// if (Files.size(path) > sizeSousRep) {

			Consumer<Path> action = new Consumer<Path>() {
				@Override
				public void accept(Path pathChild) {
					if (pathChild.toFile().isDirectory()) {
						childDirectories.add(pathChild);
					} else {
						files.add(pathChild);
					}

				}
			};
			Files.list(path).forEach(action);
			createChildDirectoryIsNecessary(files);
			for (Path pathDPath : childDirectories) {
				arrangeFiles(pathDPath.toAbsolutePath().toString());
			}
			// }

		}

	}

	public void arrangeFiles() throws IOException {
		arrangeFiles(this.repSource);

	}

	/**
	 * Crée un repertoire et déplace les fichiers
	 * 
	 * @param files
	 * @throws IOException
	 */
	private void createChildDirectoryIsNecessary(List<Path> files)
			throws IOException {
		long sizeCalc = 0;
		int index = 1;
		List<Path> filesChildDirect = new ArrayList<>();
		for (Path path : files) {
			LOGGER.info("arrange le répertoire " + path.getFileName()
					+ " size fichier " + Files.size(path) + " size calculé "
					+ sizeCalc);
			if (Files.size(path) >= sizeSousRep) {
				Path pathChildDir = Files.createDirectory(Paths.get(path
						.getParent().toAbsolutePath()
						+ File.separator
						+ prefixRepertoire + index++));
				Files.move(
						path,
						Paths.get(pathChildDir.toAbsolutePath().toString()
								+ File.separator + path.getFileName()));
			} else {
				if (sizeCalc >= sizeSousRep) {
					Path pathChildDir = Files.createDirectory(Paths.get(path
							.getParent().toAbsolutePath()
							+ File.separator
							+ prefixRepertoire + index++));
					for (Path pathFile : filesChildDirect) {
						Files.move(pathFile, Paths.get(pathChildDir
								.toAbsolutePath().toString()
								+ File.separator
								+ pathFile.getFileName()));
					}
					filesChildDirect = new ArrayList<>();
					filesChildDirect.add(path);
					sizeCalc = Files.size(path);
				} else {
					sizeCalc = sizeCalc + Files.size(path);
					filesChildDirect.add(path);
				}
			}
		}
		if (!filesChildDirect.isEmpty()) {
			Path pathChildDir = Files.createDirectory(Paths
					.get(filesChildDirect.get(0).getParent().toAbsolutePath()
							+ File.separator + prefixRepertoire + index));
			for (Path pathFile : filesChildDirect) {
				Files.move(
						pathFile,
						Paths.get(pathChildDir.toAbsolutePath().toString()
								+ File.separator + pathFile.getFileName()));
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Files.move(
			// Paths.get("D:\\devs\\test\\ManagementController.java.xool"),
			// Paths.get("D:\\devs\\test\\part1\\"
			// + Paths.get(
			// "D:\\devs\\test\\ManagementController.java.xool")
			// .getFileName()));
			XoolibeutFiles.builder("D:\\devs\\test").size(5 * 1024 * 1024)
					.atPrefix("part").build().arrangeFiles();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @return the repSource
	 */
	public String getRepSource() {
		return repSource;
	}

	/**
	 * @param repSource
	 *            the repSource to set
	 */
	public void setRepSource(String repSource) {
		this.repSource = repSource;
	}

	public static final class Builder {
		private String repSource;
		private long size;
		private String prefix;

		public Builder source(String repertoire) {
			this.repSource = repertoire;
			return this;
		}

		public Builder size(long size) {
			this.size = size;
			return this;
		}

		public Builder atPrefix(String prefix) {
			this.prefix = prefix;
			return this;
		}

		public XoolibeutFiles build() {
			return new XoolibeutFiles(this);
		}

		/**
	 * 
	 */
		public Builder() {

		}

		public Builder(String source) {
			this.repSource = source;
		}
	}

	/**
	 * @return the sizeSousRep
	 */
	public long getSizeSousRep() {
		return sizeSousRep;
	}

	/**
	 * @param sizeSousRep
	 *            the sizeSousRep to set
	 */
	public void setSizeSousRep(long sizeSousRep) {
		this.sizeSousRep = sizeSousRep;
	}
}
