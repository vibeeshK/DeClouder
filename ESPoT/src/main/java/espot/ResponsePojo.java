package espot;

public class ResponsePojo {
	/*
	 * Holder for response details from server process after processing a request
	 */
	public ERLpojo fromERLpojo = null;
	public ERLpojo toERLpojo = null;
	public String responseText = "";

	public ResponsePojo(ERLpojo inFromERLpojo,
			ERLpojo inToERLpojo,
			String inResponseText
			) {
		fromERLpojo = inFromERLpojo;
		toERLpojo = inToERLpojo;
		responseText = inResponseText;
	}

	public ResponsePojo() {
		fromERLpojo = new ERLpojo();
		toERLpojo = new ERLpojo();
		responseText = "";
	}
}