package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class AllocatdTaskItemDoc extends GenericItemDocPojo {
	/*
	 * json doc holder for Allocated task item
	 */	
	AllocatdTaskItemPojo allocatedTaskItemPojo;

	public AllocatdTaskItemDoc(ItemPojo inItemPojo) {
		super(inItemPojo);
	}
	
	public AllocatdTaskItemPojo getAllocatedTasksPojo() {
		return allocatedTaskItemPojo;
	}

	public void initializeItem() {
		System.out
				.println("@@123 From AllocatedTasksItemDoc item initilization");
		if (allocatedTaskItemPojo == null) {
			allocatedTaskItemPojo = new AllocatdTaskItemPojo(0);
		}

		allocatedTaskItemPojo.initializeAdditionalItemPojoFields();
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		// TODO Auto-generated method stub
		allocatedTaskItemPojo = (AllocatdTaskItemPojo) inItemPojo;
	}

	@Override
	public ItemPojo getItem() {
		// TODO Auto-generated method stub
		return allocatedTaskItemPojo;
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		// TODO Auto-generated method stub
		allocatedTaskItemPojo = (AllocatdTaskItemPojo) inItemPojo;
	}
}