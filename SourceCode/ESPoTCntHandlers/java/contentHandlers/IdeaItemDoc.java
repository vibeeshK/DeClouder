package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class IdeaItemDoc extends GenericItemDocPojo  {
	/*
	 * json doc holder for the Idea item
	 */	
	IdeaPojo ideaPojo;

	public IdeaItemDoc(ItemPojo inItemPojo){
		super(inItemPojo);
	}
	
	public IdeaPojo getIdeaPojo() {
		return ideaPojo;
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		ideaPojo = (IdeaPojo) inItemPojo;
	}
	
	@Override
	public ItemPojo getItem() {
		return ideaPojo;
	}
	
	@Override
	public void initializeItem() {
		System.out
		.println("@@123 From IdeaItemDoc initilization");
		if (ideaPojo == null) {
			ideaPojo = new IdeaPojo(0);
		}		
		ideaPojo.initializeAdditionalItemPojoFields();
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		ideaPojo = (IdeaPojo) inItemPojo;
		System.out.println("at setItem for inItemPojo " + inItemPojo );
		System.out.println("at setItem for itemID " + ideaPojo.itemID);
		System.out.println("at setItem for itemID relevance" + ideaPojo.relevance);
		System.out.println("at setItem for itemID title" + ideaPojo.title);
	}
}