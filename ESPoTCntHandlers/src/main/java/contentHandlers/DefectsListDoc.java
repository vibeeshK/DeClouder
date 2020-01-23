package contentHandlers;

import java.util.ArrayList;
import espot.GenericGrouperDocPojo;
import espot.ItemPojo;

public class DefectsListDoc extends GenericGrouperDocPojo {
	/*
	 * json doc holder for the Allocated tasks grouper
	 */	
	ArrayList<DefectItemPojo> defectList;
	public ArrayList<DefectItemPojo> getItemList() {
		return defectList;
	}
	public ArrayList<DefectItemPojo> createItemList() {
		defectList = new ArrayList<DefectItemPojo>();
		return defectList;
	}
	public void setItem(int inCurrentLocation,ItemPojo inItemPojo){
		defectList.set(inCurrentLocation, (DefectItemPojo) inItemPojo);
	}
	public void addItem(ItemPojo inItemPojo) {
		defectList.add((DefectItemPojo) inItemPojo);
	}
	public void setItemList(ArrayList<?> inItemPojoList) {
		defectList = (ArrayList<DefectItemPojo>) inItemPojoList;
	}		
}