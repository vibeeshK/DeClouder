package espot;

public class SelfAuthoredArtifactpojo extends ArtifactPojo {
	/*
	 * This class holds details about the draft content
	 */
	final public static String ArtifactStatusDraft = "Draft";
	final public static String ArtifactStatusToBeUploaded = "ToBeUploaded";
	final public static String ArtifactStatusUploaded = "Uploaded";
	final public static String ArtifactStatusProcessed = "Processed";
	final public static String ArtifactStatusNoAction = "NoAction";
	final public static String ArtifactStatusOutdated = "Outdated";
	final public static String ArtifactStatusToBeBatchUploaded = "ToBeBatchUploaded";
	
	final public static String ArtifactStatusScreenTextCreateArtifact = "CreateArtifact";
	final public static String ArtifactStatusScreenTextUpload = "Upload";
	final public static String ArtifactStatusScreenTextModify = "Modify";

	public String LocalFileName;
	public String draftingState;
	public String ReqRespFileName;
	public int unpulishedVerNum;
	public String DelegatedTo;
	ArtifactKeyPojo parentArtifactKeyPojo;
											// To be looked into for why it was set null in all situations
											// Check if this is really needed...???

	public SelfAuthoredArtifactpojo(
			ArtifactKeyPojo inArtifactKeyPojo,
			String inRequestor,
			String inAuthor,
			boolean inHasSpecialHandler,
			String inReviewFileName,
			String inERLStatus,
			ArtifactKeyPojo inParentArtifactKeyPojo,
			String inLocalFileName,
			String inDraftingState,
			String inReqRespFileName,
			int inUnpulishedVerNum,
			String inDelegatedTo
	) {
		super(inArtifactKeyPojo,
				inRequestor, inAuthor, inHasSpecialHandler, inReviewFileName, inERLStatus);
		LocalFileName = inLocalFileName;
		draftingState = inDraftingState;
		ReqRespFileName = inReqRespFileName;
		unpulishedVerNum = inUnpulishedVerNum;
		DelegatedTo = (inDelegatedTo!=null)?inDelegatedTo:"";
		parentArtifactKeyPojo = (inParentArtifactKeyPojo!=null)?inParentArtifactKeyPojo:null;
	}
	
	public SelfAuthoredArtifactpojo(
			ArtifactPojo inArtifactPojo,
			ArtifactKeyPojo inParentArtifactKeyPojo,
			String inLocalFileName,
			String inDraftingState,
			String inReqRespFileName,
			int inUnpulishedVerNum,
			String inDelegatedTo
	) {
		super(inArtifactPojo);
		LocalFileName = inLocalFileName;
		draftingState = inDraftingState;
		ReqRespFileName = inReqRespFileName;
		unpulishedVerNum = inUnpulishedVerNum;
		DelegatedTo = (inDelegatedTo!=null)?inDelegatedTo:"";
		parentArtifactKeyPojo = (inParentArtifactKeyPojo!=null)?inParentArtifactKeyPojo:null;
	}	
}