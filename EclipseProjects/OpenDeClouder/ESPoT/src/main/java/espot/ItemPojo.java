package espot;

import java.util.Date;

public class ItemPojo {
	/*
	 * This item pojo is created only for checking existence againts a rolled up artifact with same key
	 */
	public static ItemPojo getStartUpItemPojo(String inItemID, String inArtifactName, String inContentType, String inRelevance) {
		
		ItemPojo startUpItemPojo = new ItemPojo(-1);
		startUpItemPojo.itemID = inItemID;
		startUpItemPojo.artifactName = inArtifactName;
		startUpItemPojo.contentType = inContentType;		
		startUpItemPojo.relevance = inRelevance;
		startUpItemPojo.updatedAt = null;

		return startUpItemPojo;
	}
	public int itemNumber = 0;
	public String itemID = "";
	public String artifactName = "";
	public String relevance = "";
	public String contentType = "";
	public String title = "";
	public String author = "";
	public String requestor = "";
	public String status = "";
	public Date updatedAt;
	public ItemPojo(int inItemNumber) {
		itemNumber = inItemNumber;
        System.out.println("At super construction of ItemPojo title = " + title);
	}

    public boolean equals(ItemPojo obj) {		//overriding equals and hashcode functions for any sorting and indexing
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        
        System.out.println("At equals equals check for itemID = " + itemID);
        System.out.println("getClass() = " + getClass());
        System.out.println("obj.getClass() = " + obj.getClass());

        ItemPojo other = (ItemPojo) obj;
        if (other.itemID == null || other.contentType == null || other.relevance == null || other.artifactName == null)  return false;

        System.out.println("At equals equals check for other itemID = " + other.itemID);
        
        if (!itemID.equalsIgnoreCase(other.itemID)) return false;
        if (!contentType.equalsIgnoreCase(other.contentType)) return false;
        if (!relevance.equalsIgnoreCase(other.relevance)) return false;
        if (!artifactName.equalsIgnoreCase(other.artifactName)) return false;
        return true;
    }    
    
    public int hashCode() {					
    	//overriding equals and hashcode functions for any sorting and indexing
        final int prime = 31;
        System.out.println("artifactName = " + artifactName);
        System.out.println("itemID = " + itemID);
        System.out.println("contentType = " + contentType);
        System.out.println("relevance = " + relevance);
        return prime + 
        		((artifactName != null)? artifactName.hashCode() : 0) + 
        		((itemID != null)? itemID.hashCode() : 0) + 
        		((contentType != null)? contentType.hashCode() : 0) + 
        		((relevance != null)? relevance.hashCode() : 0);
    }
}