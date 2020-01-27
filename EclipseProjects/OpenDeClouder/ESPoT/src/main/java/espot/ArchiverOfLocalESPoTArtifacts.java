package espot;

import java.io.IOException;
import java.text.ParseException;

public class ArchiverOfLocalESPoTArtifacts {
	/*
	 * Deletes all local drafts and artifacts of all roots at user's desktop
	 * CAUTION: YOU WILL LOOSE ALL DATA IF YOU INVOKE THIS CLASS.
	 * 			DONT INVOKE BEFORE BACKING UP CURRENT DATA
	 */

	private CommonUIData commonUIData;
	private Commons commons;
	private CatelogPersistenceManager catelogPersistenceManager;

	public ArchiverOfLocalESPoTArtifacts(CommonUIData inCommonUIData) {
		commonUIData = inCommonUIData;
		commons = commonUIData.getCommons();
		catelogPersistenceManager = commonUIData.getCatelogPersistenceManager();
	}
	public void archiveLocalESPoTArtifacts() {

		catelogPersistenceManager.neverCallMe_DeleteAllSelfAuthoredArtifacts();
		catelogPersistenceManager.neverCallMe_DeleteSubscriptions();
		catelogPersistenceManager.neverCallMe_DeleteAllReviews();		
		catelogPersistenceManager.neverCallMe_DeleteAllTriggers();

		try {

			//Catalog downloads are very small and clearing it will require a manual rerun of client archestrator
			//and updating the tracker file. Hence not archiving it.
			//commons.archiveLocalFolder(commons.getClientSideCatalogDbReceiveFolderOfRoot());
			
			System.out.println("At archiveLocalESPoTArtifacts1 trying to archive getNewArtifactsFolder " + commons.getNewArtifactsFolder(commons.getCurrentRootNick()));
			commons.archiveLocalFolder(commons.getNewArtifactsFolder());

			System.out.println("At archiveLocalESPoTArtifacts1 trying to archive getNewReviewsFolder " + commons.getNewReviewsFolder(commons.getCurrentRootNick()));			
			commons.archiveLocalFolder(commons.getNewReviewsFolder());
			
			System.out.println("At archiveLocalESPoTArtifacts1 trying to archive getResponsesfolderlocal " + commons.getResponsesfolderlocal(commons.getCurrentRootNick()));			
			commons.archiveLocalFolder(commons.getResponsesfolderlocal());

			System.out.println("At archiveLocalESPoTArtifacts1 trying to archive getContentDownLoadFolder " + commons.getContentDownLoadFolder(commons.getCurrentRootNick()));			
			commons.archiveLocalFolder(commons.getContentDownLoadFolder());
			
			System.out.println("At archiveLocalESPoTArtifacts1 trying to archive getDownloadedReviewsFolder " + commons.getDownloadedReviewsFolder(commons.getCurrentRootNick()));			
			commons.archiveLocalFolder(commons.getDownloadedReviewsFolder());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in ArchiverOfLocalESPoTArtifacts archiveLocalESPoTArtifacts of " + commons.getCurrentRootNick(), e);
		}	
	}
}