package contentHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import espot.GenericItemHandler;
import espot.ItemPojo;

public class IdeaGenerator extends GenericItemHandler {
	/*
	 * This content handler helps to let any team member to log the an idea
	 * which will be collated into the grouping content type by the server.
	 */
	Text applicationText;
	Text statusText;
	Text reviewerText;
	Text descriptionText;

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
		applicationText = new Text(applicationInfo, SWT.WRAP | SWT.CENTER);
		applicationText.setText(ideaPojo.application);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		applicationInfo.setLayoutData(formData);
		inPrevGroup = applicationInfo;

		Group descriptionInfo = new Group(itemContentGroup, SWT.LEFT);
		descriptionInfo.setText("Description");
		descriptionInfo.setLayout(new FillLayout());
		descriptionText = new Text(descriptionInfo, SWT.WRAP | SWT.CENTER);
		descriptionText.setText(ideaPojo.description);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		descriptionInfo.setLayoutData(formData);
		inPrevGroup = descriptionInfo;

		Group reviewerInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		reviewerInfo.setText("Reviewer");
		reviewerInfo.setLayout(new FillLayout());
		reviewerText = new Text(reviewerInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		reviewerText.setText(ideaPojo.reviewer);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		reviewerInfo.setLayoutData(formData);
		inPrevGroup = reviewerInfo;
	
		Group statusInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		statusInfo.setText("Status");
		statusInfo.setLayout(new FillLayout());
		statusText = new Text(statusInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		statusText.setText(ideaPojo.status);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		statusInfo.setLayoutData(formData);
		inPrevGroup = statusInfo;

		return inPrevGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		IdeaPojo ideaPojo = (IdeaPojo) inItemPojo;
		ideaPojo.application = applicationText.getText();
		ideaPojo.status = statusText.getText();
		ideaPojo.reviewer = reviewerText.getText();
		ideaPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
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