package espot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class ResponseChecker {
	/*
	 * Checks responses from the server processor for the requests sent
	 */
	private CommonData commonData;
	private CatelogPersistenceManager catelogPersistenceManager;
	private Commons commons;
	private RemoteAccesser remoteAccesser;
	private  RootPojo rootPojo = null;

	public ResponseChecker(CommonData inCommonData, RemoteAccesser inRemoteAccesser) {
		commonData = inCommonData;
		rootPojo = commonData.getCurrentRootPojo();
		commons = commonData.getCommons();
		catelogPersistenceManager = commonData.getCatelogPersistenceManager();
		remoteAccesser = inRemoteAccesser;		
	}

	public void checkResponsesForOneRoot()
			throws IOException,
			TransformerConfigurationException, TransformerException,
			ParserConfigurationException, ClassNotFoundException, SAXException {

		checkResponsesOfArtifactsOfOneRoot();
		checkResponsesOfReviewsOfOneRoot();

	}

	public void checkResponsesOfArtifactsOfOneRoot()
		throws IOException,
		TransformerConfigurationException, TransformerException,
		ParserConfigurationException, ClassNotFoundException, SAXException {
		
		ArrayList<SelfAuthoredArtifactpojo> selfAuthoredArtifactpojoList = null;
		selfAuthoredArtifactpojoList = catelogPersistenceManager
				.readArtfictsPendingResponseForOneRoot(rootPojo.rootNick);

		for (int k = 0; k < selfAuthoredArtifactpojoList.size(); k++) {
			String timeStamp = commons.getCurrentTimeStamp();
			System.out.println("@checkResponsesForOneRoot 1111");
			System.out.println("@checkResponsesForOneRoot timeStamp " + timeStamp);
			System.out.println("@checkResponsesForOneRoot timeStamp " + timeStamp);
			System.out.println("@checkResponsesForOneRoot selfAuthoredArtifactpojoList k " + k);
			System.out.println("@checkResponsesForOneRoot selfAuthoredArtifactpojoList ReqRespFileName " + selfAuthoredArtifactpojoList.get(k).ReqRespFileName);

			if (selfAuthoredArtifactpojoList.get(k).ReqRespFileName.equalsIgnoreCase("")){
				System.out.println("skipping old record without reqrespfile");
				continue; // records without reqRespFile cannot be processed. 
			}
				
			boolean responseFileProcessed = false;

			System.out.println("artifactWithRootPojoList.get(k).rootPojo.rootString = " + rootPojo.rootString);

			responseFileProcessed = downloadResponseFile_v2(remoteAccesser,
					rootPojo, selfAuthoredArtifactpojoList.get(k).ReqRespFileName);
			
			System.out.println("at 3333");
			
			if (responseFileProcessed) {
				catelogPersistenceManager.updateArtifactStatus(
						selfAuthoredArtifactpojoList.get(k),
						SelfAuthoredArtifactpojo.ArtifactStatusProcessed);
			}
		}
		System.out.println("Uploads successful");
	}
	
	public void checkResponsesOfReviewsOfOneRoot()
		throws IOException,
		TransformerConfigurationException, TransformerException,
		ParserConfigurationException, ClassNotFoundException, SAXException {

		System.out.println("@checkReviewResponsesForOneRoot 1111");

		
		ArrayList<ClientSideNew_ReviewPojo> reviewItemPojoList = null;
		reviewItemPojoList = catelogPersistenceManager
				.readReviewsPendingResponseOfOneRoot();
	
		for (int k = 0; k < reviewItemPojoList.size(); k++) {
			String timeStamp = commons.getCurrentTimeStamp();
			System.out.println("@checkResponsesForOneRoot for reviews 1111");
			System.out.println("@checkResponsesForOneRoot timeStamp " + timeStamp);
			System.out.println("@checkResponsesForOneRoot reviewItemPojoList k " + k);
			System.out.println("@checkResponsesForOneRoot reviewItemPojoList reqRespFileName " + reviewItemPojoList.get(k).reqRespFileName);

			if (reviewItemPojoList.get(k).reqRespFileName.equalsIgnoreCase("")){
				System.out.println("skipping old record without reqrespfile");
				continue; // records without reqRespFile cannot be processed. 
			}

			System.out.println("reviewItemPojoList.get(k).itemKeyPojo.artifactKeyPojo.rootString = " + rootPojo.rootString);

			boolean responseFileProcessed = false;
			responseFileProcessed = downloadResponseFile_v2(remoteAccesser,
					rootPojo, reviewItemPojoList.get(k).reqRespFileName);
			
			
			System.out.println("at 3333");
			
			if (responseFileProcessed) {
				reviewItemPojoList.get(k).processStatus = SelfAuthoredArtifactpojo.ArtifactStatusProcessed;
				catelogPersistenceManager.updateReviewProcessStatus(
						reviewItemPojoList.get(k));
			}
		}
		System.out.println("Uploads successful");
		System.out.println("@Ending ReviewResponsesForOneRoot 1111");
	}

	public boolean downloadResponseFile_v2(RemoteAccesser inRemoteAccesser,
			RootPojo inRootPojo, String inReqRespFileName) {

		boolean uploadedFileProcessed = false;
		remoteAccesser = inRemoteAccesser;

		String remoteResponseFileString = commons.getRemoteResponsePickBox(rootPojo.rootString,rootPojo.fileSeparator)
								+ rootPojo.fileSeparator
								+ inReqRespFileName;
		
		System.out.println("remoteResponseFileString = " + remoteResponseFileString);

		try {
			if (remoteAccesser.exists(remoteResponseFileString)) {
				uploadedFileProcessed = true;
				InputStream responseXMLStream = remoteAccesser.getRemoteFileStream(remoteResponseFileString);
				
				String localResponseFileName = commons.getFullLocalPathFileNameOfResponseFile(inRootPojo.rootNick, inReqRespFileName);
				System.out.println("localResponseFileName: " + localResponseFileName);
				commons.storeInStream(responseXMLStream, localResponseFileName);
				
			} else {
				System.out.println("uploaded file is yet to be processed : " + remoteResponseFileString);
			}
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "Error in ResponseChecker downloadResponseFile_v2 " + " " + inReqRespFileName, e);
		}
		return uploadedFileProcessed;
	}	
}