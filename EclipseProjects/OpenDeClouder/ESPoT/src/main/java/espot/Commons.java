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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public final static String siteToCheckInternetLIT = "siteToCheckInternet";
	public final static String sysCompCurrLocalLogUpdateTmLIT = "sysCompCurrLocalLogUpdateTm";
	public final static String suppressSysCompRefreshLIT = "suppressSysCompRefresh";
	public final static String adminBranchRelevanceLIT = "adminBranchRelevance";
	public final static String STR_YES = "YES";
	public final static String STR_NO = "NO";
	public static final String SCREENROWNUMLIT = "ScreenRowNum";
	
	public boolean suppressSysCompRefresh = false;
	public String platformRoot = null;
	public final static String platformRootLIT = "platformRoot";


	public final static String ARTIFACT_PRIME_FILE = "primeFile.json";

	public final static String REQUEST_TRACKING_FILE_SUBFIX = "_RequestsTracker.json";
	public final static String VERSIONING_FILE_SUBFIX = "_versioningDetails.json";
	public final static String REQUESTFILE_EXTN = ".json";
	
	public final static int BASE_CATALOG_SERVER = 1;
	public final static int CLIENT_MACHINE = 2;
	public final static int EXTENDED_CATALOG_SERVER = 3;
	public int processMode = 0;
	public final static int FATALEXITCODE = 8;

	public final static int LOGINID_MAXLEN = 20;	// rootSysLoginID length limit
	public final static String USERNAME_PREFIX_LIT = "UserName.";
	
	public static String BASE_CATLOG_SERVER_FOLDER = "bsSrver";
	public static String CLIENT_MACHNE_FOLDER = "clientMc";
	public String xtdCatalogSrvrFolder = "extdSrvr";

	public int erlMaxVersions;
	public int inactiveAgingDaysLimit; //initiated with default

	public String remoteArchive = null;
	public String remoteInactiveERLsArchive = null;
	public String remoteErroredRequests = null;

	public static Commons baseCatalogServerCommonsInstance = null;
	public static Commons clientMachineCommonsInstance = null;
	public static Commons extendedCatalogServerCommonsInstance = null;
	
	private static boolean proxy_checked_already = false;

	public Date sysCompCurrLocalLogUpdateTm = null;
	public int catalogDownloadTimeGapSec = 0;

	public String localFileSeparator = null;
	public String installFileFolder = null;
	public String configDataFolder = null;
	//private String certificatFile = null;
	public String certificatesFolder = null;

	private String localArchive = null;
	private int archiveDupeMax; // this gets overridden from the property file
	
	private String newArtifactsFolder = null;
	private String newReviewsFolder = null;	
	private String responsesfolderlocal = null;
	private String contentDownLoadFolder = null;
	private String downloadedReviewsFolder = null;

	private String templatesFolder = null;
	private String clientDbFilePath = null;
	private String clientDbFileName = null;

	public String contentHandlersFolder = null;
	public String osHandlersFolder = null;
	public String remoteAccessersFolder = null;

	public String backgroundImagePathFileName = null;
	public String applicationIcon = null;
	public String rootConfigFolder = null;
	public String artifactsFolder = null;
	private String tempFolder = null;
	
	//private String backupRootNick = null;
	//private String remoteBackupFolder = null;
	
	private String remoteArtifacts = null;	//both artifacts and remarks are maintained in hte same branch
	private String contentdropbox = null;
	private String requestdropbox = null;
	private String responsepickbox = null;

	public String sysUpdateLogDoc = null;
	
	public String[] serverRootNicks;

	private String catalogDbPublishFilePrefix = null;	
	private String clientSideCatalogDbReceiveFolder=null;
	private String serverSideSideCatalogDbPublishFolder=null;
	private String serversMasterCopyofCatalogDbLocalFolder=null;
	private String serversMasterCopyofCatalogDbPrefix=null;

	public String downloadedCatalogDetailsFile=null;

	public String sysDbFileLocation = null;
	private String reqTrackersFolderLocal = null;
	private String versioningFilesFolderLocal = null;
	//private String remoteReviewsFolder = null;
	public String publishedRootsFileName = null;
	public String subscribedRootNicksFileName = null;
	public String userName = null;
	private String osHandlerName = null;
	private String defaultUIRootNick = null;
	private String processingRootNick = null;
	boolean remoteCommunicationInitiated = false;

	public String adminBranchRelevance;
	private String siteToCheckInternet;
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


	public Properties getPropertiesFromFile(String inPropertyFileName) throws IOException{
		Properties propObject = new Properties();
		InputStream propertiesInStream = new FileInputStream(inPropertyFileName);
		propObject.load(propertiesInStream);
		propertiesInStream.close();
		return propObject;
	}
	
	public String readPropertyFromFile(String inPropName, String inPropFileName) throws IOException{
		Properties propObject = getPropertiesFromFile(inPropFileName);
		return propObject.getProperty(inPropName);
	}
	
	
	private void readCommonPropForAllMachines() throws IOException {
//		InputStream commonPropertiesInStream = null;
//		Properties commonPropObject = new Properties();
//
//		commonPropertiesInStream = new FileInputStream(commonPropertiesFILENAME);
//		commonPropObject.load(commonPropertiesInStream);
//		commonPropertiesInStream.close();

		Properties commonPropObject = getPropertiesFromFile(commonPropertiesFILENAME);
		
		
		localFileSeparator = Character.toString(File.separatorChar);

		installFileFolder = commonPropObject.getProperty("installFileFolder") + localFileSeparator + System.getProperty("user.name");
		
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
		applicationIcon = configDataFolder + localFileSeparator + commonPropObject.getProperty("ApplicationIcon");
		rootConfigFolder = configDataFolder + localFileSeparator + commonPropObject.getProperty("rootConfigFolder");

		//folders and content that sit within configDataFolder ends/////////////
		////////////////////////////////////////////////////////////////////////

		artifactsFolder= System.getProperty("user.home") + localFileSeparator + commonPropObject.getProperty("artifactsFolder") + localFileSeparator + folderStub;
		
		////////////////////////////////////////////////////////////////////////
		//folders and content that sit within Artifacts path starts/////////////
		tempFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("tempfolder");
		newArtifactsFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("newArtifactsFolder");
		newReviewsFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("newReviewsFolder");
		responsesfolderlocal = artifactsFolder + localFileSeparator + commonPropObject.getProperty("responsesfolderlocal");
		contentDownLoadFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("ContentDownLoadFolder");
		downloadedReviewsFolder = artifactsFolder + localFileSeparator + commonPropObject.getProperty("downloadedReviewsFolder");
		localArchive = artifactsFolder + localFileSeparator + commonPropObject.getProperty("localArchive");
		archiveDupeMax = Integer.parseInt(commonPropObject.getProperty("archiveDupeMax"));
		//folders and content that sit within Artifacts path ends///////////////
		////////////////////////////////////////////////////////////////////////

		templatesFolder = installFileFolder + localFileSeparator + commonPropObject.getProperty("templatesFolder");
		clientDbFilePath = installFileFolder + localFileSeparator + folderStub + localFileSeparator + commonPropObject.getProperty("clientDbFilePath");

		System.out.println("here is the issue clientDbFilePath = " + clientDbFilePath);
		
		clientDbFileName = commonPropObject.getProperty("clientDbFileName");

		clientSideCatalogDbReceiveFolder = installFileFolder + localFileSeparator + commonPropObject.getProperty("clientSideCatalogDbReceiveFolder");
		serverSideSideCatalogDbPublishFolder = commonPropObject.getProperty("serverSideSideCatalogDbPublishFolder");

		remoteArtifacts = commonPropObject.getProperty("remoteArtifacts");
		contentdropbox = commonPropObject.getProperty("contentdropbox");
		requestdropbox = commonPropObject.getProperty("requestdropbox");
		responsepickbox = commonPropObject.getProperty("responsepickbox");
		//remoteReviewsFolder = commonPropObject.getProperty("remoteReviewsFolder");
	
		//remoteArchive = commonPropObject.getProperty("remoteArchive");		
		//remoteInactiveERLsArchive = commonPropObject.getProperty("remoteInactiveERLsArchive");
		//remoteErroredRequests = commonPropObject.getProperty("remoteErroredRequests");
		
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

		adminBranchRelevance = commonPropObject.getProperty(adminBranchRelevanceLIT);		
		System.out.println("adminBranchRelevance = " + adminBranchRelevance);

		siteToCheckInternet=commonPropObject.getProperty(siteToCheckInternetLIT);
		
		httpProxyHost=commonPropObject.getProperty("httpProxyHost");
		httpProxyPort=commonPropObject.getProperty("httpProxyPort");
		httpsProxyHost=commonPropObject.getProperty("httpsProxyHost");
		httpsProxyPort=commonPropObject.getProperty("httpsProxyPort");

		System.out.println("siteToCheckInternet = " + siteToCheckInternet);
		System.out.println("httpProxyHost = " + httpProxyHost);
		System.out.println("httpProxyPort = " + httpProxyPort);
		System.out.println("httpsProxyHost = " + httpsProxyHost);
		System.out.println("httpsProxyPort = " + httpsProxyPort);

	}
	
	public void setDefaultUIRootNick(String inRootNick) throws IOException {
		setPropertyFileValue(commonSyspropertiesFILENAME, "defaultUIRootNick", inRootNick);
		System.out.println(" At setDefaultUIRootNick DefaultUIRootNick is " + defaultUIRootNick); 
		defaultUIRootNick = inRootNick;
		userName = readRootSysLoginIDFromClienSideProperties(defaultUIRootNick);		
	}
	
	public boolean isInternetAvailable(){
		boolean internetAvailable = false;
	    try {
	
	    	checkSetProxy();
	    	
	    	System.out.println("At Commons isInternetAvailable about to check via " + siteToCheckInternet);
			logger.info("At Commons isInternetAvailable about to check via " + siteToCheckInternet);
	    	
	        final URL url = new URL(siteToCheckInternet);
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
			internetAvailable = true;

	        logger.info("At commons isInternetAvailable able to connect to " + siteToCheckInternet);
			System.out.println("At commons isInternetAvailable able to connect to " + siteToCheckInternet);

	    } catch (IOException e) {

	    	logger.info("At commons isInternetAvailable couldn't test connect with " + siteToCheckInternet);
			System.out.println("At commons isInternetAvailable couldn't test connect with " + siteToCheckInternet);
	    }
		
		return internetAvailable;
	}

	public void checkSetProxy() {
		if (!proxy_checked_already) {
			logger.info("At checkSetProxy proxy not yet set");
    		System.out.println("Proxy not checked yet");
			proxy_checked_already = true;
		    try {
				System.out.println("At Commons checkSetProxy about to check via " + siteToCheckInternet);
				logger.info("At Commons checkSetProxy about to check via " + siteToCheckInternet);
		    	
		        final URL url = new URL(siteToCheckInternet);
		        final URLConnection conn = url.openConnection();
		        conn.connect();
		        conn.getInputStream().close();

				logger.info("At commons checkSetProxy able to connect to " + siteToCheckInternet + " without proxy");
		    } catch (IOException e) {
				logger.info("At commons checkSetProxy couldn't test connect with " + siteToCheckInternet + " without proxy");
		        
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
//		Properties serverPropObject = new Properties();
//
//		InputStream propertiesStream = new FileInputStream(serverPropertiesFILENAME);
//		serverPropObject.load(propertiesStream);
//		propertiesStream.close();

		Properties serverPropObject = getPropertiesFromFile(serverPropertiesFILENAME);
		
		
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
		System.out.println(" erlMaxVersions is " + erlMaxVersions);
		
		inactiveAgingDaysLimit = Integer.parseInt(serverPropObject.getProperty("inactiveAgingDaysLimit"));
		System.out.println(" inactiveAgingDaysLimit is " + inactiveAgingDaysLimit);
		
		remoteArchive = serverPropObject.getProperty("remoteArchive");		
		System.out.println(" remoteArchive is " + remoteArchive);
		
		remoteInactiveERLsArchive = serverPropObject.getProperty("remoteInactiveERLsArchive");
		System.out.println(" remoteInactiveERLsArchive is " + remoteInactiveERLsArchive);
		
		remoteErroredRequests = serverPropObject.getProperty("remoteErroredRequests");	
		System.out.println(" remoteErroredRequests is " + remoteErroredRequests);
	}

	private void readCommonSysCompProperties() throws IOException, ParseException {
//		Properties commonSysCompPropObject = new Properties();
//		
//		InputStream sysPropertiesStream = new FileInputStream(commonSyspropertiesFILENAME);
//		commonSysCompPropObject.load(sysPropertiesStream);
//		sysPropertiesStream.close();

		Properties commonSysCompPropObject = getPropertiesFromFile(commonSyspropertiesFILENAME);
		
		String sysCompCurrLocalLogUpdateTmString = commonSysCompPropObject.getProperty(sysCompCurrLocalLogUpdateTmLIT);
		platformRoot = commonSysCompPropObject.getProperty(platformRootLIT);
		
		sysCompCurrLocalLogUpdateTm = getDateFromString(sysCompCurrLocalLogUpdateTmString);
		System.out.println("sysCompDownloadedAtString = " + sysCompCurrLocalLogUpdateTmString);
		System.out.println("sysCompDownloadedAt = " + sysCompCurrLocalLogUpdateTm);
		defaultUIRootNick = commonSysCompPropObject.getProperty("defaultUIRootNick");
		System.out.println("At readCommonSysCompProperties defaultUIRootNick = " + defaultUIRootNick);
	}
	
	private void readClienSideProperties() throws IOException {
//		Properties clientPropObject = new Properties();
//
//		InputStream clientSidePropertiesStream = new FileInputStream(clientPropertiesFILENAME);
//		clientPropObject.load(clientSidePropertiesStream);
//		clientSidePropertiesStream.close();

		Properties clientPropObject = getPropertiesFromFile(clientPropertiesFILENAME);
		
		
		//CLIENT_MACHNE_FOLDER = clientPropObject.getProperty("clientMcFolder");	why this is required???
		//userName = clientPropObject.getProperty("userName");
		System.out.println("At readClienSideProperties defaultUIRootNick is " + defaultUIRootNick);

		userName = readRootSysLoginIDFromClienSideProperties(defaultUIRootNick);

		System.out.println("At readClienSideProperties userName is " + userName);

		osHandlerName = clientPropObject.getProperty("OSHandler");
		//backupRootNick = clientPropObject.getProperty("backupRootNick");
		//remoteBackupFolder = clientPropObject.getProperty("remoteBackupFolder");
	}

	public String readRootSysLoginIDFromClienSideProperties(String inRootNick) throws IOException {
//		Properties clientPropObject = new Properties();
//
//		InputStream clientSidePropertiesStream = new FileInputStream(clientPropertiesFILENAME);
//		clientPropObject.load(clientSidePropertiesStream);
//		clientSidePropertiesStream.close();

		//Properties clientPropObject = getPropertiesFromFile(clientPropertiesFILENAME);		
		System.out.println("At readRootSysLoginIDFromClienSideProperties USERNAME_PREFIX_LIT + inRootNick is " + USERNAME_PREFIX_LIT + inRootNick);		
		//return clientPropObject.getProperty(USERNAME_PREFIX_LIT + inRootNick);
		
		return readPropertyFromFile(USERNAME_PREFIX_LIT + inRootNick, clientPropertiesFILENAME);
	}

	public void setRootSysLoginIDInClienSideProperties(String inRootNick, String inRootSysLoginID) throws IOException {
		setPropertyFileValue(clientPropertiesFILENAME, USERNAME_PREFIX_LIT + inRootNick, inRootSysLoginID);
	}

	public String getClientDbFileLocation(){
		String clientDbFileLocation = clientDbFilePath + localFileSeparator + clientDbFileName;
		System.out.println("clientDbFileLocation = " + clientDbFileLocation);
		return clientDbFileLocation;
	}

	public String getClientSideCatalogDbReceiveFolderOfRoot(String inRootNick){

		String catalogDbReceiveFolderOfRoot = clientSideCatalogDbReceiveFolder + localFileSeparator + inRootNick;
		
		System.out.println("inRootNick = " + inRootNick);
		System.out.println("clientSideCatalogDbReceiveFolder = " + clientSideCatalogDbReceiveFolder);
		System.out.println("catalogDbReceiveFolderOfRoot = " + catalogDbReceiveFolderOfRoot);
		return catalogDbReceiveFolderOfRoot;
	}

//	private String newArtifactsFolder = null;
//	private String newReviewsFolder = null;	
//	private String responsesfolderlocal = null;
//	private String contentDownLoadFolder = null;
//	private String downloadedReviewsFolder = null;
//	private String clientSideCatalogDbReceiveFolder=null;

	public String getNewArtifactsFolder(String inRootNick){
		return getNewArtifactsFolder() + localFileSeparator + inRootNick;
	}
	public String getNewArtifactsFolder(){
		return newArtifactsFolder;
	}

	public String getNewReviewsFolder(String inRootNick){
		return getNewReviewsFolder() + localFileSeparator + inRootNick;
	}

	public String getNewReviewsFolder(){
		return newReviewsFolder;
	}

	public String getResponsesfolderlocal(String inRootNick){
		return getResponsesfolderlocal() + localFileSeparator + inRootNick;
	}

	public String getResponsesfolderlocal(){
		return responsesfolderlocal;
	}

	public String getContentDownLoadFolder(String inRootNick){
		return getContentDownLoadFolder() + localFileSeparator + inRootNick;
	}

	public String getContentDownLoadFolder(){
		return contentDownLoadFolder;
	}

	public String getDownloadedReviewsFolder(String inRootNick){
		return getDownloadedReviewsFolder() + localFileSeparator + inRootNick;
	}

	public String getDownloadedReviewsFolder(){
		return downloadedReviewsFolder;
	}
	
	public String getlocalArhiveFolder(){
		return localArchive;
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
		String serversMasterCopyofCatalogDbLocalFolderOfRoot = serversMasterCopyofCatalogDbLocalFolder + localFileSeparator + serversMasterCopyofCatalogDbPrefix + inRootNick;
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

	    readCommonPropForAllMachines(); // Its important to call this method only after readExtendedCatalogServerPrimaryProperties
	    								// as it needs xtdCatalogSrvrFolder name

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
		String fullLocalPathFileNameOfNewArtifact = getNewArtifactsFolder(inRootNick) + localFileSeparator + inRelevance + localFileSeparator + inLocalFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfNewArtifact);
		return fullLocalPathFileNameOfNewArtifact;
	}	
	
	public String getFullLocalPathFileNameOfDownloadedArtifact(String inRootNick, String inRelevance, String inLocalFileName) {
		System.out.println("getFullLocalPathFileNameOfDownloadedArtifact inRootNick=" + inRootNick + "::" + "inRelevance=" + inRelevance + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfDownloadedArtifact = getContentDownLoadFolder(inRootNick) + localFileSeparator + inRelevance + localFileSeparator + inLocalFileName;
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
				+ localFileSeparator + getFileNameFromFullPath(inLocalFileName,localFileSeparator);
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfCatalogdbdownloadfolder);
		return fullLocalPathFileNameOfCatalogdbdownloadfolder;
	}
		
	public String getFullLocalPathFileNameOfNewReview(String inRootNick, String inRelevance, String inReviewFileName) {
		System.out.println("inRootNick=" + inRootNick + "::" + "inRelevance=" + inRelevance + "::inReviewFileName=" + inReviewFileName);
		String fullLocalPathFileNameOfReview = getNewReviewsFolder(inRootNick) + localFileSeparator + inRelevance + localFileSeparator + inReviewFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfReview);
		return fullLocalPathFileNameOfReview;
	}
	
	public String getFullLocalPathFileNameOfDownloadedReview(String inRootNick, String inRelevance, String inReviewFileName) {
		System.out.println("inRootNick=" + inRootNick + "::" + "inRelevance=" + inRelevance + "::inReviewFileName=" + inReviewFileName);
		String fullLocalPathFileNameOfReview = getDownloadedReviewsFolder(inRootNick) + localFileSeparator + inRelevance + localFileSeparator + inReviewFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfReview);
		return fullLocalPathFileNameOfReview;
	}

	public String getFullLocalPathFileNameOfResponseFile(String inRootNick, String inLocalFileName) {
		System.out.println("inRootNick=" + inRootNick + "::inLocalFileName=" + inLocalFileName);
		String fullLocalPathFileNameOfResponseFile = getResponsesfolderlocal(inRootNick) + localFileSeparator + inLocalFileName;
		System.out.println("FullPathFromRelevancePath:"
				+ fullLocalPathFileNameOfResponseFile);
		return fullLocalPathFileNameOfResponseFile;
	}
	
	public String getFullPathFromRelativeDownloadPath(String inRootNick, String inRelativeDownloadFilePath) {
		System.out.println("ContentDownLoadFolder::inRelativeDownloadFilePath is " + contentDownLoadFolder + "::" + inRootNick + "::" + inRelativeDownloadFilePath);
		String fullPathFromRelativeDownloadPath = getContentDownLoadFolder(inRootNick) + localFileSeparator + inRelativeDownloadFilePath;
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
			try {
				jsonDocObj = sysGetJsonDocObjFromInputStream(fileInputStream,inClass);
				fileInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Error in Commons getJsonDocFromFile while closing FileName = " + inFileName);
				System.exit(Commons.FATALEXITCODE);
			}
		}
		return jsonDocObj;
	}

	public Object getJsonDocFromString(String inJSonString, Class inClass) {
		return sysGetJsonDocObjFromString(inJSonString,inClass);
	}

	public String getStringFromJson(Object inJsonObj) {
		return sysGetStringFromJsonObj(inJsonObj);
	}

//	public Object getXMLObjFromFile(String inFileName, Class inClass) throws FileNotFoundException, JAXBException {
//		Object xmlDocObj = null;
//		xmlDocObj = sysGetXMLObjFromFile(inFileName,inClass);
//		return xmlDocObj;
//	}
	
	public Object getJsonDocFromInputStream(InputStream inInputStream, Class inClass) throws IOException {
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
							+ remoteArtifacts
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

	//public String getRemoteArchivalPathFileName(String inRootString, String inRelevance, String inFileName, String inRemoteFileSeparator){
	//	String remoteArchPathFileName = "" ;
	//
	//	if (inFileName.equals("")) return remoteArchPathFileName;
	//	
	//	remoteArchPathFileName = inRootString
	//						+ inRemoteFileSeparator
	//						+ remoteArhive
	//						+ inRemoteFileSeparator
	//						+ inRelevance
	//						+ inRemoteFileSeparator
	//						+ inFileName;
	//
	//	System.out.println("remotePathFileName...=" + remoteArchPathFileName);
	//
	//	//SlashFix starts
	//	//remoteArchPathFileName = remoteArchPathFileName.replace('\\', '/');
	//	remoteArchPathFileName = remoteArchPathFileName.replace('\\', inRemoteFileSeparator.charAt(0));
	//	remoteArchPathFileName = remoteArchPathFileName.replace('/', inRemoteFileSeparator.charAt(0));		
	//	//SlashFix ends
	//
	//	return remoteArchPathFileName;
	//}

	public String getRemoteArchivalPathFileName(String inRootString, String inRelevance, 
											String inFileName, String inRemoteFileSeparator){
		return getRemoteCoreArchivalPathFileName(inRootString, 
													inRelevance, 
													inFileName, 
													inRemoteFileSeparator, 
													remoteArchive);
	}

	public String getRemoteInactiveArchivalPathFileName(String inRootString, String inRelevance,
											String inFileName, String inRemoteFileSeparator){
		return getRemoteCoreArchivalPathFileName(inRootString, 
													inRelevance, 
													inFileName, 
													inRemoteFileSeparator, 
													remoteInactiveERLsArchive);
	}
	
	public String getRemoteCoreArchivalPathFileName(String inRootString, String inRelevance, 
											String inFileName, String inRemoteFileSeparator, 
											String inRemoteArhiveFolder){

		String remoteArchPathFileName = "" ;

		if (inFileName.equals("")) return remoteArchPathFileName;
		
		remoteArchPathFileName = inRootString
							+ inRemoteFileSeparator
							+ inRemoteArhiveFolder
							+ inRemoteFileSeparator
							+ inRelevance
							+ inRemoteFileSeparator
							+ inFileName;

		System.out.println("remotePathFileName...=" + remoteArchPathFileName);
	
		remoteArchPathFileName = remoteArchPathFileName.replace('\\', inRemoteFileSeparator.charAt(0));
		remoteArchPathFileName = remoteArchPathFileName.replace('/', inRemoteFileSeparator.charAt(0));		

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

		//OutputStream outputStream = null;
		String outPutFileName = tempFolder + localFileSeparator
				+ inFileNameFromURL;
		storeInStream(inRemoteInputStream, outPutFileName);

		return outPutFileName;
	}

	private String prepDupeArchiveName(File sourcelocalFile) {
	// sets up a nondupe archive name prior to actual archive of a source file/folder

		String archiveFileName = localArchive + localFileSeparator + sourcelocalFile.getName() + "_Tm" + getCurrentTimeStamp();

		System.out.println("At Commons prepDupeArchiveName before archiveLocalFolder from " + sourcelocalFile.getName() + " into " + archiveFileName);
		
		if (doesFileExist(archiveFileName)) {
		// in case a file already exists prior to archiving - maybe due to multiple calls in the same run,
		// try to append with a dupe counter to find a unique name
			System.out.println("At Commons prepDupeArchiveName " + archiveFileName + " already exists 1 ");
			int versionNum = 1;
			for (int dupeCount = 0; dupeCount < archiveDupeMax; dupeCount++) {
				String checkName = archiveFileName + "_" + versionNum;
				if (!doesFileExist(checkName)) {
					logger.info("in Commons prepDupeArchiveName found a unique name for archiving " + checkName);
					System.out.println("in Commons prepDupeArchiveName found a unique name for archiving " + checkName);
					archiveFileName = checkName;
					break;
				}
			}
			if (doesFileExist(archiveFileName)) {
				System.out.println("At Commons prepDupeArchiveName " + archiveFileName + " still already exists 2 ");
			// in case a folder/file still exists in spite of appending with dupeCount, no other choice but to delete it.
				deleteFileORFolder(archiveFileName);
				logger.info("archiving " + archiveFileName + " already existed hence deleted prior to the new archival" );
				System.out.println("archiving " + archiveFileName + " already existed hence deleted prior to the new archival");
			}			
		} else {
			System.out.println("At Commons prepDupeArchiveName " + archiveFileName + " never existed");			
		}
		return archiveFileName;
	}

	public void dirtyFileMoveCheck(String inSourceFileName) throws IOException {
		// dirty check to know if a file is moveable or locked up by some process
		File sourcelocalFile = new File(inSourceFileName);		
		String dirtyMoveFileName = localArchive + localFileSeparator + sourcelocalFile.getName() + "_dirtyChecker_" + getCurrentTimeStamp();
		File dirtyMoveFile = new File(dirtyMoveFileName);

		System.out.println("At Commons dirtyFileMoveCheck inSourceFileName " + inSourceFileName);
		System.out.println("At Commons dirtyFileMoveCheck dirtyMoveFileName " + dirtyMoveFileName);

		moveFileViaName(inSourceFileName, dirtyMoveFileName);
		
		System.out.println("At Commons moving back dirtyFileMoveCheck inSourceFileName " + inSourceFileName);
		System.out.println("At Commons moving back dirtyFileMoveCheck dirtyMoveFileName " + dirtyMoveFileName);
		
		moveFileViaName(dirtyMoveFileName, inSourceFileName);

		System.out.println("At Commons dirtyFileMoveCheck done ");
	}

	public void archiveLocalFile(String inLocalFileLocation) throws IOException {

//		System.out.println("At Commons archiveLocalFile inLocalFileLocation before dirty move check " + inLocalFileLocation);		
//		dirtyFileMoveCheck(inLocalFileLocation);
//		System.out.println("At Commons archiveLocalFile inLocalFileLocation after dirty move check " + inLocalFileLocation);
		
		File sourcelocalFile = new File(inLocalFileLocation);
		if (!doesFileExist(inLocalFileLocation)) {
			logger.info("Nothing to archive as the source file " + inLocalFileLocation + " doesn't exist");
			return;
		}		
		//Path sourceFilePath = Paths.get(inLocalFileLocation);
		//boolean sourceFileWritable = Files.isWritable(sourceFilePath);
		//System.out.println("At Commons archiveLocalFile source file writable state is " + sourceFileWritable);
		
		String archiveFileName = prepDupeArchiveName(sourcelocalFile);

		if (doesFileExist(inLocalFileLocation)) {
			logger.info("At Commons archiveLocalFile source file name exists before archival sourcelocalFile " + sourcelocalFile);
		}

		if (doesFileExist(archiveFileName)) {
			logger.info("At Commons archiveLocalFile target file name exists before archival archiveFileName " + archiveFileName);
		}

		moveFileViaName(inLocalFileLocation, archiveFileName);
		logger.info("At Commons after Archiving the file " + inLocalFileLocation + " into " + archiveFileName);
		System.out.println("At Commons after Archiving the file " + inLocalFileLocation + " into " + archiveFileName);
	}
	
	public void archiveLocalFolder(String inLocalFolderLocation) throws IOException {
		File sourcelocalFile = new File(inLocalFolderLocation);
		if (!doesFileExist(inLocalFolderLocation) && !sourcelocalFile.isDirectory()) {
			logger.info("Nothing to archive as the source Folder " + inLocalFolderLocation + " doesn't exist or Not a Folder" );
			return;
		}
		String archiveFolderName = prepDupeArchiveName(sourcelocalFile);
		moveFolderViaName(inLocalFolderLocation, archiveFolderName);
		logger.info("At Commons after Archiving the folder " + inLocalFolderLocation + " into " + archiveFolderName);
		System.out.println("At Commons after archiveLocalFolder from " + inLocalFolderLocation + " into " + archiveFolderName);
	}
	
	public String getRemoteContentDropFileName(String inRootString, String inFileName, String inRemoteFileSeparator, String inRequestor) {
		System.out.println("At getRemoteContentDropFileName ");
		System.out.println("contentdropbox is " + contentdropbox);
		System.out.println("inAuthor is " + inRequestor);
		System.out.println("inFileName is " + inFileName);
		
		String remoteFileString = inRootString
				+ inRemoteFileSeparator + contentdropbox + inRemoteFileSeparator
				+ inRequestor + inRemoteFileSeparator
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

	//Column sorting not implemented
	////////// table sorting starts
	//public void setComparator(TableColumn inTableColumn) {
	//	inTableColumn.setData(new Comparator<TableItem>() {
	//		// public int compare(TableItem t1, TableItem t2) {
	//		// int i1 = Integer.parseInt(t1.getText(0));
	//		// int i2 = Integer.parseInt(t2.getText(0));
	//		// if (i1 < i2) return -1;
	//		// if (i1 > i2) return 1;
	//		// return 0;
	//		// }
	//		// keeping it simple to sort only as text
	//		public int compare(TableItem t1, TableItem t2) {
	//			System.out.println("comparator called t1.getText(1) is " + t1.getText(1));
	//			System.out.println("comparator called t2.getText(1) is " + t2.getText(1));
	//			System.out.println("comparator called t1.getText(1).compareTo(t2.getText(1) is " + t1.getText(1).compareTo(t2.getText(1)));
	//			return t1.getText(1).compareTo(t2.getText(1));
	//		}
	//		//// @Override
	//		// public int compare(TableItem t1, TableItem t2) {
	//		// return
	//		//// Date.parse(t1.getText(2)).compareTo(Date.parse(t2.getText(2)));
	//		// }
	//	});
	//}
	//
	//public Listener setTableSortListener(Table table) {
	//	TableColumn sortColumn = table.getSortColumn();
	//
	//	Listener sortListener = new Listener() {
	//		@Override
	//		public void handleEvent(Event e) {
	//			System.out.println("table sorter called 1");
	//
	//			TableColumn selectedColumn = (TableColumn) e.widget;
	//			int dir = table.getSortDirection();
	//			if (sortColumn == selectedColumn) {
	//				System.out.println("table sorter called 2");
	//				dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
	//			} else {
	//				System.out.println("table sorter called 3");
	//				table.setSortColumn(selectedColumn);
	//				dir = SWT.UP;
	//			}
	//			TableItem[] items = table.getItems();
	//			final Comparator<TableItem> comparator = (Comparator<TableItem>) selectedColumn.getData();
	//			System.out.println("table sorter called 4.  items.length is " + items.length);
	//			for (int i = 1; i < items.length; i++) {
	//				System.out.println("table sorter called 5.  i is " + i);
	//				for (int j = 0; j < i; j++) {
	//					System.out.println("table sorter called 6.  j is " + j);
	//					System.out.println("table sorter called 6.1.  items[j].0 is " + items[j].getText(0));
	//					System.out.println("table sorter called 6.1.  items[j].1 is " + items[j].getText(1));
	//
	//					
	//					if ((comparator.compare(items[i], items[j]) < 0 && dir == SWT.UP)
	//							|| (comparator.compare(items[i], items[j]) > 0 && dir == SWT.DOWN)) {
	//						String[] oldItem = new String[table.getColumnCount()];
	//						System.out.println("table sorter called 7. table.getColumnCount() is " + table.getColumnCount());
	//						for (int h = 0; h < table.getColumnCount(); h++) {
	//							System.out.println("table sorter called 8. h is " + h);
	//							oldItem[h] = items[i].getText(h);
	//						}
	//						//ERROR ERROR ERROR
	//						//HAVEN'T IMPLEMENTED SCREENROWNUMLIT or CURRNTROWNUMBER SWAPPING!!!!
	//						items[i].dispose();
	//						TableItem newItem = new TableItem(table, SWT.NONE, j);
	//						newItem.setText(oldItem);
	//						items = table.getItems();
	//						break;
	//					}
	//				}
	//			}
	//			table.setSortDirection(dir);
	//		}
	//
	//	};
	//	return sortListener;
	//}
	////////// table sorting ends
}