package espot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import commonTechs.CommonTechs;
import commonTechs.CompareForLaterName;
import commonTechs.CustomClassLoader;

public class Commons extends CommonTechs {
	/*
	 * This class consolidates the commonly used functions to avoid redundancy
	 */
	public final static String CONFIGFOLDERPREFIX = "config/";
	public final static String commonPropertiesFILENAME = CONFIGFOLDERPREFIX + "commons.properties";
	public final static String clientPropertiesFILENAME = CONFIGFOLDERPREFIX + "client.properties";
	public final static String serverPropertiesFILENAME = CONFIGFOLDERPREFIX + "server.properties";
	public final static String commonSyspropertiesFILENAME = CONFIGFOLDERPREFIX + "commonSysComponents.properties";
	
	public final static String sysCompCurrLocalLogUpdateTmLIT = "sysCompCurrLocalLogUpdateTm";
	public final static String suppressSysCompRefreshLIT = "suppressSysCompRefresh";
	public final static String STR_YES = "YES";
	public final static String STR_NO = "NO";
	public boolean suppressSysCompRefresh = false;


	public final static String ARTIFACT_PRIME_FILE = "primeFile.json";

	public final static String REQUEST_TRACKING_FILE_SUBFIX = "_RequestsTracker.json";
	public final static String VERSIONING_FILE_SUBFIX = "_versioningDetails.json";
	public final static String REQUESTFILE_EXTN = ".json";
	
	public final static int BASE_CATALOG_SERVER = 1;
	public final static int CLIENT_MACHINE = 2;
	public final static int EXTENDED_CATALOG_SERVER = 3;
	public int processMode = 0;
	public final static int FATALEXITCODE = 8;

	public static String BASE_CATLOG_SERVER_FOLDER = "bsSrver";
	public static String CLIENT_MACHNE_FOLDER = "clientMc";
	public String xtdCatalogSrvrFolder = "extdSrvr";

	public int erlMaxVersions = 2; //initiated with default
	public static Commons baseCatalogServerCommonsInstance = null;
	public static Commons clientMachineCommonsInstance = null;
	public static Commons extendedCatalogServerCommonsInstance = null;
	
	private static Boolean PROXY_CHECKED = false;

	public Date sysCompCurrLocalLogUpdateTm = null;
	public int catalogDownloadTimeGapSec = 0;

	public String localFileSeparator = null;
	public String installFileFolder = null;
	public String configDataFolder = null;
	private String certificatFile = null;
	public String certificatesFolder = null;
	private String newArtifactsFolder = null;
	private String templatesFolder = null;

	public String contentHandlersFolder = null;
	public String osHandlersFolder = null;
	public String remoteAccessersFolder = null;

	public String backgroundImagePathFileName = null;
	public String rootConfigFolder = null;
	public String artifactsFolder = null;
	private String responsesfolderlocal = null;
	private String tempFolder = null;
	private String localArhive = null;
	
	private String backupRootNick = null;
	private String remoteBackupFolder = null;
	private String contentdropbox = null;
	private String requestdropbox = null;
	private String responsepickbox = null;
	public String remoteArhive = null;

	public String sysUpdateLogDoc = null;
	
	public String[] serverRootNicks;

	private String catalogDbPublishFilePrefix = null;	
	private String clientSideCatalogDbReceiveFolder=null;
	private String serverSideSideCatalogDbPublishFolder=null;
	private String serversMasterCopyofCatalogDbLocalFolder=null;
	private String serversMasterCopyofCatalogDbPrefix=null;

	public String downloadedCatalogDetailsFile=null;

	private String clientDbFilePath = null;
	private String clientDbFileName = null;
	public String sysDbFileLocation = null;
	private String newReviewsFolder = null;	
	private String reqTrackersFolderLocal = null;
	private String versioningFilesFolderLocal = null;
	private String remoteReviewsFolder = null;
	private String contentDownLoadFolder = null;
	private String downloadedReviewsFolder = null;
	public String publishedRootsFileName = null;
	public String subscribedRootNicksFileName = null;
	public String userName = null;
	private String osHandlerName = null;
	private String defaultUIRootNick = null;
	private String processingRootNick = null;
	boolean remoteCommunicationInitiated = false;

	public String httpProxyHost;
	public String httpProxyPort;
	public String httpsProxyHost;
	public String httpsProxyPort;
	
	public void createFoldersForRootNick(String inRootNick) {
		appendFolder(newArtifactsFolder,inRootNick);
		appendFolder(newReviewsFolder,inRootNick);
		appendFolder(responsesfolderlocal,inRootNick);
		appendFolder(tempFolder,inRootNick);
		appendFolder(downloadedReviewsFolder,inRootNick);
		appendFolder(contentDownLoadFolder,inRootNick);
		appendFolder(clientSideCatalogDbReceiveFolder,inRootNick);
	}


	private void readCommonPropForAllMachines() throws IOException {
		InputStream commonPropertiesInStream = null;
		Properties commonPropObject = new Properties();

		commonPropertiesInStream = new FileInputStream(commonPropertiesFILENAME);
		//commonPropertiesInStream = getClass().getResourceAsStream(commonPropertiesFileName);
		// load a properties file
		commonPropObject.load(commonPropertiesInStream);

		localFileSeparator = Character.toString(File.separatorChar);

		installFileFolder = commonPropObject.getProperty("installFileFolder") + File.separatorChar + System.getProperty("user.name");
		
		String folderStub = "xx";
		System.out.println("going somewhere " + folderStub);
		if (processMode == BASE_CATALOG_SERVER) {
			folderStub = BASE_CATLOG_SERVER_FOLDER;
			System.out.println("gone into base server path " + folderStub);
		} else if (processMode == CLIENT_MACHINE) {
			folderStub = CLIENT_MACHNE_FOLDER;
			System.out.println("gone into clientpath " + folderStub);
		} else if (processMode == EXTENDED_CATALOG_SERVER) {
			folderStub = xtdCatalogSrvrFolder;
			System.out.println("gone into extend path " + folderStub);
		}
		
		System.out.println("commonPropObject.getProperty(installFileFolder) = " + commonPropObject.getProperty("installFileFolder"));
		System.out.println("System.getProperty(\"user.name\") = " + System.getProperty("user.name"));
		System.out.println("File.pathSeparatorChar = " + File.separatorChar);
		System.out.println("installFileFolder = " + installFileFolder);
		System.out.println("folderStub = " + folderStub);

		configDataFolder = installFileFolder + localFileSeparator + commonPropObject.getProperty("configDataFolder");
		////////////////////////////////////////////////////////////////////////
		//folders and content that sit within configDataFolder starts////////////
		publishedRootsFileName = configDataFolder + localFileSeparator + commonPropObject.getProperty("publishedRootsFile");
		subscribedRootNicksFileName = configDataFolder + localFileSeparator + commonPropObject.getProperty("SubscribedRootNicksFileName");
		downloadedCatalogDetailsFile = configDataFolder + localFileSeparator + commonPropObject.getProperty("downloadedCatalogDetailsFile");
		certificatesFolder = configDataFolder + localFileSeparator + commonPropObject.getProperty("certificates");
		osHandlersFolder = configDataFolder + localFileSeparator + commonPropObject.getProperty("osHandlers");
		remoteAccessersFolder = configDataFolder + localFileSeparator + commonPropObject.getProperty("remoteAccessers");
		contentHandlersFolder = configDataFolder + localFileSeparator + commonPropObject.getProperty("contentHandlers");
		backgroundImagePathFileName = configDataFolder + localFileSeparator + commonPropObject.getProperty("BackgroundImage");;
		rootConfigFolder=configDataFolder + localFileSeparator + commonPropObject.getProperty("rootConfigFolder");

		//folders and content that sit within configDataFolder ends/////////////
		////////////////////////////////////////////////////////////////////////

		artifactsFolder= System.getProperty("user.home") + localFileSeparator + commonPropObject.getProperty("artifactsFolder") + localFileSeparator + folderStub;
		
		////////////////////////////////////////////////////////////////////////
		//folders and content that sit within Artifacts path starts/////////////
		tempFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("tempfolder");
		newReviewsFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("newReviewsFolder");
		responsesfolderlocal = artifactsFolder + localFileSeparator + commonPropObject.getProperty("responsesfolderlocal");
		contentDownLoadFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("ContentDownLoadFolder");
		newArtifactsFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("newArtifactsFolder");
		downloadedReviewsFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("downloadedReviewsFolder");
		localArhive = artifactsFolder + localFileSeparator + commonPropObject.getProperty("localArhive");
		//folders and content that sit within Artifacts path ends///////////////
		////////////////////////////////////////////////////////////////////////

		templatesFolder = installFileFolder + localFileSeparator + commonPropObject.getProperty("templatesFolder");
		clientDbFilePath = installFileFolder + localFileSeparator + folderStub + localFileSeparator + commonPropObject.getProperty("clientDbFilePath");

		System.out.println("here is the issue clientDbFilePath = " + clientDbFilePath);
		
		clientDbFileName = commonPropObject.getProperty("clientDbFileName");

		clientSideCatalogDbReceiveFolder = installFileFolder + localFileSeparator + commonPropObject.getProperty("clientSideCatalogDbReceiveFolder");
		serverSideSideCatalogDbPublishFolder = commonPropObject.getProperty("serverSideSideCatalogDbPublishFolder");

		contentdropbox = commonPropObject.getProperty("contentdropbox");
		requestdropbox = commonPropObject.getProperty("requestdropbox");
		responsepickbox = commonPropObject.getProperty("responsepickbox");
		remoteReviewsFolder = commonPropObject.getProperty("remoteReviewsFolder");
		remoteArhive = commonPropObject.getProperty("remoteArhive");
		catalogDbPublishFilePrefix = commonPropObject.getProperty("catalogDbPublishFilePrefix");

		
		//catalogPublishFolder = commonPropObject.getProperty("catalogPublishFolder");
		System.out.println("catalogDbPublishFilePrefix = " + catalogDbPublishFilePrefix);
		//System.out.println("catalogPublishFolder = " + catalogPublishFolder);


		sysUpdateLogDoc = commonPropObject.getProperty("sysUpdateLogDoc");
		//sysUpdateRemoteFolder = commonPropObject.getProperty("sysUpdateRemoteFolder");
		sysDbFileLocation = installFileFolder + localFileSeparator + commonPropObject.getProperty("sysDbFileLocation");

		String suppressSysCompRefreshProp = commonPropObject.getProperty(suppressSysCompRefreshLIT);
		
		System.out.println("suppressSysCompRefreshLIT = " + suppressSysCompRefreshLIT);
		System.out.println("suppressSysCompRefreshProp = " + suppressSysCompRefreshProp);
		System.out.println("STR_YES = " + STR_YES);
		
		if (suppressSysCompRefreshProp.equalsIgnoreCase(STR_YES)) {
			suppressSysCompRefresh = true;
		} else {
			suppressSysCompRefresh = false;
		}

		catalogDownloadTimeGapSec = Integer.parseInt(commonPropObject.getProperty("catalogDownloadTimeGapSec"));
		System.out.println("catalogDownloadTimeGapSec = " + catalogDownloadTimeGapSec);

		httpProxyHost=commonPropObject.getProperty("httpProxyHost");
		httpProxyPort=commonPropObject.getProperty("httpProxyPort");
		httpsProxyHost=commonPropObject.getProperty("httpsProxyHost");
		httpsProxyPort=commonPropObject.getProperty("httpsProxyPort");
	}
	
	public void setDefaultUIRootNick(String inRootNick) throws IOException {
		setPropertyFileValue(commonSyspropertiesFILENAME, "defaultUIRootNick", inRootNick);
		System.out.println(" At setDefaultUIRootNick DefaultUIRootNick is " + defaultUIRootNick); 
		defaultUIRootNick = inRootNick;
	}

	public void checkSetProxy(){
		if (!PROXY_CHECKED) {
			logger.info("At checkSetProxy proxy not yet set");
    		System.out.println("Proxy not checked yet");
			PROXY_CHECKED = true;
		    try {
		        final URL url = new URL("http://www.google.com");
		        final URLConnection conn = url.openConnection();
		        conn.connect();
		        conn.getInputStream().close();
				logger.info("Able to connect to google without proxy");
		    } catch (IOException e) {
				logger.info("At checkSetProxy couldn't test connect with google");

		        System.setProperty("http.proxyHost", httpProxyHost);
		        System.setProperty("http.proxyPort", httpProxyPort);        
		        System.setProperty("https.proxyHost", httpsProxyHost);
		        System.setProperty("https.proxyPort", httpsProxyPort);        
				logger.info("Proxy set");
				
		    }
		} else {
    		System.out.println("Proxy already checked");
		}
	}
	
	public String getCurrentRootNick() {
		String rootNick = null;
		if (processingRootNick == null || processingRootNick.equalsIgnoreCase("")) {
			System.out.println("At getCurrentRootNick processingRootNick1 is " + processingRootNick);
			rootNick = defaultUIRootNick;
		} else {
			System.out.println("At getCurrentRootNick processingRootNick2 is " + processingRootNick);
			rootNick = processingRootNick;
		}
		System.out.println("At getCurrentRootNick processingRootNick3 is " + rootNick);
		return rootNick;
	}
	
	public void setSysCompCurrLocalLogUpdateTm(String inSysCompCurrLocalLogUpdateTm) throws IOException {
		setPropertyFileValue(commonSyspropertiesFILENAME, sysCompCurrLocalLogUpdateTmLIT, inSysCompCurrLocalLogUpdateTm);
	}

	private void readServerSideProperties() throws IOException {
		Properties serverPropObject = new Properties();
		InputStream propertiesStream = null;
		propertiesStream = new FileInputStream(serverPropertiesFILENAME);
		// load a properties file
		serverPropObject.load(propertiesStream);
		BASE_CATLOG_SERVER_FOLDER = serverPropObject.getProperty("bsSrverFolder");
		
		String serverRootNicksText = serverPropObject.getProperty("serverrootNicks");
		serverRootNicks = serverRootNicksText.split(";");
		// set the properties value
		System.out.println("serverRootStrings size: " + serverRootNicks.length);
		for (int i=0;i<serverRootNicks.length;i++){
			System.out.println("serverRootStrings["+ i + "]=" + serverRootNicks[i]);
		}
		serversMasterCopyofCatalogDbLocalFolder = installFileFolder + localFileSeparator + serverPropObject.getProperty("serversOwnCopyofCatalogDbLocalFolder");
		serversMasterCopyofCatalogDbPrefix = serverPropObject.getProperty("serversMasterCopyofCatalogDbPrefix");

		reqTrackersFolderLocal = installFileFolder + localFileSeparator + serverPropObject.getProperty("reqTrackersFolderLocal");
		System.out.println(" reqtrackersfolderlocal property file value is " +  serverPropObject.getProperty("reqTrackersFolderLocal"));
		System.out.println(" reqTrackersFolderLocal is " + installFileFolder + localFileSeparator + serverPropObject.getProperty("reqTrackersFolderLocal"));

		versioningFilesFolderLocal = installFileFolder + localFileSeparator + serverPropObject.getProperty("versioningFilesFolderLocal");
		System.out.println(" versioningFilesFolderLocal property file value is " +  serverPropObject.getProperty("versioningFilesFolderLocal"));
		System.out.println(" versioningFilesFolderLocal is " + installFileFolder + localFileSeparator + serverPropObject.getProperty("versioningFilesFolderLocal"));

		erlMaxVersions = Integer.parseInt(serverPropObject.getProperty("erlMaxVersions"));
	}

	private void readCommonSysCompProperties() throws IOException, ParseException {
		Properties commonSysCompPropObject = new Properties();
		InputStream sysPropertiesStream = null;
			sysPropertiesStream = new FileInputStream(commonSyspropertiesFILENAME);
		//sysPropertiesStream = getClass().getResourceAsStream(commonSyspropertiesFileName);
		// load a properties file
		commonSysCompPropObject.load(sysPropertiesStream);
		String sysCompCurrLocalLogUpdateTmString = commonSysCompPropObject.getProperty(sysCompCurrLocalLogUpdateTmLIT);
		sysCompCurrLocalLogUpdateTm = getDateFromString(sysCompCurrLocalLogUpdateTmString);
		System.out.println("sysCompDownloadedAtString = " + sysCompCurrLocalLogUpdateTmString);
		System.out.println("sysCompDownloadedAt = " + sysCompCurrLocalLogUpdateTm);
		defaultUIRootNick = commonSysCompPropObject.getProperty("defaultUIRootNick");
		System.out.println("At readCommonSysCompProperties defaultUIRootNick = " + defaultUIRootNick);
	}
	
	private void readClienSideProperties() throws IOException {
		Properties clientPropObject = new Properties();
		InputStream clientSidePropertiesStream = null;
		clientSidePropertiesStream = new FileInputStream(clientPropertiesFILENAME);
		//clientSidePropertiesStream = getClass().getResourceAsStream(clientPropertiesFileName);
		// load a properties file
		clientPropObject.load(clientSidePropertiesStream);

		CLIENT_MACHNE_FOLDER = clientPropObject.getProperty("clientMcFolder");
		userName = clientPropObject.getProperty("userName");

		osHandlerName = clientPropObject.getProperty("OSHandler");
		backupRootNick = clientPropObject.getProperty("backupRootNick");
		remoteBackupFolder = clientPropObject.getProperty("remoteBackupFolder");
	}


	public String getClientDbFileLocation(){
		String clientDbFileLocation = clientDbFilePath + File.separatorChar + clientDbFileName;
		System.out.println("clientDbFileLocation = " + clientDbFileLocation);
		return clientDbFileLocation;
	}

	public String getClientSideCatalogDbReceiveFolderOfRoot(String inRootNick){

		String catalogDbReceiveFolderOfRoot = clientSideCatalogDbReceiveFolder + File.separatorChar + inRootNick;
		
		System.out.println("inRootNick = " + inRootNick);
		System.out.println("clientSideCatalogDbReceiveFolder = " + clientSideCatalogDbReceiveFolder);
		System.out.println("catalogDbReceiveFolderOfRoot = " + catalogDbReceiveFolderOfRoot);
		return catalogDbReceiveFolderOfRoot;
	}
	
	public String getNewCatalogDbPublishFileName(String inRootNick) {
		String catalogDbPublishFilePrefixWithRoot = catalogDbPublishFilePrefix + inRootNick + getCurrentTimeStamp();
		return catalogDbPublishFilePrefixWithRoot;
	}
	
	public String getServerSideSideCatalogDbPublishFolderOfRoot(){

		return serverSideSideCatalogDbPublishFolder;
	}
					 
	public String getServersMasterCopyofCatalogDbLocalFileOfRoot(String inRootNick){
		System.out.println("inRootNick = " + inRootNick);
		String serversMasterCopyofCatalogDbLocalFolderOfRoot = serversMasterCopyofCatalogDbLocalFolder + File.separatorChar + serversMasterCopyofCatalogDbPrefix + inRootNick;
		System.out.println("serversOwnCopyofCatalogDbLocalFolder = " + serversMasterCopyofCatalogDbLocalFolder);
		System.out.println("serversOwnCopyofCatalogDbPrefix = " + serversMasterCopyofCatalogDbPrefix);
		System.out.println("serversOwnCopyofCatalogDbLocalFolderOfRoot = " + serversMasterCopyofCatalogDbLocalFolderOfRoot);
		return serversMasterCopyofCatalogDbLocalFolderOfRoot;
	}

	public void readExtendedCatalogServerPrimaryProperties(String inXtdArg) throws IOException {
		//to be overriden from xtdCommons
		System.out.println("xxx Commons readExtendedCatalogServerPrimaryProperties why this is called inXtdArg is " + inXtdArg);
	}
	
	public void readExtendedCatalogServerSecondaryProperties(String inXtdArg) throws IOException {
		//to be overriden from xtdCommons
		System.out.println("xxx Commons readExtendedCatalogServerSecondaryProperties why this is called inXtdArg is " + inXtdArg);
	}
	
	public Commons(int inProcessMode, String inProcessingRootNick, String inXtdArg) throws IOException, ParseException {
		System.out.println("Commons constructor initiating inProcessMode = " + inProcessMode);
		processMode = inProcessMode;
		processingRootNick = inProcessingRootNick;

		System.out.println("Commons constructor inProcessMode = " + inProcessMode);
		System.out.println("Commons constructor inProcessingRootNick = " + inProcessingRootNick);
		
		String current = null;
		current = new java.io.File( "." ).getCanonicalPath();
	    System.out.println("Current dir:"+current);

		if (inProcessMode == EXTENDED_CATALOG_SERVER){
			readExtendedCatalogServerPrimaryProperties(inXtdArg);
		}

	    readCommonPropForAllMachines();

		readCommonSysCompProperties();

		if (inProcessMode == CLIENT_MACHINE){
			readClienSideProperties();
		} else if (inProcessMode == EXTENDED_CATALOG_SERVER){
			readExtendedCatalogServerSecondaryProperties(inXtdArg);
		} else if (inProcessMode == BASE_CATALOG_SERVER){
			readServerSideProperties();
		}

	}

	public static Commons getInstance(int inProcessMode, String inProcessingRootNick) throws IOException, ParseException {
		/* The same method is duplicated in Commons & XtdCommons as no other choice. 
		 * Any change here should be applied there as well */
		Commons requestedInstance = null;

		System.out.println("Commons getInstance inProcessMode = " + inProcessMode);
		System.out.println("Commons getInstance inProcessingRootNick = " + inProcessingRootNick);
		
		if (inProcessMode == BASE_CATALOG_SERVER) {
			if (baseCatalogServerCommonsInstance == null  || !baseCatalogServerCommonsInstance.getCurrentRootNick().equalsIgnoreCase(inProcessingRootNick)) {
				baseCatalogServerCommonsInstance = new Commons(inProcessMode,inProcessingRootNick,null);
			}
			requestedInstance = baseCatalogServerCommonsInstance;
		} else if (inProcessMode == CLIENT_MACHINE) {
			System.out.println("Commons init2 inProcessMode = " + inProcessMode);
			if (clientMachineCommonsInstance == null || !clientMachineCommonsInstance.getCurrentRootNick().equalsIgnoreCase(inProcessingRootNick)) {
				System.out.println("Commons init3 inProcessMode = " + inProcessMode);
				System.out.println("Commons init3a inProcessingRootNick = " + inProcessingRootNick);
				clientMachineCommonsInstance = new Commons(inProcessMode,inProcessingRootNick,null);
				System.out.println("Commons init3b inProcessMode = " + inProcessMode);
			}
			System.out.println("Commons init4 inProcessMode = " + inProcessMode);

			requestedInstance = clientMachineCommonsInstance;
		} else {
			System.out.println("inProcessMode to be handled by extended common for " + inProcessMode);
	    }
		System.out.println("Commons initx inProcessMode = " + inProcessMode);
		
		return requestedInstance;		
	}

	public static Commons getInstance(int inProcessMode) throws IOException, ParseException {
		Commons requestedInstance = null;
		try {
			requestedInstance = getInstance(inProcessMode,null);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			logger.error("Error in Commons getInstance processMode = " + inProcessMode);
			System.exit(Commons.FATALEXITCODE);
		}
		return requestedInstance;
	}

	public String getFullLocalPathFileNameOfNewArtifact(String inRootNick, String inRelevance, String inLocalFileName) {
		System.out.println("getFullLocalPathFileNameOfNewArtifact inRootNick=" + inRootNick + "::" + "inRelevance=" + inRelevance + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfNewArtifact = newArtifactsFolder
				+ localFileSeparator + inRootNick + localFileSeparator + inRelevance + localFileSeparator + inLocalFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfNewArtifact);
		return fullLocalPathFileNameOfNewArtifact;
	}	
	
	public String getFullLocalPathFileNameOfDownloadedArtifact(String inRootNick, String inRelevance, String inLocalFileName) {
		System.out.println("getFullLocalPathFileNameOfDownloadedArtifact inRootNick=" + inRootNick + "::" + "inRelevance=" + inRelevance + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfDownloadedArtifact = contentDownLoadFolder
				+ localFileSeparator + inRootNick + localFileSeparator + inRelevance + localFileSeparator + inLocalFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfDownloadedArtifact);
		return fullLocalPathFileNameOfDownloadedArtifact;
	}
	
	public String getFullLocalPathFileNameOfTempFile(String inRootNick, String inLocalFileName) {
		System.out.println("inRootNick=" + inRootNick + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfTempFile = tempFolder
				+ localFileSeparator + inRootNick + localFileSeparator + inLocalFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfTempFile);
		return fullLocalPathFileNameOfTempFile;
	}	

	public String getFullLocalPathFileNameOfDownloadedDbFile(String inRootNick, String inLocalFileName) {
		System.out.println("inRootNick=" + inRootNick + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfDownloadedDbFile = getClientSideCatalogDbReceiveFolderOfRoot(inRootNick) 
															+ localFileSeparator + inLocalFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfDownloadedDbFile);
		return fullLocalPathFileNameOfDownloadedDbFile;
	}	

	public String getFullLocalPathFileNameOfCatalogdbdownloadfolder(String inRootNick, String inLocalFileName) {
		System.out.println("inRootNick=" + inRootNick + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfCatalogdbdownloadfolder = getClientSideCatalogDbReceiveFolderOfRoot(inRootNick)
				+ localFileSeparator + getFileNameFromFullPath(inLocalFileName,File.separator);
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfCatalogdbdownloadfolder);
		return fullLocalPathFileNameOfCatalogdbdownloadfolder;
	}
		
	public String getFullLocalPathFileNameOfNewReview(String inRootNick, String inRelevance, String inReviewFileName) {
		System.out.println("inRootNick=" + inRootNick + "::" + "inRelevance=" + inRelevance + "::inReviewFileName=" + inReviewFileName);
		String fullLocalPathFileNameOfReview = newReviewsFolder
				+ localFileSeparator + inRootNick + localFileSeparator + inRelevance + localFileSeparator + inReviewFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfReview);
		return fullLocalPathFileNameOfReview;
	}
	
	public String getFullLocalPathFileNameOfDownloadedReview(String inRootNick, String inRelevance, String inReviewFileName) {
		System.out.println("inRootNick=" + inRootNick + "::" + "inRelevance=" + inRelevance + "::inReviewFileName=" + inReviewFileName);
		String fullLocalPathFileNameOfReview = downloadedReviewsFolder
				+ localFileSeparator + inRootNick + localFileSeparator + inRelevance + localFileSeparator + inReviewFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfReview);
		return fullLocalPathFileNameOfReview;
	}

	public String getFullLocalPathFileNameOfResponseFile(String inRootNick, String inLocalFileName) {
		System.out.println("inRootNick=" + inRootNick + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfResponseFile = responsesfolderlocal
				+ localFileSeparator + inRootNick + localFileSeparator + inLocalFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfResponseFile);
		return fullLocalPathFileNameOfResponseFile;
	}
	
	public String getFullPathFromRelativeDownloadPath(String inRootNick, String inRelativeDownloadFilePath) {
		System.out.println("ContentDownLoadFolder::inRelativeDownloadFilePath is " + contentDownLoadFolder + "::" + inRootNick + "::" + inRelativeDownloadFilePath);
		String fullPathFromRelativeDownloadPath = contentDownLoadFolder
				+ localFileSeparator + inRootNick + localFileSeparator + inRelativeDownloadFilePath;
		System.out.println("fullPathFromRelativeDownloadPath is "
				+ fullPathFromRelativeDownloadPath);
		return fullPathFromRelativeDownloadPath;
	}

	public String getTemplateFileName(String inTemplate) {
		System.out.println("At getTemplateFileName inTemplate:" + inTemplate);
		String templateFileString = templatesFolder + localFileSeparator + inTemplate;
		System.out.println("At getTemplateFileName templateFileString:" + templateFileString);
		return templateFileString;
	}	

	public String getLocalReqTrackingPathFile(String inRootNick) {
		System.out.println("inRootNick=" + inRootNick);
		String localReqTrackingPathFile = reqTrackersFolderLocal
				+ localFileSeparator + inRootNick + REQUEST_TRACKING_FILE_SUBFIX;
		System.out.println("FullPath of LocalReqTrackingPathFile:"
				+ localReqTrackingPathFile);
		return localReqTrackingPathFile;
	}

	public String getLocalERLVersioningPathFile(String inRootNick) {
		System.out.println("inRootNick=" + inRootNick);
		String localERLVersioningPathFile = versioningFilesFolderLocal
				+ localFileSeparator + inRootNick + VERSIONING_FILE_SUBFIX;
		System.out.println("FullPath of LocalERLVersioningPath:"
				+ localERLVersioningPathFile);
		return localERLVersioningPathFile;		
	}

	//public String getNewRequestFileName() {
	//	// -- TOBEMODIFOED -- use this function in all places
	//	// This function should be phased out with the one below with ForTS
	//	String requestFileName = userName + getCurrentTimeStamp() + REQUESTFILE_EXTN;
	//	System.out.println("new requestFileName is " + requestFileName);
	//	return requestFileName;
	//}

	public String getNewRequestFileNameForTS(String inTS) {
		// -- TOBEMODIFOED -- use this function in all places
		String requestFileName = userName + inTS + REQUESTFILE_EXTN;
		System.out.println("new requestFileName is " + requestFileName);
		return requestFileName;
	}

	public String getVersionedFileName(String inArtifactName, String inExtension, int inVersionNum) {
		String inVersionedFileName = new String();
		//Rework this method with ContentHandlerSpecs
		inVersionedFileName = inArtifactName
				+ ((inVersionNum!=0)? 
						("_" + inVersionNum): "")
		//		+ inExtension;	//suppress zip extension to show a folder on same name 
				+ (!(isZipFile(inExtension))? inExtension : "");
		System.out.println("localFileName:" + inVersionedFileName);

		return inVersionedFileName;
	}	
	public ResponsePojo createResponsePojo(	ERLpojo inUploadedERLpojo,
											ERLpojo inProcessedERLpojo,
											String inResponseText) {

		ResponsePojo responsePojo = new ResponsePojo(
				inUploadedERLpojo,
				inProcessedERLpojo,
				inResponseText
				);
		return responsePojo;
	}
	
	public Object getJsonDocFromFile(String inFileName, Class inClass) throws FileNotFoundException, UnsupportedEncodingException {
		Object jsonDocObj = null;
		System.out.println("At getJsonDocFromFile inFileName is " + inFileName);
		System.out.println("At getJsonDocFromFile inClass is " + inClass);

		File fileToRead = new File(inFileName);
		InputStream fileInputStream = null;
		if (fileToRead.exists()) {
			fileInputStream = new FileInputStream(fileToRead);
			jsonDocObj = sysGetJsonDocObjFromInputStream(fileInputStream,inClass);
		}
		return jsonDocObj;
	}

	public Object getJsonDocFromString(String inJSonString, Class inClass) {
		return sysGetJsonDocObjFromString(inJSonString,inClass);
	}

	public String getStringFromJson(Object inJsonObj) {
		return sysGetStringFromJsonObj(inJsonObj);
	}

	public Object getXMLObjFromFile(String inFileName, Class inClass) throws FileNotFoundException, JAXBException {
		Object xmlDocObj = null;
		xmlDocObj = sysGetXMLObjFromFile(inFileName,inClass);
		return xmlDocObj;
	}
	
	public Object getJsonDocFromInputStream(InputStream inInputStream, Class inClass) throws UnsupportedEncodingException {
		System.out.println("At getJsonDocFromInputStream inClass is " + inClass);
		Object jsonDocObj = null;
		jsonDocObj = sysGetJsonDocObjFromInputStream(inInputStream,inClass);
		return jsonDocObj;
	}

	public void putJsonDocToFile(String inFileName, Object inGsonDocObj) throws IOException {
		System.out.println("putJsonDocToFile " + inFileName);
		System.out.println("inGsonDocObj " + inGsonDocObj);
		createFolderOfFilePathIfDontExist(inFileName);
		sysPutJsonDocObjToFile(inFileName,inGsonDocObj);
	}

	public InputStream getJsonDocInStream(Object inGsonDocObj) throws UnsupportedEncodingException {
		String jsonDocString = getGson().toJson(inGsonDocObj);
		System.out.println("at getJsonDocInStream jsonDocString is " + jsonDocString);
		InputStream instr = null;
		instr = new ByteArrayInputStream(jsonDocString.getBytes("UTF-8"));
		return instr;
	}
	
	public ResponsePojo createResponsePojoFromXMLDoc(Document inDoc) throws IOException, TransformerException {
		ResponsePojo responsePojo = new ResponsePojo();
		printDocument(inDoc, System.out);
		Element tempFromERLpojoElement = (Element) inDoc.getElementsByTagName("fromERLpojo").item(0);
		if (tempFromERLpojoElement != null){
			responsePojo.fromERLpojo.getERLpojoFromDocElement(tempFromERLpojoElement);
		} else {
			System.out.println("from ERL null");
		}
		Element tempToERLpojoElement = (Element) inDoc.getElementsByTagName("toERLpojo").item(0);
		responsePojo.toERLpojo.getERLpojoFromDocElement(tempToERLpojoElement);
		responsePojo.responseText = ((Element) inDoc.getElementsByTagName("responseText").item(0)).getAttribute("responseText");
		
		return responsePojo;
	}

	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}


	public String getRemoteResponseFileString(String inTimeStamp,
			ArtifactWithRootPojo inArtifactWithRootPojo, String inRemoteFileSeparator) {

		String remoteFileString = getRemoteResponsePickBox(inArtifactWithRootPojo.rootPojo.rootString, inRemoteFileSeparator)
				+ inArtifactWithRootPojo.rootPojo.fileSeparator + userName + inTimeStamp + ".xml";

		return remoteFileString;
	}
	
	public String getRemoteRequestPathFile(String inRootString, String inReqFileName, String inRemoteFileSeparator) {
		String remoteRequestPathFile = getRemoteDropBox(inRootString, inRemoteFileSeparator)
		+ inRemoteFileSeparator + inReqFileName;
		return remoteRequestPathFile;
	}
	
	public String getRemoteDropBox(String inRootString, String inRemoteFileSeparator) {
		String remoteFileString = inRootString + inRemoteFileSeparator
				+ requestdropbox;
		return remoteFileString;
	}

	public String getRemoteResponsePickBox(String inRootString, String inRemoteFileSeparator) {
		String remoteFileString = inRootString + inRemoteFileSeparator
				+ responsepickbox;
		return remoteFileString;
	}

	public String getRemotePathFileName(String inRootString, String inRelevance, String inFileName, String inRemoteFileSeparator){

		String remotePathFileName = "" ;
		if (inFileName.equals("")) return remotePathFileName;
		
		remotePathFileName = inRootString
							+ inRemoteFileSeparator
							+ inRelevance
							+ inRemoteFileSeparator
							+ inFileName;

		System.out.println("remotePathFileName...=" + remotePathFileName);
	
		//SlashFix starts
		//remotePathFileName = remotePathFileName.replace('\\', '/');
		remotePathFileName = remotePathFileName.replace('\\', inRemoteFileSeparator.charAt(0));
		remotePathFileName = remotePathFileName.replace('/', inRemoteFileSeparator.charAt(0));		
		//SlashFix ends
		
		return remotePathFileName;
	}

	public String getRemoteArchivalPathFileName(String inRootString, String inRelevance, String inFileName, String inRemoteFileSeparator){
		String remoteArchPathFileName = "" ;

		if (inFileName.equals("")) return remoteArchPathFileName;
		
		remoteArchPathFileName = inRootString
							+ inRemoteFileSeparator
							+ remoteArhive
							+ inRemoteFileSeparator
							+ inRelevance
							+ inRemoteFileSeparator
							+ inFileName;

		System.out.println("remotePathFileName...=" + remoteArchPathFileName);
	
		//SlashFix starts
		//remoteArchPathFileName = remoteArchPathFileName.replace('\\', '/');
		remoteArchPathFileName = remoteArchPathFileName.replace('\\', inRemoteFileSeparator.charAt(0));
		remoteArchPathFileName = remoteArchPathFileName.replace('/', inRemoteFileSeparator.charAt(0));		
		//SlashFix ends

		return remoteArchPathFileName;
	}
	
	public void openFileToView(String inFileString) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		OSHandler osHandler = (OSHandler) CustomClassLoader.getInstance(osHandlerName,getHandlerJarNames());
		osHandler.openFileToView(inFileString);
	}


	public String[] getExdHandlerJar() {
		//To be overridden from extended Commons for extended process
		System.out.println("wrongly calling getExdHandlerJar from Commons ");

		return null;
	}
	
	public String[] getHandlerJarNames() {
		String[] handlerJarNames = null;
		System.out.println("At getHandlerJarNames ");
		System.out.println("contentHandlersFolder = " + contentHandlersFolder);
		System.out.println("osHandlersFolder = " + osHandlersFolder);
		System.out.println("remoteAccessersFolder = " + remoteAccessersFolder);
		//System.out.println("xtndHandlersFolder = " + xtndHandlersFolder);
		System.out.println("processMode = " + processMode);
		System.out.println("EXTENDED_CATALOG_SERVER = " + EXTENDED_CATALOG_SERVER);

		if (processMode == EXTENDED_CATALOG_SERVER) {
			handlerJarNames = getExdHandlerJar();
		} else {
			handlerJarNames = new String[] {contentHandlersFolder,osHandlersFolder,remoteAccessersFolder};
		}
		System.out.println("At commons handlerJarNames length is " + handlerJarNames.length);
		return handlerJarNames;
	}

	public String storeRemoteFile(InputStream inRemoteInputStream,
			String inFileNameFromURL) throws IOException {

		OutputStream outputStream = null;
		String outPutFileName = tempFolder + localFileSeparator
				+ inFileNameFromURL;
		storeInStream(inRemoteInputStream, outPutFileName);

		return outPutFileName;
	}

	public void archiveLocalFile(String inLocalFileLocation) throws IOException {
		File sourcelocalFile = new File(inLocalFileLocation);
		if (!sourcelocalFile.exists()) {
			logger.info("Nothing to archive as the source file " + inLocalFileLocation + " doesn't exist");
			return;
		}
		String archiveFileName = localArhive + File.separator + sourcelocalFile.getName() + "_ArchivedAt_" + getCurrentTimeStamp();
		File archiveFile = new File(archiveFileName);
		FileUtils.moveFile(sourcelocalFile, archiveFile);
		logger.info("Archived the file " + inLocalFileLocation + " into " + archiveFileName);
	}	
	
	public String getRemoteContentDropFileName(String inRootString, String inFileName, String inRemoteFileSeparator, String inAuthor) {
		System.out.println("At getRemoteContentDropFileName ");
		System.out.println("contentdropbox is " + contentdropbox);
		System.out.println("inAuthor is " + inAuthor);
		System.out.println("inFileName is " + inFileName);
		
		String remoteFileString = inRootString
				+ inRemoteFileSeparator + contentdropbox + inRemoteFileSeparator
				+ inAuthor + inRemoteFileSeparator
				+ inFileName;
		return remoteFileString;
	}

	public ArrayList<String> sortLatestRemoteCatalogPublishFile(
			ArrayList<String> inPublishFileNameURLs) {
		Collections.sort(inPublishFileNameURLs, new CompareForLaterName());
		return inPublishFileNameURLs;
	}

	public String getRolledUpRelevance(String inRelevance, int inRollupLevel) {
		
		System.out.println("getRolledUpRelevance : with inRelevance:" + inRelevance);
		System.out.println("getRolledUpRelevance : with inRollupLevel:" + inRollupLevel);
		String rejointString;
		if (inRollupLevel == -1) {
			System.out.println("inRollupLevel -1, hence leaving the Relevance untouched");
			rejointString = inRelevance;
		} else {
			String[] splitStrings = StringUtils.split(inRelevance,localFileSeparator);
			System.out.println("getRolledUpRelevance : splitStrings size:" + splitStrings.length);
			System.out.println("getRolledUpRelevance : splitStrings:" + splitStrings);
			rejointString = StringUtils.join(splitStrings,localFileSeparator,0,inRollupLevel);
			System.out.println("getRolledUpRelevance : rejointString:" + rejointString);
		}
		return rejointString;
	}	
}