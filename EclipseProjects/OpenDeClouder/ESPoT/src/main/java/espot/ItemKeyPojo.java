package espot;

public class ItemKeyPojo {
	/*
	 * Convenience class for holding item keys (items get grouped to the corresponding rollup classes)
	 */
	public String itemName;
	public ArtifactKeyPojo artifactKeyPojo;
	public ItemKeyPojo(ArtifactKeyPojo inArtifactKeyPojo, String inItemName) {
		artifactKeyPojo = inArtifactKeyPojo;
		itemName = inItemName;
	}
}