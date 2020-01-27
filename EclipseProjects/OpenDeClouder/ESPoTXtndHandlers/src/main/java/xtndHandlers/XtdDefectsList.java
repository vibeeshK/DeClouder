package xtndHandlers;

import java.io.IOException;
import java.util.ArrayList;

import contentHandlers.DefectItemPojo;
import contentHandlers.DefectsList;
import contentHandlers.DefectsListDoc;
import espot.ArtifactKeyPojo;
import espot.ArtifactPojo;
import espot.ArtifactPrepper;
import espot.CommonData;
import espot.ErrorHandler;
import espot.SelfAuthoredArtifactpojo;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdContntHandlerManager;
import xtdSrvrComp.XtdStdProcessRecord;
import xtdSrvrComp.XtdStdRtCtCatlogPersistenceManager;

public class XtdDefectsList extends DefectsList implements ExtendedHandler {
	/*
	 * Handler for DefectsList extended processing
	 */

	final static String INITIATED_TYPE1CONTENT_ProjTracker = "ProjTracker";

	XtdCommons xtdCommons = null;
	public void initializeExtendedHandlerForExtdSrvrProcess(CommonData inCommonData, 
			ArtifactPojo inArtifactPojo) {
		System.out.println("At initializeExtendedHandlerForExtdSrvrProcess of XtdDefectsList");
		initializeContentHandlerForExtdSrvrProcess(inCommonData, inArtifactPojo);		
		xtdCommons = (XtdCommons) inCommonData.getCommons();
	}

	@Override
	public void processItemDetail(ArtifactPojo inChildArtifactPojo) {
		System.out.println("at XtdDefectsList 2345432 processItemDetail = " + inChildArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at 2345432 This process wont be called");
		System.out.println("at 2345432 processItemDetail ended");
	}

	@Override
	public void processItemSummary(ArtifactPojo inChildArtifactPojo) {
		System.out.println("at XtdDefectsList 23454231 processItemSummary inChildArtifactPojo = " + inChildArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at 23454231 This process wont be called");
		System.out.println("at 23454231 processItemSummary ended");
	}

	@Override
	public String processXtdStdProcessRec(String inXtdProcStatus) {
		System.out.println("at start of 1234A1 processxtdStdProcessRec XtdDefectsList inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at start of 1234A2 processxtdStdProcessRec XtdDefectsList start");
		String processEndingStatus = inXtdProcStatus;
		DefectsListDoc defectsListDoc = (DefectsListDoc) primerDoc;
		
		ArrayList<DefectItemPojo> defectItemsList = defectsListDoc.getItemList();
		DefectItemPojo firstProjTaskItem = defectItemsList.get(0);

		System.out.println("at 1234A3 of processxtdStdProcessRec XtdProjTask");
		
		System.out.println("at 1234A4 of process xtdStdProcessRec ProjTasks List = " + defectItemsList);
		System.out.println("at 1234A5 of process xtdStdProcessRec ProjTasks List size = " + defectItemsList.size());
		System.out.println("at 1234A6 of process processxtdStdProcessRec firstProjTaskItem itemID = " + firstProjTaskItem.itemID);

		//1. invoke project tracker with the defectsList 

		ArtifactKeyPojo projTrackerKey = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
				invokedArtifactPojo.artifactKeyPojo.relevance, 
				firstProjTaskItem.projectName, INITIATED_TYPE1CONTENT_ProjTracker);

		SelfAuthoredArtifactpojo extdTaskItemSelfAuthoredArtifactpojo = setupDraftArtifact(projTrackerKey);
		
		ExtendedHandler projTrackerHandler = XtdContntHandlerManager.getInstance(xtdCommons, 
				(XtdStdRtCtCatlogPersistenceManager) commonData.getCatelogPersistenceManager(), 
				INITIATED_TYPE1CONTENT_ProjTracker);
		System.out.println("At 3 of q4324324x");
		System.out.println("at 1234A7 of process processxtdStdProcessRec");
		projTrackerHandler.initializeExtendedHandlerForExtdSrvrProcess(commonData, extdTaskItemSelfAuthoredArtifactpojo);
		System.out.println("At 4 of q4324324x");
		System.out.println("at 1234A8 of process processxtdStdProcessRec");

		projTrackerHandler.absorbInput(defectItemsList,XtdProjTracker.INSTRUCTION_ABSORB_DefectsList);

		System.out.println("at 1234A9 of process processxtdStdProcessRec");
		
		updateOtherDraftStatusForUpload(extdTaskItemSelfAuthoredArtifactpojo);
		
		System.out.println("at 1234AA of process processxtdStdProcessRec");

		processEndingStatus = XtdStdProcessRecord.ERLRecord_SKIP; // nothing expected to be changed as only impacts other artifacts

		System.out.println("at 1234AB of process processxtdStdProcessRec processEndingStatus " + processEndingStatus);
		
		try {
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in XtdDefectsList processXtdStdProcessRec " + inXtdProcStatus, e);
		}
		System.out.println("at end of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at 1234AC of process processxtdStdProcessRec processEndingStatus " + processEndingStatus);

		return processEndingStatus;
	}

	private void updateOtherDraftStatusForUpload(SelfAuthoredArtifactpojo inSelfAuthoredArtifactpojo) {
		commonData.getCatelogPersistenceManager()
		.updateArtifactStatus(
				inSelfAuthoredArtifactpojo,
				SelfAuthoredArtifactpojo.ArtifactStatusToBeBatchUploaded);
		if (inSelfAuthoredArtifactpojo.unpulishedVerNum > 0) {
			commonData.getCatelogPersistenceManager()
				.updateOlderArtifact(
						inSelfAuthoredArtifactpojo.artifactKeyPojo,
						SelfAuthoredArtifactpojo.ArtifactStatusOutdated,
						inSelfAuthoredArtifactpojo.unpulishedVerNum);
		}
	}

	private SelfAuthoredArtifactpojo setupDraftArtifact(ArtifactKeyPojo inArtifactKeyPojo){
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo, commonData);
		SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
		extdSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		commonData.getCatelogPersistenceManager().insertArtifactUI(extdSelfAuthoredArtifactpojo);
		return extdSelfAuthoredArtifactpojo;
	}

	@Override
	public String absorbInput(Object inInput, String inInstruction) {
		return null;
	}
}