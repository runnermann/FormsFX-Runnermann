package fileops;

import ch.qos.logback.classic.Level;
import flashmonkey.FlashCardOps;
import flashmonkey.Timer;
import fmhashtablechain.PriorityHashTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.*;
/**
 * Client class.
 * Aggregates the files from the local system and the cloud, and returns an
 * ArrayList of LinkObjects. Link Objects contain a name, and a link to either
 * local files, or files from the server. 
 * 
 * <P>Problem: This class does not do a comparison of each individual card. If the EncryptedUser
 * makes a change to a card on one of their systems, ie their iPad. And it does
 * not upload to the cloud, later the EncryptedUser uses their iPhone and the changes
 * are not uploaded. The current system will only check the last timeMils the cards
 * were accessed. Not when an individual makes a change to each card. If the
 * EncryptedUser then opens the deck(flashCard file) from the iPad, the changes that were
 * not uploaded will be deleted.
 *
 * Solution: Either create a separate file, with an array that tracks changes
 * to each individual card. And when changes are made but not immediately uploaded, an update
 * program will make changes to each card that are in the cloud when the EncryptedUser.EncryptedUser connects
 * at a later time. This probably means that the program will need to check the
 * timeMils that the card was changed. b) A second solution is to simply do the comparison
 * /changes when the EncryptedUser.EncryptedUser clicks on the file, and do the conversion/update when
 * as the file is loaded into the flashTree. Thus sparing extra, potentially 
 * unnecessary processing and memory. </p>
 * 
 * 
 * STICKING POINT.... What is returned from the searchNCompare method. 
 *  - a hyperlink
 *  - a file
 *  - a fileStream
 *  - a string name. If string name, then how to inform system that the file is
 *    either from the local system or from the remote system. ???? this seems
 *    like a good option since retrieveFileStream uses a string. Problem is as 
 *    questioned, how to inform the system to use returnRemoteFile method or
 *    returnLocalFile method? 
 * - Returned is a LinkObj. The LinkObj has two constructors. Both constructors
 * take a description name. One constructor is created for local files, the other
 * is created for remote files. As the CompareFile.getAllFiles() method chooses which file is
 * the most recent, it simply inserts either a linkObj to the local file or the
 * remote file. 
 * 
 * Flow... For this iteration. 
 *  loop O(n)
 *  1) Compare files dates and names for the most recent file. O(log(n))
 *      - If file is not on server, save to server?
 *      - If file is not on server, provide file and name to LinkObj
 *      - If file is on server, provide FTPlink and name to LinkObj
 *      - If file is within 90 Days? set in recent list
 * 
 * Algorithm description: This class imports the PriorityQueue from the Java 8 lib
 *   as the queue. As a File name on the remote system is the same as the file name
 *   on the client system, the comparator sets the file with the most recent timeMils
 *   as the higher value. The most recent file is inserted into the priority queue. 
 * 
 *   For a convient UX, The priority queue is then processed for the most recent
 *   files which are stored in the "recent" array in order by timeMils, Older files
 *   are in alphabetical order in the "older" array.
 *
 *   @author Lowell Stadelman
 */



public class AgrFileList
{
    // THE LOGGER
    // REMOVE BEFORE DEPLOYING
    //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AgrFileList.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(AgrFileList.class);
    // 90 days = 5126400000
    private static final long MINUS_NINETY = System.currentTimeMillis() - (1000l * 60 * 60 * 24 * 93);

    /** files younger than 90 days in numeric order. */
    private ArrayList<LinkObj> recent;     
    /** files older than timeMils in alphabetic order */
    private ArrayList<LinkObj> older;
    private static PriorityQueue<LinkObj> queue;
    /** Array of files on the client */
    private static File[] localFiles;
    /** ArrayList of CloudLinks from the cloud server */
    //private static ArrayList<CloudLink> cloudLinks;// = new ArrayList<>();
    /** inner class */
    private SyncFiles sync = new SyncFiles();
    private LinkObj linkObj;
    /** the timeMils used to compare against for the array of recent files */
    private long timeMils;
    private static int size;
    
    /**
     * Default constructor. 
     */
    public AgrFileList()
    {
       /* no args */
    }
    
    /**
     * Constructor will check local decks,
     * check remote decks, and create an arrayList of LinkObj's. Directory and
     * a deck must exist already or will crash.
     * @param folder 
     */
    public AgrFileList(File folder) {
        //LOGGER.setLevel(Level.DEBUG);
        LOGGER.debug("AgrFileList constructor. seeking folder: {}", folder.getPath());
        // @TODO remove tester if statement
        /*if(folder.getPath().contains("userData")) {
            System.out.println("seeking folder contains \"userData\": ending...");
            System.exit(1);
        }*/


        LOGGER.debug("MINUS_NINETY days: {}, currentTimeMillis: {}" , MINUS_NINETY, System.currentTimeMillis());
        //LOGGER.debug("Calling method. ???: " + Thread.currentThread().getStackTrace()[3].getMethodName());
        //Thread.dumpStack();

        //@TODO uncomment line below after testing!!! if this is still needed
        //FlashCardOps.getInstance().fileExists("default", FlashCardOps.getInstance().getDeckFolder());
        localFiles  = folder.listFiles();
        ArrayList<CloudLink> cloudLinks = CloudOps.getCloudLinks();
        // Get decks from S3
        if(cloudLinks != null && cloudLinks.size() != 0) {
            // testing
            LOGGER.debug("cloudLinks size: " + cloudLinks.size());
            for (CloudLink c : cloudLinks) {
                LOGGER.debug("\tcloudLink: " + c.getName() + " " + c.getDateInMillis());
            }
        } else {
            LOGGER.warn("WARNING: Cloudlinks are null or size 0");
        }
    
        // Synchronize for the most recent version of
        // decks and add it to "queue"
        sync.syncFiles(localFiles, cloudLinks);

        // @TODO set date of deck for recent or older in AgrFileList
        // Split the decks between older and recent
        setTimeMils("04/04/2018", "00:00");
        recent = new ArrayList<>(10);
        older = new ArrayList<>(10);
        recentSplit();

        LOGGER.debug("AgrFileList() completed");
    }
        
    public LinkObj getLinkObj()
    {
        return this.linkObj;
    }
    
    /**
     * Returns the number of LinkObj's in the ArrayList of files.
     * Note this method returns the variable size.
     * @return The value contained in the variable size
     */
    public int getSize()
    {
        return recent.size() + older.size();
    }
    

    
    /**
     * Returns an ordered array of LinkObj's based on timeMils order. Files are
     * either from remote server, or from local file if they are previous
     * to the timeMils set as the relevant timeMils. IE the beginning of the semester or
     * quarter. 
     * @return Numerically ordered list of LinkObjects if their timeMils is more
     * recent than the timeMils set in timeMils.
     */
    public ArrayList<LinkObj> getRecentFiles()
    {
        //LOGGER.debug("in AgrFileList, getting alphaFiles ");
        return recent;      
    }
    
    /**
     * Returns an alphabetical ordered array of LinkObj's that are older than
     * the timeMils set in timeMils.
     * @return 
     */
    public ArrayList<LinkObj> getOlderFiles()
    {
        return older;
    }
    
    /**
     * sets the LinkObj for  this object.
     * @param lo
     */
    public void setLinkObj(LinkObj lo)
    {
        this.linkObj = lo;
    }
    
    /**
     * Using the epoch date time in milliseconds using Jan 01, 1970 at 00:00:00
     * @param strDate Expects "MM/dd/yyyy"
     * @param strTime Expects "HH:mm"
     */
    public void setTimeMils(String strDate, String strTime)
    {
        flashmonkey.Timer timer = Timer.getClassInstance();
        this.timeMils = timer.getMills(strDate, strTime);
    }
    
    /**
     * Helper method creates the flash scene. Note that this is to delay the call that is
     * made prior to the classes initialization. 
     */
 /*   private static void createF() {
        FlashMonkeyMain.getWindow().setScene(FlashMonkeyMain.createFlash.createFlashScene());
        //FlashMonkeyMain.createButtonAction();
    }
    
  */

    /**
     * Starter method. Splits PriorityQueue files/LinkObj's based on "timeMils"
     * and adds them to either "recent" or "older" arrays
     */
    private void recentSplit() {
    
        LOGGER.debug("recentSplit() called");
        
        while(recentSplit(queue)) {
            recentSplit();
        }
    }
    
    
    /**
     * Splits the <i> PriorityQueue of type { @LinkObj } </i> into recent or older arrays.
     * Assumes that queue exists.
     * @param q
     * @return Returns true if this iteration returned a link object, false if
     * the iteration is null;
     */
    private boolean recentSplit(PriorityQueue<LinkObj> q) {
    
        LOGGER.debug("recentSplit( ... ) called");
        LinkObj linkObj1;

        // base case
        if(q.isEmpty()) {
            return false;
        }
        // Check the queue containing local and remote files
        if(q.peek().getCloudLink() != null) {
            linkObj1 = new LinkObj(q.poll()); //new LinkObj( q.peek().getDescrpt(), new CloudLink(q.peek().getCloudLink()));
        } else {
            LOGGER.debug("localFile info: fileName: <{}>, fileDescript: <{}>", q.peek().getFile().getPath(), q.peek().getDescrpt());
            linkObj1 = new LinkObj(q.peek().getDescrpt(), new File(q.peek().getFile().getName()), q.peek().getFile().length());
            q.poll();
        }
        // short circut
        if(linkObj1.getDescrpt() == null || ! linkObj1.getDescrpt().endsWith(".dec")) {
            LOGGER.warn("linkObj1 descript was null or did not end with .dec");
            q.poll();
            return recentSplit(q);
        }
        LOGGER.info("linkObj deckName: " + (linkObj1.getDescrpt()));

        try {
                if (linkObj1.getFile() != null) {
                    LOGGER.debug("localFile.lastModiefied {} > MINUS_NINETY {}: <{}>", linkObj1.getFile().lastModified(), MINUS_NINETY, linkObj1.getFile().lastModified() > MINUS_NINETY);
                    LOGGER.debug("localfile.exists: <{}>", linkObj1.getFile().toString());
                    //@TODO Fix local file lastModified date issue. "<" should be ">"
                    if (new File(DirectoryMgr.getMediaPath('t') + linkObj1.getFile().getName()).lastModified() > MINUS_NINETY) {
                        LOGGER.debug("adding local to recent, Date: " + DirectoryMgr.getMediaPath('t') + linkObj1.getFile().lastModified());
                        recent.add(linkObj1);
                    }
                    else {
                        LOGGER.debug("adding local to older");
                        older.add(linkObj1);
                    }
                    return recentSplit(q);
                }
                else {
                    LOGGER.debug("linkObj1.getDescrpt(): " + linkObj1.getDescrpt());
                    LOGGER.debug("linkObj1.getCloudLink().getDateInMillis(): " + linkObj1.getCloudLink().getDateInMillis()  );
                  
                    if (linkObj1.getCloudLink().getDateInMillis() > MINUS_NINETY) {
                        LOGGER.debug(linkObj1.getCloudLink().getName() + " added remote to recent list");
                        recent.add(linkObj1);
                    }
                    else {
                        LOGGER.debug(linkObj1.getCloudLink().getName() + " added remote to older list");
                        older.add(linkObj1);
                   }
                   return recentSplit(q);
                }
        } catch (NullPointerException e) {
            LOGGER.warn("NullPointerException");
            e.printStackTrace();
            return recentSplit(q);
        }
    }

   
    // *** INNER CLASS ****
    
    /**
     * <pre>
     * Creates a queue with an alphabetized list of local and remote files
     *  - check if there are any files locally or remotely
     *      - If not, send authcrypt.user to create a new file.
     *  - If there are local files
     *      - add them to the{@code( Map<String name, LinkObj> )}.
     *  - If there are remote files
     *      - add them to the {@code( Map<String name, LinkObj> )}.
     *  - If there is a collision.
     *      - Gard against overwrite with zero
     *      - Compare by LinkObj.getDate(). Youngest wins
     *</pre>
     * <p>Creates a queue of decks contained remotely and locally. If there are two that are the same name,
     * returns the youngest of the two. The oldest is overwritten.</p>
     *
     * @author Lowell Stadelman
    */
    private static class SyncFiles {
        private final int INITIAL_QUEUE_SIZE = 15;

        public SyncFiles() { /* do nothing */ }

        private void syncFiles(File[] localFiles, ArrayList<CloudLink> remoteLinks) {
            Map<String, LinkObj> syncMap = new PriorityHashTable<>();
    
            // If there are local files. Create LinkObj's and
            // Add them to the Map.
            if(localFiles != null && localFiles.length > 0) {
                LOGGER.info("Adding local files to HashMap");
                for(File f : localFiles) {
    
                    LOGGER.debug("localFiles(i) name: " + f.getName());
                    
                    if( f.getName().endsWith(".dec") ) {
                        syncMap.put(f.getName(), new LinkObj(f.getName(), f, f.length()));
                    }
                }
            }
            // If there are remote files. Create LinkObj's and
            // add them to the Map.
            if(remoteLinks != null && remoteLinks.size() > 0) {
                LOGGER.debug("Adding remoteFiles to hashMap");
                for(CloudLink c : remoteLinks) {
                    if( c.getKey().endsWith(".dec")) {
                        LOGGER.info("\t cloudFileName: {}", c.getName());
                        syncMap.put(c.getName(), new LinkObj(c.getName(), c, c.getSize()));
                    }
                }
            }
            
            queue = new PriorityQueue<>(INITIAL_QUEUE_SIZE);
            LOGGER.debug("syncMap size: <" + syncMap.size() + "> before conversion.");
            
            // Return a priorityQueue of the LinkObjs compared by compareTo.
            // This provides the desired property of allowing a deck to exist
            // locally on the users machine with 0 cards and the user may
            // have decks as placeholder. But guards against an overwrite
            // from a cloudlink with a 0-card deck.
            Iterator iterator = syncMap.values().iterator();

            while(iterator.hasNext()) {
                LinkObj lo = (LinkObj) iterator.next();
                
                if(lo.getCloudLink() != null) {
                    LOGGER.debug("adding cloudLink to linkObj and to queue and descript:  {}", lo.getDescrpt());
                    queue.add(new LinkObj(lo.getDescrpt(), new CloudLink(lo.getCloudLink()), lo.getByteSize()));
                } else {
                    LOGGER.debug("adding local file to linkObj and to queue and descript: {}", lo.getDescrpt());
                    queue.add(new LinkObj(lo.getDescrpt(), new File(lo.getFile().getName()), lo.getByteSize()));
                }
            }
    
            LOGGER.debug("Returning the queue. size: " + queue.size());
        }
    }
}
