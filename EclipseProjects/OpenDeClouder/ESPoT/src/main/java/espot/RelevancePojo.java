package espot;

public class RelevancePojo {
	/*
	 * Convenience class to hold each available relevance
	 */	
	public String rootNick;
	public String relevance;
	public boolean RelevancePicked;

	public RelevancePojo(String inRootNick, String inRelevance,
				String inPickedRelevance) {

		System.out.println("inRelevance = " + inRelevance);
		System.out.println("inPickedRelevance = " + inPickedRelevance);
		
		rootNick = inRootNick;
		relevance = inRelevance;
		RelevancePicked = (inPickedRelevance !=null && !inPickedRelevance.isEmpty())? true : false;
		
		System.out.println("RelevancePicked = " + RelevancePicked);
	}
}
