package contentHandlers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import espot.ArtifactKeyPojo;
import espot.ArtifactPrepper;
import espot.ContentHandlerInterface;
import espot.ContentHandlerManager;
import espot.ERLDownload;
import espot.ErrorHandler;
import espot.GenericGrouper;
import espot.GenericGrouperDocPojo;
import espot.GenericItemDocPojo;
import espot.ItemPojo;
import espot.RemoteAccesser;
import espot.RequestProcesserPojo;
import espot.RootPojo;
import espot.ScreenPatternFieldPojo;

public class DeckerGrouper extends DeckerLite {
	/*
	 * This content handler helps to deck up contents that implements decking interface
	 */
//	protected String[] centerAddlColInteractions = null; 	// this are not display fields by themselves, 
//	protected String[] centerAddlColFileFields = null; 		// but holder for interactions and file field reference
//	public static String DECKINGSTAT_DECKED = "Decked";
//	public static String DECKINGSTAT_YETTOBEDECKED = "NotDeckedYet";															
//	final static String COL_SEL_HDR = "SELECT";
//	
//	Text applicationText;
//	Text statusText;
//	Text reviewerText;
//	Text authorNameText;
//	CCombo childContentTypeList;	// childType is a persisted field as all items must be of same type
//	CCombo relevanceList;			// Volatile field only for item selection as each can have different value
//	CCombo artifactNameList;		// Also a volatile field ...
//
//	final static String FREE_FORM_TEXT = "freeformtext";
//	final static String PROTECTED_TEXT = "protected";
//	final static String DROPDOWN_FIELD = "dropdown";
//	final static String ITEM_SUMMARYLIT = "item_summary";
//	final static String FILE_LINK_BUTTON = "filelinkbutton";
//	final static String CONTENT_TYPE = "ContentType";
//	final static String RELEVANCEHDR = "Relevance";
//	final static String SUMMARYFILEHDR = "ItemSummaryFile";
//	final static String DECKING_STATEHDR = "DeckingStatus";
//
//	final static String ATTACHMENT_HDR = "Attachment";
//	final static String ATTACHMENT_FIELD = "attachment";
//
//	ArrayList<Integer> selctdItemNums;

	protected void setScreenTitle() {
		mainShell.setText("DeckerGrouper: <viewContentsAtDesk>");
	}

//	public void extendedCommonInit() {
//		selctdItemNums = new ArrayList<Integer>();
//		
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//		if (!deckerGrouperDocPojo.screenAddlFieldPattern.containsKey(ATTACHMENT_HDR)){
//			ScreenPatternFieldPojo attachmentFieldPattern = new ScreenPatternFieldPojo();
//			attachmentFieldPattern.screenFieldName = ATTACHMENT_HDR;
//			attachmentFieldPattern.interactionType = FILE_LINK_BUTTON;
//			deckerGrouperDocPojo.screenAddlFieldPattern.put(ATTACHMENT_FIELD, attachmentFieldPattern);
//		}
//	}

//	public ItemPojo getItemPojo(int inItemNumber){
//		System.out.println(" At getItemPojo itemNumber : " + inItemNumber);
//
//		DeckerGrouperItemPojo deckerGrperItem = new DeckerGrouperItemPojo(inItemNumber);
//
//		return deckerGrperItem;
//	}

//	public void setCenterAddlColHeaders() {
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//
//		centerAddlColHeaders = new String[deckerGrouperDocPojo.screenAddlFieldPattern.size()];
//		centerAddlColFileFields = new String[deckerGrouperDocPojo.screenAddlFieldPattern.size()];
//		centerAddlColInteractions = new String[deckerGrouperDocPojo.screenAddlFieldPattern.size()];
//		
//		Object[] patternFiledKeys = deckerGrouperDocPojo.screenAddlFieldPattern.keySet().toArray();
//		for (int patternFiledKeyNum = 0;patternFiledKeyNum < patternFiledKeys.length;patternFiledKeyNum++) {
//			String patternField = (String) patternFiledKeys[patternFiledKeyNum];
//			centerAddlColFileFields[patternFiledKeyNum] = patternField;
//			centerAddlColHeaders[patternFiledKeyNum] = deckerGrouperDocPojo.screenAddlFieldPattern.get(patternField).screenFieldName;
//			centerAddlColInteractions[patternFiledKeyNum] = deckerGrouperDocPojo.screenAddlFieldPattern.get(patternField).interactionType;
//
//			System.out.println("centerAddlColFileFields[" + patternFiledKeyNum + "] = " + centerAddlColFileFields[patternFiledKeyNum]);
//			System.out.println("centerAddlColHeaders[" + patternFiledKeyNum + "] = " + centerAddlColHeaders[patternFiledKeyNum]);
//			System.out.println("centerAddlColInteractions[" + patternFiledKeyNum + "] = " + centerAddlColInteractions[patternFiledKeyNum]);
//		}
//	}

//	protected void setAddlColumnHeaders(){
//
//		if (invokedForEdit) {
//			addlLeftColumnHeaders = new String[] {COL_SEL_HDR};
//		} else {
//			addlLeftColumnHeaders = new String[0];
//		}
//
//		centerBaseColHeaders = new String[] {
//												RELEVANCEHDR,
//												SUMMARYFILEHDR,
//												DECKING_STATEHDR
//											};
//
//		setCenterAddlColHeaders();
//		
//		coreLeftColumnHeaders = new String[] {"Artifact"};	// resetting the core column
//		coreRightColumnHeaders = new String[] {};			// resetting the core column
//
//	}

	protected void setCenterBaseColHeaders() {
		centerBaseColHeaders = new String[] {
												RELEVANCEHDR,
												SUMMARYFILEHDR,
												DECKING_STATEHDR
											};
	}
	
//	public void setDisplayItemscoreRightFieldsInMultiDisplay(TableEditor inEditor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo itemPojoScrolled, int screenRowNum) {
//		return; // suppressing the default displays on this core area
//	}


//	public void setDisplayCoreLeftFieldsInMultiDisplay(TableEditor inEditor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojoScrolled, Button inMaintenanceButton, int inScreenRowNum) {
//		System.out.println("setDisplayCoreLeftFieldsInMultiDisplay");
//		TableEditor maintenanceButtonEditor = new TableEditor(inTable);
//		inMaintenanceButton
//				.setText(inItemPojoScrolled.artifactName);
//		inMaintenanceButton.setToolTipText("click for maintenance of: " + inItemPojoScrolled.title);
//		inMaintenanceButton.setData(SCREENROWNUMLIT, inScreenRowNum);
//
//		System.out.println("itemPojo.itemID:"+inItemPojoScrolled.itemID);
//		System.out.println("set data = "
//				+ inMaintenanceButton.getData("screenRowNum"));
//
//		maintenanceButtonProcess(inMaintenanceButton);
//		inMaintenanceButton.pack();
//		maintenanceButtonEditor.minimumWidth = inMaintenanceButton.getSize().x;
//		maintenanceButtonEditor.horizontalAlignment = SWT.LEFT;
//		maintenanceButtonEditor.setEditor(inMaintenanceButton, inTableItem,++inLastColLocation);
//
//		System.out.println("maintenanceButton cellEditor.minimumWidth = " + maintenanceButtonEditor.minimumWidth);
//		System.out.println("maintenanceButton text = " + inMaintenanceButton.getText());
//		System.out.println("linkTextButton cellEditor.horizontalAlignment = " + maintenanceButtonEditor.horizontalAlignment);
//		System.out.println("maint button absoluteColumnPosition = " + inLastColLocation);
//	}
	
//	public void setDisplayItemsAddlLeftFieldsInMultiDisplay(TableEditor inEditor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo) {
//		System.out.println("setDisplayItemsAddlLeftFieldsInMultiDisplay");
//		
//		if (invokedForEdit) {
//			DeckerGrouperItemPojo extendedItemPojo = (DeckerGrouperItemPojo) inItemPojo;
//			int absoluteColumnPosition = -1;
//			for (int addlLeftColCount=0; addlLeftColCount < addlLeftColumnHeaders.length; addlLeftColCount++){
//				absoluteColumnPosition = (inLastColLocation+1) + addlLeftColCount;
//				inEditor = new TableEditor(inTable);
//				inEditor.grabHorizontal = true;
//	
//				Button itemSelectecCheckBox = new Button(inTable, SWT.CHECK);
//				itemSelectecCheckBox.setToolTipText("click to select/deselect artifact & selctdItemNums : " + extendedItemPojo.artifactName + " " + extendedItemPojo.relevance);
//				itemSelectecCheckBox.setData(ITEMNUMLIT, extendedItemPojo.itemNumber);
//				inEditor.setEditor(itemSelectecCheckBox, inTableItem,absoluteColumnPosition);
//				
//				itemSelectecCheckBox.addSelectionListener(new SelectionAdapter() {
//					
//					public void widgetSelected(SelectionEvent e) {
//						Button itemSelectecCheckBox = (Button) e.getSource();
//
//						boolean itemSelected = itemSelectecCheckBox.getSelection();
//						System.out.println("Extended itemSelectecCheckBox = " + itemSelected);
//						int itemNumber = (Integer) itemSelectecCheckBox.getData(ITEMNUMLIT);
//						System.out.println("itemNumber = " + itemNumber);
//
//						int itemIndex = selctdItemNums.indexOf(itemNumber);						
//						if (itemSelected) {
//							if (itemIndex==-1){	// this check is reduntant
//								selctdItemNums.add(itemNumber);
//							} else {
//								System.out.println("WARNING itemNumber already exists " + itemNumber);
//								System.out.println("WARNING at index " + itemIndex);
//							}
//						} else {	// item de-selected by user
//							if (itemIndex!=-1){
//								selctdItemNums.remove(itemIndex);
//							} else {
//								System.out.println("WARNING itemIndex never existed " + itemIndex);
//							}
//						}
//					}
//				});
//			}
//		}		
//	}
	
//	public void setDisplayItemsCenterBaseFieldsInMultiDisplay(TableEditor inEditor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo){
//		System.out.println("setDisplayItemsBaseFieldsInMultiDisplay:");
//		DeckerGrouperItemPojo extendedItemPojo = (DeckerGrouperItemPojo) inItemPojo;
//		int absoluteColumnPosition = -1;
//		for (int centerBaseColCount=0; centerBaseColCount < centerBaseColHeaders.length; centerBaseColCount++){
//			absoluteColumnPosition = (inLastColLocation+1) + centerBaseColCount;
//			System.out.println("absoluteColumnPosition = " + absoluteColumnPosition);
//			System.out.println("centerBaseColHeaders["+centerBaseColCount + "] = " + centerBaseColHeaders[centerBaseColCount]);
//			System.out.println("inItemPojo artifactName = " + inItemPojo.artifactName);
//			System.out.println("inItemPojo itemID = " + inItemPojo.itemID);
//
//			inEditor = new TableEditor(inTable);
//			inEditor.grabHorizontal = true;
//
//			//setting relevance starts
//			//setting relevance starts
//			inEditor = new TableEditor(inTable);
//			inEditor.grabHorizontal = true;
//			if (centerBaseColHeaders[centerBaseColCount].equalsIgnoreCase(RELEVANCEHDR)) {
//
//				Text relevance_Tx = new Text(inTable, SWT.READ_ONLY);
//				relevance_Tx.setText(extendedItemPojo.relevance);
//				inEditor.grabHorizontal = true;
//				inEditor.setEditor(relevance_Tx, inTableItem, absoluteColumnPosition);
//				
//			}
//			//setting relevance ends
//			//setting relevance ends
//
//			//setting summaryFileLink starts
//			//setting summaryFileLink starts
//			inEditor = new TableEditor(inTable);
//			inEditor.grabHorizontal = true;
//			if (centerBaseColHeaders[centerBaseColCount].equalsIgnoreCase(SUMMARYFILEHDR)) {
//				inEditor = new TableEditor(inTable);
//				Button linkTextButton = new Button(inTable, SWT.PUSH);
//				if (extendedItemPojo.itemSummaryFile == null || extendedItemPojo.itemSummaryFile.equalsIgnoreCase("")) {
//					System.out.println("disabling summary link button for " + inItemPojo.artifactName);
//					linkTextButton.setEnabled(false);
//				} else {
//					System.out.println("enabling summary link button for " + inItemPojo.artifactName);
//					linkTextButton.setText(extendedItemPojo.itemSummaryFile);
//					linkTextButton.setToolTipText("click to view item summary : " + extendedItemPojo.itemSummaryFile);
//					linkTextButton.setData(ITEM_SUMMARYLIT, extendedItemPojo.itemSummaryFile);
//					linkTextButton.addSelectionListener(new SelectionAdapter() {
//	
//						public void widgetSelected(SelectionEvent e) {
//							Button eventButton = (Button) e.getSource();
//							String itemSummaryFileNm = (String) eventButton.getData(ITEM_SUMMARYLIT);
//							File file = commonData.getCommons().getFileFromPathAndFile(contentPathFolderName,itemSummaryFileNm);
//							try {
//								commonData.getCommons().openFileToView(file.getPath());
//							} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//									| IOException e1) {
//								e1.printStackTrace();
//								ErrorHandler.showErrorAndQuit(commons, "Error in DeckerGrouper setDisplayItemsCenterBaseFieldsInMultiDisplay " + inItemPojo.artifactName, e1);
//							}
//						}
//					});
//				}
//				System.out.println("@1 linkTextButton linkTextButton.getSize().x = " + linkTextButton.getSize().x);
//				linkTextButton.pack();
//				System.out.println("@2 linkTextButton linkTextButton.getSize().x = " + linkTextButton.getSize().x);
//				System.out.println("linkTextButton cellEditor.minimumWidth = " + inEditor.minimumWidth);
//				inEditor.minimumWidth = linkTextButton.getSize().x;
//				inEditor.setEditor(linkTextButton, inTableItem, absoluteColumnPosition);
//				inEditor.horizontalAlignment = SWT.LEFT;
//				System.out.println("linkTextButton cellEditor.minimumWidth = " + inEditor.minimumWidth);
//				System.out.println("linkTextButton text = " + linkTextButton.getText());
//				System.out.println("linkTextButton cellEditor.horizontalAlignment = " + inEditor.horizontalAlignment);
//				System.out.println("absoluteColumnPosition = " + absoluteColumnPosition);
//			}
//			//setting summaryFileLink ends
//			//setting summaryFileLink ends
//
//			//setting deckingState starts
//			//setting deckingState starts
//			inEditor = new TableEditor(inTable);
//			inEditor.grabHorizontal = true;
//			if (centerBaseColHeaders[centerBaseColCount].equalsIgnoreCase(DECKING_STATEHDR)) {
//				Text freeFormText = new Text(inTable, SWT.READ_ONLY);
//				if (extendedItemPojo.deckingCompletedAt != null) {
//					ArtifactKeyPojo rowArtifactKeyPojo = new ArtifactKeyPojo(
//								invokedArtifactPojo.artifactKeyPojo.rootNick,
//								extendedItemPojo.relevance,
//								extendedItemPojo.artifactName,
//								extendedItemPojo.contentType);
//					ArtifactPrepper artifactPrepper = new ArtifactPrepper(rowArtifactKeyPojo,commonData);
//					if (artifactPrepper.errorEncountered) {
//						return;
//					}
//					try {
//						if (commons.isThisLeftDateLater(extendedItemPojo.deckingCompletedAt, 
//								commons.getDateFromString(artifactPrepper.erlDownload.uploadedTimeStamp))){
//							freeFormText.setText(DECKINGSTAT_DECKED);
//						} else {
//							freeFormText.setText(DECKINGSTAT_YETTOBEDECKED);	
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//						ErrorHandler.showErrorAndQuit(commons, 
//							"Error in DeckerGrouper setDisplayItemsCenterBaseFieldsInMultiDisplay date parsing on last update time of " 
//							+ rowArtifactKeyPojo.artifactName, e);
//					}
//
//				} else {
//					freeFormText.setText(DECKINGSTAT_YETTOBEDECKED);					
//				}
//				inEditor.setEditor(freeFormText, inTableItem, absoluteColumnPosition);
//				freeFormText.pack();
//				freeFormText.setEnabled(false);
//			}			
//			//setting deckingState ends
//			//setting deckingState ends
//		}
//	}
	
//	public void setDisplayItemsCenterAddlFieldsInMultiDisplay(TableEditor inEditor, Table inTable, TableItem inTableItem, int inLastColLocation, ItemPojo inItemPojo){
//		DeckerGrouperItemPojo extendedItemPojo = (DeckerGrouperItemPojo) inItemPojo;
//		int absoluteColumnPosition = -1;
//		System.out.println("setDisplayItemsAddlFieldsInMultiDisplay:");
//		for (int centerAddlColCount=0; centerAddlColCount < centerAddlColHeaders.length; centerAddlColCount++){
//			absoluteColumnPosition = (inLastColLocation+1) + centerAddlColCount;
//			System.out.println("centerAddlColCount = " + centerAddlColCount);
//			System.out.println("absoluteColumnPosition = " + absoluteColumnPosition);
//			System.out.println("coreLeftColumnHeaders.length = " + coreLeftColumnHeaders.length);
//			System.out.println("centerAddlColHeaders.length = " + centerBaseColHeaders.length);
//			System.out.println("absoluteColumnPosition = " + absoluteColumnPosition);
//			System.out.println("centerAddlColInteractions["+ centerAddlColCount + "] = " + centerAddlColInteractions[centerAddlColCount]);
//			System.out.println("centerAddlColHeaders[" + centerAddlColCount + "] = " + centerAddlColHeaders[centerAddlColCount]);
//			System.out.println("1value = " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//						
//			TableEditor cellEditor = new TableEditor(inTable);
//			cellEditor.grabHorizontal = true;
//			if (centerAddlColInteractions[centerAddlColCount].equalsIgnoreCase(DROPDOWN_FIELD)) {
//				CCombo dropDownList = new CCombo(inTable, SWT.DROP_DOWN | SWT.READ_ONLY);
//				dropDownList.setData(ITEMNUMLIT, extendedItemPojo.itemNumber);
//				{
//					System.out.println("dropDownList = " + dropDownList);
//					System.out.println("centerColumnCount = " + centerAddlColCount);
//					System.out.println("centerColumnHeaders[centerColumnCount] = " + centerAddlColHeaders[centerAddlColCount]);
//					System.out.println("extendedItemPojo = " + extendedItemPojo);
//
//					dropDownList.setItems(((DeckerGrouperDocPojo) primerDoc).screenAddlFieldPattern.get(centerAddlColFileFields[centerAddlColCount]).validValues);
//					if (extendedItemPojo!=null && !extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]).equalsIgnoreCase("")) {
//						dropDownList.select(dropDownList.indexOf(extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount])));
//					} else {
//						dropDownList.select(0);
//						extendedItemPojo.addlFieldValues.put(centerAddlColFileFields[centerAddlColCount], dropDownList.getItem((dropDownList.getSelectionIndex())));
//					}
//					
//					if (invokedForEdit) {
//						
//						dropDownList.setData(ITEMNUMLIT, extendedItemPojo.itemNumber);
//						dropDownList.setData(COLFIELDLIT, centerAddlColFileFields[centerAddlColCount]);
//	
//						dropDownList.addSelectionListener(new SelectionAdapter() {
//							
//							public void widgetSelected(SelectionEvent e) {
//								CCombo dropDownList = (CCombo) e.getSource();
//								System.out.println("Extended dropDown selection = " + dropDownList.getSelectionIndex());
//								System.out.println("dropDown selected Item = " + dropDownList.getItem((dropDownList.getSelectionIndex())));
//							
//								int itemNumber = (Integer) dropDownList.getData(ITEMNUMLIT);
//								String colField2 = (String) dropDownList.getData(COLFIELDLIT);
//	
//								System.out.println("itemNumber = " + itemNumber);
//								System.out.println("colField = " + colField2);
//								System.out.println("Before::: items colField = " + ((DeckerGrouperItemPojo) itemList.get(itemNumber)).addlFieldValues.get(colField2));
//								((DeckerGrouperItemPojo) itemList.get(itemNumber)).addlFieldValues.put(colField2, dropDownList.getItem((dropDownList.getSelectionIndex())));
//								System.out.println("after ::: items colField = " + ((DeckerGrouperItemPojo) itemList.get(itemNumber)).addlFieldValues.get(colField2));
//							}
//						});
//					} else {
//						dropDownList.setEnabled(false);
//					}
//				}
//				cellEditor.setEditor(dropDownList, inTableItem, absoluteColumnPosition);
//				dropDownList.pack();
//			} else if (centerAddlColInteractions[centerAddlColCount].equalsIgnoreCase(FILE_LINK_BUTTON)) {
//
//				System.out.println("centerAddlColCount = " + centerAddlColCount);
//				System.out.println("centerAddlColFileFields[centerAddlColCount] = " + centerAddlColFileFields[centerAddlColCount]);
//				System.out.println("centerAddlColFileFields[centerAddlColCount] = " + centerAddlColFileFields[centerAddlColCount]);
//				System.out.println("centerAddlColInteractions[centerAddlColCount] = " + centerAddlColInteractions[centerAddlColCount]);
//				System.out.println("extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]) = " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//				
//									
//				
//				System.out.println("2value = " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//
//				System.out.println("2value = " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//				System.out.println("2value = " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//				System.out.println("2value = " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//
//				cellEditor = new TableEditor(inTable);
//				Button linkTextButton = new Button(inTable, SWT.PUSH);
//				linkTextButton.setText(extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//				linkTextButton.setToolTipText("click to view item summary : " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//				
//				if (extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]) == null || extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]).equalsIgnoreCase("")) {
//					System.out.println("disabling link button for " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//					linkTextButton.setEnabled(false);
//				} else {
//					linkTextButton.setData(ITEM_SUMMARYLIT, extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//					linkTextButton.addSelectionListener(new SelectionAdapter() {
//	
//						public void widgetSelected(SelectionEvent e) {
//							Button eventButton = (Button) e.getSource();
//							String itemSummary = (String) eventButton.getData(ITEM_SUMMARYLIT);
//							File file = commonData.getCommons().getFileFromPathAndFile(contentPathFolderName,itemSummary);
//							try {
//								commonData.getCommons().openFileToView(file.getPath());
//							} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//									| IOException e1) {
//								e1.printStackTrace();
//								ErrorHandler.showErrorAndQuit(commons, "Error in DeckerGrouper setDisplayItemsCenterBaseFieldsInMultiDisplay " + inItemPojo.artifactName, e1);
//							}
//						}
//					});
//				}
//				System.out.println("@1 linkTextButton linkTextButton.getSize().x = " + linkTextButton.getSize().x);
//				linkTextButton.pack();
//				System.out.println("@2 linkTextButton linkTextButton.getSize().x = " + linkTextButton.getSize().x);
//				System.out.println("linkTextButton cellEditor.minimumWidth = " + cellEditor.minimumWidth);
//				cellEditor.minimumWidth = linkTextButton.getSize().x;
//				cellEditor.horizontalAlignment = SWT.LEFT;
//				cellEditor.setEditor(linkTextButton, inTableItem, absoluteColumnPosition);
//				System.out.println("linkTextButton cellEditor.minimumWidth = " + cellEditor.minimumWidth);
//				System.out.println("linkTextButton text = " + linkTextButton.getText());
//				System.out.println("linkTextButton cellEditor.horizontalAlignment = " + cellEditor.horizontalAlignment);
//				System.out.println("absoluteColumnPosition = " + absoluteColumnPosition);
//
//			} else if (centerAddlColInteractions[centerAddlColCount].equalsIgnoreCase(FREE_FORM_TEXT)
//					|| centerAddlColInteractions[centerAddlColCount].equalsIgnoreCase(PROTECTED_TEXT)) {
//				cellEditor = new TableEditor(inTable);
//				Text freeFormText = null;
//				if (invokedForEdit && centerAddlColInteractions[centerAddlColCount].equalsIgnoreCase(FREE_FORM_TEXT)) {
//					freeFormText = new Text(inTable, SWT.NONE);
//				} else {
//					freeFormText = new Text(inTable, SWT.READ_ONLY);
//				}
//				freeFormText.setText(extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//				cellEditor.setEditor(freeFormText, inTableItem, absoluteColumnPosition);
//				freeFormText.pack();
//
//				if (invokedForEdit) {
//					freeFormText.setData(ITEMNUMLIT, extendedItemPojo.itemNumber);
//					freeFormText.setData(COLFIELDLIT, centerAddlColFileFields[centerAddlColCount]);
//
//					System.out.println("inside freeform text centerAddlColCount = " + centerAddlColCount);
//					System.out.println("inside freeform text centerAddlColFileFields[centerAddlColCount] = " + centerAddlColFileFields[centerAddlColCount]);
//					System.out.println("inside freeform text extendedItemPojo.screenFieldValues.get(centerAddlColFileFields[centerAddlColCount]) = " + extendedItemPojo.addlFieldValues.get(centerAddlColFileFields[centerAddlColCount]));
//					System.out.println("inside freeform text freeFormText.getText = " + freeFormText.getText());
//
//					freeFormText.addSelectionListener(new SelectionAdapter() {
//						
//						public void widgetSelected(SelectionEvent e) {
//							Text freeFormText = (Text) e.getSource();
//							System.out.println("Extended freeFormText = " + freeFormText.getText());
//						
//							int itemNumber = (Integer) freeFormText.getData(ITEMNUMLIT);
//							String colField = (String) freeFormText.getData(COLFIELDLIT);
//	
//							System.out.println("itemNumber = " + itemNumber);
//							System.out.println("colField = " + colField);
//							System.out.println("Before::: items colField = " + ((DeckerGrouperItemPojo) itemList.get(itemNumber)).addlFieldValues.get(colField));
//							((DeckerGrouperItemPojo) itemList.get(itemNumber)).addlFieldValues.put(colField, freeFormText.getText());
//							System.out.println("after ::: items colField = " + ((DeckerGrouperItemPojo) itemList.get(itemNumber)).addlFieldValues.get(colField));
//						}
//					});
//				} else {
//					freeFormText.setEnabled(false);
//				}
//				System.out.println("while moving on from freeform text freeFormText.getText = " + freeFormText.getText());
//				freeFormText.pack();
//				
//				freeFormText.pack();
//				System.out.println("@2 linkTextButton linkTextButton.getSize().x = " + freeFormText.getSize().x);
//				System.out.println("linkTextButton cellEditor.minimumWidth = " + cellEditor.minimumWidth);
//				cellEditor.minimumWidth = freeFormText.getSize().x;
//				cellEditor.horizontalAlignment = SWT.LEFT;
//				cellEditor.setEditor(freeFormText, inTableItem, absoluteColumnPosition);
//			}
//		}
//	}

//	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGrp,FormData formData, ItemPojo itemPojo){
//	
//		Group lastGroup = inPrevGrp;
//		
//		DeckerGrouperItemPojo extendedItemPojo = (DeckerGrouperItemPojo) itemPojo;
//	
//		return lastGroup;
//	}

	//public void setInitialItemPojoAddlFields(ItemPojo inItemPojo){
	//	DeckerGrouperItemPojo extendedItemPojo = (DeckerGrouperItemPojo) inItemPojo;
	//	extendedItemPojo.author = commonData.getCommons().userName;
	//	extendedItemPojo.status = "Draft";
	//}

//	public void getPrimerDocAddlFields() {
//		// from persistence to screen
//	}
//	public void setPrimerDocAddlFields() {
//		// from screen to persistence		
//	}
	
	
//	public void processContentAtWeb(RootPojo inRootPojo, RemoteAccesser inRemoteAccesser, RequestProcesserPojo inRequestProcesserPojo) {
//		//This method invoked on the serverside processes the uploaded document
//		System.out.println("begin processContentAtWeb for DeckerGrouper");
//		inRequestProcesserPojo.updatedContentFileLocation = inRequestProcesserPojo.incomingContentFullPath;
//		inRequestProcesserPojo.updatedContentInputStream = inRemoteAccesser.getRemoteFileStream(inRequestProcesserPojo.updatedContentFileLocation);
//		System.out.println("end processContentAtWeb");
//	}
	
//	public String validateBeforeUIEdit() {
//		String validationBeforeEdit = "";
//		return validationBeforeEdit;
//	}

	public Class getPrimerDocClass() {
		return DeckerGrouperDocPojo.class;
	}

//	public Class getBasePrimerDocClass() {
//		return null;		
//	}

	//public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo) {
	//	DeckerGrouperItemPojo extendedItemPojo = (DeckerGrouperItemPojo) inItemPojo;
	//}

//	public void additionalRibbonButtons(Composite inRibbon) {
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//
//		if (deckerGrouperDocPojo.combinedFileName != null && !deckerGrouperDocPojo.combinedFileName.equalsIgnoreCase("")) {
//			Button viewSummaryBtn = new Button(inRibbon, SWT.NONE);
//			viewSummaryBtn.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					System.out.println("Summary view ");
//					String targetOverallSummaryPath = commonData.getCommons().getAbsolutePathFromDirAndFileNm(contentPathFolderName,deckerGrouperDocPojo.combinedFileName);
//
//					try {
//						commonData.getCommons().openFileToView(targetOverallSummaryPath);
//					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e1) {
//						e1.printStackTrace();
//						ErrorHandler.showErrorAndQuit(commons, "Error in DeckerGrouper additionalRibbonButtons", e1);
//					}
//				}
//			});
//			viewSummaryBtn.setText("Summary");
//			viewSummaryBtn.pack();
//		}
//	}
//	
//	public void showAdditionalRibbonEditControls(Composite inRibbon){
//		//////////////////////CloneFromContentType starts
//
//		System.out.println("at child content type display0");
//		
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//		
//		System.out.println("at child content type display1");
//		if (invokedForEdit) {
//			{
//				System.out.println("at child content type display2");
//				Group childContentTypeGroup = new Group(inRibbon, SWT.NONE);
//				childContentTypeGroup.setLayout(new FillLayout());
//				childContentTypeGroup.setText("Child_ContentType");
//		
//				childContentTypeList = new CCombo(childContentTypeGroup,
//						SWT.DROP_DOWN | SWT.READ_ONLY);
//
//				childContentTypeList.setItems(commonData.getContentTypes());
//				childContentTypeList.setEnabled(true);
//				childContentTypeList.pack();
//				childContentTypeGroup.pack();
//	
//				if (deckerGrouperDocPojo.getItemList()==null || deckerGrouperDocPojo.getItemList().size()==0) {
//					childContentTypeList.clearSelection();
//				} else {
//					childContentTypeList.select(childContentTypeList.indexOf(deckerGrouperDocPojo.getItemList().get(0).contentType));
//				}
//				childContentTypeList.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						System.out.println("childContentTypeList.getSelectionIndex():" + childContentTypeList.getSelectionIndex());
//						System.out.println("childContentTypeList.getItem(childContentTypeList.getSelectionIndex()):" + childContentTypeList.getItem(childContentTypeList.getSelectionIndex()));
//
//						int contentTypeSelIndex = childContentTypeList.getSelectionIndex();
//						if (contentTypeSelIndex == -1) {
//							return;	// nothing got selected; (if any UI allows de-selection)
//						}
//						String selectedContentType = childContentTypeList.getItem(childContentTypeList.getSelectionIndex());
//						
//						if (deckerGrouperDocPojo.getItemList().size() > 0
//							&& selectedContentType.equalsIgnoreCase(deckerGrouperDocPojo.getItemList().get(0).contentType)) {
//							return; // selection is same as current items' type
//						}
//
//						ContentHandlerInterface contentHandlerObjectInterface = ContentHandlerManager.getInstance(commons,catelogPersistenceManager,selectedContentType);
//						System.out.println("contentHandlerObjectInterface : " + contentHandlerObjectInterface);
//						if (contentHandlerObjectInterface instanceof DeckableContentTypeInterface) {
//							DeckableContentTypeInterface deckableContentTypeInterface = (DeckableContentTypeInterface) contentHandlerObjectInterface;
//							deckableContentTypeInterface.testOk("Am I being printed");
//						} else {
//							System.out.println("this is not going tobe being printed");
//							return; // not a deckable type
//						}
//
//						selctdItemNums.clear();
//						primerDoc.clearList();	// when child content type is changed, all existing items becomes invalid
//						
//						//populate relevance drop down with available unincluded artifacts of ContentType
//						//and set the selection to none; also, clear the artifact drop down and disable
//
//						refreshRelevanceList();
//						
//						redrawDetails();		// redraw screen below ribbon
//					}
//				});
//			}
//			{
//				System.out.println("at relevance display");
//				Group relevanceSelectionGroup = new Group(inRibbon, SWT.NONE);
//				relevanceSelectionGroup.setLayout(new FillLayout());
//				relevanceSelectionGroup.setText("RelevanceSelection");
//		
//				relevanceList = new CCombo(relevanceSelectionGroup,
//						SWT.DROP_DOWN | SWT.READ_ONLY);
///////////
///////////
//				if (deckerGrouperDocPojo.getItemList()==null || deckerGrouperDocPojo.getItemList().size()==0) {
//					relevanceList.setEnabled(false);
//				} else {
//					String[] unIncludedERLRelevances = getUnIncludedERLRelevances();
//					relevanceList.setItems(unIncludedERLRelevances);				
//					relevanceList.setEnabled(true);
//					relevanceList.clearSelection();
//					System.out.println("going via valid content types. count of uncludedrelevenance is " + unIncludedERLRelevances.length);
//				}
///////////
///////////
//				relevanceList.pack();
//				relevanceSelectionGroup.pack();
//
//				relevanceList.addSelectionListener(new SelectionAdapter() {
//					
//					public void widgetSelected(SelectionEvent e) {
//						System.out.println("relevanceList.getSelectionIndex():" + relevanceList.getSelectionIndex());
//						System.out.println("relevanceList.getItem(childContentTypeList.getSelectionIndex()):" + relevanceList.getItem(relevanceList.getSelectionIndex()));
//
//						ArrayList<ERLDownload> unIncludedERLs = getUnIncludedERLsOfDbWithChildContentTypeRelevance();
//						ArrayList<String> artifactList = new ArrayList<String>();
//						
//						for (ERLDownload unIncludedERL : unIncludedERLs) {
//							artifactList.add(unIncludedERL.artifactKeyPojo.artifactName);
//						}
//						String[] artifactNames = new String[artifactList.size()];
//						artifactList.toArray(artifactNames);
//
//						artifactNameList.setItems(artifactNames);
//						artifactNameList.clearSelection();
//						artifactNameList.setEnabled(true);
//					}
//				});
//			}
//			
//			{
//				System.out.println("at artifactNameList display");
//				Group artifactNameSelectionGroup = new Group(inRibbon, SWT.NONE);
//				artifactNameSelectionGroup.setLayout(new FillLayout());
//				artifactNameSelectionGroup.setText("ArtifactNameSelection");
//		
//				artifactNameList = new CCombo(artifactNameSelectionGroup,
//						SWT.DROP_DOWN | SWT.READ_ONLY);
//				artifactNameList.setEnabled(false);
//				artifactNameList.pack();
//				artifactNameSelectionGroup.pack();
//
//				artifactNameList.addSelectionListener(new SelectionAdapter() {
//					
//					public void widgetSelected(SelectionEvent e) {
//						System.out.println("artifactNameList.getSelectionIndex():" + artifactNameList.getSelectionIndex());
//						System.out.println("artifactNameList.getItem(artifactNameList.getSelectionIndex()):" + artifactNameList.getItem(artifactNameList.getSelectionIndex()));
//						// no action required for artifactName selection; action will start only upon add all button
//					}
//				});
//			}
//			{
//				System.out.println("DeckerGrouper Add all under criterian button");
//				Button btnAddAllArtifacts = new Button(inRibbon, SWT.NONE);
//				btnAddAllArtifacts.pack();
//	
//				btnAddAllArtifacts.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						System.out.println("add artifacts under criterian");
//						ArrayList<ERLDownload> unIncludedERLs = null;
//						if (artifactNameList.isEnabled() && artifactNameList.getSelectionIndex() > -1) {
//							// get dbERLS based on selections contentTp/relevance/artifactname
//							unIncludedERLs = getUnIncludedERLsOfDbWithChildContentTypeRelevanceArtifactName();
//						} else if (relevanceList.isEnabled() && relevanceList.getSelectionIndex() > -1) {
//							// get dbERLS based on selections contentTp/relevance
//							unIncludedERLs = getUnIncludedERLsOfDbWithChildContentTypeRelevance();
//						} else if (childContentTypeList.isEnabled() && childContentTypeList.getSelectionIndex() > -1) {
//							// get dbERLS based on selections contentTp							
//							unIncludedERLs = getUnIncludedERLsOfDbWithChildContentType();
//						} else {
//							ErrorHandler.displayError(mainShell, commons, "Selection error");
//
//							return;
//						}
//						System.out.println("loadERLsIntoItemList(unIncludedERLs)" + unIncludedERLs.size());;
//						loadERLsIntoItemList(unIncludedERLs);
//						redrawDetails();		// redraw screen below ribbon
//					}
//				});
//				btnAddAllArtifacts.setBounds(10, 10, 120, 25);
//				btnAddAllArtifacts.setText("Add All Artifacts");
//			}
//			{
//				System.out.println("DeckerGrouper del all button");
//				Button btnDelAllArtifacts = new Button(inRibbon, SWT.NONE);
//				btnDelAllArtifacts.pack();
//	
//				btnDelAllArtifacts.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						System.out.println("del all artifacts for contentType");
//						primerDoc.clearList();
//						selctdItemNums.clear();
//						refreshRelevanceList();						
//						redrawDetails();		// redraw screen below ribbon
//					}
//				});
//				btnDelAllArtifacts.setBounds(10, 10, 120, 25);
//				btnDelAllArtifacts.setText("Del All Artifacts");				
//			}
//			{
//				System.out.println("DeckerGrouper del selected button");
//				Button btnDelSelected = new Button(inRibbon, SWT.NONE);
//				btnDelSelected.pack();
//	
//				btnDelSelected.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						System.out.println("del selected artifacts");
//						
//						for (int selectedItemNum : selctdItemNums) {
//							primerDoc.removeItemByItemNumber(selectedItemNum);
//						}
//						selctdItemNums.clear();
//						refreshRelevanceList();
//						redrawDetails();		// redraw screen below ribbon
//					}
//				});
//				btnDelSelected.setBounds(10, 10, 120, 25);
//				btnDelSelected.setText("Del Selected");				
//			}			
//		}
//		//////////////////////CloneFromContentType ends
//		
//	}
//
//	public void refreshRelevanceList() {
//		String[] unIncludedERLRelevances = getUnIncludedERLRelevances();
//		relevanceList.setItems(unIncludedERLRelevances);
//		relevanceList.clearSelection();
//		relevanceList.setEnabled(true);
//		
//		artifactNameList.clearSelection();						
//		artifactNameList.setEnabled(false);
//		artifactNameList.removeAll();
//	}
//
//	public String[] getUnIncludedERLRelevances() {
//		ArrayList<ERLDownload> unIncludedERLsOfContentType = getUnIncludedERLsOfDbWithChildContentType();
//		System.out.println("going via valid content types. count of all ERLRelevances is " + unIncludedERLsOfContentType.size());
//		
//		ArrayList<String> unIncludedRelevanceList = new ArrayList<String>();
//		for (ERLDownload unIncludedERL : unIncludedERLsOfContentType) {
//			if (unIncludedRelevanceList.indexOf(unIncludedERL.artifactKeyPojo.relevance) == -1) {
//				unIncludedRelevanceList.add(unIncludedERL.artifactKeyPojo.relevance);
//			}
//		}
//		String[] unIncludedRelevanceArray = new String[unIncludedRelevanceList.size()];
//		unIncludedRelevanceList.toArray(unIncludedRelevanceArray);
//
//		return unIncludedRelevanceArray;
//	}		
//
//	public ArrayList<ERLDownload> getUnIncludedERLsOfDbWithChildContentType() {
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//		ArrayList<ERLDownload> dbERLsOfContentType = catelogPersistenceManager.readERLDownLoadsOfRootsSpecificContentType(
//														childContentTypeList.getItem(childContentTypeList.getSelectionIndex()));
//		return dbERLsOfContentType = getUnIncludedERLsOfGivenList(dbERLsOfContentType);
//	}
//
//	public ArrayList<ERLDownload> getUnIncludedERLsOfDbWithChildContentTypeRelevance() {
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//		ArrayList<ERLDownload> dbERLsOfContentTypeRelevance = catelogPersistenceManager.readERLDownLoadsOfRootsSpecificContentTypeAndRelevance(
//																childContentTypeList.getItem(childContentTypeList.getSelectionIndex()), 
//																relevanceList.getItem(relevanceList.getSelectionIndex()));		
//		return dbERLsOfContentTypeRelevance = getUnIncludedERLsOfGivenList(dbERLsOfContentTypeRelevance);
//	}
//
//	public ArrayList<ERLDownload> getUnIncludedERLsOfDbWithChildContentTypeRelevanceArtifactName() {
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//		ArrayList<ERLDownload> dbERLsOfContentTypeRelevanceArtifactName = catelogPersistenceManager.readERLDownLoadsOfRootsSpecificContentTypeRelevanceArtifactName
//																	(childContentTypeList.getItem(childContentTypeList.getSelectionIndex()), 
//																	relevanceList.getItem(relevanceList.getSelectionIndex()),
//																	artifactNameList.getItem(artifactNameList.getSelectionIndex()));
//		return dbERLsOfContentTypeRelevanceArtifactName = getUnIncludedERLsOfGivenList(dbERLsOfContentTypeRelevanceArtifactName);
//	}
//
//	public ArrayList<ERLDownload> getUnIncludedERLsOfGivenList(ArrayList<ERLDownload> inGivenERLs) {
//		ArrayList<ERLDownload> unIncludedERLs = new ArrayList<ERLDownload>();
//		for (ERLDownload erlDownload : inGivenERLs) {	// Though this looping is inefficient, its likely to be called only once
//			ItemPojo alreadyExistingItem = getERLItemByChildArtifactName(erlDownload.artifactKeyPojo.relevance,erlDownload.artifactKeyPojo.artifactName); 
//			if (alreadyExistingItem == null) {
//				unIncludedERLs.add(erlDownload);
//			}
//		}
//		return unIncludedERLs;
//	}
//
//	public void loadERLsIntoItemList(ArrayList<ERLDownload> inERLsToBeLoaded) {
//		for (ERLDownload erlDownload : inERLsToBeLoaded) {
//			loadERLIntoItemsList(erlDownload);
//		}
//		selctdItemNums.clear();		
//	}
//
//	public void loadERLIntoItemsList(ERLDownload inERLDownload) {
//		DeckerGrouperDocPojo deckerGrouperDocPojo = (DeckerGrouperDocPojo) primerDoc;
//		DeckerGrouperItemPojo deckerGrouperItemPojo = new DeckerGrouperItemPojo(inERLDownload.artifactKeyPojo.contentType, inERLDownload.artifactKeyPojo.relevance, inERLDownload.artifactKeyPojo.artifactName);	
//		deckerGrouperItemPojo.status = inERLDownload.erlStatus;
//		deckerGrouperItemPojo.author = inERLDownload.author;		
//		deckerGrouperItemPojo.absorbScreenFieldValues(deckerGrouperDocPojo.screenFieldDefaults);
//		deckerGrouperDocPojo.absorbIncomingItemPojo(deckerGrouperItemPojo);
//	}

//	public GenericItemDocPojo getBaseDoc(ItemPojo inItemPojo) {
//		// this override required only for rollup types and DeckerGrouper is not one.
//		// calling this would be an error as Decker's child content type can be of any!!
//		return null;
//	}

	public ItemPojo createItemPojo(String inContentType, String inRelevance, String inArtifactName){
		return new DeckerGrouperItemPojo(inContentType, inRelevance, inArtifactName);
	}	
	
	public GenericGrouperDocPojo getNewPrimerDoc() {
		GenericGrouperDocPojo deckerGrouperDocPojo = new DeckerGrouperDocPojo();
		return deckerGrouperDocPojo;
	}

	public DeckerGrouperDocPojo getPrimerDoc() {		
		return (DeckerGrouperDocPojo) primerDoc;
	}

//	public String getDefaultSourceFileName(ItemPojo inItemPojo) {
//		return null;
//	}	
}