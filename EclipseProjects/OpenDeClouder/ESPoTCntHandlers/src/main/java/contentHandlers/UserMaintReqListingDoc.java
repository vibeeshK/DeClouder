package contentHandlers;

import java.util.ArrayList;

import espot.GenericGrouperDocPojo;
import espot.ItemPojo;

public class UserMaintReqListingDoc extends GenericGrouperDocPojo {
	/*
	 * json doc holder for User grouper
	 */	
	ArrayList<UserMaintReqPojo> userMaintReqItemsList;

	public ArrayList<UserMaintReqPojo> getItemList() {
		return userMaintReqItemsList;
	}
	public ArrayList<UserMaintReqPojo> createItemList() {
		userMaintReqItemsList = new ArrayList<UserMaintReqPojo>();
		return userMaintReqItemsList;
	}
	public void setItem(int inCurrentLocation,ItemPojo inItemPojo){
		userMaintReqItemsList.set(inCurrentLocation, (UserMaintReqPojo) inItemPojo);
	}
	public void addItem(ItemPojo inItemPojo) {
		userMaintReqItemsList.add((UserMaintReqPojo) inItemPojo);
	}
	public void setItemList(ArrayList<?> inItemPojoList) {
		userMaintReqItemsList = (ArrayList<UserMaintReqPojo>) inItemPojoList;
	}	
}
