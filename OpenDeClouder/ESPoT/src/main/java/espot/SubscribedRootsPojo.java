package espot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SubscribedRootsPojo {
	/*
	 * This class holds details of the subscribed roots and lets to update subscriptions
	 */

	private CatelogPersistenceManager catelogPersistenceManager; // referred only during add/remove of subcriptions
	private Commons commons;
	private HashMap<String, RootPojo> subscribedRootsMap = null;
	private ArrayList<String> subscribedRootNicks = null;
	private Document subscribedRootsDoc = null;
	private HashMap<String, RootPojo> publishedRootsMap = null;
	private boolean invokedWithAddOption = false;

	public SubscribedRootsPojo(
			CommonData inCommonData) {
	// Constructor for add/remove of subscriptions
		
		invokedWithAddOption = true;
		commons = inCommonData.getCommons();
		catelogPersistenceManager = inCommonData.getCatelogPersistenceManager();
		commonInitialize();
	}

	public SubscribedRootsPojo(
			Commons inCommons) {		
		// Constructor for read only. Dont use this when adding or removing subscriptions
		System.out.println("At SubscribedRootsPojo commons = " + inCommons);
		commons = inCommons;
		commonInitialize();
	}

	private void commonInitialize() {		
		System.out.println("At commonInitialize commons = " + commons);
		publishedRootsMap = PublishedRootsHandler.getPublishedRoots(commons);
		readSubscribedRoots();
	}	
	
	public boolean doesRootNickExist(String inRootNick){
		boolean rootNickExist = subscribedRootNicks.contains(inRootNick);
		return rootNickExist;
	}
	
	public ArrayList<String> getRootNickList(){
		return subscribedRootNicks;
	}

	private void readSubscribedRoots() {
		System.out
				.println("@ readSubscribedRootNicks commons.subscribedRootNicksFileName = "
						+ commons.subscribedRootNicksFileName);
		try {
			subscribedRootsDoc = commons
					.getDocumentFromXMLFile(commons.subscribedRootNicksFileName);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in SubscribedRootsPojo readSubscribedRoots", e);
		}
		int rootsTotal = subscribedRootsDoc.getElementsByTagName("Root")
				.getLength();
		System.out
				.println("@ PublishedRootsHandler rootsTotal = " + rootsTotal);

		subscribedRootNicks = new ArrayList<String>();
		subscribedRootsMap = new HashMap<String, RootPojo>();

		for (int rootCount = 0; rootCount < rootsTotal; rootCount++) {
			String subscribedRootNick = ((Element) subscribedRootsDoc
					.getElementsByTagName("Root").item(rootCount))
					.getAttribute("RootNick");
			subscribedRootNicks.add(subscribedRootNick);
			subscribedRootsMap.put(subscribedRootNick, publishedRootsMap
					.get(subscribedRootNick));
		}
	}

	void removeSubscription(String inRootNick) {
		// subscribedRootsDoc
		System.out.println("@ removeSubscription inRootNick = " + inRootNick);
		for (int rootCount = 0; rootCount < subscribedRootNicks.size(); rootCount++) {
			String checkRootNick = ((Element) subscribedRootsDoc
					.getElementsByTagName("Root").item(rootCount))
					.getAttribute("RootNick");
			System.out.println("@ removeSubscription rootCount = " + rootCount);
			System.out.println("@ removeSubscription checkRootNick = "
					+ checkRootNick);

			if (checkRootNick.equalsIgnoreCase(inRootNick)) {
				System.out
						.println("@ removeSubscription removing checkRootNick = "
								+ checkRootNick);

				Node checkRootNickNode = subscribedRootsDoc
						.getElementsByTagName("Root").item(rootCount);
				System.out.println("selected rootNick = "
						+ ((Element) checkRootNickNode)
								.getAttribute("RootNick"));
				checkRootNickNode.getParentNode()
						.removeChild(checkRootNickNode);
				subscribedRootsMap.remove(inRootNick);
				subscribedRootNicks.remove(rootCount);
				break;
			}
		}
		try {
			commons.saveXMLFileFromDocument(subscribedRootsDoc,
					commons.subscribedRootNicksFileName);
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in SubscribedRootsPojo removeSubscription " + " " + inRootNick, e);			
		}
	}

	void addSubscription(String inRootNick) {
		// subscribedRootsDoc
		System.out.println("@ addSubscription inRootNick = " + inRootNick);
		if (!invokedWithAddOption) {
			ErrorHandler.showErrorAndQuit(commons, "Error92 at addSubscription for " + inRootNick + ", but this function is out of place here as invokedWithAddOption = " + invokedWithAddOption);
			return;
		}

		subscribedRootNicks.add(inRootNick);
		subscribedRootsMap.put(inRootNick, publishedRootsMap.get(inRootNick));
		Element baseElement = (Element) subscribedRootsDoc
				.getElementsByTagName("Roots").item(0);

		Element newRootElement = subscribedRootsDoc.createElement("Root");
		newRootElement.setAttribute("RootNick", inRootNick);
		baseElement.appendChild(newRootElement);

		commons.createFoldersForRootNick(inRootNick);

		RootPojo selectedRootPojo = publishedRootsMap.get(inRootNick);
		System.out.println("selectedRootPojo = " + selectedRootPojo);
		RemoteAccesser remoteAccesser = RemoteAccessManager.getInstance(commons, inRootNick);

		CatalogDownloader catalogDownloaderOfRoot = new CatalogDownloader(
				commons, selectedRootPojo, remoteAccesser);
		try {
			catalogDownloaderOfRoot.downloadCatalog();
			catelogPersistenceManager.refreshForLatestCatalog();
			commons.saveXMLFileFromDocument(subscribedRootsDoc,
					commons.subscribedRootNicksFileName);
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in SubscribedRootsPojo addSubscription " + " " + inRootNick, e);			
		}
		System.out.println("@ addSubscription completed for inRootNick = " + inRootNick);
	}
}