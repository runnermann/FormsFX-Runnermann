package fileops;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import flashmonkey.FlashCardOps;
import flashmonkey.ReadFlash;
import forms.utility.Alphabet;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.LoggerFactory;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

//import static habanero.edu.rice.pcdp.PCDP.*;



public class CloudOps {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CloudOps.class);
	//private static final Logger LOGGER = LoggerFactory.getLogger(CloudOps.class);

	// -- For thumbs -- //
	// ***** AWS *******
	private BasicAWSCredentials creds = new BasicAWSCredentials(" AKIA3YIX3CI5ZQXD4MHP", "UHLFa7WBS7Cy8mbjNw9t5VtjnCwY13kohnBnYXzH");
	private AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider(creds);
	private String bucketName = "iooily.flashmonkey";
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
	private static volatile String token;

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
		isInValid();
		return this.token;
	}

	protected void setToken(String tkn) {
		this.token = tkn;
	}
	public ArrayList<CloudLink> getCloudLinks() {
		return cloudLinks;
	}

	// ************************* THUMBS ******************************* //
	// @TODO remove getMediaFmS3 and move to own class. Obscure, encrypt and download S3 pw and username for thumbs access.
	/**
	 * @param deckThumbs ArrayList of deck image string names
	 * @return Returns an arrayList of images retrieved from S3 based on the
	 * input provided in the Parameter. If there is a problem with the connection
	 * returns an empty arraylist.
	 */
	private S3Object s3Object;
	private String name;
	public ArrayList<Image> getMediaFmS3(ArrayList<String> deckThumbs)
	{
		LOGGER.info("*** getMediaFmS3 *** imageNames: {}", deckThumbs);

		if(!Utility.isConnected()) {
			return new ArrayList<Image>(0);
		}

		long getTime; //= System.currentTimeMillis();
		String bucketName = "iooily.flashmonkey.deck-thumbs";
		if (s3client.doesBucketExistV2(bucketName)) {
			LOGGER.debug("bucket name: {}", bucketName);
			// *** performance testing ***
			getTime = System.currentTimeMillis();

			ArrayList<Image> imgList = new ArrayList<>(deckThumbs.size());
			// @TODO parallelize getMediaFmS3
			// NO NO NO! Try barriers
			//forallChunked(0, mediaObjs.size() -1, i -> {
			//ImageInputStream imageInput;
			for(int i = 0; i < deckThumbs.size(); i++) {
				name = deckThumbs.get(i);
				try {
					// get the mediaFile from s3
					s3Object = s3client.getObject(bucketName, name);
					S3ObjectInputStream s3is = s3Object.getObjectContent();
					ImageInputStream imageInput = ImageIO.createImageInputStream(s3is);
					BufferedImage buIm = ImageIO.read(imageInput);
					imgList.add(SwingFXUtils.toFXImage(buIm, null));

				} catch (AmazonServiceException e) {
					LOGGER.info("getMediaFmS3() Downloading failed :" + e.getMessage());
					LOGGER.debug("bucket name: {}", bucketName);
				} catch (Exception e) {
					LOGGER.warn("\tUnknown Exception: in connectCloudIn(...) \n" + e.getStackTrace());
					e.printStackTrace();
				}
			}
			getTime = System.currentTimeMillis() - getTime;
			LOGGER.info("getMediaFmS3: CloudOps time passed: {} milliseconds.", getTime);

			return imgList;

		} else {
			LOGGER.warn("could not connect to the bucket during download");
		}
		return null;
	}



	// ************************* Deck RELATED ************************* //

	/**
	 * Sets the list of decks from the users S3 and a token for future
	 * communications with S3. INtended to be the first communication
	 * with S3 after the start of the app and login.
	 * <p>NOTE: Replaces cloudGetListFromFile(...)</p>
	 * @param name The usersname provided by the login form
	 * @param pw The password provided by the login form
	 * @return              
	 */
	public int setS3DeckList(String name, String pw) {
		LOGGER.debug("setS3DeckList called, getting decks if name & pw are correct");
		// set s3ListDecks
		int res = 0;
		if(s3ListObjs == null || token == null) {
			s3ListObjs = new S3ListObjs();
			res = s3ListObjs.listDecks(name, pw);
		} else {
			LOGGER.debug("attempting to use token ");
			res = s3ListObjs.listDecks(token);
			// use token
			//@TODO use token if we already have it instead of PassWord to get list from s3
		}
		// get list of decks from s3
		if(s3ListObjs.getCloudLinks() != null && res == 1) {
			token = s3ListObjs.getToken();
			cloudLinks = s3ListObjs.getCloudLinks();
		}
		return res;
	}


	/**
	 * Returns a list of the media contained in the
	 * clouds repository for the deck given in the parameter.
	 * The user name that is used to retrieve the deck is embeded
	 * in the token.
	 * <b>NOTE:</b> Token must be valid prior to the use of this
	 * method. Check that it is valid prior. This method runs on
	 * a seperate thread from the main thread.
	 * @param deckName
	 * @return
	 */
	public ArrayList<CloudLink> getS3MediaList(String deckName) {
		//if(s3ListObjs == null || token == null) {
		if(s3ListObjs == null || token == null) {
			s3ListObjs = new S3ListObjs();
			// we indlude the users name in the token
			return s3ListObjs.listMedia(token, ReadFlash.getInstance().getDeckName());
		} else {
			if(token == null) {
				LOGGER.debug("ending Token is null!!!");
				System.exit(1);
			}
			return s3ListObjs.listMedia(token, deckName);
		}
	}

	/**
	 * Renews the token if possible
	 * @return Returns the new token if was successful. Otherwise returns
	 * false.
	 */
	private static String renewToken() {
		VertxIO v = new VertxIO();
		return v.ou8123(token);
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
		LOGGER.debug("getMedia called for {} items", downloads.size());
		// Reqst signedURL's for all downloads in one trip to vertx
		S3GetObjs getObjs = new S3GetObjs();
		getObjs.serialGetMedia(downloads, FlashCardOps.getInstance().CO.getToken());
	}

	/**
	 * upload media to S3 from CreateFlash when there are only a few uploads
	 * and the overhead of async is not advantageous.
	 * Ussed by CreateFlash as media is created, card by card.
	 * @param uploads
	 */
	public void putMedia(String[] uploads) {
		LOGGER.debug("putMedia for serial called");
		S3PutObjs putObjs = new S3PutObjs();
		ArrayList<String> l = new ArrayList<>(Arrays.asList(uploads));
		putObjs.serialPutMedia(l, FlashCardOps.getInstance().CO.getToken());
	}

	/**
	 * Uses async upload to upload to S3.
	 * @param uploads
	 */
	public void putMedia(ArrayList<MediaSyncObj> uploads) {
		// 1) get the list of signedURLs
		// 2) send the files to s3 storage
		System.out.println("cloudops put media called");
		// Reqst signedURL's for all uploads in one trip to vertx
		S3PutObjs putObjs = new S3PutObjs();
		String sToken = FlashCardOps.getInstance().CO.getToken();
		if( ! sToken.substring(3).startsWith("fail")) {
			putObjs.asyncPutMedia(uploads, sToken);
		} else {
			LOGGER.warn("Token failed login {}", sToken);
		}
	}

	public void getDeck(String deckName) {
		// send token, deckname in Json and send to
		// getDeck destination
		//String json = new String("{" +
		//		"\"token\":\"" + token + "\"" +
		//		",\"deckname\":\"" + deckName + "\"}");
//		S3GetObjs getObjs = new S3GetObjs();
//		getObjs.serialGetDeck(downloads, 'm',FlashCardOps.getInstance().CO.getToken());

		//String destination = "deck-s3-get";
		//boolean bool = getDeckHelper(json, destination);

		// if successful, set FlashListMM to new deck
	}

	public void putDeck(String fileName) {
		S3PutObjs putObjs = new S3PutObjs();
		putObjs.putDeck(fileName, FlashCardOps.getInstance().CO.getToken());
	}


	private boolean getDeckHelper(String json, String destination) {
		// get deck from Vert.X
		final HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofSeconds(10))
				.build();
		// save deck to file
		final HttpRequest req = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.uri(URI.create("http://localhost:8080/" + destination))
				//@TODO set S3Creds to HTTPS
				//.uri(URI.create("https://localhost:8080/resource-s3-list"))
				.header("Content-Type", "application/json")
				.build();
		try {
			final HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() == 200) { return true; }

		} catch (ConnectException e) {
			// failed, send user a notification
			LOGGER.warn("ConnectException: <{}> is unable to connect: Server not found", Alphabet.encrypt(UserData.getUserName()));
//			return -1;
		} catch (JsonProcessingException e) {
			LOGGER.warn("ERROR: while parsing JSON to java message: {}", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.warn("IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			LOGGER.debug(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}


	protected void removeFmCloud(String fileName) {
		/* stub */
		//@TODO removeFmCloud
	}

	/* ***** TOKEN RELATED ***** */


	/**
	 * Verifies if a token has expired.
	 * @return if the token has expired and is past the reset time, returns 1; If it is
	 * still within the expired time, it returns -1. Otherwise returns 0
	 */
	public static boolean isInValid() {
		LOGGER.debug("isInValid called");
		if(token == null) {
			// we include the users name in the token
			LOGGER.warn("Token is null");
		}
		else {
			String[] parts = token.split("\\.", 0);
			byte[] b = Base64.getUrlDecoder().decode(parts[1]);
			String decoded = new String(b, StandardCharsets.UTF_8);
			try {
				Map<String, Object> m = Utility.jsonToMap(decoded);
				int now = (int) (System.currentTimeMillis() * .001);
				int expired = (int) m.get("exp");
				int initiated = (int) m.get("iat");
				int blocks = 1000 * 15;
				// reset length * 15 min blocks past intitialization time
				int num = (int) m.get("rl");
				LOGGER.debug("now <{}> is greater than exp <{}> ? <{}>", now, expired, (now > expired));

				if (now < expired) {
					return false;
				} else if (now < (initiated + num * blocks)) {
					token = renewToken();
					LOGGER.debug("renewing token. new token: {}", token);
					if (token.contains("token")) {
						return true;
					}
				}
			} catch (JsonProcessingException e) {
				LOGGER.warn("ERROR processing JWT payload");
				e.printStackTrace();
			}
		}
		return false;
	}
}
