package fileops;

import java.io.File;
import java.util.Comparator;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * LinkObj contains the file/class description along with, if the file is local -
 * the file, or if it is remote - the awsS3 link. This enables a single interaction
 * for files from multiple locations. This may be more appropriately implmented as
 * an interface and an abstract class.
 * Used to provide a clickable link in the SelectFilePane in the firstPage.
 *
 * @author Lowell Stadelman
 */
public final class LinkObj implements Comparable<LinkObj>, Comparator<LinkObj> //, Serializable
{
      //private static final long serialVersionUID = FlashMonkeyMain.VERSION;
      //ObjectListing objectListing = s3client.listObjects(bucketName);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LinkObj.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(LinkObj.class);


      private CloudLink cloudLink;// = new FTPFile();
      private File file;// = new File("0");
      private String descrpt; // the description of the file or class name
      private Long timeInMillis;
      private long byteSize;

      /**
       * default constructor
       */
      public LinkObj() {
            LOGGER.info("default constructor called");
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("Called LinkObj");

            cloudLink = null;
            file = null;
      }

      /**
       * Constructor. Builds a LinkObj using the description, and the CloudLink
       *
       * @param descr The file name as a string.
       * @param cl    The S3 file handle for this deck.
       */
      public LinkObj(String descr, CloudLink cl, long bSize) {
            //LOGGER.setLevel(Level.DEBUG);
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("constructor using Cloudlink called & descrpt: {}", descr);
            //cloudLink = new FTPFile();
            descrpt = descr;
            cloudLink = cl;
            timeInMillis = cl.getDateInMillis();
            byteSize = bSize;
      }

      /**
       * Constructor. Builds a LinkObj using the description, and the File
       *
       * @param descr
       * @param f
       */
      public LinkObj(String descr, File f, long bSize) {
            //LOGGER.setLevel(Level.DEBUG);
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("constructor for file called");
            descrpt = descr;
            file = f;
            timeInMillis = f.lastModified();
            byteSize = bSize;
      }


      /**
       * Copy constructor
       *
       * @param original
       */
      public LinkObj(LinkObj original) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("Copy constructor called");

            if (original != null) {
                  this.descrpt = original.descrpt;
                  //        this.file    = original.file;
                  this.timeInMillis = original.timeInMillis;
                  this.byteSize = original.byteSize;

                  if (original.cloudLink != null) {
                        this.cloudLink = new CloudLink(original.cloudLink);
                  } else {
                        this.file = new File(original.file.getPath());
                  }

                  //           this.ftpFile = original.ftpFile;
            }
      }

      /**
       * Constructor. Builds a LinkObj using the description only,
       *
       * @param descr The description or LabelName of the deck. May be provided by the EncryptedUser
       *              // @param f The File where the flashCard deck is stored
       */
      public LinkObj(String descr) {
            //LOGGER.setLevel(Level.DEBUG);
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.info("String only constructor called");

            file = new File(descr);
            descrpt = descr;
            //file = f;
      }

      /**
       * Returns the file name as a string
       *
       * @return
       */
      public String getDescrpt() {
            return descrpt;
      }

      public CloudLink getCloudLink() {
            return cloudLink;
      }

      protected File getFile() {
            return file;
      }

      protected long getByteSize() {
            return byteSize;
      }

      public long getTimeInMillis() {
            return timeInMillis;
      }

      /**
       * Only comparing the names of the Link Objects for the sake of
       * the PriorityQueue. Expects descrimination to occur prior to
       * the use of this method
       *
       * @param other
       * @return normal compareTo method based on Strings of the Descrpt
       */
      @Override
      public int compareTo(LinkObj other) {

            if (other != null) {
                  return this.getDescrpt().compareTo(other.getDescrpt());
            }

            return -1;
      }

      @Override
      public boolean equals(Object other) {
            if (other == null) {
                  return false;
            } else {
                  //LinkObj lo = (LinkObj) other;
                  return this.descrpt.equals(((LinkObj) other).descrpt);
            }
      }

      /**
       * Compares two LinkObjs by their dateInMilli's, {@code &} if length is {@code >} 6 bytes
       *
       * @param linkObj1 ..
       * @param linkObj2 ..
       * @return -1 if byteSize {@code < } 1 if {@code >} 6, 0 otherwise
       */
      @Override
      public int compare(LinkObj linkObj1, LinkObj linkObj2) {

            LOGGER.debug("compare called");
            // if 1 is a null deck, ie < 6 bytes,
            // then use the other deck.
            if (linkObj1.byteSize < 6) {
                  LOGGER.debug("link obj1 is cloudlink: {}, byteSize is < 6, returning -1", linkObj1.getClass().getName().contains("cloudLink"));
                  return -1;
            } else if (linkObj2.byteSize < 6) {
                  LOGGER.debug("link obj2 is cloudlink: {}, byteSize is < 6, returning 1", linkObj2.getClass().getName().contains("cloudLink"));
                  return 1;
            }
            return linkObj1.timeInMillis.compareTo(linkObj2.timeInMillis);
      }
}
