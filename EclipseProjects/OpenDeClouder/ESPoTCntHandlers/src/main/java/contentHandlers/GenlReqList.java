package contentHandlers;

import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import espot.ErrorHandler;
import espot.GenericGrouper;
import espot.GenericGrouperDocPojo;
import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class GenlReqList extends GenericGrouper {
	/*
	 * This content handler helps to group genlRequests within a relevance
	 */
	public static final int PREFERED_DESC_WIDTH = 600;
	public static final int PREFERED_DESC_HEIGHT = 100;

	Text requestForText;
	Text requestDescText;
	Text requestorShortNameText;
	Text requestCategoryText;	
	DateTime requestStartDateDisplay;
	DateTime requestEndDateDisplay;

	protected void setScreenTitle() {
		mainShell.setText("GenlReqList: <viewContentsAtDesk> on " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	}

	public ItemPojo getItemPojo(int inItemCount){
		GenlRequestPojo genlRequestPojo = new GenlRequestPojo(inItemCount);
		return genlRequestPojo;
	}
	
	protected void setAddlColumnHeaders(){

		int addlHeadersCount = 0;
		
		System.out.println("setAddlColumnHeaders  ");
		System.out.println("addlHeadersCount=" + addlHeadersCount);

		centerBaseColHeaders = new String[] {"requestor","requestTitle","Description","Category","ReqStartsOn","ReqUpto","Status"};
	}

	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor editor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo){
		GenlRequestPojo genlRequestPojo = (GenlRequestPojo) inItemPojo;

//		requestorShortName = "";
//		requestFor = "";
//		requestDesc = "";
//		requestCategory = "";	
//		requestStartDate = "";
//		requestEndDate = "";

		editor = new TableEditor(inTable);
		Text requestorShortName_Tx = new Text(inTable, SWT.READ_ONLY);
		requestorShortName_Tx.setText(genlRequestPojo.requestorShortName);
		editor.grabHorizontal = true;
		editor.setEditor(requestorShortName_Tx, inTableItem, ++inLastColLocation);

		editor = new TableEditor(inTable);
		Text requestFor_Tx = new Text(inTable, SWT.READ_ONLY);
		requestFor_Tx.setText(genlRequestPojo.title);
		editor.grabHorizontal = true;
		editor.setEditor(requestFor_Tx, inTableItem, ++inLastColLocation);
	
		editor = new TableEditor(inTable);
		Text requestDesc_Tx = new Text(inTable, SWT.READ_ONLY);
		requestDesc_Tx.setText(genlRequestPojo.requestDesc);
		editor.grabHorizontal = true;
		editor.setEditor(requestDesc_Tx, inTableItem, ++inLastColLocation);
	
		editor = new TableEditor(inTable);
		Text requestCategory_Tx = new Text(inTable, SWT.READ_ONLY);
		requestCategory_Tx.setText(genlRequestPojo.requestCategory);
		editor.grabHorizontal = true;
		editor.setEditor(requestCategory_Tx, inTableItem, ++inLastColLocation);

		editor = new TableEditor(inTable);
		Text requestStartDate_Tx = new Text(inTable, SWT.READ_ONLY);
		requestStartDate_Tx.setText(genlRequestPojo.requestStartDate);
		editor.grabHorizontal = true;
		editor.setEditor(requestStartDate_Tx, inTableItem, ++inLastColLocation);

		editor = new TableEditor(inTable);
		Text requestEndDate_Tx = new Text(inTable, SWT.READ_ONLY);
		requestEndDate_Tx.setText(genlRequestPojo.requestEndDate);
		editor.grabHorizontal = true;
		editor.setEditor(requestEndDate_Tx, inTableItem, ++inLastColLocation);
		
		editor = new TableEditor(inTable);
		Text status_Tx = new Text(inTable, SWT.READ_ONLY);
		status_Tx.setText(genlRequestPojo.status);
		editor.grabHorizontal = true;
		editor.setEditor(status_Tx, inTableItem, ++inLastColLocation);
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){

		GenlRequestPojo genlRequestPojo = (GenlRequestPojo) itemPojo;

		{	
			Group requestForInfo = new Group(itemContentGroup, SWT.LEFT);
			requestForInfo.setText("Title");
			requestForInfo.setLayout(new FillLayout());
			requestForText = new Text(requestForInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
			requestForText.setText(genlRequestPojo.title);
			
			formData = new FormData();
			formData.top = new FormAttachment(inPrevGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			requestForInfo.setLayoutData(formData);
			inPrevGroup = requestForInfo;
		}

		{
			Group requestDescInfo = new Group(itemContentGroup, SWT.LEFT);
			requestDescInfo.setText("Description");
			requestDescInfo.setLayout(new FillLayout());
			requestDescText = new Text(requestDescInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
			requestDescText.setText(genlRequestPojo.requestDesc);
			
			formData = new FormData();
			formData.top = new FormAttachment(inPrevGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			
			if (genlRequestPojo.requestDesc == null || genlRequestPojo.requestDesc.equalsIgnoreCase("")) {
				formData.height = PREFERED_DESC_HEIGHT;
				formData.width = PREFERED_DESC_WIDTH;
			}
	
			requestDescInfo.setLayoutData(formData);
			inPrevGroup = requestDescInfo;
		}
	
		{
			Group requestorShortNameInfo = new Group(itemContentGroup, SWT.LEFT);
			requestorShortNameInfo.setText("RequestorShortName");
			requestorShortNameInfo.setLayout(new FillLayout());
			requestorShortNameText = new Text(requestorShortNameInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
			requestorShortNameText.setText(genlRequestPojo.requestorShortName);
			
			formData = new FormData();
			formData.top = new FormAttachment(inPrevGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			requestorShortNameInfo.setLayoutData(formData);
			inPrevGroup = requestorShortNameInfo;
		}

		{
			Group requestCategoryInfo = new Group(itemContentGroup, SWT.LEFT);
			requestCategoryInfo.setText("RequestCategory");
			requestCategoryInfo.setLayout(new FillLayout());
			requestCategoryText = new Text(requestCategoryInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
			requestCategoryText.setText(genlRequestPojo.requestCategory);
			
			formData = new FormData();
			formData.top = new FormAttachment(inPrevGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			requestCategoryInfo.setLayoutData(formData);
			inPrevGroup = requestCategoryInfo;
		}

		{
			Group requestStartDateInfo = new Group(itemContentGroup, SWT.LEFT);
			requestStartDateInfo.setText("RequestStartDate");
			requestStartDateInfo.setLayout(new FillLayout());
			requestStartDateDisplay = new DateTime(requestStartDateInfo, SWT.DATE | SWT.CENTER | SWT.READ_ONLY);			
			if (genlRequestPojo.requestStartDate != null && !genlRequestPojo.requestStartDate.isEmpty()) {
				try {
					commons.setDateOnDisplay(requestStartDateDisplay, genlRequestPojo.requestStartDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ErrorHandler.showErrorAndQuit(commons, "Error in GenlRequest setAddlFieldsForItemDisplay StartDate of " 
																+ genlRequestPojo.artifactName, e);
				}
			}
			formData = new FormData();
			formData.top = new FormAttachment(inPrevGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			requestStartDateInfo.setLayoutData(formData);
			inPrevGroup = requestStartDateInfo;
		}

		{
			Group requestEndDateInfo = new Group(itemContentGroup, SWT.LEFT);
			requestEndDateInfo.setText("RequestStartDate");
			requestEndDateInfo.setLayout(new FillLayout());
			requestEndDateDisplay = new DateTime(requestEndDateInfo, SWT.DATE | SWT.CENTER | SWT.READ_ONLY);			
			if (genlRequestPojo.requestEndDate != null && !genlRequestPojo.requestEndDate.isEmpty()) {
				try {
					commons.setDateOnDisplay(requestEndDateDisplay, genlRequestPojo.requestEndDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ErrorHandler.showErrorAndQuit(commons, "Error in GenlRequest setAddlFieldsForItemDisplay EndDate of "
																+ genlRequestPojo.artifactName, e);
				}
			}

			formData = new FormData();
			formData.top = new FormAttachment(inPrevGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			requestEndDateInfo.setLayoutData(formData);
			inPrevGroup = requestEndDateInfo;
		}

		return inPrevGroup;
	}
	
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
		return GenlRequestItemDoc.class;
	}

	@Override
	public Class getPrimerDocClass() {
		return GenlReqListDoc.class;
	}
	
	@Override
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo genlRequestsGrouperDoc = new GenlReqListDoc();
		return genlRequestsGrouperDoc;
	}

	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
		System.out.println("at1 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at1 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at1 getBaseDoc for itemID title" + inItemPojo.title);


		GenericItemDocPojo genlRequestItemDoc = new GenlRequestItemDoc(inItemPojo);

		System.out.println("at2 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at2 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at2 getBaseDoc for itemID title" + inItemPojo.title);

		System.out.println("at3 getBaseDoc for doc " + genlRequestItemDoc);
		System.out.println("at3 getBaseDoc for item " + genlRequestItemDoc.getItem());
		System.out.println("at3 getBaseDoc for itemID " + genlRequestItemDoc.getItem().itemID);
		System.out.println("at3 getBaseDoc for itemID relevance" + genlRequestItemDoc.getItem().relevance);
		System.out.println("at3 getBaseDoc for itemID title" + genlRequestItemDoc.getItem().title);
		return genlRequestItemDoc;		
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

	@Override
	public GenlReqListDoc getPrimerDoc() {
		return (GenlReqListDoc) primerDoc;
	}
}