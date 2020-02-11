package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class ContntHandlrTriggr implements Runnable {
	/* 
	 * This class processes the auto triggered contents related to the users such as timesheet capture
	 */
	
	private CommonData commonData;
	private CatelogPersistenceManager catelogPersistenceManager;
	private Commons commons;
	private RootPojo rootPojo;
	private String rootSysLoginID;
	ArrayList<AutoTriggerPojo> timeupTriggers;
	Uploader uploader;

	public ContntHandlrTriggr(
			RootPojo inRootPojo,
			String inRootSysLoginID,			
			CommonData inCommonData, RemoteAccesser inRemoteAccesser) throws IOException, ParseException {

		//21OCt2018 major issue in reusing the same common persistance manager in this thread as
		// it conflicted with sql calls from parent thread
		//21OCt2018 Creating a separate instance of commons to avoid conflicts with parent thread
		//commonData = inCommonData;
		//commons = commonData.getCommons();
		//catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		//rootPojo = commonData.getCurrentRootPojo();
		rootPojo = inRootPojo;
		rootSysLoginID = inRootSysLoginID;			

		commons = Commons.getInstance(inCommonData.getCommons().processMode,rootPojo.rootNick);
		
		System.out.println("in ContntHandlrTriggr rootNick " +  rootPojo.rootNick);
		System.out.println("in ContntHandlrTriggr rootSysLoginID " +  rootSysLoginID);
		System.out.println("in ContntHandlrTriggr commons : " +  commons);
		
		//commonData = inCommonData;
		commonData = CommonUIData.getUIInstance(commons);	// IMPORTANT NOTE: This is not an oversight. Though one may wish 
															// to assign the input argument inCommonData here,
															// since it would cause racing between threads, a separate 
															// instance is created.
															// But to avoid lagging behind, a separate refresh of catalog 
															// persistence manager included in the run method to take 
															// latest catalog file coming from catalog downloader
															// executed in the parent thread.
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		uploader = new Uploader(commonData, inRemoteAccesser);
	}

	public void run() {
		catelogPersistenceManager.refreshForLatestCatalog();
 		arrangeForNewAutoTriggerERLs();
 		try {
	  		timeupTriggers = catelogPersistenceManager.getElapsedAutoTriggers(rootPojo.rootNick, rootSysLoginID);
	 		processElapsedTriggers();
	 		synchronized(Uploader.LOCKING_RESOURCE) { // sychronized to avoid racing with the main thread
				uploader.uploadArtifactsOfOneRoot();
				uploader.uploadReviewsOfOneRoot();
	 		}		
		} catch (ClassNotFoundException | IOException | TransformerException | ParserConfigurationException | SAXException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in ContntHandlrTriggr run ", e);			
		}
		return;
	}

	private void arrangeForNewAutoTriggerERLs(){
		
		System.out.println("catalog persistance manager inside arrangeForNewAutoTriggerERLs tobeConnectedCatalogDbFile is " + catelogPersistenceManager.tobeConnectedCatalogDbFile);
		System.out.println("catalog persistance manager in inside arrangeForNewAutoTriggerERLs tobeConnectedCatalogDbFile is " + catelogPersistenceManager);

		ArrayList<ERLDownload> autoTriggerERLDownlods = catelogPersistenceManager.readNewAutoTriggerERLDownLoadsForAuthor(rootSysLoginID);

		System.out.println("from arrangeForNewAutoTriggerERLs autoTriggerERLDownload size " + autoTriggerERLDownlods.size());

		for (ERLDownload autoTriggerERLDownload : autoTriggerERLDownlods){

			ContentHandlerInterface contentHandlerObjectInterface = null;
			contentHandlerObjectInterface = ContentHandlerManager.getInstance(commons, catelogPersistenceManager, autoTriggerERLDownload.artifactKeyPojo.contentType);

			System.out.println("from arrangeForNewAutoTriggerERLs before calling initializeContentHandlerForDownloadedArtifact artifactName " + autoTriggerERLDownload.artifactKeyPojo.artifactName);
			contentHandlerObjectInterface.
				initializeContentHandlerForDownloadedArtifact((CommonUIData) commonData, autoTriggerERLDownload);
			System.out.println("from arrangeForNewAutoTriggerERLs after calling initializeContentHandlerForDownloadedArtifact artifactName " + autoTriggerERLDownload.artifactKeyPojo.artifactName);

			int triggerIntervalSec = contentHandlerObjectInterface.getTriggerInterval();

			AutoTriggerPojo autoTriggerPojo = catelogPersistenceManager.readAutoTrigger(autoTriggerERLDownload.artifactKeyPojo, rootSysLoginID);
			if (autoTriggerPojo == null 
					&& !autoTriggerERLDownload.erlStatus.equalsIgnoreCase(
								SelfAuthoredArtifactpojo.ERLSTAT_INACTIVE)){
				System.out.println("from arrangeForNewAutoTriggerERLs commons.getDateTS() is " + commons.getDateTS());
				System.out.println("from arrangeForNewAutoTriggerERLs commons.getCurrentTimeStamp() is " + commons.getCurrentTimeStamp());
				
				autoTriggerPojo = new AutoTriggerPojo(autoTriggerERLDownload.artifactKeyPojo,
														rootSysLoginID,
														autoTriggerERLDownload.uploadedTimeStamp,
														null,	// prevtrigger timestamp
														triggerIntervalSec,AutoTriggerPojo.PROCESS_STAT_NEW);
				catelogPersistenceManager.insertAutoTrigger(autoTriggerPojo);
			} else {
				if (!autoTriggerERLDownload.erlStatus.equalsIgnoreCase(
							SelfAuthoredArtifactpojo.ERLSTAT_INACTIVE)){
					autoTriggerPojo.processState = AutoTriggerPojo.PROCESS_STAT_CONTINUE;
				} else {
					autoTriggerPojo.processState = AutoTriggerPojo.PROCESS_STAT_DISCONTINUE;
				}
				if (autoTriggerERLDownload.reviewTimeStamp != null && 
					autoTriggerERLDownload.reviewTimeStamp.compareTo(autoTriggerPojo.erlORRwUploadedTimeStamp)>0){
					autoTriggerPojo.erlORRwUploadedTimeStamp = autoTriggerERLDownload.reviewTimeStamp;
				} else {
					autoTriggerPojo.erlORRwUploadedTimeStamp = autoTriggerERLDownload.uploadedTimeStamp;
				}
				autoTriggerPojo.triggerIntervalSec = triggerIntervalSec;

				catelogPersistenceManager.updateAutoTrigger(autoTriggerPojo);
			}
		}
	}

	public void processElapsedTriggers() {

		System.out.println("from processElapsedTriggers size " + timeupTriggers.size());

		for (AutoTriggerPojo trigger : timeupTriggers) {

			trigger.prevTriggeredAt = commons.getCurrentTimeStamp();
			System.out.println("from processElapsedTriggers a0 inside  loop trigger.prevTriggeredAt is " + trigger.prevTriggeredAt);
			ERLDownload triggerERL = catelogPersistenceManager.readERLDownLoad(trigger.artifactKeyPojo);

			System.out.println("from processElapsedTriggers a1 inside  loop trigger.prevTriggeredAt is " + trigger.prevTriggeredAt);
			ContentHandlerInterface contentHandlerObjectInterface =
										ContentHandlerManager.getInstance(commons, 
																catelogPersistenceManager, 
																triggerERL.artifactKeyPojo.contentType);
			System.out.println("from processElapsedTriggers before calling initializeContentHandlerForDownloadedArtifact artifactName " + triggerERL.artifactKeyPojo.artifactName);
			System.out.println("from processElapsedTriggers a2 inside  loop trigger.prevTriggeredAt is " + trigger.prevTriggeredAt);
			contentHandlerObjectInterface.
				initializeContentHandlerForDownloadedArtifact((CommonUIData) commonData, triggerERL);
			System.out.println("from processElapsedTriggers a3 inside  loop trigger.prevTriggeredAt is " + trigger.prevTriggeredAt);
			System.out.println("from processElapsedTriggers before calling initializeContentHandlerForDownloadedArtifact artifactName " + triggerERL.artifactKeyPojo.artifactName);
			contentHandlerObjectInterface.triggeredProcess(trigger.prevTriggeredAt);
			System.out.println("from processElapsedTriggers inside  loop " + trigger.artifactKeyPojo.artifactName);
			System.out.println("from processElapsedTriggers a4 inside  loop trigger.prevTriggeredAt is " + trigger.prevTriggeredAt);
			System.out.println("from processElapsedTriggers inside  loop trigger.prevTriggeredAt is " + trigger.prevTriggeredAt);
			catelogPersistenceManager.updateAutoTrigger(trigger);
		}
	}
}
