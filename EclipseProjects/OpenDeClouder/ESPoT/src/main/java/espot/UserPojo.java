package espot;

public class UserPojo {	
	public final static int TEAMMEMBER_PRVLLEVEL = 10;
	public final static int TEAMMLEADER_PRVLLEVEL = 30;
	public final static int ADMIN_PRVLLEVEL = 90;

	public final static String ACTIVESTAT_ACTIVE = "Active";
	public final static String ACTIVESTAT_INACTIVE = "Inactive";

	public final static String ADMIN_LEVEL_LIT = "Admin";
	public final static String TEAMMLEADER_LEVEL_LIT = "TeamLead";
	public final static String TEAMMEMBER_LEVEL_LIT = "TeamMember";
	
	public String shortId = "";
	public String userName = "";
	//public String emailID = "";
	public String rootSysLoginID = "";
	//public String userName = "";
	public String activeStatus = "";
	public int privilegeLevel = 0;

	public static int getPrivilegeLevelOfLit(String inPrivilegeLit) {
		int privilegeLevel = -1;
		if (inPrivilegeLit.equalsIgnoreCase(UserPojo.TEAMMEMBER_LEVEL_LIT)){
			privilegeLevel = TEAMMEMBER_PRVLLEVEL;
		} else if (inPrivilegeLit.equalsIgnoreCase(UserPojo.TEAMMLEADER_LEVEL_LIT)){
			privilegeLevel = TEAMMLEADER_PRVLLEVEL;
		} else if (inPrivilegeLit.equalsIgnoreCase(UserPojo.ADMIN_LEVEL_LIT)){
			privilegeLevel = ADMIN_PRVLLEVEL;
		}
		return privilegeLevel;		
	}

	public static String getPrivilegeLitOfLevel(int inPrivilegeLevel) {
		String usersPrivilegeTx = "";
		if (inPrivilegeLevel == UserPojo.TEAMMEMBER_PRVLLEVEL){
			usersPrivilegeTx = UserPojo.TEAMMEMBER_LEVEL_LIT;
		} else if (inPrivilegeLevel == UserPojo.TEAMMLEADER_PRVLLEVEL){
			usersPrivilegeTx = UserPojo.TEAMMLEADER_LEVEL_LIT;
		} else if (inPrivilegeLevel == UserPojo.ADMIN_PRVLLEVEL){
			usersPrivilegeTx = UserPojo.ADMIN_LEVEL_LIT;
		}
		return usersPrivilegeTx;
	}

	public UserPojo(){		
	}

	public UserPojo(String inShortId, String inEmployeeName, String inEmailID, int inPrivilegeLevel, String inActiveStatus){
		shortId = inShortId;
		userName = inEmployeeName;
		rootSysLoginID = inEmailID;
		privilegeLevel = inPrivilegeLevel;
		activeStatus = inActiveStatus;
	}
	
	public String getDisplayString() {
		String displayString = userName + " (" + shortId + ")";
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
	
	public boolean isActive() {
		if (activeStatus!= null && activeStatus.equalsIgnoreCase(ACTIVESTAT_ACTIVE)) {
			return true;
		}
		return false;
	}
	
}