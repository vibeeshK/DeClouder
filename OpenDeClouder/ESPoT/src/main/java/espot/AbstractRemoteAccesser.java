package espot;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractRemoteAccesser implements espot.RemoteAccesser {
	/*
	 * Forms the foundation for connecting to different doc centers
	 */
	public Commons commons = null;
	public RootPojo rootPojo = null;
	public String fileSeparator = null;

	public void commonInit(RootPojo inRootPojo, Commons inCommons) {
		rootPojo = inRootPojo;
		commons = inCommons;
		fileSeparator = rootPojo.fileSeparator;
	}
	
	public void downloadFile(Commons commons, String inRootString, String inRemoteFileName, String inLocalFileName) {
		System.out.println("@@AbstractRemoteAccesser start for downloading " + inRemoteFileName + " into " + inLocalFileName);
		InputStream inputStream = getRemoteFileStream(inRemoteFileName);
		if (inputStream != null) {
			try {
				commons.storeInStream(inputStream, inLocalFileName);
				inputStream.close();
				System.out.println("@@AbstractRemoteAccesser inputStream closed" + inRemoteFileName);
			} catch (IOException e) {
				//e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in downloadFile of AbstractRemoteAccesser " + inRemoteFileName + " into " + inLocalFileName +  commons.processMode, e);
				//System.exit(8);
			}
		}
		System.out.println("@@AbstractRemoteAccesser download complete:::");
		System.out.println("inRootString = " + inRootString);
		System.out.println("inRemoteFileName = " + inRemoteFileName);
		System.out.println("inLocalFileName = " + inLocalFileName);
	}
}