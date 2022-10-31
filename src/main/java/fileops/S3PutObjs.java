package fileops;

import authcrypt.UserData;
import com.amazonaws.services.s3.AmazonS3URI;
import com.fasterxml.jackson.core.JsonProcessingException;
import fileops.utility.FileExtension;
import fileops.utility.ProcessingUtility;
import fileops.utility.Utility;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;


public class S3PutObjs {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(S3PutObjs.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(S3PutObjs.class);


      public S3PutObjs() {
            //LOGGER.setLevel(Level.DEBUG);
            /* do nothing */
      }


      private String localURL;
      private final URI fileToBeUploaded = null;
      private File deckDir;

      /**
       * Communicates directly to S3 IAM. Sends an object
       * to S3 using a signedUrl.
       * <p><b>NOTE:</b> Expects that the device is connected. Does
       * not check for a connection before use.</p>
       *
       * @param mediaObjs uploaded media fileNames.
       * @param token
       */
      public boolean asyncPutMedia(ArrayList<MediaSyncObj> mediaObjs, String token) {
            LOGGER.debug("asyncPutMedia called");
            //long getTime = System.currentTimeMillis();
            boolean succeeded = false;
            // All media is stored in the same bucket in AWS
            // so we use 'c'
            String diskDir = DirectoryMgr.getMediaPath('C');
            deckDir = new File(diskDir);
            FileOpsUtil.folderExists(deckDir);

            // 1st we get the signedURL's from Vert.x
            ArrayList<String> signedUrls = getMediaPutURLs(mediaObjs, token);
            if ((signedUrls.size() < 1) || signedUrls.get(0).startsWith("fail")) {
                  LOGGER.warn("serialPutMedia failed");
                  return false;
            }
            succeeded = send(signedUrls);
            if (succeeded) {
                  LOGGER.debug("asyncSend succeded to send files");
            } else {
                  LOGGER.warn("asyncSend failed to send to s3");
            }
            return true;
      }

      /**
       * Serially uploads media to s3.
       *
       * @param fileNames
       * @param token
       */
      public void serialPutMedia(ArrayList<String> fileNames, String token) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("serialPutMedia called");
            boolean succeeded = false;
            //long getTime = System.currentTimeMillis();

            // 1st we request the signedURL's from our server
            ArrayList<String> signedUrls = getMediaPutURLsSerial(fileNames, token);
            if ((signedUrls.size() == 0) || signedUrls.get(0).startsWith("fail")) {
                  LOGGER.warn("serialPutMedia failed");
                  return;
            }
            send(signedUrls);
      }

      private boolean send(ArrayList<String> signedUrls) {
            int retry = 0;
            int response = 0;
            for (int i = 0; i < signedUrls.size(); i++) {
                  if (retry > 2) {
                        //if we've retried more than 3 times'
                        return false;
                  }
                  URI uploadS3URI = null;
                  try {
                        LOGGER.debug("singedUrls({}) == {}", signedUrls.get(i));
                        uploadS3URI = new URI(signedUrls.get(i));
                  } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return false;
                  }
                  String localURL = getNameFromS3URL(uploadS3URI);
                  if (localURL == null) {
                        return false;
                  }
                  String ending = localURL.substring(localURL.length() - 3);
                  if (FileExtension.IS_FX_IMAGE.check(ending)) {
                        // the content type to be uploaded, ie "image/jpg"
                        response = uploadURI(localURL, signedUrls.get(i), "image/" + ending);
                        if (response == -1) {
                              return false;
                        } else if (response == 0 && retry < 3) {
                              // retry
                              retry++;
                              i--;
                        }
                  }
                  // .mp4
                  else if (ending.equals("mp4")) {
                        // the content type to be uploaded, ie "image/jpg"
                         response = uploadURI(localURL, signedUrls.get(i), "media/mp4");
                        if (response == -1) {
                              return false;
                        } else if (response == 0 && retry < 3) {
                              // retry
                              retry++;
                              i--;
                        }
                  }
                  // .dec or .shp
                  else if (ending.equals("dec") || ending.equals(("shp"))) {
                        response = uploadURI(localURL, signedUrls.get(i), null);
                        if (response == 01) {
                              return false;
                        } else if (response == 0 && retry < 3) {
                              //retry
                              retry++;
                              i--;
                        }
                  }
            }
            return true;
      }

      private String getNameFromS3URL(URI s3PutURL) {
            AmazonS3URI s3URI = new AmazonS3URI(s3PutURL);
            LOGGER.debug("s3.getBucketName() {}, s3.getKey: {}", s3URI.getBucket(), s3URI.getKey());
            String str = s3URI.getKey();
            int count = str.lastIndexOf("/");
            String toDiskDir = DirectoryMgr.getMediaPath('M');
            String localURL = toDiskDir + s3URI.getKey().substring(count + 1);
            LOGGER.debug("toDiskDir: contains extra / ? ", toDiskDir);
            return localURL;
      }

      /**
       * If successful returns 1; else returns a 0 for retry
       * or -1 for failure.
       *
       * @param localURL
       * @param signedUrl
       * @param contentType
       * @return
       */
      private int uploadURI(String localURL, String signedUrl, String contentType) {
            try {
                  return outWriterUploader(localURL, signedUrl, contentType);
            } catch (MalformedURLException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            } catch (IOException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            }
            return -1;
      }


      /**
       * Uploads a deck to s3
       *
       * @param key
       * @param token
       */
      public void putDeck(String key, String token) {
            LOGGER.debug("putDeck called");
            //Thread.dumpStack();
            // get the file directory

            String toDiskDir = DirectoryMgr.getMediaPath('t');
            deckDir = new File(toDiskDir);
            if (!deckDir.exists()) {
                  LOGGER.warn("Directory {}, does not exist", deckDir);
                  System.exit(1);
            }

            // 1st we get the signedURL from Vert.x. Using an array
            // due to method output. Will only need first element.
            ArrayList<String> signedUrl = getDeckURL(key, token);

            if (signedUrl.isEmpty() || signedUrl.get(0).startsWith("fail")) {
                  LOGGER.warn("put deck failed: SignedUrls was empty or returned fail");
            } else {
                  try {
                        int response = outWriterUploader(localURL, signedUrl.get(0), null);
                        if (response == -1) {
                              LOGGER.warn("WARNING: deck failed to upload to S3");
                        } else if (response == 0) {
                              response = outWriterUploader(localURL, signedUrl.get(0), null);
                              if (response != 1) {
                                    String message = "Deck failed to upload";

                                    FxNotify.notification("", " Ooops! " + message, Pos.CENTER, 3,
                                    "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow());

                              }
                        }
                  } catch (MalformedURLException e) {
                        LOGGER.warn(e.getMessage());
                        e.printStackTrace();
                  } catch (IOException e) {
                        LOGGER.warn(e.getMessage());
                        e.printStackTrace();
                  }
            }
      }


      private boolean byteUploader(String localURL, HttpURLConnection conn) throws IOException {
            try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(localURL));
                 ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(conn.getOutputStream())))  // file output stream
            {
                  LOGGER.debug("S3put request built ... sending to s3...");

                  byte[] readBuffArr = new byte[4096];
                  int readBytes = 0;
                  while ((readBytes = bin.read(readBuffArr)) >= 0) {

                        out.write(readBuffArr, 0, readBytes);
                  }
                  out.flush();
                  conn.getResponseCode();
                  LOGGER.debug("response code: {}", conn.getResponseCode());
                  //conn.disconnect();
                  //conn.disconnect();
                  return conn.getResponseCode() == 200;
            } catch (FileNotFoundException e) {
                  LOGGER.warn("\tFile Not Found exception");
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            }
            return false;
      }

      /**
       * <p>Used for decks and images/media</p>
       * <p>Content type: "image/png" "", Returns false if connection is unsuccessful, or
       * if takes longer than 8 seconds to complete.</p>
       *
       * @param localURL
       * @param signedUrl
       * @param contentType
       * @return Returns -1 if other failure. 0 if timeout or slow connection, 1 if successful.
       * @throws IOException
       */
      private int outWriterUploader(String localURL, String signedUrl, String contentType) throws IOException {
            //Thread.dumpStack();

            int timeoutValue = 4000;
            try {
                  LOGGER.debug("signedUrl: {}", signedUrl);
                  URL url = new URL(signedUrl);
                  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                  conn.setDoOutput(true);
                  conn.setRequestMethod("PUT");
                  conn.setConnectTimeout(timeoutValue); // connection timeout for slow connections
                  conn.setReadTimeout(timeoutValue); //
                  if (contentType != null) {
                        conn.setRequestProperty("Content-Type", contentType);
                  }
                  //conn.setRequestProperty("Content-Type", "application/octet-stream");
                  conn.setInstanceFollowRedirects(false);

                  try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(localURL));
                       BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream()))  // file output stream
                  {
                        int numReads = 0;
                        byte[] readBuffArr = new byte[1024];
                        int readBytes = 0;
                        long timeout = System.currentTimeMillis() + timeoutValue;
                        boolean isCancelled = false;
                        while ((readBytes = in.read(readBuffArr)) >= 0) {
                              out.write(readBuffArr, 0, readBytes);
                              numReads++;
                              if (System.currentTimeMillis() > timeout) {
                                    return 0;
                              }
                        }
                        out.flush();
                        LOGGER.debug("Num reads: {}", numReads);
                        LOGGER.debug("response code: {}", conn.getResponseCode());
                        if (conn.getResponseCode() != 200) {
                              return 0;
                        }
                        conn.disconnect();
                  } catch (FileNotFoundException e) {
                        LOGGER.warn("\tFile Not Found exception");
                        LOGGER.warn(e.getMessage());
                        e.printStackTrace();
                  }
                  return 1;
                  //@TODO wait and retry if failed
            } catch (JsonProcessingException e) {
                  LOGGER.warn("ERROR: while parsing JSON to java message: {}", e.getMessage());
                  //e.printStackTrace();
                  return -1;
            } catch (IOException e) {
                  LOGGER.warn("IOException: " + e.getMessage());
                  e.printStackTrace();
                  return -1;
            }
      }


      // Didn't work. Problem with MD5 calculation from Vertx without the object that is being sent?

   /* private boolean s3ClientUploader(String signedurl, long fileLength) {
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .build();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileLength);
            // bucket name and location, objectKey (aka fileKey) is the deckName, file
            //if(file.length() > 6) {
                LOGGER.info("putting file to s3");

                //May need to use SigV2 or SigV4

            URL url = new URL(signedurl);
                PresignedUrlUploadRequest req = new PresignedUrlUploadRequest(url)
                        .withMetadata(metadata);
                        //.withPresignedUrl(url);
                s3client.upload(req);
                return true;

        } catch (AmazonServiceException e) {
            LOGGER.info("uploadFileTos3bucket().Uploading failed :" + e.getMessage());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }*/


      // ***** MEDIA RELATED ***** //


      /**
       * used to get the keys for media by the serial method.
       *
       * @param fileNames
       * @param notUsed   not used
       * @return Returns the keys to be consumed by getUrlsHelper.
       */
      private String getMediaKeys(ArrayList<String> fileNames, boolean notUsed) {
            StringBuilder keyBuilder = new StringBuilder();
            String s3mediaSubDir = FileNaming.getMediaSubDir(FlashCardOps.getInstance().getFileName());
            // we do not add a comma on the last element
            for (int i = 0; i < fileNames.size() - 1; i++) {
                  keyBuilder.append(s3mediaSubDir +
                      "/" + fileNames.get(i) + ",");
            }
            // get last item.
            keyBuilder.append(s3mediaSubDir +
                "/" + fileNames.get(fileNames.size() - 1));
            return keyBuilder.toString();
      }


      /**
       * Used for media
       *
       * @param mediaObjs
       * @return Returns the keys to be consumed by getUrlsHelper.
       */
      private String getMediaKeys(ArrayList<MediaSyncObj> mediaObjs) {
            StringBuilder keyBuilder = new StringBuilder();
            String s3mediaSubDir = FileNaming.getMediaSubDir(FlashCardOps.getInstance().getFileName());

            // we do not add a comma on the last element
            for (int i = 0; i < mediaObjs.size() - 1; i++) {
                  keyBuilder.append(s3mediaSubDir +
                      "/" + mediaObjs.get(i).getFileName() + ",");
            }
            keyBuilder.append(s3mediaSubDir +
                "/" + mediaObjs.get(mediaObjs.size() - 1).getFileName());
            return keyBuilder.toString();
      }

      private ArrayList<String> getMediaPutURLsSerial(ArrayList<String> fileNames, String token) {
            LOGGER.debug("getMediaPutURLs called");
            // cerate array of download names
            boolean bool = false;
            String keys = getMediaKeys(fileNames, bool);

            if (!Utility.isConnected()) {
                  LOGGER.warn("I am not connected... returning nothing");
                  return null;
            }
            // create a JsonArray with token and keys
            String json = "[{\"token\":\"" + token + "\"},{\"key\":\"" + keys + "\"}]";
            return getUrlsHelper(json, 'm');
      }


      /**
       * Returns a ArrayList of signedURLs for media uploads. Used by asyncUploadMedia
       *
       * @param mediaObjs
       * @param token
       * @return an arraylist of signed URLs
       */
      private ArrayList<String> getMediaPutURLs(ArrayList<MediaSyncObj> mediaObjs, String token) {
            // create array of download names
            LOGGER.debug("getMediaPutURLs called, next call to getUrlsHelper");
            String keys = getMediaKeys(mediaObjs);  // create getMediaKeys(deckKey)
            if (!Utility.isConnected()) {
                  LOGGER.warn("I am not connected... returning nothing");
                  return null;
            }
            // create a JsonArray with token and keys
            String json = "[{\"token\":\"" + token + "\"},{\"key\":\"" + keys + "\"}]";
            return getUrlsHelper(json, 'm');
      }


      // ****** DECK RELATED ****** //

      /**
       * Used for decks
       *
       * @param keyObj
       * @return Returns the key to be consumed by getUrlsHelper.
       */
      private String getDeckKey(String keyObj) {
            // xxxxxx create md5 hash and attach it to the end of the key after :
            String key = FileNaming.hashToHex(UserData.getUserName()) + "/decks/" + keyObj;

            return key;
      }


      /**
       * Returns an arraylist with one element containing the signedURL
       * for a deck upload.
       *
       * @param fileName
       * @param token
       * @return
       */
      private ArrayList<String> getDeckURL(String fileName, String token) {

            if (!Utility.isConnected()) {
                  LOGGER.warn("I am not connected... returning nothing");
                  return null;
            }
            String key = getDeckKey(fileName);
            String toDiskDir = DirectoryMgr.getMediaPath('t');
            localURL = toDiskDir + fileName;

            // remove the ending for the folder
            File file = new File(localURL);
            if (!file.exists() && !localURL.endsWith(".dec")) {
                  LOGGER.warn("Could not find file: " + file + " exiting ...");
            } else {
                  LOGGER.debug("getDeckURL called");
                  String base64Str = null;
                  try {
                        base64Str = Utility.md5AsBase64(new FileInputStream(file));
                  } catch (IOException e) {
                        LOGGER.warn("ERROR: IO Exception in getDeckURL");
                        e.printStackTrace();
                  }
                  String jsonArray = getDeckJsonArray(key, token, base64Str);
                  return getUrlsHelper(jsonArray, 't');
            }
            // should never get here.
            return null;
      }

      private String getDeckJsonArray(String key, String token, String md5hash) {
            // create a JsonArray with token and keys

            String jsonArray = "[{\"token\":\"" + token + "\"},{\"hash\":\"" + md5hash + "\"},{\"key\":\"" + key + "\"}]";


            LOGGER.debug("the keys look like: " + key);
            LOGGER.debug("The token should not be in JSON format. Token looks like: <{}>", token);
            LOGGER.debug("The JSON string looks like: " + jsonArray);
            return jsonArray; // one element jsonArray
      }

      /**
       * Returns an arrayList of presignedURLs, for media
       *
       * @param jsonArray contains an array with the neccessary information to get the
       *                  presignedUrls from s3. Should include the token.
       * @return Returns an arrayList of presignedURLs
       */
      private ArrayList<String> getUrlsHelper(String jsonArray, char type) {
            LOGGER.debug("getUrlsHelper called");
            final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(2))
                .build();

            HttpRequest req;
            if (type == 'm') {
                  req = mediaRequest(jsonArray);
                  LOGGER.debug("media S3put request built ... sending...");
            } else {
                  req = deckRequest(jsonArray);
                  LOGGER.debug("deck S3put request built ... sending...");
            }

            LOGGER.debug("sending: {}", jsonArray);

            // send the name list to vertx
            try {
                  HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                  String s = response.body();
                  //s = s.substring(2, s.length() - 2);
                  LOGGER.debug("response code {}", response.statusCode());
                  LOGGER.debug("response body: {}", s);  // signedUrl
                  if (response.body().length() > 1) {
                        Set resSet = ProcessingUtility.httpParse(s);
                        // The hashSet is converted by the arrayList constructor. :)
                        return new ArrayList<>(resSet);
                  } else {
                        LOGGER.warn("ERROR: WARING!!! putObject response returned a string less than 2 chars in length");
                  }

                  //@TODO do something on error "failed":"true"

            } catch (IOException e) {
                  LOGGER.warn(e.getMessage());
                  e.printStackTrace();
            } catch (InterruptedException e) {
                  LOGGER.debug(e.getMessage());
            }
            // failed return an empty string
            return new ArrayList<String>();
      }


      // ***** OTHER *****

      private HttpRequest deckRequest(String json) {
            return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(Connect.LINK.getLink() + "/deck-s3-put"))
                //@TODO set S3Creds to HTTPS
                .header("Content-Type", "application/json")
                .build();
      }


      private HttpRequest mediaRequest(String json) {
            return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(Connect.LINK.getLink() + "/media-s3-put"))
                //@TODO set S3Creds to HTTPS
                .header("Content-Type", "application/json")
                .build();
      }
}
