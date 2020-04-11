package espot;

import org.apache.commons.lang.StringUtils;

public class ContentHandlerSpecs {
	/*
	 * Provides the object view of a content type's specifications 
	 */
	final static String ROLLUP_ADDUP_TYPE_NONE = "NONE";
	final static String ROLLUP_ADDUP_TYPE_ROLLUP = "ROLLUP";
	final static String ROLLUP_ADDUP_TYPE_ADDUP = "ADDUP";

	Commons commons = null;
	public String contentType = "";
	public String template = "";
	public String extension = "";
	public boolean hasSpecialHandler = false;
	public boolean userInitiated = false;	
	public boolean autoTriggered = false;	
	public boolean personified = false;
	public String handlerClass = "";
	public String extdHandlerCls = "";
	public String rollupOrAddup = "";
	public String rollAddSeparator = "";
	public String replOptRelevance = "";
	public String replOptArtifact = "";
		
	public int rollupLevel = -1;
	public String rollAddContentType = "";
	public String addupRelevance = "";
	public String rollAddArtifactName = "";
	public String instructions = "";
	public CntProcInstrucDoc cntProcInstrucDoc = null;

	public boolean rollupAddupType = false;
	
	public ContentHandlerSpecs(Commons inCommons){
		rollupAddupType = false;
		commons = inCommons;
	}

	public void setContentHandlerSpecs(String inContentType, String inTemplate, String inExtension, 
			Boolean inHasSpecialHandler, Boolean inUserInitiated, Boolean inAutoTriggered, Boolean inPersonified,
			String inHandlerClass, String inExtdHandlerCls, String inRollupOrAddup, 
			String inRollAddSeparator, String inReplOptRelevance, String inReplOptArtifact, int inRollupLevel, 
			String inRollAddContentType, 
			String inAddupRelevance,
			String inRollAddArtifactName, String inInstructions) {
		
		contentType = inContentType;
		template = (inTemplate != null) ? inTemplate : "";
		extension = (inExtension != null) ? inExtension : "";
		hasSpecialHandler = (inHasSpecialHandler != null) ? inHasSpecialHandler : false;
		userInitiated = (inUserInitiated != null) ? inUserInitiated : false;				
		autoTriggered = (inAutoTriggered != null) ? inAutoTriggered : false;
		personified = (inPersonified != null) ? inPersonified: false;
		handlerClass = (inHandlerClass != null) ? inHandlerClass : "";
		extdHandlerCls = (inExtdHandlerCls != null) ? inExtdHandlerCls : "";
		rollupOrAddup = inRollupOrAddup;
		rollAddSeparator = (inRollAddSeparator != null) ? inRollAddSeparator : "";
		replOptRelevance = (inReplOptRelevance != null) ? inReplOptRelevance : "";
		replOptArtifact = (inReplOptArtifact != null) ? inReplOptArtifact : "";
		
		rollupLevel = (inRollupLevel != -1) ? inRollupLevel : -1;
		rollAddContentType = (inRollAddContentType != null)? inRollAddContentType : "";
		addupRelevance = (inAddupRelevance != null)? inAddupRelevance : "";
		rollAddArtifactName = (inRollAddArtifactName != null) ? inRollAddArtifactName : "";

		instructions = (inInstructions != null) ? inInstructions : "";
		System.out.println("11 setContentHandlerSpecs inInstructions : " + inInstructions);
		
		if (instructions != null && !instructions.equalsIgnoreCase("")) {
			System.out.println("22 setContentHandlerSpecs instructions : " + instructions);
			cntProcInstrucDoc = (CntProcInstrucDoc) commons.getJsonDocFromString(instructions,CntProcInstrucDoc.class);
			System.out.println("@ setContentHandlerSpecs cntProcInstrucJson : " + cntProcInstrucDoc);
		}
		System.out.println("@ setContentHandlerSpecs instructions : " + instructions);
		System.out.println("@ setContentHandlerSpecs contentType : " + contentType);
		System.out.println("@ setContentHandlerSpecs addupRelevance : " + addupRelevance);
		System.out.println("@ setContentHandlerSpecs rollupArtifactName : " + rollAddArtifactName);
		System.out.println("@ setContentHandlerSpecs rollupContentType : " + rollAddContentType);
		System.out.println("@ setContentHandlerSpecs hasSpecialHandler : " + hasSpecialHandler);

		//if (rollupOrAddup == null || rollupOrAddup.equalsIgnoreCase("") || rollupOrAddup.equalsIgnoreCase(ROLLUP_ADDUP_TYPE_NONE)){
		//	rollupAddupType = true;			
		//}		
		
		if (rollupOrAddup != null && !rollupOrAddup.isEmpty() && !rollupOrAddup.equalsIgnoreCase(ROLLUP_ADDUP_TYPE_NONE)){
			rollupAddupType = true;
		}

		System.out.println("@ setContentHandlerSpecs rollupAddupType : " + rollupAddupType);
	}
	
	public ArtifactKeyPojo getFinalArtifactKeyPojo(String inChildRootNick, String inChildRelevance, String inChildArtifactName, String inSeparator) {
		System.out.println("getFinalArtifactKeyPojo ");
		System.out.println("getFinalArtifactKeyPojo contentType is " + contentType);
		System.out.println("getFinalArtifactKeyPojo inChildRelevance is " + inChildRelevance);
		System.out.println("getFinalArtifactKeyPojo inChildArtifactName is " + inChildArtifactName);
		System.out.println("getFinalArtifactKeyPojo inSeparator is " + inSeparator);

		ArtifactKeyPojo finalArtifactKeyPojo = null;
		//if (rollupOrAddup == null || rollupOrAddup.equalsIgnoreCase("") || rollupOrAddup.equalsIgnoreCase(ROLLUP_ADDUP_TYPE_NONE)){
		if (!rollupAddupType) {
			System.out.println("getFinalArtifactKeyPojo ROLLUP_ADDUP_TYPE_NONE");
			finalArtifactKeyPojo = new ArtifactKeyPojo(inChildRootNick,inChildRelevance,inChildArtifactName,contentType);
		} else {
			String rejointRelevanceString;

			//SlashFix starts			
			//StringUtils.replace(inChildRelevance,inSeparator,"\\");
			//String[] splitStrings = StringUtils.split(inChildRelevance,"\\");

			String childRelevanceForSplits = inChildRelevance;
			StringUtils.replace(childRelevanceForSplits,inSeparator,commons.localFileSeparator);
			String[] splitStrings = StringUtils.split(childRelevanceForSplits,commons.localFileSeparator);
			//SlashFix ends


			System.out.println("getRolledUpRelevance : splitStrings size:" + splitStrings.length);
			System.out.println("getRolledUpRelevance : splitStrings:" + splitStrings);
			System.out.println("getRolledUpRelevance : rollupLevel:" + rollupLevel);
			System.out.println("getRolledUpRelevance : inSeparator:" + inSeparator);
			
			for (int level=0;level<rollupLevel;level++) {
				System.out.println("getRolledUpRelevance : splitStrings[" + level + "] is " + splitStrings[level]);
			}

			if (rollupOrAddup.equalsIgnoreCase(ROLLUP_ADDUP_TYPE_ROLLUP)){
				//ROLLUP ----- rollup artifacts rollsup upto the nth level parent folder			
				//SlashFix starts
				//rejointRelevanceString = StringUtils.join(splitStrings,inSeparator,0,rollupLevel);
				rejointRelevanceString = StringUtils.join(splitStrings,commons.localFileSeparator,0,rollupLevel);
				//SlashFix ends
			} else {
				//ADDUP ----- addup artifacts just adds at the same relevance path as its child
				if (!addupRelevance.equalsIgnoreCase("")) {
					rejointRelevanceString = addupRelevance; 
				} else {
					rejointRelevanceString = inChildRelevance;
				}
			}
			System.out.println("getRolledUpRelevance ROLLUP_ADDUP_TYPE is:" + rollupOrAddup);
			System.out.println("getRolledUpRelevance : rejointString:" + rejointRelevanceString);
			System.out.println("getRolledUpRelevance : rollAddSeparator:" + rollAddSeparator);

			String artifactSpltLeftSide = getParentPartOfChildArtifactName(inChildArtifactName);
			
			System.out.println("getRolledUpRelevance : inChildArtifactName :" + inChildArtifactName);

			System.out.println("getRolledUpRelevance : artifactSpltStgs:" + artifactSpltLeftSide);
			System.out.println("getRolledUpRelevance : artifactSpltStgs[0] it is :" + artifactSpltLeftSide);

			System.out.println("getFinalArtifactKeyPojo ROLLUP0 replOptRelevance is " + replOptRelevance);
			String rolledArtifactName = rollAddArtifactName;
			if (!replOptRelevance.equalsIgnoreCase("")){
				rolledArtifactName = rolledArtifactName.replace(replOptRelevance,splitStrings[splitStrings.length-1]);
				System.out.println("getFinalArtifactKeyPojo ROLLUP1 rolledArtifactName is " + rolledArtifactName);
				System.out.println("getFinalArtifactKeyPojo ROLLUP1 splitStrings.length is " + splitStrings.length);
				System.out.println("getFinalArtifactKeyPojo ROLLUP1 splitStrings[splitStrings.length-1] is " + splitStrings[splitStrings.length-1]);
			}
			if (!replOptArtifact.equalsIgnoreCase("")){
				rolledArtifactName = rolledArtifactName.replace(replOptArtifact,artifactSpltLeftSide);
				System.out.println("getFinalArtifactKeyPojo ROLLUP2 rolledArtifactName is " + rolledArtifactName);
				System.out.println("getFinalArtifactKeyPojo ROLLUP2 artifactSpltStgs[0] is " + artifactSpltLeftSide);
			}

			System.out.println("getFinalArtifactKeyPojo inChildArtifactName is " + inChildArtifactName);
			System.out.println("getFinalArtifactKeyPojo rollAddSeparator is " + rollAddSeparator);
			System.out.println("getFinalArtifactKeyPojo StringUtils.substringBeforeLast(inChildArtifactName,rollAddSeparator) is " + StringUtils.substringBeforeLast(inChildArtifactName,rollAddSeparator));

			finalArtifactKeyPojo = new ArtifactKeyPojo(
											inChildRootNick,
											rejointRelevanceString,
											rolledArtifactName,
											rollAddContentType);
		}
		return finalArtifactKeyPojo;		
	}

	public String getChildPartOfArtifactName(String inArtifactName) {
		String childPartOfArtifactName = "";
		if (!rollAddSeparator.equals("")){
			childPartOfArtifactName = StringUtils.substringAfter(inArtifactName,rollAddSeparator);
		} else {
			childPartOfArtifactName = inArtifactName;
		}
		return childPartOfArtifactName;
	}

	public String getParentPartOfChildArtifactName(String inArtifactName) {
		String parentPartOfChildArtifactName = "";
		if (!rollAddSeparator.equals("")){
			parentPartOfChildArtifactName = StringUtils.substringBefore(inArtifactName,rollAddSeparator);
		}
		return parentPartOfChildArtifactName;
	}
}