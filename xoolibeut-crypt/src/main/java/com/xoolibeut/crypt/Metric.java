/**
 * 
 */
package com.xoolibeut.crypt;

/**
 * @author cer4495267
 * 
 */
public class Metric {
	private int countFileTraite;
	private int countAllFilInFolder;
	private long totalSize;

	/**
	 * format le taille du fichier ou dossier crypté
	 * 
	 * @return
	 */
	public String formatTotalSize() {
		if (totalSize < 1024) {
			return totalSize + " Octet";
		}
		if (totalSize < 1024 * 1024) {
			return (totalSize / (1024)) + " KO";
		}
		if (totalSize < 1024 * 1024 * 1024) {
			return (totalSize / (1024 * 1024)) + " MO";
		} else {
			return (totalSize / (1024 * 1024*1024)) + " GO";
		}
	}

	/**
	 * @return the countFileTraite
	 */
	public int getCountFileTraite() {
		return countFileTraite;
	}

	/**
	 * @param countFileTraite the countFileTraite to set
	 */
	public void setCountFileTraite(int countFileTraite) {
		this.countFileTraite = countFileTraite;
	}

	/**
	 * @return the countAllFilInFolder
	 */
	public int getCountAllFilInFolder() {
		return countAllFilInFolder;
	}

	/**
	 * @param countAllFilInFolder the countAllFilInFolder to set
	 */
	public void setCountAllFilInFolder(int countAllFilInFolder) {
		this.countAllFilInFolder = countAllFilInFolder;
	}

	/**
	 * @return the totalSize
	 */
	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * @param totalSize the totalSize to set
	 */
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	
}
