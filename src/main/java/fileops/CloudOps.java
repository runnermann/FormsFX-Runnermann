package fileops;

import ch.qos.logback.classic.Level;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import flashmonkey.FlashCardOps;
import flashmonkey.ReadFlash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.util.*;

//import static habanero.edu.rice.pcdp.PCDP.*;



public class CloudOps {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CloudOps.class);
	//private static final Logger LOGGER = LoggerFactory.getLogger(CloudOps.class);

	// list of deckLinks returned from S3GetList
	private static ArrayList<CloudLink> cloudLinks = new ArrayList<>(4);
	// list of mediaLinks returned from s3
	// The token for communications with S3 after
	// initial contact with VertX
	private String token;

	private S3ListObjs s3ListObjs;
	//private static ArrayList<CloudLink> cloudLinks;

	/**
	 * No args constructor
	 */
	public CloudOps() {
		LOGGER.setLevel(Level.DEBUG);
	}

	public static int getNCores() {
		String ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
		if (ncoresStr == null) {
			return Runtime.getRuntime().availableProcessors();
		} else {
			return Integer.parseInt(ncoresStr);
		}
	}

	protected String getToken() {
		return this.token;
	}
	protected void setToken(String tkn) {
		this.token = tkn;
	}
	public ArrayList<CloudLink> getCloudLinks() {
		return cloudLinks;
	}

	// ************************* Deck RELATED ************************* //

	/**
	 * Sets the list of decks from the users S3 and a token for future
	 * communications with S3. INtended to be the first communication
	 * with S3 after the start of the app and login.
	 * <p>NOTE: Replaces cloudGetListFromFile(...)</p>
	 * @param name The usersname provided by the login form
	 * @param pw The password provided by the login form
	 */
	public void setS3DeckList(String name, String pw) {
		// set s3ListDecks
		if(s3ListObjs == null && token == null) {
			s3ListObjs = new S3ListObjs();
			s3ListObjs.listDecks(name, pw);
		} else {
			// use token
			//@TODO use token if we already have it instead of PassWord to get list from s3
		}
		// get list of decks from s3
		if(s3ListObjs.getCloudLinks() != null) {
			token = s3ListObjs.getToken();
			cloudLinks = s3ListObjs.getCloudLinks();
		}
	}


	/**
	 * Returns a list of the media contained in the
	 * clouds repository for the deck given in the parameter.
	 * The user name that is used to retrieve the deck is embeded
	 * in the token.
	 * @param deckName
	 * @return
	 */
	public ArrayList<CloudLink> getS3MediaList(String deckName) {
		if(s3ListObjs == null && token == null) {
			s3ListObjs = new S3ListObjs();
			// we indlude the users name in the token
			return s3ListObjs.listMedia(FlashCardOps.getInstance().CO.token, ReadFlash.getInstance().getDeckName());
		} else {
			return s3ListObjs.listMedia(FlashCardOps.getInstance().CO.token, deckName);
		}
	}

	// ***********************  CLOUD SYNC METHODS *********************** //

	/**
	 * Requests to download media fm S3 and saves to file.
	 * The lower layers will serially (not-asynchronously) get the
	 * media and save.
	 * <p>Run this on a seperate thread.</p>
	 * @param downloads
	 */
	public void getMedia(ArrayList<MediaSyncObj> downloads) {
		// Reqst signedURL's for all downloads in one trip to vertx
		S3GetObjs getObjs = new S3GetObjs();
		getObjs.serialGetMedia(downloads, FlashCardOps.getInstance().CO.token);
	}

	/**
	 * upload media to S3 from CreateFlash when there are only a few uploads
	 * and the overhead of async is not advantageous.
	 * @param uploads
	 * @param numFiles
	 */
	public void putMedia(String[] uploads, int numFiles) {
		LOGGER.debug("putMedia for serial called");
		S3PutObjs putObjs = new S3PutObjs();
		ArrayList<String> l = new ArrayList<>(Arrays.asList(uploads));
		putObjs.serialPutMedia(l, numFiles, FlashCardOps.getInstance().CO.token);
	}

	/**
	 * Uses async upload to upload to S3.
	 * @param uploads
	 */
	public void putMedia(ArrayList<MediaSyncObj> uploads) {
		// 1) get the list of signedURLs
		// 2) send the files to s3 storage

		// Reqst signedURL's for all uploads in one trip to vertx
		S3PutObjs putObjs = new S3PutObjs();
		putObjs.asyncPutMedia(uploads, FlashCardOps.getInstance().CO.token);
	}

	public void putDeck(String fileName) {
		S3PutObjs putObjs = new S3PutObjs();
		putObjs.putDeck(fileName, FlashCardOps.getInstance().CO.token);
	}


	protected void removeFmCloud(String fileName) {
		/* stub */
		//@TODO removeFmCloud
	}
}
