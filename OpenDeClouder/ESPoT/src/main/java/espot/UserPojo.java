package espot;

public class UserPojo {	
	final static int TEAMMEMBER_PRVLLEVEL = 10;
	final static int TEAMMLEADER_PRVLLEVEL = 30;
	final static int ADMIN_PRVLLEVEL = 90;
	
	public String shortId = "";
	public String employeeName = "";
	public String emailID = "";
	public int privilegeLevel = 0;

	public UserPojo(String inShortId, String inEmployeeName, String inEmailID, int inPrivilegeLevel){
		shortId = inShortId;
		employeeName = inEmployeeName;
		emailID = inEmailID;
		privilegeLevel = inPrivilegeLevel;
	}
	public String getDisplayString() {
		String displayString = employeeName + " (" + shortId + ")";
		return displayString;
	}

	public boolean hasTeamMemberPrivilege() {
		if (privilegeLevel >= TEAMMEMBER_PRVLLEVEL) {
			return true;
		}
		return false;
	}

	public boolean hasTeamLeaderPrivilege() {
		if (privilegeLevel >= TEAMMLEADER_PRVLLEVEL) {
			return true;
		}
		return false;
	}

	public boolean hasAdminPrivilege() {
		if (privilegeLevel >= ADMIN_PRVLLEVEL) {
			return true;
		}
		return false;
	}
}