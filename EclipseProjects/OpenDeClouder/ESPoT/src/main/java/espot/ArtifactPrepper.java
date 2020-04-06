package espot;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtifactPrepper {
	/*
	 * Finds out where to get the source for creating a new draft
	 * out of the below options
		use template
		use active draft
		use erl download - stand alone
		use erl download - rolledup child
	 */
	public boolean errorEncountered;
	public boolean useTemplate;
	public boolean useActiveDraft;
	public boolean useErlDownloadStandalone;
	public boolean useErlDownloadRolledupChild;

	public boolean localDraftActive;
	public boolean uptoDateERLExists;
	public boolean isRollupChild;

	public SelfAuthoredArtifactpojo localDraft;
	public ERLDownload erlDownload;
	public ERLDownload parentERLDownload;	
	public ItemPojo uptoDateERLItemPojo;
	public byte[] baseDocContentBytes;
	
	public ArtifactKeyPojo targetArtifactKeyPojo;
	public ArtifactKeyPojo parentArtifactKeyPojo;
	String itemID;
	
	private String newAuthor = null;
	private String currentAuthor = null;
	private String currentRequestor = null;
	private CommonData commonData;
	private Commons commons;
	private CatelogPersistenceManager catelogPersistenceManager;
	private HashMap<String, ContentHandlerSpecs> contentHandlerSpecsMap;
	private RootPojo currentRootPojo;
	private ContentHandlerSpecs targetArtifactContentHandlerSpecs;

	public ArtifactPrepper(ArtifactKeyPojo inTargetArtifactKeyPojo, String inItemID, CommonData inCommonData, String inNewAuthor) {
		initProcess(inTargetArtifactKeyPojo,inItemID,inCommonData,inNewAuthor);
	}
	
	public ArtifactPrepper(ArtifactKeyPojo inTargetArtifactKeyPojo, String inItemID, CommonData inCommonData) {
		initProcess(inTargetArtifactKeyPojo,inItemID,inCommonData,null);
	}

	public ArtifactPrepper(ArtifactKeyPojo inTargetArtifactKeyPojo, CommonData inCommonData, String inNewAuthor) {
		initProcess(inTargetArtifactKeyPojo,"",inCommonData,inNewAuthor);	// shortcut when itemid is unknown
	}

	public ArtifactPrepper(ArtifactKeyPojo inTargetArtifactKeyPojo, CommonData inCommonData) {
		initProcess(inTargetArtifactKeyPojo,"",inCommonData,null);	// shortcuts 
	}
	
	private void initProcess(ArtifactKeyPojo inTargetArtifactKeyPojo, String inItemID, CommonData inCommonData, String inNewAuthor) {
		commonData = inCommonData;
		commons = commonData.getCommons();
		itemID = inItemID;

		newAuthor = inNewAuthor;
		targetArtifactKeyPojo = inTargetArtifactKeyPojo;
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		contentHandlerSpecsMap = commonData.getContentHandlerSpecsMap();
		currentRootPojo = commonData.getCurrentRootPojo();
		setFlags();
	}
	
	private void setFlags() {
		errorEncountered = false;
		useTemplate = false;
		useActiveDraft = false;
		useErlDownloadStandalone = false;
		useErlDownloadRolledupChild = false;

		localDraftActive = false;
		uptoDateERLExists = false;
		isRollupChild = false;
		
		// check active draft existence
		localDraft = catelogPersistenceManager.readSelfAuthoredArtifact(targetArtifactKeyPojo);
		if (localDraft != null) {
			System.out.println("at setFlags localDraft.draftingState is " + localDraft.draftingState);
			System.out.println("at setFlags SelfAuthoredArtifactpojo.ArtifactStatusDraft is " + SelfAuthoredArtifactpojo.ArtifactStatusDraft);
			System.out.println("at setFlags SelfAuthoredArtifactpojo.ArtifactStatusToBeBatchUploaded is " + SelfAuthoredArtifactpojo.ArtifactStatusToBeBatchUploaded);

			if (!localDraft.draftingState.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusProcessed)
				&& !localDraft.draftingState.equalsIgnoreCase(SelfAuthoredArtifactpojo.ArtifactStatusOutdated)) {
				
				// if the artifact is already processed then it will be part of erldownload anyway
				// if the artifact is outdated that means there is a more recent erl
				// in the extended server as the process is done via single thread
				// bunch up all changes into same draft safely even after setting to tobeuploaded
				// 
				localDraftActive= true;
			}
			System.out.println("at setFlags localDraftActive is " + localDraftActive);
		}
		
		//check if its a rollup child and get the erlDownload
		targetArtifactContentHandlerSpecs = contentHandlerSpecsMap.get(targetArtifactKeyPojo.contentType);

		System.out.println("at setFlags targetArtifactContentHandlerSpecs.rollupOrAddup is " + targetArtifactContentHandlerSpecs.rollupOrAddup);
		
		if ((targetArtifactContentHandlerSpecs.rollupOrAddup.equalsIgnoreCase(ContentHandlerSpecs.ROLLUP_ADDUP_TYPE_ROLLUP))
			|| (targetArtifactContentHandlerSpecs.rollupOrAddup.equalsIgnoreCase(ContentHandlerSpecs.ROLLUP_ADDUP_TYPE_ADDUP))){
			isRollupChild = true;
			erlDownload = null;

			parentArtifactKeyPojo = targetArtifactContentHandlerSpecs.getFinalArtifactKeyPojo(
					targetArtifactKeyPojo.rootNick, 
					targetArtifactKeyPojo.relevance, 
					targetArtifactKeyPojo.artifactName, 
					currentRootPojo.fileSeparator);
			parentERLDownload = catelogPersistenceManager.readERLDownLoad(parentArtifactKeyPojo);
			if (parentERLDownload != null) {
				if (!parentERLDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_AVAILABLE)) {
					if (commons.processMode == Commons.CLIENT_MACHINE) {
						if (parentERLDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED) 
							|| parentERLDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
							String popUpMsg = parentERLDownload.artifactKeyPojo.artifactName + " is not available. Do you want to subscribe?";
							if (ErrorHandler.confirmationPopup(((CommonUIData) commonData).getESPoTDisplay(), popUpMsg)) {
								commonData.getCatelogPersistenceManager().replaceSubscription(parentERLDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
							}
						} else {
							String popUpMsg = parentERLDownload.artifactKeyPojo.artifactName + " is not available. Pl. wait until its downloaded";
							ErrorHandler.infoPopup(((CommonUIData) commonData).getESPoTDisplay(), popUpMsg);
						}
					} else {
						ErrorHandler.showErrorAndQuit(commons, "expected erl not yet downloaded " + parentERLDownload.artifactKeyPojo.artifactName);						
					}
					errorEncountered = true;
					return;
				}
				//erlExists = true; ensure that the same item is present with a newer version at the ERL
				
				ItemPojo draftItemPojo = null;
				if (localDraftActive) {
				
					ContentHandlerInterface contentHandlerInterfaceOfChild = 
							ContentHandlerManager.getInstance(commons, catelogPersistenceManager, targetArtifactKeyPojo.contentType);
					contentHandlerInterfaceOfChild
					.initNonUIContentHandlerForDraftArtifact(
						commonData,
						localDraft
						);
					draftItemPojo = contentHandlerInterfaceOfChild.getFocusedItemPojo();
				} else if (itemID != null && !itemID.equals("")) {
					draftItemPojo = ItemPojo.getStartUpItemPojo(itemID,targetArtifactKeyPojo.artifactName,targetArtifactKeyPojo.contentType,targetArtifactKeyPojo.relevance);
				}
				
				ContentHandlerInterface contentHandlerInterfaceOfParent = 
						ContentHandlerManager.getInstance(commons, catelogPersistenceManager, parentArtifactKeyPojo.contentType);
				contentHandlerInterfaceOfParent
				.initNonUIContentHandlerForDownloadedArtifact(
					commonData,
					parentERLDownload
					);
				if (draftItemPojo!= null) {
					uptoDateERLItemPojo = contentHandlerInterfaceOfParent.getUptoDateERLItem(draftItemPojo);
				} else {
					uptoDateERLItemPojo = contentHandlerInterfaceOfParent.getERLItemByChildArtifactName
																		(targetArtifactKeyPojo.relevance,
																		targetArtifactKeyPojo.artifactName,
																		targetArtifactKeyPojo.contentType);
				}

				if (uptoDateERLItemPojo!=null) {
					uptoDateERLExists = true;
					localDraftActive = false;	// as there is a newer erl item, the local draft cannot be used anymore
					baseDocContentBytes = contentHandlerInterfaceOfParent.getBaseItemDocBytes(uptoDateERLItemPojo);
				}
			}
		} else {
			System.out.println("at setFlags targetArtifactContentHandlerSpecs.rollupOrAddup else path is " + targetArtifactContentHandlerSpecs.rollupOrAddup);
			isRollupChild = false;
			parentERLDownload = null;
			erlDownload = catelogPersistenceManager.readERLDownLoad(targetArtifactKeyPojo);

			if (erlDownload != null) {
				if (!erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_AVAILABLE)) {
					if (commons.processMode == Commons.CLIENT_MACHINE) {
						if (erlDownload.subscriptionStatus.equalsIgnoreCase(ERLDownload.NEVER_SUBSCRIBED) 
							|| erlDownload.localCopyStatus.equalsIgnoreCase(ERLDownload.LOCAL_COPY_TOBERENEWED)) {
							String popUpMsg = erlDownload.artifactKeyPojo.artifactName + " is not available. Do you want to subscribe?";
							if (ErrorHandler.confirmationPopup(((CommonUIData) commonData).getESPoTDisplay(), popUpMsg)) {
								commonData.getCatelogPersistenceManager().replaceSubscription(erlDownload,ERLDownload.CURRENTLY_SUBSCRIBED);
							}
						} else {
							String popUpMsg = erlDownload.artifactKeyPojo.artifactName + " is not available. Pl. wait until its downloaded";
							ErrorHandler.infoPopup(((CommonUIData) commonData).getESPoTDisplay(), popUpMsg);
						}
					} else {
						ErrorHandler.showErrorAndQuit(commons, "expected erl not yet downloaded " + erlDownload.artifactKeyPojo.artifactName);						
					}
					errorEncountered = true;
					return;
				}
			} else {
				System.out.println("at setFlags ERLExists ");
			}
		}
		if (!errorEncountered) {

			if (isRollupChild && uptoDateERLExists) {
				useErlDownloadRolledupChild = true;
				currentAuthor = uptoDateERLItemPojo.author;
				currentRequestor = parentERLDownload.requestor;
			} else if (localDraftActive) {
				useActiveDraft = true;						
				currentAuthor = localDraft.author;
				currentRequestor = localDraft.requestor;
			} else if (erlDownload!=null) {
				useErlDownloadStandalone = true;
				currentAuthor = erlDownload.author;
				currentAuthor = erlDownload.requestor;
			} else {
				useTemplate = true;
				newAuthor = (newAuthor!=null)?newAuthor:commons.userName;
			}
			System.out.println("at setFlags useErlDownloadRolledupChild " + useErlDownloadRolledupChild);
			System.out.println("at setFlags useActiveDraft " + useActiveDraft);
			System.out.println("at setFlags useErlDownloadStandalone " + useErlDownloadStandalone);
			System.out.println("at setFlags useTemplate " + useTemplate);
		}
	}
	
	public SelfAuthoredArtifactpojo createDraft() {
		ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);

		int maxLocalVerionNumber;
		maxLocalVerionNumber = catelogPersistenceManager.getMaxDBVersionNumberOfSelfAuthoredArtifact(
				targetArtifactKeyPojo);
		if (commons.processMode == Commons.EXTENDED_CATALOG_SERVER && maxLocalVerionNumber >  -1) {
			// for extended servers, no need to maintain multiple versions
			//catelogPersistenceManager.deleteAllSelfAuthoredArtifacts(targetArtifactKeyPojo);
			// clear old drafts starts
			//read all versions of drafts from db
			ArrayList<SelfAuthoredArtifactpojo> allVersionsSelfAuthoredArtifacts 
				= catelogPersistenceManager.readAllVersionsSelfAuthoredArtifacts(targetArtifactKeyPojo);

			//scroll thru allVersions, form the file name and archive
			for (SelfAuthoredArtifactpojo oneVersionOfSelfAuthoredArtifact : allVersionsSelfAuthoredArtifacts) {
				
				//delete the draft in db
				catelogPersistenceManager.deleteSelfAuthoredArtifactpojo(
						oneVersionOfSelfAuthoredArtifact);

				//ArtifactMover artifactMover = ArtifactMover.getInstance(commonData);
				artifactMover.archiveDraft(oneVersionOfSelfAuthoredArtifact);

				System.out.println("At ArtifactPrepper createDraft old draft archived successfully."
						+ " root: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.rootNick
						+ "; relevance: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.relevance
						+ "; artifact: " + oneVersionOfSelfAuthoredArtifact.artifactKeyPojo.artifactName
						+ "; versionNum: " + oneVersionOfSelfAuthoredArtifact.unpulishedVerNum
						+ ". draftingState: " + oneVersionOfSelfAuthoredArtifact.draftingState
				);
			}
			// clear old drafts ends

			setFlags(); // need reset flags again since the drafts just got deleted.
			maxLocalVerionNumber = -1;
		}
		maxLocalVerionNumber = maxLocalVerionNumber + 1;

		String versionedFileName = commons.getVersionedFileName(targetArtifactKeyPojo.artifactName,
				targetArtifactContentHandlerSpecs.extension, maxLocalVerionNumber);
		SelfAuthoredArtifactpojo draftSelfAuthoredArtifactpojo = new SelfAuthoredArtifactpojo(
				targetArtifactKeyPojo,
				(currentRequestor!=null)?currentRequestor:commons.userName,	// Requester
				(newAuthor!=null)?newAuthor:currentAuthor, 					// Author
				targetArtifactContentHandlerSpecs.hasSpecialHandler,
				"",					// ReviewFileName
				ArtifactPojo.ERLSTAT_DRAFT,	// ERLStatus
				null,				// ParentKey
				versionedFileName, 	// localFilePath
				SelfAuthoredArtifactpojo.ArtifactStatusDraft, // DraftingState
				"", 				// ReqRespFileName
				maxLocalVerionNumber,					// localVerionNumber
				""					// DelegatedTo
				);

		if (useErlDownloadRolledupChild) {
			artifactMover.createDraftFromBaseContent(baseDocContentBytes, draftSelfAuthoredArtifactpojo);
		} else if (useErlDownloadStandalone) {
			artifactMover.moveArtifact(erlDownload, draftSelfAuthoredArtifactpojo);
		} else if (useActiveDraft) {
			artifactMover.moveArtifact(localDraft, draftSelfAuthoredArtifactpojo);
		} else if (useTemplate) {
			artifactMover.moveFromTemplate(targetArtifactContentHandlerSpecs.template, draftSelfAuthoredArtifactpojo);
		}
		return draftSelfAuthoredArtifactpojo;
	}
}