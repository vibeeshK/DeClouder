package espot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ArtifactWrapperUI {
	/*
	 * This is an entry screen for maintaining artifacts before invoking using
`	 * their own viewers using specific content handlers.
 	 * For the authors of the artifacts, it provides ability
	 * to view as well as edit, upload, backup on cloud, make clone etc.
	 * For non-authors, it provides ability to view, make clone.
	 * It has two constructors for a) calling using ERLPojo (from CatelogDisplay)
	 * and b) SelfAuthoredArtifactspojo (from CreateArtifact)
	 */

	String calledFor = "";
	final public static String CALLED_For_NewDraftSetup = "CALLED_For_NewDraftSetup";
	final public static String CALLED_ForCloning = "CALLED_For_Cloning";
	final public static String CALLED_After_DraftCreation = "CALLED_For_Returning";
	final public static String CALLED_For_DisplayItemFocused = "CALLED_For_DisplayItemFocused";

	CommonUIData commonUIData = null;
	Commons commons = null;

	CCombo relevanceList;	
	Text artifactNameText;
	
	private Shell mainShell = null;
	ScrolledComposite scrolledComposite_1 = null;
	ArtifactKeyPojo processingArtifactKeyPojo = null;
	ArtifactPojo invokedArtifactPojo = null;
	ArtifactKeyPojo cloneToArtifactKeyPojo = null;
	ItemPojo viewFocusItemPojo = null;
	SelfAuthoredArtifactpojo newestDBSelfAuthoredArtifactspojo = null;

	DownloadedReviewsHandler downloadedReviewsHandler = null;
	
	CCombo artifactVersionsList = null;
	ContentHandlerSpecs contentHandlerSpecs = null;
	CatelogPersistenceManager catelogPersistenceManager = null;	
	boolean authorCanEdit = true;
	boolean draftAvailable = false;
	boolean waitForContentDownload = false;	
	boolean awaitingUpload = false;
	int maxLocalVerionNumber = -1;
	HashMap<String, RootPojo> rootsMap = null;

	ERLDownload cloningERLDownload = null;

	ArrayList<ArtifactVersionPojo> artifactVersionPojoList = null;

	public void doCommonInit(CommonUIData inCommonUIData,ArtifactPojo inArtifactPojo, 
			ArtifactKeyPojo inCloneToArtifactKeyPojo, ItemPojo inViewFocusItemPojo) {
		doCommonInit(inCommonUIData,inArtifactPojo, inCloneToArtifactKeyPojo);
		viewFocusItemPojo = inViewFocusItemPojo;
	}

	public void doCommonInit(CommonUIData inCommonUIData,ArtifactPojo inArtifactPojo, 
			ArtifactKeyPojo inCloneToArtifactKeyPojo) {
		commonUIData = inCommonUIData ;
		commons = commonUIData.getCommons();
		catelogPersistenceManager = commonUIData.getCatelogPersistenceManager();
		invokedArtifactPojo = inArtifactPojo;
		cloneToArtifactKeyPojo = inCloneToArtifactKeyPojo;
		if (invokedArtifactPojo!=null) {
			contentHandlerSpecs = commonUIData.getContentHandlerSpecsMap().get(invokedArtifactPojo.artifactKeyPojo.contentType);
		} else if (inCloneToArtifactKeyPojo!=null) {
			contentHandlerSpecs = commonUIData.getContentHandlerSpecsMap().get(inCloneToArtifactKeyPojo.contentType);
		}

		if (invokedArtifactPojo!=null) {
			maxLocalVerionNumber = commonUIData.getCatelogPersistenceManager().
										getMaxDBVersionNumberOfSelfAuthoredArtifact(
												invokedArtifactPojo.artifactKeyPojo);
			startupWithArtifactKeyPojo();
			downloadedReviewsHandler = new DownloadedReviewsHandler(inCommonUIData, invokedArtifactPojo.artifactKeyPojo);
		}
	}
	

	/* Constructor for call using ERLPojo (from CatelogDisplay) */
	public ArtifactWrapperUI(CommonUIData inCommonUIData,ERLDownload inERLDownload) {
		System.out.println("At xxa ArtifactWrapperUI invoke CommonUIData inCommonUIData,ERLDownload inERLDownload");
		doCommonInit(inCommonUIData,inERLDownload,null);
		System.out.println("editingArtifactWrapperArtifactKeyPojo1: " + processingArtifactKeyPojo);		
	}

	/*
	 * Constructor for call using SelfAuthoredArtifactspojo (from
	 * CreateArtifact & Grouper/Lister)
	 */
	public ArtifactWrapperUI(CommonUIData inCommonUIData, SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo) {
		System.out.println("At xx0 ArtifactWrapperUI invoke CommonUIData inCommonUIData, SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo");
		doCommonInit(inCommonUIData,inSelfAuthoredArtifactspojo, null);
	}

	/* Constructor for new draft set up (from CreateArtifact) */
	public ArtifactWrapperUI(ArtifactKeyPojo inCreateArtifactKeyPojo, CommonUIData inCommonUIData) {
		System.out.println("At xx1 ArtifactWrapperUI invoke ArtifactKeyPojo inCloneToArtifactKeyPojo, CommonUIData inCommonUIData");
		calledFor = CALLED_For_NewDraftSetup;
		doCommonInit(inCommonUIData,null, inCreateArtifactKeyPojo);
	}

	/* Constructor for cloning (from Catalog Display) */
	public ArtifactWrapperUI(String inCalledFor, ERLDownload inERLDownload, CommonUIData inCommonUIData, ArtifactKeyPojo inCloneToArtifactKeyPojo) {
		System.out.println("At xx2 ArtifactWrapperUI invoke with String inCalledFor, ERLDownload inERLDownload, CommonUIData inCommonUIData, ArtifactKeyPojo inCloneToArtifactKeyPojo");
		calledFor = inCalledFor; //(CALLED_For_Cloning)
		doCommonInit(inCommonUIData,inERLDownload, inCloneToArtifactKeyPojo);

		cloningERLDownload = inERLDownload;
	}

	public ArtifactWrapperUI(CommonUIData inCommonUIData, ERLDownload inERLDownload, ItemPojo inERLItemPojo) {
		System.out.println("At xxa ArtifactWrapperUI invoke CommonUIData inCommonUIData,ERLDownload inERLDownload,ItemPojo inERLItemPojo");
		calledFor = CALLED_For_DisplayItemFocused;
		doCommonInit(inCommonUIData,inERLDownload,null,inERLItemPojo);

		System.out.println("editingArtifactWrapperArtifactKeyPojo1: " + processingArtifactKeyPojo);		
	}

	public void startupWithArtifactKeyPojo() {
		processingArtifactKeyPojo = invokedArtifactPojo.artifactKeyPojo;
		contentHandlerSpecs = commonUIData.getContentHandlerSpecsMap().get(invokedArtifactPojo.artifactKeyPojo.contentType);
		
		System.out.println("within startupWithERLpojo invoked ERL remarksFileName=" + invokedArtifactPojo.reviewFileName);
		System.out.println("editingArtifactWrapperArtifactKeyPojo221: " + processingArtifactKeyPojo);

		ArtifactVersionPojo tempArtifactVersionPojo = null;
		artifactVersionPojoList = new ArrayList<ArtifactVersionPojo>();
		// If any in-progress artifacts are available, place them on top of
		// the list
		System.out
				.println("before catelogPersistenceManager.readSelfAuthoredArtifactWithRootPojo");
		SelfAuthoredArtifactpojo inProgressSelfAuthoredArtifactpojo = commonUIData.getCatelogPersistenceManager().readSelfAuthoredArtifact(
				invokedArtifactPojo.artifactKeyPojo);
		System.out
				.println("after catelogPersistenceManager.readSelfAuthoredArtifactWithRootPojo");
		
		if (inProgressSelfAuthoredArtifactpojo != null) {
			System.out
					.println("draft read..."
							+ inProgressSelfAuthoredArtifactpojo.artifactKeyPojo.artifactName);
			loadUnprocessedSelfAuthArtifactsIntoVersionPojoList(inProgressSelfAuthoredArtifactpojo);

			System.out.println("newestSelfAuthoredArtifactspojo.Status is "
					+ newestDBSelfAuthoredArtifactspojo.draftingState);

			if (newestDBSelfAuthoredArtifactspojo.draftingState
					.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusDraft)
				|| newestDBSelfAuthoredArtifactspojo.draftingState
					.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusProcessed)
				|| newestDBSelfAuthoredArtifactspojo.draftingState
				.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusOutdated)){

				awaitingUpload = false;
				if (newestDBSelfAuthoredArtifactspojo.draftingState
						.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusDraft)) {
					draftAvailable = true;
				} else {
					draftAvailable = false;
				}
			} else {
				awaitingUpload = true;
				draftAvailable = false;
			}
		} else {
			awaitingUpload = false;
			draftAvailable = false;
		}
		System.out.println("artifactHasDraft awaitingUpload 1 is " + awaitingUpload);
		System.out.println("artifactHasDraft uploadable 1 is " + draftAvailable);

		System.out.println("within ArtifactWrapperUI startupWithArtifactKeyPojo Before readin erlDownload artifactName is " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("within ArtifactWrapperUI startupWithArtifactKeyPojo Before readin erlDownload relevance is " + invokedArtifactPojo.artifactKeyPojo.relevance);
		System.out.println("within ArtifactWrapperUI startupWithArtifactKeyPojo Before readin erlDownload contentType is " + invokedArtifactPojo.artifactKeyPojo.contentType);

		ERLDownload erlDownload = commonUIData.getCatelogPersistenceManager().readERLDownLoad(invokedArtifactPojo.artifactKeyPojo);
		System.out.println("erlDownload after reading is " + erlDownload);

		if (erlDownload != null) {
			System.out.println("within ArtifactWrapperUI startupWithArtifactKeyPojo erlDownload is not null " + erlDownload);
			System.out.println("erlDownload.subscriptionStatus is " + erlDownload.subscriptionStatus);
			System.out.println("ERLDownload.LOCAL_COPY_AVAILABLE is " + ERLDownload.LOCAL_COPY_AVAILABLE);
			System.out.println("erlDownload reviewfilename " + erlDownload.downLoadedReviewFile);

			System.out.println("erlpojo read..." + erlDownload.artifactKeyPojo.artifactName);
			if (!erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_AVAILABLE)) {
				waitForContentDownload = true;
			} else {
				waitForContentDownload = false;
			}

			//if (!commonUIData.getCommons().userName.equalsIgnoreCase(erlDownload.author)) {
			if (commonUIData.getCurrentUserPojo().hasAdminPrivilege() 
				|| commonUIData.getCurrentUserPojo().hasTeamLeaderPrivilege() 
				|| commonUIData.getUsersHandler().doesUserHaveRightsOverMember(
						commonUIData.getCommons().userName, erlDownload.author)) {
				authorCanEdit = true;
			} else {
				authorCanEdit = false;				
			}

			//String fullPathReviewFileName = "";
			//if (erlDownload.downLoadedReviewFile != null && !erlDownload.downLoadedReviewFile.equalsIgnoreCase("")) {
			//	fullPathReviewFileName = commonUIData.getCommons().getFullLocalPathFileNameOfDownloadedReview(commonUIData.getCurrentRootNick(), erlDownload.artifactKeyPojo.relevance,erlDownload.downLoadedReviewFile);
			//}
			tempArtifactVersionPojo = new ArtifactVersionPojo(
					erlDownload, true, 0, erlDownload.localCopyStatus, "");
			
			artifactVersionPojoList.add(tempArtifactVersionPojo);
			System.out.println("within startupWithArtifactKey remarksFileName=" + erlDownload.downLoadedReviewFile);

		} else {
			// you get in here not only when the ERLDownload unavailable, but also for
			// the rollupChild artifact drafting since the children are hidden in a parent ERLDownload
			
			System.out.println("within ArtifactWrapperUI startupWithArtifactKeyPojo erlDownload is null " + erlDownload);

			
			//if (inProgressSelfAuthoredArtifactpojo != null 
			//		&& newestDBSelfAuthoredArtifactspojo.author.equalsIgnoreCase(commons.userName)) {
			if (inProgressSelfAuthoredArtifactpojo != null 
				&& (commonUIData.getCurrentUserPojo().hasAdminPrivilege() 
					|| commonUIData.getCurrentUserPojo().hasTeamLeaderPrivilege() 
					|| commonUIData.getUsersHandler().doesUserHaveRightsOverMember(
						commonUIData.getCommons().userName, newestDBSelfAuthoredArtifactspojo.author))) {
				authorCanEdit = true;
			} else {
				authorCanEdit = false;
			}
			waitForContentDownload = false;
		}
		//re-sequence if there are outdated versions or procesed versions
		cleanseArtifactVersionPojoList();
	}
	
	public void loadUnprocessedSelfAuthArtifactsIntoVersionPojoList(
			SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo)
			{
		System.out
				.println("before read allVersionsSelfAuthoredArtifacts");
		ArrayList<SelfAuthoredArtifactpojo> allVersionsSelfAuthoredArtifacts = null;
		System.out.println("catelogPersistenceManager = " + commonUIData.getCatelogPersistenceManager());
		System.out.println("catelogPersistenceManager = " + inSelfAuthoredArtifactspojo);
		System.out.println("catelogPersistenceManager = " + inSelfAuthoredArtifactspojo.artifactKeyPojo);
		allVersionsSelfAuthoredArtifacts = commonUIData.getCatelogPersistenceManager().readAllVersionsSelfAuthoredArtifacts(inSelfAuthoredArtifactspojo.artifactKeyPojo);
		System.out
				.println("after read allVersionsSelfAuthoredArtifacts");

		for (int j = 0; j < allVersionsSelfAuthoredArtifacts.size(); j++) {
			System.out.println("New ArtifactVersionPojo 5");

			ArtifactVersionPojo tempArtifactVersionPojo = new ArtifactVersionPojo(
					allVersionsSelfAuthoredArtifacts.get(j),
					false,
					allVersionsSelfAuthoredArtifacts.get(j).unpulishedVerNum,
					"",allVersionsSelfAuthoredArtifacts.get(j).draftingState);
			artifactVersionPojoList.add(tempArtifactVersionPojo);
			if (j==0){
				newestDBSelfAuthoredArtifactspojo = allVersionsSelfAuthoredArtifacts.get(j);
				System.out.println("unpublished version number of newest = "
						+ newestDBSelfAuthoredArtifactspojo.unpulishedVerNum);
			}
		}

		System.out.println("List size = "
				+ allVersionsSelfAuthoredArtifacts.size());
	}
	
	public void refreshScreen() {
		Control[] oldControls = mainShell.getChildren();

		System.out.println("before dispose disp:" + commonUIData.getESPoTDisplay() + "   cat:"
				+ commonUIData.getCatelogPersistenceManager() + "   commn:" + commonUIData.getCommons()
				+ "   invokedArtifactPojo:" + invokedArtifactPojo);

		for (Control oldControl : oldControls) {
			oldControl.dispose();
		}
		System.out.println("before dispose disp:" + commonUIData.getESPoTDisplay() + "   cat:"
				+ commonUIData.getCatelogPersistenceManager() + "   commn:" + commonUIData.getCommons()
				+ "   erlpoj:" + invokedArtifactPojo);
		startupWithArtifactKeyPojo();
		displayArtifactWrapperUI();
	}

	public void displayArtifactWrapperUI() {
		if (mainShell == null) {
			System.out.println("mainShell being set. prev value: " + mainShell);

			mainShell = new Shell(commonUIData.getESPoTDisplay(), SWT.APPLICATION_MODAL | SWT.CLOSE
					| SWT.TITLE | SWT.BORDER | SWT.RESIZE);
			mainShell.setImage(new Image(commonUIData.getESPoTDisplay(), commonUIData.getCommons().applicationIcon));
		} else {
			System.out.println("mainShell already set. prev value: "
					+ mainShell);
		}
		mainShell.setText("ArtifactWrapperUI");
		//mainShell.setLayout(new FillLayout());
		mainShell.setLayout(new GridLayout());
		displayContent();
	}

	public void displayContent() {
		/*
		 * For the authors of the artifacts, it provides ability to view as well
		 * as edit, upload, backup on cloud, make clone For non-authors, it
		 * provides ability to view, make clone
		 */
		// displayContent() - base set up starts
		scrolledComposite_1 = new ScrolledComposite(mainShell, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);

		scrolledComposite_1.setLayout(new GridLayout(2, false));

		GridData gridDataSrol = new GridData(SWT.FILL, SWT.FILL, true, true);

		scrolledComposite_1.setLayoutData(gridDataSrol);

		Composite childCompositeOfDuplexView = new Composite(
				scrolledComposite_1, SWT.NONE);
		scrolledComposite_1.setContent(childCompositeOfDuplexView);
		childCompositeOfDuplexView.setLayout(new GridLayout(2, false));
		GridData gridDataDuplx = new GridData(SWT.FILL, SWT.FILL, true, true);

		childCompositeOfDuplexView.setLayoutData(gridDataDuplx);
		
		Composite childCompositeOfLeftView = new Composite(
				childCompositeOfDuplexView, SWT.NONE);
		childCompositeOfLeftView.setLayout(new FillLayout(SWT.VERTICAL));

		GridData gridDataLeft = new GridData(SWT.FILL, SWT.FILL, true, true);

		childCompositeOfLeftView.setLayoutData(gridDataLeft);		

		Image bg = new Image(commonUIData.getESPoTDisplay(),
				commonUIData.getCommons().backgroundImagePathFileName);

		bg = new Image(commonUIData.getESPoTDisplay(),
				commonUIData.getCommons().backgroundImagePathFileName);

		scrolledComposite_1.setBackgroundImage(bg);
		// displayContent() - base set up ends

		// displayContent() - ideaGroup Splitting Starts
		Group artifactDetailGroup = new Group(childCompositeOfLeftView,
				SWT.SHADOW_NONE);
		artifactDetailGroup.setText("ArtifactDetail");
		artifactDetailGroup.setLayout(new FillLayout(SWT.VERTICAL));
		// displayContent() - ideaGroup Splitting ends

		// displayContent() - ArtifactName display starts
		Group artifactNameGroup = new Group(artifactDetailGroup, SWT.LEFT);
		artifactNameGroup.setLayout(new FillLayout());
		artifactNameGroup.setText("ArtifactName");
		artifactNameText = new Text(artifactNameGroup, SWT.NONE);
		if (calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) || calledFor.equalsIgnoreCase(CALLED_ForCloning)) {
			if (cloneToArtifactKeyPojo!=null) {
				artifactNameText
				.setText(cloneToArtifactKeyPojo.artifactName);
				artifactNameText.setEnabled(false);
			} else {
				artifactNameText.setText("pl. enter artifact name");
			}
		} else {
			artifactNameText
					.setText(invokedArtifactPojo.artifactKeyPojo.artifactName);
			artifactNameText.setEnabled(false);
		}
		// displayContent() - ArtifactName display ends

		// displayContent() - ContentType display starts
		Group contentTypeGroup = new Group(artifactDetailGroup, SWT.LEFT);
		contentTypeGroup.setLayout(new FillLayout());
		contentTypeGroup.setText("ContentType");
		CCombo contentTypeList = new CCombo(contentTypeGroup,
				SWT.DROP_DOWN | SWT.READ_ONLY);

		if (calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup)) {
			if (cloneToArtifactKeyPojo!=null) {
				contentTypeList
				.setItems(new String[] { cloneToArtifactKeyPojo.contentType });
				contentTypeList.setEnabled(false);
			} else {
				//contentTypeList.setItems(commonUIData.getContentTypes());
				contentTypeList.setItems(catelogPersistenceManager.getUserInitiatedContentTypes());
			}
		} else {
			contentTypeList
				.setItems(new String[] { invokedArtifactPojo.artifactKeyPojo.contentType });
			contentTypeList.setEnabled(false);
		}
		contentTypeList.select(0);
		// displayContent() - ContentType display ends


		// displayContent() - Relevance display starts
		Group relevanceGroup = new Group(artifactDetailGroup, SWT.LEFT);
		relevanceGroup.setLayout(new FillLayout());
		relevanceGroup.setText("Relevance");
		relevanceList = new CCombo(relevanceGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) || calledFor.equalsIgnoreCase(CALLED_ForCloning)) {
			if (cloneToArtifactKeyPojo!=null) {
				relevanceList
				.setItems(new String[] { cloneToArtifactKeyPojo.relevance });
				relevanceList.setEnabled(false);
			} else {
				relevanceList.setItems(commonUIData.getRelavances());
			}
		} else {
			relevanceList
					.setItems(new String[] { invokedArtifactPojo.artifactKeyPojo.relevance });
			relevanceList.setEnabled(false);
		}
		relevanceList.select(0);
		// displayContent() - Relevance display ends


		// displayContent() - Users display starts

		Group authorsGroup = new Group(artifactDetailGroup, SWT.LEFT);
		authorsGroup.setLayout(new FillLayout());
		//authorsGroup.setText("Author");
		String currentAuthor = "";
		boolean invokedForEdit = false;
		if (calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) || calledFor.equalsIgnoreCase(CALLED_ForCloning)) {
			if (invokedArtifactPojo==null || invokedArtifactPojo.author == null || invokedArtifactPojo.author.equalsIgnoreCase("")) {
				currentAuthor = commonUIData.getCommons().userName;
				System.out.println("AA currentAuthor " + currentAuthor);
			} else {
				currentAuthor = invokedArtifactPojo.author;
				System.out.println("BB currentAuthor " + currentAuthor);
			}
		} else {
			currentAuthor = invokedArtifactPojo.author;
			System.out.println("CC currentAuthor " + currentAuthor);
		}
		System.out.println("A invokedArtifactPojo " + invokedArtifactPojo);
		
		if (invokedArtifactPojo == null || invokedArtifactPojo.erlStatus.equalsIgnoreCase(ArtifactPojo.ERLSTAT_DRAFTREQ)) {
			invokedForEdit = true;
		}
		System.out.println("000 Invoking UsersDisplay from artifactWrapper inInvokedForEdit is " + invokedForEdit);
		System.out.println("000 Invoking UsersDisplay from artifactWrapper ArtifactPojo.ERLSTAT_DRAFTREQ is " + ArtifactPojo.ERLSTAT_DRAFTREQ);

		UsersDisplay usersDisplay = new UsersDisplay(commonUIData.getUsersHandler(),authorsGroup,currentAuthor,false,UsersDisplay.AUTHOR_LIT);

		// displayContent() - Users display ends
		
		
		// displayContent() - artifactVersionsGrp display starts
		if (!calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) && !calledFor.equalsIgnoreCase(CALLED_ForCloning)) {
			Group artifactVersionsGrp = new Group(artifactDetailGroup, SWT.LEFT);
			artifactVersionsGrp.setText("ArtifactVersions");
			artifactVersionsGrp.setLayout(new FillLayout());

			artifactVersionsList = new CCombo(artifactVersionsGrp, SWT.DROP_DOWN | SWT.READ_ONLY);
			String[] artifactVersions = new String[artifactVersionPojoList
					.size()];
			for (int j = 0; j < artifactVersions.length; j++) {
				artifactVersions[j] = artifactVersionPojoList.get(j).displayName;
			}
			artifactVersionsList.setItems(artifactVersions);
			artifactVersionsList.select(0);
		}
		// displayContent() - artifactVersionsGrp display ends

		// view as well as edit, upload, backup on cloud, make clone
		// For non-authors, it provides ability to view, make clone
		//final Group actionsGrp = new Group(childCompositeOfLeftView, SWT.SHADOW_NONE);
		Group actionsGrp = new Group(artifactDetailGroup, SWT.SHADOW_NONE);		
		actionsGrp.setText("Actions");
		actionsGrp.setLayout(new FillLayout());

		// displayContent() - view button process starts
		if (!calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) && !calledFor.equalsIgnoreCase(CALLED_ForCloning)) {

			System.out.println("ArtifactWrapperUI displayContent() - view button process starts");

			Button btnViewButton = new Button(actionsGrp, SWT.CENTER);
			btnViewButton.setText("View");
			btnViewButton.setToolTipText("View the artifact details");
			
			btnViewButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					System.out.println("ArtifactWrapperUI displayContent() - widgetSelected");
					
					System.out.println("viewing the artifact list position:"
							+ artifactVersionsList.getSelectionIndex());

					int selectedVersionDropDownNum = artifactVersionsList
							.getSelectionIndex();
					
					ArtifactVersionPojo selectedArtifactVersionPojo = artifactVersionPojoList.get(selectedVersionDropDownNum);

					if (selectedArtifactVersionPojo.publishedVersion && !selectedArtifactVersionPojo.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_AVAILABLE)) {
						System.out.println("Skipping the published version due to " + selectedArtifactVersionPojo.localCopyStatus);
						System.out.println("cant proceed with " + selectedArtifactVersionPojo.displayName);
						MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
						messageBox1.setMessage(selectedArtifactVersionPojo.localCopyStatus);
						int rc1 = messageBox1.open();
						return;
					}

					ArtifactMover artifactMover = ArtifactMover.getInstance(commonUIData);
					String fullPathViewFileNameString = artifactMover.getPrimeFilePath(selectedArtifactVersionPojo.artifactPojo);
					System.out.println("Before View process fullPathViewFileNameString is " + fullPathViewFileNameString);
					
					if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
						ErrorHandler.displayError(mainShell, commonUIData.getCommons(), "Error12 at ArtifactWrapper artifactMover " + artifactMover.lastProcessStatus + " while dealing with : " + fullPathViewFileNameString);
						return;
					}
					//ZIP processor required 1 of 4 ends.

					if (!commonUIData.getContentHandlerSpecsMap().get(processingArtifactKeyPojo.contentType).hasSpecialHandler) {

						MessageBox messageBox = new MessageBox(mainShell,
								SWT.ICON_WARNING | SWT.OK);
						messageBox
								.setMessage("Ensure to complete and close the artifact opened in separate window before proceding further");
						int rc = messageBox.open();
						
						try {
							commonUIData.getCommons().openFileToView(fullPathViewFileNameString);
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
								| IOException e1) {

							//e1.printStackTrace();
							ErrorHandler.showErrorAndQuit(commons, "Error in ARtifactWrapperUI displayContent ", e1);
						}
					} else {
						ContentHandlerInterface contentHandlerObjectInterface = null;
						
						try {

							System.out.println("before initializing ContentHandler fullPathFileNameString = " + fullPathViewFileNameString);
							System.out.println("kkhere before initializing ContentHandler relevance = " + invokedArtifactPojo.artifactKeyPojo.relevance);
							
							System.out.println("contentHandlerSpecs " + contentHandlerSpecs);
							System.out.println("contentHandlerSpecs.className " + contentHandlerSpecs.handlerClass);
							System.out.println("commons " + commonUIData.getCommons());

							contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonUIData.getCommons(), commonUIData.getCatelogPersistenceManager(), processingArtifactKeyPojo.contentType);

							System.out.println("contentHandlerObjectInterface : " + contentHandlerObjectInterface);
							
							System.out.println("ArtifactWrapperUI displayContent() - widgetSelected");

							if (!selectedArtifactVersionPojo.publishedVersion) {
								System.out.println("ArtifactWrapperUI displayContent() - widgetSelected - not publishedVersion");
								SelfAuthoredArtifactpojo selfAuthoredArtifact = commonUIData.getCatelogPersistenceManager().readSelfAuthoredArtifact(selectedArtifactVersionPojo.artifactPojo.artifactKeyPojo);
								System.out.println("About to initialize for draft artifact. contentHandlerObjectInterface = " + contentHandlerObjectInterface);
								contentHandlerObjectInterface
								.initializeContentHandlerForDraftArtifact(
									commonUIData, selfAuthoredArtifact);
								System.out.println("Initializing for draft artifact complete");
								contentHandlerObjectInterface.viewContentsAtDesk();
							} else {
								System.out.println("ArtifactWrapperUI displayContent() - widgetSelected - publishedVersion");

								if (calledFor.equalsIgnoreCase(CALLED_For_DisplayItemFocused) && viewFocusItemPojo != null) {
									contentHandlerObjectInterface
									.initializeContentHandlerForERLDownloadAndItemFocus(
										commonUIData,
										(ERLDownload) selectedArtifactVersionPojo.artifactPojo,
										viewFocusItemPojo
										);
									contentHandlerObjectInterface.viewContentsAtDesk();									
								} else {
									contentHandlerObjectInterface
									.initializeContentHandlerForDownloadedArtifact(
										commonUIData,
										(ERLDownload) selectedArtifactVersionPojo.artifactPojo
										);
									contentHandlerObjectInterface.viewContentsAtDesk();
								}
							}
						} catch (IOException e2) {
							//e2.printStackTrace();
							ErrorHandler.showErrorAndQuit(commons, "Error in ARtifactWrapperUI displayContent ", e2);							
						}
					}
				}
			});
		}
		// displayContent() - view button process ends

		// displayContent() - Create button process starts
		if (calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) || calledFor.equalsIgnoreCase(CALLED_ForCloning)) {
		// Edit and upload allowed only on special conditions
			Button btnCreateButton = new Button(actionsGrp, SWT.CENTER);
			btnCreateButton.setText("Create");
			btnCreateButton.setToolTipText("Create this artifact");			
			btnCreateButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					if (StringUtils.isBlank(artifactNameText
							.getText())) {
						MessageBox editMessage2Box = new MessageBox(mainShell,
								SWT.ICON_ERROR | SWT.OK);
						editMessage2Box.setMessage("Artifact Name cannot be empty");
						int rc1 = editMessage2Box.open();
						return;
					}

					if (rootsMap == null) {	// not sure if this is required at all. TO BE CHECKED
						PublishedRootsHandler.getPublishedRoots(commonUIData.getCommons());
					}
					
					processingArtifactKeyPojo = new ArtifactKeyPojo(commonUIData.getCurrentRootNick(),
								relevanceList.getItem(relevanceList
								.getSelectionIndex()), artifactNameText
								.getText(), contentTypeList
								.getItem(contentTypeList
										.getSelectionIndex()));

					contentHandlerSpecs = commonUIData.getContentHandlerSpecsMap().get(processingArtifactKeyPojo.contentType);
					// Create button validation starts
					String childRelevanceForSplits = relevanceList.getItem(relevanceList
																			.getSelectionIndex());
					
					StringUtils.replace(childRelevanceForSplits,commonUIData.getCurrentRootPojo().fileSeparator,commons.localFileSeparator);
					String[] splitStrings = StringUtils.split(childRelevanceForSplits,commons.localFileSeparator);
					if (splitStrings.length < contentHandlerSpecs.rollupLevel ) {
						MessageBox editMessage1Box = new MessageBox(mainShell,
								SWT.ICON_ERROR | SWT.OK);
						editMessage1Box.setMessage("Relevance branching should NOT be less than " + contentHandlerSpecs.rollupLevel
								+ " for content Type " + contentHandlerSpecs.contentType);
						int rc1 = editMessage1Box.open();
						return;
					}

					SelfAuthoredArtifactpojo checkDraftArtifactpojo = commonUIData.getCatelogPersistenceManager().readSelfAuthoredArtifact(
							processingArtifactKeyPojo);
					ERLDownload checkERLDownload = commonUIData.getCatelogPersistenceManager().readERLDownLoad(processingArtifactKeyPojo);

					if (checkERLDownload != null || checkDraftArtifactpojo != null) {
						MessageBox editMessage2Box = new MessageBox(mainShell,
								SWT.ICON_ERROR | SWT.OK);
						editMessage2Box.setMessage("Duplicate Artifact");
						int rc1 = editMessage2Box.open();
						return;
					}

					if (contentHandlerSpecs.hasSpecialHandler) {
						ContentHandlerInterface prevalidatingContentHandlerInterface = ContentHandlerManager.getInstance(commonUIData.getCommons(), commonUIData.getCatelogPersistenceManager(), processingArtifactKeyPojo.contentType);
						String prevalidateString = prevalidatingContentHandlerInterface.prevalidate(commonUIData,processingArtifactKeyPojo);
						if (!prevalidateString.equalsIgnoreCase("")) {
							MessageBox editMessage3Box = new MessageBox(mainShell,
									SWT.ICON_ERROR | SWT.OK);
							editMessage3Box.setMessage(prevalidateString);
							int rc3 = editMessage3Box.open();
							return;
						}
					}

					// Create button validation ends
					
					System.out.println("contentHandlerSpecs="
							+ contentHandlerSpecs);

					maxLocalVerionNumber = maxLocalVerionNumber + 1;
					String versionedFileName = commonUIData.getCommons().getVersionedFileName(processingArtifactKeyPojo.artifactName,
							contentHandlerSpecs.extension, maxLocalVerionNumber);
					System.out.println("versionedFileName" + versionedFileName);
								
					newestDBSelfAuthoredArtifactspojo = new SelfAuthoredArtifactpojo(
						processingArtifactKeyPojo,
						"", 				//requestor
						usersDisplay.userText.getText(),
						contentHandlerSpecs.hasSpecialHandler,
						"",					//reviewFileName
						ArtifactPojo.ERLSTAT_DRAFT,					// ERLStatus
						null,				//ParentKey
						versionedFileName, 	// localFilePath
						SelfAuthoredArtifactpojo.ArtifactStatusDraft,
						"", 				// ReqRespFileName
						maxLocalVerionNumber,
						""					// inDelegatedTo
						);

					//ZIP processor required 2 of 4 starts. create a new folder for unzip

					ArtifactMover artifactMover = ArtifactMover.getInstance(commonUIData);
					if (calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup)) {

						System.out.println("At CALLED_For_NewDraftSetup contentHandlerSpecs.template = " + contentHandlerSpecs.template);
						artifactMover.moveFromTemplate(contentHandlerSpecs.template, newestDBSelfAuthoredArtifactspojo);
						System.out.println("At CALLED_For_NewDraftSetup artifactMover. lastProcessStatus = " + artifactMover.lastProcessStatus);
						System.out.println("At CALLED_For_NewDraftSetup artifactMover. ArtifactMover.NO_SOURCE_FILE = " + ArtifactMover.NO_SOURCE_FILE);
						System.out.println("At CALLED_For_NewDraftSetup artifactMover. lastProcessStatus = " + ArtifactMover.PROCESSED_OK);
						System.out.println("At CALLED_For_NewDraftSetup artifactMover. contentHandlerSpecs.rollupOrAddup = " + contentHandlerSpecs.rollupOrAddup);

						if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
							System.out.println("At CALLED_For_NewDraftSetup artifactMover. lastProcessStatus not nosource not ok = " + artifactMover.lastProcessStatus);
							ErrorHandler.displayError(mainShell, commonUIData.getCommons(), " error while creating draft from template artifactMover.lastProcessStatus is " + artifactMover.lastProcessStatus);
							return;
						}
						System.out.println("At CALLED_For_NewDraftSetup post primer check/pop up artifactMover.lastProcessStatus " + artifactMover.lastProcessStatus);
						
						// if no source file found for non-zip files, its ok as it will be taken care by the handler itself
						
					} else if (calledFor.equalsIgnoreCase(CALLED_ForCloning)) {
						artifactMover.moveArtifact(cloningERLDownload, newestDBSelfAuthoredArtifactspojo);
					}

					System.out.println("@@1");

					MessageBox messageBox = new MessageBox(mainShell,
							SWT.ICON_WARNING | SWT.OK);
					messageBox
							.setMessage("Ensure to complete and close the artifact opened in separate window before proceding further");
					int rc = messageBox.open();
					
					openArtifactForEdit(artifactMover.destPath);

					commonUIData.getCatelogPersistenceManager()
							.insertArtifactUI(newestDBSelfAuthoredArtifactspojo);
					

					invokedArtifactPojo = newestDBSelfAuthoredArtifactspojo;
					calledFor = CALLED_After_DraftCreation;
					refreshScreen();
				}
			});
		}
		// displayContent() - Create process ends

		// displayContent() - EDIT button starts
		// ***before showing edit or upload buttons check if there is any doc
		// pending for upload already
		// Edit and upload allowed only on special conditions
		System.out.println("authorConflict? while presenting is "
				+ authorCanEdit);
		System.out.println("waitForContentDownload? while presenting is "
				+ waitForContentDownload);
		System.out.println("awaitingUpload? while presenting is "
				+ awaitingUpload);
		if (authorCanEdit && !waitForContentDownload && !awaitingUpload
				&& !calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) 
				&& !calledFor.equalsIgnoreCase(CALLED_ForCloning)) {

			// Edit and upload allowed only on special conditions
			Button btnEditButton = new Button(actionsGrp, SWT.CENTER);
			btnEditButton.setText("Edit");
			btnEditButton.setToolTipText("Edit this artifact");
			btnEditButton.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {

					int selectedVersionDropDownNum = artifactVersionsList
							.getSelectionIndex();
					ArtifactVersionPojo selectedArtifactVersionPojo = artifactVersionPojoList
							.get(selectedVersionDropDownNum);
					if (selectedArtifactVersionPojo.publishedVersion && !selectedArtifactVersionPojo.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_AVAILABLE)) {
						//check for erl version - not downloaded or relevance not picked etc
						System.out.println("Skipping the published version due to " + selectedArtifactVersionPojo.localCopyStatus);
						System.out.println("cant proceed with " + selectedArtifactVersionPojo.displayName);
						MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
						messageBox1.setMessage(selectedArtifactVersionPojo.localCopyStatus);
						int rc1 = messageBox1.open();
						return;
					}

					System.out.println("selectedVersionDropDownNum: "
							+ selectedVersionDropDownNum);
					System.out
							.println("localVersionNumber: "
									+ artifactVersionPojoList
											.get(selectedVersionDropDownNum).localVersionNumber);
					System.out.println("maxLocalVerionNumber: "
							+ maxLocalVerionNumber);
										
					if (selectedArtifactVersionPojo.localVersionNumber == maxLocalVerionNumber
					&& selectedArtifactVersionPojo.draftingStatus.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusDraft)
					) {
						// if an earlier version was selected for edit, then ask user if 
						// the edit to be performed with a new version num

						MessageBox messageBox1 = new MessageBox(mainShell,
								SWT.ICON_WARNING | SWT.YES | SWT.NO);
						messageBox1.setMessage("Create a newer version?");
						int rc1 = messageBox1.open();
						
						if (rc1 == SWT.YES) {
							maxLocalVerionNumber = maxLocalVerionNumber + 1;
						}
					} else {
						maxLocalVerionNumber = maxLocalVerionNumber + 1;
					}
					System.out.println("new maxLocalVerionNumber: " + maxLocalVerionNumber);

					String versionedFileName = commonUIData.getCommons().getVersionedFileName(processingArtifactKeyPojo.artifactName,
							contentHandlerSpecs.extension, maxLocalVerionNumber);
					
					String newEditFileFullPath = commonUIData.getCommons().getFullLocalPathFileNameOfNewArtifact(commonUIData.getCurrentRootNick(), processingArtifactKeyPojo.relevance, versionedFileName);
					System.out.println("newFileFullPath :"
							+ newEditFileFullPath);

					System.out.println("within displayContent remarksFileName = " + invokedArtifactPojo.reviewFileName);

					newestDBSelfAuthoredArtifactspojo = new SelfAuthoredArtifactpojo(
						processingArtifactKeyPojo,
						selectedArtifactVersionPojo.artifactPojo.requestor, 				//requestor
						usersDisplay.userText.getText(),
						contentHandlerSpecs.hasSpecialHandler,
						selectedArtifactVersionPojo.artifactPojo.reviewFileName,	//reviewFileName
						invokedArtifactPojo.erlStatus,
						null,				//ParentKey
						versionedFileName, 	// localFilePath
						SelfAuthoredArtifactpojo.ArtifactStatusDraft,
						"", 				// ReqRespFileName
						maxLocalVerionNumber,
						""					// inDelegatedTo
						);
					
					if (!(selectedArtifactVersionPojo.artifactPojo instanceof SelfAuthoredArtifactpojo) ||
					(!newestDBSelfAuthoredArtifactspojo.LocalFileName.equalsIgnoreCase(((SelfAuthoredArtifactpojo) selectedArtifactVersionPojo.artifactPojo).LocalFileName))) {
						// If the source file is ERLDownLoad or another new local version insert new record
						//
						////ZIP processor required 3 of 4 starts. Copy into a new version

						ArtifactMover artifactMover = ArtifactMover.getInstance(commonUIData);
						artifactMover.moveArtifact(selectedArtifactVersionPojo.artifactPojo,newestDBSelfAuthoredArtifactspojo);
						if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
							ErrorHandler.displayError(mainShell, commonUIData.getCommons(), "Error14 at ArtifactWrapper artifactMover " + artifactMover.lastProcessStatus + " while moving into : " + newestDBSelfAuthoredArtifactspojo.LocalFileName);
							return;
						}
						
						commonUIData.getCatelogPersistenceManager()
								.insertArtifactUI(newestDBSelfAuthoredArtifactspojo);
					}
					System.out.println("@@2");
					
					MessageBox messageBox2 = new MessageBox(mainShell,
							SWT.ICON_WARNING | SWT.OK);
					messageBox2
							.setMessage("The document is opened in a separate window. Pl. process and close before proceding further");
					int rc2 = messageBox2.open();
					openArtifactForEdit(newEditFileFullPath);

					invokedArtifactPojo = newestDBSelfAuthoredArtifactspojo;
					
					calledFor = CALLED_After_DraftCreation;
					refreshScreen();
				}
			});
		}
		// displayContent() - EDIT button ends

		// displayContent() - Upload button starts
		System.out.println("authorCanEdit for upload is "
				+ authorCanEdit);
		System.out.println("waitForContentDownload? for upload is "
				+ waitForContentDownload);
		System.out.println("awaitingUpload? for upload is "
				+ awaitingUpload);
		if (draftAvailable && !waitForContentDownload)
			if (authorCanEdit && !waitForContentDownload && !awaitingUpload) {
			// Edit and upload allowed only on special conditions
			Button btnUploadButton = new Button(actionsGrp, SWT.CENTER);
			btnUploadButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					MessageBox messageBox = new MessageBox(mainShell,
							SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					messageBox
							.setMessage("Save the artifact if kept open before clicking OK");

					int rc = messageBox.open();
					switch (rc) {
					case SWT.OK:
						break;
					case SWT.CANCEL:
						System.out.println("SWT.CANCEL");
						return;
					}
					
					ArtifactMover artifactMover = ArtifactMover.getInstance(commonUIData);
					artifactMover.prepForUpload(newestDBSelfAuthoredArtifactspojo,contentHandlerSpecs.extension);
					if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
						ErrorHandler.displayError(mainShell, commonUIData.getCommons(), "Error17 at ArtifactWrapper artifactMover " + artifactMover.lastProcessStatus + " while prepForUpload : " + newestDBSelfAuthoredArtifactspojo.LocalFileName);
						return;
					}
					commonUIData.getCatelogPersistenceManager()
						.updateArtifactStatus(
								newestDBSelfAuthoredArtifactspojo,
								SelfAuthoredArtifactpojo.ArtifactStatusToBeUploaded);
					if (newestDBSelfAuthoredArtifactspojo.unpulishedVerNum > 0) {
						commonUIData.getCatelogPersistenceManager()
							.updateOlderArtifact(
									newestDBSelfAuthoredArtifactspojo.artifactKeyPojo,
									SelfAuthoredArtifactpojo.ArtifactStatusOutdated,
									newestDBSelfAuthoredArtifactspojo.unpulishedVerNum);
					}

					invokedArtifactPojo = newestDBSelfAuthoredArtifactspojo;
					refreshScreen();
				}
			});
			btnUploadButton.setText("Upload Latest");
			btnUploadButton.setToolTipText("Upload the latest version onto the Doc central");
		}
		// displayContent() - Upload button ends

		// displayContent() - final prep before display starts
		actionsGrp.pack();
		artifactDetailGroup.pack();

		//review group integration starts
		//review group integration starts		
		if (downloadedReviewsHandler != null && downloadedReviewsHandler.canBeReviewed()) {		
			Composite childCompositeOfRightView = new Composite(
					childCompositeOfDuplexView, SWT.NONE | SWT.WRAP);
	
			GridData gridDataRight = new GridData(SWT.FILL, SWT.FILL, true, true);
	
			childCompositeOfRightView.setLayoutData(gridDataRight);		
	
			if (!calledFor.equalsIgnoreCase(CALLED_For_NewDraftSetup) 
				&& !calledFor.equalsIgnoreCase(CALLED_ForCloning)
				&& !contentHandlerSpecs.rollupAddupType) {	// rollAddType reviews should go inside the item level
			
				childCompositeOfRightView.setLayout(new GridLayout());
				ReviewHandler reviewHander = new ReviewHandler(commonUIData, childCompositeOfRightView, invokedArtifactPojo, mainShell);
				reviewHander.displayContent();
	
				childCompositeOfRightView.pack();
				mainShell.layout(true);			
			}
		}
		//review group integration ends
		//review group integration ends

		childCompositeOfDuplexView.pack();
		scrolledComposite_1.pack();
		mainShell.pack();
		//mainShell.layout(true);
		mainShell.open();
		while (!mainShell.isDisposed()) {
			if (!commonUIData.getESPoTDisplay().readAndDispatch()) {
				if (commonUIData.getArtifactDisplayOkayToContinue()) {
					commonUIData.getESPoTDisplay().sleep();
				} else {
					break;
				}
			}
		}
		System.out.println("end of......displayContent");
		// displayContent() - final prep before display ends
	}

	private void openArtifactForEdit(String inFileName) {
		System.out.println("ArtifactWrapperUI openArtifactForEdit");
		if (!contentHandlerSpecs.hasSpecialHandler) {
			try {
				commonUIData.getCommons().openFileToView(inFileName);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
				//e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in ARtifactWrapperUI openArtifactForEdit " + inFileName, e);				
			}
		} else {
			System.out.println("kkkk at 1");

			ContentHandlerInterface contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonUIData.getCommons(), commonUIData.getCatelogPersistenceManager(), processingArtifactKeyPojo.contentType);
			System.out.println("contentHandlerObjectInterface : " + contentHandlerObjectInterface);

			contentHandlerObjectInterface.initializeContentHandlerForDraftArtifact(
					commonUIData, newestDBSelfAuthoredArtifactspojo);
			try {
				contentHandlerObjectInterface.editContentAtDesk();
			} catch (IOException e) {
				//e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in ARtifactWrapperUI openArtifactForEdit ", e);
			}
			System.out.println("kkkk at 8");
			// //////////replace by class loader ends
		}

	}
	void cleanseArtifactVersionPojoList() {
		ArtifactVersionPojo tempArtifactVersionPojo = null;
		int outdateArtifactLocation = -1;
		int processedArtifactLocation = -1;
		int erlArtifactLocation = -1;
		
		for (int artifactCount=0;artifactCount<artifactVersionPojoList.size();artifactCount++) {
			tempArtifactVersionPojo = artifactVersionPojoList.get(artifactCount);
			if (tempArtifactVersionPojo.publishedVersion) {
				erlArtifactLocation = artifactCount;
			} else if (tempArtifactVersionPojo.draftingStatus.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusOutdated)){
				if (outdateArtifactLocation == -1) {
					outdateArtifactLocation = artifactCount;
				}
			} else if (tempArtifactVersionPojo.draftingStatus.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusProcessed)){
				if (processedArtifactLocation == -1) {
					processedArtifactLocation = artifactCount;
				}
			}
		}
		if (erlArtifactLocation != -1 && (outdateArtifactLocation != -1 || processedArtifactLocation != -1)) {
		//remove outdated artifacts
			for (int artifactCount=artifactVersionPojoList.size()-1; artifactCount>-1;artifactCount--) {
				tempArtifactVersionPojo = artifactVersionPojoList.get(artifactCount);
				if (tempArtifactVersionPojo.draftingStatus.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusOutdated) 
						|| tempArtifactVersionPojo.draftingStatus.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusProcessed)) {
					artifactVersionPojoList.remove(artifactCount);
				}
			}
		}
	}
}

class ArtifactVersionPojo {
	String displayName = "";
	boolean publishedVersion = false;

	int localVersionNumber = 0;
	ArtifactPojo artifactPojo;
	String localCopyStatus = "";
	String draftingStatus = "";

	ArtifactVersionPojo(ArtifactPojo inArtifactPojo, boolean inPublishedVersion,
			int inLocalVersionNumber, String inLocalCopyStatus, String inDraftingStatus) {

		artifactPojo = inArtifactPojo;
		publishedVersion = inPublishedVersion;
		localVersionNumber = inLocalVersionNumber;

		localCopyStatus = inLocalCopyStatus;
		draftingStatus = inDraftingStatus;
		if (publishedVersion){
			if (localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_AVAILABLE)) {
				displayName = "<PublishedVersion> " + artifactPojo.artifactKeyPojo.artifactName;
			} else {
				displayName = "<PublishedVersion> " + artifactPojo.artifactKeyPojo.artifactName + " *** " + localCopyStatus;
			}
		} else {
			displayName = "<UnpublishedVerNo:" + localVersionNumber + "> "
					+ artifactPojo.artifactKeyPojo.artifactName + ". Drafting status: " + inDraftingStatus;
		}
	}
}