package fileops;

import authcrypt.Auth;
import authcrypt.UserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import fileops.utility.Utility;
import flashmonkey.FlashMonkeyMain;
import fmannotations.FMAnnotations;
import fmexception.EmptyTokenException;
import forms.utility.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static fileops.utility.ProcessingUtility.convert;

//import static habanero.edu.rice.pcdp.PCDP.*;


public abstract class CloudOps implements Serializable {

      private static final long serialVersionUID = FlashMonkeyMain.VERSION;

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CloudOps.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(CloudOps.class);

      //@TODO remove before deploy
      @FMAnnotations.DoNotDeployField
      private static final DoNotDeploy.DoNotDeployTimer timer = new DoNotDeploy.DoNotDeployTimer();


      // list of deckLinks returned from S3GetList
      private static ArrayList<CloudLink> cloudLinks = new ArrayList<>(4);
      // list of mediaLinks returned from s3
      // The token for communications with S3 after
      // initial contact with VertX
      //private TokenStore String token;


      private static S3ListObjs s3ListObjs;

      /**
       * No args constructor
       */
      private CloudOps() {
            //LOGGER.setLevel(Level.DEBUG);
      }

      public static int getNCores() {
            String ncoresStr = System.getenv("COURSERA_GRADER_NCORES");
            if (ncoresStr == null) {
                  return Runtime.getRuntime().availableProcessors();
            } else {
                  return Integer.parseInt(ncoresStr);
            }
      }


      //  ****** account related *********

      /**
       * Request from vertx and stripe to cancell the subscription.
       *
       * @param pw
       * @return returns true if the cancellation was successful.
       */
      public static int requestSubCancel(String pw) throws EmptyTokenException {
            VertxIO vio = new VertxIO();

            String json = vio.ou812cancel(Alphabet.encrypt(UserData.getUserName()), pw, TokenStore.get());
            return vertxPost(json, VertxLink.SUBSCRIPT_CANCEL.getEndPoint());

      }


      /**
       * Step one for pw reset.
       * Requests Vertx to send email with
       * user create code/PRNG.
       *
       * @param email
       * @return Returns a 200.
       */
      public static int requestResetCode(String email) {
            String json = "{\"email\":\"" + email + "\"}";
            String destination = "/PFF7/AB1152";
            return vertxPost(json, destination);
      }


      /**
       * Step two for pw reset.
       * After the user receives the PRNG, this
       * method allows the user to send validation
       * and the new pw.
       *
       * @param x1 email
       * @param x2 new pw
       * @param x3 PRNG code
       * @return Returns true if successful, receives 200 from Vertx.
       */
      public static int requestReset(String x1, String x2, String x3) {
            VertxIO vio = new VertxIO();
            String json = vio.ou812reset(x1, x2, x3);
            String destination = "/PFF7/F9F9F9";
            try {
                  return vertxPost(json, destination, x1, x2);
            } catch (NullPointerException e) {
                  e.printStackTrace();
                  return -1;
            }
      }


      /**
       * Requests Vertx to send email with
       * user create code/PRNG.
       *
       * @param email
       * @return
       */
      public static int requestCreateUserCode(String email) {
            // No token exists
            // create json string with field0, and field1
            String json = "{\"email\":\"" + email + "\"}";
            String destination = "/AD1/AAE017"; // list

            return vertxPost(json, destination);
      }

      /**
       * Step 2
       * Sends a requests to finalize the creation of a user at Vertx.
       * This call is expected to update the existing user created in step 1.
       *
       * @param code
       * @param email
       * @param firstName
       * @param password
       * @return
       */
      public static int requestFinalizeUserRemote(String code, String email, String firstName, String password) {

            //LOGGER.setLevel(Level.DEBUG);

            VertxIO vio = new VertxIO();
            String json = vio.ou812confirm(code, email, firstName, password);
            LOGGER.debug("code: {}, email: {}, pw: {}, name: {}", code, email, password, firstName);
            LOGGER.debug("JSON: " + json);
            String destination = "/AD1/AAE027"; // list

            int res = vertxPost(json, destination);
            LOGGER.debug("boolean for send: ", res);

            return res;
      }


      /**
       * Does not guarantee against null.
       * Use caution when using this method and gaurd
       * against null;
       *
       * @return null or a token.
       */
      private String getToken() {
            isInValid();

            return TokenStore.get();
      }


      protected static ArrayList<CloudLink> getCloudLinks() {
            return cloudLinks;
      }

      // ************************* THUMBS ******************************* //
      // @TODO remove getMediaFmS3 and move to own class. Obscure, encrypt and download S3 pw and username for thumbs access.
      /**
       * //@param deckThumbs ArrayList of deck image string names
       * @return Returns an arrayList of images retrieved from S3 based on the
       * input provided in the Parameter. If there is a problem with the connection
       * returns an empty arraylist.
       */
/*	private S3Object s3Object;
	private String name;
	public ArrayList<Image> getMediaFmS3(ArrayList<String> deckThumbs)
	{
		LOGGER.info("*** getMediaFmS3 *** imageNames: {}", deckThumbs);

		if(!Utility.isConnected()) {
			return new ArrayList<Image>(0);
		}

		long getTime; //= System.currentTimeMillis();
		String bucketName = "iooily-flashmonkey-deck-thumbs";
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

 */


      // ************************* Deck RELATED ************************* //

      /**
       * On the first use, sets the list of decks from the users S3,
       * and sets the token for future communications with S3. On
       * After the first use, uses the token for future comms with s3.
       * Intended to be the first communication
       * with S3 after the start of the app and login.
       *
       * @param name The username provided by the login form
       * @param pw   The password provided by the login form
       * @return returns the respons from a child method. If
       * the token is null, returns 500.
       */
      public static int setS3DeckList(String name, String pw) {
            LOGGER.debug("setS3DeckList called, getting decks if name & pw are correct");
            try {
                  String token = TokenStore.get();
                  timer.start();
                  // set s3ListDecks
                  int res = 0;
                  if (s3ListObjs == null || token == null) {
                        s3ListObjs = new S3ListObjs();
                        res = s3ListObjs.listDecks(name, pw);
                  } else {
                        LOGGER.debug("attempting to use token ");
                        res = s3ListObjs.listDecks(token);
                  }
                  // get list of decks from s3
                  if (s3ListObjs.getCloudLinksObj() != null && res == 1) {
                        TokenStore.set(s3ListObjs.getToken());
                        cloudLinks = s3ListObjs.getCloudLinksObj();
                  }
                  timer.end();
                  LOGGER.debug("\n\n **** setS3DeckList: Send to VERTX and return list + token:  run time: {} **** \n\n", timer.getTotalTimeString());
                  return res;
            } catch (EmptyTokenException e) {
                  LOGGER.warn(e.getMessage());
                  return 500;
            }
      }

      public static void retrieveDeck(String deckFileName, String key) {
            S3GetObjs getObj = new S3GetObjs();

            String token = TokenStore.get();
            if (token != null) {
                  getObj.getDeck(DirectoryMgr.getMediaPath('t') + deckFileName, key, token);
            } else {
                  LOGGER.warn("ERROR: Warning, Token is null when attempting to retrieve a Deck. Expected the token to be set.");
            }
      }


      /**
       * Returns a list of the media contained in the
       * clouds repository for the deck given in the parameter.
       * The user name that is used to retrieve the deck is embeded
       * in the token.
       * <b>NOTE:</b> Token must be valid prior to the use of this
       * method. Check that it is valid prior. This method runs on
       * a seperate thread from the main thread.
       *
       * @param deckFileName
       * @return
       */
      public static ArrayList<CloudLink> getS3MediaList(String deckFileName) {

            String token = TokenStore.get();
            if (token != null) {
                  if (s3ListObjs == null) {
                        s3ListObjs = new S3ListObjs();
                  }
                  //The user's name is include in the token
                  return s3ListObjs.listMedia(token, deckFileName);
            } else {
                  LOGGER.warn("NullTokenException when calling getS3MediaList");
                  return null;
            }
      }

      /**
       * Extracts the file names from an arrayList of cloudlinks
       *
       * @param s3List
       * @return Returns an array of the fileNames.
       */
      private static ArrayList<String> extractNames(ArrayList<CloudLink> s3List) {
            ArrayList<String> ret = new ArrayList<>(s3List.size());
            for (CloudLink l : s3List) {
                  ret.add(l.getName());
            }
            return ret;
      }

      /**
       * Renews the token if possible
       *
       * @return Returns the new token if was successful. Otherwise returns
       * "false".
       */
      private static String renewToken() throws EmptyTokenException {
            VertxIO v = new VertxIO();
            return v.ou8123(TokenStore.get());
      }


      // ***********************  CLOUD SYNC METHODS *********************** //

      /**
       * Compares the objectList against S3. Returns a list of objects
       * that are not in S3.
       *
       * @param objectList
       * @param deckFileName the fileName, not the titleName
       * @return retuns null if size is 0. Otherwise returns a list of items
       * that do not exist in s3.
       */
      public static String[] checkExistsInS3(String[] objectList, String deckFileName) {
            // get list of objects from S3
            ArrayList<CloudLink> s3List = getS3MediaList(deckFileName);
            ArrayList<String> fmS3Names = extractNames(s3List);
            // Set the s3list in hashSet
            Set s3Set = new HashSet(fmS3Names);
            // check that the s3 contains all objects from objectList and return what is not in s3
            ArrayList<String> getList = new ArrayList<>(0);
            for (String s : objectList) {
                  if (s != null && !s3Set.contains(s)) {
                        getList.add(s);
                  }
            }
            // return a list of objects that do not exist in s3.
            return getList.size() == 0 ? null : convert(getList);
      }

      /**
       * Requests to download media fm S3 and saves to file.
       * The lower layers will serially (not-asynchronously) get the
       * media and save.
       * <p>Run this on a separate thread?</p>
       *
       * @param downloads
       */
      public static void getMedia(ArrayList<MediaSyncObj> downloads) {

            LOGGER.debug("getMedia called for {} items", downloads.size());
            timer.start();
            // Reqst signedURL's for all downloads in one trip to vertx

            String token = TokenStore.get();
            if (token != null) {

                  S3GetObjs getObjs = new S3GetObjs();
                  getObjs.serialGetMedia(downloads, token);
            } else {
                  LOGGER.warn("ERROR: Warning, Token is null when attempting to retrieve media. Expected the token to be set.");
            }

            timer.end();
            LOGGER.debug("\n\ngetMedia running time: {}\n\n", timer.getTotalTimeString());
      }

      /**
       * upload media to S3 from CreateFlash when there are only a few uploads
       * and the overhead of async is not advantageous.
       * Used by CreateFlash as media is created, card by card.
       *
       * @param uploads
       */
      public static void putMedia(String[] uploads) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("putMedia for serial called");
            timer.start();
            S3PutObjs putObjs = new S3PutObjs();
            String token = TokenStore.get();
            if (token != null) {
                  ArrayList<String> l = new ArrayList<>(Arrays.asList(uploads));
                  putObjs.serialPutMedia(l, token);
            } else {
                  LOGGER.warn("ERROR: Warning, Token is null when attempting to put media. Expected the token to be set.");
            }
            timer.end();
            LOGGER.debug("\n\nputMedia running time: {}\n\n", timer.getTotalTimeString());
      }

      /**
       * Uses async upload to upload to S3.
       *
       * @param uploads
       */
      public static void putMedia(ArrayList<MediaSyncObj> uploads) {
            // 1) get the list of signedURLs
            // 2) send the files to s3 storage
            timer.start();
            String token = TokenStore.get();
            if (token != null) {

                  // Reqst signedURL's for all uploads in one trip to vertx
                  S3PutObjs putObjs = new S3PutObjs();
                  //String sToken = FlashCardOps.getInstance().CO.getToken();
                  if (!token.startsWith("fail")) {
                        putObjs.asyncPutMedia(uploads, token);
                        //putObjs.serialPutMedia(uploads,sToken);
                  } else {
                        //LOGGER.warn("Token failed login {}", token);
                        LOGGER.warn("failed login");
                  }
            } else {
                  LOGGER.warn("ERROR: Warning, Token is null when attempting to putMediaAsync. Expected the token to be set.");
            }
            timer.end();
            LOGGER.debug("\n\nputMedia running time: {}\n\n", timer.getTotalTimeString());
      }


      /**
       * An async call to uplaad the deck to S3. Ensure there are not other
       * async calls to upload the deck.
       * @param fileName
       */
      public static void putDeck(String fileName) {
            timer.start();
            S3PutObjs putObjs = new S3PutObjs();
            String token = TokenStore.get();
            if (token != null) {
                  ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
                  Runnable task = () -> {
                        putObjs.putDeck(fileName, token);
                        timer.end();
                        LOGGER.debug("\n\nputDeck running time: {}\n\n", timer.getTotalTimeString());
                        scheduledExecutor.shutdown();
                  };
                  scheduledExecutor.execute(task);



            } else {
                  LOGGER.warn("ERROR: Warning, Token is null when attempting putDeck. Expected the token to be set.");
            }
      }

      /**
       * When requesting a reset
       *
       * @param json        json string
       * @param destination the endpoint
       * @param x1          x1
       * @param x2          x2
       * @return int if successful or not
       * @throws NullPointerException
       */
      private static int vertxPost(String json, String destination, String x1, String x2) throws NullPointerException {
            int res = vertxPost(json, destination);
            if (res == 1) {
                  Auth a = new Auth(x2, x1.toLowerCase(), 10);
                  a.setResetLocal("TexasA&M");
                  a.execute();
            }
            return res;
      }

      /**
       * Sends a post request to Vertx server.
       *
       * @param json        contains the neccessary data for the post to succeed or fail.
       * @param destination The destination address that should include the starting "/"
       * @return Returns 1 if the response is 200, 0 if not connected, -1 if failed at server.
       */
      private static int vertxPost(String json, String destination) {
            // get deck from Vert.X
            LOGGER.debug("vertxPost called. calling destination: {}", destination);
            final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(7))
                .build();
            // save deck to file
            final HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(Connect.LINK.getLink() + destination))
                //@TODO set S3Creds to HTTPS
                .header("Content-Type", "application/json")
                .build();
            try {
                  final HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                  if (response.statusCode() == 200 || response.statusCode() == 201) {
                        return 1;
                  }
                  return -1;
            } catch (ConnectException e) {
                  // failed, send user a notification
                  LOGGER.warn("ConnectException: <{}> is unable to connect: Server not found", UserData.getUserName());
                  return 0;
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
            return -1;
      }


      protected void removeFmCloud(String fileName) {
            /* stub */
            //@TODO removeFmCloud
      }

      /* ***** TOKEN RELATED ***** */


      /**
       * Verifies if a token has expired.
       *
       * @return returns true if it is NOT a valid token
       */
      public static boolean isInValid() {
            LOGGER.debug("isInValid called");
            try {
                  String token = TokenStore.get();
                  if (token == null || token.length() == 0) {
                        // we include the users encrypted? name in the token
                        // it should not be null
                        LOGGER.warn("Token is empty");
                  } else {
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
                              // if expired
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
            } catch (EmptyTokenException e) {
                  LOGGER.warn(e.getMessage());
            }
            return false;
      }
}
