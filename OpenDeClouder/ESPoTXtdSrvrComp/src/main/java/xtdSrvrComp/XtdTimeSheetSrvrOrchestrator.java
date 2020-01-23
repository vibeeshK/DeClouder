package xtdSrvrComp;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import espot.Commons;
import espot.ErrorHandler;
import espot.RootPojo;

public class XtdTimeSheetSrvrOrchestrator extends AbstractRtCtOrchestrator {
	/*
	 * NOT USED.
	 * 25june2019: this process has been merged with the projTrackerOrchestrator; hence we can discontinue this
	 */
	
	final static String TimeShTrigerContTpConst = "TimeShTrigger";
	final static int ProcessInterval = 1 * 5 * 1000;
	final static String ARG_XtdCtlgSrvrPropFileName = Commons.CONFIGFOLDERPREFIX + "extdCtlgSrvrForTmShProcessor.properties";
	
	public XtdTimeSheetSrvrOrchestrator() throws IOException, ParseException {
		super(Commons.EXTENDED_CATALOG_SERVER,ARG_XtdCtlgSrvrPropFileName);
		System.out.println("1 TimeSheetSrvrOrchestrator commons is " + initialCommons);
	}

	public HashMap<String, XtdStdContentProcMaster> getProcessorsMap(){
		return new HashMap<String, XtdStdContentProcMaster>();
	}
	
	public boolean isThisRootToBeProcessed(String inRootNick){		
		return true; //the rootNick check done within abstract parent holds good;
	}
	
	public boolean isThisContentTypeToBeProcessed(String inContentType){
		// Additional check on top of contentType check done in abstract parent
		if (inContentType.equalsIgnoreCase(TimeShTrigerContTpConst)){
			return true;
		} else {
			return false;
		}
	}

	public XtdStdContentProcMaster getProcesor(RootPojo inRootPojo, String inProcessingContentType) {
		XtdCatalogPersistenceManager xtdCatalogPersistenceMgr = null;
		System.out.println("XtdStdContentProcMaster getProcesor commons is " + initialCommons);

		XtdCommons xtdCommon = null;
		try {
			xtdCommon = new XtdCommons(Commons.EXTENDED_CATALOG_SERVER, inRootPojo.rootNick, ARG_XtdCtlgSrvrPropFileName);
			xtdCatalogPersistenceMgr = new XtdTmShCatlogPersistenceMgr(inRootPojo, initialCommons,
												Commons.EXTENDED_CATALOG_SERVER);
		} catch (ClassNotFoundException | IOException | ParseException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(initialCommons, "Error TimeSheetSrvrOrchestrator getProcesor " + inRootPojo.rootNick + " " + inProcessingContentType, e);
		}
		System.out.println("XtdStdContentProcMaster before return getProcesor commons is " + initialCommons);
		return new XtdStdContentProcMaster(initialCommons,inRootPojo,inProcessingContentType, xtdCatalogPersistenceMgr);
	}

	public int getSleepInterval() {
		return ProcessInterval;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		XtdTimeSheetSrvrOrchestrator timeSheetSrvrOrchestrator = new XtdTimeSheetSrvrOrchestrator();
		timeSheetSrvrOrchestrator.orchestration();		
	}
}