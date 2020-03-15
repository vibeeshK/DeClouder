package commonTechs;

import java.util.HashMap;

import org.eclipse.swt.widgets.Display;

public class DisplayKeeper {
	private static HashMap<String,Display> displaysMap = null;
	private DisplayKeeper () {}
	public static synchronized Display getDisplay() {
		Display display = null;
		if (displaysMap == null) {
			displaysMap = new HashMap<String,Display>();
		}
		if (displaysMap.get(Thread.currentThread().getName()) == null) {
			System.out.println("getting new display for thread " + Thread.currentThread().getName());
			display = new Display();
			displaysMap.put(Thread.currentThread().getName(), display);
		} else {
			String threadName = Thread.currentThread().getName();
			display = displaysMap.get(threadName);
			if (display == null || display.isDisposed()) {
				display = new Display();
				displaysMap.put(threadName, display);
			}
		}
		return display;
	}	
}
