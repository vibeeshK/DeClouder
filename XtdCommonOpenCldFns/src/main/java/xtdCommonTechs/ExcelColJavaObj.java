package xtdCommonTechs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import commonTechs.CommonTechs;

public class ExcelColJavaObj {
	/*
	 * This class holds the details of an excel column's characteristics
	 */

	public final static int COLTYPE_NUMERIC = 5;
	public final static int COLTYPE_STRING = 10;
	public final static int COLTYPE_DATE = 15;
	public final static int COLTYPE_FORMULA = 20;

	public final static double INITVALUE_NUMERIC = 0;
	public final static String INITVALUE_STRING = "";

	public final static String INITVALUE_DATEONLYSTRING1900 = "19000101010101";
	public final static String INITVALUE_DATEONLYSTRING1904 = "19040101010101";
	public final static Date INITVALUE_DATE1900 = getInitDate(INITVALUE_DATEONLYSTRING1900);
	public final static Date INITVALUE_DATE1904 = getInitDate(INITVALUE_DATEONLYSTRING1904);
	
	public final static Date getInitDate(String inDate) {
        System.out.println("At getInitDate inDate = " + inDate);
        System.out.println("At getInitDate CommonTechs.SIMPL_DTEONLY_FORMAT = " + CommonTechs.SIMPL_DTE_FORMAT);
		Date initDate = null;
		try {
			initDate = (new SimpleDateFormat(CommonTechs.SIMPL_DTE_FORMAT)).parse(inDate);
		} catch (ParseException e) {
			e.printStackTrace();
			CommonTechs.logger.error("Date parse error in getInitDate of ExcelColJavaObj for inDate " + inDate, e);
			System.exit(8);
		}
        System.out.println("At getInitDate initDate = " + initDate);
		return initDate;
	}

	public String colHeaderName;
	public int xlColType;
	public int shColPosition;
	public int rowObjColPosition;

	public ExcelColJavaObj(String inColHeaderName,int inXlColType, int inRowObjColPosition) {
		colHeaderName = inColHeaderName;
		xlColType = inXlColType;
		rowObjColPosition = inRowObjColPosition;
		shColPosition = -1;	// col position will be set by the excel handler
	}
	
    public boolean equals(Object obj) { //overriding equals and hashcode functions for any sorting and indexing
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        
        System.out.println("At equals equals check for colHeaderName = " + colHeaderName);

        if (!getClass().isInstance(obj)) {
            return false;        	
        }
        ExcelColJavaObj other = (ExcelColJavaObj) obj;
        if (other.colHeaderName == null) return false;

        System.out.println("At equals equals check for other colHeaderName = " + other.colHeaderName);
        
        if (!colHeaderName.equalsIgnoreCase(other.colHeaderName)) return false;
        return true;
    }    
    
    public int hashCode() {	//overriding equals and hashcode functions for any sorting and indexing
        final int prime = 31;
        System.out.println("colHeaderName = " + colHeaderName);
        return prime + ((colHeaderName != null)? colHeaderName.hashCode() : 0);
    }
}