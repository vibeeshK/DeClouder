package espot;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ReviewHandler {
	/*
	 * Review handling UI for an artifact or an item within
	 */
	public static final int PREFERED_REVIEW_PANEL_WIDTH = 600;
	public static final int PREFERED_REVIEW_HEIGHT = 100;
	private static final String hideReviewLIT = "HideReview";

	Composite wrappingExtlComposite;
	Composite reviewFrameOutmostScroller;
	Composite reviewFrameOutmostWrapper;	// in grid layout
	Composite reviewContentScroller; 		// in grid layout
	Composite reviewContntHolder; 			// in form layout

	ArtifactPojo artifactPojo = null;
	String itemName = null;
	CommonUIData commonUIData;
	Document newCommentDoc = null;
	String finalReviewPathFileName = null;
	ArtifactAllReviewsPojo artifactAllReviewsPojo = null;
	Document allReviewsDoc = null;
	UsersDisplay authorsDisplay = null;
	UsersDisplay requestorDisplay = null;	
	Shell outerMainShell = null;
	ERLStatusDisplay erlStatusDisplay = null;
	boolean reviewVisible = true;
	Group hideReviewGrp = null;	
	Text newCommentText = null;
	ContentHandlerSpecs contentHandlerSpecs = null;
	
	public ReviewHandler(CommonUIData inCommonUIData,
							Composite inWrappingComposite,
							ArtifactPojo inArtifactPojo, 
							String inItemName,
							Shell inMainShell)
	{
		commonUIData = inCommonUIData;
		wrappingExtlComposite = inWrappingComposite;
		artifactPojo = inArtifactPojo;
		itemName = inItemName;	//itemName will be same as the artifact name for artifact reviews

		contentHandlerSpecs = commonUIData.getContentHandlerSpecs(artifactPojo.artifactKeyPojo.contentType);
		
		
		outerMainShell = inMainShell;
		System.out.println("@ReviewHandler inArtifactPojo = " + inArtifactPojo);
		System.out.println("@ReviewHandler inArtifactPojo.artifactKeyPojo = " + inArtifactPojo.artifactKeyPojo);
		System.out.println("@ReviewHandler inArtifactPojo.artifactKeyPojo.contentType = " + inArtifactPojo.artifactKeyPojo.contentType);
		System.out.println("@ReviewHandler inCommonUIData.getContentHandlerSpecsMap().get(inArtifactPojo.artifactKeyPojo.contentType) = " + inCommonUIData.getContentHandlerSpecsMap().get(inArtifactPojo.artifactKeyPojo.contentType));
		
		ArtifactKeyPojo finalArtifactKeyPojo = inCommonUIData.getContentHandlerSpecsMap().get(inArtifactPojo.artifactKeyPojo.contentType)
												.getFinalArtifactKeyPojo(inArtifactPojo.artifactKeyPojo.rootNick, 
													inArtifactPojo.artifactKeyPojo.relevance,
													inArtifactPojo.artifactKeyPojo.artifactName,
													inCommonUIData.getCurrentRootPojo().fileSeparator);
						
		finalReviewPathFileName = getParentRemarksFileIfAny(
										finalArtifactKeyPojo.rootNick,
										finalArtifactKeyPojo.relevance,
										finalArtifactKeyPojo.artifactName,
										finalArtifactKeyPojo.contentType);

		reviewFrameOutmostScroller = new Composite(wrappingExtlComposite,SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		reviewFrameOutmostScroller.setLayoutData(gridData);
		reviewFrameOutmostWrapper = new Composite(reviewFrameOutmostScroller, SWT.NONE);
		reviewFrameOutmostScroller.setLayout(new GridLayout());

		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		reviewFrameOutmostWrapper.setLayoutData(gridData);
		reviewFrameOutmostWrapper.setLayout(new RowLayout());
		hideReviewGrp = new Group(reviewFrameOutmostWrapper, SWT.NONE);		
		hideReviewGrp.setLayoutData(new RowData());
		hideReviewGrp.setLayout(new FillLayout(SWT.VERTICAL));

		Button hideReviewButton = new Button(hideReviewGrp, SWT.PUSH);
		hideReviewButton.setText(hideReviewLIT);
		hideReviewButton.setToolTipText("Hide this review pane to make room in screen");
		hideReviewButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (reviewVisible) {

					System.out.println("reviewVisible");
											
					if (newCommentText.getEditable() && !newCommentText.getText().equalsIgnoreCase("")) {
						MessageBox WarningMessageBox = new MessageBox(outerMainShell,
								SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
						WarningMessageBox.setMessage("The keyed in text will be lost if hidden. Proceed?");
						if (WarningMessageBox.open() == SWT.CANCEL) {
							System.out.println("Cancel chosen. returning");
							return;
						} else {
							System.out.println("Hide confirmed. Proceeding");
						}
					} else {
						System.out.println("review not editable or empty");
					}
					((Button) event.getSource()).setText("ShowReview");
					System.out.println("Hiding Review");
					Control[] oldControls = reviewContentScroller.getChildren();
					for (Control oldControl : oldControls) {
					    oldControl.dispose();
					}
					reviewContentScroller.dispose();
					reviewVisible = false;

					hideReviewGrp.pack();
					reviewFrameOutmostScroller.pack();
					wrappingExtlComposite.pack();
					wrappingExtlComposite.layout(true);
				} else {
					((Button) event.getSource()).setText(hideReviewLIT);
					displayContent();
					reviewVisible = true;
				}
				outerMainShell.pack();
				outerMainShell.layout();				
			}
		});
		hideReviewButton.pack();
		hideReviewGrp.pack();

		try {
			System.out.println("@ReviewHandler allReviewPathFileName = " + finalReviewPathFileName);
			
			if (finalReviewPathFileName == null || finalReviewPathFileName.equalsIgnoreCase("")) {
			} else {
				artifactAllReviewsPojo = new ArtifactAllReviewsPojo();
				allReviewsDoc = commonUIData.getCommons().getDocumentFromXMLFile(finalReviewPathFileName);
				artifactAllReviewsPojo.buildArtifactAllReviewsPojoFromFromDoc(allReviewsDoc);
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commonUIData.getCommons(), "Error in ReviewHandler constructor " 
			+ inArtifactPojo.artifactKeyPojo.artifactName + " " 
			+ " " + inItemName, e);
		}
	}

	public void displayContent() {
		
		System.out.println("at 1 displayContent of reviewHandler");
		reviewContentScroller = new Composite(reviewFrameOutmostWrapper,SWT.BORDER);
		reviewContentScroller.setLayoutData(new RowData());
		reviewContentScroller.setLayout(new GridLayout());
		
		reviewContntHolder = new Composite(reviewContentScroller, SWT.NONE); 
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		reviewContntHolder.setLayoutData(gridData);
		reviewContntHolder.setLayout(new FormLayout());

		Group prevCommentGrp = new Group(reviewContntHolder, SWT.V_SCROLL | SWT.H_SCROLL);
		prevCommentGrp.setLayout(new FillLayout(SWT.VERTICAL));
		prevCommentGrp.setText("PrevComment");
		Text prevCommentText = new Text(prevCommentGrp, 
				SWT.WRAP | SWT.READ_ONLY | SWT.LEFT | SWT.V_SCROLL | SWT.H_SCROLL);
		System.out.println("11 x = " + prevCommentText.getSize().x);
		System.out.println("11 y = " + prevCommentText.getSize().y);
		prevCommentText.pack();
		System.out.println("22 x = " + prevCommentText.getSize().x);
		System.out.println("22 y = " + prevCommentText.getSize().y);

		int prevCommentHeight = 0;
		if (artifactAllReviewsPojo != null &&  artifactAllReviewsPojo.itemsReviews.containsKey(itemName)) {
			System.out.println("prevCommentText = " + prevCommentText);
			System.out.println("artifactAllReviewsPojo = " + artifactAllReviewsPojo);
			System.out.println("artifactAllReviewsPojo.itemsReviews = " + artifactAllReviewsPojo.itemsReviews);
			System.out.println("artifactPojo = " + artifactPojo);
			System.out.println("itemName = " + itemName);
			prevCommentText.setText(artifactAllReviewsPojo.itemsReviews.get(itemName));
			prevCommentHeight = PREFERED_REVIEW_HEIGHT;
		} else {
			prevCommentText.setText("-None-");
		}
		prevCommentText.pack();
		
		FormData formData = new FormData();
		if (prevCommentHeight > 0) {
			formData.height = PREFERED_REVIEW_HEIGHT;
		}
		formData.width = PREFERED_REVIEW_PANEL_WIDTH;
		formData.top = new FormAttachment(reviewContntHolder, 0, SWT.TOP);;
		prevCommentGrp.setLayoutData(formData);
		prevCommentGrp.pack();
		Group lastReviewWdgtGrp = prevCommentGrp;

		System.out.println("33 x = " + prevCommentText.getSize().x);
		System.out.println("33 y = " + prevCommentText.getSize().y);
		
		final Group newCommentGrp = new Group(reviewContntHolder, SWT.READ_ONLY | SWT.MAX);
		newCommentGrp.setLayout(new FillLayout(SWT.VERTICAL));
		newCommentGrp.setText("NewComment");

		newCommentText = new Text(newCommentGrp, SWT.WRAP | SWT.LEFT | SWT.H_SCROLL | SWT.V_SCROLL);

		ClientSideNew_ReviewPojo reviewPojo = null;
		try {
			reviewPojo = commonUIData.getCatelogPersistenceManager().readReview(artifactPojo.artifactKeyPojo,itemName);

			System.out.println("@@2r4 artifactKeyPojo = " + reviewPojo);
			
			if (reviewPojo == null) {
				newCommentText.setText("");
			} else {
				System.out.println("@@2r4 artifactKeyPojo.artifactKeyPojo = " + reviewPojo.artifactKeyPojo);
				System.out.println("new comment already exists at:" + reviewPojo.reviewFileName);

				System.out.println("commons:" + commonUIData.getCommons());
				System.out.println("commons.defaultUIRootNick:" + commonUIData.getCommons().getCurrentRootNick());
				System.out.println("reviewPojo" + reviewPojo);
				System.out.println("reviewPojo.artifactKeyPojo" + reviewPojo.artifactKeyPojo);
				System.out.println("reviewPojo.artifactKeyPojo.relevance" + reviewPojo.artifactKeyPojo.relevance);
				System.out.println("reviewPojo.localFileName:" + reviewPojo.reviewFileName);
				
				String localReviewFullPathFileName = commonUIData.getCommons().getFullLocalPathFileNameOfNewReview(commonUIData.getCommons().getCurrentRootNick(), reviewPojo.artifactKeyPojo.relevance, reviewPojo.reviewFileName);

				Document reviewXMLDocument = commonUIData.getCommons().getDocumentFromXMLFile(localReviewFullPathFileName);
				reviewPojo.buildReviewPojoFromDocument(reviewXMLDocument);		
				newCommentText.setText(reviewPojo.description);
				newCommentText.setEditable(false);
			}
		} catch (SAXException | IOException | ParserConfigurationException e1) {
			e1.printStackTrace();
			ErrorHandler.showErrorAndQuit(commonUIData.getCommons(), "Error in ReviewHandler displayContent", e1);
		}

		formData = new FormData();
		formData.top = new FormAttachment(lastReviewWdgtGrp,0,SWT.BOTTOM);
		formData.height = PREFERED_REVIEW_HEIGHT;
		formData.width = PREFERED_REVIEW_PANEL_WIDTH;
		newCommentText.pack();
		newCommentGrp.setLayoutData(formData);

		newCommentGrp.pack();
		lastReviewWdgtGrp = newCommentGrp;

		final Group reviewActionsGrp = new Group(reviewContntHolder, SWT.READ_ONLY);
		reviewActionsGrp.setLayout(new FillLayout(SWT.VERTICAL));
		reviewActionsGrp.setText("ReviewActions");

		formData = new FormData();
		formData.top = new FormAttachment(lastReviewWdgtGrp,0,SWT.BOTTOM);
		reviewActionsGrp.setLayoutData(formData);

		System.out.println("before reassign display:");
		System.out.println("before reassign display: artifactPojo.requestor is " + artifactPojo.requestor);
		System.out.println("before reassign display: userName is " + commonUIData.getCommons().userName);

		UserPojo deskUserDetail = commonUIData.getUsersHandler().getUserDetailsFromRootSysLoginID(commonUIData.getCommons().userName);

		if (!contentHandlerSpecs.rollupAddupType && 
				(commonUIData.getUsersHandler().doesUserHaveRightsOverMember(
					commonUIData.getCommons().userName, artifactPojo.requestor))) {
				//commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.requestor) || deskUserDetail.hasAdminPrivilege() || deskUserDetail.hasTeamLeaderPrivilege())) { 
			
		//Only requestor can reassign. Later enhance to allow admins to change as well
			if (reviewPojo != null) {
				if (reviewPojo.reassignedAuthor != null) {
					System.out.println("before reassign display2: reviewPojo.reassignedAuthor is " + reviewPojo.reassignedAuthor);
					authorsDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,reviewPojo.reassignedAuthor,false,UsersDisplay.AUTHOR_REASSIGN_TEXT);
				}
			} else {
				System.out.println("before reassign display2: artifactPojo.author is " + artifactPojo.author);
				authorsDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,artifactPojo.author,true,UsersDisplay.AUTHOR_REASSIGN_TEXT);
			}
		//}
		//if (commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.requestor) || deskUserDetail.hasAdminPrivilege() || deskUserDetail.hasTeamLeaderPrivilege()) {
		//Only requestor can reassign. Later enhance to allow admins to change as well
			if (reviewPojo != null) {
				if (reviewPojo.reassignedRequestor != null) {
					System.out.println("before reassign display2: reviewPojo.reassignedRequestor is " + reviewPojo.reassignedRequestor);
					requestorDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,reviewPojo.reassignedRequestor,false,UsersDisplay.REQUESTOR_REASSIGN_TEXT);
				}
			} else {
				System.out.println("before reassign display2: artifactPojo.requestor is " + artifactPojo.requestor);
				requestorDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,artifactPojo.requestor,true,UsersDisplay.REQUESTOR_REASSIGN_TEXT);
			}
		}
		
		///////////////// NEW ERL status starts
		final Group erlStatusGroup = new Group(reviewActionsGrp, SWT.CENTER);
		erlStatusGroup.setLayout(new FillLayout());
		//erlStatusGroup.setText("ERL Status");

		formData = new FormData();
		formData.top = new FormAttachment(lastReviewWdgtGrp,0,SWT.BOTTOM);
		reviewActionsGrp.setLayoutData(formData);

		ERLStatusDisplay currentERLStatusDisplay = new ERLStatusDisplay(null, erlStatusGroup, artifactPojo.erlStatus, false, ERLStatusDisplay.CURR_STATUS_TEXT);
		
		String[] validActions = null;
		if (deskUserDetail.hasAdminPrivilege() || deskUserDetail.hasTeamLeaderPrivilege()) {
			validActions = ArtifactPojo.ADMIN_VALID_ACTIONS;
		} else if (commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.requestor)){
			validActions = ArtifactPojo.REQUESTOR_VALID_ACTIONS;
		} else if (commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.author)){
			validActions = ArtifactPojo.AUTHOR_VALID_ACTIONS;
		}

		if (commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.requestor) || commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.author) || deskUserDetail.hasAdminPrivilege() || deskUserDetail.hasTeamLeaderPrivilege()) {
			if (reviewPojo !=null) {
				if (reviewPojo.newERLStatus != null) {
					erlStatusDisplay = new ERLStatusDisplay(validActions, erlStatusGroup, reviewPojo.newERLStatus, false, ERLStatusDisplay.STATUS_ASSIGN_TEXT);
				}
			} else {
				erlStatusDisplay = new ERLStatusDisplay(validActions, erlStatusGroup, artifactPojo.erlStatus, true, ERLStatusDisplay.STATUS_ASSIGN_TEXT);
			}
		}
		///////////////// New ERL status ends

		Button submitReviewButton = new Button(reviewActionsGrp, SWT.PUSH);
		submitReviewButton.setText("SubmitReview");
		submitReviewButton.setToolTipText("Submit the review and any change to ownership and status.");
		
		if (reviewPojo != null) {
			submitReviewButton.setEnabled(false);
			submitReviewButton.setText("Submitted already");
		} else {
			submitReviewButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
	
					try {
						((Button) event.getSource()).setEnabled(false);
						System.out.println("Saving Remarks");

						ClientSideNew_ReviewPojo clientSideNew_ReviewPojo = new ClientSideNew_ReviewPojo(
								commonUIData.getCommons(), artifactPojo.artifactKeyPojo,itemName,commonUIData.getCommons().userName,commonUIData.getCommons().getCurrentTimeStamp());
						if (authorsDisplay!=null) {
							System.out.println("Inside Author display reassign sel Auth is " + authorsDisplay.userText.getText());
							clientSideNew_ReviewPojo.reassignedAuthor = authorsDisplay.userText.getText();
						}
						if (requestorDisplay!=null) {
							System.out.println("Inside requestor display reassign sel requestor is " + requestorDisplay.userText.getText());
							clientSideNew_ReviewPojo.reassignedRequestor = requestorDisplay.userText.getText();
						}
						if (erlStatusDisplay!=null) {
							System.out.println("Inside newERLStatusStaging3 is " + erlStatusDisplay.newERLStatusCaptured);
							clientSideNew_ReviewPojo.newERLStatus = erlStatusDisplay.newERLStatusCaptured;
							System.out.println("new value of newERLStatusStaging is " + clientSideNew_ReviewPojo.newERLStatus);
						}
						
						System.out.println("new value of sel Auth is " + clientSideNew_ReviewPojo.reassignedAuthor);
						System.out.println("new value of sel Req is " + clientSideNew_ReviewPojo.reassignedRequestor);
						clientSideNew_ReviewPojo.captureNewComment(newCommentText.getText());
						String localReviewPathFileName = commonUIData.getCommons().getFullLocalPathFileNameOfNewReview(commonUIData.getCommons().getCurrentRootNick(), artifactPojo.artifactKeyPojo.relevance, clientSideNew_ReviewPojo.reviewFileName);
	
						System.out.println("clientSideNew_ReviewPojo.newReviewDocument:" + clientSideNew_ReviewPojo.newReviewDocument);
						System.out.println("reviewPathFileName to be saved:" + localReviewPathFileName);
						commonUIData.getCommons().saveXMLFileFromDocument(clientSideNew_ReviewPojo.newReviewDocument, localReviewPathFileName);
						commonUIData.getCatelogPersistenceManager().insertReview(clientSideNew_ReviewPojo);
					} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
						e.printStackTrace();
						ErrorHandler.showErrorAndQuit(commonUIData.getCommons(), "Error in ReviewHandler displayContent", e);
					}
				}
			});
		}
		reviewActionsGrp.pack();
		lastReviewWdgtGrp = reviewActionsGrp;

		System.out.println("44 x = " + prevCommentText.getSize().x);
		System.out.println("44 y = " + prevCommentText.getSize().y);
		
		reviewContntHolder.pack(); 			// in form layout
		reviewContentScroller.pack(); 		// in grid layout
		reviewFrameOutmostWrapper.pack();	// in grid layout
		reviewFrameOutmostScroller.pack();
		wrappingExtlComposite.pack();
	}
	
	public String getParentRemarksFileIfAny(String inRootNick, String inRolledupRelevance, String inRolledupArtifactName, String inRolledupContentType) {
		String finalRemarkPathFile = null;
		
		System.out.println("At getParentRemarksFileIfAny going to read the parent remarks  for inRootNick " + inRootNick + " for inRolledupRelevance,inRolledupArtifactName " + inRolledupRelevance + " , " + inRolledupArtifactName);
		
		ERLDownload erlDownLoad = commonUIData.getCatelogPersistenceManager().readERLDownLoad(new ArtifactKeyPojo(inRootNick, inRolledupRelevance,inRolledupArtifactName,inRolledupContentType));
		
		System.out.println("At getParentRemarksFileIfAny erlDownLoad is " + erlDownLoad + " for inRolledupRelevance,inRolledupArtifactName " + inRolledupRelevance + " , " + inRolledupArtifactName);
		if (erlDownLoad != null) {
			String finalRemrkFilename = erlDownLoad.downLoadedReviewFile;
			
			System.out.println(" at getParentRemarksFileIfAny here we go..... downLoadedReviewFile loaded with ERLDownload is "  + erlDownLoad.downLoadedReviewFile);
						
			
			System.out.println("At getParentRemarksFileIfAny finalRemrkFilename is " + finalRemrkFilename);
			if (finalRemrkFilename != null && !finalRemrkFilename.equalsIgnoreCase("")) {
				finalRemarkPathFile = commonUIData.getCommons().getFullLocalPathFileNameOfDownloadedReview(commonUIData.getCurrentRootNick(), inRolledupRelevance, finalRemrkFilename);
			}
		}
		return finalRemarkPathFile;
	}
}