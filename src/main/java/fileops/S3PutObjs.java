package fileops;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3URI;
import com.fasterxml.jackson.core.JsonProcessingException;
import flashmonkey.ReadFlash;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * <p><b>NOTE:</b> Expects that the devide is connected. Does
     * not check for a connection before use.</p>
     * @param mediaObjs uploaded media fileNames.
     * @param token
     */
    public void asyncPutMedia(ArrayList<MediaSyncObj> mediaObjs, String token) {

        long getTime = System.currentTimeMillis();
        //LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("asyncPutMedia called");
        String diskDir = DirectoryMgr.getMediaPath( 'M');
        diskFile = new File(diskDir);
        FileOpsUtil.folderExists(diskFile);



        // 1st we get the signedURL's from Vert.x
        ArrayList<String> signedUrls = getMediaPutURLs(mediaObjs, token);
        // 2nd we send upload asynchronously.

        /*
        S3AsyncClient client = S3AsyncClient.builder()
                .region(S3_REGION)
                .build();

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        */


        forallChunked(0, signedUrls.size() - 1, (i) -> {
                    try {
                        LOGGER.debug("singnedUrls({}) == {}, the sixth char is illegal?", i, signedUrls.get(i));
                        fileToBeUploaded = new URI(signedUrls.get(i));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return;
                    }
                String cntStr = UserData.getUserName() + ReadFlash.getInstance().getDeckName();
                int count = cntStr.length() + 2;
                AmazonS3URI s3URI = new AmazonS3URI(fileToBeUploaded);
                LOGGER.debug("s3.getBucketName() {}, s3.getKey: {}", s3URI.getBucket(), s3URI.getKey());
                localURL = diskDir + "/" + s3URI.getKey().substring(count);

                try {

                    URL url = new URL(signedUrls.get(i));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("PUT");
                    // this is not an object. Use byteStream???
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(localURL));
                    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                    out.write(in.read());
                    out.close();
                    in.close();

                    connection.getResponseCode();

                    LOGGER.debug("Returned code after send: {}", connection.getResponseCode());


                    //@TODO wait and retry if failed
                } catch (JsonProcessingException e) {
                    LOGGER.warn("ERROR: while parsing JSON to java message: {}", e.getMessage());
                    //e.printStackTrace();
                } catch (IOException e) {
                    LOGGER.warn("IOException: " + e.getMessage());
                    e.printStackTrace();
                }
        });
        getTime = (System.currentTimeMillis() - getTime);
        System.out.print("Total get time in syncCloudMediaAction: {" + getTime + "} milliseconds, numElement: {" + signedUrls.size() + "}");
    }

    public void serialPutMedia(ArrayList<String> fileNames,int numFiles, String token) {
        long getTime = System.currentTimeMillis();
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("serialPutMedia called");
        // 1st we get the signedURL's from Vert.x
        ArrayList<String> signedUrls = getMediaPutURLs(fileNames, numFiles, token);
        if ((signedUrls.size() < 1) || signedUrls.get(0).substring(0, 4).equals("fail")) {
            LOGGER.warn("serialPutMedia failed");
            return;
        }

        String toDiskDir = DirectoryMgr.getMediaPath('M');

        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        try {
            HttpURLConnection connection;
            for (int i = 0; i < signedUrls.size(); i++) {
                URL url = new URL(signedUrls.get(i));
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("PUT");
                localURL = toDiskDir + "/" + fileNames.get(i);
                try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(new File(localURL)));
                     ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream())))  // file output stream
                {
                    LOGGER.debug("S3put request built ... sending to s3...");

                    // ------------------- new ---------------------- //
                    byte[] readBuffArr = new byte[4096];
                    int readBytes = 0;
                    while ((readBytes = bin.read(readBuffArr)) >= 0) {
                        out.write(readBuffArr, 0, readBytes);
                    }
                    connection.getResponseCode();
                    LOGGER.debug("response code: {}", connection.getResponseCode());

                } catch (FileNotFoundException e) {
                    LOGGER.warn("\tFile Not Found exception:  getObjFmFile() Line 986 FlashCard");
                    LOGGER.warn(e.getMessage());
                    e.printStackTrace();
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

    /**
     * Uploads a deck to s3
     * @param key
     * @param token
     */
    public void putDeck(String key, String token) {
        // get the file directory
        String toDiskDir = DirectoryMgr.getMediaPath( 't');
        diskFile = new File(toDiskDir);

        // 1st we get the signedURL from Vert.x
        ArrayList<String> signedUrls = getDeckURL(key, token);
        localURL = toDiskDir + "/" + key;
        File file = new File(localURL);

        // @TODO complete putDeck(...)
    }



    /**
     * Returns an arraylist with one element containing the signedURL
     * for a deck upload.
     * @param fileName
     * @param token
     * @return
     */
    private ArrayList<String> getDeckURL(String fileName, String token) {
        String keys = getKeys(fileName);
        return getUrlsHelper(keys, token);
    }

    private ArrayList<String> getMediaPutURLs(ArrayList<String> fileNames, int numFiles, String token) {
        // cerate array of download names
        String keys = getKeys(fileNames, numFiles);
        return getUrlsHelper(keys, token);
    }


    /**
     * Returns a ArrayList of signedURLs for media uploads. Used by asyncUploadMedia
     * @param mediaObjs
     * @param token
     * @return an arraylist of signed URLs
     */
    private ArrayList<String> getMediaPutURLs(ArrayList<MediaSyncObj> mediaObjs, String token) {
        // cerate array of download names
        String keys = getKeys(mediaObjs);  // create getKeys(deckKey)
        return getUrlsHelper(keys, token);
    }

    /**
     * REturns an arrayList of presignedURLs
     * @param keys
     * @param token
     * @return REturns an arrayList of presignedURLs
     */
    private ArrayList<String> getUrlsHelper(String keys, String token) {
        System.out.println("the keys look like: " + keys);
        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(2))
                .build();

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

    /**
     * used to get the keys for media by the serial method.
     * @param fileNames
     * @param numFiles not used
     * @return Returns the keys to be consumed by getUrlsHelper.
     */
    private String getKeys(ArrayList<String> fileNames, int numFiles) {
        StringBuilder keyBuilder = new StringBuilder();
        // we do not add a comma on the last element
        for(int i = 0; i < fileNames.size() - 1; i++) {
            keyBuilder.append(UserData.getUserName() + "/" + ReadFlash.getInstance().getDeckName() +
                    "/" + fileNames.get(i) + ",");
        }
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


    /**
     * Used for decks
     * @param keyObj
     * @return Returns the key to be consumed by getUrlsHelper.
     */
    private String getKeys(String keyObj) {
        return UserData.getUserName() + "/" + ReadFlash.getInstance().getDeckName() + "/" + keyObj;
    }



    private HttpRequest getRequest(String json) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/resource-s3-put"))
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
