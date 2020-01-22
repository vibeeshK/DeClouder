package espot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;

public class RootMaintenanceUI {
	/*
	 * UI for the maintenance of root subscription and default root setting
	 */

	CommonUIData commonUIData = null;
	private Shell mainShell = null;
	private ArrayList<String> allRootNicksList = null;
	HashMap<String, RootPojo> publishedRootsMap = null;
	HashMap<String, RootPojo> subscribedRootsMap = null;
	ArrayList<String> subscribedRootNicks = null;
	Document subscribedRootsDoc = null;
	SubscribedRootsPojo subscribedRootsPojo = null;

	public RootMaintenanceUI(CommonUIData inCommonUIData) {
		commonUIData = inCommonUIData;
		subscribedRootsPojo = new SubscribedRootsPojo(commonUIData);
	}

	public void refreshRootMaintenanceUI() {
		mainShell.close();

		displayRootMaintenanceUI();
	}

	public void displayRootMaintenanceUI() {

		mainShell = new Shell(commonUIData.getESPoTDisplay(), SWT.APPLICATION_MODAL | SWT.CLOSE
				| SWT.TITLE | SWT.BORDER | SWT.RESIZE);
		mainShell.setText("ESPoT: Root Maintenance");
		mainShell.setLayout(new GridLayout(1, false));
		mainShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		System.out.println("RootMaintenanceUI.displayRootMaintenanceUI() bef");
		publishedRootsMap = PublishedRootsHandler.getPublishedRoots(commonUIData.getCommons());
		allRootNicksList = new ArrayList<String>();
		allRootNicksList.addAll(publishedRootsMap.keySet());
		System.out
				.println("RootMaintenanceUI.displayRootMaintenanceUI() aft22");

		mainShell.setLayout(new GridLayout(1, false));

		//final Composite composite = new Composite(mainShell, SWT.NONE);
		//composite.setLayout(new GridLayout(1,false));
	    //composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Table table = new Table(mainShell, SWT.BORDER);
		//Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		String[] columnHeaders = new String[] { "RootNick", "RootString",
				"RootType", "RemoteAccesserType", "FileSeparator",
				"Subscriptions", "Default", "RelevancePick" };

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			if (columnHeaders[i].equalsIgnoreCase("RootString")) {
				column.setWidth(200);
			} else {
				column.setWidth(100);
			}
		}
		int screenMaxNum = allRootNicksList.size();

		for (int i = 0; i < screenMaxNum; i++) {
			// create table rows - one for a new artifact and as many for
			// already in-progress
			new TableItem(table, SWT.NONE);
		}

		TableItem[] items = table.getItems();

		for (int ScreenRowNum = 0; ScreenRowNum < screenMaxNum; ScreenRowNum++) {

			int columnCount = 0;
			RootPojo dbRootPojo = publishedRootsMap.get(allRootNicksList
					.get(ScreenRowNum));
			System.out.println("ScreenRowNum = " + ScreenRowNum);
			System.out.println("commons.defaultUIRootNick = " + commonUIData.getCurrentRootNick());
			
			System.out.println("allRootNicksList.get(ScreenRowNum) = "
					+ allRootNicksList.get(ScreenRowNum));
			System.out.println("dbRootPojo Nick = " + dbRootPojo.rootNick);
			System.out.println("dbRootPojo file separator = "
					+ dbRootPojo.fileSeparator);

			TableEditor editor = new TableEditor(table);
			Text rootNickTx = new Text(table, SWT.READ_ONLY);
			rootNickTx.setText(dbRootPojo.rootNick);
			editor.grabHorizontal = true;
			editor.setEditor(rootNickTx, items[ScreenRowNum], columnCount++);

			editor = new TableEditor(table);
			Text rootStringTx = new Text(table, SWT.READ_ONLY);
			rootStringTx.setText(dbRootPojo.rootString);
			editor.grabHorizontal = true;
			editor.setEditor(rootStringTx, items[ScreenRowNum], columnCount++);

			editor = new TableEditor(table);
			Text rootTypeTx = new Text(table, SWT.READ_ONLY);
			rootTypeTx.setText(dbRootPojo.rootString);
			editor.grabHorizontal = true;
			editor.setEditor(rootTypeTx, items[ScreenRowNum], columnCount++);

			editor = new TableEditor(table);
			Text remoteAccessorTx = new Text(table, SWT.READ_ONLY);
			remoteAccessorTx.setText(dbRootPojo.remoteAccesserType);
			editor.grabHorizontal = true;
			editor.setEditor(remoteAccessorTx, items[ScreenRowNum],
					columnCount++);

			editor = new TableEditor(table);
			Text fileSeparatorTx = new Text(table, SWT.READ_ONLY);
			fileSeparatorTx.setText(dbRootPojo.fileSeparator);
			editor.grabHorizontal = true;
			editor.setEditor(fileSeparatorTx, items[ScreenRowNum],
					columnCount++);

			if (dbRootPojo.rootType.equalsIgnoreCase(RootPojo.RegRootType)) {
				// Subscriptions related process
				editor = new TableEditor(table);
				Button maintainRootButton = new Button(table, SWT.PUSH);
				maintainRootButton.setData("ScreenRowNum", ScreenRowNum);

				System.out.println("RootNum = "
						+ maintainRootButton.getData("ScreenRowNum"));

				if (subscribedRootsPojo.doesRootNickExist(dbRootPojo.rootNick)) {
					maintainRootButton.setText("UnSubscribe");
					maintainRootButton
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e) {
									Button eventButton = (Button) e.getSource();
									System.out.println("eventButton = "
											+ eventButton);
									Integer screenRowNum = (Integer) eventButton
											.getData("ScreenRowNum");
									System.out
											.println("selected screenRowNum = "
													+ screenRowNum);

									subscribedRootsPojo.removeSubscription(allRootNicksList
											.get(screenRowNum));
									refreshRootMaintenanceUI();
								}
							});

				} else {
					maintainRootButton.setText("Subscribe");
					maintainRootButton
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e) {
									Button eventButton = (Button) e.getSource();
									System.out.println("eventButton = "
											+ eventButton);
									Integer screenRowNum = (Integer) eventButton
											.getData("ScreenRowNum");
									System.out
											.println("selected screenRowNum = "
													+ screenRowNum);
									subscribedRootsPojo.addSubscription(allRootNicksList
											.get(screenRowNum));
									refreshRootMaintenanceUI();
								}
							});
				}

				maintainRootButton.pack();
				editor.minimumWidth = maintainRootButton
						.getSize().x;
				editor.horizontalAlignment = SWT.LEFT;
				editor.setEditor(maintainRootButton,
						items[ScreenRowNum], columnCount++);
				// Default rootnick setting starts
				// Subscriptions related process

				if (!commonUIData.getCurrentRootNick()
						.equalsIgnoreCase(dbRootPojo.rootNick)) {

					System.out.println("this is NOT the default root");
					
					editor = new TableEditor(
							table);
					Button defaultNickSettingButton = new Button(table,
							SWT.PUSH);
					defaultNickSettingButton.setData("ScreenRowNum",
							ScreenRowNum);

					System.out.println("RootNum = "
							+ defaultNickSettingButton.getData("ScreenRowNum"));
					defaultNickSettingButton.setText("Set as Default");
					defaultNickSettingButton
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e) {
									Button eventButton = (Button) e.getSource();
									System.out.println("eventButton = "
											+ eventButton);
									Integer screenRowNum = (Integer) eventButton
											.getData("ScreenRowNum");
									System.out
											.println("selected screenRowNum = "
													+ screenRowNum);
									System.out
									.println("allRootNicksList.get(screenRowNum) is " 
											+ allRootNicksList
											.get(screenRowNum));
									try {
										commonUIData.getCommons().setDefaultUIRootNick(allRootNicksList
												.get(screenRowNum));
									} catch (IOException e1) {
										e1.printStackTrace();
										ErrorHandler.showErrorAndQuit(commonUIData.getCommons(), "Error in RootMaintenanceUI displayRootMaintenanceUI", e1);													
									}
									commonUIData.initBaseData();
									refreshRootMaintenanceUI();
								}
							});
					defaultNickSettingButton.pack();
					editor.minimumWidth = defaultNickSettingButton
							.getSize().x;
					editor.horizontalAlignment = SWT.LEFT;
					editor.setEditor(
							defaultNickSettingButton, items[ScreenRowNum],
							columnCount++);

				} else {
					System.out.println("this is THE default root");
					
					editor = new TableEditor(table);
					Text defaultNickSettingButtonTx = new Text(table,
							SWT.READ_ONLY);
					defaultNickSettingButtonTx.setText("Default");
					editor.grabHorizontal = true;
					editor.setEditor(defaultNickSettingButtonTx, items[ScreenRowNum],
							columnCount++);
					defaultNickSettingButtonTx.pack();
				}

				// Default rootnick setting ends

				if (commonUIData.getCurrentRootNick()
						.equalsIgnoreCase(dbRootPojo.rootNick)) {
					editor = new TableEditor(table);
					Button relevancebutton = new Button(table, SWT.PUSH);
					relevancebutton.setText("Choose");
					relevancebutton.pack();
					editor.minimumWidth = relevancebutton.getSize().x;
					editor.horizontalAlignment = SWT.LEFT;
					editor.setEditor(relevancebutton,
							items[ScreenRowNum], columnCount++);
	
					relevancebutton.setData("ScreenRowNum", ScreenRowNum);
	
					System.out.println("set data = "
							+ relevancebutton.getData("ScreenRowNum"));
	
					relevancebutton.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							Button eventButton = (Button) e.getSource();

							System.out.println("invoking Relevance Pick UI");
	
							System.out.println("eventButton = " + eventButton);
							Integer ScreenRowNum = (Integer) eventButton
									.getData("ScreenRowNum");
							System.out.println("selectedRowNum = " + ScreenRowNum);
							String rootNick = allRootNicksList.get(ScreenRowNum);
							System.out.println("rootNik = " + rootNick);
	
							RelevancePickUI relevancePickUI = new RelevancePickUI(commonUIData);
							relevancePickUI.displayRelevancePickUI();
							refreshRootMaintenanceUI();
						}
					});
				}
			}
		}

		//composite.pack();
		mainShell.pack();
		mainShell.layout(true);
		mainShell.open();

		while (!mainShell.isDisposed()) {
			if (!commonUIData.getESPoTDisplay().readAndDispatch())
				commonUIData.getESPoTDisplay().sleep();
		}
	}
}