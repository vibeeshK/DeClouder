package espot;

public class AutoTriggerPojo {
	/*
	 * Pojo of AutoTrigger record
	 */
	final public static String PROCESS_STAT_NEW = "New";
	final public static String PROCESS_STAT_CONTINUE = "Continue";
	final public static String PROCESS_STAT_DISCONTINUE = "Discontinue";
	final public static String ArtifactStatusKEEPLOCAL = "KeepLocal";
	ArtifactKeyPojo artifactKeyPojo;
	String rootSysLoginID;
	public String erlORRwUploadedTimeStamp;
	public String prevTriggeredAt;
	public int triggerIntervalSec;
	public String processState;
	public AutoTriggerPojo(
			ArtifactKeyPojo inArtifactKeyPojo,
			String inRootSysLoginID,
			String inERLUploadedTimeStamp,
			String inPrevTriggeredAt,
			Integer inTriggerIntervalSec,
			String inProcessState) {
		artifactKeyPojo = inArtifactKeyPojo;
		rootSysLoginID = inRootSysLoginID;
		erlORRwUploadedTimeStamp = inERLUploadedTimeStamp;
		System.out.println("inPrevTriggeredAt hsa been passed as " + inPrevTriggeredAt);
		prevTriggeredAt = (inPrevTriggeredAt!=null)?inPrevTriggeredAt:null;
		System.out.println("inPrevTriggeredAt hsa been set as as " + prevTriggeredAt);
		if (prevTriggeredAt == null){
			System.out.println("prevTriggeredAt is empty " + prevTriggeredAt);
		}
		if (prevTriggeredAt != null){
			System.out.println("prevTriggeredAt is not null" + prevTriggeredAt);
		} else {
			System.out.println("prevTriggeredAt is Null" + prevTriggeredAt);			
		}
		triggerIntervalSec = (inTriggerIntervalSec!=null)?inTriggerIntervalSec:null;;
		processState = (inProcessState!=null)?inProcessState:null;
	}	
}