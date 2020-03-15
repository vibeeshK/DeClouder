package espot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public class ArchiveInactiveERLs {

	/* ArchiveInactiveERLs moves the inactive ERLs which were created before 
	 * the inactiveHoldPeriod gap to InactiveArchive folder
	 */
	
	private RootPojo rootPojo = null;
	private CatelogPersistenceManager catelogPersistenceManager;
	private Commons commons;
	private CommonData commonData = null;			
	private RemoteAccesser remoteAccesser;
	private String erlVersionDocPathFileName;
	ERLVersionDocPojo erlVersionDetail;
	
	public ArchiveInactiveERLs(CommonData inCommonData,RemoteAccesser inRemoteAccesser) {
		commonData = inCommonData;
		rootPojo = commonData.getCurrentRootPojo();
		commons = commonData.getCommons();
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		remoteAccesser = inRemoteAccesser;
		commons.logger.info("At ArchiveInactiveERLs start for root " + rootPojo.rootNick);	

		erlVersionDocPathFileName = commons.getLocalERLVersioningPathFile(rootPojo.rootNick);		
		try {
			erlVersionDetail = (ERLVersionDocPojo) commonData.getCommons().getJsonDocFromFile(	
											erlVersionDocPathFileName,
											ERLVersionDocPojo.class);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in RequestProcessor constr2", e);			
		}
		if (erlVersionDetail == null) {
			erlVersionDetail = new ERLVersionDocPojo();
		}
	}

	public void archiveInactiveERLSOfOneRoot() throws TransformerConfigurationException, IOException, TransformerException, ParserConfigurationException, ClassNotFoundException, ParseException {

		ArrayList<ERLpojo> allInactiveERLs = catelogPersistenceManager.readInactiveERLs();
		
		int inactiveERLArchivalCount = 0;
		
		for (ERLpojo inactiveERL : allInactiveERLs){			
			if (!commons.hasDaysElapsed(commons.getDateFromString(inactiveERL.uploadedTimeStamp),commons.inactiveAgingDaysLimit)) {
				System.out.println("At archiveInactiveERLSOfOneRoot; skipping erl as days not elapsed for inactiveERL = " + inactiveERL.artifactKeyPojo.artifactName);
				continue;
			} else {
				String erlVersioningReviewItemKey = ERLVersioningDocItem.getERLVersioningItemKey(
						inactiveERL.artifactKeyPojo.relevance,
						inactiveERL.artifactKeyPojo.artifactName,
						RequestPojo.REVIEW);
	
				System.out.println("At archiveInactiveERLSOfOneRoot archiving erlVersioningReviewItemKey = " + erlVersioningReviewItemKey);
				
				inactiveERLArchivalCount = 
						inactiveERLArchivalCount 
						+ removeVersionsOfERLItem(inactiveERL.artifactKeyPojo, erlVersioningReviewItemKey);
	
				System.out.println("At archiveInactiveERLSOfOneRoot after removing erlVersioningReviewItemKey inactiveERLArchivalCount = " + inactiveERLArchivalCount);
				
				String erlVersioningArtifactItemKey = ERLVersioningDocItem.getERLVersioningItemKey(
						inactiveERL.artifactKeyPojo.relevance,
						inactiveERL.artifactKeyPojo.artifactName,
						RequestPojo.ARTIFACT);
	
				System.out.println("At archiveInactiveERLSOfOneRoot removing erlVersioningArtifactItemKey = " + erlVersioningArtifactItemKey);
	
				inactiveERLArchivalCount = 
						inactiveERLArchivalCount 
						+ removeVersionsOfERLItem(inactiveERL.artifactKeyPojo, erlVersioningArtifactItemKey);
				System.out.println("At archiveInactiveERLSOfOneRoot after removing erlVersioningArtifactItemKey inactiveERLArchivalCount = " + inactiveERLArchivalCount);
				commons.logger.info("At ArchiveInactiveERLs archived ArtifactItemKey = " + erlVersioningArtifactItemKey);
				commons.logger.info("At ArchiveInactiveERLs the inactiveERLArchivalCount at this point is = " + inactiveERLArchivalCount);
			}
		}

		System.out.println("At archiveInactiveERLSOfOneRoot after all removals inactiveERLArchivalCount = " + inactiveERLArchivalCount);
		commons.logger.info("At archiveInactiveERLSOfOneRoot after all removals inactiveERLArchivalCount = " + inactiveERLArchivalCount);
		
		if (inactiveERLArchivalCount > 0) {
			String catalogpublishFolder = rootPojo.rootString
					+ rootPojo.fileSeparator
					+ commons.getServerSideSideCatalogDbPublishFolderOfRoot();
			
			String catalogpublishFile = catalogpublishFolder
					+ rootPojo.fileSeparator
					+ commons.getNewCatalogDbPublishFileName(rootPojo.rootNick);

			System.out.println("new catalogpublishFile = " + catalogpublishFile);
			
			remoteAccesser.uploadToRemote(catalogpublishFile, commons.getServersMasterCopyofCatalogDbLocalFileOfRoot(rootPojo.rootNick));			
		}
		
	}
	
	public int removeVersionsOfERLItem(ArtifactKeyPojo inArtifactKeyPojo, String inERLVersioningItemKey) throws IOException{

		int inactiveCoreERLArchivalCount = 0;
		
		ERLVersioningDocItem erlVersioningDocItem
			= erlVersionDetail.erlVersionTrackItems.get(inERLVersioningItemKey);

		if (erlVersioningDocItem != null) {
		
			for (String erlItemFileLocation : erlVersioningDocItem.erlVerList) {
				String contentRemoteLocation
											= commons.getRemotePathFileName(
											rootPojo.rootString,
											inArtifactKeyPojo.relevance,
											erlItemFileLocation,
											rootPojo.fileSeparator);
				String archivalLocation = commons.getRemoteInactiveArchivalPathFileName(
											rootPojo.rootString,
											inArtifactKeyPojo.relevance,
											erlItemFileLocation,
											rootPojo.fileSeparator);
				
				System.out.println("At removeVersionsOfERLItem archiving oldestContentRemoteLocation = " + contentRemoteLocation);
				System.out.println(" archive location  = " + archivalLocation);
				
				remoteAccesser.moveToRemoteLocation(contentRemoteLocation, archivalLocation);
				
				System.out.println("At removeVersionsOfERLItem removing versionFile erlItem " + erlItemFileLocation);

				erlVersioningDocItem.erlVerList.remove(erlItemFileLocation);
			
				commonData.getCommons().putJsonDocToFile(	
											erlVersionDocPathFileName,
											erlVersionDetail);
				inactiveCoreERLArchivalCount++;
				System.out.println("At removeVersionsOfERLItem inactiveCoreERLArchivalCount " + inactiveCoreERLArchivalCount);
			}
		}
		
		if (inactiveCoreERLArchivalCount > 0) {
			erlVersionDetail.erlVersionTrackItems.remove(inERLVersioningItemKey);
			commonData.getCommons().putJsonDocToFile(	
										erlVersionDocPathFileName,
										erlVersionDetail);
		}
	
		return inactiveCoreERLArchivalCount;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		Commons commons = Commons.getInstance(Commons.BASE_CATALOG_SERVER);
		CommonData commonData = CommonUIData.getInstance(commons);
		RootPojo rootPojo = commonData.getCurrentRootPojo();		
		RemoteAccesser remoteAccesser = RemoteAccessManager.getInstance(commons, rootPojo.rootNick);
		ArchiveInactiveERLs ArchiveInactiveERLs = new ArchiveInactiveERLs(commonData,remoteAccesser);
		try {
			ArchiveInactiveERLs.archiveInactiveERLSOfOneRoot();
		} catch (ClassNotFoundException | TransformerException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in ArchiveInactiveERLs main");
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in ArchiveInactiveERLs main ", e);
		}
	}
	
}