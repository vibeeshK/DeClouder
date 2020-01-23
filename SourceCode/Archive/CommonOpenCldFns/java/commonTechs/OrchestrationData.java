package commonTechs;

public class OrchestrationData {
	/* 
	 * This class used mainly for passing and holding state data of connected processes
	 */
	public boolean okToContinue = true;
	public String userName;
	public String message;
	public String title;
	public int healthCheckIntervalMin = 1;
	public int repeatIntervalMin = 1;
	String backgroundImagePathFileName = null;
	public OrchestrationData(String inUserName,String inTitle, String inBackgroundImagePathFileName) {
		userName = inUserName;
		title = inTitle;
		backgroundImagePathFileName = inBackgroundImagePathFileName;
	}

	public int getHealthCheckIntervalInSeconds(){
		return healthCheckIntervalMin * 60;
	}
	public int getrepeatIntervalInSeconds(){
		return repeatIntervalMin * 60;
	}	
}