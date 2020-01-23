package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class ImpedimentItemDoc extends GenericItemDocPojo {
	/*
	 * json doc holder for Impediment item
	 */
	ImpedimentItemPojo impedimentItemPojo;

	public ImpedimentItemDoc(ItemPojo inItemPojo) {
		super(inItemPojo);
	}
	
	public ImpedimentItemPojo getAllocatedTasksPojo() {
		return impedimentItemPojo;
	}

	public void initializeItem() {
		System.out
				.println("@@123 From ImpedimentItemDoc item initilization");
		if (impedimentItemPojo == null) {
			impedimentItemPojo = new ImpedimentItemPojo(0);
		}

		impedimentItemPojo.initializeAdditionalItemPojoFields();
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		impedimentItemPojo = (ImpedimentItemPojo) inItemPojo;
	}

	@Override
	public ItemPojo getItem() {
		return impedimentItemPojo;
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		impedimentItemPojo = (ImpedimentItemPojo) inItemPojo;
	}
}