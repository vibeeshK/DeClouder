package espot;

public class ArtifactKeyPojo {
	/*
	 * Provides an object view of the composite key of an artifact
	 */
	public String rootNick;
	public String relevance;
	public String artifactName;
	public String contentType;

	public ArtifactKeyPojo(){
	};

	public ArtifactKeyPojo(String inRootNick, String inRelevance,String inArtifactName, String inContentType) {
		rootNick = inRootNick;
		relevance = inRelevance;
		artifactName = inArtifactName;
		contentType = inContentType;
	}
	public ArtifactKeyPojo cloneArtifactKeyPojo() {
		ArtifactKeyPojo clonedArtifactKeyPojo = new ArtifactKeyPojo(rootNick,relevance,artifactName,contentType);
		return clonedArtifactKeyPojo;
	}
	
	public boolean isDiffArtifact(ArtifactKeyPojo inArtifactKeyPojo) {
		if (inArtifactKeyPojo == null || 
			!inArtifactKeyPojo.rootNick.equalsIgnoreCase(this.rootNick) || 
			!inArtifactKeyPojo.relevance.equalsIgnoreCase(this.relevance) || 
			!inArtifactKeyPojo.artifactName.equalsIgnoreCase(this.artifactName) || 
			!inArtifactKeyPojo.contentType.equalsIgnoreCase(this.contentType)) {
			return true;
		} else return false;
	}
}