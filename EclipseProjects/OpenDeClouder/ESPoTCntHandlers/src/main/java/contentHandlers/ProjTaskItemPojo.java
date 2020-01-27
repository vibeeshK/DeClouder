package contentHandlers;

import java.util.Date;

import espot.ItemPojo;

public class ProjTaskItemPojo extends ItemPojo{
	/*
	 * Data holder for a project task item
	 */
	final static String CONTENT_TYPE = "ProjTask";

	public final static String TASKSTATUSVALUES_YesToStart = "YesToStart";
	public final static String TASKSTATUSVALUES_InProgress = "InProgress";
	public final static String TASKSTATUSVALUES_Completed = "Completed";

	public String projectName;

	public String taskID;
	public String description;
	public String lead;
	public double plannedHours;
	public Date plannedStart;
	public Date plannedEnd;
	public String taskStatus;
	public String actualStart;
	public String actualEnd;
	public double burntHours;
	public double estimatedOverrunEffortToComplete;
	public double estimatedEffortToComplete;
	public Date expectedEnd;
	public String remark;

	public ProjTaskItemPojo(int inItemNumber){
		super (inItemNumber);
        System.out.println("After super construction of ItemPojo title = " + title);		
        initializeAdditionalItemPojoFields();
	}

	public void initializeAdditionalItemPojoFields(){
		System.out.println("@@123 ProjTaskItemPojo initializeAdditionalItemPojoFields done");
		projectName="";
		taskID="";
		description="";
		lead="";
		plannedHours=0.0;
		plannedStart=null;
		plannedEnd=null;
		taskStatus="";
		actualStart=null;
		actualEnd=null;
		burntHours=0.0;
		estimatedOverrunEffortToComplete=0.0;
		estimatedEffortToComplete=0.0;
		expectedEnd=null;
		remark="";
	}
}