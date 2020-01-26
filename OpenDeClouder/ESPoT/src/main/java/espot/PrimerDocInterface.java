package espot;

public interface PrimerDocInterface {
	/*
	 * Interface mandates for the primer doc consumer.
	 * Primer doc is the meta data holder of the artifact packaged in a zip file
	 */	
	void absorbIncomingItemPojo (ItemPojo inItemPojo);
}