package commonTechs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


public class CommonTechs {
	/*
	 * Repository of commonly required functions
	 */

	public static final Logger logger = LogManager.getLogger("ESPoTLogger");
	public static String SIMPL_DTE_FORMAT = "yyyyMMddHHmmss";
	public static String SIMPL_DTEONLY_FORMAT = "yyyyMMdd";
	public static DocumentBuilder documentBuilder = null;
	public static Gson gson = null;
	
	public static String setUniqueSubfix(String inName, ArrayList<String> inOutCurrentNames, String inAppendStub){
		// Affixes the inName with the appendStub + an integer increment w.r.t inOutCurrentNames
		// and adds to the collection too (thanks to pass by reference of objects)

		// Being extra cautious as there is a open loop following
		if (inName == null || inOutCurrentNames == null || inAppendStub == null) return null;

		int subfixIncrement = 0;

		String fileExtension = getFileExtention(inName);
		String nameWithoutExtension = StringUtils.left(inName, inName.length()-fileExtension.length());

		
		String appendedName = String.copyValueOf(inName.toCharArray());	// note that direct EQ assignment would 
																		// have pointed to same object, but 
																		// we need independent objects
		System.out.println("At setUniqueSubfix ArrayList<String> inOutCurrentNames is " + inOutCurrentNames);

		
		
		while (true) {
			// looping until a unique name is set
			System.out.println("appendedName is " + appendedName);
			System.out.println("inOutCurrentNames size " + inOutCurrentNames.size());
			
			if (inOutCurrentNames.contains(appendedName)) {
				System.out.println("appendedName already present as " + appendedName);
				//appendedName = inName + inAppendStub + ++subfixIncrement;
				// increment and append a subfix until a new name is found
				appendedName = nameWithoutExtension + inAppendStub + ++subfixIncrement + fileExtension;
				System.out.println("new appendedName is " + appendedName);
				
			} else {
				inOutCurrentNames.add(appendedName);
				System.out.println("this appendedName added to the collection : " + appendedName);
				System.out.println("inOutCurrentNames size now is " + inOutCurrentNames.size());
				break;	// IMPORTANT BREAK STMT TO AVOID INFINITE LOOP ****
						// IMPORTANT BREAK STMT TO AVOID INFINITE LOOP ****
			}
		}
		System.out.println("unique appendedName is " + appendedName);
		return appendedName;
	}
	
	public static void removeAllExceptSpecified(HashMap<String, ArrayList<String>> inHashMap, String inKeepKey){
	    Iterator<HashMap.Entry<String, ArrayList<String>>> entryIterator = inHashMap.entrySet().iterator();
		System.out.println("At removeAllExceptSpecified ");

	    while (entryIterator.hasNext()) {
	        HashMap.Entry<String, ArrayList<String>> entry = entryIterator.next();
	        if(!inKeepKey.equalsIgnoreCase(entry.getKey())) {
	            entryIterator.remove();
	        }
	    }
	}

	public void copyFileUsingName(String source, String dest) throws IOException {
		if (!dest.equalsIgnoreCase(source)) {
			copyFileUsingApache(new File (source),new File(dest));
		}
	}
	
	public void moveFileUsingName(String source, String dest) throws IOException {
		FileUtils.moveFile(new File (source),new File(dest));
	}

	public void copyFileUsingApache(File source, File dest) throws IOException {
		FileUtils.copyFile(source, dest);
	}
	
	public void saveBytesIntoFile(byte[] inBytes, File outFile) throws IOException {
		createFolderOfFileIfDontExist(outFile);
		FileUtils.writeByteArrayToFile(outFile, inBytes);
	}

	public void saveBytesIntoNamedFile(byte[] inBytes, String outFileName) throws IOException {
		File outFile = new File(outFileName);
		saveBytesIntoFile(inBytes, outFile);		
	}

	public void copyFolderViaName(String srcDirString, String destDirString) throws IOException {
		if (!destDirString.equalsIgnoreCase(srcDirString)) {
			copyFolderUsingApache(new File(srcDirString), new File(destDirString));
		}
	}

	public void copyFolderUsingApache(File srcDir, File destDir) throws IOException {
		FileUtils.copyDirectory(srcDir, destDir);
	}
	
	public boolean deleteFile(String inFileName) {
		System.out.println("At deleteFile inFileName is " + inFileName);
		return FileUtils.deleteQuietly(new File(inFileName));
	}

	public boolean createFolderOfFileIfDontExist(File inFile) {
		File parentFolder = inFile.getParentFile();
	    if (!parentFolder.exists()) {
	    	return parentFolder.mkdirs();
	    }
	    return true;
	}

	public boolean createFolderOfFilePathIfDontExist(String inFilePath) {
		File fileOfFilePath = new File(inFilePath);
		return createFolderOfFileIfDontExist(fileOfFilePath);
	}

	public boolean createFolder(String inDirName) {
		File directory = new File(inDirName);
		return createFolderOfFileIfDontExist(directory);		
	}

	public String appendFolder(String inParentFolderAbsName, String inBrachingFolderName) {
		String appendedFolderName = inParentFolderAbsName + File.separator + inBrachingFolderName;
		System.out.println(" appendedFolderName = " + appendedFolderName);
		File newArtifactFolder = new File(appendedFolderName);
		newArtifactFolder.mkdir();
		return appendedFolderName;
	}

	public boolean doesFileExist(String inFileNameFile) {
		File file = new File(inFileNameFile);
		return file.exists();
	}

	public void copyFileUsingFileChannels(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;

		System.out.println("source file :::"
				+ source.getAbsolutePath());
		System.out.println("dest file:::"
				+ dest.getAbsolutePath());

		System.out.println("before copy file:::");
		if (source.getAbsolutePath().equalsIgnoreCase(
				dest.getAbsolutePath())) {
			System.out
					.println("copy skipped as source and destn both refert to same file");
			return;
		}
		System.out.println("dest.:"
				+ dest);
		System.out.println("dest.getParentFile():"
				+ dest.getParentFile());
		System.out.println("dest.getParentFile().exists():"
				+ dest.getParentFile().exists());
		createFolderOfFileIfDontExist(dest);

		inputChannel = new FileInputStream(source).getChannel();
		outputChannel = new FileOutputStream(dest).getChannel();
		outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

		inputChannel.close();
		outputChannel.close();
	}


	public byte[] getFilebyteDataFromFileName(String inFileStringWithFullPath)
			throws IOException {
		File fileObject = new File(inFileStringWithFullPath);
		FileInputStream localInputFileStream = new FileInputStream(fileObject);

		int len1 = (int) (fileObject.length());
		byte buf1[] = new byte[len1];
		localInputFileStream.read(buf1);
		return buf1;
	}

	public synchronized String getTimeStamp(Date inDate) {
		String timeStamp = null;
		if (inDate == null) { inDate = new Date(); }
		SimpleDateFormat simple_dte_format = new SimpleDateFormat(SIMPL_DTE_FORMAT);
		timeStamp = simple_dte_format.format(inDate);

		System.out.println("getTimeStamp inDate=" + inDate);
		System.out.println("getTimeStamp timeStamp=" + timeStamp);
		return timeStamp;
	}	

	public synchronized String getDateString() {
		// get date string in a simple date only format
		return getDateString(null);	
	}

	public synchronized String getDateString(Date inDate) {
		String dateOnlyString = null;
		if (inDate == null) { inDate = new Date(); }
		SimpleDateFormat simpl_dteonly_format = new SimpleDateFormat(SIMPL_DTEONLY_FORMAT);
		dateOnlyString = simpl_dteonly_format.format(inDate);

		System.out.println("date only =" + dateOnlyString);
		return dateOnlyString;
	}	

	public synchronized Date getDateOnly(Date inDate) throws ParseException {

		SimpleDateFormat simpl_dteonly_format = new SimpleDateFormat(SIMPL_DTEONLY_FORMAT);
		return simpl_dteonly_format.parse(simpl_dteonly_format.format(inDate));
	}

	public synchronized Date getDateFromDateOnlyString(String inDateOnlyString) throws ParseException {
		if (inDateOnlyString==null) {
			return null;
		}
		SimpleDateFormat simpl_dteonly_format = new SimpleDateFormat(SIMPL_DTEONLY_FORMAT);
		return simpl_dteonly_format.parse(inDateOnlyString);
	}

	public synchronized String getCalendarTS(Calendar inClndrDate) {
		String timeStamp = null;
		//timeStamp = SIMPL_DTE_FORMAT.format(inClndrDate);	// replaced by local variable
															// as it was not threadsafe

		SimpleDateFormat simple_dte_format = new SimpleDateFormat(SIMPL_DTE_FORMAT);
		timeStamp = simple_dte_format.format(inClndrDate);
		
		System.out.println("timeStamp=" + timeStamp);
		return timeStamp;
	}	

	public String getCurrentTimeStamp() {
		return getTimeStamp(null);
	}	

	public synchronized Date getDateTS() {
		Date DateTS = null;
		DateTS = new Date();
		System.out.println("DateTS=" + DateTS);
		return DateTS;
	}

	public synchronized Calendar getCalendarTS() {
		Calendar CalendarTS = null;
		CalendarTS = Calendar.getInstance();
		System.out.println("CalendarTS=" + CalendarTS);
		return CalendarTS;
	}

	public synchronized Calendar getCalendarDteFromDate(Date inDate) {
		Calendar calendarDte = null;
		calendarDte = Calendar.getInstance();
		calendarDte.setTime(inDate);
		System.out.println("CalendarTS=" + calendarDte);
		return calendarDte;
	}

	public boolean isThisLeftDateLater(Date inLeftDate, Date inRightDate){
		Calendar leftCalendardate = getCalendarDteFromDate(inLeftDate);
		Calendar rightCalendardate = getCalendarDteFromDate(inRightDate);
		return (leftCalendardate.compareTo(rightCalendardate) > 0);
	}

	public synchronized Date getDateFromString(String dateString) throws ParseException {
		
		Date dateFromString = null;
		if (dateString == null) return dateFromString;
		System.out.println(" getDateFromString input dateString = " + dateString);
		SimpleDateFormat simple_dte_format = new SimpleDateFormat(SIMPL_DTE_FORMAT);
		dateFromString = simple_dte_format.parse(dateString);

		System.out.println(" getDateFromString output dateFromString = " + dateFromString);
		return dateFromString;
	}

	public Date getDate(
			int inYear,
			int inMonth,
			int inDay,
			int inHour,
			int inMinute,
			int inSecond
	) throws ParseException{
		SimpleDateObj simpleDateObj = new SimpleDateObj(
													inYear,
													inMonth,
													inDay,
													inHour,
													inMinute,
													inSecond
													);
		return SimpleDateObj.getDate(simpleDateObj);
	}


	
	public synchronized boolean hasTimeSecElapsed(Date inStartTime, int inSec){
		System.out.println("hasTimeSecElapsed??");
		
		Calendar currentCalendrDate = getCalendarTS();
		Calendar startTimePlusGap 
							= getCalendarDteFromDate(inStartTime); 	// step 1 of 2
		startTimePlusGap.add(Calendar.SECOND,inSec);	// step 2 of 2

		System.out.println("startTimePlusGap : " + startTimePlusGap);
		System.out.println("currentCalendrDate : " + currentCalendrDate);

		if (currentCalendrDate.compareTo(startTimePlusGap) < 0){
			System.out.println("TimeSecElapsed is too short");
			return false;		// Gap is too short for a new download
		}
		System.out.println("Sec elapsed");
		return true;
	}

	public String getDocumentAsXml(Document doc)
			throws TransformerConfigurationException, TransformerException {
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();

		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		// we want to pretty format the XML output
		// note : this is broken in jdk1.5 beta!
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		//
		java.io.StringWriter sw = new java.io.StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		return sw.toString();
	}

	public String getFileNameFromFullPath(String inFullPath, String inFileSeperator) {
		return getLastNodeFromFullFilePath(inFullPath, inFileSeperator);
	}

	public String getLastNodeFromFullFilePath(String inFullPath, String inFileSeperator) {
		int lastNodeLocation = inFullPath.lastIndexOf(inFileSeperator);
		System.out.println("inFullPath = " + inFullPath);
		System.out.println("lastNodeLocation = " + lastNodeLocation);

		String outFileName = inFullPath.substring(lastNodeLocation + 1);
		System.out.println("outFileName = " + outFileName);

		return outFileName;
	}

	
	public String getFolderNameFromFullPath(String inFullPath) {
		File file = new File(inFullPath);
		return file.getParent();
	}

	public File getFileFromPathAndFile(String inDirectory, String inFileName) {
		File file = new File(inDirectory,inFileName);
		return file;
	}

	public String getAbsolutePathFromDirAndFileNm(String inDirectory, String inFileName) {
		File file = new File(inDirectory,inFileName);
		return file.getAbsolutePath();
	}

	public String getFileNameWithoutExtension(String inFullPath) {
		return FilenameUtils.removeExtension(inFullPath);
	}

	public void storeByteArrayIntoFile(byte[] myByteArray, String inTargetFileName) throws IOException {
		FileUtils.writeByteArrayToFile(new File(inTargetFileName), myByteArray);
	}
	
	public void storeInStream(InputStream inInputStream, String inTargetFileName) throws IOException {

		OutputStream outputStream = null;

		File file = new File(inTargetFileName);

		System.out.println("file try1 inTargetFileName = " + inTargetFileName);

		if (!file.exists()) {
			createFolderOfFileIfDontExist(file);
			file.createNewFile();
			System.out.println("file created111....");
			System.out.println("outPutFileName = " + inTargetFileName);
		}
		System.out.println("file try3 ....");

		// read this file into InputStream
		outputStream = new FileOutputStream(file);
		int read = 0;
		byte[] bytes = new byte[1024];

		System.out.println("reading....");
		while ((read = inInputStream.read(bytes)) != -1) {
			System.out.println("writing....");
			outputStream.write(bytes, 0, read);
			System.out.println("reading....");
		}
		System.out.println("Done!");


		if (inInputStream != null) {
			inInputStream.close();
		}
		if (outputStream != null) {
			outputStream.close();
		}
	}

	public static String getFileExtention(String inFileName) {
		System.out.println("extension find: infile = " + inFileName);
		String fileExtension = "";
		int extensionLocation = inFileName.lastIndexOf(".");

		System.out.println("extensionLocation = " + extensionLocation);

		if (extensionLocation > -1) {
			fileExtension = inFileName.substring(extensionLocation);
		}
		if (fileExtension.length() > 6) {
			// last nodes which are longer than 5 chars maynot be real extensions
			fileExtension = "";
		}
		System.out.println("fileExtension = " + fileExtension);
		return fileExtension;
	}

	public String getFileNameFromURL(String inFileName, String inFileSeparator) {

		System.out.println("getFileNameFromURL: infile = " + inFileName);
		String fileNameFromURL = "";
		int fileNameLocation = inFileName.lastIndexOf(inFileSeparator);

		System.out.println("fileNameLocation = " + fileNameLocation);

		if (fileNameLocation > 0) {
			fileNameFromURL = inFileName.substring(fileNameLocation + 1);
		} else if (fileNameLocation == -1 && !inFileName.equalsIgnoreCase("")) {
			fileNameFromURL = inFileName;
		}

		System.out.println("fileNameFromURL = " + fileNameFromURL);

		return fileNameFromURL;
	}

	public String getFolderNameFromURL(String inFileName, String inFileSeparator) {

		System.out.println("getFolderNameFromURL infile = " + inFileName);
		String fileNameFromURL = "";
		int fileNameLocation = inFileName.lastIndexOf(inFileSeparator);

		System.out.println("fileNameLocation = " + fileNameLocation);

		if (fileNameLocation > 0) {
			fileNameFromURL = inFileName.substring(0,fileNameLocation);
		} else if (fileNameLocation == -1 && !inFileName.equalsIgnoreCase("")) {
			fileNameFromURL = inFileName;
		}

		System.out.println("fileNameFromURL = " + fileNameFromURL);

		return fileNameFromURL;
	}


	public String getHostName(String inURLName) {
		String hostName;
		int left = inURLName.indexOf("/", 9);	// considering the prefix http:// and https:// and at least one char beyond for valid url
		if (left == -1) {	// in case the url didn't have any additional folder ref
			left = inURLName.length();
		}
		hostName = inURLName.substring(0, left);
		return hostName;
	}

	public static Document getNewDocument() throws ParserConfigurationException {
		createDocumentBuilder();
		return documentBuilder.newDocument();
	}	

	public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		if (documentBuilder == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			documentBuilder = factory.newDocumentBuilder();
		}
		return documentBuilder;
	}
	
	public Document getDocumentFromXMLFileStream(InputStream inInputStream) throws SAXException, IOException, ParserConfigurationException {
		System.out.println("@getDocumentFromXMLFileStream inInputStream: .toString(): " + inInputStream.toString());

		System.out.println("@getDocumentFromXMLFileStream inInputStream:" + inInputStream);
		System.out.println("@getDocumentFromXMLFileStream 2inInputStream: .toString(): " + inInputStream.toString());

		Document doc = null;
		doc = createDocumentBuilder().parse(inInputStream);

        // get the first element
        Element element = doc.getDocumentElement();
        System.out.println("element attr 0" + element.getAttributes().item(0));
        System.out.println("element attr 1" + element.getAttributes().item(1));

        // get all child nodes
        NodeList nodes = element.getChildNodes();

        // print the text content of each child
        for (int i = 0; i < nodes.getLength(); i++) {
           System.out.println("node number " + i + " is " + nodes.item(i).getTextContent());
        }
		
		System.out.println("@getDocumentFromXMLFileStream doc.toString(): " + doc.toString());
		System.out.println("@getDocumentFromXMLFileStream doc: " + doc);
		System.out.println("@getDocumentFromXMLFileStream 2doc.toString(): " + doc.toString());
		System.out.println("@getDocumentFromXMLFileStream 2doc: " + doc);
		
		return doc;
	}

	public BufferedReader getReaderForFile(String inFileName) throws FileNotFoundException {
	   BufferedReader br = new BufferedReader(new FileReader(inFileName));
	   return br;
	}

	public BufferedWriter getWriterForFile(String inFileName) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(inFileName));
		return bw;
	}
	
	public Gson getGson(){
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}

	public Object sysGetXMLObjFromFile(String inFileName, Class inClass) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(inClass);    
        
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();    
        Object unMarshalledObject = jaxbUnmarshaller.unmarshal(new File(inFileName));    
		Object jsonDocObj = getGson().fromJson(getReaderForFile(inFileName), inClass);
		return unMarshalledObject;
	}
	
	public Object sysGetJsonDocObjFromFile(String inFileName, Class inClass) throws FileNotFoundException {
		Object jsonDocObj = getGson().fromJson(getReaderForFile(inFileName), inClass);
		System.out.println(" At sysGetJsonDocObjFromFile jsonDocObj is " + jsonDocObj);
		System.out.println(" At sysGetJsonDocObjFromFile inFileName is " + inFileName);
		System.out.println(" At sysGetJsonDocObjFromFile inClass is " + inClass);
		System.out.println(" jsonDocObj is " + jsonDocObj);
		return jsonDocObj;
	}

	public Object sysGetJsonDocObjFromInputStream(InputStream inputStream, Class inClass) throws UnsupportedEncodingException {
		JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
		Object jsonDocObj = getGson().fromJson(reader, inClass);
		return jsonDocObj;
	}

	public Object sysGetJsonDocObjFromString(String inJsonString, Class inClass) {
		Object jsonDocObj = getGson().fromJson(inJsonString, inClass);
		return jsonDocObj;
	}

	public String sysGetStringFromJsonObj(Object inJsonObj) {
		String jsonObjString = getGson().toJson(inJsonObj);
		return jsonObjString;
	}

	public byte[] sysGetBytesFromJsonObj(Object inJsonObj) {
		return sysGetStringFromJsonObj(inJsonObj).getBytes();
	}

	public void sysPutJsonDocObjToFile(String inFileName, Object inGsonDocObj) throws IOException {
		String jsonDocString = getGson().toJson(inGsonDocObj);
		System.out.println("gonna write json :: " + jsonDocString);
		System.out.println("object passed was :: " + inGsonDocObj);
		System.out.println("inFileName :: " + inFileName);
		BufferedWriter bw = getWriterForFile(inFileName);
		bw.write(jsonDocString);
		bw.close();
	}
	
	public InputStream sysGetJsonDocInStream(Object inGsonDocObj) throws IOException {
		String jsonDocString = getGson().toJson(inGsonDocObj);
		InputStream instr = new ByteArrayInputStream(jsonDocString.getBytes("UTF-8"));
		return instr;
	}
	
	public Document getDocumentFromXMLFile(String inFileName)
			throws SAXException, IOException, ParserConfigurationException {
		System.out.println("@getDocumentFromXMLFile inFileNamexml = " + inFileName);
		
		File fXmlFile = new File(inFileName);
		System.out.println("@getDocumentFromXMLFile fXmlFile.exists() = " + fXmlFile.exists());

	
		if (!fXmlFile.exists()) return null;

		System.out.println("@getDocumentFromXMLFile fXmlFile.exists() crossed " + fXmlFile.exists());
			
		System.out.println("inFileNamexml1233 = " + inFileName);

		Document doc = createDocumentBuilder().parse(fXmlFile);
		doc.getDocumentElement().normalize();
		System.out.println("Root element :"
				+ doc.getDocumentElement().getNodeName());
		return doc;
	}

	public InputStream getInputStreamOfXMLDoc(Document inXMLDoc) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		InputStream inputStreamOfXMLDoc;
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Result outputTarget = new StreamResult(outputStream);
		Source xmlSource = new DOMSource(inXMLDoc);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		inputStreamOfXMLDoc = new ByteArrayInputStream(outputStream.toByteArray());
	
		System.out.println("Final Document : " + inputStreamOfXMLDoc);
		return inputStreamOfXMLDoc;
	}
	

	public void saveXMLFileFromDocument(Document inDocument, String inFileName)
			throws SAXException, IOException, ParserConfigurationException,
			TransformerException {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(inDocument);
		createFolder(inFileName);
		StreamResult streamResult = new StreamResult(new File(inFileName));
		transformer.transform(domSource, streamResult);
		System.out.println("File saved to specified path!" + inFileName);
		return;
	}

	public String getLatestFileNameStringInFolder(String inFolder) {
		System.out.println("getLatestFileNameStringInFolder inFolder : " + inFolder);

		File folder = new File(inFolder);
		File[] listOfFiles = folder.listFiles();
		File latestFile = null;

		System.out.println("file count === " + listOfFiles.length);

		for (int i = 0; i < listOfFiles.length; i++) {
			System.out.println("latestFile " + latestFile);
			System.out.println("listOfFiles[" + i + "] === " + listOfFiles[i]);

			if (listOfFiles[i].isFile()) {
				System.out.println("this is file");

				if (latestFile == null || latestFile.compareTo(listOfFiles[i]) < 0) {
					System.out.println("and it is latest");
					latestFile = listOfFiles[i];
				}
			}
		}
		return latestFile.getAbsolutePath();
	}


	public void Zip(String inFolder, String outZipFile) throws IOException {
		int BUFFER = 2048;

		System.out.println("Zipping inFolder = " + inFolder);
		System.out.println("outZipFile = " + outZipFile);
		BufferedInputStream origin = null;
		FileOutputStream dest = new FileOutputStream(outZipFile);
		CheckedOutputStream checksum = new CheckedOutputStream(dest,
				new Adler32());
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				checksum));

		byte data[] = new byte[BUFFER];
		// get a list of files from current directory
		File f = new File(inFolder);
		String files[] = f.list();

		for (int i = 0; i < files.length; i++) {
			System.out.println("Adding: " + files[i]);
			String fullInputFileName = inFolder + File.separator + files[i];
			System.out.println("fullInputFileName: " + fullInputFileName);

			FileInputStream fi = new FileInputStream(fullInputFileName);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(files[i]);
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
		}
		out.close();
		System.out.println("checksum:" + checksum.getChecksum().getValue());
		checksum.close();
		dest.close();
		origin.close();	

	}

	public boolean isZipFile(String inFileFolderString) {
		return getFileExtention(inFileFolderString).equalsIgnoreCase(".zip");
	}

	
	public void UnZip(String inZipFile, String outFolder) throws IOException {
		int BUFFER = 2048;
		new File(outFolder).mkdir();
		
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(inZipFile);
		ZipInputStream zis = new ZipInputStream(
				new BufferedInputStream(fis));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			System.out.println("Extracting: " + entry);
			int count;
			byte data[] = new byte[BUFFER];
			// write the files to the disk
			FileOutputStream fos = new FileOutputStream(outFolder
					+ File.separator + entry.getName());
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
		}
		zis.close();
	}

	public String getDirectoryOfZipFile(String inZipFile) {
		String directoryOfZipFile = StringUtils.left(inZipFile, inZipFile.length() - 4);
		return directoryOfZipFile;
	}
	
	public int getIndexOfStringInArray(String inSearchString, String[] inStringArray) {
		int indexOfString = -1;
		for (int stringCount = 0; stringCount < inStringArray.length; stringCount++) {
			if (inSearchString.equals(inStringArray[stringCount])) {
				indexOfString = stringCount;
				break;
			}
		}
		return indexOfString;
	}
	
	public String[] getCombinedStringArray3(String[] inStringArray1, String[] inStringArray2, String[] inStringArray3) {
		String[] combinedStrArray = new String[inStringArray1.length+inStringArray2.length+inStringArray3.length];
		System.arraycopy(inStringArray1, 0, combinedStrArray, 0, inStringArray1.length);
		System.arraycopy(inStringArray2, 0, combinedStrArray, inStringArray1.length, inStringArray2.length);
		System.arraycopy(inStringArray3, 0, combinedStrArray, inStringArray1.length+inStringArray2.length, inStringArray3.length);
		return combinedStrArray;
	}
	
	public String[] getCombinedStringArray4(String[] inStringArray1, String[] inStringArray2, String[] inStringArray3, String[] inStringArray4) {
		String[] combinedStrArray = new String[inStringArray1.length+inStringArray2.length+inStringArray3.length+inStringArray4.length];
		System.arraycopy(inStringArray1, 0, combinedStrArray, 0, inStringArray1.length);
		System.arraycopy(inStringArray2, 0, combinedStrArray, inStringArray1.length, inStringArray2.length);
		System.arraycopy(inStringArray3, 0, combinedStrArray, inStringArray1.length+inStringArray2.length, inStringArray3.length);
		System.arraycopy(inStringArray4, 0, combinedStrArray, inStringArray1.length+inStringArray2.length+inStringArray3.length, inStringArray4.length);
		return combinedStrArray;
	}

	public String[] getCombinedStringArray5(String[] inStringArray1, String[] inStringArray2, String[] inStringArray3, String[] inStringArray4, String[] inStringArray5) {
		String[] combinedStrArray = new String[inStringArray1.length+inStringArray2.length+inStringArray3.length+inStringArray4.length+inStringArray5.length];
		System.arraycopy(inStringArray1, 0, combinedStrArray, 0, inStringArray1.length);
		System.arraycopy(inStringArray2, 0, combinedStrArray, inStringArray1.length, inStringArray2.length);
		System.arraycopy(inStringArray3, 0, combinedStrArray, inStringArray1.length+inStringArray2.length, inStringArray3.length);
		System.arraycopy(inStringArray4, 0, combinedStrArray, inStringArray1.length+inStringArray2.length+inStringArray3.length, inStringArray4.length);
		System.arraycopy(inStringArray5, 0, combinedStrArray, inStringArray1.length+inStringArray2.length+inStringArray3.length+inStringArray4.length, inStringArray5.length);
		return combinedStrArray;
	}
	
	public String[] splitStringIntoArray(String inString) {
		String[] splitStrings = StringUtils.split(inString);
		return splitStrings;
	}

	public String convertIntToString(int inInt) {
		String outString = Integer.toString(inInt);
		return outString;
	}

	public String convertDoubleToString(double inDouble) {
		String outString = Double.toString(inDouble);
		return outString;
	}
	
	public int convertStringToInt(String inString) {
		int outInt = Integer.valueOf(inString);
		return outInt;
	}

	public double convertStringToDouble(String inString) {
		Double outDouble = Double.valueOf(inString);
		return outDouble;
	}

	public boolean checkNumeric(String inString) {
		return StringUtils.isNumeric(inString);
	}

	public boolean isStringAvailableInArray(String inCheckString, String[] inStringSet){
		System.out.println("At isStringAvailableInArray for " + inCheckString);
		System.out.println("inStringSet is " + inCheckString);

		boolean stringPresent = false;
		if (inStringSet==null) {
			System.out.println("inStringSet is null " + inCheckString);
			return false;
		}
		System.out.println(" inStringSet.length " + inStringSet.length);
		for (int stringCnt=0;stringCnt<inStringSet.length;stringCnt++){
			System.out.println("stringCnt " + stringCnt + " inStringSet[] " + inStringSet[stringCnt]);
			if (inCheckString.equalsIgnoreCase(inStringSet[stringCnt])) {
				stringPresent = true;
				break;
			}
		}
		System.out.println("stringPresent " + stringPresent);
		return stringPresent;
	}

	public List<String> getStringListFromObjectList(List<?> inObjectList){
		System.out.println("getStringListFromObjectList received size = " + inObjectList.size());
		System.out.println("in Object = " + inObjectList);
		List<String> stringList = new ArrayList<String>(inObjectList.size());
		for (Object object : inObjectList) {
			stringList.add(object != null ? object.toString() : null);
			System.out.println("added object = " + object.toString());
		}		
		return stringList;
	}
	
	public boolean isWebURI(String inString) {
		boolean isWebURI = false;
		URI uri = null;
	    try {
	      uri = new URI(inString);
	      isWebURI = uri.getScheme().equals("http") || uri.getScheme().equals("https");
	    }
	    catch (Exception e) {
	        return false;
	    }
	    return isWebURI;
	}	
	
	public void setPropertyFileValue(String inPropertyStreamName, String inPropertyName, String inPropertyValue) throws IOException {
		Properties propUpdtObject = new Properties();
		// load a properties file
		InputStream	propertiesInStream = null;
		FileOutputStream propertiesOutStream;
		propertiesInStream = new FileInputStream(inPropertyStreamName);
		propUpdtObject.load(propertiesInStream);
		System.out.println("Before updating the prop stream :: " + inPropertyStreamName + " ; PropertyName :: " + inPropertyName + " = " + propUpdtObject.getProperty(inPropertyName));
		
		propUpdtObject.setProperty(inPropertyName,inPropertyValue); // ensure to load all the prop before any updates
		propertiesInStream.close();

		propertiesOutStream = new FileOutputStream(inPropertyStreamName);
		propUpdtObject.store(propertiesOutStream,null);

		System.out.println("After updating the prop stream :: " + inPropertyStreamName + " ; PropertyName :: " + inPropertyName + " = " + propUpdtObject.getProperty(inPropertyName));
	}
	
	public boolean delay(int inMilli) throws InterruptedException {
		boolean processStat = false;
		Thread.sleep(inMilli);
		processStat = true;
		return processStat;
	}
}