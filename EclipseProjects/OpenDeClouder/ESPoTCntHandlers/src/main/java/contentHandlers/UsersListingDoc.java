package contentHandlers;

import java.util.ArrayList;

import espot.GenericGrouperDocPojo;
import espot.ItemPojo;

public class UsersListingDoc extends GenericGrouperDocPojo {
	/*
	 * json doc holder for User grouper
	 */	
	ArrayList<UserItemPojo> userItemsList;

	public ArrayList<UserItemPojo> getItemList() {
		return userItemsList;
	}
	public ArrayList<UserItemPojo> createItemList() {
		userItemsList = new ArrayList<UserItemPojo>();
		return userItemsList;
	}
	public void setItem(int inCurrentLocation,ItemPojo inItemPojo){
		userItemsList.set(inCurrentLocation, (UserItemPojo) inItemPojo);
	}
	public void addItem(ItemPojo inItemPojo) {
		userItemsList.add((UserItemPojo) inItemPojo);
	}
	public void setItemList(ArrayList<?> inItemPojoList) {
		userItemsList = (ArrayList<UserItemPojo>) inItemPojoList;
	}	
}
