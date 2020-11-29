package type.celltypes;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.kordamp.ikonli.entypo.Entypo;
import uicontrols.ButtoniKonClazz;
import uicontrols.SceneCntl;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.media.*;
import javafx.util.Duration;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uicontrols.UIColors;

import static javafx.application.Platform.runLater;

/**
 * @author Lowell Stadelman
 */

public class AVCell {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AVCell.class);
    
    private ButtoniKonClazz VID_BACK_5 = new ButtoniKonClazz("", "Skip back 5 seconds", Entypo.CONTROLLER_FAST_BACKWARD, UIColors.FOCUS_BLUE_OPAQUE);
    private ButtoniKonClazz VID_FWD_5  = new ButtoniKonClazz("", "Skip forward 5 seconds", Entypo.CONTROLLER_FAST_FORWARD, UIColors.FOCUS_BLUE_OPAQUE);
    private ButtoniKonClazz VID_RESET  = new ButtoniKonClazz("", "Reset", Entypo.CCW, UIColors.FOCUS_BLUE_OPAQUE);
    private ButtoniKonClazz VID_PLAY   = new ButtoniKonClazz("", "Play", Entypo.CONTROLLER_PLAY, UIColors.FOCUS_BLUE_OPAQUE);
    private ButtoniKonClazz VID_PAUSE  = new ButtoniKonClazz("", "Pause", Entypo.CONTROLLER_PAUS, UIColors.FOCUS_BLUE_OPAQUE);

    private Duration currentTime;
    private Duration totalDuration;
    private boolean notPause;
    private String strTime;
    //private PopUp popUp;
    private File mediaFile;
    private Label timeLabel;
    
    protected MediaPlayer mediaPlayer;
    private MediaView mediaViewer;
    
    //private Pane rightPane;

    /**
     * no args constructor. Sets notPause to false!
     */
    public AVCell() {
        //rightPane = new Pane();
        notPause = false;
    }



    /**************************************************************************
                                BUILDERS
     **************************************************************************/


    /**
     * Builds the mediaCell from the data provided in the parameters
     * @param pane The pane that it will be displayed in
     * @param mediaPathStr The mediaPath
     * @return
     */
    
    private AVCell mc = this;
    public Pane buildCell(double mediaWd, double mediaHt, String mediaPathStr) {
        
        LOGGER.info("\n***in first AVCell.buildCell() ***");
        timeLabel = new Label();
        timeLabel.setStyle("-fx-text-fill: white");
        //Pane pane = new Pane();
        // Create file from media path
        mediaFile = new File(mediaPathStr);
        // create media from file
        if(mediaFile.exists()) {

            LOGGER.info("file: " + mediaFile.toURI() + " exists");

            Media media = new Media(mediaFile.toURI().toString());
            Pane rightPane = buildCell(media, mediaWd, mediaHt, true);
            rightPane.setOnMouseClicked(e -> {
                new Thread(() -> {
                    runLater(() -> {
                        Pane popupPane;
                        popupPane = buildCell(media, (double) media.getWidth(), (double) media.getHeight(), false);
                        MediaPopUp.getInstance().popUpScene(popupPane, mc);
                    });
                }).start();
            });
            
            return rightPane;

        } else {
            Pane rightPane = new Pane();
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
     * Builds the mediaCell (Video and audio) using width and height
     *
     * @param media
     * @param prefWd
     * @param prefHt
     * @return
     */
    //Button playButton;
    private synchronized Pane buildCell(Media media, Double prefWd, Double prefHt, boolean scaled) {
        System.out.println("\n*** Second buildCell in AVCell ***");
    
        Button resetButton;// = ButtoniKon.VID_RESET.get();
        Button playButton; // = ButtoniKon.VID_PLAY.get();
        Button forward5Button; // = ButtoniKon.VID_FWD_5.get();
        Button back5Button; // = ButtoniKon.VID_BACK_5.get();
        
        
        VBox mediaVBox = new VBox();
    
        System.out.println("media source: " + media.getSource().toString());
        System.out.println("MediaW: " + media.heightProperty() + " MediaH: " + media.widthProperty());
    
        // create player
        mediaPlayer = new MediaPlayer(media);
        mediaViewer = new MediaView(mediaPlayer);
        mediaViewer.setPreserveRatio(true);
        mediaViewer.setSmooth(true);
        
        // width and height of pop-up ensuring that
        // the video is not larger than the users
        // screen.
        double w = 0;
        double h = 0;
        // Scaled = image in the rightPane
        // We do not show a player in the right pane
        // only in the popup
        if (scaled) {
            mediaViewer.setFitWidth(prefWd);
            mediaViewer.setFitHeight(prefHt);
    
            // show play image over mediaViewer
            Image playIcon = new Image(getClass().getResourceAsStream("/image/play.png"));
            
            ImageView iView = new ImageView(playIcon);
            iView.setSmooth(true);
            iView.setPreserveRatio(true);
            iView.setFitHeight(50);
            
            StackPane viewerPane = new StackPane(mediaViewer);
            viewerPane.setAlignment(Pos.CENTER);
            viewerPane.getChildren().add(iView);
            mediaVBox.getChildren().add(viewerPane);
            mediaVBox.setAlignment(Pos.TOP_CENTER);
    
            //rightPane.setStyle("-fx-background-color: WHITE");
            //pane.getChildren().clear();
            //rightPane.getChildren().add(mediaVBox);
            return new Pane(mediaVBox);
    
        // Its not scaled, it's a popup
        // Show the player
        } else {
    
            Slider timeSlider = getTimeSlider(prefWd);
    
            Slider speedSlider = getSpeedSlider();
            
            HBox speedHBox = new HBox(6);
            Label speedLabel = new Label("Speed");
            speedLabel.setStyle("-fx-text-fill: white");
            speedHBox.getChildren().addAll(speedLabel, speedSlider);
            speedHBox.setAlignment(Pos.CENTER_RIGHT);
    
            // Set size of buttons
            //ButtoniKon.VID_PLAY.setSize(24);
    
            resetButton = VID_RESET.get();
            resetButton.setFocusTraversable(false);
            resetButton.setDisable(true);
            playButton = VID_PLAY.get();
            playButton.setFocusTraversable(false);
            forward5Button = VID_FWD_5.get();
            forward5Button.setFocusTraversable(false);
            back5Button = VID_BACK_5.get();
            back5Button.setFocusTraversable(false);
            
   
            // ensure it is not larger than the screen or
            // resize it to fit.
            if (media.getWidth() > media.getHeight()) {
                // The smaller of the video or the screen
                w = Math.min(media.getWidth(), SceneCntl.getScreenWd());
                mediaViewer.setFitWidth(w);
                // calc the height since we do not get it
                // from this api until later.
                double ratio = w / media.getWidth();
                double rHt = media.getHeight() * ratio;
                // Ensure that the new height is not larger than
                // the height of the screen. Use the smaller
                // of the two.
                if (rHt > SceneCntl.getScreenHt() - 100) {
                    h = Math.min(rHt - 100, media.getHeight() - 100);
                    mediaViewer.setFitHeight(h);
                }
    
                System.out.println(" calculated width: " + w + " calculated height: " + h);
            } else {
                h = (Math.min(media.getHeight(), (SceneCntl.getScreenHt())));
                System.out.println("calculated height: " + h);
                mediaViewer.setFitHeight(h);
            }
        
    
            try {
    
                mediaPlayer.currentTimeProperty().addListener((Observable ov) -> {
                    currentTime = mediaPlayer.getCurrentTime();
                    updateTimeLabelValues(currentTime);
                });
    
                mediaPlayer.setOnReady(() -> {
                    currentTime = mediaPlayer.getCurrentTime();
                    totalDuration = mediaPlayer.getMedia().getDuration();
                    updateTimeLabelValues(currentTime);
                });
            
                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if(!timeSlider.isValueChanging()) {
                        timeSlider.setValue(newTime.toSeconds());
                    }
                });
            
                
                timeSlider.valueProperty().addListener((Observable ov) -> {
                    if (timeSlider.isValueChanging()) {
                        mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                        currentTime = Duration.seconds(timeSlider.getValue());
                    }
                    if(timeSlider.isPressed())
                    {
                        mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                        currentTime = Duration.seconds(timeSlider.getValue());
                    }
                });
            
            
                // Set the button
                playButton.setOnAction((e) -> {
                    //notPause = true;
                    playPauseBtnAction(playButton, resetButton);
                });
            
                // Set the pause button
                resetButton.setOnAction((e) ->
                {
                    resetBtnAction(playButton);
                    resetButton.setDisable(true);
                    timeSlider.setValue(0);
                });
            
                forward5Button.setOnAction((e) -> {
                    mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
                });
                back5Button.setOnAction((e) -> {
                    mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
                });
                
                
                // set actions on pane click
                mediaVBox.setOnMouseClicked(e -> playPauseBtnAction(playButton, resetButton));
            
                // Set an error handler
                mediaPlayer.setOnError(() -> System.out.println(mediaPlayer.getError().getMessage()));
    
                
                mediaVBox.setOnKeyPressed((key) -> {
                    if(key.getCode()== KeyCode.SPACE){
                        playPauseBtnAction(playButton, resetButton);
                    }
                    else if(key.getCode()==KeyCode.RIGHT || key.getCode()==KeyCode.R || key.getCode()==KeyCode.KP_RIGHT){
                        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
                    }
                    else if(key.getCode()==KeyCode.LEFT || key.getCode()==KeyCode.L || key.getCode()==KeyCode.KP_LEFT){
                        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
                    }
                });
            
            } catch (MediaException e) {
                System.err.println("\n\n **** Media Exception ****");
            
                mediaPlayer.setOnError(() -> System.out.println("MediaPlayer Error: " + mediaPlayer.getError().getMessage()));
                media.setOnError(() -> System.out.println("Media Error: " + media.getError().getMessage()));
                mediaViewer.setOnError((MediaErrorEvent me) ->
                {
                    MediaException error = me.getMediaError();
                    MediaException.Type errorType = error.getType();
                    String errorMsg = error.getMessage();
                    System.out.println("MediaViewer Error: " + "\n\t Type: " + errorType + "\n\t Message: " + errorMsg);
                });
            }
    
            HBox buttonBox = new HBox();
            buttonBox.getChildren().addAll(resetButton, back5Button, playButton, forward5Button);
            HBox spaceBox = new HBox(10);
            spaceBox.setMinWidth(200);
            HBox playHBox = new HBox(16);
            playHBox.getChildren().addAll(speedHBox, buttonBox, spaceBox);
            playHBox.setAlignment(Pos.CENTER);
            
            VBox controlVBox = new VBox(8);
            controlVBox.getChildren().addAll(timeSlider, timeLabel);
            controlVBox.getChildren().add(playHBox);
            controlVBox.setStyle("-fx-background-color: " + UIColors.CREATE_PANE_BLUE);
            controlVBox.setAlignment(Pos.CENTER);
            controlVBox.setPadding(new Insets(8, 4, 8, 4));
            
            // container for media viewer / player
            HBox viewerHBox = new HBox();
            viewerHBox.setStyle("-fx-background-color: " + UIColors.FM_GREY);
            viewerHBox.getChildren().add(mediaViewer);
            viewerHBox.setPadding(new Insets(16, 32, 16, 32));
            viewerHBox.setAlignment(Pos.CENTER);

            // Entire pane with viewers and controls
            mediaVBox.getChildren().addAll(viewerHBox, controlVBox);
            mediaVBox.setAlignment(Pos.TOP_CENTER);
            
            //popupPane.getChildren().add(mediaVBox);
            return new Pane(mediaVBox);
 
        } // end of if statement

        //pane.setId("playerPopup");
        //pane.setStyle("-fx-background-color: WHITE");
        //pane.getChildren().clear();
        //pane.getChildren().add(mediaVBox);

        //return pane;
        //return mediaVBox;
    }
    
    /**
     * Action taken when the window/stage is closed
     * Stops audio or video, and clears memory.
     */
    public void onClose() {
        mediaPlayer.dispose();
        mediaViewer = null;
    }
    
    private void back5Action(Slider timeSlider) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
        currentTime = Duration.seconds(timeSlider.getValue());
    }
    
    private void forward5Action(Slider timeSlider) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
        currentTime = Duration.seconds(timeSlider.getValue());
    }




    /**************************************************************************
                                    VIDEO
     **************************************************************************/






    private void playPauseBtnAction(Button playBtn, Button resetBtn)
    {
        // MediaPlayer API is Counter-Intuitive. !???!
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) // || player.getStatus() == MediaPlayer.Status.PAUSED)
        {
            mediaPlayer.pause();
            notPause = false;
            playBtn = VID_PLAY.getPlay(playBtn);
            resetBtn.setDisable(false);
            //updateValues(mediaPlayer, timeSlider);
        }
        else
        {
            mediaPlayer.play();
            notPause = true;
            playBtn = VID_PLAY.getPause(playBtn);
            resetBtn.setDisable(false);
        }
    }


    private void resetBtnAction( Button playBtn)
    {
        //MediaPlayer.Status status = player.getStatus();
        System.out.println("\n\t *** Pause / resume button pressed ***");

        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
        playBtn = VID_PLAY.getPlay(playBtn);
        currentTime = new Duration(0);
        mediaPlayer.setStartTime(currentTime);
        notPause = false;
        
        //return player;
    }
    
    private Slider getTimeSlider(double prefWd) {
        Slider timeSlider = new Slider();
        timeSlider.setMinWidth(prefWd);
        timeSlider.setMaxWidth(prefWd);
        timeSlider.setShowTickMarks(true);
        //timeSlider.setStyle("-fx-background-color: " + UIColors.CREATE_PANE_BLUE);
        return timeSlider;
    }
    
    
    /**
     * Creates a slider that controls the speed of the Media player
     * @return
     */
    private Slider getSpeedSlider() {
        Slider speedSlider = new Slider();
        HBox.setHgrow(speedSlider, Priority.ALWAYS);
        
        speedSlider.setShowTickMarks(true);
        speedSlider.setTooltip(new Tooltip("Media Speed"));
        
        speedSlider.setMinWidth(200);
        speedSlider.setMaxWidth(200);
        //speedSlider.setPrefWidth(70);
        speedSlider.setValue(50);
        speedSlider.valueProperty().addListener((Observable ov) -> {
            if (speedSlider.isValueChanging() || speedSlider.isPressed()) {
                double rate = speedSlider.getValue()/10;
                if(rate<1)
                    rate = 0.5;
                else if(rate < 2)
                    rate = 0.6;
                else if(rate < 3)
                    rate = 0.7;
                else if(rate < 4)
                    rate = 0.8;
                else if(rate < 5)
                    rate = 0.9;
                else if(rate < 6)
                    rate = 1;
                else if(rate < 7)
                    rate = 1.1;
                else if(rate < 8)
                    rate = 1.2;
                else if(rate < 9)
                    rate = 1.3;
                else
                    rate = 1.4;
                mediaPlayer.setRate(rate);
            }
        });
        return speedSlider;
    }
    
    
    /**
     * Updates the
     * @param timeSlider
     */
    private void updateValues(Slider timeSlider) {
        if (timeSlider != null) {

        //    currentTime = player.getCurrentTime();
            strTime = formatTime(currentTime, totalDuration);
            //timeSlider.setDisable(duration.isUnknown());
            if (!timeSlider.isDisabled() && currentTime.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging())
            {
                //Duration d = player.getTotalDuration();
                double total = totalDuration.toMillis();
                //System.out.println("\n\ttotalDuration seconds: " + (int) totalDuration.toSeconds());
                timeSlider.setValue((currentTime.toMillis() / total) * 100);

                //System.out.println("\tslider update: " + timeSlider.getValue());
            }
        }
    }
    
    protected void updateTimeLabelValues(Duration currentTime) {
        if (timeLabel != null) {
            runLater(() -> {
                timeLabel.setText(formatTime(currentTime, totalDuration));
                
            });
        }
    }
    
    
    

    private static String formatTime(Duration elapsed, Duration duration) {
        // Isn't this seconds???
        int originalNumInSeconds = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = originalNumInSeconds / 3600; //(60 * 60)

        if (elapsedHours > 0)
        {
            originalNumInSeconds -= elapsedHours * 3600; //(60 * 60)
        }

        int elapsedMinutes = originalNumInSeconds / 60;
        int elapsedSeconds = originalNumInSeconds - (elapsedHours * 3600) - (elapsedMinutes * 60);

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / 3600; //(60 * 60)
            if (durationHours > 0) {
                intDuration -= durationHours * 3600; //(60 * 60)
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 3600 //(60 * 60)
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
}
