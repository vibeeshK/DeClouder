package contentHandlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import espot.ArtifactKeyPojo;
import espot.CommonData;
import espot.Commons;
import espot.GenericItemHandler;
import espot.ItemPojo;
import espot.UserPojo;
import espot.UsersDisplay;

public class UserMaintReq extends GenericItemHandler {
	/*
	 * This content handler helps to let any team member to log the an user
	 * which will be collated into the grouping content type by the server.
	 */
//	public static final int PREFERED_DESC_WIDTH = 600;
//	public static final int PREFERED_DESC_HEIGHT = 100;

	Text rootSysLoginIDText;
	Text userNameText;
	//Text leadIDText;
	UsersDisplay leadIDDisplay;
	
	CCombo activeStatesList;

	CCombo privilegeList;
	private final static String[] PRIVILEGE_LIST_LITs = new String[]{UserPojo.ADMIN_LEVEL_LIT,UserPojo.TEAMMLEADER_LEVEL_LIT,UserPojo.TEAMMEMBER_LEVEL_LIT};
	private final static String[] ACTIVESTATE_LIST_LITs = new String[]{UserPojo.ACTIVESTAT_ACTIVE, UserPojo.ACTIVESTAT_INACTIVE};
	

	public final static String userItemPreValidation(CommonData inCommonData,ArtifactKeyPojo inArtifactKeyPojo){
		String validateString = "";

		if (!inCommonData.getUsersHandler().getUserDetailsFromRootSysLoginID(inCommonData.getCommons().userName).hasAdminPrivilege()){
			validateString = "You need admin privilege to maintain UserItem";
		} else if (!inArtifactKeyPojo.relevance.equalsIgnoreCase(inCommonData.getCommons().adminBranchRelevance)) {
			validateString = "For UserItem, Relevance must be " + inCommonData.getCommons().adminBranchRelevance;
		}
		
		return validateString;
	}

	public String prevalidate(CommonData inCommonData,ArtifactKeyPojo inArtifactKeyPojo){		
		return userItemPreValidation(inCommonData,inArtifactKeyPojo);
	}

	public ItemPojo getItemPojo(int itemCount){
		ItemPojo userMaintReqPojo = new UserMaintReqPojo(itemCount);
		return userMaintReqPojo;
	}

	@Override
	public void setInitialItemPojoAddlFields() {
		UserMaintReqPojo userMaintReqPojo = (UserMaintReqPojo) primerDoc.getItem();
	}

	public void checkSetNewItemID() {
		UserMaintReqPojo userMaintReqPojo = (UserMaintReqPojo) primerDoc.getItem();
		if (userMaintReqPojo.itemID.equalsIgnoreCase("")) {
			userMaintReqPojo.itemID = userMaintReqPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){

		UserMaintReqPojo userMaintReqPojo = (UserMaintReqPojo) itemPojo;

		Group userShortIDInfo = new Group(itemContentGroup, SWT.LEFT);
		userShortIDInfo.setText("LoginID");
		userShortIDInfo.setLayout(new FillLayout());
		
		if (userMaintReqPojo.itemNumber == -1 && invokedForEdit) {
		// ShortID is restricted for changes after initial set up, as it is the key identifier
			rootSysLoginIDText = new Text(userShortIDInfo, SWT.WRAP | SWT.CENTER);			
		} else {
			rootSysLoginIDText = new Text(userShortIDInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		}
		rootSysLoginIDText.setText(userMaintReqPojo.userPojo.rootSysLoginID);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		userShortIDInfo.setLayoutData(formData);
		inPrevGroup = userShortIDInfo;

		Group userNameInfo = new Group(itemContentGroup, SWT.LEFT);
		userNameInfo.setText("UserName");
		userNameInfo.setLayout(new FillLayout());
		if (invokedForEdit) {
			userNameText = new Text(userNameInfo, SWT.WRAP | SWT.CENTER);
		} else {
			userNameText = new Text(userNameInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		}
		userNameText.setText(userMaintReqPojo.userPojo.userName);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);		
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		userNameInfo.setLayoutData(formData);
		inPrevGroup = userNameInfo;

		Group leadIDInfo = new Group(itemContentGroup, SWT.LEFT);
		//leadIDInfo.setText("");
		leadIDInfo.setLayout(new FillLayout());
		//if (invokedForEdit) {
		//	leadIDText = new Text(leadIDInfo, SWT.WRAP | SWT.CENTER);
		//} else {
		//	leadIDText = new Text(leadIDInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);		
		//}
		//leadIDText.setText(userMaintReqPojo.userPojo.leadID);

		leadIDDisplay = new UsersDisplay(commonData.getUsersHandler(),leadIDInfo,userMaintReqPojo.userPojo.leadID,invokedForEdit,UsersDisplay.LEAD_LIT);

		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);		
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		leadIDInfo.setLayoutData(formData);
		inPrevGroup = leadIDInfo;

		Group activeStateIDInfo = new Group(itemContentGroup, SWT.LEFT);
		activeStateIDInfo.setText("ActiveState");
		activeStateIDInfo.setLayout(new FillLayout());
		//if (commonData.getUsersHandler().getUserDetailsFromShortId(commons.userName).hasAdminPrivilege()){
		if (invokedForEdit) {
			activeStatesList = new CCombo(activeStateIDInfo, SWT.DROP_DOWN | SWT.CENTER);
		} else {
			activeStatesList = new CCombo(activeStateIDInfo, SWT.DROP_DOWN | SWT.CENTER | SWT.READ_ONLY);
			activeStatesList.setEnabled(false);
		}

		activeStatesList.setItems(ACTIVESTATE_LIST_LITs);
		if (userMaintReqPojo!=null && userMaintReqPojo.userPojo != null && !userMaintReqPojo.userPojo.activeStatus.equalsIgnoreCase("")) {
			activeStatesList.select(activeStatesList.indexOf(userMaintReqPojo.userPojo.activeStatus));
		}
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		activeStateIDInfo.setLayoutData(formData);
		inPrevGroup = activeStateIDInfo;

		Group userPreveledgeInfo = new Group(itemContentGroup, SWT.LEFT);
		userPreveledgeInfo.setText("UserPreveledge");
		userPreveledgeInfo.setLayout(new FillLayout());	
		//if (commonData.getUsersHandler().getUserDetailsFromShortId(commons.userName).hasAdminPrivilege()){
		if (invokedForEdit) {
			privilegeList = new CCombo(userPreveledgeInfo, SWT.DROP_DOWN | SWT.CENTER);
		} else {
			privilegeList = new CCombo(userPreveledgeInfo, SWT.DROP_DOWN | SWT.CENTER | SWT.READ_ONLY);
			privilegeList.setEnabled(false);
		}
		privilegeList.setItems(PRIVILEGE_LIST_LITs);
		if (userMaintReqPojo!=null && userMaintReqPojo.userPojo != null && userMaintReqPojo.userPojo.privilegeLevel>0) {
			privilegeList.select(privilegeList.indexOf(UserPojo.getPrivilegeLitOfLevel(userMaintReqPojo.userPojo.privilegeLevel)));
		}
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);		
		userPreveledgeInfo.setLayoutData(formData);
		inPrevGroup = userPreveledgeInfo;

		return inPrevGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		UserMaintReqPojo userMaintReqPojo = (UserMaintReqPojo) inItemPojo;
		//userMaintReqPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
		userMaintReqPojo.userPojo.rootSysLoginID = rootSysLoginIDText.getText();
		userMaintReqPojo.userPojo.userName = userNameText.getText();
		//userMaintReqPojo.userPojo.leadID = leadIDText.getText();
		userMaintReqPojo.userPojo.leadID = leadIDDisplay.userText.getText();
		
		//if (commonData.getUsersHandler().getUserDetailsFromShortId(commons.userName).hasAdminPrivilege()) {
			if (privilegeList.getSelectionIndex() > -1){
				userMaintReqPojo.userPojo.privilegeLevel = UserPojo.getPrivilegeLevelOfLit(PRIVILEGE_LIST_LITs[privilegeList.getSelectionIndex()]);
			}
			if (activeStatesList.getSelectionIndex() > -1){
				userMaintReqPojo.userPojo.activeStatus = ACTIVESTATE_LIST_LITs[activeStatesList.getSelectionIndex()];
			}
			
		//}
	}
	
	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	public Class getPrimerDocClass() {
		return UserMaintReqDoc.class;
	}

	@Override
	public void testPrinter(String inPrintHead) {
	}
	
	@Override
	public boolean validateAddlScrFields() {
		boolean validationPassFlag = true;
		String scrMsg = "";
		UserMaintReqPojo userMaintReqPojo = (UserMaintReqPojo) primerDoc.getItem();
		if (userMaintReqPojo.itemNumber == -1 && invokedForEdit) {
			
			String enteredRootSysLoginID = rootSysLoginIDText.getText();
			if (enteredRootSysLoginID == null || enteredRootSysLoginID.isEmpty() || enteredRootSysLoginID.contains(" ")) {
				validationPassFlag = false;
				scrMsg = "LoginID shouldn't be blank or contain space";
			} else if (!enteredRootSysLoginID.toUpperCase().equals(enteredRootSysLoginID)) {
				validationPassFlag = false;
				scrMsg = "LoginID should be UPPER CASE. Suggestion: Enter " + enteredRootSysLoginID.toUpperCase()
				+ " instead of " + enteredRootSysLoginID;
			} else if (enteredRootSysLoginID.length() > Commons.LOGINID_MAXLEN) {
				validationPassFlag = false;
				scrMsg = "LoginID length " + enteredRootSysLoginID.length() + " exceeds allowed max length of " + Commons.LOGINID_MAXLEN;
			} else if (commonData.getUsersHandler().getUserDetailsFromRootSysLoginID(enteredRootSysLoginID) != null) {
				validationPassFlag = false;
				scrMsg = "LoginID " + enteredRootSysLoginID + " already exists in the root";				
			}			
		}
		
		//if (validationPassFlag && invokedForEdit) {
		//	
		//	String enteredLeadID = leadIDText.getText();
		//
		//	if (!enteredLeadID.isEmpty() && commonData.getUsersHandler().getUserDetailsFromRootSysLoginID(enteredLeadID) == null) {
		//		//its ok not to have a lead
		//		validationPassFlag = false;
		//		scrMsg = "LeadID " + enteredLeadID + " doesn't exist";				
		//	}			
		//}
		
		if (!validationPassFlag) {
			MessageBox editMessage2Box = new MessageBox(mainShell,
					SWT.ICON_ERROR | SWT.OK);
			editMessage2Box.setMessage(scrMsg);
			editMessage2Box.open();
		}

		return validationPassFlag;
	}
	
	@Override
	public UserMaintReqDoc getPrimerDoc() {
		return (UserMaintReqDoc) primerDoc;
	}

	public UserMaintReqDoc getNewPrimerDoc() {
		return new UserMaintReqDoc(new UserMaintReqPojo(-1));
	}
}