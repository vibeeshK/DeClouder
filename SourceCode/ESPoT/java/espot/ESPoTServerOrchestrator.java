
package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import commonTechs.OrchestrationData;
import commonTechs.OrchestrationUI;

public class ESPoTServerOrchestrator {
	/*
	 * Server side Orchestrator that processes the incoming requests for content updates and publishes new catalog
	 */			
	public static void main(String[] args) throws IOException, ParseException {
		Commons commons = Commons.getInstance(Commons.BASE_CATALOG_SERVER);
		
		HashMap<String, RootPojo> publishedRootsMap = PublishedRootsHandler.getPublishedRoots(commons);
		SysCompRefresh.compRefresh(commons);

		OrchestrationData orchestrationData = new OrchestrationData(commons.userName,"ESPoTServerOrchestrator",commons.backgroundImagePathFileName); 
		OrchestrationUI orchestrationUI = new OrchestrationUI(orchestrationData);
		Thread orchestrationUIThread = new Thread(orchestrationUI);
		orchestrationUIThread.start();		
		
		try {
			System.out.println("at 2");
			HashMap<String,MasterRootProcessor> rootProcessorMap = new HashMap<String,MasterRootProcessor>();

			while (orchestrationData.okToContinue) {
				
				MasterRootProcessor masterRootProcessr = null;
				for (int rootCount = 0; rootCount < commons.serverRootNicks.length; rootCount++) {
					masterRootProcessr = null;

					RootPojo currentRootPojo = publishedRootsMap.get(commons.serverRootNicks[rootCount]);

					System.out.println("rootCount = " + rootCount);
					System.out.println("currentRootPojo = " + currentRootPojo);
					System.out.println("commons.serverRootNicks[rootCount] = " + commons.serverRootNicks[rootCount]);

					if (!rootProcessorMap.containsKey(currentRootPojo.rootNick)) {

						try {
							masterRootProcessr = new MasterRootProcessor(currentRootPojo,orchestrationData);
						} catch (IOException | ParseException e) {

							e.printStackTrace();
							ErrorHandler.showErrorAndQuit(commons, "Error in ESPoTServerOrchestrator main ", e);
						}
						rootProcessorMap.put(
								currentRootPojo.rootNick,
								masterRootProcessr);
						System.out.println("1 masterRootProcessr = " + masterRootProcessr);
						System.out.println("Root = (" + rootCount + ") : " + currentRootPojo.rootNick);

						new Thread(masterRootProcessr).start();
					} else {
						System.out.println("Notifying the masterRootProcessor to resume");

						masterRootProcessr = rootProcessorMap.get(currentRootPojo.rootNick);

						System.out.println("2 masterRootProcessr = " + masterRootProcessr);
						
						synchronized (masterRootProcessr) {
							masterRootProcessr.notify();
							System.out.println("Notified masterRootProcessor");
						}
					}
				}
				System.out.println("Sleeping Server Orchestrator");
				System.out.println("goint to sleep for " + orchestrationData.getHealthCheckIntervalInSeconds() + " seconds");

				Thread.sleep(orchestrationData.getHealthCheckIntervalInSeconds() * 1000);

				System.out.println("resuming Server Orchestrator");
				System.out.println(" orchestrationDatab okToContinue 11 is " + orchestrationData.okToContinue);
			}
			System.out.println(" orchestrationDatab okToContinue 12 is " + orchestrationData.okToContinue);			
		} catch (InterruptedException e) {

			System.out.println("resuming server Orchestrator post sleep " + e);
		}
		System.out.println("at 4");
	}
}