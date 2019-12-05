package xtndHandlers;

import contentHandlers.AllocatedTasks;
import espot.ArtifactPojo;
import espot.CommonData;
import xtdCommonTechs.ExcelColJavaObj;
import xtdCommonTechs.ExcelJavaObj;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;

public class XtdAllocatedTasks extends AllocatedTasks implements ExtendedHandler {
	/*
	 * Handler for Allocated tasks grouper extended processing
	 */
	public static final String SHORTID_COLHDR = "ShortId";
	public static final String TASKID_COLHDR = "TaskID";
	public static final String TEAM_COLHDR = "Team";	// team can be the projectID or supportTeamID
	public static final String TEAM_RELEVANCE_COLHDR = "Relevance";	// teamRelevance denoted the grouping in relevance
	public static final String DESCRIPTION_COLHDR = "Description";
	public static final String TIMEESTIMATED_COLHDR  = "TimeEstimated";
	public static final String EXPECTEDSTART_COLHDR = "ExpectedStart";
	public static final String EXPECTEDEND_COLHDR = "ExpectedEnd";
	public static final Integer SHORTID_POSN  = 0;
	public static final Integer TASKID_POSN = 1;
	public static final Integer TEAM_POSN  = 2;
	public static final Integer TEAMRELEVANCE_POSN  = 3;
	public static final Integer DESCRIPTION_POSN = 4;
	public static final Integer TIMEESTIMATED_POSN  = 5;
	public static final Integer EXPECTEDSTART_POSN = 6;
	public static final Integer EXPECTEDEND_POSN = 7;

	final static int DECKER_KEY_COLUMN = 0;

	public final static String INSTRUCTION_ABSORB_XLJAVAOBJ = "ABSORB_XLJAVAOBJ";

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
	
	public void absorbAllocatedTasksExcel(String inAllocatedTasksExcel){
		
	}

	public ExcelJavaObj getBaseXlJavaObjWithColTypes() {

		ExcelJavaObj xlJavaObj = new ExcelJavaObj();
		xlJavaObj.addColOfInterest(SHORTID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		xlJavaObj.addColOfInterest(TEAM_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		xlJavaObj.addColOfInterest(TEAM_RELEVANCE_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		xlJavaObj.addColOfInterest(TASKID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		xlJavaObj.addColOfInterest(DESCRIPTION_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		xlJavaObj.addColOfInterest(TIMEESTIMATED_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		xlJavaObj.addColOfInterest(EXPECTEDSTART_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		xlJavaObj.addColOfInterest(EXPECTEDEND_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		xlJavaObj.initKeyColumn(SHORTID_COLHDR);
		
		return xlJavaObj;
	}

	public String absorbInput(Object inInput,String inInstruction) {
		return null;
	}
}