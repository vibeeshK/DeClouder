package contentHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import espot.ArtifactKeyPojo;
import espot.ERLDownload;
import espot.GenericGrouper;
import espot.GenericGrouperDocPojo;
import espot.GenericItemDocPojo;
import espot.ItemPojo;
import espot.RelevancePojo;

public class ToDoLister extends GenericGrouper {
	/*
	 * This content handler helps to group the ToDo activities
	 */
	//Text authorText;
	//Text reviewerText;
	//Text statusText;
	//Text cloneFromArtifactNameText;
	//Text cloneFromRelevanceText;
	//Text cloneFromContentTypeText;

	protected void setScreenTitle() {
		mainShell.setText("ESPoT:ToDoLister: <viewContentsAtDesk>");
	}

	public ItemPojo getItemPojo(int inItemCount){
		ToDoPojo toDoPojo = new ToDoPojo(inItemCount);
		return toDoPojo;
	}

	protected void setAddlColumnHeaders(){
		int addlHeadersCount = 0;
		
		System.out.println("setAddlColumnHeaders  ");
		System.out.println("addlHeadersCount=" + addlHeadersCount);

		centerBaseColHeaders = new String[] {"Author","Status","cloneFromArtifactName",
				"cloneFromRelevance","cloneFromContentType"};
	}

	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor editor, Table table, TableItem tableItem, int inLastColLocation, ItemPojo inItemPojo){
		System.out.println("setDisplayItemsBaseFieldsInMultiDisplay:");

		ToDoPojo toDoPojo = (ToDoPojo) inItemPojo;

		editor = new TableEditor(table);
		Text author_Tx = new Text(table, SWT.READ_ONLY);
		author_Tx.setText(toDoPojo.author);
		editor.grabHorizontal = true;
		editor.setEditor(author_Tx, tableItem, ++inLastColLocation);
		
		editor = new TableEditor(table);
		Text status_Tx = new Text(table, SWT.READ_ONLY);
		status_Tx.setText(toDoPojo.status);
		editor.grabHorizontal = true;
		editor.setEditor(status_Tx, tableItem, ++inLastColLocation);
		
		editor = new TableEditor(table);
		Text cloneFromArtifactName_Tx = new Text(table, SWT.READ_ONLY);
		cloneFromArtifactName_Tx.setText(toDoPojo.cloneFromArtifactName);
		editor.grabHorizontal = true;
		editor.setEditor(cloneFromArtifactName_Tx, tableItem, ++inLastColLocation);

		editor = new TableEditor(table);
		Text cloneFromRelevance_Tx = new Text(table, SWT.READ_ONLY);
		cloneFromRelevance_Tx.setText(toDoPojo.cloneFromRelevance);
		editor.grabHorizontal = true;
		editor.setEditor(cloneFromRelevance_Tx, tableItem, ++inLastColLocation);

		editor = new TableEditor(table);
		Text cloneFromContentType_Tx = new Text(table, SWT.READ_ONLY);
		cloneFromContentType_Tx.setText(toDoPojo.cloneFromContentType);
		editor.grabHorizontal = true;
		editor.setEditor(cloneFromContentType_Tx, tableItem, ++inLastColLocation);
	}
	
	@Override
	public void setDisplayItemsCenterAddlFieldsInMultiDisplay(
			TableEditor editor, Table inTable, TableItem intableItem,
			int inLastColLocation, ItemPojo inItemPojo) {
	}
	
	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){
		ToDoPojo toDoPojo = (ToDoPojo) itemPojo;
		System.out.println("ERROR ERROR. individual display shall not be invoked on multiDisplay");
		return inPrevGroup;
	}
	
	//public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
	//	ToDoPojo toDoPojo = (ToDoPojo) inItemPojo;
	//}
	//
	//public void setInitialItemPojoAddlFields(ItemPojo inItemPojo){
	//	IdeaPojo ideaPojo = (IdeaPojo) inItemPojo;
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
	
	public String getDefaultSourceFileName(ItemPojo inItemPojo) {

		ToDoPojo selectedToDoPojo = (ToDoPojo) inItemPojo;
		ArtifactKeyPojo cloneFromErlArtifactKeyPojo = new ArtifactKeyPojo(
				commonData.getCommons().getCurrentRootNick(),
				selectedToDoPojo.cloneFromRelevance,
				selectedToDoPojo.cloneFromArtifactName, 
				selectedToDoPojo.cloneFromContentType
		);
		ERLDownload cloneFromErlDownload = commonData.getCatelogPersistenceManager().readERLDownLoad(cloneFromErlArtifactKeyPojo);

		int cloneFromErlDownloadEditIssue = cloneFromErlDownload.getEditIssueWithErlVersion(mainShell, commonData.getCatelogPersistenceManager(), new RelevancePojo(cloneFromErlDownload.artifactKeyPojo.rootNick,selectedToDoPojo.cloneFromRelevance,""), "CloneFrom Artifact");
		System.out.println("editIssue:" + cloneFromErlDownloadEditIssue);

		String cloneSourceFileString = null;
		if (cloneFromErlDownloadEditIssue == ERLDownload.NO_VIEW_ISSUES) {
			cloneSourceFileString = commonData.getCommons().getFullLocalPathFileNameOfDownloadedArtifact(
					commonData.getCommons().getCurrentRootNick(), 
					cloneFromErlDownload.artifactKeyPojo.relevance, 
					cloneFromErlDownload.downLoadedFile);
	
			System.out.println("cloneSourceFileString:::"
					+ cloneSourceFileString);
		}

		return cloneSourceFileString;
	}

	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	public Class getPrimerDocClass() {
		return ToDoGroupDoc.class;
	}

	public Class getBasePrimerDocClass() {
		return ToDoItemDoc.class;
	}

	@Override
	public void additionalRibbonButtons(Composite ribbon) {
	}

	@Override
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo toDoGroupDoc = new ToDoGroupDoc();
		return toDoGroupDoc;
	}

	@Override
	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
		System.out.println("at1 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at1 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at1 getBaseDoc for itemID title" + inItemPojo.title);

		GenericItemDocPojo toDoItemDoc = new ToDoItemDoc(inItemPojo);

		System.out.println("at2 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at2 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at2 getBaseDoc for itemID title" + inItemPojo.title);

		System.out.println("at3 getBaseDoc for doc " + toDoItemDoc);
		System.out.println("at3 getBaseDoc for item " + toDoItemDoc.getItem());
		System.out.println("at3 getBaseDoc for itemID " + toDoItemDoc.getItem().itemID);
		System.out.println("at3 getBaseDoc for itemID relevance" + toDoItemDoc.getItem().relevance);
		System.out.println("at3 getBaseDoc for itemID title" + toDoItemDoc.getItem().title);
		return toDoItemDoc;		

	}

	@Override
	public ToDoGroupDoc getPrimerDoc() {
		return (ToDoGroupDoc) primerDoc;
	}
}