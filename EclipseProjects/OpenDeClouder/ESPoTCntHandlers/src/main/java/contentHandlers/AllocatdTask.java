package contentHandlers;

import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import espot.ContentHandlerSpecs;
import espot.GenericItemHandler;
import espot.ItemPojo;

public class AllocatdTask extends GenericItemHandler {
	/*
	 * This content handler helps to allocate a new task item to a content creator 
	 * by providing a template and assigning
	 */

	public static String getAllocatdTaskArtifactName(String inUserName, ContentHandlerSpecs inContentHandlerSpecs, String inProjID, String inTaskID) {
		return inUserName + inContentHandlerSpecs.rollAddSeparator 
			+ inContentHandlerSpecs.contentType + "_" 
			+ inProjID + "_" + inTaskID;
	}

	public String getArtifactNameOfItem(Object inItemPojo, ContentHandlerSpecs inContentHandlerSpecs) {
		AllocatdTaskItemPojo allocatdTaskItemPojo = (AllocatdTaskItemPojo) inItemPojo;
		return getAllocatdTaskArtifactName(allocatdTaskItemPojo.author, inContentHandlerSpecs, allocatdTaskItemPojo.teamID,allocatdTaskItemPojo.taskID);
	}

	public void setInitialItemPojoAddlFields(){
		AllocatdTaskItemPojo allocatedTaskItemPojo = (AllocatdTaskItemPojo) primerDoc.getItem();
	}

	public void checkSetNewItemID() {
		AllocatdTaskItemPojo allocatedTaskItemPojo = (AllocatdTaskItemPojo) primerDoc.getItem();
		if (allocatedTaskItemPojo.itemID.equalsIgnoreCase("")) {
			allocatedTaskItemPojo.itemID = allocatedTaskItemPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo inItemPojo){
		AllocatdTaskItemPojo allocatedTaskItemPojo = (AllocatdTaskItemPojo) inItemPojo;
		Group lastGroup = inPrevGroup;
		return lastGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		AllocatdTaskItemPojo allocatedTaskItemPojo = (AllocatdTaskItemPojo) primerDoc.getItem();
	}
	
	public AllocatdTaskItemPojo getItem() {
		return (AllocatdTaskItemPojo) primerDoc.getItem();
	}
	
	public void testPrinter(String inPrintHead) {
		AllocatdTaskItemPojo allocatedTaskItemPojo1 = (AllocatdTaskItemPojo) primerDoc.getItem();
		System.out.println("In testPrinter from inPrintHead " + inPrintHead);

		System.out.println("In testPrinter itemPojo title is " + primerDoc.getItem().title);
		AllocatdTaskItemPojo allocatedTaskItemPojo2 = (AllocatdTaskItemPojo) primerDoc.getItem();

		System.out.println("In testPrinter Pojo title from primeDoc is " + allocatedTaskItemPojo2.title);

	}

	public void setInitialItemPojoAddlFields(Object inItemPojo){
		AllocatdTaskItemPojo allocatedTaskItemPojo = (AllocatdTaskItemPojo) inItemPojo;
		//allocatedTaskItemPojo.status = "Draft";
	}
	
	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	public Class getPrimerDocClass() {
		return AllocatdTaskItemDoc.class;
	}

	@Override
	public boolean validateAddlScrFields(){
		System.out.println("At the start of validateAddlScrFields ");
		return true;
	}

	@Override
	public AllocatdTaskItemDoc getPrimerDoc() {
		return (AllocatdTaskItemDoc) primerDoc;
	}
	
	public AllocatdTaskItemDoc getNewPrimerDoc() {
		return new AllocatdTaskItemDoc(new AllocatdTaskItemPojo(-1));
	}
}