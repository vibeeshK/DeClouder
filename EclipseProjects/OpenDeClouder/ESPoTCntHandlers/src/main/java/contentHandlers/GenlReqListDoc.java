package contentHandlers;

import java.util.ArrayList;

import espot.GenericGrouperDocPojo;
import espot.ItemPojo;

public class GenlReqListDoc extends GenericGrouperDocPojo {
	/*
	 * json doc holder for GenlRequest grouper
	 */	
	ArrayList<GenlRequestPojo> genlRequestItemsList;

	public ArrayList<GenlRequestPojo> getItemList() {
		return genlRequestItemsList;
	}
	public ArrayList<GenlRequestPojo> createItemList() {
		genlRequestItemsList = new ArrayList<GenlRequestPojo>();
		return genlRequestItemsList;
	}
	public void setItem(int inCurrentLocation,ItemPojo inItemPojo){
		genlRequestItemsList.set(inCurrentLocation, (GenlRequestPojo) inItemPojo);
	}
	public void addItem(ItemPojo inItemPojo) {
		genlRequestItemsList.add((GenlRequestPojo) inItemPojo);
	}
	public void setItemList(ArrayList<?> inItemPojoList) {
		genlRequestItemsList = (ArrayList<GenlRequestPojo>) inItemPojoList;
	}	
}
