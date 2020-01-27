package espot;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CatalogDownloadDtlsHandler {
	/*
	 * This class helps absorbing the catalog download details into the application object
	 */
	private final int mapRefreshGap = 5; // time seconds to refresh hashMap
	private Commons commons;
	private static CatalogDownloadDtlsHandler catalogDownloadsHandler = null;
	private HashMap <String,CatalogDownloadDetailsPojo> catalogDownloadDetailsMap = null;
	private Calendar mapRefreshedTime = null;
	static final String ROOTSTX = "Roots";
	static final String ROOTTX = "Root";
	static final String ROOTNICKTX = "RootNick";
	static final String DOWNLOADEDFILENAMETX = "DownloadedFileName";
	static final String DOWNLOADEDTIMETX = "DownloadedTime";

	public static CatalogDownloadDtlsHandler getInstance(Commons inCommons){
		if (catalogDownloadsHandler == null) {
			catalogDownloadsHandler = new CatalogDownloadDtlsHandler(inCommons);
			System.out.println("commons1 : " + catalogDownloadsHandler.commons);
		} else {
			catalogDownloadsHandler.refreshCatalogDownloadDetailsMap();
		}
		System.out.println("commons2 : " + catalogDownloadsHandler.commons);

		return catalogDownloadsHandler;
	}
	private CatalogDownloadDtlsHandler(Commons inCommon){
		commons = inCommon;
		System.out.println("commons : " + commons);
		refreshCatalogDownloadDetailsMap();
	}
	
	private synchronized Document getCatalogDownloadsDetailsDoc(){
		Document catalogDownloadsDetailsDoc = null;
		try {
			System.out.println("commons : " + commons);
			System.out.println("commons.downloadedCatalogDetailsFile : " + commons.downloadedCatalogDetailsFile);
			catalogDownloadsDetailsDoc = commons.getDocumentFromXMLFile(commons.downloadedCatalogDetailsFile);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in CatalogDownloadDtlsHandler getCatalogDownloadsDetailsDoc", e);
		}
		return catalogDownloadsDetailsDoc;
	}

	public void refreshCatalogDownloadDetailsMap(){
		System.out.println("starting refreshCatalogDownloadDetailsMap mapRefreshedTime is " + mapRefreshedTime);
		System.out.println("starting refreshCatalogDownloadDetailsMap mapRefreshGap is " + mapRefreshGap);
		
		if (mapRefreshedTime != null && !commons.hasTimeSecElapsed(mapRefreshedTime.getTime(),mapRefreshGap)) {
			System.out.println("At refreshCatalogDownloadDetailsMap time too short to refresh for all already downloaded roots");
			return; // no need to refresh as its too short after previous refresh
		}
		coreRefreshCatalogDownloadDetailsMap();
	}

	public synchronized void coreRefreshCatalogDownloadDetailsMap(){
		System.out.println("starting coreRefreshCatalogDownloadDetailsMap");

		Document catalogDownloadsDetailsDoc = getCatalogDownloadsDetailsDoc();
		NodeList rootDetailsList  = ((Element) catalogDownloadsDetailsDoc.getElementsByTagName(ROOTSTX).item(0)).getElementsByTagName(ROOTTX);

		catalogDownloadDetailsMap = new HashMap<String,CatalogDownloadDetailsPojo>();

		for (int nodeCount = 0; nodeCount < rootDetailsList.getLength(); nodeCount++){
			System.out.println("At refreshCatalogDownloadDetailsMap nodeCount " + nodeCount);
			Element rootDetailElement =  (Element) rootDetailsList.item(nodeCount);
			CatalogDownloadDetailsPojo catalogDownloadDetailsPojo = new CatalogDownloadDetailsPojo();
			catalogDownloadDetailsPojo.rootNick = rootDetailElement.getAttribute(ROOTNICKTX);
			System.out.println("At refreshCatalogDownloadDetailsMap catalogDownloadDetailsPojo.rootNick " + catalogDownloadDetailsPojo.rootNick);
			catalogDownloadDetailsPojo.downloadedFileName = rootDetailElement.getAttribute(DOWNLOADEDFILENAMETX);
			System.out.println("At refreshCatalogDownloadDetailsMap catalogDownloadDetailsPojo.downloadedFileName " + catalogDownloadDetailsPojo.downloadedFileName);
			catalogDownloadDetailsPojo.downloadedTime = rootDetailElement.getAttribute(DOWNLOADEDTIMETX);
			System.out.println("At refreshCatalogDownloadDetailsMap catalogDownloadDetailsPojo.downloadedTime " + catalogDownloadDetailsPojo.downloadedTime);
			catalogDownloadDetailsMap.put(catalogDownloadDetailsPojo.rootNick, catalogDownloadDetailsPojo);
		}
		mapRefreshedTime = commons.getCalendarTS();
	}

	public String getCatalogDownLoadedFileName(String inRootNick){
		System.out.println("starting getCatalogDownLoadedFileName for inRootNick " + inRootNick);
		refreshCatalogDownloadDetailsMap();
		String catalogDownLoadedFileName = null;
		if (catalogDownloadDetailsMap.containsKey(inRootNick)){
			catalogDownLoadedFileName = commons.getFullLocalPathFileNameOfDownloadedDbFile(inRootNick,
												catalogDownloadDetailsMap.get(inRootNick).downloadedFileName);
		} else {
			//Commons.logger.error("At end of getCatalogDownLoadedFileName inRootNick " + inRootNick + " is not downlaoded yet");
			System.out.println("At end of getCatalogDownLoadedFileName inRootNick " + inRootNick + " is not downlaoded yet");
			ErrorHandler.showErrorAndQuit(commons, "At end of getCatalogDownLoadedFileName inRootNick " + inRootNick + " is not downlaoded yet");
		}
		return catalogDownLoadedFileName;
	}
	
	public boolean isFreshDownLoadAllowed(String inRootNick){
		refreshCatalogDownloadDetailsMap();
		if (!catalogDownloadDetailsMap.containsKey(inRootNick)){
			System.out.println("Continue for new Catalog download as there was no prev download");
			return true;
		}
		try {
			System.out.println("prevCatalogDownloadedTme          : " + commons.getDateFromString(catalogDownloadDetailsMap.get(inRootNick).downloadedTime));
			System.out.println("commons.catalogDownloadTimeGapSec : " + commons.catalogDownloadTimeGapSec);

			if (commons.hasTimeSecElapsed(commons.getDateFromString(catalogDownloadDetailsMap.get(inRootNick).downloadedTime),commons.catalogDownloadTimeGapSec)) {
				System.out.println("Continue for new Catalog download");
				return true;
			}
		} catch (ParseException e) {
			//e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in CatalogDownloadDtlsHandler isFreshDownLoadAllowed " + inRootNick, e);
		}
		System.out.println("Gap is too short for a new Catalog download");
		return false;		// Gap is too short for a new download
	}
		
	public synchronized void updateCatalogDownloadDetail(String inRootNick, String inDownloadedFileName, String inDownloadedTime) {
		System.out.println("at updateCatalogDownloadDetail for inRootNick " + inRootNick);
		System.out.println("at updateCatalogDownloadDetail for inDownloadedFileName " + inDownloadedFileName);

		Document catalogDownloadsDetailsDoc = getCatalogDownloadsDetailsDoc();
		try {
			Element rootsElement = (Element) catalogDownloadsDetailsDoc.getElementsByTagName(ROOTSTX).item(0);
			if (!catalogDownloadDetailsMap.containsKey(inRootNick)){
				Element newRootDtl = catalogDownloadsDetailsDoc.createElement(ROOTTX);
				newRootDtl.setAttribute(ROOTNICKTX,inRootNick);
				newRootDtl.setAttribute(DOWNLOADEDTIMETX,inDownloadedTime);
				newRootDtl.setAttribute(DOWNLOADEDFILENAMETX,inDownloadedFileName);
				rootsElement.appendChild(newRootDtl);
			} else {
				NodeList rootDetailsList  = rootsElement.getElementsByTagName(ROOTTX);
				for (int nodeCount = 0; nodeCount < rootDetailsList.getLength(); nodeCount++){
					Element rootDetailElement =  (Element) rootDetailsList.item(nodeCount);
					if (rootDetailElement.getAttribute(ROOTNICKTX).equalsIgnoreCase(inRootNick)){
						rootDetailElement.setAttribute(DOWNLOADEDTIMETX,inDownloadedTime);
						rootDetailElement.setAttribute(DOWNLOADEDFILENAMETX,inDownloadedFileName);
						break;
					}
				}
			}
			commons.saveXMLFileFromDocument(catalogDownloadsDetailsDoc, commons.downloadedCatalogDetailsFile);
			System.out.println("at updateCatalogDownloadDetail saved file to " + commons.downloadedCatalogDetailsFile);
			catalogDownloadDetailsMap.put(inRootNick,new CatalogDownloadDetailsPojo(inRootNick,inDownloadedFileName,inDownloadedTime));
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			//e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in CatalogDownloadDtlsHandler updateCatalogDownloadDetail " + inRootNick + " " + inDownloadedFileName + " " + inDownloadedTime, e);
		}
	}
}