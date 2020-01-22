package espot;

class Comment {
	/*
	 * Convenience class
	 */
	final static String CommentToBeUploaded = "CommentToBeUploaded";
	final static String CommentToBeProcessed = "CommentToBeProcessed";
	final static String CommentProcessed = "CommentProcessed";
	
	public String createdTime;
	public String processStatus;
	public String author;
	public String commentText;
	
	public Comment(
			String inCreatedTime,
			String inProcessStatus,
			String inAuthor,
			String inCommentText) {
		createdTime = inCreatedTime;
		processStatus = inProcessStatus;
		author = inAuthor;
		commentText = inCommentText;
	}
}