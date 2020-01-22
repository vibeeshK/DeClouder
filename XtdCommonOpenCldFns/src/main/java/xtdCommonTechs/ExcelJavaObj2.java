package xtdCommonTechs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ExcelJavaObj2 {
	/*
	 * This class acts as java replica of an excel sheet and provides means to seamlessly
	 * refer specific cell data with an indexing function
	 */

	public final static int PROCSTAT_GOOD = 0;
	public final static int PROCSTAT_WARNING = 4;
	public final static int PROCSTAT_ERROR = 8;
	public int processStatus;
	public String processStateMsg;

	private  ArrayList<ExcelColJavaObj> colObjsOfInterest;
	private ExcelColJavaObj keyColObj;
	private ArrayList<String> colHdrsOfInterest;
	private HashMap<String,Integer> shRowKeyPointers; //the excel row number stored agaist the key
	private ArrayList<String> rowObjKeyPointers;	//the keys are stored here in the same sequence as the rowObj's
	private ArrayList<Integer> rowObjShIndexes;	// physical row number of the object
												// IMPORTANT NOTE: column type will not be directly
												// set by the excelHandler while reading excel files.
												// It has to be manually set and used.
												// It is included here only for convenience.
	private ArrayList<ArrayList<Object>> xlObjRows;
	private ArrayList<Object> cachedXlObjRow;
	private int cachedXlObjRowNum = -1;
	
	public ExcelJavaObj2() {
		processStatus = 0;
		processStateMsg = "";

		xlObjRows = new ArrayList<ArrayList<Object>>();
		colObjsOfInterest = null;
		colHdrsOfInterest = null;
		keyColObj = null;
		shRowKeyPointers = null;
		rowObjKeyPointers = null;

	}

	//Get the existing row obj number or create a new one for the given row key and return the rownum
	//when sheet rownum unknown 
	public int getCreateXlObjRow(String inRowKey) {
		return getCreateXlObjRow(inRowKey, -1); // passing -1 as position as its unknown
	}

	//Get the existing row obj or create a new one for the given row key and return the row
	//when sheet rownum known 
	public int getCreateXlObjRow(String inRowKey, int shRowNum) {

		int keyRowObjLocation = getXlObjRowForKey(inRowKey);

		System.out.println("At getCreateXlObjRow inRowKey is " + inRowKey);
		System.out.println("At getCreateXlObjRow shRowNum is " + shRowNum);
		System.out.println("At getCreateXlObjRow keyRowObjLocation is " + keyRowObjLocation);
		System.out.println("At getCreateXlObjRow rowObjKeyPointers is " + rowObjKeyPointers);

		if (keyRowObjLocation == -1) {
			//Add a row to the end of the list and return the pointer to caller
			xlObjRows.add(new ArrayList<Object>(Collections.nCopies(colObjsOfInterest.size(),null))); 
			rowObjKeyPointers.add(inRowKey);
			rowObjShIndexes.add(shRowNum);
			keyRowObjLocation = rowObjKeyPointers.size()-1; // new key's location which is 0 based.
			shRowKeyPointers.put(inRowKey,shRowNum);	// as this row doesn't exist in excel, mark it as -1 so it will be added as a new row
		}
		System.out.println("At getCreateXlObjRow new keyRowObjLocation is " + keyRowObjLocation);
		System.out.println("At getCreateXlObjRow rowObjKeyPointers is " + rowObjKeyPointers);
		System.out.println("At getCreateXlObjRow rowObjKeyPointers at keyRowObjLocation is " + rowObjKeyPointers.get(keyRowObjLocation));

		return keyRowObjLocation;
	}
	
	public int getTotalDetailRows() {
		return xlObjRows.size();
	}
	
	public int getXlObjRowForKey(String inRowKey) {
		return rowObjKeyPointers.indexOf(inRowKey);
	}
	
	public void addColOfInterest(String inColHeaderName, int inXlColType) {
		if (colObjsOfInterest == null) {
			colObjsOfInterest = new ArrayList<ExcelColJavaObj>();
			colHdrsOfInterest = new ArrayList<String>();
		}
		ExcelColJavaObj xlColJavaObj = new ExcelColJavaObj(inColHeaderName,inXlColType,colHdrsOfInterest.size());
			// note the last arg i.e. the current size of collection would be the location of new object
		
		colObjsOfInterest.add(xlColJavaObj);
		colHdrsOfInterest.add(inColHeaderName);
		System.out.println("colHdrsOfInterest is now " + colHdrsOfInterest);
		System.out.println("colHdrsOfInterest last element is now " + colHdrsOfInterest.get(colHdrsOfInterest.size()-1));
	}

	public void initKeyColumn(String inKeyColHeaderName){
		//this method should be called only after setting the key's header via addColOfInterest
		keyColObj = new ExcelColJavaObj(inKeyColHeaderName,ExcelColJavaObj.COLTYPE_STRING, colHdrsOfInterest.indexOf(inKeyColHeaderName));
		shRowKeyPointers = new HashMap<String,Integer>();
		rowObjKeyPointers = new ArrayList<String>();
		rowObjShIndexes = new ArrayList<Integer>();		
	}
	
	public Object getColValAtObjRowNumFor(int inObjRowNum, String inColHeader){
		// Get the cell value of the mentioned column for the given rownum of xlJavaObj
		// Note: The obj rows dont include header row

		if (inObjRowNum != cachedXlObjRowNum) {
		// caching done to improve performance during back to back reading on same row
			cachedXlObjRowNum = inObjRowNum;
			cachedXlObjRow = xlObjRows.get(cachedXlObjRowNum);
		}
		return cachedXlObjRow.get(colHdrsOfInterest.indexOf(inColHeader));
	}

	public void setColValAtObjRowNumAndObjColNum(int inObjRowNum, int inObjColNum, Object inCellValue){
		if (inObjRowNum != cachedXlObjRowNum) {
		// caching done to improve performance during back to back reading on same row
			cachedXlObjRowNum = inObjRowNum;
			cachedXlObjRow = xlObjRows.get(cachedXlObjRowNum);
		}
		cachedXlObjRow.set(inObjColNum, inCellValue);
	}
	
	public Object getColValAtObjRowNumAndObjColNum(int inObjRowNum, int inObjColNum){
		if (inObjRowNum != cachedXlObjRowNum) {
		// caching done to improve performance during back to back reading on same row
			cachedXlObjRowNum = inObjRowNum;
			cachedXlObjRow = xlObjRows.get(cachedXlObjRowNum);
		}
		return cachedXlObjRow.get(inObjColNum);		
	}

	public int getShNumOfObjRowNum(int inObjRowNum){
		return rowObjShIndexes.get(inObjRowNum);
	}
	
	public void setColValAtObjRowNumFor(int inObjRowNum, String inColHeader, Object inCellValue){
		// Get the cell value of the mentioned column for the given rownum of xlJavaObj
		// Note: The obj rows dont include header row

		setColValAtObjRowNumAndObjColNum(inObjRowNum, colHdrsOfInterest.indexOf(inColHeader),inCellValue);
	}

	public int getShRowNumForKey(String inRowIDKey){
		return shRowKeyPointers.get(inRowIDKey);
	}

	public int getObjRowNumForKey(String inRowIDKey){
		return rowObjKeyPointers.indexOf(inRowIDKey);
	}
	
	public void setDetailShRowNumForKey(String inRowIDKey, int inShPhysicalRowNum){
		shRowKeyPointers.put(inRowIDKey, inShPhysicalRowNum);
	}
	
	public int getObjColSeqNumOfHdr(String inColHeader) {
		return colHdrsOfInterest.indexOf(inColHeader);
	}
	
	public int getShColSeqNumOfHdr(String inColHeader) {
		int objColSeqNum = getObjColSeqNumOfHdr(inColHeader);
		int shColSeqNum = -1;
		if (objColSeqNum != -1) {
			shColSeqNum = colObjsOfInterest.get(objColSeqNum).shColPosition;
		}
		return shColSeqNum;
	}
	
	public void setShColSeqNumForHdr(String inColHeader, int inShColSeqNum) {
		int objColSeqNum = getObjColSeqNumOfHdr(inColHeader);
		if (objColSeqNum != -1) {
			colObjsOfInterest.get(objColSeqNum).shColPosition = inShColSeqNum;
			if (inColHeader.equals(keyColObj.colHeaderName)) {
				keyColObj.shColPosition = inShColSeqNum;
			}
		}
	}
	
	public String getAnyUnfoundHeader() {
		String unfoundHdr = null;
		for (ExcelColJavaObj colObj : colObjsOfInterest) {
			if (colObj.shColPosition == -1) {
				unfoundHdr = colObj.colHeaderName;
				break;
			}
		}
		return unfoundHdr;
	}
	
	public int getKeyShColPosition() {
	// Gets the key column's position in the excel
		return keyColObj.shColPosition;
	}

	public int getKeyObjColPosition() {
		// Gets the key column's position in the java obj
		return keyColObj.rowObjColPosition;
	}
	
	public int getTotalObjCols() {
		return colHdrsOfInterest.size();
	}

	public int getShColNumOfObjCol(int inObjColNum) {
		return colObjsOfInterest.get(inObjColNum).shColPosition;
	}

	public int getColTypeOfObjCol(int inObjColNum) {
		return colObjsOfInterest.get(inObjColNum).xlColType;
	}
}