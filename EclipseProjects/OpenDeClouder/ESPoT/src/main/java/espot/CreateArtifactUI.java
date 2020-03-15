package espot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class CreateArtifactUI {
	/*
	 * UI to create a new artifact
	 */

	private Shell mainShell = null;
	private ERLDownload cloneERLDownload = null;
	boolean cloneRequested = false;
	SelfAuthoredArtifactpojo selectedSelfAuthoredArtifactspojo = null;
	CommonUIData commonUIData;
	Commons commons;

	public CreateArtifactUI(CommonUIData inCommonData) {
		commonUIData = inCommonData;
		commons = commonUIData.getCommons();
		System.out.println("In the constructor of CreateArtifactUI commonUIData : " + commonUIData);
	}

	public void refreshScreen() {

		commonUIData.getCatelogPersistenceManager().tobeConnectedCatalogDbFile = 
			CatalogDownloadDtlsHandler.getInstance(commonUIData.getCommons()).getCatalogDownLoadedFileName(commonUIData.getCurrentRootNick());
		commonUIData.getCatelogPersistenceManager().connectToToBECataloged();
			
		Control[] oldControls = mainShell.getChildren();
		for (Control oldControl : oldControls) {
			oldControl.dispose();
		}
		displayContent();
	}

	public void displayCreateArtifactUI() {

		mainShell = new Shell(commonUIData.getESPoTDisplay(), SWT.APPLICATION_MODAL|SWT.CLOSE|SWT.TITLE|SWT.BORDER|SWT.RESIZE);
		mainShell.setImage(new Image(commonUIData.getESPoTDisplay(), commonUIData.getCommons().applicationIcon));		
		//mainShell.setText("ESPoT: My Drafts");
		//mainShell.setLayout(new GridLayout(1, false));
		displayContent();
	}

	public void displayCloneCreateArtifactUI(ERLDownload inCloneERLpojo) {
		cloneERLDownload = inCloneERLpojo;
		cloneRequested = true;
		System.out.println("inside displayCloneCreateArtifactUI");
		displayCreateArtifactUI();
	}

	public void displayContent() {
		System.out.println("In displayContent of CreateArtifactUI commonUIData : " + commonUIData);

		System.out.println("testtest mainShell : " + mainShell);

		mainShell.setText("My Drafts");
		mainShell.setLayout(new GridLayout(1, false));
		
		final Composite buttonRibbon = new Composite(mainShell, SWT.NONE);
		buttonRibbon.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		//NewDraft Starts
		Button btnNewDraft = new Button(buttonRibbon, SWT.NONE);

		btnNewDraft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Refreshing after New Draft");

				ArtifactWrapperUI artifactWrapperUI = null;
				artifactWrapperUI = new ArtifactWrapperUI(null, commonUIData);
				artifactWrapperUI.displayArtifactWrapperUI();
				System.out.println("after triggering ArtifactWrapperUI from CreateArtifactUI");
				refreshScreen();
			}
		});
		btnNewDraft.setBounds(10, 10, 120, 25);
		btnNewDraft.setText("+ Build New Draft");
		btnNewDraft.setToolTipText("Navigate for creating a fresh draft");
		//NewDraft Ends
		
		Button btnRefresh = new Button(buttonRibbon, SWT.NONE);
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Refreshing the Drafts UI");
				refreshScreen();
			}
		});
		btnRefresh.setBounds(10, 10, 120, 25);
		btnRefresh.setText("Refresh");
		btnRefresh.setToolTipText("Refresh screen");
		buttonRibbon.pack();

		ArrayList<SelfAuthoredArtifactpojo> selfAuthoredArtifactLists = null;

		selfAuthoredArtifactLists = commonUIData.getCatelogPersistenceManager()
		.readInProgressArtfictsForOneRoot(commonUIData.getCommons().getCurrentRootNick());

		final Composite composite = new Composite(mainShell, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Table table = new Table(composite, SWT.BORDER);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		String[] columnHeaders = new String[] { "ArtifactName", "ContentType",
				"Root", "Relevance", "Status", "Delete?"};

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			if (columnHeaders[i].equalsIgnoreCase("Relevance")) {
				column.setWidth(200);
			} else {
				column.setWidth(100);
			}
		}
		final ArrayList<SelfAuthoredArtifactpojo> selfAuthoredArtifactListsFinal = selfAuthoredArtifactLists;

		//int screenMaxNum = selfAuthoredArtifactListsFinal.size() + 1;
		int screenMaxNum = selfAuthoredArtifactListsFinal.size();
		System.out.println("screenMaxNum = " + screenMaxNum);

		for (int i = 0; i < screenMaxNum; i++) {
			// create table rows - one for a new artifact and as many for
			// already in-progress
			new TableItem(table, SWT.NONE);
		}

		TableItem[] items = table.getItems();

		////Create row for new artifact - begin (*****this logic is going away. Its moved to ArtifactWrapper****)
		//for (int i = 0; i < 1; i++) {
		//	System.out.println("i = " + i );
		//
		//	final TableEditor artifactNameEditor = new TableEditor(table);
		//	Text text1 = new Text(table, SWT.NONE);
		//	if (cloneRequested) {
		//		text1.setText(cloneERLDownload.artifactKeyPojo.artifactName + "_Cloned");
		//	} else {
		//		text1.setText("pl. enter artifact name");
		//	}
		//	artifactNameEditor.grabHorizontal = true;
		//	artifactNameEditor.setEditor(text1, items[i], 0);
		//
		//	final TableEditor ContentTypeEditor = new TableEditor(table);
		//
		//	CCombo ContentTypeList = new CCombo(table, SWT.DROP_DOWN
		//			| SWT.READ_ONLY);
		//	if (cloneRequested) {
		//		ContentTypeList
		//				.setItems(new String[] { cloneERLDownload.artifactKeyPojo.contentType });
		//	} else {
		//		System.out.println("ContentTypeList : " + ContentTypeList);
		//		System.out.println("commonData : " + commonUIData);
		//		ContentTypeList.setItems(commonUIData.getContentTypes());
		//	}
		//	ContentTypeList.select(0);
		//	ContentTypeEditor.grabHorizontal = true;
		//	ContentTypeEditor.setEditor(ContentTypeList, items[i], 1);
		//
		//	final TableEditor rootNickEditor = new TableEditor(table);
		//	CCombo rootNickList = new CCombo(table, SWT.DROP_DOWN
		//			| SWT.READ_ONLY);
		//	String[] rootsNicks = commonUIData.getRootNicks();
		//
		//	rootNickList.setItems(rootsNicks);
		//	rootNickList.select(0);
		//	rootNickEditor.grabHorizontal = true;
		//	rootNickEditor.setEditor(rootNickList, items[i], 2);
		//
		//	final TableEditor relevanceEditor = new TableEditor(table);
		//	CCombo relevanceList = new CCombo(table, SWT.DROP_DOWN
		//			| SWT.READ_ONLY);
		//	if (cloneRequested) {
		//		relevanceList
		//				.setItems(new String[] { cloneERLDownload.artifactKeyPojo.relevance });
		//	} else {
		//		relevanceList.setItems(commonUIData.getCatelogPersistenceManager()
		//				.readPickedRelevance(commonUIData.getCommons().getCurrentRootNick()));
		//	}
		//	relevanceList.select(0);
		//	relevanceEditor.grabHorizontal = true;
		//	relevanceEditor.setEditor(relevanceList, items[i], 3);
		//
		//	final TableEditor statusEditor = new TableEditor(table);
		//	Text text4 = new Text(table, SWT.READ_ONLY);
		//	text4.setText("Draft");
		//	statusEditor.grabHorizontal = true;
		//	statusEditor.setEditor(text4, items[i], 4);
		//
		//	final TableEditor CreateArtifactButtonEditor = new TableEditor(table);
		//	Button CreateArtifactButton = new Button(table, SWT.PUSH);
		//	CreateArtifactButton
		//			.setText(SelfAuthoredArtifactpojo.ArtifactStatusScreenTextCreateArtifact);
		//	CreateArtifactButton.setData("CURRNTROWNUMBER", i);
		//	CreateArtifactButton.setData("item", items[i]);
		//	CreateArtifactButton.pack();
		//	CreateArtifactButtonEditor.minimumWidth = CreateArtifactButton.getSize().x;
		//	CreateArtifactButtonEditor.horizontalAlignment = SWT.LEFT;
		//	CreateArtifactButtonEditor.setEditor(CreateArtifactButton, items[i], 5);
		//
		//	System.out.println("set data = "
		//			+ CreateArtifactButton.getData("CURRNTROWNUMBER"));
		//
		//	System.out.println("column 2 in item= " + items[i].getText(1));
		//
		//	CreateArtifactButton.addSelectionListener(new SelectionAdapter() {
		//		@Override
		//		public void widgetSelected(SelectionEvent e) {
		//			Button eventButton = (Button) e.getSource();
		//
		//			eventButton
		//					.setText(SelfAuthoredArtifactpojo.ArtifactStatusDraft);
		//
		//			System.out.println("changed the text to Draft");
		//			System.out.println("eventButton = " + eventButton);
		//			Integer i = (Integer) eventButton
		//					.getData("CURRNTROWNUMBER");
		//			System.out.println("selected row = " + i);
		//
		//			System.out
		//					.println("NameEditor:"
		//							+ ((Text) artifactNameEditor.getEditor())
		//									.getText());
		//			System.out.println("ContentTypeEditor:"
		//					+ ((CCombo) ContentTypeEditor.getEditor())
		//							.getItem(((CCombo) ContentTypeEditor
		//									.getEditor()).getSelectionIndex()));
		//			System.out.println("rootNickEditorEditor:"
		//					+ ((CCombo) rootNickEditor.getEditor())
		//							.getItem(((CCombo) rootNickEditor
		//									.getEditor()).getSelectionIndex()));
		//			System.out.println("relevanceEditor"
		//					+ ((CCombo) relevanceEditor.getEditor())
		//							.getItem(((CCombo) relevanceEditor
		//									.getEditor()).getSelectionIndex()));
		//			System.out.println("statusEditor"
		//					+ ((Text) statusEditor.getEditor()).getText());
		//
		//			String artifactName = ((Text) artifactNameEditor.getEditor())
		//			.getText();
		//			String contentType = ((CCombo) ContentTypeEditor
		//					.getEditor())
		//					.getItem(((CCombo) ContentTypeEditor
		//							.getEditor())
		//							.getSelectionIndex());
		//			String rootNick = ((CCombo) rootNickEditor.getEditor())
		//			.getItem(((CCombo) rootNickEditor
		//					.getEditor())
		//					.getSelectionIndex());
		//			String relevance = ((CCombo) relevanceEditor.getEditor())
		//			.getItem(((CCombo) relevanceEditor
		//					.getEditor())
		//					.getSelectionIndex());
		//			String status = ((Text) statusEditor.getEditor()).getText();
		//			
		//			System.out.println("rootNick=" + rootNick);
		//			System.out.println("contentType=" + contentType);
		//			System.out.println("relevance=" + relevance);
		//			System.out.println("artifactName=" + artifactName);
		//			
		//			if (true) {
		//				return;
		//			}
		//
		//			ContentHandlerSpecs contentHandlerSpecs = commonUIData.getContentHandlerSpecsMap().get(contentType);
		//
		//			try {
		//				System.out.println("rootNick = " + rootNick);
		//
		//				
		//				String versionedFileName = commonUIData.getCommons().getVersionedFileName(selectedSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName, contentHandlerSpecs.extension,0);
		//
		//				selectedSelfAuthoredArtifactspojo = new SelfAuthoredArtifactpojo(
		//					new ArtifactKeyPojo(
		//					commonUIData.getCurrentRootNick(),
		//					relevance,
		//					artifactName,
		//					contentType),
		//					commonUIData.getCommons().userName, //requestor
		//					"",				//author
		//					commonUIData.getContentHandlerSpecsMap().get(contentType).hasSpecialHandler,
		//					"", 			//inReviewFileName
		//					"",				// ERLStatus
		//					null,			//inParentArtifactKeyPojo,
		//					versionedFileName,				//inLocalFileName,
		//					SelfAuthoredArtifactpojo.ArtifactStatusDraft,				//inStatus,
		//					"",				//inReqRespFileName,
		//					0,				//inUnpulishedVerNum,
		//					""				//inDelegatedTo
		//					);
		//
		//				ArtifactMover artifactMover = ArtifactMover.getInstance(commonUIData);
		//				if (cloneRequested) {
		//					artifactMover.moveArtifact(cloneERLDownload, selectedSelfAuthoredArtifactspojo);
		//				} else {
		//					String createSource = commonUIData.getCommons()
		//					.getTemplateFileName(contentHandlerSpecs.template);
		//					artifactMover.moveFromTemplate(createSource, selectedSelfAuthoredArtifactspojo);
		//				}
		//				if (artifactMover.lastProcessStatus != ArtifactMover.PROCESSED_OK) {
		//					ErrorHandler.displayError(mainShell, commonUIData.getCommons(), "Error at ArtifactWrapper " + artifactMover.lastProcessStatus + " while dealing with : " + selectedSelfAuthoredArtifactspojo.LocalFileName);
		//					return;
		//				}
		//
		//				if (contentHandlerSpecs.hasSpecialHandler) {
		//					String app_cmd = "_undefinedprotocolsowillthrowexception_";
		//					System.out
		//							.println("trying to launch the word test");
		//					app_cmd = "rundll32 url.dll, FileProtocolHandler "
		//							+ artifactMover.destPath;
		//					System.out.println("command to be executed: "
		//							+ app_cmd);
		//
		//					Runtime.getRuntime().exec(app_cmd);
		//					System.out
		//							.println("2222.1trying to launch the word test");
		//				} else {
		//
		//					System.out.println("kkkk at 1");
		//					ContentHandlerInterface contentHandlerObjectInterface = ContentHandlerManager.getInstance(commonUIData.getCommons(), commonUIData.getCatelogPersistenceManager(),selectedSelfAuthoredArtifactspojo.artifactKeyPojo.contentType);
		//
		//					contentHandlerObjectInterface.initializeContentHandlerForDraftArtifact(commonUIData, selectedSelfAuthoredArtifactspojo);
		//					contentHandlerObjectInterface.editContentAtDesk();
		//
		//					System.out.println("kkkk at 8");
		//
		//				}
		//
		//				commonUIData.getCatelogPersistenceManager().insertArtifactUI(selectedSelfAuthoredArtifactspojo);
		//				cloneRequested = false;
		//				refreshScreen();
		//			} catch (java.io.IOException ioe) {
		//				System.out
		//						.println("Windows could not provide a default\napplication for this file type or protocol.\nSuitable application not found!");
		//
		//				ErrorHandler.showErrorAndQuit(commons, "Error in CreateArtifactUI displayContent ", ioe);
		//			}
		//		}
		//	});
		//}

		SelfAuthoredArtifactpojo dbSelfAuthoredArtifactspojo = null;

		//for (int ScreenRowNum = 1, inProgressArtifactNum = 0; ScreenRowNum < screenMaxNum; ScreenRowNum++, inProgressArtifactNum++) {
		for (int ScreenRowNum = 0; ScreenRowNum < screenMaxNum; ScreenRowNum++) {
			dbSelfAuthoredArtifactspojo = selfAuthoredArtifactLists
					.get(ScreenRowNum);

			TableEditor editor = new TableEditor(table);

			Text text2 = new Text(table, SWT.READ_ONLY);
			text2.setText(dbSelfAuthoredArtifactspojo.artifactKeyPojo.contentType);
			editor.grabHorizontal = true;
			editor.setEditor(text2, items[ScreenRowNum], 1);

			editor = new TableEditor(table);
			Text rootNik = new Text(table, SWT.READ_ONLY);
			rootNik.setText(commonUIData.getCurrentRootNick());
			editor.grabHorizontal = true;
			editor.setEditor(rootNik, items[ScreenRowNum], 2);

			editor = new TableEditor(table);
			Text text3 = new Text(table, SWT.READ_ONLY);
			text3.setText(dbSelfAuthoredArtifactspojo.artifactKeyPojo.relevance);
			editor.grabHorizontal = true;
			editor.setEditor(text3, items[ScreenRowNum], 3);

			editor = new TableEditor(table);
			Text text4 = new Text(table, SWT.READ_ONLY);
			text4.setText(dbSelfAuthoredArtifactspojo.draftingState);
			editor.grabHorizontal = true;
			editor.setEditor(text4, items[ScreenRowNum], 4);

			System.out.println("dbSelfAuthoredArtifactspojo.Status = "
					+ dbSelfAuthoredArtifactspojo.draftingState);
			System.out
					.println("SelfAuthoredArtifactspojo.ArtifactStatusDraft = "
							+ SelfAuthoredArtifactpojo.ArtifactStatusDraft);

			System.out.println("Got inside the mystery path");

			//maintain starts
			TableEditor maintainButtoneditor = new TableEditor(table);
			Button maintainButton = new Button(table, SWT.PUSH);

			maintainButton.setText(dbSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName);
			maintainButton.setToolTipText("Navigate to view " + dbSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName);
			
			maintainButton.setData(Commons.SCREENROWNUMLIT, ScreenRowNum);
			//maintainButton.setData("inProgressArtifactNum",
			//		ScreenRowNum);

			System.out.println("set data = "
					+ maintainButton.getData(Commons.SCREENROWNUMLIT));

			maintainButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button eventButton = (Button) e.getSource();
					System.out.println("eventButton = " + eventButton);
					//Integer i = (Integer) eventButton
					//		.getData("inProgressArtifactNum");
					Integer i = (Integer) eventButton
							.getData(Commons.SCREENROWNUMLIT);
					
					System.out.println("selected inProgressArtifactNum = "
							+ i);
					SelfAuthoredArtifactpojo selectedSelfAuthoredArtifactspojoB = selfAuthoredArtifactListsFinal
							.get(i);

					ArtifactWrapperUI artifactWrapperUI = new 
					ArtifactWrapperUI(commonUIData,
										selectedSelfAuthoredArtifactspojoB);
					artifactWrapperUI.displayArtifactWrapperUI();
					refreshScreen();					
				}
			});

			maintainButton.pack();
			maintainButtoneditor.minimumWidth = maintainButton.getSize().x;
			maintainButtoneditor.horizontalAlignment = SWT.CENTER;
			//changed the modify button to artifact maintenance and moved to leftmost
			//maintainButtoneditor.setEditor(maintainButton, items[ScreenRowNum],
			//		5);
			maintainButtoneditor.setEditor(maintainButton, items[ScreenRowNum],
			0);
			
			//Delete button process starts
			{
				TableEditor deleteBtneditor = new TableEditor(table);
				Button delButn = new Button(table, SWT.PUSH);
	
				delButn
				.setText("Delete");
				delButn
				.setToolTipText("Delete " + dbSelfAuthoredArtifactspojo.artifactKeyPojo.artifactName);

				delButn.setData(Commons.SCREENROWNUMLIT, ScreenRowNum);
				//delButn.setData("inProgressArtifactNum",
				//		ScreenRowNum);
	
				System.out.println("set data = "
						+ delButn.getData(Commons.SCREENROWNUMLIT));
	
				delButn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button eventButton = (Button) e.getSource();
						System.out.println("eventButton = " + eventButton);
						//Integer i = (Integer) eventButton
						//		.getData("inProgressArtifactNum");
						Integer i = (Integer) eventButton
								.getData(Commons.SCREENROWNUMLIT);
						
						System.out.println("selected inProgressArtifactNum = "
								+ i);
						SelfAuthoredArtifactpojo selectedSelfAuthoredArtifactspojoB = selfAuthoredArtifactListsFinal
								.get(i);


						//get confirmation for delete
						MessageBox usefulArtifactCheckMsgBox = new MessageBox(mainShell,
								SWT.ICON_WARNING | SWT.YES | SWT.NO);
						usefulArtifactCheckMsgBox.setMessage("CAUTION: Will you require this artifact anytime in future?");

						int usefulArtifactCheckRc = usefulArtifactCheckMsgBox.open();
						if (usefulArtifactCheckRc != SWT.NO) {	// this question is purposely kept negative, since -
												// the default YES button might be pressed inadvertently
							MessageBox noDeleteMsgBox = new MessageBox(mainShell,
									SWT.ICON_INFORMATION);
							noDeleteMsgBox.setMessage("Its not a good idea to archive as you need it. Archiving cancelled.");
							noDeleteMsgBox.open();
							return;
						}

						MessageBox delConfirmMsgBox = new MessageBox(mainShell,
								SWT.ICON_WARNING | SWT.YES | SWT.NO);
						delConfirmMsgBox.setMessage("Are you sure to DELETE all drafts of " 
									+ selectedSelfAuthoredArtifactspojoB.artifactKeyPojo.artifactName + " ?");
						int delConfirmRc1 = delConfirmMsgBox.open();						
						if (delConfirmRc1 != SWT.YES) {
							return;
						}						
						//read all versions of drafts from db
						ArrayList<SelfAuthoredArtifactpojo> allVersionsSelfAuthoredArtifacts 
							= commonUIData.getCatelogPersistenceManager().readAllVersionsSelfAuthoredArtifacts(selectedSelfAuthoredArtifactspojoB.artifactKeyPojo);

						//scroll thru allVersions, form the file name and archive
						for (SelfAuthoredArtifactpojo oneVersionOfSelfAuthoredArtifact : allVersionsSelfAuthoredArtifacts) {
							
							if (!oneVersionOfSelfAuthoredArtifact.draftingState.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusDraft)
									&& !oneVersionOfSelfAuthoredArtifact.draftingState.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusOutdated)
									&& !oneVersionOfSelfAuthoredArtifact.draftingState.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusProcessed)
								//drafts in other states may still be e.g. midst of publications
									
									){
								System.out.println("skipped archiving "
										+ " root: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.rootNick
										+ ". relevance: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.relevance
										+ ". artifact: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.artifactName
										+ ". versionNum: " + oneVersionOfSelfAuthoredArtifact.unpulishedVerNum
										+ ". since the drafting state is: " + oneVersionOfSelfAuthoredArtifact.draftingState
								);
								continue; // skip this version as it is not in right state to delete
							}
														
							//delete the draft in db
							commonUIData.getCatelogPersistenceManager().deleteSelfAuthoredArtifactpojo(
									oneVersionOfSelfAuthoredArtifact);

							ArtifactMover artifactMover = ArtifactMover.getInstance(commonUIData);
							artifactMover.archiveDraft(oneVersionOfSelfAuthoredArtifact);

							//String physicalFileName = commonUIData.getCommons().getFullLocalPathFileNameOfNewArtifact(
							//		//String inRootNick, String inRelevance, String inLocalFileName
							//		oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.rootNick,
							//		oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.relevance,
							//		oneVersionOfSelfAuthoredArtifact.LocalFileName
							//		);							
							//try {
							//	commonUIData.getCommons().archiveLocalFile(physicalFileName);
							//} catch (IOException e1) {
							//	// TODO Auto-generated catch block
							//	ErrorHandler.showErrorAndQuit(commons, "Error in CreateArtifactUI displayContent while deleting draft " + physicalFileName
							//			+ ". root: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.rootNick
							//			+ ". relevance: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.relevance
							//			+ ". artifact: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.artifactName
							//			+ ". versionNum: " + oneVersionOfSelfAuthoredArtifact.unpulishedVerNum
							//			, e1);
							//}

							System.out.println("draft archived successful."
									+ " root: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.rootNick
									+ "; relevance: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.relevance
									+ "; artifact: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.artifactName
									+ "; versionNum: " + oneVersionOfSelfAuthoredArtifact.unpulishedVerNum
									+ ". draftingState: " + oneVersionOfSelfAuthoredArtifact.draftingState
							);
						}
						refreshScreen();
					}
				});
	
				delButn.pack();
				deleteBtneditor.minimumWidth = delButn.getSize().x;
				deleteBtneditor.horizontalAlignment = SWT.CENTER;
				deleteBtneditor.setEditor(delButn, items[ScreenRowNum],
				5);	
			}
			//Delete button process ends
			
			

		}
		composite.pack();
		mainShell.pack();
		mainShell.layout(true);
		mainShell.open();
		
///////////////// screen sorting starts
//		ArrayList<String>  mylist = 
//                new ArrayList<String>(); 
//		Collections.swap(mylist, 0,1);
///////////////// screen sorting ends
		
		
		while (!mainShell.isDisposed()) {
			if (!commonUIData.getESPoTDisplay().readAndDispatch()) {
				if (commonUIData.getArtifactDisplayOkayToContinue()) {
					commonUIData.getESPoTDisplay().sleep();
				} else {
					break;
				}
			}			
		}
	}
}