package fileops;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import flashmonkey.FlashCardMM;
import flashmonkey.FlashCardOps;
import flashmonkey.ReadFlash;
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

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(S3GetObjs.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(S3PutObjs.class);
    private static final String BUCKET_NAME = "iooily-flashmonkey";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    /**
     * COnstructor
     */
    public S3GetObjs() {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("S3GetObjs constructor called");
    }

    // ************************* DECK RELATED ************************* //

    /**
     * <p>Retrieves a deck. This method
     * requests a signedURL for the user-selected deck. The first element
     * must contain the correct username and token.</p>
     * <p> End result is deck is saved to local file, and FlashListMM
     * is reset to the deck in ReadFlash. </p>
     * @param key
     * @param token
     */
    public void cloudGetDeckFmS3(String key, String token) {

        if(! Utility.isConnected()) {
            LOGGER.warn("I am not connected... returning nothing");
            return;// flashListMM;
        }
        // a jsonArray
        String json = "[{\"username\":\"" + authcrypt.UserData.getUserName() + "\",\"token\":\"" + token + "\"},{\"key\":\"" + key + "\"}]";
        LOGGER.debug("cloudGetDeckFmS3 called:userName: {}, key: <{}>, token <{}>", authcrypt.UserData.getUserName(),  key, token);

        // *** Request a signedURL for the deck. We post when using credentials: *** //
        // Retrieve the deck
        // (1) contact Vert.x and get the deck signedURL
        // (2) use signedURL to retrieve deck
        // (3) save deck to file
        // (4) handle errors
        // (5) point FlashCardOps.flashListMM to the retrieved deck.
        HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/resource-s3-get"))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/resource-s3-get"))
                .header("Content-Type", "application/json")
                .build();
        LOGGER.debug("deck S3get request built ... sending...");

        try {
            HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            String s = response.body();

            if(! s.contains("fail")) {
                LOGGER.debug("prior to parsing, response.body returns: {}", s);

                s = s.substring(2, s.length() - 2);
                LOGGER.debug("response code {}", response.statusCode());
                LOGGER.debug("response body: {}", s);  // signedUrl
                retrieveDeckHelper(s);
                //@TODO do something when s == "failed":"true"
            } else {
                LOGGER.warn("WARNING!!! 0 objects found in s3 for key: {}", key);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.debug(e.getMessage());
        }
    }



    /**
     * Helper method for cloudGetDeckFmS3
     * Retrieves an object from the cloud using a signedUrl as the param
     * @param key signedURL to access the S3 object.
     * @return An arraylist containing the deck elements.
     */
    private void retrieveDeckHelper(String key) {

        ArrayList<FlashCardMM> flashMM = new ArrayList<>();
        if(key.contains("fail")) {
            LOGGER.warn("retrieveDeck failed! the key was empty.");
            return;
        }
        else {
            LOGGER.info("cloudGetDeck called. deck key: {}", key);
            if (!Utility.isConnected()) {
                LOGGER.warn("I am not connected... returning nothing");
                return;// flashListMM;
            }

            //System.out.println("copyTo directory: " + FlashCardOps.getInstance().getDeckFolder() + "/" + FlashCardOps.getInstance().getFO().getFileName());

            // once we get the signedURL, make the request using a separate thread.
            new Thread(() -> {
                try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new URL(key).openStream()))) {
                    int i = 0;
                    while (flashMM.add(( FlashCardMM) input.readObject())) {
                       System.out.println(i++ + " items");
                    }
                   // ((ArrayList<FlashCardMM>) input.readObject());
                    //byte[] readBuffArr = new byte[4096];
                   // int readBytes = 0;
                    //while((readBytes = input.read(readBuffArr)) >= 0) {
                    //    bos.write(readBuffArr, 0, readBytes);
                    //}

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
                FlashCardOps.getInstance().getFO().setListinFile(flashMM, '+');
            }).start();
        }

        LOGGER.debug("cloudGetDeck() returned flashListMM with {} cards", flashMM.size());
    }

    // ************************* MEDIA RELATED ************************* //

    private String localURL;
    private File diskFile;
    /**
     * <p>Communicates directly to S3 IAM. Retrieves an object
     * from S3 using a signedUrl as the param. Saves object to file.</p>
     * <p><b>NOTE:</b> Expects that the device is connected. Does
     * not check for a connection before use.</p>
     * @param mediaObjs request list of media filesNames
     * @param token
     */
    public void serialGetMedia(ArrayList<MediaSyncObj> mediaObjs, String token) {
        long getTime = System.currentTimeMillis();

        // Ensure media directory exists or create it
        String toDiskDir = DirectoryMgr.getMediaPath( 'M');
        diskFile = new File(toDiskDir);
        FileOpsUtil.folderExists(diskFile);
        LOGGER.debug("cloudGetMedia line 192 called");

        // 1st we get the signedURL's from Vert.x
        ArrayList<String> signedUrls = getMediaURLs(mediaObjs, token);
        LOGGER.debug("cloudGetMedia called. signedURLs length: {}", signedUrls.size());
        // Process signedURLs
        for(String signedurl : signedUrls) {
            LOGGER.debug("cloudGetMedia called. signedURL is null: {}", signedurl == null);
            // make multiple requests using a threadPool.
            //new Thread(() -> {
                URI fileToBeDownloaded = null;
                try {
                    fileToBeDownloaded = new URI(signedurl);
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
                    url =  new URL(signedurl);
                    PresignedUrlDownloadRequest req = new PresignedUrlDownloadRequest(url);
                    client.download(req, file);
                }
                catch (MalformedURLException e) {
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }
            //}).start();
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
     * Returns a ArrayList of signedURLs
     * @param mediaObjs
     * @param token
     * @return an arraylist of signed URLs
     */
    private ArrayList<String> getMediaURLs(ArrayList<MediaSyncObj> mediaObjs, String token) {
        // cerate array of download names
        String keys = getKeys(mediaObjs);

        System.out.println("the keys look like: " + keys);

        if(! Utility.isConnected()) {
            LOGGER.warn("I am not connected... returning nothing");
            return null;
        }
        // create a JsonArray with token and keys
        String json = "[{\"token\":\"" + token + "\"},{\"key\":\"" + keys + "\"}]";

        System.out.println("The JSON string looks like: " + json);

        HttpRequest req = getRequest(json);
        LOGGER.debug("media S3get request built ... sending...");

        // send the name list to vertx
        try {
                HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                String s = response.body();
                //s = s.substring(2, s.length() - 2);
                LOGGER.debug("response code {}", response.statusCode());
                LOGGER.debug("response body: {}", s);  // signedUrl
                return httpParse(s);


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

    private HttpRequest getRequest(String json) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/media-s3-get"))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/media-s3-get"))
                .header("Content-Type", "application/json")
                .build();
    }

    private ArrayList<String> httpParse(String response) {
        LOGGER.debug("response: {}", response);
        String res = response.substring(1, response.length()-1);
        System.err.println("RESPONSE: " + res);
        String[] rAry = res.split(",");
        ArrayList<String> e = new ArrayList();
        for(int i = 0; i < rAry.length; i++) {
            e.add(rAry[i].substring(1, rAry[i].length() -1));
            System.out.println("element: " + rAry[i]);
        }
        return e;
    }
}
