package contentHandlers;

import espot.ItemPojo;

public class TimeShTriggerPojo extends ItemPojo{
	/*
	 * Data holder for time sheet triggering item
	 */

	public String description;
	public String reviewer;
	public int captureInterval;
	public int allocationInterval;
	public boolean extProcTriggerInitiated; //this flag will be set at the extended processor
	public String lastTriggeredAt;
	public String captureStartDate;
	public String captureEndDate;

	TimeShTriggerPojo(int inItemNumber){
		super (inItemNumber);
		initializeAdditionalItemPojoFields();
        System.out.println("After super construction of ItemPojo title = " + title);		
	}
	
	void initializeAdditionalItemPojoFields(){
		description = "";
		reviewer = "";
		captureInterval = 0;
		allocationInterval = 0;
		extProcTriggerInitiated = false;
		lastTriggeredAt=null;
		captureStartDate=null;
		captureEndDate=null;
		System.out.println("@@123 From TimeShTriggerItemDoc initializeAdditionalItemPojoFields done");
	}
}