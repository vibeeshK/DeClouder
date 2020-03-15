package espot;

import java.util.HashMap;

public class CommonData {
	/*
	 * This class holds key data required by all process
	 */
	public static int PROC_STAT_OK = 0;
	public static int PROC_STAT_ERROR = 8;
	public static int PROC_STAT_WARN = 4;
	public int process_state = 0;
	boolean onlyCatalogDownloadAllowed = false;

	private Commons commons = null;
	private CatelogPersistenceManager catelogPersistenceManager = null;
	private HashMap<String,RootPojo> rootPojoMap = null;
	private HashMap<String, ContentHandlerSpecs> contentHandlerSpecsMap = null;
	private String[] contentTypes = null;
	private String[] rootNicks = null;
	private RootPojo currentRootPojo = null;
	private String[] relavances = null;
	private UsersHandler usersHandler = null;

	public static CommonData getInstance(Commons inCommons) {
		return new CommonData(inCommons);
	}

	public static CommonData getInstance(Commons inCommons, CatelogPersistenceManager inCatelogPersistenceManager) {
		return new CommonData(inCommons, inCatelogPersistenceManager);
	}

	protected String getCurrentRootNick() {
		return commons.getCurrentRootNick();
	}

	protected CommonData(Commons inCommons) {
		System.out.println("CommonData construction1 inCommons is " + inCommons);		
		System.out.println("CommonData construction1 inCommons rootNick is " + inCommons.getCurrentRootNick());		
		commons = inCommons;
		initBaseData();
		initOtherBaseData();
	}
	
	protected CommonData(Commons inCommons, CatelogPersistenceManager inCatelogPersistenceManager) {
		System.out.println("CommonData construction2 inCommons is " + inCommons);		
		System.out.println("CommonData construction2 inCommons rootNick is " + inCommons.getCurrentRootNick());		
		commons = inCommons;
		System.out.println("CommonData inCommons is " + inCommons);		
		System.out.println("CommonData inCatelogPersistenceManager is " + inCatelogPersistenceManager);		
		catelogPersistenceManager = inCatelogPersistenceManager;
		initOtherBaseData();
	}

	public void refresh() {
		initBaseData();
		initOtherBaseData();		
	}
	protected void initBaseData(){
		System.out.println("At initBaseData for commons.getCurrentRootNick() " + commons.getCurrentRootNick());
		catelogPersistenceManager = new CatelogPersistenceManager(PublishedRootsHandler.getPublishedRoots(commons).get(commons.getCurrentRootNick()),commons,
				commons.processMode);
	}	
	
	protected void initOtherBaseData(){
		System.out.println("initOtherBaseData catelogPersistenceManager is " + catelogPersistenceManager);

		contentHandlerSpecsMap = catelogPersistenceManager.getContentHandlerSpecsMap();
		rootPojoMap = PublishedRootsHandler.getPublishedRoots(commons);
		if (catelogPersistenceManager.tobeConnectedCatalogDbFile!=null || commons.processMode == Commons.BASE_CATALOG_SERVER){
			//IMPORTANT NOTE: this check is done to skip detailed processing before first time download of catalogdb

			setUsersHandler();			
		} else {
			System.out.println("At initOtherBaseData onlyCatalogDownloadAllowed at this point");		
			onlyCatalogDownloadAllowed = true;			
		}
	}	
	
	public RootPojo getCurrentRootPojo(){
		if (currentRootPojo == null){
			currentRootPojo = getRootPojoMap().get(getCurrentRootNick());		
		}
		return currentRootPojo;
	}

	public UsersHandler getUsersHandler(){
		if (usersHandler == null) {
			System.out.println("At commonData getUsersHandler usershandler is still not set, hence trying to set it now");			
			setUsersHandler();
		}
		System.out.println("At getUsersHandler userhandler is " + usersHandler);
		
		return usersHandler;
	}
	

	public void setUsersHandler(){		
		if (catelogPersistenceManager.tobeConnectedCatalogDbFile!=null || commons.processMode == Commons.BASE_CATALOG_SERVER){
			//IMPORTANT NOTE: this check is done to skipping detailed processing before first time download of catalogdb
			
			System.out.println("At initOtherBaseData trying to get the user handler");		
			
			usersHandler = UsersHandler.createInstance(catelogPersistenceManager,
					commons);
		} else {
			System.out.println("At CommonData setUsersHandler() usersHandler cannot be set as onlyCatalogDownloadAllowed at this point");
		}
	}

	public CatelogPersistenceManager getCatelogPersistenceManager(){
		return catelogPersistenceManager;
	}

	public Commons getCommons(){
		return commons;
	}

	public String[] getContentTypes() {
		if (contentTypes == null) {
			getContentHandlerSpecsMap();
			contentTypes = new String[contentHandlerSpecsMap.keySet().size()];
			contentHandlerSpecsMap.keySet().toArray(contentTypes);
		}
		return contentTypes;
	}

	public String[] getRootNicks() {
		if (rootNicks == null) {
			rootNicks = new String[rootPojoMap.keySet().size()];
			rootPojoMap.keySet().toArray(rootNicks);
		}
		return rootNicks;
	}
	
	public HashMap<String, ContentHandlerSpecs>  getContentHandlerSpecsMap() {
		return contentHandlerSpecsMap;	
	}
	
	public ContentHandlerSpecs getContentHandlerSpecs(String inContentType) {
		return contentHandlerSpecsMap.get(inContentType);
	}

	public HashMap<String,RootPojo> getRootPojoMap() {
		return rootPojoMap;	
	}
	
	public String[] getRelavances() {
		if (relavances == null) {
			relavances = getCatelogPersistenceManager().readAllRelevanceStrings();			
		}
		return relavances;
	}	
}