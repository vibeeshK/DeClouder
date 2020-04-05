package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

import commonTechs.OrchestrationData;
import commonTechs.OrchestrationUI;

public class ColbTrkClientOrchestrator implements Runnable {
	/*
	 * Client side Orchestrator that syncs up catalogs and contents between desktop and server
	 */			

	public OrchestrationData orchestrationData; 
	OrchestrationUI orchestrationUI;
	Thread orchestrationUIThread;		
	HashMap<String, RootPojo> rootPojosFromDBmap;
	Commons commons;
	//CommonUIData commonUIData;
	
	public ColbTrkClientOrchestrator (Commons inCommons){

		//commonUIData = inCommonUIData;
		commons = inCommons;
		rootPojosFromDBmap = null;
		
		SysCompRefresh.compRefresh(commons);

		Commons.logger.info("ColbTrkClientOrchestrator starting up - logging set to info");
		System.out.println("ColbTrkClientOrchestrator starting up; printing directly via sysout");

		//System.out.println("Display value in client orchestrator constructor is " + commonUIData.getESPoTDisplay());

		orchestrationData = new OrchestrationData(commons.userName,"ColbTrkClientOrchestrator",commons.applicationIcon);
		orchestrationUI = new OrchestrationUI(orchestrationData);
		orchestrationUIThread = new Thread(orchestrationUI);
	}
		

	public void run() {
	
		Commons.logger.info("ColbTrkClientOrchestrator started0.2 - logging set to info");

		orchestrationUIThread.start();
		
		Commons.logger.info("ColbTrkClientOrchestrator started2 - logging set to info");
		System.out.println("ColbTrkClientOrchestrator started2 printing directly via sysout");

		try {
			System.out.println("at 2");

			HashMap<String, DesktopRootProcessor> rootProcessors = new HashMap<String, DesktopRootProcessor>();

			while (orchestrationData.getOkayToContinue()) {

				rootPojosFromDBmap = PublishedRootsHandler.getPublishedRoots(commons);

				System.out.println("At ColbTrkClientOrchestrator commons = " + commons);
				System.out.println("At ColbTrkClientOrchestrator rootPojosFromDBmap size = " + rootPojosFromDBmap.size());
				System.out.println("At ColbTrkClientOrchestrator rootPojosFromDBmap key set  = " + rootPojosFromDBmap.keySet());

				SubscribedRootsPojo subscribedRootsPojo = new SubscribedRootsPojo(commons);
				
				//*********************************************//
				// Initial check for default root check Starts
				//*********************************************//
				// To handle initial process immediately install:
				// If the default root is subscribed already, force subscribe.
				//*********************************************//
				String[] rootsNicks;
				{
					//String[] rootsNicks = new String[subscribedRootsPojo.getRootNickList().size()];

					String currentRoot = commons.getCurrentRootNick();

					if (subscribedRootsPojo.getRootNickList().size() == 0 || subscribedRootsPojo.getRootNickList().contains(currentRoot)){
						System.out.println("At ColbTrkClientOrchestrator Subscribed roots size is = " + subscribedRootsPojo.getRootNickList().size());
						System.out.println("At ColbTrkClientOrchestrator missing currentRoot is = " + currentRoot);
						
//						Commons tempCommons = Commons.getInstance(Commons.CLIENT_MACHINE,currentRoot);
//						CommonData tempCommonData = CommonData.getInstance(tempCommons);
//						SubscribedRootsPojo tempSubscribedRootsPojo = new SubscribedRootsPojo(tempCommonData);
//
//						tempSubscribedRootsPojo.addSubscription(currentRoot);
						System.out.println("At ColbTrkClientOrchestrator Subscribed roots size after adding currentRoot is = " + subscribedRootsPojo.getRootNickList().size());
				
						
						rootsNicks = new String[subscribedRootsPojo.getRootNickList().size() + 1];
						subscribedRootsPojo.getRootNickList().toArray(rootsNicks);
						rootsNicks[subscribedRootsPojo.getRootNickList().size()] = currentRoot;
						System.out.println("AAA At ColbTrkClientOrchestrator subscribedRootsPojo.getRootNickList().size() is = " + subscribedRootsPojo.getRootNickList().size());
						System.out.println("AAA At ColbTrkClientOrchestrator Subscribed roots rootsNicks[subscribedRootsPojo.getRootNickList().size()] is = " + rootsNicks[subscribedRootsPojo.getRootNickList().size()]);
						System.out.println("AAA At ColbTrkClientOrchestrator Subscribed roots rootsNicks[0] is = " + rootsNicks[0]);

					} else {
						rootsNicks = new String[subscribedRootsPojo.getRootNickList().size()];						
						subscribedRootsPojo.getRootNickList().toArray(rootsNicks);
						System.out.println("BBB At ColbTrkClientOrchestrator subscribedRootsPojo.getRootNickList().size() is = " + subscribedRootsPojo.getRootNickList().size());
						System.out.println("BBB At ColbTrkClientOrchestrator Subscribed roots rootsNicks[subscribedRootsPojo.getRootNickList().size()] is = " + rootsNicks[subscribedRootsPojo.getRootNickList().size()]);
					}
				}
				System.out.println("CCC At ColbTrkClientOrchestrator Subscribed roots rootsNicks[0] is = " + rootsNicks[0]);

				//System.exit(8);

				// Initial check for default root check Ends
				//*********************************************//

				//String[] rootsNicks = new String[subscribedRootsPojo.getRootNickList().size()];
				//subscribedRootsPojo.getRootNickList().toArray(rootsNicks);
				
				DesktopRootProcessor rootProcessor = null;
				for (int rootCount = 0; rootCount < rootsNicks.length; rootCount++) {
					
					System.out.println("At ColbTrkClientOrchestrator rootCount = " + rootCount);
					System.out.println("At ColbTrkClientOrchestrator rootsNicks[rootCount] = " + rootsNicks[rootCount]);
					System.out.println("At ColbTrkClientOrchestrator rootPojosFromDBmap = " + rootPojosFromDBmap);
					System.out.println("At ColbTrkClientOrchestrator rootPojosFromDBmap.get(rootsNicks[rootCount]) = " + rootPojosFromDBmap.get(rootsNicks[rootCount]));
					System.out.println("At ColbTrkClientOrchestrator rootPojosFromDBmap.get(rootsNicks[rootCount]).rootType = " + rootPojosFromDBmap.get(rootsNicks[rootCount]).rootType);
					
					if (!rootPojosFromDBmap.get(rootsNicks[rootCount]).rootType.equalsIgnoreCase(RootPojo.RegRootType)) {
						continue;
					}

					//rootProcessor = null;
					System.out.println("At ColbTrkClientOrchestrator iniitating rootNick  = " + rootsNicks[rootCount]);
					
					System.out.println("rootMap: SIZE = " + rootProcessors.size());
					System.out.println("rootCount = " + rootCount);
					System.out.println("rootsList = " + rootPojosFromDBmap);
					System.out.println("rootsList.get(rootNick) = " + rootPojosFromDBmap.get(rootsNicks[rootCount]));
					System.out.println("rootMap = " + rootProcessors);
					System.out.println("rootPojosFromDBmap.get(rootsNicks[rootCount])" + rootPojosFromDBmap.get(rootsNicks[rootCount]));
					System.out.println("rootProcessors.containsKey(rootPojosFromDBmap.get(rootsNicks[rootCount])) = " + rootProcessors.containsKey(rootsNicks[rootCount]));
	
					if (!rootProcessors.containsKey(rootsNicks[rootCount])) {

						System.out.println("starting thread as it did not already contain key for " + rootsNicks[rootCount]);
						
						try {
							rootProcessor = new DesktopRootProcessor(rootPojosFromDBmap
									.get(rootsNicks[rootCount]),orchestrationData);
						} catch (IOException | ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Commons.logger.error("ColbTrkClientOrchestrator error rootProcessor " + rootsNicks[rootCount]);
						}
						rootProcessors.put(rootsNicks[rootCount],
								rootProcessor);

						System.out.println("rootMap: SIZEbbb1 = " + rootProcessors.size());
						System.out.println("initiating thread for:" + rootsNicks[rootCount]);
						
						new Thread(rootProcessor).start();
						System.out.println("initiated thread for:" + rootsNicks[rootCount]);
					} else {
						rootProcessor = rootProcessors.get(rootsNicks[rootCount]);
						synchronized (rootProcessor) {
							rootProcessor.notify();
						}
						System.out.println("notified thread for:" + rootsNicks[rootCount]);
					}
					System.out.println("***********Orchestrator rootCount:" + rootCount);
					System.out.println("***********Orchestrator rootCount:" + rootCount);
					System.out.println("***********Orchestrator rootCount:" + rootCount);
					System.out.println("***********Orchestrator rootCount:" + rootCount);
				}

				System.out.println("Sleeping Client Orchestrator");

				System.out.println("goint to sleep for " + orchestrationData.getHealthCheckIntervalInSeconds() + " seconds");

				Thread.sleep(orchestrationData.getHealthCheckIntervalInSeconds() * 1000);	//sleep for x milliseconds

				System.out.println("resuming Client Orchestrator");
				System.out.println(" orchestrationData okToContinue 11 is " + orchestrationData.getOkayToContinue());
			}
			System.out.println(" orchestrationData okToContinue 12 is " + orchestrationData.getOkayToContinue());

		} catch (InterruptedException e) {
			System.out.println("resuming Client Orchestrator post sleep " + e);
			e.printStackTrace();
		}
		System.out.println("at 4");
	}
}