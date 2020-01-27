package xtndHandlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import contentHandlers.ProjTask;
import contentHandlers.TimeShRollup;
import contentHandlers.TimeShRollupDoc;
import contentHandlers.TimeSheetPojo;
import espot.ArtifactKeyPojo;
import espot.ArtifactPojo;
import espot.ArtifactPrepper;
import espot.CommonData;
import espot.ContentHandlerSpecs;
import espot.ErrorHandler;
import espot.SelfAuthoredArtifactpojo;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdContntHandlerManager;
import xtdSrvrComp.XtdStdProcessRecord;
import xtdSrvrComp.XtdStdRtCtCatlogPersistenceManager;
import xtdSrvrComp.XtdTmShCatlogPersistenceMgr;

public class XtdTimeShRollup extends TimeShRollup implements ExtendedHandler {
	/*
	 * Handler for TimeShRollup extended processing
	 */

	final static String INITIATED_TYPE1CONTENT_ProjTask = "ProjTask";
	
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
		// this function will be called whenever the timesheetRollup changes
		// it processes in the below steps
		// 1) update the timesheet db with the cumulated hours for task/user/date
		// 2) invoke projTask extended process to update the hours and metrics reconciliation
		//		with the task/relevance

		System.out.println("at start of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		String processEndingStatus = inXtdProcStatus;
		TimeShRollupDoc timeShRollupDoc = (TimeShRollupDoc) primerDoc;

		// 1) accumulate hours and update db
		ArrayList<TimeSheetPojo> personTimeSheets = timeShRollupDoc.getItemList();
		HashMap<PersonTaskHoursKey,Integer> timeDetails = new HashMap<PersonTaskHoursKey,Integer>();
		
		for (TimeSheetPojo timeSheetPojo : personTimeSheets) {
			
			PersonTaskHoursKey personTaskHoursKey = null;
			personTaskHoursKey = new PersonTaskHoursKey(timeSheetPojo.taskID,
														timeSheetPojo.relevance,
														timeSheetPojo.teamID,
														timeSheetPojo.author,
														//commonData.getCommons().getDateOnly(commonData.getCommons().getDateFromString(timeSheetPojo.capturedAt)));
														timeSheetPojo.capturedAt);
			int existingHours = 0;
			if (timeDetails.containsKey(personTaskHoursKey)) {
				existingHours = timeDetails.get(personTaskHoursKey);
			}
			int cumulativeHours = existingHours + timeSheetPojo.hoursLogged;
			timeDetails.put(personTaskHoursKey,cumulativeHours);
		}
		
		XtdTmShCatlogPersistenceMgr xtdTmShCatlogPersistenceMgr = (XtdTmShCatlogPersistenceMgr) commonData.getCatelogPersistenceManager();

		for (PersonTaskHoursKey personTaskHoursKey : timeDetails.keySet()) {			
			xtdTmShCatlogPersistenceMgr.replaceTimeDetail(personTaskHoursKey.taskID, 
											personTaskHoursKey.relevance, 
											personTaskHoursKey.teamID, 
											personTaskHoursKey.userID, 
											personTaskHoursKey.date, 
											timeDetails.get(personTaskHoursKey));
		}

		if (personTimeSheets.size() == 0) {
			processEndingStatus = XtdStdProcessRecord.ERLRecord_SKIP;
			return processEndingStatus; // nothing changed to do extensive process
		}

		//2. set up ProjTaskItem from process each task row of the project plan, 

		HashMap<ProjTaskKey,ArtifactKeyPojo> projTaskItemKeys = new HashMap<ProjTaskKey,ArtifactKeyPojo>();

		ContentHandlerSpecs projTaskSpecs = commonData.getContentHandlerSpecsMap().get(INITIATED_TYPE1CONTENT_ProjTask);
		
		for (TimeSheetPojo timeSheetPojo : personTimeSheets) {
			ProjTaskKey projTaskKey = new ProjTaskKey(timeSheetPojo.teamID, timeSheetPojo.taskID,timeSheetPojo.relevance);
			if (!projTaskItemKeys.containsKey(projTaskKey)) {
				String projTaskArtifactName = ProjTask.getProjTaskArtifactName(projTaskSpecs,timeSheetPojo.teamID,timeSheetPojo.taskID);
				ArtifactKeyPojo projTaskitemKey = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick,timeSheetPojo.relevance,projTaskArtifactName,INITIATED_TYPE1CONTENT_ProjTask);
				projTaskItemKeys.put(projTaskKey,projTaskitemKey);
			}
		}

		for (ProjTaskKey projTaskKey : projTaskItemKeys.keySet()) {

			String projTaskArtifactName = ProjTask.getProjTaskArtifactName (
					projTaskSpecs, projTaskKey.teamID,projTaskKey.taskID);
			
			ArtifactKeyPojo taskArtifactKeyPojo = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
														projTaskKey.relevance,
														projTaskArtifactName, INITIATED_TYPE1CONTENT_ProjTask);
			SelfAuthoredArtifactpojo extdProjTaskArtifactpojo = setupDraftChildArtifact(commonData.getCommons().userName, taskArtifactKeyPojo);
			if (extdProjTaskArtifactpojo!=null) { // skip for timesh added manually which may not have alignment with a project
				ExtendedHandler xtdProjTaskItemHandler = XtdContntHandlerManager.getInstance(xtdCommons, 
						(XtdStdRtCtCatlogPersistenceManager) commonData.getCatelogPersistenceManager(), 
						INITIATED_TYPE1CONTENT_ProjTask);
				System.out.println("At 3 of q4324324y");
				xtdProjTaskItemHandler.initializeExtendedHandlerForExtdSrvrProcess(commonData, extdProjTaskArtifactpojo);

				processEndingStatus = xtdProjTaskItemHandler.processXtdStdProcessRec(processEndingStatus);
				if (processEndingStatus.equalsIgnoreCase(XtdStdProcessRecord.ERLRecord_ERROR)) {
					return processEndingStatus;
				}
				updateOtherDraftStatusForUpload(extdProjTaskArtifactpojo);
			} else {
				commons.logger.warn("teamID & taskID not mapped to any proj task. Team = " + projTaskKey.teamID + ". Proj = " + projTaskKey.taskID );
				System.out.println("teamID & taskID not mapped to any proj task. Team = " + projTaskKey.teamID + ". Proj = " + projTaskKey.taskID );
			}
		}

		processEndingStatus = XtdStdProcessRecord.ERLRecord_PROCESSED;
		
		try {
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in XtdTimeShRollup processXtdStdProcessRec " + inXtdProcStatus, e);
		}
		System.out.println("at end of 23454233 processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);

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

	private SelfAuthoredArtifactpojo setupDraftChildArtifact(String inUserName, ArtifactKeyPojo inArtifactKeyPojo){
		SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = null;
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo, commonData, inUserName);
		if (artifactPrepper.useErlDownloadRolledupChild || artifactPrepper.useActiveDraft) {
			
			extdSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
			extdSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
			commonData.getCatelogPersistenceManager().insertArtifactUI(extdSelfAuthoredArtifactpojo);
		}

		return extdSelfAuthoredArtifactpojo;
	}

	@Override
	public String absorbInput(Object inInput, String inInstruction) {
		String processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;
		return processEndingStatus;
	}
}

class PersonTaskHoursKey {
	String taskID;
	String relevance;
	String teamID;
	String userID;
	String date;	//only date part without time
	PersonTaskHoursKey(String inTaskID,
			String inRelevance,
			String inTeamID,
			String inUserID, String inDate) {
		taskID = inTaskID;
		relevance = inRelevance;
		teamID = inTeamID;
		userID = inUserID;
		date  = inDate;
	}

	public boolean equals(Object obj) {		//overriding equals and hashcode functions for 
											// any sorting and indexing
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PersonTaskHoursKey other = (PersonTaskHoursKey) obj;
        if (other.taskID == null || other.relevance == null 
        		|| other.teamID == null || other.userID == null)  return false;        
        if (!taskID.equalsIgnoreCase(other.taskID)) return false;
        if (!relevance.equalsIgnoreCase(other.relevance)) return false;
        if (!teamID.equalsIgnoreCase(other.teamID)) return false;
        if (!userID.equalsIgnoreCase(other.userID)) return false;
        if (date != other.date) return false;
        return true;
    }    
}

class ProjTaskKey {
	String teamID;
	String taskID;
	String relevance;
	ProjTaskKey (String inTeamID, String inTaskID,
			String inRelevance) {
		teamID = inTeamID;
		taskID = inTaskID;
		relevance = inRelevance;
	}
	public boolean equals(Object obj) {		//overriding equals and hashcode functions for 
											// any sorting and indexing
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ProjTaskKey other = (ProjTaskKey) obj;
        if (other.teamID == null || other.taskID == null || other.relevance == null)  return false;        
        if (!taskID.equalsIgnoreCase(other.taskID)) return false;
        if (!teamID.equalsIgnoreCase(other.teamID)) return false;
        if (!relevance.equalsIgnoreCase(other.relevance)) return false;
        return true;
    }    
}