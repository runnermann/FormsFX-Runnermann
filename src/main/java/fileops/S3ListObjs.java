package fileops;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flashmonkey.FlashMonkeyMain;
import forms.utility.Alphabet;
import javafx.geometry.Pos;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>The S3ListDecks Class is responsible for:</p>
 * <p> 1) Fetching a list of decks that belong to the
 * app user based on the userName/originalEmail.</p>
 * <p>    a) Constructs the linkObjs used for selecting a deck
 * in the fileSelectPane.</p>
 * <p> 2) As this is expected to be the first call
 * to the Vertx Server. It also requests a validation
 * token or AuthN. The AuthN token is used on following
 * calls from the App such as getDeck, and get resources
 * which use signedURLs to fetch from S3. </p>
 * <p> The token is ultimately expected to be stored in
 * CloudOps as of the creation of this class</p>
 * @author Lowell Stadelman
 */
public class S3ListObjs {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(S3ListObjs.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(S3ListObjs.class);
    private String token;
    private static volatile ArrayList<CloudLink> cloudLinks;

    public S3ListObjs() { /* no args constructor */}

    /**
     * Sets the list of decks that are returned from s3.
     * If the pw/userName combo fails, Server returns
     * failed, true
     * @param name
     * @param pw
     * @return an int depending on if user exists and pw failed = 0, if password succeeded == 1, or user does not exist -1
     */
    public int listDecks(String name, String pw) {

        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("S3Creds constructor called.");

        String json = new String("{" +
                "\"username\":\"" + name + "\"" +
                ",\"password\":\"" + pw + "\"}");
        String destination = "deck-s3-333"; // list
        return listObjsHelper(json, destination);
    }

    /**
     * Sets the list of decks that are returned from s3. If the pw/userName combo fails, Server returns
     * the pw/userName combo fails, Server returns failed, true.
     * @param token
     * @return
     */
    public int listDecks(String token) {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("S3Creds constructor called for token.");
        String json = new String("{\"token\":\"" + token + "\"}");
        String destination = "decks-s3-747"; // token
        return listObjsHelper(json, destination);
    }


    public ArrayList<CloudLink> listMedia(String token, String deckName) {
        LOGGER.debug("listMedia called");
        String json = new String("{" +
                "\"token\":\"" + token + "\"" +
                ",\"deckname\":\"" + deckName + "\"}");
        String destination = "media-s3-list";
        listObjsHelper(json, destination);

        LOGGER.debug("listMedia returning <{}> names from media-s3", this.cloudLinks.size());

        return this.cloudLinks;
    }


    /**
     * @param json
     * @param destination
     * @return Returns 1 if successful, 0 if failed, -1 if failed bc of missing network.
     */
    private int listObjsHelper(String json, String destination) {
        LOGGER.debug("json: {}", json );
        LOGGER.debug("destination; {}", destination);

        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // vertx expects router.post("/resource-s3-list")
        // we post when using passwords:
        final HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/" + destination))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/resource-s3-list"))
                .header("Content-Type", "application/json")
                .build();
        try {
            final HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("response code {}", response.statusCode());

            if (response.statusCode() == 200 & response.body().length() > 5) {
                ArrayList<String> elements = parseResponse(response);
                // sets the token and removes it
                // from the array.
                if(! isFailed(elements)) {
                    elements = setToken(elements);
                    setCloudLinks(elements);
                    return 1;
                }
                // there was either a log in error == 0
                // or user did not exist == 0
                return 0;
            }
            //@TODO wait and retry if failed
        } catch (ConnectException e) {
            // failed, send user a notification
            LOGGER.warn("ConnectException: <{}> is unable to connect: Server not found", Alphabet.encrypt(UserData.getUserName()));
            return -1;
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
        return 0;
    }

    private boolean isFailed(ArrayList<String> elements) {
        return elements.get(0).contains("fail");
    }

    /* expects the caller to ensure there is data in the
     * response.
     */
    private ArrayList<String> parseResponse(HttpResponse<String> response) {
        int size = response.body().length();
        // remove start and end "[" "]"
        String res = response.body().substring(2, size - 2);
        String[] parts = res.split("},\\{");
        ArrayList<String> elements = new ArrayList<>(parts.length);

        LOGGER.debug("from split num parts: {}", parts.length);
        for(String s : parts) {
            elements.add("{" + s + "}");
        }
        LOGGER.debug("parseResponse returning {} elements", elements.size());
        return elements;
    }

    private ArrayList<String> setToken(ArrayList<String> elements) throws JsonProcessingException {
        // Get the token and remove it from the array.
        Map<String, Object> n = new ObjectMapper().readValue(elements.remove(elements.size() - 1), Map.class);
        LOGGER.debug("returned token:: {}",  n.get("token"));
        this.token = ((String) n.get("token"));
        return elements;
    }
    // Used to parse decks
    //Traced problem to here. How many elements are processed here???
    private void setCloudLinks(ArrayList<String> elements) throws JsonProcessingException {
        cloudLinks = new ArrayList<>();
        // DO NOT DELETE!!!!!!
        Map<String, Object> m = new HashMap<>();
        // Build the cloudlinks from the remaining JsonArray.
        for (String s : elements) {
            m = new ObjectMapper().readValue(s, Map.class);
            // prevent a delay to the cloud from setting it as a
            // younger element.
            long date = (Long) m.get("date") - 10000;
            cloudLinks.add(new CloudLink((String) m.get("name"), date, (Integer) m.get("size")));
            //LOGGER.debug("part from Vertx: {}", s);
        }

        System.out.println("adding elements to cloudlinks num = " + cloudLinks.size());
    }

    protected String getToken() {
        return this.token;
    }

    protected ArrayList<CloudLink> getCloudLinks() {
        return cloudLinks;
    }

}
