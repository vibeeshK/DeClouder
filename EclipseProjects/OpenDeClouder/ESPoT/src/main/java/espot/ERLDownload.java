package espot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ERLDownload extends ERLpojo{
	/*
	 * Convenience class for holding the parameters of a specific ERL viewed at Desktop side
	 */

	public String subscriptionStatus = "";

	final public static String CURRENTLY_SUBSCRIBED = "Subscribed";
	final public static String AvailableStatus = "Available";
	final public static String NEVER_SUBSCRIBED = "";
	
	final public static String AUTOTRIGGER_NEW = "";
	final public static String AUTOTRIGGER_STARTED = "Started";
	
	public String downLoadedFile;
	public String downLoadedReviewFile;
	public String downLoadedArtifactTimeStamp;		// future enhancement: this is not relevant for server side
	public String downLoadedReviewTimeStamp;
	public String localCopyStatus;
	public boolean relevancePicked;
	public boolean autoTriggered;

	public final static String LOCAL_COPY_NOTAVAILABLE = "CONTENT_NOT_AVAILABLE";
	public final static String LOCAL_COPY_AVAILABLE = "CONTENT_AVAILABLE";
	public final static String LOCAL_COPY_TOBERENEWED = "CONTENT_TOBE_RENEWED";
	public final static String LOCAL_COPY_BEING_RENEWED = "CONTENT_BEING_RENEWED";

	public final static int NO_VIEW_ISSUES = 0;
	public final static int CANT_VIEW_RELEVANCE_NOT_PICKED = 1;
	public final static int CANT_VIEW_NOT_SUBSCRIBED_YET = 2;
	public final static int CANT_VIEW_NOT_RENEWED_YET = 3;
	public final static int CANT_VIEW_NOT_DOWNLOADED_YET = 4;
	
	public String leadID;
	public boolean personified;
	
	public ERLDownload(
			ArtifactKeyPojo inArtifactKeyPojo,
			String inRequestor,
			String inAuthor,
			String inLeadID,			
			boolean inHasSpecialHandler,
			boolean inAutoTriggered,
			boolean inPersonified,
			String inReviewFileName,
			String inERLStatus,
			String inContentFileName, 
			String inUploadedTimeStamp, 
			String inReviewTimeStamp, 
			String inSubscriptionStatus,
			String inDownLoadedFile,
			String inDownLoadedReviewFile,
			String inDownLoadedArtifactTimeStamp,
			String inDownLoadedReviewTimeStamp,
			boolean inRelevancePicked
			) {
		super(inArtifactKeyPojo,
				inRequestor, inAuthor, inHasSpecialHandler, inReviewFileName,inERLStatus,
				inContentFileName, 
				inUploadedTimeStamp, 
				inReviewTimeStamp
				
		);
		subscriptionStatus = inSubscriptionStatus;
		downLoadedFile = inDownLoadedFile;
		downLoadedReviewFile = inDownLoadedReviewFile;
		downLoadedArtifactTimeStamp = inDownLoadedArtifactTimeStamp;
		downLoadedReviewTimeStamp = inDownLoadedReviewTimeStamp;
		relevancePicked  = inRelevancePicked;
		autoTriggered = inAutoTriggered;
		leadID = (inLeadID!= null) ? inLeadID : "";
		personified = inPersonified;		
		
		System.out.println(" here we go..... inDownLoadedReviewFile loaded at ERLDownload is "  + inDownLoadedReviewFile);
		System.out.println(" here we go..... downLoadedReviewFile loaded with ERLDownload is "  + downLoadedReviewFile);
		
		if (subscriptionStatus != null && downLoadedArtifactTimeStamp!=null
				&& (subscriptionStatus.equalsIgnoreCase(AvailableStatus)
				|| 	!downLoadedArtifactTimeStamp.equalsIgnoreCase(""))) {
			System.out.println("setERLpojoForClient DownLoadedArtifactTimeStamp:" + downLoadedArtifactTimeStamp);
			System.out.println("setERLpojoForClient uploadedTimeStamp:" + uploadedTimeStamp);
			
			if (downLoadedArtifactTimeStamp == null || downLoadedArtifactTimeStamp.compareToIgnoreCase(uploadedTimeStamp) < 0){
				//check and set Local copy being renewed
				if (subscriptionStatus.equalsIgnoreCase(CURRENTLY_SUBSCRIBED)) {
					localCopyStatus = LOCAL_COPY_BEING_RENEWED;
					System.out.println("setERLpojoForClient localCopyStatus1:" + localCopyStatus);
				} else {
					localCopyStatus = LOCAL_COPY_TOBERENEWED;
					System.out.println("setERLpojoForClient localCopyStatus1:" + localCopyStatus);
				}
			} else {
				localCopyStatus = LOCAL_COPY_AVAILABLE;
				System.out.println("setERLpojoForClient localCopyStatus2:" + localCopyStatus);
			}
		} else {
			localCopyStatus = LOCAL_COPY_NOTAVAILABLE;
			System.out.println("setERLpojoForClient localCopyStatus3:" + localCopyStatus);
		}
	}

	public int getEditIssueWithErlVersion(Shell inMainShell,CatelogPersistenceManager inCatelogPersistenceManager,RelevancePojo inRelevancePojo, String inProcessFor) {
		//check for erl version - not downloaded or relevance not picked etc
		System.out.println("relevancePicked:" + relevancePicked);
		System.out.println("downLoadedArtifactTimeStamp:" + downLoadedArtifactTimeStamp);
		int editIssue;
		if (localCopyStatus.equalsIgnoreCase(LOCAL_COPY_AVAILABLE)) {
			editIssue = NO_VIEW_ISSUES;
		} else {
			if (!relevancePicked) {
				editIssue = CANT_VIEW_RELEVANCE_NOT_PICKED;
			} else if (subscriptionStatus.equalsIgnoreCase("")) {
				editIssue = CANT_VIEW_NOT_SUBSCRIBED_YET;
			} else if (localCopyStatus.equalsIgnoreCase(LOCAL_COPY_TOBERENEWED)) {
				editIssue = CANT_VIEW_NOT_RENEWED_YET;
			} else {
				editIssue = CANT_VIEW_NOT_DOWNLOADED_YET; 
			}
		}

		if (editIssue != ERLDownload.NO_VIEW_ISSUES) {
			if (editIssue == ERLDownload.CANT_VIEW_RELEVANCE_NOT_PICKED) {
				MessageBox messageBox1 = new MessageBox(inMainShell,
						SWT.OK | SWT.CANCEL);
				messageBox1.setMessage(inProcessFor + "'s Relevance not picked. Do you want to pick the relevance and subscribe?");
				int rc1 = messageBox1.open();
				if (rc1 == SWT.OK) {
					inCatelogPersistenceManager
					.pickRelevance(inRelevancePojo);
					inCatelogPersistenceManager.replaceSubscription(this,ERLDownload.CURRENTLY_SUBSCRIBED);
				}
			} else if (editIssue == ERLDownload.CANT_VIEW_NOT_SUBSCRIBED_YET) {
				MessageBox messageBox2 = new MessageBox(inMainShell,
						SWT.OK | SWT.CANCEL);
				messageBox2.setMessage(inProcessFor + " not subscribed yet. Do you want to subscribe?");
				int rc1 = messageBox2.open();
				if (rc1 == SWT.OK) {
					inCatelogPersistenceManager.replaceSubscription(this,ERLDownload.CURRENTLY_SUBSCRIBED);
				} 
			} else if (editIssue == ERLDownload.CANT_VIEW_NOT_RENEWED_YET) {
				MessageBox messageBox2 = new MessageBox(inMainShell,
						SWT.OK | SWT.CANCEL);
				messageBox2.setMessage("Source Artifact not renewed yet. Do you want to renew?");
				int rc1 = messageBox2.open();
				if (rc1 == SWT.OK) {
					inCatelogPersistenceManager.replaceSubscription(this,ERLDownload.CURRENTLY_SUBSCRIBED);
				} 
			} else if (editIssue == ERLDownload.CANT_VIEW_NOT_DOWNLOADED_YET) {
				MessageBox messageBox2 = new MessageBox(inMainShell,
						SWT.OK | SWT.CANCEL);
				messageBox2.setMessage(inProcessFor + " not downloaded yet. Please wait");
				int rc1 = messageBox2.open();
			}
		}
		return editIssue;
	}
}