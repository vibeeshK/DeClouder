package espot;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class UsersDisplay {

	public final static String AUTHOR_LIT = "Author";
	public final static String REVIEWER_LIT = "Reviewer";
	public final static String LEAD_LIT = "Lead";
	public final static String AUTHOR_ASSIGN_TEXT = "Assign Author";
	public final static String REQUESTOR_ASSIGN_TEXT = "Assign Requestor";
	public final static String AUTHOR_REASSIGN_TEXT = "Re-assign Author";
	public final static String REQUESTOR_REASSIGN_TEXT = "Re-assign Requestor";
	public Text userText = null;
	
	public UsersDisplay(final UsersHandler inUsersHandler, Group inContainerGroup, String inCurrentAuthor, boolean inInvokedForEdit, String inGroupText){
		System.out.println("At start of UsersDisplay inGroupText " + inGroupText);
		System.out.println("At start of UsersDisplay inCurrentAuthor " + inCurrentAuthor);
		System.out.println("At start of UsersDisplay inInvokedForEdit " + inInvokedForEdit);

		final Group assignedAuthorGroup = new Group(inContainerGroup, SWT.CENTER);
		assignedAuthorGroup.setLayout(new FillLayout());
		assignedAuthorGroup.setText(inGroupText);
		
		if (inUsersHandler.getIndexOfUserShortId(inCurrentAuthor) == -1) {
			UserPojo currentUserPojo = new UserPojo(inCurrentAuthor, "_Unknown Current User ", "" , 0, "");
			inUsersHandler.appendUserPojo(currentUserPojo);
		}
		userText = new Text(assignedAuthorGroup, SWT.CENTER | SWT.READ_ONLY);
		userText.setText(inCurrentAuthor);

		final CCombo usersList = new CCombo(assignedAuthorGroup, SWT.DROP_DOWN | SWT.READ_ONLY);

		usersList.setItems(inUsersHandler.getUsersNamesStrings());
		usersList.select(inUsersHandler.getIndexOfUserShortId(inCurrentAuthor));
		if (inInvokedForEdit) {
			System.out.println("000 UsersDisplay inInvokedForEdit true enabling selection");
			usersList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("000 cloneFromContentTypeList size:" + usersList.getSize());
					userText.setText(inUsersHandler.getUserShortnameByIndex(usersList.getSelectionIndex()));
				}
			});
		} else {
			System.out.println("000 UsersDisplay inInvokedForEdit false disabling selection");
			usersList.setEnabled(inInvokedForEdit);
		}
	}
}