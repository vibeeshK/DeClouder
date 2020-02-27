package espot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class ERLStatusDisplay {
	/*
	 * Convenience class that derives the ERL Status text to be displayed
	 */
	public final static String STATUS_ASSIGN_TEXT = "New ERLStatus";
	public final static String CURR_STATUS_TEXT = "Current ERLStatus";
	CCombo statusList = null;
	Text statusText = null;
	String[] validActions = null;
	String newERLStatusCaptured = null;
	
	public ERLStatusDisplay(String[] inValidActions, Group inContainerGroup, String inNewERLStatus, boolean inInvokedForEdit, String inGroupText){
		System.out.println("At start of StatusDisplay inGroupText " + inGroupText);
		System.out.println("At start of StatusDisplay inNewERLStatus " + inNewERLStatus);
		System.out.println("At start of StatusDisplay inInvokedForEdit " + inInvokedForEdit);

		validActions = inValidActions;
		newERLStatusCaptured = inNewERLStatus;

		final Group newStatusGroup = new Group(inContainerGroup, SWT.CENTER);
		newStatusGroup.setLayout(new FillLayout());
		newStatusGroup.setText(inGroupText);

		if (inInvokedForEdit) {
			statusList = new CCombo(newStatusGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			statusList.setItems(validActions);
			statusList.select(statusList.indexOf(newERLStatusCaptured));
			System.out.println("000 StatusDisplay inInvokedForEdit true enabling selection");
			statusList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("000 StatusList size:" + statusList.getSize());
					newERLStatusCaptured = statusList.getItem(statusList.getSelectionIndex());
					System.out.println("statusList.getSelectionIndex() " + statusList.getSelectionIndex());
					System.out.println("newERLStatusStaging within display set to " + newERLStatusCaptured);
					
				}
			});
		} else {
			System.out.println("000 StatusDisplay inInvokedForEdit false");
			statusText = new Text(newStatusGroup, SWT.LEFT | SWT.READ_ONLY);
			statusText.setText(newERLStatusCaptured);
		}
	}
}