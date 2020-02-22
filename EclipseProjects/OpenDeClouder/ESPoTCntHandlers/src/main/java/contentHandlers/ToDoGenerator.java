package contentHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import espot.GenericItemHandler;
import espot.ItemPojo;
import espot.UsersDisplay;

public class ToDoGenerator extends GenericItemHandler {
	/*
	 * This content handler helps the content requesters to set up a ToDo activity for another member
	 */
	//Text reviewerText;

	//Text statusText;
	CCombo cloneFromArtifactNameList;
	CCombo cloneFromRelevanceList;
	CCombo cloneFromContentTypeList;

	public void setInitialItemPojoAddlFields(){
		ToDoPojo toDoPojo = (ToDoPojo) primerDoc.getItem();
	}

	public void checkSetNewItemID() {
		ToDoPojo toDoPojo = (ToDoPojo) primerDoc.getItem();
		if (toDoPojo.itemID.equalsIgnoreCase("")) {
			toDoPojo.itemID = toDoPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public void setDisplayItemsAddlFieldsInMultiDisplay(TableEditor editor, Table table, TableItem tableItem, ItemPojo itemPojo){
		ToDoPojo toDoPojo = (ToDoPojo) itemPojo;
		System.out.println("ERROR ERROR. multi display shall not be invoked for ItemDisplay");
	}

	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		if (commonData.getRelavances().length == 0) {
			validationBeforeEdit = "no Sample available";
		}
		return validationBeforeEdit;
	}
	
	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){

		ToDoPojo toDoPojo = (ToDoPojo) itemPojo;

		//////////////////////cloneFromRelevance starts
		Group cloneFromRelevanceGroup = new Group(itemContentGroup, SWT.LEFT);
		cloneFromRelevanceGroup.setLayout(new FillLayout());
		cloneFromRelevanceGroup.setText("CloneFromRelevance");

		System.out.println("toDoPojo.cloneFromRelevance = " + toDoPojo.cloneFromRelevance);
		
		cloneFromRelevanceList = new CCombo(cloneFromRelevanceGroup,
				SWT.DROP_DOWN | SWT.READ_ONLY | SWT.CENTER);

		cloneFromRelevanceList.setItems(commonData.getCatelogPersistenceManager().getERLRelevances(commonData.getCommons().getCurrentRootNick()));

		cloneFromRelevanceList.setEnabled(true);
		System.out.println("@@12");
		if (toDoPojo.cloneFromRelevance == null || toDoPojo.cloneFromRelevance.equalsIgnoreCase("")) {
			System.out.println("@@121");			
			System.out.println("@@2111.0a cloneFromRelevanceList.getSelectionIndex() :" +  cloneFromRelevanceList.getSelectionIndex());
			cloneFromRelevanceList.select(0);
			System.out.println("@@2111.0b cloneFromRelevanceList.getSelectionIndex() :" +  cloneFromRelevanceList.getSelectionIndex());
		} else {
			System.out.println("@@122");
			cloneFromRelevanceList.select(cloneFromRelevanceList.indexOf(toDoPojo.cloneFromRelevance));
		}
		
		System.out.println("@@211.2 cloneFromRelevanceList.getSelectionIndex() :" +  cloneFromRelevanceList.getSelectionIndex());

		cloneFromRelevanceList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("000 cloneFromContentTypeList size:" + cloneFromContentTypeList.getSize());
				cloneFromContentTypeList.setItems(commonData.getCatelogPersistenceManager().getERLContentTypesInRelevance(commonData.getCommons().getCurrentRootNick(),
						cloneFromRelevanceList.getItem(cloneFromRelevanceList
						.getSelectionIndex())));
				System.out.println("12 cloneFromContentTypeList size:" + cloneFromContentTypeList.getSelectionIndex());
				cloneFromContentTypeList.select(0);
				System.out.println("123 cloneFromContentTypeList size:" + cloneFromContentTypeList.getSize());
				System.out.println("123 cloneFromContentTypeList sel index:" + cloneFromContentTypeList.getSelectionIndex());
				System.out.println("123 cloneFromContentTypeList sel item:" + 
						cloneFromContentTypeList.getItem(cloneFromContentTypeList
								.getSelectionIndex()));

				System.out.println("cloneFromRelevanceList.getSelectionIndex():" + cloneFromRelevanceList.getSelectionIndex());
				System.out.println("cloneFromRelevanceList.getItem(cloneFromRelevanceList.getSelectionIndex()):" + cloneFromRelevanceList.getItem(cloneFromRelevanceList
							.getSelectionIndex()));
				cloneFromArtifactNameList.setItems(
							commonData.getCatelogPersistenceManager().getERLArtifactsInRelevanceAndContentType(
							commonData.getCommons().getCurrentRootNick(),
							cloneFromRelevanceList.getItem(cloneFromRelevanceList
							.getSelectionIndex()),
							cloneFromContentTypeList.getItem(cloneFromContentTypeList
							.getSelectionIndex())));
				cloneFromArtifactNameList.select(0);
			}
		});
		System.out.println("@@212 cloneFromRelevanceList.getSelectionIndex() :" +  cloneFromRelevanceList.getSelectionIndex());
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		cloneFromRelevanceGroup.setLayoutData(formData);
		inPrevGroup = cloneFromRelevanceGroup;
		//////////////////////cloneFromRelevance ends

		//////////////////////CloneFromContentType starts
		Group cloneFromContentTypeGroup = new Group(itemContentGroup, SWT.LEFT);
		cloneFromContentTypeGroup.setLayout(new FillLayout());
		cloneFromContentTypeGroup.setText("CloneFromContentType");

		cloneFromContentTypeList = new CCombo(cloneFromContentTypeGroup,
				SWT.DROP_DOWN | SWT.READ_ONLY | SWT.CENTER);
		System.out.println("cloneFromRelevanceList.getSelectionIndex() : " + cloneFromRelevanceList
				.getSelectionIndex());
		
		cloneFromContentTypeList.setItems(commonData.getCatelogPersistenceManager().getERLContentTypesInRelevance(commonData.getCommons().getCurrentRootNick(),cloneFromRelevanceList.getItem(cloneFromRelevanceList
				.getSelectionIndex())));
		cloneFromContentTypeList.setEnabled(true);
		
		System.out.println("12 cloneFromContentTypeList.getItemCount() = " + cloneFromContentTypeList.getItemCount());
		System.out.println("12 cloneFromRelevanceList.getItem(cloneFromRelevanceList.getSelectionIndex()) = " + cloneFromRelevanceList.getItem(cloneFromRelevanceList
				.getSelectionIndex()));
		
		
		if (toDoPojo.cloneFromContentType == null || toDoPojo.cloneFromContentType.equalsIgnoreCase("")) {
			cloneFromContentTypeList.select(0);
		} else {
			cloneFromContentTypeList.select(cloneFromContentTypeList.indexOf(toDoPojo.cloneFromContentType));
		}
		cloneFromContentTypeList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("cloneFromRelevanceList.getSelectionIndex():" + cloneFromRelevanceList.getSelectionIndex());
				System.out.println("cloneFromRelevanceList.getItem(cloneFromRelevanceList.getSelectionIndex()):" + cloneFromRelevanceList.getItem(cloneFromRelevanceList.getSelectionIndex()));
				
				System.out.println("cloneFromContentTypeList.getSelectionIndex():" + cloneFromContentTypeList.getSelectionIndex());
				System.out.println("cloneFromContentTypeList.getItem(cloneFromContentTypeList.getSelectionIndex()):" + cloneFromContentTypeList.getItem(cloneFromContentTypeList.getSelectionIndex()));
				
				cloneFromArtifactNameList.setItems(
					commonData.getCatelogPersistenceManager().getERLArtifactsInRelevanceAndContentType(
					commonData.getCommons().getCurrentRootNick(),
					cloneFromRelevanceList.getItem(cloneFromRelevanceList
							.getSelectionIndex()),
					cloneFromContentTypeList.getItem(cloneFromContentTypeList
							.getSelectionIndex())));
				cloneFromArtifactNameList.select(0);
			}
		});
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		cloneFromContentTypeGroup.setLayoutData(formData);
		inPrevGroup = cloneFromContentTypeGroup;
		//////////////////////CloneFromContentType ends
		
		//////////////////////cloneFromArtifactName starts
		Group cloneFromArtifactNameGroup = new Group(itemContentGroup, SWT.LEFT);
		cloneFromArtifactNameGroup.setLayout(new FillLayout());
		cloneFromArtifactNameGroup.setText("CloneFromArtifactName");

		System.out.println("cloneFromRelevanceList.getSelectionIndex() = " + cloneFromRelevanceList
								.getSelectionIndex());
		System.out.println("cloneFromRelevanceList.getItemCount() = " + cloneFromRelevanceList.getItemCount());

		System.out.println("cloneFromContentTypeList.getSelectionIndex() = " + cloneFromContentTypeList
				.getSelectionIndex());
		System.out.println("cloneFromContentTypeList.getItemCount() = " + cloneFromContentTypeList.getItemCount());
		System.out.println("cloneFromRelevanceList.getItem(cloneFromRelevanceList.getSelectionIndex()) = " + cloneFromRelevanceList.getItem(cloneFromRelevanceList
				.getSelectionIndex()));

		System.out.println("cloneFromContentTypeList.getItem(cloneFromContentTypeList.getSelectionIndex())) = " + cloneFromContentTypeList.getItem(cloneFromContentTypeList
								.getSelectionIndex()));

		cloneFromArtifactNameList = new CCombo(cloneFromArtifactNameGroup,
				SWT.DROP_DOWN | SWT.READ_ONLY | SWT.CENTER);
		cloneFromArtifactNameList.setItems(
				commonData.getCatelogPersistenceManager().getERLArtifactsInRelevanceAndContentType(
						commonData.getCommons().getCurrentRootNick(),
						cloneFromRelevanceList.getItem(cloneFromRelevanceList
								.getSelectionIndex()),
						cloneFromContentTypeList.getItem(cloneFromContentTypeList
								.getSelectionIndex())));
		cloneFromArtifactNameList.setEnabled(true);
		if (toDoPojo.cloneFromArtifactName == null || toDoPojo.cloneFromArtifactName.equalsIgnoreCase("")) {
			cloneFromArtifactNameList.select(0);
		} else {
			cloneFromArtifactNameList.select(cloneFromArtifactNameList.indexOf(toDoPojo.cloneFromArtifactName));
		}
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		cloneFromArtifactNameGroup.setLayoutData(formData);
		inPrevGroup = cloneFromArtifactNameGroup;

		//////////////////////cloneFromArtifactName ends


		return cloneFromArtifactNameGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		ToDoPojo toDoPojo = (ToDoPojo) inItemPojo;
		toDoPojo.cloneFromArtifactName = cloneFromArtifactNameList.getItem(cloneFromArtifactNameList.getSelectionIndex());
		toDoPojo.cloneFromRelevance = cloneFromRelevanceList.getItem(cloneFromRelevanceList.getSelectionIndex());
		toDoPojo.cloneFromContentType = cloneFromContentTypeList.getItem(cloneFromContentTypeList.getSelectionIndex());
		toDoPojo.contentType = toDoPojo.cloneFromContentType; //Item's contentType sync up with cloneFrom's
	}

	@Override
	public Class getPrimerDocClass() {
		return ToDoItemDoc.class;
	}

	@Override
	public ToDoItemDoc getPrimerDoc() {
		return (ToDoItemDoc) primerDoc;
	}

	public ToDoItemDoc getNewPrimerDoc() {
		return new ToDoItemDoc(new ToDoPojo(-1));
	}

	@Override
	public void testPrinter(String inPrintHead) {
	}

	@Override
	public boolean validateAddlScrFields() {
		return true;
	}
}