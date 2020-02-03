package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class UserItemDoc extends GenericItemDocPojo  {
	/*
	 * json doc holder for the User item
	 */	
	UserItemPojo userItemPojo;

	public UserItemDoc(ItemPojo inItemPojo){
		super(inItemPojo);
	}
	
	public UserItemPojo getUserItemPojo() {
		return userItemPojo;
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		userItemPojo = (UserItemPojo) inItemPojo;
	}
	
	@Override
	public ItemPojo getItem() {
		return userItemPojo;
	}
	
	@Override
	public void initializeItem() {
		System.out
		.println("@@123 From UserItemDoc initilization");
		if (userItemPojo == null) {
			userItemPojo = new UserItemPojo(0);
		}		
		userItemPojo.initializeAdditionalItemPojoFields();
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		userItemPojo = (UserItemPojo) inItemPojo;
		System.out.println("at setItem for inItemPojo " + inItemPojo );
		System.out.println("at setItem for itemID " + userItemPojo.itemID);
		System.out.println("at setItem for itemID relevance" + userItemPojo.relevance);
		System.out.println("at setItem for itemID title" + userItemPojo.title);
	}
}