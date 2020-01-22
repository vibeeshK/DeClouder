package xtdSrvrComp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import espot.ErrorHandler;
import espot.RootPojo;

public class XtdTmShCatlogPersistenceMgr extends XtdStdRtCtCatlogPersistenceManager {
	/*
	 * This class maintains the sqls for extended time tracking process
	 */
	
	public XtdTmShCatlogPersistenceMgr(RootPojo inRootPojo, XtdCommons inCommons, int inProcessMode)
			throws ClassNotFoundException {
		super(inRootPojo, inCommons, inProcessMode);
	}
	
	public void replaceTimeDetail(
			String inTaskID,
			String inRelevance,
			String inTeamID,
			String inUserID,
			String inDate,
			int inHoursBooked) {
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String replaceString = "REPLACE INTO " + extdSrvrDBAliasPrefix + "TimeDetail "
					+ " (TaskID, "
					+ " Relevance, "
					+ " TeamID, "
					+ " UserID, "
					+ " Date, "
					+ " HoursBooked) "
					+ " VALUES ('" 
					+ inTaskID + "', '" 
					+ inRelevance + "', '" 
					+ inTeamID + "', '" 
					+ inUserID + "', '" 
					+ inDate + "', '" 
					+ inHoursBooked + "')";
			System.out.println(replaceString);
			statement.executeUpdate(replaceString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdTmShCatlogPersistenceMgr replaceTimeDetail "
			+ inTaskID
			+ " " + inRelevance
			+ " " + inTeamID
			+ " " + inUserID
			+ " " + inDate
			+ " " + inHoursBooked, e);
		}
	}

	public String getFirstHrsEntryTimingOfTask(
			String inTaskID,
			String inRelevance,
			String inTeamID) {

		String earliestBookingDate = null;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = " select min(Date) as EarliestDate from  "
				+ extdSrvrDBAliasPrefix + "TimeDetail tm "					
				+ " where tm.TaskID = '" + inTaskID + "' "
				+ " and tm.Relevance = '" + inRelevance + "' "
				+ " and tm.TeamID = '" + inTeamID + "' "
				;

			System.out.println("QUERY for readEarliestDateBookingOfTask = "
					+ queryString);

			System.out.println("before query xc111sa1.2");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {
				earliestBookingDate = rs.getString("EarliestDate");
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdTmShCatlogPersistenceMgr getFirstHrsEntryTimingOfTask "
			+ inTaskID
			+ " " + inRelevance
			+ " " + inTeamID, e);
		}
		System.out.println("after reading readEarliestDateBookingOfTask");

		return earliestBookingDate;
	}
	
	public double readTimeDetailOfTask(
			String inTaskID,
			String inRelevance,
			String inTeamID) {
		double hoursBooked = 0;

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT coalesce(sum(HoursBooked),0) as SumHours from  "
				+ extdSrvrDBAliasPrefix + "TimeDetail tm "					
				+ " where tm.TaskID = '" + inTaskID + "' "
				+ " and tm.Relevance = '" + inRelevance + "' "
				+ " and tm.TeamID = '" + inTeamID + "' "
				;

			System.out.println("QUERY for readTimeDetailOfTask = "
					+ queryString);

			System.out.println("before query xc1111.2");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query");

			while (rs.next()) {
				hoursBooked = rs.getDouble("SumHours");
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			ErrorHandler.showErrorAndQuit(commons, "Error XtdTmShCatlogPersistenceMgr readTimeDetailOfTask "
			+ inTaskID
			+ " " + inRelevance
			+ " " + inTeamID, e);
		}
		System.out.println("after reading readTimeDetailOfTask");

		return hoursBooked;
	}
		
	public HashMap<String, Double> readEffortsOfUsersInTeam(String inRelevance, String inTeamID) {

		HashMap<String,Double> effortsOfUsersInTeam = new HashMap<String,Double>();

		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String queryString = "SELECT coalesce(sum(HoursBooked),0) as SumHours, UserID from  "
				+ extdSrvrDBAliasPrefix + "TimeDetail tm "					
				+ " where tm.Relevance = '" + inRelevance + "' "
				+ " and tm.TeamID = '" + inTeamID + "' "
				+ " Group By UserID ";

			System.out.println("QUERY for readTimeDetailOfTask = "
					+ queryString);

			System.out.println("before query readEffortsOfUsersInTeam ");

			ResultSet rs = statement.executeQuery(queryString);

			System.out.println("after query readEffortsOfUsersInTeam ");

			while (rs.next()) {
				Double hoursBooked = rs.getDouble("SumHours");
				String userID = rs.getString("UserID");				
				effortsOfUsersInTeam.put(userID, hoursBooked);
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			ErrorHandler.showErrorAndQuit(commons, "Error XtdTmShCatlogPersistenceMgr readEffortsOfUsersInTeam "
			+ " " + inRelevance
			+ " " + inTeamID, e);
		}

		System.out.println("after reading all readEffortsOfUsersInTeam");

		return effortsOfUsersInTeam;
	}
	
	public void neverCallMe_DeleteTimeDetail() {
		// TODO Auto-generated method stub
		try {
			if (connection == null || statement == null) {
				createConnectionAndStatment();
			}
			String deleteString = "delete from " + extdSrvrDBAliasPrefix + "TimeDetail ";
			System.out.println(deleteString);
			statement.executeUpdate(deleteString);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			//System.err.println(e.getMessage());
			ErrorHandler.showErrorAndQuit(commons, "Error XtdTmShCatlogPersistenceMgr neverCallMe_DeleteTimeDetail ", e);
		}
	}
}
