package xtndHandlers;

import contentHandlers.TimeShCapture;
import contentHandlers.TimeShCapturePojo;
import contentHandlers.TimeShTriggerPojo;
import espot.ArtifactPojo;
import espot.CommonData;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdStdProcessRecord;

public class XtdTmShCapture extends TimeShCapture implements ExtendedHandler {
	/*
	 * Handler for TmShCapture capturing extended processing
	 */

	public final static String ABSORB_TIMETRIGGER_INSTRUCTION = "ABSORB_TIMETRIGGER";

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
	public String processXtdStdProcessRec(String xtdProcStatus) {
		
		System.out.println("at start of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at 23454233 This process wont be called");		
		System.out.println("at end of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		return xtdProcStatus;
	}

	public String absorbInput(Object inInput, String inInstruction) {
		String processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;
	
		TimeShTriggerPojo timeShTriggerPojo = (TimeShTriggerPojo) inInput;

		if (inInstruction.equalsIgnoreCase(ABSORB_TIMETRIGGER_INSTRUCTION)){

			TimeShCapturePojo timeShCapturePojo = (TimeShCapturePojo) primerDoc.getItem();
			timeShCapturePojo.updatedAt = commons.getDateTS();
			timeShCapturePojo.allocationInterval = timeShTriggerPojo.allocationInterval;
			timeShCapturePojo.author = timeShTriggerPojo.author;
			timeShCapturePojo.captureInterval = timeShTriggerPojo.captureInterval;
			timeShCapturePojo.itemID = timeShTriggerPojo.itemID;
			timeShCapturePojo.itemNumber = timeShTriggerPojo.itemNumber;
			timeShCapturePojo.relevance = timeShTriggerPojo.relevance;
			timeShCapturePojo.status = timeShTriggerPojo.status;
			timeShCapturePojo.title = timeShTriggerPojo.title;
			timeShCapturePojo.captureStartDate = timeShTriggerPojo.captureStartDate;
			timeShCapturePojo.captureEndDate = timeShTriggerPojo.captureEndDate;

			writePrimer();			
		}
		return processEndingStatus;
	}
}