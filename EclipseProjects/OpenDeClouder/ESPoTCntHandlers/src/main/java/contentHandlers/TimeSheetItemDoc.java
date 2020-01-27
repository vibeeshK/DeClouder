package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class TimeSheetItemDoc extends GenericItemDocPojo  {
	/*
	 * json doc holder for Time sheet item
	 */
	TimeSheetPojo timeSheetPojo;
	public TimeSheetItemDoc(ItemPojo inItemPojo){
		super(inItemPojo);
		System.out.println("at TimeSheetItemDoc for timeSheetPojo " + timeSheetPojo);
		System.out.println("at TimeSheetItemDoc for itemID " + timeSheetPojo.itemID);
	}
	
	public TimeSheetPojo getTimeSheetPojo() {
		return timeSheetPojo;
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		timeSheetPojo = (TimeSheetPojo) inItemPojo;
	}
	@Override
	public ItemPojo getItem() {
		return timeSheetPojo;
	}
	@Override
	public void initializeItem() {
		System.out
		.println("@@123 From TimeSheetItemDoc initilization");
		if (timeSheetPojo == null) {
			timeSheetPojo = new TimeSheetPojo(0);
		}		
		timeSheetPojo.initializeAdditionalItemPojoFields();
	}
	@Override
	public void setItem(ItemPojo inItemPojo) {
		timeSheetPojo = (TimeSheetPojo) inItemPojo;
		System.out.println("at setItem for inItemPojo " + inItemPojo );
		System.out.println("at setItem for itemID " + timeSheetPojo.itemID);
		System.out.println("at setItem for itemID relevance" + timeSheetPojo.relevance);
		System.out.println("at setItem for itemID title" + timeSheetPojo.title);

	}
}