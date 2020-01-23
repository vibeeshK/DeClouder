package contentHandlers;

import java.util.Date;

public class AllocatedOneTaskPojo{
	/*
	 * Convenience class for holding each allocated task
	 * Note: this data resides within an allocated task item
	 */
	
	public String taskID = "";
	public String team = "";
	public String teamRelevance = "";
	public String description = "";
	public double timeEstimated = 0;	
	public Date expectedStart = null;
	public Date expectedEnd = null;
	
	public AllocatedOneTaskPojo(String inTaskID, String inTeam, String inTeamRelevance, String inDescription, double inTimeEstimated, Date inExpectedStart, Date inExpectedEnd) {
        absorbData(inTaskID, inTeam, inTeamRelevance, inDescription, inTimeEstimated, inExpectedStart, inExpectedEnd);
	}
	
	public void absorbData(String inTaskID, String inTeam, String inTeamRelevance, String inDescription, double inTimeEstimated, Date inExpectedStart, Date inExpectedEnd) {
		taskID = inTaskID;
		team = inTeam;
		teamRelevance = inTeamRelevance;
		description = inDescription;
		timeEstimated = inTimeEstimated;	
		expectedStart = inExpectedStart;
		expectedEnd = inExpectedEnd;
		teamRelevance = inTeamRelevance;
        System.out.println("After construction of AllocatedOneTaskPojo for taskID = " + taskID);				
	}
}