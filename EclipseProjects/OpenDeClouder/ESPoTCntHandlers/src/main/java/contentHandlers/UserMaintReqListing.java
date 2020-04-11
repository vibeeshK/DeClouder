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

public class UserMaintReqListing extends GenericGrouper {
	/*
	 * This content handler helps to view users
	 */

	//These single item fields will be referred when one single item is pulled out
	//Text userShortIDText;
	//Text userNameText;
	//Text leadIDText;
	//Text activeStateText;	
	//Text privilegeText;

	public String prevalidate(CommonData inCommonData,ArtifactKeyPojo inArtifactKeyPojo){		
		return UserMaintReq.userItemPreValidation(inCommonData,inArtifactKeyPojo);
	}
	
	protected void setScreenTitle() {
		mainShell.setText("UsersListing: <viewContentsAtDesk> on " + invokedArtifactPojo.artifactKeyPojo.artifactName);
	}

	public ItemPojo getItemPojo(int inItemCount){
		UserMaintReqPojo userMaintReqPojo = new UserMaintReqPojo(inItemCount);
		return userMaintReqPojo;
	}
	
	protected void setAddlColumnHeaders(){

		int addlHeadersCount = 0;
		
		System.out.println("setAddlColumnHeaders  ");
		System.out.println("addlHeadersCount=" + addlHeadersCount);

		//centerBaseColHeaders = new String[] {"Description","Author","Subportfolio","Application","Reviewer","Status"};
		centerBaseColHeaders = new String[] {"RootSysLoginID","UserName","LeadID","ActiveState", "Privilege"};
	}

	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor editor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo){
		UserMaintReqPojo userMaintReqPojo = (UserMaintReqPojo) inItemPojo;

		editor = new TableEditor(inTable);
		Text userShortID_Tx = new Text(inTable, SWT.READ_ONLY);
		userShortID_Tx.setText(userMaintReqPojo.userPojo.rootSysLoginID);
		editor.grabHorizontal = true;
		editor.setEditor(userShortID_Tx, inTableItem, ++inLastColLocation);
		inTableItem.setText(inLastColLocation, userShortID_Tx.getText());
		
		editor = new TableEditor(inTable);
		Text userName_Tx = new Text(inTable, SWT.READ_ONLY);
		userName_Tx.setText(userMaintReqPojo.userPojo.userName);
		editor.grabHorizontal = true;
		editor.setEditor(userName_Tx, inTableItem, ++inLastColLocation);
		inTableItem.setText(inLastColLocation, userName_Tx.getText());
	
		editor = new TableEditor(inTable);
		Text leadID_Tx = new Text(inTable, SWT.READ_ONLY);
		leadID_Tx.setText(userMaintReqPojo.userPojo.leadID);
		editor.grabHorizontal = true;
		editor.setEditor(leadID_Tx, inTableItem, ++inLastColLocation);		
		inTableItem.setText(inLastColLocation, leadID_Tx.getText());

		editor = new TableEditor(inTable);
		Text activeState_Tx = new Text(inTable, SWT.READ_ONLY);
		activeState_Tx.setText(userMaintReqPojo.userPojo.activeStatus);
		editor.grabHorizontal = true;
		editor.setEditor(activeState_Tx, inTableItem, ++inLastColLocation);		
		inTableItem.setText(inLastColLocation, activeState_Tx.getText());

		editor = new TableEditor(inTable);
		Text privilege_Tx = new Text(inTable, SWT.READ_ONLY);
		privilege_Tx.setText(UserPojo.getPrivilegeLitOfLevel(userMaintReqPojo.userPojo.privilegeLevel));				
		editor.grabHorizontal = true;
		editor.setEditor(privilege_Tx, inTableItem, ++inLastColLocation);
		inTableItem.setText(inLastColLocation, privilege_Tx.getText());
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup,Group inPrevGroup,FormData formData,ItemPojo itemPojo){

		UserMaintReqPojo userMaintReqPojo = (UserMaintReqPojo) itemPojo;

		Group userInfo = new Group(itemContentGroup, SWT.LEFT);
		userInfo.setText("RootSysLoginID");
		userInfo.setLayout(new FillLayout());
		Text userShortIDText = new Text(userInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		userShortIDText.setText(userMaintReqPojo.userPojo.rootSysLoginID);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		userInfo.setLayoutData(formData);
		inPrevGroup = userInfo;

		Group userNameInfo = new Group(itemContentGroup, SWT.LEFT);
		userNameInfo.setText("UserName");
		userNameInfo.setLayout(new FillLayout());
		Text userNameText = new Text(userNameInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		userNameText.setText(userMaintReqPojo.userPojo.userName);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		userNameInfo.setLayoutData(formData);
		inPrevGroup = userNameInfo;

		Group leadIDInfo = new Group(itemContentGroup, SWT.LEFT);
		leadIDInfo.setText("LeadID");
		leadIDInfo.setLayout(new FillLayout());
		Text leadIDText = new Text(leadIDInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		leadIDText.setText(userMaintReqPojo.userPojo.leadID);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		userNameInfo.setLayoutData(formData);
		inPrevGroup = userNameInfo;

		Group activeStateInfo = new Group(itemContentGroup, SWT.LEFT);
		activeStateInfo.setText("LeadID");
		activeStateInfo.setLayout(new FillLayout());
		Text activeStateText = new Text(activeStateInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		activeStateText.setText(userMaintReqPojo.userPojo.activeStatus);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		activeStateInfo.setLayoutData(formData);
		inPrevGroup = activeStateInfo;

		Group privilegeInfo = new Group(itemContentGroup, SWT.LEFT);
		privilegeInfo.setText("Privilege");
		privilegeInfo.setLayout(new FillLayout());
		Text privilegeText = new Text(privilegeInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		privilegeText.setText(UserPojo.getPrivilegeLitOfLevel(userMaintReqPojo.userPojo.privilegeLevel));
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		privilegeInfo.setLayoutData(formData);
		inPrevGroup = privilegeInfo;
		
		return inPrevGroup;
	}
	
	//public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
	//	UserItemPojo userMaintReqPojo = (UserItemPojo) inItemPojo;
	//	userMaintReqPojo.application = applicationText.getText();
	//	userMaintReqPojo.status = statusText.getText();
	//	userMaintReqPojo.reviewer = reviewerText.getText();
	//	userMaintReqPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
	//}
	//
	//public void setInitialItemPojoAddlFields(ItemPojo inItemPojo){
	//	UserItemPojo userMaintReqPojo = (UserItemPojo) inItemPojo;
	//	userMaintReqPojo.author = commonData.getCommons().userName;
	//	userMaintReqPojo.status = "Draft";
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
		return UserMaintReqDoc.class;
	}

	@Override
	public Class getPrimerDocClass() {
		return UserMaintReqListingDoc.class;
	}
	
	public void setInitialContent(String inRelevance, String inContentType, GenericGrouperDocPojo inDocumentToUpdate) {
	// Appends users into the given doc
		int usersCnt = 0;
		for (UserPojo userPojo : commonData.getUsersHandler().getUserDetails()) {
			usersCnt++;
			UserMaintReqPojo userMaintReqPojo = new UserMaintReqPojo(userPojo);
			userMaintReqPojo.itemID = commonData.getCommons().userName + commonData.getCommons().getCurrentTimeStamp() + "x" + usersCnt;
			userMaintReqPojo.contentType = inContentType;
			userMaintReqPojo.relevance = inRelevance;
			userMaintReqPojo.artifactName = userPojo.rootSysLoginID;

			inDocumentToUpdate.absorbIncomingItemPojo(userMaintReqPojo);
		}
	}
	
	public void additionalRollAddWebProcess(ItemPojo inItemPojo) {
	// update users database
		System.out.println("catelogPersistenceManager is " + catelogPersistenceManager);
		System.out.println("inItemPojo is " + inItemPojo);
		System.out.println("((UserItemPojo) inItemPojo).userPojo is " + ((UserMaintReqPojo) inItemPojo).userPojo);
		catelogPersistenceManager.replaceUser(((UserMaintReqPojo) inItemPojo).userPojo);		
	}
	

	@Override
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo userRqMaintListingDoc = new UserMaintReqListingDoc();
		return userRqMaintListingDoc;
	}

	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
		System.out.println("at1 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at1 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at1 getBaseDoc for itemID title" + inItemPojo.title);


		GenericItemDocPojo userRqMaintItemDoc = new UserMaintReqDoc(inItemPojo);

		System.out.println("at2 getBaseDoc for itemID " + inItemPojo.itemID);
		System.out.println("at2 getBaseDoc for itemID relevance" + inItemPojo.relevance);
		System.out.println("at2 getBaseDoc for itemID title" + inItemPojo.title);

		System.out.println("at3 getBaseDoc for doc " + userRqMaintItemDoc);
		System.out.println("at3 getBaseDoc for item " + userRqMaintItemDoc.getItem());
		System.out.println("at3 getBaseDoc for itemID " + userRqMaintItemDoc.getItem().itemID);
		System.out.println("at3 getBaseDoc for itemID relevance" + userRqMaintItemDoc.getItem().relevance);
		System.out.println("at3 getBaseDoc for itemID title" + userRqMaintItemDoc.getItem().title);
		return userRqMaintItemDoc;		
	}

	@Override
	public void additionalRibbonButtons() {
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
	public UserMaintReqListingDoc getPrimerDoc() {
		return (UserMaintReqListingDoc) primerDoc;
	}
}