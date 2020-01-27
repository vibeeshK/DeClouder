package espot;

import java.util.ArrayList;

public class AssignedContentSubscriber {
	/*
	 * Helps to subscribe the erl contents where user is marked as the author
	 */
	private CatelogPersistenceManager catelogPersistenceManager = null;
	private Commons commons = null;
	private CommonData commonData = null;

	public AssignedContentSubscriber(CommonData inCommonData) {
		commonData = inCommonData;
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		commons = commonData.getCommons();
	}

	public void subscribeToAssignedContent(){
		System.out.println("at subscribeToDeckerParents start" );
		ArrayList<ERLDownload> dbAssignedERLDownloadsList = catelogPersistenceManager.readERLDownLoadsOfAssignedContent(commons.userName);
		System.out.println("at subscribeToDeckerParents 123 dbAssignedERLDownloadsList.size() is " + dbAssignedERLDownloadsList.size() );
		for (int erlCount = 0; erlCount < dbAssignedERLDownloadsList.size(); erlCount++) {
			ERLDownload erlDownload = dbAssignedERLDownloadsList.get(erlCount);
			System.out.println("at subscribeToAssignedContent 324123" );
			System.out.println("at subscribeToAssignedContent erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
			if (erlDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED)
					|| erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
				System.out.println("at subscribeToAssignedContent 543123" );
				catelogPersistenceManager.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				System.out.println("at subscribeToAssignedContent 324a14231" );
			} else {
				System.out.println("at subscribeToAssignedContent 54343523 Unexpected erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
			}			
			System.out.println("at subscribeToAssignedContent 1436723" );
		}
		System.out.println("at subscribeToAssignedContent end");
	}
}