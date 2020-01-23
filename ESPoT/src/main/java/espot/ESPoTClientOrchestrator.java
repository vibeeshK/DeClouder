package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

import commonTechs.OrchestrationData;
import commonTechs.OrchestrationUI;

public class ESPoTClientOrchestrator {
	/*
	 * Client side Orchestrator that syncs up catalogs and contents between desktop and server
	 */			

	public static void main(String[] args) throws IOException, ParseException {

		HashMap<String, RootPojo> rootPojosFromDBmap = null;
		Commons commons = Commons.getInstance(Commons.CLIENT_MACHINE);
		SysCompRefresh.compRefresh(commons);

		Commons.logger.info("ESPoTClientOrchestrator started - logging set to info");
		System.out.println("ESPoTClientOrchestrator started printing directly via sysout");

		OrchestrationData orchestrationData = new OrchestrationData(commons.userName,"ESPoTClientOrchestrator",commons.backgroundImagePathFileName); 
		OrchestrationUI orchestrationUI = new OrchestrationUI(orchestrationData);
		Thread orchestrationUIThread = new Thread(orchestrationUI);
		orchestrationUIThread.start();

		commons.logger.info("ESPoTClientOrchestrator started2 - logging set to info");
		System.out.println("ESPoTClientOrchestrator started2 printing directly via sysout");

		try {
			System.out.println("at 2");

			HashMap<String, DesktopRootProcessor> rootProcessors = new HashMap<String, DesktopRootProcessor>();

			while (orchestrationData.okToContinue) {

				rootPojosFromDBmap = PublishedRootsHandler.getPublishedRoots(commons);

				System.out.println("At ESPoTClientOrchestrator commons = " + commons);
				System.out.println("At ESPoTClientOrchestrator rootPojosFromDBmap size = " + rootPojosFromDBmap.size());
				System.out.println("At ESPoTClientOrchestrator rootPojosFromDBmap key set  = " + rootPojosFromDBmap.keySet());

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
						System.out.println("At ESPoTClientOrchestrator Subscribed roots size is = " + subscribedRootsPojo.getRootNickList().size());
						System.out.println("At ESPoTClientOrchestrator missing currentRoot is = " + currentRoot);
						
//						Commons tempCommons = Commons.getInstance(Commons.CLIENT_MACHINE,currentRoot);
//						CommonData tempCommonData = CommonData.getInstance(tempCommons);
//						SubscribedRootsPojo tempSubscribedRootsPojo = new SubscribedRootsPojo(tempCommonData);
//
//						tempSubscribedRootsPojo.addSubscription(currentRoot);
						System.out.println("At ESPoTClientOrchestrator Subscribed roots size after adding currentRoot is = " + subscribedRootsPojo.getRootNickList().size());
				
						
						rootsNicks = new String[subscribedRootsPojo.getRootNickList().size() + 1];
						subscribedRootsPojo.getRootNickList().toArray(rootsNicks);
						rootsNicks[subscribedRootsPojo.getRootNickList().size()] = currentRoot;
						System.out.println("AAA At ESPoTClientOrchestrator subscribedRootsPojo.getRootNickList().size() is = " + subscribedRootsPojo.getRootNickList().size());
						System.out.println("AAA At ESPoTClientOrchestrator Subscribed roots rootsNicks[subscribedRootsPojo.getRootNickList().size()] is = " + rootsNicks[subscribedRootsPojo.getRootNickList().size()]);
						System.out.println("AAA At ESPoTClientOrchestrator Subscribed roots rootsNicks[0] is = " + rootsNicks[0]);

					} else {
						rootsNicks = new String[subscribedRootsPojo.getRootNickList().size()];						
						subscribedRootsPojo.getRootNickList().toArray(rootsNicks);
						System.out.println("BBB At ESPoTClientOrchestrator subscribedRootsPojo.getRootNickList().size() is = " + subscribedRootsPojo.getRootNickList().size());
						System.out.println("BBB At ESPoTClientOrchestrator Subscribed roots rootsNicks[subscribedRootsPojo.getRootNickList().size()] is = " + rootsNicks[subscribedRootsPojo.getRootNickList().size()]);
					}
				}
				System.out.println("CCC At ESPoTClientOrchestrator Subscribed roots rootsNicks[0] is = " + rootsNicks[0]);

				//System.exit(8);

				// Initial check for default root check Ends
				//*********************************************//

				//String[] rootsNicks = new String[subscribedRootsPojo.getRootNickList().size()];
				//subscribedRootsPojo.getRootNickList().toArray(rootsNicks);
				
				DesktopRootProcessor rootProcessor = null;
				for (int rootCount = 0; rootCount < rootsNicks.length; rootCount++) {
					
					System.out.println("At ESPoTClientOrchestrator rootCount = " + rootCount);
					System.out.println("At ESPoTClientOrchestrator rootsNicks[rootCount] = " + rootsNicks[rootCount]);
					System.out.println("At ESPoTClientOrchestrator rootPojosFromDBmap = " + rootPojosFromDBmap);
					System.out.println("At ESPoTClientOrchestrator rootPojosFromDBmap.get(rootsNicks[rootCount]) = " + rootPojosFromDBmap.get(rootsNicks[rootCount]));
					System.out.println("At ESPoTClientOrchestrator rootPojosFromDBmap.get(rootsNicks[rootCount]).rootType = " + rootPojosFromDBmap.get(rootsNicks[rootCount]).rootType);
					
					if (!rootPojosFromDBmap.get(rootsNicks[rootCount]).rootType.equalsIgnoreCase(RootPojo.RegRootType)) {
						continue;
					}

					//rootProcessor = null;
					System.out.println("At ESPoTClientOrchestrator iniitating rootNick  = " + rootsNicks[rootCount]);
					
					System.out.println("rootMap: SIZE = " + rootProcessors.size());
					System.out.println("rootCount = " + rootCount);
					System.out.println("rootsList = " + rootPojosFromDBmap);
					System.out.println("rootsList.get(rootNick) = " + rootPojosFromDBmap.get(rootsNicks[rootCount]));
					System.out.println("rootMap = " + rootProcessors);
					System.out.println("rootPojosFromDBmap.get(rootsNicks[rootCount])" + rootPojosFromDBmap.get(rootsNicks[rootCount]));
					System.out.println("rootProcessors.containsKey(rootPojosFromDBmap.get(rootsNicks[rootCount])) = " + rootProcessors.containsKey(rootsNicks[rootCount]));
	
					if (!rootProcessors.containsKey(rootsNicks[rootCount])) {

						System.out.println("starting thread as it did not already contain key for" + rootsNicks[rootCount]);
						
						rootProcessor = new DesktopRootProcessor(rootPojosFromDBmap
								.get(rootsNicks[rootCount]),orchestrationData);
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

				Thread.sleep(orchestrationData.getHealthCheckIntervalInSeconds() * 1000);

				System.out.println("resuming Client Orchestrator");
				System.out.println(" orchestrationData okToContinue 11 is " + orchestrationData.okToContinue);
			}
			System.out.println(" orchestrationData okToContinue 12 is " + orchestrationData.okToContinue);

		} catch (InterruptedException e) {
			System.out.println("resuming Client Orchestrator post sleep " + e);
		}
		System.out.println("at 4");
	}
}