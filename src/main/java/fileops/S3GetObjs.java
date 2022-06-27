package fileops;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import fileops.utility.ProcessingUtility;
import fileops.utility.Utility;
import flashmonkey.FlashCardMM;
import flashmonkey.FlashCardOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

import static habanero.edu.rice.pcdp.PCDP.forallChunked;

//import static habanero.edu.rice.pcdp.PCDP.*;

/**
 * For retrieve operations from S3. This class is primarily Serial in operation
 * due to limitations specifically in downloading objects using presignedURLs and
 * due to issues with processing file names created by the presignedURL api. When
 * using inputStream to efficiently "get" objects, inputStream threw exceptions for
 * the names. The S3 client is not thread safe and the result is serial downloading.
 */

public class S3GetObjs {
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(S3GetObjs.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(S3GetObjs.class);
      private static final HttpClient httpClient = HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .connectTimeout(Duration.ofSeconds(2))
          .build();

      /**
       * Constructor
       */
      public S3GetObjs() {
            //LOGGER.setLevel(Level.DEBUG);
            //LOGGER.debug("S3GetObjs constructor called");
      }

      // ************************* DECK RELATED ************************* //


      /**
       * <p>Retrieves a deck. This method
       * requests a signedURL for the user-selected deck. The first element
       * must contain the correct username and token.</p>
       * <p> End result is deck is saved to local file, and FlashListMM
       * is reset to the deck in ReadFlash. </p>
       *
       * @param pathName The full pathName to the file including the file
       * @param key      the S3 key with deckName. ie idk@idk.com/decks/adcd123Test.dec
       * @param token
       */
      public void getDeck(String pathName, String key, String token) {

            if (!Utility.isConnected()) {
                  LOGGER.warn("I am not connected... returning nothing");
                  return;// flashListMM;
            }
            // jsonArray
            String json = "[{\"username\":\"" + authcrypt.UserData.getUserName() + "\",\"token\":\"" + token + "\"},{\"key\":\"" + key + "\"}]";
            LOGGER.debug("getDeck called. userName: {}, key: <{}>, token <{}>", authcrypt.UserData.getUserName(), key, token);

            // *** Request a signedURL for the deck. We post when using credentials: *** //
            // Retrieve the deck
            // (1) contact Vert.x and get the deck signedURL
            // (2) use signedURL to retrieve deck
            // (3) save deck to file
            // (4) handle errors
            // (5) point FlashCardOps.flashListMM to the retrieved deck.

            // (1)
            HttpRequest req = getRequest(json, "/deck-s3-get");
            LOGGER.debug("deck S3get req built ... sending to Vertx...");
            try {
                  HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                  //  the signed URL
                  String sURL = response.body();
                  LOGGER.debug("response code {}", response.statusCode());
                  String failCheck = sURL.substring(0, 10);
                  if (!failCheck.contains("fail") && response.statusCode() == 200) {
                        LOGGER.debug("prior to parsing, response.body returns: <{}>", sURL);
                        sURL = sURL.substring(2, sURL.length() - 2);
                        // (2)
                        LOGGER.debug("response code {}", response.statusCode());
                        LOGGER.debug("response body: {}", sURL);  // signedUrl
                        File file = new File(pathName);
                        if (!file.exists()) {
                              FlashCardOps.getInstance().getDeckFolder().mkdirs();
                              boolean bool = file.createNewFile();
                              LOGGER.debug("File was created for new file in S3getObjs: <{}>", bool);
                        }
                        retrieveDeckHelper(file, sURL);
                        //@TODO do something when s == "failed":"true"
                  } else {
                        LOGGER.warn("WARNING!!! 0 objects found in s3 for key: {}", key);
                  }
            } catch (IOException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            } catch (SecurityException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            } catch (InterruptedException e) {
                  LOGGER.debug(e.getMessage());
            }
      }

      private void retrieveDeckHelper(File file, String signedurl) {
            LOGGER.debug("called retrieveDeckHelper( file, String)");
            URI fileToBeDownloaded = null;
            try {
                  fileToBeDownloaded = new URI(signedurl);
            } catch (URISyntaxException e) {
                  e.printStackTrace();
            }

            AmazonS3URI s3URI = new AmazonS3URI(fileToBeDownloaded);
            AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withRegion(s3URI.getRegion())
                .build();
            URL url;
            // download the deck to file
            try {
                  LOGGER.debug("Attempting to download the deck");
                  url = new URL(signedurl);
                  PresignedUrlDownloadRequest req = new PresignedUrlDownloadRequest(url);
                  client.download(req, file);
            } catch (MalformedURLException e) {
                  LOGGER.warn("ERROR when trying to download the deck. message: {}", e.getMessage());
                  e.printStackTrace();
            }
      }


      /**
       * Helper method for cloudGetDeckFmS3
       * Retrieves an object from the cloud using a signedUrl as the param
       * Creates An arraylist containing the deck elements used by the class
       *
       * @param key signedURL to access the S3 object.
       */
      private void retrieveDeckHelper(String key) {
            ArrayList<FlashCardMM> flashMM = new ArrayList<>();
            if (key.contains("fail")) {
                  LOGGER.warn("retrieveDeck failed! the key was empty.");
                  return;
            } else {
                  LOGGER.info("retrieveDeckHelper called. deck key: {}", key);
                  if (!Utility.isConnected()) {
                        LOGGER.warn("I am not connected... returning nothing");
                        return;// flashListMM;
                  }

                  //System.out.println("copyTo directory: " + FlashCardOps.getInstance().getDeckFolder() + "/" + FlashCardOps.getInstance().getFO().getFileName());

                  // once we get the signedURL, make the request using a separate thread.
                  //new Thread(() -> {
                  try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new URL(key).openStream()))) {
                        FlashCardOps.getInstance().setListinFile(flashMM, '+');
                  } catch (EOFException e) {
                        // do nothing, it is expected.

                  } catch (AmazonServiceException e) {
                        LOGGER.info("cloudGetDeck.Downloading failed. " +
                                "\n\tKey: {} " +
                                "\n\t{}"
                            , key, e.getMessage());
                        e.printStackTrace();
                        return;
                  } catch (Exception e) {
                        LOGGER.warn("\tUnknown Exception: in cloudGetDeck(...) \n" + e.getStackTrace());
                        e.printStackTrace();
                        return;
                  }
                  // save the deck to file
                  //FlashCardOps.getInstance().getFO().setListinFile(flashMM, '+');
                  //}).start();
            }
            LOGGER.debug("cloudGetDeck() returned flashListMM with {} cards", flashMM.size());
      }

      // ***************************************************************** //
      // ************************* MEDIA RELATED ************************* //
      // ***************************************************************** //

      private String localURL;
      private File diskFile;

      /**
       * <p>Communicates directly to S3 IAM. Retrieves an object
       * from S3 using a signedUrl as the param. Saves object to file.</p>
       * <p><b>NOTE:</b> Expects that the device is connected. Does
       * not check for a connection before use.</p>
       *
       * @param mediaObjs request list of media filesNames
       * @param token
       */
      public void serialGetMedia(ArrayList<MediaSyncObj> mediaObjs, String token) {
            long getTime = System.currentTimeMillis();

            // Ensure media directory exists or create it
            String toDiskDir = DirectoryMgr.getMediaPath('M');
            diskFile = new File(toDiskDir);
            FileOpsUtil.folderExists(diskFile);
            LOGGER.info("serialGetMedia called " +
                "\n mediaObjs.size: {}", mediaObjs.size());

            // 1st we get the signedURL's from Vert.x
            ArrayList<String> signedUrls = getMediaURLs(mediaObjs, token);

            // **** FOR TESTING ****
            if (signedUrls.size() != mediaObjs.size()) {
                  System.err.println("ERROR: S3GetObjs.serialGetMedia did not return the correct number of media objects");
                  LOGGER.debug("cloudGetMedia called. Returning <{}> signedURLs. Should be: <{}>", signedUrls.size(), mediaObjs.size());
            }

            // Process signedURLs
            for (String signedurl : signedUrls) {
                  LOGGER.debug("cloudGetMedia called. signedURL is null: {}", signedurl == null);
                  // make multiple requests using a threadPool.
                  //new Thread(() -> {
                  URI fileToBeDownloaded = null;
                  try {
                        fileToBeDownloaded = new URI(signedurl);
                  } catch (URISyntaxException e) {
                        e.printStackTrace();
                  }
                  //String cntStr = UserData.getUserName() + ReadFlash.getInstance().getDeckName();
                  //String cntStr = UserData.getUserName() + FlashCardOps.getInstance().getDeckFileName();
                  //int count = cntStr.length();
                  AmazonS3URI s3URI = new AmazonS3URI(fileToBeDownloaded);
                  LOGGER.debug("s3.getBucketName() {}, s3.getKey: {}", s3URI.getBucket(), s3URI.getKey());

                  int slash = s3URI.getKey().indexOf('/') + 1;
                  String key1 = s3URI.getKey().substring(slash);
                  localURL = toDiskDir + key1;

                  LOGGER.debug("The localURL: {}", localURL);

                  File file = new File(localURL);

                  AmazonS3 client = AmazonS3ClientBuilder.standard()
                      .withRegion(s3URI.getRegion())
                      .build();
                  URL url;
                  try {
                        url = new URL(signedurl);
                        PresignedUrlDownloadRequest req = new PresignedUrlDownloadRequest(url);
                        client.download(req, file);
                  } catch (MalformedURLException e) {
                        LOGGER.warn(e.getMessage());
                        e.printStackTrace();
                  } catch (AmazonS3Exception e) {
                        // requested user may not exist.
                        LOGGER.warn("ERROR: something wasn't right when attempting to download media", e.getMessage());
                        e.printStackTrace();
                  }
                  // }).start();
            }
      }

    /*
    public void cloudGetMedia(ArrayList<MediaSyncObj> mediaObjs, String token) {
        LOGGER.debug("called cloudGetMedia");
        // Ensure media directory exists or create it
        String toDiskDir = DirectoryMgr.getMediaPath( 'M');
        diskFile = new File(toDiskDir);
        FileOpsUtil.folderExists(diskFile);

        long getTime = System.currentTimeMillis();

        // 1st we get the signedURL's from Vert.x
        ArrayList<String> signedUrls = getMediaURLs(mediaObjs, token);

        forallChunked(0, signedUrls.size() - 1, (i) -> {
            try {
                fileToBeDownloaded = new URI(signedUrls.get(i));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String cntStr = UserData.getUserName() + ReadFlash.getInstance().getDeckName();
            int count = cntStr.length() + 2;
            AmazonS3URI s3URI = new AmazonS3URI(fileToBeDownloaded);
            LOGGER.debug("s3.getBucketName() {}, s3.getKey: {}", s3URI.getBucket(), s3URI.getKey());
            localURL = toDiskDir + "/" + s3URI.getKey().substring(count);
            File file = new File(localURL);

            AmazonS3 client = AmazonS3ClientBuilder.standard()
                    .withRegion(s3URI.getRegion())
                    .build();
            URL url;
            try{
                url =  new URL(signedUrls.get(i));
                PresignedUrlDownloadRequest req = new PresignedUrlDownloadRequest(url);
                client.download(req, file);
            }
            catch (MalformedURLException e) {
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
        });
        getTime = (System.currentTimeMillis() - getTime);
        System.out.print("Total get time in syncCloudMediaAction: {" + getTime + "} milliseconds, numElement: {" + signedUrls.size() + "}");

    }

     */

      /**
       * Returns an ArrayList of signedURLs
       *
       * @param mediaObjs
       * @param token
       * @return an arraylist of signed URLs
       */
      private ArrayList<String> getMediaURLs(ArrayList<MediaSyncObj> mediaObjs, String token) {
            // create array of download names
            String keys = getMediaKeys(mediaObjs);

            LOGGER.debug("getting keys: {}", keys);

            if (!Utility.isConnected()) {
                  LOGGER.warn("I am not connected... returning nothing");
                  return null;
            }
            // create a JsonArray with token and keys
            String json = "[{\"token\":\"" + token + "\"},{\"key\":\"" + keys + "\"}]";

            LOGGER.debug("The token should not be in JSON format. Token looks like: <{}>", token);
            LOGGER.debug("The JSON string looks like: " + json);

            HttpRequest req = getRequest(json, "/media-s3-get");
            LOGGER.debug("media S3get request built ... sending...");

            // send the name list to vertx
            try {
                  int count = 0;
                  String s = "";
                  while (s.isEmpty() && count < 5) {
                        HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                        s = response.body();
                        //s = s.substring(2, s.length() - 2);
                        LOGGER.debug("response code {}", response.statusCode());
                        //LOGGER.debug("response body: {}", s);  // signedUrl
                        if (s.isEmpty()) {
                              if (count >= 4) {
                                    LOGGER.warn("failed to download media URLs");
                                    return null;
                              }
                              Thread.sleep(200);
                        }
                        count++;
                  }

                  Set resSet = ProcessingUtility.httpParse(s);
                  // The hashSet is converted by the arrayList constructor. :)
                  return new ArrayList<>(resSet);


                  //@TODO do something on error "failed":"true"

            } catch (IOException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            } catch (InterruptedException e) {
                  LOGGER.debug(e.getMessage());
            }
            // @TODO complete getMediaURLS replace return
            return new ArrayList<String>();
      }

      /**
       * Builds the request keys for the mediaObjs provided
       *
       * @param mediaObjs
       * @return
       */
      private String getMediaKeys(ArrayList<MediaSyncObj> mediaObjs) {
            StringBuilder keyBuilder = new StringBuilder();
            String s3mediaSubDir = FileNaming.getMediaSubDir(FlashCardOps.getInstance().getFileName());
            // We do not add a comma on the last element
            for (int i = 0; i < mediaObjs.size() - 1; i++) {
                  keyBuilder.append(s3mediaSubDir + "/" + mediaObjs.get(i).getFileName() + ",");
            }
            // the last element with no comma.
            keyBuilder.append(s3mediaSubDir + "/" + mediaObjs.get(mediaObjs.size() - 1).getFileName());
            return keyBuilder.toString();
      }

      /**
       * Returns the HttpRequest
       *
       * @param json
       * @param destination
       * @return
       */
      private HttpRequest getRequest(String json, String destination) {
            return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(Connect.LINK.getLink() + destination))
                //@TODO set S3Creds to HTTPS
                .header("Content-Type", "application/json")
                .build();
      }


}
