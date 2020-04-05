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

public class SimpleTracker extends GenericItemHandler implements DeckerLiteContentTypeInterface {
	/*
	 * This content handler helps to track a simple dashboard workbook
	 */
	public String simpleTrackerPathFilename;

	@Override
	public String testOk(String inText) {
		System.out.println("print inText " + inText);
		return inText + " got passed";
	}
	
	public void setInitialItemPojoAddlFields(){
		SimpleTrackerPojo simpleTrackerPojo = (SimpleTrackerPojo) primerDoc.getItem();
		checkSetNewItemID();
	}

	public void checkSetNewItemID() {
		SimpleTrackerPojo simpleTrackerPojo = (SimpleTrackerPojo) primerDoc.getItem();
		if (simpleTrackerPojo.itemID.equalsIgnoreCase("")) {
			simpleTrackerPojo.itemID = simpleTrackerPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo inItemPojo){

		SimpleTrackerPojo simpleTrackerPojo = (SimpleTrackerPojo) inItemPojo;
		
		Group lastGroup = inPrevGroup;

		Group simpleTrackerFileLinkInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		simpleTrackerFileLinkInfo.setText("SimpleTracker");
		simpleTrackerFileLinkInfo.setLayout(new FillLayout());

		Button linkTextButton = new Button(simpleTrackerFileLinkInfo, SWT.PUSH | SWT.CENTER);
		linkTextButton.setText(simpleTrackerPojo.simpleTrackerFile);
		linkTextButton.setToolTipText("click to view : " + simpleTrackerPathFilename);
		linkTextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					commons.openFileToView(simpleTrackerPathFilename);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					ErrorHandler.showErrorAndQuit(commons, "Error in SimpleTracker setAddlFieldsForItemDisplay " + " " + inItemPojo.artifactName, e1);
				}
			}
		});

		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		simpleTrackerFileLinkInfo.setLayoutData(formData);
		lastGroup = simpleTrackerFileLinkInfo;

		if (invokedForEdit) {
			simpleTrackerPojo.corePlanChanged = true;
			System.out.println("In setAddlFieldsForItemDisplay corePlan change physically saved " + simpleTrackerPojo.corePlanChanged);
			writePrimer();	// whenever the user invokes in edit mode do a precautionary save with base changes
							// so that even if the user forgets to click the save button on the UI after saving the 
							// associated spreadsheet, the extended server process will not skip
		}
		
		return lastGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		SimpleTrackerPojo simpleTrackerPojo = (SimpleTrackerPojo) primerDoc.getItem();
		//simpleTrackerPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
		simpleTrackerPojo.corePlanChanged = true;	// As this method is called only when save button is pressed in UI
												// it would indicate that the core plan had changed.		
	}
	
	public void testPrinter(String inPrintHead) {
		SimpleTrackerPojo simpleTrackerPojo1 = (SimpleTrackerPojo) primerDoc.getItem();
		System.out.println("In testPrinter from inPrintHead " + inPrintHead);
		System.out.println("In testPrinter itemPojo title is " + primerDoc.getItem().title);
		SimpleTrackerPojo simpleTrackerPojo2 = (SimpleTrackerPojo) primerDoc.getItem();

		System.out.println("In testPrinter Pojo title from primeDoc is " + simpleTrackerPojo2.title);
	}

	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}
	@Override
	public Class getPrimerDocClass() {		
		return SimpleTrackerItemDoc.class;
	}

	@Override
	public SimpleTrackerItemDoc getPrimerDoc() {
		return (SimpleTrackerItemDoc) primerDoc;
	}
	
	public SimpleTrackerItemDoc getNewPrimerDoc() {
		return new SimpleTrackerItemDoc(new SimpleTrackerPojo(-1));
	}	

	public void addlCommonInit() {
		SimpleTrackerPojo simpleTrackerPojo = (SimpleTrackerPojo) primerDoc.getItem();

		if (!simpleTrackerPojo.initialized){
			String initialPlanningPathFileName = commons.getAbsolutePathFromDirAndFileNm(contentPathFolderName, SimpleTrackerPojo.INITIAL_TRACKER_FILE);

			simpleTrackerPojo.simpleTrackerFile = invokedArtifactPojo.artifactKeyPojo.artifactName + SimpleTrackerPojo.TRACKER_SUBFIX;

			simpleTrackerPathFilename = commons.getAbsolutePathFromDirAndFileNm(contentPathFolderName, simpleTrackerPojo.simpleTrackerFile);

			try {
				commons.moveFileUsingName(initialPlanningPathFileName, simpleTrackerPathFilename);
			} catch (IOException e) {
				ErrorHandler.showErrorAndQuit(mainShell, commons, "error in addlCommonInit of SimpleTracker while renaming " + simpleTrackerPathFilename, e );
				
			}
			simpleTrackerPojo.initialized = true;
			writePrimer(); // synch up immediately to avoid mismatch
		} else {
			simpleTrackerPathFilename = commons.getAbsolutePathFromDirAndFileNm(contentPathFolderName, simpleTrackerPojo.simpleTrackerFile);
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
		return simpleTrackerPathFilename;
	}
}