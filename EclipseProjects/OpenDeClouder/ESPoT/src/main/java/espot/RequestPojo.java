package espot;

public class RequestPojo {
	/*
	 * Holder of each request record
	 */
	final static String ARTIFACT = "artifact";
	final static String REVIEW = "review";
	
	public String relevance;
	public String artifactName;
	public String contentType;
	public String requestor;
	public String author;
	public String contentFileName;
	public String erlStatus;
	public String uploadedTimeStamp;
	public String itemName;
	public String reviewID;
	public String artifactOrReview;

	public RequestPojo() {
	}

	public RequestPojo(String inRelevance,
			String inArtifactName, String inItemName, String inERLStatus, String inReviewID, 
			String inContenType, String inRequestor, String inAuthor, String inContentPathFile, 
			String inUploadedTimeStamp, String inArtifactOrReview
			) {
		relevance = inRelevance;
		artifactName = inArtifactName;
		itemName = inItemName;
		erlStatus = inERLStatus;
		reviewID = inReviewID;
		contentType = inContenType;
		requestor = inRequestor;
		author = inAuthor;
		contentFileName = inContentPathFile;
		uploadedTimeStamp = inUploadedTimeStamp;
		artifactOrReview = inArtifactOrReview;
		
		System.out.println("Creating RequestPojo contenType = " + contentType);
	}
}