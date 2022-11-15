package type.tools.imagery;

import fileops.CloudOps;
import fileops.DirectoryMgr;
import fileops.utility.Utility;
import flashmonkey.FlashCardOps;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import uicontrols.SceneCntl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class ImageUploader {

      private VBox uploaderBox;// = new VBox();
      private DragNDrop dragDrop; // = new DragNDrop();
      private ImageView iView;// = new ImageView();
      private int wd;
      private int ht;
      private boolean isNewImgName;
      private String imgName;

      public ImageUploader() {
            this.uploaderBox = new VBox();
            this.iView = new ImageView();
            dragDrop = new DragNDrop();
            init();
      }

      public VBox getVBox() {
            return this.uploaderBox;
      }

      public void init() {

            //verify if there is already an image
            // and if so, set it.
            setExistingImage();
            dragDrop.dndOperations(this.uploaderBox, DragNDrop.MKT_IMG);
            setListener();
      }

      private void setExistingImage() {

            String mediaPath = DirectoryMgr.getMediaPath('p');
            File path = new File(mediaPath);
            if(path.exists()) {
                  // check the files in the directory if they match the
                  // existing name (-) the mime/ending.
                  String s = FlashCardOps.getInstance().getDeckFileName();
                  s = s.substring(0, s.length() - 4);
                  // blank spaces do not work when fetching from s3
                  // convert blanks to underscore.
                  s = s.replaceAll(" ", "_");
                  // get the files from the directory
                  File[] fileAry = path.listFiles();

                  for(int i = 0; i < fileAry.length; i++) {
                        String imgFile = fileAry[i].getName();
                        // remove mime and the bucket char, plus dot "." = 5.
                        imgFile = imgFile.substring(0, imgFile.length() - 5);
                        if(imgFile.equals(s)) {
                              imgHelper(fileAry[i].getPath());
                              //iView.setImage(new Image("File:" + fileAry[i], true));
                              isNewImgName = false;
                        }
                  }
            }
      }

      private void imgHelper(String urlStr) {
            wd = (int) Math.round(SceneCntl.getConsumerPaneWd() * .38);
            ht = (int) Math.round(wd * .5625);
            // 16:9 ratio
            Rectangle2D viewPort = new Rectangle2D(0,0, wd, ht);

            Image deckImg = new Image("File:" + urlStr,
                wd,
                -1,
                true,
                true,
                true
            );

            iView.setImage(deckImg);
            iView.setViewport(viewPort);
            uploaderBox.getChildren().clear();
            uploaderBox.getChildren().add(iView);
      }


      public String getImgName() {
            return imgName;
      }


      public void setListener() {
            dragDrop.getMediaURLProperty().addListener((obj, old, changed) -> {
                  if( ! old.equals(changed)) {
                        iView = deckImageZoomPan(changed);
                        uploaderBox.getChildren().clear();
                        uploaderBox.getChildren().add(iView);
                        isNewImgName = true;
                  }
            });

            dragDrop.getMediaNameProperty().addListener((obj, old, changed) -> {
                  imgName = changed;
            });
      }

      private ImageView deckImageZoomPan(String urlStr) {
            wd = (int) Math.round(SceneCntl.getConsumerPaneWd() * .38);
            ht = (int) Math.round(wd * .5625);
            // 16:9 ratio
            Rectangle2D viewPort = new Rectangle2D(0,0, wd, ht);

            Image deckImg = new Image("File:" + urlStr,
                wd,
                -1,
                true,
                true,
                true
            );

            AtomicReference<ImageView> iviewAtomicRef = new AtomicReference<>(new ImageView(deckImg));
            iviewAtomicRef.get().setViewport(viewPort);

            uploaderBox.setOnScroll(e -> {
                  iviewAtomicRef.set(XYZ.zoomImage(e, iviewAtomicRef.get(), deckImg));
            });

            uploaderBox.setOnMousePressed(e -> XYZ.set(e, deckImg, wd, ht));
            uploaderBox.setOnMouseDragged(e -> iviewAtomicRef.get().setViewport(XYZ.drag(e)));
            uploaderBox.setOnMouseReleased(e -> XYZ.release());

            return iviewAtomicRef.get();
      }


      /**
       * If DragDrop has changed the image name. IE has been used
       * then saves the pixels shown in the uploaderBox to file. and
       * to the cloud.
       * @param type 'a' for avatar, 'p' for the PUBLIC deck descript image.
       */
      public void snapShot(char type) {
            if(isNewImgName) {
                  WritableImage writableImage = new WritableImage(wd, ht);
                  SnapshotParameters params = new SnapshotParameters();
                  Image img = uploaderBox.snapshot(params, writableImage);
                  FlashCardOps fo = FlashCardOps.getInstance();
                  String[] imgArr = {dragDrop.getMediaName()};

                  fo.saveImage(imgArr[0], img, ".png", type);
                  sendAndCheck(type);
                  //CloudOps.putMedia(imgArr);
            }
      }


      /**
       * Asynchronously sends media to cloud. verifies if the item is there,
       * and re-attempts if it did not succeed.
       */
      private void sendAndCheck(char bucket) {
            //Thread.dumpStack();
            ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
            AtomicInteger count = new AtomicInteger(); // increased to 5, for bad networks.
            //     AtomicBoolean bool = new AtomicBoolean(false);
            if (Utility.isConnected()) {
                  String[] uploads = new String[1];
                  uploads[0] = dragDrop.getMediaNameProperty().get();// mediaMergeUnique(editors.EDITOR_U.getMediaNameArray(), editors.EDITOR_L.getMediaNameArray());
                  Runnable task = () -> {
                        sendMedia(uploads, bucket);
                        try {
                              if (CloudOps.checkImgIsInS3(uploads[0], bucket) || count.get() >= 5) {
                                    scheduledExecutor.shutdownNow();
                              }
                              count.getAndIncrement();
                        } catch (IOException e) {
                              throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                              throw new RuntimeException(e);
                        }
                  };
                  scheduledExecutor.scheduleWithFixedDelay(task, 1, 5, TimeUnit.SECONDS);
            }
      }

      /**
       * If the media does not exist in s3, it sends it. Then checks to ensure
       * it exists. If not, returns false.
       * @param uploads
       * @param bucket either 'p' for public: the deck descript img, or 'a' for the avatar.
       * @return
       */
      private void sendMedia(String[] uploads, char bucket) {
            CloudOps.putMedia(uploads, bucket);
      }
}
