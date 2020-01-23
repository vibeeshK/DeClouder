package xtndHandlers;

import java.io.IOException;

import contentHandlers.ProjTask;
import contentHandlers.ProjTaskItemDoc;
import contentHandlers.ProjTaskItemPojo;
import espot.ArtifactPojo;
import espot.CommonData;
import espot.ErrorHandler;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdStdProcessRecord;
import xtdSrvrComp.XtdTmShCatlogPersistenceMgr;

public class XtdProjTask extends ProjTask implements ExtendedHandler {
	/*
	 * Handler for ProjTask extended processing
	 */

	public final static String INSTRUCTION_ABSORB_XLJAVAOBJ = "ABSORB_XLJAVAOBJ";

	XtdCommons xtdCommons = null;

	public void setInitialItemPojoAddlFields(){
		// for projtask, this method will be overridden at the extended handler process where  
		// the task ID will be set, and the same has to be sent before itemID gets set
		ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) primerDoc.getItem();
	}

	public void initializeExtendedHandlerForExtdSrvrProcess(CommonData inCommonData, 
			ArtifactPojo inXtdArtifactPojo) {
		System.out.println("At initializeExtendedHandlerForExtdSrvrProcess");
		initializeContentHandlerForExtdSrvrProcess(inCommonData, inXtdArtifactPojo);
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
		// this function will be called whenever the extended time records updated to make progress
		// it processes in the below steps
		// 1) update the projTask with the total logged hours for the task from time db
		
		System.out.println("at start of 21323454233 processxtdStdProcessRec XtdProjTask start");
		System.out.println("at start of 21323454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		String processEndingStatus = inXtdProcStatus;
		ProjTaskItemDoc projTaskItemDoc = (ProjTaskItemDoc) primerDoc;

		
		// 1) accumulate hours and update db
		ProjTaskItemPojo projTaskItem = (ProjTaskItemPojo) projTaskItemDoc.getItem();

		XtdTmShCatlogPersistenceMgr xtdTmShCatlogPersistenceMgr = (XtdTmShCatlogPersistenceMgr) commonData.getCatelogPersistenceManager();
		projTaskItem.updatedAt = commons.getDateTS();
		
		if (projTaskItem.burntHours == 0 ) {
			// when no hours booked so far, the first booking would show the task start up
			projTaskItem.actualStart = xtdTmShCatlogPersistenceMgr.getFirstHrsEntryTimingOfTask(projTaskItem.taskID, 
					projTaskItem.relevance, 
					projTaskItem.projectName);			
		}

		System.out.println("at processxtdStdProcessRec XtdProjTask 111");

		projTaskItem.burntHours = xtdTmShCatlogPersistenceMgr.readTimeDetailOfTask(projTaskItem.taskID, 
														projTaskItem.relevance, 
														projTaskItem.projectName);
		projTaskItem.estimatedEffortToComplete = getEstimatedEffortToComplete();

		System.out.println("at processxtdStdProcessRec XtdProjTask 222");

		processEndingStatus = XtdStdProcessRecord.ERLRecord_PROCESSED;
		
		System.out.println("at processxtdStdProcessRec XtdProjTask 333 processEndingStatus is " + processEndingStatus);
		
		try {
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in XtdProjTask processXtdStdProcessRec " + inXtdProcStatus, e);
		}
		System.out.println("at processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		return processEndingStatus;
	}

	@Override
	public String absorbInput(Object inInput, String inInstruction) {
		return null;
	}
}