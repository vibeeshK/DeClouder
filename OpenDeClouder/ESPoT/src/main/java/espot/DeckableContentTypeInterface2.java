package espot;

public interface DeckableContentTypeInterface2 {
	/*
	 * Interface requirements which all Deckable content types shall adhere to
	 */
	public String testOk(String inText);
	public String getDetailFilePath();
	public String getDetailSheetName();
	public String getSummarySheetName();
	public String getSummaryShKeyColumnHdr();
	public String getSummaryShKeyColumnVal();
	public int getSummaryShKeyColSeqNum();
}