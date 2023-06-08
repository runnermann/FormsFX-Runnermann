package fileops;


import authcrypt.FMToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public class VertxIO {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(VertxIO.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(VertxIO.class);

      public VertxIO() { /* no args constructor */ }

      /**
       * Generates the token and bonaFides, and formats message into JSON
       * @param x1 email
       * @param x2 password
       * @return
       */
      protected String ou812CreateNewUser(String x1, String x2) {
            // token
            String tkn = FMToken.createUserToken(x1, 4);
            // bonafides
            String bf = genCode(tkn);
            return "{\"x1\":\"" + x1 + "\",\"x4\":\"" + x2 + "\",\"x6\":\"" + tkn + "\",\"x3\":\"" + bf + "\"}";
      }

      /**
       * User create process.
       * Provides the necessary information to be sent to Vertx to create a user
       * based on Vertx bona-fides challenge q.
       * The JsonArray follows the following format
       * <p>x1: email, x2: code, x3: bona-fides, x4: password, x5: userName, x6: token</p>
       *
       * @param x1     Users orig_email
       * @param x2  The entered password
       * @param x3      Code entered by user
       * @param x4 firstName ..
       * @return Returns the json object String to be sent to Vertx.
       */
      protected String ou812confirm( String x1, String x2, String x3, String x4) {
            // token
            String tkn = FMToken.createUserToken(x1, 4);
            // Bonafides
            String bf = genCode(tkn);
            return "{\"x2\":\"" + x3 + "\",\"x1\":\"" + x1 + "\",\"x4\":\"" + x2 + "\",\"x5\":\"" + x4 + "\",\"x6\":\"" + tkn + "\",\"x3\":\"" + bf + "\"}";
      }


      /**
       * For Vertx Bona-Fides response to
       * change the users pw. We create the response
       * to upload it to Vertx.
       * If the response is 200 returns true. Else
       * returns false.
       * <p>x1: email, x2: code, x3: bona-fides, x4: password, x5: userName, x6: token</p>
       *
       * @param x1,   the users encypted original email.
       * @param x2 The new password. Encrypted
       * @param x3     The code provided by the user from form entry
       * @return Returns the json object String to be sent to Vertx.
       */
      protected String ou812reset(String x1, String x2, String x3) {
            String tkn = FMToken.createUserToken(x1, 4);
            String bf = genCode(tkn);

            return "{\"x2\":\"" + x3 + "\",\"x1\":\"" + x1 + "\",\"x4\":\"" + x2 + "\",\"x6\":\"" + tkn + "\",\"x3\":\"" + bf + "\"}";
      }

      protected String ou812cancel(String email, String password, String token) {
            String bonaFides = genCode(token);
            return "{\"x1\":\"" + email + "\",\"x4\":\"" + password + "\",\"x6\":\"" + token + "\",\"x3\":\"" + bonaFides + "\"}";
      }


      /**
       * For Vertx Bona-Fides response aka ou8123. A token
       * is used for a bona-fides question.
       * We create the response and return.
       *
       * @param token
       * @return response
       */
      protected String ou8123(String token) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("S3Creds constructor called.");

            String code = genCode(token);
            // create a JsonArray with token and keys
            String json = "[{\"token\":\"" + token + "\"},{\"code\":\"" + code + "\"}]";

            //LOGGER.debug(json.toString());

            String destination = "/FFFFFF"; // list
            return ou8123Helper(json, destination);
      }

      /**
       * @param json
       * @param destination
       * @return returns the String token
       */
      private String ou8123Helper(String json, String destination) {
            // @TODO remove ou8123Helper and use cloud ops
            LOGGER.debug("json: {}", json);
            LOGGER.debug("destination; {}", destination);

            final HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(7))
                .build();

            // vertx expects router.post("/resource-s3-list")
            // we post when using tokens:
            final HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                //@TODO set to HTTPS
                .uri(URI.create(Connect.LINK.getLink() + destination))
                .header("Content-Type", "application/json")
                .build();
            try {
                  final HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                  LOGGER.debug("response code {}", response.statusCode());

                  if (response.statusCode() == 200) {
                        String res = response.body();
                        if (res.contains("token") || res.equals("succeeded")) {
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

      // Uses Image to create response code.
      private String genCode(String token) {
            long start = System.nanoTime();
            int num = token.lastIndexOf(".");
            String subToken = token.substring(num);
            char[] chars = subToken.toCharArray();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < 32; i++) {
                  int key = chars[i] * primes[i];
                  sb.append((ou812[key] | 0) + ",");
            }
            long time = System.nanoTime() - start;
            String s = sb.toString();
            s.substring(0, s.length() - 1);
            return s;
      }

      /**
       * Image used for encrypt/decrypt token reissue key
       *
       * @return see method
       */
      private byte[] ou812M() {
            byte[] imgAry = new byte[1];
            try {
                  BufferedImage bImage = ImageIO.read(getClass().getResourceAsStream("/image/eve_ai_e.jpg"));
                  ByteArrayOutputStream bos = new ByteArrayOutputStream();
                  ImageIO.write(bImage, "jpg", bos);
                  imgAry = bos.toByteArray();
                  bos.close();
            } catch (IOException e) {
                  LOGGER.debug("IOException: Could not find 193");
            }
            return imgAry;
      }

      private int[] genPrimes() {
            int num = 0;
            int[] primeNumbers = new int[32];

            for (int i = 100, j = 0; j < 32; i++) {
                  int counter = 0;
                  for (num = i; num >= 1; num--) {
                        if (i % num == 0) {
                              counter = counter + 1;
                        }
                  }
                  if (counter == 2) {
                        //Appended the Prime number to the String
                        primeNumbers[j] = i;
                        j++;
                  }
            }
            return primeNumbers;
      }

      private boolean isFailed(String res) {
            return res == null | res.isEmpty();
      }
}
