package espot;

import java.io.IOException;
import java.util.HashMap;

import commonTechs.CustomClassLoader;

public class ContentHandlerManager {
	/*
	 * This is a singleton class that dynamically loads content handler classes and provides mapping
	 */
	private static ContentHandlerManager contentHanlderMgr = null;
	private Commons commons = null;
	private HashMap<String,ContentHandlerInterface> contentHandlerInterfaceMap = null;
	private HashMap<String,ContentHandlerSpecs> contentHandlerSpecsMap = null;

	private ContentHandlerManager(Commons inCommons, CatelogPersistenceManager inCatelogPersistenceManager){
		contentHandlerInterfaceMap = new HashMap<String,ContentHandlerInterface>();
		commons = inCommons;
		contentHandlerSpecsMap = inCatelogPersistenceManager.getContentHandlerSpecsMap();
	}
	
	public static synchronized ContentHandlerInterface getInstance(Commons inCommons, CatelogPersistenceManager inCatelogPersistenceManager, String inContentType){
		System.out.println("at ContentHandlerInterface getInstance inContentType = " + inContentType);
		if (contentHanlderMgr == null) {
			System.out.println("at ContentHandlerInterface getInstance21 inContentType = " + inContentType);
			contentHanlderMgr = new ContentHandlerManager(inCommons, inCatelogPersistenceManager);
			System.out.println("at ContentHandlerInterface getInstance45 inContentType = " + inContentType);
		}
		System.out.println("at ContentHandlerInterface getInstance inContentType67 = " + inContentType);
		return contentHanlderMgr.getContentHandlerInterface(inContentType);
	}

	private ContentHandlerInterface getContentHandlerInterface(String inContentType) {
		ContentHandlerInterface contentHandlerInterface = null;
		System.out.println("at start getContentHandlerInterface inContentType = " + inContentType);
		if (!contentHanlderMgr.contentHandlerInterfaceMap.containsKey(inContentType)){
			System.out.println("getContentHandlerInterface inContentType = " + inContentType);
			System.out.println("commons = " + commons);
			System.out.println("contentHandlerSpecsMap = " + contentHandlerSpecsMap);

			ClassLoader parentClassLoader1 = ContentHandlerManager.class.getClassLoader();
			System.out.println(" parentClassLoader1 pointer at getContentHandlerInterface is " + parentClassLoader1);
			System.out.println(" System.getenv(CLASSPATH) at getContentHandlerInterface is " + System.getenv("CLASSPATH"));
			System.out.println(" System.getProperty(CLASSPATH) at getContentHandlerInterface is " + System.getProperty("CLASSPATH"));

			try {
				contentHandlerInterface = (ContentHandlerInterface) CustomClassLoader.getInstance(
																	contentHandlerSpecsMap.get(inContentType).handlerClass,
																	commons.getHandlerJarNames());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in ContentHandlerManager getContentHandlerInterface " + inContentType, e);
			}
			contentHanlderMgr.contentHandlerInterfaceMap.put(inContentType,contentHandlerInterface);
		}
		System.out.println("at start getContentHandlerInterface12 inContentType = " + inContentType);
		contentHandlerInterface = contentHanlderMgr.contentHandlerInterfaceMap.get(inContentType);
		System.out.println("at start getContentHandlerInterface435 inContentType = " + inContentType);
		return contentHandlerInterface;
	}
}