package contentHandlers;

public interface DeckableContentTypeInterface extends DeckerLiteContentTypeInterface {
	/*
	 * Interface requirements which all Deckable content types shall adhere to
	 */
	public String getDetailSheetName();
	public String getSummarySheetName();
	public String getSummaryShKeyColumnHdr();
	public String getSummaryShKeyColumnVal();
	public int getSummaryShKeyColSeqNum();
}