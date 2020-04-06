package espot;

import java.util.ArrayList;

public abstract class GenericGrouperDocPojo implements PrimerDocInterface{
	/*
	 * Data holder base for Grouping type content persistence
	 */

	int maxItemNumber;	// its necessary to hold an independent maxNum outside mere list size since
						// an intermediate item can get deleted and a valid gap can be formed resulting
						// in a difference between max itemnum and list size
	
	public GenericGrouperDocPojo(){
		createItemList();		
	}

	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {

		if (getItemList()==null) {
			createItemList();
		//maxItemNumber = -1;
		}
		if (getItemList().size() == 0) {
			maxItemNumber = -1;
		}
		int itemLocation = getItemsCurrentLocation(inItemPojo);
		if (itemLocation!=-1) {	// item location and itemNum can be different especially for DeckerProcessor
								// where deletion is allowed resulting in gaps in itemNumbers
			setItem(itemLocation,inItemPojo);
		} else {
			inItemPojo.itemNumber = ++maxItemNumber;
			addItem(inItemPojo);
		}
	}
	public abstract ArrayList<?> getItemList();
	public abstract ArrayList<?> createItemList();
	public int getItemsCurrentLocation(ItemPojo inItemPojo) {
		int location = -1;
 		ArrayList<?> items = getItemList();
		for (int itemlocn=0; itemlocn<items.size();itemlocn++) {
			ItemPojo itemPojo = (ItemPojo) items.get(itemlocn);
			if (itemPojo.equals(inItemPojo)) {
				location = itemlocn;
				break;
			}			
		}		
		return location;
	}

	//public ItemPojo getItemByChildArtifactName(String inChildRelevance, String inChildArtifactName) {
	//	ItemPojo childItemPojo = null;
	//	ArrayList<?> items = getItemList();
	//	for (int itemLocation=0; itemLocation<items.size();itemLocation++) {
	//		ItemPojo itemPojo = (ItemPojo) items.get(itemLocation);
	//		if (itemPojo.artifactName.equals(inChildArtifactName) 
	//				&& itemPojo.relevance.equals(inChildRelevance)) {
	//			childItemPojo = itemPojo;
	//			break;
	//		}
	//	}
	//	return childItemPojo;
	//}
	
	public ItemPojo getItemByChildArtifactName(String inChildRelevance, String inChildArtifactName, String inChildContentType) {
		ItemPojo childItemPojo = null;
 		ArrayList<?> items = getItemList();
		for (int itemLocation=0; itemLocation<items.size();itemLocation++) {
			ItemPojo itemPojo = (ItemPojo) items.get(itemLocation);
			if (itemPojo.artifactName.equals(inChildArtifactName) 
					&& itemPojo.relevance.equals(inChildRelevance)
					&& itemPojo.contentType.equals(inChildContentType)) {
				childItemPojo = itemPojo;
				break;
			}
		}
		return childItemPojo;
	}
	
	public void clearList() {
		maxItemNumber = -1;
 		ArrayList<?> items = getItemList();
 		items.clear();
	}

	public ItemPojo getItemByItemNumber(int inItemNum) {
		ItemPojo childItemPojo = null;
 		ArrayList<?> items = getItemList();
		for (int itemLocation=0; itemLocation<items.size();itemLocation++) {
			ItemPojo itemPojo = (ItemPojo) items.get(itemLocation);
			if (itemPojo.itemNumber == inItemNum) {
				childItemPojo = itemPojo;
				break;
			}			
		}		
		return childItemPojo;
	}

	public void removeItemByItemNumber(int inItemNum) {
 		ArrayList<?> items = getItemList();
		ItemPojo item = getItemByItemNumber(inItemNum);
		items.remove(item);
	}
	
	public abstract void setItem(int inCurrentLocation,ItemPojo inItemPojo);
	public abstract void setItemList(ArrayList<?> inItemList);
	public abstract void addItem(ItemPojo inItemPojo);	
}