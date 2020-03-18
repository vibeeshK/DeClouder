package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import commonTechs.DisplayKeeper;
import commonTechs.SysTrayHanlder;

public class ColbTrkClientUI {
	/*
	 * Diplays catalogs where the relevance is marked as interested
	 */	
	
	Display mainDisplay;
	Shell mainShell;
	Button btnCatalogUI;
	Button btnClientOrchestrator;
	Thread catalogDisplayThread;
	Thread espotClientOrchestratorThread;
	ColbTrkClientOrchestrator espotClientOrchestrator;
	CatalogDisplay catalogDisplay;
	
	Commons commons;

    final static int MIN_SHELL_WIDTH = 600;
    final static int MIN_SHELL_HEIGHT = 200;	
	
	public ColbTrkClientUI() {
		System.out.println("test test test");

		try {
			commons = Commons.getInstance(Commons.CLIENT_MACHINE);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Commons.logger.error("CollabTrackClientUI error creating commons");
		}

		mainDisplay = DisplayKeeper.getDisplay();
				
		mainShell = new Shell(mainDisplay,SWT.APPLICATION_MODAL
				|SWT.CLOSE|SWT.TITLE|SWT.BORDER|SWT.RESIZE|SWT.MAX|SWT.MIN);
		
		mainShell.setLayout(new GridLayout(1, false));
		mainShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		mainShell.setText("CollabTracker of " + System.getProperty("user.name"));		
		mainShell.setToolTipText("CollabTracker of " + System.getProperty("user.name"));

		//mainShell.pack();
		mainShell.setMinimumSize(
	            MIN_SHELL_WIDTH,
	            MIN_SHELL_HEIGHT);		
	}
	
	public void displayCollabTrackClient() {

		btnCatalogUI = new Button(mainShell, SWT.NONE);
		btnCatalogUI.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Refreshing the Catalog Display");
				btnCatalogUI();
			}
		});
		btnCatalogUI.setText("Catalog UI");
		btnCatalogUI.setToolTipText("Bring Up Catalog UI");
		btnCatalogUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		btnClientOrchestrator = new Button(mainShell, SWT.NONE);
		btnClientOrchestrator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("btnClientOrchestrator");
				btnClientOrchestratorProcess();
			}
		});
		btnClientOrchestrator.setText("Client Orchestrator");
		btnClientOrchestrator.setToolTipText("Start Client Orchestrator");
		btnClientOrchestrator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		mainShell.pack();
		mainShell.open();

		mainShell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  
		  		MessageBox closeConfirmMsgBox = new MessageBox(mainShell,
						SWT.ICON_WARNING | SWT.YES | SWT.NO);
				closeConfirmMsgBox.setMessage("HAVE YOU ENSURED ALL ARTIFACTs ARE SAVED?");
				int rc = closeConfirmMsgBox.open();
				if (rc != SWT.YES) {
					event.doit = false;
				}
		      }
		    });
		
		btnClientOrchestratorProcess();
		btnCatalogUI();

		shellDisposeHolder();
	}

	private void shellDisposeHolder() {
		while (!mainShell.isDisposed()) {
			if (mainDisplay.readAndDispatch()) {
				//System.out.println("something done....");
				mainDisplay.sleep();
			}
		}
		System.out.println("here disposing....");

		espotClientOrchestrator.orchestrationData.setOkayToContinue(false);
		catalogDisplay.commonUIData.setArtifactDisplayOkayToContinue(false);

		if (!mainShell.isDisposed()) {
			mainShell.dispose();
		}
	}
	
	public void btnCatalogUI() {
		if (catalogDisplayThread != null && catalogDisplayThread.isAlive()){
			MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
			messageBox1.setMessage("CatalogDisplay already open");
			messageBox1.open();
			return;
		}

		CatalogDownloadDtlsHandler catalogDownloadDtlsHandler = CatalogDownloadDtlsHandler.getInstance(commons);		
		String catalogDownLoadedFileName = catalogDownloadDtlsHandler.getCatalogDownLoadedFileNameIfAvailable(commons.getCurrentRootNick());
		
		if (catalogDownLoadedFileName == null) {
			MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
			messageBox1.setMessage("Catalogs not downLoaded yet. You can invoke CatalogUI after download completes in a few min.");
			messageBox1.open();
			return;			
		}		

		commons.logger.info("Catalog Display Started ");

		CommonUIData commonUIData = (CommonUIData) CommonUIData.getUIInstance(commons);
		catalogDisplay = new CatalogDisplay(commonUIData);
		catalogDisplayThread = new Thread(catalogDisplay);
		catalogDisplayThread.start();

	}

	public void btnClientOrchestratorProcess() {
		if (espotClientOrchestratorThread != null && espotClientOrchestratorThread.isAlive()){		
			MessageBox messageBox1 = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.OK);
			messageBox1.setMessage("ClientOrchestrator already active");
			messageBox1.open();
			return;
		}
		
		commons.logger.info(" ClientOrchestrator Started ");

		espotClientOrchestrator = new ColbTrkClientOrchestrator(commons);
		espotClientOrchestratorThread = new Thread(espotClientOrchestrator);
		espotClientOrchestratorThread.start();
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		
		ColbTrkClientUI collabTrackClientUI = new ColbTrkClientUI();
		collabTrackClientUI.displayCollabTrackClient();
	}
}