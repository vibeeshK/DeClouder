package contentHandlers;

import espot.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList; //import org.eclipse.swt.events.SelectionAdapter;
import java.util.HashMap;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IdeasGrouper extends GenericGrouper {
	/*
	 * This content handler helps to group ideas within a relevance
	 */

	//These single item fields will be referred when one single item is pulled out
	Text applicationText;
	Text statusText;
	Text reviewerText;
	Text authorNameText;

	protected void setScreenTitle() {
		mainShell.setText("ESPoT:IdeasGrouper: <viewContentsAtDesk> on " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	}

	public ItemPojo getItemPojo(int inItemCount){
		IdeaPojo ideaPojo = new IdeaPojo(inItemCount);
		return ideaPojo;
	}
	
	protected void setAddlColumnHeaders(){

		int addlHeadersCount = 0;
		
		System.out.println("setAddlColumnHeaders  ");
		System.out.println("addlHeadersCount=" + addlHeadersCount);

		centerBaseColHeaders = new String[] {"Description","Author","Subportfolio","Application","Reviewer","Status"};
	}

	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor editor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo){
		IdeaPojo ideaPojo = (IdeaPojo) inItemPojo;

		editor = new TableEditor(inTable);
		Text description_Tx = new Text(inTable, SWT.READ_ONLY);
		description_Tx.setText(ideaPojo.description);
		editor.grabHorizontal = true;
		editor.setEditor(description_Tx, inTableItem, ++inLastColLocation);

		editor = new TableEditor(inTable);
		Text author_Tx = new Text(inTable, SWT.READ_ONLY);
		author_Tx.setText(ideaPojo.author);
		editor.grabHorizontal = true;
		editor.setEditor(author_Tx, inTableItem, ++inLastColLocation);
	
		editor = new TableEditor(inTable);
		Text subportfolio_Tx = new Text(inTable, SWT.READ_ONLY);
		subportfolio_Tx.setText(ideaPojo.subportfolio);
		editor.grabHorizontal = true;
		editor.setEditor(subportfolio_Tx, inTableItem, ++inLastColLocation);
	
		editor = new TableEditor(inTable);
		Text application_Tx = new Text(inTable, SWT.READ_ONLY);
		application_Tx.setText(ideaPojo.application);
		editor.grabHorizontal = true;
		editor.setEditor(application_Tx, inTableItem, ++inLastColLocation);
	
		editor = new TableEditor(inTable);
		Text reviewer_Tx = new Text(inTable, SWT.READ_ONLY);
		reviewer_Tx.setText(ideaPojo.reviewer);
		editor.grabHorizontal = true;
		editor.setEditor(reviewer_Tx, inTableItem, ++inLastColLocation);
	
		editor = new TableEditor(inTable);
		Text status_Tx = new Text(inTable, SWT.READ_ONLY);
		status_Tx.setText(ideaPojo.status);
		editor.grabHorizontal = true;
		editor.setEditor(status_Tx, inTableItem, ++inLastColLocation);
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){

		IdeaPojo ideaPojo = (IdeaPojo) itemPojo;
		Group applicationInfo = new Group(itemContentGroup, SWT.LEFT);
		applicationInfo.setText("Application");
		applicationInfo.setLayout(new FillLayout());
		applicationText = new Text(applicationInfo, SWT.WRAP | SWT.CENTER);
		applicationText.setText(ideaPojo.application);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		applicationInfo.setLayoutData(formData);
	
		Group reviewerInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		reviewerInfo.setText("Reviewer");
		reviewerInfo.setLayout(new FillLayout());
		reviewerText = new Text(reviewerInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		reviewerText.setText(ideaPojo.reviewer);
		
		formData = new FormData();
		formData.top = new FormAttachment(applicationInfo);
		reviewerInfo.setLayoutData(formData);
	
		Group statusInfo = new Group(itemContentGroup, SWT.LEFT
				| SWT.WRAP | SWT.READ_ONLY);
		statusInfo.setText("Status");
		statusInfo.setLayout(new FillLayout());
		statusText = new Text(statusInfo, SWT.WRAP | SWT.READ_ONLY | SWT.CENTER);
		statusText.setText(ideaPojo.status);
		
		formData = new FormData();
		formData.top = new FormAttachment(reviewerInfo);
		statusInfo.setLayoutData(formData);
		
		Group authorInfo = new Group(itemContentGroup, SWT.LEFT);
		authorInfo.setText("Author");
		authorInfo.setLayout(new FillLayout());
		authorNameText = new Text(authorInfo, SWT.WRAP
				| SWT.READ_ONLY | SWT.CENTER);
		authorNameText.setText(ideaPojo.author);
	
		formData = new FormData();
		formData.top = new FormAttachment(statusInfo);
		authorInfo.setLayoutData(formData);
		
		return authorInfo;
	}
	
	//public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
	//	IdeaPojo ideaPojo = (IdeaPojo) inItemPojo;
	//	ideaPojo.application = applicationText.getText();
	//	ideaPojo.status = statusText.getText();
	//	ideaPojo.reviewer = reviewerText.getText();
	//	ideaPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
	//}
	//
	//public void setInitialItemPojoAddlFields(ItemPojo inItemPojo){
	//	IdeaPojo ideaPojo = (IdeaPojo) inItemPojo;
	//	ideaPojo.author = commonData.getCommons().userName;
	//	ideaPojo.status = "Draft";
	//}

	public void getPrimerDocAddlFields() {
		// from persistence to screen
	}
	public void setPrimerDocAddlFields() {
		// from screen to persistence		
	}
	
	@Override
	public void extendedCommonInit() {
	}
	
	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	protected Class getBasePrimerDocClass() {
		return IdeaItemDoc.class;
	}

	@Override
	public Class getPrimerDocClass() {
		return IdeasGrouperDoc.class;
	}
	
	@Override
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo ideasGrouperDoc = new IdeasGrouperDoc();
		return ideasGrouperDoc;
	}

	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
		System.out.println("at1 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at1 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at1 getBaseDoc for itemID title" + inItemPojo.title);


		GenericItemDocPojo ideaItemDoc = new IdeaItemDoc(inItemPojo);

		System.out.println("at2 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at2 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at2 getBaseDoc for itemID title" + inItemPojo.title);

		System.out.println("at3 getBaseDoc for doc " + ideaItemDoc);
		System.out.println("at3 getBaseDoc for item " + ideaItemDoc.getItem());
		System.out.println("at3 getBaseDoc for itemID " + ideaItemDoc.getItem().itemID);
		System.out.println("at3 getBaseDoc for itemID relevance" + ideaItemDoc.getItem().relevance);
		System.out.println("at3 getBaseDoc for itemID title" + ideaItemDoc.getItem().title);
		return ideaItemDoc;		
	}

	@Override
	public void additionalRibbonButtons(Composite ribbon) {
	}

	@Override
	public void setDisplayItemsCenterAddlFieldsInMultiDisplay(
			TableEditor editor, Table inTable, TableItem intableItem,
			int inLastColLocation, ItemPojo inItemPojo) {
	}

	@Override
	public String getDefaultSourceFileName(ItemPojo inItemPojo) {
		return null;
	}

	@Override
	public IdeasGrouperDoc getPrimerDoc() {
		return (IdeasGrouperDoc) primerDoc;
	}
}