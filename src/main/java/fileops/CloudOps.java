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
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

//import static habanero.edu.rice.pcdp.PCDP.*;



public class CloudOps {
	
	//private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CloudOps.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(S3ListObjs.class);
	// ***** AWS *******
	private BasicAWSCredentials creds = new BasicAWSCredentials(" AKIA3YIX3CI5ZQXD4MHP", "UHLFa7WBS7Cy8mbjNw9t5VtjnCwY13kohnBnYXzH");
	private AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider(creds);
	private final String BUCKET_NAME = "iooily-flashmonkey";
	private AmazonS3 s3client = AmazonS3ClientBuilder.standard()
			.withCredentials(credProvider)
			//.withEndPoint("arn:aws:s3:us-west-2:808040731195:accesspoint/userdecks")// 2020/2/18
			.withRegion(Regions.US_WEST_2)
			.build();
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
		//	LOGGER.setLevel(Level.DEBUG);
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

	public ArrayList<CloudLink> getCloudLinks() {
		return cloudLinks;
	}

	protected void setToken(String tkn) {
		this.token = tkn;
	}

	/**
	 * Sets the list of decks from the users S3 and a token for future
	 * communications with S3. INtended to be the first communication
	 * with S3 after the start of the app and login.
	 * <p>NOTE: Replaces cloudGetListFromFile(...)</p>
	 * @param name The usersname provided by the login form
	 * @param pw The password provided by the login form
	 */
	public void setDecksFmS3(String name, String pw) {
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
	public ArrayList<CloudLink> getMediaListFmS3(String deckName) {

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
		getObjs.cloudGetMedia(downloads, FlashCardOps.getInstance().CO.token);
	}


	protected void putMedia(ArrayList<MediaSyncObj> uploads) {
		// Reqst signedURL's for all uploads in one trip to vertx
//		S3PutObjs putObjs = new S3PutObjs();

	}



	protected void removeFmCloud(String fileName) {
		/* stub */
		//@TODO removeFmCloud
	}

	
	private String fullBucketURL;
	/**
	 * Uploads a file to S3. type 'd' is for thumbs, 't' is for text or decks,
     * default is media
	 * @param fileNames to local file variable length one or an array
	 */
	public void connectCloudOut(char type, String userName, String ... fileNames) {
		LOGGER.info("In connect cloud out");
		//Thread.dumpStack();
		if(fileNames.length < 1) {
			return;
		}
		//userName = data.getUserName(); //@todo hash username to unique uuid

		// comment out seperate thread during testing
		new Thread(() -> {
			// performance testing
			long sendTime = System.currentTimeMillis();
			
			try {
				if (s3client.doesBucketExistV2(BUCKET_NAME)) {
					//@TODO parallelize connectCloudOut
					File file = null;
					for (String f : fileNames) {
						
						if (f != null) {
							// get .dat file for card
							if(type == 'd') {
							    LOGGER.info("type == 'd'");
								fullBucketURL = BUCKET_NAME;
								file = new File(DirectoryMgr.getMediaPath('t') + f);
							}
							else if (type == 't') { // if String
								fullBucketURL = BUCKET_NAME + "/" + userName + "/decks";
								file = new File(DirectoryMgr.getMediaPath('t') + f);
								// get media data
							} else {
								LOGGER.info("Looking for media at: {}", DirectoryMgr.getMediaPath(type) + f);
								file = new File(DirectoryMgr.getMediaPath(type) + "/" + f);
								fullBucketURL = BUCKET_NAME + "/" + userName + "/" + ReadFlash.getInstance().getDeckName();
							}

							LOGGER.info("connectCloudOut sendFile {} to cloud. fileExists: {}\n fullBucketURL {}", file.getName(), file.exists(), fullBucketURL);
							
							try {
								// bucket name and location, objectKey (aka fileKey) is the deckName, file
								if(file.length() > 6) {
								    LOGGER.info("putting file to s3");
									s3client.putObject(new PutObjectRequest(fullBucketURL, f, file));
								} else {
									LOGGER.warn("Attempted to send file to S3 smaller than 6 bytes: " + file.getName());
								}
							} catch (AmazonServiceException e) {
								LOGGER.info("uploadFileTos3bucket().Uploading failed :" + e.getMessage());
							}
						} else {
							LOGGER.warn("connectCloudOut received an empty String name. fileName == null} - Not sent -");
						}
					}
				} else {
					LOGGER.warn("could not connect to the bucket");
				}
			} catch (SdkClientException e) {
				LOGGER.warn("Could not connect to AWS. ASW SdkClientException: \nMESSAGE = {}", e.getMessage());
			}
			finally {
				sendTime = (System.currentTimeMillis() - sendTime);
				LOGGER.info("CloudOps send time: {}, num objs: {}", (int) (sendTime / 1000), fileNames.length);
			}
		}).start();
	}

}
