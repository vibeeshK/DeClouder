package osHandlers;
import java.io.IOException;

import commonTechs.CommonTechs;
import espot.OSHandler;

public class WindowsFileAccesser implements OSHandler {
	/*
	 * File opener on a windows machine
	 */
	public void openFileToView(String inFileString) {
		String app_cmd = "rundll32 url.dll, FileProtocolHandler " + inFileString;
		System.out.println("command to be executed: " + app_cmd);
		try {
			System.out.println("000Trying to launch document");
		
			Runtime.getRuntime().exec(app_cmd);
		} catch (IOException e1) {
			e1.printStackTrace();
			CommonTechs.logger.error("Error in WindowsFileAccesser openFileToView " 
										+ " " + inFileString + " " + app_cmd, e1);
		}
	}
}
