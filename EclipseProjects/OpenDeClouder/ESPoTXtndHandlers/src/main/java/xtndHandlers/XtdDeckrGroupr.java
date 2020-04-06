package xtndHandlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import contentHandlers.DeckableContentTypeInterface;
import contentHandlers.DeckerGrouper;
import contentHandlers.DeckerGrouperDocPojo;
import contentHandlers.DeckerGrouperItemPojo;
import espot.ArtifactKeyPojo;
import espot.ArtifactPojo;
import espot.ArtifactPrepper;
import espot.CommonData;
import espot.ErrorHandler;
import espot.SelfAuthoredArtifactpojo;
import xtdCommonTechs.ExcelHandler;
import xtdSrvrComp.ExtendedHandler;
import xtdSrvrComp.XtdCommons;
import xtdSrvrComp.XtdContntHandlerManager;
import xtdSrvrComp.XtdStdProcessRecord;

public class XtdDeckrGroupr extends DeckerGrouper implements ExtendedHandler {
	/*
	 * Handler for DeckrGroupr extended processing
	 */

	final static int DECKER_KEY_COLUMN = 0;
	final static String DETAILFILEPREFIX = "ItemDetl";
	final static String SUMMARY_FILE_SUBFIX	= "_overallSummary.xlsx";
	XtdCommons xtdCommons = null;
	public void initializeExtendedHandlerForExtdSrvrProcess(CommonData inCommonData, 
			ArtifactPojo inArtifactPojo) {
		System.out.println("At initializeExtendedHandlerForExtdSrvrProcess");
		initializeContentHandlerForExtdSrvrProcess(inCommonData, inArtifactPojo);		
		xtdCommons = (XtdCommons) inCommonData.getCommons();
	}

	@Override
	public void processItemDetail(ArtifactPojo inChildArtifactPojo) {
		System.out.println("at 2345432 processItemDetail = " + inChildArtifactPojo.artifactKeyPojo.artifactName);

		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
		DeckerGrouperItemPojo deckerGrouperItemPojo = 
				(DeckerGrouperItemPojo) deckerGrouperDocPojo.getItemByChildArtifactName(inChildArtifactPojo.artifactKeyPojo.relevance, 
																						inChildArtifactPojo.artifactKeyPojo.artifactName,
																						inChildArtifactPojo.artifactKeyPojo.contentType);

		SelfAuthoredArtifactpojo xtdChildDraft = setupDraftArtifact(inChildArtifactPojo.artifactKeyPojo);

		ExtendedHandler childContentHandlerInterface = XtdContntHandlerManager.getInstance(xtdCommons,catelogPersistenceManager,xtdChildDraft.artifactKeyPojo.contentType);
		childContentHandlerInterface.initializeExtendedHandlerForExtdSrvrProcess(commonData, xtdChildDraft);
		DeckableContentTypeInterface deckableContentTypeInterface = (DeckableContentTypeInterface) childContentHandlerInterface;

		if (deckerGrouperDocPojo.summaryShKeyColSeqNum == -1) {
		// leveraging colSeq as an indicator to know if all base parameters are already set

			deckerGrouperDocPojo.summaryShKeyColSeqNum = deckableContentTypeInterface.getSummaryShKeyColSeqNum();
			deckerGrouperDocPojo.summaryFilePage = deckableContentTypeInterface.getSummarySheetName();
			deckerGrouperDocPojo.detailFilePage = deckableContentTypeInterface.getDetailSheetName();
		}

		String srcChildFilePath = deckableContentTypeInterface.getDetailFilePath();
		System.out.println("at 2345432 processItemDetail srcChildFilePath = " + srcChildFilePath);

		deckerGrouperItemPojo.itemSummaryFile = DETAILFILEPREFIX + deckerGrouperItemPojo.itemNumber + "_" + commons.getFileNameFromURL(srcChildFilePath,commons.localFileSeparator);
		System.out.println("at 2345432 processItemDetail deckerGrouperItemPojo.itemSummaryFile = " + deckerGrouperItemPojo.itemSummaryFile);

		String targetItemDetailsPath = commonData.getCommons().getAbsolutePathFromDirAndFileNm(contentPathFolderName,deckerGrouperItemPojo.itemSummaryFile);
		System.out.println("at 2345432 processItemDetail targetItemDetailsPath = " + targetItemDetailsPath);

		deckerGrouperItemPojo.summaryShKeyColVal = deckableContentTypeInterface.getSummaryShKeyColumnVal();
		System.out.println("at 2345432 processItemDetail deckerGrouperItemPojo.summaryShKeyColVal = " + deckerGrouperItemPojo.summaryShKeyColVal);

		ExcelHandler excelHandler;
		try {
			excelHandler = new ExcelHandler(commons,srcChildFilePath,targetItemDetailsPath);
			excelHandler.cloneSheetsToNewWB(new String[]{deckerGrouperDocPojo.summaryFilePage,deckerGrouperDocPojo.detailFilePage});
		} catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in ExtdDeckrGroupr processItemDetail " + inChildArtifactPojo.artifactKeyPojo.artifactName, e);
		}
		deckerGrouperItemPojo.deckingCompletedAt = commons.getDateTS();
		writePrimer();
		System.out.println("at 2345432 processItemDetail ended");
	}

	@Override
	public void processItemSummary(ArtifactPojo inChildArtifactPojo) {
		System.out.println("at 23454231 processItemSummary inChildArtifactPojo = " + inChildArtifactPojo.artifactKeyPojo.artifactName);

		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
		DeckerGrouperItemPojo deckerGrouperItemPojo = 
				(DeckerGrouperItemPojo) deckerGrouperDocPojo.getItemByChildArtifactName(inChildArtifactPojo.artifactKeyPojo.relevance,
																inChildArtifactPojo.artifactKeyPojo.artifactName,
																inChildArtifactPojo.artifactKeyPojo.contentType);

		String srcChildItemFilePath = commonData.getCommons().getAbsolutePathFromDirAndFileNm(contentPathFolderName,deckerGrouperItemPojo.itemSummaryFile);
		System.out.println("at 23454231 processItemSummary srcChildFilePath = " + srcChildItemFilePath);

		if (deckerGrouperDocPojo.combinedFileName == null || deckerGrouperDocPojo.combinedFileName.equalsIgnoreCase("")) {
			deckerGrouperDocPojo.combinedFileName = invokedArtifactPojo.artifactKeyPojo.artifactName + SUMMARY_FILE_SUBFIX;
		}
		System.out.println("at 23454231 processItemSummary DeckerGrouperDocPojo.OVERALLSUMMARYFILENAME = " + deckerGrouperDocPojo.combinedFileName);
		String targetOverallSummaryPath = commonData.getCommons().getAbsolutePathFromDirAndFileNm(contentPathFolderName,deckerGrouperDocPojo.combinedFileName);

		System.out.println("at 23454231 processItemSummary targetItemDetailsPath = " + targetOverallSummaryPath);

		ExcelHandler excelHandler;
		try {
			excelHandler = new ExcelHandler(commons,srcChildItemFilePath,targetOverallSummaryPath);

			System.out
			.println("at 23454231 processItemSummary deckerGrouperDocPojo.summaryFilePage is " + deckerGrouperDocPojo.summaryFilePage);

			try {
				excelHandler.copyRows(deckerGrouperDocPojo.summaryFilePage,deckerGrouperDocPojo.summaryShKeyColSeqNum,deckerGrouperItemPojo.summaryShKeyColVal);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in ExtdDeckrGroupr processItemSummary " + inChildArtifactPojo.artifactKeyPojo.artifactName, e);
		}
		deckerGrouperDocPojo.deckerEdited = false;
		writePrimer();		
		System.out.println("at 23454231 processItemSummary ended");
	}

	@Override
	public String processXtdStdProcessRec(String xtdProcStatus) {
		System.out.println("at start ExtdDeckrGroupr processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		System.out.println("at ExtdDeckrGroupr This process wont be called");		
		System.out.println("at end ExtdDeckrGroupr processxtdStdProcessRec inArtifactKeyPojo = " + invokedArtifactPojo.artifactKeyPojo.artifactName);
		return xtdProcStatus;		
	}

	@Override
	public String absorbInput(Object inInput, String inInstruction) {
		String processEndingStatus = XtdStdProcessRecord.ERLRecord_CONTINUE;
		System.out.println("at 23454233 This process wont be called");		
		return processEndingStatus;
	}

	private SelfAuthoredArtifactpojo setupDraftArtifact(ArtifactKeyPojo inArtifactKeyPojo){
		ArtifactPrepper artifactPrepper = new ArtifactPrepper(inArtifactKeyPojo, commonData);
		SelfAuthoredArtifactpojo extdSelfAuthoredArtifactpojo = artifactPrepper.createDraft();
		extdSelfAuthoredArtifactpojo.draftingState = SelfAuthoredArtifactpojo.ArtifactStatusDraft;
		catelogPersistenceManager.insertArtifactUI(extdSelfAuthoredArtifactpojo);
		return extdSelfAuthoredArtifactpojo;
	}
}