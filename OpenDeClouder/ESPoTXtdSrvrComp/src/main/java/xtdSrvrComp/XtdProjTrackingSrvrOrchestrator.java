package xtdSrvrComp;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import espot.Commons;
import espot.ErrorHandler;
import espot.RootPojo;

public class XtdProjTrackingSrvrOrchestrator extends AbstractRtCtOrchestrator {
	/*
	 * This processor invokes extended Project Tracker process sequence on defined intervals
	 */
	final static int ProcessInterval = 1 * 5 * 1000;
	final static String ARG_XtdCtlgSrvrPropFileName = Commons.CONFIGFOLDERPREFIX + "extdCtlgSrvrForStdProcessor.properties";
	
	public XtdProjTrackingSrvrOrchestrator() throws IOException, ParseException {
		super(Commons.EXTENDED_CATALOG_SERVER,ARG_XtdCtlgSrvrPropFileName);
		System.out.println("1 ProjTrackingSrvrOrchestrator commons is " + initialCommons);
	}

	public HashMap<String, XtdStdContentProcMaster> getProcessorsMap(){
		return new HashMap<String, XtdStdContentProcMaster>();
	}
	
	public boolean isThisRootToBeProcessed(String inRootNick){		
		return true; //the rootNick check done within abstract parent holds good;
	}
	
	public boolean isThisContentTypeToBeProcessed(String inContentType){
		// Additional check on top of contentType check done in abstract parent
		List<String> myList =
				Arrays.asList(initialCommons.xtdSrvrContentTypes);
		if (myList.contains(inContentType)){
			System.out.println("isThisContentTypeToBeProcessed is true for inContentType is " + inContentType);
			
			return true;
		} else {
			System.out.println("isThisContentTypeToBeProcessed is false for inContentType is " + inContentType);
			return false;
		}
	}

	public XtdStdContentProcMaster getProcesor(RootPojo inRootPojo, String inProcessingContentType) {
		XtdCatalogPersistenceManager xtdCatalogPersistenceMgr = null;
		System.out.println("XtdStdContentProcMaster getProcesor commons is " + initialCommons);

		XtdCommons xtdCommon = null;
		try {
			xtdCommon = new XtdCommons(Commons.EXTENDED_CATALOG_SERVER, inRootPojo.rootNick, ARG_XtdCtlgSrvrPropFileName);
			xtdCatalogPersistenceMgr = new XtdTmShCatlogPersistenceMgr(inRootPojo, xtdCommon,
												Commons.EXTENDED_CATALOG_SERVER);
		} catch (ClassNotFoundException | IOException | ParseException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(initialCommons, "Error XtdProjTrackingSrvrOrchestrator getProcesor " + inRootPojo.rootNick + " " + inProcessingContentType, e);
		}
		System.out.println("XtdStdContentProcMaster before return getProcesor commons is " + xtdCommon);
		System.out.println("XtdStdContentProcMaster before return getProcesor inRootPojo.rootNick " + inRootPojo.rootNick);
		System.out.println("XtdStdContentProcMaster before return getProcesor inProcessingContentType " + inProcessingContentType);
		return new XtdStdContentProcMaster(xtdCommon,inRootPojo,inProcessingContentType, xtdCatalogPersistenceMgr);
	}

	public int getSleepInterval() {
		return ProcessInterval;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		XtdProjTrackingSrvrOrchestrator timeSheetSrvrOrchestrator = new XtdProjTrackingSrvrOrchestrator();
		timeSheetSrvrOrchestrator.orchestration();
	}
}