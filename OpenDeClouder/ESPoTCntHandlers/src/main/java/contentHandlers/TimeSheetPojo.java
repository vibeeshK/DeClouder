package contentHandlers;

import espot.ItemPojo;

public class TimeSheetPojo extends ItemPojo{
	/*
	 * Data holder for a timesheet item
	 */
	public static int ALLOCATION_TYPE_PREALLOCATED = 10;
	public static int ALLOCATION_TYPE_PREVUSED = 20;
	public static int ALLOCATION_TYPE_MANUALADD = 30;

	public String taskID;
	public String teamID;
	public String description;
	public String capturedAt;
	public int hoursLogged;
	public String attachments;
	public String reviewer;
	public int timeAllocationType;

	TimeSheetPojo(int inItemNumber){
		super (inItemNumber);
		initializeAdditionalItemPojoFields();
	}
	
	void initializeAdditionalItemPojoFields(){
		taskID = "";
		teamID = "";
		description = "";
		hoursLogged = 0; // keep the default as 1 hour for each time someone clicks on timeSheet popup
		capturedAt = null;
		attachments = "";
		reviewer = "";
		timeAllocationType = 0;
	}
}