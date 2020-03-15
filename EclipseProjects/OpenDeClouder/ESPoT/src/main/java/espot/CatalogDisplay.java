package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class CatalogDisplay extends ArtifactsDisplay implements Runnable {
	/*
	 * Diplays catalogs where the relevance is marked as interested
	 */	
	private String catalogDisplayTitle = "Catalog Display";

	public CatalogDisplay(CommonUIData inCommonUIData) {
		super(inCommonUIData);
		displayTitle = catalogDisplayTitle;
	}

	public void setData(){
		System.out.println("catelogPersistenceManager in setData is " + commonUIData.getCatelogPersistenceManager());

		ArrayList<ERLDownload> allERLDownLoads = catelogPersistenceManager.readActiveERLDownLoadsOfRoot();
		
		ArrayList<ERLDownload> dbDisplayERLs = new ArrayList<ERLDownload>();
		for (ERLDownload erlDownload : allERLDownLoads){
			System.out.println("CatalogDisplay SetData erlDownload " + erlDownload.artifactKeyPojo.artifactName);
			System.out.println("CatalogDisplay SetData erlDownload relevancePicked " + erlDownload.relevancePicked);
			System.out.println("CatalogDisplay SetData erlDownload relevancePicked " + erlDownload.relevancePicked);
			System.out.println("CatalogDisplay SetData erlDownload contentType " + erlDownload.artifactKeyPojo.contentType);
			System.out.println("CatalogDisplay SetData erlDownload personified " + erlDownload.personified);
			System.out.println("CatalogDisplay SetData erlDownload personified " + erlDownload.personified);
			
			if (erlDownload.relevancePicked 
				&& (erlDownload.author.equalsIgnoreCase(commons.userName)
				|| !commonUIData.getContentHandlerSpecsMap().get(erlDownload.artifactKeyPojo.contentType).personified)) {

				System.out.println("CatalogDisplay SetData Added erlDownload " + erlDownload.artifactKeyPojo.artifactName);

				dbDisplayERLs.add(erlDownload);
			} else {
				System.out.println("CatalogDisplay SetData Skipped erlDownload " + erlDownload.artifactKeyPojo.artifactName);				
			}			
		}
		setArtifactValues(dbDisplayERLs);

	}

	public void setAddlRibbonButtons() {
		Button btnCreateArtifact = new Button(buttonRibbon, SWT.NONE);
		
		btnCreateArtifact.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("getting into the createArtifact");

				lblMessageToUser.setText("CHILD WINDOW OPEN");
				lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				lblMessageToUser.redraw();

				System.out.println("before calling CreateArtifactUI commonUIData : " + commonUIData);
				
				CreateArtifactUI createArtifactUI = new CreateArtifactUI(
						commonUIData);
				
				createArtifactUI.displayCreateArtifactUI();
				System.out.println("after triggering create artifact UI");
				refreshScreen();				

				lblMessageToUser.setText("Welcome Back");
				lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				lblMessageToUser.redraw();
			}
		});
		btnCreateArtifact.setBounds(10, 10, 120, 25);
		btnCreateArtifact.setText("My Drafting");
		btnCreateArtifact.setToolTipText("Navigate to view or create drafts");

		Button btnAssignedArtifact = new Button(buttonRibbon, SWT.NONE);
		
		btnAssignedArtifact.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("getting into the Assigned");

				lblMessageToUser.setText("CHILD WINDOW OPEN");
				lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				lblMessageToUser.redraw();

				System.out.println("before calling AssignedArtifactUI commonUIData : " + commonUIData);
				
				AssignedArtifactsDisplay assignedArtifactsDisplay = new AssignedArtifactsDisplay(commonUIData);
				
				assignedArtifactsDisplay.displayArtifact();
				System.out.println("after triggering AssignedArtifactsDisplay");
				refreshScreen();				

				lblMessageToUser.setText("Welcome Back");
				lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				lblMessageToUser.redraw();
			}
		});
		btnAssignedArtifact.setBounds(10, 10, 120, 25);
		btnAssignedArtifact.setText("Assigned Tasks");
		btnAssignedArtifact.setToolTipText("Navigate to view assigned simple artifacts to author");
		
		//Assigned Work button end
		
		Button btnRootsSubscriptions = new Button(buttonRibbon, SWT.NONE);
		btnRootsSubscriptions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("getting into the RootsSubsriptions");

				RootsSubsciptionsUI rootsSubscriptionsUI = new RootsSubsciptionsUI(commonUIData);

				lblMessageToUser.setText("CHILD WINDOW OPEN");
				lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				lblMessageToUser.redraw();
				
				rootsSubscriptionsUI.displayRootsSubsriptionsUI();
				System.out.println("after triggering RootsSubsriptionsUI");
				refreshScreen();				

				lblMessageToUser.setText("Welcome Back");
				lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				lblMessageToUser.redraw();
			}
		});
		btnRootsSubscriptions.setBounds(10, 10, 120, 25);
		btnRootsSubscriptions.setText("Root Maintenance");
		btnRootsSubscriptions.setToolTipText("Navigate to choose roots and relevances");

		//Delete ALL ESPoT Artifacts starts
		Button btnDeleteArtifacts = new Button(buttonRibbon, SWT.NONE);
		btnDeleteArtifacts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("getting into the DeleteArtifacts");

				MessageBox usefulArtifactCheckMsgBox = new MessageBox(mainShell,
						SWT.ICON_WARNING | SWT.YES | SWT.NO);
				usefulArtifactCheckMsgBox.setMessage("CAUTION: Do you have any local artifact which you need in future?");
				int usefulArtifactCheckRc = usefulArtifactCheckMsgBox.open();

				if (usefulArtifactCheckRc != SWT.NO) {	// this question is purposely kept negative, since -
										// the default YES button might be pressed inadvertently
					MessageBox noDeleteMsgBox = new MessageBox(mainShell,
							SWT.ICON_INFORMATION);
					noDeleteMsgBox.setMessage("Its not a good idea to archive as you need them. Archivnig cancelled.");
					noDeleteMsgBox.open();
					return;
				}
				
				//get confirmation for delete
				
				
				MessageBox delConfirmMsgBox = new MessageBox(mainShell,
						SWT.ICON_WARNING | SWT.YES | SWT.NO);
				delConfirmMsgBox.setMessage("CAUTION: Are you 100% SURE to DELETE all local drafts and artifacts of all roots???");
				int delConfirmRc1 = delConfirmMsgBox.open();
				if (delConfirmRc1 != SWT.YES) {
					return;
				}
				
				ArchiverOfLocalESPoTArtifacts archiverOfLocalESPoTArtifacts = new ArchiverOfLocalESPoTArtifacts(commonUIData);
				archiverOfLocalESPoTArtifacts.archiveLocalESPoTArtifacts();

				System.out.println("after local artifacts moved to " + commonUIData.getCommons().getlocalArhiveFolder());
				refreshScreen();
				
				MessageBox postDelMsgBox = new MessageBox(mainShell,
						SWT.ICON_INFORMATION);
				postDelMsgBox.setMessage("All local artifacts archived into " + commonUIData.getCommons().getlocalArhiveFolder());
				postDelMsgBox.open();						
			}
		});
		btnDeleteArtifacts.setBounds(10, 10, 120, 25);
		//btnDeleteArtifacts.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_RED)); *OS wont allow to set color

		btnDeleteArtifacts.setText("Delete Artifacts");
		btnDeleteArtifacts.setToolTipText("Deletes all local drafts and artifacts of all roots");
		//Delete ALL ESPoT Artifacts ends
		
	}
		
	@Override
	public void setAddlFirstColumnHeaders() {
		// TODO Auto-generated method stub
		firstAddlColumnHeaders = new String[] {}; 	//no first addl. columns for catalog display
	}

	@Override
	public void setAddlSecondColumnHeaders() {
		// TODO Auto-generated method stub
		secondAddlColumnHeaders = new String[] { 
												"Author",
												"UploadedAt",
												"SubscriptionState"}; 
	}

	@Override
	public void setAddlThirdColumnHeaders() {
		// TODO Auto-generated method stub
		thirdAddlColumnHeaders = new String[] { 
												"Subscribe?",
												"Viewing", 
												"Cloning"};
	}
	
	public void setDisplayItemsFirstAddlColFieldsOfRow(TableEditor editor, Table table, TableItem tableItem, 
			ArtifactPojo displayArtifact, int inRowNumber) {
		//int lastColNum = firstAddlColumnHeaders.length + coreFirstColumnHeaders.length; etc..
		//ERLDownload displayERL = (ERLDownload) displayArtifact;
	}

	public void setDisplayItemsSecondAddlColFieldsOfRow(TableEditor editor, Table table, TableItem tableItem, 
		ArtifactPojo displayArtifact, int inRowNumber) {
		int lastColNum = firstAddlColumnHeaders.length + coreFirstColumnHeaders.length;
		ERLDownload displayERL = (ERLDownload) displayArtifact;

		editor = new TableEditor(table);
		Text textContentRemoteLocation = new Text(table, SWT.READ_ONLY);
		textContentRemoteLocation.setText(displayERL.author);
		editor.grabHorizontal = true;
		editor.setEditor(textContentRemoteLocation, tableItem, lastColNum++);

		editor = new TableEditor(table);
		Text textUploadedTimeStamp = new Text(table, SWT.READ_ONLY);
		textUploadedTimeStamp.setText(displayERL.uploadedTimeStamp);
		editor.grabHorizontal = true;
		editor.setEditor(textUploadedTimeStamp, tableItem, lastColNum++);

		editor = new TableEditor(table);
		Text textSubscriptions = new Text(table, SWT.READ_ONLY);
		textSubscriptions.setText(displayERL.subscriptionStatus);
		editor.grabHorizontal = true;
		System.out.println("columnCount : " + lastColNum + " Subscription ");
		editor.setEditor(textSubscriptions, tableItem, lastColNum++);

	}

	public void setDisplayItemsThirdAddlColFieldsOfRow(TableEditor editor, Table table, TableItem tableItem, 
			ArtifactPojo displayArtifact, int inRowNumber) {
		int lastColNum = firstAddlColumnHeaders.length + coreFirstColumnHeaders.length 
							+ secondAddlColumnHeaders.length + coreSecondColumnHeaders.length ;
		ERLDownload displayERL = (ERLDownload) displayArtifact;
		editor = new TableEditor(table);
		
		if (displayERL.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED) 
				|| displayERL.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {

			editor = new TableEditor(table);
			Button subscribeButton = new Button(table, SWT.PUSH);
			
			if (displayERL.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
				subscribeButton.setText("Renew");
				subscribeButton.setToolTipText("Renew downloaded artifact: " + displayERL.artifactKeyPojo.artifactName);
			} else {
				subscribeButton.setText("Subscribe");
				subscribeButton.setToolTipText("Subscribe to artifact: " + displayERL.artifactKeyPojo.artifactName);
			}
			
			subscribeButton.setData("CURRNTROWNUMBER", inRowNumber);

			subscribeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button eventButton = (Button) e.getSource();

					eventButton.setText("Subscribed");
					System.out.println("changed the text to subscribed");
					System.out.println("eventButton = " + eventButton);
					Integer i = (Integer) eventButton
							.getData("CURRNTROWNUMBER");
					System.out.println("selected row = " + i);
					System.out.println("ERL Name Original = "
							+ ((ERLDownload) artifactPojos.get(i)).artifactKeyPojo.artifactName);

					ERLDownload selectedERLpojo = (ERLDownload) artifactPojos.get(i);
					
					//if (selectedERLpojo.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)){
					//	commonUIData.getCatelogPersistenceManager().updateSubscriptionStatus(selectedERLpojo,ERLDownload.CURRENTLY_SUBSCRIBED);
					//} else {
					//	commonUIData.getCatelogPersistenceManager().insertSubscription(selectedERLpojo,ERLDownload.CURRENTLY_SUBSCRIBED);
					//}
					catelogPersistenceManager.replaceSubscription(selectedERLpojo,ERLDownload.CURRENTLY_SUBSCRIBED);
					refreshScreen();
				}
			});
			subscribeButton.pack();
			editor.minimumWidth = subscribeButton.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			System.out.println("columnCount : " + lastColNum + " at subscribe?: ");
			editor.setEditor(subscribeButton, tableItem, lastColNum++);
		} else {
			System.out.println("columnCount : " + lastColNum + " skipping subscribe ");
			lastColNum++;
		}
		if (displayERL.subscriptionStatus.equalsIgnoreCase(ERLDownload.AvailableStatus)
			|| 	!displayERL.downLoadedArtifactTimeStamp.equalsIgnoreCase("")) {
			editor = new TableEditor(table);
			Button viewButton = new Button(table, SWT.PUSH);
			if (displayERL.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED) ||
					displayERL.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_BEING_RENEWED)) {
				viewButton.setText(" View Old");
			} else {
				viewButton.setText(" View ");
			}
			viewButton.setData("CURRNTROWNUMBER", inRowNumber);
			viewButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button eventButton = (Button) e.getSource();

					System.out.println("eventButton = " + eventButton);
					Integer rowNumWithinView = (Integer) eventButton
							.getData("CURRNTROWNUMBER");
					System.out.println("selected row = " + rowNumWithinView);
					ERLDownload selectedERLpojoB = (ERLDownload) artifactPojos.get(rowNumWithinView);

					System.out.println("dbERLpojo.author = "
							+ selectedERLpojoB.author);
					System.out.println("dbERLpojo = " + selectedERLpojoB);
					System.out.println("dbERLpojo.contentType = " + selectedERLpojoB.artifactKeyPojo.contentType);
					System.out.println("contentHandlerSpecsMap = " + commonUIData.getContentHandlerSpecsMap());

					System.out.println("Iamhere to invoke artifactWrapperUI");

					//here you should add the artifactWrapperUI call
					ArtifactWrapperUI artifactWrapperUI = new ArtifactWrapperUI(commonUIData,selectedERLpojoB);
					
					lblMessageToUser.setText("Child Window Open");
					lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_RED));
					lblMessageToUser.redraw();

					artifactWrapperUI.displayArtifactWrapperUI();
					
					System.out.println("after triggering ArtifactWrapperUI");

					mainShell.forceActive();
					lblMessageToUser.setText("Welcome Back");
					lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
					lblMessageToUser.redraw();
				}
			});
			viewButton.pack();
			editor.minimumWidth = viewButton.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			System.out.println("columnCount : " + lastColNum + " view ");
			editor.setEditor(viewButton, tableItem, lastColNum++);

			///clone startA
			editor = new TableEditor(table);
			Button cloneButton = new Button(table, SWT.PUSH);
			cloneButton.setText(" Clone ");
			cloneButton.setData("CURRNTROWNUMBER", inRowNumber);
			cloneButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button eventButton = (Button) e.getSource();

					System.out.println("eventButton = " + eventButton);
					Integer rowNumWithinClone = (Integer) eventButton
							.getData("CURRNTROWNUMBER");
					System.out.println("selected row = " + rowNumWithinClone);
					ERLDownload selectedERLpojoD = (ERLDownload) artifactPojos.get(rowNumWithinClone);
					
					System.out.println("getting into the createArtifact for one pojo");

					lblMessageToUser.setText("CHILD WINDOW OPEN");
					lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_RED));
					lblMessageToUser.redraw();
					ArtifactWrapperUI artifactWrapperUI = new 
					ArtifactWrapperUI(ArtifactWrapperUI.CALLED_ForCloning,
										selectedERLpojoD,
										commonUIData,null);
					artifactWrapperUI.displayArtifactWrapperUI();
					mainShell.forceActive();
					lblMessageToUser.setText("Welcome Back");
					lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
					lblMessageToUser.redraw();
				}

			});
			cloneButton.pack();
			editor.minimumWidth = cloneButton.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			System.out.println("columnCount : " + lastColNum + " clone");
			editor.setEditor(cloneButton, tableItem, lastColNum++);
			///clone endsA
		} else {
			lastColNum++; // for skipping columns of view button
			lastColNum++; // for skipping columns of clone button
		}
	}
 
	public static void main(String[] args) throws IOException, ParseException {
		System.out.println("test test test");
		
		Commons commons = Commons.getInstance(Commons.CLIENT_MACHINE);
		CommonUIData commonUIData = (CommonUIData) CommonUIData.getUIInstance(commons);
		System.out.println("after setting commonUIData : " + commonUIData);

		commons.logger.info(" Catalog Display Started ");

		CatalogDisplay catalogDisplay = new CatalogDisplay(commonUIData);
		catalogDisplay.displayArtifact();

		commonUIData.getCatelogPersistenceManager().closeup();
		commonUIData.getESPoTDisplay().dispose();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		displayArtifact();
	}
}