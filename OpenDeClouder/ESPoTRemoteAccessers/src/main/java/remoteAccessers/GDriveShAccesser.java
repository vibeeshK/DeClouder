package remoteAccessers;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import commonTechs.ParentChildObject;
import espot.AbstractRemoteAccesser;
import espot.Commons;
import espot.ErrorHandler;
import espot.RootPojo;

public class GDriveShAccesser extends AbstractRemoteAccesser {
	/*
	 * This class provides the means to access files on a Google Drive
	 */
    private static final String APPLICATION_NAME = "Google Drive API Java GoogleDriveAccesser";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY = "tokens";
    private static final String GOOGLE_BASE_FOLDER = "https://drive.google.com";
    private static final int MAXPAGESIZE = 1000;
    
    NetHttpTransport HTTP_TRANSPORT = null;  
    Drive service = null;

    // Global instance of the scopes required by this quickstart.
    // If modifying these scopes, delete your previously saved tokens/ folder.
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY,DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_READONLY);

    private static final String CREDENTIALS_FILE_SUBFIX = "_credentials.json"; // Ensure to keep the name of credential file downloaded from api site

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
    	 String current = new java.io.File( "." ).getCanonicalPath();
         System.out.println("Current dir:"+current);
         String credentialsPathFileName = commons.rootConfigFolder + commons.localFileSeparator + rootPojo.rootNick + commons.localFileSeparator + rootPojo.rootNick + CREDENTIALS_FILE_SUBFIX;
         System.out.println("credentialsPathFileName:"+credentialsPathFileName);
         String tokenPathName = commons.artifactsFolder + commons.localFileSeparator + TOKENS_DIRECTORY + commons.localFileSeparator + rootPojo.rootNick;
         System.out.println("tokenPathName:"+tokenPathName);
         
        InputStream credentialStream = new FileInputStream(credentialsPathFileName);
        System.out.println(credentialStream);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentialStream));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokenPathName)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        
		System.out.println("22 proxy host is now set to " + System.getProperty("https.proxyHost"));
		System.out.println("22 proxy port is now set to " + System.getProperty("https.proxyPort"));
                
        
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }

    public static boolean checkFileExistence(Drive service, String inRemoteURL) throws IOException {
    	boolean fileFound = false;

        String pageToken = null;
        List<File> fileList = new ArrayList<File>();

		String query =  " 'root' in parents and trashed = false ";
        System.out.println("query is " + query);

        FileList result = service.files().list().setQ(query).setSpaces("drive") //
                // Fields will be assigned values: id, name, createdTime, mimeType
                .setFields("nextPageToken, files(id, name, mimeType)")//
                .setPageToken(pageToken).execute();

    	System.out.printf("result size" + result.size());
        
        for (File file : result.getFiles()) {
        	fileList.add(file);
        	System.out.printf("\n found folder " + file);
        }
 
        if (fileList.size() > 1) {
        	System.out.printf("found listing 1 " + fileList.get(0));
        	fileFound = true;
        }
    	
    	return fileFound;
	}

    public static void main(String... args) throws IOException, GeneralSecurityException {
    	
    	GDriveShAccesser g1 = new GDriveShAccesser();

		Commons commons = null;
		try {
			commons = Commons.getInstance(Commons.CLIENT_MACHINE);
		} catch (ParseException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAhAccesser main " , e);
		}

        System.out.println("credential file content is " + 	commons.getFilebyteDataFromFileName(CREDENTIALS_FILE_SUBFIX));

        commons.checkSetProxy();

    	g1.intiateCommunications(null, commons);

		InputStream ins = g1.get("1DnfPrmgScI2AggSRRhGDhJod4Us8KBYC/testdoc.txt");

		//https://drive.google.com/open?id=1DnfPrmgScI2AggSRRhGDhJod4Us8KBYC

    	String outfile = "C:\\Kannan\\Java\\ESPoT\\testOutput\\GDriveTest\\testDoc.txt";
		System.out.println(" inputstream received is " + ins);
		System.out.println(" outfile name is " + outfile);
    	
		g1.commons.storeInStream(ins, outfile);

		System.out.println("test file stored at " + outfile);
		
    	System.exit(0);
    	
    	//https://drive.google.com/drive/folders/1jr2oDmZpy3CNCv74Itc4Fs62f6poGBT0
    		
    	if (g1.exists("1jr2oDmZpy3CNCv74Itc4Fs62f6poGBT0/test2Folder/test3Folder/testGoogleFile.txt")) {
    		System.out.println("1jr2oDmZpy3CNCv74Itc4Fs62f6poGBT0/test2Folder/test3Folder/testGoogleFile.txt exists");
    	} else {
    		System.out.println("1jr2oDmZpy3CNCv74Itc4Fs62f6poGBT0/test2Folder/test3Folder/testGoogleFile.txt does not exist");    		
    	}

    	System.exit(0);

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, g1.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }

        System.out.printf("file " + files.get(1).getName() + " really exists " + checkFileExistence(service, "root"));
    }

	//@Override
	public void intiateCommunications(RootPojo inRootPojo, Commons inCommons) {

		if (inRootPojo == null) {
			inRootPojo = new RootPojo();
			inRootPojo.rootString = "1DnfPrmgScI2AggSRRhGDhJod4Us8KBYC";
			inRootPojo.fileSeparator = "/";
		}

		System.out.println("inRootPojo.rootString is " + inRootPojo.rootString);
		commonInit(inRootPojo,inCommons);
		System.out.println("rootPojo.rootString is " + rootPojo.rootString);

        try {
    		System.out.println("calling checkSetProxy");

            commons.checkSetProxy();
    		//set the proxy when the user is within a firewall
    		System.out.println("proxy host is now set to " + System.getProperty("https.proxyHost"));
    		System.out.println("proxy port is now set to " + System.getProperty("https.proxyPort"));
            
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	                .setApplicationName(APPLICATION_NAME)
	                .build();
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveShAccesser intiateCommunications " + inRootPojo.rootNick, e);
		}
	}
	

	public void createFolderOfFileIfDontExist(String inFileName) {
		String parentFolder = commons.getFolderNameFromURL(inFileName, fileSeparator);
		if (!exists(parentFolder)){
			createFolderpath(parentFolder);
		}
	}

	//@Override
	public boolean exists(String inRemoteURL) {

		System.out.println("starting exists check inRemoteURL " + inRemoteURL);

		boolean urlExists = false;
		if (getFileResourceIDFromURL(inRemoteURL)!= null) {
			urlExists = true;
		}
		return urlExists;
	}

	public String getFileResourceIDFromURL(String inRemoteURL) {

		String fileResourceID = null;
		System.out.println("starting getFileResourceIDFromURL inRemoteURL " + inRemoteURL);

		ParentChildObject parentChildObject = getParentChildObjectForURL(inRemoteURL);

		if (parentChildObject!= null && parentChildObject.childObj!= null) {
			fileResourceID = (String) parentChildObject.childObj;
		}
		return fileResourceID;
	}


	private String getRemoteFolderID(String inRemoteURL) {
		String remoteFolderID = null;
		ParentChildObject parentChildObject = getParentChildObjectForURL(inRemoteURL);
		if (parentChildObject!= null && parentChildObject.parentObj!= null) {
			remoteFolderID = (String) parentChildObject.parentObj;
		}
		System.out.println(" remoteFolderID is " + remoteFolderID);
		return remoteFolderID;
	}

	void createFolderpath(String inFolderPath) {
		if (preCheckURL(inFolderPath)) {
			String canonicPostBaseString = getCanonicPostBaseString(inFolderPath);
			System.out.println(" canonicPostBaseString is " + canonicPostBaseString);
			createFolderPathFromCanoniPostString(canonicPostBaseString);
		}
		return;		
	}

	void createFolderPathFromCanoniPostString(String inCanonicPostBaseString) {
		String[] splitFolderNodes = StringUtils.split(inCanonicPostBaseString,rootPojo.fileSeparator);

		String prevNodeResourceID = rootPojo.rootString;

        System.out.println("\n at 0a inCanonicPostBaseString is " + inCanonicPostBaseString);
        System.out.println("\n at 0a rootPojo.fileSeparator is " + rootPojo.fileSeparator);
        System.out.println("\n at 0a splitFolderNodes length is " + splitFolderNodes.length);
        System.out.println("\n at 0a splitFolderNodes[0] is " + splitFolderNodes[0]);
		
		ParentChildObject currentNodeParentChildObject = null;
		for (String node : splitFolderNodes) {
	        System.out.println("\n at 0a prevNodeResourceID is " + prevNodeResourceID);
	        System.out.println("\n at 0a node to find is " + node);
	        currentNodeParentChildObject = 
					new ParentChildObject(prevNodeResourceID,null); // at this time we know parent id
	        findChildNSiblingForNode(currentNodeParentChildObject, node);

	        System.out.println("\n at 0a currentNodeParentChildObject.childObj is " + currentNodeParentChildObject.childObj);
	        System.out.println("\n at 0a inside node " + node);

	        if (currentNodeParentChildObject.childObj == null){
		        System.out.println("\n at 0a inside currentNodeParentChildObject.childObj null ");
	        	File fileMetadata = new File();
	        	fileMetadata.setName(node);
	        	fileMetadata.setMimeType("application/vnd.google-apps.folder");
	    		fileMetadata.setParents(Collections.singletonList(prevNodeResourceID));

	        	File file = null;
				try {
					file = service.files().create(fileMetadata)
					    .setFields("id, parents")
					    .execute();
				} catch (IOException e) {
					e.printStackTrace();
					ErrorHandler.showErrorAndQuit(commons, "error in GDriveAccess createFolderPathFromCanoniPostString " + inCanonicPostBaseString, e);
				}
	        	System.out.println("Folder created for : " + node);
	        	System.out.println("Folder ID created: " + file.getId());
	        	currentNodeParentChildObject.childObj = file.getId();
	        	
	        } else {
		        System.out.println("\n at 0a currentNodeParentChildObject.childObj not null value " + currentNodeParentChildObject.childObj);
	        }
	        
			System.out.println("\n at 0a got fileResourceID as " + prevNodeResourceID);
			prevNodeResourceID = (String) currentNodeParentChildObject.childObj;
		}
		return;
	}

	ParentChildObject getParentChildObjectForURL(String inRemoteURL) {
		ParentChildObject parentChildObject = null;
		if (preCheckURL(inRemoteURL)) {
			String canonicPostBaseString = getCanonicPostBaseString(inRemoteURL);
			System.out.println(" canonicPostBaseString is " + canonicPostBaseString);
			parentChildObject = getFileResourceIDFromCanoniPostString(canonicPostBaseString);
		}
		return parentChildObject;
	}

	ParentChildObject getFileResourceIDFromCanoniPostString(String inCanonicPostBaseString) {
		String[] splitFolderNodes = StringUtils.split(inCanonicPostBaseString,rootPojo.fileSeparator);

		String prevNodeResourceID = rootPojo.rootString;

        System.out.println("\n at 00 inCanonicPostBaseString is " + inCanonicPostBaseString);
        System.out.println("\n at 00 rootPojo.fileSeparator is " + rootPojo.fileSeparator);
        System.out.println("\n at 00 splitFolderNodes length is " + splitFolderNodes.length);

		ParentChildObject currentNodeParentChildObject = new ParentChildObject(prevNodeResourceID,null);;

		for (String node : splitFolderNodes) {
	        System.out.println("\n at 00 prevNodeResourceID is " + prevNodeResourceID);
	        System.out.println("\n at 00 node to find is " + node);

	        if (prevNodeResourceID == null) { 
				System.out.println("\n at 00 got fileResourceID as null hence breaking");
				currentNodeParentChildObject = null;
	        	break;
	        }

	        currentNodeParentChildObject = 
					new ParentChildObject(prevNodeResourceID,null); // at this time we know parent id
	        findChildNSiblingForNode(currentNodeParentChildObject, node);

	        if (currentNodeParentChildObject.childObj != null) {
	        	prevNodeResourceID = (String) currentNodeParentChildObject.childObj;
	        } else {
	        	prevNodeResourceID = null;
	        }

	        System.out.println("\n at 00 got fileResourceID as " + prevNodeResourceID);
		}
		
		return currentNodeParentChildObject;
	}
	
	List<File> getChildListForNode(String inFileID) {
		
        String pageToken = null;

		String query =  " '" + inFileID + "' in parents and trashed = false ";
        System.out.println("query 111 is " + query);
        System.out.println("query 111 getChildListForNode inFileID is " + inFileID);
		System.out.println("22a proxy host is now set to " + System.getProperty("https.proxyHost"));
		System.out.println("22a proxy port is now set to " + System.getProperty("https.proxyPort"));

        FileList resultFileList = null;
		try {
			resultFileList = service.files().list().setQ(query).setSpaces("drive") //
			        // Fields will be assigned values: id, name, createdTime, mimeType
			        .setFields("nextPageToken, files(id, name, mimeType)")//
			        .setPageSize(MAXPAGESIZE)	// as the sibling list will be meaningless for split paged query
			        							// ensure to keep high enough expected pagesize
			        							// Paginated read is avoided
			        .setPageToken(pageToken).execute();
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAccess getChildListForNode while finding inFileID " + inFileID, e);
		}

    	System.out.printf("result getChildListForNode 22 resultFileList size" + resultFileList.size());

		return resultFileList.getFiles();
	}
		
	void findChildNSiblingForNode(ParentChildObject inPrevNodeParentChildObject, String inNode) {
		
        String pageToken = null;
		String query =  " '" + inPrevNodeParentChildObject.parentObj + "' in parents and trashed = false ";
        System.out.println("query 112 is " + query);
        System.out.println("query 112 inNode is is " + inNode);
        System.out.println("query 112 inPrevNodeParentChildObject parentObj is " + inPrevNodeParentChildObject.parentObj);
		System.out.println("22b proxy host is now set to " + System.getProperty("https.proxyHost"));
		System.out.println("22b proxy port is now set to " + System.getProperty("https.proxyPort"));

        FileList resultFileList = null;
		try {
			resultFileList = service.files().list().setQ(query).setSpaces("drive") //
			        // Fields will be assigned values: id, name, createdTime, mimeType
			        .setFields("nextPageToken, files(id, name, mimeType)")//
			        .setPageSize(MAXPAGESIZE)	// as the sibling list will be meaningless for split paged query
			        							// ensure to keep high enough expected pagesize
			        							// Paginated read is avoided
			        .setPageToken(pageToken).execute();
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAccess findChildNSiblingForNode while finding inNode " + inNode, e);		
		}

		System.out.printf("result 22 size" + resultFileList.size());
        
        for (File file : resultFileList.getFiles()) {
        	System.out.printf("\n 22 found folder " + file.toString());
        	System.out.printf("\n 22 found file name is " + file.getName().toString());
        	System.out.printf("\n 22 found folder again is " + file.toString());
        	System.out.printf("\n 22 found file name again is " + file.getName().toString());

        	if (file.getName().equals(inNode)) {
            	System.out.printf("\n 22 adding inNode name is " + file.getName());
            	inPrevNodeParentChildObject.childObj = file.getId();
        	}
        }
		return;
	}

	boolean preCheckURL(String inRemoteURL) {
		boolean preCheckURLPassed = false;
		if (!StringUtils.startsWith(inRemoteURL, rootPojo.rootString)) {
			//commons.logger.error("incorrect url passed. " + inRemoteURL + " doesnt strart with rootstring: " + rootPojo.rootString);
			ErrorHandler.showErrorAndQuit(commons, "incorrect url passed. " + inRemoteURL + " doesnt strart with rootstring: " + rootPojo.rootString);

		} else {
			preCheckURLPassed = true;
		}
		return preCheckURLPassed;
	}
	
	private String getCanonicPostBaseString(String inFullCanonicFolderString){
		String canonicPostBaseString = StringUtils.substring(inFullCanonicFolderString,rootPojo.rootString.length());
		return canonicPostBaseString;
	}

	private InputStream get(String inRemoteURL) {
		InputStream inputStreamOfFile = null;
		String fileResourceID = null;
		try {
			fileResourceID = (String) getFileResourceIDFromURL(inRemoteURL);
			inputStreamOfFile  = service.files().get(fileResourceID).executeMediaAsInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAccess get inRemoteURL " + inRemoteURL, e);
		}
        
		return inputStreamOfFile;
	}

	@Override
	public ArrayList<String> getRemoteList(String inRemoteURL) {
		ArrayList<String> arrayListOfFiles = null;
		System.out.println("starting getRemoteList inRemoteURL " + inRemoteURL);

		ParentChildObject parentChildObject = getParentChildObjectForURL(inRemoteURL);
		if (parentChildObject!= null && parentChildObject.childObj!= null) {
			List<File> childList = getChildListForNode((String) parentChildObject.childObj);
	
			if (childList!= null) {
				arrayListOfFiles = new ArrayList<String>();
				for (File fileName : childList) {
					arrayListOfFiles.add(inRemoteURL + rootPojo.fileSeparator + fileName.getName());
				}
			}
		}
		return arrayListOfFiles;
	}

	//@Override
	//public List<String> getList(String inRemoteURL) {
	//	ArrayList<String> arrayListOfFiles = getRemoteList(inRemoteURL);
	//	List<String> listOfFiles = commons.getStringListFromObjectList(arrayListOfFiles);
	//	return listOfFiles;
	//}

	@Override
	public void put(String inNewContentRemoteLocation, byte[] inBytes) {
		String fileName = commons.getFileNameFromFullPath(inNewContentRemoteLocation, fileSeparator);
		String tempPathFileName = commons.getFullLocalPathFileNameOfTempFile(rootPojo.rootNick,fileName);
		try {
			commons.storeByteArrayIntoFile(inBytes, tempPathFileName);			
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAccess put of inNewContentRemoteLocation " + inNewContentRemoteLocation, e);
		}
		uploadToRemote(inNewContentRemoteLocation, tempPathFileName);
	}

	public void uploadToRemote(String inRemoteLocation, String inLocalFileToBeUploaded){
		//refer https://developers.google.com/drive/api/v3/folder

		createFolderOfFileIfDontExist(inRemoteLocation);
		
    	String remoteFolderID = getRemoteFolderID(inRemoteLocation);
    	
		File fileMetadata = new File();
		fileMetadata.setParents(Collections.singletonList(remoteFolderID));

		System.out.println("trying to upload file: " + inLocalFileToBeUploaded );
		String remoteFileName = commons.getFileNameFromFullPath(inRemoteLocation,rootPojo.fileSeparator);		

		System.out.println("inRemoteLocation is : " + inRemoteLocation);
		System.out.println("remoteFileName is : " + remoteFileName);
		
		fileMetadata.setName(remoteFileName);

		java.io.File filePath = new java.io.File(inLocalFileToBeUploaded);
		FileContent mediaContent = new FileContent("*/*", filePath);
		File file = null;
		try {
			file = service.files().create(fileMetadata, mediaContent)
			    .setFields("id, parents")
			    .execute();
			System.out.println("File ID: " + file.getId());
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAccess uploadToRemote " + inLocalFileToBeUploaded, e);
		}
	}

	@Override
	public void putInStreamIntoRemoteLocation(String inNewContentRemoteLocation,
			InputStream inUpdatedContentByteArray) {
		String fileName = commons.getFileNameFromFullPath(inNewContentRemoteLocation, fileSeparator);
		String tempPathFileName = commons.getFullLocalPathFileNameOfTempFile(rootPojo.rootNick,fileName);
		try {
			commons.storeInStream(inUpdatedContentByteArray, tempPathFileName);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAhAccesser putInStreamIntoRemoteLocation inNewContentRemoteLocation " + inNewContentRemoteLocation, e);			
		}

		uploadToRemote(inNewContentRemoteLocation, tempPathFileName);
	}

	@Override
	public void moveToRemoteLocation(String inSourceFileRemoteLocation, String inDestinationFileRemoteLocation) {
		System.out.println(" a2 moveToRemoteLocation from " + inSourceFileRemoteLocation);
		System.out.println(" a2 moveToRemoteLocation to " + inDestinationFileRemoteLocation);

		createFolderOfFileIfDontExist(inDestinationFileRemoteLocation);

		ParentChildObject fromParentChildObject =  getParentChildObjectForURL(inSourceFileRemoteLocation);
		String payloadFileId = (String) fromParentChildObject.childObj;
		
		System.out.println(" a2 payloadFileId is " + payloadFileId);

		createFolderOfFileIfDontExist(inDestinationFileRemoteLocation);
		
		ParentChildObject targetParentChildObject =  getParentChildObjectForURL(inDestinationFileRemoteLocation);
		String targetFolderId = (String) targetParentChildObject.parentObj;
		
		System.out.println(" a2 targetFolderId is " + targetFolderId);
	
		String newFileName = commons.getFileNameFromFullPath(inDestinationFileRemoteLocation, rootPojo.fileSeparator);

		System.out.println(" a2 newFileName is " + newFileName);

		File paylodfile;
		try {
			// Retrieve the existing parents to remove
			paylodfile = service.files().get(payloadFileId)
			    .setFields("parents")
			    .execute();

			StringBuilder previousParents = new StringBuilder();
			
			for (String parent : paylodfile.getParents()) {
			  previousParents.append(parent);
			  previousParents.append(',');
			}

			// Move the file to the new folder
			paylodfile = service.files().update(payloadFileId, null)
			    .setAddParents(targetFolderId)
			    .setRemoveParents(previousParents.toString())
			    .setFields("id, parents")
			    .execute();

			System.out.println(" a2 parents  changed ");

		    File fileMetadata = new File();
		    fileMetadata.setName(newFileName);

		    Drive.Files.Update update = service.files().update(payloadFileId, fileMetadata);
		    update.execute();			

			System.out.println(" a2 name changed ");
			
		} catch (IOException e) {
			ErrorHandler.showErrorAndQuit(commons, "error in GDriveAccess moveToRemoteLocation from " + inSourceFileRemoteLocation + " to " + inDestinationFileRemoteLocation, e);
		}
	}

	@Override
	public InputStream getRemoteFileStream(String inRemoteFileName) {
		System.out.println("At getRemoteFileStream inRemoteFileName is " + inRemoteFileName);
		return get(inRemoteFileName);
	}
}