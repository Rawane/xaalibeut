package com.xoolibeut.crypt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XoolibeutZipIn {
	private static final Logger LOGGER = LoggerFactory.getLogger(XoolibeutZipIn.class);

	public static void main(String[] args) {

	}

	public void zipDirectory(String fileSource) {
		byte[] buffer = new byte[1024];
		try {
			FileOutputStream fos = new FileOutputStream(fileSource + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
			final List<String> listFile = new ArrayList<>();
			listAllFiles(fileSource, listFile);

			for (String file : listFile) {
				LOGGER.info("File Added : " + file.replace(fileSource, ""));
				ZipEntry ze = new ZipEntry(file.replace(fileSource, ""));
				zos.putNextEntry(ze);
				FileInputStream in = new FileInputStream(file);
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				in.close();
			}

			zos.closeEntry();			
			zos.close();

			System.out.println("Done");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void listAllFiles(final String fileSource, final List<String> listFile) throws IOException {

		Consumer<Path> action = new Consumer<Path>() {

			@Override
			public void accept(Path path) {
				if (path.toFile().isDirectory()) {
					try {
						listAllFiles(path.toAbsolutePath().toString(), listFile);
					} catch (IOException exception) {

						exception.printStackTrace();
					}
				} else {
					listFile.add(path.toAbsolutePath().toString());
				}
			}
		};
		Files.list(Paths.get(fileSource)).forEach(action);
	}
}
