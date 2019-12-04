package espot;

import org.eclipse.swt.widgets.Display;

public class CommonUIData extends CommonData{
	/*
	 * This class holds key data required by all UI process
	 */

	private CommonUIData(Commons inCommons) {
		super(inCommons);
	}

	public static CommonUIData getUIInstance(Commons inCommons) {
		return new CommonUIData(inCommons);
	}
	
	public Display getESPoTDisplay() {

		Display espotDisplay = DisplayKeeper.getESPoTDisplay();
		return espotDisplay;
	}	
}