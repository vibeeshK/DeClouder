package contentHandlers;

import espot.ItemPojo;

public class GenlRequestPojo extends ItemPojo{
	/* 
	 * Data holder for an GenlRequestPojo item
	 */

	//String requestorShortName;
	String requestDesc;	
	String requestCategory;	
	String requestStartDate;
	String requestEndDate;

	GenlRequestPojo(int inItemNumber){
		super (inItemNumber);
		initializeAdditionalItemPojoFields();
	}
	
	void initializeAdditionalItemPojoFields(){
		System.out.println("initializeAdditionalItemPojoFields at genlRequestPojoPojo class");

		//requestorShortName = "";
		requestDesc = "";
		requestCategory = "";	
		requestStartDate = "";
		requestEndDate = "";
		
	}
}

