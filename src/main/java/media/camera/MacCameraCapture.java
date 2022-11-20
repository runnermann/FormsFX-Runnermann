package media.camera;

import ch.qos.logback.classic.Level;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import flashmonkey.CreateFlash;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.celleditors.DrawTools;
import type.celleditors.SectionEditor;
import type.celleditors.SnapShot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MacCameraCapture extends Application {

      private static MacCameraCapture CLASS_INSTANCE;
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MacCameraCapture.class);
      private static final Logger LOGGER = LoggerFactory.getLogger(CameraCapture.class);

      private SectionEditor parentEditor;
      private static Stage cameraStage;

      private FlowPane bottomCameraControlPane;
      private FlowPane topPane;
      private BorderPane root;
      private final String cameraListPromptText = "Choose Camera";
      private ImageView imgWebCamCapturedImage;
      private Webcam sarxoswebCam = null;
      private static boolean stopCamera = false;
      //private BufferedImage grabbedImage;
      private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
      private BorderPane webCamPane;
      private Button btnCameraStop;
      private Button btnCameraStart;
      private Button btnCameraDispose;

      private MacCameraCapture.WebCamControl camControl;

      /* *************** CONSTRUCTOR ****************/

      private MacCameraCapture() {/* no args constructor */}

      public static synchronized MacCameraCapture getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new MacCameraCapture();
            }
            return CLASS_INSTANCE;
      }

      /**
       * Creates the capture overlay stage. If there is no camera
       * detected returns true = failed;
       *
       * @param editor ..
       * @return true if successful
       * @throws Exception ..
       */
      public boolean cameraCaptureBuilder(SectionEditor editor) throws Exception {
            LOGGER.info("cameraCaptureBuilder called");

            //Webcam webcam = Webcam.getDefault(300, TimeUnit.MILLISECONDS);
            List<Webcam> webCams = Webcam.getWebcams(300);
            if (webCams.isEmpty()) {
                  //webcam.close();
                  LOGGER.warn("No camera's found");
                  //throw new TimeoutException("No webcams found");
                  return true;
            } //else if (!webcam.isOpen() && !webcam.open()) {
            //  webcam.close();
            //  throw new IllegalStateException("Unable to open webcam");
            //}

            this.parentEditor = editor;
            //LOGGER.info("line 97");
            cameraStage = new Stage();
            start(cameraStage);
            //cameraStage.show();
            cameraStage.setOnCloseRequest(e -> stop());
            return false;
      }

      @Override
      public void stop() {
            System.out.println("CameraCapture stop called");
            if (sarxoswebCam != null && sarxoswebCam.isOpen()) {
                  sarxoswebCam.close();
                  sarxoswebCam = null;
            }
            cameraStage.close();
            SnapShot.getInstance().onClose();

            CreateFlash.getInstance().enableButtons();
            btnCameraStart.setDisable(false);
            btnCameraStop.setDisable(true);
            camControl.stopWebCamCamera();
            stopCamera = true;
            DrawTools.getInstance().abandAction(this.parentEditor);
      }


      @Override
      public void start(Stage primaryStage) {

            primaryStage.setTitle("Connecting Camera Device Using Webcam Capture API");

            root = new BorderPane();
            topPane = new FlowPane();
            root.setTop(topPane);
            webCamPane = new BorderPane();
            webCamPane.setStyle("-fx-background-color: #ccc;");
            imgWebCamCapturedImage = new ImageView();
            webCamPane.setCenter(imgWebCamCapturedImage);
            root.setCenter(webCamPane);

            //createTopPane();

            bottomCameraControlPane = new FlowPane();
            bottomCameraControlPane.setOrientation(Orientation.HORIZONTAL);
            bottomCameraControlPane.setAlignment(Pos.CENTER);
            bottomCameraControlPane.setHgap(20);
            bottomCameraControlPane.setVgap(10);
            bottomCameraControlPane.setPrefHeight(40);
            bottomCameraControlPane.setDisable(true);
            camControl = new MacCameraCapture.WebCamControl();
            camControl.createCameraControls();
            root.setBottom(bottomCameraControlPane);

            primaryStage.setScene(new Scene(root));
            primaryStage.setHeight(820);
            primaryStage.setWidth(1280);
            primaryStage.centerOnScreen();
            primaryStage.show();

            Platform.runLater(new Runnable() {
                  @Override
                  public void run() {
                        setImageViewSize();
                  }
            });

            if (Webcam.getWebcams().size() == 1) {

                  Platform.runLater(() -> {
                        //cameraSelectCBox.setValue(cameraList.get(0));
                        MacCameraCapture.WebCamControl wc = new MacCameraCapture.WebCamControl();
                        wc.initializeWebCam(0);
                  });
            } else {

                  LOGGER.info("line 178, more than one camera");

                  primaryStage.setHeight(860);
                  topPane.setAlignment(Pos.CENTER);
                  topPane.setHgap(20);
                  topPane.setOrientation(Orientation.HORIZONTAL);
                  topPane.setPrefHeight(40);
                  createTopPane();
            }


            LOGGER.info("start finished ;)");
      }

      protected void setImageViewSize() {

            LOGGER.info("settingImageViewSize called.");

            //@formatter:off
            Dimension[] nonStandardResolutions = new Dimension[]{
                WebcamResolution.PAL.getSize(),
                WebcamResolution.HD.getSize(),
                new Dimension(2000, 1250),
                new Dimension(1000, 500),
            };


            //@formatter:on

            // your camera must support HD720p to run this code
            Webcam webcam = Webcam.getDefault();
            webcam.setCustomViewSizes(nonStandardResolutions);
            webcam.setViewSize(WebcamResolution.HD.getSize());

            LOGGER.info("line 210, before Platform.runlater");

            double width = WebcamResolution.HD.getWidth();
            double height = WebcamResolution.HD.getHeight();
            webCamPane.setPrefHeight(height + 450);
            webCamPane.setPrefWidth(width + 50);

            imgWebCamCapturedImage.setFitHeight(height);
            imgWebCamCapturedImage.setFitWidth(width);
            imgWebCamCapturedImage.prefHeight(height);
            imgWebCamCapturedImage.prefWidth(width);
            imgWebCamCapturedImage.setPreserveRatio(true);

            LOGGER.debug("line 225, setImageViewSize() completed width: {}, height {}", width, height);
      }

      private void createTopPane() {

            int webCamCounter = 0;
            javafx.scene.control.Label lbInfoLabel = new Label("Select Your WebCam Camera");
            ObservableList<MacCameraCapture.WebCamInfo> cameraList = FXCollections.observableArrayList();

            topPane.getChildren().add(lbInfoLabel);

            for (Webcam webcam : Webcam.getWebcams()) {
                  MacCameraCapture.WebCamInfo webCamInfo = new MacCameraCapture.WebCamInfo();
                  webCamInfo.setWebCamIndex(webCamCounter);
                  webCamInfo.setWebCamName(webcam.getName());
                  cameraList.add(webCamInfo);
                  webCamCounter++;
            }

            ComboBox<MacCameraCapture.WebCamInfo> cameraSelectCBox = new ComboBox<>();
            cameraSelectCBox.setItems(cameraList);
            cameraSelectCBox.setPromptText(cameraListPromptText);

            System.out.println("Camera List size: " + cameraList.size());

            cameraSelectCBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MacCameraCapture.WebCamInfo>() {

                  @Override
                  public void changed(ObservableValue<? extends MacCameraCapture.WebCamInfo> arg0, MacCameraCapture.WebCamInfo arg1, MacCameraCapture.WebCamInfo arg2) {

                        if (arg2 != null) {
                              MacCameraCapture.WebCamControl wc = new MacCameraCapture.WebCamControl();
                              System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
                              wc.initializeWebCam(arg2.getWebCamIndex());
                        }
                  }
            });
            topPane.getChildren().add(cameraSelectCBox);
      }

      private class WebCamControl extends Thread {

            protected void initializeWebCam(final int webCamIndex) {

                  LOGGER.info("inner class CameraCapture.WebCamControl initializeWebCam called");

                  Task<Void> webCamTask = new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {

                              if (sarxoswebCam != null) {
                                    disposeWebCamCamera();
                              }

                              sarxoswebCam = Webcam.getWebcams().get(webCamIndex);
                              sarxoswebCam.open();

                              startWebCamStream();

                              return null;
                        }
                  };

                  Thread webCamThread = new Thread(webCamTask);
                  webCamThread.setDaemon(true);
                  webCamThread.start();

                  bottomCameraControlPane.setDisable(false);
                  btnCameraStart.setDisable(true);
            }

            protected void startWebCamStream() {

                  stopCamera = false;

                  Task<Void> task = new Task<>() {

                        @Override
                        protected Void call() throws Exception {

                              LOGGER.info("inner class CameraCapture.WebCamControl startWebCamStream called");

                              final AtomicReference<WritableImage> ref = new AtomicReference<>();
                              BufferedImage img = null;

                              while (!stopCamera) {
                                    System.out.println("running");
                                    try {
                                          if ((img = sarxoswebCam.getImage()) != null) {

                                                ref.set(SwingFXUtils.toFXImage(img, ref.get()));
                                                img.flush();

                                                Platform.runLater(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                            imageProperty.set(ref.get());
                                                      }
                                                });
                                          }
                                    } catch (Exception e) {
                                          e.printStackTrace();
                                    }
                              }

                              return null;
                        }
                  };

                  Thread th = new Thread(task);
                  th.setDaemon(true);
                  th.start();
                  imgWebCamCapturedImage.imageProperty().bind(imageProperty);
            }

            private void createCameraControls() {

                  LOGGER.info("inner class CameraCapture.WebCamControl createCameraControls called");

                  btnCameraStop = new Button();
                  btnCameraStop.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent arg0) {
                              parentEditor.snapShotBtnAction();
                              stopWebCamCamera();
                        }
                  });

                  btnCameraStop.setText("SnapShot");
                  btnCameraStart = new Button();
                  btnCameraStart.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent arg0) {
                              parentEditor.stopSnapShotAction();
                              startWebCamCamera();
                        }
                  });

                  btnCameraStart.setText("Camera on");
                  btnCameraDispose = new Button();
                  //btnCameraDispose.setText("Camera off");
                  btnCameraDispose.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent notused) {
                              disposeWebCamCamera();
                        }
                  });

                  bottomCameraControlPane.getChildren().add(btnCameraStart);
                  bottomCameraControlPane.getChildren().add(btnCameraStop);
                  bottomCameraControlPane.getChildren().add(btnCameraDispose);
            }

            protected void disposeWebCamCamera() {

                  System.out.println("Dispose Camera called");

                  stopCamera = true;
                  if (sarxoswebCam != null) {
                        sarxoswebCam.close();
                  }
                  btnCameraStart.setDisable(false);
                  btnCameraStop.setDisable(true);
            }

            protected void startWebCamCamera() {

                  LOGGER.info("inner class CameraCapture.WebCamControl startWebCamCamera called");

                  stopCamera = false;
                  startWebCamStream();
                  btnCameraStop.setDisable(false);
                  btnCameraStart.setDisable(true);
            }

            protected void stopWebCamCamera() {

                  LOGGER.info("inner class CameraCapture.WebCamControl stopWebCamCamera called");

                  // close shapes pane

                  stopCamera = true;
                  btnCameraStart.setDisable(false);
                  btnCameraStop.setDisable(true);
                  //parentEditor.snapShotBtnAction();
                  disposeWebCamCamera();
            }
       /*
        private void captureFrame() {
            try {
                String name = String.format("test-%d.jpg", System.currentTimeMillis());
                ImageIO.write(webCam.getImage(), "JPG", new File(name));
                System.out.format("File %s has been saved\n", name);
            } catch (IOException t) {
                t.printStackTrace();
            }
        }
        */
      }

      /**
       * INNER CLASS WebCamInfo
       */
      private class WebCamInfo {

            private String webCamName;
            private int webCamIndex;

            public String getWebCamName() {
                  return webCamName;
            }

            public void setWebCamName(String webCamName) {
                  this.webCamName = webCamName;
            }

            public int getWebCamIndex() {
                  return webCamIndex;
            }

            public void setWebCamIndex(int webCamIndex) {
                  this.webCamIndex = webCamIndex;
            }

            @Override
            public String toString() {
                  return webCamName;
            }
      }
}
