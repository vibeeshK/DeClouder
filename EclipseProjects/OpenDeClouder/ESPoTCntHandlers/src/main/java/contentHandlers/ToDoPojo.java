package contentHandlers;

import espot.ItemPojo;

public class ToDoPojo extends ItemPojo{
	/*
	 * Data holder for a toDo item content
	 */
	String cloneFromArtifactName;
	String cloneFromRelevance;
	String cloneFromContentType;
	String attachment;

	ToDoPojo(int inItemNumber){
		super (inItemNumber);
		initializeAdditionalItemPojoFields();
	}
	
	void initializeAdditionalItemPojoFields(){
		cloneFromArtifactName="";
		cloneFromRelevance="";
		cloneFromContentType="";
		attachment="";
	}

	//public String getStatus() {
	//	return status;
	//}
	//
	//public void setStatus(String status) {
	//	this.status = status;
	//}

	public String getCloneFromArtifactName() {
		return cloneFromArtifactName;
	}

	public void setCloneFromArtifactName(String cloneFromArtifactName) {
		this.cloneFromArtifactName = cloneFromArtifactName;
	}

	public String getCloneFromRelevance() {
		return cloneFromRelevance;
	}

	public void setCloneFromRelevance(String cloneFromRelevance) {
		this.cloneFromRelevance = cloneFromRelevance;
	}

	public String getCloneFromContentType() {
		return cloneFromContentType;
	}

	public void setCloneFromContentType(String cloneFromContentType) {
		this.cloneFromContentType = cloneFromContentType;
	}
}

