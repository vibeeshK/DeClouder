package espot;

import java.io.IOException;

public class ArtifactMover {
	/*
	 * Common process to build artifacts from different sources
	 */

	public int lastProcessStatus;
	public static int PROCESSED_OK = 0;
	public static int ERROR_INCORRECT_ARTIFACTTYPE = 1;
	public static int ERROR_IN_FOLDER_CREATION = 2;
	public static int ERROR_IN_FILE_CREATION = 3;
	public static int NO_SOURCE_FILE = 4;
	public static int ERROR_FILE_NOT_DOWNLOADEDYET = 5;
	public String sourcePath;
	public String destPath;
	public CommonData commonData;
	Commons commons;

	//incorrect reuse prevented as the commons data would differ for each root.
	//private static ArtifactMover artifactMover = null;
	
	private ArtifactMover(CommonData inCommonData) {
		commonData = inCommonData;
		commons = inCommonData.getCommons();
		lastProcessStatus = 0;
		sourcePath = null;
		destPath = null;
	}
	public static ArtifactMover getInstance(CommonData inCommonData) {
		//if (artifactMover == null) {
		//	artifactMover = new ArtifactMover(inCommonData);
		//}
		ArtifactMover artifactMover = new ArtifactMover(inCommonData);
		return artifactMover;
	}

	private String getFullFilePath(ArtifactPojo inArtifactPojo) {
		lastProcessStatus = PROCESSED_OK;

		String filePath = null;
		if (inArtifactPojo instanceof SelfAuthoredArtifactpojo){
			SelfAuthoredArtifactpojo selfAuthoredArtifactpojo = (SelfAuthoredArtifactpojo) inArtifactPojo;
			filePath = commons.getFullLocalPathFileNameOfNewArtifact(selfAuthoredArtifactpojo.artifactKeyPojo.rootNick,
					selfAuthoredArtifactpojo.artifactKeyPojo.relevance,
					selfAuthoredArtifactpojo.LocalFileName);
		} else if (inArtifactPojo instanceof ERLDownload){
			ERLDownload erlDownload = (ERLDownload) inArtifactPojo;
			if (erlDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.AvailableStatus)) {
			filePath = commons.getFullLocalPathFileNameOfDownloadedArtifact(erlDownload.artifactKeyPojo.rootNick,
					erlDownload.artifactKeyPojo.relevance,
					erlDownload.downLoadedFile);			
			} else {
				System.out.println("ERROR1a CONTENT NOT AVAILABLE YET");
				lastProcessStatus = ERROR_FILE_NOT_DOWNLOADEDYET;				
			}
		} else {
			System.out.println("ERROR1 inSrcArtifactPojo is neith SelfAuthoredArtifactpojo nor ERLDownload");
			lastProcessStatus = ERROR_INCORRECT_ARTIFACTTYPE;
		}
		return filePath;
	}

	public void createDraftFromBaseContent(byte[] inBaseDocBytes, ArtifactPojo inDestArtifactPojo) {
		lastProcessStatus = PROCESSED_OK;
		
		destPath = getFullFilePath(inDestArtifactPojo);
		System.out.println("At createDraftFromBaseContent destPath is " + destPath);
		if (lastProcessStatus == PROCESSED_OK){ 
			try {
				commons.saveBytesIntoNamedFile(inBaseDocBytes,destPath);
			} catch (IOException e) {
				//e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error ArtifactMover createDraftFromBaseContent " + inDestArtifactPojo.artifactKeyPojo.artifactName, e);
			}
		}
	}

	public void moveArtifact(ArtifactPojo inSrcArtifactPojo, ArtifactPojo inDestArtifactPojo) {
		lastProcessStatus = PROCESSED_OK;

		sourcePath = getFullFilePath(inSrcArtifactPojo);
		System.out.println("At moveArtifact sourcePath is " + sourcePath);

		if (lastProcessStatus == PROCESSED_OK) {

			destPath = getFullFilePath(inDestArtifactPojo);
			System.out.println("At moveArtifact destPath is " + destPath);
	
			//ZIP processor required 2 of 4 starts. create a new folder for unzip
			FileChecker createFileChecker = FileChecker.getFileChecker(commons, sourcePath);
			try {
				if (!createFileChecker.fileOrDirExists && !createFileChecker.isZipFile) {
		
					System.out.println("ERROR3 File Not Available/Not downloaded yet");
					lastProcessStatus = NO_SOURCE_FILE;
					return;
				}
				if (!createFileChecker.isZipFile) {
					System.out.println("At moveArtifact Not at Zip file");
					if (createFileChecker.isDirectory) {
						System.out.println("At moveArtifact is a directory");
						commons.copyFolderViaName(sourcePath, destPath);
					} else {
						System.out.println("At moveArtifact not a directory");
						commons.copyFileUsingName(sourcePath, destPath);
					}
				} else {
					//Though it is a zip artifact, it would have been unzipped already into its folder
					// either during initial set up or during subscription download
					System.out.println("At moveArtifact Is a Zip file");
					System.out.println("check unzip");
					System.out.println("source : " + sourcePath);
					String targetCreateDirName = commons.getDirectoryOfZipFile(destPath);
					System.out.println("target : " + targetCreateDirName);
					if (!commons.createFolder(targetCreateDirName)) {
						System.out.println("ERROR4 Issue with directory creation : " + targetCreateDirName);
						lastProcessStatus = ERROR_IN_FOLDER_CREATION;
						return;
					}
					System.out.println("Copying " + createFileChecker.folderNamePostZipStrip + " into " + targetCreateDirName);
					commons.copyFolderViaName(createFileChecker.folderNamePostZipStrip, targetCreateDirName);
				}
			} catch (IOException e) {
				//e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error ArtifactMover moveArtifact " + inSrcArtifactPojo.artifactKeyPojo.artifactName + " " + inDestArtifactPojo.artifactKeyPojo.artifactName, e);
			}
		}
	}

	public void moveFromTemplate(String inTemplateFileName, SelfAuthoredArtifactpojo inDestSelfAuthoredArtifactpojo) {
		lastProcessStatus = PROCESSED_OK;
		
		System.out.println("At moveFromTemplate inTemplateFileName = " + inTemplateFileName);
		System.out.println("At moveFromTemplate 1 lastProcessStatus = " + lastProcessStatus);

		sourcePath = commons.getTemplateFileName(inTemplateFileName);

		System.out.println("At moveFromTemplate 2 lastProcessStatus = " + lastProcessStatus);
		
		destPath = getFullFilePath(inDestSelfAuthoredArtifactpojo);

		System.out.println("At moveFromTemplate 3 lastProcessStatus = " + lastProcessStatus);
		
		System.out.println("At moveFromTemplate sourcePath  = " + sourcePath );
		System.out.println("At moveFromTemplate destPath  = " + destPath );

		if (lastProcessStatus != PROCESSED_OK){
			return;
		}

		//ZIP processor required 2 of 4 starts. create a new folder for unzip
		FileChecker createFileChecker = FileChecker.getFileChecker(commons, sourcePath);
		
		System.out.println("At moveFromTemplate 4 lastProcessStatus = " + lastProcessStatus);
		
		try {
			if (!createFileChecker.fileOrDirExists && !createFileChecker.isZipFile) {
	
	
				System.out.println("At moveFromTemplate 5 lastProcessStatus = " + lastProcessStatus);

				ContentHandlerInterface contentHandlerInterfaceForNewPrimeFileCreation = 
						ContentHandlerManager.getInstance(commons, 
						commonData.getCatelogPersistenceManager(), 
						inDestSelfAuthoredArtifactpojo.artifactKeyPojo.contentType);
				System.out.println("contentHandlerInterfaceForNewPrimeFileCreation : " + contentHandlerInterfaceForNewPrimeFileCreation);
				System.out.println("At moveFromTemplate 6 lastProcessStatus = " + lastProcessStatus);
	
				contentHandlerInterfaceForNewPrimeFileCreation.initializeContentHandlerWithMinimumSetup(commonData);
				System.out.println("At moveFromTemplate 7 lastProcessStatus = " + lastProcessStatus);
				
				//contentHandlerInterfaceForNewPrimeFileCreation.createNewStartupPrimer(artifactMover.getPrimeFilePath(inDestSelfAuthoredArtifactpojo),
				//		inDestSelfAuthoredArtifactpojo.artifactKeyPojo.contentType);
				contentHandlerInterfaceForNewPrimeFileCreation.createNewStartupPrimer(
																	getPrimeFilePath(inDestSelfAuthoredArtifactpojo),
																	inDestSelfAuthoredArtifactpojo);
				System.out.println("At moveFromTemplate 8 lastProcessStatus = " + lastProcessStatus);
				if (lastProcessStatus == NO_SOURCE_FILE) {
					lastProcessStatus = PROCESSED_OK;	// as this condition is already handled resetting to ok
					System.out.println("At moveFromTemplate 8a lastProcessStatus = " + lastProcessStatus);
				}
				System.out.println("At moveFromTemplate 8b lastProcessStatus = " + lastProcessStatus);
				
			} else {
				if (!createFileChecker.isZipFile) {
					if (createFileChecker.isDirectory) {
						commons.copyFolderViaName(sourcePath, destPath);
					} else {
						commons.copyFileUsingName(sourcePath, destPath);
					}
				} else {
					System.out.println("check unzip");
					System.out.println("source : " + sourcePath);
					//DestPath is already built without zip extension as a directory
					String targetCreateDirName = destPath;
					System.out.println("target : " + targetCreateDirName);
					if (!commons.createFolder(targetCreateDirName)) {
						System.out.println("ERROR5 Issue with directory creation : " + targetCreateDirName);
						lastProcessStatus = ERROR_IN_FOLDER_CREATION;
						return;
					}
					System.out.println("NewDraft UnZipping " + sourcePath + " into " + targetCreateDirName);
					commons.UnZip(sourcePath, targetCreateDirName);
				}
			}
		} catch (IOException e) {
			//e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error ArtifactMover moveFromTemplate " + 
					inTemplateFileName + " " + inDestSelfAuthoredArtifactpojo.artifactKeyPojo.artifactName, e);
		}
		System.out.println("At moveFromTemplate 9 lastProcessStatus = " + lastProcessStatus);
		
	}

	public void downloadTo(String inSourceFilePath, ERLDownload inDestERLDownload) {
		lastProcessStatus = PROCESSED_OK;

		String downLoadedFile = "";
		if (commons.isZipFile(inDestERLDownload.contentFileName)) {
			System.out.println("yes it is zip file ...");
			System.out.println("subscribedERLpojoList.get(erlCount).contentFileName " + inDestERLDownload.contentFileName);
			
			downLoadedFile = commons.getFileNameWithoutExtension(inDestERLDownload.contentFileName);
			String unZippedFilePathString = commons.getFileNameWithoutExtension(inSourceFilePath);
			if (commons.createFolder(unZippedFilePathString)) {
				System.out.println("folder created " + unZippedFilePathString);
			} else {
				System.out.println("ERROR6 Issue with directory creation : " + unZippedFilePathString);
				lastProcessStatus = ERROR_IN_FOLDER_CREATION;
				return;
			}
			
			try {
				commons.UnZip(inSourceFilePath,unZippedFilePathString);
			} catch (IOException e) {
				//e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error ArtifactMover downloadTo " + inSourceFilePath + " " + inDestERLDownload.artifactKeyPojo.artifactName, e);				
			}
			
		} else {
			System.out.println("not its not a zip file ...");
			System.out.println("subscribedERLpojoList.get(erlCount).contentFileName " + inDestERLDownload.contentFileName);
			
			downLoadedFile = inDestERLDownload.contentFileName;
		}
		inDestERLDownload.downLoadedFile = downLoadedFile;
	}
  
	public void prepForUpload(SelfAuthoredArtifactpojo inDestSelfAuthoredArtifactpojo, String inFileExtn) {
		lastProcessStatus = PROCESSED_OK;
		//ZIP processor required 4 of 4 starts. Zip for uploading
		
		if (!commons.getFileExtention(inDestSelfAuthoredArtifactpojo.LocalFileName).equalsIgnoreCase(inFileExtn)) {
			inDestSelfAuthoredArtifactpojo.LocalFileName = inDestSelfAuthoredArtifactpojo.LocalFileName + inFileExtn;
		}
		
		String fullPathUploadFileNameString = commons.getFullLocalPathFileNameOfNewArtifact(inDestSelfAuthoredArtifactpojo.artifactKeyPojo.rootNick, 
				inDestSelfAuthoredArtifactpojo.artifactKeyPojo.relevance, 
				inDestSelfAuthoredArtifactpojo.LocalFileName);

		FileChecker uploadFileChecker = FileChecker.getFileChecker(commons, fullPathUploadFileNameString);
		
		System.out.println("fullPathFileNameString = " + fullPathUploadFileNameString);
		System.out.println("newestDBSelfAuthoredArtifactspojo.LocalFileName = " + inDestSelfAuthoredArtifactpojo.LocalFileName);
		System.out.println("uploadFileChecker.folderNamePostZipStrip = " + uploadFileChecker.folderNamePostZipStrip);
		System.out.println("uploadFileChecker.fileOrDirExists = " + uploadFileChecker.fileOrDirExists);
		System.out.println("uploadFileChecker.isZipFile = " + uploadFileChecker.isZipFile);
		
		if (!uploadFileChecker.fileOrDirExists && !uploadFileChecker.isZipFile) {
			System.out.println("ERROR7 File Not Available/Not downloaded yet" + fullPathUploadFileNameString);
			lastProcessStatus = NO_SOURCE_FILE;
			return;
		}
		if (uploadFileChecker.isZipFile) {			
			try {
				commons.Zip(uploadFileChecker.folderNamePostZipStrip, fullPathUploadFileNameString);
			} catch (IOException e) {
				//e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error ArtifactMover prepForUpload " + inDestSelfAuthoredArtifactpojo.artifactKeyPojo.artifactName + " " + inFileExtn, e);
			}
			System.out.println("Uploading dir zippped");
			System.out.println("uploadFileChecker.folderNamePostZipStrip = " + uploadFileChecker.folderNamePostZipStrip);
			System.out.println("fullPathUploadFileNameString = " + fullPathUploadFileNameString);
		}
	}

	public void archiveDraft(ArtifactPojo inSrcArtifactPojo) {
		lastProcessStatus = PROCESSED_OK;

		sourcePath = getFullFilePath(inSrcArtifactPojo);
		System.out.println("At moveArtifact sourcePath is " + sourcePath);

		if (lastProcessStatus == PROCESSED_OK) {

			//ZIP processor required 2 of 4 starts. create a new folder for unzip
			FileChecker createFileChecker = FileChecker.getFileChecker(commons, sourcePath);
			try {
				if (!createFileChecker.fileOrDirExists && !createFileChecker.isZipFile) {
		
					System.out.println("ERROR3 File Not Available/Not downloaded yet");
					lastProcessStatus = NO_SOURCE_FILE;
					return;
				}
				if (!createFileChecker.isZipFile) {
					System.out.println("At moveArtifact Not a Zip file");
					if (createFileChecker.isDirectory) {
						System.out.println("At moveArtifact is a directory");
						commons.archiveLocalFolder(sourcePath);
					} else {
						System.out.println("At moveArtifact not a directory");
						commons.archiveLocalFile(sourcePath);
					}
				} else {
					//Though it is a zip artifact, it would have been unzipped already into its folder
					// either during initial set up or during subscription download
					System.out.println("At moveArtifact Is a Zip file");
					System.out.println("check unzip");
					System.out.println("source : " + sourcePath);
					System.out.println("Archiving " + createFileChecker.folderNamePostZipStrip);
					commons.archiveLocalFolder(createFileChecker.folderNamePostZipStrip);
				}
			} catch (IOException e) {
				ErrorHandler.showErrorAndQuit(commons, "Error ArtifactMover archiveArtifact " + inSrcArtifactPojo.artifactKeyPojo.artifactName, e);
			}
		}
	}
	
	
	public String getPrimeFilePath(ArtifactPojo inSrcArtifactPojo) {
		System.out.println("At getPrimeFilePath ");

		lastProcessStatus = PROCESSED_OK;
		String artifactPath = getFullFilePath(inSrcArtifactPojo);
		String primeFilePath = null;
		String primeFolder = null;

		System.out.println("checkpoint1 artifactPath = " + artifactPath);

		FileChecker createFileChecker = null;

		if (lastProcessStatus == PROCESSED_OK) {
			createFileChecker = FileChecker.getFileChecker(commons, artifactPath);
			System.out.println("checkpoint2 createFileChecker = " + createFileChecker);
			
			if (!createFileChecker.fileOrDirExists && !createFileChecker.isZipFile) {
				System.out.println("Warning - File Not Available/Not downloaded yet : " + artifactPath);
				lastProcessStatus = NO_SOURCE_FILE;
				//This may not be an error. Hence not Returning			
			}
			System.out.println("checkpoint3 createFileChecker.fileOrDirExists = " + createFileChecker.fileOrDirExists);
			System.out.println("checkpoint3 createFileChecker.isZipFile = " + createFileChecker.isZipFile);
			if (createFileChecker.isZipFile) {
				System.out.println("It is isZipFile " + artifactPath);
				primeFolder = commons.getFileNameWithoutExtension(artifactPath);
				primeFilePath = commons.getAbsolutePathFromDirAndFileNm(primeFolder,Commons.ARTIFACT_PRIME_FILE);
	
			} else if (createFileChecker.isDirectory) {
				System.out.println("It is isDirectory " + artifactPath);
				primeFolder = artifactPath;
				primeFilePath = commons.getAbsolutePathFromDirAndFileNm(primeFolder,Commons.ARTIFACT_PRIME_FILE);
			} else {
				System.out.println("It is neither zip file nor dir " + artifactPath);
				primeFilePath = artifactPath;
			}
		}
		return primeFilePath;
	}
	
	public String getChildFilePath(ArtifactPojo inSrcArtifactPojo, String inChildFileName) {
		lastProcessStatus = PROCESSED_OK;
		String primeFilePath = getPrimeFilePath(inSrcArtifactPojo);
		String primeFolder = commons.getFolderNameFromFullPath(primeFilePath);
		String childFilePath = commons.getAbsolutePathFromDirAndFileNm(primeFolder,inChildFileName);
		return childFilePath;
	}
}