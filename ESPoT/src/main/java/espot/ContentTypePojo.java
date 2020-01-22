package espot;

public class ContentTypePojo {
	/*
	 * Holds the key specifications of a content type
	 */
	public String contentType = null;
	public String template = null;
	public String extension = null;
	public boolean hasSpecialHandler = false;
	public String class_Name = null;
	public int rollupLevel = 0;
	public String artifactName = null;
	public String rollupContentType = null;
	
	public ContentTypePojo() {
	}

	public ContentTypePojo(String inContentType, String inTemplate, String inExtension) {
		System.out.println("5Oct_ContentTypePojo");
		
		setContentTypePojo(inContentType, inTemplate, inExtension);
	}
	
	public void setContentTypePojo(String inContentType, String inTemplate, String inExtension) {
		
		System.out.println("5Oct_setContentTypePojo1 " + "inContentType=" + inContentType + " Template= " + inTemplate + " inExtension=" + inExtension);
		
		contentType = inContentType;
		template = inTemplate;
		extension = inExtension;
		
		System.out.println("5Oct_setContentTypePojo2");
		
	}
}
