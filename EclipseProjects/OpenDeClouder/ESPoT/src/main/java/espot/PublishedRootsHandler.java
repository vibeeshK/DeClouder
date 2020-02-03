package espot;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PublishedRootsHandler {
	/*
	 * Holder of content from the published roots xml
	 */	
	Commons commons = null;
	Document pubishedRootsDoc = null;
	HashMap<String, RootPojo> publishedRootsMap = null;
	int rootsTotal = 0;
	private static PublishedRootsHandler publishedRootsHandler = null;

	public static synchronized HashMap<String, RootPojo> getPublishedRoots(Commons inCommons) {
		System.out.println("@ getPublishedRoots ");

		if (publishedRootsHandler == null) {
			publishedRootsHandler = new PublishedRootsHandler(inCommons);
		}
		return publishedRootsHandler.publishedRootsMap;		
	}

	private PublishedRootsHandler(Commons inCommons) {
		commons = inCommons;
		try {
			System.out.println("@ PublishedRootsHandler commons.pulishedRootsFile = " + commons.publishedRootsFileName);
			pubishedRootsDoc = commons.getDocumentFromXMLFile(commons.publishedRootsFileName);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in PublishedRootsHandler PublishedRootsHandler ", e);
		}

		rootsTotal = pubishedRootsDoc.getElementsByTagName("Root").getLength();
		System.out.println("@ PublishedRootsHandler rootsTotal = " + rootsTotal);

		publishedRootsMap = new HashMap<String,RootPojo>();

		for (int rootCount=0;rootCount < rootsTotal; rootCount++) {
			RootPojo rootPojo = new RootPojo();			

			/* The below are attributes in published roots xml
			RootNick="SysController"
			RootType="System"
			RootString="D:\Kannan\Java\ESPoT\Controller" 
			RemoteAccesserType="remoteAccessers.WindowsAccesser"
			 */

			rootPojo.rootNick = ((Element) pubishedRootsDoc.getElementsByTagName("Root").item(rootCount)).getAttribute("RootNick");
			rootPojo.rootType = ((Element) pubishedRootsDoc.getElementsByTagName("Root").item(rootCount)).getAttribute("RootType");
			rootPojo.rootString = ((Element) pubishedRootsDoc.getElementsByTagName("Root").item(rootCount)).getAttribute("RootString");
			rootPojo.remoteAccesserType = ((Element) pubishedRootsDoc.getElementsByTagName("Root").item(rootCount)).getAttribute("RemoteAccesserType");
			rootPojo.fileSeparator = ((Element) pubishedRootsDoc.getElementsByTagName("Root").item(rootCount)).getAttribute("FileSeparator");

			String requiresInternetAsTx = ((Element) pubishedRootsDoc.getElementsByTagName("Root").item(rootCount)).getAttribute("RequiresInternet");
			if (requiresInternetAsTx.equalsIgnoreCase("Yes")) {
				rootPojo.requiresInternet = true;
			}
			
			System.out.println("@ PublishedRootsHandler Loop rootCount = " + rootCount);
			System.out.println("@ PublishedRootsHandler rootPojo.rootNick = " + rootPojo.rootNick);
			System.out.println("@ PublishedRootsHandler rootPojo.rootString = " + rootPojo.rootString);
			
			if (commons.isWebURI(rootPojo.rootString)){
				rootPojo.rootPrefix = commons.getHostName(rootPojo.rootString);
			} else {
				rootPojo.rootPrefix = rootPojo.rootString;
			}
			System.out.println("@ PublishedRootsHandler rootPojo.rootPrefix = " + rootPojo.rootPrefix);
			
			publishedRootsMap.put(rootPojo.rootNick,rootPojo);
			System.out.println("@ Added rootPojo.rootNick = " + rootPojo.rootNick);
			System.out.println("@ Added rootPojo.rootString = " + rootPojo.rootString);
			System.out.println("@ Added rootPojo.fileSeparator = " + rootPojo.fileSeparator);
			System.out.println("@ Added rootPojo.rootPrefix = " + rootPojo.rootPrefix);
		}
	}
}