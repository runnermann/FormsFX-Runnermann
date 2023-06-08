package type.celleditors;

import ch.qos.logback.classic.Level;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import fileops.FileOpsUtil;
import fileops.utility.FileExtension;
import flashmonkey.CreateFlash;
import flashmonkey.FlashCardOps;
import flashmonkey.FlashMonkeyMain;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.media.MediaPlayer;
import media.api.JaveInterface;
import org.slf4j.LoggerFactory;
import type.celltypes.DoubleCellType;
import type.tools.imagery.Fit;
import uicontrols.FxNotify;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static type.celleditors.SectionEditor.MAX_DURATION;

/**************************************************************************
 *                      ***** INNER CLASS ******
 *                **** Drag and Drop methods *****
 ***************************************************************************/

public class DragAndDrop {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DragAndDrop.class);
    //private static final Logger LOGGER = LoggerFactory.getLogger(CreateFlash.class);
    // Only one instance of this class may exist within a JVM. Not
    // a 100% solution.
    private static DragAndDrop CLASS_INSTANCE;


    private boolean dNdIsDisabled = false;
    private boolean isImage = false;
    private boolean isVid = false;
    private boolean isJave = false;
    private boolean isFile = false;
    private String vidUrl = null;
    private String errorMsg = null;
    private Image image;

    private final StringProperty aviFileNameProperty = new SimpleStringProperty("");
    private final StringProperty aviURLProperty = new SimpleStringProperty("");
    // Fails = -1, succeeds = 1
//    private final IntegerProperty xferCompleteProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty javeCompleteProperty = new SimpleIntegerProperty(0);
    private final BooleanProperty imgReadyProperty = new SimpleBooleanProperty(false);

    private ProgressIndicator progressIndicator;

    private Runnable task;

//    private String cId = null;
//    private char qOrA = 0;


    private DragAndDrop() {
//        progressIndicator = new ProgressIndicator();
//        mediaURL = "";
    }

    /**
     * <p>Returns an instance of the class. If getInstance hasn't been called before a new instance of the class is
     * created. Otherwise it returns the existing instance.</p>
     * <P>CAUTION: This class conducts operations on seperate threads. Specifically for video, it will modify video for
     * scale and length if it is too large. This can be a long running operation. </P>
     * To initialize: DragNDrop dnd = DragNDrop.newInstance();
     *
     * @return The instance of the class.
     */
    public static synchronized DragAndDrop getInstance() {
        if (CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new DragAndDrop();
        }
        return CLASS_INSTANCE;
    }

    Runnable getTask() {
        return task;
    }

    void setDisabled(boolean b) {
        this.dNdIsDisabled = b;
    }


    /**
     * @return 1 if successful, -1 if failed. Starts at 0;
     */
//    IntegerProperty isImgXferCompletedProperty() {
//        return xferCompleteProperty;
//    }
    BooleanProperty isImgReadyProperty() {
        return imgReadyProperty;
    }

    /**
     * @return 1 if successful, -1 if failed. Starts at 0;
     */
    IntegerProperty isJaveCompleteProperty() {
        return javeCompleteProperty;
    }

    StringProperty getAVIFileNameProperty() {
        return aviFileNameProperty;
    }

    StringProperty getAviURLProperty() {
        return aviURLProperty;
    }


    boolean isImage() {
        return isImage;
    }

    boolean isVid() {
        return isVid;
    }

    boolean isFile() {
        return isFile;
    }

    boolean isJave() {
        return isJave;
    }

    String getVidUrl() {
        return vidUrl;
    }

    String getShapesFileName() {
        String mediaFileName = aviFileNameProperty.get();
        return FileNaming.getShapesName(mediaFileName);
    }

    String getErrorMsg() {
        return errorMsg;
    }

    private void reset() {
        task = null;
        progressIndicator = new ProgressIndicator();
        // listeners isolated to each sectionEditor ???
        imgReadyProperty.set(false);
        javeCompleteProperty.set(0);// = new SimpleIntegerProperty(0);
//        xferCompleteProperty.set(0);// = new SimpleIntegerProperty(0);
        aviFileNameProperty.set("");
        isImage = false;
        isVid = false;
        isFile = false;
        isJave = false;
        vidUrl = null;
        image = null;
    }

    /**
     * Returns the image
     *
     * @return
     */
    Image getImage() {
        return this.image;
    }

    /**
     * Currently, set to copy either an image, URL or File,
     * May need to accept text.
     *
     * @param e ..
     */
    void dragOver(DragEvent e) {
        //if(!fileAccepted || fileRejected) {
        // allows an image, URL or a file
        final Dragboard dragboard = e.getDragboard();
        if (dragboard.hasImage() || dragboard.hasFiles() || dragboard.hasUrl()) {
            e.acceptTransferModes(TransferMode.COPY);
        }
        e.consume();
    }


    /**
     * The drag and drop capability for this card and this section
     *
     * @param e ..
     */
    void dragDropped(DragEvent e, ProgressIndicator pi, String cId, char qOrA, DnDNode node) {
        //       this.node = node;
        boolean isImage = false;
        boolean isVid = false;
        boolean isJave = false;
        boolean isFile = false;
        String vidUrl = "";
//        final StringProperty mediaFileNameProperty = new SimpleStringProperty("");
        // Fails = -1, succeeds = 1
//        final IntegerProperty xferCompleteProperty = new SimpleIntegerProperty(0);
        final IntegerProperty javeCompleteProperty = new SimpleIntegerProperty(0);
        final BooleanProperty aviReadyProperty = new SimpleBooleanProperty(false);

        LOGGER.setLevel(Level.DEBUG);
        boolean isCompleted = false;
        // reset all variables
        reset();


        if (!dNdIsDisabled) {
            LOGGER.info("\n *** In dragDropped(dragevent e) *** ");
            // Transfer the data to the target
            final Dragboard dragboard = e.getDragboard();

            if (dragboard.hasImage()) {
                isImage = true;
                LOGGER.debug("\t dragBoard has Image");
                final String str = dragboard.getUrl();
                // handle .gif animations differently
                final String end = str.substring(str.length() - 3);
                isCompleted = this.transferImage(dragboard.getImage(), end);

                if (isCompleted) {
                    aviXferCompletedAction(node, 1);
                } else {
                    aviXferCompletedAction(node, -1);
                }
            } else if (dragboard.hasFiles()) {
                isFile = true;
                LOGGER.debug("\t dragboard hasFiles: ");

                try {
                    isCompleted = transferMediaFile(dragboard.getFiles(), node);
                    if (isCompleted) {
                        aviXferCompletedAction(node, 1);
                        aviReadyProperty.setValue(true);
                    } else {
                        aviXferCompletedAction(node, -1);
                    }
                } catch (Exception ex) {
                    LOGGER.warn("WARNING: transferMedia(...) Unable to copy video from dragboard");
                    ex.printStackTrace();
                }
            } else if (dragboard.hasUrl()) {
                isVid = true;
                LOGGER.debug("\t dragBoard hasURL ");
                vidUrl = dragboard.getUrl();
                isCompleted = this.transferMediaURL(dragboard.getUrl(), node);
                if (isCompleted) {
                    aviXferCompletedAction(node, 1);
                } else {
                    aviXferCompletedAction(node, -1);
                }
            } else {
                LOGGER.warn("\nDragboard does not contain an image or media \nin the expected format: Image, File, URL");
            }

            //Notify DragEvent if successful.
            e.setDropCompleted(isCompleted);
        } else {
            aviXferCompletedAction(node, -1);
        }

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
    private boolean transferImage(Image img, String mime) {
        LOGGER.debug("*** In transferImage() ***");
        if (img != null) {
            image = img;
            final String deckName = FlashCardOps.getInstance().getDeckLabelName();
            // Transfer the image to the FMCanvas Folder
            // and rename it.
            saveImage(img, mime, deckName);
            return true;
        }
        errorMsg = " That's a drag. That didn't work." +
                "\n Try dragging to the desktop first. " +
                "\n then drag from the desk top";
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
     * @param mime     the file ending .gif for gif, all others .png. Must be three letters. E.g not .jpeg
     *                 Use jpg. jpg JPG JPEG and jpeg are all the same. !?! maybe
     * @param deckName ..
     * @return Returns the imageFileName
     */
    public String saveImage(Image image, String mime, String deckName) {
        final BufferedImage inputImage = SwingFXUtils.fromFXImage(image, null);
        final FileNaming fileNaming = new FileNaming(FileNaming.getImageHash(inputImage), 'i', mime);
        final String mediaFileName = fileNaming.getMediaFileName();
        aviFileNameProperty.setValue(mediaFileName);
        final FlashCardOps fco = FlashCardOps.getInstance();
        fco.saveImage(mediaFileName, image, mime, 'c');

        return mediaFileName;
    }

    /**
     * Transfers a media file based on its type. Discriminates files if they are not
     * of a media type accepted by javaFX. Convert if possible using JAVE2
     *
     * @param files, contains the file to be transferred in [0]
     * @return true if successful
     * @throws EncoderException
     */
    private boolean transferMediaFile(List<File> files, DnDNode node) throws EncoderException {
        // Use the first index contains the dragged file
        final File fromDrag = files.get(0);

        final int num = fromDrag.getName().lastIndexOf('.') + 1;
        String mime = fromDrag.getName().substring(num);
        // convert to 3 letters
        mime = if4LetterImg(mime);

        LOGGER.info(" *** in transferMediaFile(list<File>) and OriginFilePath: " + fromDrag.toPath() + " ***");
        final String fromPath = fromDrag.toPath().toString();
        LOGGER.debug(" mimeType should be image or video: {}", fromPath);

        final MultimediaObject mmObject = new MultimediaObject(new File(fromPath));
        final MultimediaInfo sourceInfo = mmObject.getInfo();

        if (fromPath == null) {
            return false;
        }
        // Is accepted javaFX image for transfer
        // Note that the image will be converted to ".png"
        if (FileExtension.IS_FX_IMAGE.check(mime)) {
            isImage = true;
            // Transfer the image to the FMCanvas Folder
            // and rename it.
            image = new Image("File:" + fromPath);
            //imgReadyProperty.setValue(true);

            return transferMediaURL(fromPath, node);
        } else if (type.tools.video.Fit.checkDuration(sourceInfo, MAX_DURATION)) {
            errorMsg = "Video length cannot be longer than 60 minutes.";
            return false;
        } else if (FileExtension.IS_FX_AV.check(mime) && type.tools.video.Fit.checkSize(sourceInfo)) {
            // Else its a video, or audio, first try to use JavaFX.
            // If not in a format that JavaFX handles,
            // then use JAVE
            MediaPlayer m = null;
            isVid = true;
            isJave = false;
            // Transfer the media to the
            // FMCanvas Folder and
            // rename it.
            this.transferVideoURL(fromPath, node);
            return true;
        } else if (FileExtension.IS_JAVE_VIDEO.check(mime)) {
            // Video is not a format that JavaFX handles, use
            // JAVE for video
            isVid = true;
            isJave = true;
            //Rename the file. End with mp4
            final String mediaPath = DirectoryMgr.getMediaPath('M');
            aviFileNameProperty.set(FileNaming.getVideoName(node.editor.cID, node.editor.qOra, "mp4"));

            final String outputPathName = mediaPath + aviFileNameProperty.get();
            aviURLProperty.set(outputPathName);
            progressIndicator = new ProgressIndicator();

//            LOGGER.debug("Trying to copy file: media outputPathName: {}", outputPathName);

            final File outputFile = new File(outputPathName);

            // Transfer and convert the video to a smaller size
            // if needed.
            final AtomicBoolean hasError = new AtomicBoolean(true);
            task = () -> {
                JaveInterface jave = new JaveInterface();
                try {
                    hasError.set(false);
                    // Convert the video to smaller format and to .mp4
                    jave.transfer(outputFile, sourceInfo, mmObject);

                    // notify the listener that it succeeded.
                    javeCompleteProperty.set(1);

                } catch (EncoderException ex) {
                    jave.stop();
                    // notify the listener that it failed.
                    javeCompleteProperty.set(-1);
                    errorMsg = "The video upload had a problem.";
                    Platform.runLater(() -> stopProcessingVideo(node));
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                    javeCompleteProperty.set(-1);
                    errorMsg = "The video upload had a problem.";
                    Platform.runLater(() -> stopProcessingVideo(node));
                }
            };

//            final Thread thread = new Thread(task);
//            thread.start();
            return true;
        }
        return false;
    }

    /**
     * If the mime or ending of a file is a JPEG or jpeg,
     * converts to a 3 letter ending. The image compression is
     * the same, so we rename it for compatibility with cloud
     * synchronization.
     * @param end
     * @return
     */
    private String if4LetterImg(String end) {
        if (end.equals("JPEG")) {
            end = "JPG";
        } else if (end.equals("jpeg")) {
            end = "jpg";
        }
        return end;
    }


    /**
     * Helper method transfers an image provided in the parameter
     * to a file for this card and section. Resizes the image and
     * sets it in the rightPane.
     *
     * @param imageURL
     * @return true if successful
     */
    private boolean transferMediaURL(String imageURL, DnDNode node) {
        try {
            LOGGER.info("in transferMediaURL");
            copyMediaFile(imageURL, 'C', node);
            return true;
        } catch (Exception e) {
            errorMsg = " That's a drag. That didn't work." +
                    "\n Try dragging to the desktop first. " +
                    "\n then drag from the desk top";
            e.printStackTrace();
        }
        return false;
    }

    public void downloadFile(URL url, String fileName) throws Exception {
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(fileName), REPLACE_EXISTING);
        }
    }


    /**
     * Helper method, Copies a media object, ie video or audio provided in the
     * parameter, to a file for this card and section.
     *
     * @param mediaURLStr The media URL or path for the media to be copied
     * @return ture if successful
     */
    private boolean transferVideoURL(String mediaURLStr, DnDNode node) {
        try {
            LOGGER.info("\n *** in transferVideoURL ***");
            LOGGER.debug("\tMedia coming from mediaURLStr:  {}", mediaURLStr);
            // Create a fileName for this card, and copy
            // the file from the URLstring to it.
            copyMediaFile(mediaURLStr, 'M', node);
            return true;
        } catch (Exception e) {
            LOGGER.warn("WARNING: Unknown Exception transfering MediaURL");
        }
        return false;
    }


    /**
     * Copies a media file to this cards local directory. Provides the
     * path name based on mediapath, Names the file based on cardNum and
     * charLetter. If a folder does not exist, it creates the folder
     * before inserting the card.
     * <p>
     * Non Java capable Audio and Video conversion is provided by JAVE
     * Java Audio Video Encoder. https://github.com/a-schild/jave2
     *
     * @param source    The source path
     * @param mediaType The char type for this file.
     *                  'M '= Media(Audio or Video), 'C' = canvas(Image and Drawings)
     */
    private void copyMediaFile(String source, char mediaType, DnDNode node) {

        LOGGER.info("copyMediaFile called, type: {}, source: {}", mediaType, source);

        //Rename the file. Get the ending of the file.
        final int idx = source.lastIndexOf('.');
        final String ending = if4LetterImg( source.substring(idx + 1) );
        final DirectoryMgr dirMgr = new DirectoryMgr();
        final String mediaPath = DirectoryMgr.getMediaPath(mediaType);
        LOGGER.debug("mediaPath: " + mediaPath);

        if (FileOpsUtil.folderExists(new File(mediaPath))) {
            LOGGER.debug("copyMediaFile folder exists");
            if (mediaType == 'M') {
                try {
                    final String mediaFileName = FileNaming.getVideoName(node.editor.cID, node.editor.qOra, ending);
                    aviFileNameProperty.setValue(mediaFileName);
                    final String mediaURL = mediaPath + mediaFileName;
                    aviURLProperty.set(mediaURL);
                    java.nio.file.Path original = Paths.get(source);
                    java.nio.file.Path target = Paths.get(mediaURL);
                    Files.copy(original, target, REPLACE_EXISTING);
                } catch (IOException e) {
                    LOGGER.warn("WARNING: IOException while copying file in SectionEditor" +
                            "\n from source: {}", source);
                    e.printStackTrace();
                } catch (Exception s) {
                    LOGGER.warn(s.getMessage());
                    s.printStackTrace();
                }
            } else {
                // it is an image
                final Image image = new Image("File:" + source);
                final BufferedImage inputImage = SwingFXUtils.fromFXImage(image, null);
                final FileNaming fileName = new FileNaming(FileNaming.getImageHash(inputImage), 'i', "." + ending);
                aviFileNameProperty.set(fileName.getMediaFileName());
                // Resize image if larger than 800 x 800
                if (800 < image.getHeight() || 800 < image.getWidth()) {
                    double scale = Fit.calcScale(image.getWidth(), image.getHeight(), 800, 800);
                    LOGGER.debug("scale: " + scale);

                    final double scaledHeight = image.getHeight() * scale;
                    final double scaledWidth = image.getWidth() * scale;
                    java.nio.file.Path targetImg = null;
                    try {
                        // create output image
                        LOGGER.info("copying image by hand");

                        targetImg = Paths.get(mediaPath + getAVIFileNameProperty().get());

                        LOGGER.debug("image getType: " + inputImage.getType()
                                + "\n ht" + (int) scaledHeight
                                + "\n wd" + (int) scaledWidth
                                + "\n imgPath:" + targetImg
                                + "\n ending: " + ending
                        );
                        // create output image
                        final BufferedImage outputImage = new BufferedImage((int) scaledWidth,
                                (int) scaledHeight, inputImage.getType());
                        final Graphics2D g2d = outputImage.createGraphics();
                        g2d.drawImage(inputImage, 0, 0, (int) scaledWidth, (int) scaledHeight, null);
                        ImageIO.write(outputImage, ending, new File(targetImg.toUri()));
                        g2d.dispose();
                    } catch (IOException e) {
                        LOGGER.error("Error copying image: {}", e.getMessage());
                        //e.printStackTrace();
                    }

                    final File check = new File(targetImg.toUri());
                    if (!check.exists()) {
                        LOGGER.warn("WARNING: Image file does not exist");
                    }

                } else {
                    // the image is smaller than 800x800
                    final String mediaFileName = aviFileNameProperty.get();
                    final String mediaURL = mediaPath + mediaFileName;
                    java.nio.file.Path original = Paths.get(source);
                    java.nio.file.Path target = Paths.get(mediaURL);
                    try {
                        Files.copy(original, target, REPLACE_EXISTING);
                    } catch (IOException e) {
                        LOGGER.warn("WARNING: IOException while copying file in SectionEditor");
                    }
                }
            }
        }
    }

    //* **********************************************************************************
    //
    //                                  INNER CLASS
    //
    //* **********************************************************************************


    /**
     * May set a thread to handle long-running video processing.
     * Ensure usage is thread safe.
     */
    //@Override
    public void aviXferCompletedAction(DnDNode node, int val) {
        node.editor.iView = null;
        if (val == 1) {

            System.out.println("\n\n\nValue changed in Section Editor \n\n\n");
            node.editor.getArrayOfFMShapes().clear();
            node.editor.setMediaFileName(getAVIFileNameProperty().get());
            if (isImage) {
                node.editor.sectionType = DoubleCellType.CANVAS;
                // Sets the image in the rPane from
                // the image created in transferImage(...)
                node.editor.setImageHelperForRPane(image);
            } else if (isVid) {
                node.editor.sectionType = DoubleCellType.AV;
                if (isJave) {
                    isJaveCompleteProperty().addListener((i, j, k) -> {
                        if (k.intValue() == 1) {
                            Platform.runLater(() -> runVideoHelper(aviURLProperty.get(), node));
                        } else {
                            // failed
                        }
                    });

                    Thread thread = new Thread(task);
                    thread.start();
                }
            }
        } else {
            // It failed.
            node.editor.deleteMMcellAction();

            FxNotify.notificationError("OUCH!!!!", getErrorMsg(), Pos.CENTER, 7,
                    "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getPrimaryWindow());
        }
    }


    String[] setMediaFileNames(DnDNode node) {
        // String[] m;// = new String[];
        // images always have shapeFileNames, video and audio do not
        // if (shapesFileNameProperty.get() != null) {
        //     m = new String[2];
        //     m[0] = aviFileName;
        //     m[1] = shapesFileName;
        // } else {
        String[] m = new String[1];
        m[0] = node.editor.aviFileNameProperty.get();
        //}
        return m;
    }

    private void runVideoHelper(String outputPathName, DnDNode node) {
        node.editor.progressIndicator = null;
        node.editor.setVideoHelper(outputPathName, 100, 100);
    }

    private void stopProcessingVideo(DnDNode node) {
        // Error stop processing.
        node.editor.progressIndicator = null;
        node.editor.aviFileNameProperty.set("");
        //aviFileName = null;
        node.editor.deleteMMcellAction();
        String msg = "The video upload had a problem.";

        FxNotify.notificationError("Oooph", msg, Pos.CENTER, 5,
                "image/flashFaces_sunglasses_60.png", FlashMonkeyMain.getPrimaryWindow());

    }

}



