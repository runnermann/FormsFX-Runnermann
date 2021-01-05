package fileops;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import flashmonkey.FlashCardOps;
import flashmonkey.ReadFlash;
import org.slf4j.LoggerFactory;


import java.nio.charset.StandardCharsets;
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
	 * @param uploads
	 * @param numFiles
	 */
	public void putMedia(String[] uploads, int numFiles) {
		LOGGER.debug("putMedia for serial called");
		S3PutObjs putObjs = new S3PutObjs();
		ArrayList<String> l = new ArrayList<>(Arrays.asList(uploads));
		putObjs.serialPutMedia(l, numFiles, FlashCardOps.getInstance().CO.getToken());
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
		String sToken = FlashCardOps.getInstance().CO.getToken();
		if( ! sToken.substring(3).startsWith("fail")) {
			putObjs.asyncPutMedia(uploads, sToken);
		} else {
			LOGGER.warn("Token failed login {}", sToken);
		}
	}

	public void putDeck(String fileName) {
		S3PutObjs putObjs = new S3PutObjs();
		putObjs.putDeck(fileName, FlashCardOps.getInstance().CO.getToken());
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
