package xtndHandlers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import contentHandlers.AllocatdTask;
import contentHandlers.AllocatdTaskItemPojo;
import contentHandlers.Defect;
import contentHandlers.DefectItemPojo;
import contentHandlers.Impediment;
import contentHandlers.ImpedimentItemPojo;
import contentHandlers.ProjTask;
import contentHandlers.ProjTaskItemPojo;
import contentHandlers.ProjTracker;
import contentHandlers.ProjTrackerItemDoc;
import contentHandlers.ProjTrackerPojo;

import espot.ArtifactKeyPojo;
import espot.ArtifactPojo;
import espot.ArtifactPrepper;
import espot.CommonData;
import espot.ContentHandlerSpecs;
import espot.ErrorHandler;
import espot.SelfAuthoredArtifactpojo;
import xtdCommonTechs.ExcelColJavaObj;
import xtdCommonTechs.ExcelHandler;
import xtdCommonTechs.ExcelJavaObj;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdContntHandlerManager;
import xtdSrvrComp.XtdStdProcessRecord;
import xtdSrvrComp.XtdStdRtCtCatlogPersistenceManager;
import xtdSrvrComp.XtdTmShCatlogPersistenceMgr;

public class XtdProjTracker extends ProjTracker implements ExtendedHandler {
	/*
	 * Handler for ProjTracker extended processing
	 */
	final static String CONTENTTYPE1_ProjTask = "ProjTask";
	final static String CONTENTTYPE2_AllocatdTask = "AllocatdTask";
	final static String CONTENTTYPE3_ProjTasksList = "ProjTasksList";
	final static String CONTENTTYPE4_Impediment = "Impediment";
	final static String CONTENTTYPE5_ImpedimentsList = "ImpedimentsList";
	final static String CONTENTTYPE6_Defect = "Defect";
	final static String CONTENTTYPE7_DefectsList = "DefectsList";
	public static final String INSTRUCTION_ABSORB_ProjTaskItemList = "ABSORB_ProjTaskItemList";
	public static final String INSTRUCTION_ABSORB_ImpedimentItemsList = "ABSORB_ImpedimentItemsList";
	public static final String INSTRUCTION_ABSORB_DefectsList = "ABSORB_DefectsList";

	final static String ProjSummary_SHEETNAME = "ProjSummary";
	final static String ProjPlan_SHEETNAME = "ProjPlan";
	final static String ResourcePl_SHEETNAME = "Resources";
	final static String Impediments_SHEETNAME = "Impediments";
	final static String Defects_SHEETNAME = "Defects";

	public static final String PROJSUMSH_EngagementID_COLHDR = "EngagementID";
	
	public static final int PROJPLANSH_KICKOFF_ROWNUM = 0;

	public static final String PROJPLANSH_TaskID_COLHDR = "TaskID";
	public static final String PROJPLANSH_TaskDesc_COLHDR = "Task Desc";
	public static final String PROJPLANSH_Owner_COLHDR = "OwnerID";
	public static final String PROJPLANSH_Lead_COLHDR = "LeadID";
	public static final String PROJPLANSH_PlannedHours_COLHDR = "PlannedHours";
	public static final String PROJPLANSH_PlannedStart_COLHDR = "PlannedStart";
	public static final String PROJPLANSH_PlannedEnd_COLHDR = "PlannedEnd";
	public static final String PROJPLANSH_PlannedCompletion_COLHDR = "Planned%Completion";
	public static final String PROJPLANSH_PlannedValue_COLHDR = "Planned Value at this point";
	public static final String PROJPLANSH_Status_COLHDR = "Status";
	public static final String PROJPLANSH_ActualStart_COLHDR = "ActualStart";
	public static final String PROJPLANSH_ActualEnd_COLHDR = "ActualEnd";
	public static final String PROJPLANSH_BurntHours_COLHDR = "BurntHours";
	public static final String PROJPLANSH_EstimatedOverrunEffortToComplete_COLHDR = "Estimated Overrun Effort required to complete";
	public static final String PROJPLANSH_EstimatedEffortToComplete_COLHDR = "Estimated Effort required to complete";
	public static final String PROJPLANSH_EarnedValueSoFar_COLHDR = "Earned Value so far";
	public static final String PROJPLANSH_ExpectedEnd_COLHDR = "ExpectedEnd";
	public static final String PROJPLANSH_CPI_COLHDR = "CPI =  Earned / Actual";
	public static final String PROJPLANSH_SPI_COLHDR = "SPI = Earned / Planned";
	public static final String PROJPLANSH_OpenImpedsCnt_COLHDR = "Open Impediments Count";
	public static final String PROJPLANSH_OpenDefectsCnt_COLHDR = "Open Defects Count";
	public static final String PROJPLANSH_Remark_COLHDR = "Remarks";

	final static String INVALIDTASKIDCHARS = "_";
	
	public static final String RESOURCEPLANSH_ResourceID_COLHDR = "ResourceID";
	public static final String RESOURCEPLANSH_RoleID_COLHDR = "RoleID";
	public static final String RESOURCEPLANSH_HourlyRate_COLHDR = "Hourly Rate";

	public static final String RESOURCEPLANSH_EffortHrsSpent_COLHDR = "Effort Hrs spent";
	public static final String RESOURCEPLANSH_UnknownRole = "UnknownRole";
	public static final int RESOURCEPLANSH_UnknownRate = 0;

	//Impediments sheet start
	public static final String IMPEDIMENTSH_ImpedimentID_COLHDR = "ImpedimentID";
	public static final String IMPEDIMENTSH_ImpedimentDesc_COLHDR = "Impediment Desc";
	public static final String IMPEDIMENTSH_Severity_COLHDR = "Severity (High/Medium/Low)";
	public static final String IMPEDIMENTSH_Owner_COLHDR = "OwnerID";
	public static final String IMPEDIMENTSH_OpenedDate_COLHDR = "OpenedDate";
	public static final String IMPEDIMENTSH_Status_COLHDR = "Status";
	public static final String IMPEDIMENTSH_ExpectedEnd_COLHDR = "ExpectedEnd";
	public static final String IMPEDIMENTSH_ActualEnd_COLHDR = "ActualEnd";
	public static final String IMPEDIMENTSH_PctgCompleted_COLHDR = "%Completed";
	public static final String IMPEDIMENTSH_BurntHours_COLHDR = "BurntHours (sum of hrs booked)";
	public static final String IMPEDIMENTSH_Remark_COLHDR = "Remarks";
	//Impediments sheet end

	//Defects sheet start
	public static final String DEFECTSH_DefectID_COLHDR = "DefectID";
	public static final String DEFECTSH_DefectDesc_COLHDR = "Defect Desc";
	public static final String DEFECTSH_Severity_COLHDR = "Severity (High/Medium/Low)";
	public static final String DEFECTSH_RaisedBy_COLHDR = "RaisedBy";
	public static final String DEFECTSH_Owner_COLHDR = "OwnerID";
	public static final String DEFECTSH_OpenedDate_COLHDR = "OpenedDate";
	public static final String DEFECTSH_Status_COLHDR = "Status";
	public static final String DEFECTSH_ExpectedEnd_COLHDR = "ExpectedEnd";
	public static final String DEFECTSH_ActualEnd_COLHDR = "ActualEnd";
	public static final String DEFECTSH_PctgCompleted_COLHDR = "%Completed";
	public static final String DEFECTSH_BurntHours_COLHDR = "BurntHours (sum of hrs booked)";
	public static final String DEFECTSH_Remark_COLHDR = "Remarks";
	//Defects sheet end
	
	private HashMap<String, ArrayList<DefectItemPojo>> tasksOpenDefects;
	private HashMap<String, ArrayList<ImpedimentItemPojo>> tasksOpenImpediments;

	XtdCommons xtdCommons = null;
	ExcelHandler excelHandler;

	public void initializeExtendedHandlerForExtdSrvrProcess(CommonData inCommonData, 
			ArtifactPojo inArtifactPojo) {
		System.out.println("At initializeExtendedHandlerForExtdSrvrProcess");
		initializeContentHandlerForExtdSrvrProcess(inCommonData, inArtifactPojo);		
		xtdCommons = (XtdCommons) inCommonData.getCommons();
		try {
			excelHandler = new ExcelHandler(xtdCommons,projTrackerPathFilename,projTrackerPathFilename);
			System.out.println("At 5 initializeExtendedHandlerForExtdSrvrProcess of XtdProjTracker excelHandler " + excelHandler);
			
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(xtdCommons, "Error in XtdProjTracker initializeExtendedHandlerForExtdSrvrProcess " + inArtifactPojo.artifactKeyPojo.artifactName, e);
		}
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

		String processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;
		
		ProjTrackerItemDoc projTrackerItemDoc = (ProjTrackerItemDoc) primerDoc;
		
		ProjTrackerPojo projTrackerPojo = projTrackerItemDoc.getProjTrackerPojo();

		System.out.println("at start of 23454233 processxtdStdProcessRec projTrackerPojo = " + projTrackerPojo);
		System.out.println("at start of 23454233 processxtdStdProcessRec projTrackerPojo.corePlanChanged = " + projTrackerPojo.corePlanChanged);
		System.out.println("at start of 23454233 processxtdStdProcessRec projTrackerPojo.initialized = " + projTrackerPojo.initialized);
		System.out.println("at start of 23454233 processxtdStdProcessRec projTrackerPojo.contentType = " + projTrackerPojo.contentType);

		try {
			//check for repeats
			if (!projTrackerPojo.corePlanChanged || !projTrackerPojo.initialized) {
				processEndingStatus = XtdStdProcessRecord.ERLRecord_SKIP;
				return processEndingStatus; // nothing changed to do extensive process
			}
	
			ExcelJavaObj resourcePlanXlJavaObj = getResourcePlanXlJavaObjFromSh();
	
			if (resourcePlanXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
				processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
				return processEndingStatus;
			}
	
			ExcelJavaObj projTasksXlJavaObj = getProjTaskXlJavaObjFromShForTasksUpdate();
	
			if (projTasksXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
				processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
				return processEndingStatus;
			}
	
			//1. set up ProjTaskItem from each task row of the project plan, 
	
			ContentHandlerSpecs projTaskSpecs = commonData.getContentHandlerSpecsMap().get(CONTENTTYPE1_ProjTask);
	
			for (int taskRowNum = 0; taskRowNum < projTasksXlJavaObj.getTotalDetailRows(); taskRowNum++) {
	
				System.out.println("at processxtdStdProcessRec set up ProjTaskItem from each task row. PROJPLANSH_TaskID_COLHDR is " + PROJPLANSH_TaskID_COLHDR);
				System.out.println("at processxtdStdProcessRec set up ProjTaskItem taskRowNum is " + taskRowNum);
				System.out.println("at processxtdStdProcessRec set up ProjTaskItem from each task row. projTasksXlJavaObj taskID is " + (String) projTasksXlJavaObj.getColValAtObjRowNumFor(taskRowNum, PROJPLANSH_TaskID_COLHDR));
				
				String taskID = (String) projTasksXlJavaObj.getColValAtObjRowNumFor(taskRowNum, PROJPLANSH_TaskID_COLHDR);
				if (taskID.contains(Impediment.IMPTASKSEPARATOR) || taskID.contains(Defect.DEFECTTASKSEPARATOR)){
					xtdCommons.logger.error("error in XtdProjTracker TaskID should't have underscore, " 
										+ Impediment.IMPTASKSEPARATOR + " , " + Defect.DEFECTTASKSEPARATOR + " . " 
										+ taskID + " while at processXtdStdProcessRec3 for " + invokedArtifactPojo.artifactKeyPojo.artifactName);
					processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
					return processEndingStatus;
				}
				
				String projTaskArtifactName = ProjTask.getProjTaskArtifactName (
						projTaskSpecs, projTrackerPojo.artifactName,taskID);
	
				ArtifactKeyPojo projTaskKeyPojo = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
						invokedArtifactPojo.artifactKeyPojo.relevance, 
						projTaskArtifactName, projTaskSpecs.contentType);
	
				SelfAuthoredArtifactpojo extdTaskItemSelfAuthoredArtifactpojo = setupDraftArtifact((String) projTasksXlJavaObj.getColValAtObjRowNumFor(taskRowNum, PROJPLANSH_Owner_COLHDR), projTaskKeyPojo);
				ExtendedHandler projTaskItemHandler = XtdContntHandlerManager.getInstance(xtdCommons, 
						(XtdStdRtCtCatlogPersistenceManager) commonData.getCatelogPersistenceManager(), 
						projTaskSpecs.contentType);
				System.out.println("At 3 of q4324324x");
				projTaskItemHandler.initializeExtendedHandlerForExtdSrvrProcess(commonData, extdTaskItemSelfAuthoredArtifactpojo);
				System.out.println("At 4 of q4324324x");
				ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) projTaskItemHandler.getFocusedItemPojo();
				setProjTaskItemFromProjTaskXlRowObj(projTrackerPojo, projTaskItemPojo,projTasksXlJavaObj,taskRowNum);
	
				projTaskItemHandler.writePrimer();
				updateOtherDraftStatusForUpload(extdTaskItemSelfAuthoredArtifactpojo);
			}
			
			//2. set up AllocatedOneTaskItem for each project team member with the first task row of the project plan
	
			ContentHandlerSpecs allocatdTaskSpecs = commonData.getContentHandlerSpecsMap().get(CONTENTTYPE2_AllocatdTask);
	
			for (int resourceRowNum = 0; resourceRowNum < resourcePlanXlJavaObj.getTotalDetailRows(); resourceRowNum++) {
	
				String teamMember = (String) resourcePlanXlJavaObj.getColValAtObjRowNumFor(resourceRowNum, RESOURCEPLANSH_ResourceID_COLHDR);
	
				String allocatdTaskArtifactName = AllocatdTask.getAllocatdTaskArtifactName (
														teamMember, allocatdTaskSpecs, projTrackerPojo.artifactName, 
														(String) projTasksXlJavaObj.getColValAtObjRowNumFor(PROJPLANSH_KICKOFF_ROWNUM,PROJPLANSH_TaskID_COLHDR));
	
				ArtifactKeyPojo artifactKeyPojo = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
															invokedArtifactPojo.artifactKeyPojo.relevance, 
															allocatdTaskArtifactName, allocatdTaskSpecs.contentType);
				SelfAuthoredArtifactpojo extdAllocatedOneTaskSelfAuthoredArtifactpojo = setupDraftArtifact(teamMember, artifactKeyPojo);
				ExtendedHandler allocatdTaskItemHandler = XtdContntHandlerManager.getInstance(xtdCommons, 
														(XtdStdRtCtCatlogPersistenceManager) commonData.getCatelogPersistenceManager(), 
														allocatdTaskSpecs.contentType);
				System.out.println("At 3 of q4324324ycc");
				allocatdTaskItemHandler.initializeExtendedHandlerForExtdSrvrProcess(commonData, extdAllocatedOneTaskSelfAuthoredArtifactpojo);
	
				AllocatdTaskItemPojo allocatedTaskItemPojo = (AllocatdTaskItemPojo) allocatdTaskItemHandler.getFocusedItemPojo();
				setAllocatdTaskItemFromProjTaskXlRowObj(projTrackerPojo, allocatedTaskItemPojo,projTasksXlJavaObj,PROJPLANSH_KICKOFF_ROWNUM);
				System.out.println("At 4 of q4324324y");
				allocatdTaskItemHandler.writePrimer();
				updateOtherDraftStatusForUpload(extdAllocatedOneTaskSelfAuthoredArtifactpojo);
			}
	
			processEndingStatus = setSummaryShKeyColumnVal();
			if (processEndingStatus.equalsIgnoreCase(XtdStdProcessRecord.ERLRecord_ERROR) ) {
				return processEndingStatus;
			}
			
			projTrackerPojo.corePlanChanged = false; // reset the flag once taken for extended processing completed.

			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in XtdProjTracker processXtdStdProcessRec " + inXtdProcStatus, e);			
		}
		System.out.println("at end of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		//Skipping the subsequent default process to avoid creating another ERL version
		processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;		
		
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

	private SelfAuthoredArtifactpojo setupDraftArtifact(String inUserName, ArtifactKeyPojo inArtifactKeyPojo){
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo,commonData, inUserName);
		SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
		extdSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		catelogPersistenceManager.insertArtifactUI(extdSelfAuthoredArtifactpojo);
		return extdSelfAuthoredArtifactpojo;
	}

	@Override
	public String absorbInput(Object inInput, String inInstruction) {

		String processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;
		ProjTrackerPojo projTrackerPojo = (ProjTrackerPojo) primerDoc.getItem();

		System.out.println("ProjTrackerX instruction for XtdProjTracker is " + inInstruction);
		System.out.println("ProjTrackerX instruction for XtdProjTracker is " + inInstruction);
		System.out.println("ProjTrackerX instruction for XtdProjTracker is " + inInstruction);
		System.out.println("ProjTrackerX instruction for Artifact " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		System.out.println("at 1234AB of XtdProjTracker absorbInput inInstruction is " + inInstruction);

		try {

			if (inInstruction.equalsIgnoreCase(INSTRUCTION_ABSORB_ProjTaskItemList)){
				//Steps
				// 1) absorb tasks
				//	1.a) build xlobj of existing tasks sheet; build task pointers hashmap
				//	  b) move task item values into corresponding rows - insert new rows as necessary
				//    c) write xl file tasks
				//	2.a) build xlobj of existing impediments sheet; build impediment pointers hashmap
				//	  b) move impediment item values into corresponding rows - insert new rows as necessary
				//    c) write xl for impediments
				//	3.a) build xlobj of existing defects sheet; build defect pointers hashmap
				//	  b) move defects item values into corresponding rows - insert new rows as necessary
				//    c) write xl for defects
	
				// 1) absorb tasks
				//	1.a) build xlobj of existing tasks sheet; build task pointers hashmap
	
				ArrayList<ProjTaskItemPojo> projTasksItemList = (ArrayList<ProjTaskItemPojo>) inInput;
	
				System.out.println("ProjTrackerX Gone into INSTRUCTION_ABSORB_ProjTaskItemList " + INSTRUCTION_ABSORB_ProjTaskItemList);
				System.out.println("ProjTrackerX Gone into inInput " + inInput);
	
				ExcelJavaObj projTasksXlJavaObj = getProjTaskXlJavaObjForTaskDetailsUpdateIntoSh();
	
				if (projTasksXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
					processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
					System.out.println("at 1234AD of XtdProjTracker absorbInput error leaving");
					return processEndingStatus;
				}
	
				System.out.println("at 1234AE of XtdProjTracker absorbInput projTasksItemList size is " + projTasksItemList.size());
				for (ProjTaskItemPojo projTaskItemPojo : projTasksItemList) {
					// all tasks are expected to be present in the tracker plan sheet
					System.out.println("at 1234AE of XtdProjTracker absorbInput projTaskItemPojo is " + projTaskItemPojo);
					System.out.println("at 1234AE of XtdProjTracker absorbInput projTaskItemPojo.taskID is " + projTaskItemPojo.taskID);
					int taskRowNum = projTasksXlJavaObj.getObjRowNumForKey(projTaskItemPojo.taskID);
					System.out.println("at 1234AE of XtdProjTracker absorbInput taskRowNum is " + taskRowNum);
					if (taskRowNum > -1) {	// this check is done to skip manual hours booking which maynot have correct task
						moveTaskItemDataIntoPlanShRowObj(projTaskItemPojo,projTasksXlJavaObj,taskRowNum);
					}
				}
				System.out.println("at 1234AF of XtdProjTracker absorbInput");
	
				excelHandler.replaceFromXlJavaObj(projTasksXlJavaObj, ProjPlan_SHEETNAME);
				
				//Labor cost update starts
				//Labor cost update starts
				//Whenever tasklist is updated, also refresh the labor cost in resource sheet
				
				ProjTaskItemPojo firstTaskItemPojo = projTasksItemList.get(0);
				HashMap<String, Double> effortsOfUsersInTeam = ((XtdTmShCatlogPersistenceMgr) catelogPersistenceManager).
																			readEffortsOfUsersInTeam(
																				firstTaskItemPojo.relevance, 
																				firstTaskItemPojo.projectName);					

				ExcelJavaObj resourcePlanWithCostXlJavaObj = getResourcePlanXlJavaObjFromSh();
				
				if (resourcePlanWithCostXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
					processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
					return processEndingStatus;
				}

				System.out.println("XtdProjTracker effortsOfUsersInTeam size " + effortsOfUsersInTeam.size());

				for (Entry<String, Double> effortsOfUsersEntrySet : effortsOfUsersInTeam.entrySet()) {
					String userID = effortsOfUsersEntrySet.getKey();
					Double hoursBooked = effortsOfUsersEntrySet.getValue();

					System.out.println("at 1234AE of XtdProjTracker effortsOfUsersInTeam userID is " + userID);
					System.out.println("at 1234AE of XtdProjTracker effortsOfUsersInTeam hoursBooked is " + hoursBooked);
					int resourceRowNum = resourcePlanWithCostXlJavaObj.getCreateXlObjRow(userID);
					System.out.println("at 1234AE of XtdProjTracker absorbInput taskRowNum is " + resourceRowNum);
					moveResourceEffortIntoResourcePlanShRowObj(userID, hoursBooked,resourcePlanWithCostXlJavaObj,resourceRowNum);
				}
				System.out.println("at 1234AF of XtdProjTracker absorbInput");
	
				excelHandler.replaceFromXlJavaObj(resourcePlanWithCostXlJavaObj, ResourcePl_SHEETNAME);
				//Labor cost update ends
				//Labor cost update ends
				
	
			} else if (inInstruction.equalsIgnoreCase(INSTRUCTION_ABSORB_ImpedimentItemsList)){
	
				//	2.a) build xlobj of existing impediments sheet; build impediment pointers hashmap
				//	  b) move impediment item values into corresponding rows - insert new rows as necessary
				//	  b.2) update the open impediments count field in the task sheet
				//    c) write xl for impediments
	
				ArrayList<ImpedimentItemPojo> impedimentItemsList = (ArrayList<ImpedimentItemPojo>) inInput;
				
				System.out.println("at 1234AG of XtdProjTracker absorbInput impedimentItemsList size is " + impedimentItemsList.size());
				System.out.println("at 1234AG of XtdProjTracker absorbInput ");
			
				ExcelJavaObj impedimentsXlJavaObj = getImpedimentsBaseXlJavaObj();
		
				if (impedimentsXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
					processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
					System.out.println("at 1234AH of XtdProjTracker absorbInput error");
					return processEndingStatus;
				}
	
				for (int impCount = 0; impCount < impedimentItemsList.size(); impCount++) {
					ImpedimentItemPojo impedimentItemPojo = (ImpedimentItemPojo) impedimentItemsList.get(impCount);
					int impledimentXlObjRowNum = impedimentsXlJavaObj.getCreateXlObjRow(impedimentItemPojo.impedimentID);
					moveImpedimentItemDataIntoImpedimentShRowObj(impedimentItemPojo,impedimentsXlJavaObj,impledimentXlObjRowNum);
				}
	
				excelHandler.replaceFromXlJavaObj(impedimentsXlJavaObj, Impediments_SHEETNAME);
	
				//2.b.2) update the open impediments count field in the task sheet
				{
					ExcelJavaObj tasksForImpedUpdateXlJavaObj = getProjTaskXlJavaObjForTaskImpedimentsCntUpdateIntoSh();
					
					if (tasksForImpedUpdateXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
						processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
						System.out.println("at 1234AH imped of XtdProjTracker absorbInput error");
						return processEndingStatus;
					}
	
					HashMap<String,ArrayList<ImpedimentItemPojo>> impedsOfTasks = Impediment.getImpedimentsOfTasks(impedimentItemsList);
					System.out.println("at 1234AF321 imped of XtdProjTracker absorbInput impedimentItemsList size is " + impedimentItemsList.size());
					System.out.println("at 1234AF321 imped of XtdProjTracker absorbInput impedsOfTasks.keySet().size() is " + impedsOfTasks.keySet().size());
	
					for (Entry<String, ArrayList<ImpedimentItemPojo>> taskImpedimentsEntrySet : impedsOfTasks.entrySet()) {
						// all tasks are expected to be present in the tracker plan sheet
	
						ArrayList<ImpedimentItemPojo> associatedOpenImpediments = Impediment.filterOpenImpediments(taskImpedimentsEntrySet.getValue());
	
						System.out.println("at 1234AF321 imped of XtdProjTracker absorbInput impedimentItemsList size is " + associatedOpenImpediments.size());
						System.out.println("at 1234AF321 imped of XtdProjTracker absorbInput taskImpedimentsEntrySet.getKey() is " + taskImpedimentsEntrySet.getKey());
											
						int taskRowNum = tasksForImpedUpdateXlJavaObj.getObjRowNumForKey(taskImpedimentsEntrySet.getKey());
	
						System.out.println("at 1234AF imped of XtdProjTracker absorbInput taskRowNum is " + taskRowNum);
	
						moveTaskOpenImpedCntIntoPlanShRowObj(associatedOpenImpediments.size(),tasksForImpedUpdateXlJavaObj, taskRowNum);
					}
					System.out.println("at 1234AF imped of XtdProjTracker absorbInput");
		
					excelHandler.replaceFromXlJavaObj(tasksForImpedUpdateXlJavaObj, ProjPlan_SHEETNAME);
				}
	
			} else if (inInstruction.equalsIgnoreCase(INSTRUCTION_ABSORB_DefectsList)){
				
				//	3.a) build xlobj of existing defects sheet; build defect pointers hashmap
				//	  b) move defects item values into corresponding rows - insert new rows as necessary
				//    c) write xl for defects
	
				ArrayList<DefectItemPojo> defectItemsList = (ArrayList<DefectItemPojo>) inInput;
				System.out.println("at 1234AF of XtdProjTracker absorbInput defectItemsList size is " + defectItemsList.size());
				
				ExcelJavaObj defectsXlJavaObj = getDefectsBaseXlJavaObj();
				
				if (defectsXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
					processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
					return processEndingStatus;
				}
	
				System.out.println("at safd23454233 absorbInput defectItemsList.size() = " + defectItemsList.size());
				
				for (int defectCount = 0; defectCount < defectItemsList.size(); defectCount++) {
					DefectItemPojo defectItemPojo = (DefectItemPojo) defectItemsList.get(defectCount);
					System.out.println("at safd234542331 absorbInput defectItemPojo item is  = " + defectItemPojo.itemID);
					int defectXlRowNum = defectsXlJavaObj.getCreateXlObjRow(defectItemPojo.defectID);
					
					System.out.println("at safd234542332 absorbInput defectCount = " + defectCount);
					moveDefectItemDataIntoDefectShRowObj(defectItemPojo,defectsXlJavaObj,defectXlRowNum);
				}
				excelHandler.replaceFromXlJavaObj(defectsXlJavaObj, Defects_SHEETNAME);
				
				//3.b.2) update the  open defects count field in the task sheet
				{
					ExcelJavaObj tasksForDefectUpdateXlJavaObj = getProjTaskXlJavaObjForTaskDefectsCntUpdateIntoSh();
					
					if (tasksForDefectUpdateXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
						processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
						System.out.println("at 1234AH defect of XtdProjTracker absorbInput error");
						return processEndingStatus;
					}
	
					HashMap<String,ArrayList<DefectItemPojo>> defctsOfTasks = Defect.getDefectsOfTasks(defectItemsList);
	
					for (Entry<String, ArrayList<DefectItemPojo>> taskDefectsEntrySet : defctsOfTasks.entrySet()) {
	
						ArrayList<DefectItemPojo> associatedOpenDefects = Defect.filterOpenDefects(taskDefectsEntrySet.getValue());
						
						int taskRowNum = tasksForDefectUpdateXlJavaObj.getObjRowNumForKey(taskDefectsEntrySet.getKey());
						moveTaskOpenDefectCntIntoPlanShRowObj(associatedOpenDefects.size(),tasksForDefectUpdateXlJavaObj,taskRowNum);
					}
					System.out.println("at 1234AF def of XtdProjTracker absorbInput");
		
					excelHandler.replaceFromXlJavaObj(tasksForDefectUpdateXlJavaObj, ProjPlan_SHEETNAME);
				}
			}
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
			System.out.println("at end of 23454233 absorbInput inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		} catch (IOException | ParseException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in XtdProjTracker absorbInput" + inInput + " " + inInstruction, e);
		}
		
		return processEndingStatus;		
	}

	private ExcelJavaObj getResourcePlanXlJavaObjFromSh() throws IOException {
		ExcelJavaObj resourcePlanXlObj = new ExcelJavaObj();
		resourcePlanXlObj.addColOfInterest(RESOURCEPLANSH_ResourceID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		resourcePlanXlObj.addColOfInterest(RESOURCEPLANSH_RoleID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		resourcePlanXlObj.addColOfInterest(RESOURCEPLANSH_HourlyRate_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		resourcePlanXlObj.addColOfInterest(RESOURCEPLANSH_EffortHrsSpent_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		resourcePlanXlObj.initKeyColumn(RESOURCEPLANSH_ResourceID_COLHDR);

		System.out.println("At 5a of q4324324a");
		System.out.println("At 6a of q4324324a");
		excelHandler.buildXlJavaObj(resourcePlanXlObj, ResourcePl_SHEETNAME);
		System.out.println("At 7a of q4324324a");

		if (resourcePlanXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = resourcePlanXlObj.processStateMsg + " while at processXtdStdProcessRec1 for " + invokedArtifactPojo.artifactKeyPojo.artifactName;

			System.out.println("at getResourcePlanXlJavaObjFromSh. resourcePlanXlJavaObj resourcePlanXlJavaObj is " + resourcePlanXlObj.processStateMsg);
			System.out.println("at getResourcePlanXlJavaObjFromSh. resourcePlanXlJavaObj.processStatus is " + resourcePlanXlObj.processStatus);
			
			System.out.println(message);
			if (resourcePlanXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				xtdCommons.logger.warn(message);
			} else {
				xtdCommons.logger.error(message);
			}
		}
		return resourcePlanXlObj;
	}

	private ExcelJavaObj getDefectsBaseXlJavaObj() throws IOException {
		ExcelJavaObj defectsXlObj = new ExcelJavaObj();

		defectsXlObj.addColOfInterest(DEFECTSH_DefectID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		defectsXlObj.addColOfInterest(DEFECTSH_DefectDesc_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		defectsXlObj.addColOfInterest(DEFECTSH_Severity_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		defectsXlObj.addColOfInterest(DEFECTSH_RaisedBy_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		defectsXlObj.addColOfInterest(DEFECTSH_Owner_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		defectsXlObj.addColOfInterest(DEFECTSH_OpenedDate_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		defectsXlObj.addColOfInterest(DEFECTSH_Status_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		defectsXlObj.addColOfInterest(DEFECTSH_ExpectedEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		defectsXlObj.addColOfInterest(DEFECTSH_ActualEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		defectsXlObj.addColOfInterest(DEFECTSH_PctgCompleted_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		defectsXlObj.addColOfInterest(DEFECTSH_BurntHours_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		defectsXlObj.addColOfInterest(DEFECTSH_Remark_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		defectsXlObj.initKeyColumn(DEFECTSH_DefectID_COLHDR);

		System.out.println("At 5 of q4324324Def");
		System.out.println("At 6 of q4324324Def");
		excelHandler.buildXlJavaObj(defectsXlObj, Defects_SHEETNAME);
		System.out.println("At 7 of q4324324Def");

		if (defectsXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = defectsXlObj.processStateMsg + " while at absortInput defects for " + invokedArtifactPojo.artifactKeyPojo.artifactName;
			System.out.println(message);
			if (defectsXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				xtdCommons.logger.warn(message);
			} else {
				xtdCommons.logger.error(message);
			}
		}
		return defectsXlObj;
	}

	private ExcelJavaObj getImpedimentsBaseXlJavaObj() throws IOException {
		ExcelJavaObj impedimentsXlObj = new ExcelJavaObj();

		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_ImpedimentID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_ImpedimentDesc_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_Severity_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_Owner_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_OpenedDate_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_Status_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_ExpectedEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_ActualEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_PctgCompleted_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_BurntHours_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		impedimentsXlObj.addColOfInterest(IMPEDIMENTSH_Remark_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		impedimentsXlObj.initKeyColumn(IMPEDIMENTSH_ImpedimentID_COLHDR);

		System.out.println("At 5 of q4324324Imp");
		System.out.println("At 6 of q4324324Imp");
		excelHandler.buildXlJavaObj(impedimentsXlObj, Impediments_SHEETNAME);
		System.out.println("At 7 of q4324324Imp");

		if (impedimentsXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = impedimentsXlObj.processStateMsg + " while at absortInput impediments for " + invokedArtifactPojo.artifactKeyPojo.artifactName;
			System.out.println(message);
			if (impedimentsXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				xtdCommons.logger.warn(message);
			} else {
				xtdCommons.logger.error(message);
			}
		}
		
		return impedimentsXlObj;
	}
	
	private ExcelJavaObj getProjTaskXlJavaObjFromShForTasksUpdate() throws IOException {
		ExcelJavaObj projTasksXlObj = new ExcelJavaObj();

		projTasksXlObj.addColOfInterest(PROJPLANSH_TaskID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_TaskDesc_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Owner_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Lead_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_PlannedHours_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.addColOfInterest(PROJPLANSH_PlannedStart_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.addColOfInterest(PROJPLANSH_PlannedEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.initKeyColumn(PROJPLANSH_TaskID_COLHDR);

		System.out.println("At 5 of q4324324 task");
		System.out.println("At 6 of q4324324 task");
		excelHandler.buildXlJavaObj(projTasksXlObj, ProjPlan_SHEETNAME);
		System.out.println("At 7 of q4324324 task");

		if (projTasksXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = projTasksXlObj.processStateMsg + " while at processXtdStdProcessRec2 for " + invokedArtifactPojo.artifactKeyPojo.artifactName;
			System.out.println("at getProjTaskXlJavaObjFromShForTasksUpdate. resourcePlanXlJavaObj.processStatus is " + projTasksXlObj.processStatus);
			System.out.println(message);
			if (projTasksXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				XtdCommons.logger.warn(message);
			} else {
				XtdCommons.logger.error(message);
			}
		}

		return projTasksXlObj;
	}

	private ExcelJavaObj getProjTaskXlJavaObjForTaskDetailsUpdateIntoSh() throws IOException {
		ExcelJavaObj projTasksXlObj = new ExcelJavaObj();

		projTasksXlObj.addColOfInterest(PROJPLANSH_TaskID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Status_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_ActualStart_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.addColOfInterest(PROJPLANSH_ActualEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.addColOfInterest(PROJPLANSH_BurntHours_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.addColOfInterest(PROJPLANSH_EstimatedOverrunEffortToComplete_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.addColOfInterest(PROJPLANSH_EstimatedEffortToComplete_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.addColOfInterest(PROJPLANSH_ExpectedEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Remark_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		
		projTasksXlObj.initKeyColumn(PROJPLANSH_TaskID_COLHDR);

		System.out.println("At 5 of q4324324TaskDetails");
		System.out.println("At 6 of q4324324TaskDetails");
		excelHandler.buildXlJavaObj(projTasksXlObj, ProjPlan_SHEETNAME);
		System.out.println("At 7 of q4324324TaskDetails");

		if (projTasksXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = projTasksXlObj.processStateMsg + " while at absortInput taskslist for " + invokedArtifactPojo.artifactKeyPojo.artifactName;
			System.out.println(message);
			if (projTasksXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				xtdCommons.logger.warn(message);
			} else {
				xtdCommons.logger.error(message);
			}
		}
		return projTasksXlObj;
	}

	private ExcelJavaObj getProjTaskXlJavaObjForTaskDefectsCntUpdateIntoSh() throws IOException {
		ExcelJavaObj projTasksXlObj = new ExcelJavaObj();

		projTasksXlObj.addColOfInterest(PROJPLANSH_TaskID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_OpenDefectsCnt_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.initKeyColumn(PROJPLANSH_TaskID_COLHDR);

		System.out.println("At 5 of q4324324def excelHandler " + excelHandler);
		System.out.println("At 6 of q4324324def");
		excelHandler.buildXlJavaObj(projTasksXlObj, ProjPlan_SHEETNAME);
		System.out.println("At 7 of q4324324def");

		if (projTasksXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = projTasksXlObj.processStateMsg + " while at absortInput defects into tasks sheet for " + invokedArtifactPojo.artifactKeyPojo.artifactName;
			System.out.println(message);
			if (projTasksXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				xtdCommons.logger.warn(message);
			} else {
				xtdCommons.logger.error(message);
			}
		}

		return projTasksXlObj;
	}

	private ExcelJavaObj getProjSummaryXlJavaObjForProjNameUpdateIntoSh() throws IOException {
		ExcelJavaObj projSummaryXlObj = new ExcelJavaObj();

		projSummaryXlObj.addColOfInterest(PROJSUMSH_EngagementID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projSummaryXlObj.initKeyColumn(PROJSUMSH_EngagementID_COLHDR);

		System.out.println("At 5 of q5432243def");
		System.out.println("At 6 of q5432243def excelHandler " + excelHandler);
		excelHandler.buildXlJavaObj(projSummaryXlObj, ProjSummary_SHEETNAME);
		System.out.println("At 7 of q5432243def");

		if (projSummaryXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = projSummaryXlObj.processStateMsg + " At XtdProjTracker getProjSummaryXlJavaObjForProjNameUpdateIntoSh " + invokedArtifactPojo.artifactKeyPojo.artifactName;
			System.out.println(message);
			if (projSummaryXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				xtdCommons.logger.warn(message);
			} else {
				xtdCommons.logger.error(message);
			}
		}
		return projSummaryXlObj;
	}

	private ExcelJavaObj getProjTaskXlJavaObjForTaskImpedimentsCntUpdateIntoSh() throws IOException {
		ExcelJavaObj projTasksXlObj = new ExcelJavaObj();

		projTasksXlObj.addColOfInterest(PROJPLANSH_TaskID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_OpenImpedsCnt_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.initKeyColumn(PROJPLANSH_TaskID_COLHDR);

		System.out.println("At 5 of q4324324imp");
		System.out.println("At 6 of q4324324imp");
		excelHandler.buildXlJavaObj(projTasksXlObj, ProjPlan_SHEETNAME);
		System.out.println("At 7 of q4324324imp");

		if (projTasksXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = projTasksXlObj.processStateMsg + " while at absortInput impediments into tasks sheet for " + invokedArtifactPojo.artifactKeyPojo.artifactName;
			System.out.println(message);
			if (projTasksXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				XtdCommons.logger.warn(message);
			} else {
				XtdCommons.logger.error(message);
			}
		}

		return projTasksXlObj;
	}
	
	private ExcelJavaObj getProjTaskXlJavaObjForTaskShUpdateForTasks() {
		ExcelJavaObj projTasksXlObj = new ExcelJavaObj();

		projTasksXlObj.addColOfInterest(PROJPLANSH_TaskID_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_TaskDesc_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Owner_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Lead_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Status_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.addColOfInterest(PROJPLANSH_ActualStart_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.addColOfInterest(PROJPLANSH_ActualEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.addColOfInterest(PROJPLANSH_BurntHours_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.addColOfInterest(PROJPLANSH_EstimatedOverrunEffortToComplete_COLHDR, ExcelColJavaObj.COLTYPE_NUMERIC);
		projTasksXlObj.addColOfInterest(PROJPLANSH_EstimatedEffortToComplete_COLHDR, ExcelColJavaObj.COLTYPE_FORMULA);
		projTasksXlObj.addColOfInterest(PROJPLANSH_ExpectedEnd_COLHDR, ExcelColJavaObj.COLTYPE_DATE);
		projTasksXlObj.addColOfInterest(PROJPLANSH_Remark_COLHDR, ExcelColJavaObj.COLTYPE_STRING);
		projTasksXlObj.initKeyColumn(PROJPLANSH_TaskID_COLHDR);

		return projTasksXlObj;
	}

	private void setAllocatdTaskItemFromProjTaskXlRowObj(ProjTrackerPojo inProjTrackerPojo, AllocatdTaskItemPojo inAllocatedTaskItem, ExcelJavaObj inProjTasksXlJavaObj, int inTaskRowNum) {
		inAllocatedTaskItem.updatedAt = commons.getDateTS();
		inAllocatedTaskItem.teamID = inProjTrackerPojo.artifactName;
		inAllocatedTaskItem.taskID = (String) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_TaskID_COLHDR);
		inAllocatedTaskItem.description = (String) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_TaskDesc_COLHDR);
		inAllocatedTaskItem.timeEstimated = (Double) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedHours_COLHDR);
		inAllocatedTaskItem.plannedStart = (Date) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedStart_COLHDR);
		inAllocatedTaskItem.plannedEnd = (Date) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedEnd_COLHDR);
	}
	
	private void setProjTaskItemFromProjTaskXlRowObj(ProjTrackerPojo inProjTrackerPojo, ProjTaskItemPojo inProjTaskItem, ExcelJavaObj inProjTasksXlJavaObj,int inTaskRowNum) {
		inProjTaskItem.updatedAt = commons.getDateTS();
		inProjTaskItem.projectName = inProjTrackerPojo.artifactName;
		inProjTaskItem.taskID = (String) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum,PROJPLANSH_TaskID_COLHDR);

		if (inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_TaskDesc_COLHDR)!=null){
			inProjTaskItem.description = (String) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_TaskDesc_COLHDR);
		} else {
			inProjTaskItem.description = "";
		}
		inProjTaskItem.author = (String) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_Owner_COLHDR);
		inProjTaskItem.lead = (String) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_Lead_COLHDR);
		if (inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedHours_COLHDR)!=null) {
			inProjTaskItem.plannedHours = (double) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedHours_COLHDR);
		} else {
			inProjTaskItem.plannedHours = 0;
		}
		if (inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedStart_COLHDR)!=null) {
			inProjTaskItem.plannedStart = (Date) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedStart_COLHDR);
		} else {
			inProjTaskItem.plannedStart = null;
		}
		if (inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedEnd_COLHDR)!=null) {
			inProjTaskItem.plannedEnd = (Date) inProjTasksXlJavaObj.getColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_PlannedEnd_COLHDR);
		} else {
			inProjTaskItem.plannedEnd = null;
		}
	}
	
	private void moveTaskItemDataIntoPlanShRowObj(ProjTaskItemPojo inProjTaskItem, ExcelJavaObj inProjTasksXlJavaObj, int inTaskRowNum) throws ParseException {
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_Status_COLHDR, inProjTaskItem.taskStatus);
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_ActualStart_COLHDR, commons.getDateFromString(inProjTaskItem.actualStart));
		try {
			inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_ActualEnd_COLHDR, commons.getDateFromDateOnlyString(inProjTaskItem.actualEnd));
		} catch (ParseException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error XtdProjTracker moveTaskItemDataIntoPlanShRowObj " + inProjTaskItem.artifactName, e);
		}
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_BurntHours_COLHDR, inProjTaskItem.burntHours);
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_EstimatedOverrunEffortToComplete_COLHDR, inProjTaskItem.estimatedOverrunEffortToComplete);
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_EstimatedEffortToComplete_COLHDR, inProjTaskItem.estimatedEffortToComplete);
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_ExpectedEnd_COLHDR, inProjTaskItem.expectedEnd);
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_Remark_COLHDR, inProjTaskItem.remark);
	}

	private void moveTaskOpenImpedCntIntoPlanShRowObj(int inOpenImpedimentsCntOfTask, ExcelJavaObj inProjTasksXlJavaObj, int inTaskRowNum) {
		System.out.println("At moveTaskOpenImpedCntIntoPlanShRowObj inOpenImpedimentsCntOfTask is " + inOpenImpedimentsCntOfTask);
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_OpenImpedsCnt_COLHDR, inOpenImpedimentsCntOfTask);
	}

	private void moveTaskOpenDefectCntIntoPlanShRowObj(int inOpenDefectsCntOfTask, ExcelJavaObj inProjTasksXlJavaObj, int inTaskRowNum) {
		System.out.println("At moveTaskOpenDefectCntIntoPlanShRowObj inOpenDefectsCntOfTask is " + inOpenDefectsCntOfTask);
		inProjTasksXlJavaObj.setColValAtObjRowNumFor(inTaskRowNum, PROJPLANSH_OpenDefectsCnt_COLHDR, inOpenDefectsCntOfTask);
	}
	
	private void moveImpedimentItemDataIntoImpedimentShRowObj(ImpedimentItemPojo inImpedimentItemPojo,ExcelJavaObj inImpedimentsXlJavaObj,int inImpledimentXlObjRowNum) {
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_ImpedimentID_COLHDR, inImpedimentItemPojo.impedimentID);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_ImpedimentDesc_COLHDR, inImpedimentItemPojo.description);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_Severity_COLHDR, inImpedimentItemPojo.severity);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_Owner_COLHDR, inImpedimentItemPojo.author);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_OpenedDate_COLHDR, inImpedimentItemPojo.openedDate);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_Status_COLHDR, inImpedimentItemPojo.impedimentStatus);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_ExpectedEnd_COLHDR, inImpedimentItemPojo.expectedEnd);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_ActualEnd_COLHDR, inImpedimentItemPojo.actualEnd);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_PctgCompleted_COLHDR, inImpedimentItemPojo.pctgCompleted);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_BurntHours_COLHDR, inImpedimentItemPojo.burntHours);
		inImpedimentsXlJavaObj.setColValAtObjRowNumFor(inImpledimentXlObjRowNum, IMPEDIMENTSH_Remark_COLHDR, inImpedimentItemPojo.remark);
	}
	
	private void moveDefectItemDataIntoDefectShRowObj(DefectItemPojo inDefectItemPojo, ExcelJavaObj inDefectsXlJavaObj, int inDefectXlRowNum) {		
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_DefectID_COLHDR, inDefectItemPojo.defectID);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_DefectDesc_COLHDR, inDefectItemPojo.description);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_Severity_COLHDR, inDefectItemPojo.severity);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_RaisedBy_COLHDR, inDefectItemPojo.raisedBy);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_Owner_COLHDR, inDefectItemPojo.author);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_OpenedDate_COLHDR, inDefectItemPojo.openedDate);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_Status_COLHDR, inDefectItemPojo.defectStatus);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_ExpectedEnd_COLHDR, inDefectItemPojo.expectedEnd);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_ActualEnd_COLHDR, inDefectItemPojo.actualEnd);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_PctgCompleted_COLHDR, inDefectItemPojo.pctgCompleted);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_BurntHours_COLHDR, inDefectItemPojo.burntHours);
		inDefectsXlJavaObj.setColValAtObjRowNumFor(inDefectXlRowNum, DEFECTSH_Remark_COLHDR, inDefectItemPojo.remark);		
	}

	private void moveResourceEffortIntoResourcePlanShRowObj(String inUserId, Double inHoursBooked, ExcelJavaObj inResourcePlanXlJavaObj, int inResourceXlRowNum) throws ParseException {
		if (inResourcePlanXlJavaObj.getShRowNumForKey(inUserId) == -1) {
			inResourcePlanXlJavaObj.setColValAtObjRowNumFor(inResourceXlRowNum, RESOURCEPLANSH_ResourceID_COLHDR, inUserId);
			inResourcePlanXlJavaObj.setColValAtObjRowNumFor(inResourceXlRowNum, RESOURCEPLANSH_RoleID_COLHDR, RESOURCEPLANSH_UnknownRole);
			inResourcePlanXlJavaObj.setColValAtObjRowNumFor(inResourceXlRowNum, RESOURCEPLANSH_HourlyRate_COLHDR, RESOURCEPLANSH_UnknownRate);
		}
		inResourcePlanXlJavaObj.setColValAtObjRowNumFor(inResourceXlRowNum, RESOURCEPLANSH_EffortHrsSpent_COLHDR, inHoursBooked);
	}

	@Override
	public String getDetailSheetName() {
		return ProjPlan_SHEETNAME;
	}

	@Override
	public String getSummarySheetName() {
		return ProjSummary_SHEETNAME;
	}

	@Override
	public String getSummaryShKeyColumnHdr() {
		return PROJSUMSH_EngagementID_COLHDR;
	}

	public String setSummaryShKeyColumnVal() {
		String processEndingStatus = "";
		ProjTrackerItemDoc projTrackerItemDoc = (ProjTrackerItemDoc) primerDoc;
		ProjTrackerPojo projTrackerPojo = projTrackerItemDoc.getProjTrackerPojo();
		{
			ExcelJavaObj projSummaryUpdateXlJavaObj;
			try {
				projSummaryUpdateXlJavaObj = getProjSummaryXlJavaObjForProjNameUpdateIntoSh();
				projSummaryUpdateXlJavaObj.setColValAtObjRowNumFor(0, PROJSUMSH_EngagementID_COLHDR, getSummaryShKeyColumnVal());
				if (projSummaryUpdateXlJavaObj.processStatus == ExcelJavaObj.PROCSTAT_ERROR) {
					processEndingStatus = XtdStdProcessRecord.ERLRecord_ERROR;	// error situation
					System.out.println("at 1234AH defect of XtdProjTracker setSummaryShKeyColumnVal error");
					return processEndingStatus;
				}
				excelHandler.replaceFromXlJavaObj(projSummaryUpdateXlJavaObj, ProjSummary_SHEETNAME);
			} catch (IOException e) {
				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(xtdCommons, "Error in XtdProjTracker setSummaryShKeyColumnVal " + invokedArtifactPojo.artifactKeyPojo.artifactName, e);
			}
		}
		return processEndingStatus;
	}
	
	@Override
	public String getSummaryShKeyColumnVal() {
		ProjTrackerItemDoc projTrackerItemDoc = (ProjTrackerItemDoc) primerDoc;
		ProjTrackerPojo projTrackerPojo = projTrackerItemDoc.getProjTrackerPojo();
		return projTrackerPojo.artifactName;
	}

	@Override
	public int getSummaryShKeyColSeqNum() {
		int summaryShKeyColSeqNum = -1;
		ProjTrackerItemDoc projTrackerItemDoc = (ProjTrackerItemDoc) primerDoc;
		ProjTrackerPojo projTrackerPojo = projTrackerItemDoc.getProjTrackerPojo();
		{
			ExcelJavaObj projSummaryUpdateXlJavaObj;
			try {
				projSummaryUpdateXlJavaObj = getProjSummaryXlJavaObjForProjNameUpdateIntoSh();
				summaryShKeyColSeqNum = projSummaryUpdateXlJavaObj.getShColSeqNumOfHdr(PROJSUMSH_EngagementID_COLHDR);
			} catch (IOException e) {
				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(xtdCommons, "Error in XtdProjTracker getSummaryShKeyColSeqNum " + invokedArtifactPojo.artifactKeyPojo.artifactName, e);
			}
		}
		return summaryShKeyColSeqNum;
	}	
}