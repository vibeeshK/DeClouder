package contentHandlers;

import java.util.Date;

import espot.ItemPojo;

public class AllocatdTaskItemPojo extends ItemPojo{
	/*
	 * Data holder for the allocated task item
	 */

	public String teamID;	// team ID represents the project or 
							// the maintenance engagement under which the task is defined 
	public String taskID;
	public String description;
	public Double timeEstimated;
	public Date plannedStart;
	public Date plannedEnd;
	public final static String CONTENT_TYPE = "AllocatdTask";

	public AllocatdTaskItemPojo(int inItemNumber){
		super (inItemNumber);
        System.out.println("After super construction of ItemPojo title = " + title);		
        initializeAdditionalItemPojoFields();
	}

	public void initializeAdditionalItemPojoFields(){
		System.out.println("@@123 AllocatedTaskItemPojo initializeAdditionalItemPojoFields done");
		taskID = "";
		description = "";
		timeEstimated = 0.0;
		plannedStart = null;
		plannedEnd = null;
		
	}
}