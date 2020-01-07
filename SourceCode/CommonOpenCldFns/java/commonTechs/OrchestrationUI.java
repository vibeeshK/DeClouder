package commonTechs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class OrchestrationUI implements Runnable {
	/*
	 * Provides a system tray to control Orchestrators that usually run in the background
	 */

	final int DEFAULTINTERVAL_MINIM_MINTS = 1;
	final int DEFAULTINTERVAL_INCREMT_MINTS = 1;
	final int DEFAULTINTERVAL_MAX_MINTS = 2;
	final int DEFAULTINTERVAL_DEFSEL_MINTS = 1;
	
	Spinner healthCheckIntervalDateTime;
	Spinner repeatIntervalDateTime;
	Display display;

	OrchestrationData orchestration;
	Shell mainShell;
	SysTrayHanlder sysTray;
	public OrchestrationUI(OrchestrationData inOrchestration) {
		orchestration = inOrchestration;
	}

	void displayOrchUI() {
		display = Display.getDefault();
		mainShell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE
				| SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.MIN | SWT.MAX);
		mainShell.setLayout(new FillLayout());
		mainShell.setText("OpenDeClouder: " + orchestration.title);


		Group healthCheckIntervalInfo = new Group(mainShell, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		healthCheckIntervalInfo.setText("HealthChkIntervalMinutes");
		healthCheckIntervalInfo.setLayout(new FillLayout());

		healthCheckIntervalDateTime = new Spinner(healthCheckIntervalInfo, SWT.NONE | SWT.CENTER);
		healthCheckIntervalDateTime.setMinimum(DEFAULTINTERVAL_MINIM_MINTS);
		healthCheckIntervalDateTime.setIncrement(DEFAULTINTERVAL_INCREMT_MINTS);
		healthCheckIntervalDateTime.setMaximum(DEFAULTINTERVAL_MAX_MINTS);
		healthCheckIntervalDateTime.setSelection(DEFAULTINTERVAL_DEFSEL_MINTS);

		Group repeatIntervalInfo = new Group(mainShell, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		repeatIntervalInfo.setText("RepeatIntervalMinutes");
		repeatIntervalInfo.setLayout(new FillLayout());

		repeatIntervalDateTime = new Spinner(repeatIntervalInfo, SWT.NONE | SWT.CENTER);
		repeatIntervalDateTime.setMinimum(DEFAULTINTERVAL_MINIM_MINTS);
		repeatIntervalDateTime.setIncrement(DEFAULTINTERVAL_INCREMT_MINTS);
		repeatIntervalDateTime.setMaximum(DEFAULTINTERVAL_MAX_MINTS);
		repeatIntervalDateTime.setSelection(DEFAULTINTERVAL_DEFSEL_MINTS);
		
		Button applyButton = new Button (mainShell, SWT.PUSH);
		applyButton.setText ("Apply");
		applyButton.addSelectionListener (new SelectionAdapter () {
	        public void widgetSelected (SelectionEvent e) {
				orchestration.healthCheckIntervalMin = healthCheckIntervalDateTime.getSelection();
				orchestration.repeatIntervalMin = repeatIntervalDateTime.getSelection();
				System.out.println("healthCheckIntervalMin is " + orchestration.healthCheckIntervalMin);
				System.out.println("repeatIntervalMin is " + orchestration.repeatIntervalMin);
	        }
	      });
		
		Button stopButton = new Button (mainShell, SWT.PUSH);
		stopButton.setText ("Stop");
		stopButton.addSelectionListener (new SelectionAdapter () {
	        public void widgetSelected (SelectionEvent e) {
	        	orchestration.okToContinue = false;
	        }
	      });
				
		mainShell.setSize(200, 200);
		mainShell.pack();
		mainShell.open();
		mainShell.layout(true);

		System.out.println("at displayArtifact before new SysTrayHanlder " + mainShell);
	}

	public void run() {
		displayOrchUI();

		sysTray =  new SysTrayHanlder(display, mainShell, orchestration.title, orchestration.backgroundImagePathFileName);
		sysTray.displayTray();

		mainShell.addListener(SWT.Close, new Listener() {
		    public void handleEvent(Event event) {
				System.out.println("inside window closure event ");
		    	orchestration.okToContinue = false;
		    }
		});

		// Wait forever...
		while (orchestration.okToContinue) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		System.out.println("ouside while Orchestr wait loop display is " + display);
    	display.dispose();
	}
}
