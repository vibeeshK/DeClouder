package contentHandlers;

import java.util.Date;
import java.util.HashMap;
import espot.ItemPojo;

public class DeckerGrouperItemPojo2 extends ItemPojo {
	/*
	 * json doc holder for a decker item
	 */

	//defining this class here as there is no corresponding
	public Date deckingCompletedAt;
	public String itemSummaryFile;
	public String summaryShKeyColVal;
	public HashMap<String,String> addlFieldValues;
	DeckerGrouperItemPojo2(int inItemNumber) {
		super(inItemNumber);
	}
	DeckerGrouperItemPojo2(String inContentType, String inRelevance, String inArtifactName) {
		super(-1);
		contentType = inContentType;
		relevance = inRelevance;
		artifactName = inArtifactName;
		deckingCompletedAt = null;
		itemSummaryFile = "";
		summaryShKeyColVal = "";
	}
	void absorbScreenFieldValues(HashMap<String,String> inScreenFieldValues){
		addlFieldValues = new HashMap<String,String>();
		for (String screenHdr : inScreenFieldValues.keySet()){
			addlFieldValues.put(screenHdr,inScreenFieldValues.get(screenHdr));
		}
	}
}