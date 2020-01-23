package remoteAccessers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

import commonTechs.CommonTechs;
import espot.AbstractRemoteAccesser;
import espot.Commons;
import espot.CredentialHandler;
import espot.CredentialsPojo;
import espot.ErrorHandler;
import espot.RootPojo;

public class WebDAVAccesser extends AbstractRemoteAccesser {
	/*
	 * This class provides the means to access files on a WebDAV based Doc Centers
	 */
	private String hostName = null;
	private Sardine sardine = null;
	private String fileSeparator = null;

	public WebDAVAccesser (){
	}

	public void intiateCommunications(String inRootString, Commons inCommons, String inFileSeparator) {
		commons = inCommons;
		fileSeparator = inFileSeparator;
		try {
			hostName = (new URL(inRootString)).getHost();
			refreshTrustStore();
			
			// WebDAVdocs-start
			CredentialsPojo credentialsPojo = CredentialHandler.getCredentialsFor(hostName);
			System.out.println("password received @ getTransportHandler = " + credentialsPojo.password);
			System.out.println("username received @ getTransportHandler= " + credentialsPojo.userName);

			credentialsPojo.userName = "kvasavaiah";
			credentialsPojo.password = "****";
			
			if (credentialsPojo.userName.equalsIgnoreCase("")
					|| credentialsPojo.password.equalsIgnoreCase("")) {
				ErrorHandler.showErrorAndQuit(commons, "error in WebDAV intiateCommunications " + inRootString);
			}
			
			sardine = SardineFactory.begin(credentialsPojo.userName,
					credentialsPojo.password);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV intiateCommunications " + inRootString, e);
		}

		System.out.println("initiated Sardine @ getTransportHandler ");
		// WebDAVdocs-ends
	}
	
	private void refreshTrustStore() {
		// WebDAVdocs-start
		System.setProperty("javax.net.ssl.trustStore", commons.certificatesFolder + File.separatorChar + hostName.replace(".", "_") + ".cer");
		System.setProperty("javax.net.ssl.trustStorePassword", "");
		System.out.println("key store set");

		// The loaded JSSE trust keystore location
		System.out.println("trustore now set to : "
				+ System.getProperty("javax.net.ssl.trustStore")); 
		// The loaded JSSE trust keystore type
		System.out.println("storeType ="
				+ System.getProperty("javax.net.ssl.trustStoreType"));
		// The JSSE trust keystore provider & encrypted password
		System.out.println("storeprovider ="
				+ System.getProperty("javax.net.ssl.trustStoreProvider"));
		System.out.println("storepwd ="
				+ System.getProperty("javax.net.ssl.trustStorePassword"));
		
		int port = 443;
		char[] passphrase = "changeit".toCharArray();

		File file = new File(System.getProperty("javax.net.ssl.trustStore"));
		try {

			if (!file.exists()){
				file.createNewFile(); // create an empty file if it does not exist.
				FileUtils.copyFile(getSystemCertFile(), file);
			}
			System.out.println("Loading KeyStore " + file + "...");
			InputStream in = null;
			KeyStore ks = null;
			System.out.println("Loading KeyStore dir " + file.getAbsolutePath());
				in = new FileInputStream(file);
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, passphrase);
			in.close();
	
			//////////////////
			//////////////////
	            System.out.println(" Expiry check::");
	            System.out.println(" Expiry check::");
	            System.out.println(" Expiry check::");

		    	KeyStore keystore = ks;
		    	
		        Enumeration<String> aliases = keystore.aliases();
		        while(aliases.hasMoreElements()){
		            String alias = aliases.nextElement();
		            if(keystore.getCertificate(alias).getType().equals("X.509")){
		                System.out.println(alias + " expires " + ((X509Certificate) keystore.getCertificate(alias)).getNotAfter());
		                Date certExpiryDate = ((X509Certificate) keystore.getCertificate(alias)).getNotAfter();
		                System.out.println(" certExpiryDate =  " + certExpiryDate);
		                if (alias.startsWith(hostName)){
			                System.out.println(" this is our guy");
			                if (certExpiryDate.before(commons.getDateTS())) {
				                System.out.println(" expired ");
			                } else {
				                System.out.println(" active.......... ");
				                return; // an active certificate is available hence no need to refresh;
			                }
		                }
		            }
		        }

			//////////////////
			//////////////////

			SSLContext context = SSLContext.getInstance("TLS");
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager) tmf
					.getTrustManagers()[0];
			SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
			context.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory factory = context.getSocketFactory();
	
			System.out
					.println("Opening connection to " + hostName + ":" + port + "...");
			SSLSocket socket = (SSLSocket) factory.createSocket(hostName, port);
		
			System.out.println("setting timeout");
			socket.setSoTimeout(10000);
			System.out.println("timeout set");
	
			System.out.println("Starting SSL handshake...");
			socket.startHandshake();
			System.out.println("SSL handshaked");
			socket.close();
			System.out.println();
			System.out.println("No errors, certificate is already trusted. skipping the refress");

			System.out.println("proceeding after check");
	
			X509Certificate[] chain = tm.chain;
			if (chain == null) {
				System.out.println("Could not obtain server certificate chain");
				ErrorHandler.showErrorAndQuit(commons, "error in WebDAV refreshTrustStore Could not obtain server certificate chain ");
			}
	
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
	
			System.out.println();
			System.out.println("Server sent " + chain.length + " certificate(s):");
			System.out.println();
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			for (int i = 0; i < chain.length; i++) {
				X509Certificate cert = chain[i];
				System.out.println(" " + (i + 1) + " Subject "
						+ cert.getSubjectDN());
				System.out.println("   Issuer  " + cert.getIssuerDN());
				sha1.update(cert.getEncoded());
				System.out.println("   sha1    " + toHexString(sha1.digest()));
				md5.update(cert.getEncoded());
				System.out.println("   md5     " + toHexString(md5.digest()));
				System.out.println();
			}
	
			X509Certificate cert = chain[chain.length-1]; 
			
			String alias = hostName + "-" + (chain.length);
			ks.setCertificateEntry(alias, cert);
	
			OutputStream out = new FileOutputStream("jssecacerts");
			ks.store(out, passphrase);
			out.close();

			System.out.println();
			System.out.println(cert);
			System.out.println();
			System.out
					.println("Added certificate to keystore 'jssecacerts' using alias '"
							+ alias + "'");
		} catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV refreshTrustStore ", e);
		}
	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	public void createFolderOfFileIfDontExist(String inFileName) {
		String parentFolder = commons.getFolderNameFromURL(inFileName, fileSeparator);
		try {
			if (!sardine.exists(parentFolder)){
				sardine.createDirectory(inFileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV createFolderOfFileIfDontExist " + inFileName, e);
		}
	}

	public void putInStreamIntoRemoteLocation(String inNewContentRemoteLocation, InputStream inUpdatedContentByteArray) {
		try {
			createFolderOfFileIfDontExist(inNewContentRemoteLocation);
			sardine.put(inNewContentRemoteLocation.replaceAll(" ", "%20"), inUpdatedContentByteArray);
			System.out.println("end processContentAtWeb");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV putInStreamIntoRemoteLocation into " + inNewContentRemoteLocation + " from inUpdatedContentByteArray ", e);
		}
	}

	public void putByteArrayDataIntoRemoteLocation(String inNewContentRemoteLocation, byte[] inByteArrayData) {
		//this method is not used anymore anywhere!!!
		try {
			createFolderOfFileIfDontExist(inNewContentRemoteLocation.replaceAll(" ", "%20"));
			sardine.put(inNewContentRemoteLocation.replaceAll(" ", "%20"), inByteArrayData);
			System.out.println("end processContentAtWeb");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV putByteArrayDataIntoRemoteLocation into " + inNewContentRemoteLocation + " from inByteArrayData ", e);
		}
	}
	
	public void moveToRemoteLocation(String inSourceFileRemoteLocation, String inDestinationFileRemoteLocation) {
		try {
			createFolderOfFileIfDontExist(inDestinationFileRemoteLocation.replaceAll(" ", "%20"));
			sardine.move(inSourceFileRemoteLocation.replaceAll(" ", "%20"), inDestinationFileRemoteLocation.replaceAll(" ", "%20"));
			System.out.println("moved file from " + inSourceFileRemoteLocation + " to " + inDestinationFileRemoteLocation);
			System.out.println("end moveToRemoteLocation");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV moveToRemoteLocation from " + inSourceFileRemoteLocation + " to " + inDestinationFileRemoteLocation, e);
		}
	}
	public ArrayList<String> getRemoteList(String inRemoteDropBox) {
		ArrayList<String> resourcesStringList = null;
		System.out.println("getRemoteList on Web:: " + inRemoteDropBox);

		try {
			List<DavResource> resources = sardine.getResources(inRemoteDropBox.replaceAll(" ", "%20"));
			
			resourcesStringList = new ArrayList<String>();
			for (int i = 0; i < resources.size(); i++) {
				System.out.println("resources received for i = " + i + " "
						+ resources.get(i).toString());
				resourcesStringList.add(resources.get(i).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV getRemoteList " + inRemoteDropBox, e);
		}
		return resourcesStringList;
	}
	public InputStream getRemoteFileStream(String inRemoteFileName) {
		InputStream remoteFileStream = null;
		System.out.println("@getRemoteFileStream inRemoteFileName: " + inRemoteFileName);
		try {
			remoteFileStream = sardine.getInputStream(inRemoteFileName.replaceAll(" ", "%20"));			
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV getRemoteFileStream " + inRemoteFileName, e);
		}
		System.out.println("@getRemoteFileStream remoteFileStream: " + remoteFileStream);
		return remoteFileStream;
	}

	@Override
	public void put(String inRemoteURL, byte[] inBytes) {
		try {
			createFolderOfFileIfDontExist(inRemoteURL);			
			sardine.put(inRemoteURL.replaceAll(" ", "%20"),inBytes);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV put into " + inRemoteURL, e);
		}
	}

	//@Override
	//public List<String> getList(String inRemoteURL) {
	//	List<DavResource> publishFileNameURLs = null;
	//	List<String> publishFileNameURLList = null;
	//
	//	System.out.println("inRemoteURL= " + inRemoteURL);
	//
	//	try {
	//		publishFileNameURLs = sardine.getResources(inRemoteURL.replaceAll(" ", "%20"));
	//		
	//	} catch (IOException e) {
	//		e.printStackTrace();
	//		ErrorHandler.showErrorAndQuit(commons, "error in WebDAV getLists for " + inRemoteURL, e);
	//	}
	//	System.out.println("publishFileNameURLs.size() = " + publishFileNameURLs.size());
	//	publishFileNameURLList = commons.getStringListFromObjectList(publishFileNameURLs);
	//	System.out.println("listing done");
	//	return publishFileNameURLList;
	//}

	@Override
	public boolean exists(String inRemoteURL) {
		boolean urlExists = false;
		try {
			urlExists = sardine.exists(inRemoteURL.replaceAll(" ", "%20"));
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.showErrorAndQuit(commons, "error in WebDAV exists check for " + inRemoteURL, e);
		}
		return urlExists;
	}	

	public File getSystemCertFile(){
		File file = new File("jssecacerts");
		if (file.isFile() == false) {
			System.out.println("via aaa");
			char SEP = File.separatorChar;
			File dir = new File(System.getProperty("java.home") + SEP + "lib"
					+ SEP + "security");
			file = new File(dir, "jssecacerts");
			if (file.isFile() == false) {
				System.out.println("via bbb");
				file = new File(dir, "cacerts");
				if (file.isFile() == false) {
					System.out.println("via ccc");
				}
			}
		}
		System.out.println("orig file is = " + file);
		return file;
	}

	@Override
	public void intiateCommunications(RootPojo inRootPojo, Commons inCommons) {
		intiateCommunications(inRootPojo.rootString, inCommons, inRootPojo.fileSeparator);
	}

	@Override
	public void uploadToRemote(String inDestinationFileAtRemote, String inSourceFileAtLocal) {
		FileInputStream localInputFileStream;
		try {
			localInputFileStream = new FileInputStream(new File(inSourceFileAtLocal));
			putInStreamIntoRemoteLocation(inDestinationFileAtRemote, localInputFileStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			CommonTechs.logger.error("Error WebDAVAccessor uploadToRemote " + inDestinationFileAtRemote + " " + inSourceFileAtLocal, e);
		}
	}
}

class SavingTrustManager implements X509TrustManager {

	private final X509TrustManager tm;
	protected X509Certificate[] chain;

	SavingTrustManager(X509TrustManager tm) {
		this.tm = tm;
	}

	public X509Certificate[] getAcceptedIssuers() {
		throw new UnsupportedOperationException();
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		throw new UnsupportedOperationException();
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		this.chain = chain;
		tm.checkServerTrusted(chain, authType);
	}
}