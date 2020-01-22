package espot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class tableCheck {
	/*
	 * Used only as a POC
	 */

	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
	    final Table table = new Table(shell, SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
	    final TableEditor editor = new TableEditor(table);

	    for (int i = 0; i < 4; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
		}
	
		TableItem[] items = table.getItems();    
	    for (int i = 0; i < 10; i++) {
	        TableItem item = new TableItem(table, SWT.NONE);
    		Text text1 = new Text(table, SWT.NONE);
    		text1.setText("1st col");
    		editor.grabHorizontal = true;
    		editor.setEditor(text1, item, 1);
    		
    		text1.pack();

    		Text text2 = new Text(table, SWT.NONE);
    		text2.setText("2nd col");
    		editor.grabHorizontal = true;
    		editor.setEditor(text2, item, 2);
    	
    		text2.pack();
            
            Button button = new Button(table, SWT.PUSH);
			button.setText(" please clickme");
            editor.setEditor(button, item, 3);
            button.pack();
    }
 
    shell.pack();
	shell.open();

	while (!shell.isDisposed()) {
		if (!display.readAndDispatch())
			display.sleep();
	}

	display.dispose();
	}
}