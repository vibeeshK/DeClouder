package espot;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import commonTechs.SysTrayHanlder;

public abstract class ArtifactsDisplay {
	/*
	 * Foundation for displaying artifact list
	 */

	Shell mainShell;
	Label lblMessageToUser;
	
	ArrayList<ArtifactPojo> artifactPojos = null;
	public CommonUIData commonUIData = null;
	Commons commons = null;
	CatelogPersistenceManager catelogPersistenceManager = null;
	
	String displayTitle = null;
	Composite buttonRibbon = null;
	
	// to have columns spanned in the pattern: Addl - Core - Addl - Core - Addl
	String [] firstAddlColumnHeaders = null;
	String [] secondAddlColumnHeaders = null;
	String [] thirdAddlColumnHeaders = null;
	String [] coreFirstColumnHeaders = null;
	String [] coreSecondColumnHeaders = null;
	String [] columnHeaders = null;

	public ArtifactsDisplay(CommonUIData inCommonUIData) {
		commonUIData = inCommonUIData ;
		commons = commonUIData.getCommons();
		catelogPersistenceManager = commonUIData.getCatelogPersistenceManager();
	}
	abstract public void setData();
	
	public void setArtifactValues(ArrayList inArtifacts) {
		System.out.println("inArtifactPojos at setArtifactValues is " + inArtifacts);
		artifactPojos = inArtifacts;
		System.out.println("artifactPojos after setting is " + artifactPojos);
	}
	
	public void displayArtifact() {
		mainShell = new Shell(commonUIData.getESPoTDisplay(),SWT.APPLICATION_MODAL
								|SWT.CLOSE|SWT.TITLE|SWT.BORDER|SWT.RESIZE|SWT.MAX|SWT.MIN);
		mainShell.setLayout(new GridLayout(1, false));
		mainShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainShell.setImage(new Image(commonUIData.getESPoTDisplay(), commonUIData.getCommons().applicationIcon));

		System.out.println("at 1");
		System.out.println("at displayArtifact before new SysTrayHanlder " + mainShell);
		SysTrayHanlder sysTray =  new SysTrayHanlder(commonUIData.getESPoTDisplay(), mainShell, displayTitle, commonUIData.getCommons().applicationIcon);
		sysTray.displayTray();

		System.out.println("at displayArtifact after new SysTrayHanlder " + mainShell);
		
		displayContent();	
		shellDisposeHolder();		
	}

	public abstract void setAddlRibbonButtons();

	private void displayContent() {
		setData();
		
		System.out.println("at ArtifactDisplay DisplayContent Startxxx");

		mainShell.setText(displayTitle 
					+ ". Root: " + commonUIData.getCurrentRootNick()
					+ ". RootSysLogin: " + commonUIData.getCommons().userName
					+ ". DesktopUser: " + System.getProperty("user.name"));
		mainShell.setLayout(new GridLayout(1, false));
		mainShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		buttonRibbon = new Composite (mainShell, SWT.NONE);
		buttonRibbon.setLayout(new FillLayout(SWT.HORIZONTAL));

		lblMessageToUser = new Label(buttonRibbon, SWT.CENTER | SWT.BORDER_SOLID);
		lblMessageToUser.setText("Welcome!");
		lblMessageToUser.setForeground(commonUIData.getESPoTDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));

		setAddlRibbonButtons();

		Button btnRefresh = new Button(buttonRibbon, SWT.NONE);
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Refreshing the Catalog Display");
				refreshScreen();
			}
		});
		btnRefresh.setBounds(10, 10, 120, 25);
		btnRefresh.setText("Refresh");
		btnRefresh.setToolTipText("Refresh screen");

		final Composite composite = new Composite(mainShell, SWT.NONE);
		//composite.setLayout(new GridLayout());
		composite.setLayout(new GridLayout(1,false));
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    //final Table table = new Table(composite, SWT.None);
	    final Table table = new Table(composite, SWT.BORDER| SWT.V_SCROLL | SWT.H_SCROLL);

	    table.setHeaderVisible(true);
	    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		setAddlFirstColumnHeaders();
		coreFirstColumnHeaders = new String[] { 
											"Relevance",
											"ArtifactName", 
											"ContentType", 
											"Requestor"}; 
		setAddlSecondColumnHeaders();
		coreSecondColumnHeaders = new String[] {};
		
		setAddlThirdColumnHeaders();
		columnHeaders = commonUIData.getCommons().getCombinedStringArray5(firstAddlColumnHeaders,
														coreFirstColumnHeaders,
														secondAddlColumnHeaders,
														coreSecondColumnHeaders,
														thirdAddlColumnHeaders);

		System.out.println("columnHeaders size " + columnHeaders.length);
		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			column.setWidth(100);
			System.out.println("columnHeaders col(" + i + ") " + columnHeaders[i]);

			//Column sorting not implemented
			//commonUIData.getCommons().setComparator(column);
			//column.addListener(SWT.Selection,commonUIData.getCommons().setTableSortListener(table));			
		}
		System.out.println("screen rows filling starts");
		//////////
		//////////
		for (int i = 0; i < artifactPojos.size(); i++) {
			new TableItem(table, SWT.NONE);
		}
		TableItem[] tableItems = table.getItems();
		for (int screenRowNo = 0; screenRowNo < tableItems.length; screenRowNo++) {
			ArtifactPojo displayArtifact = artifactPojos.get(screenRowNo);
			int columnCount = 0;
			TableItem tableItem = tableItems[screenRowNo];
			TableEditor editor = new TableEditor(table);

			setDisplayItemsFirstAddlColFieldsOfRow(editor,table,tableItem,displayArtifact,screenRowNo);
			columnCount = firstAddlColumnHeaders.length;
			
			//coreFirstColumnSettingStarts
			editor = new TableEditor(table);
			Text textRelevance = new Text(table, SWT.READ_ONLY);
			textRelevance.setText(displayArtifact.artifactKeyPojo.relevance);
			editor.grabHorizontal = true;
			editor.setEditor(textRelevance, tableItem, columnCount++);

			editor = new TableEditor(table);
			Text artifactNameTx = new Text(table, SWT.READ_ONLY);
			artifactNameTx.setText(displayArtifact.artifactKeyPojo.artifactName);
			editor.grabHorizontal = true;
			editor.setEditor(artifactNameTx, tableItem, columnCount++);

			editor = new TableEditor(table);
			Text textContentType = new Text(table, SWT.READ_ONLY);
			textContentType.setText(displayArtifact.artifactKeyPojo.contentType);
			editor.grabHorizontal = true;
			editor.setEditor(textContentType, tableItem, columnCount++);

			editor = new TableEditor(table);
			Text textLastActionBy = new Text(table, SWT.READ_ONLY);
			textLastActionBy.setText(displayArtifact.requestor);
			editor.grabHorizontal = true;
			editor.setEditor(textLastActionBy, tableItem, columnCount++);
			//coreFirstColumnSettingEnd

			setDisplayItemsSecondAddlColFieldsOfRow(editor,table,tableItem,displayArtifact,screenRowNo);

			//coreSecondColumnSettingStart
			columnCount = firstAddlColumnHeaders.length + coreFirstColumnHeaders.length;
			//...no coreSecondColumns at this time
			//coreSecondColumnSettingEnd

			setDisplayItemsThirdAddlColFieldsOfRow(editor,table,tableItem,displayArtifact,screenRowNo);
		}
		//table.pack();

		//Column sorting not implemented
	    //table.setSortColumn(null);
	    //table.setSortDirection(SWT.UP);		
		
		
		composite.pack();
		mainShell.pack();
		mainShell.layout(true);
		mainShell.open();		
		//table.addListener(SWT.Selection,commonUIData.getCommons().setTableSortListener(table));
//		mainShell.addListener(SWT.Resize, new Listener () {
//			@Override
//			public void handleEvent(Event arg0) {
//				// TODO Auto-generated method stub
////				table.pack();
////				table.layout(true);
//				if (composite != null && !composite.isDisposed()) {
//					composite.pack();
//					composite.layout(true);		
//				}
//				System.out.println("resize caught");
//				
//			}
//		  }
//		);
//		table.addListener(SWT.Resize, new Listener () {
//			@Override
//			public void handleEvent(Event arg0) {
//				// TODO Auto-generated method stub
//				table.pack();
//				table.layout(true);
//				composite.pack();
//				composite.layout(true);		
//				mainShell.pack();
//				mainShell.layout(true);
//				System.out.println("resize caught");				
//			}
//		  }
//		);		
	}
	
	private void shellDisposeHolder() {
		while (!mainShell.isDisposed()) {
			if (!commonUIData.getESPoTDisplay().readAndDispatch()) {
				if (commonUIData.getArtifactDisplayOkayToContinue()) {
					commonUIData.getESPoTDisplay().sleep();
				} else {
					break;
				}
			}
		}
		System.out.println("here disposing....");
		mainShell.dispose();
	}

	abstract public void setAddlFirstColumnHeaders();
	abstract public void setAddlSecondColumnHeaders();
	abstract public void setAddlThirdColumnHeaders();
	abstract public void setDisplayItemsFirstAddlColFieldsOfRow(TableEditor editor,Table table, TableItem tableItem,ArtifactPojo displayArtifact, int inRowNumber);
	abstract public void setDisplayItemsSecondAddlColFieldsOfRow(TableEditor editor,Table table, TableItem tableItem,ArtifactPojo displayArtifact, int inRowNumber);
	abstract public void setDisplayItemsThirdAddlColFieldsOfRow(TableEditor editor,Table table, TableItem tableItem,ArtifactPojo displayArtifact, int inRowNumber);

	public void refreshScreen() {
		catelogPersistenceManager.tobeConnectedCatalogDbFile = 
			CatalogDownloadDtlsHandler.getInstance(commonUIData.getCommons()).getCatalogDownLoadedFileName(commonUIData.getCurrentRootNick());
		catelogPersistenceManager.connectToToBECataloged();

		System.out.println("refreshed catelogPersistenceManager is " + catelogPersistenceManager);
		
		Control[] oldControls = mainShell.getChildren();
		for (Control oldControl : oldControls) {
		    oldControl.dispose();
		}
		displayContent();
	}
}