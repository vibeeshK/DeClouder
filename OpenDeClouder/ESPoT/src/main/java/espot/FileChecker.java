package espot;

import java.io.File;

public class FileChecker {
	/*
	 * Checks the given file's availability, whether a folder or a zip file
	 */
	public boolean fileOrDirExists = false;
	public boolean isDirectory = false;
	public boolean isZipFile = false;
	private String fileFolderString;
	public String folderNamePostZipStrip;
	private Commons commons;

	private FileChecker (Commons inCommons, String inFileFolderString) {
		fileFolderString = inFileFolderString;
		commons = inCommons;
		fileOrDirExists = false;
		isDirectory = false;
		isZipFile = false;
		folderNamePostZipStrip = "";
		System.out.println("FileChecker:::");
		System.out.println("inFileFolderString : " + inFileFolderString);

		File fileOrFolder = new File(inFileFolderString);
		if (fileOrFolder.exists()){
			fileOrDirExists = true;
			System.out.println("fileOrDirExists23 = " + fileOrDirExists);
		}

		if (fileOrFolder.isDirectory()) {
			isDirectory = true;
			System.out.println("isDirectory33 = " + isDirectory);
		} else {
			System.out.println("isDirectory45");

			if (commons.isZipFile(inFileFolderString)) {
				isZipFile = true;
				System.out.println("isDirectory54");
				folderNamePostZipStrip = commons.getFileNameWithoutExtension(inFileFolderString);
				System.out.println("inFileFolderString = " + fileFolderString);
				System.out.println("folderNamePostZipStrip = " + folderNamePostZipStrip);
				System.out.println("isZipFile = " + isZipFile);
			}
			System.out.println("isDirectory65545");
		}
		System.out.println("isDirectory = " + isDirectory);
	}

	public static synchronized FileChecker getFileChecker(Commons inCommons, String inFileFolderString) {
		System.out.println("inFileFolderString inFileFolderString = " + inFileFolderString);

		FileChecker fileChecker = new FileChecker(inCommons, inFileFolderString);
		return fileChecker;
	}
}