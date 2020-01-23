package contentHandlers;

import java.util.ArrayList;
import espot.GenericGrouperDocPojo;
import espot.ItemPojo;

public class ProjTasksListDoc extends GenericGrouperDocPojo {
	/*
	 * json doc holder for Project Tasks Grouping content
	 */
	ArrayList<ProjTaskItemPojo> projTasksList;

	public ArrayList<ProjTaskItemPojo> getItemList() {
		return projTasksList;
	}
	public ArrayList<ProjTaskItemPojo> createItemList() {
		projTasksList = new ArrayList<ProjTaskItemPojo>();
		return projTasksList;
	}
	public void setItem(int inCurrentLocation,ItemPojo inItemPojo){
		projTasksList.set(inCurrentLocation, (ProjTaskItemPojo) inItemPojo);
	}
	public void addItem(ItemPojo inItemPojo) {
		projTasksList.add((ProjTaskItemPojo) inItemPojo);
	}
	public void setItemList(ArrayList<?> inItemPojoList) {
		projTasksList = (ArrayList<ProjTaskItemPojo>) inItemPojoList;
	}		
}