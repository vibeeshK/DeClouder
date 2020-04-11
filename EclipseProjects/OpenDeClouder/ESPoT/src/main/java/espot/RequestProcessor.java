package espot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class RequestProcessor {

	/* Request processor processes the requests submitted by the contributors to progress their content or remark.
	 * The requests are in the form of json files and are stored in the request drop box of remote content repository 
	 * tools (viz Google drive, WebDAV enabled doc repositories).
	 * 
	 * The ESPoT server reads and processes the requests from remote. To handle the network failures it uses 
	 * ReqProcTracking file to note down the last update done to any file/db.
	 * 
	 * To prevent cluttering of old content in the repository, whenever a new content arrives the oldest is moved to archive.
	 * if it crosses the the specified limit 
	 */
	
	private RootPojo rootPojo = null;
	private HashMap<String, ContentHandlerSpecs> contentHandlerSpecsMap = null;
	private CatelogPersistenceManager catelogPersistenceManager;
	private Commons commons;
	private CommonData commonData = null;			
	private RemoteAccesser remoteAccesser;
	ReqProcDocPojo reqProcTracking = null;

	private String reqProcTrackingPathFileName;
	private String erlVersionDocPathFileName;
	ERLVersionDocPojo erlVersionDetail;
	
	public RequestProcessor(CommonData inCommonData,RemoteAccesser inRemoteAccesser) {
		commonData = inCommonData;
		rootPojo = commonData.getCurrentRootPojo();
		commons = commonData.getCommons();
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		contentHandlerSpecsMap = commonData.getContentHandlerSpecsMap();
		remoteAccesser = inRemoteAccesser;
		System.out.println("on creation of requestprocessor sardine = "
				+ remoteAccesser);

		reqProcTrackingPathFileName = 	
				commons.getLocalReqTrackingPathFile(rootPojo.rootNick);
		System.out.println("reqProcTrackingPathFileName = "
				+ reqProcTrackingPathFileName);
		
		try {
			reqProcTracking = (ReqProcDocPojo) commonData.getCommons().getJsonDocFromFile(	
													reqProcTrackingPathFileName,ReqProcDocPojo.class);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in RequestProcessor constr1", e);
		}

		if (reqProcTracking == null) {
			reqProcTracking = new ReqProcDocPojo();
		}
		
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

	public void processRequestsOfOneRoot() throws TransformerConfigurationException, IOException, TransformerException, ParserConfigurationException, ClassNotFoundException {

		RequestPojo requestPojo = null;

		String remoteDropBox = commons
				.getRemoteDropBox(rootPojo.rootString,rootPojo.fileSeparator);

		System.out.println("remoteDropBox = " + remoteDropBox);
		System.out.println("before listing in reqprocessor remoteAccesser = "
				+ remoteAccesser);

		ArrayList<String> resourcesStringList = remoteAccesser.getRemoteList(remoteDropBox);

		reqProcTracking.dbTobeRenewed = false;
		for (int resourceCount = 0; resourceCount < resourcesStringList.size(); resourceCount++) {
			String requestFile;
			String requestFileExtension;

			System.out.println("in loop of resources current resourceCount : " + resourceCount);
			System.out.println("in loop of resources current total resourceCount : " + resourcesStringList.size());
			System.out.println("before receive of resourceCount : " + resourceCount);

			// Test for right file
			requestFileExtension = commons.getFileExtention(resourcesStringList.get(
					resourceCount).toString());
			// skipping files with other extensions
			if (!requestFileExtension.equalsIgnoreCase(Commons.REQUESTFILE_EXTN)) {
				System.out.println("skipping nonjson : "
						+ resourcesStringList.get(resourceCount).toString());
				continue;
			}
			RequestProcesserPojo requestProcesserPojo = null;

			reqProcTracking.dbTobeRenewed = true;
			System.out.println("dbRenewed = " + reqProcTracking.dbTobeRenewed);
			System.out.println("1 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);

			// Build requestpojo
			String reqFileNameFromURL = commons.getFileNameFromURL(resourcesStringList.get(
													resourceCount).toString(),rootPojo.fileSeparator);
			System.out.println("fileExtension = " + requestFileExtension);
			requestFile = remoteDropBox + rootPojo.fileSeparator + reqFileNameFromURL;
			requestFile = requestFile.replaceAll(" ", "%20");			
			System.out.println("requestFile = " + requestFile);

			ReqTrackItem reqTrackItem = reqProcTracking.reqTrackItems.get(reqFileNameFromURL);

			ERLVersioningDocItem erlVersioningDocItem = null;

			System.out.println("1.1 At processRequestsOfOneRoot reqTrackItem = " + reqTrackItem);
			
			//boolean recordErrored = false;			
			//String errorMessage = "";
			
			if (reqTrackItem == null || !reqTrackItem.artifactMoveComplete) {

				InputStream requestDocInputStream = remoteAccesser.getRemoteFileStream(requestFile);
				requestPojo = (RequestPojo) commons.getJsonDocFromInputStream(requestDocInputStream, RequestPojo.class);
				requestDocInputStream.close(); // its important to close here to avoid getting the file locked during moves
				
				requestProcesserPojo = new RequestProcesserPojo(requestPojo);
				
				reqTrackItem = new ReqTrackItem(requestProcesserPojo);
				reqProcTracking.reqTrackItems.put(reqFileNameFromURL,reqTrackItem);

				System.out.println("2 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);
				System.out.println("2 At processRequestsOfOneRoot reqTrackItem.requestProcesserPojo = " + reqTrackItem.requestProcesserPojo);

				System.out.println("before calling remoteDropfilename requestPojo.author is " + requestPojo.author);
				System.out.println("before calling remoteDropfilename requestPojo.contentFileName is " + requestPojo.contentFileName);
				//requestProcesserPojo.incomingContentFullPath = commons.getRemoteContentDropFileName(rootPojo.rootString,requestPojo.contentFileName,rootPojo.fileSeparator,requestPojo.requestor);
				requestProcesserPojo.incomingContentFullPath = commons.getRemoteContentDropFileName(rootPojo.rootString,requestPojo.contentFileName,rootPojo.fileSeparator,requestPojo.requestor);
			
				// check for user's active status and content file availability
				// if not error the record
				
				if (requestPojo.requestor == null ||
					commonData.getUsersHandler().getUserDetailsFromRootSysLoginID(requestPojo.requestor.toUpperCase()) == null ||
					!commonData.getUsersHandler().getUserDetailsFromRootSysLoginID(requestPojo.requestor.toUpperCase()).isActive()) {
					
					reqTrackItem.errored = true;
					reqTrackItem.errorMessage = "Request coming from inactive user " + requestPojo.requestor;

					commons.putJsonDocToFile(reqProcTrackingPathFileName,reqProcTracking);
					System.out.println("2.01 At processRequestsOfOneRoot req tracker written");
					
				} else if (!remoteAccesser.exists(requestProcesserPojo.incomingContentFullPath)) {
					reqTrackItem.errored = true;
					reqTrackItem.errorMessage = "Content not found " + requestProcesserPojo.incomingContentFullPath;

					commons.putJsonDocToFile(reqProcTrackingPathFileName,reqProcTracking);
					System.out.println("2.02 At processRequestsOfOneRoot req tracker written");
				}
				
				if (!reqTrackItem.errored) {
					System.out.println("requestProcesserPojo.incomingContentFullPath is " + requestProcesserPojo.incomingContentFullPath);
	
					requestProcesserPojo.contentHandlerSpecs = contentHandlerSpecsMap.get(requestPojo.contentType);				
		
					System.out.println("@@0 requestProcesserPojo.requestPojo.contentPathFile=" + requestProcesserPojo.requestPojo.contentFileName);
					System.out.println("chk1 requestPojo.contenType = " + requestPojo.contentType);
	
					System.out.println("before creating finalArtifactKeyPojo requestProcesserPojo.requestPojo.relevance is " + requestProcesserPojo.requestPojo.relevance);
					System.out.println("CheckSlash before creating finalArtifactKeyPojo requestProcesserPojo.requestPojo.relevance is " + requestProcesserPojo.requestPojo.relevance);
					System.out.println("before creating finalArtifactKeyPojo requestProcesserPojo.contentHandlerSpecs.contentType is " + requestProcesserPojo.contentHandlerSpecs.contentType);
					System.out.println("CheckSlash rootPojo.fileSeparator is " + rootPojo.fileSeparator);
		
					ArtifactKeyPojo finalArtifactKeyPojo = requestProcesserPojo.contentHandlerSpecs.getFinalArtifactKeyPojo(
							rootPojo.rootNick, 
							requestProcesserPojo.requestPojo.relevance, 
							requestProcesserPojo.requestPojo.artifactName, 
							rootPojo.fileSeparator);
	
					System.out.println("CheckSlash after creating finalArtifactKeyPojo requestProcesserPojo.requestPojo.relevance is " + requestProcesserPojo.requestPojo.relevance);
					System.out.println("CheckSlash after creating finalArtifactKeyPojo relevance is " + finalArtifactKeyPojo.relevance);
					System.out.println("finalArtifactKeyPojo is " + finalArtifactKeyPojo);
					System.out.println("finalArtifactKeyPojo relevance is " + finalArtifactKeyPojo.relevance);
					System.out.println("finalArtifactKeyPojo artifactName is " + finalArtifactKeyPojo.artifactName);
					System.out.println("finalArtifactKeyPojo contentType is " + finalArtifactKeyPojo.contentType);
					
		
					requestProcesserPojo.prevERLPojo = catelogPersistenceManager.readERL(finalArtifactKeyPojo);
					
					//1) Subfix the ERL with the timestamp and get the new ERL name
					//2) Update the ERL file with the new filename
					//3) Archive the incoming Content & Request files into archive folder
					//4) Insert records into ToBeDeletedFiles table with the prev content, req, in-came content files
					//5) Create Response xml and save it into the Response file with name including the request details
					//6) in case of special handlers, the relevance could be a rollup location
					//	& contentfile could be created in a workfolder
	
					System.out.println("@@1asdf RequestProcesser requestPojo.artifactOrReview =" + requestPojo.artifactOrReview);
	
					String erlVersioningItemKey = ERLVersioningDocItem.getERLVersioningItemKey(
							finalArtifactKeyPojo.relevance,
							finalArtifactKeyPojo.artifactName,
							requestPojo.artifactOrReview);
	
					erlVersioningDocItem = erlVersionDetail.erlVersionTrackItems.get(erlVersioningItemKey);				
					if (erlVersioningDocItem == null) {
						erlVersioningDocItem = new ERLVersioningDocItem();
						erlVersionDetail.erlVersionTrackItems.put(erlVersioningItemKey,erlVersioningDocItem);					
					}
	
					System.out.println("3 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);
	
					if (requestPojo.artifactOrReview.equalsIgnoreCase(RequestPojo.ARTIFACT)) {
						//String newContentFileName = 
						//		finalArtifactKeyPojo.artifactName
						//		+ "_"
						//		+ commons.getCurrentTimeStamp()
						//		+ requestProcesserPojo.contentHandlerSpecs.extension;
						//
						//System.out.println("requestPojo.contentFile...="
						//		+ requestProcesserPojo.requestPojo.contentFileName);
						//System.out.println("requestProcesserPojo.requestPojo.uploadedTimeStamp...="
						//		+ requestProcesserPojo.requestPojo.uploadedTimeStamp);
						//
						//System.out.println("requestProcesserPojo.contentHandlerSpecs.extension ="
						//		+ requestProcesserPojo.contentHandlerSpecs.extension);
						//System.out.println("check if zip is present newContentFileName ="
						//		+ newContentFileName);
						//
						//requestProcesserPojo.newERLPojo = new ERLpojo(finalArtifactKeyPojo,
						//				//requestProcesserPojo.requestPojo.requestor,
						//				(requestProcesserPojo.prevERLPojo!=null?
						//						requestProcesserPojo.prevERLPojo.requestor
						//						:requestProcesserPojo.requestPojo.requestor),
						//				//requestProcesserPojo.requestPojo.author,
						//				(requestProcesserPojo.prevERLPojo!=null?
						//						requestProcesserPojo.prevERLPojo.author
						//						:requestProcesserPojo.requestPojo.author),
						//				requestProcesserPojo.contentHandlerSpecs.hasSpecialHandler,
						//				requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.reviewFileName:"",
						//
						//				//requestProcesserPojo.requestPojo.erlStatus,
						//				//rollupAddup parents cannot take their child status.
						//				//Inactive parents will become active if there is any child activity
						//				requestProcesserPojo.contentHandlerSpecs.rollupAddupType?
						//					(requestProcesserPojo.prevERLPojo != null
						//						&& !requestProcesserPojo.prevERLPojo.erlStatus.
						//							equals(ERLpojo.ERLSTAT_INACTIVE)?
						//						requestProcesserPojo.prevERLPojo.erlStatus:"")
						//					:requestProcesserPojo.requestPojo.erlStatus,											
						//				newContentFileName,	//inContentFileName
						//				requestProcesserPojo.requestPojo.uploadedTimeStamp,	//Content TimeStamp
						//				requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.reviewTimeStamp:""	// ReviewTimeStamp
						//				);
						//
						//System.out.println("at 23432 requestProcesserPojo.newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
						//
						//String newContentRemoteLocation = commons.getRemotePathFileName(rootPojo.rootString,finalArtifactKeyPojo.relevance,newContentFileName,rootPojo.fileSeparator);
						//System.out.println("@@1 newContentRemoteLocation=" + newContentRemoteLocation);
						//System.out.println("@@1 finalArtifactKeyPojo.relevance=" + finalArtifactKeyPojo.relevance);
						//System.out.println("@@1 newContentFileName=" + newContentFileName);
						//
						//System.out.println("@@1 requestProcesserPojo.requestPojo.contentPathFile=" + requestProcesserPojo.requestPojo.contentFileName);
						//System.out.println("@@1 requestProcesserPojo.incomingContentFullPath=" + requestProcesserPojo.incomingContentFullPath);
						//
						//if (!requestProcesserPojo.contentHandlerSpecs.hasSpecialHandler) {
						//	requestProcesserPojo.updatedContentFileLocation = requestProcesserPojo.incomingContentFullPath;
						//	System.out.println("@@xx1 RequestProcesserPojo");
						//	System.out.println("@@xx1 requestProcesserPojo.updatedContentFileLocation=" + requestProcesserPojo.updatedContentFileLocation);
						//	System.out.println("@@xx1 newContentRemoteLocation=" + newContentRemoteLocation);
						//	
						//	remoteAccesser.moveToRemoteLocation(requestProcesserPojo.updatedContentFileLocation, newContentRemoteLocation);
						//
						//} else {
						//	System.out.println("at 2143a requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
						//
						//	ContentHandlerInterface contentHandlerObjectInterface = ContentHandlerManager.getInstance(commons, catelogPersistenceManager, finalArtifactKeyPojo.contentType);
						//
						//	System.out.println("at 2143b requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
						//	
						//	contentHandlerObjectInterface.initializeContentHandlerWithMinimumSetup(commonData);
						//	
						//	System.out.println("at 2143c requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
						//
						//	contentHandlerObjectInterface.processContentAtWeb(rootPojo, remoteAccesser, requestProcesserPojo);
						//
						//	System.out.println("at 2143d requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
						//
						//	System.out.println("newContentRemoteLocation is " + newContentRemoteLocation);
						//
						//	remoteAccesser.putInStreamIntoRemoteLocation(newContentRemoteLocation, requestProcesserPojo.updatedContentInputStream);
						//	requestProcesserPojo.updatedContentInputStream.close();
						//	System.out.println("At request processer requestProcesserPojo.updatedContentInputStream is closed for " + requestProcesserPojo.updatedContentInputStream);
						//	
						//	String remoteContentArchiveFile = rootPojo.rootString
						//			+ rootPojo.fileSeparator + commons.remoteArchive
						//			+ rootPojo.fileSeparator
						//			+ commons.getFileNameFromURL(requestProcesserPojo.incomingContentFullPath,rootPojo.fileSeparator);
						//
						//	System.out.println("remoteContentArchiveFile...=" + remoteContentArchiveFile);
						//	remoteAccesser.moveToRemoteLocation(requestProcesserPojo.incomingContentFullPath, remoteContentArchiveFile);
						//}

						updateTargetContent(requestProcesserPojo,finalArtifactKeyPojo);
	
						erlVersioningDocItem.stackUp(requestProcesserPojo.newERLPojo.contentFileName);
						
					} else {
						// processing the remarks files.
						// a) mark current reviews file for archival
						// b) append new reviews with the old remark file and put
						// c) update ERL record to point to new reviews file
						/////
						//////////
						System.out.println("contentType = " + requestProcesserPojo.requestPojo.contentType);
						
						System.out.println("requestProcesserPojo.requestPojo.uploadedTimeStamp...="
								+ requestProcesserPojo.requestPojo.uploadedTimeStamp);
						if (requestProcesserPojo.prevERLPojo!=null) {
						System.out.println("requestProcesserPojo.prevERLPojo.uploadedTimeStamp...="
								+ requestProcesserPojo.prevERLPojo.uploadedTimeStamp);
						} else {
							System.out.println("requestProcesserPojo.prevERLPojo is null");
						}
		
						requestProcesserPojo.newERLPojo = new ERLpojo(finalArtifactKeyPojo,
								(requestProcesserPojo.prevERLPojo!=null?
										requestProcesserPojo.prevERLPojo.requestor
										:requestProcesserPojo.requestPojo.requestor),
								(requestProcesserPojo.prevERLPojo!=null?
										requestProcesserPojo.prevERLPojo.author
										:requestProcesserPojo.requestPojo.author),
								requestProcesserPojo.contentHandlerSpecs.hasSpecialHandler,
								requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.reviewFileName:"",
								requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.erlStatus:"",			//remarks do not change erl status
								requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.contentFileName:"",	 
								requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.uploadedTimeStamp:"",  //content timestamp
								requestProcesserPojo.requestPojo.uploadedTimeStamp	//Review timestamp 
							);
						
						processRemarksAtWeb(requestProcesserPojo);						
						erlVersioningDocItem.stackUp(requestProcesserPojo.newERLPojo.reviewFileName);
						
						if (requestProcesserPojo.artifactToBeUpdatedForRemarkFields){
							updateTargetContent(requestProcesserPojo,finalArtifactKeyPojo);
							erlVersioningDocItem.stackUp(requestProcesserPojo.newERLPojo.contentFileName);
						}
					}
					System.out.println("4.01 At processRequestsOfOneRoot reqTrackItem.artifactMoveComplete is " + reqTrackItem.artifactMoveComplete);
					System.out.println("4.01 At processRequestsOfOneRoot gonna write req tracker");
					reqTrackItem.artifactMoveComplete = true;
					reqTrackItem.erlVersioningDocItem = erlVersioningDocItem;
	
					commons.putJsonDocToFile(reqProcTrackingPathFileName,reqProcTracking);
					System.out.println("4.01 At processRequestsOfOneRoot req tracker written");
				}
					
			} else {
				System.out.println("5 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);
				
				requestProcesserPojo = reqTrackItem.requestProcesserPojo;
				requestPojo = requestProcesserPojo.requestPojo;
				erlVersioningDocItem = reqTrackItem.erlVersioningDocItem;

				System.out.println("5.1 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);
				System.out.println("5.1 At processRequestsOfOneRoot reqTrackItem.requestProcesserPojo = " + reqTrackItem.requestProcesserPojo);
				System.out.println("5.1 At processRequestsOfOneRoot requestPojo = " + requestPojo);
				System.out.println("5.1 At processRequestsOfOneRoot contentHandlerSpecs = " + requestProcesserPojo.contentHandlerSpecs);
				System.out.println("5.1 At processRequestsOfOneRoot erlVersioningDocItem = " + erlVersioningDocItem);
			}

			System.out.println("6 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);
			
			commonData.getCommons().putJsonDocToFile(	
					erlVersionDocPathFileName,
					erlVersionDetail);

			if (!reqTrackItem.errored && !reqTrackItem.oldestContentVersionArchived) {
				String oldestFileToBeRemoved = erlVersioningDocItem.getOldestVerFileDetail(commons.erlMaxVersions);
	
				if (oldestFileToBeRemoved!= null) {
					erlVersioningDocItem.removeOldestVerFileDetail();
					commonData.getCommons().putJsonDocToFile(	
							erlVersionDocPathFileName,
							erlVersionDetail);
					String oldestContentRemoteLocation = commons.getRemotePathFileName(
																	rootPojo.rootString,
																	requestProcesserPojo.newERLPojo.artifactKeyPojo.relevance,
																	oldestFileToBeRemoved,
																	rootPojo.fileSeparator);
					String archivalLocation = commons.getRemoteArchivalPathFileName(
																	rootPojo.rootString,
																	requestProcesserPojo.newERLPojo.artifactKeyPojo.relevance,
																	(ERLVersioningDocItem.OLD_PREFIX + oldestFileToBeRemoved),
																	rootPojo.fileSeparator);
					remoteAccesser.moveToRemoteLocation(oldestContentRemoteLocation, archivalLocation);
					reqTrackItem.oldestContentVersionArchived = true;

					System.out.println("6.a At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);
					
					commonData.getCommons().putJsonDocToFile(	
							erlVersionDocPathFileName,
							erlVersionDetail);
				}
			}

			//ContentHandlerEnds

			System.out.println("7 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);
			
			// update ERL database
			
			if (!reqTrackItem.errored && !reqTrackItem.erlMasterDBUpdated) {
				if (requestProcesserPojo.doesERLAlreadyExist()) {
					System.out.println("doesERLAlreadyExist is true and requestProcesserPojo.newERLPojo contentType" + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
	
					catelogPersistenceManager.updateERL(requestProcesserPojo.newERLPojo);
					System.out.println("doesERLAlreadyExist is true");
					
				} else {
					System.out.println("doesERLAlreadyExist is false and requestProcesserPojo.newERLPojo contentType" + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
					
					catelogPersistenceManager.insertERL(requestProcesserPojo.newERLPojo);
					System.out.println("doesERLAlreadyExist is false");
				}
				reqTrackItem.erlMasterDBUpdated = true;
				commons.putJsonDocToFile(reqProcTrackingPathFileName,reqProcTracking);
			}
			System.out.println("8 At processRequestsOfOneRoot requestProcesserPojo = " + requestProcesserPojo);

			System.out.println("@@dbRenewed check1 RequestProcesser dbRenewed =" + reqProcTracking.dbTobeRenewed);

			if (!reqTrackItem.reqRespFileUpdated) {
				String responseMessage = "";
				if (reqTrackItem.errored) {
					responseMessage = reqTrackItem.errorMessage;
				} else {
					responseMessage = "Your artifact \"" + requestPojo.artifactName + "\" of \"" 
							+ requestPojo.relevance + "\" has been processed into " 
							+ requestProcesserPojo.newERLPojo.contentFileName + "\" of \"" 
							+ requestProcesserPojo.newERLPojo.artifactKeyPojo.relevance + "\"";
				}
				uploadResponseFile(requestProcesserPojo.prevERLPojo,
						requestProcesserPojo.newERLPojo,
						responseMessage,
						reqFileNameFromURL);
				reqTrackItem.reqRespFileUpdated = true;
				commons.putJsonDocToFile(reqProcTrackingPathFileName,reqProcTracking);
			}
			
			if (!reqTrackItem.reqArchived) {
				if (!reqTrackItem.errored) {
				// archive request files
					String remoteReqArchiveFile = rootPojo.rootString
							+ rootPojo.fileSeparator + commons.remoteArchive
							+ rootPojo.fileSeparator
							+ commons.getFileNameFromURL(requestFile,rootPojo.fileSeparator);
		
					System.out.println("remoteArchiveFile...=" + remoteReqArchiveFile);
					remoteAccesser.moveToRemoteLocation(requestFile, remoteReqArchiveFile);
				} else {
				// move errored request files to errorReq folder
					String remoteErroredReqFile = rootPojo.rootString
							+ rootPojo.fileSeparator + commons.remoteErroredRequests
							+ rootPojo.fileSeparator
							+ commons.getFileNameFromURL(requestFile,rootPojo.fileSeparator);		
					System.out.println("remoteErroredRequest...=" + remoteErroredReqFile);
					remoteAccesser.moveToRemoteLocation(requestFile, remoteErroredReqFile);

					// skip the remaining process for the errored record.
					// skip the remaining process for the errored record.
					continue;	
					// skip the remaining process for the errored record.
					// skip the remaining process for the errored record.
				}				
			}
			reqTrackItem.reqArchived = true;
			reqProcTracking.reqTrackItems.remove(reqFileNameFromURL); // clean up the request item as its no longer required
			commons.putJsonDocToFile(reqProcTrackingPathFileName,reqProcTracking);
		}
		System.out.println("@@dbRenewed check2 RequestProcesser dbRenewed =" + reqProcTracking.dbTobeRenewed);

		String catalogpublishFolder = rootPojo.rootString
				+ rootPojo.fileSeparator
				+ commons.getServerSideSideCatalogDbPublishFolderOfRoot();

		System.out.println("AA At RequestProcessor Check first time run for forceSetting reqProcTracking.dbTobeRenewed = " + reqProcTracking.dbTobeRenewed);

		if (!reqProcTracking.dbTobeRenewed) {
			// if it is first time after go live of an installation, publish a copy of catalog db any way,
			// so that the client users can start their drafting

			System.out.println("AB At RequestProcessor Check first time run for forceSetting reqProcTracking.dbTobeRenewed = " + reqProcTracking.dbTobeRenewed);

			if (remoteAccesser.getRemoteList(catalogpublishFolder) == null) {
				reqProcTracking.dbTobeRenewed = true;
				// Force setting the renewal for initial run post installation
				System.out.println("AC At RequestProcessor Check first time run for forceSetting reqProcTracking.dbTobeRenewed = " + reqProcTracking.dbTobeRenewed);

			}
			System.out.println("AD At RequestProcessor Check first time run for forceSetting reqProcTracking.dbTobeRenewed = " + reqProcTracking.dbTobeRenewed);
		}
		System.out.println("AE At RequestProcessor Check first time run for forceSetting reqProcTracking.dbTobeRenewed = " + reqProcTracking.dbTobeRenewed);

		if (reqProcTracking.dbTobeRenewed) {
			// publish the new catalog database
			//String catalogpublishFile = rootPojo.rootString
			//							+ rootPojo.fileSeparator
			//							+ commons.getServerSideSideCatalogDbPublishFolderOfRoot()
			//							+ rootPojo.fileSeparator
			//							+ commons.getNewCatalogDbPublishFileName(rootPojo.rootNick);
			String catalogpublishFile = catalogpublishFolder
										+ rootPojo.fileSeparator
										+ commons.getNewCatalogDbPublishFileName(rootPojo.rootNick);
			
			System.out.println("catalogpublishFile = " + catalogpublishFile);
			remoteAccesser.uploadToRemote(catalogpublishFile, commons.getServersMasterCopyofCatalogDbLocalFileOfRoot(rootPojo.rootNick));

			System.out.println("New db published: " + catalogpublishFile);

			reqProcTracking.dbTobeRenewed = false;
			commons.putJsonDocToFile(reqProcTrackingPathFileName,reqProcTracking);

		} else {
			System.out.println("Nothing new to publish");
		}
		
		if (reqProcTracking.reqTrackItems.size() > 0) {
			commons.logger.warn(" rootNick " + rootPojo.rootNick + " has " 
				+ reqProcTracking.reqTrackItems.size()
				+ " unfinished requests. e.g. " 
				+ reqProcTracking.reqTrackItems.get(reqProcTracking.reqTrackItems.keySet().toArray()[0]));
		}
	}

	public void updateTargetContent(RequestProcesserPojo requestProcesserPojo,ArtifactKeyPojo finalArtifactKeyPojo) throws IOException{
		String newContentFileName = 
				finalArtifactKeyPojo.artifactName
				+ "_"
				+ commons.getCurrentTimeStamp()
				+ requestProcesserPojo.contentHandlerSpecs.extension;
		
		System.out.println("requestPojo.contentFile...="
				+ requestProcesserPojo.requestPojo.contentFileName);
		System.out.println("requestProcesserPojo.requestPojo.uploadedTimeStamp...="
				+ requestProcesserPojo.requestPojo.uploadedTimeStamp);

		System.out.println("requestProcesserPojo.contentHandlerSpecs.extension ="
				+ requestProcesserPojo.contentHandlerSpecs.extension);
		System.out.println("check if zip is present newContentFileName ="
				+ newContentFileName);

		if (requestProcesserPojo.requestPojo.artifactOrReview.equalsIgnoreCase(RequestPojo.ARTIFACT)) {

			requestProcesserPojo.newERLPojo = new ERLpojo(finalArtifactKeyPojo,
					(requestProcesserPojo.prevERLPojo!=null?
							requestProcesserPojo.prevERLPojo.requestor
							:requestProcesserPojo.requestPojo.requestor),
					(requestProcesserPojo.prevERLPojo!=null?
							requestProcesserPojo.prevERLPojo.author
							:requestProcesserPojo.requestPojo.author),
					requestProcesserPojo.contentHandlerSpecs.hasSpecialHandler,
					requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.reviewFileName:"",
					//rollupAddup parents cannot take their child status.
					//Inactive parents will become active if there is any child activity
					requestProcesserPojo.contentHandlerSpecs.rollupAddupType && requestProcesserPojo.prevERLPojo != null?
						requestProcesserPojo.prevERLPojo.erlStatus:"",
					newContentFileName,	//inContentFileName
					requestProcesserPojo.requestPojo.uploadedTimeStamp,	//Content TimeStamp
					requestProcesserPojo.prevERLPojo!=null?requestProcesserPojo.prevERLPojo.reviewTimeStamp:""	// ReviewTimeStamp
				);
		} else {
			// some fields of newERLPojo is already set
			
			// Though its remark upload, since the content also is being changed now hence refreshing content and its timestamp
			requestProcesserPojo.newERLPojo.contentFileName = newContentFileName;
			requestProcesserPojo.newERLPojo.uploadedTimeStamp = requestProcesserPojo.requestPojo.uploadedTimeStamp;
			//requestProcesserPojo.newERLPojo.erlStatus = requestProcesserPojo.requestPojo.erlStatus;			//remarks do not change erl status
		}

		System.out.println("at 23432 requestProcesserPojo.newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);

		String newContentRemoteLocation = commons.getRemotePathFileName(rootPojo.rootString,finalArtifactKeyPojo.relevance,newContentFileName,rootPojo.fileSeparator);
		System.out.println("@@1 newContentRemoteLocation=" + newContentRemoteLocation);
		System.out.println("@@1 finalArtifactKeyPojo.relevance=" + finalArtifactKeyPojo.relevance);
		System.out.println("@@1 newContentFileName=" + newContentFileName);

		System.out.println("@@1 requestProcesserPojo.requestPojo.contentPathFile=" + requestProcesserPojo.requestPojo.contentFileName);
		System.out.println("@@1 requestProcesserPojo.incomingContentFullPath=" + requestProcesserPojo.incomingContentFullPath);

		if (!requestProcesserPojo.contentHandlerSpecs.hasSpecialHandler) {
			requestProcesserPojo.updatedContentFileLocation = requestProcesserPojo.incomingContentFullPath;
			System.out.println("@@xx1 RequestProcesserPojo");
			System.out.println("@@xx1 requestProcesserPojo.updatedContentFileLocation=" + requestProcesserPojo.updatedContentFileLocation);
			System.out.println("@@xx1 newContentRemoteLocation=" + newContentRemoteLocation);
			
			remoteAccesser.moveToRemoteLocation(requestProcesserPojo.updatedContentFileLocation, newContentRemoteLocation);

		} else {
			System.out.println("at 2143a requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);

			ContentHandlerInterface contentHandlerObjectInterface = ContentHandlerManager.getInstance(commons, catelogPersistenceManager, finalArtifactKeyPojo.contentType);

			System.out.println("at 2143b requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);
			
			contentHandlerObjectInterface.initializeContentHandlerWithMinimumSetup(commonData);
			
			System.out.println("at 2143c requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);

			contentHandlerObjectInterface.processContentAtWeb(rootPojo, remoteAccesser, requestProcesserPojo);

			System.out.println("at 2143d requestProcesserPojo newERLPojo contentType is " + requestProcesserPojo.newERLPojo.artifactKeyPojo.contentType);

			System.out.println("newContentRemoteLocation is " + newContentRemoteLocation);

			remoteAccesser.putInStreamIntoRemoteLocation(newContentRemoteLocation, requestProcesserPojo.updatedContentInputStream);
			requestProcesserPojo.updatedContentInputStream.close();
			System.out.println("At request processer requestProcesserPojo.updatedContentInputStream is closed for " + requestProcesserPojo.updatedContentInputStream);
			
			String remoteContentArchiveFile = rootPojo.rootString
					+ rootPojo.fileSeparator + commons.remoteArchive
					+ rootPojo.fileSeparator
					+ commons.getFileNameFromURL(requestProcesserPojo.incomingContentFullPath,rootPojo.fileSeparator);

			System.out.println("remoteContentArchiveFile...=" + remoteContentArchiveFile);
			remoteAccesser.moveToRemoteLocation(requestProcesserPojo.incomingContentFullPath, remoteContentArchiveFile);			
		}		
	}

	
	
	public void processRemarksAtWeb(RequestProcesserPojo inRequestProcesserPojo) throws IOException {
		System.out.println("begin processRemarksAtWeb");

		InputStream prevFileStream;
		InputStream incomingReviewFileStream;

		Document documentToUpdate = null;
		Document incomingReviewDoc = null;
		int newInnovationNumber = 0;

		System.out.println("inRequestProcesserPojo.incomingContentFullPath=" + inRequestProcesserPojo.incomingContentFullPath);

		incomingReviewFileStream = remoteAccesser.getRemoteFileStream(inRequestProcesserPojo.incomingContentFullPath);
		try {
			incomingReviewDoc = commons.getDocumentFromXMLFileStream(incomingReviewFileStream);
		} catch (SAXException | ParserConfigurationException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in RequestProcessor processRemarksAtWeb inRequestProcesserPojo", e);
		}
			
		System.out.println("incomingDoc=" + incomingReviewDoc);
		NewReviewPojo incomingItemNewReviewPojo = new NewReviewPojo(commons, inRequestProcesserPojo.newERLPojo.artifactKeyPojo,incomingReviewDoc);

		System.out.println("inRequestProcesserPojo=" + inRequestProcesserPojo);
		System.out.println("inRequestProcesserPojo.prevERLPojo=" + inRequestProcesserPojo.prevERLPojo);
		System.out.println("inRequestProcesserPojo.prevERLPojo.artifactKeyPojo=" + inRequestProcesserPojo.prevERLPojo.artifactKeyPojo);
		System.out.println("rootPojo.rootString=" + rootPojo.rootString);
		System.out.println("inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance=" + inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance);
		System.out.println("inRequestProcesserPojo.newERLPojo.artifactKeyPojo.artifactName=" + inRequestProcesserPojo.newERLPojo.artifactKeyPojo.artifactName);
		
		String prevReviewsRemoteLocation = commons.getRemotePathFileName(rootPojo.rootString,
																	inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance,
																	inRequestProcesserPojo.prevERLPojo.reviewFileName,rootPojo.fileSeparator);
		System.out.println("prevReviewsRemoteLocation = " + prevReviewsRemoteLocation);
		ArtifactAllReviewsPojo artifactAllReviewsPojo = new ArtifactAllReviewsPojo(
													rootPojo.rootString,
													inRequestProcesserPojo.newERLPojo.artifactKeyPojo.relevance,
													inRequestProcesserPojo.newERLPojo.artifactKeyPojo.artifactName);
		if (prevReviewsRemoteLocation == null || prevReviewsRemoteLocation.isEmpty()){
			//use the incoming xml reviews as base
			try {
				artifactAllReviewsPojo.initiateArtifactReviewsDoc();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in RequestProcessor 2 processRemarksAtWeb inRequestProcesserPojo", e);
			}
		} else {
			//append incoming xml reviews	
			System.out.println("prevReviewsRemoteLocation = " + prevReviewsRemoteLocation);
			InputStream artifactAllReviewsXMLInputStream = remoteAccesser.getRemoteFileStream(prevReviewsRemoteLocation);
			Document artifactAllReviewsXMLDocument = null;
			try {
				artifactAllReviewsXMLDocument = commons.getDocumentFromXMLFileStream(artifactAllReviewsXMLInputStream);
			} catch (SAXException | ParserConfigurationException e) {
				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in RequestProcessor 3 processRemarksAtWeb inRequestProcesserPojo", e);				
			}
			
			System.out.println("artifactAllReviewsXMLDocument = " + artifactAllReviewsXMLDocument.getTextContent());
			artifactAllReviewsPojo.buildArtifactAllReviewsPojoFromFromDoc(artifactAllReviewsXMLDocument);
		}
		System.out.println("before calling appendItemReviewsDoc()");
		System.out.println("inRequestProcesserPojo.requestPojo.itemName = " + inRequestProcesserPojo.requestPojo.itemName);
		System.out.println("incomingItemNewReviewPojo.getCondensedReviewString() = " + incomingItemNewReviewPojo.getCondensedReviewString());
		
		artifactAllReviewsPojo.appendItemReviewsDoc(inRequestProcesserPojo.requestPojo.itemName, incomingItemNewReviewPojo.getCondensedReviewString());

		try {
			inRequestProcesserPojo.updatedContentInputStream = commons.getInputStreamOfXMLDoc(artifactAllReviewsPojo.artifactAllReviewsDocument);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in RequestProcessor2 constr", e);
		} catch (TransformerFactoryConfigurationError e3) {
			e3.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "TransformerFactoryConfigurationError3 in RequestProcessor constr");
		}
		
		System.out.println("Final Document : " + inRequestProcesserPojo.updatedContentInputStream);
		inRequestProcesserPojo.newERLPojo.reviewFileName =
			inRequestProcesserPojo.newERLPojo.artifactKeyPojo.artifactName
			+ "_Reviews_"
			+ commons.getCurrentTimeStamp()
			+ ".xml";
		
		String newContentRemoteLocation = commons.getRemotePathFileName(rootPojo.rootString,inRequestProcesserPojo.newERLPojo.artifactKeyPojo.relevance,inRequestProcesserPojo.newERLPojo.reviewFileName,rootPojo.fileSeparator);
		System.out.println("newContentRemoteLocation : " + newContentRemoteLocation);
		remoteAccesser.putInStreamIntoRemoteLocation(newContentRemoteLocation, inRequestProcesserPojo.updatedContentInputStream);
		inRequestProcesserPojo.updatedContentInputStream.close();
		
		//Reassignment of Requester and Author if the review record is from Requestor or Admin Privilege

		System.out.println("why null point error1 inRequestProcesserPojo is " + inRequestProcesserPojo);
		System.out.println("why null point error2 requestPojo is " + inRequestProcesserPojo.requestPojo);
		System.out.println("why null point error3 requestor is " + inRequestProcesserPojo.requestPojo.requestor);
		System.out.println("why null point error4 " + commonData.getUsersHandler());
		System.out.println("why null point error5 " + commonData.getUsersHandler().getUserDetailsFromRootSysLoginID(inRequestProcesserPojo.requestPojo.requestor));

		UserPojo requestAuthorsDetail = commonData.getUsersHandler().getUserDetailsFromRootSysLoginID(inRequestProcesserPojo.requestPojo.requestor);

		if ((incomingItemNewReviewPojo.reassignedRequestor!= null && !incomingItemNewReviewPojo.reassignedRequestor.isEmpty()) 
			|| (incomingItemNewReviewPojo.reassignedAuthor != null && !incomingItemNewReviewPojo.reassignedAuthor.isEmpty())
			|| (incomingItemNewReviewPojo.newERLStatus != null && !incomingItemNewReviewPojo.newERLStatus.isEmpty())) {

			if (inRequestProcesserPojo.contentHandlerSpecs.rollupAddupType) {
			// for rollAddTypes, this info is at item level
				inRequestProcesserPojo.artifactToBeUpdatedForRemarkFields = true;
				
				inRequestProcesserPojo.itemReassignedRequestor = incomingItemNewReviewPojo.reassignedRequestor;
				inRequestProcesserPojo.itemReassignedAuthor = incomingItemNewReviewPojo.reassignedAuthor;	
				inRequestProcesserPojo.itemNewERLStatus = incomingItemNewReviewPojo.newERLStatus;
				
			}
			else {
				
				if (requestAuthorsDetail.hasAdminPrivilege() 
						|| requestAuthorsDetail.hasTeamLeaderPrivilege() 
						|| (inRequestProcesserPojo.prevERLPojo != null 
							&& (requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.author)
								|| requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.requestor)
								|| commonData.getUsersHandler().doesUserHaveRightsOverMember(requestAuthorsDetail.rootSysLoginID, inRequestProcesserPojo.prevERLPojo.author)
						))) {
						
						if (incomingItemNewReviewPojo.reassignedRequestor != null && !incomingItemNewReviewPojo.reassignedRequestor.isEmpty()) {
							System.out.println("reassignment processing for requestor change");
							inRequestProcesserPojo.newERLPojo.requestor = incomingItemNewReviewPojo.reassignedRequestor;
						}
						if (incomingItemNewReviewPojo.reassignedAuthor != null && !incomingItemNewReviewPojo.reassignedAuthor.isEmpty()) {
							System.out.println("reassignment processing for Author change");						
							inRequestProcesserPojo.newERLPojo.author = incomingItemNewReviewPojo.reassignedAuthor;
						}
						if (incomingItemNewReviewPojo.newERLStatus != null && !incomingItemNewReviewPojo.newERLStatus.isEmpty()) {
							System.out.println("erl status upgrades processing");
							inRequestProcesserPojo.newERLPojo.erlStatus = incomingItemNewReviewPojo.newERLStatus;
						}
					}				
			//	if (!incomingItemNewReviewPojo.reassignedRequestor.isEmpty()
			//	&& (requestAuthorsDetail.hasAdminPrivilege() 
			//		|| requestAuthorsDetail.hasTeamLeaderPrivilege() 
			//		|| (inRequestProcesserPojo.prevERLPojo != null 
			//			&& (requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.author)
			//				|| requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.requestor)
			//				|| commonData.getUsersHandler().doesUserHaveRightsOverMember(requestAuthorsDetail.rootSysLoginID, inRequestProcesserPojo.prevERLPojo.author)
			//		)))) {
			//
			//		
			//		System.out.println("reassignment processing for requestor change");
			//
			//		inRequestProcesserPojo.newERLPojo.requestor = incomingItemNewReviewPojo.reassignedRequestor;				
			//	}
			//	
			//	if (!incomingItemNewReviewPojo.reassignedAuthor.isEmpty()
			//		&& (requestAuthorsDetail.hasAdminPrivilege() 
			//			|| requestAuthorsDetail.hasTeamLeaderPrivilege() 
			//			|| (inRequestProcesserPojo.prevERLPojo != null 
			//				&& (requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.author)
			//					|| requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.requestor))))) {
			//		
			//		System.out.println("reassignment processing for Author change");
			//
			//		inRequestProcesserPojo.newERLPojo.author = incomingItemNewReviewPojo.reassignedAuthor;		
			//	}
			//
			//	if (!incomingItemNewReviewPojo.newERLStatus.isEmpty() 
			//		&& (requestAuthorsDetail.hasAdminPrivilege()
			//			|| requestAuthorsDetail.hasTeamLeaderPrivilege() 
			//			|| (inRequestProcesserPojo.prevERLPojo != null
			//				&& (requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.requestor)
			//					|| requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(inRequestProcesserPojo.prevERLPojo.author))))) {
			//		System.out.println("erl status upgrades processing");
			//		System.out.println("erl status being changed from " + inRequestProcesserPojo.newERLPojo.erlStatus + " to " +  incomingItemNewReviewPojo.newERLStatus);
			//
			//		inRequestProcesserPojo.newERLPojo.erlStatus = incomingItemNewReviewPojo.newERLStatus;
			//	}
			
			// Archival of incoming remark content. Note: when item fields are to be updated, this archival is deferred and
			// taken care in the next step
				String remoteRemarkArchiveFile = rootPojo.rootString
						+ rootPojo.fileSeparator + commons.remoteArchive
						+ rootPojo.fileSeparator
						+ commons.getFileNameFromURL(inRequestProcesserPojo.incomingContentFullPath,rootPojo.fileSeparator);
				System.out.println("remoteRemarkArchiveFile...=" + remoteRemarkArchiveFile);
				remoteAccesser.moveToRemoteLocation(inRequestProcesserPojo.incomingContentFullPath, remoteRemarkArchiveFile);
			}
		}
		System.out.println("end processContentAtWeb");
	}

//	public void checkIfArtifactAlsoToBeUpdated(RequestProcesserPojo inRequestProcesserPojo){
//	// whenever a remarks field viz. status, requestor, author is changed for a child artifact 
//	// the artifact itself to be updated since the values are at item level
//		if (inRequestProcesserPojo.contentHandlerSpecs.hasSpecialHandler) {
//			inRequestProcesserPojo.artifactToBeUpdatedForRemarkFields = true;
//		}
//	}
	
	public static void main(String[] args) throws IOException, ParseException {

		ArrayList<RootMasterPojo> rootMasterPojoList = null;
		CatelogPersistenceManager catelogPersistenceManager = null;
		Commons commons = Commons.getInstance(Commons.BASE_CATALOG_SERVER);

		System.out.println("at 4");
		System.out.println("end....");
	}

	public String uploadResponseFile(
			ERLpojo inUploadedERLpojo,
			ERLpojo inProcessedERLpojo,
			String inResponseText,
			String inResponseFileName
	) throws IOException,
			TransformerConfigurationException, TransformerException,
			ParserConfigurationException {
		System.out.println("@ uploadResponseFile: remoteAccesser = " + remoteAccesser);
		System.out.println("@ uploadResponseFile: inUploadedERLpojo = " + inUploadedERLpojo);
		System.out.println("@ uploadResponseFile: inProcessedERLpojo = " + inProcessedERLpojo);
		System.out.println("@ uploadResponseFile: inResponseText = " + inResponseText);
		System.out.println("@ uploadResponseFile: inResponseFileName = " + inResponseFileName);
		System.out.println("@ uploadResponseFile: pojo.rootString = " + rootPojo.rootString);
		System.out.println("@ uploadResponseFile: responsepickbox = " + commons.getRemoteResponsePickBox(rootPojo.rootString,rootPojo.fileSeparator));
		System.out.println("@ uploadResponseFile: inResponseFileName = " + inResponseFileName);

		String remoteFileString = commons.getRemoteResponsePickBox(rootPojo.rootString,rootPojo.fileSeparator) + rootPojo.fileSeparator + inResponseFileName;
		
		
		System.out.println("remoteFileString = " + remoteFileString);

		ResponsePojo responsePojo = createResponsePojo(inUploadedERLpojo,
				inProcessedERLpojo,
				inResponseText);

		String responseString = commons.getStringFromJson(responsePojo);
		

		System.out.println("responseString = " + responseString);
		System.out.println("uploadResponseFile: remoteFileString = " + remoteFileString);

		remoteAccesser.put(remoteFileString, responseString.getBytes());
		
		return remoteFileString;
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

	//Response process ends
}