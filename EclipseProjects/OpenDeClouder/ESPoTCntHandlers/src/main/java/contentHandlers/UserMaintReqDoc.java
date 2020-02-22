package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class UserMaintReqDoc extends GenericItemDocPojo  {
	/*
	 * json doc holder for the User item
	 */	
	UserMaintReqPojo userMaintReqPojo;

	public UserMaintReqDoc(ItemPojo inItemPojo){
		super(inItemPojo);
	}
	
	public UserMaintReqPojo getUserItemPojo() {
		return userMaintReqPojo;
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		userMaintReqPojo = (UserMaintReqPojo) inItemPojo;
	}
	
	@Override
	public ItemPojo getItem() {
		return userMaintReqPojo;
	}
	
	@Override
	public void initializeItem() {
		System.out
		.println("@@123 From UserItemDoc initilization");
		if (userMaintReqPojo == null) {
			userMaintReqPojo = new UserMaintReqPojo(0);
		}		
		userMaintReqPojo.initializeAdditionalItemPojoFields();
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		userMaintReqPojo = (UserMaintReqPojo) inItemPojo;
		System.out.println("at setItem for inItemPojo " + inItemPojo );
		System.out.println("at setItem for itemID " + userMaintReqPojo.itemID);
		System.out.println("at setItem for itemID relevance" + userMaintReqPojo.relevance);
		System.out.println("at setItem for itemID title" + userMaintReqPojo.title);
	}
}