package espot;

public class ArtifactPojo {
	/*
	 * Provides an object view of all parameters of an artifact
	 */

	public static final String ERLSTAT_DRAFT = "DRAFT";
	public static final String ERLSTAT_DRAFTREQ = "DRAFTREQ";
	public static final String ERLSTAT_DRAFTRQACK = "DRAFTRQACK";
	public static final String ERLSTAT_REVIEWREQ = "REVIEWREQ";
	public static final String ERLSTAT_REVIEWRQACK = "REVIEWRQACK";
	public static final String ERLSTAT_APPROVED = "APPROVED";
	public static final String ERLSTAT_INACTIVE = "INACTIVE";
	
	public static final String[] REQUESTOR_VALID_ACTIONS = {
																ERLSTAT_DRAFTREQ,
																ERLSTAT_REVIEWRQACK,
																ERLSTAT_APPROVED,
																ERLSTAT_INACTIVE
															};
	public static final String[] AUTHOR_VALID_ACTIONS = {
																ERLSTAT_DRAFTRQACK,
																ERLSTAT_DRAFT,
																ERLSTAT_REVIEWREQ
															};
	//public static final String[] LEADER_VALID_ACTIONS = {
	//															ERLSTAT_DRAFTREQ,
	//															ERLSTAT_DRAFTRQACK,
	//															ERLSTAT_DRAFT,
	//															ERLSTAT_REVIEWREQ,
	//															ERLSTAT_REVIEWRQACK,
	//															ERLSTAT_APPROVED,
	//															ERLSTAT_INACTIVE
	//														};
	public static final String[] ADMIN_VALID_ACTIONS = {
																ERLSTAT_DRAFTREQ,
																ERLSTAT_DRAFTRQACK,
																ERLSTAT_DRAFT,
																ERLSTAT_REVIEWREQ,
																ERLSTAT_REVIEWRQACK,
																ERLSTAT_APPROVED,
																ERLSTAT_INACTIVE
															};
	public ArtifactKeyPojo artifactKeyPojo = null;
	public String requestor;
	public boolean hasSpecialHandler;
	public String reviewFileName;
	public String erlStatus = ERLSTAT_DRAFTREQ;		// starting status as default
	public String author;

	//ReviewRq/ReviewRqAck/Approved/DraftReq/DraftRqAck
	
	
	public ArtifactPojo() {
	}
	
	public ArtifactPojo(ArtifactKeyPojo inArtifactKeyPojo,
			String inRequestor, String inAuthor, boolean inHasSpecialHandler, String inReviewFileName, String inERLStatus) {
		System.out.println("setting artifactPojo wiht separate fields");
		System.out.println("setting artifactPojo");
		System.out.println("setting artifactPojo");
		setArtifactPojo(inArtifactKeyPojo,
			inRequestor, inAuthor, inHasSpecialHandler, inReviewFileName, inERLStatus);
	}

	
	public ArtifactPojo(ArtifactPojo inArtifactPojo) {
		System.out.println("setting artifactPojo directly with another inArtifactPojo");
		System.out.println("setting artifactPojo");
		setArtifactPojo(inArtifactPojo.artifactKeyPojo,
			inArtifactPojo.requestor,inArtifactPojo.author,inArtifactPojo.hasSpecialHandler,
			inArtifactPojo.reviewFileName, inArtifactPojo.erlStatus);
	}


//	public ArtifactPojo(String inArtifactName, String inContentType,
//			RootPojo inRootPojo, String inRelevance, String inRequestor, 
//			boolean inHasSpecialHandler, String inReviewFileName,
//			String inParentArtifactName,
//			String inParentContentType, RootPojo inParentRootPojo,
//			String inParentRelevance) {
//		setArtifactPojo(inArtifactName, inContentType, inRootPojo, inRelevance,
//				inRequestor, inHasSpecialHandler, inReviewFileName, inParentArtifactName,
//				inParentContentType, inParentRootPojo, inParentRelevance);
//
//	}
//
//	public void setArtifactPojo(String inArtifactName, String inContentType,
//			RootPojo inRootPojo, String inRelevance, String inRequestor,
//			boolean inHasSpecialHandler, String inReviewFileName, String inParentArtifactName,
//			String inParentContentType, RootPojo inParentRootPojo,
//			String inParentRelevance) {
//		ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo(inRootPojo,
//				inRelevance, inArtifactName, inContentType);
//		ArtifactKeyPojo tempParentArtifactKeyPojo = new ArtifactKeyPojo(
//				inParentRootPojo, inParentRelevance,
//				inParentArtifactName, inParentContentType);
//		setArtifactPojo(tempArtifactKeyPojo, inRequestor, inHasSpecialHandler, inReviewFileName,
//				tempParentArtifactKeyPojo);
//
//	}

//	public ArtifactPojo(String inArtifactName, String inContentType,
//			String inRootNick, String inRootString, String inRelevance,
//			String inRequestor,
//			boolean inHasSpecialHandler, String inReviewFileName,
//			String inParentArtifactName, String inParentContentType,
//			String inParentRootNick, String inParentRootString,
//			String inParentRelevance) {
//
//		setArtifactPojo(inArtifactName, inContentType, inRootNick,
//				inRootString, inRelevance, inRequestor, inHasSpecialHandler, inReviewFileName,
//				inParentArtifactName, inParentContentType, inParentRootNick,
//				inParentRootString, inParentRelevance);
//	}
//
//	public void setArtifactPojo(String inArtifactName, String inContentType,
//			String inRootNick, String inRootString, String inRelevance,
//			String inRequestor, boolean inHasSpecialHandler, String inReviewFileName,
//			String inParentArtifactName, String inParentContentType,
//			String inParentRootNick, String inParentRootString,
//			String inParentRelevance) {
//		ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo(inRootNick,
//				inRootString, inRelevance, inArtifactName, inContentType);
//		ArtifactKeyPojo tempParentArtifactKeyPojo = new ArtifactKeyPojo(
//				inParentRootNick, inParentRootString, inParentRelevance,
//				inParentArtifactName, inParentContentType);
//		setArtifactPojo(tempArtifactKeyPojo, inRequestor, inHasSpecialHandler, inReviewFileName,tempParentArtifactKeyPojo);
//	}

	public void setArtifactPojo(ArtifactKeyPojo inArtifactKeyPojo,
			String inRequestor, String inAuthor, boolean inHasSpecialHandler, String inReviewFileName, String inERLStatus) {
		artifactKeyPojo = inArtifactKeyPojo;
		requestor = (inRequestor != null) ? inRequestor : "";
		hasSpecialHandler = inHasSpecialHandler;
		reviewFileName = inReviewFileName;
		erlStatus = inERLStatus;
		author = inAuthor;

		System.out.println("setting artifactPojo: ContentType = "
				+ artifactKeyPojo.contentType);
		System.out
				.println("artifactPojo: artifactKeyPojo.artifactName = "
						+ artifactKeyPojo.artifactName);
		System.out
		.println("artifactPojo: reviewFileName = "
				+ reviewFileName);
		
		System.out
		.println("artifactPojo: erlStatus = "
				+ erlStatus);
	}
}