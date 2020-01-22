package contentHandlers;

import java.util.ArrayList;
import espot.GenericGrouperDocPojo;
import espot.ItemPojo;

public class ImpedimentsListDoc extends GenericGrouperDocPojo {
	/*
	 * json doc holder for Impediment grouper
	 */	
	ArrayList<ImpedimentItemPojo> impedimentList;

	public ArrayList<ImpedimentItemPojo> getItemList() {
		return impedimentList;
	}
	public ArrayList<ImpedimentItemPojo> createItemList() {
		impedimentList = new ArrayList<ImpedimentItemPojo>();
		return impedimentList;
	}

	public void setItem(int inCurrentLocation,ItemPojo inItemPojo){
		impedimentList.set(inCurrentLocation, (ImpedimentItemPojo) inItemPojo);
	}

	public void addItem(ItemPojo inItemPojo) {
		impedimentList.add((ImpedimentItemPojo) inItemPojo);
	}
	
	public void setItemList(ArrayList<?> inItemPojoList) {
		impedimentList = (ArrayList<ImpedimentItemPojo>) inItemPojoList;
	}		
}