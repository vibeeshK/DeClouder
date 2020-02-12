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

public class UsersListing extends GenericGrouper {
	/*
	 * This content handler helps to view users
	 */

	//These single item fields will be referred when one single item is pulled out
	Text userShortIDText;
	Text userNameText;
	Text leadIDText;
	Text activeStateText;	
	Text privilegeText;

	public String prevalidate(CommonData inCommonData,ArtifactKeyPojo inArtifactKeyPojo){		
		return UserItem.userItemPreValidation(inCommonData,inArtifactKeyPojo);
	}
	
	protected void setScreenTitle() {
		mainShell.setText("ESPoT:UsersListing: <viewContentsAtDesk> on " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	}

	public ItemPojo getItemPojo(int inItemCount){
		UserItemPojo userItemPojo = new UserItemPojo(inItemCount);
		return userItemPojo;
	}
	
	protected void setAddlColumnHeaders(){

		int addlHeadersCount = 0;
		
		System.out.println("setAddlColumnHeaders  ");
		System.out.println("addlHeadersCount=" + addlHeadersCount);

		//centerBaseColHeaders = new String[] {"Description","Author","Subportfolio","Application","Reviewer","Status"};
		centerBaseColHeaders = new String[] {"ShortID","UserName","LeadID","ActiveState", "Privilege"};
	}

	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor editor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo){
		UserItemPojo userItemPojo = (UserItemPojo) inItemPojo;

		editor = new TableEditor(inTable);
		Text userShortID_Tx = new Text(inTable, SWT.READ_ONLY);
		userShortID_Tx.setText(userItemPojo.userPojo.rootSysLoginID);
		editor.grabHorizontal = true;
		editor.setEditor(userShortID_Tx, inTableItem, ++inLastColLocation);
		
		editor = new TableEditor(inTable);
		Text userName_Tx = new Text(inTable, SWT.READ_ONLY);
		userShortID_Tx.setText(userItemPojo.userPojo.userName);
		editor.grabHorizontal = true;
		editor.setEditor(userName_Tx, inTableItem, ++inLastColLocation);
	
		editor = new TableEditor(inTable);
		Text leadID_Tx = new Text(inTable, SWT.READ_ONLY);
		leadID_Tx.setText(userItemPojo.userPojo.leadID);
		editor.grabHorizontal = true;
		editor.setEditor(leadID_Tx, inTableItem, ++inLastColLocation);		

		editor = new TableEditor(inTable);
		Text activeState_Tx = new Text(inTable, SWT.READ_ONLY);
		activeState_Tx.setText(userItemPojo.userPojo.activeStatus);
		editor.grabHorizontal = true;
		editor.setEditor(activeState_Tx, inTableItem, ++inLastColLocation);		

		editor = new TableEditor(inTable);
		Text privilege_Tx = new Text(inTable, SWT.READ_ONLY);
		privilege_Tx.setText(UserPojo.getPrivilegeLitOfLevel(userItemPojo.userPojo.privilegeLevel));				
		editor.grabHorizontal = true;
		editor.setEditor(privilege_Tx, inTableItem, ++inLastColLocation);

	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup,Group inPrevGroup,FormData formData,ItemPojo itemPojo){

		UserItemPojo userItemPojo = (UserItemPojo) itemPojo;

		Group userInfo = new Group(itemContentGroup, SWT.LEFT);
		userInfo.setText("UserShortID");
		userInfo.setLayout(new FillLayout());
		userShortIDText = new Text(userInfo, SWT.WRAP | SWT.CENTER);
		userShortIDText.setText(userItemPojo.userPojo.rootSysLoginID);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		userInfo.setLayoutData(formData);
		inPrevGroup = userInfo;

		Group userNameInfo = new Group(itemContentGroup, SWT.LEFT);
		userNameInfo.setText("UserName");
		userNameInfo.setLayout(new FillLayout());
		userNameText = new Text(userNameInfo, SWT.WRAP | SWT.CENTER);
		userNameText.setText(userItemPojo.userPojo.userName);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		userNameInfo.setLayoutData(formData);
		inPrevGroup = userNameInfo;

		Group leadIDInfo = new Group(itemContentGroup, SWT.LEFT);
		leadIDInfo.setText("LeadID");
		leadIDInfo.setLayout(new FillLayout());
		leadIDText = new Text(leadIDInfo, SWT.WRAP | SWT.CENTER);
		leadIDText.setText(userItemPojo.userPojo.leadID);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		userNameInfo.setLayoutData(formData);
		inPrevGroup = userNameInfo;

		Group activeStateInfo = new Group(itemContentGroup, SWT.LEFT);
		activeStateInfo.setText("LeadID");
		activeStateInfo.setLayout(new FillLayout());
		activeStateText = new Text(activeStateInfo, SWT.WRAP | SWT.CENTER);
		activeStateText.setText(userItemPojo.userPojo.activeStatus);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		activeStateInfo.setLayoutData(formData);
		inPrevGroup = activeStateInfo;

		Group privilegeInfo = new Group(itemContentGroup, SWT.LEFT);
		privilegeInfo.setText("Privilege");
		privilegeInfo.setLayout(new FillLayout());
		privilegeText = new Text(privilegeInfo, SWT.WRAP | SWT.CENTER);
		privilegeText.setText(UserPojo.getPrivilegeLitOfLevel(userItemPojo.userPojo.privilegeLevel));
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		privilegeInfo.setLayoutData(formData);
		inPrevGroup = privilegeInfo;
		
		return inPrevGroup;
	}
	
	//public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
	//	UserItemPojo userItemPojo = (UserItemPojo) inItemPojo;
	//	userItemPojo.application = applicationText.getText();
	//	userItemPojo.status = statusText.getText();
	//	userItemPojo.reviewer = reviewerText.getText();
	//	userItemPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
	//}
	//
	//public void setInitialItemPojoAddlFields(ItemPojo inItemPojo){
	//	UserItemPojo userItemPojo = (UserItemPojo) inItemPojo;
	//	userItemPojo.author = commonData.getCommons().userName;
	//	userItemPojo.status = "Draft";
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
		return UserItemDoc.class;
	}

	@Override
	public Class getPrimerDocClass() {
		return UsersListingDoc.class;
	}
	
	public void setInitialContent(String inRelevance, String inContentType, GenericGrouperDocPojo inDocumentToUpdate) {
	// Appends users into the given doc
		int usersCnt = 0;
		for (UserPojo userPojo : commonData.getUsersHandler().getUserDetails()) {
			usersCnt++;
			UserItemPojo userItemPojo = new UserItemPojo(userPojo);
			userItemPojo.itemID = commonData.getCommons().userName + commonData.getCommons().getCurrentTimeStamp() + "x" + usersCnt;
			userItemPojo.contentType = inContentType;
			userItemPojo.relevance = inRelevance;
			userItemPojo.artifactName = userPojo.rootSysLoginID;

			inDocumentToUpdate.absorbIncomingItemPojo(userItemPojo);
		}
	}
	
	public void additionalRollAddWebProcess(ItemPojo inItemPojo) {
	// update users database
		System.out.println("catelogPersistenceManager is " + catelogPersistenceManager);
		System.out.println("inItemPojo is " + inItemPojo);
		System.out.println("((UserItemPojo) inItemPojo).userPojo is " + ((UserItemPojo) inItemPojo).userPojo);
		catelogPersistenceManager.replaceUser(((UserItemPojo) inItemPojo).userPojo);		
	}
	

	@Override
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo usersListingDoc = new UsersListingDoc();
		return usersListingDoc;
	}

	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
		System.out.println("at1 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at1 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at1 getBaseDoc for itemID title" + inItemPojo.title);


		GenericItemDocPojo userItemDoc = new UserItemDoc(inItemPojo);

		System.out.println("at2 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at2 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at2 getBaseDoc for itemID title" + inItemPojo.title);

		System.out.println("at3 getBaseDoc for doc " + userItemDoc);
		System.out.println("at3 getBaseDoc for item " + userItemDoc.getItem());
		System.out.println("at3 getBaseDoc for itemID " + userItemDoc.getItem().itemID);
		System.out.println("at3 getBaseDoc for itemID relevance" + userItemDoc.getItem().relevance);
		System.out.println("at3 getBaseDoc for itemID title" + userItemDoc.getItem().title);
		return userItemDoc;		
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
	public UsersListingDoc getPrimerDoc() {
		return (UsersListingDoc) primerDoc;
	}
}