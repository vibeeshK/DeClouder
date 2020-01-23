package espot;

public class ContentInstrDecoder {
	/*
	 * Decodes the given instruction
	 */
	private final static String Recurence_ForEachUser = "ForEachUser";
	private final static String Recurence_None = "None";
	private CommonData commonData = null;

	public BaseInstruction instruction = null;

	public String[] recurrences = null;
	public ContentInstrDecoder(CommonData inCommonData, BaseInstruction inBaseInstruction) {
		commonData = inCommonData;
		instruction = inBaseInstruction;
		decodeRecurrence();
	}
	private void decodeRecurrence() {
		if (instruction.recurrenceType.equalsIgnoreCase(Recurence_None)){
			recurrences = null;
		} else if (instruction.recurrenceType.equalsIgnoreCase(Recurence_ForEachUser)){
			recurrences = commonData.getUsersHandler().getUsersNamesStrings();
		}
	}
}