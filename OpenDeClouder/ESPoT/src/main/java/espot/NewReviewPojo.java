package espot;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import commonTechs.CommonTechs;

public class NewReviewPojo {
	/*
	 * Holds New Review created by the user
	 */
	final static String ItemRejected = "Rejected";
	final static String ItemApproved = "Approved";
	final static String ItemToBeAmended = "ToBeAmended";

	final static String ARTIFACT_ITEM_ONREVIEW_ELEMENT = "ArtifactItemOnReview";
	final static String ITEMNAME_TX = "ItemName";

	final static String ARTIFACT_ITEM_REVIEW_ELEMENT = "ArtifactItemReview";
	final static String REVIEWID_TX = "ReviewID";
	final static String REVIEWER_TX = "Reviewer";
	final static String CREATED_TIME_TX = "CreatedTime";
	final static String REVIEWER_APPROVAL_DECISION_TX = "ReviewerApprovalDecision";
	final static String REASSIGNED_REQUESTOR_TX = "ReassignedRequestor";
	final static String REASSIGNED_AUTHOR_TX = "ReassignedAuthor";
	final static String NEW_ERLSTATUS_TX = "NewERLStatus";

	private Commons commons;
	public ArtifactKeyPojo artifactKeyPojo=null;
	public String itemName=null;
	public String reviewID=null;
	public String reviewer=null;
	public String createdTime=null;
	public String reviewerApprovalDecision=null;
	public String description=null;
	public String reviewFileName=null;

	public String reassignedRequestor = null;
	public String reassignedAuthor = null;
	public String newERLStatus=null;
	
	public Document newReviewDocument=null;
	boolean newReviewDocumentBuilt = false;
	Element artifactItemOnReview = null;
	Element newReviewElement = null;
	
	public NewReviewPojo(Commons inCommon, ArtifactKeyPojo inArtifactKeyPojo, String inItemName,String inReviewer,String inCreatedTime) {
		commons = inCommon;
		artifactKeyPojo = inArtifactKeyPojo;
		System.out.println("@@2r2 inArtifactKeyPojo = " + inArtifactKeyPojo);
		System.out.println("@@2r2 artifactKeyPojo = " + artifactKeyPojo);
		System.out.println("@@2r2 artifactKeyPojo.relevance" + artifactKeyPojo.relevance);
		
		itemName = inItemName;
		reviewer = inReviewer;
		createdTime = inCreatedTime;
	}

	public NewReviewPojo(Commons inCommon, ArtifactKeyPojo inArtifactKeyPojo, Document inItemReviewDocument) {
		commons = inCommon;
		artifactKeyPojo = inArtifactKeyPojo;
		buildReviewPojoFromDocument(inItemReviewDocument);
	}

	public void buildReviewPojoFromDocument(Document inItemReviewDocument) {
		newReviewDocument = inItemReviewDocument;
		newReviewDocumentBuilt = true;

		artifactItemOnReview = (Element) inItemReviewDocument.getElementsByTagName(ARTIFACT_ITEM_ONREVIEW_ELEMENT).item(0);

		if (artifactItemOnReview != null) {
			itemName = artifactItemOnReview.getAttribute(ITEMNAME_TX);

			newReviewElement = (Element) artifactItemOnReview.getElementsByTagName(ARTIFACT_ITEM_REVIEW_ELEMENT).item(0);
			System.out.println("ItemNewReviewPojo-> newReviewElement = " + newReviewElement);
			
			if (newReviewElement != null) {
				reviewID = newReviewElement.getAttribute(REVIEWID_TX);
				System.out.println("reviewID = " + reviewID);
				reviewer = newReviewElement.getAttribute(REVIEWER_TX);
				createdTime = newReviewElement.getAttribute(CREATED_TIME_TX);
				reviewerApprovalDecision = newReviewElement.getAttribute(REVIEWER_APPROVAL_DECISION_TX);
				reassignedRequestor = newReviewElement.getAttribute(REASSIGNED_REQUESTOR_TX);
				reassignedAuthor = newReviewElement.getAttribute(REASSIGNED_AUTHOR_TX);
				newERLStatus=newReviewElement.getAttribute(NEW_ERLSTATUS_TX);
				
				description = newReviewElement.getTextContent();
			}
		}
	}

	public void createNewReviewDocument() {

		try {
			newReviewDocument = commons.getNewDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			CommonTechs.logger.error("Error at createNewReviewDocument NewReviewPojo");
			System.exit(Commons.FATALEXITCODE);;
		}

		artifactItemOnReview = newReviewDocument.createElement(ARTIFACT_ITEM_ONREVIEW_ELEMENT);
		newReviewDocument.appendChild(artifactItemOnReview);
		artifactItemOnReview.setAttribute(ITEMNAME_TX,itemName);
		
		newReviewElement = newReviewDocument.createElement(ARTIFACT_ITEM_REVIEW_ELEMENT);
		artifactItemOnReview.appendChild(newReviewElement);
		
		System.out.println("just created a newReview Document.....");
		System.out.println("just created a newReview Document.....");
		System.out.println("just created a newReview Document.....");

		newReviewElement.setAttribute(REVIEWID_TX,reviewID);
		newReviewElement.setAttribute(REVIEWER_TX,reviewer);
		newReviewElement.setAttribute(CREATED_TIME_TX,createdTime);
		newReviewElement.setAttribute(REVIEWER_APPROVAL_DECISION_TX,reviewerApprovalDecision);
		newReviewElement.setTextContent(description);
		if (reassignedRequestor!=null && !reassignedRequestor.equalsIgnoreCase("")) {
			newReviewElement.setAttribute(REASSIGNED_REQUESTOR_TX,reassignedRequestor);
		}
		if (reassignedAuthor!=null && !reassignedAuthor.equalsIgnoreCase("")){
			newReviewElement.setAttribute(REASSIGNED_AUTHOR_TX,reassignedAuthor);
		}
		if (newERLStatus!=null && !newERLStatus.equalsIgnoreCase("")){
			newReviewElement.setAttribute(NEW_ERLSTATUS_TX,newERLStatus);
		}
		newReviewDocumentBuilt = true;
	}
	
	public Document getNewReviewDocument(){
		if (!newReviewDocumentBuilt) createNewReviewDocument();
		return newReviewDocument;
	}

	public String getCondensedReviewString() {
		//String condensedReviewString  = createdTime + "::" + reviewID + "::" + reviewer + "\n" + description;
		String condensedReviewString  = "***" + reviewID + " : " + "\n" + description;
		System.out.println("condensedReviewString  = " + condensedReviewString);
		return condensedReviewString;
	}

	protected void setReviewID(){
		reviewID = "ReviewBy_" + reviewer + "_at_" + createdTime;
	}
	
	protected void setReviewFileName(){
		reviewFileName = artifactKeyPojo.artifactName
								+ "_" + itemName
								+ "_" + reviewID;
	}
}