package espot;

import org.eclipse.swt.widgets.Display;

import commonTechs.DisplayKeeper;

public class CommonUIData extends CommonData{
	/*
	 * This class holds key data required by all UI process
	 */

	private boolean artifactDisplayOkayToContinue;
	private UserPojo currentUserDetail = null;

	private CommonUIData(Commons inCommons) {
		super(inCommons);
		setArtifactDisplayOkayToContinue(true);
		currentUserDetail = getUsersHandler().getUserDetailsFromRootSysLoginID(inCommons.userName);
	}

	public static CommonUIData getUIInstance(Commons inCommons) {
		return new CommonUIData(inCommons);
	}
	
	public Display getESPoTDisplay() {

		Display espotDisplay = DisplayKeeper.getDisplay();
		return espotDisplay;
	}	

	public synchronized void setArtifactDisplayOkayToContinue(boolean inOkayToContinue) {
		artifactDisplayOkayToContinue = inOkayToContinue;
	}

	public synchronized boolean getArtifactDisplayOkayToContinue() {
		return artifactDisplayOkayToContinue;
	}

	public UserPojo getCurrentUserPojo(){
		return currentUserDetail;
	}
}