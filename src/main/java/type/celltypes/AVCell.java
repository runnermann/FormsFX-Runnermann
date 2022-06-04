package type.celltypes;

import ch.qos.logback.classic.Level;
import flashmonkey.FlashMonkeyMain;
//import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//import javafx.scene.control.*;
import javafx.scene.image.Image;
//import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
//import org.kordamp.ikonli.entypo.Entypo;
//import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
//import uicontrols.ButtoniKonClazz;
//import uicontrols.SceneCntl;
//import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.media.*;
//import javafx.util.Duration;

import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.LoggerFactory;
//import uicontrols.UIColors;

//import static javafx.application.Platform.runLater;

/**
 * @author Lowell Stadelman
 */

public class AVCell implements MediaPlayerInterface {
    
    //private static final Logger LOGGER = LoggerFactory.getLogger(AVCell.class);
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AVCell.class);

    private MediaPlayer mediaPlayer;
    private MediaView mediaViewer;
    private File mediaFile;
    private Pane rightPane;

    private int origHt;
    private int origWd;


    /**
     * no args constructor. Sets notPause to false!
     */
    public AVCell() {
        rightPane = new Pane();
    }


    /**************************************************************************
                                BUILDERS
     **************************************************************************/


    private AVCell mc = this;
    /**
     * Builds the mediaCell from the data provided in the parameters
     * @param mediaWd ..
     * @param mediaHt ..
     * @param mediaPathStr The mediaPath
     * @return the pane containing the cell
     */
    public Pane buildCell(double mediaWd, double mediaHt, String mediaPathStr) {

        LOGGER.setLevel(Level.DEBUG);
        LOGGER.info("\n***in first AVCell.buildCell() ***");

        // Create file from media path
        mediaFile = new File(mediaPathStr);
        rightPane = new Pane();



        if (mediaFile.exists()) {
            Media media = new Media(mediaFile.toURI().toString());
            this.origWd = media.getWidth();
            this.origHt = media.getHeight();
            // create media from file
            //if(ok.get()) {
            LOGGER.info("file: " + mediaFile.toURI() + " exists");
            rightPane = buildRightCell(mediaPathStr, mediaWd, mediaHt);
            rightPane.setId("rightPaneWhite");
            rightPane.setPadding(new Insets(4, 0, 0, 0));
            rightPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    FlashMonkeyMain.getVideoWindow(mediaPathStr);
                }
            });

            return rightPane;

        } else { // media file cannot be found, show problem img currently poop img.
            LOGGER.warn("video/audio does not exist, calling oops.png: filePath: ", mediaFile.toURI());
            String imagePath = "File:" + "src/image/poop_img_problem.png";
            mediaFile = new File(imagePath);
            //LOGGER.info("poopImoji imagePath exists: " + mediaFile.exists());
            ImageView iView = new ImageView(imagePath);
            rightPane.getChildren().add(iView);

            return rightPane;
        }
    }

    /**
     * Builds the mediaCell (Video and audio) using width and height.
     * The rightCell will run in a recursive loop created by the caller
     * due to problems with the video not being loaded into the cell. The
     * loop will run either until it exceeds the count, or the MediaPlayer
     * status is Ready. As of 2022-04-12 this appears to be a windows 10/11
     * issue as reported by Java-Bug ???.
     * The Scheduled executor creates a threadpool to
     * @param prefWd
     * @param prefHt
     * @return
     */
    private int count = 0; // increased to 5, for bad networks.
    private boolean isShowing = false;
    private synchronized Pane buildRightCell(String mediaPathStr, Double prefWd, Double prefHt) {
        System.out.println("\n*** buildRightCell called in AVCell ***");
        // temp fix for Java-Bug ??? for windows. Video does not always load in win 10/11

        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

        VBox mediaVBox = new VBox();
        Image playIcon = new Image("image/play_blue3.png");
        Image loaderIcon = new Image("image/play.png");
        ImageView playIconImgView = new ImageView(loaderIcon);
        playIconImgView.setSmooth(true);
        playIconImgView.setPreserveRatio(true);
        playIconImgView.setFitWidth(50);
        mediaVBox.setAlignment(Pos.CENTER);
        mediaVBox.getChildren().add(playIconImgView);
        mediaVBox.setMinSize(prefWd, 100);
        mediaVBox.setAlignment(Pos.CENTER);

        Runnable task = () -> {
                // create player
                Media media = new Media(mediaFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                MediaView mediaViewer = new MediaView(mediaPlayer);
                mediaViewer.setPreserveRatio(true);
                mediaViewer.setSmooth(true);

                System.out.println("running task and count is: " + count);
                mediaPlayer.statusProperty().addListener((obs, ov, nv) -> {
                    System.out.println("in listener, mediaPlayer statusProperty is: " + nv);
                    if (nv == MediaPlayer.Status.READY) {
                        scheduledExecutor.shutdownNow();
                        isShowing = true;
                        mediaViewer.setFitWidth(prefWd);
                        mediaViewer.setFitHeight(prefHt);
                        StackPane viewerPane = new StackPane(mediaViewer);
                        viewerPane.setAlignment(Pos.CENTER);
                        playIconImgView.setImage(playIcon);
                        viewerPane.getChildren().add(playIconImgView);
                        mediaVBox.getChildren().clear();
                        mediaVBox.getChildren().add(viewerPane);
                        mediaVBox.setAlignment(Pos.TOP_CENTER);
                        mediaVBox.setPadding(new Insets(4, 0, 0, 0));
                    }
                });
                //mediaPlayer.dispose();

                if(count++ > 9) {
                    scheduledExecutor.shutdownNow();
                    //this.rightPane.getChildren().clear();
                    //this.rightPane.getChildren().add(buildRightCell(mediaPathStr, prefWd, prefHt));
                }
        };
        //if(scheduledExecutor.isTerminated())
        scheduledExecutor.scheduleWithFixedDelay(task, 624, 300, TimeUnit.MILLISECONDS);

        return new Pane(mediaVBox);
    }
    
    /**
     * Action taken when the window/stage is closed
     * Stops audio or video, and clears memory.
     */
    public void onClose() {
        mediaPlayer.dispose();
        mediaViewer = null;
    }
}
