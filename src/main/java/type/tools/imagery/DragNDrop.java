package type.tools.imagery;

import authcrypt.UserData;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import fileops.FileOpsUtil;
import fileops.utility.FileExtension;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.FxNotify;
import ws.schild.jave.EncoderException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DragNDrop {

      private static final Logger LOGGER = LoggerFactory.getLogger(DragNDrop.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DragNDrop.class);

      // p for public
      public static final char DECK_IMG = 'p';
      // a for avatar
      public static final char AVATAR_IMG = 'a';

      private final StringProperty mediaNameProperty;
      private final StringProperty mediaURLProperty;

      DragNDrop() {
            mediaNameProperty = new SimpleStringProperty("");
            mediaURLProperty = new SimpleStringProperty("");
      }

      /**
       * Once the dragNDrop has completed. It provides the complete
       * media URL for the local file. Returns the property for a listener.
       * @return
       */
      public StringProperty getMediaURLProperty() {
            return mediaURLProperty;
      }

      public StringProperty getMediaNameProperty() {
            return mediaNameProperty;
      }

      public String getMediaName() {
            return mediaNameProperty.get();
      }

      /**
       * The drag and drop handler for this card and section.
       */
      void dndOperations(Node node, char bucket) {
            node.setOnDragOver(this::dragOver);
            node.setOnDragDropped( e -> this.imageDragDropped(e, bucket));
      }


      /**
       * Currently, set to copy either an image, URL or File,
       * May need to accept text.
       *
       * @param e ..
       */
      private void dragOver(DragEvent e) {
            // allows an image, URL or a file
            Dragboard dragboard = e.getDragboard();
            if (dragboard.hasImage() || dragboard.hasFiles() || dragboard.hasUrl()) {
                  LOGGER.debug("\t file is accepted ");
                  e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
      }


      /**
       * The drag and drop capability
       *
       * @param e the DragEvent.
       * @param bucket The destination or type should be 'a' for avatar or 'p' public: for
       *               Deck Image.
       * Returns false if the file was NOT an FX compatible image. Otherwise will
       * return true.
       */
      private void imageDragDropped(DragEvent e, char bucket) {
            //LOGGER.setLevel(Level.DEBUG);
            boolean isCompleted = false;

            LOGGER.info("\n *** In dragDropped(dragevent e) *** ");
            // Transfer the data to the target
            Dragboard dragboard = e.getDragboard();

            if (dragboard.hasImage()) {
                  LOGGER.debug("\t dragBoard has Image");
                  String str = dragboard.getUrl();
                  // handle .gif animations differently
                  String ending = str.substring(str.length() - 3);
                  isCompleted = this.transferImage(dragboard.getImage(), ending, bucket);
            }
            else if (dragboard.hasFiles()) {
                  LOGGER.debug("\t dragboard hasFiles: ");
                  //iView = null;
                  try {
                        isCompleted = transferMediaFile(dragboard.getFiles(), bucket);
                  } catch (Exception ex) {
                        LOGGER.warn("WARNING: transferMedia(...) Unable to copy video from dragboard");
                        ex.printStackTrace();
                  }
            }
            else {
                  LOGGER.warn("\nDragboard does not contain an image or media \nin the expected format: Image, File, URL");
            }

            LOGGER.debug("dragDropped isCompleted: <{}>", isCompleted);
            //Notify DragEvent if successful.
            e.setDropCompleted(isCompleted);

            e.consume();
            CreateFlash.getInstance().setFlashListChanged(true);
      }


      /**
       * Intended as an internal method on a drag-drop action.
       * Sets this objects ImageView to the image in the parameter. Names the
       * image, sets the sectionType to 'C', saves the image, and sets the
       * shapesFileName.
       *
       * @param img  ..
       * @param mime the file ending either .png for all except, if. gif use .gif
       * @return true if successful
       */
      private boolean transferImage(Image img, String mime, char bucket) {

            LOGGER.debug("*** In transferImage() ***");

            if (img != null) {
                  // String deckName = FlashCardOps.getInstance().getDeckLabelName();
                  // Transfer the image to the FMCanvas Folder
                  // and rename it.
                  saveImage(img, mime, bucket);

                  return true;
            }
            return false;
      }

      // Methods to create a file for SnapShot are in SnapShot.

      /**
       * Names an image file and saves a javaFX Image to the
       * correct media directory based on the
       * parameters.
       * If file is not saved, a warning message is printed to the log.
       *
       * @param image    The image to be saved
       * @param mime     the file ending .gif for gif, all others .png
       * @return Returns the imageFileName
       */
      private String saveImage(Image image, String mime, char bucket) {
            FileNaming fileNaming = getDeckMediName(mime);
            String mediaName = fileNaming.getMediaFileName();
            FlashCardOps fco = FlashCardOps.getInstance();
            boolean bool = fco.saveImage(mediaName, image, mime, bucket);
            if (!bool) {
                  String errorMessage = " That's a drag. That didn't work." +
                      "\n Try dragging to the desktop first. " +
                      "\n then drag from the desk top";
                  FxNotify.notificationError("OUCH!!!!", errorMessage, Pos.CENTER, 7,
                      "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getPrimaryWindow());
            } else {
                  String mediaPath = DirectoryMgr.getMediaPath(bucket);
                  mediaNameProperty.set(mediaName);
                  mediaURLProperty.set( mediaPath + mediaName);
            }
            return mediaName;
      }

      /**
       * Transfers a media file based on its type. Discriminate files if they are not
       * of a media type accepted by javaFX. Convert video if possible using JAVE2
       *
       * @param files, contains the file to be transferred in [0]
       * @return true if successful
       * @throws EncoderException
       */
      private boolean transferMediaFile(List<File> files, char bucket) throws EncoderException {
            // Use the first index contains the dragged file
            File fromDrag = files.get(0);

            int num = fromDrag.getName().lastIndexOf('.') + 1;
            String mime = fromDrag.getName().substring(num);
            if(mime.equals("JPEG")) {
                  mime = "JPG";
            } else if (mime.equals("jpeg")) {
                  mime = "jpg";
            }

            LOGGER.info(" *** in transferMediaFile(list<File>) and OriginFilePath: " + fromDrag.toPath() + " ***");
            String fromPath = fromDrag.toPath().toString();
            LOGGER.debug(" mimeType should be image or video: {}", fromPath);

            if (fromPath == null) {
                  return false;
            }
            // else Is accepted javaFX image for transfer
            // Note that the image will be converted to ".png"
            if (FileExtension.IS_FX_IMAGE.check(mime)) {
                  // Transfer the image to the appropriate Folder
                  transferImageURL(fromPath, bucket);

                  return true;
            }
            return false;
      }


      /**
       * Helper method transfers an image provided in the parameter
       * to a file for this card and section. Resizes the image and
       * sets it in the rightPane.
       *
       * @param imageURL
       * @return true if successful
       */
      private boolean transferImageURL(String imageURL, char bucket) {
            try {
                  LOGGER.info("in transferImageURL");
                  copyMediaFile(imageURL, bucket);
                  return true;
            } catch (Exception e) {

                  LOGGER.warn("WARNING:  Unknown Exception transfering ImageURL {}", e.getMessage());
                  String errorMessage = " That's a drag. That didn't work." +
                      "\n Try dragging to the desktop first. " +
                      "\n then drag from the desk top";
                  FxNotify.notificationError("OUCH!!!!", errorMessage, Pos.CENTER, 7,
                      "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getPrimaryWindow());
                  e.printStackTrace();
            }
            return false;
      }

      public void downloadFile(URL url, String fileName) throws Exception {
            try (InputStream in = url.openStream()) {
                  Files.copy(in, Paths.get(fileName));
            }
      }


      /**
       * Helper method, Copies a media object, ie video or audio provided in the
       * parameter, to a file for this card and section.
       *
       * @param mediaURLStr The media URL or path for the media to be copied
       * @return ture if successful
       */
      private boolean transferVideoURL(String mediaURLStr) {
            try {
                  LOGGER.info("\n *** in transferVideoURL ***");
                  LOGGER.debug("\tMedia coming from mediaURLStr:  {}", mediaURLStr);
                  // Create a fileName for this card, and copy
                  // the file from the URLstring to it.
                  copyMediaFile(mediaURLStr, 'M');
                  return true;
            } catch (Exception e) {
                  LOGGER.warn("WARNING: Unknown Exception transfering MediaURL");
            }
            return false;
      }


      /**
       * <p>
       * Copies a media file to FlashMonkey directory of this machine. Provides the
       * path name based on mediapath, Names the file based on the image
       * file appearance. If a folder does not exist, it creates the folder.
       * </p>
       * <p>
       * Non Java capable Audio and Video conversion is provided by JAVE
       * Java Audio Video Encoder. https://github.com/a-schild/jave2
       *</p>
       * @param source    The source path
       * @param bucket The char type for this file.
       *                  'm '= Media(Audio or Video), 'c' = canvas(Image and Drawings), 'p' = public for mktplace images
       *                  'a' = for user avatar
       */
      private void copyMediaFile(String source, char bucket) {
            LOGGER.info("copyMediaFile called, type: {}, source: {}", bucket, source);

            //Rename the file. Get the ending of the file.
            int idx = source.lastIndexOf('.');
            String ending = source.substring(idx + 1);
            String mediaPath = DirectoryMgr.getMediaPath(bucket);

            if (FileOpsUtil.folderExists(new File(mediaPath))) {
                  LOGGER.debug("copyMediaFile folder exists");
                  // it is an image
                  Image image = new Image("File:" + source);
                  BufferedImage inputImage = SwingFXUtils.fromFXImage(image, null);
                  // The deck image is always the name of the deck.
                  FileNaming fileName;
                  if(bucket == 'p') {
                        fileName = getDeckMediName(ending);
                  } else if(bucket == 'a') {
                        fileName = new FileNaming(UserData.getUserMD5Hash(), bucket, "." + ending);
                  } else {
                        fileName = new FileNaming(FileNaming.getImageHash(inputImage), bucket, "." + ending);
                  }
                  LOGGER.debug("the image is smaller than 800x800");
                  String mediaURL = mediaPath + fileName.getMediaFileName();
                  java.nio.file.Path original = Paths.get(source);
                  java.nio.file.Path target = Paths.get(mediaURL);
                  try {
                        LOGGER.debug("trying to copy the file to: {}", mediaURL);
                        Files.copy(original, target, REPLACE_EXISTING);
                        // After we copy the file, change the names of the properties.
                        // so the listeners know to change the values.
                        mediaNameProperty.set( fileName.getMediaFileName() );
                        mediaURLProperty.set( mediaURL );
                  } catch (IOException e) {
                        LOGGER.warn("WARNING: IOException while copying file in SectionEditor");
                  }
            }
      }

      /**
       * The deckFileName is always the same as the deck name. Method provides
       * the new media file name with the proper ending.
       * @param ending the ending of the Image. jpg, png...
       * @return Media File Name for the Deck Image.
       */
      public static final FileNaming getDeckMediName(String ending) {
            String s = FlashCardOps.getInstance().getDeckFileName();
            int n = s.indexOf(".");
            s = s.substring(0, n);
            FileNaming f = new FileNaming(s, 'p', "." + ending);
            return f;
      }
}