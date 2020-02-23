package contentHandlers;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import espot.ArtifactKeyPojo;
import espot.ArtifactPrepper;
import espot.CommonUIData;
import espot.Commons;
import espot.ContentHandlerInterface;
import espot.ContentHandlerManager;
import espot.ContentHandlerSpecs;
import espot.ERLDownload;
import espot.ErrorHandler;
import espot.GenericGrouper;
import espot.GenericGrouperDocPojo;
import espot.GenericItemDocPojo;
import espot.ItemPojo;
import espot.SelfAuthoredArtifactpojo;

public class ProjTasksList extends GenericGrouper {
	/*
	 * This content handler helps to group proj tasks
	 */
	final static String EVENTDATA_ProjTaskItemPojo = "ProjTaskItemPojo";
	final static String CONTENTTYPE1_AllocatdTask = "AllocatdTask";
	final static String CONTENTTYPE2_AllocatedTasks = "AllocatedTasks";
	
	//Text descriptionText;
	//Text authorText;
	//Text statusText;

	private HashMap<String,AllocatdTaskItemPojo> allocatdTasksFromProj;
	private HashMap<String,AllocatdTaskItemPojo> allocatdTasksDrafts;
	boolean allocatdTasksUseable = true;

	public void addlCommonInit() {
		
		// Load the allocated tasks from ERL and drafts
		
		ProjTaskItemPojo firstProjTask = (ProjTaskItemPojo) itemList.get(0);

		// (1) Get AllocatedTasks from RolledUp ERLs
		allocatdTasksFromProj = new HashMap<String,AllocatdTaskItemPojo>();
		ContentHandlerSpecs allocatedTasksSpecs = commonData.getContentHandlerSpecsMap().get(CONTENTTYPE2_AllocatedTasks);

		ArrayList<ERLDownload> allocatedTasksAllERLs = commonData.getCatelogPersistenceManager().readERLDownLoadsForAuthorOnContentType(commons.userName,CONTENTTYPE2_AllocatedTasks);
		if (allocatedTasksAllERLs.size() > 0) {
			System.out.println("Info addlCommonInit allocatedTasksAll size is " + allocatedTasksAllERLs.size());		
			commonData.getCommons().logger.info("addlCommonInit allocatedTasksAll size is " + allocatedTasksAllERLs.size());
		}
		
		for (ERLDownload allocatedTasksERL : allocatedTasksAllERLs) { // this is only first level loop of all lists

			if (!allocatedTasksERL.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_AVAILABLE)) {
				if (commons.processMode == Commons.CLIENT_MACHINE) {
					if (allocatedTasksERL.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED) 
						|| allocatedTasksERL.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
						String popUpMsg = allocatedTasksERL.artifactKeyPojo.artifactName + " is not available. Do you want to subscribe?";
						if (ErrorHandler.confirmationPopup(((CommonUIData) commonData).getESPoTDisplay(), popUpMsg)) {
							commonData.getCatelogPersistenceManager().replaceSubscription(allocatedTasksERL,ERLDownload.CURRENTLY_SUBSCRIBED);
						}
					} else {
						String popUpMsg = allocatedTasksERL.artifactKeyPojo.artifactName + " is not available. Pl. wait until its downloaded";
						ErrorHandler.infoPopup(((CommonUIData) commonData).getESPoTDisplay(), popUpMsg);
					}
				} else {
					ErrorHandler.showErrorAndQuit(commons, "expected erl not yet downloaded " + allocatedTasksERL.artifactKeyPojo.artifactName);
				}
				allocatdTasksUseable = false;
				return;
			}

			ContentHandlerInterface contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonData.getCommons(), commonData.getCatelogPersistenceManager(), allocatedTasksERL.artifactKeyPojo.contentType);
			contentHandlerObjectInterface.
				initializeContentHandlerForDownloadedArtifact((CommonUIData) commonData, allocatedTasksERL);

			AllocatedTasks allocatedTasksInterface = (AllocatedTasks) contentHandlerObjectInterface;
			
			ArrayList<AllocatdTaskItemPojo> currAllocations = allocatedTasksInterface.getAllocatedTasks();
			System.out.println("Addl common init added allocation from allocatedTasksERL.artifactKeyPojo.artifactName " + allocatedTasksERL.artifactKeyPojo.artifactName);
			System.out.println("Addl common init added allocation currAllocations size is " + currAllocations.size());
	
			for (AllocatdTaskItemPojo allocation : currAllocations) {
				if (allocation.teamID.equalsIgnoreCase(firstProjTask.projectName)) {
					allocatdTasksFromProj.put(allocation.taskID, allocation);
					System.out.println("Addl common init added allocation.taskID from ERL " + allocation.taskID);
				}
			}
		}
		
		// (2) Get AllocatedTask drafts
		allocatdTasksDrafts = new HashMap<String,AllocatdTaskItemPojo>();
		
		ContentHandlerSpecs childAllocatdTaskSpecs = commonData.getContentHandlerSpecsMap().get(CONTENTTYPE1_AllocatdTask);

		ArrayList<SelfAuthoredArtifactpojo> childAllocatdDrafts = catelogPersistenceManager.readDraftsForAuthorOnContentType(invokedArtifactPojo.artifactKeyPojo.rootNick, commons.userName,childAllocatdTaskSpecs.contentType);
		
		if (childAllocatdDrafts.size() > 0) {
			System.out.println("Info addlCommonInit childAllocatdDrafts size is " + childAllocatdDrafts.size());		
			commonData.getCommons().logger.info("addlCommonInit childAllocatdDrafts size is " + childAllocatdDrafts.size());
		}
		
		for (SelfAuthoredArtifactpojo childAllocatdDrft : childAllocatdDrafts) { // this is only first level loop of all lists

			ContentHandlerInterface contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonData.getCommons(), commonData.getCatelogPersistenceManager(), childAllocatdDrft.artifactKeyPojo.contentType);
			contentHandlerObjectInterface.initializeContentHandlerForDraftArtifact((CommonUIData) commonData, childAllocatdDrft);

			AllocatdTask allocatdTaskInterface = (AllocatdTask) contentHandlerObjectInterface;
			
			AllocatdTaskItemPojo allocatdTaskItem = (AllocatdTaskItemPojo) allocatdTaskInterface.getFocusedItemPojo();
			System.out.println("Addl common init added allocation from allocatedTasksERL.artifactKeyPojo.artifactName " + allocatdTaskItem);

			//allocatdTasksFromProj.put(allocatdTaskItem.taskID, allocatdTaskItem);
			allocatdTasksDrafts.put(allocatdTaskItem.taskID, allocatdTaskItem);
			System.out.println("Addl common init added allocation.taskID from draft " + allocatdTaskItem.taskID);
		}
		
	}

	public ItemPojo getItemPojo(int inItemCount){
		ProjTaskItemPojo projTaskPojo = new ProjTaskItemPojo(inItemCount);
		return projTaskPojo;
	}
	
	protected void setAddlColumnHeaders(){

		int addlHeadersCount = 0;
		
		System.out.println("setAddlColumnHeaders  ");
		System.out.println("addlHeadersCount=" + addlHeadersCount);

		centerBaseColHeaders = new String[] {"Description","Author","Status"};
		centerAddlColHeaders = new String[] {"Associate?"};
		
		System.out.println("centerBaseColHeaders 0 " + centerBaseColHeaders[0]);
		System.out.println("centerBaseColHeaders 1 " + centerBaseColHeaders[1]);
		System.out.println("centerBaseColHeaders 2 " + centerBaseColHeaders[2]);
		//System.out.println("centerBaseColHeaders 3 " + centerBaseColHeaders[3]);
	}

	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor inEditor, Table inTable, 
									TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo){
		ProjTaskItemPojo projTaskPojo = (ProjTaskItemPojo) inItemPojo;

		inEditor = new TableEditor(inTable);
		Text description_Tx = new Text(inTable, SWT.READ_ONLY);
		description_Tx.setText(projTaskPojo.description);
		inEditor.grabHorizontal = true;
		inEditor.setEditor(description_Tx, inTableItem, ++inLastColLocation);

		inEditor = new TableEditor(inTable);
		Text author_Tx = new Text(inTable, SWT.READ_ONLY);
		author_Tx.setText(projTaskPojo.author);
		inEditor.grabHorizontal = true;
		inEditor.setEditor(author_Tx, inTableItem, ++inLastColLocation);	

		inEditor = new TableEditor(inTable);
		Text status_Tx = new Text(inTable, SWT.READ_ONLY);
		status_Tx.setText(projTaskPojo.status);
		inEditor.grabHorizontal = true;
		inEditor.setEditor(status_Tx, inTableItem, ++inLastColLocation);
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,
												FormData inFormData, ItemPojo inItemPojo){

		ProjTaskItemPojo projTaskPojo = (ProjTaskItemPojo) inItemPojo;

		Group descriptionInfo = new Group(itemContentGroup, SWT.LEFT);
		descriptionInfo.setText("Description");
		descriptionInfo.setLayout(new FillLayout());
		Text descriptionText = new Text(descriptionInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		descriptionText.setText(projTaskPojo.description);
		
		inFormData = new FormData();
		inFormData.top = new FormAttachment(inPrevGroup);
		descriptionInfo.setLayoutData(inFormData);

		inPrevGroup = descriptionInfo;

		Group authorInfo = new Group(itemContentGroup, SWT.LEFT);
		authorInfo.setText("Author");
		authorInfo.setLayout(new FillLayout());
		Text authorText = new Text(authorInfo, SWT.WRAP
				| SWT.READ_ONLY | SWT.CENTER);
		authorText.setText(projTaskPojo.author);
		
		inFormData = new FormData();
		inFormData.top = new FormAttachment(inPrevGroup);
		authorInfo.setLayoutData(inFormData);

		inPrevGroup = authorInfo;

		Group statusInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		statusInfo.setText("Status");
		statusInfo.setLayout(new FillLayout());
		Text  statusText = new Text(statusInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		statusText.setText(projTaskPojo.status);
		
		inFormData = new FormData();
		inFormData.top = new FormAttachment(inPrevGroup);
		statusInfo.setLayoutData(inFormData);
		
		inPrevGroup = statusInfo;

		return inPrevGroup;
	}
	//public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
	//	ProjTaskItemPojo projTaskPojo = (ProjTaskItemPojo) inItemPojo;
	//}
	//public void setInitialItemPojoAddlFields(ItemPojo inItemPojo){
	//	ProjTaskItemPojo projTaskPojo = (ProjTaskItemPojo) inItemPojo;
	//	projTaskPojo.author = commonData.getCommons().userName;
	//	projTaskPojo.status = "Draft";
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
		return ProjTaskItemDoc.class;
	}

	@Override
	public Class getPrimerDocClass() {
		return ProjTasksListDoc.class;
	}
	
	@Override
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo projTasksListDoc = new ProjTasksListDoc();
		return projTasksListDoc;
	}

	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
		System.out.println("at1 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at1 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at1 getBaseDoc for itemID title" + inItemPojo.title);

		GenericItemDocPojo projTaskItemDoc = new ProjTaskItemDoc((ProjTaskItemPojo) inItemPojo);

		System.out.println("at3 getBaseDoc for doc " + projTaskItemDoc);
		System.out.println("at3 getBaseDoc for item " + projTaskItemDoc.getItem());
		System.out.println("at3 getBaseDoc for itemID " + projTaskItemDoc.getItem().itemID);
		System.out.println("at3 getBaseDoc for itemID relevance" + projTaskItemDoc.getItem().relevance);
		System.out.println("at3 getBaseDoc for itemID title" + projTaskItemDoc.getItem().title);
		return projTaskItemDoc;		
	}

	@Override
	public void additionalRibbonButtons(Composite ribbon) {
	}

	@Override
	public void setDisplayItemsCenterAddlFieldsInMultiDisplay(
			TableEditor inEditor, Table inTable, TableItem inTableItem,
			int inLastColLocation, ItemPojo inItemPojo) {

		ProjTaskItemPojo projTaskPojo = (ProjTaskItemPojo) inItemPojo;

		System.out.println("Starting setDisplay for Dare to join the party on : " + projTaskPojo.description);

		inEditor = new TableEditor(inTable);
		Button linkTextButton = new Button(inTable, SWT.PUSH);

		System.out.println("atxx going to set the allocated flag for " + projTaskPojo.taskID);

		if (allocatdTasksUseable
			&& allocatdTasksFromProj.get(projTaskPojo.taskID) == null
			&& allocatdTasksDrafts.get(projTaskPojo.taskID) == null) {

			linkTextButton.setText("Associate " + projTaskPojo.taskID + "?");
			linkTextButton.setEnabled(true);
			System.out.println("atxx going to set true for " + projTaskPojo.taskID);

		} else if (allocatdTasksUseable
			&& allocatdTasksFromProj.get(projTaskPojo.taskID) != null) {
		
			linkTextButton.setText("Already Associated");
			linkTextButton.setEnabled(false);
			System.out.println("atxx going to set already exists for " + projTaskPojo.taskID);

		} else if (allocatdTasksUseable
			&& allocatdTasksDrafts.get(projTaskPojo.taskID) != null) {
			
			linkTextButton.setText("Assn.Req in-progress");
			linkTextButton.setEnabled(false);
			System.out.println("atxx going to set Assn.Req in-progress for " + projTaskPojo.taskID);
		
		} else {
			
			linkTextButton.setText("Try Later");
			System.out.println("atxx going to set try later for " + projTaskPojo.taskID);
			linkTextButton.setEnabled(false);
		}
		
		inEditor.setEditor(linkTextButton, inTableItem, ++inLastColLocation);

		linkTextButton.setToolTipText("Dare to join the party on : " + projTaskPojo.description);
		
		linkTextButton.setData(EVENTDATA_ProjTaskItemPojo, projTaskPojo);
		linkTextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Button eventButton = (Button) e.getSource();

				ProjTaskItemPojo projTaskPojoInEvent = (ProjTaskItemPojo) eventButton.getData(EVENTDATA_ProjTaskItemPojo);

				ContentHandlerSpecs allocatdTaskSpecs = commonData.getContentHandlerSpecsMap().get(CONTENTTYPE1_AllocatdTask);

				String allocatdTaskArtifactName = AllocatdTask.getAllocatdTaskArtifactName (
														commons.userName, allocatdTaskSpecs, 
														projTaskPojoInEvent.projectName, projTaskPojoInEvent.taskID);
				ArtifactKeyPojo allocatedTaskArtifactKeyPojo = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
														invokedArtifactPojo.artifactKeyPojo.relevance,
														allocatdTaskArtifactName, allocatdTaskSpecs.contentType);
				
				SelfAuthoredArtifactpojo allocTaskSelfAuthoredArtifactpojo = setupDraftArtifact(allocatedTaskArtifactKeyPojo);

				ContentHandlerInterface contentHandlerInterface = ContentHandlerManager.getInstance(commons, catelogPersistenceManager, allocatdTaskSpecs.contentType);
				contentHandlerInterface.initializeContentHandlerForDraftArtifact((CommonUIData) commonData, allocTaskSelfAuthoredArtifactpojo);
				AllocatdTaskItemPojo allocatdTaskItemPojo = (AllocatdTaskItemPojo) contentHandlerInterface.getFocusedItemPojo();

				allocatdTaskItemPojo.teamID = projTaskPojo.artifactName;
				allocatdTaskItemPojo.taskID = projTaskPojo.taskID;
				allocatdTaskItemPojo.description = projTaskPojo.description;
				allocatdTaskItemPojo.timeEstimated = projTaskPojo.plannedHours;
				allocatdTaskItemPojo.plannedStart = projTaskPojo.plannedStart;
				allocatdTaskItemPojo.plannedEnd = projTaskPojo.plannedEnd;

				contentHandlerInterface.writePrimer();

				catelogPersistenceManager.updateArtifactStatus(
							allocTaskSelfAuthoredArtifactpojo,
							SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
				if (allocTaskSelfAuthoredArtifactpojo.unpulishedVerNum > 0) {
					commonData.getCatelogPersistenceManager()
						.updateOlderArtifact(
								allocTaskSelfAuthoredArtifactpojo.artifactKeyPojo,
								SelfAuthoredArtifactpojo.ArtifactStatusOutdated,
								allocTaskSelfAuthoredArtifactpojo.unpulishedVerNum);
				}
		
				MessageBox messageBox1 = new MessageBox(
				mainShell, SWT.OK);
				messageBox1.setMessage("Thank you! You will be associated with the task: " + projTaskPojoInEvent.taskID + " of Project: " + projTaskPojoInEvent.projectName);
				int rc1 = messageBox1.open();
				if (rc1 != SWT.OK) {
					return;
				}
			}
		});
		System.out.println("@1 linkTextButton linkTextButton.getSize().x = " + linkTextButton.getSize().x);
		linkTextButton.pack();
		inEditor.minimumWidth = linkTextButton.getSize().x;
		inEditor.horizontalAlignment = SWT.LEFT;
	}

	@Override
	public String getDefaultSourceFileName(ItemPojo inItemPojo) {
		return null;
	}

	@Override
	public ProjTasksListDoc getPrimerDoc() {
		return (ProjTasksListDoc) primerDoc;
	}

	private SelfAuthoredArtifactpojo setupDraftArtifact(ArtifactKeyPojo inArtifactKeyPojo){
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo, commonData);
		SelfAuthoredArtifactpojo extraSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
		extraSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		commonData.getCatelogPersistenceManager().insertArtifactUI(extraSelfAuthoredArtifactpojo);
		return extraSelfAuthoredArtifactpojo;
	}
}