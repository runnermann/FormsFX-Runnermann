package fileops;


import authcrypt.UserData;
//import brachtendorf.jimagehash.hash.Hash;
//import brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
//import brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celleditors.VidSnapshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

/**
 * For media file names we are using a similar hash algorithm based on the images
 * that the user is saved. The file name is derived from the (for example) the
 * appearance of the image. Since most hashing algorithms change drastically by small
 * changes, we use a different hashing algorithm in order to tell if the files are similar.
 * See https://reposhub.com/java/imagery/KilianB-JImageHash.html
 * Credit to Kilian Brachtendorf for this hashing capability. MIT license
 * <p>
 * File Naming provides a unique name, and provides a location in local storage and S3.
 * Image and Video fileNames are the following syntax. UserHash_DeckFileName_MediaHash.mime
 * Shapes fileNames are UserHash_DeckFileName_MediaHash_millis.shp
 * DeckNames are the creatorHash_distributorHash$Deckname.dec
 *
 * @author Lowell Stadelman
 */

public class FileNaming {

      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FileNaming.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(FileNaming.class);

      // *** contains the resulting name  ***
      private String fileName;


      public FileNaming() { /* no args constructor */ }

      /**
       * <p>Creates a fileName for images, Use getImgFileName to retrieve the
       * fileName. Does not include the path.</p>
       *
       * @param hash   either the imageHash or prefered unique information for video and audio
       * @param bckt   The the file type, 'd' for deck, 'i' for image, 'v' video or audio.
       * @param ending The file type ending including the leading "."
       */
      public FileNaming(String hash, char bckt, String ending) {
            // if file is an image

            switch (bckt) {
                  case 'i':
                  case 'v': {
                        setMediaFileName(hash + bckt, ending);
                        break;
                  }
                  case 'd':
                  default: {
                        setDeckFileName(hash + bckt, ending);
                  }
            }
      }


      /**
       * Sanitizes the deckName and if the
       * remainder's length is greater than 6,
       * then returns a fileName for the deck.
       *
       * @param deckName
       * @return the name of the deck or default.dec if
       * does not meet the name conventions.
       */
      public static final String buildDeckFileName(String deckName) {
            int num = deckName.lastIndexOf('.');
            if (num > 0) {
                  deckName = deckName.substring(0, num);
            }
            // @TODO add unique file ID for deck
            // Sanitize the fileName
            deckName = FileNaming.nameSanitize(deckName);
            // hash the userName into hex
            String md5hashUserName = hashToHex(UserData.getUserName());

            String newName = "default.dec";
            if (deckName.length() > 5) {
                  newName = md5hashUserName + "_" + md5hashUserName + "$" + deckName + ".dec";
            }
            return newName;
      }

      public static String hashToHex(String s) {
            return DigestUtils
                .md5Hex(s);
      }

      /**
       * This method extracts the decks common s3 media sub directory name.
       * Ref s3 media storage: A decks media is always stored in the original hash
       * subdirectory. The parent deck, and the child decks share the same s3 Dir
       * allowing us to conserve memory and transfers when a deck is shared between
       * users. Decks do not share the same s3 location. The first hash is the
       * original deck hash, and the second hash is this child's hash. Media in s3 is stored
       * using the first hash in the deck file name.
       *
       * @param deckFileName
       * @return The common s3 media directory name
       */
      public static final String getMediaSubDir(String deckFileName) {
            String hash = deckFileName.substring(0, deckFileName.indexOf('_'));
            String deckName = deckFileName.substring(deckFileName.indexOf('$'), deckFileName.length() - 4);
            return hash + "_0" + deckName;
      }

      public static final String getQRFileName(String deckFileName) {
            return deckFileName.substring(0, deckFileName.length() - 4) + ".png";
      }


      /**
       * IMPORTANT: See notes on naming conventions for media.
       * <p>Creates a file name by using a similar hashing algorithm,
       * or the fileName of the deck. As of 2021-09-06, we are currently
       * hashing images. All other media should use the deckName and
       * a unique_identifier such as a time-stamp in millis.</p>
       *
       * @param hashOrUniqueName The hash of the image, or deckName and unique ID if not an image.
       * @param ending           preferably '.png' for images. Note: Include the '.' in the ending.
       */
      private void setDeckFileName(String hashOrUniqueName, String ending) {
            this.fileName = hashOrUniqueName + ending;
      }

      private void setMediaFileName(String mediaName, String ending) {
            this.fileName = mediaName + ending;
      }


      /**
       * Returns a hash from the image provided. Uses the image
       * to generate the hash. If two images are identical, this
       * will generate an identical hash.
       *
       * @param img
       * @return
       */
      public static String getImageHash(BufferedImage img) {
            LOGGER.debug("getImageHash called");

            HashingAlgorithm hasher = new PerceptiveHash(32);
            Hash hash = hasher.hash(img);
            LOGGER.debug("imagHash: {} ", bytesToHex(hash.toByteArray()));
            // convert to hex and add image identifier 'i' for Vertx
            return 'i' + bytesToHex(hash.toByteArray());
      }

      /**
       * Returns a hash from the image provided. Uses the image
       * to generate the hash. If two images are identical, this
       * will generate an identical hash.
       *
       * @param file The file containing the image.
       * @return
       */
      public static String getImageHash(File file) throws IOException {
            LOGGER.debug("getImageHash called");
            HashingAlgorithm hasher = new PerceptiveHash(32);
            Hash hash = hasher.hash(file);
            LOGGER.debug("imagHash: {} ", bytesToHex(hash.toByteArray()));

            return 'i' + bytesToHex(hash.toByteArray());
      }

      /**
       * The name of the image file
       *
       * @return Returns the media file Name
       */
      public final String getMediaFileName() {
            return fileName;
      }

      /**
       * Returns the shapeFilename including the letter identifier
       * at the beginning of the shapesFilename. Note that this
       * does not create a Hash. If associated with an image, use
       * imageHashName as shapesFileName
       *
       * @param shapeFilename
       * @return
       */
      public static String getShapesName(String shapeFilename) {
            String newName = shapeFilename.substring(1, shapeFilename.length() - 4);
            // NOTE: Using shapeName to set the rest of the name
            return shapeName(newName + "_" + System.currentTimeMillis());
      }

      public static String getShapesName(String deckName, String cID) {
            // NOTE: Using shapeName to set the rest of the name
            return shapeName(deckName + "_" + cID + "_" + System.currentTimeMillis());
      }

      private static String shapeName(String name) {
            return 'i' + name + "i.shp";
      }

      /**
       * //@TODO Implement an efficient method to create hashnames from video frames. See FFMPEG
       * <p>NOT USED until an efficient means to create hashnames from
       * frames is implemented. Current method is costly in memory and time.</p>
       * <p>NOTE: THis method has not been implemented yet.</p>
       * <p>Returns a hash from the video provided. Uses the image
       * to generate the hash. If two videos are identical, this
       * will generate an identical hash.
       *
       * @param source, the source name file
       * @return The hash of 4 images from the video. Taken at 5% 33% 66% and 95%
       */
      public static String getVideoHash(String source) {
            LOGGER.debug("getVideoHash called");
            File fi = new File(source);
            VidSnapshot snapshot = new VidSnapshot(fi);
            String s = source.substring(0, source.length() - 3) + "png";
            File f = new File(s);
            //Image inputImage = snapshot.getHashingImage();
            BufferedImage bImg = snapshot.getHashingImage();

            return getImageHash(bImg);
      }

      public static String getVideoName(String cID, char uOrL, String mime) {
            int num = cID.indexOf("_");
            return videoName(cID.substring(0, num) + uOrL + "_" + System.currentTimeMillis(), mime);
      }

      private static String videoName(String name, String mime) {
            return 'v' + name + "v." + mime;
      }

      /**
       * Returns a hash from the params provided.
       *
       * @param name, presumedly the deckName. Creates a
       *              unique name for this cHash or cID.
       * @return
       */
      public String getCardHash(String name, String algorith) {
            MessageDigest md;
            try {
                  md = MessageDigest.getInstance(algorith);
            } catch (NoSuchAlgorithmException e) {
                  throw new RuntimeException(algorith + " is not available");
            }
            String temp = name + System.currentTimeMillis();
            byte[] bHash = md.digest(temp.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(bHash);
      }


      /**
       * Helper method for getHash
       */
      private static String bytesToHex(byte[] bytes) {
            char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
            char[] chars = new char[bytes.length * 2];
            for (int i = 0; i < bytes.length; i++) {
                  int x = 0x0F & bytes[i];
                  chars[i * 2] = HEX_CHARS[x >>> 4];
                  chars[1 + i * 2] = HEX_CHARS[0x0F & x];
            }
            return new String(chars);
      }

      public static final String getDeckLabelName(String str) {
            int num = str.indexOf("$");
            return str.substring(0, num - 1);
      }

      /**
       * Filters a string and removes any non-legal identifiers.
       * Note that this removes almost everything except alpha numeric
       * characters including some characters which are legal working
       * java identifiers such as '@' and '$'.
       *
       * @param str
       * @return
       */
      public static final String nameSanitize(String str) {
            StringBuilder sb = new StringBuilder();

            str.codePoints()
                .filter(c -> {
                      if (sanitizeHelper(c)) {
                            return true;
                      } else {
                            return Character.isJavaIdentifierPart(c);
                      }
                }).forEach(sb::appendCodePoint);
            return sb.toString();
      }

      /**
       * any non character is deleted from the nameSanitize method.
       * If there are any characters that should be allowable, add
       * it in this method.
       *
       * @param c the character to be evaluated.
       * @return true if we allow this character, false otherwise.
       */
      private static final boolean sanitizeHelper(int c) {
            switch (c) {
                  case '_':
                  case '@':
                  case ' ': {
                        return true;
                  }
                  default: {
                        return false;
                  }
            }
      }

}
