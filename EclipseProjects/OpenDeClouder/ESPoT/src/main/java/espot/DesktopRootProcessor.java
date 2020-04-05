package espot;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import commonTechs.OrchestrationData;

public class DesktopRootProcessor implements Runnable {
	/*
	 * This processor syncs-up contents between the desktop of users with doc-central
	 */
	CatelogPersistenceManager catelogPersistenceManager = null;
	Commons commons = null;
	CommonUIData commonUIData = null;	
	RootPojo rootPojo = null;

	RemoteAccesser remoteAccesser = null;
	
	CatalogDownloader catalogDownloader = null;
	Uploader uploader = null;
	ResponseChecker responseChecker = null;
	ContentDownloader contentDownloader = null;
	AssignedContentSubscriber assignedContentSubscriber = null;
	ContntHandlrTriggr contntHandlrTriggr = null;
	Thread contntHandlerTrgThread = null;

	OrchestrationData orchestrationData;

	public DesktopRootProcessor(RootPojo inRootPojo,OrchestrationData inOrchestrationData) throws IOException, ParseException {
		System.out.println("initiating DesktopRootProcessor rootNick11a: " +  inRootPojo.rootNick);
		rootPojo = inRootPojo;
		commons = Commons.getInstance(Commons.CLIENT_MACHINE,rootPojo.rootNick);
		System.out.println("in DesktopRootProcessor commons : " +  commons);

		//commonUIData = 	CommonUIData.getUIInstance(commons);
		//catelogPersistenceManager = commonUIData.getCatelogPersistenceManager();
		//orchestrationData = inOrchestrationData;

		System.out.println("initiating RootProcessor at 11x1 rootNick11a: " +  inRootPojo.rootNick);
		System.out.println("commons 123: " +  commons);

		System.out.println("at 21");

		remoteAccesser = RemoteAccessManager.getInstance(commons, rootPojo.rootNick);
		System.out.println("at 222221 remoteAccesser = " + remoteAccesser);

		catalogDownloader = new CatalogDownloader(commons, rootPojo, remoteAccesser);
		catalogDownloader.downloadCatalog();	

		System.out.println("at 222221 1 catalogDownloader " + catalogDownloader);

		commonUIData = 	CommonUIData.getUIInstance(commons);

		if (commonUIData.onlyCatalogDownloadAllowed){
			System.out.println("at 222221 x returning as onlyCatalogDownloadAllowed ");
			return;
		}
		System.out.println("at 222221 xy crossed Beyond the step of onlyCatalogDownloadAllowed");
		//System.exit(8);

		catelogPersistenceManager = commonUIData.getCatelogPersistenceManager();
		orchestrationData = inOrchestrationData;

		System.out.println("catalogDBAlias232323cd123: " + catelogPersistenceManager.catalogDBAlias);

		uploader = new Uploader(commonUIData, remoteAccesser);
		System.out.println("at 222221.2" );

		
		System.out.println("catalogDBAlias343434cd: " + catelogPersistenceManager.catalogDBAlias);

		responseChecker = new ResponseChecker(commonUIData, remoteAccesser);
		System.out.println("at 222221.3" );

		System.out.println("catalogDBAlias454545d: " + catelogPersistenceManager.catalogDBAlias);

		assignedContentSubscriber = new AssignedContentSubscriber(commonUIData);

		contentDownloader = new ContentDownloader(commonUIData, remoteAccesser);

		System.out.println("at 222221.4" );

		contntHandlrTriggr = new ContntHandlrTriggr(rootPojo, commons.readRootSysLoginIDFromClienSideProperties(rootPojo.rootNick), commonUIData, remoteAccesser);

		System.out.println("at 222221 4 autoTriggerSubThreads after ContntHandlrTriggr initiation" );
		
		System.out.println("catalogDBAlias565656: " + catelogPersistenceManager.catalogDBAlias);

		System.out.println("at 4");
	}

	public void run() {
		boolean runContinue = true;
		int rootProcessCount = 0;

		System.out.println("catalogDBAlias676767: " + catelogPersistenceManager.catalogDBAlias);

		while (orchestrationData.getOkayToContinue()) {
			if (rootPojo.requiresInternet && !commons.isInternetAvailable()){
				commons.logger.warn(" Internet umavailable, hence skipping DesktopRootProcess for " + rootPojo.rootNick);	
				System.out.println(" Internet umavailable, hence skipping DesktopRootProcess for " + rootPojo.rootNick);
				break;
			}

			rootProcessCount++;
			System.out.println("ROOTPROCESSCOUNTAS=" + rootProcessCount);
			System.out.println("ROOTPROCESSCOUNTAS=" + rootProcessCount);
			System.out.println("ROOTPROCESSCOUNTAS=" + rootProcessCount);
			System.out.println("ROOTPROCESSCOUNTAS=" + rootProcessCount);
			try {
				System.out.println("catalogDBAlias123123abab: " + catelogPersistenceManager.catalogDBAlias);

				catalogDownloader.downloadCatalog();
				
				catelogPersistenceManager.refreshForLatestCatalog();
				if (commonUIData.onlyCatalogDownloadAllowed){
					System.out.println("returning as onlyCatalogDownloadAllowed ");					
					return;
				}

				System.out.println("catalog persistance manager file in outside Desktop processor tobeConnectedCatalogDbFile is " + commonUIData.getCatelogPersistenceManager().tobeConnectedCatalogDbFile);
				System.out.println("catalog persistance manager in outside Desktop processor tobeConnectedCatalogDbFile is " + catelogPersistenceManager);
				
				
				assignedContentSubscriber.subscribeToAssignedContent();

				//correction: dont skip downloads as there could be new subscriptions
				//if (downloadedCatalogDbFileTempPath != null) {
				System.out.println("@12341");
				contentDownloader.downloadContentFilesForOneRoot();
				System.out.println("@12342");
				contentDownloader.downloadRemarksFilesForOneRoot();
				System.out.println("@12343");

				if (contntHandlerTrgThread == null || contntHandlerTrgThread.getState()==Thread.State.TERMINATED) {
					System.out.println("contntHandlrTriggr being started");					
					contntHandlerTrgThread = new Thread(contntHandlrTriggr);
					contntHandlerTrgThread.start();
				} else {
					System.out.println("contntHandlrTriggr already running hence skipping another start");					
				}
		 		synchronized(Uploader.LOCKING_RESOURCE) { // sychronized to avoid racing with the contenntHdlrTrigger thread
					uploader.uploadArtifactsOfOneRoot();
					uploader.uploadReviewsOfOneRoot();
		 		}
				responseChecker.checkResponsesForOneRoot();

			} catch (IOException | ClassNotFoundException | TransformerException | ParserConfigurationException | SAXException | ParseException e) {

				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in DesktopRootProcessor run ", e);
				
				System.out.println("EXITING THE CURRENT LOOP3");
			}
			synchronized (this) {
				System.out.println("GONNA WAIT22");
				try {

					System.out.println("goint to wait " + orchestrationData.getRepeatIntervalInSeconds() + " seconds");

					wait(orchestrationData.getRepeatIntervalInSeconds() * 1000);				

				} catch (InterruptedException e) {

					e.printStackTrace();
					System.out.println("EXITING THE CURRENT LOOP1");

				}
				System.out.println("COMING OUT OF WAIT");
			}
			System.out.println(" orchestrationData okToContinue 21 is " + orchestrationData.getOkayToContinue());	
		}
		System.out.println(" orchestrationData okToContinue 22 is " + orchestrationData.getOkayToContinue());
	}
}