package xtdSrvrComp;

import java.io.IOException;
import java.util.HashMap;

import commonTechs.CustomClassLoader;
import espot.CatelogPersistenceManager;
import espot.Commons;
import espot.ContentHandlerSpecs;
import espot.ErrorHandler;

public class XtdContntHandlerManager {
	/*
	 * Factory class to create extended content handler instances and provide references
	 */
	private static XtdContntHandlerManager xtdCntentHanlderMgr = null;
	private Commons commons = null;
	private HashMap<String,ExtendedHandler> xtndContntHandlerInterfaceMap = null;
	private HashMap<String,ContentHandlerSpecs> contentHandlerSpecsMap = null;

	private XtdContntHandlerManager(Commons inCommons, CatelogPersistenceManager inCatelogPersistenceManager){
		xtndContntHandlerInterfaceMap = new HashMap<String,ExtendedHandler>();
		commons = inCommons;
		contentHandlerSpecsMap = inCatelogPersistenceManager.getContentHandlerSpecsMap();
	}
	
	public static ExtendedHandler getInstance(Commons inCommons, CatelogPersistenceManager inCatelogPersistenceManager, String inContentType){
		if (xtdCntentHanlderMgr == null) {
			xtdCntentHanlderMgr = new XtdContntHandlerManager(inCommons, inCatelogPersistenceManager);
		}
		return xtdCntentHanlderMgr.getExtendedHandlerInterface(inContentType);
	}

	private ExtendedHandler getExtendedHandlerInterface(String inContentType) {
		ExtendedHandler extendedHandler = null;
		if (!xtdCntentHanlderMgr.xtndContntHandlerInterfaceMap.containsKey(inContentType)){
			System.out.println("getExtendedHandlerInterface inContentType = " + inContentType);
			try {
				extendedHandler = (ExtendedHandler) CustomClassLoader.getInstance(contentHandlerSpecsMap.get(inContentType).extdHandlerCls,commons.getHandlerJarNames());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in XtdContntHandlerManager getExtendedHandlerInterface " + inContentType, e);
			}
			xtdCntentHanlderMgr.xtndContntHandlerInterfaceMap.put(inContentType,extendedHandler);
		}
		extendedHandler = xtdCntentHanlderMgr.xtndContntHandlerInterfaceMap.get(inContentType);
		return extendedHandler;
	}
}