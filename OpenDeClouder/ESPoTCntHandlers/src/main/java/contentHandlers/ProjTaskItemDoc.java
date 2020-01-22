package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class ProjTaskItemDoc extends GenericItemDocPojo {
	/*
	 * json doc holder for Proj task item
	 */	
	ProjTaskItemPojo projTaskItemPojo;

	public ProjTaskItemDoc(ItemPojo inItemPojo) {
		super(inItemPojo);
	}
	
	public ProjTaskItemPojo getAllocatedTasksPojo() {
		return projTaskItemPojo;
	}

	public void initializeItem() {
		System.out
				.println("@@123 From AllocatedTasksItemDoc item initilization");
		if (projTaskItemPojo == null) {
			projTaskItemPojo = new ProjTaskItemPojo(0);
		}

		projTaskItemPojo.initializeAdditionalItemPojoFields();
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		projTaskItemPojo = (ProjTaskItemPojo) inItemPojo;
	}

	@Override
	public ItemPojo getItem() {
		return projTaskItemPojo;
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		projTaskItemPojo = (ProjTaskItemPojo) inItemPojo;
	}
}