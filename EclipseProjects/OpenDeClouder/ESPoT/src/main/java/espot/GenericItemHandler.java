package espot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList; //import org.eclipse.swt.events.SelectionAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

//import remoteAccessers.RemoteAccesser;

import com.google.gson.Gson;

public abstract class GenericItemHandler extends SelectionAdapter implements
		ContentHandlerInterface {
	/*
	 * Content handler abstraction for non-grouping types
	 */

	public static final int PREFERED_ITEM_PANEL_WIDTH = 600;
	public ArtifactPojo invokedArtifactPojo = null;
	DownloadedReviewsHandler downloadedReviewsHandler = null;
	
	public boolean invokedForEdit = false;

	public Shell mainShell = null;
	private ScrolledComposite scrolledComposite_1 = null;
	public CommonData commonData = null;
	public GenericItemDocPojo primerDoc = null;
	public boolean cloneRequested = false;
	public String contentPathFileName = null;
	public String contentPathFolderName = null;
	public Commons commons = null;
	public CatelogPersistenceManager catelogPersistenceManager = null;
	public ContentHandlerSpecs contentHandlerSpecs = null;

	Text titleText;
	Text statusText;
	
	public GenericItemHandler() {
	}

	public void initializeContentHandlerWithMinimumSetup(CommonData inCommonData){
		commonData = inCommonData;
		commons = commonData.getCommons();
	}


	public void doCommonInit(CommonData inCommonData, ArtifactPojo inArtifactPojo) {
		commonData = inCommonData;
		invokedArtifactPojo = inArtifactPojo;
		commons = commonData.getCommons();
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		contentHandlerSpecs = commonData.getContentHandlerSpecsMap().get(invokedArtifactPojo.artifactKeyPojo.contentType);

		ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);

		contentPathFileName = artifactMover.getPrimeFilePath(inArtifactPojo);

		if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
				//&& artifactMover.lastProcessStatus != ArtifactMover.NO_SOURCE_FILE) {

			if (commons.processMode == Commons.CLIENT_MACHINE) {
				ErrorHandler.displayError(mainShell, commonData.getCommons(), "Error at GenericItemHandler doCommonInit artifactMover " + artifactMover.lastProcessStatus + " while dealing with artifactName :" + inArtifactPojo.artifactKeyPojo.artifactName);
				return;
			} else {
				ErrorHandler.showErrorAndQuit(commons, "Error at GenericItemHandler doCommonInit artifactMover " + artifactMover.lastProcessStatus + " while dealing with artifactName :" + inArtifactPojo.artifactKeyPojo.artifactName);
			}
		}

		if (!commons.doesFileExist(contentPathFileName)) {
			createNewStartupPrimer(contentPathFileName,invokedArtifactPojo);
		}

		System.out.println("checkhere ");
		System.out.println("invokedForEdit = " + invokedForEdit);
		System.out.println("contentPathFileName = " + contentPathFileName);
		System.out.println("commons.doesFileExist(contentPathFileName) = " + commons.doesFileExist(contentPathFileName));

		contentPathFolderName = commonData.getCommons().getFolderNameFromFullPath(contentPathFileName);

		try {
			primerDoc = (GenericItemDocPojo) commonData.getCommons().getJsonDocFromFile(contentPathFileName,getPrimerDocClass());
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(mainShell, commonData.getCommons(), "Error at GenericItemHandler doCommonInit " + inArtifactPojo.artifactKeyPojo.artifactName, e);
		}
		System.out.println("primerDoc = " + primerDoc);
		System.out.println("primerDoc item = " + primerDoc.getItem());
		System.out.println("primerDoc title = " + primerDoc.getItem().title);

		ItemPojo itemPojo = primerDoc.getItem();
		testPrinter("From doCommonInit");

		//if (itemPojo.itemID.equalsIgnoreCase("")) {
		//	itemPojo.author = invokedArtifactPojo.author;	//the invoked artifact author name is carried for the single items
		//	itemPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance; // relevance remains same as the containing artifact
		//	itemPojo.status = invokedArtifactPojo.erlStatus;
		//	itemPojo.artifactName = invokedArtifactPojo.artifactKeyPojo.artifactName;
		//	setInitialItemPojoAddlFields();
		//	checkSetNewItemID();
		//	//itemID will be set by a separate process
		//}
		if (itemPojo.itemNumber == -1) {
			setInitialCoreFields();
			setInitialItemPojoAddlFields();
		}

		addlCommonInit();
	}
	
	public void setInitialCoreFields(){
		ItemPojo itemPojo = primerDoc.getItem();
		itemPojo.author = invokedArtifactPojo.author;	//the invoked artifact author name is carried for the single items
		itemPojo.requestor = invokedArtifactPojo.requestor;	//the invoked artifact author name is carried for the single items
		itemPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance; // relevance remains same as the containing artifact
		
		itemPojo.status = invokedArtifactPojo.erlStatus;
		itemPojo.artifactName = invokedArtifactPojo.artifactKeyPojo.artifactName;
		checkSetNewItemID();		
	}

	public void doCommonUIInit(CommonUIData inCommonUIData, ArtifactPojo inArtifactPojo) {

		System.out.println("doCommonUIInit is inCommonUIData " + inCommonUIData);
		System.out.println("doCommonUIInit is artifactName " + inArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("doCommonUIInit is mainShell " + mainShell);

		mainShell = new Shell(inCommonUIData.getESPoTDisplay(), SWT.APPLICATION_MODAL | SWT.CLOSE
				| SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.MAX);
		mainShell.setImage(new Image(inCommonUIData.getESPoTDisplay(), inCommonUIData.getCommons().applicationIcon));
		System.out.println("doCommonUIInit mainShell created " + mainShell);
		mainShell.setLayout(new GridLayout(1, false));
		//mainShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		doCommonInit(inCommonUIData, inArtifactPojo);

		downloadedReviewsHandler = new DownloadedReviewsHandler(inCommonUIData, invokedArtifactPojo.artifactKeyPojo);
	}

	public void initializeContentHandlerForDraftArtifact(CommonUIData inCommonUIData, SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo) {
		System.out.println("initializeContentHandlerForDraftArtifact before doCommonUIInit xx23a contentPathFileName: " + contentPathFileName);

		invokedForEdit = true;

		doCommonUIInit(inCommonUIData,inSelfAuthoredArtifactspojo);
		System.out.println("initializeContentHandlerForDraftArtifact after doCommonUIInit xx23b contentPathFileName: " + contentPathFileName);

		Gson gson = new Gson();
		String json = gson.toJson(primerDoc);
		System.out.println("json value of primerDoc read is " + json);

		System.out.println("@@123 doc: " + primerDoc);
		System.out.println("@@123 doc: " + primerDoc.toString());
		System.out.println("@@123 doc item: " + primerDoc.getItem());
		if (primerDoc.getItem()== null) {
			System.out.println("@@123 doc item to be initilized" + primerDoc.getItem());
			primerDoc.initializeItem();
			System.out.println("@@123 doc item got initilized" + primerDoc.getItem());
		}

		System.out.println("@@123 doc item title: " + primerDoc.getItem().title);

		testPrinter("From initializeContentHandlerForDraftArtifact1");

		ItemPojo itemPojo = primerDoc.getItem();
		testPrinter("From initializeContentHandlerForDraftArtifact2");

		itemPojo.author = invokedArtifactPojo.author;	//the invoked artifact author name is carried for the single items
		itemPojo.requestor = invokedArtifactPojo.requestor;	//the invoked artifact author name is carried for the single items
		
		itemPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance; // relevance remains same as the containing artifact
		itemPojo.status = SelfAuthoredArtifactpojo.ERLSTAT_DRAFT;

		if (itemPojo.itemID.equalsIgnoreCase("")) {
			//itemPojo.author = invokedArtifactPojo.author;	//the invoked artifact author name is carried for the single items
			//												// for item artifacts it could be different
			itemPojo.itemID = commonData.getCommons().userName + commonData.getCommons().getCurrentTimeStamp();
			setInitialItemPojoAddlFields();
		}

		System.out.println("@@ itemPojo: " + itemPojo);
		System.out.println("@@ doc: " + primerDoc);
		System.out.println("@@ doc title : " + primerDoc.getItem().title);
		System.out.println("@@1 indisplay = " + ((CommonUIData) commonData).getESPoTDisplay());
	}

	public void setItemFromInvokdedAritifact() {
		ItemPojo itemPojo = primerDoc.getItem();
		itemPojo.author = invokedArtifactPojo.author;
		itemPojo.requestor = invokedArtifactPojo.requestor;	//the invoked artifact author name is carried for the single items
		itemPojo.itemID = invokedArtifactPojo.artifactKeyPojo.artifactName;
		itemPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
	}

	public void initializeContentHandlerForDownloadedArtifact(CommonUIData inCommonUIData, ERLDownload inERLDownload) {
		System.out.println("initializeContentHandlerForDownloadedArtifact before doCommonUIInit xx23a contentPathFileName: " + contentPathFileName);
		doCommonUIInit(inCommonUIData,inERLDownload);
		System.out.println("initializeContentHandlerForDownloadedArtifact after doCommonUIInit xx23b contentPathFileName: " + contentPathFileName);
		return;		
	}

	public void initializeContentHandlerForERLDownloadAndItemFocus(CommonUIData inCommonUIData,ERLDownload inERLDownload, ItemPojo inItemPojo) {
		return;		
	}

	public void initNonUIContentHandlerForDownloadedArtifact(CommonData inCommonData, ERLDownload inERLDownload) {
		System.out.println("GenericItemHandler for initNonUIContentHandlerForDownloadedArtifact");
		doCommonInit(inCommonData, inERLDownload);		
	}

	public void initNonUIContentHandlerForDraftArtifact(CommonData inCommonData, SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo) {
		System.out.println("GenericItemHandler for initNonUIContentHandlerForDraftArtifact");
		invokedForEdit = false;
		doCommonInit(inCommonData, inSelfAuthoredArtifactspojo);
	}
	
	public ItemPojo getFocusedItemPojo() {
		return primerDoc.getItem();
	}
	
	public void initializeContentHandlerForExtdSrvrProcess(CommonData inCommonData, ArtifactPojo inArtifactPojo) {
		System.out.println("GenericItemHandler for initializeContentHandlerForExtdSrvrProcess");
		doCommonInit(inCommonData, inArtifactPojo);
	}
	
	public abstract void checkSetNewItemID();
	
	public ArrayList<ArtifactKeyPojo> extractAssociatedArtifactKeys(){
	//dummy method to satisfy interface constraint;
		return null;
	}

	public void editContentAtDesk()
			throws IOException {

		invokedForEdit = true;
		System.out.println("before editContentAtDesk");
		mainShell.setText("Item Generator: <editContentsAtDesk> for " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		System.out.println("before displayContent");
		
		String validationBeforeEdit = validateBeforeUIEdit();
		if (!validationBeforeEdit.equalsIgnoreCase("")) {
			MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
			messageBox1.setMessage(validationBeforeEdit);
			int rc1 = messageBox1.open();
		} else {
			displayItemUI();
			System.out.println("after displayContent");
		}
		
		System.out.println("after editContentAtDesk");
	}
	
	public void viewContentsAtDesk() throws IOException
	{
		invokedForEdit = false;
		mainShell.setText("Item Generator: <viewContentsAtDesk> for " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	
		System.out.println("before displayItemUI()");
		displayItemUI();
		System.out.println("after displayItemUI()");

	}

	public void displayCloneCreateArtifactUI(ERLpojo inCloneERLpojo) {
		cloneRequested = true;
		System.out.println("ERROR ERROR inside displayCloneCreateArtifactUI. No such scenario");
	}
	
	public void displayItemUI() {
		System.out.println("itemPojo :2: " + primerDoc.getItem());
		System.out.println("author :2: " + primerDoc.getItem().author);

		scrolledComposite_1 = new ScrolledComposite(mainShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = 100;
		gridData.heightHint = 200;
		scrolledComposite_1.setLayoutData(gridData);
		
		final Composite childCompositeOfSingleView = new Composite(scrolledComposite_1, SWT.WRAP); 
		scrolledComposite_1.setContent(childCompositeOfSingleView);

		Group lastGroup = null;

		FormLayout layout = new FormLayout();
		childCompositeOfSingleView.setLayout(layout);
	    Image bg = new Image(((CommonUIData) commonData).getESPoTDisplay(), commonData.getCommons().backgroundImagePathFileName);

		childCompositeOfSingleView.setBackgroundImage(bg);
		scrolledComposite_1.setBackgroundImage(bg);
		final Group itemContentGroup = new Group(childCompositeOfSingleView, SWT.LEFT);
		itemContentGroup.setText("ItemContent");
		itemContentGroup.setLayout(new FormLayout());

		FormData formData = new FormData();
		itemContentGroup.setLayoutData(formData);
		//itemGroup Splitting Ends
		//itemGroup Splitting Ends
		
		Group itemIDInfo = new Group(itemContentGroup, SWT.LEFT);
		itemIDInfo.setText("Item-ID");
		itemIDInfo.setLayout(new FillLayout());

		final Text itemIDText = new Text(itemIDInfo, 
				SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		itemIDText.setText(primerDoc.getItem().itemID);

		formData = new FormData();
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		itemIDInfo.setLayoutData(formData);
		lastGroup = itemIDInfo;

		Group titleInfo = new Group(itemContentGroup, SWT.LEFT);
		titleInfo.setText("Title");
		titleInfo.setLayout(new FillLayout());
	
		if (invokedForEdit) {
			titleText = new Text(titleInfo, SWT.WRAP | SWT.CENTER);
		} else {
			titleText = new Text(titleInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
		}
		
		titleText.setText(primerDoc.getItem().title);

		formData = new FormData();
		formData.top = new FormAttachment(lastGroup,0,SWT.BOTTOM);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		titleInfo.setLayoutData(formData);
		lastGroup = titleInfo;

		//////////for real author starts
		//////////
		//final Group authorInfo = new Group(itemContentGroup, SWT.LEFT);
		//authorInfo.setText("Author");
		//authorInfo.setLayout(new FillLayout());
		//formData = new FormData();
		//formData.top = new FormAttachment(lastGroup);
		//formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		//authorInfo.setLayoutData(formData);
		//
		//Text authorText = new Text(authorInfo, SWT.MULTI | SWT.READ_ONLY | SWT.CENTER);
		//if (primerDoc.getItem().author == null || primerDoc.getItem().author.equalsIgnoreCase("")) {
		//	authorText.setText(invokedArtifactPojo.author);	//For single items always the wrapper UI's author goes to item as well.
		//} else {
		//	authorText.setText(primerDoc.getItem().author);
		//}
		//lastGroup = authorInfo;

		// displayContent() - Users display starts
		Group authorsGroup = new Group(itemContentGroup, SWT.LEFT);
		authorsGroup.setLayout(new FillLayout());
		UsersDisplay usersDisplay = new UsersDisplay(commonData.getUsersHandler(),
											authorsGroup,primerDoc.getItem().author,
											false,UsersDisplay.AUTHOR_LIT);
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup,0,SWT.BOTTOM);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		authorsGroup.setLayoutData(formData);		
		lastGroup = authorsGroup;
		// displayContent() - Users display ends

		final Group statusInfo = new Group(itemContentGroup, SWT.LEFT);
		statusInfo.setText("Status");
		statusInfo.setLayout(new FillLayout());
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		statusInfo.setLayoutData(formData);

		statusText = new Text(statusInfo, SWT.MULTI | SWT.READ_ONLY | SWT.CENTER);
		if (primerDoc.getItem().status == null || primerDoc.getItem().status.equalsIgnoreCase("")) {
			statusText.setText(invokedArtifactPojo.erlStatus);	//For single items always the wrapper UI's author goes to item as well.
		} else {
			statusText.setText(primerDoc.getItem().status);
		}
		lastGroup = statusInfo;
		//////////
		//////////for status ends
		
		////// author field show ends
		
		lastGroup = setAddlFieldsForItemDisplay(itemContentGroup,lastGroup,formData,primerDoc.getItem());

		System.out.println("at3a after call to setAddlFieldsForItemDisplay is " + lastGroup);
		
		
		Group actionButtonGrp = null;

		actionButtonGrp = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		actionButtonGrp.setText("Actions");
		actionButtonGrp.setLayout(new FillLayout());
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		formData.bottom = new FormAttachment(100,0);
		actionButtonGrp.setLayoutData(formData);

		if (invokedForEdit) {
			Button saveItemButton = new Button(actionButtonGrp, SWT.PUSH | SWT.CENTER);
			saveItemButton.setText("SaveDraft");
	
			saveItemButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					testPrinter("From Item Save1");
					System.out.println("starting itemPojo save details");
					
					if (!validateAddlScrFields()){
						return;
					}

					ItemPojo itemPojo = primerDoc.getItem();
					itemPojo.title = titleText.getText();
					itemPojo.status = statusText.getText();
					itemPojo.updatedAt = commons.getDateTS();
					itemPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
					
					System.out.println("itemPojo save details follows ");
					System.out.println("itemPojo title is " + primerDoc.getItem().title);
					System.out.println("itemPojo title in primerDoc is " + primerDoc.getItem().title);
					System.out.println("primeDoc is " + primerDoc);
					System.out.println("itemPojo in primeDoc is " + primerDoc.getItem());
					System.out.println("itemPojo title from primeDoc is " + primerDoc.getItem().title);
					System.out.println("itemPojo status is " + primerDoc.getItem().status);
					System.out.println("itemPojo status from primeDoc is " + primerDoc.getItem().status);
										
					getAddlFieldsOfItemPojo(primerDoc.getItem());

					//test purpose starts 1111
					Gson gson = new Gson();

					String jsonDocString = gson.toJson(primerDoc);
					System.out.println("jsonDocString to be passed is :: " + jsonDocString);
					//test purpose ends 1111
					
					testPrinter("From Item Save2");

					writePrimer();
					System.out.println("Stored the xml file : " + contentPathFileName);
				}
			});
		}
		
		Button closeItemButton = new Button(actionButtonGrp, SWT.PUSH);
		closeItemButton.setText("Close");
		
		closeItemButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button eventButton = (Button) e.getSource();
				System.out.println("eventButton = " + eventButton);
				closeScreen();
			}
		});
		lastGroup = actionButtonGrp;

		if (downloadedReviewsHandler.canBeReviewed()) {
			Group reviewGrp = new Group(childCompositeOfSingleView, SWT.RIGHT | SWT.WRAP);
			reviewGrp.setText("ReviewContent");
	
			reviewGrp.setLayout(new GridLayout(1, false));
			formData = new FormData();
			formData.left = new FormAttachment(itemContentGroup, 0, SWT.RIGHT);
			reviewGrp.setLayoutData(formData);
	
			System.out.println("itemPojo.itemID=" + primerDoc.getItem().itemID);
			
	 		ReviewHandler reviewHander = new ReviewHandler((CommonUIData) commonData,reviewGrp,invokedArtifactPojo,primerDoc.getItem(),mainShell);
			reviewHander.displayContent();
			reviewGrp.pack();
		}
		
		itemContentGroup.pack();
		childCompositeOfSingleView.pack();
		scrolledComposite_1.pack();

		mainShell.open();
		mainShell.layout(true);
		System.out.println("before looping for mainshell dispose check");
		while (!mainShell.isDisposed()) {
			if (!((CommonUIData) commonData).getESPoTDisplay().readAndDispatch()) {
				if (((CommonUIData) commonData).getArtifactDisplayOkayToContinue()) {
					((CommonUIData) commonData).getESPoTDisplay().sleep();
				} else {
					break;
				}
			}			
		}

		System.out.println("end of......displayContent");
	}

	public void closeScreen(){
		System.out.println("disposeMainShell via closeScreen started mainShell is " + mainShell);
		Control[] oldControls = mainShell.getChildren();
		for (Control oldControl : oldControls) {
			oldControl.dispose();
		}
		System.out.println("Closing " + mainShell.getText());
		mainShell.close();
	}

	//public void createNewStartupPrimer(String inNewPrimerFilePath, String inContentType) {
	public void createNewStartupPrimer(String inNewPrimerFilePath, ArtifactPojo inArtifactpojo) {
		System.out.println("At createNewStartupPrimer json file name passed : " + inNewPrimerFilePath);

		//GenericItemDocPojo newPrimerDoc = getNewPrimerDoc();		
		//newPrimerDoc.getItem().contentType = inArtifactKeyPojo.contentType;
		primerDoc = getNewPrimerDoc();		
		ItemPojo itemPojo = primerDoc.getItem();
		
		itemPojo.relevance = inArtifactpojo.artifactKeyPojo.relevance; // relevance remains same as the containing artifact
		itemPojo.artifactName = inArtifactpojo.artifactKeyPojo.artifactName;
		itemPojo.contentType = inArtifactpojo.artifactKeyPojo.contentType;

		//itemPojo.status = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		itemPojo.status = ArtifactPojo.ERLSTAT_DRAFT;
		itemPojo.author = inArtifactpojo.author;	//the invoked artifact author name is carried for the single items
		itemPojo.requestor = inArtifactpojo.requestor;	//the invoked artifact author name is carried for the single items

		checkSetNewItemID();		

		try {
			commons.putJsonDocToFile(inNewPrimerFilePath,primerDoc);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(mainShell, commonData.getCommons(), "Error at GenericItemHandler createNewStartupPrimer " + inNewPrimerFilePath + " " + inArtifactpojo.artifactKeyPojo.artifactName, e);
		}
		System.out.println("createNewStartupPrimer Stored the json file at : " + inNewPrimerFilePath);
	}

	public abstract GenericItemDocPojo getNewPrimerDoc();
	

	public void writePrimer(){
		checkSetNewItemID(); // set Item ID if not done already
		
		try {
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
		} catch (IOException e) {

			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(mainShell, commonData.getCommons(), "Error at GenericItemHandler writePrimer " + contentPathFileName, e);
		}
		System.out.println("writeJSON Stored the xml file : " + contentPathFileName);
	}

	public void processContentAtWeb(RootPojo inRootPojo, RemoteAccesser inRemoteAccesser, RequestProcesserPojo inRequestProcesserPojo) {
		//This method invoked on the serverside; processes the uploaded document
		
		System.out.println("begin processContentAtWeb 1 GenericItemHandler");

		//collate special processes
		InputStream incomingFileStream = inRemoteAccesser.getRemoteFileStream(inRequestProcesserPojo.incomingContentFullPath);
		inRequestProcesserPojo.updatedContentInputStream = incomingFileStream;

		System.out.println("At processContentAtWeb instream created for " + inRequestProcesserPojo.incomingContentFullPath);
		System.out.println("At processContentAtWeb incomingFileStream is " + incomingFileStream);

		System.out.println("end processContentAtWeb 1 ");
	}

	public ItemPojo getUptoDateERLItem(ItemPojo inItemPojo){
		boolean erlItemNewer = false;
		ItemPojo existingItemPojo = primerDoc.getItem();
		if (inItemPojo.updatedAt==null) {
			erlItemNewer = true;
		} else if (commonData.getCommons().isThisLeftDateLater(existingItemPojo.updatedAt,inItemPojo.updatedAt)) {
			erlItemNewer = true;
		}
		if (erlItemNewer) {
			return existingItemPojo;
		} else {
			return null; 	//its important to return null, as only then 
							//the caller will understand that the item is older and not to be used
		}
	}

	public ItemPojo getERLItemByChildArtifactName(String inChildRelevance, String inChildArtifactName, String inChildContentType) {
		//dummy method which is applicable only for grouper artifacts
		// but still if its called anyway, then based on values passed respond with the current item
		ItemPojo itemPojo = primerDoc.getItem();
		if (itemPojo.artifactName.equals(inChildArtifactName) 
				&& itemPojo.relevance.equals(inChildRelevance)
				&& itemPojo.contentType.equals(inChildContentType)) {
			return itemPojo;
		}
		return null;
	}
	
	public byte[] getBaseItemDocBytes(ItemPojo inItemPojo){
	//this method is meant only for groupers
		return null;		
	}

	public abstract void testPrinter(String inPrintHead);
	public abstract String validateBeforeUIEdit();
	public abstract Group setAddlFieldsForItemDisplay(Group inItemContentGroup, Group inPrevGroup,FormData inFormData, ItemPojo inItemPojo);
	public abstract void getAddlFieldsOfItemPojo(ItemPojo inItemPojo);
	public abstract void setInitialItemPojoAddlFields();

	public abstract Class getPrimerDocClass();
	public abstract GenericItemDocPojo getPrimerDoc();

	public void setPrimerDoc(GenericItemDocPojo inPrimerDocPojo){
		primerDoc = inPrimerDocPojo;
	}
	
	public abstract boolean validateAddlScrFields();

	public void addlCommonInit() {}
	
	public int getTriggerInterval(){
		// Default dummy process as its not a required function for all contents
		commonData.getCommons().logger.info(" default getTriggerInterval is called which is unexexpected. Invoked artifactname is " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		return -1;
	}
	public void triggeredProcess(String inTriggerAt){
		// Default dummy process as its not a required function for all contents
		commonData.getCommons().logger.info(" default getTriggerInterval is called which is unexexpected. Invoked artifactname is " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	}
	
	public String prevalidate(CommonData inCommonData,ArtifactKeyPojo inArtifactKeyPojo){
		//dummy method; to be overridden where used
		String validateString = "";
		return validateString;
	}

}