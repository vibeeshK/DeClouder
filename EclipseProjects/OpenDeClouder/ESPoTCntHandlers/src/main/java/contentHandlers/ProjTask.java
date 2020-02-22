package contentHandlers;

import java.text.ParseException;
import java.util.ArrayList; //import org.eclipse.swt.events.SelectionAdapter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import commonTechs.SimpleDateObj;
import espot.ArtifactKeyPojo;
import espot.ArtifactPrepper;
import espot.ArtifactWrapperUI;
import espot.CommonUIData;
import espot.ContentHandlerManager;
import espot.ContentHandlerSpecs;
import espot.ErrorHandler;
import espot.GenericItemHandler;
import espot.ItemPojo;
import espot.SelfAuthoredArtifactpojo;

public class ProjTask extends GenericItemHandler {
	/*
	 * This content handler helps to maintain a project task
	 */
	Text projectID_Tx;
	Text taskID_Tx;
	Text description_Tx;
	Text owner_Tx;
	Text lead_Tx;
	Text plannedHours_Tx;
	Text plannedStart_Tx;
	Text plannedEnd_Tx;
	//Text status_Tx;
	Text actualStart_Tx;
	Text actualEnd_Tx;
	Text burntHours_Tx;
	Text estimatedOverrunEffortToComplete_Tx;
	Text estimatedEffortToComplete_Tx;
	Text earnedValueSoFar_Tx;
	DateTime expectedEnd_DateDisplay;	

	Text impedimentsCnt_Tx;
	Text impediments_Tx;
	Text closureComment_Tx;

	private ArrayList<DefectItemPojo> associatedDefects;
	private ArrayList<ImpedimentItemPojo> associatedImpediments;
	
	private final static String INITIATED_TYPE1CONTENT_Defect = "Defect";
	private final static String INITIATED_TYPE2CONTENT_Impediment = "Impediment";

	private final static String ASSCIATED_TYPE3CONTENT_DefectsList = "DefectsList";
	private final static String ASSCIATED_TYPE4CONTENT_ImpedimentsList = "ImpedimentsList";
	
	final static String CURRNTIMPEDIMENTNUM = "CURRNTIMPEDIMENTNUM";

	final static String[] TASKSTATUSVALUES = {"YesToStart","InProgress","Completed"};

	public static String getProjTaskArtifactName(ContentHandlerSpecs inContentHandlerSpecs, String inProjID, String inTaskID) {
		return inProjID + inContentHandlerSpecs.rollAddSeparator 
				+ inContentHandlerSpecs.contentType + "_" + inTaskID;
	}

	public void addlCommonInit() {

		ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) primerDoc.getItem();

		System.out.println("at start of addlCommonInit ");
		System.out.println("at start of addlCommonInit projTaskItemPojo.projectName is " + projTaskItemPojo.projectName);
		
		if (projTaskItemPojo.projectName!= null && !projTaskItemPojo.projectName.equalsIgnoreCase("")){

			//1) Get associatedDefects
			{

				ArtifactKeyPojo associatedDefectsArtifactKeyPojo = Defect.getRollupArtifactKeyOfProjDefects(commonData.getContentHandlerSpecsMap().get(INITIATED_TYPE1CONTENT_Defect),
																			invokedArtifactPojo.artifactKeyPojo.rootNick, 
																			invokedArtifactPojo.artifactKeyPojo.relevance, 
																			projTaskItemPojo.projectName, 
																			commonData.getCurrentRootPojo().fileSeparator);

				System.out.println("at addlCommonInit associatedDefectsArtifactKeyPojo is " + associatedDefectsArtifactKeyPojo);
				System.out.println("at addlCommonInit associatedDefectsArtifactKeyPojo artifactName is " + associatedDefectsArtifactKeyPojo.artifactName);
				
				ArtifactPrepper defectsListArtifactPrepper = new ArtifactPrepper(associatedDefectsArtifactKeyPojo,commonData);
				if (!defectsListArtifactPrepper.errorEncountered && defectsListArtifactPrepper.erlDownload != null){
					DefectsList contentHandlerInterfaceOfDefectsList = 
							(DefectsList)  ContentHandlerManager.getInstance(commons, catelogPersistenceManager, associatedDefectsArtifactKeyPojo.contentType);
					contentHandlerInterfaceOfDefectsList
					.initNonUIContentHandlerForDownloadedArtifact(
						commonData,
						defectsListArtifactPrepper.erlDownload
						);				
					ArrayList<DefectItemPojo> itemList = (ArrayList<DefectItemPojo>) contentHandlerInterfaceOfDefectsList.itemList;
					associatedDefects = Defect.filterOpenDefects(Defect.getDefectsOfTasks(itemList).get(projTaskItemPojo.taskID));

				} else {
					System.out.println("skipped with defectsListArtifactPrepper.errorEncountered as " + defectsListArtifactPrepper.errorEncountered);
					System.out.println("and defectsListArtifactPrepper.erlDownload as " + defectsListArtifactPrepper.erlDownload);
				}
			}
			//2) Get associatedImpediments
			{

				ArtifactKeyPojo associatedImpedimentsArtifactKeyPojo = Impediment.getRollupArtifactKeyOfProjImpediments(commonData.getContentHandlerSpecsMap().get(INITIATED_TYPE2CONTENT_Impediment),
						invokedArtifactPojo.artifactKeyPojo.rootNick, 
						invokedArtifactPojo.artifactKeyPojo.relevance, 
						projTaskItemPojo.projectName, 
						commonData.getCurrentRootPojo().fileSeparator);

				ArtifactPrepper impedimentsListArtifactPrepper = new ArtifactPrepper(associatedImpedimentsArtifactKeyPojo,commonData);
				if (!impedimentsListArtifactPrepper.errorEncountered &&  impedimentsListArtifactPrepper.erlDownload != null){
					ImpedimentsList contentHandlerInterfaceOfImpedimentsList = 
							(ImpedimentsList)  ContentHandlerManager.getInstance(commons, catelogPersistenceManager, associatedImpedimentsArtifactKeyPojo.contentType);
					contentHandlerInterfaceOfImpedimentsList
					.initNonUIContentHandlerForDownloadedArtifact(
						commonData,
						impedimentsListArtifactPrepper.erlDownload
						);				
					ArrayList<ImpedimentItemPojo> itemList = (ArrayList<ImpedimentItemPojo>) contentHandlerInterfaceOfImpedimentsList.itemList;					
					associatedImpediments = Impediment.filterOpenImpediments(Impediment.getImpedimentsOfTasks(itemList).get(projTaskItemPojo.taskID));

				} else {
					System.out.println("skipped with impedimentsListArtifactPrepper.errorEncountered as " + impedimentsListArtifactPrepper.errorEncountered);
					System.out.println("and impedimentsListArtifactPrepper.erlDownload as " + impedimentsListArtifactPrepper.erlDownload);
				}
			}			
		}
	}

	public void setInitialItemPojoAddlFields(){
		// for projtask, this method will be overridden at the extended handler process where  
		// the task ID will be set, and the same has to be sent before itemID gets set
	}

	public void checkSetNewItemID() {
		ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) primerDoc.getItem();
		if (projTaskItemPojo.itemID.equalsIgnoreCase("") && !projTaskItemPojo.taskID.equalsIgnoreCase("")) {
			projTaskItemPojo.itemID = projTaskItemPojo.taskID + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo inItemPojo){
		ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) inItemPojo;
		Group lastGroup = inPrevGroup;

		Group projectIDInfo = new Group(itemContentGroup, SWT.LEFT);
		projectIDInfo.setText("ProjectID");
		projectIDInfo.setLayout(new FillLayout());
		projectID_Tx = new Text(projectIDInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		projectID_Tx.setText(projTaskItemPojo.projectName);
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		projectIDInfo.setLayoutData(formData);
		lastGroup = projectIDInfo;

		Group taskIDInfo = new Group(itemContentGroup, SWT.LEFT);
		taskIDInfo.setText("TaskID");
		taskIDInfo.setLayout(new FillLayout());
		taskID_Tx = new Text(taskIDInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		taskID_Tx.setText(projTaskItemPojo.taskID);
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		taskIDInfo.setLayoutData(formData);
		lastGroup = taskIDInfo;

		Group descriptionInfo = new Group(itemContentGroup, SWT.LEFT);
		descriptionInfo.setText("Description");
		descriptionInfo.setLayout(new FillLayout());
		description_Tx = new Text(descriptionInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		description_Tx.setText(projTaskItemPojo.description);
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		descriptionInfo.setLayoutData(formData);
		lastGroup = descriptionInfo;

		Group ownerInfo = new Group(itemContentGroup, SWT.LEFT);
		ownerInfo.setText("Owner");
		ownerInfo.setLayout(new FillLayout());
		owner_Tx = new Text(ownerInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		owner_Tx.setText(projTaskItemPojo.author);
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		ownerInfo.setLayoutData(formData);
		lastGroup = ownerInfo;

		Group leadInfo = new Group(itemContentGroup, SWT.LEFT);
		leadInfo.setText("Lead");
		leadInfo.setLayout(new FillLayout());
		lead_Tx = new Text(leadInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		lead_Tx.setText(projTaskItemPojo.lead);
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		leadInfo.setLayoutData(formData);
		lastGroup = leadInfo;

		Group plannedHoursInfo = new Group(itemContentGroup, SWT.LEFT);
		plannedHoursInfo.setText("PlannedHours");
		plannedHoursInfo.setLayout(new FillLayout());
		plannedHours_Tx = new Text(plannedHoursInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		plannedHours_Tx.setText(commons.convertDoubleToString(projTaskItemPojo.plannedHours));
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		plannedHoursInfo.setLayoutData(formData);
		lastGroup = plannedHoursInfo;

		Group plannedStartInfo = new Group(itemContentGroup, SWT.LEFT);
		plannedStartInfo.setText("PlannedStart");
		plannedStartInfo.setLayout(new FillLayout());
		plannedStart_Tx = new Text(plannedStartInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		plannedStart_Tx.setText(commons.getDateString(projTaskItemPojo.plannedStart));
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		plannedStartInfo.setLayoutData(formData);
		lastGroup = plannedStartInfo;

		Group plannedEndInfo = new Group(itemContentGroup, SWT.LEFT);
		plannedEndInfo.setText("PlannedEnd");
		plannedEndInfo.setLayout(new FillLayout());
		plannedEnd_Tx = new Text(plannedEndInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		plannedEnd_Tx.setText(commons.getDateString(projTaskItemPojo.plannedEnd));
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		plannedEndInfo.setLayoutData(formData);
		lastGroup = plannedEndInfo;

		Group statusInfo = new Group(itemContentGroup, SWT.LEFT);
		statusInfo.setText("Status");
		statusInfo.setLayout(new FillLayout());
		CCombo statusDropDownList = new CCombo(statusInfo, SWT.DROP_DOWN | SWT.CENTER | SWT.READ_ONLY);
		statusDropDownList.setItems(TASKSTATUSVALUES);
		if (projTaskItemPojo.taskStatus == null || projTaskItemPojo.taskStatus.equalsIgnoreCase("")) {
			statusDropDownList.select(statusDropDownList.indexOf(ProjTaskItemPojo.TASKSTATUSVALUES_InProgress));
		} else {
			statusDropDownList.select(statusDropDownList.indexOf(projTaskItemPojo.taskStatus));
		}
		if (invokedForEdit) {
			statusDropDownList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					CCombo dropDownList = (CCombo) e.getSource();
					System.out.println("dropDown selection = " + dropDownList.getSelectionIndex());
					System.out.println("dropDown selected Item = " + dropDownList.getItem((dropDownList.getSelectionIndex())));
					String selectedStatus = dropDownList.getItem((dropDownList.getSelectionIndex()));
					projTaskItemPojo.taskStatus = selectedStatus;
					if (selectedStatus.equalsIgnoreCase(ProjTaskItemPojo.TASKSTATUSVALUES_Completed)) {
						projTaskItemPojo.actualEnd = commons.getDateString();
					}
				}
			});
		} else {
			statusDropDownList.setEnabled(false);
		}
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		statusInfo.setLayoutData(formData);
		lastGroup = statusInfo;

		if (projTaskItemPojo.actualStart != null) {
			Group actualStartInfo = new Group(itemContentGroup, SWT.LEFT);
			actualStartInfo.setText("ActualStart");
			actualStartInfo.setLayout(new FillLayout());
			actualStart_Tx = new Text(actualStartInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
			actualStart_Tx.setText(projTaskItemPojo.actualStart);
			
			formData = new FormData();
			formData.top = new FormAttachment(lastGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			actualStartInfo.setLayoutData(formData);
			lastGroup = actualStartInfo;
		}

		if (projTaskItemPojo.actualEnd != null) {
			Group actualEndInfo = new Group(itemContentGroup, SWT.LEFT);
			actualEndInfo.setText("ActualEnd");
			actualEndInfo.setLayout(new FillLayout());
			actualEnd_Tx = new Text(actualEndInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
			actualEnd_Tx.setText(projTaskItemPojo.actualEnd);			
			
			formData = new FormData();
			formData.top = new FormAttachment(lastGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			actualEndInfo.setLayoutData(formData);
			lastGroup = actualEndInfo;
		}

		Group burntHoursInfo = new Group(itemContentGroup, SWT.LEFT);
		burntHoursInfo.setText("BurntHours");
		burntHoursInfo.setLayout(new FillLayout());
		burntHours_Tx = new Text(burntHoursInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		burntHours_Tx.setText(commons.convertDoubleToString(projTaskItemPojo.burntHours));
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		burntHoursInfo.setLayoutData(formData);
		lastGroup = burntHoursInfo;

		Group estimatedOverrunEffortToCompleteInfo = new Group(itemContentGroup, SWT.LEFT);
		estimatedOverrunEffortToCompleteInfo.setText("EstimatedOverrunEffortToComplete");
		estimatedOverrunEffortToCompleteInfo.setLayout(new FillLayout());
		if (invokedForEdit) {
			estimatedOverrunEffortToComplete_Tx = new Text(estimatedOverrunEffortToCompleteInfo, SWT.WRAP | SWT.CENTER);
		} else {
			estimatedOverrunEffortToComplete_Tx = new Text(estimatedOverrunEffortToCompleteInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
		}
		estimatedOverrunEffortToComplete_Tx.setText(commons.convertDoubleToString(projTaskItemPojo.estimatedOverrunEffortToComplete));
		estimatedOverrunEffortToComplete_Tx.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				System.out.println(" estimatedOverrunEffortToComplete_Tx.getText() is " + estimatedOverrunEffortToComplete_Tx.getText());
				if (estimatedOverrunEffortToComplete_Tx.getText().equals("")) return;

				if (!StringUtils.isNumeric(estimatedOverrunEffortToComplete_Tx.getText())) {
					ErrorHandler.messageBoxNumericOnly(mainShell, commons);
					return;
				}
				System.out.println(" numeric check passed for " + estimatedOverrunEffortToComplete_Tx.getText());
				projTaskItemPojo.estimatedOverrunEffortToComplete = 
						commons.convertStringToDouble(estimatedOverrunEffortToComplete_Tx.getText());
				projTaskItemPojo.estimatedEffortToComplete = getEstimatedEffortToComplete();
				estimatedEffortToComplete_Tx.setText(commons.convertDoubleToString(projTaskItemPojo.estimatedEffortToComplete));
			}
		});

		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		estimatedOverrunEffortToCompleteInfo.setLayoutData(formData);
		lastGroup = estimatedOverrunEffortToCompleteInfo;

		Group estimatedEffortToCompleteInfo = new Group(itemContentGroup, SWT.LEFT);
		estimatedEffortToCompleteInfo.setText("EstimatedOverrunEffortToComplete");
		estimatedEffortToCompleteInfo.setLayout(new FillLayout());
		estimatedEffortToComplete_Tx = new Text(estimatedEffortToCompleteInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		estimatedEffortToComplete_Tx.setText(commons.convertDoubleToString(projTaskItemPojo.estimatedEffortToComplete));
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		estimatedEffortToCompleteInfo.setLayoutData(formData);
		lastGroup = estimatedEffortToCompleteInfo;

		Group expectedEndInfo = new Group(itemContentGroup, SWT.LEFT);
		expectedEndInfo.setText("ExpectedEnd");
		expectedEndInfo.setLayout(new FillLayout());
		if (invokedForEdit) {
			expectedEnd_DateDisplay = new DateTime(expectedEndInfo, SWT.DATE | SWT.CENTER);
			System.out.println("its an editable date now");
		} else {
			expectedEnd_DateDisplay = new DateTime(expectedEndInfo, SWT.DATE | SWT.CENTER | SWT.READ_ONLY);
			System.out.println("its notEditable date now");
		}
		SimpleDateObj startDateSimpleObj;
		if (projTaskItemPojo.expectedEnd != null) {
			startDateSimpleObj = new SimpleDateObj(projTaskItemPojo.expectedEnd);
		} else {
			startDateSimpleObj = new SimpleDateObj(projTaskItemPojo.plannedEnd);
		}			
		expectedEnd_DateDisplay.setDate(startDateSimpleObj.year,startDateSimpleObj.month-1,startDateSimpleObj.day);

		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		expectedEndInfo.setLayoutData(formData);
		lastGroup = expectedEndInfo;

		System.out.println("projTaskItemPojo = "
				+ projTaskItemPojo);
		System.out.println("projTaskItemPojo.description = "
				+ projTaskItemPojo.description);
		
		System.out.println("projTaskItemPojo = "
				+ projTaskItemPojo);
		System.out.println("projTaskItemPojo.remark = "
				+ projTaskItemPojo.remark);

		Group remarkInfo = new Group(itemContentGroup, SWT.LEFT);
		remarkInfo.setText("Remark");
		remarkInfo.setLayout(new FillLayout());
		closureComment_Tx = new Text(remarkInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		closureComment_Tx.setText(projTaskItemPojo.remark);
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		remarkInfo.setLayoutData(formData);
		lastGroup = remarkInfo;

		if (associatedImpediments != null) {
			Group impedimentsInfo = new Group(itemContentGroup, SWT.LEFT
					| SWT.WRAP | SWT.READ_ONLY);
			impedimentsInfo.setText("Impediments");
			impedimentsInfo.setLayout(new GridLayout());
			Table table = new Table(impedimentsInfo, SWT.BORDER);
			
			formData = new FormData();
			formData.top = new FormAttachment(lastGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			impedimentsInfo.setLayoutData(formData);
			lastGroup = impedimentsInfo;
			System.out.println("at3a after impedimentsInfo1 is " + lastGroup);
			System.out.println("at3a associatedImpediments size is " + associatedImpediments.size());
				
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
			String[] columnHeaders = new String[] { "ImpedimentID", "Description", "" /* action for first row */};
	
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

			int screenMaxNum = associatedImpediments==null? 0 : associatedImpediments.size();
			for (int i = 0; i < screenMaxNum; i++) {
				new TableItem(table, SWT.NONE);
			}
	
			TableItem[] items = table.getItems();
	
			for (int ScreenRowNum = 0; ScreenRowNum < screenMaxNum; ScreenRowNum++) {

				ImpedimentItemPojo impedimentItemPojo = associatedImpediments.get(ScreenRowNum);
	
				TableEditor impedimentVwButtonEditor = new TableEditor(table);
				Button impedimentViewButton = new Button(table, SWT.PUSH);
				impedimentViewButton.setText(impedimentItemPojo.itemID);
				impedimentViewButton.setData(CURRNTIMPEDIMENTNUM, ScreenRowNum);
	
				System.out.println("set data CURRNTIMPEDIMENTNUM = "
										+ impedimentViewButton.getData(CURRNTIMPEDIMENTNUM));
	
				impedimentViewButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button eventButton = (Button) e.getSource();
						Integer selectedRowNum = (Integer) eventButton.getData(CURRNTIMPEDIMENTNUM);
						System.out.println("selected CURRNTIMPEDIMENTNUM = " + selectedRowNum);
						ImpedimentItemPojo selectedImpedimentItemPojo = associatedImpediments.get(selectedRowNum);
						ArtifactKeyPojo impedimentKey = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick,selectedImpedimentItemPojo.relevance,selectedImpedimentItemPojo.artifactName, selectedImpedimentItemPojo.contentType);
						ArtifactPrepper artifactPrepper = new ArtifactPrepper (impedimentKey, selectedImpedimentItemPojo.itemID, commonData);

						System.out.println("in ProjTask calling 00 artifactwrapper impedimentKey.artifactName " + impedimentKey.artifactName);
						
						if (!artifactPrepper.errorEncountered) {
							System.out.println("in ProjTask calling 0a artifactwrapper useTemplate " + artifactPrepper.useTemplate);
							System.out.println("in ProjTask calling 0a artifactwrapper useActiveDraft " + artifactPrepper.useActiveDraft);
							System.out.println("in ProjTask calling 0a artifactwrapper useErlDownloadStandalone " + artifactPrepper.useErlDownloadStandalone);
							System.out.println("in ProjTask calling 0a artifactwrapper localDraftActive " + artifactPrepper.localDraftActive);
							System.out.println("in ProjTask calling 0a artifactwrapper newERLExists " + artifactPrepper.uptoDateERLExists);
							System.out.println("in ProjTask calling 0a artifactwrapper isRollupChild " + artifactPrepper.isRollupChild);

							ArtifactWrapperUI artifactWrapperUI = null;
							if (artifactPrepper.useActiveDraft) {
								System.out.println("in ProjTask calling 1 artifactwrapper impedimentKey.artifactName " + impedimentKey.artifactName);
								artifactWrapperUI = new 
								ArtifactWrapperUI((CommonUIData) commonData,
										artifactPrepper.localDraft);
							} else if (artifactPrepper.useErlDownloadRolledupChild) {
								System.out.println("in ProjTask calling 2 artifactwrapper impedimentKey.artifactName " + impedimentKey.artifactName);
								artifactWrapperUI = new 
								ArtifactWrapperUI((CommonUIData) commonData,artifactPrepper.parentERLDownload,artifactPrepper.uptoDateERLItemPojo);
							}
							System.out.println("in ProjTask calling artifactwrapper for impedimentKey.artifactName " + impedimentKey.artifactName);
							artifactWrapperUI.displayArtifactWrapperUI();
						}
						System.out.println("in ProjTask calling 0x artifactwrapper impedimentKey.artifactName " + impedimentKey.artifactName);
					}
				});
	
				impedimentViewButton.pack();
				impedimentVwButtonEditor.minimumWidth = impedimentViewButton.getSize().x;
				impedimentVwButtonEditor.horizontalAlignment = SWT.CENTER;
				impedimentVwButtonEditor.setEditor(impedimentViewButton, items[ScreenRowNum],0);
	
				TableEditor impDescEditor = new TableEditor(table);
				Text descriptionTx = new Text(table, SWT.READ_ONLY);
				descriptionTx.setText(impedimentItemPojo.description);
				impDescEditor.grabHorizontal = true;
				impDescEditor.setEditor(descriptionTx, items[ScreenRowNum], 1);
	
				TableEditor impStatusEditor = new TableEditor(table);
				Text impStatusTx = new Text(table, SWT.READ_ONLY);
				descriptionTx.setText(impedimentItemPojo.impedimentStatus);
				impStatusEditor.grabHorizontal = true;
				impStatusEditor.setEditor(impStatusTx, items[ScreenRowNum], 2);
				
				System.out.println("Got inside the mystery path");
				
			}
	
			System.out.println("at3a after impedimentsInfo2 is " + lastGroup);
		}
		// Impediments display block ends


		// Defects display block starts
		if (associatedDefects != null) {
			Group defectsInfo = new Group(itemContentGroup, SWT.LEFT
					| SWT.WRAP | SWT.READ_ONLY);
			defectsInfo.setText("Defects");
			defectsInfo.setLayout(new GridLayout());
			Table table = new Table(defectsInfo, SWT.BORDER);
			
			formData = new FormData();
			formData.top = new FormAttachment(lastGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			defectsInfo.setLayoutData(formData);
			lastGroup = defectsInfo;
			System.out.println("at3a after defectsInfo1 is " + lastGroup);
			System.out.println("at3a associatedDefects size is " + associatedDefects.size());
	
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
			String[] columnHeaders = new String[] { "Defect ID", "Description", "" /* action for first row */};
	
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
			int screenMaxNum = associatedDefects==null? 0 : associatedDefects.size();
			for (int i = 0; i < screenMaxNum; i++) {
				new TableItem(table, SWT.NONE);
			}
	
			TableItem[] items = table.getItems();
	
			for (int ScreenRowNum = 0; ScreenRowNum < screenMaxNum; ScreenRowNum++) {
				DefectItemPojo defectItemPojo = associatedDefects.get(ScreenRowNum);
	
				TableEditor defectVwButtonEditor = new TableEditor(table);
				Button defectViewButton = new Button(table, SWT.PUSH);
				defectViewButton.setText(defectItemPojo.itemID);
				defectViewButton.setData(CURRNTIMPEDIMENTNUM, ScreenRowNum);
	
				System.out.println("set data CURRNTIMPEDIMENTNUM = "
										+ defectViewButton.getData(CURRNTIMPEDIMENTNUM));
	
				defectViewButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button eventButton = (Button) e.getSource();
						Integer selectedRowNum = (Integer) eventButton.getData(CURRNTIMPEDIMENTNUM);
						System.out.println("selected CURRNTIMPEDIMENTNUM = " + selectedRowNum);
						DefectItemPojo selectedDefectItemPojo = associatedDefects.get(selectedRowNum);					
						ArtifactKeyPojo defectKey = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick,selectedDefectItemPojo.relevance,selectedDefectItemPojo.artifactName, selectedDefectItemPojo.contentType);
						ArtifactPrepper artifactPrepper = new ArtifactPrepper (defectKey, selectedDefectItemPojo.itemID, commonData);
						if (!artifactPrepper.errorEncountered) {
							ArtifactWrapperUI artifactWrapperUI = null;
							if (artifactPrepper.useActiveDraft) {
								System.out.println("in ProjTask calling artifactwrapper defectKey.artifactName " + defectKey.artifactName);
								artifactWrapperUI = new 
								ArtifactWrapperUI((CommonUIData) commonData,
										artifactPrepper.localDraft);
							} else if (artifactPrepper.useErlDownloadRolledupChild) {
								artifactWrapperUI = new 
								ArtifactWrapperUI((CommonUIData) commonData,artifactPrepper.parentERLDownload,artifactPrepper.uptoDateERLItemPojo);
							}
							System.out.println("in ProjTask calling artifactwrapper for defectKey.artifactName " + defectKey.artifactName);
							artifactWrapperUI.displayArtifactWrapperUI();
						}
					}
				});
	
				defectViewButton.pack();
				defectVwButtonEditor.minimumWidth = defectViewButton.getSize().x;
				defectVwButtonEditor.horizontalAlignment = SWT.CENTER;
				defectVwButtonEditor.setEditor(defectViewButton, items[ScreenRowNum],0);
	
				TableEditor defDescEditor = new TableEditor(table);
				Text descriptionTx = new Text(table, SWT.READ_ONLY);
				descriptionTx.setText(defectItemPojo.description);
				defDescEditor.grabHorizontal = true;
				defDescEditor.setEditor(descriptionTx, items[ScreenRowNum], 1);
	
				TableEditor defStatusEditor = new TableEditor(table);
				Text defStatusTx = new Text(table, SWT.READ_ONLY);
				descriptionTx.setText(defectItemPojo.defectStatus);
				defStatusEditor.grabHorizontal = true;
				defStatusEditor.setEditor(defStatusTx, items[ScreenRowNum], 2);
				
				System.out.println("Got inside the mystery path");
				
			}
	
			System.out.println("at3a after defectsInfo2 is " + lastGroup);
		}
		// Defects display block ends
						
		// New Defect&Impediment group starts
		// New Defect&Impediment group starts
		{
			Group initDefectImpedimentnButtonGrp = null;

			initDefectImpedimentnButtonGrp = new Group(itemContentGroup, SWT.LEFT
					| SWT.WRAP | SWT.READ_ONLY);
			initDefectImpedimentnButtonGrp.setText("New Defect&Impediment inits");
			initDefectImpedimentnButtonGrp.setLayout(new FillLayout());
			formData = new FormData();
			formData.top = new FormAttachment(lastGroup);
			formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
			initDefectImpedimentnButtonGrp.setLayoutData(formData);
			
			Button newDefectButton = new Button(initDefectImpedimentnButtonGrp, SWT.PUSH);
			newDefectButton.setText("New Defect");
	
			newDefectButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					testPrinter("From New Defect Action");
					System.out.println("starting New Defect Action");

					ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) primerDoc.getItem();

					System.out.println("At ProjTask New Defect Action inProjID " + projTaskItemPojo.projectName);
					System.out.println("At ProjTask New Defect Action projTaskItemPojo.taskID " + projTaskItemPojo.taskID);
					
					ContentHandlerSpecs defectSpecs = commonData.getContentHandlerSpecsMap().get(INITIATED_TYPE1CONTENT_Defect);
					String defectArtifactName = Defect.getDefectArtifactName(defectSpecs, projTaskItemPojo.projectName, 
																	projTaskItemPojo.taskID, commons.userName, commons.getCurrentTimeStamp());

					System.out.println("At ProjTask received defectArtifactName " + defectArtifactName);
					ArtifactKeyPojo defectArtifactKey = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
							invokedArtifactPojo.artifactKeyPojo.relevance, 
							defectArtifactName, defectSpecs.contentType);

					SelfAuthoredArtifactpojo newDefectSelfAuthoredArtifactpojo = setupDraftArtifact(defectArtifactKey);

					ArtifactWrapperUI artifactWrapperUI = new 
						ArtifactWrapperUI((CommonUIData) commonData,newDefectSelfAuthoredArtifactpojo);
					System.out.println("at3a newDefectButtonProcess going to display the artifactWrapperUI for new defect");
					artifactWrapperUI.displayArtifactWrapperUI();
				}
			});

			Button newImpedimentButton = new Button(initDefectImpedimentnButtonGrp, SWT.PUSH);
			newImpedimentButton.setText("New Impediment");
	
			newImpedimentButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					testPrinter("From New Impediment Action");
					System.out.println("starting New Impediment Action");

					ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) primerDoc.getItem();
					ContentHandlerSpecs impedimentSpecs = commonData.getContentHandlerSpecsMap().get(INITIATED_TYPE2CONTENT_Impediment);
					String impedimentArtifactName = Impediment.getImpedimentArtifactName(impedimentSpecs, projTaskItemPojo.projectName, 
																	projTaskItemPojo.taskID, commons.userName, commons.getCurrentTimeStamp());

					ArtifactKeyPojo impedimentArtifactKey = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
							invokedArtifactPojo.artifactKeyPojo.relevance, 
							impedimentArtifactName, impedimentSpecs.contentType);

					SelfAuthoredArtifactpojo newImpedimentSelfAuthoredArtifactpojo = setupDraftArtifact(impedimentArtifactKey);

					ArtifactWrapperUI artifactWrapperUI = new 
						ArtifactWrapperUI((CommonUIData) commonData,newImpedimentSelfAuthoredArtifactpojo);
					System.out.println("at3a newImpedimentButtonProcess going to display the artifactWrapperUI for new impediment");
					artifactWrapperUI.displayArtifactWrapperUI();
				}
			});
			lastGroup = initDefectImpedimentnButtonGrp;
			System.out.println("at3a after initDefectImpedimentnButtonGrp is " + lastGroup);
		}
		// New Defect&Impediment group ends
		// New Defect&Impediment group ends
		return lastGroup;
	}


	private SelfAuthoredArtifactpojo setupDraftArtifact(ArtifactKeyPojo inArtifactKeyPojo){
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo, commonData);
		SelfAuthoredArtifactpojo extraSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
		extraSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		commonData.getCatelogPersistenceManager().insertArtifactUI(extraSelfAuthoredArtifactpojo);
		return extraSelfAuthoredArtifactpojo;
	}

	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		ProjTaskItemPojo projTaskItemPojo = (ProjTaskItemPojo) primerDoc.getItem();
		if (description_Tx != null) {
			projTaskItemPojo.description = description_Tx.getText();
		}		
		try {
			projTaskItemPojo.expectedEnd = commons.getDate(
												expectedEnd_DateDisplay.getYear(),
												expectedEnd_DateDisplay.getMonth()+1,
												expectedEnd_DateDisplay.getDay(),			
												expectedEnd_DateDisplay.getHours(),
												expectedEnd_DateDisplay.getMinutes(),
												expectedEnd_DateDisplay.getSeconds());
		} catch (ParseException e) {
			
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in ProjTask getAddlFieldsOfItemPojo " + " " + inItemPojo.artifactName, e);
		}
		
	}
	
	public ProjTaskItemPojo getItem() {
		return (ProjTaskItemPojo) primerDoc.getItem();
	}
	
	public void testPrinter(String inPrintHead) {
		ProjTaskItemPojo projTaskItemPojo1 = (ProjTaskItemPojo) primerDoc.getItem();
		System.out.println("In testPrinter from inPrintHead " + inPrintHead);

		System.out.println("In testPrinter itemPojo title is " + primerDoc.getItem().title);
		ProjTaskItemPojo projTaskItemPojo2 = (ProjTaskItemPojo) primerDoc.getItem();

		System.out.println("In testPrinter Pojo title from primeDoc is " + projTaskItemPojo2.title);
	}
	
	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	public Class getPrimerDocClass() {
		return ProjTaskItemDoc.class;
	}

	@Override
	public boolean validateAddlScrFields(){
		System.out.println("At the start of validateAddlScrFields ");
		return true;
	}

	@Override
	public ProjTaskItemDoc getPrimerDoc() {
		return (ProjTaskItemDoc) primerDoc;
	}
	
	public ProjTaskItemDoc getNewPrimerDoc() {
		return new ProjTaskItemDoc(new ProjTaskItemPojo(-1));
	}
	
	public double getEstimatedEffortToComplete() {
		ProjTaskItemPojo projTaskItem = (ProjTaskItemPojo) primerDoc.getItem();
		return projTaskItem.plannedHours - projTaskItem.burntHours + projTaskItem.estimatedOverrunEffortToComplete;
	}	
}