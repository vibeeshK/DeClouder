package xtdCommonTechs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.model.CalculationChain;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;

import commonTechs.CommonTechs;
import commonTechs.StartAndSize;

public class ExcelHandler {
	/*
	 * This class helps to read-and-write data between excel sheets and java objects
	 * One can read specific columns by setting "interest" on the column header and conveying the col type
	 * One can also refresh rows from one sheet to another based on key columns
	 */
	
	// Thanks to
	// http://www.dev2qa.com/copy-rows-between-excel-sheet-use-apache-poi
	// https://stackoverflow.com/questions/10773961/apache-poi-apply-one-style-to-different-workbooks

	
	final static String DEFAULTDATEFORMAT = "dd-MMM-yy";
	//int loopMax = 1000000;
	final static int MAXROWS = 1000000;

	String srcExcelFilePath = null;
	String targetExcelFilePath = null;
	Workbook srcExcelWB = null;
	Workbook targetExcelWB = null;
	CommonTechs commonTechs = null;
	boolean isDate1904 = false;
	XSSFFormulaEvaluator srcWbFomulaEvaluator;

	Drawing drawingPatriarch = null;
	CreationHelper factoryXSSFCreationHelper = null;
	public int recsBroughtInto = 0;

	public static void main(String[] args) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String sourceExcelFilePath = "D:\\Kannan\\Java\\ESPoT\\testOutput\\deckerID2_20180226225351.xlsx";
		String targetExcelFilePath = "D:\\Kannan\\Java\\ESPoT\\testOutput\\ArtifactSummary.xlsx";
		String copySheetName = "ItemDetails";
		int keyColumn = 0;
		String keyValue = "deckerID2";
		CommonTechs commonTechs = new CommonTechs();
		ExcelHandler esh = new ExcelHandler(commonTechs, sourceExcelFilePath,
				targetExcelFilePath);
		esh.copyRows(copySheetName, keyColumn, keyValue);
	}

	public ExcelHandler(CommonTechs inCommonTechs, String inSrcExcelFilePath,
			String inTargetExcelFilePath) throws IOException {
		commonTechs = inCommonTechs;
		/* Open the file input stream. */
		FileInputStream srcFileStream = null;
		FileInputStream targetFileStream = null;
		srcExcelFilePath = inSrcExcelFilePath;
		targetExcelFilePath = inTargetExcelFilePath;

		System.out.println("ExcelHandler srcExcelFilePath is " + srcExcelFilePath);
		System.out.println("ExcelHandler targetExcelFilePath is " + targetExcelFilePath);

		srcFileStream = new FileInputStream(inSrcExcelFilePath);
		srcExcelWB = new XSSFWorkbook(srcFileStream);
		srcFileStream.close();

		//for converting date into double and vice verse
		//https://stackoverflow.com/questions/16086504/how-to-check-if-date-uses-1904-windowing-with-apache-poi-xssf-eventmodel
		
		isDate1904 = false;
		CTWorkbook internalWorkbook = ((XSSFWorkbook) srcExcelWB).getCTWorkbook();
		CTWorkbookPr workbookPr = internalWorkbook.getWorkbookPr();
		if (workbookPr != null)
		{
		    isDate1904 = workbookPr.getDate1904();
		}
		
		System.out.println("isDate1904 is " + isDate1904);

		srcWbFomulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) srcExcelWB);
	}
	
	public String getFirstSheetNameOfSourceWB() {
		Sheet sheet = srcExcelWB.getSheetAt(0);
		return sheet.getSheetName();
	}
	
	public Sheet getTargetSheet(String inSheetName) {
		FileInputStream targetFileStream = null;
		Sheet targetSheet = null;
		try {
			if (commonTechs.doesFileExist(targetExcelFilePath)) {
				targetFileStream = new FileInputStream(targetExcelFilePath);
				targetExcelWB = new XSSFWorkbook(targetFileStream);
				targetFileStream.close();
				targetSheet = targetExcelWB.getSheet(inSheetName);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetSheet;
	}

	public boolean findHeadersMatch(){
		return findHeadersMatch(null);
	}

	public boolean findHeadersMatch(String inSheetName){
		boolean headersMatch = true;
		String sheetName = null;
		if (inSheetName == null) {
			sheetName = getFirstSheetNameOfSourceWB();
		} else {
			sheetName = inSheetName;
		}
		Sheet targetSheet = getTargetSheet(sheetName);
		Sheet srcSheet = srcExcelWB.getSheet(sheetName);
		Row srcHeaderRow = null;
		Row targetHeaderRow = null;

		if (srcSheet == null) {
			headersMatch = false;
		} else if (targetSheet == null) {
			headersMatch = true;	// consider match no target sheet created yet
		} else {
			srcHeaderRow = srcSheet.getRow(0);
			targetHeaderRow = targetSheet.getRow(0);
			if (srcHeaderRow == null || targetHeaderRow == null) {
				headersMatch = false;	// consider nonmatch when col counts dont match
			} else if (srcHeaderRow.getFirstCellNum() != targetHeaderRow.getFirstCellNum() ||
				srcHeaderRow.getLastCellNum() != targetHeaderRow.getLastCellNum()) {
				headersMatch = false;	// consider nonmatch when col counts dont match
			} else {
				for(int hdrColCnt=srcHeaderRow.getFirstCellNum(); hdrColCnt<srcHeaderRow.getLastCellNum(); hdrColCnt++) {
			        System.out.println("loop start hdrColCnt = " + hdrColCnt);
					Cell hdrColCell = srcHeaderRow.getCell(hdrColCnt);
					String srcHdrColValue = null;
					if (hdrColCell != null) {
						if (hdrColCell.getCellTypeEnum() == CellType.STRING) {
							srcHdrColValue = hdrColCell.getStringCellValue();
							
							Cell targetHdrColCell = targetHeaderRow.getCell(hdrColCnt);
							if (targetHdrColCell != null) {
								if (targetHdrColCell.getCellTypeEnum() == CellType.STRING) {
									String targetHdrColValue = targetHdrColCell.getStringCellValue();
									if (!targetHdrColValue.equals(srcHdrColValue)) {
										headersMatch = false;
										break;									
									}
									
								} else {
									headersMatch = false;
									break;
								}
							} else {
								headersMatch = false;
								break;							
							}							
						} else {
							headersMatch = false;	// consider nonmatch when a header col is nonString
							break;
						}
					} else {
						Cell targetHdrColCel2 = targetHeaderRow.getCell(hdrColCnt);
						if (targetHdrColCel2 != null) {
							headersMatch = false;	// consider nonmatch when a header col is nonString
							break;							
						}						
					}
				}
			}
		}

		return headersMatch;
	}
	
	public int getColSeqNum(String inColHeader) {
		int colSeqNum = -1;
		return colSeqNum;
	}

	public boolean getSrcXlObjForKeysAndConstraint(String inKeyHdr, String inConstraintHdr){
		boolean processOK = true;

		
		return processOK;
	}

	public ExcelJavaObj getSrcXlObjForTwoColumns(String inCol1Hdr, String inCol2Hdr){
		ExcelJavaObj srcFileXlObj = new ExcelJavaObj();

		srcFileXlObj.addColOfInterest(inCol1Hdr, ExcelColJavaObj.COLTYPE_STRING);
		if (inCol2Hdr != null) {
			srcFileXlObj.addColOfInterest(inCol2Hdr, ExcelColJavaObj.COLTYPE_STRING);
		}
		srcFileXlObj.initKeyColumn(inCol1Hdr);

		buildXlJavaObj(srcFileXlObj, getFirstSheetNameOfSourceWB());

		if (srcFileXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = srcFileXlObj.processStateMsg 
									+ " At getSrcXlObjForKeysAndConstraint of ExcelHanlder while building srcFileXlObj with Col1Hdr = "
									+ inCol1Hdr
									+ "; Col2Hdr = "
									+ inCol2Hdr;
			System.out.println(message);
			if (srcFileXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				commonTechs.logger.warn(message);
			} else {
				commonTechs.logger.error(message);
			}				
		}
		return srcFileXlObj;
	}

	public ExcelJavaObj getTargetXlObjForTwoColumns(String inCol1Hdr, String inCol2Hdr) throws IOException{
		ExcelJavaObj targetFileXlObj = new ExcelJavaObj();

		ExcelHandler tempExcelHandlerForTargetAsSrc = new ExcelHandler(commonTechs, targetExcelFilePath, null);

		targetFileXlObj.addColOfInterest(inCol1Hdr, ExcelColJavaObj.COLTYPE_STRING);
		targetFileXlObj.addColOfInterest(inCol2Hdr, ExcelColJavaObj.COLTYPE_STRING);
		targetFileXlObj.initKeyColumn(inCol1Hdr);

		System.out.println("At ExcelHanlder getTargetXlObjForTwoColumns. inCol1Hdr = " + inCol1Hdr + " and inCol2Hdr = " + inCol2Hdr);

		tempExcelHandlerForTargetAsSrc.buildXlJavaObj(targetFileXlObj, tempExcelHandlerForTargetAsSrc.getFirstSheetNameOfSourceWB());

		if (targetFileXlObj.processStatus != ExcelJavaObj.PROCSTAT_GOOD) {
			String message = targetFileXlObj.processStateMsg 
									+ " At getTargetXlObjForTwoColumns of ExcelHanlder while building targetFileXlObj with Col1Hdr = "
									+ inCol1Hdr 
									+ "; Col2Hdr = " 
									+ inCol2Hdr ;
			
			System.out.println(message);
			if (targetFileXlObj.processStatus == ExcelJavaObj.PROCSTAT_WARNING) {
				commonTechs.logger.warn(message);
			} else {
				commonTechs.logger.error(message);
			}				
		}
		return targetFileXlObj;
	}
	public void chkInitTargetSheet() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		chkInitTargetSheet(null);
	}

	
	public void chkInitTargetSheet(String inExcelSheetName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		// Two step process done. 1) check and create the file iwth sheet; 2) In case the reqd sheet only is missing then create sheet alone

		if (inExcelSheetName == null) {
			inExcelSheetName = getFirstSheetNameOfSourceWB();
		}
		
		if (!commonTechs.doesFileExist(targetExcelFilePath)) {			
			System.out.println("at combineShRows X1.1 ");
			cloneSheetsToNewWB(new String[] { inExcelSheetName });
			System.out.println("at combineShRows X1.2 ");
			removeNonHeaders(new String[] { inExcelSheetName });
			System.out.println("at combineShRows X1.3 ");
		}			
		System.out.println("at combineShRows X1.3.1 targetSheet name is " + inExcelSheetName);

		FileInputStream targetFileStream = new FileInputStream(
				targetExcelFilePath);
		targetExcelWB = new XSSFWorkbook(targetFileStream); // read for
															// existing
															// content
		targetFileStream.close();
		Sheet targetSheet = targetExcelWB.getSheet(inExcelSheetName);
		System.out.println("at combineShRows X1.4 targetSheet is " + targetSheet);

		if (targetSheet == null) {
			System.out.println("at combineShRows X1.5 targetSheet is " + targetSheet);
			cloneSheetsToNewWB(new String[] { inExcelSheetName });
			removeNonHeaders(new String[] { inExcelSheetName });
			targetSheet = targetExcelWB.getSheet(inExcelSheetName);
			System.out.println("at combineShRows X1.6 targetSheet is " + inExcelSheetName);
			System.out.println("at combineShRows X1.7 targetSheet is " + targetSheet);
		}
		
		{
			System.out.println("at combineShRows X1.4.1.1 targetSheet.getLastRowNum() is " + targetSheet.getLastRowNum());
			System.out.println("At chkInitTargetSheet inExcelSheetName " + inExcelSheetName);
			System.out.println("Checking the non empty rows of target sheet");

			for (int rowCount = 0; rowCount <= targetSheet.getLastRowNum(); rowCount++) {
				System.out.println("At chkInitTargetSheet rowCount = " + rowCount);
				Row row = targetSheet.getRow(rowCount);
				System.out.println("At chkInitTargetSheet row = " + row);
			}			
		}
		
	}
	
	public void combineShRowsByKeysAndConstraints(ExcelJavaObj inSrcXlObj, ExcelJavaObj inTargetXlObj, String inKeyHdr, 
													String inConstraintHdr, String inConstraintVal) 
													throws IllegalAccessException, IllegalArgumentException, 
													InvocationTargetException, NoSuchMethodException, 
													SecurityException, IOException {
			//copyRows(String inExcelSheetName, int inKeyColumn,
			//		String inKeyValue);
			
			String inExcelSheetName = getFirstSheetNameOfSourceWB();
			Sheet sourceSheet = srcExcelWB.getSheet(inExcelSheetName);

			if (!commonTechs.doesFileExist(targetExcelFilePath)) {			
				System.out.println("at combineShRows X1.1 ");
				cloneSheetsToNewWB(new String[] { inExcelSheetName });
				System.out.println("at combineShRows X1.2 ");
				removeNonHeaders(new String[] { inExcelSheetName });
				System.out.println("at combineShRows X1.3 ");
			}			
			System.out.println("at combineShRows X1.3.1 targetSheet name is " + inExcelSheetName);

			FileInputStream targetFileStream = new FileInputStream(
					targetExcelFilePath);
			targetExcelWB = new XSSFWorkbook(targetFileStream); // read for
																// existing
																// content
			targetFileStream.close();
			Sheet targetSheet = targetExcelWB.getSheet(inExcelSheetName);
			System.out.println("at combineShRows X1.4 targetSheet is " + targetSheet);

			if (targetSheet == null) {
				System.out.println("at combineShRows X1.5 targetSheet is " + targetSheet);
				cloneSheetsToNewWB(new String[] { inExcelSheetName });
				removeNonHeaders(new String[] { inExcelSheetName });
				targetSheet = targetExcelWB.getSheet(inExcelSheetName);
				System.out.println("at combineShRows X1.6 targetSheet is " + inExcelSheetName);
				System.out.println("at combineShRows X1.7 targetSheet is " + targetSheet);
			} else {
				{
					System.out.println("at combineShRows X1.4.1 targetSheet.getLastRowNum() is " + targetSheet.getLastRowNum());
					System.out.println("At combineShRowsByKeysAndConstraints inExcelSheetName " + inExcelSheetName);
					System.out.println("Checking the non empty rows of target sheet");

					for (int rowCount = 0; rowCount <= targetSheet.getLastRowNum(); rowCount++) {
						System.out.println("At combineShRowsByKeysAndConstraints rowCount = " + rowCount);
						Row row = targetSheet.getRow(rowCount);
						System.out.println("At combineShRowsByKeysAndConstraints row = " + row);
					}			
				}
			}

			System.out
			.println("targetSheet at combineShRows is " + targetSheet);
			System.out
			.println("targetSheet name at combineShRows is " + targetSheet.getSheetName());

			CellStyle[] columnStyles = 	getColumnStylesFromSecondRow(targetSheet);

			/////////////
			/////////////
			int shLastRowNum = targetSheet.getLastRowNum();
			
			int constraintObjColNum = -1;
			if (inConstraintHdr != null) {
				constraintObjColNum = inSrcXlObj.getObjColSeqNumOfHdr(inConstraintHdr);
			}

			System.out
			.println("At combineShRowsByKeysAndConstraints inConstraintHdr " + inConstraintHdr);
			System.out
			.println("inSrcXlObj.getShColSeqNumOfHdr(inConstraintHdr) " + inSrcXlObj.getShColSeqNumOfHdr(inConstraintHdr));
			System.out
			.println("At combineShRowsByKeysAndConstraints inConstraintHdr " + inSrcXlObj.getObjColSeqNumOfHdr(inConstraintHdr));
			
			
			/////////////
			/////////////
			for (int srcObjRowCount = 0; srcObjRowCount < inSrcXlObj.getTotalDetailRows(); srcObjRowCount++) {

				String recKey = inSrcXlObj.getKeyAtObjRowNum(srcObjRowCount);
				Row srcRow = sourceSheet.getRow(inSrcXlObj.getShRowNumOfObjRowNum(srcObjRowCount));
				Row targetRow = null;
				int targetObjRowNum = inTargetXlObj.getCreateXlObjRow(recKey);
				int targetShRowNum = inTargetXlObj.getShRowNumOfObjRowNum(targetObjRowNum);

				
				System.out.println("At combineShRowsByKeysAndConstraints targetObjRowNum = " + targetObjRowNum);
				System.out.println("targetShRowNum = " + targetShRowNum);
				
				
				boolean constraintMet = false;

				if (targetShRowNum == -1) {
					targetRow = targetSheet.getRow(shLastRowNum);
					if (!isRowEmpty(targetRow)) { // sometimes the last row may already be empty and can be used as is.
						shLastRowNum++;
						targetRow = targetSheet.createRow(shLastRowNum);
					}
					targetShRowNum = shLastRowNum;
					inTargetXlObj.setDetailShRowNumForKey(recKey, targetShRowNum);
					constraintMet = true; // constraint-check not applicable for new records.
				} else {
					targetRow = targetSheet.getRow(targetShRowNum);
					if (constraintObjColNum == -1) {
						constraintMet = true; // constraint-check not applicable when no constraint given
					} else {
						String currentConstraint = null;
						if (inTargetXlObj.getColValAtObjRowNumAndObjColNum(targetObjRowNum, constraintObjColNum) instanceof String) {					
							currentConstraint = (String) inTargetXlObj.getColValAtObjRowNumAndObjColNum(targetObjRowNum, constraintObjColNum);
						}
						if (currentConstraint != null && currentConstraint.equalsIgnoreCase(inConstraintVal)) {
							constraintMet = true;
						}
					}
				}

				System.out.println("srcRow.getFirstCellNum() = "
						+ srcRow.getFirstCellNum());
				System.out.println("srcRow.getLastCellNum() = "
						+ srcRow.getLastCellNum());
				
				if (constraintMet) {
					recsBroughtInto++;
					commuteRowValues(srcRow, targetRow, columnStyles);
				}
			}

			System.out.println("at before writing into = " + targetExcelFilePath);

//			FileOutputStream fOut = new FileOutputStream(targetExcelFilePath);
//			targetExcelWB.write(fOut);
//			System.out.println("writing done for " + targetExcelFilePath);
//			fOut.close();
			writeTargetWB();
	}
	
	private void writeTargetWB() throws IOException{
		FileOutputStream fOut = new FileOutputStream(targetExcelFilePath);
		targetExcelWB.write(fOut);
		System.out.println("writing done for " + targetExcelFilePath);
		fOut.close();		
	}

	public void combineShByKeysAndConstraint(String inKeyHdr, String inConstraintHdr, String inConstraintVal) 
					throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
							NoSuchMethodException, SecurityException {
		// For use in situations where you need to combine an incoming sheet into a combined sheet based on
		// the keys and also want to check if the incoming record adheres to constraint such as userID
		// 1. check the headers match
		// 2. get key and constraint col seq
		// 3. refresh, merge records on keys with a check to given constraint
		
		if (findHeadersMatch()) {

			ExcelJavaObj srcFileXlObj = getSrcXlObjForTwoColumns(inKeyHdr, inConstraintHdr);

			if (srcFileXlObj.processStatus == ExcelJavaObj.PROCSTAT_GOOD) {
			
				chkInitTargetSheet();
				
				ExcelJavaObj targetFileXlObj = getTargetXlObjForTwoColumns(inKeyHdr, inConstraintHdr);
				
				if (targetFileXlObj.processStatus == ExcelJavaObj.PROCSTAT_GOOD) {
					combineShRowsByKeysAndConstraints(srcFileXlObj, targetFileXlObj, inKeyHdr, 
							inConstraintHdr, inConstraintVal);
					
				}			
			}
		}
	}

	public void combineShByKeys(String inKeyHdr)
					throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
						NoSuchMethodException, SecurityException {
		// For use in situations where you need to combine an incoming sheet into a combined sheet based on
		// the keys and where no check required for the incoming record adherence to constraint such as userID				
		// 1. check the headers match
		// 2. get key col seq
		// 3. refresh, merge records on keys without any check for constraint
		
		combineShByKeysAndConstraint(inKeyHdr, null, null);		
	}
	
	public void combineShOnConstraint(String inConstraintHdr, String inConstraintVal) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
					NoSuchMethodException, SecurityException, IOException {
		// For use in situations where you need to combine an incoming sheet into a combined sheet based only on
		// the constraint such as userID				
		// 1. check the headers match
		// 2. get constraint col seq
		// 3. delete existing records with same constraint and add incoming records on the same constraint

		if (findHeadersMatch()) {
			ExcelJavaObj srcFileXlObj = getSrcXlObjForTwoColumns(inConstraintHdr,null);
			if (srcFileXlObj.processStatus == ExcelJavaObj.PROCSTAT_GOOD) {		
				copyRows(getFirstSheetNameOfSourceWB(), srcFileXlObj.getShColSeqNumOfHdr(inConstraintHdr), inConstraintVal);
			}
		}		
	}

	public void copyRows(String inExcelSheetName, int inKeyColumn,
			String inKeyValue) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		System.out.println("copyRows begins inExcelSheetName : "
				+ inExcelSheetName);
		System.out.println("copyRows begins inKeyColumn : " + inKeyColumn);
		System.out.println("copyRows begins inKeyValue: " + inKeyValue);
		Sheet sourceSheet = srcExcelWB.getSheet(inExcelSheetName);

		StartAndSize srcStartAndSize = getStartAndSizeOfSameRows(sourceSheet,
				inKeyColumn, inKeyValue);

		if (srcStartAndSize != null) {
			System.out.println("at copyrows1 srcStartAndSize for inKeyValue "
					+ inKeyValue);
			System.out.println("at copyrows1 srcStartAndSize.start = "
					+ srcStartAndSize.start);
			System.out
					.println("at copyrows1 srcStartAndSize.size = " + srcStartAndSize.size);
		}
		System.out
		.println("at at copyrows1 copyRows X1 ");

		if (!commonTechs.doesFileExist(targetExcelFilePath)) {
			
			System.out.println("at copyRows X1.1 ");
			
			cloneSheetsToNewWB(new String[] { inExcelSheetName });

			System.out.println("at copyRows X1.2 ");
			
			removeNonHeaders(new String[] { inExcelSheetName });

			System.out.println("at copyRows X1.3 ");
		}			
		
		System.out.println("at copyRows X1.3.1 targetSheet name is " + inExcelSheetName);


		FileInputStream targetFileStream = new FileInputStream(
				targetExcelFilePath);
		targetExcelWB = new XSSFWorkbook(targetFileStream); // read for
															// existing
															// content
		targetFileStream.close();
		Sheet targetSheet = targetExcelWB.getSheet(inExcelSheetName);

		System.out.println("at copyRows X1.4 targetSheet is " + targetSheet);

		if (targetSheet == null) {
			System.out.println("at copyRows X1.5 targetSheet is " + targetSheet);
			cloneSheetsToNewWB(new String[] { inExcelSheetName });

			//for debugging only start jj1
			Sheet targetSheet1 = targetExcelWB.getSheet(inExcelSheetName);
			System.out.println("at copyRows X1.6 inExcelSheetName is " + inExcelSheetName);
			System.out.println("at copyRows X1.6 targetSheet1 is " + targetSheet1);
			//for debugging only ends jj1

			removeNonHeaders(new String[] { inExcelSheetName });
			targetSheet = targetExcelWB.getSheet(inExcelSheetName);

			System.out.println("at copyRows X1.6 targetSheet is " + inExcelSheetName);
			System.out.println("at copyRows X1.7 targetSheet is " + targetSheet);
		}

		System.out
		.println("targetSheet at copyRows is " + targetSheet);
		System.out
		.println("targetSheet name at copyRows is " + targetSheet.getSheetName());
		
		removeOldRows(targetSheet, inKeyColumn, inKeyValue);

		if (srcStartAndSize != null) {
			
			System.out.println("gonna insert now for " + inKeyValue);
			System.out.println("srcStartAndSize st = " + srcStartAndSize.start);
			System.out.println("srcStartAndSize sz = " + srcStartAndSize.size);

			// for testing1 starts
			int afterRemoveOldRowsLastRowNum = targetSheet
					.getLastRowNum();
			System.out.println("afterRemoveOldRowsLastRowNum = "
					+ afterRemoveOldRowsLastRowNum);
			// for testing1 ends

			int rowStartLocation = findWhereToPlace(targetSheet,
					inKeyColumn, inKeyValue);
			System.out
					.println("final insert rowStartLocation = " + rowStartLocation);
			System.out
			.println("   for inKeyValue = " + inKeyValue);

			StartAndSize renewedStartAndSize = new StartAndSize(
					rowStartLocation, srcStartAndSize.size);
			System.out.println("renewedStart.start = "
					+ renewedStartAndSize.start);
			System.out.println("renewedStartAndSize.size = "
					+ renewedStartAndSize.size);

			makeRoomForNewRows(targetSheet, renewedStartAndSize);
			System.out.println("srcStartAndSize.start = "
					+ srcStartAndSize.start);
			System.out.println("srcStartAndSize.size  = "
					+ srcStartAndSize.size);
			System.out.println("rowStartLocation      = "
					+ rowStartLocation);
			insertRows(sourceSheet, targetSheet, srcStartAndSize,
					rowStartLocation);

			recsBroughtInto = srcStartAndSize.size;

		} else {
			System.out.println("skipped insert for " + inKeyValue);
			System.out.println("srcStartAndSize is " + srcStartAndSize);
		}

		System.out.println("at before writing into = " + targetExcelFilePath);

		//FileOutputStream fOut = new FileOutputStream(targetExcelFilePath);
		//targetExcelWB.write(fOut);
		//System.out.println("writing done for " + targetExcelFilePath);
		//fOut.close();
		writeTargetWB();
	}

	public void buildXlJavaObj(ExcelJavaObj inXlJavaObj, String inExcelSheetName) {		
		buildXlJavaObj(inXlJavaObj, inExcelSheetName,-1,"");		
	}

	public boolean setColPositions(ExcelJavaObj inXlJavaObj, String inExcelSheetName) {
		boolean goodSoFar = true;
		Sheet sourceSheet = srcExcelWB.getSheet(inExcelSheetName);
		Row headerRow = sourceSheet.getRow(0);

		for(int hdrColCnt=headerRow.getFirstCellNum(); hdrColCnt<headerRow.getLastCellNum(); hdrColCnt++) {
	        System.out.println("loop start hdrColCnt = " + hdrColCnt);
			Cell hdrColCell = headerRow.getCell(hdrColCnt);
			if (hdrColCell != null && hdrColCell.getCellTypeEnum() == CellType.STRING) {
				
				String hdrColValue = hdrColCell.getStringCellValue();
				System.out.println("hdrColCell.getStringCellValue() is " + hdrColValue);

				int colSeqNum = inXlJavaObj.getObjColSeqNumOfHdr(hdrColValue);
				System.out.println("colSeqNum  is " + colSeqNum);
				if (colSeqNum == -1) {
					continue; // the sheet column is not in interested list.
				}
				
				if (inXlJavaObj.getShColSeqNumOfHdr(hdrColValue) == -1) {
					inXlJavaObj.setShColSeqNumForHdr(hdrColValue,hdrColCnt);
				} else {
					inXlJavaObj.processStatus = ExcelJavaObj.PROCSTAT_ERROR;
					inXlJavaObj.processStateMsg = inXlJavaObj.processStateMsg + " Error in Excel process. Col header duplicate " + hdrColValue + " at " + hdrColCnt + " and " + inXlJavaObj.getShColSeqNumOfHdr(hdrColValue);
					System.out.println(" Error in Excel process. Col header duplicate " + hdrColValue + " at " + hdrColCnt + " and " + inXlJavaObj.getShColSeqNumOfHdr(hdrColValue));
					System.out.println(inXlJavaObj.processStateMsg);
					System.exit(8);
					goodSoFar = false;
					break;
				}
			}
		}

		String unfoundHdr = inXlJavaObj.getAnyUnfoundHeader();
		if (unfoundHdr != null) {
			inXlJavaObj.processStatus = ExcelJavaObj.PROCSTAT_ERROR;
			inXlJavaObj.processStateMsg = inXlJavaObj.processStateMsg + " Error in Excel process. Interested column not found " + unfoundHdr;
			System.out.println(" Error in Excel process. Interested column not found " + unfoundHdr);
			System.out.println(inXlJavaObj.processStateMsg);
			System.exit(8);
			
			goodSoFar = false;
			return goodSoFar;
		}

		return goodSoFar;
	}

	public void buildXlJavaObj(ExcelJavaObj inXlJavaObj, String inExcelSheetName,int inKeyColumn,String inKeyValue) {
		//IMPORTANT NOTE: The excelJavaObj created here doesn't set the column type (xlColType).
		//Its the user's responsibility to set correctly based on the business need
        System.out.println("buildXlJavaObj buildRows begins inExcelSheetName : " + inExcelSheetName);
        System.out.println("buildRows begins inKeyColumn : " + inKeyColumn);
        System.out.println("buildRows begins inKeyValue: " + inKeyValue);
		Sheet sourceSheet = srcExcelWB.getSheet(inExcelSheetName);

		if (!setColPositions(inXlJavaObj, inExcelSheetName)) {
			return;
		}
		
		StartAndSize srcStartAndSize = null;
		if (inKeyColumn > -1) {
			srcStartAndSize = getStartAndSizeOfSameRows(sourceSheet,inKeyColumn,inKeyValue);
		} else {
			// in case the key column number passed is -1 i.e. no key involved then retrieve all rows
			int lRowNum = sourceSheet.getLastRowNum();
	        System.out.println("buildXlJavaObj lRowNum is  " + lRowNum);
			while (lRowNum > 0 && isRowEmpty(sourceSheet.getRow(lRowNum))) {
		        System.out.println("buildXlJavaObj decrementing row count due to empty last row lRowNum : " + lRowNum);
				lRowNum--;
			}
			srcStartAndSize = new StartAndSize(1,lRowNum); //IMPORTANT: BOTH CELL NUM AND ROW NUM ARE ZERO BASED; BUT
															// ONLY THE LASTCELL() RETURNS POSITION PLUS ONE
															// Also row size to exclude the header hence subtracted 
			
	        System.out.println("buildXlJavaObj lRowNum : " + lRowNum);
	        System.out.println("buildXlJavaObj srcStartAndSize start : " + srcStartAndSize.start);
	        System.out.println("buildXlJavaObj srcStartAndSize size : " + srcStartAndSize.size);			
		}

		if (srcStartAndSize != null) {

			for(int rowCount=srcStartAndSize.start; rowCount<srcStartAndSize.start+srcStartAndSize.size; rowCount++) {
		        System.out.println("srcStartAndSize.start = " + srcStartAndSize.start);
		        System.out.println("srcStartAndSize.size = " + srcStartAndSize.size);
		        System.out.println("rowCount = " + rowCount);
		        
				Row srcXlRow = sourceSheet.getRow(rowCount);
		        System.out.println("srcXlRow = " + srcXlRow);
		        System.out.println("inXlJavaObj.getKeyShColPosition() = " + inXlJavaObj.getKeyShColPosition());

				String rowKey = srcXlRow.getCell(inXlJavaObj.getKeyShColPosition()).getStringCellValue();
				int objRowNum = inXlJavaObj.getCreateXlObjRow(rowKey,rowCount);

				System.out.println("rowCount = " + rowCount);
				System.out.println("inSrcStartAndSize.start+rowCount = " + (srcStartAndSize.start + rowCount));
	
		        System.out.println("srcRow.getFirstCellNum() = " + srcXlRow.getFirstCellNum());
		        System.out.println("srcRow.getLastCellNum() = " + srcXlRow.getLastCellNum());
	

				for (int javaObjColNum = 0; javaObjColNum < inXlJavaObj.getTotalObjCols(); javaObjColNum++) {

					int xlColNum = inXlJavaObj.getShColNumOfObjCol(javaObjColNum);
					int objColType = inXlJavaObj.getColTypeOfObjCol(javaObjColNum);
			        System.out.println("loop start javaObjColNum = " + javaObjColNum);
			        System.out.println("loop start xlColNum = " + xlColNum);
			        System.out.println("prior rowobj size is = " + objRowNum);
					Cell srcCell1 = srcXlRow.getCell(xlColNum);
			        System.out.println("prior rowobj size is = " + objRowNum);

					if (objColType == ExcelColJavaObj.COLTYPE_FORMULA){
			        System.out.println("cell at this position is a formula field for " + objColType);
				        System.out.println("cell at this position is a formula field. hence skipping");
				        continue;
					}

					if (srcCell1==null) {
				        System.out.println("cell is null ");
						System.out.println("continuing...then nulls have to checked by caller ");
						continue;
					}

					if (srcCell1.getCellTypeEnum()== null) {
						inXlJavaObj.processStatus = ExcelJavaObj.PROCSTAT_ERROR;
						inXlJavaObj.processStateMsg = inXlJavaObj.processStateMsg + " Error in Excel process. Cell type is null at col " + javaObjColNum + " row " + rowCount;
						System.out.println("excel error aa1");
						System.out.println(" Error in Excel process. Cell type is null at col " + javaObjColNum + " row " + rowCount);
						System.out.println(inXlJavaObj.processStateMsg);
						System.exit(8);
						return;
					} else if (srcCell1.getCellTypeEnum()== CellType.STRING) {
						System.out.println("excel at aax1");

						if (objColType==ExcelColJavaObj.COLTYPE_STRING){
							System.out.println("excel at aax2");
							inXlJavaObj.setColValAtObjRowNumAndObjColNum(objRowNum, javaObjColNum, srcCell1.getStringCellValue());

					        System.out.println("srcCell.getStringCellValue() = " + srcCell1.getStringCellValue());
						} else {
							System.out.println("excel at aax3");
							inXlJavaObj.processStatus = ExcelJavaObj.PROCSTAT_ERROR;

							inXlJavaObj.processStateMsg = inXlJavaObj.processStateMsg + " Error in Excel process. Type mismatch at cellCount " + javaObjColNum + ". String instead of " + objColType;
							System.out.println("excel error aa2");
							System.out.println(" Error in Excel process. Type mismatch at cellCount " + javaObjColNum + ". String instead of " + objColType);
							System.out.println(inXlJavaObj.processStateMsg);
							System.exit(8);
							return;
						}
						System.out.println("excel at aax4");
					} else if (srcCell1.getCellTypeEnum() == CellType.NUMERIC) {
						System.out.println("excel at aax5");

						if (objColType==ExcelColJavaObj.COLTYPE_DATE ||
								objColType==ExcelColJavaObj.COLTYPE_NUMERIC){
							System.out.println("excel at aax6");

							if (objColType==ExcelColJavaObj.COLTYPE_DATE){
								System.out.println("excel at aax7");
								if (HSSFDateUtil.isCellDateFormatted(srcCell1)) {
								    Date date = HSSFDateUtil.getJavaDate(srcCell1.getNumericCellValue());
									//rowJavaObj.set(javaObjColNum,date);
									inXlJavaObj.setColValAtObjRowNumAndObjColNum(objRowNum, javaObjColNum, date);
									
							        System.out.println("srcCell.getNumericCellValue() = " + srcCell1.getNumericCellValue());
							        System.out.println("srcCell.getDateCellValue() = " + date);
									System.out.println("excel at aax8");
								} else {
									inXlJavaObj.processStatus = ExcelJavaObj.PROCSTAT_ERROR;
									inXlJavaObj.processStateMsg = inXlJavaObj.processStateMsg + " Error in Excel process. Not a Valid Date at col " + javaObjColNum + " row " + rowCount;
									System.out.println("excel error aa3");
									System.out.println(inXlJavaObj.processStateMsg + " Error in Excel process. Not a Valid Date at col " + javaObjColNum + " row " + rowCount);
									System.out.println(inXlJavaObj.processStateMsg);
									System.exit(8);
									return;
								}
							} else {
								System.out.println("excel at aax9");

								inXlJavaObj.setColValAtObjRowNumAndObjColNum(objRowNum, javaObjColNum, srcCell1.getNumericCellValue());
								
						        System.out.println("srcCell.getNumericCellValue() = " + srcCell1.getNumericCellValue());								
							}
							System.out.println("excel at aax10");
						} else {
							inXlJavaObj.processStatus = ExcelJavaObj.PROCSTAT_ERROR;
							inXlJavaObj.processStateMsg = inXlJavaObj.processStateMsg + " Error in Excel process. Not a String at col " + javaObjColNum + " row " + rowCount;
							System.out.println("excel error aa4");
							System.out.println(inXlJavaObj.processStateMsg);
							System.exit(8);
							return;
						}
					} else if (srcCell1.getCellTypeEnum() == CellType.FORMULA) {
						System.out.println("excel at xx aax11");
						System.out.println("excel at xx aax11 formula is " + srcCell1.getCellFormula());
						
						//skip the formula fields as it cannot be handled outside excel

					}
					System.out.println("excel at aax11a srcCell1.getCellTypeEnum() is " + srcCell1.getCellTypeEnum());
					
					System.out.println("excel at aax11");					
					System.out.println("data stored is ....");
					System.out.println("a cellCount1 is .... = " + javaObjColNum);
			        System.out.println("b filled cell row obj size is = " + inXlJavaObj.getTotalDetailRows());

			        if (inXlJavaObj.getColValAtObjRowNumAndObjColNum(objRowNum,javaObjColNum)!= null) {

			        	if (objColType==ExcelColJavaObj.COLTYPE_DATE) {
			        		System.out.println("date stored is " + (Date) inXlJavaObj.getColValAtObjRowNumAndObjColNum(objRowNum,javaObjColNum));
			        	} else if (objColType==ExcelColJavaObj.COLTYPE_NUMERIC) {
			        		System.out.println("numeric data stored is " + (Double) inXlJavaObj.getColValAtObjRowNumAndObjColNum(objRowNum,javaObjColNum));
			        	} else if (objColType==ExcelColJavaObj.COLTYPE_STRING) {
			        		System.out.println("String data stored is " + (String) inXlJavaObj.getColValAtObjRowNumAndObjColNum(objRowNum,javaObjColNum));
			        	}
			        }					
				}
			}
		}
        System.out.println("Clean ending of buildXlJavaObj for user " + inKeyValue);
		return;
	}

	public void replaceFromXlJavaObj(ExcelJavaObj inXlJavaObj, String inExcelSheetName) throws IOException {
		// steps:
		// 0) Read the target file
		// 1) validate the header to match
		// 2) validate the input columns count match for all rows in it
		// 3) validate the output sheet columns count match for all rows in it
		// 4) make room for additional rows in out-sheet based on the size difference
		// 5) write obj rows into sheet
		// 6) Write to physical target file

		FileInputStream targetFileStream = new FileInputStream(
				targetExcelFilePath);
		targetExcelWB = new XSSFWorkbook(targetFileStream); // read for
															// existing
															// content
		targetFileStream.close();

		Sheet targetSheet = targetExcelWB.getSheet(inExcelSheetName);
		copyXlJavaObjRowsIntoSheet(inXlJavaObj, targetSheet);
		XSSFFormulaEvaluator.evaluateAllFormulaCells(targetExcelWB);

		// write to physical file
		System.out.println("at before writing into = " + targetExcelFilePath);

		//FileOutputStream fOut = new FileOutputStream(targetExcelFilePath);
		//targetExcelWB.write(fOut);
		//System.out.println("writing done for " + targetExcelFilePath);
		//fOut.close();
		writeTargetWB();
	}
	
	private void copyXlJavaObjRowsIntoSheet(ExcelJavaObj inXlJavaObj, Sheet inTargetSheet) {

		System.out.println("At start of copyXlJavaObjRowsIntoSheet inXlJavaObj.getTotalDetailRows() = " + inXlJavaObj.getTotalDetailRows());

		CellStyle cellDateStyle;
        CreationHelper createHelper = targetExcelWB.getCreationHelper();  
            cellDateStyle = targetExcelWB.createCellStyle();  
            cellDateStyle.setDataFormat(  
                createHelper.createDataFormat().getFormat(DEFAULTDATEFORMAT));

		int shLastRowNum = inTargetSheet.getLastRowNum();

		for (int objRowNum = 0; objRowNum < inXlJavaObj.getTotalDetailRows(); objRowNum++) {

			String rowIDKey = (String) inXlJavaObj.getColValAtObjRowNumAndObjColNum(objRowNum,inXlJavaObj.getKeyObjColPosition());
			System.out.println("row ID is " + rowIDKey);

			int targetShRowNum = inXlJavaObj.getShRowNumOfObjRowNum(objRowNum);
			
			System.out.println("row ID and location are " + rowIDKey + " and " + targetShRowNum);
			
			Row row = null;
			System.out.println("copyXlJavaObjRowsIntoSheet for sheet row num " + targetShRowNum);

			//ALSO CHECK IF THE LAST ROW IS EMPTY. IN THAT CASE UPDATE THE LAST ROW WITH THE NEW ROW VALUES
			//ALSO CHECK IF THE LAST ROW IS EMPTY. IN THAT CASE UPDATE THE LAST ROW WITH THE NEW ROW VALUES
			//ALSO CHECK IF THE LAST ROW IS EMPTY. IN THAT CASE UPDATE THE LAST ROW WITH THE NEW ROW VALUES

			if (targetShRowNum == -1) {
				row = inTargetSheet.getRow(shLastRowNum);
				if (!isRowEmpty(row)) {
					shLastRowNum++;	// keep incrementing this counter upon each new row
					row = inTargetSheet.createRow(shLastRowNum);	// if row didn't exist before create a new xl row
					//System.out.println("copyXlJavaObjRowsIntoSheet aa00 row is" + row);
				}
				inXlJavaObj.setDetailShRowNumForKey(rowIDKey, shLastRowNum);
			} else {
				System.out.println("copyXlJavaObjRowsIntoSheet aa02 targetShRowNum is" + targetShRowNum);				
				row = inTargetSheet.getRow(targetShRowNum);
			}

			for (int javaObjColNum = 0; javaObjColNum < inXlJavaObj.getTotalObjCols(); javaObjColNum++) {

				int objColType = inXlJavaObj.getColTypeOfObjCol(javaObjColNum);
				int shCellCount = inXlJavaObj.getShColNumOfObjCol(javaObjColNum);

				System.out.println("copyXlJavaObjRowsIntoSheet for sheet column " + shCellCount);

				if (objColType == ExcelColJavaObj.COLTYPE_FORMULA) {
					System.out.println("Skipping the formula field at " + shCellCount);
					continue;
				}

				System.out.println("cellCount = " + shCellCount);
				Cell targetCell = row.getCell(shCellCount);
				
				if (targetCell == null) {
					targetCell = row.createCell(shCellCount);
					System.out.println("targetCell new cell created ");
				}
				
				Object inputObjCellValue = inXlJavaObj.getColValAtObjRowNumAndObjColNum(objRowNum, javaObjColNum);

				if (inputObjCellValue == null) {
					//commnenting the empty cell creation as it would wipe out the format
					//targetCell = row.createCell(shCellCount);				
					if (targetCell != null && targetCell.getCellTypeEnum() != CellType.BLANK) {
						if (objColType == ExcelColJavaObj.COLTYPE_STRING) {
							targetCell.setCellValue(ExcelColJavaObj.INITVALUE_STRING);
							System.out.println("init ExcelColJavaObj.INITVALUE_STRING " + ExcelColJavaObj.INITVALUE_STRING);
							System.out.println("init string is " + targetCell.getStringCellValue());
						} else if (objColType == ExcelColJavaObj.COLTYPE_DATE) {
							System.out.println("value setting for Date cell " + ExcelColJavaObj.COLTYPE_DATE);
							if (isDate1904) {
								System.out.println("setting from ExcelColJavaObj.INITVALUE_DATE1904 " + ExcelColJavaObj.INITVALUE_DATE1904);								
								targetCell.setCellValue(ExcelColJavaObj.INITVALUE_DATE1904);
								targetCell.setCellStyle(cellDateStyle);
							} else {
								System.out.println("setting from ExcelColJavaObj.INITVALUE_DATE1900 " + ExcelColJavaObj.INITVALUE_DATE1900);
								targetCell.setCellValue(ExcelColJavaObj.INITVALUE_DATE1900);								
								targetCell.setCellStyle(cellDateStyle);
							}
							System.out.println("init date is " + targetCell.getDateCellValue());
						} else if (objColType == ExcelColJavaObj.COLTYPE_NUMERIC) {
							targetCell.setCellValue(ExcelColJavaObj.INITVALUE_NUMERIC);
							System.out.println("init ExcelColJavaObj.INITVALUE_NUMERIC " + ExcelColJavaObj.INITVALUE_NUMERIC);
							System.out.println("init numeric is " + targetCell.getNumericCellValue());
						}
						System.out.println("set init values ");
					}
					System.out.println("skipping as input is empty ");
					continue;
				}

				if (objColType == ExcelColJavaObj.COLTYPE_STRING) {				
					targetCell.setCellValue((String) inputObjCellValue);
					System.out.println("updated targetCell.getStringCellValue() = "
							+ targetCell.getStringCellValue());
				} else if (objColType == ExcelColJavaObj.COLTYPE_NUMERIC) {
					if (inputObjCellValue instanceof Integer) {
						System.out.println("updated from Integer ");
						targetCell.setCellValue(Double.valueOf((Integer) inputObjCellValue));
					} else {
						System.out.println("updated from Double ");
						targetCell.setCellValue((Double) inputObjCellValue);
					}
					System.out.println("updated targetCell.getNumericCellValue() = "
							+ targetCell.getNumericCellValue());
				} else if (objColType == ExcelColJavaObj.COLTYPE_DATE) {

					System.out.println("targetCell is " + targetCell);

					Date dteInJavaObj = (Date) inputObjCellValue;
					System.out.println("dteInJavaObj is " + dteInJavaObj);
					targetCell.setCellValue(HSSFDateUtil.getExcelDate(dteInJavaObj,isDate1904));
					targetCell.setCellStyle(cellDateStyle);
					System.out.println("updated targetCell date value is "
							+ HSSFDateUtil.getJavaDate(targetCell.getNumericCellValue()));
				}
			}
		}
		System.out.println("insertRows done = ");
	}

	private boolean isRowEmpty(Row inRow) {
        System.out.println("Checking isRowEmpty " );

		boolean rowEmpty = true;
		
		for(int cellCount=inRow.getFirstCellNum(); cellCount<inRow.getLastCellNum(); cellCount++) {
			Cell rowCell = inRow.getCell(cellCount); 
	        System.out.println("checking cell at cellCount : " + cellCount);

			if (rowCell!=null) {
		        System.out.println("cellCount is not null " + cellCount);
		        if (rowCell.getCellTypeEnum() != CellType.BLANK) {
		        	System.out.println("the cell is non blank value is :: " + rowCell.getStringCellValue());
					rowEmpty = false;
					break;
		        }
			}
		}
		return rowEmpty;		
	}

	private boolean matchAllRowsLength(Sheet inTargetSheet) {
		// this method checks if all rows have the same number of cells

		boolean rowsLengthMatchFlag = true;
		Row firstRow = inTargetSheet.getRow(0);
		int firstRowFirstCellNo = firstRow.getFirstCellNum();
		int firstRowLastCellNo = firstRow.getLastCellNum();
		int firstRowColSize = firstRowLastCellNo - firstRowFirstCellNo;

		for (int rowCount = 1; rowCount <= inTargetSheet.getLastRowNum() ; rowCount++) {
			Row row = inTargetSheet.getRow(rowCount);
			int rowFirstCellNo = row.getFirstCellNum();
			int rowLastCellNo = row.getLastCellNum();
			if (rowFirstCellNo != firstRowFirstCellNo) {
				rowsLengthMatchFlag = false;
				break;
			}
			if ((rowLastCellNo - rowFirstCellNo) != firstRowColSize ) {
				rowsLengthMatchFlag = false;
				break;
			}
		}
		return rowsLengthMatchFlag;
	}
	
	private boolean matchAllStringsRow(ArrayList<String> inStringsList, Row inStringsRow) {
		// this method checks if all cells of the row are having same string value as in the array
		// this method should be called only if all cells are strings

		boolean cellsMatchFlag = true;
		
		int rowFirstCellNo = inStringsRow.getFirstCellNum();
		int rowLastCellNo = inStringsRow.getLastCellNum();
		if ((rowLastCellNo - rowFirstCellNo) != inStringsList.size()){
			cellsMatchFlag = false;
		} else {
			for(int cellCount=inStringsRow.getFirstCellNum(); cellCount<inStringsRow.getLastCellNum(); cellCount++) {
		        System.out.println("buildXlJavaObj headerRow cellCount = " + cellCount);
		        System.out.println("buildXlJavaObj headerRow headerRow.getFirstCellNum() = " + inStringsRow.getFirstCellNum());
		        System.out.println("buildXlJavaObj headerRow headerRow.getLastCellNum() = " + inStringsRow.getLastCellNum());
		        System.out.println("buildXlJavaObj headerRow srcCell.getStringCellValue() = " + inStringsRow.getCell(cellCount).getStringCellValue());
		        System.out.println("buildXlJavaObj inXlJavaObj.xlColHeaderInPosn = " + inStringsList.get(cellCount));
				Cell rowCell = inStringsRow.getCell(cellCount); 

				if (rowCell==null) {
					if (inStringsList.get(cellCount) != null || !inStringsList.get(cellCount).equals("")) {
						cellsMatchFlag = false;
						break;
					}
				} else if (inStringsList.get(cellCount) == null) {
					if (rowCell != null || !rowCell.getStringCellValue().equals("")) {
						cellsMatchFlag = false;
						break;
					}						
				} else if (!rowCell.getStringCellValue().equals(inStringsList.get(cellCount))) {
					cellsMatchFlag = false;
					break;
				}
			}
		}
		return cellsMatchFlag;
	}
	
	private void removeOldRows(Sheet inSheet, int inKeyColumn, String inKeyValue) {
		System.out
		.println("inSheet at removeOldRows is " + inSheet);

		StartAndSize removingStartAndSize = getStartAndSizeOfSameRows(inSheet,
				inKeyColumn, inKeyValue);
		System.out.println("removingStartAndSize.start = "
				+ (removingStartAndSize == null ? "null"
						: removingStartAndSize.start));
		System.out.println("removingStartAndSize.size = "
				+ (removingStartAndSize == null ? "null"
						: removingStartAndSize.size));

		//int loopMax = 1000000;
		int loopMax = MAXROWS;
		int loopCount = 0;
		while (inSheet != null && removingStartAndSize != null
				&& loopCount++ < loopMax) {
			System.out.println("loopCount = " + loopCount);
			System.out.println("in removeOldRows removingStartAndSize.start = "
					+ (removingStartAndSize == null ? "null"
							: removingStartAndSize.start));
			System.out.println("removingStartAndSize.size = "
					+ (removingStartAndSize == null ? "null"
							: removingStartAndSize.size));

			if (removingStartAndSize != null) {
				removeRows(inSheet, removingStartAndSize);
			}

			// for testing2 starts
			int afterRemoveOldRowsLastRowNum = inSheet.getLastRowNum();
			System.out.println("at testing2 of RemoveOldRowsLastRowNum = "
					+ afterRemoveOldRowsLastRowNum);
			// for testing2 ends

			System.out
					.println("In removeOldRows Trying again to find removeable rows = ");
			removingStartAndSize = getStartAndSizeOfSameRows(inSheet,
					inKeyColumn, inKeyValue);
		}
	}

	public StartAndSize getStartAndSizeOfSameRows(Sheet inSheet,
			int inKeyColumn, String inKeyValue) {
		StartAndSize startAndSize = null;
		System.out
		.println("inSheet at getStartAndSizeOfSameRows is " + inSheet);
		
		int lRowNum = inSheet.getLastRowNum();
		for (int rowCount = 0; rowCount <= lRowNum; rowCount++) {
			System.out.println("rowCount = " + rowCount);
			Row row = inSheet.getRow(rowCount);

			if (row == null
					|| row.getCell(inKeyColumn) == null
					|| row.getCell(inKeyColumn).getCellTypeEnum() != CellType.STRING) {
				if (startAndSize == null) {
					System.out
							.println("not a string, and not yet found the key rows, hence skip and continue");
					continue;
				} else {
					System.out.println("already set hence breaking. row = "
							+ row);
					break;
				}
			}
			System.out.println("not skipped");

			String cValue = row.getCell(inKeyColumn).getStringCellValue();

			if (cValue != null && cValue.equalsIgnoreCase(inKeyValue)) {
				if (startAndSize == null) {
					System.out.println("cValue found = " + cValue);
					startAndSize = new StartAndSize(rowCount, 1);
				} else {
					startAndSize.size++;
				}
				System.out.println("adding startAndSize.size = "
						+ startAndSize.size);
			} else if (startAndSize != null) {
				break; // already captured the required rows
			}
		}
		System.out.println("return value startAndSize.size = "
				+ (startAndSize == null ? "null" : startAndSize.size));
		return startAndSize;
	}

	private void removeRows(Sheet inSheet, StartAndSize inStartAndSize) {

		if (inSheet == null || inStartAndSize == null) {
			return;
		}
		int lastRowNum = inSheet.getLastRowNum();
		System.out.println("removeRows lastRowNum = " + lastRowNum);
		System.out.println("lastRowNum = " + lastRowNum);
		System.out.println("inStartAndSize.start = "
				+ (inStartAndSize.start));
		System.out
				.println("inStartAndSize.size = " + (inStartAndSize.size));
		System.out.println("inStartAndSize.start+inStartAndSize.size = "
				+ (inStartAndSize.start + inStartAndSize.size));
		System.out.println("-1*inStartAndSize.size = "
				+ (-1 * inStartAndSize.size));
		if (inStartAndSize.start >= 0 && inStartAndSize.start < lastRowNum) {
			System.out.println("path2");

			int postRows = lastRowNum
					- (inStartAndSize.start + inStartAndSize.size - 1);
			System.out.println("postRows = " + postRows);

			if (postRows < inStartAndSize.size) {
				System.out.println("postRows less than size ");
				int excessRowStart = inStartAndSize.start + postRows;
				int excessRowEnd = inStartAndSize.start
						+ inStartAndSize.size - 1;
				System.out.println("excessRowStart = " + excessRowStart);
				System.out.println("excessRowEnd = " + excessRowEnd);
				for (int excessRowCount = excessRowStart; excessRowCount <= excessRowEnd; excessRowCount++) {
					System.out
							.println("excessRowCount = " + excessRowCount);
					Row removingRow2 = inSheet.getRow(excessRowCount);
					inSheet.removeRow(removingRow2);

					// for testing6 starts
					int afterRemoveOldRowsLastRowNum = inSheet
							.getLastRowNum();
					System.out
							.println("at testing6 of RemoveOldRowsLastRowNum = "
									+ afterRemoveOldRowsLastRowNum);
					// for testing6 ends

				}
			}

			int beforeShiftingOldRowsLastRowNum = inSheet.getLastRowNum();
			System.out
					.println("in RemoveOldRows beforeShiftingOldRowsLastRowNum = "
							+ beforeShiftingOldRowsLastRowNum);

			System.out
					.println("in RemoveOldRows inStartAndSize.start+inStartAndSize.size = "
							+ (inStartAndSize.start + inStartAndSize.size));
			System.out
					.println("in RemoveOldRows beforeShiftingOldRowsLastRowNum = "
							+ (beforeShiftingOldRowsLastRowNum));

			System.out
					.println("inStartAndSize.start+inStartAndSize.size = "
							+ (inStartAndSize.start + inStartAndSize.size));
			System.out.println("lastRowNum = " + lastRowNum);
			System.out.println("-1*inStartAndSize.size = "
					+ (-1 * inStartAndSize.size));
			if (inStartAndSize.start + inStartAndSize.size <= beforeShiftingOldRowsLastRowNum) {
				System.out.println("still shifting is required");
				System.out.println("lastRowNum = " + lastRowNum);
				System.out
						.println("in RemoveOldRows inStartAndSize.start+inStartAndSize.size-1 = "
								+ (inStartAndSize.start
										+ inStartAndSize.size - 1));
				System.out
						.println("in RemoveOldRows -1*inStartAndSize.size = "
								+ (-1 * inStartAndSize.size));

				inSheet.shiftRows(inStartAndSize.start
						+ inStartAndSize.size, lastRowNum, -1
						* inStartAndSize.size,false,false);
			} else {
				System.out.println("shifting was not required");
			}
			// for testing4 starts
			int afterRemoveOldRowsLastRowNum = inSheet.getLastRowNum();
			System.out.println("at testing2 of RemoveOldRowsLastRowNum = "
					+ afterRemoveOldRowsLastRowNum);
			// for testing4 ends
		} else if (inStartAndSize.start == lastRowNum) {
			System.out.println("path3");
			Row removingRow = inSheet.getRow(lastRowNum);
			if (removingRow != null) {
				System.out.println("path4");
				inSheet.removeRow(removingRow);
			}
		}
		// for testing3 starts
		int afterRemoveOldRowsLastRowNum = inSheet.getLastRowNum();
		System.out.println("at testing3 of RemoveOldRowsLastRowNum = "
				+ afterRemoveOldRowsLastRowNum);
		// for testing3 ends

	}

	public int findWhereToPlace(Sheet inSheet, int inKeyColumn,
			String inKeyValue) {
		int toPlaceRow = -1;
		int lRowNum = inSheet.getLastRowNum();
		System.out.println("in findWhereToPlace for inKeyValue " + inKeyValue);
		System.out.println("in findWhereToPlace default toPlaceRow " + toPlaceRow);
		System.out.println("in findWhereToPlace lRowNum " + lRowNum);
		for (int rowCount = 1; rowCount <= lRowNum; rowCount++) // first row
																// headerRow
		{
			System.out.println("in findWhereToPlace loop for rowCount = " + rowCount);
			Row row = inSheet.getRow(rowCount);
			String cValue = row.getCell(inKeyColumn).getStringCellValue();
			System.out.println("in findWhereToPlace loop cValue is " + cValue);

			if (cValue != null && cValue.compareTo(inKeyValue) > 0) {
				toPlaceRow = rowCount;
				System.out.println("in findWhereToPlace found toPlaceRow " + toPlaceRow);
				break; // already captured the required rows
			}
		}
		if (toPlaceRow == -1) {
			toPlaceRow = lRowNum + 1;
		}
		
		return toPlaceRow;
	}

	private int makeRoomForNewRows(Sheet inSheet, StartAndSize inStartAndSize) {
		System.out.println("makeRoomForNewRows begins inSheet = "
				+ inSheet.getSheetName());
		System.out.println("makeRoomForNewRows begins start = "
				+ inStartAndSize.start);
		System.out.println("makeRoomForNewRows begins size = "
				+ inStartAndSize.size);

		int startRow = -1;
		try {
			if (inSheet != null && inStartAndSize != null) {
				int lastRowNum = inSheet.getLastRowNum();
				System.out.println("lastRow = " + lastRowNum);
				if (inStartAndSize.start >= 0
					&& inStartAndSize.start <= lastRowNum
						) {
					System.out.println("shifting inStartAndSize.start = "
							+ inStartAndSize.start);
					System.out.println("shifting inStartAndSize.size = "
							+ inStartAndSize.size);
					System.out.println("shifting lastRowNum = " + lastRowNum);
					inSheet.shiftRows(inStartAndSize.start, lastRowNum,
							inStartAndSize.size,false,false);
				}
			}
		} finally {
		}
		return startRow;
	}

	CellStyle[] getColumnStylesFromSecondRow(Sheet inTargetSheet){
		CellStyle[] columnStyles = null;
		Row secondRow = inTargetSheet.getRow(1);
		System.out.println("secondRow = " + secondRow);

		if (secondRow != null) {
			int totalColumns = secondRow.getLastCellNum();
			System.out.println("totalColumns = " + totalColumns);
			System.out.println("secondRow.getFirstCellNum() = "
					+ secondRow.getFirstCellNum());
			System.out.println("secondRow.getLastCellNum() = "
					+ secondRow.getLastCellNum());

			columnStyles = new CellStyle[totalColumns];
			for (int cellCount = secondRow.getFirstCellNum(); cellCount < secondRow
					.getLastCellNum(); cellCount++) {
				System.out.println("cellCount = " + cellCount);
				Cell cell = secondRow.getCell(cellCount);
				if (cell.getCellTypeEnum() == CellType.STRING) {
					System.out.println("cell Value() getString = "
							+ cell.getStringCellValue());
				} else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
					System.out.println("cell Value getNumeric = "
							+ cell.getNumericCellValue());
				} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
					System.out.println("cell Value getBoolean= "
							+ cell.getBooleanCellValue());
				}

				columnStyles[cellCount] = secondRow.getCell(cellCount)
						.getCellStyle();
				System.out.println("columnStyles[cellCount] = "
						+ columnStyles[cellCount]);
			}
		}
		return columnStyles;
	}

	private void insertRows(Sheet inSrcSheet, Sheet inTargetSheet,
			StartAndSize inSrcStartAndSize, int inStartRowNum) {
		System.out.println("insertRows begins inSrcSheet = "
				+ inSrcSheet.getSheetName());
		System.out.println("insertRows begins inTargetSheet = "
				+ inTargetSheet.getSheetName());
		System.out.println("insertRows begins inSrcSheet = "
				+ inSrcSheet.getSheetName());
		System.out.println("inSrcStartAndSize.start = "
				+ inSrcStartAndSize.start);
		System.out.println("inSrcStartAndSize.start = "
				+ inSrcStartAndSize.start);
		System.out.println("inStartRowNum = " + inStartRowNum);

		CellStyle[] columnStyles = 	getColumnStylesFromSecondRow(inTargetSheet);

		System.out.println("inStartRowNum = " + inStartRowNum);
		System.out
				.println("inSrcStartAndSize.size = " + inSrcStartAndSize.size);
		System.out.println("inStartRowNum+inSrcStartAndSize.size = "
				+ (inStartRowNum + inSrcStartAndSize.size));

		for (int rowCount = inStartRowNum; rowCount < inStartRowNum
				+ inSrcStartAndSize.size; rowCount++) {
			Row targetRow = inTargetSheet.createRow(rowCount);
			Row srcRow = inSrcSheet.getRow(inSrcStartAndSize.start + rowCount
					- inStartRowNum);

			System.out.println("rowCount = " + rowCount);
			System.out
					.println("inSrcStartAndSize.start+rowCount-inStartRowNum = "
							+ (inSrcStartAndSize.start + rowCount - inStartRowNum));

			System.out.println("srcRow.getFirstCellNum() = "
					+ srcRow.getFirstCellNum());
			System.out.println("srcRow.getLastCellNum() = "
					+ srcRow.getLastCellNum());
			
			commuteRowValues(srcRow, targetRow, columnStyles);

//			for (int cellCount = srcRow.getFirstCellNum(); cellCount < srcRow
//					.getLastCellNum(); cellCount++) {
//
//				System.out.println("cellCount = " + cellCount);
//				Cell newCell = targetRow.createCell(cellCount);
//				Cell srcCell = srcRow.getCell(cellCount);
//
//				if (columnStyles == null) {
//					// set new cellStyles only once for the WB for second row
//					// and reuse for later rows
//					columnStyles = new CellStyle[srcRow.getLastCellNum()];
//					for (int cloneCellCount = srcRow.getFirstCellNum(); cloneCellCount < srcRow
//							.getLastCellNum(); cloneCellCount++) {
//						System.out.println("cloneCellCount = " + cloneCellCount);
//						columnStyles[cloneCellCount] = targetExcelWB
//								.createCellStyle();
//						if (srcRow.getCell(cloneCellCount) != null) {
//							CellStyle origStyle = srcRow.getCell(cloneCellCount)
//									.getCellStyle(); // Or from a cell
//							columnStyles[cloneCellCount].cloneStyleFrom(origStyle);
//							System.out.println("style has been set for cloneCellCount = " + cloneCellCount);
//						} else {
//							columnStyles[cloneCellCount] = targetExcelWB.getCellStyleAt(0);
//						}
//					}
//				}
//				newCell.setCellStyle(columnStyles[cellCount]);
//
//				newCell.setCellType(srcCell.getCellTypeEnum());
//				System.out.println("srcCell.getCellTypeEnum() = "
//						+ srcCell.getCellTypeEnum());
//
//				if (srcCell.getCellTypeEnum() == CellType.STRING) {
//					newCell.setCellValue(srcCell.getStringCellValue());
//					System.out.println("srcCell.getStringCellValue() = "
//							+ srcCell.getStringCellValue());
//				} else if (srcCell.getCellTypeEnum() == CellType.NUMERIC) {
//					newCell.setCellValue(srcCell.getNumericCellValue());
//					System.out.println("srcCell.getNumericCellValue() = "
//							+ srcCell.getNumericCellValue());
//				} else if (srcCell.getCellTypeEnum() == CellType.BOOLEAN) {
//					newCell.setCellValue(srcCell.getBooleanCellValue());
//					System.out.println("srcCell.getBooleanCellValue() = "
//							+ srcCell.getBooleanCellValue());
//				} else if (srcCell.getCellTypeEnum() == CellType.FORMULA) {
//					
//					CellValue newCellValue = srcWbFomulaEvaluator.evaluate(srcCell);
//
//					switch (newCellValue.getCellTypeEnum()) {
//						case BOOLEAN:
//					    	newCell.setCellValue(newCellValue.getBooleanValue());
//					    	break;
//						case NUMERIC:
//					    	newCell.setCellValue(newCellValue.getNumberValue());
//					    	break;
//						case STRING:
//							newCell.setCellValue(newCellValue.getStringValue());
//					    	break;
//					    case BLANK:
//					    	break;
//					    case ERROR:
//					    	break;
//					    // CELL_TYPE_FORMULA will never happen
//					    case FORMULA: 
//					    	break;
//						case _NONE:
//							break;
//						default:
//							break;
//					}
//					newCell.setCellFormula(null);	// reset the formula
//				}
//
//				if (srcCell.getCellComment() != null) {
//					if (drawingPatriarch == null) {
//						drawingPatriarch = newCell.getSheet().createDrawingPatriarch();
//						factoryXSSFCreationHelper = newCell.getSheet().getWorkbook()
//								.getCreationHelper();
//					}
//					ClientAnchor anchor = factoryXSSFCreationHelper.createClientAnchor();
//					anchor.setCol1(newCell.getColumnIndex());
//					anchor.setCol2(newCell.getColumnIndex() + 1);
//					anchor.setRow1(newCell.getRowIndex());
//					anchor.setRow2(newCell.getRowIndex() + 3);
//
//					Comment comment = drawingPatriarch.createCellComment(anchor);
//					comment.setVisible(Boolean.FALSE);
//					comment.setString(srcCell.getCellComment().getString());
//					newCell.setCellComment(srcCell.getCellComment());
//				}
//			}
		}
		System.out.println("insertRows done = ");
		return;
	}
	
	private void commuteRowValues(Row inSrcRow, Row inTargetRow, CellStyle[] columnStyles) {
		//Row row = inTargetSheet.createRow(rowCount);
		//Row srcRow = inSrcSheet.getRow(inSrcStartAndSize.start + rowCount
		//		- inStartRowNum);
		System.out.println("At ExcelHandler commuteRowValues");

		for (int cellCount = inSrcRow.getFirstCellNum(); cellCount < inSrcRow
				.getLastCellNum(); cellCount++) {

			System.out.println("cellCount = " + cellCount);
			Cell newCell = inTargetRow.createCell(cellCount);
			Cell srcCell = inSrcRow.getCell(cellCount);

			if (columnStyles == null) {
				// set new cellStyles only once for the WB for second row
				// and reuse for later rows
				columnStyles = new CellStyle[inSrcRow.getLastCellNum()];
				for (int cloneCellCount = inSrcRow.getFirstCellNum(); cloneCellCount < inSrcRow
						.getLastCellNum(); cloneCellCount++) {
					System.out.println("cloneCellCount = " + cloneCellCount);
					columnStyles[cloneCellCount] = targetExcelWB
							.createCellStyle();
					if (inSrcRow.getCell(cloneCellCount) != null) {
						CellStyle origStyle = inSrcRow.getCell(cloneCellCount)
								.getCellStyle(); // Or from a cell
						columnStyles[cloneCellCount].cloneStyleFrom(origStyle);
						System.out.println("style has been set for cloneCellCount = " + cloneCellCount);
					} else {
						columnStyles[cloneCellCount] = targetExcelWB.getCellStyleAt(0);
					}
				}
			}
			newCell.setCellStyle(columnStyles[cellCount]);

			//newCell.setCellType(srcCell.getCellTypeEnum());
			System.out.println("srcCell.getCellTypeEnum() = "
					+ srcCell.getCellTypeEnum());

			if (srcCell.getCellTypeEnum() == CellType.STRING) {
				newCell.setCellType(srcCell.getCellTypeEnum());
				newCell.setCellValue(srcCell.getStringCellValue());
				System.out.println("srcCell.getStringCellValue() = "
						+ srcCell.getStringCellValue());
			} else if (srcCell.getCellTypeEnum() == CellType.NUMERIC) {
				newCell.setCellType(srcCell.getCellTypeEnum());
				newCell.setCellValue(srcCell.getNumericCellValue());
				System.out.println("srcCell.getNumericCellValue() = "
						+ srcCell.getNumericCellValue());
			} else if (srcCell.getCellTypeEnum() == CellType.BOOLEAN) {
				newCell.setCellType(srcCell.getCellTypeEnum());
				newCell.setCellValue(srcCell.getBooleanCellValue());
				System.out.println("srcCell.getBooleanCellValue() = "
						+ srcCell.getBooleanCellValue());
			} else if (srcCell.getCellTypeEnum() == CellType.FORMULA) {
				
				CellValue newCellValue = srcWbFomulaEvaluator.evaluate(srcCell);

				switch (newCellValue.getCellTypeEnum()) {
					case BOOLEAN:
				    	newCell.setCellValue(newCellValue.getBooleanValue());
				    	break;
					case NUMERIC:
				    	newCell.setCellValue(newCellValue.getNumberValue());
				    	break;
					case STRING:
						newCell.setCellValue(newCellValue.getStringValue());
				    	break;
				    case BLANK:
				    	break;
				    case ERROR:
				    	break;
				    // CELL_TYPE_FORMULA will never happen
				    case FORMULA: 
				    	break;
					case _NONE:
						break;
					default:
						break;
				}
				newCell.setCellFormula(null);	// reset the formula
			}

			if (srcCell.getCellComment() != null) {
				if (drawingPatriarch == null) {
					drawingPatriarch = newCell.getSheet().createDrawingPatriarch();
					factoryXSSFCreationHelper = newCell.getSheet().getWorkbook()
							.getCreationHelper();
				}
				ClientAnchor anchor = factoryXSSFCreationHelper.createClientAnchor();
				anchor.setCol1(newCell.getColumnIndex());
				anchor.setCol2(newCell.getColumnIndex() + 1);
				anchor.setRow1(newCell.getRowIndex());
				anchor.setRow2(newCell.getRowIndex() + 3);

				Comment comment = drawingPatriarch.createCellComment(anchor);
				comment.setVisible(Boolean.FALSE);
				comment.setString(srcCell.getCellComment().getString());
				newCell.setCellComment(srcCell.getCellComment());
			}
		}		
	}

	private void removeRows(Sheet inSheet, int inStartRowNum, int inRemoveLength) {
		System.out.println("At removeRows for inSheet " + inSheet
				+ " from start row " + inStartRowNum + " for inRemoveLength "
				+ inRemoveLength);

		// remove in descending order since the rownumber will get decremented
		// after each removal
		for (int rowCount = inStartRowNum + inRemoveLength - 1; rowCount >= inStartRowNum; rowCount--) {
			Row row = inSheet.getRow(rowCount);
			if (row != null) {
				inSheet.removeRow(row);
			}
		}
	}

	private void removeNonHeaders(String[] inCheckheetNames) throws IOException {
		System.out.println("At removeNonHeaders for " + inCheckheetNames[0]
				+ " through " + inCheckheetNames[inCheckheetNames.length - 1]);
		
		//FileOutputStream targetFileStream3 = new FileOutputStream(
		//		targetExcelFilePath);
		//targetExcelWB = new XSSFWorkbook(); // at this juncture we have already have content in targetExcelWB

		for (int sheetCount = 0; sheetCount < inCheckheetNames.length; sheetCount++) {
			
			
			Sheet sheet = targetExcelWB.getSheet(inCheckheetNames[sheetCount]);
			System.out.println("Inside1 For Loop of removeNonHeaders for " + inCheckheetNames[sheetCount]
					);
			System.out.println("Inside2 For Loop of removeNonHeaders for " + inCheckheetNames[sheetCount]
					+ ". sheet obj is '" + sheet + "'. sheet value is "  + sheet.getSheetName());
			System.out.println("Inside3 For Loop of removeNonHeaders for " + inCheckheetNames[sheetCount]
					+ " sheet top row is "  + sheet.getTopRow());
			System.out.println("Inside3 For Loop of removeNonHeaders for " + inCheckheetNames[sheetCount]
					+ " sheet LastRowNum is "  + sheet.getLastRowNum());

			System.out.println("Inside3 For Loop of removeNonHeaders for " + inCheckheetNames[sheetCount]
					+ ". sheet obj is '" + sheet + "'. sheet top row is "  + sheet.getTopRow());
			//removeRows(sheet, 1, sheet.getLastRowNum() - 1);
			removeRows(sheet, 1, sheet.getLastRowNum());
		}

		//FileOutputStream targetFileStream3 = new FileOutputStream(targetExcelFilePath);
		//targetExcelWB.write(targetFileStream3);
		//targetFileStream3.close();
		writeTargetWB();

	}

	public void cloneSheetsToNewWB(String[] inCheckheetNames) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		System.out.println("At cloneSheetsToNewWB for " + inCheckheetNames[0]
				+ " through " + inCheckheetNames[inCheckheetNames.length - 1]);
		// write the sourceWB into target file as is
		FileOutputStream targetFileStream1 = new FileOutputStream(
				targetExcelFilePath);
		srcExcelWB.write(targetFileStream1); // write the current sourceWB
												// into targetWB as is
		targetFileStream1.close();
		srcExcelWB.close();

		// read the target file in inputmode to build targetWB
		FileInputStream srcFileStream2 = new FileInputStream(
				targetExcelFilePath);
		targetExcelWB = new XSSFWorkbook(srcFileStream2); // read for
															// existing
															// content
		XSSFFormulaEvaluator.evaluateAllFormulaCells(targetExcelWB);	// ensure to refresh cache before cloning
		srcFileStream2.close();

		System.out.println("at cloneSheetsToNewWB V1.1 " + inCheckheetNames[0] + " is " + targetExcelWB.getSheet(inCheckheetNames[0]));
		if (inCheckheetNames.length > 1) {
			System.out.println("at cloneSheetsToNewWB V1.2 " + inCheckheetNames[1] + " is " + targetExcelWB.getSheet(inCheckheetNames[1]));
		}
		
		// remove the sheets other than the input names
		// remove in descending order since the sheetnumber will get
		// decremented after each removal
		for (int sheetCount = targetExcelWB.getNumberOfSheets() - 1; sheetCount >= 0; sheetCount--) {
			System.out.println("checking for removal of sheet number "
					+ sheetCount + " which is "
					+ targetExcelWB.getSheetName(sheetCount));
			if (!commonTechs.isStringAvailableInArray(targetExcelWB
					.getSheetName(sheetCount), inCheckheetNames)) {
				System.out.println("removing the sheet at " + sheetCount + " which is "
						+ targetExcelWB.getSheetName(sheetCount));
				targetExcelWB.removeSheetAt(sheetCount);
			} else {
				System.out.println("skipped the removal of sheet at " + sheetCount + " which is "
						+ targetExcelWB.getSheetName(sheetCount));
			}
		}
		System.out.println("at cloneSheetsToNewWB V1.3 " + inCheckheetNames[0] + " is " + targetExcelWB.getSheet(inCheckheetNames[0]));
		
		System.out.println("now going to write the sheet cloned wb into " + targetExcelFilePath);
		System.out.println("at cloneSheetsToNewWB A1.1 inCheckheetNames[0] is " + inCheckheetNames[0]);
		System.out.println("at cloneSheetsToNewWB A1.1 targetSheet is " + targetExcelWB.getSheet(inCheckheetNames[0]));

		// write the updated targetWB into target file
		removeAllFormulas((XSSFWorkbook) targetExcelWB);
		
		//FileOutputStream targetFileStream3 = new FileOutputStream(targetExcelFilePath);
		//targetExcelWB.write(targetFileStream3);
		//targetFileStream3.close();
		writeTargetWB();
		
		System.out.println("at cloneSheetsToNewWB A1.2 targetSheet is " + targetExcelWB.getSheet(inCheckheetNames[0]));
		
		System.out.println("file write successful into " + targetExcelFilePath);
	}
	
	
	private void removeCalcChain(XSSFWorkbook workbook) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//CalculationChain calcchain = workbook.getCalculationChain();
		//Method removeRelation = POIXMLDocumentPart.class.getDeclaredMethod("removeRelation", POIXMLDocumentPart.class); 
		//removeRelation.setAccessible(true); 
		//removeRelation.invoke(workbook, calcchain);
		
		CalculationChain calcchain = workbook.getCalculationChain();
		Method removeRelation = POIXMLDocumentPart.class.getDeclaredMethod("removeRelation", POIXMLDocumentPart.class); 
		removeRelation.setAccessible(false); 
		removeRelation.invoke(workbook, calcchain);
		
	}

	private void removeAllFormulas(XSSFWorkbook workbook) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		for (Sheet sheet : workbook) {
			for (Row row : sheet) {
				for (Cell cell : row) {
					XSSFCell xssfcell = (XSSFCell) cell;
					if (xssfcell.getCTCell().isSetF() && xssfcell.getCTCell().getF().getT() != STCellFormulaType.DATA_TABLE) {
						xssfcell.getCTCell().unsetF();
					}
				}
			}
		}
		//removeCalcChain(workbook);
	}	
}
