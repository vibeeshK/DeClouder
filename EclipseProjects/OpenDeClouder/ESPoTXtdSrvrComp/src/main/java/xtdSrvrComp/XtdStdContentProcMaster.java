package xtdSrvrComp;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import espot.CatalogDownloader;
import espot.CommonData;
import espot.Commons;
import espot.ContentDownloader;
import espot.ErrorHandler;
import espot.RemoteAccessManager;
import espot.RemoteAccesser;
import espot.ResponseChecker;
import espot.RootPojo;
import espot.Uploader;

public class XtdStdContentProcMaster {
	/*
	 * Extended Process sequence for standard processes
	 */
	XtdCatalogPersistenceManager xtdCatlogPersistenceManager;					

	Commons commons;
	RootPojo rootPojo;
	String contentType;
	RemoteAccesser remoteAccesser;
	CommonData commonData;

	XtdStdContentProcessor xtdStdContentProcesor;
	CatalogDownloader catalogDownloader;
	Uploader uploader;
	ResponseChecker responseChecker;
	ContentDownloader contentDownloader;

	public XtdStdContentProcMaster(Commons inCommons, RootPojo inRootPojo, String inContentType, XtdCatalogPersistenceManager inXtdCatalogPersistenceManager ) {
		System.out.println("at start XtdStdContentProcMaster inCommons is " + inCommons);		
		System.out.println("at start XtdStdContentProcMaster inContentType is " + inContentType);		
		commons = inCommons;		
		rootPojo = inRootPojo;
		contentType = inContentType;
		xtdCatlogPersistenceManager = inXtdCatalogPersistenceManager;
		System.out.println("initiating XtdTmSheetProcMaster at 11x1 rootNick11a: " +  inRootPojo.rootNick);

		System.out.println("at 21a");	
		commonData = CommonData.getInstance(commons, xtdCatlogPersistenceManager);
		System.out.println("XtdStdContentProcMaster 2 commonData is " + commonData);		
		remoteAccesser = RemoteAccessManager.getInstance(commons, rootPojo.rootNick);

		System.out.println("XtdStdContentProcMaster rootPojo rootNick is " + rootPojo.rootNick);
		System.out.println("XtdStdContentProcMaster rootPojo rootString is " + rootPojo.rootString);
		System.out.println("XtdStdContentProcMaster commonData.getCurrentRootPojo() rootNick is " + commonData.getCurrentRootPojo().rootNick);
		System.out.println("XtdStdContentProcMaster commonData.getCurrentRootPojo() rootString is " + commonData.getCurrentRootPojo().rootString);
		System.out.println("XtdStdContentProcMaster remoteAccesser " + remoteAccesser);

		catalogDownloader = new CatalogDownloader(commons,rootPojo,remoteAccesser);
		contentDownloader = new ContentDownloader(commonData,remoteAccesser);
		responseChecker = new ResponseChecker(commonData,remoteAccesser);
		uploader = new Uploader(commonData, remoteAccesser);
		
		// removed loading of processor from constructor to make it modular for extensions
		// xtdStdContentProcesor = new XtdStdContentProcessor(commonData,contentType,remoteAccesser);
		initProcessor();
		
		System.out.println("at 222221.2" );
		System.out.println("at 222221.3" );
		System.out.println("at 222221 4bava" );
		System.out.println("at 4");
	}
	
	public void initProcessor() {
		// separating processor loading to make it modular for extensions
		xtdStdContentProcesor = new XtdStdContentProcessor(commonData,contentType,remoteAccesser);
	}
	
	public void coreProcessOfRecords() {

		try {
			
			if (rootPojo.requiresInternet && !commons.isInternetAvailable()){
				commons.logger.warn(" Internet umavailable, hence skipping extendedrocess for " + rootPojo.rootNick);	
				System.out.println(" Internet umavailable, hence skipping extendedrocess for " + rootPojo.rootNick);
				return;
			}

			catalogDownloader.downloadCatalog();
			System.out.println("at 222221 4bavasa" );
			xtdCatlogPersistenceManager.refreshForLatestCatalog();
			System.out.println("at 222221 4bavafdfd" );
			xtdStdContentProcesor.subscribeToDeckerParents();
			xtdStdContentProcesor.subscribeDependents();

			System.out.println("at 222221 4bavadfs" );
			contentDownloader.downloadContentFilesForOneRoot();	//downloading for new parent subscriptions
			System.out.println("at 222221 4bavafgdfg" );
			//here set the subscriptions for the child erls
			System.out.println("at 222221 4bavadsfsf" );
			System.out.println("at 222221 4bavafgdd" );
																	//Any recent child uploads to be subscribed
			System.out.println("at 222221 4bavagfdd" );
																	//Downloading for new child subscriptions
			System.out.println("at 222221 4bavadgfg" );
			//invoke the parent grouper's extended grouping processes e.g. summary decking
			xtdStdContentProcesor.extendedContentProcessing();		//For each parent, whichever child was refreshed
			System.out.println("at 222221 4bavagfdgfd" );
																	//   do child decking into parent
																	// and if any child was decked,
																	//   then do parent decking
																	// mark all selfAuthored artifacts for upload

			//correction: dont skip downloads as there could be new subscriptions
			//if (downloadedCatalogDbFileTempPath != null) {
			System.out.println("@12341");
			System.out.println("@12343");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("EXITING THE CURRENT LOOP3");
			ErrorHandler.showErrorAndQuit(commons, "Error XtdStdContentProcMaster coreProcessOfRecords " + rootPojo.rootNick + " " + contentType, e);
		}
	}
	
	public void endProcessOfRecords() {
		System.out.println("at 222221 endProcessOfRecords start" );
		try {
			uploader.uploadArtifactsOfOneRoot();
			System.out.println("at 222221 4bavadfdhh" );
			responseChecker.checkResponsesForOneRoot();
			System.out.println("at 222221 4bavartytr" );
		} catch (ClassNotFoundException | IOException | TransformerException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error XtdStdContentProcMaster endProcessOfRecords " + rootPojo.rootNick + " " + contentType, e);
		}
	}
}