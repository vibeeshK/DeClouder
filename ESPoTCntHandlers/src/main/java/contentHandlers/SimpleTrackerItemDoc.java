package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class SimpleTrackerItemDoc extends GenericItemDocPojo {
	/*
	 * json doc holder for the Simple Tracker
	 */	

	public SimpleTrackerItemDoc(ItemPojo inItemPojo) {
		super(inItemPojo);
	}

	SimpleTrackerPojo simpleTrackerPojo;

	public SimpleTrackerPojo getSimpleTrackerPojo() {
		return simpleTrackerPojo;
	}

	public void initializeItem() {
		System.out.println("@@123 From SimpleTrackerDoc item initilization");
		if (simpleTrackerPojo == null) {
			simpleTrackerPojo = new SimpleTrackerPojo(0);
		}
		simpleTrackerPojo.initializeAdditionalItemPojoFields();
	}

	public void setSimpleTrackerPojo(SimpleTrackerPojo simpleTrackerPojo) {
		this.simpleTrackerPojo= simpleTrackerPojo;
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		simpleTrackerPojo = (SimpleTrackerPojo) inItemPojo;
	}

	@Override
	public ItemPojo getItem() {
		return simpleTrackerPojo;
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		simpleTrackerPojo = (SimpleTrackerPojo) inItemPojo;
	}
}