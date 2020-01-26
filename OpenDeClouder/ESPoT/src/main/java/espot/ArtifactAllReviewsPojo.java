package espot;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ArtifactAllReviewsPojo {
	/*
	 * Provides an object view of all reviews of an item
	 */
	final static String ItemRejected = "Rejected";
	final static String ItemApproved = "Approved";
	final static String ItemToBeAmended = "ToBeAmended";
	boolean artifactAllReviewsDocumentBuilt = false;
	int numberOfItems = 0;
	String rootString = null;
	String relevance = null;
	String contentName = null;

	HashMap<String,String> itemsReviews = null;
	
	public Document artifactAllReviewsDocument = null;
	Element artifactItemReviewsElement = null;
	boolean intiatedArtifactReviewsDoc = false;

	public ArtifactAllReviewsPojo() {}
	
	public ArtifactAllReviewsPojo(String inRootString,String inRelevance,String inContentName) {
		rootString = inRootString;
		relevance = inRelevance;
		contentName = inContentName;
	}
	
	public void buildArtifactAllReviewsPojoFromFromDoc(Document inArtifactAllReviewsDocument) {
		artifactAllReviewsDocument = inArtifactAllReviewsDocument;
		itemsReviews = new HashMap<String,String>();
		artifactAllReviewsDocumentBuilt = true;
		
		System.out.println("@1 artifactAllReviewsDocument=" + artifactAllReviewsDocument);

		if (artifactAllReviewsDocument == null) {
			numberOfItems = 0;
			return;
		}

		artifactItemReviewsElement = artifactAllReviewsDocument.getDocumentElement();
		System.out.println("@2");
		
		System.out.println("???artifactItemReviewsElement = " + artifactItemReviewsElement.getNodeName());
		System.out.println("@3");
		
		NodeList itemNodesList = artifactItemReviewsElement.getChildNodes();
		System.out.println("@4");
		
		numberOfItems = itemNodesList.getLength();
		for (int itemCount = 0; itemCount<numberOfItems;itemCount++) {
			String itemID = itemNodesList.item(itemCount).getNodeName();
			String description = itemNodesList.item(itemCount).getTextContent();
			itemsReviews.put(itemID, description);
		}
	}

	public void initiateArtifactReviewsDoc() throws ParserConfigurationException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		builder = factory.newDocumentBuilder();

		artifactAllReviewsDocument = builder.newDocument();
		
		artifactItemReviewsElement = artifactAllReviewsDocument.createElement("ArtifactItemReviews");
		artifactItemReviewsElement.setAttribute("RootString", rootString);
		artifactItemReviewsElement.setAttribute("Relevance", relevance);
		artifactItemReviewsElement.setAttribute("ContentName", contentName);

		artifactAllReviewsDocument.appendChild(artifactItemReviewsElement);
		intiatedArtifactReviewsDoc = true;
		itemsReviews = new HashMap<String,String>();

		System.out.println("initiateArtifactReviewsDoc completed");
	}

	public void appendItemReviewsDoc(String inItemID, String inCondensedNewItemReview){
		System.out.println("Appending:::: " + inItemID + " new review:::: " + inCondensedNewItemReview);
		
		Node currentItemReviewsNode = null;
		if (itemsReviews == null || !itemsReviews.containsKey(inItemID)) {
			System.out.println("Inside appendItemReviewsDoc for inItemID = " + inItemID);
			currentItemReviewsNode = artifactAllReviewsDocument.createElement(inItemID);
			artifactItemReviewsElement.appendChild(currentItemReviewsNode);
		} else {
			System.out.println("ItemReviews is not null  = " + inItemID);
			System.out.println("itemsReviews.containsKey(inItemID) = " + itemsReviews.containsKey(inItemID));
			System.out.println("artifactItemReviewsElement.getElementsByTagName(inItemID).item(0) = " + artifactItemReviewsElement.getElementsByTagName(inItemID).item(0));

			currentItemReviewsNode = artifactItemReviewsElement.getElementsByTagName(inItemID).item(0);
			
			System.out.println("currentItemReviewsNode = " + currentItemReviewsNode);
			System.out.println("inside currentItemReviewsNode = " + currentItemReviewsNode.getNodeName());

		}
		appendItemReviewsPojo(inItemID,inCondensedNewItemReview);

		System.out.println("currentItemReviewsNode = " + currentItemReviewsNode.getNodeName());
		System.out.println("inItemID = " + inItemID);
		System.out.println("itemsReviews = " + itemsReviews);
		
		currentItemReviewsNode.setTextContent(itemsReviews.get(inItemID));
	}
	
	public void appendItemReviewsPojo(String inItemID, String inCondensedNewItemReview){
		String itemAllReviews = "";
		if (itemsReviews == null) { 
			itemsReviews = new HashMap<String,String>();
		} else if (itemsReviews.containsKey(inItemID)) {
			itemAllReviews = itemsReviews.get(inItemID);
			System.out.println("existing itemAllReviews of '" + inItemID + "' is ..." + itemAllReviews);
		}
		System.out.println("New Review of '" + inItemID + "' is ..." + inCondensedNewItemReview);

		if (itemAllReviews.equalsIgnoreCase("")){
			itemAllReviews = inCondensedNewItemReview;
		} else {
			itemAllReviews = itemAllReviews + "\n" + inCondensedNewItemReview;
		}
		System.out.println("Appended itemAllReviews of '" + inItemID + "' is ..." + itemAllReviews);
		
		itemsReviews.put(inItemID, itemAllReviews);
	}
	
	public String getItemAllReviews(String inItemID){
		String itemAllReviews = "";
		if (itemsReviews != null && itemsReviews.containsKey(inItemID)) {
			itemsReviews.get(inItemID);
		}
		return itemAllReviews;
	}
}