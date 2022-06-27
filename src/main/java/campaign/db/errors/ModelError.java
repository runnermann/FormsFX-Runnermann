package campaign.db.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <p>Singleton class</p>
 * <p>It is expected that the Main method of this class is used to encrypt the DB keys that will
 * be used to queries from the DB. Encryption should be done when a new set
 * of keys for the DB are created. The key may be placed in the Error class. After encrypted
 * two files are saved to the root. The .cpr file which contains the Cipher Encryption key ysed to
 * decrypt the s3 keys. The encrypted s3 keys are stored in the .enc file. 1) uncomment the main
 * class and the Error class. 2) replace the key with the new keys. 3) run the main method.
 * 4) comment out the main method and the Error class when completed.</p>
 * <p>
 * NOTE! Comment out the plain text keys from the Error class before shipping!!!!!
 * <p>
 * NOTE: if there is an error during runtime. Check that the keysfiles created are from the same
 * version. This is a serializable class. Files that are not built by the same version will cause a
 * runtime crash when Vertx is initialized.
 */
public class ModelError {
      //public static final long serialVersionUID = 20201112l;
      private static ModelError CLASS_INSTANCE;

      //private static final Logger LOGGER = LoggerFactory.getLogger(ModelError.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ModelError.class);

      private static final int IV_LENGTH_BYTE = 12;
      private static final int AES_KEY_BIT = 256;
      // stores the SecretKey and nonce used to decrypt the
      // s3 keys
      private final File cypherKeyFile = new File("zero.cpr");
      // stores the encrypted S3 keys
      private final File keysFile = new File("one.enc");


      private Dec dec;

      private ModelError() {
            // uncomment when creating files is completed.
            // comment out when creating files.
            init();
      }

      public static synchronized ModelError getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new ModelError();
            }
            return CLASS_INSTANCE;
      }


      // *** public methods used when app is deployed and operational ***//


      public boolean arrIsEmpty() {
            return dec.arr.length == 0;
      }

      private void init() {

            try {
                  this.setModelError();
            } catch (Exception e) {
                  //LOGGER.warn("CRITICAL ERROR!!!: ModelError threw exception in AppAccessVerticle {} \n{}", e.getMessage(), e.getStackTrace());
                  e.printStackTrace();
                  // System.exit(1);
            }
      }

      public String[] getS3Errors() {
            String[] sArr = new String[2];
            sArr[0] = dec.arr[0];
            //sArr[1] = dec.arr[1];
            return sArr;
      }

      public String[] getEpirtsErrors() {
            String[] sArr = new String[1];
            for (int i = 0, j = 0; i < 1; i++, j++) {
                  sArr[j] = dec.arr[i];
            }
            return sArr;
      }

      private void setModelError() throws Exception {
            Syekic icDec;
            InnerOps<Syekic> innoDec1 = new InnerOps<Syekic>();
            icDec = innoDec1.getSyekFile(cypherKeyFile.getName());

            // 2. retrieve keys and store in memory
            InnerOps<Syek> innoDec2 = new InnerOps<Syek>();
            // double byte array
            Syek syekDec = innoDec2.getSyekFile(keysFile.getName());

            // 2.a. The decrypted keys stored in memory.
            dec = new Dec();
            //LOGGER.debug("syekDec == null: " + (syekDec == null));
            //LOGGER.debug("icDec == null: " + (icDec == null));
            for (int i = 0; i < dec.arr.length; i++) {
                  dec.arr[i] = Util.decryptWithPrefixIV(syekDec.arr[i], icDec.one);
            }
      }


      // Used for saving the cipher key
      // to file. One is the AES/GCM SecretKey
      // two is the nonce;
      private static class Syekic implements Serializable {
            public static final long serialVersionUID = 20201112l;

            public Syekic() {/* No args constructor */}

            private SecretKey one;
            //private byte[] iv;
      }


      // one is db access key.
      // Used to store encrypted keys in file.
      private static class Syek implements Serializable {
            public Syek() {/* no args constructor */}

            public static final long serialVersionUID = 20201112l;
            private final byte[][] arr = new byte[1][];
      }


      // Stores the unencrypted key in memory. 0 is db Access String
      private static class Dec {
            String[] arr = new String[1];
      }


      private static class InnerOps<T> {
            //public static final long serialVersionUID = 20201112l;
            void createSyekFile(T syek, File m) {
                  //Thread.dumpStack();

                  //for(int i = 0; i < m.keyFiles.length; i++) {
                  try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("src/main/resources/" + m.getName()), 512))) {
                        //m.keyFiles[i].createNewFile();
                        out.writeObject(syek);
                  } catch (FileNotFoundException e) {
                        e.printStackTrace();
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
                  //}
            }

            /**
             * gets the encrypted keys from the enryptedFile.dat
             *
             * @param fileName
             * @return
             */

            T getSyekFile(String fileName) {
                  //if (new File("/" + fileName).exists()) {
                  T t = null;
                  try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(getClass().getResourceAsStream("/" + fileName)))) {
                        //System.out.println("File exists: /" + fileName);
                        while (true) {
                              t = (T) input.readObject();
                        }
                  } catch (EOFException e) {
                        // expected. do nothing
                  } catch (FileNotFoundException e) {
                        //LOGGER.warn(e.getMessage());
                  } catch (IOException e) {
                        //LOGGER.warn(e.getMessage());
                  } catch (ClassNotFoundException e) {
                        //LOGGER.warn(e.getMessage());
                  } finally {
                        return t;
                  }
            }
      }

      private static class Util {
            /**
             * AES-GCM inputs - 12 bytes IV, need the same IV and secret keys for encryption and decryption.
             * <p>
             * The output consist of iv, encrypted content, and auth tag in the following format:
             * output = byte[] {i i i c c c c c c ...}
             * <p>
             * i = IV bytes
             * c = content bytes (encrypted content, auth tag)
             */
            private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
            private static final int TAG_LENGTH_BIT = 128;
            private static final int IV_LENGTH_BYTE = 12;

            private static final Charset UTF_8 = StandardCharsets.UTF_8;

            // AES-GCM needs GCMParameterSpec
            private static byte[] encrypt(byte[] pText, SecretKey secret, byte[] iv) throws Exception {
                  Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
                  cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
                  byte[] encryptedText = cipher.doFinal(pText);
                  return encryptedText;
            }

            // prefix IV length + IV bytes to cipher text
            public static byte[] encryptWithPrefixIV(byte[] pText, SecretKey secret, byte[] iv) throws Exception {
                  byte[] cipherText = encrypt(pText, secret, iv);
                  byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + cipherText.length)
                      .put(iv)
                      .put(cipherText)
                      .array();
                  return cipherTextWithIv;
            }

            private static String decrypt(byte[] cText, SecretKey secret, byte[] iv) throws Exception {
                  Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
                  cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
                  byte[] plainText = cipher.doFinal(cText);
                  return new String(plainText, UTF_8);
            }

            public static String decryptWithPrefixIV(byte[] cText, SecretKey secret) throws Exception {
                  ByteBuffer bb = ByteBuffer.wrap(cText);
                  byte[] iv = new byte[IV_LENGTH_BYTE];
                  bb.get(iv);
                  //bb.get(iv, 0, iv.length);
                  byte[] cipherText = new byte[bb.remaining()];
                  bb.get(cipherText);
                  String plainText = decrypt(cipherText, secret, iv);
                  return plainText;
            }

            private static byte[] getRandomNonce(int numBytes) {
                  byte[] nonce = new byte[numBytes];
                  new SecureRandom().nextBytes(nonce);
                  return nonce;
            }

            // AES secret key
            private static SecretKey getAESKey(int keysize) throws NoSuchAlgorithmException {
                  KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                  keyGen.init(keysize, SecureRandom.getInstanceStrong());
                  return keyGen.generateKey();
            }

            // hex representation
            private static String hex(byte[] bytes) {
                  StringBuilder result = new StringBuilder();
                  for (byte b : bytes) {
                        result.append(String.format("%02x", b));
                  }
                  return result.toString();
            }

            // print hex with block size split
            private static String hexWithBlockSize(byte[] bytes, int blockSize) {
                  String hex = hex(bytes);
                  // one hex = 2 chars
                  blockSize = blockSize * 2;
                  // better idea how to print this?
                  List<String> result = new ArrayList<>();
                  int index = 0;
                  while (index < hex.length()) {
                        result.add(hex.substring(index, Math.min(index + blockSize, hex.length())));
                        index += blockSize;
                  }

                  return result.toString();
            }

      }

      /*  ******* DO BEFORE SHIPPING ******** */
      /* Uncomment to encrypt and save to file the DB access String */
    /*
    Note that the main method calls the class instance. Class instance calls init() that is
    used when the app is Started. init() is not needed for the main
    method. If the files do not exist, it will cause an error. Comment out init() while creating
    the files. Uncomment init() when done creating the files.
     */
/*    public static void main(String[] args) throws Exception {
       // *********** Encryption is conducted prior to a build or after creating a new IAM key for S3 *********** //
        String OUTPUT_FORMAT = "%-30s:%s";
        ModelError m = ModelError.getInstance();

        // 1. create SecretKey
        // encrypt and decrypt need the same key.
        // get AES 256 bits (32 bytes) key
        Syekic icEnc = new Syekic();
        icEnc.one = Util.getAESKey(AES_KEY_BIT);
        // 2. create IV
        // encrypt and decrypt need the same IV.
        // AES-GCM needs IV 96-bit (12 bytes)
        byte[] iv = Util.getRandomNonce(IV_LENGTH_BYTE);

        // 3. save secretKey and IV to file
        InnerOps<Syekic> innoEnc1 = new InnerOps<>();
        innoEnc1.createSyekFile(icEnc, m.cypherKeyFile);
        // 4. encrypt S3 & Stripe keys with Secret key & iv
        Syek syekEnc = new Syek();
        // s3 and Stripe accessKey, Cipher SecretKey and Cipher IV
        Error error = new Error();
        for(int i = 0; i < syekEnc.arr.length; i++) {
            syekEnc.arr[i] = Util.encryptWithPrefixIV(error.arr[i].getBytes(UTF_8), icEnc.one, iv);
        }
        // 5. save encrypted keys to file
        InnerOps<Syek> inno2 = new InnerOps<>();
        /// save array of keys to file
        inno2.createSyekFile(syekEnc, m.keysFile);


        // verify output is correct
        //System.out.println(String.format(OUTPUT_FORMAT, "User (hex)", Util.hex(syekEnc.arr[9])));

        // 1. retrieve the secretKey and IV
        Syekic icDec = new Syekic();
        InnerOps<Syekic> innoDec1 = new InnerOps<Syekic>();
        icDec = (Syekic) innoDec1.getSyekFile(m.cypherKeyFile.getName());
        // 2. retrieve s3 keys and store in memory
        InnerOps<Syek> innoDec2 = new InnerOps<Syek>();
        Syek syekDec = innoDec2.getSyekFile(m.keysFile.getName());

        // The decrypted s3 keys stored in memory.
        Dec dec = new Dec();

        for(int i = 0; i < dec.arr.length; i++) {
            dec.arr[i] = Util.decryptWithPrefixIV(syekDec.arr[i], icDec.one);
        }
    }



    private static class Error {

        private static final String[] arr = new String[1];
        // 0
        private static String dbAccessString = "jdbc:postgresql://usa-conus.caws1d0xah4s.us-west-2.rds.amazonaws.com:5432/usa-conus?user=lowell&password=ochCtirAwddThcatemHQzi2002";


        private Error() {
            buildArr();
        }

        // this is correct for decrypt as well???
        private String[] buildArr() {
            arr[0] = dbAccessString;
            return arr;
        }
    }*/
      //*/
}
