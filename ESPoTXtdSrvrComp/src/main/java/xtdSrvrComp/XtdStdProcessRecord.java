package xtdSrvrComp;

import espot.ArtifactKeyPojo;

public class XtdStdProcessRecord {
	/*
	 * Db record holder for standard processing to track status
	 */			
	public static final String ERLRecord_NEW = "NEW";
	public static final String ERLRecord_UPDATED = "RENEWED";
	public static final String ERLRecord_PROCESSED = "PROCESSED";
	public static final String ERLRecord_CONTINUE = "CONTINUE";
	public static final String ERLRecord_ERROR = "ERROR";
	public static final String ERLRecord_DISCONTINUE = "DISCONTINUE";
	public static final String ERLRecord_SKIP = "SKIP";
	
	public String parentUpdateTimeStamp = "";
	public String parentStatus = "";
	public ArtifactKeyPojo artifactKeyPojo = null;
	
	public XtdStdProcessRecord(
			ArtifactKeyPojo inArtifactKeyPojo,
			String inParentUpdateTimeStamp,
			String inParentStatus
			) {
		artifactKeyPojo = inArtifactKeyPojo;
		parentUpdateTimeStamp = inParentUpdateTimeStamp;
		parentStatus = inParentStatus;
	}
}