package contentHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import espot.GenericItemHandler;
import espot.ItemPojo;
import espot.UsersDisplay;

public class IdeaGenerator extends GenericItemHandler {
	/*
	 * This content handler helps to let any team member to log the an idea
	 * which will be collated into the grouping content type by the server.
	 */
	public static final int PREFERED_DESC_WIDTH = 600;
	public static final int PREFERED_DESC_HEIGHT = 100;

	Text applicationText;
	//Text statusText;
	//Text reviewerText;
	Text descriptionText;

	UsersDisplay reviewerDisplay;
	
	public ItemPojo getItemPojo(int itemCount){
		ItemPojo ideaPojo = new IdeaPojo(itemCount);
		return ideaPojo;
	}

	@Override
	public void setInitialItemPojoAddlFields() {
		IdeaPojo ideaPojo = (IdeaPojo) primerDoc.getItem();
	}

	public void checkSetNewItemID() {
		IdeaPojo ideaPojo = (IdeaPojo) primerDoc.getItem();
		if (ideaPojo.itemID.equalsIgnoreCase("")) {
			ideaPojo.itemID = ideaPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){

		IdeaPojo ideaPojo = (IdeaPojo) itemPojo;

		Group applicationInfo = new Group(itemContentGroup, SWT.LEFT);
		applicationInfo.setText("Application");
		applicationInfo.setLayout(new FillLayout());
		if (invokedForEdit) {
			applicationText = new Text(applicationInfo, SWT.WRAP | SWT.CENTER);
		} else {
			applicationText = new Text(applicationInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
		}
		applicationText.setText(ideaPojo.application);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		applicationInfo.setLayoutData(formData);
		inPrevGroup = applicationInfo;

		Group descriptionInfo = new Group(itemContentGroup, SWT.LEFT);
		descriptionInfo.setText("Description");
		descriptionInfo.setLayout(new FillLayout());
		if (invokedForEdit) {
			descriptionText = new Text(descriptionInfo, SWT.WRAP | SWT.CENTER);
		} else {
			descriptionText = new Text(descriptionInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
		}
		descriptionText.setText(ideaPojo.description);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		
		if (ideaPojo.description == null || ideaPojo.description.equalsIgnoreCase("")) {
			formData.height = PREFERED_DESC_HEIGHT;
			formData.width = PREFERED_DESC_WIDTH;
		}

		descriptionInfo.setLayoutData(formData);
		inPrevGroup = descriptionInfo;

		Group reviewerInfo = new Group(itemContentGroup, SWT.LEFT | SWT.WRAP);
		//reviewerInfo.setText("Reviewer");
		reviewerInfo.setLayout(new FillLayout());
//		if (invokedForEdit) {
//			reviewerText = new Text(reviewerInfo, SWT.WRAP | SWT.CENTER);
//		} else {
//			reviewerText = new Text(reviewerInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);			
//		}
//		reviewerText.setText(ideaPojo.reviewer);

		reviewerDisplay = new UsersDisplay(commonData.getUsersHandler(),reviewerInfo,ideaPojo.reviewer,invokedForEdit,UsersDisplay.REVIEWER_LIT);

		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		reviewerInfo.setLayoutData(formData);
		inPrevGroup = reviewerInfo;
	
//		Group statusInfo = new Group(itemContentGroup, SWT.LEFT
//				| SWT.WRAP | SWT.READ_ONLY);
//		statusInfo.setText("Status");
//		statusInfo.setLayout(new FillLayout());
//		statusText = new Text(statusInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
//		statusText.setText(ideaPojo.status);
//		
//		formData = new FormData();
//		formData.top = new FormAttachment(inPrevGroup);
//		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
//		statusInfo.setLayoutData(formData);
//		inPrevGroup = statusInfo;

		return inPrevGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		IdeaPojo ideaPojo = (IdeaPojo) inItemPojo;
		ideaPojo.application = applicationText.getText();
		//ideaPojo.status = statusText.getText();
		//ideaPojo.reviewer = reviewerText.getText();
		ideaPojo.reviewer = reviewerDisplay.userText.getText();
		//ideaPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
		ideaPojo.description = descriptionText.getText();
	}
	
	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	public Class getPrimerDocClass() {
		return IdeaItemDoc.class;
	}

	@Override
	public void testPrinter(String inPrintHead) {
	}
	
	@Override
	public boolean validateAddlScrFields() {
		return true;
	}
	
	@Override
	public IdeaItemDoc getPrimerDoc() {
		return (IdeaItemDoc) primerDoc;
	}

	public IdeaItemDoc getNewPrimerDoc() {
		return new IdeaItemDoc(new IdeaPojo(-1));
	}
}