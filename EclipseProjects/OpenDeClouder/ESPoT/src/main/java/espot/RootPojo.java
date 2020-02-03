package espot;

public class RootPojo {
	/*
	 * Holder of root's details
	 */
	public static String SysRootType="System";
	public static String RegRootType="Regular";

	public String rootNick = null; //Unique short identifier of the root
	public String remoteAccesserType = null;
	public String rootType = null;
	public String rootString = null;
	public String fileSeparator = null;
	public String rootPrefix = "";
	public boolean requiresInternet = false;
	
	public RootPojo() {
	}

	public RootPojo(
			String inRootNick, 
			String inRootString,
			String inRemoteAccesserType,
			String inRootType,
			String inFileSeparator,
			String inRootPrefix,
			boolean inRequiresInternet
		) {
		setRootPojo(
				inRootNick, 
				inRootString,
				inRemoteAccesserType,
				inRootType,
				inFileSeparator,
				inRootPrefix,
				inRequiresInternet
				);
	}

	public void setRootPojo(
			String inRootNick, 
			String inRootString,
			String inRemoteAccesserType,
			String inRootType,
			String inFileSeparator,
			String inRootPrefix,
			boolean inRequiresInternet
	) {
		rootNick = inRootNick;
		rootString = inRootString;
		remoteAccesserType = inRemoteAccesserType;
		rootType =  inRootType;
		fileSeparator = inFileSeparator;
		rootPrefix = inRootPrefix;
		requiresInternet = inRequiresInternet;
	}
}