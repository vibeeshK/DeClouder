package xtdSrvrComp;

import java.sql.SQLException;

import espot.CatelogPersistenceManager;
import espot.ErrorHandler;
import espot.RootPojo;

public abstract class XtdCatalogPersistenceManager extends CatelogPersistenceManager {
	/*
	 * Maintains connections to the specified db files for extended processes
	 */
	String extdSrvrDBAlias;
	String extdSrvrDBAliasPrefix;

	public XtdCatalogPersistenceManager(RootPojo inRootPojo, XtdCommons inCommons, int inProcessMode)
			throws ClassNotFoundException {
		super(inRootPojo, inCommons, inProcessMode);
		
		System.out.println("XtdCatelogPersistenceManager begins for inMasterOrClient = " + processMode);

		extdSrvrDBAlias = "extdSrvrDBAlias";
		extdSrvrDBAliasPrefix = extdSrvrDBAlias + ".";

		createConnectionAndStatmentForXtdServer();
	}

	protected void createConnectionAndStatmentForXtdServer() {
		connectToExtdSrvrDb();
	}

	public void connectToExtdSrvrDb(){
		try {
			System.out.println("Im here connectToExtdSrvrDb");
			System.out.println("getExtededCatalogDbFileOfRoot: " + ((XtdCommons) commons).getExtededCatalogDbFileOfRoot(rootPojo.rootNick));
			
			String queryString = "Attach database '"
						+ ((XtdCommons) commons).getExtededCatalogDbFileOfRoot(rootPojo.rootNick)
						+ "' As " + extdSrvrDBAlias;

			System.out.println(queryString);

			statement.execute(queryString);
				
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdCatalogPersistenceManager connectToExtdSrvrDb", e);
		}
		return;
	}
}
