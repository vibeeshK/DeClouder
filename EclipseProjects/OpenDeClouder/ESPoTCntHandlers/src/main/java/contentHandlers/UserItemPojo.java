package contentHandlers;

import espot.ItemPojo;
import espot.UserPojo;

public class UserItemPojo extends ItemPojo{
	/* 
	 * Data holder for an user item
	 */
	UserPojo userPojo;
	
	UserItemPojo(int inItemNumber){
	// helps to prepare an item at specific location; value set up would be done later by caller
		super (inItemNumber);
		initializeAdditionalItemPojoFields();
	}

	UserItemPojo(UserPojo inUserPojo){
	// additional constructor for special need during initial user add to system
		super (-1);
		userPojo = inUserPojo;
	}
	
	void initializeAdditionalItemPojoFields(){
		System.out.println("initializeAdditionalItemPojoFields at userItemPojo class");
		userPojo = new UserPojo();
	}
}