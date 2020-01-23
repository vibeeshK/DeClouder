package espot;

public class ERLVerPojo {
	/*
	 * Convenience class for reflecting the version details of ERL
	 */
	public String relevance;
	public String artifactName;
	public String versionedFileName;

	public ERLVerPojo(String inRelevance, String inArtifactName, String inVersionedFileName){
		relevance = inRelevance;
		artifactName = inArtifactName;
		versionedFileName = inVersionedFileName;		
	}
}