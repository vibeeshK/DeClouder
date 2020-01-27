package espot;

public class CatalogDownloadDetailsPojo {
	/*
	 * Convenience object to hold downloaded file details of latest catalogs
	 */
	public String rootNick;
	public String downloadedFileName;
	public String downloadedTime;
	CatalogDownloadDetailsPojo(){
	}
	CatalogDownloadDetailsPojo(String inRootNick,String inDownloadedFileName,String inDownloadedTime){
		rootNick = inRootNick;
		downloadedFileName = inDownloadedFileName;
		downloadedTime = inDownloadedTime;
	}
}