package fileops;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.JsonArray;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class VertxIO {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(VertxIO.class);

    /**
     * Renew Token aka ou8123
     * @param token
     * @return
     */
    protected String ou8123(String token) {
        LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("S3Creds constructor called.");

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = "fail";

        //try {
            String code = genCode(token);
            //String integers = mapper.writeValueAsString(code);
            // create a JsonArray with token and keys
            json = "[{\"token\":\"" + token + "\"},{\"code\":\"" + code + "\"}]";

            LOGGER.debug(json.toString());


        //} catch (JsonProcessingException e) {
        //    LOGGER.warn("ERROR: {}", e.getMessage() );
        //    e.printStackTrace();
        //}
        String destination = "/FFFFFF"; // list
        return ou8123Helper(json, destination);
    }

    /**
     *
     * @param json
     * @param destination
     * @return returns the String token
     */
    private String ou8123Helper(String json, String destination) {
        LOGGER.debug("json: {}", json );
        LOGGER.debug("destination; {}", destination);

        final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // @todo change lochost:8080 to vertx call
        // vertx expects router.post("/resource-s3-list")
        // we post when using tokens:
        final HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                //@TODO set to HTTPS
                .uri(URI.create("http://localhost:8080" + destination))
                .header("Content-Type", "application/json")
                .build();
        try {
            final HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("response code {}", response.statusCode());

            if (response.statusCode() == 200) {
                String res = response.body();
                if( res.contains("token")) {
                    res = parse(response);

                    return res;
                }
            }
        } catch (JsonProcessingException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.debug(e.getMessage());
        }
        return "failed: true";
    }

    private String parse(HttpResponse<String> response) throws JsonProcessingException {
        // Get the token and remove it from the array.
        LOGGER.debug("original returned from HttpResponse: {}", response.body());

        int size = response.body().length();
        // remove start and end "[" "]"
        String res = response.body().substring(2, size - 3);
        // remove colon and next parans.
        res = res.substring(res.indexOf(":") + 2);
        // res = res

        LOGGER.debug("parsed response:  returning JSON: {}", res);
        //Map<String, Object> n = new ObjectMapper().readValue(res, Map.class);
        //LOGGER.debug("returned token:: {}",  n.get("token"));
        return res;
    }

    final byte[] ou812 = ou812M();
    final int[] primes = genPrimes();

    // Uses Image to create in app.
    private String genCode(String token) {
        long start = System.nanoTime();
        int num = token.lastIndexOf(".");
        String subToken = token.substring( num );
        char[] chars = subToken.toCharArray();

        //List<Integer> code = new ArrayList<>(32);
        StringBuilder sb = new StringBuilder();

        //System.out.println("ou812 length " + ou812.length);
        for(int i = 0 ; i < 32; i++) {
            int key = chars[i] * primes[i];
            //code.add(ou812[key] | 0);
            sb.append((ou812[key] | 0) + ",");

            //System.out.println("key: " + key + ", code[" + i + "]: " + code[i]);
        }
        long time = System.nanoTime() - start;

        System.out.println("Time in nanos: " + time);
        System.out.println("code: " + sb.toString());

        return sb.toString();
    }

    /**
     * Image used for encrypt/decrypt token reissue key
     * @return
     * @throws IOException
     */
    private byte[] ou812M() {
        byte[] imgAry = new byte[1];
        try {
            //Image image = new Image(getClass().getResourceAsStream("/image/eve_ai_e.jpg"));
            //BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            // new File("C:\\Users\\Me\\IdeaProjects\\flashmonkey-betaB\\src\\main\\resources\\image\\eve_ai_e.jpg")
            BufferedImage bImage = ImageIO.read(getClass().getResourceAsStream("/image/eve_ai_e.jpg"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            imgAry = bos.toByteArray();
            bos.close();
        } catch (IOException e) {
            LOGGER.debug("IOException: Could not find 82");
        }
        System.out.println("ou812M imgAry length: " + imgAry.length);
        return imgAry;
    }

    private int[] genPrimes() {
        int num =0;
        int[]  primeNumbers = new int[32];

        for (int i = 100, j = 0; j < 32; i++) {
            int counter=0;
            for(num =i; num>=1; num--) {
                if(i%num==0) {
                    counter = counter + 1;
                }
            }
            if (counter ==2) {
                //Appended the Prime number to the String
                primeNumbers[j] = i;
                j++;
            }
        }
        return primeNumbers;
    }

    private boolean isFailed(String res) {
        if(res == null | res.isEmpty()) {
            return true;
        }
        return false;
    }
}
