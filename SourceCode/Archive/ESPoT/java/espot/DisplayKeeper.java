package espot;

import java.util.HashMap;

import org.eclipse.swt.widgets.Display;

public class DisplayKeeper {
	private static HashMap<String,Display> dsplaysMap = null;
	private DisplayKeeper () {}
	public static Display getESPoTDisplay() {
		Display espotDisplay = null;
		if (dsplaysMap == null) {
			dsplaysMap = new HashMap<String,Display>();
		}
		if (dsplaysMap.get(Thread.currentThread().getName()) == null) {
			System.out.println("getting new ESPoTDisplay for thread " + Thread.currentThread().getName());
			espotDisplay = new Display();
			dsplaysMap.put(Thread.currentThread().getName(), espotDisplay);
		} else {
			espotDisplay = dsplaysMap.get(Thread.currentThread().getName());
		}
		return espotDisplay;
	}	
	
}
