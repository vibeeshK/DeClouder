package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class AssignedArtifactsDisplay extends ArtifactsDisplay{
	/*
	 * Displays all ERL artifacts assigned to the user as the author
	 */
	
	private String catalogDisplayTitle = "Assigned Artifacts";
	
	public AssignedArtifactsDisplay(CommonUIData inCommonUIData) {
		super(inCommonUIData);
		displayTitle = catalogDisplayTitle;
	}

	public void setData(){
		System.out.println("catelogPersistenceManager in setData is " + commonUIData.getCatelogPersistenceManager());
		ArrayList<ERLDownload> dbDisplayERLs = 
				catelogPersistenceManager.readERLDownLoadsOfAssignedContent(commonUIData.getCommons().userName);
		setArtifactValues(dbDisplayERLs);
	}

	
	public void setAddlRibbonButtons() {
	}
		
	@Override
	public void setAddlFirstColumnHeaders() {
		firstAddlColumnHeaders = new String[] {}; 	//no first addl. columns for catalog display
	}

	@Override
	public void setAddlSecondColumnHeaders() {
		secondAddlColumnHeaders = new String[] {"ERLStatus",
												"FileName",
												"UploadedAt",
												"SubscriptionState"}; 
	}

	@Override
	public void setAddlThirdColumnHeaders() {
		thirdAddlColumnHeaders = new String[] { 
												"Subscribe?",
												"Viewing", 
												"Cloning"};
	}
	
	public void setDisplayItemsFirstAddlColFieldsOfRow(TableEditor editor, Table table, TableItem tableItem, 
			ArtifactPojo displayArtifact, int inRowNumber) {

	}

	public void setDisplayItemsSecondAddlColFieldsOfRow(TableEditor editor, Table table, TableItem tableItem, 
		ArtifactPojo displayArtifact, int inRowNumber) {
		int lastColNum = firstAddlColumnHeaders.length + coreFirstColumnHeaders.length;
		ERLDownload displayERL = (ERLDownload) displayArtifact;

		editor = new TableEditor(table);
		Text erlStatusTx = new Text(table, SWT.READ_ONLY);
		erlStatusTx.setText(displayERL.erlStatus);
		editor.grabHorizontal = true;
		editor.setEditor(erlStatusTx, tableItem, lastColNum++);

		editor = new TableEditor(table);
		Text textContentRemoteLocation = new Text(table, SWT.READ_ONLY);
		textContentRemoteLocation.setText(displayERL.contentFileName);
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
			} else {
				subscribeButton.setText("Subscribe");
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
					
					commonUIData.getCatelogPersistenceManager().replaceSubscription(selectedERLpojo,ERLDownload.CURRENTLY_SUBSCRIBED);
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

					
					System.out.println("dbERLpojo.DownLoadedFile = "
							+ selectedERLpojoB.downLoadedFile);

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
										commonUIData,
										null);
					artifactWrapperUI.displayArtifactWrapperUI();
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

		AssignedArtifactsDisplay assgnedArtifactsDisplay = new AssignedArtifactsDisplay(commonUIData);
		assgnedArtifactsDisplay.displayArtifact();

		commonUIData.getCatelogPersistenceManager().closeup();
		commonUIData.getESPoTDisplay().dispose();
	}
}