package commonTechs;

public class OrchestrationData {
	/* 
	 * This class used mainly for passing and holding state data of connected processes
	 */
	private boolean okToContinue = true;
	public String userName;
	public String message;
	public String title;
	private int healthCheckIntervalMin = 1;
	private int repeatIntervalMin = 1;
	String applicationIconPathFileName = null;

	public OrchestrationData(String inUserName,String inTitle, String inApplicationIcon) {
		userName = inUserName;
		title = inTitle;
		applicationIconPathFileName = inApplicationIcon;
		okToContinue = true;
	}
	
	public synchronized void setOkayToContinue(boolean inOkayToContinue){
		okToContinue = inOkayToContinue;
	}

	public synchronized boolean getOkayToContinue(){
		return okToContinue;
	}
	public synchronized int getHealthCheckIntervalInSeconds(){
		return healthCheckIntervalMin * 60;
	}
	public synchronized void setHealthCheckIntervalMin(int inHealthCheckIntervalMin){
		healthCheckIntervalMin = inHealthCheckIntervalMin;
	}
	public synchronized int getRepeatIntervalInSeconds(){
		return repeatIntervalMin * 60;
	}		
	public synchronized void setRepeatIntervalMin(int inRepeatIntervalMin){
		repeatIntervalMin = inRepeatIntervalMin;
	}	
}