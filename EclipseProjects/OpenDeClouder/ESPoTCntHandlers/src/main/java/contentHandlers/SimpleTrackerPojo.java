package contentHandlers;

import espot.ItemPojo;

public class SimpleTrackerPojo extends ItemPojo{
	/*
	 * Data holder for a simple tracker content
	 */
	public static final String INITIAL_TRACKER_FILE = "SimpleTrackerTemplate.xlsx";
	public static final String TRACKER_SUBFIX = ".xlsx";
	public String simpleTrackerFile;
	public boolean initialized; // this flag is for setting the initial template name close to the artifact
	public boolean corePlanChanged; // this flag is to avoid repeating extended process when core plan unchanged

	SimpleTrackerPojo(int inItemNumber){
		super (inItemNumber);
		initializeAdditionalItemPojoFields();
        System.out.println("After super construction of ItemPojo title = " + title);
	}
	
	void initializeAdditionalItemPojoFields(){
		System.out.println("@@123 From SimpleTrackerPojo initializeAdditionalItemPojoFields done");
	}
}