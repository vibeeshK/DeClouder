package commonTechs;

//This class seems redundant and may not be required anymore

import java.util.Comparator;

public class CompareForLaterName implements Comparator<String> {
	public int compare(String URL1, String URL2) {
		if (URL1.compareToIgnoreCase(URL2) < 0) {
			return 1;
		} else {
			return -1;
		}
	}
}
