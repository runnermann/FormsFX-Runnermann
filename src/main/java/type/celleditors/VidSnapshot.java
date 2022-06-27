package type.celleditors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RecursiveAction;

import ch.qos.logback.classic.Level;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;


/**
 * This class is to support naming videos by creating a hash from several frames of the video. However:
 * <p>
 * Current implementation uses the MediaPlayer to retrieve snapshots of the image produced by the video. MediaPlayer,
 * is asynchronous. SnapShots cannot occur until MediaPlayer has entered READY then PLAY, then PAUSE to take the snap
 * shot. The experianced time to take a snapshot is approx 1 second. This delay takes too long. MediaPlayer is
 * not time nor memory efficient for naming a video by its appearances.
 * <p>
 * To provide a unique name for a video and assure that the user does not upload multiple videos using some of the same
 * frames - thus causing a collision, we provide a unique name for each video. Hashing video frames for naming videos
 * by their appearance is delayed until later.
 */
public class VidSnapshot {

      private static final Logger LOGGER = LoggerFactory.getLogger(VidSnapshot.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(VidSnapshot.class);

      private final ObjectProperty<File> fromFileProperty = new SimpleObjectProperty<>();

      private MediaPlayer player;
      private BufferedImage hashingImage;
      // Used by GenerateImage in
      // an irregular looping
      // mechanism.
      private int count = 0;


      public VidSnapshot(File file) {

            fromFileProperty.set(file);
            this.init();
      }


      private final int[] nAry = {5, 33, 66, 95};

      private void init() {
            //LOGGER.setLevel(Level.DEBUG);
            player = null;
            final Media media = new Media(fromFileProperty.get().toURI().toString());
            System.gc();
            generateImage();
      }

      /**
       * @return Returns an image from the video set in the constructor.
       */
      public BufferedImage getHashingImage() {
            if (hashingImage == null) {
                  String message = "The hashingImage is null. The hash must be set by processing the " +
                      "video before this method can provide an image;";
                  throw new IllegalStateException(message);
            }
            return hashingImage;
      }

      private File getImageFile(int percent) {
            String imageFilename = fromFileProperty.get().toString().replaceAll("\\.mp4$", percent + ".png");
            return new File(imageFilename);
      }


      /**
       * Generate 4 image thumbnails.
       * Loops through from onReady to onPaused.
       * Onpaused loops through using count.
       * The Java Media Player is a PIA. Note that
       * MediaPlayer is instantiated outside of this
       * method. Do Not CHANGE!
       */
      private void generateImage() {

            int w = 100;
            int h = 100;

            final Media media = new Media(fromFileProperty.get().toURI().toString());
            player = new MediaPlayer(media);
            final MediaView mView = new MediaView(player);

            mView.setFitWidth(w);
            mView.setFitHeight(w);
            mView.setPreserveRatio(true);

            player.setOnPaused(new Runnable() {
                  @Override
                  public void run() {

                        File toFile = getImageFile(nAry[count]);
                        WritableImage writebleImage = new WritableImage(w, w);

                        SnapshotParameters params = new SnapshotParameters();
                        params.setFill(Color.BLACK);

                        mView.snapshot(params, writebleImage);
                        BufferedImage bImage = SwingFXUtils.fromFXImage(writebleImage, null);

                        try {
                              ImageIO.write(bImage, "png", toFile);
                        } catch (IOException e) {
                              LOGGER.warn("WARNING: Image cannot be written: {}", e.getMessage());
                              e.printStackTrace();

                        } finally {
                              ++count;
                              if (count < 4) {
                                    int percent = nAry[count];
                                    player.seek(Duration.millis(player.getTotalDuration().toMillis() * percent / 100));
                                    player.play();
                              } else {
                                    player.stop();
                                    player.setOnReady(null);
                                    player.setOnPlaying(null);
                                    player.setOnPaused(null);
                                    mView.setMediaPlayer(null);
                                    player = null;
                                    count = 0;
                                    createCollage();
                              }
                        }
                  }
            });

            player.setOnPlaying(new Runnable() {
                  @Override
                  public void run() {
                        System.out.println("Player playing. now setting to pause");
                        player.pause();
                  }
            });

            player.setOnReady(new Runnable() {
                  @Override
                  public void run() {
                        // we start at 5 percent of the video.
                        player.seek(Duration.millis(player.getTotalDuration().toMillis() * 5 / 100));
                        player.play();
                  }
            });

      }

      /**
       * Combine the 4 images into a single pane
       * and take a snapshot.
       */
      public void createCollage() {
            File f;

            ImageView[] ivAry = new ImageView[4];
            for (int i = 0; i < 4; i++) {
                  f = getImageFile(nAry[i]);
                  Image img = new Image(f.toURI().toString());
                  if (img == null) {
                        throw new IllegalStateException("Images are null. ");
                  }
                  ivAry[i] = new ImageView(img);
            }

            HBox pane = new HBox();
            for (int i = 0; i < 4; i++) {
                  if (ivAry[i] != null) {
                        pane.getChildren().add(ivAry[i]);
                  }
            }

            int width = 400;//(int) pane.getBoundsInLocal().getWidth();
            int height = 100;//(int) pane.getBoundsInLocal().getHeight();
            WritableImage writebleImage = new WritableImage(width, height);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.BLACK);


            WritableImage writImage = pane.snapshot(params, writebleImage);
            BufferedImage bImage = SwingFXUtils.fromFXImage(writImage, null);

            this.hashingImage = bImage;

            ImageView iv = new ImageView(writImage);
            HBox newBox = new HBox(iv);

            Scene s = new Scene(newBox);
            Stage stage = new Stage();
            stage.setScene(s);
            stage.show();

      }
}
