package espot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import commonTechs.ExportSWTTableToExcel;

public abstract class GenericGrouper extends SelectionAdapter implements
		ContentHandlerInterface {
	/*
	 * Content handler abstraction for Grouping Types
	 */
	public final static String SCREENROWNUMLIT = "screenRowNum";
	public final static String ItemOnRowHasALocalDraftLIT = "ItemOnRowHasALocalDraftFlag";
	
	public final static String ITEMNUMLIT = "ItemNumber";
	public final static String COLFIELDLIT = "ColumnField";

	public static final int PREFERED_ITEM_PANEL_WIDTH = 600;
	
	public ArtifactPojo invokedArtifactPojo = null;
	DownloadedReviewsHandler downloadedReviewsHandler = null;
	
	public boolean invokedForEdit = false;
	protected HashMap <Integer, ArrayList> displayedItemMap = null;
	
	protected Shell mainShell = null;
	protected Composite buttonRibbon = null;	
	private ScrolledComposite scrolledComposite_1 = null;
	public Composite childCompositeOfMultiView = null;
	public Composite itemsMainCompositeInMultiConentView = null;
	private GridData itemsReviewGridData;
	
	public ScrolledComposite ribbonScrollPane = null;	
	public ScrolledComposite artifactRwScrollPane = null;
	//public ScrolledComposite scrolledComposite_main = null;
	public CommonData commonData = null;
	public Commons commons = null;
	public CatelogPersistenceManager catelogPersistenceManager = null;
	public ContentHandlerSpecs contentHandlerSpecs = null;
	
	public ArrayList<?> itemList = null;	
	ArrayList<ItemPojo> filteredItems = null;
	public GenericGrouperDocPojo primerDoc = null;

	private ERLpojo cloneERLpojo = null;
	protected boolean cloneRequested = false;

	protected String contentPathFileName = null;
	protected String contentPathFolderName = null;

	protected String[] addlLeftColumnHeaders = null;
	protected String[] coreLeftColumnHeaders = null;
	protected String[] coreRightColumnHeaders = null;
	protected String[] columnHeaders = null;
	protected String[] centerBaseColHeaders = null;
	protected String[] centerAddlColHeaders = null;
	
	public ItemPojo focusedItemPojo = null;
	public ItemPojo viewFocusItemPojo = null;
	
	private static final String hidefiltersLIT = "HideFilters";
	protected Composite filtersGroup;
	protected List statusSelList;
	//protected Text allSelectedStatusText;
	protected boolean hideShowFilter;
	
	private Button showOrHideFiltersButton;
	private Button applyFilterButton;
	
	private GridData filtersRibbonData;
	
	protected Table table;
	
	public ItemPojo getERLItemByChildArtifactName(String inChildRelevance, String inChildArtifactName, String inChildContentType) {
		ItemPojo existingItemPojo = null;
		if (inChildRelevance != null && !inChildRelevance.isEmpty()
			&& inChildArtifactName != null && !inChildArtifactName.isEmpty()
			&& inChildContentType != null && !inChildContentType.isEmpty()) {
			existingItemPojo = primerDoc.getItemByChildArtifactName(inChildRelevance, inChildArtifactName, inChildContentType);
		}
		return existingItemPojo;
	}
	
	public ItemPojo getUptoDateERLItem(ItemPojo inItemPojo){
		boolean erlItemNewer = false;
		ItemPojo existingItemPojo = null;
		int itemCurrentLocation = -1;
		if (inItemPojo != null) {
			itemCurrentLocation = primerDoc.getItemsCurrentLocation(inItemPojo);
		}
		if (itemCurrentLocation > -1) {
			existingItemPojo = (ItemPojo) itemList.get(itemCurrentLocation);
			if (inItemPojo.updatedAt==null) {
				erlItemNewer = true;
			} else if (!commonData.getCommons().isThisLeftDateLater(inItemPojo.updatedAt,existingItemPojo.updatedAt)) {
				erlItemNewer = true;
			}
		}
		if (erlItemNewer) {
			return existingItemPojo;
		} else {
			return null; 	//its important to return null, as only then 
							//the caller will understand that the item is older and not to be used
		}
	}
	
	public byte[] getBaseItemDocBytes(ItemPojo inItemPojo) {
		GenericItemDocPojo itemDoc = getBaseDoc(inItemPojo);
		return commonData.getCommons().sysGetBytesFromJsonObj(itemDoc);
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
			//ErrorHandler.displayError(mainShell, commonData.getCommons(), "Error at GenericGrouper doCommontInit " + artifactMover.lastProcessStatus + " while dealing with : " + contentPathFileName);
			//return;
			if (commons.processMode == Commons.CLIENT_MACHINE) {
				ErrorHandler.displayError(mainShell, commonData.getCommons(), "Error at GenericGrouper doCommontInit " + artifactMover.lastProcessStatus + " while dealing with : " + contentPathFileName);
				return;
			} else {
				ErrorHandler.showErrorAndQuit(commons, "Error at GenericGrouper doCommonInit artifactMover " + artifactMover.lastProcessStatus + " while dealing with artifactName :" + inArtifactPojo.artifactKeyPojo.artifactName);
			}
		}
		System.out.println("contentPathFileName = " + contentPathFileName);
		contentPathFolderName = commonData.getCommons().getFolderNameFromFullPath(contentPathFileName);
		System.out.println("contentPathFolderName = " + contentPathFolderName);
		

		//itemList = primerDoc.getItemList();	// 20Apr2019 Kannan: I was tempted to introduce a refresh logic for
		//										// replacing any item with the locally drafted later version if any
		//										// but it wont be wise since a) its not the same that others view and
		//										// b) the local draft still needs to go through addl steps before 
		//										// it becomes eligible to be included
		readPrimerFile();
		extendedCommonInit(); 					// this call seems to be a dummy. Check

		System.out.println("At doCommonInit mainShell = " + mainShell);
		
		addlCommonInit();
	}

	public abstract Class getPrimerDocClass();

	public void readPrimerFile(){
		try {
			primerDoc = (GenericGrouperDocPojo) commonData.getCommons().getJsonDocFromFile(contentPathFileName,getPrimerDocClass());
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commonData.getCommons(), "Error at GenericGrouper doCommontInit while reading " + contentPathFileName, e);
		}

		itemList = primerDoc.getItemList();		// 20Apr2019 Kannan: I was tempted to introduce a refresh logic for
												// replacing any item with the locally drafted later version if any
												// but it wont be wise since a) its not the same that others view and
												// b) the local draft still needs to go through addl steps before 
												// it becomes eligible to be included
		
	}
	
	public void addlCommonInit() {}	// to be overridden as required

	public void doCommonUIInit(CommonUIData inCommonUIData, ArtifactPojo inArtifactPojo) {

		displayedItemMap = new HashMap<Integer, ArrayList>();
	
		mainShell = new Shell(inCommonUIData.getESPoTDisplay(), SWT.APPLICATION_MODAL | SWT.CLOSE
				| SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.MAX);
		mainShell.setImage(new Image(inCommonUIData.getESPoTDisplay(), inCommonUIData.getCommons().applicationIcon));
		mainShell.setLayout(new GridLayout(1, false));
		mainShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		System.out.println("At doCommonUIInit mainShell = " + mainShell);
		doCommonInit(inCommonUIData, inArtifactPojo);

		downloadedReviewsHandler = new DownloadedReviewsHandler(inCommonUIData, invokedArtifactPojo.artifactKeyPojo);
		
		///////////
		///////////
		////create pane for control ribbon starts
		ribbonScrollPane = new ScrolledComposite(mainShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		ribbonScrollPane.setLayoutData(gridData2);
		buttonRibbon = new Composite(ribbonScrollPane, SWT.NONE); 
		ribbonScrollPane.setContent(buttonRibbon);
		ribbonScrollPane.setLayout(new GridLayout());

		buttonRibbon.setLayout(new GridLayout(10,false));

		Image bg = new Image(((CommonUIData) commonData).getESPoTDisplay(), commonData.getCommons().backgroundImagePathFileName);
		buttonRibbon.setBackgroundImage(bg);
		//create pane for control ribbon ends		
		///////////
		///////////
		setSelectionFilters();

		additionalRibbonButtons();
		setExportButton();
		
		buttonRibbon.pack();
	}

	private void showArtifactReview() {
		System.out.println("review Artifact: " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		artifactRwScrollPane = new ScrolledComposite(mainShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = 30;
		artifactRwScrollPane.setLayoutData(gridData);		
		final Composite childCompositOfSingleView2 = new Composite(artifactRwScrollPane, SWT.NONE); 
		artifactRwScrollPane.setContent(childCompositOfSingleView2);
		artifactRwScrollPane.setLayout(new GridLayout());
		
 		ReviewHandler reviewHander = new ReviewHandler
 		((CommonUIData) commonData, 
 				childCompositOfSingleView2, 
 				invokedArtifactPojo,
 				mainShell);
		reviewHander.displayContent();

		childCompositOfSingleView2.pack();
		mainShell.pack();
		mainShell.layout(true);
	}

	public void initializeContentHandlerWithMinimumSetup(
			CommonData inCommonData) {

		System.out.println("GenericGrouper initiated for initializeContentHandlerWithMinimumSetup");
		commonData = inCommonData;
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
	}

	public void initializeContentHandlerForDraftArtifact(CommonUIData inCommonUIData, 
							SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo) {

		System.out.println("GenericGrouper for initializeContentHandlerForDraftArtifact");
		doCommonUIInit(inCommonUIData, inSelfAuthoredArtifactspojo);
	}

	public void initializeContentHandlerForDownloadedArtifact(CommonUIData inCommonUIData,ERLDownload inERLDownload) {

		System.out.println("GenericGrouper for initializeContentHandlerForDownloadedArtifact");
		doCommonUIInit(inCommonUIData, inERLDownload);
	}

	public void initializeContentHandlerForERLDownloadAndItemFocus(CommonUIData inCommonUIData,ERLDownload inERLDownload, ItemPojo inItemPojo) {
		System.out.println("GenericGrouper for initializeContentHandlerForDownloadedArtifact");
		doCommonUIInit(inCommonUIData, inERLDownload);
		viewFocusItemPojo = inItemPojo;
	}
	
	public void initNonUIContentHandlerForDownloadedArtifact(CommonData inCommonData, ERLDownload inERLDownload) {
		System.out.println("GenericGrouper for initNonUIContentHandlerForDownloadedArtifact");
		doCommonInit(inCommonData, inERLDownload);		
	}

	public void initNonUIContentHandlerForDraftArtifact(CommonData inCommonData, SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo) {
		System.out.println("GenericItemHandler for initNonUIContentHandlerForDraftArtifact");
		doCommonInit(inCommonData, inSelfAuthoredArtifactspojo);
	}

	public ItemPojo getFocusedItemPojo() {
		//more thought required for the usage
		return focusedItemPojo;
	}
	
	public void initializeContentHandlerForDownloadedItem(CommonUIData inCommonUIData, ItemKeyPojo inItemKeyPojo, String inMultiContentFileName) {

		System.out.println("GenericGrouper for initializeContentHandlerForDownloadedItem");
		System.out.println("@initializeContentHandlerForDownloadedItem. This method should not have been invoked. Something Wrong!!!");
		System.exit(8);
	}

	public void initializeContentHandlerForExtdSrvrProcess(CommonData inCommonData,ArtifactPojo inArtifactPojo) {
		System.out.println("GenericGrouper for initializeContentHandlerForExtdSrvrProcess");
		doCommonInit(inCommonData, inArtifactPojo);
	}


	public void editContentAtDesk()
			throws IOException {

		System.out.println("GenericGrouper initiated for editContentAtDesk");
		
		invokedForEdit = true;
		mainShell.setText("Item Generator: <editContentsAtDesk> for " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		showRibbonEditControls();
		buttonRibbon.pack();		

		System.out.println("before displayContent");
		displayMultiContent();
		shellDisposeHolder();
		System.out.println("after displayContent");
	}

	public void viewContentsAtDesk() throws IOException
	{
		System.out.println("GenericGrouper initiated for viewContentsAtDesk");

		invokedForEdit = false;
		System.out.println("mainShell = " + mainShell);
		mainShell.setText(invokedArtifactPojo.artifactKeyPojo.contentType + " <viewContentsAtDesk> for " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("invokedArtifactPojo artifactKeyPojo artifactName is = " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		System.out.println("before displayContent");
		displayMultiContent();
		shellDisposeHolder();		
		System.out.println("after displayContent");
	}

	public void displayCloneCreateArtifactUI(ERLpojo inCloneERLpojo) {
		System.out.println("GenericGrouper displayCloneCreateArtifactUI");
		cloneERLpojo = inCloneERLpojo;
		cloneRequested = true;
		System.out.println("inside displayCloneCreateArtifactUI");
	}
	
	public void checkIfItemOnRowHasALocalDraft(ItemPojo inItemPojoScrolled, Button inMaintenanceButton){
		boolean itemOnRowHasALocalDraft = false;
		ArtifactKeyPojo artifactKeyPojoOnRow = new ArtifactKeyPojo(
				invokedArtifactPojo.artifactKeyPojo.rootNick,
				inItemPojoScrolled.relevance,
				inItemPojoScrolled.artifactName,
				inItemPojoScrolled.contentType);
		SelfAuthoredArtifactpojo localDraft = catelogPersistenceManager.readSelfAuthoredArtifact(artifactKeyPojoOnRow);
		if (localDraft != null) {
			System.out.println("at setFlags localDraft.draftingState is " + localDraft.draftingState);
			System.out.println("at setFlags SelfAuthoredArtifactpojo.ArtifactStatusDraft is " + SelfAuthoredArtifactpojo.ArtifactStatusDraft);
			System.out.println("at setFlags SelfAuthoredArtifactpojo.ArtifactStatusToBeBatchUploaded is " + SelfAuthoredArtifactpojo.ArtifactStatusToBeBatchUploaded);

			if (!localDraft.draftingState.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusProcessed)
				&& !localDraft.draftingState.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusOutdated)) {
				itemOnRowHasALocalDraft = true;				
			}
		}
		
		if (itemOnRowHasALocalDraft){
			inMaintenanceButton
			.setText("*" + inItemPojoScrolled.artifactName);
			inMaintenanceButton.setToolTipText("click for maintenance of: " + inItemPojoScrolled.title + ". A draft is already available");
		} else {
			inMaintenanceButton
			.setText(inItemPojoScrolled.artifactName);			
			inMaintenanceButton.setToolTipText("click for maintenance of: " + inItemPojoScrolled.title);
		}		
		inMaintenanceButton.setData(ItemOnRowHasALocalDraftLIT, itemOnRowHasALocalDraft);
	}

	public void setDisplayCoreLeftFieldsInMultiDisplay(TableEditor inEditor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojoScrolled, Button inMaintenanceButton, int inScreenRowNum) {
		// the reason the maintenanceButton is kept as argument is it would be referred for setting focus on specific record.
		System.out.println("setDisplayCoreLeftFieldsInMultiDisplay");
		inMaintenanceButton.setData(SCREENROWNUMLIT, inScreenRowNum);

		System.out.println("itemPojo.itemID:"+inItemPojoScrolled.itemID);
		System.out.println("set data = " + inMaintenanceButton.getData("screenRowNum"));

		checkIfItemOnRowHasALocalDraft(inItemPojoScrolled,inMaintenanceButton);
		maintenanceButtonProcess(inMaintenanceButton);
		inMaintenanceButton.pack();
		
		TableEditor maintenanceButtonEditor = new TableEditor(inTable);
		maintenanceButtonEditor.minimumWidth = inMaintenanceButton.getSize().x;
		maintenanceButtonEditor.horizontalAlignment = SWT.LEFT;
		maintenanceButtonEditor.setEditor(inMaintenanceButton, inTableItem,++inLastColLocation);
		inTableItem.setText(inLastColLocation, inItemPojoScrolled.artifactName);
		
		System.out.println("maintenanceButton cellEditor.minimumWidth = " + maintenanceButtonEditor.minimumWidth);
		System.out.println("maintenanceButton text = " + inMaintenanceButton.getText());
		System.out.println("linkTextButton cellEditor.horizontalAlignment = " + maintenanceButtonEditor.horizontalAlignment);
		System.out.println("maint button absoluteColumnPosition = " + inLastColLocation);

		inEditor = new TableEditor(inTable);
		Text title_Tx = new Text(inTable, SWT.READ_ONLY);
		title_Tx.setText(inItemPojoScrolled.title);
		inEditor.grabHorizontal = true;
		inEditor.setEditor(title_Tx, inTableItem, ++inLastColLocation);
		inTableItem.setText(inLastColLocation, inItemPojoScrolled.title);
	}

	public void setDisplayItemscoreRightFieldsInMultiDisplay(TableEditor inEditor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo itemPojoScrolled, int screenRowNum) {
		System.out.println("At setDisplayItemscoreRightFieldsInMultiDisplay lastColLocation = " + inLastColLocation);
		TableEditor reviewButtonEditor = new TableEditor(inTable);
		Button reviewButton = new Button(inTable, SWT.PUSH);
		System.out.println("@1 reviewButton.getSize().x = " + reviewButton.getSize().x);
		reviewButton
				.setText("Review");
		System.out.println("@2 reviewButton.getSize().x = " + reviewButton.getSize().x);
		reviewButton.setToolTipText("click to review " + itemPojoScrolled.title);
		System.out.println("@3 reviewButton.getSize().x = " + reviewButton.getSize().x);
		reviewButton.setData(SCREENROWNUMLIT, screenRowNum);
		System.out.println("@4 reviewButton.getSize().x = " + reviewButton.getSize().x);

		System.out.println("set data = "
				+ reviewButton.getData("screenRowNum"));
		
		reviewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button eventButton = (Button) e.getSource();
				System.out.println("eventButton = " + eventButton);
				int selectedItemRow = (Integer) eventButton
						.getData("screenRowNum");
				System.out.println("selected screenRowNum = "
						+ selectedItemRow);
				if (displayedItemMap.containsKey(selectedItemRow)) {
					System.out.println("item already in the view");
					return;
				}
				ArrayList itemsObjects = new ArrayList();
				displayedItemMap.put(selectedItemRow, itemsObjects);
				
				//ItemPojo selectedItemPojo  = (ItemPojo) itemList.get(selectedItemRow);
				ItemPojo selectedItemPojo  = (ItemPojo) filteredItems.get(selectedItemRow);
				
				System.out.println("This item is being viewed = " + selectedItemPojo.title);
				primerDoc.setItem(selectedItemRow, selectedItemPojo);
				System.out.println("itemPojo :2: " + selectedItemPojo);

			    displayItemUI(selectedItemPojo,selectedItemRow);
			}
		});
		System.out.println("@5 reviewButton.getSize().x = " + reviewButton.getSize().x);
		reviewButton.pack();
		System.out.println("@6 reviewButton.getSize().x = " + reviewButton.getSize().x);
		reviewButtonEditor.minimumWidth = reviewButton.getSize().x;
		reviewButtonEditor.horizontalAlignment = SWT.LEFT;
		reviewButtonEditor.setEditor(reviewButton, inTableItem,++inLastColLocation);
		if (commonData.getContentHandlerSpecsMap().get(itemPojoScrolled.contentType).rollupAddupType){
			if (downloadedReviewsHandler.getArtifactAllReviewsPojo()!=null){
				inTableItem.setText(inLastColLocation,downloadedReviewsHandler.getArtifactAllReviewsPojo().getItemAllReviews(itemPojoScrolled.itemID));
			}
		} else {
			ArtifactKeyPojo itemArtifactKeyPojo = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, 
														itemPojoScrolled.relevance,
														itemPojoScrolled.artifactName,
														itemPojoScrolled.contentType);
					
			DownloadedReviewsHandler itemDownloadedReviewsHandler = new DownloadedReviewsHandler((CommonUIData) commonData, itemArtifactKeyPojo);
			if (downloadedReviewsHandler.getArtifactAllReviewsPojo()!=null){
				inTableItem.setText(inLastColLocation,itemDownloadedReviewsHandler.getArtifactAllReviewsPojo().getItemAllReviews(itemPojoScrolled.itemID));
			}
		}
		
		System.out.println("xxx reviewButton button rightColumnStartLocation = " + inLastColLocation);
		System.out.println("xxx reviewButton button reviews for itemPojoScrolled.itemID below " + itemPojoScrolled.itemID);
		//System.out.println("xxx reviewButton button reviews = " + downloadedReviewsHandler.getArtifactAllReviewsPojo().getItemAllReviews(itemPojoScrolled.itemID));
		
		System.out.println("reviewButtonEditor.minimumWidth = " + reviewButtonEditor.minimumWidth);
		System.out.println("reviewButton text = " + reviewButton.getText());
		System.out.println("reviewButton reviewButton.horizontalAlignment = " + reviewButtonEditor.horizontalAlignment);
		System.out.println("reviewButton @ lastColLocation = " + inLastColLocation);

		//if (commonData.getCommons().userName.equals("reviewer")) {
		//	
		//	System.out.println("Got inside the reviewer path");
		//
		//	// Approve & Reject Buttons
		//	TableEditor approveButtonEditor = new TableEditor(inTable);
		//	Button approveButton = new Button(inTable, SWT.PUSH);
		//	approveButton
		//			.setText("Approve");
		//	approveButton.setData(SCREENROWNUMLIT, screenRowNum);
		//
		//	System.out.println("set data = "
		//			+ approveButton.getData("screenRowNum"));
		//
		//	approveButton.addSelectionListener(new SelectionAdapter() {
		//		@Override
		//		public void widgetSelected(SelectionEvent e) {
		//			Button eventButton = (Button) e.getSource();
		//			System.out.println("eventButton = " + eventButton);
		//			Integer i = (Integer) eventButton
		//					.getData("screenRowNum");
		//			System.out.println("selected screenRowNum = "
		//					+ i);
		//			ItemPojo selectedItemPojo  = (ItemPojo) itemList.get(i);
		//
		//			System.out.println("This item is passed = " + selectedItemPojo.title);
		//
		//		}
		//	});
		//
		//	approveButton.pack();
		//	approveButtonEditor.minimumWidth = approveButton.getSize().x;
		//	approveButtonEditor.horizontalAlignment = SWT.LEFT;
		//	approveButtonEditor.setEditor(approveButton, inTableItem,++inLastColLocation);
		//	System.out.println("approve button lastColLocation = " + inLastColLocation);
		//
		//	TableEditor rejectButtonEditor = new TableEditor(inTable);
		//	Button rejectButton = new Button(inTable, SWT.PUSH);
		//	rejectButton
		//			.setText("Reject");
		//	rejectButton.setData(SCREENROWNUMLIT, screenRowNum);
		//
		//	System.out.println("set data = "
		//			+ rejectButton.getData("screenRowNum"));
		//
		//	rejectButton.addSelectionListener(new SelectionAdapter() {
		//		@Override
		//		public void widgetSelected(SelectionEvent e) {
		//			Button eventButton = (Button) e.getSource();
		//			System.out.println("eventButton = " + eventButton);
		//			Integer i = (Integer) eventButton
		//					.getData("screenRowNum");
		//			System.out.println("selected screenRowNum = "
		//					+ i);
		//			ItemPojo selectedItemPojo  = (ItemPojo) itemList.get(i);
		//
		//			System.out.println("This item is rejected = " + selectedItemPojo.title);
		//		}
		//	});
		//
		//	rejectButton.pack();
		//	rejectButtonEditor.minimumWidth = rejectButton.getSize().x;
		//	rejectButtonEditor.horizontalAlignment = SWT.LEFT;
		//	rejectButtonEditor.setEditor(rejectButton, inTableItem,++inLastColLocation);
		//	System.out.println("reject button rightColumnStartLocation = " + inLastColLocation);
		//	
		//}		
	}
	
	public void displayMultiContent() {
		System.out.println("GenericGrouper displayMultiContent");

		childCompositeOfMultiView = new Composite(mainShell, SWT.NONE);
		childCompositeOfMultiView.setLayout(new GridLayout(1,false));

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = 100;

		childCompositeOfMultiView.setLayoutData(gridData);
		
		table = new Table(childCompositeOfMultiView, SWT.BORDER| SWT.V_SCROLL | SWT.H_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		coreLeftColumnHeaders = new String[] { 
				"Artifact",
				"Title"};
		coreRightColumnHeaders = new String[] { 
				"Review"
				//,"Approve"
				//,"Reject"
				};
		setAddlColumnHeaders();

		if (addlLeftColumnHeaders == null) { addlLeftColumnHeaders = new String[]{};}
		if (centerBaseColHeaders == null) { centerBaseColHeaders = new String[]{};}
		if (centerAddlColHeaders == null) { centerAddlColHeaders = new String[]{};}
		
		columnHeaders = commonData.getCommons().getCombinedStringArray5(addlLeftColumnHeaders,coreLeftColumnHeaders, centerBaseColHeaders, centerAddlColHeaders, coreRightColumnHeaders);

		System.out.println("columnHeaders size " + columnHeaders.length);
		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			column.setWidth(100);
			System.out.println("columnHeaders col(" + i + ") " + columnHeaders[i]);
		}
		System.out.println("screen rows filling starts");

		populateScreenItems();
		
		//ArrayList<ItemPojo> filteredItems = getFilteredItemList();
		//
		////for (int screenRowNum = 0; screenRowNum < itemList.size(); screenRowNum++) {
		//for (int screenRowNum = 0; screenRowNum < filteredItems.size(); screenRowNum++) {
		//
		//	System.out.println("screenRowNum = " + screenRowNum);
		//	int lastColLocation = -1;
		//	
		//	//ItemPojo itemPojoScrolled = (ItemPojo) itemList.get(screenRowNum);
		//	ItemPojo itemPojoScrolled = (ItemPojo) filteredItems.get(screenRowNum);
		//	
		//
		//	TableItem tableItem = new TableItem(table, SWT.NONE);
		//
		//	TableEditor editor = new TableEditor(table);
		//
		//	setDisplayItemsAddlLeftFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled);
		//	lastColLocation = addlLeftColumnHeaders.length - 1;
		//
		//	Button maintenanceButton = new Button(table, SWT.PUSH);			
		//	setDisplayCoreLeftFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled, maintenanceButton, screenRowNum);
		//
		//	lastColLocation = addlLeftColumnHeaders.length + coreLeftColumnHeaders.length - 1;
		//	setDisplayItemsCenterBaseFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled);
		//	
		//	lastColLocation = addlLeftColumnHeaders.length + coreLeftColumnHeaders.length + centerBaseColHeaders.length - 1;
		//	setDisplayItemsCenterAddlFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled);
		//
		//	lastColLocation = addlLeftColumnHeaders.length + coreLeftColumnHeaders.length + centerBaseColHeaders.length + centerAddlColHeaders.length - 1;
		//	setDisplayItemscoreRightFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled,screenRowNum);
		//
		//	if (itemPojoScrolled.equals(viewFocusItemPojo)) {
		//
		//		maintenanceButton.setFocus();
		//	}
		//	
		//}

		////create pane for individual items start1
		//scrolledComposite_main = new ScrolledComposite(mainShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		//
		//GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		//scrolledComposite_main.setLayoutData(gridData2);
		//
		//itemsMainCompositeInMultiConentView = new Composite(scrolledComposite_main, SWT.NONE); 
		//scrolledComposite_main.setContent(itemsMainCompositeInMultiConentView);

		itemsMainCompositeInMultiConentView = new Composite(mainShell, SWT.NONE); 		
		itemsReviewGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		itemsMainCompositeInMultiConentView.setLayoutData(itemsReviewGridData);
		
		itemsMainCompositeInMultiConentView.setLayout(new GridLayout(1,true));

		itemsReviewGridData.exclude = true; // hide until any item invoked for review		
		
	    Image bg = new Image(((CommonUIData) commonData).getESPoTDisplay(), commonData.getCommons().backgroundImagePathFileName);

	    itemsMainCompositeInMultiConentView.setBackgroundImage(bg);
	    itemsMainCompositeInMultiConentView.pack();		
		////create pane for individual items end1
		
		//childCompositeOfMultiView.pack();

	    if (downloadedReviewsHandler.canBeReviewed()){
	    	showArtifactReview();
	    }

		itemsMainCompositeInMultiConentView.pack();
		//scrolledComposite_main.pack();
		mainShell.pack();		
		mainShell.open();
	}
	
	public void populateScreenItems(){

		table.removeAll(); // clearing if called via filtering
		for (Control control : table.getChildren()){
			control.dispose();
		}
		
		applyFilterButton.setVisible(false);

		filteredItems = getFilteredItemList();

		for (int screenRowNum = 0; screenRowNum < filteredItems.size(); screenRowNum++) {

			System.out.println("screenRowNum = " + screenRowNum);
			int lastColLocation = -1;
			
			//ItemPojo itemPojoScrolled = (ItemPojo) itemList.get(screenRowNum);
			ItemPojo itemPojoScrolled = (ItemPojo) filteredItems.get(screenRowNum);
			

			TableItem tableItem = new TableItem(table, SWT.NONE);

			TableEditor editor = new TableEditor(table);

			setDisplayItemsAddlLeftFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled);
			lastColLocation = addlLeftColumnHeaders.length - 1;

			Button maintenanceButton = new Button(table, SWT.PUSH);			
			setDisplayCoreLeftFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled, maintenanceButton, screenRowNum);

			lastColLocation = addlLeftColumnHeaders.length + coreLeftColumnHeaders.length - 1;
			setDisplayItemsCenterBaseFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled);
			
			lastColLocation = addlLeftColumnHeaders.length + coreLeftColumnHeaders.length + centerBaseColHeaders.length - 1;
			setDisplayItemsCenterAddlFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled);

			lastColLocation = addlLeftColumnHeaders.length + coreLeftColumnHeaders.length + centerBaseColHeaders.length + centerAddlColHeaders.length - 1;
			setDisplayItemscoreRightFieldsInMultiDisplay(editor,table,tableItem,lastColLocation,itemPojoScrolled,screenRowNum);

			if (itemPojoScrolled.equals(viewFocusItemPojo)) {

				maintenanceButton.setFocus();
			}
			
		}
		childCompositeOfMultiView.pack();
		mainShell.pack();
		mainShell.layout();
	}

	protected void maintenanceButtonProcess(Button inMaintenanceButton) {
		System.out.println("GenericGrouper maintenanceButtonProcess");
		System.out.println("Adding maintenanceButton event for Generic Grouper item");

		inMaintenanceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button eventButton = (Button) e.getSource();
				System.out.println("eventButton = " + eventButton);
				int selectedItemRow = (Integer) eventButton
						.getData("screenRowNum");
				System.out.println("selected screenRowNum = "
						+ selectedItemRow);
				//ItemPojo selectedItemPojo  = (ItemPojo) itemList.get(selectedItemRow);
				ItemPojo selectedItemPojo  = (ItemPojo) filteredItems.get(selectedItemRow);
				
				System.out.println("This item is being taken for maintenance = " + selectedItemPojo.title);
				System.out.println("selectedItemPojo.itemID = " + selectedItemPojo.itemID);
				System.out.println("selectedItemPojo.relevance = " + selectedItemPojo.relevance);
				System.out.println("selectedItemPojo.contentType = " + selectedItemPojo.contentType);

				ArtifactKeyPojo selectedArtifactKeyPojo = new ArtifactKeyPojo(
						invokedArtifactPojo.artifactKeyPojo.rootNick,
						selectedItemPojo.relevance,
						selectedItemPojo.artifactName,
						selectedItemPojo.contentType);

				String prevalidateString = prevalidate(commonData,selectedArtifactKeyPojo);				
				if (!prevalidateString.equalsIgnoreCase("")) {
					MessageBox editMessage1Box = new MessageBox(mainShell,
							SWT.ICON_ERROR | SWT.OK);
					editMessage1Box.setMessage(prevalidateString);
					int rc1 = editMessage1Box.open();
					return;
				}

				ArtifactPrepper artifactPrepper = new ArtifactPrepper(selectedArtifactKeyPojo,selectedItemPojo.itemID,commonData);

				if (artifactPrepper.errorEncountered) { return;}

				//SelfAuthoredArtifactpojo maintenanceArtifactPojo = null;
				//if (!artifactPrepper.localDraftActive) {
				//
				//	maintenanceArtifactPojo = artifactPrepper.createDraft();
				//	commonData.getCatelogPersistenceManager().insertArtifactUI(maintenanceArtifactPojo);
				//} else {
				//	maintenanceArtifactPojo = artifactPrepper.localDraft;
				//}
				//ArtifactWrapperUI artifactWrapperUI = 
				//		new ArtifactWrapperUI((CommonUIData) commonData, maintenanceArtifactPojo);

				ContentHandlerSpecs childContentHandlerSpecs = commonData.getContentHandlerSpecsMap().get(selectedItemPojo.contentType);
				System.out.println("at GenericGrouper maintenance button process childContentHandlerSpecs rollAdd is " + childContentHandlerSpecs.rollupOrAddup);
				
				ArtifactWrapperUI artifactWrapperUI = null;
				if (artifactPrepper.isRollupChild) { // as rollupChild cannot exist alone as an erl, need a local draft to process it
					SelfAuthoredArtifactpojo maintenanceArtifactPojo = null;
					if (!artifactPrepper.localDraftActive) {
						maintenanceArtifactPojo = artifactPrepper.createDraft();
						commonData.getCatelogPersistenceManager().insertArtifactUI(maintenanceArtifactPojo);
					} else {
						maintenanceArtifactPojo = artifactPrepper.localDraft;
					}					
					artifactWrapperUI = 
							new ArtifactWrapperUI((CommonUIData) commonData, maintenanceArtifactPojo);

				} else {
					artifactWrapperUI = 
							new ArtifactWrapperUI((CommonUIData) commonData, artifactPrepper.erlDownload);
				}
				System.out.println("at3 maintenanceButtonProcess going to display the artifactWrapperUI for new artifact");
				artifactWrapperUI.displayArtifactWrapperUI();

				System.out.println("at4 maintenanceButtonProcess ItemOnRowHasALocalDraftLIT about to check");				
				if (!(Boolean) eventButton.getData(ItemOnRowHasALocalDraftLIT)) {
					System.out.println("at4 maintenanceButtonProcess ItemOnRowHasALocalDraftLIT not true to start with");
					checkIfItemOnRowHasALocalDraft(selectedItemPojo, eventButton);
					eventButton.redraw();
					//mainShell.pack();					
					//mainShell.layout(true);
				} else {
					System.out.println("at4 maintenanceButtonProcess ItemOnRowHasALocalDraftLIT true upfront");					
				}
			}
		});
	}
	
	public abstract GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo);

	public abstract String getDefaultSourceFileName (ItemPojo inItemPojo);

	public void displayItemUI(ItemPojo reviewItemPojo, int reviewItemRow) {
		System.out.println("GenericGrouper displayItemUI");

		ArrayList itemsObjects = null;
		Group lastGroup = null;
		
		itemsObjects = displayedItemMap.get(reviewItemRow);

		itemsReviewGridData.exclude = false; // now that there is a content make its pane visibile

		Group actionButtonGrp1 = new Group(itemsMainCompositeInMultiConentView, SWT.RIGHT | SWT.WRAP | SWT.READ_ONLY);
		actionButtonGrp1.setLayout(new FillLayout());
		
		Button closeItemButton1 = new Button(actionButtonGrp1, SWT.PUSH);
		closeItemButton1.setText("Close Below Item");

		scrolledComposite_1 = new ScrolledComposite(itemsMainCompositeInMultiConentView, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		System.out.println("mutiview here");

		closeItemButton1.setData(SCREENROWNUMLIT, reviewItemRow);

		closeItemButton1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Button eventButton = (Button) e.getSource();
				System.out.println("eventButton = " + eventButton);
				Integer itemRwWithinEvent = (Integer) eventButton.getData("screenRowNum");

				System.out.println("itemRwWithinEvent = " + itemRwWithinEvent);
				
				ArrayList itemsObjsWithinEvent = displayedItemMap.get(itemRwWithinEvent);
				Control control1 = (Control) itemsObjsWithinEvent.get(0);
				control1.dispose();
				Control control2 = (Control) itemsObjsWithinEvent.get(1);
				control2.dispose();
				displayedItemMap.remove(itemRwWithinEvent);
				if (displayedItemMap.size() == 0) {
					itemsReviewGridData.exclude = true; // since all content is gone, regain real estate.
				}
				
				itemsMainCompositeInMultiConentView.pack();
				//scrolledComposite_main.pack();
				mainShell.pack();
				
				mainShell.layout(true);
			}
		});
		itemsObjects.add(actionButtonGrp1);
		itemsObjects.add(scrolledComposite_1);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = 100;
		gridData.heightHint = 200;
		scrolledComposite_1.setLayoutData(gridData);
		
		final Composite childCompositeOfSingleView = new Composite(scrolledComposite_1, SWT.NONE); 

		scrolledComposite_1.setContent(childCompositeOfSingleView);

		childCompositeOfSingleView.setLayout(new RowLayout());

	    Image bg = new Image(((CommonUIData) commonData).getESPoTDisplay(), commonData.getCommons().backgroundImagePathFileName);

		childCompositeOfSingleView.setBackgroundImage(bg);
		scrolledComposite_1.setBackgroundImage(bg);
		lastGroup = actionButtonGrp1;
		
		//itemGroup Splitting Starts
		//itemGroup Splitting Starts
		
		final Group itemContentGroup = new Group(childCompositeOfSingleView, SWT.LEFT);
		itemContentGroup.setText("ItemContent");
		itemContentGroup.setLayout(new FormLayout());


		itemContentGroup.setLayoutData(new RowData());

		lastGroup = itemContentGroup;

		//itemGroup Splitting Ends
		//itemGroup Splitting Ends
		
		Group itemIDInfo = new Group(itemContentGroup, SWT.LEFT);
		itemIDInfo.setText("Item-ID");
		itemIDInfo.setLayout(new FillLayout());

		final Text itemIDText = new Text(itemIDInfo, 
				SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		itemIDText.setText(reviewItemPojo.itemID);

		FormData formData = new FormData();
		itemIDInfo.setLayoutData(formData);

		lastGroup = itemIDInfo;

		Group titleInfo = new Group(itemContentGroup, SWT.LEFT);
		titleInfo.setText("Title");
		titleInfo.setLayout(new FillLayout());
		final Text titleText = new Text(titleInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		titleText.setText(reviewItemPojo.title);

		formData = new FormData();
		formData.top = new FormAttachment(lastGroup,0,SWT.BOTTOM);
		titleInfo.setLayoutData(formData);

		lastGroup = titleInfo;

		lastGroup = setAddlFieldsForItemDisplay(itemContentGroup,lastGroup,formData,reviewItemPojo);

		Group reviewGrp = new Group(childCompositeOfSingleView, SWT.NONE);
		reviewGrp.setText("ReviewContent");
		reviewGrp.setLayoutData(new RowData());
		reviewGrp.setLayout(new GridLayout(1, false));
		
		lastGroup = reviewGrp;

		System.out.println("itemPojo.itemID=" + reviewItemPojo.itemID);

		itemContentGroup.pack();

 		//ReviewHandler reviewHander = new ReviewHandler((CommonUIData) commonData,reviewGrp, invokedArtifactPojo,reviewItemPojo.itemID,mainShell);
 		ReviewHandler reviewHander = new ReviewHandler((CommonUIData) commonData,reviewGrp, invokedArtifactPojo,reviewItemPojo,mainShell);
		reviewHander.displayContent();
		reviewGrp.pack();
		childCompositeOfSingleView.pack();
		//scrolledComposite_1.pack();	// though this got commented, the scrolling result is still good!!!
		itemsMainCompositeInMultiConentView.pack();
		//mainShell.pack(); // commented this mainshell pack since it resizes even when screen in maxed

		mainShell.layout(true);

	}
	
	
	private void shellDisposeHolder() {
		while (!mainShell.isDisposed()) {
			if (!((CommonUIData) commonData).getESPoTDisplay().readAndDispatch()) {
				if (((CommonUIData) commonData).getArtifactDisplayOkayToContinue()) {
					((CommonUIData) commonData).getESPoTDisplay().sleep();
				} else {
					break;
				}
			}			
		}
		System.out.println("here disposing....");
		mainShell.dispose();
	}

	

	public ArrayList<ArtifactKeyPojo> extractAssociatedArtifactKeys(){
		System.out.println("At extractAssociatedArtifactsAtExtdSrvr of GenericGrouper");

		ArrayList<ArtifactKeyPojo> artifactKeyPojoList = new ArrayList<ArtifactKeyPojo>();

		System.out.println("At extractAssociatedArtifactsAtExtdSrvr itemList.size() = " + itemList.size());
		for (int itemCount = 0; itemCount < itemList.size(); itemCount++) {
			System.out.println("At extractAssociatedArtifactsAtExtdSrvr itemCount = " + itemCount);
			ItemPojo itemPojo = (ItemPojo) itemList.get(itemCount);
			ArtifactKeyPojo artifactKeyPojo = new ArtifactKeyPojo(invokedArtifactPojo.artifactKeyPojo.rootNick, itemPojo.relevance, itemPojo.artifactName, itemPojo.contentType);
			artifactKeyPojoList.add(artifactKeyPojo);
			System.out.println("At extractAssociatedArtifactsAtExtdSrvr artifactKeyPojo = " + artifactKeyPojo.artifactName);
			System.out.println("At extractAssociatedArtifactsAtExtdSrvr artifactKeyPojo = " + artifactKeyPojo.relevance);
			System.out.println("At extractAssociatedArtifactsAtExtdSrvr artifactKeyPojo = " + artifactKeyPojo.contentType);
		}
		return artifactKeyPojoList;
	}
	
	public void processContentAtWeb(RootPojo inRootPojo, RemoteAccesser inRemoteAccesser, RequestProcesserPojo inRequestProcesserPojo) {
		//This method invoked on the serverside processes the uploaded document
		
		System.out.println("begin processContentAtWeb GenericGrouper");

		System.out.println("commonData is " + commonData);
		System.out.println("commonData.getCommons() is " + commonData.getCommons());
		System.out.println("inRootPojo is " + inRootPojo);
		System.out.println("inRootPojo.rootString is " + inRootPojo.rootString);
		System.out.println("inRequestProcesserPojo is " + inRequestProcesserPojo);

		GenericGrouperDocPojo documentToUpdate = null;
		
		try {
		
//			if (inRequestProcesserPojo.prevERLPojo == null) {
//				documentToUpdate = getNewPrimerDoc();
//			} else {
//				InputStream prevFileStream = null;
//				System.out.println("inRequestProcesserPojo.prevERLPojo is " + inRequestProcesserPojo.prevERLPojo);
//				System.out.println("inRequestProcesserPojo.prevERLPojo.artifactKeyPojo is " + inRequestProcesserPojo.prevERLPojo.artifactKeyPojo);
//				System.out.println("inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance is " + inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance);
//				System.out.println("inRequestProcesserPojo.prevERLPojo.contentFileName is " + inRequestProcesserPojo.prevERLPojo.contentFileName);
//	
//				prevFileStream = inRemoteAccesser.getRemoteFileStream(commonData.getCommons().getRemotePathFileName(inRootPojo.rootString, 
//						inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance, inRequestProcesserPojo.prevERLPojo.contentFileName,inRootPojo.fileSeparator));
//				documentToUpdate = (GenericGrouperDocPojo) commonData.getCommons().getJsonDocFromInputStream(prevFileStream,getPrimerDocClass());
//				prevFileStream.close();
//				System.out.println("At processContentAtWeb closing the instream prevFileStream " + prevFileStream);
//			}
	
			System.out.println("At processContentAtWeb inRequestProcesserPojo.prevERLPojo.contentFileName is " + inRequestProcesserPojo.incomingContentFullPath);

			InputStream incomingFileStream = null;
			InputStream prevFileStream = null;
			
			//incomingFileStream = inRemoteAccesser.getRemoteFileStream(inRequestProcesserPojo.incomingContentFullPath);
			
			System.out.println("At processContentAtWeb incomingFileStream is " + incomingFileStream);
			System.out.println("At processContentAtWeb ContentHandlerSpecs.ROLLUP_ADDUP_TYPE_ROLLUP is " + inRequestProcesserPojo.contentHandlerSpecs.rollupOrAddup);
			System.out.println("At processContentAtWeb contenttype is " + inRequestProcesserPojo.contentHandlerSpecs.contentType);
			
			//if ((inRequestProcesserPojo.contentHandlerSpecs.rollupOrAddup.equalsIgnoreCase(ContentHandlerSpecs.ROLLUP_ADDUP_TYPE_ROLLUP))
			//	|| (inRequestProcesserPojo.contentHandlerSpecs.rollupOrAddup.equalsIgnoreCase(ContentHandlerSpecs.ROLLUP_ADDUP_TYPE_ADDUP))) {
			if (inRequestProcesserPojo.contentHandlerSpecs.rollupAddupType) {
				//For rollup contents such as innovations we need to absorb 'single item' from base content type
				//For other types just need to refresh whole list as both incoming and prev will be both 'group items'
				//documentToUpdate.absorbIncomingItemPojo(incomingDoc.getBaseItemDoc());
	
				System.out.println("At processContentAtWeb ContentHandlerSpecs.ROLLUP_ADDUP_TYPE_ROLLUP or addup side process ");
				
				if (inRequestProcesserPojo.prevERLPojo == null) {
					documentToUpdate = getNewPrimerDoc();

					// set up any initial content
					setInitialContent(inRequestProcesserPojo.requestPojo.relevance, inRequestProcesserPojo.requestPojo.contentType, documentToUpdate);
					
				} else {
					System.out.println("inRequestProcesserPojo.prevERLPojo is " + inRequestProcesserPojo.prevERLPojo);
					System.out.println("inRequestProcesserPojo.prevERLPojo.artifactKeyPojo is " + inRequestProcesserPojo.prevERLPojo.artifactKeyPojo);
					System.out.println("inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance is " + inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance);
					System.out.println("inRequestProcesserPojo.prevERLPojo.contentFileName is " + inRequestProcesserPojo.prevERLPojo.contentFileName);
		
					prevFileStream = inRemoteAccesser.getRemoteFileStream(commonData.getCommons().getRemotePathFileName(inRootPojo.rootString, 
							inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance, inRequestProcesserPojo.prevERLPojo.contentFileName,inRootPojo.fileSeparator));
					documentToUpdate = (GenericGrouperDocPojo) commonData.getCommons().getJsonDocFromInputStream(prevFileStream,getPrimerDocClass());
					prevFileStream.close();
					System.out.println("At processContentAtWeb closing the instream prevFileStream " + prevFileStream);
				}

				GenericItemDocPojo incomingDoc = null;

				if (inRequestProcesserPojo.requestPojo.artifactOrReview.equalsIgnoreCase(RequestPojo.ARTIFACT)) {
					
					incomingFileStream = inRemoteAccesser.getRemoteFileStream(inRequestProcesserPojo.incomingContentFullPath);
					incomingDoc = (GenericItemDocPojo) commonData.getCommons().getJsonDocFromInputStream(incomingFileStream,getBasePrimerDocClass());
					incomingFileStream.close();

					documentToUpdate.absorbIncomingItemPojo(incomingDoc.getItem());
					additionalRollAddWebProcess(incomingDoc.getItem());

				} else {
					//incomingFileStream = inRemoteAccesser.getRemoteFileStream(commonData.getCommons().getRemotePathFileName(inRootPojo.rootString, 
					//		inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance, inRequestProcesserPojo.prevERLPojo.contentFileName,inRootPojo.fileSeparator));
					//documentToUpdate = (GenericGrouperDocPojo) commonData.getCommons().getJsonDocFromInputStream(incomingFileStream,getPrimerDocClass());
					//incomingFileStream.close();

///////////////////
///////////////////
					ItemPojo itemPojoToUpdate = documentToUpdate.getItemByChildArtifactName(
							inRequestProcesserPojo.requestPojo.relevance, 
							inRequestProcesserPojo.requestPojo.artifactName,
							inRequestProcesserPojo.requestPojo.contentType);

					UserPojo requestAuthorsDetail = commonData.getUsersHandler().getUserDetailsFromRootSysLoginID(inRequestProcesserPojo.requestPojo.requestor);

					if ((inRequestProcesserPojo.itemReassignedRequestor!= null && !inRequestProcesserPojo.itemReassignedRequestor.isEmpty()) 
						|| (inRequestProcesserPojo.itemReassignedAuthor != null && !inRequestProcesserPojo.itemReassignedAuthor.isEmpty())
						|| (inRequestProcesserPojo.itemNewERLStatus != null && !inRequestProcesserPojo.itemNewERLStatus.isEmpty())) {

						if (requestAuthorsDetail.hasAdminPrivilege() 
							|| requestAuthorsDetail.hasTeamLeaderPrivilege() 
							|| ((requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(itemPojoToUpdate.author)
								|| requestAuthorsDetail.rootSysLoginID.equalsIgnoreCase(itemPojoToUpdate.requestor)
								|| commonData.getUsersHandler().doesUserHaveRightsOverMember(requestAuthorsDetail.rootSysLoginID, itemPojoToUpdate.author)
							))) {
							if (inRequestProcesserPojo.itemReassignedRequestor != null && !inRequestProcesserPojo.itemReassignedRequestor.isEmpty()){
								itemPojoToUpdate.requestor = inRequestProcesserPojo.itemReassignedRequestor;
							}
							if (inRequestProcesserPojo.itemReassignedAuthor != null && !inRequestProcesserPojo.itemReassignedAuthor.isEmpty()){
								itemPojoToUpdate.author = inRequestProcesserPojo.itemReassignedAuthor;
							}
							if (inRequestProcesserPojo.itemNewERLStatus != null && !inRequestProcesserPojo.itemNewERLStatus.isEmpty()){
								itemPojoToUpdate.status = inRequestProcesserPojo.itemNewERLStatus;
							}
						}
					}
///////////////////					
///////////////////					
					//ItemPojo itemPojoToUpdate = documentToUpdate.getItemByChildArtifactName(
					//		inRequestProcesserPojo.newERLPojo.artifactKeyPojo.relevance, 
					//		inRequestProcesserPojo.newERLPojo.artifactKeyPojo.artifactName);
					//
					//if (!inRequestProcesserPojo.newERLPojo.author.isEmpty()) {
					//	itemPojoToUpdate.author = inRequestProcesserPojo.newERLPojo.author;
					//}
					//if (!inRequestProcesserPojo.newERLPojo.erlStatus.isEmpty()) {
					//	itemPojoToUpdate.status = inRequestProcesserPojo.newERLPojo.erlStatus;
					//}
					
					documentToUpdate.absorbIncomingItemPojo(itemPojoToUpdate);
				}
			} else {
				System.out.println("At processContentAtWeb not a ContentHandlerSpecs.ROLLUP_ADDUP_TYPE_ROLLUP or addup" );

				if (inRequestProcesserPojo.requestPojo.artifactOrReview.equalsIgnoreCase(RequestPojo.ARTIFACT)) {				
					incomingFileStream = inRemoteAccesser.getRemoteFileStream(inRequestProcesserPojo.incomingContentFullPath);
					GenericGrouperDocPojo incomingDoc = (GenericGrouperDocPojo) commonData.getCommons().getJsonDocFromInputStream(incomingFileStream,getPrimerDocClass());
					documentToUpdate = incomingDoc;								// hence replacing the whole doc with new.
					incomingFileStream.close();
				} else {
					prevFileStream = inRemoteAccesser.getRemoteFileStream(commonData.getCommons().getRemotePathFileName(inRootPojo.rootString, 
							inRequestProcesserPojo.prevERLPojo.artifactKeyPojo.relevance, inRequestProcesserPojo.prevERLPojo.contentFileName,inRootPojo.fileSeparator));
					documentToUpdate = (GenericGrouperDocPojo) commonData.getCommons().getJsonDocFromInputStream(prevFileStream,getPrimerDocClass());
					prevFileStream.close();
				}
			}
	
			inRequestProcesserPojo.updatedContentInputStream = commonData.getCommons().getJsonDocInStream(documentToUpdate);
	
			System.out.println("At processContentAtWeb closing the instream incomingFileStream " + incomingFileStream);
			
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(mainShell, commonData.getCommons(), "Error2 at GenericGrouper processContentAtWeb " + inRequestProcesserPojo.newERLPojo.artifactKeyPojo.artifactName, e);
		}
		
		System.out.println("At processContentAtWeb inRequestProcesserPojo.updatedContentInputStream is " + inRequestProcesserPojo.updatedContentInputStream);

		System.out.println("end processContentAtWeb");
	}
	
	public void setInitialContent(String inRelevance, String inContentType, GenericGrouperDocPojo inDocumentToUpdate) {
		// dummy method to be overridden when required
	}

	public void additionalWebProcess(GenericGrouperDocPojo inDocumentToUpdate) {
		// dummy method to be overridden when required
	}
	
	public void additionalRollAddWebProcess(ItemPojo inItemPojo) {
		// dummy method to be overridden when required		
	}
	
	public abstract void extendedCommonInit();
	public abstract ItemPojo getItemPojo(int itemNumber);
	protected abstract void setAddlColumnHeaders();
	protected abstract Class getBasePrimerDocClass();	//this is used only for rollup types such as innovations
	
	public void setDisplayItemsAddlLeftFieldsInMultiDisplay(TableEditor editor, Table inTable, TableItem intableItem, int inLastColLocation, ItemPojo inItemPojo) {}
	public abstract void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor editor, Table inTable, TableItem intableItem, int inLastColLocation, ItemPojo inItemPojo);
	public void setDisplayItemsCenterAddlFieldsInMultiDisplay(TableEditor editor, Table inTable, TableItem intableItem, int inLastColLocation, ItemPojo inItemPojo) {}

	public abstract String validateBeforeUIEdit();
	
	public abstract Group setAddlFieldsForItemDisplay(Group inItemContentGroup, Group inPrevGroup,FormData inFormData, ItemPojo inItemPojo);

	public abstract void getPrimerDocAddlFields();

	public abstract void setPrimerDocAddlFields();

	public abstract GenericGrouperDocPojo getNewPrimerDoc();

	public void showRibbonEditControls() {
		System.out.println("GenericGrouper activateSaveButton");

		showAdditionalRibbonEditControls(buttonRibbon);
		
		Button btnSaveDraft = new Button(buttonRibbon, SWT.NONE);

		btnSaveDraft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Saving Draft11");
				setPrimerDocAddlFields();
				System.out.println("Fields moved for save");
				
				saveItemsJSON();
				System.out.println("Draft Saved");
			}
		});
		btnSaveDraft.setText("Save Draft");
		btnSaveDraft.pack();
	}

	public void setExportButton() {
		Button btnExportToSpreadSh = new Button(buttonRibbon, SWT.NONE);
		btnExportToSpreadSh.setToolTipText("Export to Excel");

		btnExportToSpreadSh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Export to Excel selected");

				String filename = null;
				
				FileDialog dialog = new FileDialog(mainShell, SWT.SAVE);
				dialog.setFilterExtensions(new String[]{ "*.xlsx" });
				filename = dialog.open();
				
				if (filename == null) return;

		    	if (commons.doesFileExist(filename)){
					MessageBox fileOverwriteMsgBox = new MessageBox(mainShell, SWT.YES | SWT.NO);
					fileOverwriteMsgBox.setMessage("file exists. overwrite? " + filename);
					int fileOverwriteMsgBoxRC = fileOverwriteMsgBox.open();
					if (fileOverwriteMsgBoxRC != SWT.YES) {
						return;
					}
		    	}
		    	
				System.out.println("Export to Excel file name " + filename);				
				XSSFWorkbook wb = ExportSWTTableToExcel.createWorkbookFromTable(table);

			    try {
			    	
			        FileOutputStream fos = new FileOutputStream(filename);
			        wb.write(fos);
			        fos.close();
					MessageBox testMessageBox = new MessageBox(mainShell, SWT.OK);
					testMessageBox.setMessage("Workbook saved to the file:\n\n" + filename);
					int erlViewIssueMessageBoxRC = testMessageBox.open();
			    } catch (IOException ioe) {
			        ioe.printStackTrace();
			        String msg = ioe.getMessage();
					MessageBox testMessageBox = new MessageBox(mainShell, SWT.OK);
					testMessageBox.setMessage("Save Workbook FAILED!! \n" +
				            "Writing NOT ALLOWED to the file: \n" + filename);
					int erlViewIssueMessageBoxRC = testMessageBox.open();			        
			    }
			}
		});
		btnExportToSpreadSh.setText("ExportToExcel");
		btnExportToSpreadSh.pack();	
	}
	
	public abstract void additionalRibbonButtons();
	
	public void setSelectionFilters() {

		filtersGroup = new Composite(buttonRibbon, SWT.NONE);
		filtersGroup.setLayout(new GridLayout(3, false));

	    filtersRibbonData = new GridData(SWT.FILL, SWT.FILL, true, true);
	    filtersGroup.setLayoutData(filtersRibbonData);

		//statusSelList = new List(filtersGroup, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

	    statusSelList = createNewFilterListWidget();
		initiateAdditionalFilters();
		
		hideShowFilter = true;

		//Capture the unique filter values
		for (Object itemPojoObj : itemList ) {
			ItemPojo itemPojo = (ItemPojo) itemPojoObj;
			loadFilterList(statusSelList,itemPojo.status);
			loadAdditionalFiltersList(itemPojo);
		}
		
		setFilterAction(statusSelList);
		setAdditionalFiltersSelectionAction();
		
		setFilterButtons(buttonRibbon);

		filtersGroup.pack();
	}

	// Four methods to be overridden by contentHandlers that want to implement additionalFilters - starts
	protected void initiateAdditionalFilters(){
	// dummy method to be overridden as required

		//sample code
		//parameter1SelList = initiateFilterList(paramter1SelList);		
		//parameter2SelList = initiateFilterList(paramter2SelList);		
	}

	public void setAdditionalFiltersSelectionAction(){		
		// dummy method so only the handlers that need to add more filters need to override
		//sample code
		//setFilterAction(parameter1SelList);
		//setFilterAction(parameter2SelList);
	}
	
	public void loadAdditionalFiltersList(ItemPojo inItemPojo) {
		// dummy method so only the handlers that need to add more filters need to override
		//sample code
		//loadFilterList(parameter1List,((GenlRequestPojo) inItemPojo).parameter1);
		//loadFilterList(parameter2List,((GenlRequestPojo) inItemPojo).parameter2);
	}

	public boolean checkAdditionalFilters(ItemPojo inItemPojo){
		// dummy method to be overridden

		boolean filterResult = true;
		// sample code for overriding
	 	// this pattern can be repeated if more filters added
		//if (filterResult
		//	&& !checkFilter(parameter1SelList,((GenlRequestPojo) inItemPojo).parameter1)) {
		//	filterResult = false;
		//}
		//if (filterResult
		//	&& !checkFilter(parameter2SelList,((GenlRequestPojo) inItemPojo).parameter1)) {
		//	filterResult = false;
		//}
		return filterResult;
	}	
	// Four methods to be overridden by contentHandlers that want to implement additionalFilters - ends	
	
	protected List createNewFilterListWidget(){
		List newFilterListWidget = new List(filtersGroup, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		newFilterListWidget.pack();
		return newFilterListWidget;
	}
	
	protected void loadFilterList(List inFilterList, String inFilterFieldValue) {
		if (inFilterFieldValue == null) return;
		if (inFilterList.indexOf(inFilterFieldValue) == -1) {
			inFilterList.add(inFilterFieldValue);
		}
	}

	public void showAdditionalRibbonEditControls(Composite ribbon){
	}

	public void saveItemsJSON() {
		System.out.println("GenericGrouper saveItemsXML");
		try {
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
			System.out.println("GenericGrouper saveItemsXML1212");
		} catch (IOException e) {

			System.out.println("Exception in GenericGrouper saveItemsXML1212 e is " + e);
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(mainShell, commonData.getCommons(), "Error at GenericGrouper saveItemsJSON", e);
		}
		MessageBox testMessageBox = new MessageBox(mainShell, SWT.OK);
		testMessageBox.setMessage("File saved " + contentPathFileName);
		int erlViewIssueMessageBoxRC = testMessageBox.open();

	}
	
	public int getTriggerInterval(){
		/* default dummy process as its not a required function for all contents*/
		commonData.getCommons().logger.info(" default getTriggerInterval is called which is unexexpected. Invoked artifactname is " + invokedArtifactPojo.artifactKeyPojo.artifactName);

		return -1;
	}
	public void triggeredProcess(){
		/* default dummy process as its not a required function for all contents*/
		commonData.getCommons().logger.info(" default getTriggerInterval is called which is unexexpected. Invoked artifactname is " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	}

	public abstract GenericGrouperDocPojo getPrimerDoc();	
	public void triggeredProcess(String inTriggerAt) {
		//Dummy process as its not mandatory for all situation
	}
	public void triggeredProcess(Date inTriggerAt) {
		//Dummy process as its not mandatory for all situation		
	}
	
	public void writePrimer(){
		try {
			commonData.getCommons().putJsonDocToFile(contentPathFileName,getPrimerDoc());
		} catch (IOException e) {
			e.printStackTrace();			
			ErrorHandler.showErrorAndQuit(mainShell, commonData.getCommons(), "Error at GenericGrouper writePrimer", e);
		}
		System.out.println("writeJSON Stored the xml file : " + contentPathFileName);
	}
	
	//public void createNewStartupPrimer(String inNewPrimerFilePath, String inContentType) {
	public void createNewStartupPrimer(String inNewPrimerFilePath, ArtifactPojo inArtifactpojo) {
		System.out.println("At createNewStartupPrimer json file name passed : " + inNewPrimerFilePath);
		//GenericGrouperDocPojo newPrimerDoc = getNewPrimerDoc();
		primerDoc = getNewPrimerDoc();
		try {
			commons.putJsonDocToFile(inNewPrimerFilePath,primerDoc);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(mainShell, commonData.getCommons(), "Error at GenericGrouper createNewStartupPrimer " 
							+ inNewPrimerFilePath + " " + inArtifactpojo.artifactKeyPojo.artifactName, e);
		}
		System.out.println("createNewStartupPrimer Stored the json file at : " + inNewPrimerFilePath);
	}
	
	public void redrawDetails() {
		Object[] itemMapKeys = displayedItemMap.keySet().toArray();
		for (int itemMapNum = 0;itemMapNum < itemMapKeys.length;itemMapNum++) {
			int reviewItemNo = (int) itemMapKeys[itemMapNum];
			ArrayList<Control> itemsObjsWithinEvent = displayedItemMap.get(reviewItemNo);
			Control control1 = (Control) itemsObjsWithinEvent.get(0);
			control1.dispose();
			Control control2 = (Control) itemsObjsWithinEvent.get(1);
			control2.dispose();
			displayedItemMap.remove(reviewItemNo);
		}
		if (childCompositeOfMultiView != null) {
			childCompositeOfMultiView.dispose();
		}
		if (artifactRwScrollPane != null) {
			artifactRwScrollPane.dispose();	//artifact review pane dispose;
		}
		itemsMainCompositeInMultiConentView.dispose();
		//scrolledComposite_main.dispose();	// item review pane dispose
		displayMultiContent();	// redraw screen below ribbon
	}

	public boolean wasDeckerGouperEditedForXtdProcess() {
	// This method shall be overridden by deckerGroupers appropriately
	// to know if anything changed that would call for trigger extended process
		return false;
	}

	public String prevalidate(CommonData inCommonData,ArtifactKeyPojo inArtifactKeyPojo){
		//dummy method; to be overridden where used
		String validateString = "";
		return validateString;
	}
	
	public void setFilterAction(List inList) {
	
		inList.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				List dropDownList = (List) e.getSource();
				int[] comboSelection = dropDownList.getSelectionIndices();
				if (comboSelection != null) {
					//System.out.println("selected items are " + dropDownList.getSelection()inDropDownItems[dropDownList.getSelectionIndices()[0]]);
					System.out.println("selected items are " + dropDownList.getSelection());
				}
				applyFilterButton.setVisible(true);
			}
		});
		inList.pack();
	}

	public ArrayList<ItemPojo> getFilteredItemList(){
		ArrayList<ItemPojo> filteredItemlist = new ArrayList<ItemPojo>();

		for (Object itemPojoObj : itemList ) {
			ItemPojo itemPojo = (ItemPojo) itemPojoObj;
			if (checkFilter(statusSelList,itemPojo.status)){
				if (checkAdditionalFilters(itemPojo)){
					filteredItemlist.add(itemPojo);					
				}
			}
		}
		
		return filteredItemlist;
	}
	
	public boolean checkFilter(List statusSelList,String inFilterValue){
		if (statusSelList == null || statusSelList.getSelectionCount() == 0 || inFilterValue == null
			|| statusSelList.isSelected(statusSelList.indexOf(inFilterValue))) {
			return true;
		} else return false;
	}

	public void setFilterButtons(Composite inRibbonComposite){
		
		//set up the ApplyFilter button
		applyFilterButton = new Button(filtersGroup, SWT.PUSH);	// the container is inner ribbon
		applyFilterButton.setText("ApplyFilter");
		applyFilterButton.setToolTipText("Hide this review pane to make room in screen");

		applyFilterButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) {
				populateScreenItems();
			}
		});
		applyFilterButton.pack();
		filtersGroup.pack();
		
		//set up the showhide button
		showOrHideFiltersButton = new Button(inRibbonComposite, SWT.PUSH);
		showOrHideFiltersButton.setText(hidefiltersLIT);
		showOrHideFiltersButton.setToolTipText("Hide this review pane to make room in screen");

		showOrHideFiltersButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) {
				if (hideShowFilter) {

					System.out.println("hideShowFilterVisible");
											
					//MessageBox WarningMessageBox = new MessageBox(mainShell,
					//			SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					//	WarningMessageBox.setMessage("The keyed in text will be lost if hidden. Proceed?");
					//if (WarningMessageBox.open() == SWT.CANCEL) {
					//	System.out.println("Cancel chosen. returning");
					//	return;
					//} else {
					//	System.out.println("Hide confirmed. Proceeding");
					//}
					((Button) event.getSource()).setText("ShowFilters");
					System.out.println("Hiding Filter");
					hideShowFilter = false;
					filtersGroup.setVisible(false);
					filtersRibbonData.exclude = true;
					filtersGroup.pack();
				} else {
					((Button) event.getSource()).setText(hidefiltersLIT);
					hideShowFilter = true;
					filtersGroup.setVisible(true);
					filtersRibbonData.exclude = false;
					filtersGroup.pack();
				}
				buttonRibbon.pack();
				filtersGroup.pack();
				mainShell.pack();
				mainShell.layout();				
			}
		});
		showOrHideFiltersButton.pack();
		inRibbonComposite.pack();
	}
}