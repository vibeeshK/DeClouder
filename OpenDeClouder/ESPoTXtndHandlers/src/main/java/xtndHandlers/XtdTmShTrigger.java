package xtndHandlers;

import java.io.IOException;
import java.text.ParseException;

import contentHandlers.TimeShCapture;
import contentHandlers.TimeShTrigger;
import contentHandlers.TimeShTriggerItemDoc;
import contentHandlers.TimeShTriggerPojo;
import espot.ArtifactKeyPojo;
import espot.ArtifactPojo;
import espot.ArtifactPrepper;
import espot.CommonData;
import espot.ContentHandlerSpecs;
import espot.ERLDownload;
import espot.ErrorHandler;
import espot.SelfAuthoredArtifactpojo;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdContntHandlerManager;
import xtdSrvrComp.XtdStdProcessRecord;
import xtdSrvrComp.XtdStdRtCtCatlogPersistenceManager;

public class XtdTmShTrigger extends TimeShTrigger implements ExtendedHandler {
	/*
	 * Handler for TmShTrigger capturing extended processing
	 */

	final static String INITIATED_TYPE1CONTENT_TIMESHCAPTURE = "TimeShCapture";

	XtdCommons xtdCommons = null;
	public void initializeExtendedHandlerForExtdSrvrProcess(CommonData inCommonData, 
			ArtifactPojo inArtifactPojo) {
		System.out.println("At initializeExtendedHandlerForExtdSrvrProcess");
		initializeContentHandlerForExtdSrvrProcess(inCommonData, inArtifactPojo);		
		xtdCommons = (XtdCommons) inCommonData.getCommons();
	}

	@Override
	public void processItemDetail(ArtifactPojo inChildArtifactPojo) {
		
		System.out.println("at 2345432 processItemDetail = " + inChildArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at 2345432 This process wont be called");
		System.out.println("at 2345432 processItemDetail ended");
	}

	@Override
	public void processItemSummary(ArtifactPojo inChildArtifactPojo) {
		
		System.out.println("at 23454231 processItemSummary inChildArtifactPojo = " + inChildArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at 23454231 This process wont be called");
		System.out.println("at 23454231 processItemSummary ended");
	}

	@Override
	public String processXtdStdProcessRec(String inXtdProcStatus) {

		System.out.println("at start of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		String processEndingStatus = inXtdProcStatus;

		TimeShTriggerItemDoc timeShTriggerItemDoc = (TimeShTriggerItemDoc) primerDoc;
		
		TimeShTriggerPojo timeShTriggerPojo = timeShTriggerItemDoc.getTimeShTriggerPojo();

		try {
		
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo = " + timeShTriggerPojo);
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo.extProcTriggerInitiated = " + timeShTriggerPojo.extProcTriggerInitiated);
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo.contentType = " + timeShTriggerPojo.contentType);
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo lastTriggeredAt = " + timeShTriggerPojo.lastTriggeredAt);
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo lastTriggeredAt = " + timeShTriggerPojo.lastTriggeredAt);
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo allocationInterval = " + timeShTriggerPojo.allocationInterval);
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo extProcTriggerInitiated = " + timeShTriggerPojo.extProcTriggerInitiated);
			System.out.println("at start of 23454233 processxtdStdProcessRec timeShTriggerPojo extProcTriggerInitiated = " + timeShTriggerPojo.extProcTriggerInitiated);
	
			//check to initiate recurring trigger
			if (!timeShTriggerPojo.extProcTriggerInitiated || timeShTriggerPojo.lastTriggeredAt == null || commonData.getCommons().hasTimeSecElapsed(commonData.getCommons().getDateFromString(timeShTriggerPojo.lastTriggeredAt), timeShTriggerPojo.allocationInterval)) {
	
				timeShTriggerPojo.lastTriggeredAt = commonData.getCommons().getCurrentTimeStamp();
				timeShTriggerPojo.extProcTriggerInitiated = true;
					commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
			} else {
				return processEndingStatus; // Too soon after previous run
			}
	
			System.out.println("at start of 1343 processXtdStdProcessRec contentType = " + timeShTriggerPojo.contentType);
	
			// Process for INITIATED_TYPE2CONTENT_TIMESHEET
			
			ContentHandlerSpecs timeShCaptureSpecs = commonData.getContentHandlerSpecsMap().get(INITIATED_TYPE1CONTENT_TIMESHCAPTURE);
	
			int totalUsersCount = commonData.getUsersHandler().getUsersCount();
	
			for (int userCount = 0; userCount < totalUsersCount; userCount++){
	
				System.out.println("At processXtdStdProcessRec userCount = " + userCount);
	
				String userName = commonData.getUsersHandler().getUserShortnameByIndex(userCount);
	
				System.out.println("At processXtdStdProcessRec userName = " + userName);
				
				//if (!userName.equalsIgnoreCase("demouser")) { continue; } // for testing only

				String timeSheetCaptureArtifactName = TimeShCapture.getTimeShCaptureArtifactName(timeShCaptureSpecs, userName);
				ArtifactKeyPojo timeShCaptureArtifactKey = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
										invokedArtifactPojo.artifactKeyPojo.relevance, 
										timeSheetCaptureArtifactName, timeShCaptureSpecs.contentType);
	
				SelfAuthoredArtifactpojo timeShCaptureSelfAuthoredArtifactpojo = setupDraftArtifact(userName, timeShCaptureArtifactKey);
	
				ExtendedHandler timeSheetHandler = XtdContntHandlerManager.getInstance(xtdCommons, 
														(XtdStdRtCtCatlogPersistenceManager) commonData.getCatelogPersistenceManager(), 
														INITIATED_TYPE1CONTENT_TIMESHCAPTURE);
				timeSheetHandler.initializeExtendedHandlerForExtdSrvrProcess(commonData, timeShCaptureSelfAuthoredArtifactpojo);
				timeSheetHandler.absorbInput(primerDoc.getItem(),XtdTmShCapture.ABSORB_TIMETRIGGER_INSTRUCTION);
				if (invokedArtifactPojo.erlStatus.equalsIgnoreCase(SelfAuthoredArtifactpojo.ERLSTAT_INACTIVE)) {
					timeShCaptureSelfAuthoredArtifactpojo.erlStatus = SelfAuthoredArtifactpojo.ERLSTAT_INACTIVE;
				}
				updateOtherDraftStatusForUpload(timeShCaptureSelfAuthoredArtifactpojo);
			}
	
			timeShTriggerPojo.extProcTriggerInitiated = true;
			
			if (!invokedArtifactPojo.erlStatus.equalsIgnoreCase(ERLDownload.ERLSTAT_INACTIVE)) {
				processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;	// enable repetition
			} else {
				processEndingStatus = XtdStdProcessRecord.ERLRecord_DISCONTINUE;
			}
	
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
			System.out.println("at end of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		} catch (IOException | ParseException e) {
			
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in XtdTimeShRollup processXtdStdProcessRec " + inXtdProcStatus, e);
		}
		
		return processEndingStatus;
	}

	private void updateOtherDraftStatusForUpload(SelfAuthoredArtifactpojo inSelfAuthoredArtifactpojo) {
		commonData.getCatelogPersistenceManager()
		.updateArtifactStatus(
				inSelfAuthoredArtifactpojo,
				SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
		if (inSelfAuthoredArtifactpojo.unpulishedVerNum > 0) {
			commonData.getCatelogPersistenceManager()
				.updateOlderArtifact(
						inSelfAuthoredArtifactpojo.artifactKeyPojo,
						SelfAuthoredArtifactpojo.ArtifactStatusOutdated,
						inSelfAuthoredArtifactpojo.unpulishedVerNum);
		}
	}

	public String absorbInput(Object inInput, String inInstruction) {
		String processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;
		return processEndingStatus;
	}

	private SelfAuthoredArtifactpojo setupDraftArtifact(String inUserName, ArtifactKeyPojo inArtifactKeyPojo){
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo,commonData, inUserName);
		SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
		extdSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		catelogPersistenceManager.insertArtifactUI(extdSelfAuthoredArtifactpojo);
		return extdSelfAuthoredArtifactpojo;
	}
}