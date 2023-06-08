package authcrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 *
 */
public class AuthUtility {

      private static final Logger LOGGER = LoggerFactory.getLogger(AuthUtility.class);

      /**
       * Compute the hashed-str given a str and salt
       *
       * @param str  the unhashed str
       * @param salt the salt
       * @param alg  algorithm
       * @return the hashed str or null
       */
      public String computeHash(String str, String salt, String alg) {
            // check if null or empty
            if (checkStr(str)) {
                  return "fail: string is null or empty";
            }
            if (checkStr(salt)) {
                  return "fail: salt is null or empty";
            }

            final SecretKeyFactory skf;
            try {
                  skf = SecretKeyFactory.getInstance(alg);
            } catch (NoSuchAlgorithmException nsae) {
                  LOGGER.warn("NoSuchAlgorithm: {}", nsae.getMessage());
                  throw new RuntimeException(alg + " is not available", nsae);
            }

            // This Algorithm is different from FM website
            // Pay attention not to duplicate it here.
            PBEKeySpec spec = new PBEKeySpec(
                str.toCharArray(),
                salt.getBytes(StandardCharsets.UTF_8),
                512,
                64 * 8);
            return hash(skf, spec);
      }

      /**
       * Calculates the hash for non-crypto hashing
       *
       * @param str string
       * @return hash
       */
      public String computeHash(String str) {
            final Base64.Encoder B64ENC = Base64.getEncoder();
            final MessageDigest md;

            try {
                  md = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException nsae) {
                  throw new RuntimeException("SHA-1 is not available", nsae);
            }

            return B64ENC.encodeToString(md.digest(str.getBytes(StandardCharsets.UTF_8)));
      }

      /**
       * Returns the hash from the input parameters
       *
       * @param skf  ..
       * @param spec ..
       * @return hash
       */
      private String hash(SecretKeyFactory skf, PBEKeySpec spec) {
            try {
                  byte[] hash = skf.generateSecret(spec).getEncoded();
                  return bytesToHex(hash);

            } catch (InvalidKeySpecException e) {
                  LOGGER.warn("InvalidKeySpecException during computeHash");
                  String strg = "3362EA02CBEFB118654A38D445118C32526EED307BEA2637B1E51B9CF3251078";
                  byte[] hash = strg.getBytes();
                  return bytesToHex(hash);
            }
      }

      /**
       * check if a string meets the correct length or is null
       *
       * @param str ..
       * @return true if correct
       */
      private boolean checkStr(String str) {
            return (str == null || str.length() < 8);
      }

      String bytesToHex(byte[] bytes) {

            final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

            char[] chars = new char[bytes.length * 2];
            for (int i = 0; i < bytes.length; i++) {
                  int x = 0xFF & bytes[i];
                  chars[i * 2] = HEX_CHARS[x >>> 4];
                  chars[1 + i * 2] = HEX_CHARS[0x0F & x];
            }
            return new String(chars);
      }

      String generateSalt() {
            final PRNG random = new PRNG();
            byte[] salt = new byte[32];
            random.nextBytes(salt);

            return bytesToHex(salt);
      }


      // *** INNER CLASS ***
      private class PRNG {

            SecureRandom random;

            private PRNG() {
                  random = new SecureRandom();
                  random.nextBytes(new byte[1]);
            }


            public void nextBytes(byte[] bytes) {
                  if (bytes != null) {
                        random.nextBytes(bytes);
                  }
            }

            public int nextInt() {
                  final int rand = random.nextInt();
                  return rand;
            }

            public int nextInt(final int bound) {
                  final int rand = random.nextInt(bound);
                  return rand;
            }
      }

      // Test hashing userName
    /*
    public static void main(String ... args) {

        AuthUtility ut = new AuthUtility();

            int returned = "idk@flashmonkey.co".h
            hCode();
        System.out.println("hash: " + returned);
    }
     */
}
