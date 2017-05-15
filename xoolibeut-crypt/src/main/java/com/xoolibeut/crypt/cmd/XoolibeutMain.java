package com.xoolibeut.crypt.cmd;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xoolibeut.crypt.TypeProjet;
import com.xoolibeut.crypt.XoolibeutDeCrypt;
import com.xoolibeut.crypt.XoolibeutDeCrypt.Builder;
import com.xoolibeut.crypt.XoolibeutEnCrypt;
import com.xoolibeut.crypt.XoolibeutRSAUtil;
import com.xoolibeut.crypt.XoolibeutZipIn;
import com.xoolibeut.crypt.XoolibeutZipOut;

public class XoolibeutMain {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XoolibeutMain.class);

	public static void main(String[] args) {
		XoolibeutZipIn zipIn = new XoolibeutZipIn();
		XoolibeutZipOut zipOut = new XoolibeutZipOut();
		final Options options = configParameters();
		final CommandLineParser parser = new DefaultParser();
		try {
			printHelp(args);
			final CommandLine line = parser.parse(options, args);
			String commandeLine = "";
			for (String arg : args) {
				commandeLine = commandeLine + " " + arg;
			}
			LOGGER.info(" RSA commande " + commandeLine);
			line.getOptionValue("rsa");
			if (line.hasOption("r")) {
				if (line.getOptionValue("dest") == null) {
					printCommandeEchec(args);
				}
				XoolibeutRSAUtil.generateKey(line.getOptionValue("dest"),
						Integer.parseInt(line.getOptionValue("rsasize")));
			}

			// encrypter un dossier
			if (line.hasOption("ed")) {
				if (line.getOptionValue("asource") == null
						|| line.getOptionValue("bkey") == null) {
					printCommandeEchec(args);
				}
				XoolibeutEnCrypt.builder(line.getOptionValue("bkey"))
						.algo(line.getOptionValue("algo"))
						.source(line.getOptionValue("asource"))
						.type(TypeProjet.DOSSIER)
						.withPass(line.getOptionValue("pass")).build()
						.doFinal();

			}
			// decrypter un dossier
			if (line.hasOption("dd")) {

				if (line.getOptionValue("asource") == null
						|| line.getOptionValue("bkey") == null) {
					printCommandeEchec(args);
				}
				// dezipper le fichier
				if (line.hasOption("zo")) {
					zipOut.unZipDirectory(line.getOptionValue("asource")
							+ ".zip");
					if (line.hasOption("z")) {
						deleteFile(Paths.get(line.getOptionValue("asource")
								+ ".zip"));
					}
				}
				Builder builderDecrypt = XoolibeutDeCrypt
						.builder(line.getOptionValue("bkey"))
						.algo(line.getOptionValue("algo"))
						.source(line.getOptionValue("asource"))
						.type(TypeProjet.DOSSIER);
				if (line.getOptionValue("pass") != null) {
					builderDecrypt.withPass(line.getOptionValue("pass"));
				}
				builderDecrypt.build().doFinal();

			}
			// encrypter un projet java
			if (line.hasOption("ej")) {
				if (line.getOptionValue("asource") == null
						|| line.getOptionValue("bkey") == null) {
					printCommandeEchec(args);
				}
				// encrypt.encryptProjetJava(line.getOptionValue("asource"),
				// line.getOptionValue("bkey"),
				// line.getOptionValue("nocrypt").split(";"));

				XoolibeutEnCrypt.builder(line.getOptionValue("bkey"))
						.algo(line.getOptionValue("algo"))
						.source(line.getOptionValue("asource"))
						.noCrypt(line.getOptionValue("nocrypt"))
						.type(TypeProjet.JAVA)
						.withPass(line.getOptionValue("pass")).build()
						.doFinal();

			}
			// decrypter un projet java
			if (line.hasOption("dj")) {
				if (line.getOptionValue("asource") == null
						|| line.getOptionValue("bkey") == null) {
					printCommandeEchec(args);
				}

				Builder builderDecrypt = XoolibeutDeCrypt
						.builder(line.getOptionValue("bkey"))
						.algo(line.getOptionValue("algo"))
						.source(line.getOptionValue("asource"))
						.type(TypeProjet.JAVA);
				if (line.getOptionValue("pass") != null) {
					builderDecrypt.withPass(line.getOptionValue("pass"));
				}
				builderDecrypt.build().doFinal();

			}
			if (line.hasOption("zi")) {
				if (line.getOptionValue("asource") == null) {
					printCommandeEchec(args);
				}
				zipIn.zipDirectory(line.getOptionValue("asource"));
				if (line.hasOption("z")) {
					deleteFile(Paths.get(line.getOptionValue("asource")));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Erreur ", exception);
		}
	}

	/**
	 * Configurer la liste d'options.
	 * 
	 * @return
	 */
	private static Options configParameters() {
		final Option rsaGenKeyOption = Option
				.builder("r")
				.longOpt("rsa")
				.desc("Génération de clé privé et public, utiliser avec option -d et -s ")
				.hasArg(false).required(false).build();
		final Option encryptProjetJava = Option.builder("ej")
				.longOpt("encpjava").desc("Crypter un projet java")
				.hasArg(false).required(false).build();
		final Option decryptProjetJava = Option.builder("dj")
				.longOpt("decpjava").desc("Decrypter un projet java")
				.hasArg(false).required(false).build();
		final Option encryptDossier = Option.builder("ed").longOpt("encpdoss")
				.desc("Crypter un dossier, utiliser avec -a   et -b ")
				.hasArg(false).argName("encdoss").required(false).build();
		final Option decryptDossier = Option.builder("dd").longOpt("decpdoss")
				.desc("Decrypter un dossier").hasArg(false).required(false)
				.build();
		final Option destOptionKey = Option
				.builder("d")
				.longOpt("dest")
				.desc("Répertoire de destination de la paire de clés exemple -d=/home/user/key")
				.argName("dest").hasArg(true).required(false).build();
		final Option pathDossierOption = Option
				.builder("a")
				.longOpt("asource")
				.desc("Répertoire à crypter ou decrypter exemple -a=/home/user/docs")
				.argName("source").hasArg(true).required(false).build();
		final Option keyFileOption = Option
				.builder("b")
				.longOpt("bkey")
				.desc("Clés privé ou public format PEM exemple -b=/home/user/key/Key.pem,possible de diviser la privé en plusieurs fichiers séparé par ; ")
				.argName("key").hasArg(true).required(false).build();
		final Option zipInputFileOption = Option.builder("zi").longOpt("zipin")
				.desc("zipper un repertoire").hasArg(false).required(false)
				.build();
		final Option zipOuputFileOption = Option.builder("zo")
				.longOpt("zipouput").desc("dézipper un repertoire")
				.hasArg(false).required(false).build();
		final Option suppDossierFileOption = Option
				.builder("z")
				.longOpt("supp")
				.desc("supprime le repertoire après traitement, utiliser avec -a")
				.hasArg(false).required(false).build();
		final Option dossierNonCrypt = Option
				.builder("n")
				.longOpt("nocrypt")
				.desc("choisir un ou plusieurs dossier à ne pas crypter suivi, utiliser avec -a")
				.argName("nocrypt").hasArg(true).required(false).build();
		final Option rsaSizeKeyAlgo = Option
				.builder("s")
				.longOpt("rsasize")
				.desc("choisir la taille de la clé pour algo RSA 1024,2048 exemple -s=2048")
				.argName("rsasize").hasArg(true).required(false).build();
		final Option passwordOption = Option
				.builder("p")
				.longOpt("pass")
				.desc("A renseigner que si vous avez modifié votre clé privé par souci de sécurité")
				.argName("pass").hasArg(true).required(false).build();
		final Option algoOption = Option.builder("al").longOpt("algo")
				.desc("Algo à renseigner pour preciser RSA ou AES")
				.argName("algo").hasArg(true).required(false).build();

		final Options options = new Options();
		options.addOption(rsaGenKeyOption);
		options.addOption(encryptProjetJava);
		options.addOption(decryptProjetJava);
		options.addOption(encryptDossier);
		options.addOption(decryptDossier);
		options.addOption(destOptionKey);
		options.addOption(pathDossierOption);
		options.addOption(keyFileOption);
		options.addOption(zipInputFileOption);
		options.addOption(zipOuputFileOption);
		options.addOption(suppDossierFileOption);
		options.addOption(dossierNonCrypt);
		options.addOption(rsaSizeKeyAlgo);
		options.addOption(passwordOption);
		options.addOption(algoOption);
		return options;
	}

	/**
	 * Afficher votre message de help.
	 * 
	 * @param args
	 * @throws ParseException
	 */
	private static void printHelp(String[] args) throws ParseException {
		final Option helpFileOption = Option.builder("h").longOpt("help")
				.desc("Affiche le message d'aide").build();
		final Options firstOptions = new Options();
		firstOptions.addOption(helpFileOption);
		// On parse l'aide
		final CommandLineParser parser = new DefaultParser();
		final CommandLine firstLine = parser.parse(firstOptions, args, true);
		// Si mode aide
		boolean helpMode = firstLine.hasOption("help");
		if (helpMode) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("rsa commande", configParameters(), true);
			System.exit(0);
		}

	}

	private static void printCommandeEchec(String[] args) throws ParseException {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("rsa commande", configParameters(), true);
		System.exit(0);

	}

	private static void deleteFile(Path directoryFile) {
		try {
			Files.walkFileTree(directoryFile, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException ioException) {
			LOGGER.error("suppression fichier", ioException);
		}
	}
}
