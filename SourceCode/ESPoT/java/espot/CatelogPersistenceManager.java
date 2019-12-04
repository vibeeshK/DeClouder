package espot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CatelogPersistenceManager {
	/*
	 * Maintains connections to the specified db files and SQL interactions
	 */

	protected Connection connection;
	protected Statement statement;

	protected  String sqliteDbStringPrefix = "jdbc:sqlite:";
	protected String currentCatalogFile = null;
	public String tobeConnectedCatalogDbFile = null;
	protected String catalogDBAlias;
	protected  String catalogDBAliasPrefix;

	protected  String sysDBAlias;
	protected  String sysDBAliasPrefix;	
	protected  Commons commons = null;
	protected  int processMode = 0;
	public RootPojo rootPojo = null;

	public CatelogPersistenceManager(RootPojo inRootPojo, Commons inCommons, int inProcessMode) {
		commons = inCommons;
		processMode = inProcessMode;
		rootPojo = inRootPojo;
		System.out.println("CatelogPersistenceManager begins for inMasterOrClient = " + processMode);
		System.out.println("CatelogPersistenceManager begins for rootPojo = " + rootPojo);
		System.out.println("CatelogPersistenceManager begins for rootPojo.rootNick = " + rootPojo.rootNick);

		sysDBAlias = "sysDBAlias";
		sysDBAliasPrefix = sysDBAlias + ".";
		
		if (processMode == Commons.BASE_CATALOG_SERVER) {
			System.out.println("Im here for Master db");
			catalogDBAlias = "";
			catalogDBAliasPrefix = 	"";
		} else if (processMode == Commons.CLIENT_MACHINE || processMode == Commons.EXTENDED_CATALOG_SERVER) {
			System.out.println("Im not here for Master db");
			catalogDBAlias = "catalogDBAlias";
			catalogDBAliasPrefix = 	catalogDBAlias + ".";

			System.out.println("from CatelogPersistenceManager trouble shoot to get the catalog db file for the root " + rootPojo.rootNick);

			tobeConnectedCatalogDbFile = 
			CatalogDownloadDtlsHandler.getInstance(commons).getCatalogDownLoadedFileName(rootPojo.rootNick);
			System.out.println("tobeConnectedCatalogDbFile1 : " + tobeConnectedCatalogDbFile );

		} else {
			commons.logger.error("undefined processMode: " + processMode);
			System.out.println("undefined processMode: " + processMode);
		}

 		createConnectionAndStatment();
	}

	protected void createConnectionAndStatment() {
		try {
			Class.forName("org.sqlite.JDBC");
			String connectionStrng = null;

			System.out
					.println("Im here3333 processMode = " + processMode);
			if (processMode == Commons.BASE_CATALOG_SERVER) {
				System.out.println("Im here for Master db");
				connectionStrng = sqliteDbStringPrefix + commons.getServersMasterCopyofCatalogDbLocalFileOfRoot(rootPojo.rootNick);
			} else if (processMode == Commons.CLIENT_MACHINE || processMode == Commons.EXTENDED_CATALOG_SERVER) {
				System.out.println("Im here for client db");
				connectionStrng = sqliteDbStringPrefix + commons.getClientDbFileLocation();
			}
			System.out.println("connectionStrng = " + connectionStrng);

			connection = DriverManager.getConnection(connectionStrng);

			System.out.println("dbfile location:" + connectionStrng);
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			if (processMode != Commons.BASE_CATALOG_SERVER) {
				System.out.println("getting into !master");
				connectToToBECataloged();
			}
			connectToSysDb();
		} catch (SQLException | ClassNotFoundException e) {
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager createConnectionAndStatment", e);
		}
	}

	public synchronized void refreshForLatestCatalog() {
		tobeConnectedCatalogDbFile = 
			CatalogDownloadDtlsHandler.getInstance(commons).getCatalogDownLoadedFileName(rootPojo.rootNick);
		connectToToBECataloged();
	}

	public synchronized  void connectToSysDb(){
		try {
			System.out.println("At connectToSysDb : commons.sysDbFileLocation: " + commons.sysDbFileLocation);
			
			String queryString = "Attach database '"
						+ commons.sysDbFileLocation
						+ "' As " + sysDBAlias;

			System.out.println(queryString);

			statement.execute(queryString);
				
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager connectToSysDb", e);
		}
		return;		
	}

	public synchronized  void connectToToBECataloged() {
		try {
			System.out.println("tobeConnectedCatalogDbFile: " + tobeConnectedCatalogDbFile);
			System.out.println("currentCatalogFile: " + currentCatalogFile);
			System.out.println("catalogDBAlias123123a: " + catalogDBAlias);
			
			if (currentCatalogFile == null
					|| (currentCatalogFile != null && !currentCatalogFile
							.equalsIgnoreCase(tobeConnectedCatalogDbFile))) {
				System.out.println("@@1");

				if (currentCatalogFile != null
						&& !currentCatalogFile
								.equalsIgnoreCase(tobeConnectedCatalogDbFile)) {
					System.out.println("gonna detach the previous db file");

					detachCatalogFile();
				}
				if (tobeConnectedCatalogDbFile != null) {
					String queryString = "Attach database '"
							+ tobeConnectedCatalogDbFile
							+ "' As " + catalogDBAlias;

					System.out.println(queryString);
					statement.execute(queryString);
				}
				
			}
			System.out.println("for setting the current cat file recentCatalogFile: " + tobeConnectedCatalogDbFile);
			currentCatalogFile = tobeConnectedCatalogDbFile;
			System.out.println("set the currentCatalogFile to : " + currentCatalogFile);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager connectToToBECataloged", e);
		}
		return;
	}
	
	public synchronized void detachCatalogFile() {
		try {
			String queryString = "DETACH DATABASE " + catalogDBAlias;
			System.out.println(queryString);
			statement.execute(queryString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager detachCatalogFile", e);
		}
		return;
	}

	public synchronized  HashMap<String,ContentHandlerSpecs> getContentHandlerSpecsMap() {
		HashMap<String,ContentHandlerSpecs> contentHandlerSpecsMap = new HashMap<String,ContentHandlerSpecs>();

		ContentHandlerSpecs contentHandlerSpecs = null;
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT ContentType, " 
					+ " coalesce(Template,\"\") as Template, "
					+ " coalesce(Extension,\"\") as Extension, "
					+ " coalesce(HasSpecialHandler,\"\") as HasSpecialHandler, "
					+ " coalesce(UserInitiated,\"\") as UserInitiated, "
					+ " coalesce(AutoTriggered,\"\") as AutoTriggered, "
					+ " coalesce(HandlerClass,\"\") as HandlerClass, "
					+ " coalesce(ExtdHandlerCls,\"\") as ExtdHandlerCls, "
					+ " RollupOrAddup, "
					+ " coalesce(RollAddSeparator,\"\") as RollAddSeparator, "
					+ " coalesce(ReplOptRelevance,\"\") as ReplOptRelevance, "
					+ " coalesce(ReplOptArtifact,\"\") as ReplOptArtifact, "
					+ " coalesce(RollupLevel,\"\") as RollupLevel, "
					+ " coalesce(RollAddContentType,\"\") as RollAddContentType,"
					+ " coalesce(AddupRelevance,\"\") as AddupRelevance,"
					+ " coalesce(RollAddArtifactName,\"\") as RollAddArtifactName,"
					+ " coalesce(Instructions,\"\") as Instructions"
					+ " FROM "
					+ sysDBAliasPrefix + "ContentTypes";
			System.out.println("@ getContentHandlerSpecsMap1 : " + queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				contentHandlerSpecs = new ContentHandlerSpecs(commons);
				contentHandlerSpecs.setContentHandlerSpecs(rs.getString("ContentType"), 
						rs.getString("Template"), 
						rs.getString("Extension"), 
						rs.getBoolean("HasSpecialHandler"), 
						rs.getBoolean("UserInitiated"), 
						rs.getBoolean("AutoTriggered"), 
						rs.getString("HandlerClass"),
						rs.getString("ExtdHandlerCls"),			
						rs.getString("RollupOrAddup"),
						rs.getString("RollAddSeparator"),
						rs.getString("ReplOptRelevance"),
						rs.getString("ReplOptArtifact"),
						rs.getInt("RollupLevel"),
						rs.getString("RollAddContentType"),
						rs.getString("AddupRelevance"),
						rs.getString("RollAddArtifactName"),
						rs.getString("Instructions"));
				contentHandlerSpecsMap.put(contentHandlerSpecs.contentType,contentHandlerSpecs);
				contentHandlerSpecs = null;
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getContentHandlerSpecsMap", e);
		}
		return contentHandlerSpecsMap;
	}

	public synchronized String[] getNonrollingContentTypes() {
		// note: Usage replaced by getUserInitiatedContentTypes.
		// Get the content types which are not rolled from other base contents
		// This function is needed mainly while creates a new base draft via UI,
		// where the dropdown shouldn't show rolled up types which would be indirectly created

		ArrayList<String> nonrollingContentTypeList = new ArrayList<String>();
		
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT ContentType FROM "
					+ sysDBAliasPrefix + "ContentTypes CT1"
					+ " where not exists (select 1 from ContentTypes CT2 "
					+ 						" where CT2.RollAddContentType = CT1.ContentType)"
					;
			System.out.println("@ getContentHandlerSpecsMap1 : " + queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				nonrollingContentTypeList.add(rs.getString("ContentType"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getNonRollingContentTypes", e);
		}		
		String[] nonrollingContentTypes = nonrollingContentTypeList.toArray(
													new String[nonrollingContentTypeList.size()]);
		return nonrollingContentTypes;
	}

	public synchronized String[] getUserInitiatedContentTypes() {
		// Get the content types which are meant for users to directly create via UI new draft screen
		
		ArrayList<String> nonrollingContentTypeList = new ArrayList<String>();
		
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT ContentType FROM "
					+ sysDBAliasPrefix + "ContentTypes CT1"
					+ " where UserInitiated = 1 "
					+ " order by ContentType "					
					;
			System.out.println("@ getContentHandlerSpecsMap1 : " + queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				nonrollingContentTypeList.add(rs.getString("ContentType"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getNonRollingContentTypes", e);
		}		
		String[] nonrollingContentTypes = nonrollingContentTypeList.toArray(
													new String[nonrollingContentTypeList.size()]);
		return nonrollingContentTypes;
	}

	public synchronized  ContentHandlerSpecs getContentHandlerSpecs(String inContentType) {

		ContentHandlerSpecs contentHandlerSpecs = null;
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT ContentType, " 
					+ " coalesce(Template,\"\") as Template, "
					+ " coalesce(Extension,\"\") as Extension, "
					+ " coalesce(HasSpecialHandler,\"\") as HasSpecialHandler, "
					+ " coalesce(UserInitiated,\"\") as UserInitiated, "
					+ " coalesce(AutoTriggered,\"\") as AutoTriggered, "
					+ " coalesce(HandlerClass,\"\") as HandlerClass, "
					+ " coalesce(ExtdHandlerCls,\"\") as ExtdHandlerCls, "
					+ " RollupOrAddup, "
					+ " coalesce(RollAddSeparator,\"\") as RollAddSeparator, "
					+ " coalesce(ReplOptRelevance,\"\") as ReplOptRelevance, "
					+ " coalesce(ReplOptArtifact,\"\") as ReplOptArtifact, "
					+ " coalesce(RollupLevel,\"\") as RollupLevel, "
					+ " coalesce(RollAddContentType,\"\") as RollAddContentType,"
					+ " coalesce(AddupRelevance,\"\") as AddupRelevance,"
					+ " coalesce(RollupArtifactName,\"\") as RollupArtifactName,"
					+ " coalesce(Instructions,\"\") as Instructions"
					+ sysDBAliasPrefix + "ContentTypes "
					+ " where ContentType = '" + inContentType + "'";
			System.out.println("@ getContentHandlerSpecsMap2 : " + queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				contentHandlerSpecs = new ContentHandlerSpecs(commons);
				contentHandlerSpecs.setContentHandlerSpecs(rs.getString("ContentType"), 
						rs.getString("Template"), 
						rs.getString("Extension"), 
						rs.getBoolean("HasSpecialHandler"), 
						rs.getBoolean("UserInitiated"), 
						rs.getBoolean("AutoTriggered"), 
						rs.getString("HandlerClass"),
						rs.getString("ExtdHandlerCls"),			
						rs.getString("RollupOrAddup"),
						rs.getString("RollAddSeparator"),
						rs.getString("ReplOptRelevance"),
						rs.getString("ReplOptArtifact"),
						rs.getInt("RollupLevel"),
						rs.getString("RollAddContentType"),
						rs.getString("AddupRelevance"),
						rs.getString("RollAddArtifactName"),
						rs.getString("Instructions"));				
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getContentHandlerSpecs inContentType " + inContentType, e);
		}
		return contentHandlerSpecs;
	}

	public synchronized ERLpojo readERL(ArtifactKeyPojo inArtifactKeyPojo) {

		ERLpojo dbERLpojo = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
					+ "erl.RootNick, "
					+ "erl.Relevance, "
					+ "erl.ArtifactName, "
					+ "erl.ContentType, "
					+ "erl.Requestor, "
					+ "coalesce(Author,\"\") as Author,"
					+ "coalesce(ContentFileName,\"\") as ContentFileName, "
					+ "coalesce(ReviewFileName,\"\") as ReviewFileName, "
					+ "erl.ERLStatus, "
					+ "coalesce(UploadedTimeStamp,\"\") as UploadedTimeStamp, "
					+ "coalesce(ReviewTimeStamp,\"\") as ReviewTimeStamp, "
					+ "coalesce(ct.HasSpecialHandler,\"\") as HasSpecialHandler, "
					+ "coalesce(ct.AutoTriggered,\"\") as AutoTriggered "
					+ "from "
					+ catalogDBAliasPrefix + "ERLMaster erl, "
					+ sysDBAliasPrefix + "ContentTypes ct "
					+ " where RootNick = '"
					+ inArtifactKeyPojo.rootNick
					+ "' and Relevance = '"
					+ inArtifactKeyPojo.relevance
					+ "' and erl.ArtifactName = '"
					+ inArtifactKeyPojo.artifactName
					+ "' and erl.ContentType = '"
					+ inArtifactKeyPojo.contentType
					+ "' "
					+ " and ct.ContentType = erl.ContentType "
					;

			System.out.println("@ readERL : " + queryString);

			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				dbERLpojo = new ERLpojo(inArtifactKeyPojo,
						rs.getString("Requestor"), rs.getString("Author"), rs.getBoolean("HasSpecialHandler"),
						rs.getString("ReviewFileName"), rs.getString("ERLStatus"),rs.getString("ContentFileName"),
						rs.getString("UploadedTimeStamp"), rs.getString("ReviewTimeStamp")
						);
				System.out.println("ReviewFileName read from db = " + rs.getString("ReviewFileName"));
				System.out.println("UploadedTimeStamp read from db = " + rs.getString("UploadedTimeStamp"));
				
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readERL " + inArtifactKeyPojo.artifactName, e);
		}
		return dbERLpojo;
	}

	public synchronized  ArrayList<ERLDownload> getSubscribedERLpojoList() {
		ArrayList<ERLDownload> erlpojoList = readERLDownLoadsOfRoot();
		ArrayList<ERLDownload> subscribedERLpojoList = new ArrayList<ERLDownload>();
		for (ERLDownload erlDownload : erlpojoList){
			if (erlDownload.subscriptionStatus
					.equalsIgnoreCase(ERLDownload.CURRENTLY_SUBSCRIBED)) {
				subscribedERLpojoList.add(erlDownload);
			}			
		}
		return subscribedERLpojoList;
	}


	public synchronized  ArrayList<ERLDownload> readERLDownLoadsForAuthorOnContentType(String inUserName, String inContentType) {
		String contentTypeString = " and erl.ContentType = '" + inContentType + "' and upper(erl.Author) = upper('" + inUserName + "') ";
		ArrayList<ERLDownload> erlDownLoads = readERLDownLoadsOfRootWithConstraint(contentTypeString);
		return erlDownLoads;
	}

	public synchronized  ArrayList<ERLDownload> readNewAutoTriggerERLDownLoadsForAuthor(String inUserName) {
				
		System.out.println("At readNewAutoTriggerERLDownLoadsForAuthor tobeConnectedCatalogDbFile is " + tobeConnectedCatalogDbFile);
		
		String contentTypeString = " and ct.AutoTriggered and upper(erl.author) = upper('" + inUserName + "') ";
		ArrayList<ERLDownload> autoTriggerERLDownLoads = readERLDownLoadsOfRootWithConstraint(contentTypeString);
		ArrayList<ERLDownload> newAutoTriggerERLDownLoads = new ArrayList<ERLDownload>();
		for (ERLDownload erlDownload : autoTriggerERLDownLoads){
			AutoTriggerPojo autoTriggerPojo = readAutoTrigger(erlDownload.artifactKeyPojo);
			if (autoTriggerPojo == null 
				|| erlDownload.uploadedTimeStamp.compareTo(autoTriggerPojo.erlORRwUploadedTimeStamp)>0
				|| erlDownload.reviewTimeStamp.compareTo(autoTriggerPojo.erlORRwUploadedTimeStamp)>0) {
			// including the review timestamp as well since the status changes dont impact uploadts
				newAutoTriggerERLDownLoads.add(erlDownload);
			}
		}
		return newAutoTriggerERLDownLoads;
	}

	
	public synchronized  ArrayList<ERLDownload> readERLDownLoadsOfRoot() {
		String contentTypeString = "";
		return readERLDownLoadsOfRootWithConstraint(contentTypeString);
	}

	public synchronized  ArrayList<ERLDownload> readERLDownLoadsOfRootsSpecificContentTypeRelevanceArtifactName(String inContentType, String inRelevance, String inArtifactName) {
		System.out.println("at readERLDownLoadsOfRootsSpecificContentTypeRelevanceArtifactName start " +  inContentType + " " + inRelevance + " " + inArtifactName);
		String contentTypeConstraint = " and erl.ContentType = '" + inContentType + "' " 
										+ " and erl.Relevance = '" + inRelevance + "' " 
										+ " and erl.ArtifactName = '" + inArtifactName + "' " ;
		return readERLDownLoadsOfRootWithConstraint(contentTypeConstraint);
	}
	
	public synchronized  ArrayList<ERLDownload> readERLDownLoadsOfRootsSpecificContentTypeAndRelevance(String inContentType, String inRelevance) {
		System.out.println("at readERLDownLoadsOfRootsSpecificContentTypeAndRelevance start " +  inContentType + " " + inRelevance);
		String contentTypeConstraint = " and erl.ContentType = '" + inContentType + "' " 
										+ " and erl.Relevance = '" + inRelevance + "' " ;
		return readERLDownLoadsOfRootWithConstraint(contentTypeConstraint);
	}
	
	public synchronized  ArrayList<ERLDownload> readERLDownLoadsOfRootsSpecificContentType(String inContentType) {
		System.out.println("at readERLDownLoadsOfRootsSpecificContentType start " +  inContentType);
		String contentTypeConstraint = " and erl.ContentType = '" + inContentType + "' " ;
		return readERLDownLoadsOfRootWithConstraint(contentTypeConstraint);
	}

	public synchronized  ArrayList<ERLDownload> readERLDownLoadsOfAssignedContent(String inUserName) {
		System.out.println("at readERLDownLoadsOfAssignedContent start ");
		String contentTypeConstraint = " and upper(erl.Author) = upper('" + inUserName + "') " + 
										" and (erl.ERLStatus = '" + ArtifactPojo.ERLSTAT_DRAFTREQ + "' " +
										" or erl.ERLStatus = '' or upper(erl.ERLStatus) = upper('null'))" ;
		return readERLDownLoadsOfRootWithConstraint(contentTypeConstraint);
	}

	public synchronized  ArrayList<ERLDownload> readERLDownLoadsOfRootWithConstraint(String inConstraintString) {
		System.out.println("at readERLDownLoadsOfRootWithConstraint start " +  inConstraintString);

		ArrayList<ERLDownload> dbERLDownloadsList = new ArrayList<ERLDownload>();
		ERLDownload dbERLDownload = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
			+ " erl.RootNick, "
			+ " erl.Relevance, "
			+ " erl.ArtifactName, "
			+ " erl.ContentType, "
			+ " erl.Requestor, "
			+ " coalesce(Author,\"\") as Author, "
			+ " coalesce(ContentFileName,\"\") as ContentFileName, "
			+ " coalesce(ReviewFileName,\"\") as ReviewFileName, "
			+ " erl.ERLStatus, "
			+ " coalesce(UploadedTimeStamp,\"\") as UploadedTimeStamp, "
			+ " coalesce(ReviewTimeStamp,\"\") as ReviewTimeStamp, "
			+ " coalesce(ct.HasSpecialHandler,\"\") as HasSpecialHandler, "
			+ " coalesce(ct.AutoTriggered,\"\") as AutoTriggered, "
			+ " coalesce(sub.SubscriptionStatus,\"\") as SubscriptionStatus, "
			+ " coalesce(sub.DownLoadedFile,\"\") as DownLoadedFile, "
			+ " coalesce(sub.DownLoadedReviewFile,\"\") as DownLoadedReviewFile, "
			+ " coalesce(DownLoadedArtifactTimeStamp,\"\") as DownLoadedArtifactTimeStamp, "
			+ " coalesce(DownLoadedReviewTimeStamp,\"\") as DownLoadedReviewTimeStamp, "
			+ " case when pr.Relevance is not null then 1 "
			+ "  	 else 0 end as RelevancePicked "
			+ " from  "
			+ catalogDBAliasPrefix + "ERLMaster erl, "
			+ sysDBAliasPrefix + "ContentTypes ct "
			+ " left outer join "
			+ " Subscriptions sub "
			+ " on sub.RootNick = erl.RootNick "
			+ " and sub.Relevance = erl.Relevance "
			+ " and sub.ArtifactName = erl.ArtifactName "
			+ " and sub.ContentType = erl.ContentType "
			+ " left outer join PickedRelevance pr "
			+ " on pr.RootNick = erl.RootNick "
			+ " and pr.Relevance = erl.Relevance "
			+ " where ct.ContentType = erl.ContentType "
			+ inConstraintString
			;
			System.out.println(queryString);
			
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				System.out.println("rs next ArtifactName " + rs.getString("ArtifactName"));
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("Relevance"), 
						rs.getString("ArtifactName"), rs.getString("ContentType"));

				dbERLDownload = new ERLDownload(
						tempArtifactKeyPojo,
						rs.getString("Requestor"), 
						rs.getString("Author"),
						rs.getBoolean("HasSpecialHandler"),
						rs.getBoolean("AutoTriggered"),
						rs.getString("ReviewFileName"), rs.getString("ERLStatus"), 
						rs.getString("ContentFileName"),
						rs.getString("UploadedTimeStamp"), rs.getString("ReviewTimeStamp"),
						rs.getString("SubscriptionStatus"),		
						rs.getString("DownLoadedFile"),
						rs.getString("DownLoadedReviewFile"),
						rs.getString("DownLoadedArtifactTimeStamp"),
						rs.getString("DownLoadedReviewTimeStamp"),
						rs.getBoolean("RelevancePicked")
				);
				System.out.println("rs next dbERLDownload.ArtifactName " + dbERLDownload.artifactKeyPojo.artifactName);

				dbERLDownloadsList.add(dbERLDownload);
				dbERLDownload = null;
			}
			System.out.println("rs done ");
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readERLDownLoadsOfRootWithConstraint inConstraintString " + inConstraintString, e);			
		}
		System.out.println("rs returning");
		return dbERLDownloadsList;
	}

	public synchronized ERLDownload readERLDownLoad(ArtifactKeyPojo inArtifactKeyPojo)
	{
		System.out.println("starting readERLDownLoad");
		ERLDownload dbERLDownload = null;
		System.out.println("caught1 him artifactname " + inArtifactKeyPojo.artifactName);

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			System.out.println("caught2 him artifactname " + inArtifactKeyPojo.artifactName);
			
			
			String queryString = "SELECT " 
			+ " erl.RootNick, "
			+ " erl.Relevance, "
			+ " erl.ArtifactName, "
			+ " erl.ContentType, "
			+ " Requestor, "
			+ " coalesce(Author,\"\") as Author,"
			+ " coalesce(ContentFileName,\"\") as ContentFileName, "
			+ " coalesce(ReviewFileName,\"\") as ReviewFileName, "
			+ " erl.ERLStatus, "		
			+ " coalesce(UploadedTimeStamp,\"\") as UploadedTimeStamp, "
			+ " coalesce(ReviewTimeStamp,\"\") as ReviewTimeStamp, "
			+ " coalesce(ct.HasSpecialHandler,\"\") as HasSpecialHandler, "
			+ " coalesce(ct.AutoTriggered,\"\") as AutoTriggered, "
			+ " coalesce(sub.SubscriptionStatus,\"\") as SubscriptionStatus, "
			+ " coalesce(sub.DownLoadedFile,\"\") as DownLoadedFile, "
			+ " coalesce(sub.DownLoadedReviewFile,\"\") as DownLoadedReviewFile, "
			+ " coalesce(DownLoadedArtifactTimeStamp,\"\") as DownLoadedArtifactTimeStamp, "
			+ " coalesce(DownLoadedReviewTimeStamp,\"\") as DownLoadedReviewTimeStamp, "
			+ " case when pr.Relevance is not null then 1 "
			+ "  	 else 0 end as RelevancePicked "
			+ " from "
			+ catalogDBAliasPrefix + "ERLMaster erl, "
			+ sysDBAliasPrefix + "ContentTypes ct "
			+ " left outer join Subscriptions sub "
			+ " on sub.RootNick = erl.RootNick "
			+ " and sub.Relevance = erl.Relevance "
			+ " and sub.ArtifactName = erl.ArtifactName "
			+ " and sub.ContentType = erl.ContentType "
			+ " left outer join PickedRelevance pr "
			+ " on pr.RootNick = erl.RootNick "
			+ " and pr.Relevance = erl.Relevance "
			+ " where ct.ContentType = erl.ContentType "
			+ " and erl.RootNick = '" + inArtifactKeyPojo.rootNick +"' "
			+ " and erl.Relevance = '" + inArtifactKeyPojo.relevance +"' "
			+ " and erl.ArtifactName = '" + inArtifactKeyPojo.artifactName +"' "
			+ " and erl.ContentType = '" + inArtifactKeyPojo.contentType +"' "
			;
			System.out.println(queryString);
			
			System.out.println("caught3a him artifactname " + inArtifactKeyPojo.artifactName);
			ResultSet rs = statement.executeQuery(queryString);
			System.out.println("caught3b him artifactname " + inArtifactKeyPojo.artifactName);
			while (rs.next()) {
				System.out.println("caught4a him artifactname " + inArtifactKeyPojo.artifactName);
				System.out.println(" artifactname from AutoTriggered artifactName "  +inArtifactKeyPojo.artifactName);
				System.out.println(" AutoTriggered from database "  +rs.getBoolean("AutoTriggered"));
				System.out.println(" here we go..... DownLoadedReviewFile from db is "  + rs.getString("DownLoadedReviewFile"));
				dbERLDownload = new ERLDownload(
						inArtifactKeyPojo,
						rs.getString("Requestor"), 
						rs.getString("Author"),
						rs.getBoolean("HasSpecialHandler"),
						rs.getBoolean("AutoTriggered"),
						rs.getString("ReviewFileName"), rs.getString("ERLStatus"),
						rs.getString("ContentFileName"),
						rs.getString("UploadedTimeStamp"), rs.getString("ReviewTimeStamp"),
						rs.getString("SubscriptionStatus"),		
						rs.getString("DownLoadedFile"),
						rs.getString("DownLoadedReviewFile"),
						rs.getString("DownLoadedArtifactTimeStamp"),
						rs.getString("DownLoadedReviewTimeStamp"),
						rs.getBoolean("RelevancePicked")
				);
				System.out.println("caught4b him artifactname " + inArtifactKeyPojo.artifactName);
			}
			System.out.println("caught5 him artifactname " + inArtifactKeyPojo.artifactName);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readERLDownLoad ArtifactKeyPojo " + inArtifactKeyPojo.artifactName, e);
		}
		return dbERLDownload;
	}
	
	public synchronized  ArrayList<ERLDownload> readERLForRemarksDownload(String inRootNick) {
		System.out.println("starting readERLForRemarksDownloadaaaaaaa");

		ArrayList<ERLDownload> dbERLDownloadsList = new ArrayList<ERLDownload>();
		ERLDownload dbERLDownload = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
			+ " erl.RootNick, "
			+ " erl.Relevance, "
			+ " erl.ArtifactName, "
			+ " erl.ContentType, "
			+ " Requestor, "
			+ " coalesce(Author,\"\") as Author,"
			+ " coalesce(ContentFileName,\"\") as ContentFileName, "
			+ " coalesce(ReviewFileName,\"\") as ReviewFileName, "
			+ " erl.ERLStatus, "
			+ " coalesce(UploadedTimeStamp,\"\") as UploadedTimeStamp, "
			+ " coalesce(ReviewTimeStamp,\"\") as ReviewTimeStamp, "
			+ " coalesce(ct.HasSpecialHandler,\"\") as HasSpecialHandler, "
			+ " coalesce(ct.AutoTriggered,\"\") as AutoTriggered, "
			+ " coalesce(Sub.SubscriptionStatus,\"\") as SubscriptionStatus, "
			+ " coalesce(sub.DownLoadedFile,\"\") as DownLoadedFile, "
			+ " coalesce(sub.DownLoadedReviewFile,\"\") as DownLoadedReviewFile, "
			+ " coalesce(DownLoadedArtifactTimeStamp,\"\") as DownLoadedArtifactTimeStamp, "
			+ " coalesce(DownLoadedReviewTimeStamp,\"\") as DownLoadedReviewTimeStamp, "
			+ " case when pr.Relevance is not null then 1 "
			+ "  	 else 0 end as RelevancePicked "
			+ " from  "
			+ catalogDBAliasPrefix + "ERLMaster erl, "
			+ sysDBAliasPrefix + "ContentTypes ct "
			+ " left outer join Subscriptions sub "
			+ " on sub.RootNick = erl.RootNick "
			+ " and sub.Relevance = erl.Relevance "
			+ " and sub.ArtifactName = erl.ArtifactName "
			+ " and sub.ContentType = erl.ContentType "
			//Blocking the outdated erl remarks is not going to help in outer join as it will still pull a blank record
			//+ " and (sub.DownLoadedReviewTimeStamp is null OR sub.DownLoadedReviewTimeStamp = \"\""
			//+ " OR (sub.DownLoadedReviewTimeStamp is not null and erl.ReviewTimeStamp > sub.DownLoadedReviewTimeStamp)) "
			+ " left outer join PickedRelevance pr "
			+ " on pr.RootNick = erl.RootNick "
			+ " and pr.Relevance = erl.Relevance "
			+ " where ct.ContentType = erl.ContentType "
			+ " and erl.RootNick = '" + inRootNick
			+ "' and erl.ReviewTimeStamp is not null "
			//+ " and date(erl.ReviewTimeStamp) > 0 "
			+ " and erl.ReviewTimeStamp <> \"\"";
			;
			System.out.println(queryString);
			
			ResultSet rs = statement.executeQuery(queryString);
			System.out.println("post mistery query readERLForRemarksDownloadaaaaaaa");
			while (rs.next()) {
				System.out.println("inside while post mistery query readERLForRemarksDownloadaaaaaaa");
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo ( 
						rs.getString("RootNick"), rs.getString("Relevance"), rs.getString("ArtifactName"),  
								rs.getString("ContentType"));
				dbERLDownload = new ERLDownload(
						tempArtifactKeyPojo,
						rs.getString("Requestor"), 
						rs.getString("Author"),
						rs.getBoolean("HasSpecialHandler"),
						rs.getBoolean("AutoTriggered"),
						rs.getString("ReviewFileName"), rs.getString("ERLStatus"),
						rs.getString("ContentFileName"),
						rs.getString("UploadedTimeStamp"), rs.getString("ReviewTimeStamp"),
						rs.getString("SubscriptionStatus"),		
						rs.getString("DownLoadedFile"),
						rs.getString("DownLoadedReviewFile"),
						rs.getString("DownLoadedArtifactTimeStamp"),
						rs.getString("DownLoadedReviewTimeStamp"),
						rs.getBoolean("RelevancePicked")
				);
				System.out.println("readERLForRemarksDownload :: dbERLDownload.subscriptionStatus = " + dbERLDownload.subscriptionStatus);
				if (!dbERLDownload.subscriptionStatus.equalsIgnoreCase("")) {
					System.out.println("readERLForRemarksDownload :: going to add " + dbERLDownload);
					dbERLDownloadsList.add(dbERLDownload);
				}
				dbERLDownload = null;
			}
			System.out.println("finished while post mistery query readERLForRemarksDownloadaaaaaaa");
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readERLForRemarksDownload " + inRootNick, e);
		}
		System.out.println("finished while post mistery query readERLForRemarksDownloadaaaaaaa dbERLDownloadsList size is " + dbERLDownloadsList.size());
		return dbERLDownloadsList;
	}

	public synchronized  ArrayList<ERLDownload> readRelevantERLDownLoads() {
		ArrayList<ERLDownload> relevantERLDownLoads = new ArrayList<ERLDownload>();
		ArrayList<ERLDownload> tempERLDownLoads = readERLDownLoadsOfRoot();
		for (int erlCount = 0; erlCount<=tempERLDownLoads.size();erlCount++){
			if (tempERLDownLoads.get(erlCount).relevancePicked){
				relevantERLDownLoads.add(tempERLDownLoads.get(erlCount));
			}
		}
		return relevantERLDownLoads;
	}

	public synchronized  ArrayList<SelfAuthoredArtifactpojo> readArtfictsWithGivenStatusForOneRoot(
			String inRootNick, String inStatusToRead) {

		ArrayList<SelfAuthoredArtifactpojo> selfAuthoredArtifactsOnStatusList = new ArrayList<SelfAuthoredArtifactpojo>();
		SelfAuthoredArtifactpojo selfAuthoredArtifactspojo = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}

			String queryString = "SELECT a.RootNick, a.Relevance, a.ArtifactName, a.ContentType,"
					+ " case when erl.Requestor is not null then erl.Requestor "
					+ "  	 else coalesce(a.Requestor,\"\") end as Requestor, "
					+ " case when erl.Author is not null then erl.Author "
					+ "  	 else coalesce(a.Author,\"\") end as Author, "
					+ " coalesce(a.HasSpecialHandler,\"\") as HasSpecialHandler,"
					+ " coalesce(a.ReviewFileName,\"\") as ReviewFileName,"
					+ " case when erl.ERLStatus is not null then erl.ERLStatus "
					+ "  	 else coalesce(a.ERLStatus,\"\") end as ERLStatus, "
					+ " a.LocalFileName,"
					+ " a.DraftingState,"
					+ " coalesce(a.ReqRespFileName,\"\") as ReqRespFileName,"
					+ " a.UnpulishedVerNum, coalesce(a.DelegatedTo,\"\") as DelegatedTo,"
					+ " coalesce(a.ParentRelevance,\"\") as ParentRelevance, "
					+ " coalesce(a.ParentArtifactName,\"\") as ParentArtifactName, "
					+ " coalesce(a.ParentContentType,\"\") as ParentContentType, "
					+ " coalesce(erl.UploadedTimeStamp,\"\") as UploadedTimeStamp "
					+ " from SelfAuthoredArtifacts a "
					+ " left outer join "
					+ catalogDBAliasPrefix + "ERLMaster erl "
					+ " on erl.RootNick = a.RootNick "
					+ " and erl.Relevance = a.Relevance "
					+ " and erl.ArtifactName = a.ArtifactName "
					+ " and erl.ContentType = a.ContentType "
					+ " where a.RootNick = '" + inRootNick + "' " 
					+ " and a.DraftingState = '" + inStatusToRead + "' " 
					+ " and a.UnpulishedVerNum = (select coalesce(max(UnpulishedVerNum),-1) from SelfAuthoredArtifacts c "
					+ " where c.RootNick = a.RootNick "
					+ " and c.Relevance = a.Relevance "
					+ " and c.ArtifactName = a.ArtifactName "
					+ " and c.ContentType = a.ContentType)"
					;

			System.out.println("QUERY for readArtfictsWithGivenStatusForOneRoot1 = "
					+ queryString);

			System.out.println("before query 1111.2");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {
				
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo ( 
						rs.getString("RootNick"), rs.getString("Relevance"), rs.getString("ArtifactName"),  
								rs.getString("ContentType"));
				ArtifactKeyPojo tempParentArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("ParentRelevance"), rs.getString("ParentArtifactName"),  
								rs.getString("ParentContentType"));
				
				selfAuthoredArtifactspojo = new SelfAuthoredArtifactpojo(
						tempArtifactKeyPojo,
						rs.getString("Requestor"),
						rs.getString("Author"),
						rs.getBoolean("HasSpecialHandler"),
						rs.getString("ReviewFileName"),
						rs.getString("ERLStatus"),
						tempParentArtifactKeyPojo,
						rs.getString("LocalFileName"),
						rs.getString("DraftingState"),
						rs.getString("ReqRespFileName"),
						rs.getInt("UnpulishedVerNum"),
						rs.getString("DelegatedTo"));

				System.out.println("after set SAA pojo");
				selfAuthoredArtifactsOnStatusList.add(selfAuthoredArtifactspojo);
				System.out.println("after add");
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readArtfictsWithGivenStatusForOneRoot "+ inRootNick + " " + inStatusToRead, e);
		}
		return selfAuthoredArtifactsOnStatusList;
	}

	public synchronized  ArrayList<SelfAuthoredArtifactpojo> readDraftsForAuthorOnContentType(String inRootNick, String inAuthor, String inContentType) {
		String contentTypeConstraint = " and upper(a.Author) = upper('" + inAuthor + "') " + 
										" and a.ContentType = '" + inContentType + "' " + 
										" and a.ContentType != '" + SelfAuthoredArtifactpojo.ArtifactStatusProcessed + "' " ;
		return readDrafts4RootWithConstraints(inRootNick,contentTypeConstraint);
	}

	public synchronized  ArrayList<SelfAuthoredArtifactpojo> readInProgressArtfictsForOneRoot(String inRootNick) {
		return readDrafts4RootWithConstraints(inRootNick,"");
	}

//	Reading the inprogress artifacts to display
	public synchronized  ArrayList<SelfAuthoredArtifactpojo> readDrafts4RootWithConstraints(String inRootNick, String inConstraintString) {

		ArrayList<SelfAuthoredArtifactpojo> selfAuthoredArtifactsOnStatusList = new ArrayList<SelfAuthoredArtifactpojo>();
		SelfAuthoredArtifactpojo selfAuthoredArtifactspojo = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}

			String queryString = "SELECT a.RootNick, a.Relevance, a.ArtifactName, a.ContentType,"
					+ " case when erl.Requestor is not null then erl.Requestor "
					+ "  	 else coalesce(a.Requestor,\"\") end as Requestor, "
					+ " case when erl.Author is not null then erl.Author "
					+ "  	 else coalesce(a.Author,\"\") end as Author, "
					+ " coalesce(a.HasSpecialHandler,\"\") as HasSpecialHandler,"
					+ " coalesce(a.ReviewFileName,\"\") as ReviewFileName,"
					+ " case when erl.ERLStatus is not null then erl.ERLStatus "
					+ "  	 else coalesce(a.ERLStatus,\"\") end as ERLStatus, "
					+ " a.LocalFileName," 
					+ " a.DraftingState,"
					+ " coalesce(a.ReqRespFileName,\"\") as ReqRespFileName,"
					+ " a.UnpulishedVerNum, coalesce(a.DelegatedTo,\"\") as DelegatedTo,"
					+ " coalesce(a.ParentRelevance,\"\") as ParentRelevance, "
					+ " coalesce(a.ParentArtifactName,\"\") as ParentArtifactName,"
					+ " coalesce(a.ParentContentType,\"\") as ParentContentType"
					+ " from SelfAuthoredArtifacts a "
					+ " left outer join "
					+ catalogDBAliasPrefix + "ERLMaster erl "
					+ " on erl.RootNick = a.RootNick "
					+ " and erl.Relevance = a.Relevance "
					+ " and erl.ArtifactName = a.ArtifactName "
					+ " and erl.ContentType = a.ContentType "
					+ " where a.RootNick = '" + inRootNick
					+ "' "
					+ " and a.UnpulishedVerNum = (select coalesce(max(UnpulishedVerNum),-1) from SelfAuthoredArtifacts c "
					+ " where c.RootNick = '" + inRootNick + "'"
					+ " and c.Relevance = a.Relevance "
					+ " and c.ArtifactName = a.ArtifactName "
					+ " and c.ContentType = a.ContentType)"
					+ inConstraintString
					;

			System.out.println("QUERY for readArtfictsWithGivenStatusForOneRoot2 = "
					+ queryString);

			System.out.println("before query 1111a.2");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {
				
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("Relevance"), rs.getString("ArtifactName"),  
								rs.getString("ContentType"));
				//Parant artifact's Root cannot be different from the child's.
				ArtifactKeyPojo tempParentArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("ParentRelevance"), rs.getString("ParentArtifactName"),  
								rs.getString("ParentContentType"));
				
				selfAuthoredArtifactspojo = new SelfAuthoredArtifactpojo(
						tempArtifactKeyPojo,
						rs.getString("Requestor"),
						rs.getString("Author"),						
						rs.getBoolean("HasSpecialHandler"),
						rs.getString("ReviewFileName"),
						rs.getString("ERLStatus"),
						tempParentArtifactKeyPojo,
						rs.getString("LocalFileName"),
						rs.getString("DraftingState"),
						rs.getString("ReqRespFileName"),
						rs.getInt("UnpulishedVerNum"),
						rs.getString("DelegatedTo"));

				System.out.println("after set SAAa pojo");
				selfAuthoredArtifactsOnStatusList.add(selfAuthoredArtifactspojo);
				System.out.println("after adda");
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readDrafts4RootWithConstraints " + inRootNick + " inConstraintString " + inConstraintString, e);
		}
		return selfAuthoredArtifactsOnStatusList;
	}
	
	public synchronized  SelfAuthoredArtifactpojo readSelfAuthoredArtifact(
			ArtifactKeyPojo inArtifactKeyPojo) {
		SelfAuthoredArtifactpojo selfAuthoredArtifactspojo = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT a.RootNick, a.Relevance, a.ArtifactName, a.ContentType,"
				+ " case when erl.Requestor is not null then erl.Requestor "
				+ "  	 else coalesce(a.Requestor,\"\") end as Requestor, "
				+ " case when erl.Author is not null then erl.Author "
				+ "  	 else coalesce(a.Author,\"\") end as Author, "
				+ " coalesce(a.HasSpecialHandler,\"\") as HasSpecialHandler,"
				+ " coalesce(a.ReviewFileName,\"\") as ReviewFileName,"
				+ " case when erl.ERLStatus is not null then erl.ERLStatus "
				+ "  	 else coalesce(a.ERLStatus,\"\") end as ERLStatus, "
				+ " a.LocalFileName," + " a.DraftingState,"
				+ " coalesce(a.ReqRespFileName,\"\") as ReqRespFileName,"
				+ " a.UnpulishedVerNum, coalesce(a.DelegatedTo,\"\") as DelegatedTo, "
				+ " coalesce(a.ParentRelevance,\"\") as ParentRelevance, "
				+ " coalesce(a.ParentArtifactName,\"\") as ParentArtifactName,"
				+ " coalesce(a.ParentContentType,\"\") as ParentContentType"
				+ " from SelfAuthoredArtifacts a "
				+ " left outer join "
				+ catalogDBAliasPrefix + "ERLMaster erl "
				+ " on erl.RootNick = a.RootNick "
				+ " and erl.Relevance = a.Relevance "
				+ " and erl.ArtifactName = a.ArtifactName "
				+ " and erl.ContentType = a.ContentType "
				+ " where a.RootNick = '" + inArtifactKeyPojo.rootNick + "' "
				+ " and a.Relevance = '" + inArtifactKeyPojo.relevance + "' "
				+ " and a.ArtifactName = '" + inArtifactKeyPojo.artifactName + "' "
				+ " and a.ContentType = '" + inArtifactKeyPojo.contentType + "' "
				+ " and a.UnpulishedVerNum = (select coalesce(max(UnpulishedVerNum),-1) from SelfAuthoredArtifacts c "
				+ " where c.RootNick = '" + inArtifactKeyPojo.rootNick + "' "
				+ " and c.Relevance = '" + inArtifactKeyPojo.relevance + "' "
				+ " and c.ArtifactName = '" + inArtifactKeyPojo.artifactName + "' "
				+ " and c.ContentType = '" + inArtifactKeyPojo.contentType + "') "
				;

			System.out.println("QUERY for readSelfAuthoredArtifactWithRootPojo = "
					+ queryString);

			System.out.println("before query 1111.2");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {

				//Parant artifact's Root cannot be different from the child's.
				ArtifactKeyPojo tempParentArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("ParentRelevance"), rs.getString("ParentArtifactName"),  
								rs.getString("ParentContentType"));
				selfAuthoredArtifactspojo = new SelfAuthoredArtifactpojo(
						inArtifactKeyPojo,
						rs.getString("Requestor"),
						rs.getString("Author"),
						rs.getBoolean("HasSpecialHandler"),
						rs.getString("ReviewFileName"),
						rs.getString("ERLStatus"),
						tempParentArtifactKeyPojo,
						rs.getString("LocalFileName"),
						rs.getString("DraftingState"),
						rs.getString("ReqRespFileName"),
						rs.getInt("UnpulishedVerNum"),
						rs.getString("DelegatedTo"));
				System.out.println("after set SAA pojo");
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readSelfAuthoredArtifact " + inArtifactKeyPojo.artifactName, e);
		}
		System.out.println("after reading one readSelfAuthoredArtifactWithRootPojo");

		return selfAuthoredArtifactspojo;
	}
	
	public synchronized  int getMaxDBVersionNumberOfSelfAuthoredArtifact(
			ArtifactKeyPojo inArtifactKeyPojo) {

		int maxDBVersionNumberOfSelfAuthoredArtifact = -1;
		
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = " select coalesce(max(UnpulishedVerNum),-1) as MaxDBVersionNumber from SelfAuthoredArtifacts c "
				+ " where c.rootNick = '" + inArtifactKeyPojo.rootNick + "' "
				+ " and c.Relevance = '" + inArtifactKeyPojo.relevance + "' "
				+ " and c.ArtifactName = '" + inArtifactKeyPojo.artifactName + "' "
				+ " and c.ContentType = '" + inArtifactKeyPojo.contentType + "' " 
				;

			System.out.println("QUERY for readSelfAuthoredArtifactWithRootPojo = "
					+ queryString);

			System.out.println("before query 1111.2");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {
				maxDBVersionNumberOfSelfAuthoredArtifact = rs.getInt("MaxDBVersionNumber");
				System.out.println("maxDBVersionNumberOfSelfAuthoredArtifact = " + maxDBVersionNumberOfSelfAuthoredArtifact);
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getMaxDBVersionNumberOfSelfAuthoredArtifact " + inArtifactKeyPojo.artifactName, e);
		}
		System.out.println("after reading maxDBVersionNumberOfSelfAuthoredArtifact");

		return maxDBVersionNumberOfSelfAuthoredArtifact;
	}
	public synchronized  ArrayList<SelfAuthoredArtifactpojo> readAllVersionsSelfAuthoredArtifacts(
			ArtifactKeyPojo inArtifactKeyPojo) {

		ArrayList<SelfAuthoredArtifactpojo> prevVerSelfAuthoredArtifactsList = new ArrayList<SelfAuthoredArtifactpojo>();
		SelfAuthoredArtifactpojo selfAuthoredArtifactspojo = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}

			String queryString = "SELECT a.RootNick, a.Relevance, a.ArtifactName, a.ContentType,"
					+ " case when erl.Requestor is not null then erl.Requestor "
					+ "  	 else coalesce(a.Requestor,\"\") end as Requestor, "
					+ " case when erl.Author is not null then erl.Author "
					+ "  	 else coalesce(a.Author,\"\") end as Author, "
					+ " coalesce(a.HasSpecialHandler,\"\") as HasSpecialHandler,"
					+ " coalesce(a.ReviewFileName,\"\") as ReviewFileName,"
					+ " case when erl.ERLStatus is not null then erl.ERLStatus "
					+ "  	 else coalesce(a.ERLStatus,\"\") end as ERLStatus, "
					+ " a.LocalFileName," + " a.DraftingState,"
					+ " coalesce(a.ReqRespFileName,\"\") as ReqRespFileName, "
					+ " a.UnpulishedVerNum, coalesce(a.DelegatedTo,\"\") as DelegatedTo, "
					+ " coalesce(a.ParentRelevance,\"\") as ParentRelevance, "
					+ " coalesce(a.ParentArtifactName,\"\") as ParentArtifactName,"
					+ " coalesce(a.ParentContentType,\"\") as ParentContentType"
					+ " from SelfAuthoredArtifacts a "
					+ " left outer join "
					+ catalogDBAliasPrefix + "ERLMaster erl "
					+ " on erl.RootNick = a.RootNick "
					+ " and erl.Relevance = a.Relevance "
					+ " and erl.ArtifactName = a.ArtifactName "
					+ " and erl.ContentType = a.ContentType "
					+ " where a.RootNick = '" + inArtifactKeyPojo.rootNick + "' "
					+ " and a.Relevance = '" + inArtifactKeyPojo.relevance + "' "
					+ " and a.ArtifactName = '" + inArtifactKeyPojo.artifactName + "' "
					+ " and a.ContentType = '" + inArtifactKeyPojo.contentType + "' "
					+ " order by a.UnpulishedVerNum desc"
					;

			System.out.println("QUERY for readArtfictsWithGivenStatusForOneRoot3 = "
					+ queryString);

			System.out.println("before query 1111.2");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {
				
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("Relevance"), rs.getString("ArtifactName"),  
								rs.getString("ContentType"));
				ArtifactKeyPojo tempParentArtifactKeyPojo = new ArtifactKeyPojo ( 
						rs.getString("RootNick"), rs.getString("ParentRelevance"), rs.getString("ParentArtifactName"),  
								rs.getString("ParentContentType"));
				
				selfAuthoredArtifactspojo = new SelfAuthoredArtifactpojo(
						tempArtifactKeyPojo,
						rs.getString("Requestor"),
						rs.getString("Author"),						
						rs.getBoolean("HasSpecialHandler"),
						rs.getString("ReviewFileName"),
						rs.getString("ERLStatus"),
						tempParentArtifactKeyPojo,
						rs.getString("LocalFileName"),
						rs.getString("DraftingState"),
						rs.getString("ReqRespFileName"),
						rs.getInt("UnpulishedVerNum"),
						rs.getString("DelegatedTo"));

				System.out.println("after set SAA pojo");
				prevVerSelfAuthoredArtifactsList.add(selfAuthoredArtifactspojo);
				System.out.println("after add");
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readAllVersionsSelfAuthoredArtifacts " + inArtifactKeyPojo.artifactName, e);
		}
		return prevVerSelfAuthoredArtifactsList;
		
	}

	public synchronized  ArrayList<SelfAuthoredArtifactpojo> readArtfictsPendingResponseForOneRoot(String inRootNick) {
		return readArtfictsWithGivenStatusForOneRoot(inRootNick, SelfAuthoredArtifactpojo.ArtifactStatusUploaded);
	}


	public synchronized  ContentTypePojo readContentType(String inContentType) {
		ContentTypePojo ContentTypePojo = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT ContentType, "
					+ " coalesce(Template,\"\") as Template, "
					+ " coalesce(Extension,\"\") as Extension "
					+ "from " 
					+ sysDBAliasPrefix + "ContentTypes where ContentType = '"
					+ inContentType + "'";
			System.out.println("@ readContentType : " + queryString);

			ResultSet rs = statement.executeQuery(queryString);
			System.out.println("5Oct_afterexecute");

			while (rs.next()) {
				System.out.println("5Oct_afterexecuteInLoop");
				
				ContentTypePojo = new ContentTypePojo(rs
						.getString("ContentType"), rs.getString("Template"), rs
						.getString("extension"));
				
				System.out.println("5Oct_afterexecuteEndofLoopBlock");
				
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readContentType " + inContentType, e);
		}
		return ContentTypePojo;
	}

	public synchronized  String[] readAllRelevanceStrings(String inRootNick) {
		ArrayList<String> relevanceList = new ArrayList<String>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT RL.Relevance from "
					+ catalogDBAliasPrefix
					+ "Relevance RL "
					+ " where RL.RootNick = '" + inRootNick 
					+ "' order by RL.Relevance";
			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				relevanceList.add(rs.getString("Relevance"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readAllRelevanceStrings" + inRootNick, e);
		}
		String[] relevance = new String[relevanceList.size()];
		relevanceList.toArray(relevance);
		return relevance;
	}
	
	public synchronized  String[] readPickedRelevance(String inRootNick) {
		ArrayList<String> relevanceList = new ArrayList<String>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT Relevance from "
			+ " PickedRelevance where RootNick = '" + inRootNick + "'";
			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				relevanceList.add(rs.getString("Relevance"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readPickedRelevance" + inRootNick, e);
		}
		String[] relevance = new String[relevanceList.size()];
		relevanceList.toArray(relevance);
		return relevance;
	}

	public synchronized  String[] getERLContentTypesInRelevance(String inRootNick, String inRelevance) {
		ArrayList<String> contentTypeList = new ArrayList<String>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT distinct "
			+ "erl.RootNick, "
			+ "erl.ContentType "
			+ "from "
			+ catalogDBAliasPrefix + "ERLMaster erl, "
			+ sysDBAliasPrefix + "ContentTypes ct "
			+ " where erl.RootNick = '" + inRootNick
			+ "' and Relevance = '"
			+ inRelevance
			+ "' "
			+ " and ct.ContentType = erl.ContentType "
			;
			
			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				contentTypeList.add(rs.getString("ContentType"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getERLContentTypesInRelevance " + inRootNick + " " + inRelevance, e);
		}
		String[] contentTypes = new String[contentTypeList.size()];
		contentTypeList.toArray(contentTypes);
		return contentTypes;
	}
	
	public synchronized  String[] getERLRelevances(String inRootNick) {
		ArrayList<String> relevanceList = new ArrayList<String>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT distinct "
			+ "erl.RootNick, "
			+ "erl.Relevance "
			+ "from "
			+ catalogDBAliasPrefix + "ERLMaster erl "
			+ " where erl.RootNick = '" + inRootNick + "'"
			;
			
			System.out.println("getERLRelevances :: " + queryString);
			ResultSet rs = statement.executeQuery(queryString);
			System.out.println("retrived some? ");

			while (rs.next()) {
				System.out.println("@@ db relevance : "+ rs.getString("Relevance"));
				relevanceList.add(rs.getString("Relevance"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getERLRelevances " + inRootNick, e);
		}
		String[] relevances = new String[relevanceList.size()];
		relevanceList.toArray(relevances);
		return relevances;
	}

	public synchronized  String[] getERLArtifactsInRelevanceAndContentType(String inRootNick, String inRelevance, String inContentType)  {
		ArrayList<String> artifactsList = new ArrayList<String>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT distinct "
			+ "erl.RootNick, "
			+ "erl.ArtifactName "
			+ "from "
			+ catalogDBAliasPrefix + "ERLMaster erl, "
			+ sysDBAliasPrefix + "ContentTypes ct "
			+ " where erl.RootNick = '" + inRootNick + "'"
			+ " and erl.Relevance = '"
			+ inRelevance
			+ "' and erl.ContentType = '"
			+ inContentType
			+ "' "
			+ " and ct.ContentType = erl.ContentType "
			;
			
			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				artifactsList.add(rs.getString("ArtifactName"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager getERLArtifactsInRelevanceAndContentType " +  inRootNick + " " + inRelevance + " " + inContentType, e);
		}
		String[] artifacts = new String[artifactsList.size()];
		artifactsList.toArray(artifacts);
		return artifacts;
	}

	public synchronized  ArrayList<RelevancePojo> readRelevances(String inRootNick) {
		ArrayList<RelevancePojo> relevancePojoList = new ArrayList<RelevancePojo>();
		RelevancePojo relevancePojo = null;
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT RL.RootNick, RL.Relevance, coalesce(PR.Relevance,\"\") as PickedRelevance from "
				+ catalogDBAliasPrefix
				+ "Relevance RL"
				+ " left outer join "
				+ " PickedRelevance PR "
				+ " on PR.RootNick = RL.RootNick "
				+ " and PR.Relevance = RL.Relevance "
				+ " where RL.RootNick = '" + inRootNick + "'"
				+ " order by RL.Relevance";			
			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				relevancePojo = new RelevancePojo(rs.getString("RootNick"), rs.getString("Relevance"), rs
								.getString("PickedRelevance"));
				relevancePojoList.add(relevancePojo);
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readRelevances " + inRootNick, e);
		}
		return relevancePojoList;
	}

	public synchronized  void pickRelevance(RelevancePojo inRelevancePojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "INSERT INTO PickedRelevance (RootNick, Relevance) VALUES ('"
					+ inRelevancePojo.rootNick + "', '"+ inRelevancePojo.relevance + "')";
			System.out.println(queryString);
			statement.executeUpdate(queryString);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager pickRelevance " + inRelevancePojo.relevance, e);
		}
	}

	public synchronized  void unPickRelevance(RelevancePojo inRelevancePojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "Delete from "
					+ " PickedRelevance where RootNick = '" + inRelevancePojo.rootNick + "' and Relevance = '"
					+ inRelevancePojo.relevance + "'";
			System.out.println(queryString);
			statement.executeUpdate(queryString);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager unPickRelevance " + inRelevancePojo.relevance, e);
		}
	}

	public synchronized  String[] readContentTypes() {

		ArrayList<String> ContentTypesList = new ArrayList<String>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT ContentType from " +
			sysDBAliasPrefix + "ContentTypes";
			System.out.println("@ readContentTypes : " + queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				ContentTypesList.add(rs.getString("ContentType"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readContentTypes ", e);
		}
		String[] ContentTypes = new String[ContentTypesList.size()];
		for (int i = 0; i < ContentTypes.length; i++)
			ContentTypes[i] = ContentTypesList.get(i).toString();
		return ContentTypes;
	}

	public synchronized  void closeup() {
		try {
			if (connection != null)
				connection.close();
			System.out.println("connection closed ok");
		} catch (SQLException e) { // connection close failed.
			System.err.println(e);
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager closeup ", e);
		}
	}

	public synchronized  void updateSubscriptionRemarksDownloadTS(ERLDownload inERLDownload) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = " Update Subscriptions "
			+ " Set DownLoadedReviewFile = '" + inERLDownload.downLoadedReviewFile  + "' "
			+ " , DownLoadedReviewTimeStamp = '" + inERLDownload.downLoadedReviewTimeStamp  + "' "
			+ " where RootNick = '" + inERLDownload.artifactKeyPojo.rootNick + "'"
			+ " and Relevance = '" + inERLDownload.artifactKeyPojo.relevance + "'"
			+ " and ArtifactName = '" + inERLDownload.artifactKeyPojo.artifactName + "'"
			+ " and ContentType = '" + inERLDownload.artifactKeyPojo.contentType + "'";

			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateSubscriptionRemarksDownloadTS ERLDownload " + inERLDownload.artifactKeyPojo.artifactName, e);
		}
	}
	
	public synchronized void replaceSubscription(ERLDownload inERLDownload, String newStatus) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			inERLDownload.subscriptionStatus = newStatus;
			String replaceString = "REPLACE INTO Subscriptions "
					+ " (RootNick, "
					+ " Relevance, "
					+ " ArtifactName, "
					+ " ContentType, "
					+ " SubscriptionStatus, "
					+ " DownLoadedFile, "
					+ " DownLoadedReviewFile, "
					+ " DownLoadedArtifactTimeStamp, "
					+ " DownLoadedReviewTimeStamp) "
					+ " VALUES ('" 
					+ inERLDownload.artifactKeyPojo.rootNick + "', '" 
					+ inERLDownload.artifactKeyPojo.relevance + "', '" 
					+ inERLDownload.artifactKeyPojo.artifactName + "', '" 
					+ inERLDownload.artifactKeyPojo.contentType + "', '" 
					+ inERLDownload.subscriptionStatus + "', '" 
					//+ inERLDownload.processState + "', '" 
					+ inERLDownload.downLoadedFile + "', '" 
					+ inERLDownload.downLoadedReviewFile + "', '" 
					+ inERLDownload.downLoadedArtifactTimeStamp + "', '" 
					+ inERLDownload.downLoadedReviewTimeStamp + "')";
			System.out.println(replaceString);
			statement.executeUpdate(replaceString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager replaceSubscription for " + inERLDownload.artifactKeyPojo.artifactName + " " + newStatus, e);
		}
	}

	
	public synchronized  ArrayList<AutoTriggerPojo> getElapsedAutoTriggers() throws ParseException{
		System.out.println("starting getElapsedAutoTriggers");
		ArrayList<AutoTriggerPojo> allAutoTriggers = readAllAutoTriggers();

		ArrayList<AutoTriggerPojo> elapsedAutoTriggers = new ArrayList<AutoTriggerPojo>();
		for (AutoTriggerPojo autoTrigger : allAutoTriggers){

			System.out.println("each autoTrigger from allAutoTriggers now for artifactName "  + autoTrigger.artifactKeyPojo.artifactName);
			System.out.println("checking autoTrigger prevTriggeredAt "  + autoTrigger.prevTriggeredAt);

			if (autoTrigger.processState.equalsIgnoreCase(AutoTriggerPojo.PROCESS_STAT_NEW) || 
					commons.hasTimeSecElapsed(commons.getDateFromString(autoTrigger.prevTriggeredAt),autoTrigger.triggerIntervalSec)){

				System.out.println("artifactName is new OR "  + autoTrigger.artifactKeyPojo.artifactName);
				System.out.println("time elapsed for artifactName "  + autoTrigger.artifactKeyPojo.artifactName);
				if (!autoTrigger.processState.equalsIgnoreCase(AutoTriggerPojo.PROCESS_STAT_NEW)) {
					System.out.println("xx autoTrigger prevTriggeredAt "  + autoTrigger.prevTriggeredAt);
				}
				elapsedAutoTriggers.add(autoTrigger);
			}
		}
		return elapsedAutoTriggers;
	}
	
	public synchronized  ArrayList<AutoTriggerPojo> readAllAutoTriggers() {

		System.out.println("starting readAllAutoTrigger");
		ArrayList<AutoTriggerPojo> currentAutoTriggers = new ArrayList<AutoTriggerPojo>();
		
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
								+ " RootNick, "
								+ " Relevance, "
								+ " ArtifactName, "
								+ " ContentType, "
								+ " coalesce(ERLUploadedTimeStamp,\"\") as ERLUploadedTimeStamp, "
								+ " coalesce(PrevTriggeredAt,\"\") as PrevTriggeredAt, "
								+ " coalesce(TriggerIntervalSec,\"\") as TriggerIntervalSec, "
								+ " coalesce(ProcessState,\"\") as ProcessState "
								+ " from AutoTriggers "
								+ " where ProcessState <> '" + AutoTriggerPojo.PROCESS_STAT_DISCONTINUE + "' " ;

			System.out.println(queryString);
			
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				ArtifactKeyPojo artifactKeyPojo = new ArtifactKeyPojo(
													rs.getString("RootNick"),rs.getString("Relevance"),
													rs.getString("ArtifactName"),rs.getString("ContentType"));
											
				System.out.println("showing1 readAllAutoTriggers date movement of PrevTriggeredAt as string " + rs.getString("PrevTriggeredAt"));
				AutoTriggerPojo autoTrigger = new AutoTriggerPojo(
												artifactKeyPojo,
												rs.getString("ERLUploadedTimeStamp"),
												rs.getString("PrevTriggeredAt"),
												rs.getInt("TriggerIntervalSec"),
												rs.getString("ProcessState"));
				currentAutoTriggers.add(autoTrigger);
			}
			System.out.println("At aa1 readAllAutoTriggers");
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			System.out.println("At aaeerr readAllAutoTriggers");
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readAllAutoTriggers " , e);
		}
		return currentAutoTriggers;
	}

	public synchronized  AutoTriggerPojo readAutoTrigger(ArtifactKeyPojo inArtifactKeyPojo) {

		System.out.println("starting readAutoTrigger");
		AutoTriggerPojo autoTrigger = null;
		
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
			+ " coalesce(ERLUploadedTimeStamp,\"\") as ERLUploadedTimeStamp, "
			+ " coalesce(PrevTriggeredAt,\"\") as PrevTriggeredAt, "
			+ " coalesce(TriggerIntervalSec,\"\") as TriggerIntervalSec, "
			+ " coalesce(ProcessState,\"\") as ProcessState "
			+ " from AutoTriggers at "
			+ " where at.RootNick = '" + inArtifactKeyPojo.rootNick +"' "
			+ " and at.Relevance = '" + inArtifactKeyPojo.relevance +"' "
			+ " and at.ArtifactName = '" + inArtifactKeyPojo.artifactName +"' "
			+ " and at.ContentType = '" + inArtifactKeyPojo.contentType +"' "
			;
			System.out.println(queryString);
			
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				System.out.println("showing2 readAutoTrigger String movement of PrevTriggeredAt " + rs.getString("PrevTriggeredAt"));
				
				autoTrigger = new AutoTriggerPojo(
						inArtifactKeyPojo,
						rs.getString("ERLUploadedTimeStamp"),
						rs.getString("PrevTriggeredAt"),
						rs.getInt("TriggerIntervalSec"),
						rs.getString("ProcessState"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readAutoTrigger " + inArtifactKeyPojo.artifactName, e);
		}
		return autoTrigger;
	}
	
	public synchronized  void insertAutoTrigger(AutoTriggerPojo inAutoTriggerPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String insertString = "INSERT INTO AutoTriggers "
					+ " (RootNick, "
					+ " Relevance, "
					+ " ArtifactName, "
					+ " ContentType, "
					+ " ERLUploadedTimeStamp, "
					+ " PrevTriggeredAt, "
					+ " TriggerIntervalSec, "
					+ " ProcessState) "
					+ " VALUES ('" 
					+ inAutoTriggerPojo.artifactKeyPojo.rootNick + "', '" 
					+ inAutoTriggerPojo.artifactKeyPojo.relevance + "', '" 
					+ inAutoTriggerPojo.artifactKeyPojo.artifactName + "', '" 
					+ inAutoTriggerPojo.artifactKeyPojo.contentType + "', '" 
					+ inAutoTriggerPojo.erlORRwUploadedTimeStamp + "', '"
					+ inAutoTriggerPojo.prevTriggeredAt + "', " 
					+ inAutoTriggerPojo.triggerIntervalSec + ", '" 
					+ inAutoTriggerPojo.processState + "')";
			System.out.println(insertString);
			statement.executeUpdate(insertString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager insertAutoTrigger " + inAutoTriggerPojo.artifactKeyPojo.artifactName, e);
		}
	}

	public synchronized void updateAutoTrigger(AutoTriggerPojo inAutoTriggerPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update AutoTriggers"
				+ " Set ERLUploadedTimeStamp = '" + inAutoTriggerPojo.erlORRwUploadedTimeStamp + "', "
				+ " PrevTriggeredAt = '" + inAutoTriggerPojo.prevTriggeredAt + "', "
				+ " TriggerIntervalSec = " + inAutoTriggerPojo.triggerIntervalSec + ", "
				+ " ProcessState = '" + inAutoTriggerPojo.processState + "' "
				+ " where RootNick = '" + inAutoTriggerPojo.artifactKeyPojo.rootNick + "' "
				+ " and Relevance = '" + inAutoTriggerPojo.artifactKeyPojo.relevance + "' "
				+ " and ArtifactName = '" + inAutoTriggerPojo.artifactKeyPojo.artifactName + "' "
				+ " and ContentType = '" + inAutoTriggerPojo.artifactKeyPojo.contentType + "' ";
			System.out.println(updateString);
			statement.executeUpdate(updateString);
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateAutoTrigger " + inAutoTriggerPojo.artifactKeyPojo.artifactName, e);
		}
	}

	public synchronized  ArrayList<ClientSideNew_ReviewPojo> readReviewsPendingResponseOfOneRoot() {
		ArrayList<ClientSideNew_ReviewPojo> itemPojoList = new ArrayList<ClientSideNew_ReviewPojo>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT RevA.RootNick,RevA.Relevance,RevA.ArtifactName,RevA.ContentType," +
									" ItemName,Reviewer,CreatedTime,ProcessStatus," +
									" RevA.LocalFileName, coalesce(RevA.ReqRespFileName,\"\") as ReqRespFileName " +
									" from Reviews revA " +
									" where ProcessStatus <> 'Processed' and " +
									" CreatedTime = (select max(CreatedTime) from Reviews RevIn where " +
										" RevIn.RootNick = RevA.RootNick and " +
										" RevIn.Relevance = RevA.Relevance and " +
										" RevIn.ArtifactName = RevA.ArtifactName and " +
										" RevIn.ContentType = RevA.ContentType and " +
										" RevIn.ItemName = RevA.itemName)";

			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);

			while (rs.next()) {
				System.out.println("@@1");
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("Relevance"), rs.getString("ArtifactName"),  
								rs.getString("ContentType"));
				System.out.println("@@2");
				ClientSideNew_ReviewPojo tempItemPojo = new ClientSideNew_ReviewPojo(tempArtifactKeyPojo,rs.getString("ItemName"),rs.getString("Reviewer"),rs.getString("CreatedTime"),
						rs.getString("ProcessStatus"),rs.getString("LocalFileName"),rs.getString("ReqRespFileName"));
				System.out.println("@@5");
				itemPojoList.add(tempItemPojo);
				System.out.println("@@6");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readReviewsPendingResponseOfOneRoot ", e);
		}
		return itemPojoList;
	}
	
	public synchronized ClientSideNew_ReviewPojo readReview(ArtifactKeyPojo inArtifactKeyPojo, String inItemName) {
		ClientSideNew_ReviewPojo reviewPojo = null;
		System.out.println("at start of readReview");
		System.out.println("inArtifactKeyPojo" + inArtifactKeyPojo.relevance);

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT RevA.RootNick, RevA.Relevance, RevA.ArtifactName, RevA.ContentType, " +
					" ItemName,Reviewer,CreatedTime,ProcessStatus," +
					" RevA.LocalFileName, coalesce(RevA.ReqRespFileName,\"\") as ReqRespFileName " +
					" from Reviews revA " +
					" where RevA.RootNick = '" + inArtifactKeyPojo.rootNick + "' and " +
					" RevA.Relevance = '" + inArtifactKeyPojo.relevance + "' and " +
					" RevA.ArtifactName = '" + inArtifactKeyPojo.artifactName + "' and " +
					" ItemName = '" + inItemName + "' and " +
					" ProcessStatus <> 'Processed' and " +
					" CreatedTime = (select max(CreatedTime) from Reviews where " +
						" RootNick = '" + inArtifactKeyPojo.rootNick + "' and " +
						" Relevance = '" + inArtifactKeyPojo.relevance + "' and " +
						" ArtifactName = '" + inArtifactKeyPojo.artifactName + "' and " +
						" ItemName = '" + inItemName + "')";
			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				System.out.println("@@2rr");
				System.out.println("@@2r1");
				System.out.println("@@2r1 inArtifactKeyPojo = " + inArtifactKeyPojo);
				System.out.println("@@2r1b inArtifactKeyPojo.relevance" + inArtifactKeyPojo.relevance);
				reviewPojo = new ClientSideNew_ReviewPojo(inArtifactKeyPojo,inItemName,rs.getString("Reviewer"),rs.getString("CreatedTime"),
						rs.getString("ProcessStatus"),rs.getString("LocalFileName"),rs.getString("ReqRespFileName"));
				System.out.println("@@2rra reviewPojo.artifactKeyPojo.relevance" + reviewPojo.artifactKeyPojo.relevance);
				System.out.println("@@5rr");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager ClientSideNew_ReviewPojo " + inArtifactKeyPojo.artifactName + " " + inItemName, e);
		}
		return reviewPojo;
	}
	
	public synchronized void insertReview(ClientSideNew_ReviewPojo inReviewPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			
			String insertString = 
				"INSERT INTO Reviews (RootNick, Relevance,ArtifactName,ContentType,"
				 + "ItemName,Reviewer,CreatedTime,ProcessStatus,LocalFileName,ReqRespFileName) VALUES ("
				 + "'" + inReviewPojo.artifactKeyPojo.rootNick + "',"
				 + "'" + inReviewPojo.artifactKeyPojo.relevance + "',"
				 + "'" + inReviewPojo.artifactKeyPojo.artifactName + "',"
				 + "'" + inReviewPojo.artifactKeyPojo.contentType + "',"
				 + "'" + inReviewPojo.itemName + "',"
				 + "'" + inReviewPojo.reviewer + "',"
				 + "'" + inReviewPojo.createdTime + "',"
				 + "'" + inReviewPojo.processStatus + "',"
				 + "'" + inReviewPojo.reviewFileName + "',"
				 + "'" + inReviewPojo.reqRespFileName  + "')";
			System.out.println(insertString);
			statement.executeUpdate(insertString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager insertReview " + inReviewPojo.itemName, e);
		}
	}

	public synchronized void updateReviewProcessStatus(ClientSideNew_ReviewPojo inReviewPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			
			String updateString = 
				"UPDATE Reviews Set ProcessStatus = '" + inReviewPojo.processStatus + "',"
				+ " ReqRespFileName = '" + inReviewPojo.reqRespFileName + "'"
				+ " where RootNick = '" + inReviewPojo.artifactKeyPojo.rootNick + "'"
				+ " and Relevance = '" + inReviewPojo.artifactKeyPojo.relevance + "'"
				+ " and ArtifactName = '" + inReviewPojo.artifactKeyPojo.artifactName + "'"
				+ " and ItemName = '" + inReviewPojo.itemName + "'"
				+ " and Reviewer = '" + inReviewPojo.reviewer + "'"
				+ " and CreatedTime = '" + inReviewPojo.createdTime + "'";
			System.out.println(updateString);
			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateReviewProcessStatus " + inReviewPojo.itemName, e);
		}
	}

	ArrayList<ClientSideNew_ReviewPojo> readItemsWithReviewToBeUploaded(String inRootNick) {
		ArrayList<ClientSideNew_ReviewPojo> itemsWithReviewToBeUploaded = new ArrayList<ClientSideNew_ReviewPojo>();

		ClientSideNew_ReviewPojo reviewPojo = null;
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT RevA.RootNick,RevA.Relevance,RevA.ArtifactName,RevA.ContentType,"
				+ "RevA.ItemName,RevA.Reviewer,RevA.CreatedTime,RevA.ProcessStatus,RevA.LocalFileName,"
				+ "coalesce(RevA.ReqRespFileName,\"\") as ReqRespFileName from Reviews revA "
				+ " where RootNick = '" + inRootNick + "'"
				+ " and ProcessStatus = '" + ClientSideNew_ReviewPojo.TOBEUPLOADED + "'";
			
			System.out.println(queryString);
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				System.out.println("@@@1");

				ArtifactKeyPojo artifactKeyPojo = new ArtifactKeyPojo(
						rs.getString("RootNick"),rs.getString("Relevance"),rs.getString("ArtifactName"),rs.getString("ContentType"));
				System.out.println("@@@2");
				
				System.out.println("@@@3");
				
				reviewPojo = new ClientSideNew_ReviewPojo(artifactKeyPojo,rs.getString("ItemName"),rs.getString("Reviewer"),rs.getString("CreatedTime"),
						rs.getString("ProcessStatus"),rs.getString("LocalFileName"),rs.getString("ReqRespFileName"));
				System.out.println("@@@4");
				
				itemsWithReviewToBeUploaded.add(reviewPojo);
				System.out.println("artifactKeyPojo.relevance:::" + reviewPojo.artifactKeyPojo.relevance);
				System.out.println("artifactKeyPojo.artifactName" + reviewPojo.artifactKeyPojo.artifactName);
				System.out.println("itemName" + reviewPojo.itemName);
				System.out.println("artifactKeyPojo.createdTime" + reviewPojo.createdTime);
				System.out.println("artifactKeyPojo.processStatus" + reviewPojo.processStatus);
				System.out.println("artifactKeyPojo.LocalFileName" + reviewPojo.reviewFileName);
				System.out.println("artifactKeyPojo.reqRespFileName" + reviewPojo.reqRespFileName);
				System.out.println("artifactKeyPojo.revier" + reviewPojo.reviewer);				
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readItemsWithReviewToBeUploaded " + inRootNick, e);
		}
		return itemsWithReviewToBeUploaded;
	}
	
	public synchronized void updateArtifactStatus (
			SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo,
			String newStatus) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update SelfAuthoredArtifacts"
					+ " set DraftingState = '" + newStatus + "',"
					+ " LocalFileName = '" + inSelfAuthoredArtifactspojo.LocalFileName + "'"
					+ " where RootNick = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.rootNick + "'"
					+ " and Relevance = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.relevance + "'"
					+ " and ArtifactName = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName + "'"
					+ " and ContentType = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.contentType + "'"
					+ " and UnpulishedVerNum = '" + inSelfAuthoredArtifactspojo.unpulishedVerNum  + "'"
					;
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateArtifactStatus " 
			+ inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName + " " + newStatus, e);
		}
	}
	
	public synchronized  void deleteAllSelfAuthoredArtifacts(
			ArtifactKeyPojo inArtifactKeyPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "delete from SelfAuthoredArtifacts"
					+ " where RootNick = '" + inArtifactKeyPojo.rootNick + "'"
					+ " and Relevance = '" + inArtifactKeyPojo.relevance + "'"
					+ " and ArtifactName = '" + inArtifactKeyPojo.artifactName + "'"
					+ " and ContentType = '" + inArtifactKeyPojo.contentType + "'"
					;
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager deleteAllSelfAuthoredArtifacts " 
			+ inArtifactKeyPojo.artifactName, e);
		}
	}

	public synchronized  void updateOlderArtifact(
			ArtifactKeyPojo inArtifactKeyPojo,
			String newStatus,
			int targetLocalVersion) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update SelfAuthoredArtifacts"
					+ " set DraftingState = '" + newStatus + "' where"
					+ " RootNick = '" + inArtifactKeyPojo.rootNick + "'"
					+ " and Relevance = '" + inArtifactKeyPojo.relevance + "'"
					+ " and ArtifactName = '" + inArtifactKeyPojo.artifactName + "'"
					+ " and ContentType = '" + inArtifactKeyPojo.contentType + "'"
					+ " and UnpulishedVerNum < '" + targetLocalVersion  + "'"
					;
			System.out.println(updateString);
	
			statement.executeUpdate(updateString);
	
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateOlderArtifact " 
			+ inArtifactKeyPojo.artifactName + " " + newStatus + " " + targetLocalVersion, e);
		}
	}
	

	public synchronized  void updateArtifact(SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
				
			String updateString = "update SelfAuthoredArtifacts"
					+ " Set ContentType = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.contentType
					+ "' , LocalFileName = '" + inSelfAuthoredArtifactspojo.LocalFileName	
					+ "' , ReqRespFileName = '" + inSelfAuthoredArtifactspojo.ReqRespFileName
					+ "' , Requestor = '" + inSelfAuthoredArtifactspojo.requestor
					+ "' , Author = '" + inSelfAuthoredArtifactspojo.author
					+ "' , HasSpecialHandler = '" 
					+ (inSelfAuthoredArtifactspojo.hasSpecialHandler?1:0)
					+ "' , ReviewFileName = '" + inSelfAuthoredArtifactspojo.reviewFileName
					+ "' , ERLStatus = '" + inSelfAuthoredArtifactspojo.erlStatus
					+ "' , DraftingState = '" + inSelfAuthoredArtifactspojo.draftingState
					+ "' where RootNick = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.rootNick
					+ "' and ArtifactName = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName
					+ "' and ContentType = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.contentType
					+ "' and Relevance = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.relevance
					+ "' and UnpulishedVerNum = '" + inSelfAuthoredArtifactspojo.unpulishedVerNum  + "'"
					;
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateArtifact " + 
			inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName, e);
		}

	}
	
	public synchronized  void updateArtifactStatusAndReqRespFileName(
			SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo,
			String inReqRespFileName,
			String newStatus) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update SelfAuthoredArtifacts"
					+ " set DraftingState = '" + newStatus 
					+ "', ReqRespFileName = '"
					+ inReqRespFileName
					+ "' where "
					+ " RootNick = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.rootNick
					+ "' and ArtifactName = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName
					+ "' and ContentType = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.contentType
					+ "' and Relevance = '" + inSelfAuthoredArtifactspojo.artifactKeyPojo.relevance
					+ "' and UnpulishedVerNum = '" + inSelfAuthoredArtifactspojo.unpulishedVerNum  + "'"
					;
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateArtifactStatusAndReqRespFileName " +
			inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName + " " 
			+ inReqRespFileName + " " + newStatus, e);
		}

	}

	public synchronized void insertArtifactUI(
			SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}

			String insertString = "INSERT INTO SelfAuthoredArtifacts " +
					"(RootNick,Relevance,ArtifactName,ContentType,UnpulishedVerNum,LocalFileName," +
					"DraftingState,Requestor,Author,DelegatedTo,HasSpecialHandler,ReqRespFileName,ReviewFileName," +
					" ERLStatus, " +
					"ParentRelevance,ParentArtifactName,ParentContentType" +
					") VALUES ("
					+ "'" + inSelfAuthoredArtifactspojo.artifactKeyPojo.rootNick + "',"
					+ "'" + inSelfAuthoredArtifactspojo.artifactKeyPojo.relevance + "',"
					+ "'" + inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName + "',"
					+ "'" + inSelfAuthoredArtifactspojo.artifactKeyPojo.contentType + "',"
					+ "'" + inSelfAuthoredArtifactspojo.unpulishedVerNum + "',"
					+ "'" + inSelfAuthoredArtifactspojo.LocalFileName + "',"
					+ "'" + inSelfAuthoredArtifactspojo.draftingState + "',"
					+ "'" + inSelfAuthoredArtifactspojo.requestor + "',"
					+ "'" + inSelfAuthoredArtifactspojo.author + "',"
					+ "'" + inSelfAuthoredArtifactspojo.DelegatedTo + "',"
					+ "'" + inSelfAuthoredArtifactspojo.hasSpecialHandler + "'," 
					+ "'" + inSelfAuthoredArtifactspojo.ReqRespFileName + "',"
					+ "'" + inSelfAuthoredArtifactspojo.reviewFileName + "',"
					+ "'" + inSelfAuthoredArtifactspojo.erlStatus + "',"
					+ ((inSelfAuthoredArtifactspojo.parentArtifactKeyPojo==null)?
						"'','',''"
						:
						"'" 
						+ inSelfAuthoredArtifactspojo.parentArtifactKeyPojo.relevance
						+ "','"
						+ inSelfAuthoredArtifactspojo.parentArtifactKeyPojo.contentType 
						+ "','"
						+ inSelfAuthoredArtifactspojo.parentArtifactKeyPojo.artifactName
						+ "'")
					+ ")"
					;
					
			System.out.println(insertString);

			statement.executeUpdate(insertString);

			System.out.println("@insertArtifactUI insertString success");

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager insertArtifactUI " +
			inSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName, e);
		}
	}

	public synchronized String ReadXMLArtifactUI() {

		ResultSet rs = null;
		Statement stmt = null;
		String sql;
		String XMLString = new String();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element results = doc.createElement("Results");
			doc.appendChild(results);

			sql = "select * from "
			+ " SelfAuthoredArtifacts";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			while (rs.next()) {
				Element row = doc.createElement("Row");
				results.appendChild(row);
				for (int ii = 1; ii <= colCount; ii++) {
					String columnName = rsmd.getColumnName(ii);
					Object value = rs.getObject(ii);
					Element node = doc.createElement(columnName);
					node.appendChild(doc.createTextNode(value.toString()));
					row.appendChild(node);
				}
			}

			XMLString = commons.getDocumentAsXml(doc);

		} catch (Exception e) {
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager ReadXMLArtifactUI ", e);
		}
		return XMLString;

	}

	public synchronized void updateERL(ERLpojo erlPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = " Update "
							+ catalogDBAliasPrefix
							+ "ERLMaster Set "
							+ " ContentType = '"
							+ erlPojo.artifactKeyPojo.contentType
							+ "', Requestor = '"
							+ erlPojo.requestor
							+ "', Author = '"
							+ erlPojo.author
							+ "', ContentFileName = '"
							+ erlPojo.contentFileName
							+ "', ERLStatus = '"
							+ erlPojo.erlStatus
							+ "', ReviewFileName = '"
							+ erlPojo.reviewFileName
							+ "', UploadedTimeStamp = '"
							+ erlPojo.uploadedTimeStamp 
							+ "', ReviewTimeStamp = '"
							+ erlPojo.reviewTimeStamp
							+ "' Where rootNick = '"
							+ erlPojo.artifactKeyPojo.rootNick
							+ "' and contentType = '"
							+ erlPojo.artifactKeyPojo.contentType
							+ "' and Relevance = '"
							+ erlPojo.artifactKeyPojo.relevance
							+ "' and ArtifactName = '"
							+ erlPojo.artifactKeyPojo.artifactName
							+ "'";

			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager updateERL " + erlPojo.artifactKeyPojo.artifactName, e);
		}
	}
	
	public synchronized void insertERL(ERLpojo erlPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String insertString = " INSERT INTO "
					+ catalogDBAliasPrefix
					+ "ERLMaster (RootNick, Relevance, ArtifactName, "
					+ " ContentType, Requestor, Author, "
					+ " ContentFileName, ERLStatus, reviewFileName, UploadedTimeStamp, ReviewTimeStamp) "
					+ " VALUES ("
					+ "'"
					+ erlPojo.artifactKeyPojo.rootNick
					+ "', "
					+ "'"
					+ erlPojo.artifactKeyPojo.relevance
					+ "', "
					+ "'"
					+ erlPojo.artifactKeyPojo.artifactName // enhancement: Change ContentName to ContentName
					+ "', "
					+ "'"
					+ erlPojo.artifactKeyPojo.contentType
					+ "', "
					+ "'"
					+ erlPojo.requestor
					+ "', "
					+ "'"
					+ erlPojo.author
					+ "', "
					+ "'"
					+ erlPojo.contentFileName
					+ "', "
					+ "'"
					+ erlPojo.erlStatus
					+ "', "
					+ "'"
					+ erlPojo.reviewFileName
					+ "', "
					+ "'"
					+ erlPojo.uploadedTimeStamp
					+ "', "
					+ "'"
					+ erlPojo.reviewTimeStamp
					+ "'" +
					")";

			System.out.println(insertString);

			statement.executeUpdate(insertString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager insertERL " + erlPojo.artifactKeyPojo.artifactName, e);
		}
	}

	public synchronized  ArrayList<UserPojo> readUsersList() {

		ArrayList<UserPojo> usersList = new ArrayList<UserPojo>();

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}

			String queryString = "SELECT ShortId, EmployeeName, EmailID, PrivilegeLevel from "
					+ catalogDBAliasPrefix + "Users " 
					+ " order by EmployeeName"
					;

			System.out.println("QUERY for readUsersList = "
					+ queryString);

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {
				
				UserPojo userPojo = new UserPojo (
						rs.getString("ShortId"), rs.getString("EmployeeName"), rs.getString("EmailID"),rs.getInt("PrivilegeLevel"));
				usersList.add(userPojo);
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager readUsersList ", e);
		}
		return usersList;
	}
	
	public synchronized void neverCallMe_DeleteAllSelfAuthoredArtifacts() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "delete from SelfAuthoredArtifacts"
					;
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager neverCallMe_DeleteAllSelfAuthoredArtifacts ", e);
		}
	}
	
	public synchronized void neverCallMe_DeleteAllReviews() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = " delete from Reviews ";
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager neverCallMe_DeleteAllReviews ", e);
		}
	}

	public synchronized void neverCallMe_DeleteAllTriggers() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = " delete from AutoTriggers ";
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager neverCallMe_DeleteAllTriggers ", e);
		}
	}


	public synchronized void neverCallMe_DeleteSubscriptions() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "delete from Subscriptions";
			System.out.println(updateString);
			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager neverCallMe_DeleteSubscriptions ", e);
		}
	}

	public synchronized void neverCallMe_DeleteERLs() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = " delete from " 
					+ catalogDBAliasPrefix + "ERLMaster ";
			statement.executeUpdate(updateString);

			System.out.println("@ updateSQL : " + updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error in CatelogPersistenceManager neverCallMe_DeleteERLs ", e);
		}
		return;
	}
}