package espot;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class CatalogDownloader {
	/*
	 * This class downloads the catalog of the specified root
	 */
	private Commons commons = null;
	private RemoteAccesser remoteAccesser = null;	
	private RootPojo rootPojo = null;

	public CatalogDownloader(
			Commons inCommons, RootPojo inRootPojo, RemoteAccesser inRemoteAccesser) {
		commons = inCommons;
		rootPojo = inRootPojo;
		remoteAccesser = inRemoteAccesser;
		System.out.println("at 111 rootPojo nick= " + rootPojo.rootNick);
	}

	public void downloadCatalog() throws IOException {
		
		if (!CatalogDownloadDtlsHandler.getInstance(commons).isFreshDownLoadAllowed(rootPojo.rootNick)) {
			return;	// maybe too soon to download again. hence skipping
		}
		
		String localCatalogDbFileWithPath;
		ArrayList<String> publishFileNameURLs = new ArrayList<String>();
		String catalogPublishURL = rootPojo.rootString
		+ rootPojo.fileSeparator
		+ commons.getServerSideSideCatalogDbPublishFolderOfRoot();
		
		System.out.println("at 222 already set remoteAccesser= " + remoteAccesser);

		System.out.println("catalogPublishURL= " + catalogPublishURL);

		//publishFileNameURLs.addAll(remoteAccesser.getList(catalogPublishURL));
		publishFileNameURLs.addAll(remoteAccesser.getRemoteList(catalogPublishURL));

		System.out.println("listing done");
		
		publishFileNameURLs = commons
				.sortLatestRemoteCatalogPublishFile(publishFileNameURLs);

		System.out.println("after sorting:");

		for (int fileCount2 = 0; fileCount2 < publishFileNameURLs.size(); fileCount2++) {
			System.out.println("remoteAccesser file= "
					+ publishFileNameURLs.get(fileCount2));
		}

		String latestDbFileName = commons.getFileNameFromURL(publishFileNameURLs.get(0), rootPojo.fileSeparator);

		localCatalogDbFileWithPath = commons.getFullLocalPathFileNameOfDownloadedDbFile(rootPojo.rootNick,
																							latestDbFileName);
		System.out.println("publishFileNameURLs.get(0) = " + publishFileNameURLs.get(0));
		System.out.println("commons.getFileNameFromURL(publishFileNameURLs.get(0) = " + commons.getFileNameFromURL(publishFileNameURLs.get(0),rootPojo.fileSeparator));
		System.out.println("latestDbFileName = " + latestDbFileName);
		System.out.println("localCatalogDbFileWithPath = " + localCatalogDbFileWithPath);
		String remoteURLofDbCatalog = catalogPublishURL
										+ rootPojo.fileSeparator
										+ latestDbFileName;
		
		System.out.println("rootPojo.rootPrefix ..." + rootPojo.rootPrefix);
		System.out.println("catalogPublishURL ..." + catalogPublishURL);
		System.out.println("publishFileNameURLs.get(0) ..." + publishFileNameURLs.get(0));
		System.out.println("remoteURLofDbCatalog ..." + remoteURLofDbCatalog);

		InputStream catalogDBInputStream = remoteAccesser.getRemoteFileStream(remoteURLofDbCatalog);

		System.out
				.println("received in stream..." + publishFileNameURLs.get(0));

		commons.storeInStream(catalogDBInputStream, localCatalogDbFileWithPath);

		CatalogDownloadDtlsHandler.getInstance(commons).updateCatalogDownloadDetail(rootPojo.rootNick,latestDbFileName,commons.getCurrentTimeStamp());

		
		System.out.println("received in file...1" + localCatalogDbFileWithPath);

		System.out.println("Calogdownload Success");
		
		return;
	}

	public String OLDdownCatalogToTempFolderIfNew(String inFileName) throws 
			IOException {
		String downloadedTempDbPathFile = "";
		System.out.println("at downloadCat1111");

		ArrayList<String> publishFileNameURLs = new ArrayList<String>();
		String catalogPublishURL = rootPojo.rootString
				+ rootPojo.fileSeparator + commons.getServerSideSideCatalogDbPublishFolderOfRoot();

		System.out.println("catalogPublishURL= " + catalogPublishURL);
		System.out.println("at 2221 remoteAccesser already set = " + remoteAccesser );

		publishFileNameURLs = remoteAccesser.getRemoteList(catalogPublishURL);
		System.out.println("listing done");
		System.out.println("before sorting: fileCount = " + publishFileNameURLs.size());

		publishFileNameURLs = commons
				.sortLatestRemoteCatalogPublishFile(publishFileNameURLs);

		System.out.println("after sorting:");
		
		for (int fileCount2 = 0; fileCount2 < publishFileNameURLs.size(); fileCount2++) {
			System.out.println("remoteAccesser file= "
					+ publishFileNameURLs.get(fileCount2));
		}
		System.out.println("publishFileNameURLs.get(0) =" + publishFileNameURLs.get(0));
		System.out.println("inFileName =" + inFileName);

		if (commons.getFileNameFromURL(publishFileNameURLs.get(0),rootPojo.fileSeparator).equalsIgnoreCase("")
			||	inFileName.equalsIgnoreCase(commons.getFileNameFromURL(publishFileNameURLs.get(0),rootPojo.fileSeparator))
		) {
			System.out.println("No recent publications");
			return null;
		}

		downloadedTempDbPathFile = commons.getFullLocalPathFileNameOfTempFile(rootPojo.rootNick, commons.getFileNameFromURL(publishFileNameURLs.get(0),rootPojo.fileSeparator));
		
		System.out
		.println("downloadedTempDbPathFile =" + downloadedTempDbPathFile);

		String remoteURLofDbCatalog = catalogPublishURL + rootPojo.fileSeparator + commons.getFileNameFromFullPath(publishFileNameURLs.get(0).replaceAll(" ", "%20"), rootPojo.fileSeparator);
		
		System.out.println("remoteURLofDbCatalog ..." + remoteURLofDbCatalog);

		InputStream catalogDBInputStream = remoteAccesser.getRemoteFileStream(remoteURLofDbCatalog);

		System.out
				.println("received in stream..." + publishFileNameURLs.get(0));

		commons.storeInStream(catalogDBInputStream, downloadedTempDbPathFile);
		System.out.println("received in file...2" + downloadedTempDbPathFile);
		System.out.println("Calogdownload Success");
		
		return downloadedTempDbPathFile;
	}

	public static void main(String[] args) throws IOException, ParseException {

		HashMap<String,RootPojo> rootsMap = null;
		Commons commons = Commons.getInstance(Commons.CLIENT_MACHINE);
		rootsMap = PublishedRootsHandler.getPublishedRoots(commons);
		System.out.println("at 4");
		System.out.println("end....");
	}
}