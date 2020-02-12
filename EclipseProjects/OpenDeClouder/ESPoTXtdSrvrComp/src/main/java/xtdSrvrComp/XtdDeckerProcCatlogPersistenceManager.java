package xtdSrvrComp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import espot.ArtifactKeyPojo;
import espot.ERLDownload;
import espot.ErrorHandler;
import espot.RootPojo;

public class XtdDeckerProcCatlogPersistenceManager extends XtdCatalogPersistenceManager {
	/*
	 * Extended catalog processes for extended decking handler
	 */
	public XtdDeckerProcCatlogPersistenceManager(RootPojo inRootPojo, XtdCommons inCommons, int inProcessMode)
			throws ClassNotFoundException {
		super(inRootPojo, inCommons, inProcessMode);
	}

	public synchronized  ArrayList<ERLGrouperParent> readUpdatedERLGrouperParents(String inRootNick, String inContentType) {
		ArrayList<ERLGrouperParent> dbERLGrouperParntList = new ArrayList<ERLGrouperParent>();
		ERLGrouperParent dbERLGrouperParent = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
			+ " erl.RootNick, "
			+ " erl.Relevance, "
			+ " erl.ArtifactName, "
			+ " erl.ContentType, "
			+ " coalesce(GP1.ChildTotal,0) as ChildTotal, "
			+ " coalesce(GP1.ParentUpdateTimeStamp,\"\") as ParentUpdateTimeStamp, "
			+ " coalesce(GP1.ParentStatus,\"\") as ParentStatus "
			+ " from  "
			+ catalogDBAliasPrefix + "ERLMaster erl "
			+ " left outer join "
			+ extdSrvrDBAliasPrefix + "GrouperParent GP1 "
			+ " on GP1.ParentRootNick = erl.RootNick "
			+ " and GP1.ParentRelevance = erl.Relevance "
			+ " and GP1.ParentArtifactName = erl.ArtifactName "
			+ " and GP1.ParentContentType = erl.ContentType "
			+ " where erl.RootNick = '" + inRootNick
			+ "' and erl.ContentType = '" + inContentType
			+ "' and exists (Select 1 from " + extdSrvrDBAliasPrefix + "GrouperParent GP2 "
			+ "    where GP2.ParentRootNick = erl.RootNick "
			+ "    and GP2.ParentRelevance = erl.Relevance "
			+ "    and GP2.ParentArtifactName = erl.ArtifactName "
			+ "    and GP2.ParentContentType = erl.ContentType "
			+ "    and (GP2.ParentUpdateTimeStamp is null or (GP2.ParentUpdateTimeStamp is not null and GP2.ParentUpdateTimeStamp < erl.UploadedTimeStamp))) "
			;
	
			System.out.println(queryString);
			
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				ArtifactKeyPojo tempArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("RootNick"), rs.getString("Relevance"), 
						rs.getString("ArtifactName"), rs.getString("ContentType"));
				dbERLGrouperParent = new ERLGrouperParent(
						tempArtifactKeyPojo,
						rs.getInt("ChildTotal"),
						rs.getString("ParentUpdateTimeStamp"),		
						rs.getString("ParentStatus")
				);
				dbERLGrouperParntList.add(dbERLGrouperParent);
				dbERLGrouperParent = null;
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager readUpdatedERLGrouperParents " + inRootNick + " " + inContentType, e);
		}
		return dbERLGrouperParntList;
	}

	public synchronized  ArrayList<ERLGrouperParent> readUpdatedGrouperChildnParents(String inRootNick, String inContentType) {
		System.out.println("readUpdatedGrouperChildnParents inContentType " +  inContentType);
		ArrayList<ERLGrouperParent> dbUpdatedGrouperChildnParentsList = new ArrayList<ERLGrouperParent>();

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
			+ " GP.ParentRootNick, "
			+ " GP.ParentRelevance, "
			+ " GP.ParentArtifactName, "
			+ " GP.ParentContentType, "
			+ " coalesce(GP.ChildTotal,0) as ChildTotal, "
			+ " coalesce(GP.ParentUpdateTimeStamp,\"\") as ParentUpdateTimeStamp, "
			+ " coalesce(GP.ParentStatus,\"\") as ParentStatus, "
			+ " GC.ChildRootNick, "
			+ " GC.ChildRelevance, "
			+ " GC.ChildArtifactName, "
			+ " GC.ChildContentType, "
			+ " GC.ChildNumber, "
			+ " GC.ChildStatus, "
			+ " GC.ChildUpdateTimeStamp "
			+ " from  "
			+ extdSrvrDBAliasPrefix + "GrouperParent GP, "
			+ extdSrvrDBAliasPrefix + "GrouperChild GC "
			+ " where GP.ParentRootNick = GC.ParentRootNick "
			+ " and GP.ParentRelevance = GC.ParentRelevance "
			+ " and GP.ParentArtifactName = GC.ParentArtifactName "
			+ " and GP.ParentContentType = GC.ParentContentType "
			+ " and GP.ParentRootNick = '" + inRootNick
			+ "' and GP.ParentContentType = '" + inContentType
			+ "' and GC.ChildStatus in ('" + ExtendedChildPojo.CHILD_UPDATED + "')" 
			+ " order by GP.ParentRootNick, "
			+ " GP.ParentRelevance, "
			+ " GP.ParentArtifactName, "
			+ " GP.ParentContentType "
			;
	
			System.out.println("mistry query is " + queryString);
			
			ResultSet rs = statement.executeQuery(queryString);

			ArtifactKeyPojo prevPrntArtifactKeyPojo = null;
			ERLGrouperParent dbUpdatedGrouperChildnParent = null;
			
			while (rs.next()) {
				ArtifactKeyPojo parentArtifactKeyPojo = new ArtifactKeyPojo (
						rs.getString("ParentRootNick"), rs.getString("ParentRelevance"), 
						rs.getString("ParentArtifactName"), rs.getString("ParentContentType"));
				System.out.println("at readUpdatedGrouperChildnParents mistry GC.ChildStatus = " + rs.getString("ChildStatus"));

				System.out.println("11 readUpdatedGrouperChildnParents parentArtifactKeyPojo.artifactName " +  parentArtifactKeyPojo.artifactName);
				if (prevPrntArtifactKeyPojo!=null) {
					System.out.println("22 readUpdatedGrouperChildnParents prevPrntArtifactKeyPojo.artifactName " +  prevPrntArtifactKeyPojo.artifactName);
				}

				if (parentArtifactKeyPojo.isDiffArtifact(prevPrntArtifactKeyPojo)) {
					prevPrntArtifactKeyPojo = parentArtifactKeyPojo;
					dbUpdatedGrouperChildnParent = new ERLGrouperParent(
														parentArtifactKeyPojo,
														rs.getInt("ChildTotal"),
														rs.getString("ParentUpdateTimeStamp"),		
														rs.getString("ParentStatus"));
					dbUpdatedGrouperChildnParentsList.add(dbUpdatedGrouperChildnParent);
				}
				ArtifactKeyPojo rowChildArtifactKeyPojo = new ArtifactKeyPojo (
											rs.getString("ChildRootNick"),
											rs.getString("ChildRelevance"), 
											rs.getString("ChildArtifactName"),
											rs.getString("ChildContentType"));

				ExtendedChildPojo extendedChildPojo = new ExtendedChildPojo (
										rowChildArtifactKeyPojo,
										parentArtifactKeyPojo,
										rs.getInt("ChildNumber"), 
										rs.getString("ChildStatus"),
										rs.getString("ChildUpdateTimeStamp"));

				dbUpdatedGrouperChildnParent.addChild(extendedChildPojo);
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager readUpdatedGrouperChildnParents " + inRootNick + " " + inContentType, e);
		}
		return dbUpdatedGrouperChildnParentsList;
	}

	public synchronized  ArrayList<ERLDownload> readForSubscribingArrivedDeckerChildren(String inRootNick) {
		System.out.println("At start of readForSubscribingArrivedDeckerChildren inRootNick " +  inRootNick);
		ArrayList<ERLDownload> dbChildrenERLDownload = new ArrayList<ERLDownload>();
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT " 
				+ " erl.RootNick, "
				+ " erl.Relevance, "
				+ " erl.ArtifactName, "
				+ " erl.ContentType, "
				+ " Requestor, "
				+ " coalesce(Author,\"\") as Author,"
				+ " coalesce(usr.LeadID,\"\") as LeadID, "				
				+ " coalesce(ContentFileName,\"\") as ContentFileName, "
				+ " coalesce(ReviewFileName,\"\") as ReviewFileName, "
				+ " erl.ERLStatus, "
				+ " coalesce(UploadedTimeStamp,\"\") as UploadedTimeStamp, "
				+ " coalesce(ReviewTimeStamp,\"\") as ReviewTimeStamp, "
				+ " coalesce(ct.HasSpecialHandler,\"\") as HasSpecialHandler, "
				+ " coalesce(ct.AutoTriggered,\"\") as AutoTriggered, "		
				+ " coalesce(ct.Personified,\"\") as Personified, "				
				+ " coalesce(SubscriptionStatus,\"\") as SubscriptionStatus, "
				+ " coalesce(sub.DownLoadedFile,\"\") as DownLoadedFile, "
				+ " coalesce(sub.DownLoadedReviewFile,\"\") as DownLoadedReviewFile, "
				+ " coalesce(DownLoadedArtifactTimeStamp,\"\") as DownLoadedArtifactTimeStamp, "
				+ " coalesce(DownLoadedReviewTimeStamp,\"\") as DownLoadedReviewTimeStamp, "
				+ " case when pr.Relevance is not null then 1 "
				+ "  	 else 0 end as RelevancePicked "
				+ " from "
				+ catalogDBAliasPrefix + "ERLMaster erl, "
				+ extdSrvrDBAliasPrefix + "GrouperChild GC, "
				+ sysDBAliasPrefix + "ContentTypes ct "
				+ " left outer join "
				+ catalogDBAliasPrefix + " Users usr "
				+ " on usr.RootSysLoginID = erl.Author "
				+ " left outer join Subscriptions sub "
				+ " on sub.RootNick = erl.RootNick "
				+ " and sub.Relevance = erl.Relevance "
				+ " and sub.ArtifactName = erl.ArtifactName "
				+ " and sub.ContentType = erl.ContentType "
				+ " left outer join PickedRelevance pr "
				+ " on pr.RootNick = erl.RootNick "
				+ " and pr.Relevance = erl.Relevance "
				+ " where erl.RootNick = GC.ChildRootNick "
				+ " and erl.Relevance = GC.ChildRelevance "
				+ " and erl.ArtifactName = GC.ChildArtifactName "
				+ " and erl.ContentType = GC.ChildContentType "
				+ " and erl.RootNick = '" + inRootNick
				+ "' and ct.ContentType = erl.ContentType "
				+ " and erl.UploadedTimeStamp > GC.ChildUpdateTimeStamp "
				;	
			System.out.println(queryString);
			
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				ERLDownload dbERLDownload = new ERLDownload(
						new ArtifactKeyPojo(rs.getString("RootNick"),
											rs.getString("Relevance"),
											rs.getString("ArtifactName"),
											rs.getString("ContentType")),
						rs.getString("Requestor"), 
						rs.getString("Author"),						
						rs.getString("LeadID"),
						rs.getBoolean("HasSpecialHandler"),
						rs.getBoolean("AutoTriggered"),						
						rs.getBoolean("Personified"),
						rs.getString("ReviewFileName"), rs.getString("ERLStatus"),
						rs.getString("ContentFileName"),
						rs.getString("UploadedTimeStamp"), rs.getString("ReviewTimeStamp"),
						rs.getString("SubscriptionStatus"),		
						rs.getString("DownLoadedFile"),
						rs.getString("DownLoadedReviewFile"),
						rs.getString("DownLoadedArtifactTimeStamp"),
						rs.getString("DownLoadedReviewTimeStamp"),
						rs.getBoolean("RelevancePicked"));
				dbChildrenERLDownload.add(dbERLDownload);
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager readForSubscribingArrivedDeckerChildren " + inRootNick, e);			
		}
		return dbChildrenERLDownload;
	}
	
	public synchronized  void insertDeckerParent(ArtifactKeyPojo inParentArtifactKeyPojo, int inChildTotal, String inParentUpdateTimeStamp, String inParentStatus) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String insertString = "INSERT INTO " + extdSrvrDBAliasPrefix + "GrouperParent "
					+ " (ParentRootNick, "
					+ " ParentRelevance, "
					+ " ParentArtifactName, "
					+ " ParentContentType, "
					+ " ChildTotal, "
					+ " ParentUpdateTimeStamp, "
					+ " ParentStatus) "
					+ " VALUES ('" 
					+ inParentArtifactKeyPojo.rootNick + "', '" 
					+ inParentArtifactKeyPojo.relevance + "', '" 
					+ inParentArtifactKeyPojo.artifactName + "', '" 
					+ inParentArtifactKeyPojo.contentType + "', '" 
					+ inChildTotal + "', '" 
					+ inParentUpdateTimeStamp + "', '" 
					+ inParentStatus + "')";
			System.out.println(insertString);
			statement.executeUpdate(insertString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager insertDeckerParent(ArtifactKeyPojo " + inParentArtifactKeyPojo.artifactName + " " + inChildTotal + " " + inParentUpdateTimeStamp + " " + inParentStatus, e);
		}
	}

	public synchronized  void updateDeckerParent(ArtifactKeyPojo inArtifactKeyPojo, int inChildTotal, String inParentUpdateTimeStamp, String inParentStatus) { {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update " + extdSrvrDBAliasPrefix + "GrouperParent"
					+ " set ParentUpdateTimeStamp = '" + inParentUpdateTimeStamp + "',"
					+ " ParentStatus = '" + inParentStatus + "',"
					+ " ChildTotal = '" + inChildTotal + "'"					
					+ " where ParentRootNick = '" + inArtifactKeyPojo.rootNick + "'"
					+ " and ParentRelevance = '" + inArtifactKeyPojo.relevance + "'"
					+ " and ParentArtifactName = '" + inArtifactKeyPojo.artifactName + "'"
					+ " and ParentContentType = '" + inArtifactKeyPojo.contentType + "'";

			System.out.println(updateString);

			statement.executeUpdate(updateString);

			} catch (SQLException e) {
				// if the error message is "out of memory",
				// it probably means no database file is found
				//System.err.println(e.getMessage());
				ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager updateDeckerParent " + inArtifactKeyPojo.artifactName + " " + inChildTotal + " " + inParentUpdateTimeStamp + " " + inParentStatus, e);
			}
		}
	}

	public synchronized  void insertDeckerChild(ArtifactKeyPojo inChildArtifactKeyPojo, ArtifactKeyPojo inParentArtifactKeyPojo, int inChildNumber, String inChildStatus, String inChildUpdateTimeStamp) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String insertString = "INSERT INTO " + extdSrvrDBAliasPrefix + "GrouperChild "
				+ " (ChildRootNick, "
				+ " ChildRelevance, "
				+ " ChildArtifactName, "
				+ " ChildContentType, "
				+ " ParentRootNick, "
				+ " ParentRelevance, "
				+ " ParentArtifactName, "
				+ " ParentContentType, "
				+ " ChildNumber, "
				+ " ChildStatus, "
				+ " ChildUpdateTimeStamp) "
				+ " VALUES ('" 
				+ inChildArtifactKeyPojo.rootNick + "', '" 
				+ inChildArtifactKeyPojo.relevance + "', '" 
				+ inChildArtifactKeyPojo.artifactName + "', '" 
				+ inChildArtifactKeyPojo.contentType + "', '" 
				+ inParentArtifactKeyPojo.rootNick + "', '" 
				+ inParentArtifactKeyPojo.relevance + "', '" 
				+ inParentArtifactKeyPojo.artifactName + "', '" 
				+ inParentArtifactKeyPojo.contentType + "', '"
				+ inChildNumber + "', '" 
				+ inChildStatus + "', '" 
				+ inChildUpdateTimeStamp + "') " 
				;
			System.out.println(insertString);
			statement.executeUpdate(insertString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager insertDeckerChild " + inParentArtifactKeyPojo.artifactName + inChildNumber + " " + inChildStatus + " " + inChildUpdateTimeStamp, e);
		}
	}

	public synchronized  void updateDeckerChildrenStatus(ArtifactKeyPojo inChildArtifactKeyPojo,String inChildStatus,String inChildUpdateTimeStamp) {
		// update the child status & the erl timestampfor all children with same artifact key
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update " + extdSrvrDBAliasPrefix + "GrouperChild"
					+ " set ChildStatus = '" + inChildStatus + "', "
					+ " ChildUpdateTimeStamp = '" + inChildUpdateTimeStamp + "' "
					+ " where "
					+ " ChildRootNick = '" + inChildArtifactKeyPojo.rootNick + "' and "
					+ " ChildRelevance = '" + inChildArtifactKeyPojo.relevance + "' and "
					+ " ChildArtifactName = '" + inChildArtifactKeyPojo.artifactName + "' and "
					+ " ChildContentType = '" + inChildArtifactKeyPojo.contentType + "' "
					;

			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager updateDeckerChildrenStatus " + inChildArtifactKeyPojo.artifactName + " " + inChildStatus + " " + inChildUpdateTimeStamp, e);
		}
	}

	public synchronized void updateDeckerChildStatus(ArtifactKeyPojo inChildArtifactKeyPojo,ArtifactKeyPojo inParentArtifactKeyPojo,String inChildStatus) {
		// update the child status for all children with same artifact key. To be used when new content arrives
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update " + extdSrvrDBAliasPrefix + "GrouperChild"
					+ " set ChildStatus = '" + inChildStatus + "' where "
					+ " ChildRootNick = '" + inChildArtifactKeyPojo.rootNick + "' and "
					+ " ChildRelevance = '" + inChildArtifactKeyPojo.relevance + "' and "
					+ " ChildArtifactName = '" + inChildArtifactKeyPojo.artifactName + "' and "
					+ " ChildContentType = '" + inChildArtifactKeyPojo.contentType + "' and "
					+ " ParentRootNick = '" + inParentArtifactKeyPojo.rootNick + "' and "
					+ " ParentRelevance = '" + inParentArtifactKeyPojo.relevance + "' and "
					+ " ParentArtifactName = '" + inParentArtifactKeyPojo.artifactName + "' and "
					+ " ParentContentType = '" + inParentArtifactKeyPojo.contentType + "' "
					;

			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager updateDeckerChildStatus " + inChildArtifactKeyPojo.artifactName + " " + inParentArtifactKeyPojo.artifactName + " " + inChildStatus, e);
		}
	}
	

	public synchronized void updateDeckerChild(ArtifactKeyPojo inChildArtifactKeyPojo, ArtifactKeyPojo inParentArtifactKeyPojo, int inChildERLCount, String inChildStatus) { 
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "update " + extdSrvrDBAliasPrefix + "GrouperChild"
					+ " set ChildERLCount = '" + inChildERLCount + "',"
					+ " ChildStatus = '" + inChildStatus + "' where "
					+ " ChildRootNick = '" + inChildArtifactKeyPojo.rootNick + "' and "
					+ " ChildRelevance = '" + inChildArtifactKeyPojo.relevance + "' and "
					+ " ChildArtifactName = '" + inChildArtifactKeyPojo.artifactName + "' and "
					+ " ChildContentType = '" + inChildArtifactKeyPojo.contentType + "' and "
					+ " ParentRootNick = '" + inParentArtifactKeyPojo.rootNick + "' and "
					+ " ParentRelevance = '" + inParentArtifactKeyPojo.relevance + "' and "
					+ " ParentArtifactName = '" + inParentArtifactKeyPojo.artifactName + "' and "
					+ " ParentContentType = '" + inParentArtifactKeyPojo.contentType + "'";

			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager updateDeckerChildStatus " + inChildArtifactKeyPojo.artifactName + " " + inParentArtifactKeyPojo.artifactName + " " + inChildERLCount + " " + inChildStatus, e);
		}
	}
	
	public synchronized boolean doesGrouperChildExist(ArtifactKeyPojo inChildArtifactKeyPojo, ArtifactKeyPojo inParentArtifactKeyPojo) {
		boolean grouperChildExists = false;
		
		try {
				if (connection == null || statement == null) {
					createConnectionAndStatment();
				}
				String queryString = "select 1 from " + extdSrvrDBAliasPrefix + "GrouperChild"
					+ " where ChildRootNick = '" + inChildArtifactKeyPojo.rootNick + "' and "
					+ " ChildRelevance = '" + inChildArtifactKeyPojo.relevance + "' and "
					+ " ChildArtifactName = '" + inChildArtifactKeyPojo.artifactName + "' and "
					+ " ChildContentType = '" + inChildArtifactKeyPojo.contentType + "' and "
					+ " ParentRootNick = '" + inParentArtifactKeyPojo.rootNick + "' and "
					+ " ParentRelevance = '" + inParentArtifactKeyPojo.relevance + "' and "
					+ " ParentArtifactName = '" + inParentArtifactKeyPojo.artifactName + "' and "
					+ " ParentContentType = '" + inParentArtifactKeyPojo.contentType + "'";
	
				System.out.println(queryString);
	
				ResultSet rs = statement.executeQuery(queryString);
				while (rs.next()) {
						grouperChildExists = true;
				}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager doesGrouperChildExist " + inChildArtifactKeyPojo.artifactName + " " + inParentArtifactKeyPojo.artifactName, e);
		}
		return grouperChildExists;
	}
	
	public synchronized void deleteGrouperChildren (ArtifactKeyPojo inParentArtifactKeyPojo) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = "delete from " + extdSrvrDBAliasPrefix + "GrouperChild where"
					+ " ParentRootNick = '" + inParentArtifactKeyPojo.rootNick + "' and "
					+ " ParentRelevance = '" + inParentArtifactKeyPojo.relevance + "' and "
					+ " ParentArtifactName = '" + inParentArtifactKeyPojo.artifactName + "' and "
					+ " ParentContentType = '" + inParentArtifactKeyPojo.contentType + "'";

			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager deleteGrouperChildren " + inParentArtifactKeyPojo.artifactName, e);
		}
	}

	public synchronized void deleteSubscriptionsOfOldGroupers(String inRootNick) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String updateString = " Delete from Subscriptions where "
				+ " RootNick = '" + inRootNick + "' and "
				+ " Not Exists (Select 1 from " + extdSrvrDBAliasPrefix + "GrouperChild GrpChld where"
				+ "  GrpChld.ParentRootNick = RootNick and "
				+ "  GrpChld.ParentRelevance = Relevance and "
				+ "  GrpChld.ParentArtifactName = ArtifactName and "
				+ "  GrpChld.ParentContentType = ContentType) and "
				+ " Not Exists (Select 1 from " + extdSrvrDBAliasPrefix + "GrouperParent GrpPrt where "
				+ "  GrpPrt.ParentRootNick = RootNick and "
				+ "  GrpPrt.ParentRelevance = Relevance and "
				+ "  GrpPrt.ParentArtifactName = ArtifactName and "
				+ "  GrpPrt.ParentContentType = ContentType) ";
			System.out.println(updateString);

			statement.executeUpdate(updateString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager deleteSubscriptionsOfOldGroupers " + inRootNick, e);
		}
	}	

	public void neverCallMe_DeleteXtdDeckerProcessChildTbl() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String deleteString = "delete from " + extdSrvrDBAliasPrefix + "GrouperChild ";
			System.out.println(deleteString);
			statement.executeUpdate(deleteString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager neverCallMe_DeleteXtdDeckerProcessChildTbl ", e);
		}		
	}	

	public void neverCallMe_DeleteXtdDeckerProcessParentTbl() {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String deleteString = "delete from " + extdSrvrDBAliasPrefix + "GrouperParent ";
			System.out.println(deleteString);
			statement.executeUpdate(deleteString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdDeckerProcCatlogPersistenceManager neverCallMe_DeleteXtdDeckerProcessParentTbl ", e);
		}
	}
}