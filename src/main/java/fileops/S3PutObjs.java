package fileops;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class S3PutObjs {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(S3PutObjs.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(S3PutObjs.class);
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public S3PutObjs() {
        /* do nothing */
    }


    public S3PutObjs(String token) {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("S3PutObjs constructor called");

        String json = "{" +
                "\"username\":\"" + authcrypt.UserData.getUserName() + "\"" +
                ",\"token\":\"" + token + "\"}";
        // we post when using credentials:
        HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080///resource-s3-put"))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/resource-s3-put"))
                .header("Content-Type", "application/json")
                .build();

        LOGGER.debug("S3put request built ... sending...");
        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

            LOGGER.debug("response code {}", response.statusCode());
            LOGGER.debug("response body: {}", response.body());
            //@TODO do something with the returned signedURLs :)
            //    urlList = response.body();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.debug(e.getMessage());
        }
    }

    public S3PutObjs(String username, String  pw) {
        // vertx call expects: router.post("/resource-s3-put")

        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("S3PutObjs constructor called");

        String json = "{" +
                "\"username\":\"" + username + "\"" +
                ",\"password\":\"" + pw + "\"}";

        LOGGER.debug("json string should contain name and pw {}", json);

        String jsonMsg;

        // we post when using passwords:
        HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080///resource-s3-put"))
                //@TODO set S3Creds to HTTPS
                //.uri(URI.create("https://localhost:8080/resource-s3-put"))
                .header("Content-Type", "application/json")
                .build();

        LOGGER.debug("S3Create request built ... sending...");
        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

            LOGGER.debug("response code {}", response.statusCode());
            LOGGER.debug("response body: {}", response.body());
            //@TODO do something with the returned signedURLs :)
        //    urlList = response.body();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.debug(e.getMessage());
        }
    }
}
