package espot;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class UsersHandler {

	private ArrayList<UserPojo> usersList = null;
	private String[] usersNamesStrings = null;
	private String[] usersShortNames = null;
	CatelogPersistenceManager catelogPersistenceManager = null;
	Commons commons = null;
	//private static UsersHandler usersHandler = null; // removed singleton to avoid building up of unknown users
	private String rootNick = null;
	
	private UsersHandler(CatelogPersistenceManager inCatelogPersistenceManager,
			Commons inCommons) {
		catelogPersistenceManager  = inCatelogPersistenceManager;
		rootNick = catelogPersistenceManager.rootPojo.rootNick;
		commons = inCommons;
		usersList = catelogPersistenceManager.readUsersList();
		System.out.println("usersList.size() " + usersList.size());
		System.out.println("usersList.get(0).shortId " + usersList.get(0).shortId);
		System.out.println("usersList.get(readUsersList.size-1).shortId " + usersList.get(usersList.size()-1).shortId);
		creategetUsersNamesStrings();	// set up username strings
	}
	
	public int appendUserPojo(UserPojo inUserPojo){
		int insertedUserLocation = usersList.size();	// insert at the end of the list
		usersList.add(insertedUserLocation,inUserPojo);
		creategetUsersNamesStrings(); //  refresh the names lists with new name
		return insertedUserLocation;
	}
	
	public static UsersHandler createInstance(CatelogPersistenceManager inCatelogPersistenceManager,
			Commons inCommons){
		UsersHandler usersHandler = new UsersHandler(inCatelogPersistenceManager, inCommons);
		return usersHandler;
	}

	public String[] getUsersNamesStrings(){
		if (usersNamesStrings == null) {
			creategetUsersNamesStrings();
		}
		return usersNamesStrings;
	}
	
	public void creategetUsersNamesStrings(){
		System.out.println("at creategetUsersNamesStrings ");
		
		usersNamesStrings = new String[usersList.size()];
		usersShortNames = new String[usersList.size()];
		for (int userCount = 0; userCount<usersList.size(); userCount++) {
			usersNamesStrings[userCount] = usersList.get(userCount).getDisplayString();
			usersShortNames[userCount] = usersList.get(userCount).shortId.toUpperCase();
			System.out.println("at creategetUsersNamesStrings usersShortNames[" + userCount + "]: " + usersShortNames[userCount]);
		}
	}
	public String getUserShortnameByIndex(int userIndex){
		return usersList.get(userIndex).shortId;
	}
	public int getIndexOfUserShortId(String userShortId){
		int userIndex = -1;
		System.out.println("at start of getIndexOfUserShortId : " + userShortId);
		System.out.println("in getIndexOfUserShortId usersShortNames length : " + usersShortNames);
		System.out.println("in getIndexOfUserShortId usersShortNames[0] : " + usersShortNames[0]);
		System.out.println("in getIndexOfUserShortId usersShortNames[1] : " + usersShortNames[1]);
		userIndex = commons.getIndexOfStringInArray(userShortId.toUpperCase(), usersShortNames);
		return userIndex;
	}
	public UserPojo getUserDetailsFromShortId(String userShortId){
		int userIndex = -1;
		UserPojo userDetails = null;
		System.out.println("at start of getIndexOfUserShortId : " + userShortId);
		System.out.println("in getIndexOfUserShortId usersShortNames: " + usersShortNames);
		System.out.println("in getIndexOfUserShortId usersShortNames length : " + usersShortNames.length);
		System.out.println("in getIndexOfUserShortId usersShortNames[0] : " + usersShortNames[0]);
		System.out.println("in getIndexOfUserShortId usersShortNames[1] : " + usersShortNames[1]);
		userIndex = commons.getIndexOfStringInArray(userShortId.toUpperCase(), usersShortNames);
		if (userIndex != -1) {
			userDetails = usersList.get(userIndex);
		}
		return userDetails;
	}
	public int getUsersCount(){
		return usersList.size();
	}
}