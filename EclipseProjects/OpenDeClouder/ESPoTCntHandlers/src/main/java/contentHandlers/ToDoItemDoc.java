package contentHandlers;

import espot.GenericItemDocPojo;
import espot.ItemPojo;

public class ToDoItemDoc extends GenericItemDocPojo {
	/*
	 * json doc holder for ToDo item
	 */
	ToDoPojo toDoPojo;
	public ToDoItemDoc(ItemPojo inItemPojo){
		super(inItemPojo);
	}
	
	public ToDoPojo getToDoPojo() {
		return toDoPojo;
	}

	public void setToDoPojo(ToDoPojo toDoPojo) {
		this.toDoPojo = toDoPojo;
	}
	
	@Override
	public void absorbIncomingItemPojo(ItemPojo inItemPojo) {
	toDoPojo = (ToDoPojo) inItemPojo;
	}
	
	@Override
	public ItemPojo getItem() {
		return toDoPojo;
	}
	
	@Override
	public void initializeItem() {
		System.out
		.println("@@123 From ToDoItemDoc item initilization");
		if (toDoPojo == null) {
			toDoPojo = new ToDoPojo(0);
		}		
		toDoPojo.initializeAdditionalItemPojoFields();
	}
	
	@Override
	public void setItem(ItemPojo inItemPojo) {
		toDoPojo = (ToDoPojo) inItemPojo;
	}
}