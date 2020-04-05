package contentHandlers;

import espot.ItemPojo;

public class IdeaPojo extends ItemPojo{
	/* 
	 * Data holder for an idea item
	 */

	String description;
	String application;
	String attachments;
	String reviewer;
	String expectedSafetyImprovement;
	String expectedQualityImprovement;
	String expectedDeliveryImprovement;
	String expectedCostSavings;
	String expectedMoraleImprovement;
	
	IdeaPojo(int inItemNumber){
		super (inItemNumber);
		initializeAdditionalItemPojoFields();
	}
	
	void initializeAdditionalItemPojoFields(){
		System.out.println("initializeAdditionalItemPojoFields at ideaPojo class");

		description = "";
		application = "";
		attachments = "";
		reviewer = "";
		expectedSafetyImprovement = "";
		expectedQualityImprovement = "";
		expectedDeliveryImprovement = "";
		expectedCostSavings = "";
		expectedMoraleImprovement = "";
		System.out.println("initializeAdditionalItemPojoFields at ideaPojo class application is " + application);

	}
}

