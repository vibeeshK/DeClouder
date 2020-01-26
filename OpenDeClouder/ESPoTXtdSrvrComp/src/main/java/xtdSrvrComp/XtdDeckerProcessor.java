package xtdSrvrComp;

import java.util.ArrayList;
import java.util.Date;

import espot.ArtifactKeyPojo;
import espot.ArtifactMover;
import espot.ArtifactPrepper;
import espot.CommonData;
import espot.Commons;
import espot.ERLDownload;
import espot.ErrorHandler;
import espot.GenericGrouper;
import espot.RemoteAccesser;
import espot.RootPojo;
import espot.SelfAuthoredArtifactpojo;

public class XtdDeckerProcessor {
	/*
	 * Extended decking process core function that identifies, downloads related contents
	 * and invokes corresponding extended handlers
	 */
	private XtdDeckerProcCatlogPersistenceManager xtdDeckerProcCatlogPersistenceMgr = null;
	
	private Commons commons;
	private RemoteAccesser remoteAccesser;	
	private RootPojo rootPojo;
	private String contentType;
	private CommonData commonData;

	public XtdDeckerProcessor(CommonData inCommonData,String inContentType,
			RemoteAccesser inRemoteAccesser) {
		commonData = inCommonData;
		xtdDeckerProcCatlogPersistenceMgr = (XtdDeckerProcCatlogPersistenceManager) inCommonData.getCatelogPersistenceManager();
		commons = inCommonData.getCommons();
		remoteAccesser = inRemoteAccesser;
		rootPojo = commonData.getCurrentRootPojo();
		contentType = inContentType;
	}

	public void subscribeToDeckerParents(){
		System.out.println("at subscribeToDeckerParents start" );
		ArrayList<ERLDownload> dbERLDownloadsList = xtdDeckerProcCatlogPersistenceMgr.readERLDownLoadsOfRootsSpecificContentType(contentType);
		System.out.println("at subscribeToDeckerParents 123 dbERLDownloadsList.size() is " + dbERLDownloadsList.size() );
		for (int erlCount = 0; erlCount < dbERLDownloadsList.size(); erlCount++) {
			ERLDownload erlDownload = dbERLDownloadsList.get(erlCount);
			System.out.println("at subscribeToDeckerParents 324123" );
			System.out.println("at subscribeToDeckerParents erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
			if (erlDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED)) {
				System.out.println("at subscribeToDeckerParents 543123" );
				xtdDeckerProcCatlogPersistenceMgr.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				System.out.println("at subscribeToDeckerParents 324a14231" );
				xtdDeckerProcCatlogPersistenceMgr.insertDeckerParent(erlDownload.artifactKeyPojo,0,commons.getTimeStamp(new Date(0)),ERLGrouperParent.PARENT_NEW);
			} else if (erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
				System.out.println("at subscribeToDeckerParents 4577123" );
				xtdDeckerProcCatlogPersistenceMgr.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				xtdDeckerProcCatlogPersistenceMgr.updateDeckerParent(erlDownload.artifactKeyPojo,0,commons.getTimeStamp(new Date(0)),ERLGrouperParent.PARENT_UPDATED);
			} else {
				System.out.println("at subscribeToDeckerParents 54343523 erlDownload.subscriptionStatus " + erlDownload.subscriptionStatus);
			}
			
			System.out.println("at subscribeToDeckerParents 1436723" );
		}
		System.out.println("at subscribeToDeckerParents end");
	}

	public void deleteUnconnectedSubscriptions() {
		// Deleting any subscriptions not required anymore
		xtdDeckerProcCatlogPersistenceMgr.deleteSubscriptionsOfOldGroupers(rootPojo.rootNick);
	}
	
	public void subscribeToUpdatedChildren() {
		System.out.println("subscribeToUpdatedChildren begins");
		ArrayList<ERLDownload> dbERLDownloadsList = xtdDeckerProcCatlogPersistenceMgr.readForSubscribingArrivedDeckerChildren(rootPojo.rootNick);
		System.out.println("subscribeToUpdatedChildren dbERLDownloadsList size = " + dbERLDownloadsList.size());
		for (int erlCount = 0; erlCount < dbERLDownloadsList.size(); erlCount++) {
			ERLDownload erlDownload = dbERLDownloadsList.get(erlCount);
			System.out.println("subscribeToUpdatedChildren erlDownload size = " + erlDownload.artifactKeyPojo.artifactName);
			System.out.println("subscribeToUpdatedChildren dbERLDownloadsList size = " + dbERLDownloadsList.size());
			System.out.println("erlCount = " + erlCount);

			System.out.println("erlDownload.subscriptionStatus = " + erlDownload.subscriptionStatus);
			System.out.println("erlCount = " + erlCount);

			if (erlDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED)){
				System.out.println("ERLDownload.NEVER_SUBSCRIBED = " + ERLDownload.NEVER_SUBSCRIBED);
				System.out.println("subscribeToUpdatedChildren");
				xtdDeckerProcCatlogPersistenceMgr.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				xtdDeckerProcCatlogPersistenceMgr.updateDeckerChildrenStatus(erlDownload.artifactKeyPojo,ExtendedChildPojo.CHILD_UPDATED,erlDownload.uploadedTimeStamp);
			} else if (erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
				System.out.println("ERLDownload. Local raturned = " + ERLDownload.LOCAL_COPY_TOBERENEWED);
				xtdDeckerProcCatlogPersistenceMgr.replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
				xtdDeckerProcCatlogPersistenceMgr.updateDeckerChildrenStatus(erlDownload.artifactKeyPojo,ExtendedChildPojo.CHILD_UPDATED,erlDownload.uploadedTimeStamp);
			} else {
				System.out.println("Already processed erlDownload " + erlDownload.artifactKeyPojo.artifactName);
			}
		}	
	}
	
	public void identifyGrouperChildrenOfUpdtdParents() {
		//downloading for new child subscriptions
		System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents" );

		ArrayList<ERLGrouperParent> dbUpdatdERLGrouperParentList = xtdDeckerProcCatlogPersistenceMgr.readUpdatedERLGrouperParents(rootPojo.rootNick,contentType);
		System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents4353 dbUpdatdERLGrouperParentList.size() " + dbUpdatdERLGrouperParentList.size());
		for (int parentERLCount = 0; parentERLCount < dbUpdatdERLGrouperParentList.size(); parentERLCount++) {
			System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents43534" );
			ERLGrouperParent erlGrouperParent = dbUpdatdERLGrouperParentList.get(parentERLCount);

			System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents34534" );
			ERLDownload parentERLDownload = xtdDeckerProcCatlogPersistenceMgr.readERLDownLoad(erlGrouperParent.artifactKeyPojo);
//			boolean isParentRefreshed = (erlGrouperParent.parentStatus.equalsIgnoreCase(ERLGrouperParent.PARENT_NEW))?false:true;
//			System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents45424356" );
//			if (isParentRefreshed) {
//				System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents4542434354316" );
//				//Insert new child records; delete old child table records which are not connected anymore. 
//				// Later remove those subscriptions of child records which are not connected to any parent.
//				xtdDeckerProcCatlogPersistenceMgr.deleteGrouperChildren(parentERLDownload.artifactKeyPojo);
//				System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents4542436575656" );
//			}
			System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents5436457" );
			SelfAuthoredArtifactpojo extdParentSelfAuthoredArtifactpojo = setupDraftArtifact(parentERLDownload.artifactKeyPojo);

			System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents456455424356" );

			ExtendedHandler contentHandlerObjectInterface = XtdContntHandlerManager.getInstance(commons,xtdDeckerProcCatlogPersistenceMgr,parentERLDownload.artifactKeyPojo.contentType);
			System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents4542445645356" );
			contentHandlerObjectInterface.initializeExtendedHandlerForExtdSrvrProcess(commonData, extdParentSelfAuthoredArtifactpojo);

			GenericGrouper deckerHandler = (GenericGrouper) contentHandlerObjectInterface;
			
			System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents4542445645356 deckerHandler.wasDeckerGouperEditedForXtdProcess() is " + deckerHandler.wasDeckerGouperEditedForXtdProcess());
						
			if (deckerHandler.wasDeckerGouperEditedForXtdProcess()) {
				boolean isParentRefreshed = (erlGrouperParent.parentStatus.equalsIgnoreCase(ERLGrouperParent.PARENT_NEW))?false:true;
				System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents45424356" );
				if (isParentRefreshed) {
					System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents4542434354316" );
					//Insert new child records; delete old child table records which are not connected anymore. 
					// Later remove those subscriptions of child records which are not connected to any parent.
					xtdDeckerProcCatlogPersistenceMgr.deleteGrouperChildren(parentERLDownload.artifactKeyPojo);
					System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents4542436575656" );
				}
				
				System.out.println("at 222221 identifyGrouperChildrenOfUpdtdParents454243435345256" );
				ArrayList<ArtifactKeyPojo> childArtifactKeys = contentHandlerObjectInterface.extractAssociatedArtifactKeys();
	
				System.out.println("at 222221 parentERLCount " + parentERLCount);
				System.out.println("at 222221 childArtifactKeys.size() " + childArtifactKeys.size());
	
				for (int childERLCount = 0; childERLCount < childArtifactKeys.size(); childERLCount++) {
					ArtifactKeyPojo childArtifactKey = childArtifactKeys.get(childERLCount);
	
					System.out.println("at 222221 childERLCount " + childERLCount);
					System.out.println("at 222221 childArtifactKey " + childArtifactKey.artifactName);
	
					System.out.println("at 222221 childERLCount123 aa");
					xtdDeckerProcCatlogPersistenceMgr.insertDeckerChild(childArtifactKey,erlGrouperParent.artifactKeyPojo,childERLCount,ExtendedChildPojo.CHILD_NEW,commons.getTimeStamp(new Date(0)));
					System.out.println("at 222221 childERLCount123 bb ");
				}
				erlGrouperParent.childTotal = childArtifactKeys.size();
				if (parentERLDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED)) {
					erlGrouperParent.parentStatus = ERLGrouperParent.PARENT_NEW;
				} else {
					erlGrouperParent.parentStatus = ERLGrouperParent.PARENT_UPDATED;
				}
			} else {
				System.out.println("at 222221x identifyGrouperChildrenOfUpdtdParents454243435345256 skipping to processed" );
				
				erlGrouperParent.parentStatus = ERLGrouperParent.PARENT_PROCESSED;				
			}
			xtdDeckerProcCatlogPersistenceMgr.updateDeckerParent(erlGrouperParent.artifactKeyPojo,erlGrouperParent.childTotal,parentERLDownload.uploadedTimeStamp,erlGrouperParent.parentStatus);
		}
	}

	public void extendedContentProcessing() {
		System.out.println("at 32432 extendedContentProcessing1" );
		ArrayList<ERLGrouperParent> dbUpdatedGrouperChildnParentsList = xtdDeckerProcCatlogPersistenceMgr.readUpdatedGrouperChildnParents(rootPojo.rootNick, contentType);
		System.out.println("at 32432 extendedContentProcessing2 dbUpdatedGrouperChildnParentsList.size() " + dbUpdatedGrouperChildnParentsList.size() );
		for (int parentERLCount = 0; parentERLCount < dbUpdatedGrouperChildnParentsList.size(); parentERLCount++) {
			System.out.println("at 32432 extendedContentProcessing3 parentERLCount = " + parentERLCount);
			ERLGrouperParent erlGrouperParent = dbUpdatedGrouperChildnParentsList.get(parentERLCount);
			System.out.println("at 32432 extendedContentProcessing3 parent Artifact = " + erlGrouperParent.artifactKeyPojo.artifactName);
			
			ERLDownload parentERLDownload = xtdDeckerProcCatlogPersistenceMgr.readERLDownLoad(erlGrouperParent.artifactKeyPojo);
			SelfAuthoredArtifactpojo parentDraft = setupDraftArtifact(parentERLDownload.artifactKeyPojo);

			ExtendedHandler extendedHandler = XtdContntHandlerManager.getInstance(commons, xtdDeckerProcCatlogPersistenceMgr, contentType);
			extendedHandler.initializeExtendedHandlerForExtdSrvrProcess(commonData, parentDraft);

			System.out.println("at 32432 extendedContentProcessing4 erlGrouperParent.extendedChildList.size() = " + erlGrouperParent.extendedChildList.size());

			for (int childERLCount = 0; childERLCount < erlGrouperParent.extendedChildList.size(); childERLCount++) {
				System.out.println("at 32432 extendedContentProcessing4 childERLCount = " + childERLCount);
				ExtendedChildPojo extendedChildPojo = erlGrouperParent.extendedChildList.get(childERLCount);
				ERLDownload childERLDownload = xtdDeckerProcCatlogPersistenceMgr.readERLDownLoad(extendedChildPojo.artifactKeyPojo);
				System.out.println("at 32432 extendedContentProcessing4 artifactName = " + extendedChildPojo.artifactKeyPojo.artifactName);
				if (childERLDownload == null) { 
					System.out.println("at 32432 extendedContentProcessing4 skipping the childERLCount = " + childERLCount);
					continue; 
				}
				System.out.println("at 32432 extendedContentProcessing4 not skipping the childERLCount = " + childERLCount);
				
				extendedHandler.processItemDetail(childERLDownload);
				extendedHandler.processItemSummary(childERLDownload);
				xtdDeckerProcCatlogPersistenceMgr.updateDeckerChildStatus(childERLDownload.artifactKeyPojo,parentERLDownload.artifactKeyPojo,ExtendedChildPojo.CHILD_PROCESSED);
			}
			xtdDeckerProcCatlogPersistenceMgr.updateDeckerParent(erlGrouperParent.artifactKeyPojo, erlGrouperParent.childTotal,erlGrouperParent.parentUpdateTimeStamp,ERLGrouperParent.PARENT_PROCESSED);
			ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
			artifactMover.prepForUpload(parentDraft,commonData.getContentHandlerSpecsMap().get(contentType).extension);
			if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
				ErrorHandler.showErrorAndQuit(commons, "Error17 at ArtifactWrapper artifactMover " + artifactMover.lastProcessStatus +  artifactMover.lastProcessStatus + " while prepForUpload : ");
				return;
			}
			xtdDeckerProcCatlogPersistenceMgr.updateArtifactStatus(parentDraft,SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
		}
	}

	private SelfAuthoredArtifactpojo setupDraftArtifact(ArtifactKeyPojo inArtifactKeyPojo){
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo, commonData);
		SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
		extdSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		commonData.getCatelogPersistenceManager().insertArtifactUI(extdSelfAuthoredArtifactpojo);
		return extdSelfAuthoredArtifactpojo;
	}
}