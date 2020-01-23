package contentHandlers;

import java.util.ArrayList;
import java.util.Date;

import espot.ItemPojo;

public class ImpedimentItemPojo extends ItemPojo{
	/*
	 * Data holder for impediment item
	 */
	final static String CONTENT_TYPE = "Impediment";

	public final static String IMPEDIMENTSTATUSVALUES_YesToStart = "YesToStart";
	public final static String IMPEDIMENTSTATUSVALUES_InProgress = "InProgress";
	public final static String IMPEDIMENTSTATUSVALUES_ReadyForReview = "ReadyForReview";
	public final static String IMPEDIMENTSTATUSVALUES_Completed = "Completed";

	public String description;
	public String projectName;	
	public String impedimentID;
	public String severity;
	public Date openedDate;
	public String impedimentStatus;
	public Date expectedEnd;
	public Date actualEnd;
	public double pctgCompleted;
	public double burntHours;
	public String remark;

	public ImpedimentItemPojo(int inItemNumber){
		super (inItemNumber);
        System.out.println("After super construction of ItemPojo title = " + title);		
        initializeAdditionalItemPojoFields();
	}

	public void initializeAdditionalItemPojoFields(){
		System.out.println("@@123 ImpedimentItemPojo initializeAdditionalItemPojoFields done");
		projectName="";
		impedimentID="";
		severity="";
		openedDate=null;
		impedimentStatus="";
		expectedEnd=null;
		actualEnd=null;
		pctgCompleted=0.0;
		burntHours=0.0;
		remark="";
	}

	public static ArrayList<ImpedimentItemPojo> getOpenImpedimentsList(ArrayList<ImpedimentItemPojo> inImpedimentItems){
		ArrayList<ImpedimentItemPojo> openImpedimentItems = new ArrayList<ImpedimentItemPojo>();
		if (inImpedimentItems!=null) {
			for (ImpedimentItemPojo impedimentItem : inImpedimentItems) {
				if (!impedimentItem.impedimentStatus.equalsIgnoreCase(IMPEDIMENTSTATUSVALUES_Completed)){
					openImpedimentItems.add(impedimentItem);
				}	
			}
		}
		return openImpedimentItems;
	}
	
	public static String getImpedimentsText(ArrayList<ImpedimentItemPojo> inImpedimentItems){
		String impedimentsText = "";
		for (ImpedimentItemPojo impedimentItem : inImpedimentItems) {
			impedimentsText = impedimentsText + "%n" + impedimentItem.itemID + " OpenedOn: " 
								+ impedimentItem.openedDate + ": " + impedimentItem.description;
		}
		return impedimentsText;
	}	
}