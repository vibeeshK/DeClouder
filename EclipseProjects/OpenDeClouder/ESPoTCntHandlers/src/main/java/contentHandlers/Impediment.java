package contentHandlers;

import espot.*;

//import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList; //import org.eclipse.swt.events.SelectionAdapter;
import java.util.HashMap;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Impediment extends GenericItemHandler {
	/*
	 * This content handler helps to log an impediment
	 */
	public final static String IMPPREFIX = "Impdmt";
	public final static String IMPTASKSEPARATOR = "_";
	Text description_Tx;

	public static String getImpedimentArtifactName(ContentHandlerSpecs inContentHandlerSpecs, String inProjID, String inTaskID, String inUser, String inTimeStamp) {
		return inProjID + inContentHandlerSpecs.rollAddSeparator 
				+  IMPPREFIX + inTaskID + IMPTASKSEPARATOR +  inUser + inTimeStamp;
	}

	public static String getTaskIdOfImpedimentID(String inImpedimentID){
		String taskIdWithImpPrefixAndPostfix = inImpedimentID;
		String taskIdWithOutImpPrefix = taskIdWithImpPrefixAndPostfix.substring(IMPPREFIX.length());
		String taskIdWithOutPosfix = StringUtils.substringBefore(taskIdWithOutImpPrefix,IMPTASKSEPARATOR);
		return taskIdWithOutPosfix;
	}	

	public static HashMap<String,ArrayList<ImpedimentItemPojo>> getImpedimentsOfTasks(ArrayList<ImpedimentItemPojo> inItemList){
		System.out.println("at start of getImpedimentsOfTasks inItemList size is " + inItemList.size());
		HashMap<String,ArrayList<ImpedimentItemPojo>> impedsOfTasks = new HashMap<String,ArrayList<ImpedimentItemPojo>>();
		for (ImpedimentItemPojo item : inItemList){
			ImpedimentItemPojo impedimentItem = (ImpedimentItemPojo) item;
			String taskID = getTaskIdOfImpedimentID(impedimentItem.impedimentID);
			
			ArrayList<ImpedimentItemPojo> impedimentsOfTask = impedsOfTasks.get(taskID);

			System.out.println("at getImpedimentsOfTasks taskID is " + taskID);
			System.out.println("at getImpedimentsOfTasks impedimentItem.impedimentID is " + impedimentItem.impedimentID);
			System.out.println("at getImpedimentsOfTasks getTaskIdOfImpedimentID(impedimentItem.impedimentID) is " + getTaskIdOfImpedimentID(impedimentItem.impedimentID));
			
			if (impedimentsOfTask == null){
				impedimentsOfTask = new ArrayList<ImpedimentItemPojo>();
				impedsOfTasks.put(taskID,impedimentsOfTask);
			}
			impedimentsOfTask.add(impedimentItem);
			System.out.println("at getImpedimentsOfTasks impedimentsOfTask size is " + impedimentsOfTask.size());
		}
		return impedsOfTasks;
	}
	
	public static ArrayList<ImpedimentItemPojo> filterOpenImpediments(ArrayList<ImpedimentItemPojo> inImpedimentsList){
		ArrayList<ImpedimentItemPojo> openImpediments = null;

		if (inImpedimentsList!=null) {
			for (ImpedimentItemPojo impedimentItem : inImpedimentsList){
				if (!impedimentItem.impedimentStatus.equalsIgnoreCase(ImpedimentItemPojo.IMPEDIMENTSTATUSVALUES_Completed)) {
					if (openImpediments == null){
						openImpediments = new ArrayList<ImpedimentItemPojo>();
					}
					openImpediments.add(impedimentItem);				
				}
			}
		}
		return openImpediments;
	}

	public static ArtifactKeyPojo getRollupArtifactKeyOfProjImpediments(ContentHandlerSpecs inContentHandlerSpecs,
			String inRootNick, String inChildRelevance, String inProjID, String inRelevanceSeparator) {
		//proj Id is passed in place of the child artifactName since its the prefix anyway.
		return inContentHandlerSpecs.getFinalArtifactKeyPojo(inRootNick, inChildRelevance, inProjID, inRelevanceSeparator);
	}

	public void setInitialItemPojoAddlFields(){
		ImpedimentItemPojo impedimentItemPojo = (ImpedimentItemPojo) primerDoc.getItem();
		impedimentItemPojo.impedimentID = contentHandlerSpecs.getChildPartOfArtifactName(impedimentItemPojo.artifactName);
		impedimentItemPojo.projectName = contentHandlerSpecs.getParentPartOfChildArtifactName(impedimentItemPojo.artifactName);
	}
	
	public void checkSetNewItemID() {
		ImpedimentItemPojo impedimentItemPojo = (ImpedimentItemPojo) primerDoc.getItem();
		if (impedimentItemPojo.itemID.equalsIgnoreCase("")) {
			impedimentItemPojo.itemID = impedimentItemPojo.author + commonData.getCommons().getCurrentTimeStamp();
		}
	}

	public Group setAddlFieldsForItemDisplay(Group itemContentGroup, Group inPrevGroup,FormData formData, ItemPojo inItemPojo){
		ImpedimentItemPojo impedimentItemPojo = (ImpedimentItemPojo) inItemPojo;
		Group lastGroup = inPrevGroup;

		Group descriptionInfo = new Group(itemContentGroup, SWT.LEFT);
		descriptionInfo.setText("Description");
		descriptionInfo.setLayout(new FillLayout());
		if (invokedForEdit) {
			description_Tx = new Text(descriptionInfo, SWT.WRAP | SWT.CENTER);
		} else {
			description_Tx = new Text(descriptionInfo, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);			
		}
		description_Tx.setText(impedimentItemPojo.description!=null?impedimentItemPojo.description:"");
		
		formData = new FormData();
		formData.top = new FormAttachment(lastGroup);
		formData.width = PREFERED_ITEM_PANEL_WIDTH;	// this width setting is to show meaningful size for viewing
		descriptionInfo.setLayoutData(formData);
		lastGroup = descriptionInfo;

		return lastGroup;
	}
	
	public void getAddlFieldsOfItemPojo(ItemPojo inItemPojo){
		ImpedimentItemPojo impedimentItemPojo = (ImpedimentItemPojo) primerDoc.getItem();
		if (description_Tx != null) {
			impedimentItemPojo.description = description_Tx.getText();
		}		
	}
	
	public ImpedimentItemPojo getItem() {
		return (ImpedimentItemPojo) primerDoc.getItem();
	}
	
	public void testPrinter(String inPrintHead) {
		ImpedimentItemPojo impedimentItemPojo1 = (ImpedimentItemPojo) primerDoc.getItem();
		System.out.println("In testPrinter from inPrintHead " + inPrintHead);

		System.out.println("In testPrinter itemPojo title is " + primerDoc.getItem().title);
		ImpedimentItemPojo impedimentItemPojo2 = (ImpedimentItemPojo) primerDoc.getItem();

		System.out.println("In testPrinter Pojo title from primeDoc is " + impedimentItemPojo2.title);

	}

	public String validateBeforeUIEdit() {
		String validationBeforeEdit = "";
		return validationBeforeEdit;
	}

	@Override
	public Class getPrimerDocClass() {
		return ImpedimentItemDoc.class;
	}

	@Override
	public boolean validateAddlScrFields(){
		System.out.println("At the start of validateAddlScrFields ");
		return true;
	}

	@Override
	public ImpedimentItemDoc getPrimerDoc() {
		return (ImpedimentItemDoc) primerDoc;
	}

	public ImpedimentItemDoc getNewPrimerDoc() {
		return new ImpedimentItemDoc(new ImpedimentItemPojo(-1));
	}
}