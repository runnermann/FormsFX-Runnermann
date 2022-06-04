package fileops;

import ch.qos.logback.classic.Level;
import flashmonkey.FlashCardMM;
import flashmonkey.FlashCardOps;
import flashmonkey.ReadFlash;
import flashmonkey.Timer;
import fmannotations.FMAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fmhashtablechain.StateHashTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;
import static habanero.edu.rice.pcdp.PCDP.*;

public class MediaSync {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaSync.class);
	//private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MediaSync.class);
	
	private ArrayList<MediaSyncObj> download = new ArrayList<>();
	private ArrayList<String> delete = new ArrayList<>();
	private ArrayList<MediaSyncObj> upload = new ArrayList<>();
	private ArrayList<MediaSyncObj> cache = new ArrayList<>();
	private boolean downloadBool = false;
	private boolean uploadBool = false;
	private boolean deleteBool = false;
	private boolean cacheBool = false;


	public MediaSync() {
		/* no args constructor */
	}

	// ****** GETTERS ********* //
	public ArrayList<MediaSyncObj> getDownload() {
		return download;
	}

	public ArrayList<String> getDelete() {
		return delete;
	}

	public ArrayList<MediaSyncObj> getUpload() {
		return upload;
	}

	public ArrayList<MediaSyncObj> getCache() {
		return cache;
	}

	// ******* OTHERS ******** //

	/**
	 * Starts media synchronization
	 * Called by CreateFlash
	 */
	/*public static void syncMedia() {
		//LOGGER.setLevel(Level.DEBUG);
		LOGGER.debug("syncMedia called");
		MediaSync ms = new MediaSync();
		// get local media
		ArrayList<MediaSyncObj> syncObjs = ms.getLocalMediaList();
		// get a list of media from the flashList
		ArrayList<MediaSyncObj> flashlistMNames = FlashCardOps.getInstance().getFlashListMedia();
		LOGGER.debug("flashList mediaName length: <{}>", flashlistMNames.size());
		// sync local media with flashList and media in the cloud.
		ms.syncMedia(flashlistMNames, syncObjs, ReadFlash.getInstance().getDeckName());
		FlashCardOps.getInstance().setMediaIsSynced(true);
	} */

	public static void syncMedia(ArrayList<FlashCardMM> flashList) {
		//LOGGER.setLevel(Level.DEBUG);
		LOGGER.debug(" *** syncMedia called ***");
		Timer.getClassInstance().begin();
		MediaSync ms = new MediaSync();
		// get local media
		ArrayList<MediaSyncObj> syncObjs = ms.getLocalMediaList();
		// get a list of media from the flashList
		ArrayList<MediaSyncObj> flashlistMNames = FlashCardOps.getInstance().getFlashListMedia(flashList);
		LOGGER.debug("flashList media length: <{}>", flashlistMNames.size());
		// sync local media with flashList and media in the cloud.
		boolean bool = ms.syncMediaHelper(flashlistMNames, syncObjs, FlashCardOps.getInstance().getDeckFileName());
		if(bool) {
			// synchronize media
			ms.syncCloudMediaAction();
		}
		FlashCardOps.getInstance().setMediaIsSynced(true);
		Timer.getClassInstance().end();
		long time = Timer.getClassInstance().getTotalTime();
		LOGGER.info("MediaSync: Time to sync and put files in S3: " + time);
	}

	/**
	 * Call to sync media across local files and remote files. This is the single call
	 * to sync media. End result, Media is saved locally. There is no "in memory" state
	 * as a result. Media must be accessed through the local file system.
	 * @param flashListMedia
	 * @param localNames
	 * @param deckFileName the file name, not the title name
	 */
	private boolean syncMediaHelper(ArrayList<MediaSyncObj> flashListMedia, ArrayList<MediaSyncObj> localNames, String deckFileName) {
		LOGGER.debug("*** syncMediaHelper called ***");
		LOGGER.info("flashListMedia.size() {}, localNames.size() {}", flashListMedia.size(), localNames.size());

		//CloudOps cloudOps = new CloudOps();

		// Get file names from media in users S3 bucket

		ArrayList<CloudLink> remoteNameList = CloudOps.getS3MediaList(deckFileName);
		// Create mediaSyncObjects from the remoteNameList
		ArrayList<MediaSyncObj> remoteNames = getRemoteNames(remoteNameList);
		// create the mediaState map by comparing
		// the remote list with the local list
		StateHashTable<String, MediaSyncObj> mediaState = setMediaStateMap(flashListMedia, localNames, remoteNames);

		LOGGER.info("mediaState length(): {}", mediaState.size() );
		// calc media state and synchronize;
		if(! mediaState.isEmpty()) {
			calcMediaState(mediaState);
			return true;
		} else {
			LOGGER.info("MediaState is empty " );
			return false;
		}
	}
	
	/**
	 * Helper method to syncMedia()
	 * @return
	 */
	private ArrayList<MediaSyncObj> getLocalMediaList() {
		LOGGER.debug("getLocalMediaList called");
		String mediaPath = DirectoryMgr.getMediaPath('m');
		File localFolder = new File(mediaPath);
		File[] mediaFiles = localFolder.listFiles();
		ArrayList<MediaSyncObj> syncObjs = new ArrayList<>();

		if(mediaFiles != null && mediaFiles.length > 0) {
			forall (0, mediaFiles.length - 1, i -> {
				syncObjs.add(new MediaSyncObj(mediaFiles[i].getName(), 'm', 2));
			});
		}

		LOGGER.debug("num syncObjs for local: <{}>", syncObjs.size());
		return syncObjs;
	}


	
	/**
	 * Sets the media state according to it's location in comparison with
	 * the media in the deck. I.E. if media is in the deck and not in local
	 * file system. The media is annotated for upload.
	 * @param fListMedia the flashList
	 * @param localNames local media fileNames
	 * @param remoteNames remote media fileNames
	 * @return returns a {@code(Map.StateHashTable<String, Integer>)}
	 */
	private StateHashTable<String, MediaSyncObj> setMediaStateMap(ArrayList<MediaSyncObj> fListMedia, ArrayList<MediaSyncObj> localNames, ArrayList<MediaSyncObj> remoteNames) {
		StateHashTable<String, MediaSyncObj> stateMap = new StateHashTable<>();
		
		LOGGER.info(" *** setMediaStateMap called *** \n");
		// create a map<String, boolean> of the most current decks.
		// use a hashtable and add integers for values
		// Take advantage of collisions.
		// a.	If media exists in all three locations -> value = 7
		// b.	If media@remote needs download -> value = 5
		// c.	If media@remote needs delete -> value = 4
		// d.	If media@local needs upload -> value = 3
		// e.	If media@local needs cache -> value = 2
		
		
		stateMap = getStateMap(localNames, remoteNames, fListMedia, stateMap);
		
		return stateMap;
	}
	
	/**
	 * Builds the stateMap. Determines if an item should be uploaded, downloaded
	 * deleted, cached, or do nothing.
	 * @param local ..
	 * @param remote ..
	 * @param flashListOjs ..
	 * @param stateMap ..
	 * @return
	 */
	private StateHashTable<String, MediaSyncObj> getStateMap(ArrayList<MediaSyncObj> local, ArrayList<MediaSyncObj> remote, ArrayList<MediaSyncObj> flashListOjs, StateHashTable<String, MediaSyncObj> stateMap) {
		
		LOGGER.info(" *** getStateMap called *** \n");
		
		//StateHashTable<String, MediaSyncObj> stateMap =
			//.of(local)
					// local adds 2 to value.state
					//.peek(e -> {
						LOGGER.debug("localMedia files, size {}", local.size());
						//forallChunked(0, local.size() -1, (i) -> {
						for(int i = 0; i < local.size(); i++) {
							//LOGGER.debug("idx" + i);
							local.get(i).setState(2);
							// hashMap
							stateMap.put(local.get(i).getFileName(), local.get(i));
						//});
						}
					//})
					// remote adds 4 to value.state
					//.peek(e -> {
						LOGGER.debug("for remote, remote size: {}", remote.size() );

						//forallChunked(0, remote.size() -1, (i) -> {
						for(int i = 0; i < remote.size(); i++) {
							remote.get(i).setState(4);
							// hashMap
							stateMap.put(remote.get(i).getFileName(), remote.get(i));
							
						//});
						}
					//})
					// flashListOjs adds 1
					//.peek(e -> {
						LOGGER.debug("for flashListOjs... size: <{}>", flashListOjs.size());
						//forallChunked(0, flashListOjs.size() -1, (i) -> {
						for(int i = 0; i < flashListOjs.size(); i++) {
							flashListOjs.get(i).setState(1);
							// hashMap
							stateMap.put(flashListOjs.get(i).getFileName(), flashListOjs.get(i));
						//});
						}
					//}).collect(Collectors.toCollection(StateHashTable::new));
			return stateMap;
	}

	private void clearState() {
		this.delete.clear();
		this.download.clear();
		this.upload.clear();
	}
	
	
	/**
	 * Synchronzing action based on the results contained in the mediaState map in the parameter.
	 *
	 * @param mediaState {@code StateHashTable<MediaSyncObj, Integer>}
	 */
	private void calcMediaState(StateHashTable<String, MediaSyncObj> mediaState) {

		clearState();

		LOGGER.info(" *** calcMediaState called *** \n");
		
		// create a map<String, boolean> of the most current decks.
		// use a hashtable and add integers for values
		// Take advantage of collisions.
		// a.	If media exists in all three locations -> value = 7
		// b.	If media@remote needs download -> value = 5
		// c.	If media@remote needs delete -> value = 4
		// d.	If media@local needs upload -> value = 3
		// e.	If media@local needs cache -> value = 2
		Iterator iterator = mediaState.values().iterator();
		
		while(iterator.hasNext()) {

				MediaSyncObj syncObj = (MediaSyncObj) iterator.next();
				int stateNum = syncObj.getState();
				switch (stateNum) {

					case 7: {
						// case 7 Media is local and in the cloud. It is where it should be. //
						break;
					}

					// download list
					case 5: {
						download.add(syncObj);
						downloadBool = true;
						break;
					}
					// delete from cloud list
					case 4: {
						delete.add(syncObj.getFileName());
						deleteBool = true;
						break;
					}
					// upload to cloud list
					case 3: {
						upload.add(syncObj);
						uploadBool = true;
						break;
					}
					// media exists locally, but is not in the deck.
					case 2: {
						cache.add(syncObj);
						cacheBool = true;
						break;
					}
					case 1: {
						LOGGER.warn("WARNING: syncCloudMediaAction: Media in flashList is not present " +
								"in remote or local locations!!! " + syncObj.getFileName());
						break;
					}
					default: {
						if (stateNum != 6) {
							LOGGER.warn("WARNING: syncCloudMediaAction: Unknown result: " + syncObj.getFileName() + ". " + syncObj.getState());
						}
					}
				}
		}
	}
	
	private void syncCloudMediaAction() {
		
		LOGGER.info(" *** syncCloudMediaAction called *** \n");
		LOGGER.debug("bools\n" +
				"uploadBook: {}", uploadBool
				+ "\ndownloadBool: {}", downloadBool
				+ "\ndeleteBool: {}", deleteBool
				+ "\ncachBool: {}", cacheBool);

		
		//CloudOps co = new CloudOps();
		// authcrypt.UserData data = new authcrypt.UserData();

		/*
		 * Future versions should find a way to use async parallel processing
		 * such as forAll or forAllChunked for downloads. As 11/27/2020, the
		 * AWSS3Client is not thread safe as stated in their documentation. Attempts
		 * to use Java libriaries for inputStream and the use of signedURLs to create
		 * temproary links created problems with processing file names. The end soluton
		 * is to use the S3Client download(...) convienience method. Without studying
		 * the AWS library opensource code, if available, settled for the short term solution
		 * which is slow. See https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3Client.html#download-com.amazonaws.services.s3.model.PresignedUrlDownloadRequest-
		 * documentation as an excellent source for understanding the use of the code
		 * for S3GetObjs.cloudsGetMedia(...) This specifically relates to download.
		 */
		if(downloadBool) {
			// serial processing
			//new Thread(() -> {
				// new thread created for each item in forloop. :(
				CloudOps.getMedia(download);
			//}).start();
			/*
			long sendTime = System.currentTimeMillis();
					forallChunked(0, upload.size() - 1, (i) -> {
						co.connectCloudOut(upload.get(i).getType(), data.getUserName(), upload.get(i).getFileName());
					});
					sendTime = (System.currentTimeMillis() - sendTime);
					LOGGER.info("Total send time in syncCloudMediaAction: {} milliseconds, numElement: {}", sendTime, upload.size());
			 */

		}
		if(uploadBool) {
			//upload.stream().forEach(e -> System.out.println(e.getFileName()));
			CloudOps.putMedia(upload);
		}
		if(deleteBool) {
			/*Stream.of(delete)
					.peek(e -> {
						forall(0, delete.size() - 1, (i) -> {
							co.removeFmCloud(delete.get(i));
						});
					}).close();
			 */
		}
		if(cacheBool) {
			Stream.of(cache)
					.peek(e -> {
						forall(0, cache.size() - 1, (i) -> {
							//@todo cache media files not used and mark for deletion.
						});
					}).close();
		}
	}
	
	
	/**
	 * Cache local media that is not in the deck.
	 * @param cacheNames ..
	 */
	private void cacheLocalMedia(ArrayList<MediaSyncObj> cacheNames) {
		// @TODO complete syncLocalMedia, meaning cache localMedia
		/*Stream.of(cacheNames)
				.parallel()
				.forEach(e -> {
				
				});
				
		 */
	}
	
	/**
	 * Returns a string array of mediaNames from the CloudLinks
	 * in the parameter. Expects that a connection to the internet
	 * has been confirmed previous to use of this method.
	 * @param remoteNameList ..
	 * @return An arraylist of the MediaSyncObjects
	 */
	private ArrayList<MediaSyncObj> getRemoteNames(ArrayList<CloudLink> remoteNameList) {
		
		LOGGER.info(" *** getRemoteNames called *** \n");
		//System.out.println("\t remoteNameList.size: " + remoteNameList.size());
		
		ArrayList<MediaSyncObj> syncObjs = new ArrayList<>();
		
		/*Stream.of(remoteNameList)
				.map(e -> {
					forall(0, remoteNameList.size() -1, (i) -> {
						MediaSyncObj o = new MediaSyncObj(remoteNameList.get(i));
						syncObjs.add(o);
					});
					return syncObjs;
				}).close();
		 */

		for(CloudLink cl : remoteNameList) {
			syncObjs.add(new MediaSyncObj(cl));
		}
		
		LOGGER.info("returning {} syncObjs from remote", syncObjs.size());
		
		return syncObjs;
	}

	@FMAnnotations.DoNotDeployMethod
	public MediaSync syncMediaTester(ArrayList<FlashCardMM> flashList, MediaSync ms) {
		//LOGGER.setLevel(Level.DEBUG);
		LOGGER.debug("syncMedia called");
		Timer.getClassInstance().begin();
		//MediaSync ms = new MediaSync();
		// get local media
		ArrayList<MediaSyncObj> syncObjs = ms.getLocalMediaList();
		// get a list of media from the flashList
		ArrayList<MediaSyncObj> flashlistMNames = FlashCardOps.getInstance().getFlashListMedia(flashList);
		LOGGER.debug("flashList mediaName length: <{}>", flashlistMNames.size());
		// sync local media with flashList and media in the cloud.
		boolean bool = ms.syncMediaHelper(flashlistMNames, syncObjs, FlashCardOps.getInstance().getDeckFileName());

		return  ms;
	}

	public void clearMediaStateValues() {
		download.clear();
		upload.clear();
		cache.clear();
		delete.clear();
		cacheBool = false;
		deleteBool = false;
		downloadBool = false;
		uploadBool = false;
	}
}
