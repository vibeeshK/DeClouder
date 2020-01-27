package espot;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CredentialHandler {
	/*
	 * Captures user credentials for accessing a specific root
	 * note: it doesn't persist the credentials
	 */

	private static HashMap<String, CredentialsPojo> credentialMap = null;

	public static CredentialsPojo getCredentialsFor(String rootNick) {
		CredentialsPojo outCredentialsPojo = null;
		if (credentialMap == null || !credentialMap.containsKey(rootNick)) {
			captureCredentials(rootNick);
		}
		outCredentialsPojo = credentialMap.get(rootNick);
		return outCredentialsPojo;
	}

	private static void captureCredentials(String inCredentialFor) {
		if (credentialMap == null) {
			credentialMap = new HashMap<String, CredentialsPojo>();
		}

		Display display = new Display();
		final Shell shell = new Shell(display,SWT.APPLICATION_MODAL|SWT.CLOSE|SWT.TITLE|SWT.BORDER|SWT.RESIZE);
		shell.setLayout(new GridLayout(1, false));
		shell.setText("Logon" + inCredentialFor);
		shell.setSize(200, 200);

		final Label userNameLabel = new Label(shell, SWT.NONE);
		userNameLabel.setText("UserName");
		userNameLabel.pack();
		final Text userName = new Text(shell, SWT.BORDER);
		userName.pack();

		final Label passwordLabel = new Label(shell, SWT.NONE);
		passwordLabel.setText("Password");
		passwordLabel.pack();
		// Create a password text field
		final Text passwordBox = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		passwordBox.pack();

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Login");
		button.setData("credentialsFor", inCredentialFor);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				System.out.println("credential module");

				Button eventButton = (Button) e.getSource();
				String credentialFor = (String) eventButton.getData("credentialsFor");

				CredentialsPojo credentialsPojo = new CredentialsPojo(userName.getText(), passwordBox.getText());
				credentialsPojo.setCredentials("kvasavaiah", "xxxxxxx");
				credentialMap.put(credentialFor, credentialsPojo);
				shell.close();
			}
		});
		button.pack();

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}