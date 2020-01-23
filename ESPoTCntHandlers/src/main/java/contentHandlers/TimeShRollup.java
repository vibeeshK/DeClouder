package contentHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import espot.GenericGrouper;
import espot.GenericGrouperDocPojo;
import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class TimeShRollup extends GenericGrouper {
	/*
	 * This content handler helps to group the timesheets of a user
	 */

	//These single item fields will be referred when one single item is pulled out
	Text applicationText;
	Text statusText;
	Text reviewerText;
	Text authorNameText;

	protected void setScreenTitle() {
		mainShell.setText("ESPoT:TimeSheetsGrouper: <viewContentsAtDesk> on " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	}

	public ItemPojo getItemPojo(int inItemCount){
		TimeSheetPojo timeSheetPojo = new TimeSheetPojo(inItemCount);
		return timeSheetPojo;
	}

	protected void setAddlColumnHeaders(){

		int addlHeadersCount = 0;
		
		System.out.println("setAddlColumnHeaders  ");
		System.out.println("addlHeadersCount=" + addlHeadersCount);

		centerBaseColHeaders = new String[] {"Description","Author","Application","Reviewer","Status"};
	}

	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor editor, Table table, TableItem tableItem, int inLastColLocation, ItemPojo inItemPojo){
		TimeSheetPojo timeSheetPojo = (TimeSheetPojo) inItemPojo;

		editor = new TableEditor(table);
		Text description_Tx = new Text(table, SWT.READ_ONLY);
		description_Tx.setText(timeSheetPojo.description);
		editor.grabHorizontal = true;
		editor.setEditor(description_Tx, tableItem, ++inLastColLocation);

		editor = new TableEditor(table);
		Text author_Tx = new Text(table, SWT.READ_ONLY);
		author_Tx.setText(timeSheetPojo.author);
		editor.grabHorizontal = true;
		editor.setEditor(author_Tx, tableItem, ++inLastColLocation);

		editor = new TableEditor(table);
		Text reviewer_Tx = new Text(table, SWT.READ_ONLY);
		reviewer_Tx.setText(timeSheetPojo.reviewer);
		editor.grabHorizontal = true;
		editor.setEditor(reviewer_Tx, tableItem, ++inLastColLocation);
	
		editor = new TableEditor(table);
		Text status_Tx = new Text(table, SWT.READ_ONLY);
		status_Tx.setText(timeSheetPojo.status);
		editor.grabHorizontal = true;
		editor.setEditor(status_Tx, tableItem, ++inLastColLocation);
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){

		Group prevGroup = inPrevGroup;
		TimeSheetPojo timeSheetPojo = (TimeSheetPojo) itemPojo;

		Group reviewerInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		reviewerInfo.setText("Reviewer");
		reviewerInfo.setLayout(new FillLayout());
		reviewerText = new Text(reviewerInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		reviewerText.setText(timeSheetPojo.reviewer);
		
		formData = new FormData();
		formData.top = new FormAttachment(prevGroup);
		reviewerInfo.setLayoutData(formData);
		prevGroup = reviewerInfo;
	
		Group statusInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		statusInfo.setText("Status");
		statusInfo.setLayout(new FillLayout());
		statusText = new Text(statusInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		statusText.setText(timeSheetPojo.status);
		
		formData = new FormData();
		formData.top = new FormAttachment(prevGroup);
		statusInfo.setLayoutData(formData);
		prevGroup = statusInfo;
		
		Group authorInfo = new Group(itemContentGroup, SWT.LEFT);
		authorInfo.setText("Author");
		authorInfo.setLayout(new FillLayout());
		authorNameText = new Text(authorInfo, SWT.WRAP
				| SWT.READ_ONLY | SWT.CENTER);
		authorNameText.setText(timeSheetPojo.author);
	
		formData = new FormData();
		formData.top = new FormAttachment(prevGroup);
		authorInfo.setLayoutData(formData);
		prevGroup = authorInfo;
		
		return prevGroup;
	}
	
	//public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
	//	TimeSheetPojo timeSheetPojo = (TimeSheetPojo) inItemPojo;
	//	timeSheetPojo.status = statusText.getText();
	//	timeSheetPojo.reviewer = reviewerText.getText();
	//	timeSheetPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
	//}
	//
	//public void setInitialItemPojoAddlFields(ItemPojo inItemPojo){
	//	TimeSheetPojo timeSheetPojo = (TimeSheetPojo) inItemPojo;
	//	timeSheetPojo.author = commonData.getCommons().userName;
	//	timeSheetPojo.status = "Draft";
	//}
	public void getPrimerDocAddlFields() {
		// from persistence to screen
	}
	public void setPrimerDocAddlFields() {
		// from screen to persistence		
	}

	@Override
	public void extendedCommonInit() {
	}

	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	protected Class getBasePrimerDocClass() {
		return TimeSheetItemDoc.class;
	}

	@Override
	public Class getPrimerDocClass() {
		return TimeShRollupDoc.class;
	}
	
	@Override
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo timeShRollupDoc = new TimeShRollupDoc();
		return timeShRollupDoc;
	}

	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
		System.out.println("at1 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at1 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at1 getBaseDoc for itemID title" + inItemPojo.title);

		GenericItemDocPojo timeSheetItemDoc = new TimeSheetItemDoc(inItemPojo);

		System.out.println("at2 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at2 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at2 getBaseDoc for itemID title" + inItemPojo.title);

		System.out.println("at3 getBaseDoc for doc " + timeSheetItemDoc);
		System.out.println("at3 getBaseDoc for item " + timeSheetItemDoc.getItem());
		System.out.println("at3 getBaseDoc for itemID " + timeSheetItemDoc.getItem().itemID);
		System.out.println("at3 getBaseDoc for itemID relevance" + timeSheetItemDoc.getItem().relevance);
		System.out.println("at3 getBaseDoc for itemID title" + timeSheetItemDoc.getItem().title);
		return timeSheetItemDoc;		
	}

	@Override
	public void additionalRibbonButtons(Composite ribbon) {
	}

	@Override
	public void setDisplayItemsCenterAddlFieldsInMultiDisplay(
			TableEditor editor, Table inTable, TableItem intableItem,
			int inLastColLocation, ItemPojo inItemPojo) {
	}

	@Override
	public String getDefaultSourceFileName(ItemPojo inItemPojo) {
		return null;
	}
	
	public TimeShRollupDoc getPrimerDoc(){
		return (TimeShRollupDoc) primerDoc;
	}
}