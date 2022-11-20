package type.celltypes;


import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.util.Duration;
//import org.kordamp.ikonli.entypo.Entypo;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import uicontrols.ButtoniKon;
import uicontrols.ButtoniKonClazz;
import uicontrols.SceneCntl;
import uicontrols.UIColors;

import java.awt.event.KeyListener;
import java.io.File;

import static javafx.application.Platform.runLater;


/**
 * <p>This class Creates a new Window containing a VideoPlayer</p>
 * The VideoPlayerPopUp class provides a Media, MediaPlayer, and MediaView for a video or audio file. Although
 * audio is not the intent of this player. The class creates a new Media and MediaPlayer object providing independence
 * from other players that are used in the section editors and readers. This class creates the MediaView when it is
 * initialized and the MediaView is used throught the life of this object. It is intended that a single MediaView is
 * used for all PopUps. This is a Singleton Class.
 *
 * <p>The MediaPlayerInterface provides a mechanism to dispose of the player to eleminate the player when needed.</p>
 * <p>The VideoPlayerPopUp extends Application and Implements MediaPlayerInterface</p>
 */
public class VideoPlayerPopUp implements MediaPlayerInterface {

      private static VideoPlayerPopUp CLASS_INSTANCE;

      private static Media media;
      private static MediaPlayer mediaPlayer;
      private static MediaView singleMViewer;
      private final ButtoniKonClazz VID_PLAY = new ButtoniKonClazz("", "Play", FontAwesomeSolid.PLAY, UIColors.FM_WHITE, ButtoniKonClazz.SIZE_24);
      private final ButtoniKonClazz VID_PAUSE = new ButtoniKonClazz("", "Pause", FontAwesomeSolid.PAUSE, UIColors.FM_WHITE, ButtoniKonClazz.SIZE_24);
      private final int prefWd = 624;
      private int prefHt;

      private final Button resetButton = ButtoniKon.getVidResetButton();
      private static Button playButton;// = ButtoniKon.getVidPlayButton();
      private final Button forward5Button = ButtoniKon.getVidFwdButton();
      private final Button back5Button = ButtoniKon.getVidBackButton();

      private static Duration currentTime;
      private static Duration totalDuration;
      private static boolean notPause;
      private static String strTime;
      private static File mediaFile;
      private static Label timeLabel;

      private static Slider timeSlider;
      private static Slider speedSlider;

      //ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);


      /**
       * private constructor for singleton class
       */
      private VideoPlayerPopUp() {
            singleMViewer = new MediaView();
      }

      /**
       * Returns an instance of this singleton class.
       * Synchronized. Expect only
       * one to exist within a JVM.
       *
       * @return Class instance
       */
      public static synchronized VideoPlayerPopUp getInstance(String filePathName) {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new VideoPlayerPopUp();
            }
            init(filePathName);
            return CLASS_INSTANCE;
      }

      private static void init(String filePathName) {
            mediaFile = new File(filePathName);
            media = new Media(mediaFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            // use the same MediaView for all popups.
            singleMViewer.setMediaPlayer(mediaPlayer);
            singleMViewer.setPreserveRatio(true);
            singleMViewer.setSmooth(true);
            //fit(media);
      }


      /**
       * The video player main access method.
       *
       * @return Returns a stackpane contianing the
       * video player and controls.
       */
      public StackPane getVideoPlayerPane() {
            setControls(media);


            HBox buttonBox = new HBox();
            buttonBox.getChildren().addAll(resetButton, back5Button, playButton, forward5Button);
            HBox spaceBox = new HBox(10);
            spaceBox.setMinWidth(200);

            HBox speedHBox = speedHBox();
            HBox playHBox = playHBox(speedHBox, buttonBox, spaceBox);
            VBox controlVBox = setControlVBox(playHBox);
            setControllerStyles();
            StackPane playContainer = playContainer();

            //singleMViewer.setFitWidth(1024);
            mediaPlayer.statusProperty().addListener((obs, ov, nv) -> {
                  if (nv == MediaPlayer.Status.READY) {
                        HBox viewerHBox = viewerHBox();
                        playContainer.getChildren().addAll(viewerHBox, controlVBox);
                        playContainer.setAlignment(Pos.BOTTOM_CENTER);
                  }
            });

            return playContainer;
      }


      /**
       * Ensures the video (view and player) is not larger
       * than the users screen.
       *
       * @param
       * @return
       */
//    private static void fit(Media media) {
//        double viewW = singleMViewer.getFitWidth();
//        System.out.println(singleMViewer.getLayoutBounds().getHeight());
//        System.out.println(singleMViewer.getMediaPlayer().getMedia().getHeight());
//        double viewH = singleMViewer.getViewport().getHeight();
//        double h = 0;
//        double w = 0;
//
//        System.out.println("media getMetaData: " + media.getMetadata().toString());
//        System.out.println("media getMetaData.size()" + viewW + " " + viewH);
//        // if wd is greater than ht, set the video size
//        // to the video or screen width depending on the smaller
//        // of the two.
//        if (media.getWidth() > media.getHeight()) {
//            // determine the smaller of the video or the screen
//            w = Math.min(media.getWidth(), SceneCntl.getScreenWd());
//            singleMViewer.setFitWidth(w);
//            // calc the height since we do not get it
//            // from this api until later.
//            double ratio = w / media.getWidth();
//            double rHt = media.getHeight() * ratio;
//            // Ensure that the new height is not larger than
//            // the height of the screen. Use the smaller
//            // of the two.
//            if (rHt > SceneCntl.getScreenHt() - 100) {
//                h = Math.min(rHt - 100, media.getHeight() - 100);
//                singleMViewer.setFitHeight(h);
//            }
//        } else {
//            h = Math.min(media.getHeight(), (SceneCntl.getScreenHt()));
//            System.out.println("in VdieoPlayerPopUp, calculated height: " + h);
//            singleMViewer.setFitHeight(h);
//        }
//        System.out.println(" calculated width: " + w + " calculated height: " + h);
//    }
      private void setControllerStyles() {
            timeLabel.setStyle("-fx-text-fill: white");
            timeSlider.setId("slider");
            playButton.setId("clearButtonSquare");

      }

      private HBox viewerHBox() {
            HBox viewerHBox = new HBox();
            viewerHBox.setStyle("-fx-background-color: " + UIColors.FM_GREY);
            viewerHBox.getChildren().add(singleMViewer);
            viewerHBox.setPadding(new Insets(16, 32, 16, 32));
            viewerHBox.setAlignment(Pos.CENTER);
            return viewerHBox;
      }

      private StackPane playContainer() {
            StackPane stack = new StackPane();
            stack.setAlignment(Pos.BOTTOM_CENTER);

            stack.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
                  @Override
                  public void handle(KeyEvent key) {
                        if (key.getCode() == KeyCode.SPACE) {
                              playPauseBtnAction(playButton, resetButton);
                        } else if (key.getCode() == KeyCode.RIGHT || key.getCode() == KeyCode.R || key.getCode() == KeyCode.KP_RIGHT) {
                              mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
                        } else if (key.getCode() == KeyCode.LEFT || key.getCode() == KeyCode.L || key.getCode() == KeyCode.KP_LEFT) {
                              mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
                        }
                  }
            });
            return stack;
      }


      private HBox playHBox(HBox speedHBox, HBox buttonBox, HBox spaceBox) {
            HBox playHBox = new HBox(16);
            playHBox.getChildren().addAll(speedHBox, buttonBox, spaceBox);
            playHBox.setAlignment(Pos.CENTER);
            return playHBox;
      }

      private HBox speedHBox() {
            HBox speedHBox = new HBox(6);
            Label speedLabel = new Label("Speed");
            speedLabel.setStyle("-fx-text-fill: white");
            speedHBox.getChildren().addAll(speedLabel, speedSlider);
            speedHBox.setAlignment(Pos.CENTER_RIGHT);
            return speedHBox;
      }

      private VBox setControlVBox(HBox playHBox) {
            VBox controlVBox = new VBox(8);
            controlVBox.getChildren().addAll(timeSlider, timeLabel);
            controlVBox.getChildren().add(playHBox);
            controlVBox.setStyle("-fx-background-color: TRANSPARENT");
            controlVBox.setAlignment(Pos.BOTTOM_CENTER);
            controlVBox.setPadding(new Insets(8, 4, 8, 4));
            return controlVBox;
      }


      /**************************************************************************
       CONTROLS
       **************************************************************************/

      private void setControls(Media media) {
            timeLabel = new Label();
            timeSlider = getTimeSlider(prefWd);
            speedSlider = getSpeedSlider();

            //resetButton = VID_RESET.get();
            resetButton.setFocusTraversable(false);
            resetButton.setDisable(true);
            playButton = VID_PLAY.get();
            playButton.setFocusTraversable(false);
            //forward5Button = VID_FWD_5.get();
            forward5Button.setFocusTraversable(false);
            //back5Button = VID_BACK_5.get();
            back5Button.setFocusTraversable(false);

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
                        if (!timeSlider.isValueChanging()) {
                              timeSlider.setValue(newTime.toSeconds());
                        }
                  });

                  timeSlider.valueProperty().addListener((Observable ov) -> {
                        if (timeSlider.isValueChanging()) {
                              mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                              currentTime = Duration.seconds(timeSlider.getValue());
                        }
                        if (timeSlider.isPressed()) {
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
                  resetButton.setOnAction((e) -> {
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
            } catch (MediaException e) {
                  System.err.println("\n\n **** Media Exception ****");

                  mediaPlayer.setOnError(() -> System.err.println("MediaPlayer Error: " + mediaPlayer.getError().getMessage()));

                  singleMViewer.setOnError((MediaErrorEvent me) ->
                  {
                        MediaException error = me.getMediaError();
                        MediaException.Type errorType = error.getType();
                        String errorMsg = error.getMessage();
                       // System.out.println("MediaViewer Error: " + "\n\t Type: " + errorType + "\n\t Message: " + errorMsg);
                  });
            }
      }


      private void playPauseBtnAction(Button playBtn, Button resetBtn) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) // || player.getStatus() == MediaPlayer.Status.PAUSED)
            {
                  mediaPlayer.pause();
                  notPause = false;
                  playBtn = VID_PLAY.getPlay(playBtn);
                  resetBtn.setDisable(false);
            } else {
                  mediaPlayer.play();
                  notPause = true;
                  playBtn = VID_PLAY.getPause(playBtn);
                  resetBtn.setDisable(false);
            }
      }

      private void back5Action() {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
            //currentTime = Duration.seconds(timeSlider.getValue());
      }

      private void forward5Action() {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
            //currentTime = Duration.seconds(timeSlider.getValue());
      }

      private Slider getTimeSlider(double prefWd) {
            Slider timeSlider = new Slider();
            timeSlider.setMinWidth(prefWd);
            timeSlider.setMaxWidth(prefWd);
            return timeSlider;
      }

      private void resetBtnAction(Button playBtn) {
            //MediaPlayer.Status status = player.getStatus();
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.ZERO);
            playBtn = VID_PLAY.getPlay(playBtn);
            currentTime = new Duration(0);
            mediaPlayer.setStartTime(currentTime);
            notPause = false;
            //return player;
      }


      /**
       * Creates a slider that controls the speed of the Media player
       *
       * @return
       */
      private Slider getSpeedSlider() {
            Slider speedSlider = new Slider();
            speedSlider.setId("slider");
            HBox.setHgrow(speedSlider, Priority.ALWAYS);
            //speedSlider.setShowTickMarks(true);
            speedSlider.setTooltip(new Tooltip("Media Speed"));
            speedSlider.setMinWidth(200);
            speedSlider.setMaxWidth(200);
            speedSlider.setValue(50);
            speedSlider.valueProperty().addListener((Observable ov) -> {
                  if (speedSlider.isValueChanging() || speedSlider.isPressed()) {
                        double rate = speedSlider.getValue() / 10;
                        if (rate < 1)
                              rate = 0.5;
                        else if (rate < 2)
                              rate = 0.6;
                        else if (rate < 3)
                              rate = 0.7;
                        else if (rate < 4)
                              rate = 0.8;
                        else if (rate < 5)
                              rate = 0.9;
                        else if (rate < 6)
                              rate = 1;
                        else if (rate < 7)
                              rate = 1.1;
                        else if (rate < 8)
                              rate = 1.2;
                        else if (rate < 9)
                              rate = 1.3;
                        else
                              rate = 1.4;
                        mediaPlayer.setRate(rate);
                  }
            });
            return speedSlider;
      }

      /**
       * Updates the timeSlider time label
       *
       * @param timeSlider
       */
      private void updateValues(Slider timeSlider) {
            if (timeSlider != null) {
                  strTime = formatTime(currentTime, totalDuration);
                  if (!timeSlider.isDisabled() && currentTime.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                        double total = totalDuration.toMillis();
                        timeSlider.setValue((currentTime.toMillis() / total));
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

            if (elapsedHours > 0) {
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

      /**
       * Action taken when the window/stage is closed
       * Stops audio or video, and clears memory.
       */
      public void onClose() {
            mediaPlayer.dispose();
            //singleMViewer = null;
      }

      private class Size {
            private final int wd;
            private final int ht;

            Size(int wd, int ht) {
                  this.wd = wd;
                  this.ht = ht;
            }

            Size getSize() {
                  return new Size(this.wd, this.ht);
            }

            int getWd() {
                  return wd;
            }

            int getHt() {
                  return ht;
            }
      }
}
