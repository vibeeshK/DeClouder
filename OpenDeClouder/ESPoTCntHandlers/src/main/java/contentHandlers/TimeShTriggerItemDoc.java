package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class TimeShTriggerItemDoc extends GenericItemDocPojo {
	/*
	 * json doc holder for Time sheet trigger item
	 */
	TimeShTriggerPojo timeShTriggerPojo;

	public TimeShTriggerItemDoc(ItemPojo inItemPojo) {
		super(inItemPojo);
	}

	public TimeShTriggerPojo getTimeShTriggerPojo() {
		return timeShTriggerPojo;
	}

	public void initializeItem() {
		System.out
				.println("@@123 From TimeShTriggerItemDoc item initilization");
		if (timeShTriggerPojo == null) {
			timeShTriggerPojo = new TimeShTriggerPojo(0);
		}

		timeShTriggerPojo.initializeAdditionalItemPojoFields();
	}

	public void setTimeShTriggerPojo(TimeShTriggerPojo timeShTriggerPojo) {
		this.timeShTriggerPojo = timeShTriggerPojo;
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		timeShTriggerPojo = (TimeShTriggerPojo) inItemPojo;
	}

	@Override
	public ItemPojo getItem() {
		return timeShTriggerPojo;
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		timeShTriggerPojo = (TimeShTriggerPojo) inItemPojo;
	}
}