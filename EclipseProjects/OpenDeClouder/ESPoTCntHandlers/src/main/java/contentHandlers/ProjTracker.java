package contentHandlers;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import espot.ErrorHandler;
import espot.GenericItemHandler;
import espot.ItemPojo;

public class ProjTracker extends GenericItemHandler implements DeckableContentTypeInterface {
	/*
	 * This content handler helps to track a project dashboard workbook
	 */
	public String projTrackerPathFilename;

	//Text projectIDText;

	@Override
	public String testOk(String inText) {
		System.out.println("print inText " + inText);
		return inText + " got passed";
	}
	
	public void setInitialItemPojoAddlFields(){
		ProjTrackerPojo projTrackerPojo = (ProjTrackerPojo) primerDoc.getItem();
		
		//22Mar2020 SetNewItemID is invoked in the calling method itself
		//checkSetNewItemID();
	}

	public void checkSetNewItemID() {
		ProjTrackerPojo projTrackerPojo = (ProjTrackerPojo) primerDoc.getItem();
		if (projTrackerPojo.itemID.equalsIgnoreCase("")) {
			projTrackerPojo.itemID = projTrackerPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo inItemPojo){

		ProjTrackerPojo projTrackerPojo = (ProjTrackerPojo) inItemPojo;
		
		Group lastGroup = inPrevGroup;

		Group projTrackerFileLinkInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		projTrackerFileLinkInfo.setText("ProjTracker");
		projTrackerFileLinkInfo.setLayout(new FillLayout());

		Button linkTextButton = new Button(projTrackerFileLinkInfo, SWT.PUSH | SWT.CENTER);
		linkTextButton.setText(projTrackerPojo.projTrackerFile);
		linkTextButton.setToolTipText("click to view : " + projTrackerPathFilename);
		linkTextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					commons.openFileToView(projTrackerPathFilename);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					ErrorHandler.showErrorAndQuit(commons, "Error in ProjTracker setAddlFieldsForItemDisplay " + " " + inItemPojo.artifactName, e1);
				}
			}
		});

		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		projTrackerFileLinkInfo.setLayoutData(formData);
		lastGroup = projTrackerFileLinkInfo;

		if (invokedForEdit) {
			projTrackerPojo.corePlanChanged = true;
			System.out.println("In setAddlFieldsForItemDisplay corePlan change physically saved " + projTrackerPojo.corePlanChanged);
			writePrimer();	// whenever the user invokes in edit mode do a precautionary save with base changes
							// so that even if the user forgets to click the save button on the UI after saving the 
							// associated spreadsheet, the extended server process will not skip
		}
		
		return lastGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		ProjTrackerPojo projTrackerPojo = (ProjTrackerPojo) primerDoc.getItem();
		//projTrackerPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
		projTrackerPojo.corePlanChanged = true;	// As this method is called only when save button is pressed in UI
												// it would indicate that the core plan had changed.		
	}
	
	public void testPrinter(String inPrintHead) {
		ProjTrackerPojo projTrackerPojo1 = (ProjTrackerPojo) primerDoc.getItem();
		System.out.println("In testPrinter from inPrintHead " + inPrintHead);
		System.out.println("In testPrinter itemPojo title is " + primerDoc.getItem().title);
		ProjTrackerPojo projTrackerPojo2 = (ProjTrackerPojo) primerDoc.getItem();

		System.out.println("In testPrinter Pojo title from primeDoc is " + projTrackerPojo2.title);
	}

	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}
	@Override
	public Class getPrimerDocClass() {		
		return ProjTrackerItemDoc.class;
	}

	@Override
	public ProjTrackerItemDoc getPrimerDoc() {
		return (ProjTrackerItemDoc) primerDoc;
	}
	
	public ProjTrackerItemDoc getNewPrimerDoc() {
		return new ProjTrackerItemDoc(new ProjTrackerPojo(-1));
	}	

	public void addlCommonInit() {
		ProjTrackerPojo projTrackerPojo = (ProjTrackerPojo) primerDoc.getItem();
		

		if (!projTrackerPojo.initialized){
			String initialPlanningPathFileName = commons.getAbsolutePathFromDirAndFileNm(contentPathFolderName, ProjTrackerPojo.INITIAL_TRACKER_FILE);

			projTrackerPojo.projTrackerFile = invokedArtifactPojo.artifactKeyPojo.artifactName + ProjTrackerPojo.TRACKER_SUBFIX;

			projTrackerPathFilename = commons.getAbsolutePathFromDirAndFileNm(contentPathFolderName, projTrackerPojo.projTrackerFile);

			try {
				commons.moveFileUsingName(initialPlanningPathFileName, projTrackerPathFilename);
			} catch (IOException e) {
				ErrorHandler.showErrorAndQuit(mainShell, commons, "error in addlCommonInit of ProjTracker while renaming " + projTrackerPathFilename, e );
				
			}
			projTrackerPojo.initialized = true;
			writePrimer(); // synch up immediately to avoid mismatch
		} else {
			projTrackerPathFilename = commons.getAbsolutePathFromDirAndFileNm(contentPathFolderName, projTrackerPojo.projTrackerFile);
		}		
	}

	@Override
	public boolean validateAddlScrFields(){
		System.out.println("At the start of validateAddlScrFields ");
		return true;
	}

	@Override
	public int getTriggerInterval() {
		return 0;
	}

	@Override
	public String getDetailFilePath() {
		return projTrackerPathFilename;
	}

	@Override
	public String getDetailSheetName() {
		// this is a dummy method to comply deckable interface; the extended handler will provide the actual implementation
		return null;
	}

	@Override
	public String getSummarySheetName() {
		// this is a dummy method to comply deckable interface; the extended handler will provide the actual implementation
		return null;
	}

	@Override
	public String getSummaryShKeyColumnHdr() {
		// this is a dummy method to comply deckable interface; the extended handler will provide the actual implementation
		return null;
	}

	@Override
	public String getSummaryShKeyColumnVal() {
		// this is a dummy method to comply deckable interface; the extended handler will provide the actual implementation
		return null;
	}

	@Override
	public int getSummaryShKeyColSeqNum() {
		// this is a dummy method to comply deckable interface; the extended handler will provide the actual implementation
		return -1;
	}
}