package espot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import commonTechs.TransferObject;

public class UpdatesLogPojo {
	/*
	 * Holder of updates log of sys components
	 */
	Document updateLogDoc = null;
	Date boundryDateForPartLoad = null;
	ArrayList<TransferObject> laterUpdateElements = null;
	int nextTransferObjCount = 0;
	int totalTransferObjects = 0;
	Commons commons = null;
	public Date lastUpdateTm = null;

	public UpdatesLogPojo(Document inUpdateLogDoc, Commons inCommons){
		updateLogDoc = inUpdateLogDoc;
		commons = inCommons;
		setLaterUpdateElements();
	}
	
	public boolean isNextTransferAvailable(){
		if (nextTransferObjCount < totalTransferObjects) return true;
		else return false;
	}

	public TransferObject getNextTransfer(){
		if (isNextTransferAvailable()) return laterUpdateElements.get(nextTransferObjCount++);
		else return null;
	}
	
	private void setLaterUpdateElements(){
		laterUpdateElements = new ArrayList<TransferObject>();
		NodeList allUpdates = updateLogDoc.getElementsByTagName("Update");
		
		for(int nodeCount = 0; nodeCount < allUpdates.getLength(); nodeCount++){
			String updateTmString = ((Element) allUpdates.item(nodeCount)).getAttribute("TimeStamp");
			Date updateTm = null;
			try {
				updateTm = commons.getDateFromString(updateTmString);
			} catch (ParseException e) {
				e.printStackTrace();
				ErrorHandler.showErrorAndQuit(commons, "Error in UpdatesLogPojo setLaterUpdateElements", e);
			}
			commons.logger.info(" setLaterUpdateElements. uptd Tm: " + updateTmString);
			commons.logger.info(" current tm: " + commons.sysCompCurrLocalLogUpdateTm);
			commons.logger.info(" source location: " + ((Element) allUpdates.item(nodeCount)).getAttribute("FileFromLocation"));

			if (updateTm.after(commons.sysCompCurrLocalLogUpdateTm)) {
				if (lastUpdateTm == null || lastUpdateTm.after(updateTm)) lastUpdateTm = updateTm;

				TransferObject transferObject = new TransferObject();
				transferObject.sourceObj = ((Element) allUpdates.item(nodeCount)).getAttribute("FileFromLocation");
				transferObject.destinationObj = ((Element) allUpdates.item(nodeCount)).getAttribute("FileToLocation");
				laterUpdateElements.add(transferObject);
			}
		}
		totalTransferObjects = laterUpdateElements.size();
	}
}