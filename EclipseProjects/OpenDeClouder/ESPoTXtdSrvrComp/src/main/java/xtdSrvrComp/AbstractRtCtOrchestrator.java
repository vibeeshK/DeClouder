package xtdSrvrComp;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import commonTechs.OrchestrationData;
import commonTechs.OrchestrationUI;
import espot.Commons;
import espot.ErrorHandler;
import espot.PublishedRootsHandler;
import espot.RootPojo;
import espot.SysCompRefresh;

public abstract class AbstractRtCtOrchestrator {
	/*
	 * Abstract processor for the extended processing
	 */
	HashMap<String, RootPojo> rootPojosFromDBmap = null;
	HashMap<String, XtdStdContentProcMaster> rtCtProcessThreads = null;

	XtdCommons initialCommons = null;
	OrchestrationData orchestrationData;
	
	public AbstractRtCtOrchestrator (int inProcessType, String inARG_XtdCtlgSrvrPropFileName) throws IOException, ParseException{
		rootPojosFromDBmap = null;
		rtCtProcessThreads = new HashMap<String, XtdStdContentProcMaster>();
		System.out.println("inProcessType = " + inProcessType);

		initialCommons = XtdCommons.getInstance(inProcessType,null,inARG_XtdCtlgSrvrPropFileName);
		System.out.println("1 Abstract Orchestrator AbstractRtCtOrchestrator commons extdSrvrContentTypes is " + initialCommons.xtdSrvrContentTypes );

		SysCompRefresh.compRefresh(initialCommons);
		System.out.println("2 Abstract Orchestrator AbstractRtCtOrchestrator commons extdSrvrContentTypes is " + initialCommons.xtdSrvrContentTypes );

		Commons.logger.info(this.getClass().getSimpleName() + " starting up - logging set to info");
		System.out.println("Extended Orchestrator starting up; printing directly via sysout");

		orchestrationData = new OrchestrationData(initialCommons.userName,this.getClass().getSimpleName(),initialCommons.applicationIcon);
		OrchestrationUI orchestrationUI = new OrchestrationUI(orchestrationData);
		Thread orchestrationUIThread = new Thread(orchestrationUI);
		orchestrationUIThread.start();

		Commons.logger.info(this.getClass().getSimpleName() + " has started - logging set to info");
		System.out.println("Extended Orchestrator started2 printing directly via sysout");
	}

	public abstract int getSleepInterval();
	public abstract boolean isThisRootToBeProcessed(String inRootNick);
	public abstract boolean isThisContentTypeToBeProcessed(String inContentType);
	public abstract XtdStdContentProcMaster getProcesor(RootPojo inRootPojo, String inProcessingContentType);

	public XtdStdContentProcMaster getOrchestratedThread(RootPojo inRootPojo, String inProcessingContentType){
		System.out.println("getOrchestratedThread inProcessingContentType is " + inProcessingContentType);		
		System.out.println("getOrchestratedThread commons is " + initialCommons);

		return getProcesor(inRootPojo, inProcessingContentType);
	}

	private String keyFormation(String inRootNick,String inContentType){
		String keyFormed = (inRootNick + "&&" + inContentType);
		System.out.println("-------At keyFormation-----");
		System.out.println("inRootNick = " + inRootNick);
		System.out.println("inContentType = " + inContentType);
		System.out.println("keyFormed = " + keyFormed);
		return keyFormed;
	}
	
	public void orchestration() {
		try {
			System.out.println("Abstract Orchestrator begins at 1");
			System.out.println("Abstract Orchestrator begins at 1 at commons is " + initialCommons);
			//boolean nothingToProcess = false;
			//while (!nothingToProcess) {
			while (orchestrationData.getOkayToContinue()) {
				rootPojosFromDBmap = PublishedRootsHandler.getPublishedRoots(initialCommons);
				for (int rootCount = 0; rootCount < initialCommons.extendedSrvrRtNicks.length; rootCount++) {
					String processingRootNick = initialCommons.extendedSrvrRtNicks[rootCount];
					if (!rootPojosFromDBmap.get(processingRootNick).rootType.equalsIgnoreCase(RootPojo.RegRootType)) {
						continue;
					}
					if (!isThisRootToBeProcessed(processingRootNick)){
						continue;
					}
					System.out.println("Abstract Orchestrator commons is " + initialCommons);
					System.out.println("Abstract Orchestrator commons.extdSrvrContentTypes is " + initialCommons.xtdSrvrContentTypes );
					System.out.println("Abstract Orchestrator commons.extdSrvrContentTypes.length is " + initialCommons.xtdSrvrContentTypes.length);

					for (int contentTypeCnt = 0; contentTypeCnt < initialCommons.xtdSrvrContentTypes.length; contentTypeCnt++) {
						String processingContentType = initialCommons.xtdSrvrContentTypes[contentTypeCnt];
						String proecessingKey = keyFormation(processingRootNick,processingContentType);

						System.out.println("at orchestration contentTypeCnt is " + contentTypeCnt);
						System.out.println("at orchestration processingContentType is " + processingContentType);
						XtdStdContentProcMaster xtdStdContentProcMaster = null;
						
						if (!isThisContentTypeToBeProcessed(processingContentType)){
							continue;
						}

						System.out.println("still at orchestration contentTypeCnt is " + contentTypeCnt);
						System.out.println("still at orchestration processingContentType is " + processingContentType);
						
						if (!rtCtProcessThreads.containsKey(proecessingKey)) {
							System.out.println("check here did not contain key for" + proecessingKey);
							System.out.println("rootMap: SIZEbbb = " + rtCtProcessThreads.size());
							
					        xtdStdContentProcMaster = getOrchestratedThread(rootPojosFromDBmap.get(processingRootNick), processingContentType);

					        rtCtProcessThreads.put(proecessingKey, xtdStdContentProcMaster);

							System.out.println("initiated thread for:" + proecessingKey);
						} else {
							xtdStdContentProcMaster = rtCtProcessThreads.get(proecessingKey);
						}
						System.out.println("calling xtdStdContentProcMaster.processRecords for " + proecessingKey);		
						
				        xtdStdContentProcMaster.coreProcessOfRecords();

				        System.out.println("***********Orchestrator contentTypeCnt:" + contentTypeCnt);
				        System.out.println("***********after call to processRecord for contentType " + proecessingKey);
					}
					System.out.println("***********Orchestrator rootCount:" + rootCount);
					XtdStdContentProcMaster anyoneXtdStdContentProcMaster = rtCtProcessThreads.entrySet().iterator().next().getValue();
					anyoneXtdStdContentProcMaster.endProcessOfRecords();
					System.out.println("***********Orchestrator endProcessOfRec completed as well ");
				}

				System.out.println("Sleeping ESPoTExtdCatlgSrvrOrchestrator");
				Thread.sleep(getSleepInterval());
				System.out.println("resuming ESPoTExtdCatlgSrvrOrchestrator");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(initialCommons, "Error AbstractRtCtOrchestrator orchestration", e);
		}
		System.out.println("at 4");		
	}
}