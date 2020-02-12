package xtdSrvrComp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;

import espot.Commons;

public class XtdCommons extends Commons {
	/*
	 * This class provides consolidates the commonly used extended functions to avoid redundancy.
	 * This class is kept as separate from Commons to provide factory instances
	 */	
	public String alltimesheetfile;	// for timesheet trigger

	public String[] xtdSrvrContentTypes;
	public String[] extendedSrvrRtNicks;
	private String xtdCatalogDbLocalFolder;
	private String xtdCatalogDbPrefix;
	public String xtdHandlersFolder;
	public String extdCtlgSrvrpropertiesFileName;
	public String extdSrvrProcessFolder;
	
	//public String timeAllocSheetname;
	//public int keyColumnNo;
	//public String inputDataType;

	public void readExtendedCatalogServerPrimaryProperties(String inExtdCtlgSrvrpropertiesFileName) throws IOException {
		System.out.println("xxx XtdCommons readExtendedCatalogServerPrimaryProperties called inXtdArg is " + inExtdCtlgSrvrpropertiesFileName);

		Properties extdCtlgSrvrPropObject = new Properties();
		extdCtlgSrvrpropertiesFileName = inExtdCtlgSrvrpropertiesFileName;
		InputStream propertiesStream = null;
		propertiesStream = new FileInputStream(extdCtlgSrvrpropertiesFileName);
		extdCtlgSrvrPropObject.load(propertiesStream);
		propertiesStream.close();		
		xtdCatalogSrvrFolder = extdCtlgSrvrPropObject.getProperty("extdSrvrFolder");
		System.out.println("xxx XtdCommons readExtendedCatalogServerPrimaryProperties ending after setting xtdCatalogSrvrFolder to " + xtdCatalogSrvrFolder);
	}
	
	public void readExtendedCatalogServerSecondaryProperties(String inExtdCtlgSrvrpropertiesFileName) throws IOException {

//		Properties extdCtlgSrvrPropObject = new Properties();
//		extdCtlgSrvrpropertiesFileName = inExtdCtlgSrvrpropertiesFileName;
//		InputStream propertiesStream = new FileInputStream(extdCtlgSrvrpropertiesFileName);
//		extdCtlgSrvrPropObject.load(propertiesStream);
//		propertiesStream.close();
		
		Properties extdCtlgSrvrPropObject = getPropertiesFromFile(extdCtlgSrvrpropertiesFileName);

		System.out.println("xxx extdCtlgSrvrPropObject getProperty on EXTENDED_CATLOG_SERVER_FOLDER is " + xtdCatalogSrvrFolder);
		
		userName = extdCtlgSrvrPropObject.getProperty("userName");
		xtdCatalogDbLocalFolder = installFileFolder + localFileSeparator + extdCtlgSrvrPropObject.getProperty("extededCatalogDbLocalFolder");
		xtdCatalogDbPrefix = extdCtlgSrvrPropObject.getProperty("extededCatalogDbPrefix");
		String xtdSrvrRootNicksText = extdCtlgSrvrPropObject.getProperty("extdSrvrRootNicks");
		extendedSrvrRtNicks = xtdSrvrRootNicksText.split(";");
		String extdSrvrContentTypesText = extdCtlgSrvrPropObject.getProperty("extdSrvrContentTypes");
		xtdSrvrContentTypes = extdSrvrContentTypesText.split(";");

		xtdHandlersFolder = configDataFolder + localFileSeparator + extdCtlgSrvrPropObject.getProperty("xtndHandlers");

		System.out.println("xxx extdCtlgSrvrPropObject getProperty on extdSrvrContentTypesText is " + extdSrvrContentTypesText);
		System.out.println("xxx extdCtlgSrvrPropObject getProperty on xtdHandlersFolder is " + xtdHandlersFolder);
		
		extdSrvrProcessFolder = installFileFolder + localFileSeparator + extdCtlgSrvrPropObject.getProperty("extdSrvrProcessFolder");
		alltimesheetfile = extdSrvrProcessFolder + localFileSeparator + extdCtlgSrvrPropObject.getProperty("alltimesheetfile");

		//timeAllocSheetname = extdCtlgSrvrPropObject.getProperty("timeallocsheetname");

		//System.out.println("extdCtlgSrvrPropObject getProperty on keyColumnNo is " + extdCtlgSrvrPropObject.getProperty("keyColumnNo"));
		
		//keyColumnNo = Integer.parseInt(extdCtlgSrvrPropObject.getProperty("keyColumnNo"));
		//inputDataType=extdCtlgSrvrPropObject.getProperty("inputdata");
		System.out.println("xx2 extdCtlgSrvrPropObject getProperty on extdSrvrContentTypesText is " + extdSrvrContentTypesText);
		System.out.println("xx2 extdCtlgSrvrPropObject getProperty on xtdSrvrContentTypes[0] is " + xtdSrvrContentTypes[0]);
	}

	public String getExtededCatalogDbFileOfRoot(String inRootNick){
		System.out.println("At XtdCommons getExtededCatalogDbFileOfRoot inRootNick = " + inRootNick);
		String extededCatalogDbFileOfRoot = xtdCatalogDbLocalFolder + File.separatorChar + xtdCatalogDbPrefix + inRootNick;
		System.out.println("extededCatalogDbLocalFolder = " + xtdCatalogDbLocalFolder);
		System.out.println("extededCatalogDbPrefix = " + xtdCatalogDbPrefix);
		System.out.println("extededCatalogDbFileOfRoot = " + extededCatalogDbFileOfRoot);
		return extededCatalogDbFileOfRoot;
	}
	
	public XtdCommons(int inProcessMode, String inProcessingRootNick, String inXtdArg) throws IOException, ParseException {
		super(inProcessMode,inProcessingRootNick,inXtdArg);
	}

	public String[] getHandlerJarNames() {
		String[] handlerJarNames = null;
		System.out.println("At XtdCommons getHandlerJarNames ");
		System.out.println("contentHandlersFolder = " + contentHandlersFolder);
		System.out.println("osHandlersFolder = " + osHandlersFolder);
		System.out.println("remoteAccessersFolder = " + remoteAccessersFolder);
		System.out.println("xtdHandlersFolder = " + xtdHandlersFolder);
		System.out.println("processMode = " + processMode);
		System.out.println("EXTENDED_CATALOG_SERVER = " + EXTENDED_CATALOG_SERVER);

		if (processMode == EXTENDED_CATALOG_SERVER) {
			handlerJarNames = getExdHandlerJar();
		} else {
			handlerJarNames = new String[] {contentHandlersFolder,osHandlersFolder,remoteAccessersFolder};
		}
		System.out.println("At commons handlerJarNames.length = " + handlerJarNames.length);
		return handlerJarNames;
	}

	public String[] getExdHandlerJar() {
		//overriding method from extended Commons for extended process
		System.out.println("calling getExdHandlerJar from XtdCommons ");

		return new String[] {contentHandlersFolder,osHandlersFolder,remoteAccessersFolder,xtdHandlersFolder};
	}
	
	public static XtdCommons getInstance(int inProcessMode, String inProcessingRootNick, String inXtdArg) throws IOException, ParseException{
		// The same method is duplicated in Commons & XtdCommons as no other choice. 
		// Any change here should be applied there as well
		XtdCommons requestedInstance = null;

		if (inProcessMode == EXTENDED_CATALOG_SERVER) {
			if (extendedCatalogServerCommonsInstance == null) {
				extendedCatalogServerCommonsInstance = new XtdCommons(inProcessMode,inProcessingRootNick,inXtdArg);
			}
			requestedInstance = (XtdCommons) extendedCatalogServerCommonsInstance;
		} else {
			System.out.println("Undefined inProcessMode at getInstance " + inProcessMode);
	    }
		System.out.println("xy1 extdCommons getProperty on requestedInstance xtdSrvrContentTypes 0 is " + requestedInstance.xtdSrvrContentTypes[0]);
		
		return requestedInstance;
	}
	
	public static XtdCommons getInstance(int inProcessMode) throws IOException, ParseException{
		XtdCommons requestedInstance = getInstance(inProcessMode,null,null);
		return requestedInstance;
	}	
}