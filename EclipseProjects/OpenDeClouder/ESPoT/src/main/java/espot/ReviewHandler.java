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

	private Composite wrappingExtlComposite;
	private Composite reviewFrameOutmostScroller;
	private Composite reviewFrameOutmostWrapper;	// in grid layout
	private Composite reviewContentScroller; 		// in grid layout
	private Composite reviewContntHolder; 			// in form layout

	//String itemName = null;
	private CommonUIData commonUIData;
	private Document newCommentDoc = null;
	//private String finalReviewPathFileName = null;
	//private ArtifactAllReviewsPojo artifactAllReviewsPojo = null;
	DownloadedReviewsHandler downloadedReviewsHandler = null;
	//private Document allReviewsDoc = null;
	private UsersDisplay authorsDisplay = null;
	private UsersDisplay requestorDisplay = null;	
	private Shell outerMainShell = null;
	private ERLStatusDisplay erlStatusDisplay = null;
	private boolean reviewVisible = true;
	private Group hideReviewGrp = null;	
	private Text newCommentText = null;
	//private ContentHandlerSpecs contentHandlerSpecs = null;

	//private ArtifactPojo reviewArtifactPojo = null;
	private ArtifactKeyPojo reviewArtifactKeyPojo = null;
	private ItemPojo reviewItemPojo = null;
	private String reviewOf = "";	// this is artifactName for non-rollup artifacts and
											// - itemID for rollup child.
	private String reviewERLRelevance = "";
	private String reviewERLContentType = "";

	private String reviewERLStatus = "";
	private String reviewERLRequestor = "";
	private String reviewERLAuthor = "";
	private boolean doesUserHaveUpdateRights = false;

	public ReviewHandler(CommonUIData inCommonUIData,
			Composite inWrappingComposite,
			ArtifactPojo inArtifactPojo, 
			Shell inMainShell) {

		initReviewHandler(inCommonUIData,
				inWrappingComposite,
				inArtifactPojo, 
				null,
				inMainShell);
	}

	public ReviewHandler(CommonUIData inCommonUIData,
			Composite inWrappingComposite,
			ArtifactPojo inArtifactPojo, 
			//String inItemName,
			ItemPojo inItemPojo,
			Shell inMainShell){
		initReviewHandler(inCommonUIData,
				inWrappingComposite,
				inArtifactPojo, 
				inItemPojo,
				inMainShell);		
	}
	
	public void initReviewHandler(CommonUIData inCommonUIData,
							Composite inWrappingComposite,
							ArtifactPojo inArtifactPojo, 
							//String inItemName,
							ItemPojo inItemPojo,
							Shell inMainShell)
	{
		commonUIData = inCommonUIData;
		wrappingExtlComposite = inWrappingComposite;
		//reviewArtifactPojo = inArtifactPojo;		
		//itemName = inItemName;	//itemName will be same as the artifact name for artifact reviews

		if (inItemPojo!= null) {
			if (commonUIData.getContentHandlerSpecs(inItemPojo.contentType).rollupAddupType) {
				reviewArtifactKeyPojo = inArtifactPojo.artifactKeyPojo;
				reviewOf = inItemPojo.itemID;
				reviewERLRelevance = inItemPojo.relevance;
				reviewERLContentType = inItemPojo.contentType;
				reviewERLStatus = inItemPojo.status;
				reviewERLRequestor = inItemPojo.requestor;
				reviewERLAuthor = inItemPojo.author;			
			} else {
			// for non rollup types use the item pojo's referred ERL for details
				reviewArtifactKeyPojo = new ArtifactKeyPojo(inArtifactPojo.artifactKeyPojo.rootNick, 
															inItemPojo.relevance,
															inItemPojo.artifactName,
															inItemPojo.contentType);
				reviewOf = inItemPojo.artifactName;
				reviewERLRelevance = inItemPojo.relevance;
				reviewERLContentType = inItemPojo.contentType;
				reviewERLStatus = inItemPojo.status;
				reviewERLRequestor = inItemPojo.requestor;
				reviewERLAuthor = inItemPojo.author;			

				ERLDownload erlDownLoad = commonUIData.getCatelogPersistenceManager().readERLDownLoad(
																				reviewArtifactKeyPojo);
				if (erlDownLoad!=null) {	
					System.out.println("At initReviewHandler erlDownLoad of non-rollup individual item is " + erlDownLoad);						
					reviewERLStatus = erlDownLoad.erlStatus;
					reviewERLRequestor = erlDownLoad.requestor;
					reviewERLAuthor = erlDownLoad.author;
				}
			}					
		} else {
			reviewArtifactKeyPojo = inArtifactPojo.artifactKeyPojo;
			reviewOf = reviewArtifactKeyPojo.artifactName;
			reviewERLRelevance = reviewArtifactKeyPojo.relevance;
			reviewERLContentType = reviewArtifactKeyPojo.contentType;
			reviewERLStatus = inArtifactPojo.erlStatus;
			reviewERLRequestor = inArtifactPojo.requestor;
			reviewERLAuthor = inArtifactPojo.author;			
		}
		
		downloadedReviewsHandler = new DownloadedReviewsHandler(inCommonUIData, reviewArtifactKeyPojo);

		//contentHandlerSpecs = commonUIData.getContentHandlerSpecs(artifactPojo.artifactKeyPojo.contentType);

		UserPojo deskUserDetail = commonUIData.getUsersHandler().getUserDetailsFromRootSysLoginID(commonUIData.getCommons().userName);
		
		if (deskUserDetail.hasAdminPrivilege() 
			|| deskUserDetail.hasTeamLeaderPrivilege()
			|| commonUIData.getUsersHandler().doesUserHaveRightsOverMember(
					commonUIData.getCommons().userName, reviewERLAuthor)) {
			doesUserHaveUpdateRights = true;
		} else {
			doesUserHaveUpdateRights = false;
		}

		outerMainShell = inMainShell;
		System.out.println("@ReviewHandler reviewArtifactKeyPojo = " + reviewArtifactKeyPojo);
		System.out.println("@ReviewHandler reviewOf = " + reviewOf);
		System.out.println("@ReviewHandler reviewERLRelevance = " + reviewERLRelevance);
		System.out.println("@ReviewHandler reviewERLContentType = " + reviewERLContentType);
		System.out.println("@ReviewHandler reviewERLStatus = " + reviewERLStatus);
		System.out.println("@ReviewHandler reviewERLRequestor = " + reviewERLRequestor);
		System.out.println("@ReviewHandler reviewERLAuthor = " + reviewERLAuthor);

		//ArtifactKeyPojo finalArtifactKeyPojo = inCommonUIData.getContentHandlerSpecsMap().get(inArtifactPojo.artifactKeyPojo.contentType)
		//										.getFinalArtifactKeyPojo(inArtifactPojo.artifactKeyPojo.rootNick, 
		//											inArtifactPojo.artifactKeyPojo.relevance,
		//											inArtifactPojo.artifactKeyPojo.artifactName,
		//											inCommonUIData.getCurrentRootPojo().fileSeparator);
		//				
		//String finalReviewPathFileName = getParentRemarksFileIfAny(
		//								finalArtifactKeyPojo.rootNick,
		//								finalArtifactKeyPojo.relevance,
		//								finalArtifactKeyPojo.artifactName,
		//								finalArtifactKeyPojo.contentType);
		//
		//reviewFrameOutmostScroller = new Composite(wrappingExtlComposite,SWT.BORDER);
		//GridData gridDataOuterScrl = new GridData(SWT.FILL, SWT.FILL, true, true);
		//reviewFrameOutmostScroller.setLayoutData(gridDataOuterScrl);
		//
		//reviewFrameOutmostWrapper = new Composite(reviewFrameOutmostScroller, SWT.NONE);
		//reviewFrameOutmostScroller.setLayout(new GridLayout());
		//GridData gridDatagridDataOuterWrpr = new GridData(SWT.FILL, SWT.FILL, true, true);
		//reviewFrameOutmostWrapper.setLayoutData(gridDatagridDataOuterWrpr);

		reviewFrameOutmostScroller = new Composite(wrappingExtlComposite,SWT.BORDER);
		GridData gridDataOuterScrl = new GridData(SWT.FILL, SWT.FILL, true, true);
		reviewFrameOutmostScroller.setLayoutData(gridDataOuterScrl);	// setting layout parameters for itself
		reviewFrameOutmostScroller.setLayout(new GridLayout());			// setting layout to be used by children
		
		reviewFrameOutmostWrapper = new Composite(reviewFrameOutmostScroller, SWT.NONE);
		GridData gridDatagridDataOuterWrpr = new GridData(SWT.FILL, SWT.FILL, true, true);
		reviewFrameOutmostWrapper.setLayoutData(gridDatagridDataOuterWrpr);
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
					reviewFrameOutmostWrapper.pack();
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

		
		//ArtifactKeyPojo finalArtifactKeyPojo = inCommonUIData.getContentHandlerSpecsMap().get(inArtifactPojo.artifactKeyPojo.contentType)
		//		.getFinalArtifactKeyPojo(inArtifactPojo.artifactKeyPojo.rootNick, 
		//			inArtifactPojo.artifactKeyPojo.relevance,
		//			inArtifactPojo.artifactKeyPojo.artifactName,
		//			inCommonUIData.getCurrentRootPojo().fileSeparator);
		//
		//String finalReviewPathFileName = getParentRemarksFileIfAny(
		//			finalArtifactKeyPojo.rootNick,
		//			finalArtifactKeyPojo.relevance,
		//			finalArtifactKeyPojo.artifactName,
		//			finalArtifactKeyPojo.contentType);
		//try {
		//	System.out.println("@ReviewHandler allReviewPathFileName = " + finalReviewPathFileName);
		//	
		//	if (!finalReviewPathFileName.isEmpty()) {
		//	} else {
		//		artifactAllReviewsPojo = new ArtifactAllReviewsPojo();
		//		allReviewsDoc = commonUIData.getCommons().getDocumentFromXMLFile(finalReviewPathFileName);
		//		artifactAllReviewsPojo.buildArtifactAllReviewsPojoFromFromDoc(allReviewsDoc);
		//	}
		//} catch (SAXException | IOException | ParserConfigurationException e) {
		//	e.printStackTrace();
		//	ErrorHandler.showErrorAndQuit(commonUIData.getCommons(), "Error in ReviewHandler constructor reviewOf " 
		//	+ " " + reviewOf, e);
		//}
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
		if (downloadedReviewsHandler.getArtifactAllReviewsPojo() != null &&  downloadedReviewsHandler.getArtifactAllReviewsPojo().getItemAllReviews(reviewOf) != null) {
			System.out.println("prevCommentText = " + prevCommentText);
			System.out.println("artifactAllReviewsPojo = " + downloadedReviewsHandler.getArtifactAllReviewsPojo());
			System.out.println("artifactAllReviewsPojo.itemsReviews = " + downloadedReviewsHandler.getArtifactAllReviewsPojo().itemsReviews);
			System.out.println("reviewArtifactKeyPojo = " + reviewArtifactKeyPojo);
			System.out.println("reviewOf = " + reviewOf);
			prevCommentText.setText(downloadedReviewsHandler.getArtifactAllReviewsPojo().getItemAllReviews(reviewOf));
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

		ClientSideNew_ReviewPojo newReviewPojo = null;
		try {
			newReviewPojo = commonUIData.getCatelogPersistenceManager().readReview(reviewArtifactKeyPojo,reviewOf);

			System.out.println("@@2r4 artifactKeyPojo = " + newReviewPojo);
			
			if (newReviewPojo == null) {
				newCommentText.setText("");
			} else {
				System.out.println("@@2r4 artifactKeyPojo.artifactKeyPojo = " + newReviewPojo.artifactKeyPojo);
				System.out.println("new comment already exists at:" + newReviewPojo.reviewFileName);

				System.out.println("commons:" + commonUIData.getCommons());
				System.out.println("commons.defaultUIRootNick:" + commonUIData.getCommons().getCurrentRootNick());
				System.out.println("reviewPojo" + newReviewPojo);
				System.out.println("reviewPojo.artifactKeyPojo" + newReviewPojo.artifactKeyPojo);
				System.out.println("reviewPojo.artifactKeyPojo.relevance" + newReviewPojo.artifactKeyPojo.relevance);
				System.out.println("reviewPojo.localFileName:" + newReviewPojo.reviewFileName);
				
				String localReviewFullPathFileName = commonUIData.getCommons().getFullLocalPathFileNameOfNewReview(commonUIData.getCommons().getCurrentRootNick(), newReviewPojo.artifactKeyPojo.relevance, newReviewPojo.reviewFileName);

				Document reviewXMLDocument = commonUIData.getCommons().getDocumentFromXMLFile(localReviewFullPathFileName);
				newReviewPojo.buildReviewPojoFromDocument(reviewXMLDocument);		
				newCommentText.setText(newReviewPojo.description);
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
		System.out.println("before reassign display: reviewERLRequestor is " + reviewERLRequestor);
		System.out.println("before reassign display: userName is " + commonUIData.getCommons().userName);

		//UserPojo deskUserDetail = commonUIData.getUsersHandler().getUserDetailsFromRootSysLoginID(commonUIData.getCommons().userName);

		//if (!contentHandlerSpecs.rollupAddupType && 
		//		(commonUIData.getUsersHandler().doesUserHaveRightsOverMember(
		//			commonUIData.getCommons().userName, artifactPojo.requestor))) {
		//		//commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.requestor) || deskUserDetail.hasAdminPrivilege() || deskUserDetail.hasTeamLeaderPrivilege())) { 
		////Only requestor can reassign. Later enhance to allow admins to change as well
		if (doesUserHaveUpdateRights) {
			
			if (newReviewPojo != null) {
				if (newReviewPojo.reassignedAuthor != null) {
					System.out.println("before reassign display2: reviewPojo.reassignedAuthor is " + newReviewPojo.reassignedAuthor);
					authorsDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,newReviewPojo.reassignedAuthor,false,UsersDisplay.AUTHOR_REASSIGN_TEXT);
				}
			} else {
				System.out.println("before reassign display2: reviewERLAuthor is " + reviewERLAuthor);
				authorsDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,reviewERLAuthor,true,UsersDisplay.AUTHOR_REASSIGN_TEXT);
			}
		//}
		//if (commonUIData.getCommons().userName.equalsIgnoreCase(artifactPojo.requestor) || deskUserDetail.hasAdminPrivilege() || deskUserDetail.hasTeamLeaderPrivilege()) {
		//Only requestor can reassign. Later enhance to allow admins to change as well
			if (newReviewPojo != null) {
				if (newReviewPojo.reassignedRequestor != null) {
					System.out.println("before reassign display2: reviewPojo.reassignedRequestor is " + newReviewPojo.reassignedRequestor);
					requestorDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,newReviewPojo.reassignedRequestor,false,UsersDisplay.REQUESTOR_REASSIGN_TEXT);
				}
			} else {
				System.out.println("before reassign display2: reviewERLRequestor is " + reviewERLRequestor);
				requestorDisplay = new UsersDisplay(commonUIData.getUsersHandler(),reviewActionsGrp,reviewERLRequestor,true,UsersDisplay.REQUESTOR_REASSIGN_TEXT);
			}
		}
		
		///////////////// NEW ERL status starts
		final Group erlStatusGroup = new Group(reviewActionsGrp, SWT.CENTER);
		erlStatusGroup.setLayout(new FillLayout());
		//erlStatusGroup.setText("ERL Status");

		formData = new FormData();
		formData.top = new FormAttachment(lastReviewWdgtGrp,0,SWT.BOTTOM);
		reviewActionsGrp.setLayoutData(formData);

		ERLStatusDisplay currentERLStatusDisplay = new ERLStatusDisplay(null, erlStatusGroup, reviewERLStatus, false, ERLStatusDisplay.CURR_STATUS_TEXT);
		
		String[] validActions = null;
		if (doesUserHaveUpdateRights) {
			validActions = ArtifactPojo.ADMIN_VALID_ACTIONS;
		} else if (commonUIData.getCommons().userName.equalsIgnoreCase(reviewERLRequestor)){
			validActions = ArtifactPojo.REQUESTOR_VALID_ACTIONS;
		} else if (commonUIData.getCommons().userName.equalsIgnoreCase(reviewERLAuthor)){
			validActions = ArtifactPojo.AUTHOR_VALID_ACTIONS;
		}

		if (doesUserHaveUpdateRights) {
			if (newReviewPojo !=null) {
				if (newReviewPojo.newERLStatus != null) {
					erlStatusDisplay = new ERLStatusDisplay(validActions, erlStatusGroup, newReviewPojo.newERLStatus, false, ERLStatusDisplay.STATUS_ASSIGN_TEXT);
				}
			} else {
				erlStatusDisplay = new ERLStatusDisplay(validActions, erlStatusGroup, reviewERLStatus, true, ERLStatusDisplay.STATUS_ASSIGN_TEXT);
			}
		}
		///////////////// New ERL status ends

		Button submitReviewButton = new Button(reviewActionsGrp, SWT.PUSH);
		submitReviewButton.setText("SubmitReview");
		submitReviewButton.setToolTipText("Submit the review and any change to ownership and status.");
		
		if (newReviewPojo != null) {
			submitReviewButton.setEnabled(false);
			submitReviewButton.setText("Submitted already");
		} else {
			submitReviewButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
	
					try {
						((Button) event.getSource()).setEnabled(false);
						System.out.println("Saving Remarks");

						ClientSideNew_ReviewPojo clientSideNew_ReviewPojo = new ClientSideNew_ReviewPojo(
								commonUIData.getCommons(), reviewArtifactKeyPojo,reviewOf,commonUIData.getCommons().userName,commonUIData.getCommons().getCurrentTimeStamp());
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
						String localReviewPathFileName = commonUIData.getCommons().getFullLocalPathFileNameOfNewReview(commonUIData.getCommons().getCurrentRootNick(), reviewArtifactKeyPojo.relevance, clientSideNew_ReviewPojo.reviewFileName);
	
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
	
	//public String getParentRemarksFileIfAny(String inRootNick, String inRolledupRelevance, String inRolledupArtifactName, String inRolledupContentType) {
	//	String finalRemarkPathFile = null;
	//	
	//	System.out.println("At getParentRemarksFileIfAny going to read the parent remarks  for inRootNick " + inRootNick + " for inRolledupRelevance,inRolledupArtifactName " + inRolledupRelevance + " , " + inRolledupArtifactName);
	//	
	//	ERLDownload erlDownLoad = commonUIData.getCatelogPersistenceManager().readERLDownLoad(new ArtifactKeyPojo(inRootNick, inRolledupRelevance,inRolledupArtifactName,inRolledupContentType));
	//	
	//	System.out.println("At getParentRemarksFileIfAny erlDownLoad is " + erlDownLoad + " for inRolledupRelevance,inRolledupArtifactName " + inRolledupRelevance + " , " + inRolledupArtifactName);
	//	if (erlDownLoad != null) {
	//		String finalRemrkFilename = erlDownLoad.downLoadedReviewFile;
	//		
	//		System.out.println(" at getParentRemarksFileIfAny here we go..... downLoadedReviewFile loaded with ERLDownload is "  + erlDownLoad.downLoadedReviewFile);
	//					
	//		
	//		System.out.println("At getParentRemarksFileIfAny finalRemrkFilename is " + finalRemrkFilename);
	//		if (!finalRemrkFilename.isEmpty()) {
	//			finalRemarkPathFile = commonUIData.getCommons().getFullLocalPathFileNameOfDownloadedReview(commonUIData.getCurrentRootNick(), inRolledupRelevance, finalRemrkFilename);
	//		}
	//	}
	//	return finalRemarkPathFile;
	//}
}