package fileops;

import flashmonkey.FlashCardOps;
import flashmonkey.ReadFlash;
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
	
	ArrayList<MediaSyncObj> download = new ArrayList<>();
	ArrayList<String> delete = new ArrayList<>();
	ArrayList<MediaSyncObj> upload = new ArrayList<>();
	ArrayList<MediaSyncObj> cache = new ArrayList<>();
	boolean downloadBool = false;
	boolean uploadBool = false;
	boolean deleteBool = false;
	boolean cacheBool = false;
	
	public MediaSync() {
		/* no args constructor */
	}
	
	
	/**
	 * Starts media synchronization
	 */
	public static void syncMedia() {
		
		LOGGER.debug("syncMedia called");
		MediaSync ms = new MediaSync();
		// get local media
		ArrayList<MediaSyncObj> syncObjs = ms.getLocalMediaList();
		// get a list of media from the flashList
		ArrayList<MediaSyncObj> listNames = FlashCardOps.getInstance().getFlashListMedia();
		// sync local media with flashList and media in the cloud.
		ms.syncMedia(listNames, syncObjs, ReadFlash.getInstance().getDeckName());
		FlashCardOps.getInstance().setMediaIsSynced(true);
	}
	
	/**
	 * Helper method to syncMedia()
	 * @return
	 */
	private ArrayList<MediaSyncObj> getLocalMediaList() {
		String mediaPath = DirectoryMgr.getMediaPath('m');
		File localFolder = new File(mediaPath);
		File[] mediaFiles = localFolder.listFiles();
		ArrayList<MediaSyncObj> syncObjs = new ArrayList<>();

		if(mediaFiles != null && mediaFiles.length > 0) {
			forall (0, mediaFiles.length - 1, i -> {
				syncObjs.add(new MediaSyncObj(mediaFiles[i].getName(), 'm', 2));
			});
		}
		
		return syncObjs;
	}

	/**
	 * Call to sync media across local files and remote files. This is the single call
	 * to sync media. End result, Media is saved locally. There is no "in memory" state
	 * as a result. Media must be accessed through the local file system.
	 * @param flashListMedia
	 * @param localNames
	 * @param deckName
	 */
	private void syncMedia(ArrayList<MediaSyncObj> flashListMedia, ArrayList<MediaSyncObj> localNames, String deckName) {
		
		LOGGER.info("*** syncMedia called ***");
		LOGGER.info("flashListMedia.size() {}, localNames.size() {}", flashListMedia.size(), localNames.size());
		
		CloudOps cloudOps = new CloudOps();
		
		// Get file names from media in users S3 bucket
		ArrayList<CloudLink> remoteNameList = cloudOps.getS3MediaList(deckName);
		// Create mediaSyncObjects from the remoteNameList

		ArrayList<MediaSyncObj> remoteNames = getRemoteNames(remoteNameList);
		int i = 0;
		for(MediaSyncObj m : remoteNames) {
			LOGGER.debug("remoteName: {}) {}", i++, m.getFileName());
		}

		// create the mediaState map by comparing
		// the remote list with the local list
		StateHashTable<String, MediaSyncObj> mediaState = setMediaStateMap(flashListMedia, localNames, remoteNames);
		
		LOGGER.info("mediaState length(): {}", mediaState.size() );
		// calc media state and synchronize;
		if(! mediaState.isEmpty()) {
			calcMediaState(mediaState);
			// synchronize media
			syncCloudMediaAction();
		} else {
			LOGGER.info("MediaState is empty " );
		}
	}
	
	/**
	 * Sets the media state according to it's location in comparison with
	 * the media in the deck. I.E. if media is in the deck and not in local
	 * file system. The media is annotated for upload.
	 * @param fListMedia the flashList
	 * @param localNames local media fileNames
	 * @param remoteNames remote media fileNames
	 * @return returns a @code(Map.StateHashTable<String, Integer>)
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
	 * @param local
	 * @param remote
	 * @param flashList
	 * @param stateMap
	 * @return
	 */
	private StateHashTable<String, MediaSyncObj> getStateMap(ArrayList<MediaSyncObj> local, ArrayList<MediaSyncObj> remote, ArrayList<MediaSyncObj> flashList, StateHashTable<String, MediaSyncObj> stateMap) {
		
		LOGGER.info(" *** getStateMap called *** \n");
		
		//StateHashTable<String, MediaSyncObj> stateMap =
			//.of(local)
					// local adds 2 to value.state
					//.peek(e -> {
						LOGGER.debug("forAllChunked local");
						//forallChunked(0, local.size() -1, (i) -> {
						for(int i = 0; i < local.size(); i++) {
							LOGGER.debug("idx" + i);
							local.get(i).setState(2);
							// hashMap
							stateMap.put(local.get(i).getFileName(), local.get(i));
						//});
						}
					//})
					// remote adds 4 to value.state
					//.peek(e -> {
						LOGGER.debug("forAllChunked remote");
						//forallChunked(0, remote.size() -1, (i) -> {
						for(int i = 0; i < remote.size(); i++) {
							remote.get(i).setState(4);
							// hashMap
							stateMap.put(remote.get(i).getFileName(), remote.get(i));
							
						//});
						}
					//})
					// flashList adds 1
					//.peek(e -> {
						LOGGER.debug("forAllChunked flashList");
						//forallChunked(0, flashList.size() -1, (i) -> {
						for(int i = 0; i < flashList.size(); i++) {
							flashList.get(i).setState(1);
							// hashMap
							stateMap.put(flashList.get(i).getFileName(), flashList.get(i));
						//});
						}
					//}).collect(Collectors.toCollection(StateHashTable::new));
			return stateMap;
	}
	
	
	/**
	 * Synchronzing action based on the results contained in the mediaState map in the parameter.
	 *
	 * @param mediaState StateHashTable<MediaSyncObj, Integer>
	 * @return Returns a list of mediaNames for local archive.
	 */
	protected void calcMediaState(StateHashTable<String, MediaSyncObj> mediaState) {
		
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
					// case 7 Media is where it should be. //
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
				// cache list
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
		
		CloudOps co = new CloudOps();
		authcrypt.UserData data = new authcrypt.UserData();

		/*
		 * Future versions should find a way to use async parallel processing
		 * such as forAll or forAllChunked for downloads. As 11/27/2020, the
		 * AWSS3Client is not thread safe as stated in their documentation. Attemtps
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
			new Thread(() -> {
				co.getMedia(download);
			}).start();
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
			co.putMedia(upload);
			/*
					long sendTime = System.currentTimeMillis();
					forallChunked(0, upload.size() - 1, (i) -> {
						co.connectCloudOut(upload.get(i).getType(), data.getUserName(), upload.get(i).getFileName());
					});
					sendTime = (System.currentTimeMillis() - sendTime);
					LOGGER.info("Total send time in syncCloudMediaAction: {} milliseconds, numElement: {}", sendTime, upload.size());
			*/
		}
		if(deleteBool) {
			Stream.of(delete)
					.peek(e -> {
						forall(0, delete.size() - 1, (i) -> {
							co.removeFmCloud(delete.get(i));
						});
					}).close();
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
	 * @param cacheNames
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
	 * @param remoteNameList
	 * @return
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
	
}
