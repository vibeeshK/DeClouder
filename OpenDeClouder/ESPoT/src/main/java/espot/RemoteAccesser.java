package espot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface RemoteAccesser {
	/*
	 * Interface mandate for the remote accessors which help to access various doc centers
	 */
	public void intiateCommunications(RootPojo inRootPojo,Commons inCommons);
	public boolean exists(String inRemoteURL) throws IOException;
	public InputStream getRemoteFileStream(String inRemoteFileName);
	//public List<String> getList(String inRemoteURL);	
	public ArrayList<String> getRemoteList(String inRemoteDropBox);
	public void put(String inRemoteURL, byte[] inBytes);
	public void putInStreamIntoRemoteLocation(String inNewContentRemoteLocation, InputStream inUpdatedContentByteArray);
	public void moveToRemoteLocation(String inSourceFileRemoteLocation, String inDestinationFileRemoteLocation);
	public void uploadToRemote(String inDestinationFileAtRemote, String inSourceFileAtLocal);
	public void downloadFile(Commons commons, String inRootString, String inRemoteFileName, String inLocalFileName);
}
