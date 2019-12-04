package contentHandlers;

import java.util.Date;
import java.util.HashMap;
import espot.ItemPojo;

public class DeckerLiteItemPojo extends ItemPojo {
	/*
	 * json doc holder for a decker item
	 */

	//defining this class here as there is no corresponding
	public Date deckingCompletedAt;
//	public String itemSummaryFile;
//	public String summaryShKeyColVal;
	public HashMap<String,String> addlFieldValues;
	public String itemDetailFileInRelevancePath;
	public int numberOfRecsCombined;

	DeckerLiteItemPojo(int inItemNumber) {
		super(inItemNumber);
	}
	DeckerLiteItemPojo(String inContentType, String inRelevance, String inArtifactName) {
		super(-1);
		contentType = inContentType;
		relevance = inRelevance;
		artifactName = inArtifactName;
		deckingCompletedAt = null;
		numberOfRecsCombined = 0;
//		itemSummaryFile = "";
//		summaryShKeyColVal = "";
	}
	void absorbScreenFieldValues(HashMap<String,String> inScreenFieldValues){
		addlFieldValues = new HashMap<String,String>();
		for (String screenHdr : inScreenFieldValues.keySet()){
			addlFieldValues.put(screenHdr,inScreenFieldValues.get(screenHdr));
		}
	}
}