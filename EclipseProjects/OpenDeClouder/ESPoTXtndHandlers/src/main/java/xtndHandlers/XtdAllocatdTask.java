package xtndHandlers;

import contentHandlers.AllocatdTask;
import espot.ArtifactPojo;
import espot.CommonData;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdStdProcessRecord;

public class XtdAllocatdTask extends AllocatdTask implements ExtendedHandler {
	/*
	 * Handler for allocated task item's extended processing
	 */

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

	@Override
	public String absorbInput(Object inItemPojo, String inInstruction) {
		String processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;
		System.out.println("at 23454233 This process wont be called");		
		return processEndingStatus;
	}
}