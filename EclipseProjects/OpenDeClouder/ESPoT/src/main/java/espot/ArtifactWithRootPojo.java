package espot;

public class ArtifactWithRootPojo {
	/*
	 * Convenience class
	 */
	public SelfAuthoredArtifactpojo selfAuthoredArtifactspojo;
	public RootPojo rootPojo;

	public ArtifactWithRootPojo() {
	}

	public ArtifactWithRootPojo(
			SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo,
			RootPojo inRootPojo) {

		setSelfAuthoredArtifactspojo(inSelfAuthoredArtifactspojo, inRootPojo);
	}

	public void setSelfAuthoredArtifactspojo(
			SelfAuthoredArtifactpojo inSelfAuthoredArtifactspojo,
			RootPojo inRootPojo) {
		selfAuthoredArtifactspojo = inSelfAuthoredArtifactspojo;
		rootPojo = inRootPojo;
	}
}
