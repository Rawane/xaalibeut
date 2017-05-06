package com.xoolibeut.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XoolibeutZipOut {
	private static final Logger LOGGER = LoggerFactory.getLogger(XoolibeutZipOut.class);

	public void unZipDirectory(String zipFile) {

		byte[] buffer = new byte[1024];

		try {
			// create output directory is not exists
			File folder = new File(Paths.get(zipFile).toAbsolutePath().toString().replace(".zip", ""));
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(folder.getAbsolutePath() + File.separator + fileName);

				LOGGER.info("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			LOGGER.info("Done");

		} catch (IOException ioException) {
			LOGGER.info("erreur", ioException);
		}

	}
}
