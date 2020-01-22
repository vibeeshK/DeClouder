package espot;

import org.w3c.dom.Element;

public class ERLpojo extends ArtifactPojo{
	/*
	 * Convenience class for holding the parameters of a specific ERL viewed at Server side
	 */	
	public String contentFileName;
	public String uploadedTimeStamp;
	public String reviewTimeStamp;

	public ERLpojo(){
	}
	
	public ERLpojo(
			ArtifactKeyPojo inArtifactKeyPojo,
			String inRequestor,
			String inAuthor,
			boolean inHasSpecialHandler,
			String inReviewFileName,
			String inERLStatus,
			String inContentFileName, 
			String inUploadedTimeStamp, 
			String inReviewTimeStamp) {
		super(inArtifactKeyPojo,
				inRequestor, inAuthor, inHasSpecialHandler, inReviewFileName, inERLStatus);
		contentFileName = inContentFileName;
		uploadedTimeStamp = inUploadedTimeStamp;
		reviewTimeStamp = (inReviewTimeStamp!= null) ? inReviewTimeStamp : "";

		System.out.println("remarksTimeStamp...="
				+ reviewTimeStamp);
	}

	public void createDocElementFromERLpojo(Element inElement){
		inElement.setAttribute("relevance",artifactKeyPojo.relevance);
		inElement.setAttribute("contentName",artifactKeyPojo.artifactName);
		inElement.setAttribute("contentType",artifactKeyPojo.contentType);
		inElement.setAttribute("requestor",requestor);
		inElement.setAttribute("author",author);
		inElement.setAttribute("contentFileName",contentFileName);
		inElement.setAttribute("reviewFileName",reviewFileName);
		inElement.setAttribute("erlStatus",erlStatus);
		inElement.setAttribute("uploadedTimeStamp",uploadedTimeStamp);
		inElement.setAttribute("reviewTimeStamp",reviewTimeStamp);
	}
	
	public void getERLpojoFromDocElement(Element inElement){

		artifactKeyPojo = new ArtifactKeyPojo();

		artifactKeyPojo.relevance = inElement.getAttribute("relevance");
		System.out.println("getERLpojoFromDocElement relevance:" + artifactKeyPojo.relevance);
		
		artifactKeyPojo.artifactName = inElement.getAttribute("contentName");
		System.out.println("getERLpojoFromDocElement ContentName:" + artifactKeyPojo.artifactName);
		
		artifactKeyPojo.contentType = inElement.getAttribute("contentType");
		System.out.println("getERLpojoFromDocElement contentType:" + artifactKeyPojo.contentType);
		
		requestor = inElement.getAttribute("requestor");
		System.out.println("getERLpojoFromDocElement requestor:" + requestor);

		author = inElement.getAttribute("author");
		System.out.println("getERLpojoFromDocElement author:" + author);
		
		contentFileName = inElement.getAttribute("contentRemoteLocation");
		System.out.println("getERLpojoFromDocElement contentFileName:" + contentFileName);

		reviewFileName = inElement.getAttribute("contentRemoteLocation");
		System.out.println("getERLpojoFromDocElement reviewFileName:" + reviewFileName);
		
		erlStatus = inElement.getAttribute("erlStatus");
		System.out.println("getERLpojoFromDocElement erlStatus:" + erlStatus);
		
		uploadedTimeStamp = inElement.getAttribute("uploadedTimeStamp");
		System.out.println("getERLpojoFromDocElement uploadedTimeStamp:" + uploadedTimeStamp);
		
		reviewTimeStamp = inElement.getAttribute("reviewTimeStamp");
		System.out.println("getERLpojoFromDocElement reviewTimeStamp:" + reviewTimeStamp);

	}
}