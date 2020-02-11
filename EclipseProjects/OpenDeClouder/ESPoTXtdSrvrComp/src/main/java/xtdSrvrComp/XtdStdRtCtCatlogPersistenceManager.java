package xtdSrvrComp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import espot.ArtifactKeyPojo;
import espot.ErrorHandler;
import espot.RootPojo;

public class XtdStdRtCtCatlogPersistenceManager extends XtdCatalogPersistenceManager {
	/*
	 * This class maintains the sql queries for the standard extended processes
	 */
	public XtdStdRtCtCatlogPersistenceManager(RootPojo inRootPojo, XtdCommons inCommons, int inProcessMode)
			throws ClassNotFoundException {
		super(inRootPojo, inCommons, inProcessMode);
	}

	public ArrayList<XtdStdProcessRecord> readUpdatedERLXtdStdProcessTbls(String inRootNick, String inContentType) {
		ArrayList<XtdStdProcessRecord> dbERLGrouperParntList = new ArrayList<XtdStdProcessRecord>();
		XtdStdProcessRecord dbXtdStdProcessRecd = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
			+ " erl.RootNick, "
			+ " erl.Relevance, "
			+ " erl.ArtifactName, "
			+ " erl.ContentType, "
			+ " coalesce(erl.UploadedTimeStamp,\"\") as UploadedTimeStamp, "
			+ " coalesce(GP1.XtdStdrdProcStatus,\"\") as XtdStdrdProcStatus "
			+ " from  "
			+ catalogDBAliasPrefix + "ERLMaster erl "
			+ " left outer join "
			+ extdSrvrDBAliasPrefix + "XtdStdProcessTbl GP1 "
			+ " on GP1.XtdStdrdProcRootNick = erl.RootNick "
			+ " and GP1.XtdStdrdProcRelevance = erl.Relevance "
			+ " and GP1.XtdStdrdProcArtifactName = erl.ArtifactName "
			+ " and GP1.XtdStdrdProcContentType = erl.ContentType "
			+ " where erl.RootNick = '" + inRootNick
			+ "' and erl.ContentType = '" + inContentType
			+ "' and ((exists (Select 1 from " + extdSrvrDBAliasPrefix + "XtdStdProcessTbl GP2 "
			+ "  			where GP2.XtdStdrdProcRootNick = erl.RootNick "
			+ "	    		and GP2.XtdStdrdProcRelevance = erl.Relevance "
			+ " 	   		and GP2.XtdStdrdProcArtifactName = erl.ArtifactName "
			+ "    			and GP2.XtdStdrdProcContentType = erl.ContentType "
//			+ "    			and (GP2.XtdStdrdProcUpdateTimeStamp < erl.UploadedTimeStamp " 
//			+ " 				or GP2.XtdStdrdProcStatus in ('" 
//			+ 					XtdStdProcessRecord.ERLRecord_NEW + "','" 
//			+ 					XtdStdProcessRecord.ERLRecord_UPDATED + "','" 
//			+ 					XtdStdProcessRecord.ERLRecord_CONTINUE + "'))))"
			+ "    			and (GP2.XtdStdrdProcUpdateTimeStamp < erl.UploadedTimeStamp " 
			+ " 				or GP2.XtdStdrdProcStatus in ('" 
			+ 					XtdStdProcessRecord.ERLRecord_NEW + "','" 
			+ 					XtdStdProcessRecord.ERLRecord_UPDATED + "'))))"
			+ " 	OR not exists (Select 1 from " + extdSrvrDBAliasPrefix + "XtdStdProcessTbl GP2 "
			+ "  		where GP2.XtdStdrdProcRootNick = erl.RootNick "
			+ "    		and GP2.XtdStdrdProcRelevance = erl.Relevance "
			+ "    		and GP2.XtdStdrdProcArtifactName = erl.ArtifactName "
			+ "    		and GP2.XtdStdrdProcContentType = erl.ContentType )) "
			;
	
			System.out.println(queryString);
			
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("Relevance"), 
						rs.getString("ArtifactName"), rs.getString("ContentType"));
				dbXtdStdProcessRecd = new XtdStdProcessRecord(
						tempArtifactKeyPojo,
						rs.getString("UploadedTimeStamp"),
						rs.getString("XtdStdrdProcStatus")
				);
				dbERLGrouperParntList.add(dbXtdStdProcessRecd);
				dbXtdStdProcessRecd = null;
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdStdRtCtCatlogPersistenceManager readUpdatedERLXtdStdProcessTbls" + inRootNick + " " + inContentType, e);
		}
		return dbERLGrouperParntList;
	}
	
	public void insertDeckerXtdStdrdProc(ArtifactKeyPojo inXtdStdrdProcArtifactKeyPojo, String inXtdStdrdProcUpdateTimeStamp, String inXtdStdrdProcStatus) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String insertString = "INSERT INTO " + extdSrvrDBAliasPrefix + "XtdStdProcessTbl "
				+ " (XtdStdrdProcRootNick, "
				+ " XtdStdrdProcRelevance, "
				+ " XtdStdrdProcArtifactName, "
				+ " XtdStdrdProcContentType, "
				+ " XtdStdrdProcUpdateTimeStamp, "
				+ " XtdStdrdProcStatus) "
				+ " VALUES ('" 
				+ inXtdStdrdProcArtifactKeyPojo.rootNick + "', '" 
				+ inXtdStdrdProcArtifactKeyPojo.relevance + "', '" 
				+ inXtdStdrdProcArtifactKeyPojo.artifactName + "', '" 
				+ inXtdStdrdProcArtifactKeyPojo.contentType + "', '" 
				+ inXtdStdrdProcUpdateTimeStamp + "', '" 
				+ inXtdStdrdProcStatus + "')";
			System.out.println(insertString);
			statement.executeUpdate(insertString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdStdRtCtCatlogPersistenceManager insertDeckerXtdStdrdProc(ArtifactKeyPojo inXtdStdrdProcArtifactKeyPojo" + " " + inXtdStdrdProcUpdateTimeStamp + " " + inXtdStdrdProcStatus, e);
		}
	}

	public void updateDeckerXtdStdrdProc(ArtifactKeyPojo inArtifactKeyPojo, String inXtdStdrdProcUpdateTimeStamp, String inXtdStdrdProcStatus) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}

			String updateString = "REPLACE INTO " + extdSrvrDBAliasPrefix + "XtdStdProcessTbl "
					+ " (XtdStdrdProcRootNick, "
					+ " XtdStdrdProcRelevance, "
					+ " XtdStdrdProcArtifactName, "
					+ " XtdStdrdProcContentType, "
					+ " XtdStdrdProcUpdateTimeStamp, "
					+ " XtdStdrdProcStatus) "
					+ " VALUES ('" 
					+ inArtifactKeyPojo.rootNick + "', '" 
					+ inArtifactKeyPojo.relevance + "', '" 
					+ inArtifactKeyPojo.artifactName + "', '" 
					+ inArtifactKeyPojo.contentType + "', '" 
					+ inXtdStdrdProcUpdateTimeStamp + "', '" 
					+ inXtdStdrdProcStatus + "')";

		System.out.println(updateString);

		statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdStdRtCtCatlogPersistenceManager updateDeckerXtdStdrdProc " + inArtifactKeyPojo.artifactName + " " + inXtdStdrdProcUpdateTimeStamp + " " + inXtdStdrdProcStatus, e);
		}
	}
	
	public void neverCallMe_DeleteXtdStdProcessTbl() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String deleteString = "delete from " + extdSrvrDBAliasPrefix + "XtdStdProcessTbl ";
			System.out.println(deleteString);
			statement.executeUpdate(deleteString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdStdRtCtCatlogPersistenceManager neverCallMe_DeleteXtdStdProcessTbl", e);
		}
	}
}