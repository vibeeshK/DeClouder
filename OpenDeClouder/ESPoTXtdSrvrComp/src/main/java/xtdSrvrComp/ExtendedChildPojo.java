package xtdSrvrComp;

import espot.ArtifactKeyPojo;

public class ExtendedChildPojo {
	/*
	 * db record holder for Decking grouper Child to track status
	 */	
	public static final String CHILD_NEW = "NEW";			// newly identified child for a parent
	public static final String CHILD_UPDATED = "UPDATED";	// new child content arrived
	public static final String CHILD_PROCESSED = "PROCESSED";	// new content processed

	public ArtifactKeyPojo artifactKeyPojo = null;
	public ArtifactKeyPojo parentArtifactKeyPojo = null;
	
	public int childNumber = 0;
	public String childStatus = null;
	public String childUpdateTimeStamp = null;

	public ExtendedChildPojo(
			ArtifactKeyPojo inChildArtifactKeyPojo,
			ArtifactKeyPojo inParentArtifactKeyPojo,
			int inChildNumber,
			String inChildStatus,
			String inChildUpdateTimeStamp) {
		artifactKeyPojo = inChildArtifactKeyPojo;
		parentArtifactKeyPojo = inParentArtifactKeyPojo;
		childNumber = inChildNumber;
		childStatus = inChildStatus;
		childUpdateTimeStamp = inChildUpdateTimeStamp;
	}
}