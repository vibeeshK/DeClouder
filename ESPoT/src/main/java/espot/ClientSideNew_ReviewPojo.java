package espot;

public class ClientSideNew_ReviewPojo extends NewReviewPojo{
	/*
	 * Helper class
	 */
	static final String UPLOADED = "Uploaded";
	static final String TOBEUPLOADED = "ToBeUploaded";

	String processStatus;
	String reqRespFileName;
	
	public ClientSideNew_ReviewPojo(
			Commons inCommons, ArtifactKeyPojo inArtifactKeyPojo,String inItemName,String inReviewer,String inCreatedTime,String inProcessStatus,String inReviewFileName,String inReqRespFileName) {
		super(inCommons, inArtifactKeyPojo,inItemName,inReviewer,inCreatedTime);
		clientSideSpecificInit(
				inProcessStatus,inReviewFileName,inReqRespFileName);
		System.out.println("@@2r3 inArtifactKeyPojo = " + inArtifactKeyPojo);
		System.out.println("@@2r3 inArtifactKeyPojo.relevance = " + inArtifactKeyPojo.relevance);
	}
	public ClientSideNew_ReviewPojo(
			Commons inCommons, ArtifactKeyPojo inArtifactKeyPojo,String inItemName,String inReviewer,String inCreatedTime) {
		super(inCommons, inArtifactKeyPojo,inItemName,inReviewer,inCreatedTime);
	}
	public void captureNewComment(
			String inNewCommentDesc) {
		setReviewID();
		setReviewFileName();
		processStatus = ClientSideNew_ReviewPojo.TOBEUPLOADED;
		description = inNewCommentDesc;
		createNewReviewDocument();
	}
	
	public void clientSideSpecificInit(
			String inProcessStatus,String inReviewFileName,String inReqRespFileName) {
		processStatus = inProcessStatus;
		reviewFileName = inReviewFileName;
		reqRespFileName = (inReqRespFileName != null) ? inReqRespFileName : "";
	}
}