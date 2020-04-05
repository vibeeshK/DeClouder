package contentHandlers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import espot.ArtifactKeyPojo;
import espot.ArtifactMover;
import espot.ArtifactPojo;
import espot.CommonData;
import espot.CommonUIData;
import espot.ContentHandlerInterface;
import espot.ContentHandlerManager;
import espot.ContentHandlerSpecs;
import espot.ERLDownload;
import espot.ErrorHandler;
import espot.GenericItemHandler;
import espot.ItemPojo;
import espot.SelfAuthoredArtifactpojo;

public class TimeShCapture extends GenericItemHandler {
	/*
	 * This content handler helps to pop up the form to capture timesheets
	 */
	final static String TIMESHEET_CONTENTTYPE = "TimeSheet";
	final static String TIMESHROLLUP_CONTENTTYPE = "TimeShRollup";
	final static String ALLOCATEDTASKSLIST_CONTENTTYPE = "AllocatedTasks";

	final static int TEAMID_POSN= 0;
	final static int TASKID_POSN= 1;
	final static int DESC_POSN= 2;
	final static int HOURS_POSN= 3;
	final static int SUBMITBTN_POSN= 4;

	Text captureIntervalText;
	Text allocationIntervalText;
	Text captureStartDateText;
	Text captureEndDateText;
	Text hourLoggedText;
	
	Button continueChkbox;
	ArrayList<TimeEntryOptionRec> timeEntryOptionsRecs;
	final static String CURRNTROWNUMBER = "CURRNTROWNUMBER";
	final static String ALLOCATEDTASKRECNUMBER = "ALLOCATEDTASKRECNUMBER";
	ArrayList<String> allTasksUniq;

	String triggerAt;


	public static String getTimeShCaptureArtifactName(ContentHandlerSpecs inContentHandlerSpecs, String inUserName) {
		String artifactNameCoined = inContentHandlerSpecs.contentType + "_" + inUserName;
		return artifactNameCoined;
	}

	
	public void addlCommonInit () {
		triggerAt = commonData.getCommons().getCurrentTimeStamp(); // setting if manually invoking screen

		System.out.println("at addlCommonInit triggerAt is " + triggerAt);
		
		
		allTasksUniq = new ArrayList<String>();
	}

	public void setInitialItemPojoAddlFields(){
		TimeShCapturePojo timeShCapturePojo = (TimeShCapturePojo) primerDoc.getItem();
	}

	public void checkSetNewItemID() {
		TimeShCapturePojo timeShCapturePojo = (TimeShCapturePojo) primerDoc.getItem();
		if (timeShCapturePojo.itemID.equalsIgnoreCase("")) {
			timeShCapturePojo.itemID = timeShCapturePojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}
	

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup,Group inPrevGroup,FormData formData,ItemPojo inItemPojo){

		TimeShCapturePojo timeShCapturePojo = (TimeShCapturePojo) inItemPojo;
		
		Group lastGroup = inPrevGroup;

		Group captureFreqInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		captureFreqInfo.setText("CaptureInterval");
		captureFreqInfo.setLayout(new FillLayout());
		captureIntervalText = new Text(captureFreqInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);			
		captureIntervalText.setText(commonData.getCommons().convertIntToString(timeShCapturePojo.captureInterval));
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		captureFreqInfo.setLayoutData(formData);
		lastGroup = captureFreqInfo;

		Group allocationFreqInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		allocationFreqInfo.setText("AllocationInterval");
		allocationFreqInfo.setLayout(new FillLayout());
		allocationIntervalText = new Text(allocationFreqInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);			
		allocationIntervalText.setText(commonData.getCommons().convertIntToString(timeShCapturePojo.allocationInterval));
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		allocationFreqInfo.setLayoutData(formData);
		lastGroup = allocationFreqInfo;

		Group captureStartDateInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		captureStartDateInfo.setText("Capture Start Date");
		captureStartDateInfo.setLayout(new FillLayout());
		captureStartDateText = new Text(captureStartDateInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);			
		captureStartDateText.setText(
				(timeShCapturePojo.captureStartDate!=null)?
							timeShCapturePojo.captureStartDate.toString():"");
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		captureStartDateInfo.setLayoutData(formData);
		lastGroup = captureStartDateInfo;

		Group captureEndDateInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		captureEndDateInfo.setText("Capture End Date");
		captureEndDateInfo.setLayout(new FillLayout());
		captureEndDateText = new Text(captureEndDateInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);			
		captureEndDateText.setText(
				(timeShCapturePojo.captureEndDate!=null)?
							timeShCapturePojo.captureEndDate.toString():"");
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		captureEndDateInfo.setLayoutData(formData);
		lastGroup = captureEndDateInfo;

// TimeSheet table display starts
// TimeSheet table display starts
		Group timeShEntryOptionsInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		timeShEntryOptionsInfo.setText("TimeShEntryOptions");
		timeShEntryOptionsInfo.setLayout(new GridLayout());
		Table table = new Table(timeShEntryOptionsInfo, SWT.BORDER | SWT.CENTER);
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		timeShEntryOptionsInfo.setLayoutData(formData);
		lastGroup = timeShEntryOptionsInfo;

		try {
			getAllTimeEntryOptionsRecs();
		} catch (ParseException e1) {
			e1.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in TimeShCapture setAddlFieldsForItemDisplay " + " " + inItemPojo.artifactName, e1);
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		String[] columnHeaders = new String[] { "Team", "TaskID", "Description", "Hours" , "Post?"/* action for first row */};

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			if (columnHeaders[i].equalsIgnoreCase("Description")) {
				column.setWidth(200);
			} else {
				column.setWidth(100);
			}
		}

		// create table rows - one for a new artifact and as many for
		// already in-progress
		int screenMaxNum = timeEntryOptionsRecs.size() + 1;
		System.out.println("screenMaxNum = " + screenMaxNum);
		for (int i = 0; i < screenMaxNum; i++) {
			new TableItem(table, SWT.NONE);
		}

		TableItem[] items = table.getItems();

		// Create row for manual time entry
		for (int manualRow = 0; manualRow < 1; manualRow++) {
			System.out.println("i = " + manualRow );

			final TableEditor teamIDEditor = new TableEditor(table);	// its ok make this final snice there will be a single row
			Text teamIDTx = new Text(table, SWT.NONE);
			teamIDTx.setText("pl. enter teamID ");
			teamIDEditor.grabHorizontal = true;
			teamIDEditor.setEditor(teamIDTx, items[manualRow], TEAMID_POSN);

			final TableEditor taskIDEditor = new TableEditor(table);
			Text taskIDTx = new Text(table, SWT.NONE);
			taskIDTx.setText("pl. enter taskID ");
			taskIDEditor.grabHorizontal = true;
			taskIDEditor.setEditor(taskIDTx, items[manualRow], TASKID_POSN);

			final TableEditor descriptionEditor = new TableEditor(table);
			Text descriptionTx = new Text(table, SWT.NONE);
			descriptionTx.setText("pl. enter description ");
			descriptionEditor.grabHorizontal = true;
			descriptionEditor.setEditor(descriptionTx, items[manualRow], DESC_POSN);

			final TableEditor hoursLoggedEditor = new TableEditor(table);
			Text hoursLoggedTx = new Text(table, SWT.NONE);
			hoursLoggedTx.setText("1");
			hoursLoggedEditor.grabHorizontal = true;
			hoursLoggedEditor.setEditor(hoursLoggedTx, items[manualRow], HOURS_POSN);
			
			final TableEditor taskSubmitButtonEditor = new TableEditor(table);
			Button taskSubmitButton = new Button(table, SWT.PUSH);
			taskSubmitButton
					.setText(SelfAuthoredArtifactpojo.ArtifactStatusScreenTextCreateArtifact);
			taskSubmitButton.setData(CURRNTROWNUMBER, manualRow);
			taskSubmitButton.pack();
			taskSubmitButtonEditor.minimumWidth = taskSubmitButton.getSize().x;
			taskSubmitButtonEditor.horizontalAlignment = SWT.CENTER;
			taskSubmitButtonEditor.setEditor(taskSubmitButton, items[manualRow], SUBMITBTN_POSN);

			System.out.println("set data = "
					+ taskSubmitButton.getData(CURRNTROWNUMBER));

			System.out.println("column 2 in item= " + items[manualRow].getText(1));

			taskSubmitButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button eventButton = (Button) e.getSource();

					eventButton
							.setText(SelfAuthoredArtifactpojo.ArtifactStatusDraft);

					System.out.println("changed the text to Draft");
					System.out.println("eventButton = " + eventButton);
					Integer selectedRowNum = (Integer) eventButton
							.getData(CURRNTROWNUMBER);
					System.out.println("selected row = " + selectedRowNum);

					System.out
							.println("taskIDEditor:"
									+ ((Text) taskIDEditor.getEditor())
											.getText());
					System.out
					.println("descriptionEditor:"
							+ ((Text) descriptionEditor.getEditor())
									.getText());

					String manualRowTaskID = ((Text) taskIDEditor.getEditor())
					.getText();
					String manualRowTeamID = ((Text) teamIDEditor.getEditor())
					.getText();
					String manualRowDescription = ((Text) teamIDEditor.getEditor())
					.getText();
					int hoursLogged = commons.convertStringToInt(((Text) hoursLoggedEditor.getEditor())
					.getText());
					submitTimeEntry(
							invokedArtifactPojo.artifactKeyPojo.relevance,
							manualRowTeamID,
							manualRowTaskID,
							manualRowDescription,
							TimeSheetPojo.ALLOCATION_TYPE_MANUALADD, hoursLogged);
				}
			});
		}

		TimeEntryOptionRec timeEntryOptRec = null;

		for (int ScreenRowNum = 1, inProgressArtifactNum = 0; ScreenRowNum < screenMaxNum; ScreenRowNum++, inProgressArtifactNum++) {
			timeEntryOptRec = timeEntryOptionsRecs
					.get(inProgressArtifactNum);

			final TableEditor teamIDEditor = new TableEditor(table);
			Text teamIDTx = new Text(table, SWT.READ_ONLY);
			teamIDTx.setText(timeEntryOptRec.teamID);
			teamIDEditor.grabHorizontal = true;
			teamIDEditor.setEditor(teamIDTx, items[ScreenRowNum], TEAMID_POSN);

			final TableEditor taskIDEditor = new TableEditor(table);
			Text taskIDTx = new Text(table, SWT.READ_ONLY);
			taskIDTx.setText(timeEntryOptRec.taskID);
			taskIDEditor.grabHorizontal = true;
			taskIDEditor.setEditor(taskIDTx, items[ScreenRowNum], TASKID_POSN);

			final TableEditor hoursLoggedEditor = new TableEditor(table);
			Text hoursLoggedTx = new Text(table, SWT.NONE);
			hoursLoggedTx.setText("1");
			hoursLoggedEditor.grabHorizontal = true;
			hoursLoggedEditor.setEditor(hoursLoggedTx, items[ScreenRowNum], HOURS_POSN);
			
			final TableEditor descriptionEditor = new TableEditor(table);
			Text descriptionTx = new Text(table, SWT.READ_ONLY);
			descriptionTx.setText(timeEntryOptRec.description);
			descriptionEditor.grabHorizontal = true;
			descriptionEditor.setEditor(descriptionTx, items[ScreenRowNum], DESC_POSN);
			
			final TableEditor taskSubmitButtonEditor = new TableEditor(table);
			Button taskSubmitButton = new Button(table, SWT.PUSH);
			taskSubmitButton.setText(timeEntryOptRec.taskID);
			taskSubmitButton.setData(CURRNTROWNUMBER, ScreenRowNum);
			taskSubmitButton.setData(ALLOCATEDTASKRECNUMBER,inProgressArtifactNum);

			System.out.println("set data CURRNTROWNUMBER = "
									+ taskSubmitButton.getData(CURRNTROWNUMBER));
			System.out.println("set data ALLOCATEDTASKRECNUMBER = "
					+ taskSubmitButton.getData(ALLOCATEDTASKRECNUMBER));

			taskSubmitButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					Button eventButton = (Button) e.getSource();

					eventButton
							.setText("Post");

					System.out.println("changed the text to Draft");
					System.out.println("eventButton = " + eventButton);
					Integer selectedRowNum = (Integer) eventButton
							.getData(CURRNTROWNUMBER);
					Integer selectedTimeEntryOptionRecNum = (Integer) eventButton
							.getData(ALLOCATEDTASKRECNUMBER);
					System.out.println("selected row = " + selectedRowNum);

					System.out
							.println("taskIDEditor:"
									+ ((Text) taskIDEditor.getEditor())
											.getText());
					System.out
					.println("descriptionEditor:"
							+ ((Text) descriptionEditor.getEditor())
									.getText());

					String taskID = ((Text) taskIDEditor.getEditor())
					.getText();
					int hoursLogged = commons.convertStringToInt(((Text) hoursLoggedEditor.getEditor())
					.getText());

					TimeEntryOptionRec selectedTimeEntryOptRec = timeEntryOptionsRecs
							.get(selectedTimeEntryOptionRecNum);
					
					submitTimeEntry(
							selectedTimeEntryOptRec.relevance,
							selectedTimeEntryOptRec.teamID,
							taskID,
							selectedTimeEntryOptRec.description,
							TimeSheetPojo.ALLOCATION_TYPE_PREALLOCATED,
							hoursLogged);
				}
			});

			taskSubmitButton.pack();
			taskSubmitButtonEditor.minimumWidth = taskSubmitButton.getSize().x;
			taskSubmitButtonEditor.horizontalAlignment = SWT.CENTER;
			taskSubmitButtonEditor.setEditor(taskSubmitButton, items[ScreenRowNum],SUBMITBTN_POSN);
		}

// TimeSheet table display ends		
// TimeSheet table display ends		

		return lastGroup;
	}

	void submitTimeEntry(String inRelevance, String inTeamID, String inTaskID, String inTaskDescription,
			int inAllocationType, int inHoursLogged) {
		int timeSubmissionState = submitTimeEntryCore(inRelevance, inTeamID, inTaskID,inTaskDescription,inAllocationType,inHoursLogged);
		if (timeSubmissionState == CommonData.PROC_STAT_OK) {
			System.out.println("TimeEntry submitted");
			closeScreen();
		} else if (timeSubmissionState == CommonData.PROC_STAT_WARN) {
			MessageBox messageBox1 = new MessageBox(mainShell,
					SWT.ICON_WARNING | SWT.OK);
			messageBox1.setMessage("There are warnings. Do you still want to proceed?");
			int rc1 = messageBox1.open();
			if (rc1 == SWT.OK) {
				System.out.println("TimeEntry submitted with warning");
				closeScreen();
			}
		} else {
			MessageBox messageBox1 = new MessageBox(mainShell,
					SWT.OK);
			System.out.println("Error encounterd while trying to submit timeEntry");
			messageBox1.setMessage("There are errors. Cannot submit timesheet");
			int rc1 = messageBox1.open();
		}
	}
	
	private int submitTimeEntryCore(String inRelevance, String inTeamID, String inTaskID, String inTaskDescription,
			int inAllocationType, int inHoursLogged) {
		// create timeSheetArtifact

		ContentHandlerSpecs timeSubmissionContentHandlerSpec = commonData.getContentHandlerSpecsMap().get(TIMESHEET_CONTENTTYPE);
		String timeSubmissionArtifactName = invokedArtifactPojo.author + timeSubmissionContentHandlerSpec.rollAddSeparator + triggerAt;

		System.out.println("at submitTimeEntryCore triggerAt is " + triggerAt);
		
		ArtifactKeyPojo timeSheetArtifactKeyPojo = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
				inRelevance,timeSubmissionArtifactName,TIMESHEET_CONTENTTYPE);
		
		int maxLocalVerionNumber = -1;
		maxLocalVerionNumber = commonData.getCatelogPersistenceManager().getMaxDBVersionNumberOfSelfAuthoredArtifact(
				timeSheetArtifactKeyPojo);
		maxLocalVerionNumber++;
		
		String versionedFileName = commonData.getCommons().getVersionedFileName(timeSheetArtifactKeyPojo.artifactName,
				timeSubmissionContentHandlerSpec.extension,maxLocalVerionNumber);
		
		SelfAuthoredArtifactpojo timeSheetDraft = new SelfAuthoredArtifactpojo(
				timeSheetArtifactKeyPojo,
				invokedArtifactPojo.requestor,
				invokedArtifactPojo.author,
				timeSubmissionContentHandlerSpec.hasSpecialHandler,
				"",					//reviewFileName
				ArtifactPojo.ERLSTAT_DRAFT,
				null,				//ParentKey
				versionedFileName, 	// localFilePath
				SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded,
				"", 				// ReqRespFileName
				maxLocalVerionNumber,
				""					// inDelegatedTo
				);

		System.out.println("from submitTimeEntryCore before artifactMover from template for artifactName " + timeSheetDraft.artifactKeyPojo.artifactName);
		
		ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
		artifactMover.moveFromTemplate(timeSubmissionContentHandlerSpec.template,timeSheetDraft);
		if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
			ErrorHandler.displayError(mainShell, commonData.getCommons(), "ErrorXX at timeSheetDraft " + artifactMover.lastProcessStatus + " while moving into : " + timeSheetDraft.LocalFileName);
			return CommonData.PROC_STAT_ERROR;
		}
		
		commonData.getCatelogPersistenceManager().insertArtifactUI(timeSheetDraft);

		ContentHandlerInterface contentHandlerObjectInterface = null;
		contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonData.getCommons(), commonData.getCatelogPersistenceManager(), TIMESHEET_CONTENTTYPE);

		System.out.println("from submitTimeEntryCore before calling initializeContentHandlerForDownloadedArtifact artifactName " + timeSheetDraft.artifactKeyPojo.artifactName);
		contentHandlerObjectInterface.initializeContentHandlerForDraftArtifact((CommonUIData) commonData, timeSheetDraft);
		System.out.println("from submitTimeEntryCore after calling initializeContentHandlerForDownloadedArtifact artifactName " + timeSheetDraft.artifactKeyPojo.artifactName);

		TimeSheetItemDoc timeSheetItemDoc = ((TimeSheet) contentHandlerObjectInterface).getPrimerDoc();
		TimeSheetPojo timeSheetPojo = (TimeSheetPojo) timeSheetItemDoc.getItem();
		timeSheetPojo.taskID = inTaskID;
		timeSheetPojo.teamID = inTeamID;
		timeSheetPojo.description = inTaskDescription;
		timeSheetPojo.timeAllocationType = inAllocationType;
		timeSheetPojo.capturedAt = triggerAt;
		
		System.out.println("at submitTimeEntryCore a1 triggerAt is " + triggerAt);
		System.out.println("at submitTimeEntryCore a2 timeSheetPojo.capturedAt is " + timeSheetPojo.capturedAt);
		
		timeSheetPojo.hoursLogged = inHoursLogged;

		((TimeSheet) contentHandlerObjectInterface).writePrimer();

		return CommonData.PROC_STAT_OK;
	}

	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		TimeShCapturePojo timeShCapturePojo = (TimeShCapturePojo) primerDoc.getItem();
		//timeShCapturePojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
		System.out.println("title value while saving is " + timeShCapturePojo.title);

		timeShCapturePojo.captureInterval = commonData.getCommons().convertStringToInt(captureIntervalText.getText());
		timeShCapturePojo.allocationInterval = commonData.getCommons().convertStringToInt(allocationIntervalText.getText());
	}
	
	public void testPrinter(String inPrintHead) {
		TimeShCapturePojo timeShCapturePojo1 = (TimeShCapturePojo) primerDoc.getItem();
		System.out.println("In testPrinter from inPrintHead " + inPrintHead);
		System.out.println("In testPrinter itemPojo title is " + primerDoc.getItem().title);
		TimeShCapturePojo timeShCapturePojo2 = (TimeShCapturePojo) primerDoc.getItem();

		System.out.println("In testPrinter Pojo title from primeDoc is " + timeShCapturePojo2.title);

	}

	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	public void createEmptyDoc(){
		TimeShCapturePojo timeShCapturePojo2 = new TimeShCapturePojo(0);
		primerDoc = new TimeShCaptureItemDoc(timeShCapturePojo2);
	}

	@Override
	public Class getPrimerDocClass() {
		return TimeShCaptureItemDoc.class;
	}

	@Override
	public TimeShCaptureItemDoc getPrimerDoc() {
	return (TimeShCaptureItemDoc) primerDoc;
	}	

	public TimeShCaptureItemDoc getNewPrimerDoc() {
		return new TimeShCaptureItemDoc(new TimeShCapturePojo(-1));
	}

	@Override
	public boolean validateAddlScrFields(){
		System.out.println("At the start of validateAddlScrFields ");

		if (!(commonData.getCommons().checkNumeric(allocationIntervalText.getText()))) {
			MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
			messageBox1.setMessage("Enter only numeric value in allocationInterval. Data NOT saved!");
			int rc1 = messageBox1.open();					
			return false;
		};
		
		if (!(commonData.getCommons().checkNumeric(captureIntervalText.getText()))) {
			MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
			messageBox1.setMessage("Enter only numeric value in captureIntervalText. Data NOT saved!");
			int rc1 = messageBox1.open();					
			return false;
		};
		
		return true;
	}
	
	public int getTriggerInterval(){
		TimeShCapturePojo timeShCapturePojo = (TimeShCapturePojo) primerDoc.getItem();
		return timeShCapturePojo.captureInterval;
	}
	
	public void triggeredProcess(String inTriggerAt){
		triggerAt = inTriggerAt;

		System.out.println("at triggeredProcess a2 inTriggerAt is " + inTriggerAt);
		
		System.out.println("At the start of triggeredProcess ");
		System.out.println("At the start of triggeredProcess ");
		try {
			viewContentsAtDesk();
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in TimeShCapture triggeredProcess " + " " + inTriggerAt, e);
		}
	}
	public int getAllTimeEntryOptionsRecs() throws ParseException{
	// accumulate records from
	//	TIMESHEET_CONTENTTYPE = "TimeSheet";
	//	TIMESHROLLUP_CONTENTTYPE = "TimeShRollup";
	//	ALLOCATEDTASKS_CONTENTTYPE = "AllocatedTasks";

		TimeShCapturePojo timeShCapturePojo = (TimeShCapturePojo) primerDoc.getItem();

		timeEntryOptionsRecs = new ArrayList<TimeEntryOptionRec>();
		System.out.println("at start of getAllTimeEntryOptionsRecs");
		ContentHandlerInterface contentHandlerObjectInterface = null;

		// (1) Get AllocatedTasks
		ArrayList<ERLDownload> allocatedTasksAllERLs = commonData.getCatelogPersistenceManager().readERLDownLoadsForAuthorOnContentType(invokedArtifactPojo.author,ALLOCATEDTASKSLIST_CONTENTTYPE);
		if (allocatedTasksAllERLs.size() > 1) {
			System.out.println("WARNING getAllTimeEntryOptionsRecs allocatedTasksAll size is " + allocatedTasksAllERLs.size());		
			commonData.getCommons().logger.warn("AllTimeEntryOptionsRecs allocatedTasksAll size is " + allocatedTasksAllERLs.size());
		}
		for (ERLDownload allocatedTasksERL : allocatedTasksAllERLs) {
			contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonData.getCommons(), commonData.getCatelogPersistenceManager(), allocatedTasksERL.artifactKeyPojo.contentType);
			contentHandlerObjectInterface.
				initializeContentHandlerForDownloadedArtifact((CommonUIData) commonData, allocatedTasksERL);
			AllocatedTasks allocatedTasksList = (AllocatedTasks) contentHandlerObjectInterface;
			
			ArrayList<AllocatdTaskItemPojo> currAllocations = allocatedTasksList.getAllocatedTasks();
			System.out.println("getAllTimeEntryOptionsRecs currAllocations size is " + currAllocations.size());
	
			for (AllocatdTaskItemPojo allocation : currAllocations) {
				
				System.out.println("at getAllTimeEntryOptionsRecs loop triggerAt is " + triggerAt);
				
				TimeEntryOptionRec timeEntryOptionRec = new TimeEntryOptionRec(
																allocation.taskID,
																allocation.relevance,
																allocation.teamID,																
																allocation.description,
																triggerAt,
																"",
																"",
																TimeSheetPojo.ALLOCATION_TYPE_PREALLOCATED);
				timeEntryOptionsRecs.add(timeEntryOptionRec);
				allTasksUniq.add(timeEntryOptionRec.taskID);
				System.out.println("getAllTimeEntryOptionsRecs1 timeEntryOptionRec artifact is " + timeEntryOptionRec.taskID);
			}
		}

		// (2) Get prev TimeShRollup
		ArrayList<ERLDownload> timeShRollupAll = commonData.getCatelogPersistenceManager().readERLDownLoadsForAuthorOnContentType(invokedArtifactPojo.author,TIMESHROLLUP_CONTENTTYPE);
		System.out.println("getAllTimeEntryOptionsRecs timeShRollupAll invokedArtifactPojo.author is " + invokedArtifactPojo.author);
		System.out.println("getAllTimeEntryOptionsRecs TIMESHROLLUP_CONTENTTYPE is " + TIMESHROLLUP_CONTENTTYPE);
		
		if (timeShRollupAll != null && timeShRollupAll.size() > 0) {
			if (timeShRollupAll.size() > 1) {
				System.out.println("WARNING getAllTimeEntryOptionsRecs timeShRollupAll size is " + timeShRollupAll.size());
				commonData.getCommons().logger.warn("getAllTimeEntryOptionsRecs timeShRollupAll size is " + timeShRollupAll.size());
			}

			System.out.println("getAllTimeEntryOptionsRecs timeShRollupAll size is a1 " + timeShRollupAll.size());
			
			for (ERLDownload timeShRollup : timeShRollupAll) {
				System.out.println("getAllTimeEntryOptionsRecs timeShRollup artifactName aa " + timeShRollup.artifactKeyPojo.artifactName);
				System.out.println("getAllTimeEntryOptionsRecs timeShRollup author aa " + timeShRollup.author);
				contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonData.getCommons(), commonData.getCatelogPersistenceManager(), timeShRollup.artifactKeyPojo.contentType);
				contentHandlerObjectInterface.
					initializeContentHandlerForDownloadedArtifact((CommonUIData) commonData, timeShRollup);
				TimeShRollupDoc timeShRollupDoc = ((TimeShRollup) contentHandlerObjectInterface).getPrimerDoc();
				ArrayList<TimeSheetPojo> timeSheetsItems = timeShRollupDoc.getItemList();		
				System.out.println("getAllTimeEntryOptionsRecs timeSheetsItems size is a2 " + timeSheetsItems.size());
				for (TimeSheetPojo timeSheetPojo : timeSheetsItems) {
					System.out.println("getAllTimeEntryOptionsRecs timeSheetsItems timeSheetPojo a " + timeSheetPojo.artifactName);
					if (timeSheetPojo.capturedAt == null) {
						System.out.println("getAllTimeEntryOptionsRecs timeSheetPojo capturedAt is blank hence skipping " + timeSheetPojo.itemID);
						continue;
					}
					System.out.println("getAllTimeEntryOptionsRecs timeSheetsItems timeSheetPojo b " + timeSheetPojo.artifactName);
					System.out.println("getAllTimeEntryOptionsRecs timeSheetPojo capturedAt is " + timeSheetPojo.capturedAt);
					System.out.println("getAllTimeEntryOptionsRecs timeShCapturePojo captureStartDate is " + timeShCapturePojo.captureStartDate);
					System.out.println("getAllTimeEntryOptionsRecs timeShCapturePojo captureEndDate is " + timeShCapturePojo.captureEndDate);

					if ((((timeShCapturePojo.captureStartDate == null)
						|| commonData.getCommons().isThisLeftDateLater(
								commons.getDateFromDateOnlyString(timeShCapturePojo.captureStartDate),
								commons.getDateFromDateOnlyString(timeSheetPojo.capturedAt))
						&&
						(timeShCapturePojo.captureEndDate == null 
								|| commonData.getCommons().isThisLeftDateLater(
										commons.getDateFromDateOnlyString(timeSheetPojo.capturedAt),
										commons.getDateFromDateOnlyString(timeShCapturePojo.captureEndDate)))))
								
						&& !allTasksUniq.contains(timeSheetPojo.taskID)) { 

						System.out.println("at getAllTimeEntryOptionsRecs loop2 triggerAt is " + triggerAt);
						
							TimeEntryOptionRec timeEntryOptionRec = new TimeEntryOptionRec(
																			timeSheetPojo.taskID,
																			timeSheetPojo.relevance,
																			timeSheetPojo.teamID,
																			timeSheetPojo.description,
																			triggerAt,
																			"",
																			"",
																			TimeSheetPojo.ALLOCATION_TYPE_PREVUSED);
							timeEntryOptionsRecs.add(timeEntryOptionRec);
							allTasksUniq.add(timeEntryOptionRec.taskID);
							System.out.println("getAllTimeEntryOptionsRecs2 added timeEntryOptionRec " + timeEntryOptionRec.taskID);
					}
				}
			}
		}
		

		// (3) Get prev Drafts 
		ArrayList<SelfAuthoredArtifactpojo> allPrevTimeSheetDrafts
			= commonData.getCatelogPersistenceManager().readDraftsForAuthorOnContentType(
					invokedArtifactPojo.artifactKeyPojo.rootNick,
					invokedArtifactPojo.author,TIMESHEET_CONTENTTYPE);

		if (allPrevTimeSheetDrafts != null && allPrevTimeSheetDrafts.size() > 0) {
			System.out.println("Got inside null and 0 check in Get prev Drafts TIMESHEET_CONTENTTYPE ");
			System.out.println("Got past multiple Drafts TIMESHEET_CONTENTTYPE ");
			System.out.println("getAllTimeEntryOptionsRecs allPrevTimeSheetDrafts size " + allPrevTimeSheetDrafts.size());
			
			for (SelfAuthoredArtifactpojo prevTimeSheetDraft : allPrevTimeSheetDrafts) {
				contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonData.getCommons(), commonData.getCatelogPersistenceManager(), prevTimeSheetDraft.artifactKeyPojo.contentType);
				contentHandlerObjectInterface.initializeContentHandlerForDraftArtifact((CommonUIData) commonData, prevTimeSheetDraft);
				TimeSheetItemDoc prevTimeSheetDoc = ((TimeSheet) contentHandlerObjectInterface).getPrimerDoc();
				TimeSheetPojo prevTimeSheetPojo = (TimeSheetPojo) prevTimeSheetDoc.getItem();

				if (prevTimeSheetPojo.capturedAt == null) {
					System.out.println("getAllTimeEntryOptionsRecs prevTimeSheetPojo capturedAt is blank hence skipping " + prevTimeSheetPojo.itemID);
					continue;
				}

				System.out.println("getAllTimeEntryOptionsRecs timeSheetPojo capturedAt is " + prevTimeSheetPojo.capturedAt);
				System.out.println("getAllTimeEntryOptionsRecs timeShCapturePojo captureStartDate is " + timeShCapturePojo.captureStartDate);
				System.out.println("getAllTimeEntryOptionsRecs timeShCapturePojo captureEndDate is " + timeShCapturePojo.captureEndDate);

				if (!allTasksUniq.contains(prevTimeSheetPojo.taskID)
						&& (((timeShCapturePojo.captureStartDate == null 
								|| commonData.getCommons().isThisLeftDateLater(
										commons.getDateFromDateOnlyString(timeShCapturePojo.captureStartDate),
										commons.getDateFromDateOnlyString(prevTimeSheetPojo.capturedAt))
						&& (timeShCapturePojo.captureEndDate == null 
								|| commonData.getCommons().isThisLeftDateLater(
										commons.getDateFromDateOnlyString(prevTimeSheetPojo.capturedAt),
										commons.getDateFromDateOnlyString(timeShCapturePojo.captureEndDate)))))))
				{
					TimeEntryOptionRec timeEntryOptionRec = new TimeEntryOptionRec(
																	prevTimeSheetPojo.taskID,
																	prevTimeSheetPojo.relevance,
																	prevTimeSheetPojo.teamID,
																	prevTimeSheetPojo.description,
																	prevTimeSheetPojo.capturedAt,
																	"",
																	"",
																	TimeSheetPojo.ALLOCATION_TYPE_PREVUSED);
				
					
					
					timeEntryOptionsRecs.add(timeEntryOptionRec);
					allTasksUniq.add(timeEntryOptionRec.taskID);
					System.out.println("getAllTimeEntryOptionsRecs3 add timeEntryOptionRec artifact" + timeEntryOptionRec.taskID);
				}
			}
		}		
		return CommonData.PROC_STAT_OK;
	}
}

class TimeEntryOptionRec {
	public String taskID;
	public String relevance;	
	public String teamID;	
	public String description;
	public String capturedAt;
	public String attachments;
	public String reviewer;
	public int timeAllocationType = 0;

	TimeEntryOptionRec(String inTaskID,
						String inRelevance,
						String inTeamID,
						String inDescription,
						String inCapturedAt,
						String inAttachments,
						String inReviewer,
						int inTimeAllocationType)
	{

		taskID = inTaskID;
		relevance = inRelevance;
		teamID = inTeamID;
		description = inDescription;
		capturedAt = inCapturedAt;
		attachments = inAttachments;
		reviewer = inReviewer;
		timeAllocationType = inTimeAllocationType;
	}
}