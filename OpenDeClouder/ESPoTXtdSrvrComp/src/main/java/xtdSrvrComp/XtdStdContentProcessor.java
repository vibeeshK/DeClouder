package xtdSrvrComp;

import java.util.ArrayList;

import espot.ArtifactMover;
import espot.CommonData;
import espot.Commons;
import espot.ContentHandlerSpecs;
import espot.ERLDownload;
import espot.ErrorHandler;
import espot.RemoteAccesser;
import espot.RootPojo;
import espot.SelfAuthoredArtifactpojo;

public class XtdStdContentProcessor {
	/*
	 * This processor invokes extended standard sequence on defined intervals
	 * Other more special processes extend this processor further for their needs
	 */	
	private XtdStdRtCtCatlogPersistenceManager xtdCatlogPersistenceManager = null;
	private Commons commons = null;
	private RemoteAccesser remoteAccesser = null;	
	private RootPojo rootPojo = null;
	private String contentType = null;
	private CommonData commonData = null;
	private ContentHandlerSpecs ctHandlerSpecs = null;
	private String[] dependentContentTypes = null;

	public XtdStdContentProcessor(CommonData inCommonData, String inContentType,
			RemoteAccesser inRemoteAccesser) {
		commonData = inCommonData;
		xtdCatlogPersistenceManager = (XtdStdRtCtCatlogPersistenceManager) inCommonData.getCatelogPersistenceManager(); 
		commons = commonData.getCommons();
		remoteAccesser = inRemoteAccesser;
		rootPojo  = commonData.getCurrentRootPojo();
		contentType = inContentType;
		ctHandlerSpecs = commonData.getContentHandlerSpecsMap().get(contentType);
		System.out.println("At XtdStdContentProcessor contentType is " + contentType);		
		System.out.println("At XtdStdContentProcessor ctHandlerSpecs is " + ctHandlerSpecs);
		System.out.println("At XtdStdContentProcessor ctHandlerSpecs.instructions is " + ctHandlerSpecs.instructions);
		if (!ctHandlerSpecs.instructions.equalsIgnoreCase("")) {
			dependentContentTypes = ctHandlerSpecs.cntProcInstrucDoc.dependentContentTypes;
		} else {
			dependentContentTypes = null;
		}
	}

	public void subscribeToDeckerParents(){
		System.out.println("at subscribeToDeckerParents start" );
		ArrayList<ERLDownload> dbERLDownloadsList = xtdCatlogPersistenceManager.readERLDownLoadsOfRootsSpecificContentType(contentType);
		System.out.println("at subscribeToDeckerParents 123 dbERLDownloadsList.size() is " + dbERLDownloadsList.size() );
		for (int erlCount = 0; erlCount < dbERLDownloadsList.size(); erlCount++) {
			ERLDownload erlDownload = dbERLDownloadsList.get(erlCount);
			System.out.println("at subscribeToDeckerParents 324123" );
			System.out.println("at subscribeToDeckerParents erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
			if (erlDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED)) {
				System.out.println("at subscribeToDeckerParents 543123" );
				xtdCatlogPersistenceManager.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				xtdCatlogPersistenceManager.updateDeckerXtdStdrdProc(erlDownload.artifactKeyPojo,erlDownload.uploadedTimeStamp,ERLGrouperParent.PARENT_NEW);
			} else if (erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
				System.out.println("at subscribeToDeckerParents 4577123" );
				xtdCatlogPersistenceManager.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				xtdCatlogPersistenceManager.updateDeckerXtdStdrdProc(erlDownload.artifactKeyPojo,erlDownload.uploadedTimeStamp,ERLGrouperParent.PARENT_UPDATED);
			} else {
				System.out.println("at subscribeToDeckerParents 54343523 erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
			}
			
			System.out.println("at subscribeToDeckerParents 1436723" );
		}
		System.out.println("at subscribeToDeckerParents end");
	}

	public void subscribeDependents(){
		System.out.println("at subscribeDependents start" );

		if (dependentContentTypes == null) return;		
		for (String depCtType : dependentContentTypes) {
			
			ArrayList<ERLDownload> dbERLDownloadsList = xtdCatlogPersistenceManager.readERLDownLoadsOfRootsSpecificContentType(depCtType);
			System.out.println("at subscribeToDependents 123 dbERLDownloadsList.size() is " + dbERLDownloadsList.size() );
			for (int erlCount = 0; erlCount < dbERLDownloadsList.size(); erlCount++) {
				ERLDownload erlDownload = dbERLDownloadsList.get(erlCount);
				System.out.println("at subscribeDependents 324123" );
				System.out.println("at subscribeToDependents erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
				if (erlDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED)) {
					System.out.println("at subscribeDependents 543123" );
					xtdCatlogPersistenceManager.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				} else if (erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
					System.out.println("at subscribeDependents 4577123" );
					xtdCatlogPersistenceManager.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				} else {
					System.out.println("at subscribeDependents 54343523 erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
				}
				
				System.out.println("at subscribeDependents 1436723" );
			}
		}
		System.out.println("at subscribeDependents end");
	}

	public void extendedContentProcessing() {
		System.out.println("at 32432 extendedContentProcessing1 for contentType " + contentType);
		System.out.println("at 32432 extendedContentProcessing1" );
		System.out.println("at 32432 extendedContentProcessing1" );
		ArrayList<XtdStdProcessRecord> dbUpdatedGrouperChildnParentsList = xtdCatlogPersistenceManager.readUpdatedERLXtdStdProcessTbls(rootPojo.rootNick, contentType);
		System.out.println("at 32432 extendedContentProcessing2 dbUpdatedGrouperChildnParentsList.size() " + dbUpdatedGrouperChildnParentsList.size() );
		for (int parentERLCount = 0; parentERLCount < dbUpdatedGrouperChildnParentsList.size(); parentERLCount++) {
			System.out.println("at 32432 extendedContentProcessing3 parentERLCount = " + parentERLCount);
			XtdStdProcessRecord xtdStdProcessRec = dbUpdatedGrouperChildnParentsList.get(parentERLCount);
			System.out.println("at 32432 extendedContentProcessing3 parent Artifact = " + xtdStdProcessRec.artifactKeyPojo.artifactName);
			
			ERLDownload parentERLDownload = xtdCatlogPersistenceManager.readERLDownLoad(xtdStdProcessRec.artifactKeyPojo);
			SelfAuthoredArtifactpojo parentDraft = buildLocalArtifact(parentERLDownload, false); //false since already refreshed before subscription

			ExtendedHandler extendedHandler = XtdContntHandlerManager.getInstance(commons, xtdCatlogPersistenceManager, contentType);
			extendedHandler.initializeExtendedHandlerForExtdSrvrProcess(commonData, parentDraft);

			System.out.println("at 32432 extendedContentProcessing4 erlGrouperParent.extendedChildList.size()");
			System.out.println("at 32432 extendedContentProcessing4 before calling xtd handler xtdProcStatus " + xtdStdProcessRec.parentStatus);

			String xtdProcStatus = extendedHandler.processXtdStdProcessRec(xtdStdProcessRec.parentStatus);
			
			if (xtdProcStatus.equalsIgnoreCase(XtdStdProcessRecord.ERLRecord_ERROR)) {
				System.out.println("at 32432 extendedContentProcessing4 after calling xtd handler xtdProcStatus " + xtdStdProcessRec.parentStatus);
				System.out.println("at 32432 extendedContentProcessing4 while processing artifactName " + xtdStdProcessRec.artifactKeyPojo.artifactName);
				ErrorHandler.showErrorAndQuit(commons, "Error17 while xtd of processing artifactName " + xtdStdProcessRec.artifactKeyPojo.artifactName +  " error " + xtdStdProcessRec.parentStatus);
				return;
			}

			System.out.println("at 32432 extendedContentProcessing4 xtdProcStatus " + xtdProcStatus);

			xtdCatlogPersistenceManager.updateDeckerXtdStdrdProc(xtdStdProcessRec.artifactKeyPojo,xtdStdProcessRec.parentUpdateTimeStamp,xtdProcStatus);

			System.out.println("at 32432 extendedContentProcessing4 updated xtdProcStatus " + xtdProcStatus);

			if (!xtdProcStatus.equalsIgnoreCase(XtdStdProcessRecord.ERLRecord_SKIP)) {
				ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
				System.out.println("at 32432 extendedContentProcessing4 artifactMover " + artifactMover);
				artifactMover.prepForUpload(parentDraft,commonData.getContentHandlerSpecsMap().get(contentType).extension);
				System.out.println("at 32432 extendedContentProcessing4 artifactMover.lastProcessStatus " + artifactMover.lastProcessStatus);
							
				if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
					System.out.println("at 32432 extendedContentProcessing4 artifactMover.lastProcessStatus not ok");
					ErrorHandler.showErrorAndQuit(commons, "Error17 at ArtifactWrapper artifactMover " + artifactMover.lastProcessStatus +  artifactMover.lastProcessStatus + " while prepForUpload : ");
					return;
				}
				System.out.println("at 32432 extendedContentProcessing4 artifactMover done ");
	
				xtdCatlogPersistenceManager.updateArtifactStatus(parentDraft,SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
				System.out.println("at 32432 extendedContentProcessing4 updateArtifactStatus with " + SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
			}
		}
	}
	
	public SelfAuthoredArtifactpojo buildLocalArtifact(ERLDownload inERLDownload, boolean inArtifactToBeRefreshed) {
		System.out.println("at 222221 buildLocalArtifact234324 inERLDownload.artifact : "  + inERLDownload.artifactKeyPojo.artifactName);
		System.out.println("at 222221 buildLocalArtifact234324 inERLDownload.downLoadedFile : "  + inERLDownload.downLoadedFile);
		
		//SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = xtdCatlogPersistenceManager.readSelfAuthoredArtifact(
		//		inERLDownload.artifactKeyPojo);
		//System.out.println("at 222221 buildLocalArtifact3223234324" );
		//
		//if (extdSelfAuthoredArtifactpojo != null) {
		//	// when a parent is changed and prior drafts exist, remove the old drafts
		//	xtdCatlogPersistenceManager.deleteAllSelfAuthoredArtifacts(extdSelfAuthoredArtifactpojo.artifactKeyPojo);
		//	ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
		//	artifactMover.archiveDraft(extdSelfAuthoredArtifactpojo);
		//}
		
		// clear old drafts starts
		//read all versions of drafts from db
		ArrayList<SelfAuthoredArtifactpojo> allVersionsSelfAuthoredArtifacts 
			= xtdCatlogPersistenceManager.readAllVersionsSelfAuthoredArtifacts(inERLDownload.artifactKeyPojo);

		//scroll thru allVersions, form the file name and archive
		for (SelfAuthoredArtifactpojo oneVersionOfSelfAuthoredArtifact : allVersionsSelfAuthoredArtifacts) {
			
			//delete the draft in db
			xtdCatlogPersistenceManager.deleteSelfAuthoredArtifactpojo(
					oneVersionOfSelfAuthoredArtifact);

			ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
			artifactMover.archiveDraft(oneVersionOfSelfAuthoredArtifact);

			System.out.println("draft archived successfully."
					+ " root: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.rootNick
					+ "; relevance: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.relevance
					+ "; artifact: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.artifactName
					+ "; versionNum: " + oneVersionOfSelfAuthoredArtifact.unpulishedVerNum
					+ ". draftingState: " + oneVersionOfSelfAuthoredArtifact.draftingState
			);
		}
		// clear old drafts ends
		
		
		
		
		// when a parent is new or prior drafts just got removed, insert a draft
		System.out.println("at 222221 buildLocalArtifact extdSelfAuthoredArtifactpojo is null" );
		int maxLocalVerionNumber = 0; // a single version enough at the extended server
		String versionedFileName = commons.getVersionedFileName(inERLDownload.artifactKeyPojo.artifactName,
				commonData.getContentHandlerSpecsMap().get(contentType).extension, maxLocalVerionNumber);
		System.out.println("at 222221 buildLocalArtifact versionedFileName = " + versionedFileName );
		
		SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = new SelfAuthoredArtifactpojo(
			inERLDownload.artifactKeyPojo,
			inERLDownload.requestor, 		
			inERLDownload.author,
			inERLDownload.hasSpecialHandler,
			inERLDownload.reviewFileName,
			inERLDownload.erlStatus,
			null,				// ParentKey ---> to be looked into for why its set null in all situations
			versionedFileName, 	// localFilePath
			SelfAuthoredArtifactpojo.ArtifactStatusDraft,
			"", 				// ReqRespFileName
			0,					// localVerionNumber
			""					// delegatedTo
			);
		
		//ATTENTION 
		//this is INCORRECT Sequence. the db change shall be done only after the files are updated.
		//else the db will point to ghost filenames!!!!!!!!!!!!!
		//Hence changed the sequence and also removed reduntant delete.
		//xtdCatlogPersistenceManager.insertArtifactUI(extdSelfAuthoredArtifactpojo);
		//
		//String targetCreateFileString = commons.getFullLocalPathFileNameOfNewArtifact(extdSelfAuthoredArtifactpojo.artifactKeyPojo.rootNick, extdSelfAuthoredArtifactpojo.artifactKeyPojo.relevance, extdSelfAuthoredArtifactpojo.LocalFileName);
		//
		//System.out.println("At buildLocalArtifact extdSelfAuthoredArtifactpojo.LocalFileName is " + extdSelfAuthoredArtifactpojo.LocalFileName);
		//
		//System.out.println("At buildLocalArtifact targetCreateFileString is " + targetCreateFileString);
		//
		//if (inArtifactToBeRefreshed){
		//	System.out.println("At buildLocalArtifact inArtifactToBeRefreshed is " + inArtifactToBeRefreshed);
		//	commons.deleteFile(targetCreateFileString);
		//}
		//System.out.println("At buildLocalArtifact post refresh check ");
		//ArtifactMover artifactMover = ArtifactMover.getInstance(commons);
		//artifactMover.moveArtifact(inERLDownload, extdSelfAuthoredArtifactpojo);
		//if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
		//	ErrorHandler.showErrorAndQuit(commons, "Error at ArtifactWrapper " + artifactMover.lastProcessStatus + " while dealing with : " + extdSelfAuthoredArtifactpojo.LocalFileName);
		//	return null;
		//}
		String targetCreateFileString = commons.getFullLocalPathFileNameOfNewArtifact(
										extdSelfAuthoredArtifactpojo.artifactKeyPojo.rootNick, 
										extdSelfAuthoredArtifactpojo.artifactKeyPojo.relevance, 
										extdSelfAuthoredArtifactpojo.LocalFileName);
		System.out.println("At buildLocalArtifact extdSelfAuthoredArtifactpojo.LocalFileName is " + extdSelfAuthoredArtifactpojo.LocalFileName);

		System.out.println("At buildLocalArtifact targetCreateFileString is " + targetCreateFileString);
		ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
		artifactMover.moveArtifact(inERLDownload, extdSelfAuthoredArtifactpojo);
		if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
			ErrorHandler.showErrorAndQuit(commons, "Error at ArtifactWrapper " + artifactMover.lastProcessStatus + " while dealing with : " + extdSelfAuthoredArtifactpojo.LocalFileName);
			return null;
		}
		xtdCatlogPersistenceManager.insertArtifactUI(extdSelfAuthoredArtifactpojo);
		System.out.println("at 222221 buildLocalArtifact completed" );
		return extdSelfAuthoredArtifactpojo;
	}
}