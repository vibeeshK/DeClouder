package espot;

import java.util.HashMap;

public class ERLVersionDocPojo {
	/*
	 * Convenience class that holds the different file versions of the ERL
	 */
	
	public HashMap<String,ERLVersioningDocItem> erlVersionTrackItems;	// for each Artifact/Remark Indicator + Relevance+ERLName combo string

	public ERLVersionDocPojo(){
		erlVersionTrackItems = new HashMap<String,ERLVersioningDocItem>();
	}
}