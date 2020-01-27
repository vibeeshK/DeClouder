package commonTechs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class SysTrayHanlder {
	/*
	 * This class helps to manage the system tray item
	 */
	Display display;
	Shell callerShell;
	public Tray tray;
	String backgroundImagePathFileName = null;
	
	String displayTitle;
	boolean okToContinue = true;
	final static int CALLER_ACTION_RESTORE = 1;
	final static int CALLER_ACTION_NONE = 0;
	
	public int callerToAct = 0;
	
	public static void main(String[] args) {
		
	}
	
	public SysTrayHanlder(Display inDisplay, Shell inCallerShell, String inDisplayTitle, String inBackgroundImagePathFileName) {
		display = inDisplay;
		callerShell = inCallerShell;
		displayTitle = inDisplayTitle;
		backgroundImagePathFileName = inBackgroundImagePathFileName;
	}
	
	public void displayTray(){

		// Retrieves the system tray singleton
		tray = display.getSystemTray();
		// Creates a new tray item (displayed as an icon)
		final TrayItem item = new TrayItem(tray, 0);
		final Image img = new Image(display, backgroundImagePathFileName);
		item.setToolTipText(displayTitle);
		item.setImage(img);
		// The tray item can only receive Selection/DefaultSelection (left click) or
		// MenuDetect (right click) events
		
		item.addListener(SWT.Selection,new Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				System.out.println("detected mouse click on tray SysTrayHanlder event is " + event);
				callerToAct = CALLER_ACTION_RESTORE;
				callerShell.setMinimized(false);
				callerShell.setVisible(true);
				callerShell.setActive();
			}
		});
		
		item.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				// We need a Shell as the parent of our menu
				Shell s = new Shell(event.display);
				// Style must be pop up
				Menu m = new Menu(s, SWT.POP_UP);
				// Creates a new menu item that terminates the program
				// when selected
				MenuItem exit = new MenuItem(m, SWT.NONE);
				exit.setText("Goodbye!");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						System.exit(0);
					}
				});
				// We need to make the menu visible
				m.setVisible(true);
			};
		});	 
	}
}
