package fileops;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PresignedUrlUploadRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.cj.xdevapi.JsonArray;
import flashmonkey.FlashCardOps;
import flashmonkey.ReadFlash;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;

//import software.amazon.awssdk.core.async.AsyncRequestBody;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3AsyncClient;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;
//import java.nio.file.Paths;
//import java.util.concurrent.CompletableFuture;

import static habanero.edu.rice.pcdp.PCDP.forallChunked;


public class S3PutObjs {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(S3PutObjs.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(S3PutObjs.class);
    private static final String BUCKET = "iooily-flashmonkey/";
    private static final Regions S3_REGION = Regions.US_WEST_2;
    // private static final Region S3_REGION = Region.US_WEST_2;


    public S3PutObjs() {
        LOGGER.setLevel(Level.DEBUG);
        /* do nothing */
    }


    private String localURL;
    private URI fileToBeUploaded = null;
    private File diskFile;
    /**
     * Communicates directly to S3 IAM. Sends an object
     * to S3 using a signedUrl.
     * <p><b>NOTE:</b> Expects that the device is connected. Does
     * not check for a connection before use.</p>
     * @param mediaObjs uploaded media fileNames.
     * @param token
     */
    public boolean asyncPutMedia(ArrayList<MediaSyncObj> mediaObjs, String token) {
        System.out.println("Dumping stack in asyncPutMedia");
        Thread.dumpStack();


        LOGGER.debug("asyncPutMedia called");
        long getTime = System.currentTimeMillis();

        String diskDir = DirectoryMgr.getMediaPath( 'M');
        diskFile = new File(diskDir);
        FileOpsUtil.folderExists(diskFile);

        // 1st we get the signedURL's from Vert.x
        ArrayList<String> signedUrls = getMediaPutURLs(mediaObjs, token);
        // 2nd we send upload asynchronously.

        //forallChunked(0, signedUrls.size() - 1, (i) -> {

        for(int i = 0; i < signedUrls.size(); i++) {
            //int finalI = i;
            //new Thread(() -> {
                try {
                    LOGGER.debug("singedUrls({}) == {}", i, signedUrls.get(i));
                    fileToBeUploaded = new URI(signedUrls.get(i));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return false;
                }
                // garauntee we get the correct name from the correct item even during
                // multi-threaded multi-core processing
                String cntStr = UserData.getUserName() + ReadFlash.getInstance().getDeckName();
                int count = cntStr.length() + 2;
                AmazonS3URI s3URI = new AmazonS3URI(fileToBeUploaded);
                LOGGER.debug("s3.getBucketName() {}, s3.getKey: {}", s3URI.getBucket(), s3URI.getKey());
                localURL = diskDir + "/" + s3URI.getKey().substring(count);
                File f = new File(localURL);
                /*if(f.exists()) {
                    LOGGER.warn("File:    <{}>    does not exist. Ending ...", f.getName());
                    System.exit(1);
                }*/

                try {

                    URL url = new URL(signedUrls.get(i));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("PUT");
                    conn.setInstanceFollowRedirects(false);
                    try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(new File(localURL)));
                         ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(conn.getOutputStream())))  // file output stream
                    {
                        byte[] readBuffArr = new byte[4096];
                        int readBytes = 0;
                        while ((readBytes = bin.read(readBuffArr)) >= 0) {
                            out.write(readBuffArr, 0, readBytes);
                        }
                        out.flush();

                        LOGGER.debug("response code: {}", conn.getResponseCode());
                        if(conn.getResponseCode() != 200) {
                            return false;
                        } else {
                            //return true;
                        }
                        //conn.disconnect();
                    } catch (FileNotFoundException e) {
                        LOGGER.warn("\tFile Not Found exception");
                        LOGGER.warn(e.getMessage());
                        e.printStackTrace();
                    }
                    //@TODO wait and retry if failed
                } catch (JsonProcessingException e) {
                    LOGGER.warn("ERROR: while parsing JSON to java message: {}", e.getMessage());
                    //e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    LOGGER.warn("IOException: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                } finally {
                    getTime = (System.currentTimeMillis() - getTime);
                    System.out.print("Total get time in syncCloudMediaAction: {" + getTime + "} milliseconds, numElement: {" + signedUrls.size() + "}");
                }
            //}).start();
        }
        return true;
    }

    public void serialPutMedia(ArrayList<String> fileNames, String token) {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("serialPutMedia called");
        boolean succeeded = false;
        long getTime = System.currentTimeMillis();

        // 1st we request the signedURL's from our server
        ArrayList<String> signedUrls = getMediaPutURLsSerial(fileNames, token);
        if ((signedUrls.size() < 1) || signedUrls.get(0).substring(0, 4).equals("fail")) {
            LOGGER.warn("serialPutMedia failed");
            return;
        }

        String toDiskDir = DirectoryMgr.getMediaPath('M');

        try {
            HttpURLConnection conn;
            for (int i = 0; i < signedUrls.size(); i++) {
                URL url = new URL(signedUrls.get(i));
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                conn.setInstanceFollowRedirects(false);
                localURL = toDiskDir + "/" + fileNames.get(i);
                // Note that using byte uploader may cause image downloading to be a problem if it is
                // using object download
                succeeded = byteUploader(localURL, conn);
                if(! succeeded) {
                    LOGGER.warn("WARNING: <{}> failed to upload to S3", localURL);
                }
                conn.disconnect();
            }
        } catch (MalformedURLException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        }
        getTime = (System.currentTimeMillis() - getTime);
        System.out.print("Total get time in syncCloudMediaAction: {" + getTime + "} milliseconds, numElement: {" + signedUrls.size() + "}");
    }

    /**
     * Uploads a deck to s3
     * @param key
     * @param token
     */
    public void putDeck(String key, String token) {
        LOGGER.debug("putDeck called");
        // get the file directory
        boolean succeeded = false;
        String toDiskDir = DirectoryMgr.getMediaPath('t');
        diskFile = new File(toDiskDir);
        if(!diskFile.exists()) {
            LOGGER.warn("FIle {}, does not exist", diskFile);
            System.exit(1);
        }

        // 1st we get the signedURL from Vert.x. Using an array
        // due to method output. Will only need first element.
        ArrayList<String> signedUrl = getDeckURL(key, token);
        //outWriterUploader(signedUrl.get(0));

        if(signedUrl.isEmpty() || signedUrl.get(0).startsWith("fail")) {
            LOGGER.warn("put deck failed: SignedUrls was empty or returned fail");
        }
        else {
            try {
                succeeded = outWriterUploader(signedUrl.get(0));
                if (!succeeded) {
                    LOGGER.warn("WARNING: deck failed to upload to S3");
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

            try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(new File(localURL)));
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
                if(conn.getResponseCode() != 200) {
                    //conn.disconnect();
                    return false;
                } else {
                    //conn.disconnect();
                    return true;
                }
            } catch (FileNotFoundException e) {
                LOGGER.warn("\tFile Not Found exception");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            return false;
    }

    private boolean outWriterUploader(String signedUrl) throws IOException {
        try {
            LOGGER.debug("signedUrl: {}", signedUrl);
            URL url = new URL(signedUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            //conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setInstanceFollowRedirects(false);

            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(localURL)));
                 BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream()))  // file output stream
            {
                int numReads = 0;
                byte[] readBuffArr = new byte[512];
                int readBytes = 0;
                while ((readBytes = in.read(readBuffArr)) >= 0) {
                    out.write(readBuffArr, 0, readBytes);
                    numReads++;
                }
                out.flush();
                LOGGER.debug("Num reads: {}", numReads);
                LOGGER.debug("response code: {}", conn.getResponseCode());
                if(conn.getResponseCode() != 200) {
                    return false;
                } else {
                    //return true;
                }
                conn.disconnect();
            } catch (FileNotFoundException e) {
                LOGGER.warn("\tFile Not Found exception");
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            return true;
            //@TODO wait and retry if failed
        } catch (JsonProcessingException e) {
            LOGGER.warn("ERROR: while parsing JSON to java message: {}", e.getMessage());
            //e.printStackTrace();
            return false;
        } catch (IOException e) {
            LOGGER.warn("IOException: " + e.getMessage());
            e.printStackTrace();
            return false;
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
     * @param fileNames
     * @param notUsed not used
     * @return Returns the keys to be consumed by getUrlsHelper.
     */
    private String getKeys(ArrayList<String> fileNames, boolean notUsed) {
        StringBuilder keyBuilder = new StringBuilder();
        // we do not add a comma on the last element
        for(int i = 0; i < fileNames.size() - 1; i++) {
            keyBuilder.append(UserData.getUserName() + "/" + ReadFlash.getInstance().getDeckName() +
                    "/" + fileNames.get(i) + ",");
        }
        // get last item.
        keyBuilder.append(UserData.getUserName() + "/" + ReadFlash.getInstance().getDeckName() +
                "/" + fileNames.get(fileNames.size() - 1));
        return keyBuilder.toString();
    }


    /**
     * Used for media
     * @param mediaObjs
     * @return Returns the keys to be consumed by getUrlsHelper.
     */
    private String getKeys(ArrayList<MediaSyncObj> mediaObjs) {
        StringBuilder keyBuilder = new StringBuilder();
        // we do not add a comma on the last element
        for(int i = 0; i < mediaObjs.size() - 1; i++) {
            keyBuilder.append( UserData.getUserName() + "/" + ReadFlash.getInstance().getDeckName() +
                    "/" + mediaObjs.get(i).getFileName() + ",");
        }
        keyBuilder.append(UserData.getUserName() + "/" + ReadFlash.getInstance().getDeckName() +
                "/" + mediaObjs.get(mediaObjs.size() - 1).getFileName());
        return keyBuilder.toString();
    }

    private ArrayList<String> getMediaPutURLsSerial(ArrayList<String> fileNames, String token) {
        LOGGER.debug("getMediaPutURLs called");
        // cerate array of download names
        boolean bool = false;
        String keys = getKeys(fileNames, bool);

        if(! Utility.isConnected()) {
            LOGGER.warn("I am not connected... returning nothing");
            return null;
        }
        // create a JsonArray with token and keys
        String json = "[{\"token\":\"" + token + "\"},{\"key\":\"" + keys + "\"}]";

        return getUrlsHelper(json, 'm');
    }


    /**
     * Returns a ArrayList of signedURLs for media uploads. Used by asyncUploadMedia
     * @param mediaObjs
     * @param token
     * @return an arraylist of signed URLs
     */
    private ArrayList<String> getMediaPutURLs(ArrayList<MediaSyncObj> mediaObjs, String token) {
        // create array of download names
        LOGGER.debug("getMediaPutURLs called, next call to getUrlsHelper");
        String keys = getKeys(mediaObjs);  // create getKeys(deckKey)
        if(! Utility.isConnected()) {
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
     * @param keyObj
     * @return Returns the key to be consumed by getUrlsHelper.
     */
    private String getDeckKey(String keyObj) {
        // xxxxxx create md5 hash and attatch it to the end of the key after :
        String key = UserData.getUserName() + "/decks/" + keyObj;

        return key;
    }


    /**
     * Returns an arraylist with one element containing the signedURL
     * for a deck upload.
     * @param fileName
     * @param token
     * @return
     */
    private ArrayList<String> getDeckURL(String fileName, String token) {
        if(! Utility.isConnected()) {
            LOGGER.warn("I am not connected... returning nothing");
            return null;
        }
        String key = getDeckKey(fileName);
        String toDiskDir = DirectoryMgr.getMediaPath('t');
        localURL = toDiskDir + fileName;
        File file = new File(localURL);
        if (!file.exists()) {
            System.out.println("Could not find file: " + file + " exiting ...");
            System.exit(1);
        }
        else {
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

        // create a JsonArray with token and keys
        String jsonArray = "[{\"token\":\"" + token + "\"},{\"hash\":\"" + md5hash + "\"},{\"key\":\"" + key + "\"}]";


        LOGGER.debug("the keys look like: " + key);
        LOGGER.debug("The token should not be in JSON format. Token looks like: <{}>", token);
        LOGGER.debug("The JSON string looks like: " + jsonArray);
        return jsonArray; // one element jsonArray
    }

    /**
     * Returns an arrayList of presignedURLs, for media
     * @param jsonArray contains an array with the neccessary information to get the
     *                  presignedUrls from s3. Should include the token.
     * @return REturns an arrayList of presignedURLs
     */
    private ArrayList<String> getUrlsHelper(String jsonArray, char type) {
        LOGGER.debug("getUrlsHelper called");
        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        HttpRequest req = null;
        if(type == 'm') {
            req = mediaRequest(jsonArray);
            LOGGER.debug("media S3put request built ... sending...");
        }
        else {
            req = deckRequest(jsonArray);
            LOGGER.debug("deck S3put request built ... sending...");
        }


        // send the name list to vertx
        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            String s = response.body();
            //s = s.substring(2, s.length() - 2);
            LOGGER.debug("response code {}", response.statusCode());
            LOGGER.debug("response body: {}", s);  // signedUrl
            if(response.body().length() > 1) {
                return httpParse(s);
            }
            else {
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


 /*   private String getDeckMd5hash(String deckName) {
        String strPath = fileops.DirectoryMgr.getMediaPath('t');
        return DigestUtils
                .md5Hex(strPath + deckName).toUpperCase();
    }

  */


    // ***** OTHER *****

    private HttpRequest deckRequest(String json) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/deck-s3-put"))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/media-s3-get"))
                .header("Content-Type", "application/json")
                .build();
    }



    private HttpRequest mediaRequest(String json) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/media-s3-put"))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/media-s3-get"))
                .header("Content-Type", "application/json")
                .build();
    }


    private ArrayList<String> httpParse(String response) {
        LOGGER.debug("response: {}", response);
        String res = response.substring(1, response.length()-1);
        LOGGER.debug("RESPONSE: " + res);
        String[] rAry = res.split(",");
        ArrayList<String> e = new ArrayList();
        for(int i = 0; i < rAry.length; i++) {
            e.add(rAry[i].substring(1, rAry[i].length() -1));
            System.out.println("element: " + rAry[i]);
        }
        return e;
    }
}
