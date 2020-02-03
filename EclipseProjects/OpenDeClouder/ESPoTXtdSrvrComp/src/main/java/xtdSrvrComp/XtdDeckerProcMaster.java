package xtdSrvrComp;

import java.io.IOException;

import espot.Commons;
import espot.ErrorHandler;
import espot.RootPojo;

public class XtdDeckerProcMaster extends XtdStdContentProcMaster {
	/*
	 * Extended Process sequence for deckers
	 */
	XtdDeckerProcessor extdDeckrProcessor;

	public XtdDeckerProcMaster(Commons inCommons, RootPojo inRootPojo, String inContentType, XtdCatalogPersistenceManager inXtdCatalogPersistenceManager) {
		super(inCommons,inRootPojo,inContentType,inXtdCatalogPersistenceManager);
	}

	@Override
	public void initProcessor() {
		// overriding for the special processor loading
		extdDeckrProcessor = new XtdDeckerProcessor(commonData, contentType, remoteAccesser);
	}
	
	@Override
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
			extdDeckrProcessor.subscribeToDeckerParents();
			System.out.println("at 222221 4bavadfs" );
			contentDownloader.downloadContentFilesForOneRoot();	//downloading for new parent subscriptions
			System.out.println("at 222221 4bavafgdfg" );
			//here set the subscriptions for the child erls
			extdDeckrProcessor.identifyGrouperChildrenOfUpdtdParents();
			System.out.println("at 222221 4bavadsfsf" );
			extdDeckrProcessor.deleteUnconnectedSubscriptions();
			System.out.println("at 222221 4bavafgdd" );
			extdDeckrProcessor.subscribeToUpdatedChildren();	//Any recent child uploads to be subscribed
			System.out.println("at 222221 4bavagfdd" );
			contentDownloader.downloadContentFilesForOneRoot();	//Downloading for new child subscriptions
			System.out.println("at 222221 4bavadgfg" );
			//invoke the parent grouper's extended grouping processes e.g. summary decking
			extdDeckrProcessor.extendedContentProcessing();		//For each parent, whichever child was refreshed
			System.out.println("at 222221 4bavagfdgfd" );
																//   do child decking into parent
																// and if any child was decked,
																//   then do parent decking
																// mark all selfAuthored artifacts for upload
			System.out.println("@12341");
			System.out.println("@12343");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("EXITING THE CURRENT LOOP3");
			ErrorHandler.showErrorAndQuit(commons, "Error ExtdDeckerProcMaster run", e);
		}
	}
}