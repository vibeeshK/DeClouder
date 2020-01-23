package espot;

import java.util.HashMap;

public class ReqProcDocPojo {
	/*
	 * Holder of request processing status doc data
	 */
	boolean dbTobeRenewed;
	public HashMap<String,ReqTrackItem> reqTrackItems;

	public ReqProcDocPojo(){
		dbTobeRenewed = false;
		reqTrackItems = new HashMap<String,ReqTrackItem>();
	}
}