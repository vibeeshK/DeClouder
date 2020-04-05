package espot;

import java.io.InputStream;

public class RequestProcesserPojo {
	/*
	 * Data holder for the requests coming from clients to the ESPoT server processor
	 */

	public RequestPojo requestPojo;
	public ERLpojo prevERLPojo;
	public ERLpojo newERLPojo;
	public ContentHandlerSpecs contentHandlerSpecs;
	private boolean erlAlreadyExists = false;
	public String updatedContentFileLocation;
	public transient InputStream updatedContentInputStream = null;
	public String incomingContentFullPath = null;
	public boolean artifactToBeUpdatedForRemarkFields = false;

	// holding item values to pass to the grouper handler
	public String itemReassignedRequestor;
	public String itemReassignedAuthor;	
	public String itemNewERLStatus;

	public RequestProcesserPojo(RequestPojo inRequestPojo) {
		requestPojo = inRequestPojo;
		artifactToBeUpdatedForRemarkFields = false;
		itemReassignedRequestor = "";
		itemReassignedAuthor = "";
		itemNewERLStatus = "";
	}

	public boolean doesERLAlreadyExist() {
		if (prevERLPojo != null) {
			erlAlreadyExists = true;
		} else {
			erlAlreadyExists = false;
		}
		return erlAlreadyExists;
	}
}