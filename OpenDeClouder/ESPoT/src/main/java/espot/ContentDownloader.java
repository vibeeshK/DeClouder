package espot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

public class ContentDownloader {
	/*
	 * This class downloads the subscribed contents
	 */
	
	private CommonData commonData;

	private CatelogPersistenceManager catelogPersistenceManager;
	private Commons commons;
	private RemoteAccesser remoteAccesser;	
	private RootPojo rootPojo;

	public ContentDownloader(CommonData inCommonData, RemoteAccesser inRemoteAccesser) {

		System.out.println("@ContentDownloader initializing inCommonData" + inCommonData);
		System.out.println("@ContentDownloader initializing inRemoteAccesser" + inRemoteAccesser);

		commonData = inCommonData;
		rootPojo = commonData.getCurrentRootPojo();
		commons = commonData.getCommons();
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		remoteAccesser = inRemoteAccesser;
		
		System.out.println("@ContentDownloader initializing");
		System.out.println("@ContentDownloader " + rootPojo.rootNick);
		System.out.println("@ContentDownloader " + rootPojo.rootString);
		System.out.println("@ContentDownloader " + rootPojo.remoteAccesserType);
		System.out.println("@ContentDownloader " + remoteAccesser);
		
		
	}

	public void downloadContentFilesForOneRoot()
			throws IOException, ClassNotFoundException {

		ArrayList<ERLDownload> subscribedERLpojoList = null;

		System.out.println("@123411");

		subscribedERLpojoList = catelogPersistenceManager.getSubscribedERLpojoList();

		for (int erlCount = 0; erlCount < subscribedERLpojoList.size(); erlCount++) {

			System.out.println("Downloading the Subscribed content " + subscribedERLpojoList.get(erlCount).artifactKeyPojo.artifactName);

			String timeStamp = commons.getCurrentTimeStamp();
			System.out.println("at 1111");
			System.out.println("timeStamp " + timeStamp);

			System.out.println("inerlpojoList of  "
					+ subscribedERLpojoList.get(erlCount).contentFileName);

			String remoteFileURL = commons.getRemotePathFileName(rootPojo.rootString,
					subscribedERLpojoList.get(erlCount).artifactKeyPojo.relevance,
					subscribedERLpojoList.get(erlCount).contentFileName, rootPojo.fileSeparator);

			String absoluteDownloadFilePathFolder = commons.getFullLocalPathFileNameOfDownloadedArtifact(
					rootPojo.rootNick, 
					subscribedERLpojoList.get(erlCount).artifactKeyPojo.relevance,
					"");

			String absoluteDownloadFileName = commons.getFullLocalPathFileNameOfDownloadedArtifact(
					rootPojo.rootNick, 
					subscribedERLpojoList.get(erlCount).artifactKeyPojo.relevance,
					subscribedERLpojoList.get(erlCount).contentFileName);

			System.out.println("absoluteDownloadFileName = "
					+ absoluteDownloadFileName);

			System.out.println("absoluteDownloadFilePathFolder = "
					+ absoluteDownloadFilePathFolder);
			(new File(absoluteDownloadFilePathFolder)).mkdirs();

			InputStream contentInputStream = remoteAccesser.getRemoteFileStream(remoteFileURL);

			System.out.println("received in stream..." + remoteFileURL);

			System.out.println("absoluteDownloadFilePath..."
					+ absoluteDownloadFileName);

			commons.storeInStream(contentInputStream, absoluteDownloadFileName);

			System.out.println("received in file..." + absoluteDownloadFileName
					+ commons.getFileNameFromURL(remoteFileURL,rootPojo.fileSeparator));

			ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
			artifactMover.downloadTo(absoluteDownloadFileName, subscribedERLpojoList.get(erlCount));

			subscribedERLpojoList.get(erlCount).downLoadedArtifactTimeStamp = subscribedERLpojoList.get(erlCount).uploadedTimeStamp;

			catelogPersistenceManager.replaceSubscription(subscribedERLpojoList
					.get(erlCount), ERLDownload.AvailableStatus);

			int maxLocalVerionNumber = catelogPersistenceManager.
					getMaxDBVersionNumberOfSelfAuthoredArtifact(
							subscribedERLpojoList
							.get(erlCount).artifactKeyPojo);

			if (maxLocalVerionNumber > -1) {
				catelogPersistenceManager.updateOlderArtifact(
					subscribedERLpojoList
					.get(erlCount).artifactKeyPojo,
					SelfAuthoredArtifactpojo.ArtifactStatusOutdated,
					maxLocalVerionNumber+1); // set all local versions outdated including the max
			}
			
		}

		System.out.println("Content Download Successful");
	}
	
	public void downloadRemarksFilesForOneRoot()
	throws IOException, ClassNotFoundException, ParseException {

		ArrayList<ERLDownload> erlpojoList = null;
			
		ArrayList<ERLDownload> subscribedERLpojoList = null;

		erlpojoList = catelogPersistenceManager.readERLForRemarksDownload(rootPojo.rootNick);
		System.out.println("downloadRemarksFilesForOneRoot erlpojoList size = " + erlpojoList.size());
		
		subscribedERLpojoList = getUpdatedRemarksERLpojoList(erlpojoList);
		
		for (int erlCount = 0; erlCount < subscribedERLpojoList.size(); erlCount++) {
			System.out.println("Downloading the Subscribed content " + subscribedERLpojoList.get(erlCount).artifactKeyPojo.artifactName);
		
			String timeStamp = commons.getCurrentTimeStamp();
			System.out.println("at 1111");
			System.out.println("timeStamp " + timeStamp);
		
			System.out.println("inerlpojoList of  "
					+ subscribedERLpojoList.get(erlCount).reviewFileName);

			String remoteFileURL = commons.getRemotePathFileName(rootPojo.rootString,
					subscribedERLpojoList.get(erlCount).artifactKeyPojo.relevance,
					subscribedERLpojoList.get(erlCount).reviewFileName, rootPojo.fileSeparator);

			System.out.println("remoteFileURL aaa..."
					+ remoteFileURL);

			String absoluteDownloadRwFilePath = commons.getFullLocalPathFileNameOfDownloadedReview(
					rootPojo.rootNick, 
					subscribedERLpojoList.get(erlCount).artifactKeyPojo.relevance,
					subscribedERLpojoList.get(erlCount).reviewFileName);

			System.out.println("absoluteDownloadRwFilePath = "
					+ absoluteDownloadRwFilePath);

			String absoluteDownloadRwFilePathFolder = commons.getFullLocalPathFileNameOfDownloadedReview(
					rootPojo.rootNick, 
					subscribedERLpojoList.get(erlCount).artifactKeyPojo.relevance,
					"");
			
			System.out.println("absoluteDownloadRwFilePathFolder = "
					+ absoluteDownloadRwFilePathFolder);
		
			(new File(absoluteDownloadRwFilePathFolder)).mkdirs();

			System.out.println("remoteFileURL bbb..."
					+ remoteFileURL);
			
			InputStream contentInputStream = remoteAccesser.getRemoteFileStream(remoteFileURL);
		
			System.out.println("received in stream..." + remoteFileURL);
		
			System.out.println("downloadFilewithfullpath..."
					+ absoluteDownloadRwFilePath);
		
			commons.storeInStream(contentInputStream, absoluteDownloadRwFilePath);
		
			System.out.println("received in file..." + absoluteDownloadRwFilePath);

			subscribedERLpojoList.get(erlCount).downLoadedReviewTimeStamp = subscribedERLpojoList.get(erlCount).reviewTimeStamp;
			subscribedERLpojoList.get(erlCount).downLoadedReviewFile = subscribedERLpojoList.get(erlCount).reviewFileName;
			
			catelogPersistenceManager.updateSubscriptionRemarksDownloadTS(subscribedERLpojoList.get(erlCount));
		}

		System.out.println("Content Download Successful");
	}

	private ArrayList<ERLDownload> getSubscribedERLpojoList(
			ArrayList<ERLDownload> inErlpojoList) {
		ArrayList<ERLDownload> subscribedERLpojoList = new ArrayList<ERLDownload>();
		for (int erlpojoCount = 0; erlpojoCount < inErlpojoList.size(); erlpojoCount++) {
			if (inErlpojoList.get(erlpojoCount).subscriptionStatus
					.equalsIgnoreCase(ERLDownload.CURRENTLY_SUBSCRIBED)) {
				subscribedERLpojoList.add(inErlpojoList.get(erlpojoCount));
			}
		}
		return subscribedERLpojoList;
	}

	private ArrayList<ERLDownload> getUpdatedRemarksERLpojoList(
			ArrayList<ERLDownload> inErlpojoList) throws ParseException {
		ArrayList<ERLDownload> updatedRemarksERLpojoList = new ArrayList<ERLDownload>();
		System.out.println("inErlpojoList.get(erlpojoCount).size = " + inErlpojoList.size());

		//for (int erlpojoCount = 0; erlpojoCount < inErlpojoList.size(); erlpojoCount++) {
		//	System.out.println("erlpojoCount = " + erlpojoCount);
		//	System.out.println("inErlpojoList.get(erlpojoCount).contentName = " + inErlpojoList.get(erlpojoCount).artifactKeyPojo.artifactName);
		//	System.out.println("inErlpojoList.get(erlpojoCount).SubscriptionStatus = " + inErlpojoList.get(erlpojoCount).subscriptionStatus);
		//	
		//	if (inErlpojoList.get(erlpojoCount).subscriptionStatus
		//			.equalsIgnoreCase(ERLDownload.CURRENTLY_SUBSCRIBED) ||
		//		inErlpojoList.get(erlpojoCount).subscriptionStatus
		//			.equalsIgnoreCase(ERLDownload.AvailableStatus)
		//		)
		//	{
		//		updatedRemarksERLpojoList.add(inErlpojoList.get(erlpojoCount));
		//		System.out.println(inErlpojoList.get(erlpojoCount).artifactKeyPojo.artifactName + "'s remarks will be downloaded");
		//	}
		//}

		for (ERLDownload erlWithRemark : inErlpojoList) {
			System.out.println("erlWithRemark.contentName = " + erlWithRemark.artifactKeyPojo.artifactName);
			System.out.println("erlWithRemark.SubscriptionStatus = " + erlWithRemark.subscriptionStatus);
			
			if ((erlWithRemark.subscriptionStatus
					.equalsIgnoreCase(ERLDownload.CURRENTLY_SUBSCRIBED) ||
					erlWithRemark.subscriptionStatus
					.equalsIgnoreCase(ERLDownload.AvailableStatus)
				) && 
				(erlWithRemark.downLoadedReviewTimeStamp == null ||
						erlWithRemark.downLoadedReviewTimeStamp.equals("") ||
						commons.isThisLeftDateLater(commons.getDateFromString(erlWithRemark.reviewTimeStamp), 
													commons.getDateFromString(erlWithRemark.downLoadedReviewTimeStamp))))
			{
				updatedRemarksERLpojoList.add(erlWithRemark);
				System.out.println(erlWithRemark.artifactKeyPojo.artifactName + "'s remarks will be downloaded");
			} else {
				System.out.println(erlWithRemark.artifactKeyPojo.artifactName + "'s remarks will NOT be downloaded as its not new");
				
			}
			
		}
		
		//commons.isThisLeftDateLater(inLeftDate, inRightDate))
			
		
		return updatedRemarksERLpojoList;
	}	
}