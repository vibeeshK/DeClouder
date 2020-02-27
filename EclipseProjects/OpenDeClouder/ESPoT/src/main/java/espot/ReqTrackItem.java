package espot;

public class ReqTrackItem {
	/*
	 * Holder of request status data within the request process doc
	 */
	//reqFileName would be the key for this object maintained outside
	public RequestProcesserPojo requestProcesserPojo;
	public boolean artifactMoveComplete;
	public boolean oldestContentVersionArchived;	
	public boolean erlMasterDBUpdated;
	public boolean reqRespFileUpdated;
	public boolean reqArchived;
	public boolean errored;
	public String errorMessage;
	
	public ERLVersioningDocItem erlVersioningDocItem;
	//requestArchived = false; not required as the request would hvae been already archived and 
	//this record would have been removed from hashMap.
	public ReqTrackItem(RequestProcesserPojo inRequestProcesserPojo) {
		requestProcesserPojo = inRequestProcesserPojo;
		artifactMoveComplete = false;
		oldestContentVersionArchived = false;		
		erlMasterDBUpdated = false;
		reqRespFileUpdated = false;
		reqArchived = false;
		erlVersioningDocItem = null;
		errored = false;
		errorMessage = "";
	}
}
