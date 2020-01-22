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

	public RequestProcesserPojo(RequestPojo inRequestPojo) {
		requestPojo = inRequestPojo;
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