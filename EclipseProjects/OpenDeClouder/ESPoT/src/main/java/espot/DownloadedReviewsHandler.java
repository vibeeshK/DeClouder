package espot;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DownloadedReviewsHandler {

	CommonUIData commonUIData = null;
	ArtifactKeyPojo artifactKeyPojo = null;
	private boolean canBeReviewed = false;
	String finalReviewPathFileName = null;
	private ArtifactAllReviewsPojo artifactAllReviewsPojo = null;
	
	public DownloadedReviewsHandler(CommonUIData inCommonUIData, ArtifactKeyPojo inArtifactKeyPojo){
		commonUIData = inCommonUIData;
		artifactKeyPojo = inArtifactKeyPojo;
		capturetReviewDetails();
	}
	
	public ArtifactAllReviewsPojo getArtifactAllReviewsPojo () {
		return artifactAllReviewsPojo;
	}
	
	public boolean canBeReviewed() {
		return canBeReviewed;
	}
	
	private void capturetReviewDetails(){
		artifactAllReviewsPojo = null;
		
		ArtifactKeyPojo finalArtifactKeyPojo = commonUIData.getContentHandlerSpecsMap().get(artifactKeyPojo.contentType)
				.getFinalArtifactKeyPojo(artifactKeyPojo.rootNick, 
					artifactKeyPojo.relevance,
					artifactKeyPojo.artifactName,
					commonUIData.getCurrentRootPojo().fileSeparator);

		checkPastReviewDetails(
							finalArtifactKeyPojo.rootNick,
							finalArtifactKeyPojo.relevance,
							finalArtifactKeyPojo.artifactName,
							finalArtifactKeyPojo.contentType);
		try {
			System.out.println("@DownloadedReviewHandler allReviewPathFileName = " + finalReviewPathFileName);
			
			if (finalReviewPathFileName != null && !finalReviewPathFileName.isEmpty()) {
				artifactAllReviewsPojo = new ArtifactAllReviewsPojo();
				Document allReviewsDoc = commonUIData.getCommons().getDocumentFromXMLFile(finalReviewPathFileName);
				artifactAllReviewsPojo.buildArtifactAllReviewsPojoFromFromDoc(allReviewsDoc);
				System.out.println("artifactAllReviewsPojo item 1 review for GenlReq5 is " 
								+ artifactAllReviewsPojo.getItemAllReviews("GenlReq5"));
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commonUIData.getCommons(), "Error in DownloadedReviewHandler constructor for " 
			+ " " + finalArtifactKeyPojo.artifactName, e);
		}
	}
	
	private void checkPastReviewDetails(String inRootNick, String inRolledupRelevance, String inRolledupArtifactName, String inRolledupContentType) {

		System.out.println("At getParentRemarksFileIfAny going to read the parent remarks  for inRootNick " + inRootNick + " for inRolledupRelevance,inRolledupArtifactName " + inRolledupRelevance + " , " + inRolledupArtifactName);
		
		ERLDownload erlDownLoad = commonUIData.getCatelogPersistenceManager().readERLDownLoad(new ArtifactKeyPojo(inRootNick, inRolledupRelevance,inRolledupArtifactName,inRolledupContentType));
		
		System.out.println("At getParentRemarksFileIfAny erlDownLoad is " + erlDownLoad + " for inRolledupRelevance,inRolledupArtifactName " + inRolledupRelevance + " , " + inRolledupArtifactName);
		if (erlDownLoad != null) {

			canBeReviewed = true; // only when a ERL already present a review can be made
			
			String finalRemrkFilename = erlDownLoad.downLoadedReviewFile;			
			System.out.println(" at getParentRemarksFileIfAny here we go..... downLoadedReviewFile loaded with ERLDownload is "  + erlDownLoad.downLoadedReviewFile);

			System.out.println("At getParentRemarksFileIfAny finalRemrkFilename is " + finalRemrkFilename);
			if (finalRemrkFilename !=null && !finalRemrkFilename.isEmpty()) {
				finalReviewPathFileName = commonUIData.getCommons().getFullLocalPathFileNameOfDownloadedReview(commonUIData.getCurrentRootNick(), inRolledupRelevance, finalRemrkFilename);
			}
		}
	}
}
