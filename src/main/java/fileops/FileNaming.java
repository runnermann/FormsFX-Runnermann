package fileops;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * We assume that a Question ID or CardID number could already exist. This is due
 * to the problem that arises when cards are deleted from an array, and new cards
 * are added to the deck later. A cards ID does not change, however, if a qID is
 * the index when the card is created there is a possiblity that the card ID already
 * exists. Thus, when a new card is created, and files are created for images,
 * videos, or files, since they are associated with a cards ID, we must check that
 * a file with the same name does not already exist.
 *
 * For card editing, this is not the case, since that file should be replaced by
 * what the EncryptedUser.EncryptedUser is creating to replace it.
 *
 * @author Lowell Stadelman
 */

public class FileNaming
{
    // *** contains the resulting name  ***
    private String fileName;

    //private NumberFormat numFormat;
    public FileNaming() { /* no args constructor */ }
    
    /**
     * Creates a fileName, Use getFileName to retrieve the name
     * @param deckName
     * @param hash
     * @param qOrA
     * @param ending The file type ending including the leading "."
     */
    public FileNaming(String deckName, String hash, char qOrA, String ending)
    {
        setMediaFileName(deckName, hash, qOrA, ending);
    }
    
    
    /**
     * Creates a file name using the mediaNum, either a "q" or "a"
     * for question or answer, and the ending. Preferably '.png' for
     * images.
     * @param deckName
     * @param qOrA
     * @param ending preferably '.png' for images
     */
    private void setMediaFileName(String deckName, String cardHash, char qOrA, String ending)
    {
        //String deckName = ReadFlash.getDeckName();
        //String strNum = String.format("%04d", mediaNum);
        long num = System.currentTimeMillis();
        deckName = deckName.replace(" ", "");
        this.fileName = deckName.toLowerCase() + "_" + cardHash + "_" + num + qOrA + ending;
    }
    
    /**
     * Returns a hash from the params provided.
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
    
    
    // private final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    /**
     * Helper method for getHash
     */
    private static String bytesToHex(byte[] bytes) {
        char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
        char[] chars = new char[bytes.length * 2];
        for(int i = 0; i < bytes.length; i++) {
            int x = 0x0F & bytes[i];
            chars[i * 2] = HEX_CHARS[x >>> 4];
            chars[1 + i * 2] = HEX_CHARS[0x0F & x];
        }
        return new String(chars);
    }

    /**
     * The name of the image file
     * @return Returns the media file Name
     */
    public final String getFileName()
    {
        return fileName;
    }

}
