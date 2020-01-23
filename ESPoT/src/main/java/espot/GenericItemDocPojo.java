package espot;

public abstract class GenericItemDocPojo implements  PrimerDocInterface{
	/*
	 * Data holder base for Non-grouping type content persistence
	 */

	public GenericItemDocPojo(ItemPojo inItemPojo){
		setItem(inItemPojo);
		System.out.println("after setItem for getItem()" + getItem() );
		System.out.println("after setItem for itemID " + getItem().itemID);
		System.out.println("after setItem for itemID relevance" + getItem().relevance);
		System.out.println("after setItem for itemID title" + getItem().title);

	}

	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		setItem(inItemPojo);
	}
	public abstract void setItem(ItemPojo inItemPojo);
	public abstract ItemPojo getItem();
	public abstract void initializeItem();
}

