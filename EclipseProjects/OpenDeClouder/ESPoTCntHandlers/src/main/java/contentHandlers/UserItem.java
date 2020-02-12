package contentHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import espot.ArtifactKeyPojo;
import espot.CommonData;
import espot.GenericItemHandler;
import espot.ItemPojo;
import espot.UserPojo;

public class UserItem extends GenericItemHandler {
	/*
	 * This content handler helps to let any team member to log the an user
	 * which will be collated into the grouping content type by the server.
	 */
//	public static final int PREFERED_DESC_WIDTH = 600;
//	public static final int PREFERED_DESC_HEIGHT = 100;

	Text userShortIDText;
	Text userNameText;
	Text leadIDText;
	CCombo activeStatesList;

	CCombo privilegeList;
	private final static String[] PRIVILEGE_LIST_LITs = new String[]{UserPojo.ADMIN_LEVEL_LIT,UserPojo.TEAMMLEADER_LEVEL_LIT,UserPojo.TEAMMEMBER_LEVEL_LIT};
	private final static String[] ACTIVESTATE_LIST_LITs = new String[]{UserPojo.ACTIVESTAT_ACTIVE, UserPojo.ACTIVESTAT_INACTIVE};
	

	public final static String userItemPreValidation(CommonData inCommonData,ArtifactKeyPojo inArtifactKeyPojo){
		String validateString = "";

		if (!inCommonData.getUsersHandler().getUserDetailsFromShortId(inCommonData.getCommons().userName).hasAdminPrivilege()){
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
		ItemPojo userItemPojo = new UserItemPojo(itemCount);
		return userItemPojo;
	}

	@Override
	public void setInitialItemPojoAddlFields() {
		UserItemPojo userItemPojo = (UserItemPojo) primerDoc.getItem();
	}

	public void checkSetNewItemID() {
		UserItemPojo userItemPojo = (UserItemPojo) primerDoc.getItem();
		if (userItemPojo.itemID.equalsIgnoreCase("")) {
			userItemPojo.itemID = userItemPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo itemPojo){

		UserItemPojo userItemPojo = (UserItemPojo) itemPojo;

		Group userShortIDInfo = new Group(itemContentGroup, SWT.LEFT);
		userShortIDInfo.setText("ShortID");
		userShortIDInfo.setLayout(new FillLayout());
		
		if (userItemPojo.itemNumber == -1) {
		// ShortID is restricted for changes after initial set up, as it is the key identifier
			userShortIDText = new Text(userShortIDInfo, SWT.WRAP | SWT.CENTER);			
		} else {
			userShortIDText = new Text(userShortIDInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
		}
		userShortIDText.setText(userItemPojo.userPojo.rootSysLoginID);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		userShortIDInfo.setLayoutData(formData);
		inPrevGroup = userShortIDInfo;

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
		leadIDInfo.setText("leadID");
		leadIDInfo.setLayout(new FillLayout());
		leadIDText = new Text(leadIDInfo, SWT.WRAP | SWT.CENTER);
		leadIDText.setText(userItemPojo.userPojo.leadID);
		
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);		
		leadIDInfo.setLayoutData(formData);
		inPrevGroup = leadIDInfo;

		Group activeStateIDInfo = new Group(itemContentGroup, SWT.LEFT);
		activeStateIDInfo.setText("ActiveState");
		activeStateIDInfo.setLayout(new FillLayout());
		if (commonData.getUsersHandler().getUserDetailsFromShortId(commons.userName).hasAdminPrivilege()){
			activeStatesList = new CCombo(activeStateIDInfo, SWT.DROP_DOWN);
		} else {
			activeStatesList = new CCombo(activeStateIDInfo, SWT.DROP_DOWN | SWT.READ_ONLY);
		}

		activeStatesList.setItems(ACTIVESTATE_LIST_LITs);
		if (userItemPojo!=null && userItemPojo.userPojo != null && !userItemPojo.userPojo.activeStatus.equalsIgnoreCase("")) {
			activeStatesList.select(activeStatesList.indexOf(userItemPojo.userPojo.activeStatus));
		}
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);
		activeStateIDInfo.setLayoutData(formData);
		inPrevGroup = activeStateIDInfo;

		Group userPreveledgeInfo = new Group(itemContentGroup, SWT.LEFT);
		userPreveledgeInfo.setText("UserPreveledge");
		userPreveledgeInfo.setLayout(new FillLayout());	
		if (commonData.getUsersHandler().getUserDetailsFromShortId(commons.userName).hasAdminPrivilege()){
			privilegeList = new CCombo(userPreveledgeInfo, SWT.DROP_DOWN);
		} else {
			privilegeList = new CCombo(userPreveledgeInfo, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		privilegeList.setItems(PRIVILEGE_LIST_LITs);
		if (userItemPojo!=null && userItemPojo.userPojo != null && userItemPojo.userPojo.privilegeLevel>0) {
			privilegeList.select(privilegeList.indexOf(UserPojo.getPrivilegeLitOfLevel(userItemPojo.userPojo.privilegeLevel)));
		}
		formData = new FormData();
		formData.top = new FormAttachment(inPrevGroup);		
		userPreveledgeInfo.setLayoutData(formData);
		inPrevGroup = userPreveledgeInfo;

		return inPrevGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		UserItemPojo userItemPojo = (UserItemPojo) inItemPojo;
		userItemPojo.relevance = invokedArtifactPojo.artifactKeyPojo.relevance;
		userItemPojo.userPojo.rootSysLoginID = userShortIDText.getText();
		userItemPojo.userPojo.userName = userNameText.getText();		
		if (commonData.getUsersHandler().getUserDetailsFromShortId(commons.userName).hasAdminPrivilege()) {
			if (privilegeList.getSelectionIndex() > -1){
				userItemPojo.userPojo.privilegeLevel = UserPojo.getPrivilegeLevelOfLit(PRIVILEGE_LIST_LITs[privilegeList.getSelectionIndex()]);
			}
			if (activeStatesList.getSelectionIndex() > -1){
				userItemPojo.userPojo.activeStatus = ACTIVESTATE_LIST_LITs[activeStatesList.getSelectionIndex()];
			}
			
		}
	}
	
	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	public Class getPrimerDocClass() {
		return UserItemDoc.class;
	}

	@Override
	public void testPrinter(String inPrintHead) {
	}
	
	@Override
	public boolean validateAddlScrFields() {
		return true;
	}
	
	@Override
	public UserItemDoc getPrimerDoc() {
		return (UserItemDoc) primerDoc;
	}

	public UserItemDoc getNewPrimerDoc() {
		return new UserItemDoc(new UserItemPojo(-1));
	}
}