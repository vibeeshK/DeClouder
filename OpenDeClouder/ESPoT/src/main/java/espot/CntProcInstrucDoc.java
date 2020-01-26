package espot;

import java.util.ArrayList;
import java.util.HashMap;

public class CntProcInstrucDoc {
	/*
	 * Convenience class to provide complex instructions
	 */
	public final static String XTD_INSTRUCTION = "XtdInstructions";
	public final static String CLIENT_INSTRUCTION = "ClientInstructions";
	public final static String USETYPE_iNSTRUCTIONS = "UseInstructionList";
	public final static String INVOKDTYPE_iNSTRUCTIONS = "InvokeInstructionList";
	public String[] dependentContentTypes = null;

	public String instructionTo = null;
	public HashMap<String, ArrayList<BaseInstruction>> instrSet = null;
}