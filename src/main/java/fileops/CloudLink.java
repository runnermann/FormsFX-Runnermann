package fileops;


import flashmonkey.FlashCardOps;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A handle class to ambiguate files that are either
 * remote or local. Used in AGRFileList. Set in
 * CloudOps and AGRFileList. If File is local,
 * set remote to false. If file is remote.. then
 * true.
 */
public class CloudLink implements Comparable<CloudLink> {
    // The key returned from S3
    private String key;
    // The name of the file for display
    private String name;
    // The date in millis
    private long dateInMillis;
    private long size;
    
    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CloudLink.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudLink.class);
    
    
    public CloudLink(String name, long dateInMillis, long size) {
        // The file name
        this.setName(name);
        this.dateInMillis = dateInMillis;
        // the signedURL
        this.key = name;
        this.size = size;
    }
    
    public CloudLink(CloudLink other) {
        this.setName(other.name);
        this.dateInMillis = other.dateInMillis;
        this.key = other.key;
        this.size = other.size;
    }
    
    //public String getKey() { return key; }
    
    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setName(String name) {
        int num = name.lastIndexOf("/") + 1;
        name = name.substring(num);
        this.name = name;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public void setKey(String key) {
        LOGGER.debug("key: {}", key);
        this.key = key;
    }
    
    public String getKey() {
        return this.key;
    }
    

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }
    
    /**
     * <p>Retrieves the Deck from the cloud and sets it to
     * the FlashCardOps.FileOps FlashList</p>
     * <p><b> NOTE: </b>Expects that the token expiration is checked prior being used
     * here.</p>
     *
     * // @todo move this method to the calling class and return the flashdeck
     * // Should not call a method from another package from a utility package.
     */
    public void retrieveDeckFmCloud() {
        LOGGER.debug("Called retrieveDeckFmCloud()");
        // get the deck from S3 using key and the token.
        CloudOps.retrieveDeck(name, this.key);
    }

    // Compares by dateInMillis
    @Override
    public int compareTo(@NotNull CloudLink otherFile) {
        if(this.dateInMillis > otherFile.dateInMillis ) {
            return 1;
        } else {
            return -1;
        }
    }
}
