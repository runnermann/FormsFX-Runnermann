package metadata;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class MetadataUtility {

    private final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public String getDeckHash(String creatorEmail, String userEmail, Integer dateCreatedinSeconds) throws InvalidKeySpecException {
        return computeHash(creatorEmail, userEmail, dateCreatedinSeconds);
    }

    /**
     * Proprietary method for FlashMonkey website
     * Compute the hashed-password given an unhashed password and the salt
     *
     * @param password the unhashed password
     * @param salt     the salt
     * @param date     in seconds
     * @return the hashed password
     */
    public String computeHash(String password, String salt, Integer date) throws InvalidKeySpecException {

        final SecretKeyFactory skf;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("PBKDF2 is not available", nsae);
        }

        // Proprietary hashing
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt.getBytes(StandardCharsets.UTF_8),
                date,
                64 * 8);


        byte[] hash = skf.generateSecret(spec).getEncoded();
        return bytesToHex(hash);
    }


    String bytesToHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int x = 0xFF & bytes[i];
            chars[i * 2] = HEX_CHARS[x >>> 4];
            chars[1 + i * 2] = HEX_CHARS[0x0F & x];
        }
        return new String(chars);
    }
}
