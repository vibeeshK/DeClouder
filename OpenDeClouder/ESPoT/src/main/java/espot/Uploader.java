package espot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import commonTechs.CommonTechs;

public class Uploader {
	/*
	 * Helps to upload the contents and while doing so creates and uploads a request file as well
	 */
	final static String LOCKING_RESOURCE = "LOCK";
	final static String FILEUNIQUEAPPENDSTUB = "_";
	private CatelogPersistenceManager catelogPersistenceManager;
	private Commons commons;
	private RemoteAccesser remoteAccesser;
	private RootPojo rootPojo = null;
	private CommonData commonData = null;

	private static HashMap<String,ArrayList<String>> namesCreatedAtTmSeconds;
	private static synchronized String checkSetUniqueNameForTmSec(String inCurrentTS, String inName){
		if (namesCreatedAtTmSeconds == null) {
			namesCreatedAtTmSeconds = new HashMap<String,ArrayList<String>>();
			System.out.println("new namesCreatedAtTmSecond set");
		}
		CommonTechs.removeAllExceptSpecified(namesCreatedAtTmSeconds, inCurrentTS);
		ArrayList<String> namesAtTS = namesCreatedAtTmSeconds.get(inCurrentTS);
		System.out.println("At checkSetUniqueNameForTmSec inCurrentTS is " + inCurrentTS);
		System.out.println("At checkSetUniqueNameForTmSec inName is " + inName);		
		if (namesAtTS == null) {
			namesAtTS = new ArrayList<String>();
			namesCreatedAtTmSeconds.put(inCurrentTS, namesAtTS);
		}
		System.out.println("At checkSetUniqueNameForTmSec namesAtTS " + namesAtTS.size());
		String uniqueNameForTmSec = CommonTechs.setUniqueSubfix(inName, namesAtTS, FILEUNIQUEAPPENDSTUB);

		System.out.println("At checkSetUniqueNameForTmSec namesCreatedAtTmSecond is " + namesCreatedAtTmSeconds);
		System.out.println("At checkSetUniqueNameForTmSec namesCreatedAtTmSecond size is " + namesCreatedAtTmSeconds.size());
		System.out.println("At checkSetUniqueNameForTmSec namesCreatedAtTmSecond.get(inCurrentTS) is " + namesCreatedAtTmSeconds.get(inCurrentTS));
		System.out.println("At checkSetUniqueNameForTmSec namesCreatedAtTmSecond.get(inCurrentTS) size is " + namesCreatedAtTmSeconds.get(inCurrentTS).size());
		
		return uniqueNameForTmSec;
	}

	public Uploader(CommonData inCommonData, RemoteAccesser inRemoteAccesser) {
		commonData = inCommonData;
		commons = commonData.getCommons();
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		rootPojo = commonData.getCurrentRootPojo();
		remoteAccesser = inRemoteAccesser;
	}

	private void prepUpBatchUploads(){
		ArrayList<SelfAuthoredArtifactpojo> artfictsToBeBatchUploaded = null;
		ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);

		artfictsToBeBatchUploaded = catelogPersistenceManager.readArtfictsWithGivenStatusForOneRoot(rootPojo.rootNick, SelfAuthoredArtifactpojo.ArtifactStatusToBeBatchUploaded);
		if (artfictsToBeBatchUploaded == null) return;
		for (SelfAuthoredArtifactpojo draft : artfictsToBeBatchUploaded) {
			ContentHandlerSpecs contentHandlerSpecs = commonData.getContentHandlerSpecsMap().get(draft.artifactKeyPojo.contentType);
			artifactMover.prepForUpload(draft,contentHandlerSpecs.extension);
			if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
				ErrorHandler.showErrorAndQuit(commons, "Error17 at Uploader artifactMover ");
			}
			catelogPersistenceManager
				.updateArtifactStatus(
						draft,
						SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
		}
		return;
	}
	public void uploadArtifactsOfOneRoot()
			throws IOException,
			TransformerConfigurationException, TransformerException,
			ParserConfigurationException, ClassNotFoundException {
		prepUpBatchUploads();
		ArrayList<SelfAuthoredArtifactpojo> selfAuthoredArtifactpojoList = null;
		selfAuthoredArtifactpojoList = catelogPersistenceManager
											.readArtfictsWithGivenStatusForOneRoot(rootPojo.rootNick, SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
		for (int k = 0; k < selfAuthoredArtifactpojoList.size(); k++) {
			String currentTS = commons.getCurrentTimeStamp();
			System.out.println("at 1111b");
			System.out.println("currentTS:" + currentTS );

			SelfAuthoredArtifactpojo selfAuthoredArtifactpojo = selfAuthoredArtifactpojoList.get(k);
			File localFileObject = new File(
					selfAuthoredArtifactpojo.LocalFileName);
			String localFileName = localFileObject.getName();
			String remoteFileNameWithTS = localFileName + "_" + currentTS ;
			String remoteContentDropPathFile = commons.getRemoteContentDropFileName(rootPojo.rootString,remoteFileNameWithTS,rootPojo.fileSeparator,commons.userName);
			remoteContentDropPathFile = remoteContentDropPathFile
					.replaceAll(" ", "%20");
			System.out.println("remoteContentFileString = "
					+ remoteContentDropPathFile);

			String fullLocalPathOfNewArtifactFile = commons.getFullLocalPathFileNameOfNewArtifact(rootPojo.rootNick, selfAuthoredArtifactpojo.artifactKeyPojo.relevance, selfAuthoredArtifactpojo.LocalFileName);

			RequestPojo requestPojo = new RequestPojo(
				selfAuthoredArtifactpojo.artifactKeyPojo.relevance,
				selfAuthoredArtifactpojo.artifactKeyPojo.artifactName,
				"",	// itemName
				"",	// ERLStatus
				"",	// ReviewID
				selfAuthoredArtifactpojo.artifactKeyPojo.contentType,
				//commons.userName,				
				//selfAuthoredArtifactpojo.author,
				commons.userName, 					// Requester of this request
				selfAuthoredArtifactpojo.author,	// Author
				remoteFileNameWithTS,
				currentTS,
				RequestPojo.ARTIFACT
				);
			//16Aug2019: Included subfix instead of time delay for unique names
			//try {
			//	// Apply a 1 second delay to ensure unique request names
			//	// this is a temporary workaround. TO BE Replaced with the right solution				
			//	commons.delay(1000);
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//	commons.logger.info("delay1 interrupted");
			//}
			//selfAuthoredArtifactpojo.ReqRespFileName = commons.getNewRequestFileName();
			selfAuthoredArtifactpojo.ReqRespFileName 
					= checkSetUniqueNameForTmSec(currentTS, commons.getNewRequestFileNameForTS(currentTS));

			System.out.println("At uploadArtifactsOfOneRoot namesCreatedAtTmSecond.get(currentTS) is : " + namesCreatedAtTmSeconds.get(currentTS));

			System.out.println("At uploadArtifactsOfOneRoot namesCreatedAtTmSecond " + namesCreatedAtTmSeconds.size());
			System.out.println("At uploadArtifactsOfOneRoot currentTS " + currentTS);
			System.out.println("At uploadArtifactsOfOneRoot namesCreatedAtTmSecond.get(currentTS) size " + namesCreatedAtTmSeconds.get(currentTS).size());
			
			uploadContenAndRequestFiles(rootPojo, fullLocalPathOfNewArtifactFile, remoteContentDropPathFile,
											requestPojo, selfAuthoredArtifactpojo.ReqRespFileName);

			selfAuthoredArtifactpojo.draftingState = selfAuthoredArtifactpojo.ArtifactStatusUploaded;
			System.out.println("at 3333b");
			catelogPersistenceManager.updateArtifact(selfAuthoredArtifactpojo);
			System.out.println("at 3333bb");
		}
		System.out.println("Upload of artifacts successful");
	}

	public void uploadReviewsOfOneRoot()
			throws IOException,
			TransformerConfigurationException, TransformerException,
			ParserConfigurationException, ClassNotFoundException, SAXException {

		ArrayList<ClientSideNew_ReviewPojo> itemsWithReviewToBeUploaded = null;
		itemsWithReviewToBeUploaded = catelogPersistenceManager.readItemsWithReviewToBeUploaded(rootPojo.rootNick);

		for (int k = 0; k < itemsWithReviewToBeUploaded.size(); k++) {
			String currentTS = commons.getCurrentTimeStamp();
			System.out.println("at 1111b");
			System.out.println("currentTS:" + currentTS );

			ClientSideNew_ReviewPojo reviewPojo = itemsWithReviewToBeUploaded.get(k);

			String reviewFullFileName = commons.getFullLocalPathFileNameOfNewReview(rootPojo.rootNick,
					reviewPojo.artifactKeyPojo.relevance,
					reviewPojo.reviewFileName);
			System.out.println("uploadArtifactsOfOneRoot reviewFullFileName="+reviewFullFileName);
			File localFileObject = new File(reviewFullFileName);
			String remoteFileNameWithTS = reviewPojo.reviewFileName + "_" + currentTS ;
			//16Aug2019 Not tested yet. Included subfix instead of time delay for unique names
			//try {
			//	//apply a 1 second delay to ensure unique request names
			//	commons.delay(1000);
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//	commons.logger.info("delay2 interrupted");
			//}
			//reviewPojo.reqRespFileName = commons.getNewRequestFileName();
			reviewPojo.reqRespFileName
					= checkSetUniqueNameForTmSec(currentTS, commons.getNewRequestFileNameForTS(currentTS));

			System.out.println("At uploadReviewsOfOneRoot namesCreatedAtTmSecond " + namesCreatedAtTmSeconds.size());
			System.out.println("At uploadReviewsOfOneRoot currentTS " + currentTS);
			System.out.println("At uploadReviewsOfOneRoot namesCreatedAtTmSecond.get(currentTS) size " + namesCreatedAtTmSeconds.get(currentTS).size());
			System.out.println("At uploadReviewsOfOneRoot namesCreatedAtTmSecond.get(currentTS) elements are : " + namesCreatedAtTmSeconds.get(currentTS));
			
			String remoteContentDropPathFile = commons.getRemoteContentDropFileName(rootPojo.rootString,remoteFileNameWithTS,rootPojo.fileSeparator,commons.userName);
			remoteContentDropPathFile = remoteContentDropPathFile
					.replaceAll(" ", "%20");
			System.out.println("remoteContentFileString = "
					+ remoteContentDropPathFile);
			System.out.println("itemPojo.localPathFileName = "
					+ reviewPojo.reviewFileName);

			System.out.println("uploadArtifactsOfOneRoot reviewFullFileName="+reviewFullFileName);
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document reviewXMLDocument = dBuilder.parse(reviewFullFileName);

			String reviewID = ((Element) reviewXMLDocument.getElementsByTagName("ArtifactItemReview").item(0)).getAttribute("ReviewID");
		
			RequestPojo requestPojo = new RequestPojo(
					reviewPojo.artifactKeyPojo.relevance,
					reviewPojo.artifactKeyPojo.artifactName,
					reviewPojo.itemName,
					"",					// ERLStatus
					reviewID,
					reviewPojo.artifactKeyPojo.contentType, 
					commons.userName, 	// Requester of this request
					"",					// Author
					remoteFileNameWithTS, 
					currentTS, 
					RequestPojo.REVIEW
					);

			uploadContenAndRequestFiles(rootPojo, reviewFullFileName, remoteContentDropPathFile,requestPojo, reviewPojo.reqRespFileName);
			reviewPojo.processStatus = ClientSideNew_ReviewPojo.UPLOADED;
			System.out.println("at 3333b");
			catelogPersistenceManager.updateReviewProcessStatus(reviewPojo);
			System.out.println("uploaded successfully");
			System.out.println("at 3333bb");
		}
		System.out.println("Upload of artifacts successful");
	}
	
	private void uploadContenAndRequestFiles(RootPojo inRootPojo, String inLocalPathFileName, String inRemoteContentDropPathFile, RequestPojo inRequestPojo, String inReqRespFileName) throws IOException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
		byte[] localInputFileByteStream = commons.getFilebyteDataFromFileName(inLocalPathFileName);
		System.out.println("At start of uploadContenAndRequestFiles ");
		System.out.println("before Put of Artifact inLocalPathFileName is " + inLocalPathFileName);
		remoteAccesser.put(inRemoteContentDropPathFile, localInputFileByteStream);
		System.out.println("after Put of Artifact");
		String requestString = commons.getStringFromJson(inRequestPojo);

		System.out.println("requestString = " + requestString);
		String remoteRequestPathFile = commons.getRemoteRequestPathFile(inRootPojo.rootString,inReqRespFileName,inRootPojo.fileSeparator);
		System.out.println("inRequestPojo itemName = " + inRequestPojo.itemName);
		System.out.println("inRequestPojo reviewID = " + inRequestPojo.reviewID);
		System.out.println("remoteRequestPathFile = " + remoteRequestPathFile);
		System.out.println("before Putting Request");
		remoteAccesser.put(remoteRequestPathFile, requestString.getBytes());
		System.out.println("after Putting Request");
	}
}