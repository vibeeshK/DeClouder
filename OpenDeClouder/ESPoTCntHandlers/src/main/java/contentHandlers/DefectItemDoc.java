package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class DefectItemDoc extends GenericItemDocPojo {
	/*
	 * json doc holder for Defect item
	 */	
	DefectItemPojo defectItemPojo;

	public DefectItemDoc(ItemPojo inItemPojo) {
		super(inItemPojo);
	}
	
	public DefectItemPojo getAllocatedTasksPojo() {
		return defectItemPojo;
	}

	public void initializeItem() {
		System.out
				.println("@@123 From DefectItemDoc item initilization");
		if (defectItemPojo == null) {
			defectItemPojo = new DefectItemPojo(0);
		}
		defectItemPojo.initializeAdditionalItemPojoFields();
	}

	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
		defectItemPojo = (DefectItemPojo) inItemPojo;
	}

	@Override
	public ItemPojo getItem() {
		return defectItemPojo;
	}

	@Override
	public void setItem(ItemPojo inItemPojo) {
		defectItemPojo = (DefectItemPojo) inItemPojo;
	}
}