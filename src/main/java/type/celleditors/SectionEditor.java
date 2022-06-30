/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.xyz) All rights reserved.
 *
 * License: This is for internal use only by those who are current employees of FlashMonkey Inc, or have an official
 *  authorized relationship with FlashMonkey Inc..
 *
 * DISCLAIMER OF WARRANTY.
 *
 * COVERED CODE IS PROVIDED UNDER THIS LICENSE ON AN "AS IS" BASIS, WITHOUT WARRANTY OF ANY
 *  KIND, EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT THE COVERED
 *  CODE IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE
 *  ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED CODE IS WITH YOU. SHOULD ANY
 *  COVERED CODE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE INITIAL DEVELOPER OR ANY OTHER
 *  CONTRIBUTOR) ASSUME THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 *  DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.  NO USE OF ANY COVERED
 *  CODE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 *
 */

package type.celleditors;

import authcrypt.UserData;
import ch.qos.logback.classic.Level;
import fileops.*;
import fileops.utility.FileExtension;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.media.MediaPlayer;
import org.slf4j.Logger;
import type.celltypes.MediaPlayerInterface;
import type.draw.DrawObj;
import type.draw.shapes.FMRectangle;
//import FMTriangle;
import type.draw.shapes.GenericShape;
import flashmonkey.*;
//import jave2.videoconverter.*;
import javafx.scene.layout.*;
//import org.kordamp.ikonli.entypo.Entypo;
import org.kordamp.ikonli.fontawesome5.*;
import type.celltypes.AVCell;
import type.celltypes.TextCell;
import type.tools.imagery.Fit;
import fmannotations.FMAnnotations;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.LoggerFactory;
import uicontrols.*;
import video.api.JaveInterface;
import video.camera.CameraCapture;
import video.camera.WindowsCameraCapture;
import ws.schild.jave.*;
import ws.schild.jave.info.MultimediaInfo;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * <p>SectionEditor is the default editor shown in CreateCard and EditCard ?. It contains the buttons to create/ edit an image,
 * add or delete video or audio files, add drawings to images, and delete. It contains the ability to take a screen snap
 * shot or drag and drop files into the left pane.</p>
 *
 * <p>!!! *** NOTE *** !!!<br>
 * Image: Images are either created in snapshot and set using setImage(). setImage() creates an image from an BufferedImage
 * that is created by SnapShot. If cards are being edited, the image is set in setCard() method. </p>
 * <p>
 * Algorithm,
 * - Each section of a card (not cells) is edited by the section editor. Each cell type contains its own editing methods
 * - EncryptedUser creates a card. The default card is a doubleVertical card containing two sections.
 * - The default section view is a singleSection containing a tCell.textVBox (TextCell)
 * <p>
 * !!!*** NOTE ***!!!
 * fields in this class should not be static. There may be two or more instances of this class
 * in an edited card.
 * <p>
 * !!!***  NOTE ***!!!
 * Methods that contain the SnapShot() constructor call do
 * not return to that method. Calls after the SnapShot constructor call may not
 * behave correctly or may not execute until later. The Runnable statement may be needed
 * to execute them.
 * <p>
 * - (SnapShot) If the EncryptedUser clicks on snapShot,
 * - SnapShot creates a full screen stage and contains the methods and variables to allow the EncryptedUser to draw a dashed
 * rectangle anywhere on the screen. When the EncryptedUser releases the mouse button, an image of the area within the
 * rectangle is saved to the buffer. The size and location of the snapshot rectangle is saved and passed to DrawTools.
 * - The image is saved to file, and the fileName is saved to the arrayOfFiles which is stored in each FlashCard
 * object.
 * - An image is shown in the rightPane, The rightpane is contained in the right StackPane.
 * - A delete button is added to the right StackPane.
 * - A delete button is added to the left StackPane. Left StackPane contains the textVBox.
 * - DrawTools recreates the rectangle the EncryptedUser created to take the snapshot. The rectangle is solid blue and is the
 * same shape/size and in the same location.
 * - The DrawTools provides access to shape classes, methods and variables to create, edit, and delete shapes.
 * - Shapes are added to a shapeArray that is saved to each FlashCard object so they are editable later.
 * - See DrawTools for further details when needed.
 * - When the EncryptedUser closes drawtools, the blue rectangle is removed from the screen.
 * - (Drag and Drop) If the EncryptedUser drags an image, audio, or video file onto the TextArea
 * - If the file is of the right type, not all files are supported. The file is saved to to its location on the users
 * computer. The shape is saved to the arrayOfBuilderShapes according to its location on the screen. IE upper area,
 * lower area.
 * - If an image is added, it has all the features of an image meaning it has a pop-up view capability which has
 * the abilty to add and edit shapes.
 * - If an audio or video file is added, then the multi-media player is shown to the viewer and the EncryptedUser can
 * play the file (but not edit).
 *
 * @author Lowell Stadelman
 */
public class SectionEditor {
      // THE LOGGER
      private static final Logger LOGGER = LoggerFactory.getLogger(SectionEditor.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SectionEditor.class);

      // made changes to this class and it created a problem with serialization.
      // added serial versionUID.
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;
      // The max duration in minutes for a video length
      // Video's should be segmented into sections to fit
      // the concise information paradigm. Also ensures a
      // smaller file size to prevent upload download blocking
      // by internet providers.
      private static final int MAX_DURATION = 7;


      // The array of FM shapes as opposed to the arrayOfBuilderShapes which are JavaShapes. FM
      // shapes have added variables and methods
      // do not change
      private ArrayList<GenericShape> arrayOfFMShapes = new ArrayList<>(5);

      // **** Variables ****
      public HBox sectionHBox;
      private VBox txtVBox;
      private Button clearBtn;
      private Button snapShotBtn;
      private Button drawpadBtn;
      private Button deleteTCellBtn;
      private Button deleteMMCellBtn;
      private Button textAreaBtn;
      private Button cameraBtn;
      private Button findBtn;

      private final ButtoniKonClazz FIND = new ButtoniKonClazz("FIND", "Search for images, videos, animations, and tools", FontAwesomeSolid.SEARCH, UIColors.FM_WHITE);
      private final ButtoniKonClazz CAMERA = new ButtoniKonClazz("", "Take a snapshot from your camera", FontAwesomeSolid.CAMERA, UIColors.FOCUS_BLUE_OPAQUE);
      private final ButtoniKonClazz SNAPSHOT = new ButtoniKonClazz("", "Take a snapshot from your screen", "icon/24/snapshot_blue4.png", UIColors.FOCUS_BLUE_OPAQUE);
      private final ButtoniKonClazz DRAWPAD = new ButtoniKonClazz("", "Draw shapes only", FontAwesomeSolid.DRAW_POLYGON, UIColors.FOCUS_BLUE_OPAQUE);
      private final ButtoniKonClazz CLEAR_TEXT = new ButtoniKonClazz("", "Clear text", FontAwesomeSolid.BACKSPACE, UIColors.FOCUS_BLUE_OPAQUE);
      private final ButtoniKonClazz CLEAR_T_AREA = new ButtoniKonClazz("", "Remove text area", FontAwesomeSolid.MINUS_CIRCLE, UIColors.FOCUS_BLUE_OPAQUE);
      private final ButtoniKonClazz CLEAR_RIGHT = new ButtoniKonClazz("", "Remove right area", FontAwesomeSolid.MINUS_CIRCLE, UIColors.FOCUS_BLUE_OPAQUE);

      private char qOra;
      private String cID;

      // *** Flags ***
      private boolean drawPadOpen;

      private ProgressIndicator progressIndicator;

      /**
       * Double cell sections are upper-case
       * 'M' text cell on left and Media to right
       * 'C' Text cell on left and Canvas on right
       * 'D' text cell on left and DrawPad/shapes on right
       * <p>
       * Single cell sections are lower-case
       * 'm' media only
       * 't' text only
       * 'd' drawpad/shapes only
       * 'c' canvas only
       * The default is 't' for single cell text.
       */
      private char sectionType = 't'; // double or single section
      private static DrawTools draw;
      private Image image;
      private ImageView iView;
      private Pane rightPane; // pane showing shape and image
      private StackPane stackL;
      private StackPane stackR;

      public TextCell tCell;
      //   private AVCell avCell;

      // This objects image or mediaFileName used
      // by getMediaFileNames[0]
      private String aviFileName;
      // This objects shape File Name used
      // by getMediaFileNames[1]
      private String shapesFileName;

      // FLAGS
      // Disable flag for Drag n Drop
      private boolean dNdIsdisabled;

      /* ------------------------------------------------------- **/


      /* ------------------------------------------------------- **/


      private void initButtons() {
            this.drawpadBtn = DRAWPAD.get();
            this.snapShotBtn = SNAPSHOT.get();
            this.clearBtn = CLEAR_TEXT.get();
            this.cameraBtn = CAMERA.get();
            //setTextCellWidthFull();
            //deleteMMcellAction();
      }



      /**
       * Constructor called for a new FlashCard
       *
       * @param prompt ..
       * @param qOrA   ..
       * @param cID    ..
       */
      public SectionEditor(String txt, String prompt, char qOrA, String cID) {

            if ( drawpadBtn == null ) {
                  initButtons();
            }
            //LOGGER.setLevel(Level.ALL);
            LOGGER.info("constructor called");

            // Set here for new cards
            // for existing cards array is set
            // later.
            this.arrayOfFMShapes = new ArrayList<>(5);
            shapesFileName = null;
            aviFileName = null;
            rightPane = new Pane();

            //if(edPopup == null) {
            //    edPopup = ShapesEditorPopup.getInstance();
            //}

            this.tCell = new TextCell();
            this.sectionHBox = new HBox();
            this.stackL = new StackPane();
            this.stackR = new StackPane();
            // create a deep copy of tCellVBox
            this.txtVBox = new VBox( tCell.buildCell ( "", prompt, true, 0 ) );


            double w = FlashMonkeyMain.getWindow().getWidth();
            this.txtVBox.setPrefWidth(w);

            // The stackpanes containing left and right items. Allows
            // delete buttons on the layer above the panes.
            stackL.getChildren().add(txtVBox);
            stackL.setAlignment(Pos.TOP_RIGHT);
            stackR.getChildren().add(this.rightPane);
            stackR.setAlignment(Pos.TOP_RIGHT);
     //       tCell.getTextArea().setEditable(true);
     //       tCell.getTextArea().setPrefHeight(Double.MAX_VALUE);

            this.cID = cID;
            this.qOra = qOrA;

            // Get the sectionEditorButtons
            HBox buttonBox = sectionEditorButtons();
            buttonBox.setId("editorButtonBox");
            txtVBox.getChildren().add(buttonBox);

            sectionHBox.setSpacing(6);
            sectionHBox.setPadding(new Insets(4, 4, 4, 4));
            sectionHBox.setStyle("-fx-background-color: white");
            sectionHBox.setAlignment(Pos.BOTTOM_LEFT);

            // Drag and Drop capability
            DragAndDrop dragAndDrop = new DragAndDrop();
            dragAndDrop.dndOperations();

            // Set the initial textCell size and container size
            double no = SceneCntl.calcCenterHt(40, 150, FlashMonkeyMain.getWindow().getHeight());
            sectionHBox.setPrefHeight(no);
            txtVBox.setPrefHeight(no);
            tCell.getTextArea().setPrefHeight(no - 150);
            tCell.getTextCellVbox().setPrefHeight(no - 125);

            // Provide a responsive UI.
            // Responsive height
            FlashMonkeyMain.getWindow().heightProperty().addListener((obs, oldVal, newVal) -> {
                  double n = SceneCntl.calcCenterHt(40, 150, (double) newVal);
                  sectionHBox.setPrefHeight(n);
                  txtVBox.setPrefHeight(300);
            });
            // Responsive width
            CreateFlash.getInstance().getCFPCenter().widthProperty().addListener( ( obs, oldval, newVal ) -> txtVBox.setPrefWidth(newVal.doubleValue() ) );

            // add stackL (stackLeft) to sectionHBox
            this.sectionHBox.getChildren().addAll(this.stackL);
      } // END CONSTRUCTOR

      /* ------------------------------------------------------- **/


      /**
       * Contains the buttons for this class
       *
       * @return HBox containing the buttons
       * for this class along with actions.
       */
      private HBox sectionEditorButtons() {
            // Add the Find button
            this.findBtn = FIND.get();
            this.findBtn.setId("navButtonWhtLetters");
            this.findBtn.setFocusTraversable(false);
            this.findBtn.setOnAction(e -> {
                  /* stub for later */
            });
            this.findBtn.setDisable(true);
            // Clear text in text area button
      //      this.clearBtn = CLEAR_TEXT.get();
            this.clearBtn.setId("navButtonLight");
            this.clearBtn.setFocusTraversable(false);
            this.clearBtn.setOnAction((ActionEvent e) -> {
                  tCell.getTextArea().setText("");
                  tCell.getTextArea().requestFocus();
            });

            // SnapShot button
     //       this.snapShotBtn = SNAPSHOT.get();
            this.snapShotBtn.setId("navButtonLight");
            this.snapShotBtn.setFocusTraversable(false);
            // SnapShot action
            this.snapShotBtn.setOnAction((ActionEvent e) ->
            {
                  LOGGER.debug("\n\t snapshot button called in SectionEditor");
                  this.snapShotBtnAction();
            });

            // Drawpad button
     //       this.drawpadBtn = DRAWPAD.get();
            this.drawpadBtn.setId("navButtonLight");
            this.drawpadBtn.setFocusTraversable(false);
            //this.drawpadBtn.setTooltip(new Tooltip("Create a drawing"));
            // drawpad action
            this.drawpadBtn.setOnAction((ActionEvent e) -> {
                  LOGGER.info("\n\t drawpad button called in SectionEditor");
                  this.drawpadBtnAction(sectionHBox);
            });

            // Camera Button
            this.cameraBtn.setId("navButtonLight");
            this.cameraBtn.setFocusTraversable(false);
            //this.cameraBtn.setTooltip(new Tooltip("Local Camera"));
            this.cameraBtn.setOnAction((ActionEvent) -> {
                  this.cameraBtnAction();
            });

            // Text area button
            this.textAreaBtn = new Button("add text");
            this.textAreaBtn.setFocusTraversable(false);
            this.textAreaBtn.setTooltip(new Tooltip("Add a text area to the\n card"));
            // Action when textAreaBtn is pressed.
            this.textAreaBtn.setOnAction((ActionEvent e) -> {
                  addTCellAction();
            });

            // Delete text cell & multi media cell buttons
            this.deleteTCellBtn = new Button("x");
            this.deleteMMCellBtn = new Button("x");
            deleteTCellBtn.setFocusTraversable(false);
            deleteMMCellBtn.setFocusTraversable(false);
            deleteTCellBtn.setTooltip(new Tooltip("Remove TextArea"));
            deleteMMCellBtn.setTooltip(new Tooltip("Remove Multi-Media"));
            deleteTCellBtn.setId("clrBtn");
            deleteMMCellBtn.setId("clrBtn");

            // Clear stackL from sectionHBox & remove
            // deleteMMCellBtn from right stackR
            this.deleteTCellBtn.setOnAction((ActionEvent e) -> {
                  deleteTCellAction();
                  LOGGER.debug("textAreaBtn width {}", this.textAreaBtn.getBoundsInLocal().getWidth());
            });

            // Clear stackR from sectionHBox & remove
            this.deleteMMCellBtn.setOnAction((ActionEvent e) -> {
                  CreateFlash.getInstance().setFlashListChanged(true);
                  deleteMMcellAction();
            });

            HBox buttonBox = new HBox();
            buttonBox.getChildren().addAll(this.findBtn, this.clearBtn, this.cameraBtn, this.snapShotBtn, this.drawpadBtn);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            return buttonBox;
      } // End Button settings


      /* ------------------------------------------------------- **/


      /**
       * Deletes the TextCell and replaces it with a single section
       * containing a Media or Image cell. Used by deleteTCellBtn.
       */
      private void deleteTCellAction() {
            LOGGER.info(" DeleteTCellButton pressed ");
            LOGGER.info("masterBox width setting to: " + this.sectionHBox.getWidth());

            CreateFlash.getInstance().setFlashListChanged(true);

            if (this.sectionType == 'D') {
                  LOGGER.info("\tsetting type to 'd'");
                  this.sectionType = 'd';
            } else if (this.sectionType == 'M') {
                  LOGGER.info("\tsetting type to'm'");
                  this.sectionType = 'm';
            } else if (this.sectionType == 'C') { // this is a drawing only
                  LOGGER.info("\t setting type to 'c'");
                  this.sectionType = 'c';
            } else {
                  LOGGER.info("this section type is: " + this.sectionType);
            }

            this.sectionHBox.getChildren().clear();

            stackR.getChildren().clear();
            // if this section is not a drawing type
            if (this.sectionType != 'D' && this.sectionType != 'd') {
                  // for images
                  if (this.iView == null) {
                        this.iView = new ImageView(image);
                  }
                  LOGGER.debug("deleteTCellAction, setting iView");

                  int width = (int) this.sectionHBox.getWidth() - 90;
                  int height = (int) this.sectionHBox.getHeight() - 20;
                  // Scale image to the pane
                  this.iView = Fit.viewResize(iView.getImage(), width, height);

                  rightPane.getChildren().clear();
                  rightPane.getChildren().add(iView);

                  if (iView != null) {
                        // responsive width
                        sectionHBox.widthProperty().addListener((obs, oldval, newVal) -> iView.setFitWidth(newVal.doubleValue() - 90));
                        // repsonsive height
                        sectionHBox.heightProperty().addListener((obs, oldval, newVal) -> iView.setFitHeight(newVal.doubleValue() - 20));
                  }
            } else {
                  // for media
                  LOGGER.info("section is 'd' or 'D'");
                  this.rightPane.setMaxWidth(this.sectionHBox.getWidth() - 90);
            }

            stackR.getChildren().add(this.rightPane);
            this.sectionHBox.getChildren().addAll(textAreaBtn, stackR);
      }

      /* ------------------------------------------------------- **/


      /**
       * Removes the TCell from the left pane. Called by CreateFlash
       * when a card is edited as opposed to created.
       */
      public void removeTCell() {
            this.sectionHBox.getChildren().clear();
            stackR.getChildren().clear();
            stackR.getChildren().add(this.rightPane);
            this.sectionHBox.getChildren().addAll(textAreaBtn, stackR);
      }

      /* ------------------------------------------------------- **/

      /**
       * Adds a text cell to the left pane.
       */
      private void addTCellAction() {
            LOGGER.info("\ntextAreaBtn pressed");

            if (this.sectionType == 'd') {
                  //LOGGER.info("\tsetting type to 'D'");
                  this.sectionType = 'D';
            } else if (this.sectionType == 'm') {
                  //LOGGER.info("\tsetting type to'M'");
                  this.sectionType = 'M';
            } else if (this.sectionType == 'c') { // this is a drawing only
                  //LOGGER.info("\t setting type to 'C'");
                  this.sectionType = 'C';
            } else {
                  LOGGER.info("this section type is: " + this.sectionType);
            }

            this.rightPane.setMaxWidth(100);
            this.sectionHBox.getChildren().clear();
            this.sectionHBox.getChildren().addAll(stackL, stackR);
            this.stackR.getChildren().add(deleteMMCellBtn);
      }


      /* ------------------------------------------------------- **/


      /**
       * Clear stackR from sectionHBox {@code &} remove
       * deleteTCellbtn from stackL.
       */
      private void deleteMMcellAction() {
            this.sectionType = 't';
            if (arrayOfFMShapes != null) {
                  arrayOfFMShapes.clear();
            }
            // set textArea width to full Width
            setTextCellWidthFull();
            rightPane.getChildren().clear();
            sectionHBox.getChildren().clear();
            stackL.getChildren().clear();
            stackL.getChildren().add(txtVBox);
            sectionHBox.getChildren().add(this.stackL);
      }


      /* ------------------------------------------------------- **/


      /**
       * Clears the text area, removes the clear button
       * and removes stackR.
       */
      public void resetSection() {
            image = null;
            iView = null;
            aviFileName = null;

            rightPane.getChildren().clear();
            stackR.getChildren().clear();
            shapesFileName = null;
            tCell.getTextArea().setText("");
            setTextCellWidthFull();
            arrayOfFMShapes = new ArrayList<>();

            deleteMMcellAction();
      }


      /****************************************************************************
       GETTERS
       *****************************************************************************/

/*      public boolean isDrawOpen() {
            return drawPadOpen;
      }*/


 /*     public HBox getSectionHBox() {
            return this.sectionHBox;
      }*/

      /**
       * Creates an array containing the mediaFileName, and shapesFileName,
       * and returns it.
       *
       * @return String array containing the shapes
       */
      public String[] getMediaNameArray() {
            LOGGER.debug(" **** called getMediaFileNames() before check ****" + "\nmediaFileName: " + aviFileName + " | shapesFile: " + shapesFileName);
            //Thread.dumpStack();
            if (this.arrayOfFMShapes.size() > 1) {
                  final String[] str = {aviFileName, shapesFileName};
                  return str;
            } else {
                  final String[] s = {aviFileName};
                  return s;
            }
      }

      public String getMediaFileName() {
            return aviFileName;
      }

      public String getShapesFileName() {
            return shapesFileName;
      }



      /* ------------------------------------------------------- **/


      /**
       * Returns the text from the textCell textArea.
       * Convienience method.
       *
       * @return ..
       */
      public String getText() {
            return this.tCell.getTextArea().getText();
      }


      /* ------------------------------------------------------- **/


      /**
       * Returns the rightPane containing the image and shapes
       *
       * @return Returns the rightPane containing the image and shapes.
       */
      public Pane getRightPane() {
            return this.rightPane;
      }


      /* ------------------------------------------------------- **/


      /**
       * Returns the DrawTools object... Do not call this reference from outside
       * of the section editor. DrawTools should be called directly using
       * getInstance().
       * @return Returns the DrawTools object set in setDrawTools().
       */
      /*public DrawTools getDrawTools() {
            return draw;
      }*/



      /* ------------------------------------------------------- **/


      /**
       * Returns the mediaType used as well as
       * the sectionType for this section. Section being
       * either a double or singl cell section.
       *
       * @return Returns the media type.
       */
      public char getMediaType() {
            return this.sectionType;
      }


      /* ------------------------------------------------------- **/


      /**
       * Returns the iView object for this editor
       *
       * @return Returns an ImageView
       */
      public ImageView getImageView() {
            return this.iView;
      }


      /* ------------------------------------------------------- **/


      public double getScale() {
            double imgWd;
            double imgHt;

            if (iView != null) {
                  imgWd = iView.getImage().getWidth();
                  imgHt = iView.getImage().getHeight();
                  return Fit.calcScale(imgWd, imgHt, 100, 100);
            } else {
                  imgWd = draw.getOverlayWd();
                  imgHt = draw.getOverlayHt();
                  return Fit.calcScale(imgWd, imgHt, 100, 100);
            }
      }




    /* ************************************************************************************************************ **
     *                                                                                                                 *
                                                    SETTERS
     *                                                                                                                 *
     ** ************************************************************************************************************ ***/

      void setDrawPadClosed() {
            drawPadOpen = false;
      }


      protected void setMediaFileName(String mediaName) {
            if (mediaName != null && !mediaName.equals("")) {
                  aviFileName = mediaName;
            } else {
                  LOGGER.warn("Setting avi/media FileName to null or empty");
            }
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets the shapesFile string name representing
       * the shapesFile to the name in the parameter.
       *
       * @param name ..
       */
      public void setShapeFileName(String name) {
            if (name != null && !name.equals("")) {
                  shapesFileName = name;
            } else {
                  LOGGER.warn("Settting shapesFileName to null or empty");
            }
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets the character color and style to
       * a prompt appearance
       */
      public void styleToPrompt() {
            this.tCell.getTextArea().setStyle("-fx-prompt-text-fill: rgba(0,0,0,.5); ");
      }


      /* ------------------------------------------------------- **/


      /**
       * @return returns the array of FM shapes for this card.
       */
      public ArrayList<GenericShape> getArrayOfFMShapes() {
            return this.arrayOfFMShapes;
      }


      /* ------------------------------------------------------- **/

      public char getQorA() {
            return this.qOra;
      }

      /* ------------------------------------------------------- **/

      public String getCID() {
            return this.cID;
      }

      /* ------------------------------------------------------- **/


      /**
       * Sets the text in the textCells textArea.
       * Convienience method
       *
       * @param text ..
       */
      public void setText(String text) {
            this.tCell.getTextArea().setText(text);
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets the prompt text in the textCells textArea
       *
       * @param text
       */
      public void setPrompt(String text) {
            this.tCell.getTextArea().setPromptText(text);
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets this section to t
       */
      public void setSectionType(char sectionType) {
            this.sectionType = sectionType;
            if (!hasTextCell(sectionType)) {
                  deleteTCellAction();
            }
      }


      /* ------------------------------------------------------- **/


      public boolean hasTextCell(char sectionType) {
            return sectionType == 't' || sectionType == 'C' || sectionType == 'M' || sectionType == 'D';
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets an image in the rightPane for this section
       * from the BufferredImage in the parmeter.
       *
       * @param imgBuffer Expects a BufferedImage
       */
      public void setImage(BufferedImage imgBuffer) {
            image = SwingFXUtils.toFXImage(imgBuffer, null);
            setImageHelper(image);
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets the TextCell width to allow the rightPane
       * in the HBox
       */
      public void setTextCellWdForMedia() {
            double w = FlashMonkeyMain.getWindow().getWidth() - 144;
            this.txtVBox.setPrefWidth(w);
      }


      /* ------------------------------------------------------- **/


      public void setTextCellWidthFull() {
            double w = FlashMonkeyMain.getWindow().getWidth();
            this.txtVBox.setPrefWidth(w);
      }


      /* ------------------------------------------------------- **/


      /**
       * Used for editing cards that exist.
       * <p>
       * Sets the section width, sets media (Image and shapes, audio, video)
       * if available, in the rightPane for this section.
       * If there is no media sets the width to normal
       * for the text box. Also adds
       * the delete cell buttons if media is present
       *
       * @param mediaFileNames Expects the imageName
       * @param mediaType      drawing only = 'D' 'd', Media = 'm' 'M', Canvas (image and drawing or image only) = 'c' or 'C'
       * @param qOrA           ..
       * @param cID            ..
       */
      public void setSectionMedia(String[] mediaFileNames, char mediaType, char qOrA, final String cID) {
            DirectoryMgr dirMgr = new DirectoryMgr();
            LOGGER.info(" setSectionMedia() called \n");
            // Get the path to the media directory


            this.sectionType = mediaType;

            double num = SceneCntl.calcCenterHt(40, 150, FlashMonkeyMain.getWindow().getHeight());
            sectionHBox.setPrefHeight(num);
            txtVBox.setPrefHeight(num);
            tCell.getTextArea().setPrefHeight(num - 150);
            tCell.getTextCellVbox().setPrefHeight(num - 125);

            switch (mediaType) {
                  // text
                  // 'T' is never used
                  case 't': {
                        double w = FlashMonkeyMain.getWindow().getWidth() - 16;
                        this.txtVBox.setPrefWidth(w);
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) -> txtVBox.setPrefWidth(newVal.doubleValue()));
                        break;
                  }
                  // image with or without shapes
                  case 'C':
                  case 'c': {
                        LOGGER.debug("case 'c' or 'C' setImage and shapes");
                        setMediaFileName(mediaFileNames[0]);
                        //this.mediaFileName = mediaFileNames[0];
                        this.arrayOfFMShapes.clear();

                        String path = DirectoryMgr.getMediaPath('c') + mediaFileNames[0];
                        File f = new File(path);
                        if (f.exists()) {
                              image = new Image("File:" + path);
                              // set shapes in right pane with image
                              setImageHelper(image);

                              FileOpsShapes fo = new FileOpsShapes();
                              if (mediaFileNames.length == 2) {
                                    this.shapesFileName = mediaFileNames[1];
                                    this.arrayOfFMShapes = fo.getListFromFile(mediaFileNames[1]);
                                    setShapesInRtPane(this.arrayOfFMShapes, image.getWidth(), image.getHeight());
                              }

                              this.rightPane.setOnMouseClicked(e -> {
                                    rightPaneAction(mediaFileNames);
                              });
                        }

                        // For responsive text pane with the right pane.
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) -> txtVBox.setPrefWidth(newVal.doubleValue() - 124));
                        break;
                  }
                  // drawings only
                  case 'D':
                  case 'd': {
                        if (mediaFileNames == null || mediaFileNames.length < 2) {
                              LOGGER.warn("mediaFileNames[1] is null. processing as text");
                              setSectionMedia(mediaFileNames, 't', qOrA, cID);
                              return;
                        }
                        this.shapesFileName = mediaFileNames[1];
                        LOGGER.debug(" Shapes Only .. This is a DrawPad");
                        this.arrayOfFMShapes.clear();

                        String path = DirectoryMgr.getMediaPath('c');
                        String shapesPath = path + shapesFileName;
                        File f = new File(shapesPath);
                        if (f.exists()) {
                              FileOpsShapes fo = new FileOpsShapes();
                              this.arrayOfFMShapes = fo.getListFromFile(mediaFileNames[1]);
                              double ht = ((FMRectangle) arrayOfFMShapes.get(0)).getWd();
                              double wd = ((FMRectangle) arrayOfFMShapes.get(0)).getHt();
                              addDrawRPane(this);
                              setShapesInRtPane(this.arrayOfFMShapes, wd, ht);
                              this.rightPane.setOnMouseClicked(e -> {
                                    ShapesEditorPopup edPopup = ShapesEditorPopup.getInstance();
                                    edPopup.init();
                                    String deckName = FlashCardOps.getInstance().getDeckLabelName();
                                    edPopup.shapePopupHandler(this.arrayOfFMShapes, this, shapesFileName, deckName, cID, qOrA);
                              });
                        } else {
                              LOGGER.warn("ERROR: Shapes file does not exist. Path: {}", f);
                        }

                        LOGGER.debug("mediaPath: " + path + ", shapesPathName: " + shapesFileName);
                        LOGGER.debug("mediaType: " + mediaType);


                        // for responsive text pane with the right pane.
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) -> txtVBox.setPrefWidth(newVal.doubleValue() - 124));

                        break;
                  }
                  // media Video and Sound
                  case 'M':
                  case 'm': {
                        String path = DirectoryMgr.getMediaPath('c');
                        String relativeImgPath = path + mediaFileNames[0];
                        LOGGER.debug("relativeImgPath: " + relativeImgPath + ", & mediaType: " + mediaType);
                        setMediaFileName(mediaFileNames[0]);
                        //this.mediaFileName = mediaFileNames[0];
                        setVideoHelper(relativeImgPath);
                        LOGGER.debug("media rightPane has content: " + rightPane.getChildren().isEmpty());

                        break;
                  }
                  // default is do nothing
            }
      }

      /**
       * Provides the rightPane clickOn action that should popup the image
       * and shapeToolPane/popup.
       *
       * @param mediaFileNames ..
       */
      private void rightPaneAction(String[] mediaFileNames) {
            minimizeFullScreen();
            LOGGER.debug("rightPane action called");
            ShapesEditorPopup edPopup = ShapesEditorPopup.getInstance();
            edPopup.init();
            String deckName = FlashCardOps.getInstance().getDeckLabelName();
            edPopup.imagePopupHandler(this.arrayOfFMShapes, this, mediaFileNames, deckName, cID);
            CreateFlash.getInstance().setFlashListChanged(true);
      }


      /* ------------------------------------------------------- **/


      /**
       * Adds an image to the rightPane, adds the rightPane deleteButton
       * and Adds the leftPane deleteButton.
       *
       * @param image ..
       */
      private void setImageHelper(Image image) {
            LOGGER.debug("does image exist, check width: {}", image.getWidth());

            this.sectionHBox.getChildren().clear();
            this.stackR.getChildren().clear();
            // Set the size of the ImageView pane
            this.iView = Fit.viewResize(image, 100, 100);
            setTextCellWdForMedia();
            LOGGER.debug("is imageView contains an image: Check if width is a number: {}", iView.getImage().getWidth());
            // sets the image, & shapes, in the right
            setViewInRPane(iView);
            // sets the textVBox and delete button in the left stackPane
            addDeleteToLPane(this.stackL);
            // Adds the delete button to the right stackPane
            addDeleteToRPane(this.stackR);

            this.sectionHBox.getChildren().addAll(this.stackL, this.stackR);
      }



      /* ------------------------------------------------------- **/


      /**
       * Adds video or audio to the rightPane, adds the rightPane deleteButton
       * and Adds the leftPane deleteButton.
       *
       * @param relativePath ..
       */
      private void setVideoHelper(String relativePath) {
            this.sectionHBox.getChildren().clear();
            this.stackR.getChildren().clear();

            AVCell avCell = new AVCell();
            rightPane = avCell.buildCell(100, 100, relativePath);
            setTextCellWdForMedia();

            // sets the textVBox and delete button in the left stackPane
            addDeleteToLPane(this.stackL);
            // Adds the delete button to the right stackPane
            addDeleteToRPane(this.stackR);

            this.sectionHBox.getChildren().addAll(this.stackL, this.stackR);
      }



      /* ------------------------------------------------------- **/


      /**
       * If stackR is empty, adds rightPane and delete btn to stackR.
       * Clears and adds if it is not clear.
       *
       * @param rStack ..
       */
      private void addDeleteToRPane(Pane rStack) {
            if (rStack.getChildren().isEmpty()) {
                  rStack.getChildren().addAll(this.rightPane, deleteMMCellBtn);

            } else {
                  rStack.getChildren().clear();
                  rStack.getChildren().addAll(this.rightPane, deleteMMCellBtn);
            }
      }


      /* ------------------------------------------------------- **/


      /**
       * If LeftPane does not contain a deleteTCell Btn
       * Add it to the pane.
       *
       * @param lPane ..
       */
      private void addDeleteToLPane(Pane lPane) {
            if (lPane.getChildren().contains(deleteTCellBtn)) {
                  // do nothing
            } else {
                  lPane.getChildren().add(deleteTCellBtn);
            }
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets iView (photo) in rightPane. Checks and clears rightPane if not empty
       *
       * @param iView ..
       */
      private void setViewInRPane(Node iView) {
            LOGGER.info("\n *** setViewInRPane() ***");
            if (iView == null) {
                  LOGGER.warn("WARNING: \tiView is null");
            }
            if (rightPane.getChildren().isEmpty()) {
                  rightPane.getChildren().add(iView);
            } else {
                  rightPane.getChildren().clear();
                  rightPane.getChildren().add(iView);
            }
      }


      /* ------------------------------------------------------- **/

      public void clearShapes(ArrayList<GenericShape> fmShapes) {
            fmShapes.clear();

            this.rightPane.getChildren().clear();
            if (iView != null) {
                  this.rightPane.getChildren().add(iView);
            }
      }

      /* ------------------------------------------------------- **/


      /**
       * Adds Shapes to the rightPane.
       * Called from SnapShot when they
       * are created by the EncryptedUser.EncryptedUser.
       *
       * @param fmShapes ..
       * @param origHt   ..
       * @param origWd   ..
       */
      public void setShapesInRtPane(ArrayList<GenericShape> fmShapes, double origWd, double origHt) {
            LOGGER.info("setShapesInRtPane called ");
            double scale;

            this.rightPane.getChildren().clear();
            if (iView != null) {
                  this.rightPane.getChildren().add(iView);
            } else {
                  this.rightPane.setMinWidth(100);
                  this.rightPane.setMinHeight(100);
            }

            scale = Fit.calcScale(origWd, origHt, 100, 100);

            LOGGER.info(" Scale: " + scale);

            for (int i = 1; i < fmShapes.size(); i++) {
                  this.rightPane.getChildren().add(fmShapes.get(i).getScaledShape(scale));
            }
      }


      /* ------------------------------------------------------- **/


      /**
       * For drawings that are not over images. Sets the shapes in the rightPane
       * and scales them for the size of the drawPane.
       *
       * @param editor Uses the editor from the caller
       */
      public void addDrawRPane(SectionEditor editor) {
            editor.sectionHBox.getChildren().clear();
            setTextCellWdForMedia();
            // sets the textVBox and delete button in the left stackPane
            addDeleteToLPane(this.stackL);
            // Adds the delete button to the right stackPane
            addDeleteToRPane(this.stackR);
            editor.sectionHBox.getChildren().addAll(editor.stackL, editor.stackR);
      }



      /*******************************************************************************
       OTHER METHODS
       ********************************************************************************/


      /**
       * Call this when leaving or opening a new window.
       * Ensure Stage/window instances are closed and users work is saved.
       *
       * @throws NoSuchMethodError
       */
      public void onClose() throws NoSuchMethodException {
            Consumer<MediaPlayerInterface> onClose = MediaPlayerInterface::onClose;

            //MainGenericsPaneTest.onClose();
            //window.close();
            //if(window != null) { window.close() }
            //super.getTextCellVbox().getChildren().clear();
            //super.getTextCellVbox().getChildren().add(super.getTextArea());
            //super.getTextArea().setEditable(false);
      }

      private void cellOnClose(MediaPlayerInterface cell) {
            cell.onClose();
      }


      /* ------------------------------------------------------- **/


      /**
       * Alternate correct Answers that would be correct answers
       * to this question.
       *
       * @param ansSet ..
       * @return Returns an arrayList of correctAnswers
       */
      public ArrayList<Integer> getCorrect(TextField ansSet) {
            ArrayList<Integer> intList = new ArrayList<>(1);
            String temp = ansSet.getText();

            if ( ! temp.isEmpty() ) {
                  temp = temp.replaceAll("[\\p{Ps}\\p{Pe}]", "");
                  temp = temp.trim();

                  LOGGER.info("\n *** In getCorrect, temp: " + temp + " ***");

                  String[] parts = temp.split(",");
                  int intPart;
                  for (String e : parts) {
                        LOGGER.debug("\tin for loop: {}", e);

                        e = e.replaceAll("\\D", "");
                        e = e.replace("\\s+", "");

                        LOGGER.debug("\tafter clean : {}", e);

                        if (!e.equals("") && !e.equals(" ")) {
                              intPart = Integer.parseInt(e);
                              intList.add(intPart);
                        }
                  }
                  return intList;
            }
            // add this questions number
            return intList;
      }


      /* ------------------------------------------------------- **/


      /**
       * Populates the TextField for otherAnswers with
       * a string of integers to be displayed for the EncryptedUser.
       *
       * @param intAry ..
       * @return ..
       */
      public String populate(ArrayList<Integer> intAry) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < intAry.size(); i++) {
                  sb.append(intAry.get(i) + ", ");
            }

            LOGGER.debug("in populate, sb: {}", sb);

            return sb.toString();
      }



      /* ------------------------------------------------------- **/


      /**
       * Starts CameraCapture to capture an image
       * from the camera.
       */
      private void cameraBtnAction() {
            this.sectionType = 'C';
            LOGGER.info("\n ~^~^~^ *** cameraBtnAction called *** ~^~^~^");

            this.rightPane.setMinWidth(100);
            this.rightPane.setMaxWidth(100);
            this.rightPane.setMinHeight(100);

            CreateFlash cfp = CreateFlash.getInstance();
            cfp.disableButtons();
            // buttons are enabled when stop is called
            // handled in CameraCapture.stop()

            try {
                  String os = System.getProperty("os.name");
                  if (os.toLowerCase(Locale.ROOT).startsWith("win")) {
                        WindowsCameraCapture.getInstance().cameraCaptureBuilder(this);
                  } else {
                        CameraCapture.getInstance().cameraCaptureBuilder(this);
                  }
            } catch (Exception e) {
                  cfp.enableButtons();

                  String message =
                      "\n  Something went wrong. I didn't find a web-cam." +
                          "\n  If you have one, check your computer settings and try again. ";
                  FxNotify.notificationBlue("Ouch!", message, Pos.CENTER, 20,
                      "emojis/flash_headexplosion_60.png", FlashMonkeyMain.getWindow());
                  LOGGER.warn("Camera error: Possibly no camera available");
            }

            // prevent unneccessary work/ Are we editing or creating/ initailly
            // we are creating
            cfp.setFlashListChanged(true);
      }



      /* ------------------------------------------------------- **/


      //    private FileNaming fileNaming;
      private static DrawObj drawObj;

      /**
       * Starts SnapShot class. To capture a EncryptedUser.EncryptedUser
       * defined image from their screen.
       */
      public void snapShotBtnAction() {
            minimizeFullScreen();
            this.sectionType = 'C';
            LOGGER.info("\n ~^~^~^ *** SnapShot Button Action called *** ~^~^~^");

            this.rightPane.setMinWidth(100);
            this.rightPane.setMaxWidth(100);
            this.rightPane.setMinHeight(100);


            CreateFlash cfp = CreateFlash.getInstance();
            cfp.disableButtons();
            // prevent unecessary work/ Are we editing or creating/ & initially
            // we are creating
            cfp.setFlashListChanged(true);

            //@TODO remove the repitiion with file naming between image path and shapes path.
            // The naming and path for image and shapes files.
            // Shapes file ending must be changed later.
            DirectoryMgr dirMgr = new DirectoryMgr();
            String path = DirectoryMgr.getMediaPath('C');

            String name = UserData.getUserName();
            String cID = CreateFlash.getInstance().getCurrentCID();
            // Instantiate drawObj before it is used. ;)
            drawObj = new DrawObj();
            // get the snapshot instance and let the authcrypt.user take the snapshot.
            // Snapshot saves to file there.
            SnapShot.getInstance().snapStage = new Stage(StageStyle.TRANSPARENT);

            SnapShot.getInstance().snapShotBuilder(drawObj);

            AtomicReference<String> imgFileName = new AtomicReference<>(null);

            SnapShot.getInstance().snapStage.setOnHidden(e -> {
                  // Set the image in this editor, and
                  // set it in the rightPane
                  if (SnapShot.getInstance().getImgBuffer() != null) {
                        setImage(SnapShot.getInstance().getImgBuffer());
                        String imgHash = FileNaming.getImageHash(SnapShot.getInstance().getImgBuffer());
                        FileNaming fileNaming = new FileNaming(imgHash, 'i', ".png");
                        //Atomic reference for lambda
                        imgFileName.set(fileNaming.getMediaFileName());

                        // store the imageName in the mediaFileName.
                        //mediaFileName = imgFileName.get();
                        setMediaFileName(imgFileName.get());

                        // check that folder exists, if not it will
                        // be created.
                        FileOpsUtil.folderExists(new File(path));
                        FlashCardOps.getInstance().saveImage(imgFileName.get(), image, "png", 'c');
                        // SnapShot.getInstance().saveImage(path + imgFileName.get());
                        draw = DrawTools.getInstance();

                        // Clear the arrayOfFMShapes if it has
                        // shapes left in it from the last time
                        // it was used.
                        if (this.arrayOfFMShapes != null) {
                              this.arrayOfFMShapes.clear();
                        }
                        // set the drawTools up if the authcrypt.user wants to draw.
                        // DrawTools save to the local arrayOfFMShapes
                        // Also saves the shapes to file.
                        // set the drawObj fileName to be passed to
                        // DrawTools
                        //fileNaming = new FileNaming(name, imgHash, 'i', ".shp");
                        shapesFileName = FileNaming.getShapesName(imgFileName.get());
                        drawObj.setFileName(shapesFileName);
                        // and finally build the drawtools
                        draw.buildDrawTools(drawObj, this);
                        draw.popUpTools();

                        drawObj.clearDrawObj();

                        // prevent unecessary work/ Are we editing or creating/ & initially
                        // we are creating
                        cfp.setFlashListChanged(true);
                  }
                  drawObj.clearDrawObj();
            });
            String[] mediaNames = {imgFileName.get(), shapesFileName};
            //       rightPane.setOnMouseDragReleased(g ->  setEditorPopup(mediaNames, name, cID));
            this.rightPane.setOnMouseClicked(e -> {
                  rightPaneAction(getMediaNameArray());
            });

            SnapShot.getInstance().snapStage.show();
      }

      /**
       * Checks if FlashMonkeyMain is fullscreen, if true
       * sets full screen false, then sets iconified true.
       * If used, ensure to reset iconified and fullscreen.
       * in the callers onClose().
       */
      private void minimizeFullScreen() {
            FlashMonkeyMain.minimizeFullScreen();
      }

      private void setEditorPopup(String[] mediaFileNames, String deckName, String cID) {
            ShapesEditorPopup edPopup = ShapesEditorPopup.getInstance();
            edPopup.init();
            edPopup.imagePopupHandler(this.getArrayOfFMShapes(),
                this, mediaFileNames, deckName,
                cID);
      }


      /* ------------------------------------------------------- **/


      /**
       * Closes the snapShotStage
       */
      public void stopSnapShotAction() {
            SnapShot.getInstance().onClose();
      }


      /* ------------------------------------------------------- **/


      /**
       * Creates the drawPad for the EncryptedUser.EncryptedUser to draw on.
       *
       * @param parentPane ..
       */
      protected void drawpadBtnAction(Pane parentPane) {
            this.rightPane.setMinWidth(100);
            this.rightPane.setMaxWidth(100);
            this.rightPane.setMinHeight(100);

            LOGGER.info("*** drawpad action called ***");
            this.sectionType = 'D';
            this.arrayOfFMShapes.clear();
            aviFileName = null;
            this.iView = null;

            Bounds bounds = parentPane.getBoundsInLocal();
            Bounds screenBounds = parentPane.localToScreen(bounds);

            CreateFlash cfp = CreateFlash.getInstance();
            cfp.disableButtons();

            int minX = (int) screenBounds.getMinX() - 450;
            int minY = (int) screenBounds.getMinY() - 50;

            DrawObj drawObj = new DrawObj();
            drawObj.setDems(minX, minY, 400, 400);

            DirectoryMgr dirMgr = new DirectoryMgr();
            String mediaPath = DirectoryMgr.getMediaPath('C');
//        authcrypt.UserData data = new authcrypt.UserData();
            String deckName = FlashCardOps.getInstance().getDeckLabelName();
            String cID = CreateFlash.getInstance().getCurrentCID();

            setShapeFileName(FileNaming.getShapesName(deckName, cID));
            //shapesFileName = FileNaming.getShapesName(deckName, cID);

            // check that folder exists, if not it will
            // be created.
            FileOpsUtil.folderExists(new File(mediaPath));

            minimizeFullScreen();
            // Flag for detecting if this instance of SectionEditor has a drawpad open.
            drawPadOpen = true;
            draw = DrawTools.getInstance();
            draw.buildDrawTools(minX, minY, shapesFileName, this);
            draw.popUpTools();

            this.rightPane.getChildren().clear();
            this.rightPane.setOnMouseClicked(e -> {
                  rightPaneAction(getMediaNameArray());
            });
            // set drawing in rightPane of sectionEditor
            addDrawRPane(this);

            CreateFlash.getInstance().setFlashListChanged(true);
      }


      /**************************************************************************
       *                      ***** INNER CLASS ******
       *                **** Drag and Drop methods *****
       ***************************************************************************/

      private class DragAndDrop {

            private final String mediaURL;

            DragAndDrop() {
                  mediaURL = "";
            }

            /**
             * The drag and drop handler for this card and section.
             */
            private void dndOperations() {
                  txtVBox.setOnDragOver(this::dragOver);
                  txtVBox.setOnDragDropped(this::dragDropped);
            }


            /**
             * Currently, set to copy either an image, URL or File,
             * May need to accept text.
             *
             * @param e ..
             */

            private void dragOver(DragEvent e) {

                  //if(!fileAccepted || fileRejected) {
                  // allows an image, URL or a file
                  Dragboard dragboard = e.getDragboard();
                  if (dragboard.hasImage() || dragboard.hasFiles() || dragboard.hasUrl()) {
                        //LOGGER.debug("\t file is accepted ");
                        e.acceptTransferModes(TransferMode.COPY);
                  }
                  e.consume();
                  //}
            }


            /**
             * The drag and drop capability for this card and this section
             *
             * @param e ..
             */
            private void dragDropped(DragEvent e) {
                  //LOGGER.setLevel(Level.DEBUG);
                  boolean isCompleted = false;

                  if (!dNdIsdisabled) {
                        LOGGER.info("\n *** In dragDropped(dragevent e) *** ");
                        // Transfer the data to the target
                        Dragboard dragboard = e.getDragboard();

                        if (dragboard.hasImage()) {
                              LOGGER.debug("\t dragBoard has Image");
                              String str = dragboard.getUrl();
                              // handle .gif animations differently
                              String ending = str.substring(str.length() - 3);

                              isCompleted = this.transferImage(dragboard.getImage(), ending);
                              if (isCompleted) {
                                    // Sets the image in the rPane from
                                    // the image created in transferImage(...)
                                    setImageHelper(image);
                              }
                        } else if (dragboard.hasFiles()) {
                              LOGGER.debug("\t dragboard hasFiles: ");
                              iView = null;
                              try {
                                    isCompleted = transferMediaFile(dragboard.getFiles());
                              } catch (Exception ex) {
                                    LOGGER.warn("WARNING: transferMedia(...) Unable to copy video from dragboard");
                                    ex.printStackTrace();
                              }

                        } else if (dragboard.hasUrl()) {
                              LOGGER.debug("\t dragBoard hasURL ");
                              iView = null;
                              isCompleted = this.transferImageURL(dragboard.getUrl());
                              if (isCompleted) {
                                    // sets the video in the rPane
                                    // to the dragboard file
                                    setVideoHelper(dragboard.getUrl());
                              }
                        } else {
                              LOGGER.warn("\nDragboard does not contain an image or media \nin the expected format: Image, File, URL");
                        }

                        if (isCompleted) {
                              LOGGER.debug("drag-n-drop is completed");
                              //FlashCardOps.getInstance().setMediaIsSynced(false);
                              String[] mediaFileNames;// = new String[];
                              // images always have shapeFileNames, video and audio do not
                              if (shapesFileName != null) {
                                    mediaFileNames = new String[2];
                                    mediaFileNames[0] = getMediaFileName();
                                    mediaFileNames[1] = getShapesFileName();
                              } else {
                                    mediaFileNames = new String[1];
                                    mediaFileNames[0] = getMediaFileName();
                              }
                              rightPane.setOnMouseClicked(m -> {
                                    LOGGER.debug("RightPane clicked.");
                                    rightPaneAction(mediaFileNames);
                              });
                        }
                        //Notify DragEvent if successful.
                        e.setDropCompleted(isCompleted);
                  }
                  e.consume();
                  CreateFlash.getInstance().setFlashListChanged(true);
            }

            /**
             * Sets this objects ImageView to the image in the parameter.
             *
             * @param img  ..
             * @param mime the file ending either .png for all except, if. gif use .gif
             * @return true if successful
             */
            private boolean transferImage(Image img, String mime) {
                  arrayOfFMShapes.clear();

                  LOGGER.debug("*** In transferImage() ***");

                  if (img != null) {
                        image = img;
                        // set section type to double section
                        // with canvas
                        sectionType = 'C';
                        // Transfer the image to the FMCanvas Folder
                        // and rename it.
                        saveImage(img, mime, FlashCardOps.getInstance().getDeckLabelName());
                        String deckName = FlashCardOps.getInstance().getDeckLabelName();
                        String cID = CreateFlash.getInstance().getCurrentCID();
                        shapesFileName = FileNaming.getShapesName(deckName, cID);

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
             * @param deckName ..
             * @return Returns the imageFileName
             */
            private String saveImage(Image image, String mime, String deckName) {
                  BufferedImage inputImage = SwingFXUtils.fromFXImage(image, null);
                  FileNaming fileNaming = new FileNaming(FileNaming.getImageHash(inputImage), 'i', mime);
                  FlashCardOps fco = FlashCardOps.getInstance();
                  boolean bool = fco.saveImage(fileNaming.getMediaFileName(), image, mime, 'c');
                  if (!bool) {
                        deleteMMcellAction();
                        String errorMessage = " That's a drag. That didn't work." +
                            "\n Try dragging to the desktop first. " +
                            "\n then drag from the desk top";
                        FxNotify.notificationBlue("OUCH!!!!", errorMessage, Pos.CENTER, 7,
                            "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getWindow());
                  } else {
                        setMediaFileName(fileNaming.getMediaFileName());
                  }
                  return fileNaming.getMediaFileName();
            }

            /**
             * Transfers a media file based on its type. Discriminates files if they are not
             * of a media type accepted by javaFX. Convert if possible using JAVE2
             *
             * @param files, contains the file to be transferred in [0]
             * @return true if successful
             * @throws EncoderException
             */
            private boolean transferMediaFile(List<File> files) throws EncoderException {
                  // Use the first index contains the dragged file
                  File fromDrag = files.get(0);

                  int num = fromDrag.getName().lastIndexOf('.') + 1;
                  String mime = fromDrag.getName().substring(num);

                  LOGGER.info(" *** in transferMediaFile(list<File>) and OriginFilePath: " + fromDrag.toPath() + " ***");
                  String fromPath = fromDrag.toPath().toString();
                  LOGGER.debug(" mimeType should be image or video: {}", fromPath);

                  MultimediaObject mmObject = new MultimediaObject(new File(fromPath));
                  MultimediaInfo sourceInfo = mmObject.getInfo();

                  if (fromPath == null) {
                        return false;
                  }
                  // Is accepted javaFX image for transfer
                  // Note that the image will be converted to ".png"
                  if (FileExtension.IS_FX_IMAGE.check(mime)) {
                        sectionType = 'C';
                        // Transfer the image to the FMCanvas Folder
                        // and rename it.
                        transferImageURL(fromPath);
                        image = new Image("File:" + fromPath);
                        // insert media into the rightPane.
                        // and add delete buttons
                        setImageHelper(image);

                        return true;

                        // Else its a video, or audio, first try to use JavaFX.
                        // If not in a format that JavaFX handles,
                        // then use JAVE
                  } else if (type.tools.video.Fit.checkDuration(sourceInfo, MAX_DURATION)) {
                        return false;
                  } else if (FileExtension.IS_FX_AV.check(mime) && type.tools.video.Fit.checkSize(sourceInfo)) {
                        MediaPlayer m = null;
                        sectionType = 'M';
                        // Transfer the media to the
                        // FMCanvas Folder and
                        // rename it.
                        this.transferVideoURL(fromPath);
                        // creates the video and insert
                        // into the right pane, add
                        // delete buttons
                        setVideoHelper(fromPath);
                        return true;
                        // Video is not a format that JavaFX handles, use
                        // JAVE for video
                  } else if (FileExtension.IS_JAVE_VIDEO.check(mime)) {
                        sectionType = 'M';
                        //Rename the file. End with mp4
                        setMediaFileName(FileNaming.getVideoName(cID, getQorA(), "mp4"));

                        //mediaFileName = FileNaming.getVideoName(cID, getQorA(), "mp4");
                        DirectoryMgr dirMgr = new DirectoryMgr();
                        String mediaPath = DirectoryMgr.getMediaPath('M');
                        String outputPathName = mediaPath + getMediaFileName();
                        progressIndicator = new ProgressIndicator();

                        LOGGER.debug("Trying to copy file: media outputPathName: {}", outputPathName);

                        File sourceFile = new File(fromPath);
                        //LOGGER.debug("Video sourceFile path: {}", sourceFile.toPath());
                        File outputFile = new File(outputPathName);
                        JaveInterface jave = new JaveInterface();
                        // Transfer and convert the video to a smaller size
                        // if needed.
                        Runnable task = () -> {
                              try {
                                    // Convert the video to smaller format and to .mp4
                                    jave.transfer(sourceFile, outputFile, sourceInfo, mmObject);

                                    String[] mediaFileNames = new String[1];
                                    mediaFileNames[0] = getMediaFileName();

                                    Platform.runLater(() -> runVideoHelper(outputPathName));
                                    //runVideoHelper(outputPathName, progressIndicator);
                              } catch (EncoderException ex) {
                                    throw new AssertionError("Unexpected exception in encoder", ex);
                              }
                        };

                        Thread thread = new Thread(task);
                        thread.start();

                        LOGGER.debug("Video ends with .mov. output pathName: {}", outputFile.getPath());
                        // sets right pane
                        setVideoHelper(outputPathName);
                        rightPane.getChildren().add(progressIndicator);
                        rightPane.setMinSize(100, 50);
                        return true;
                  }

                  return false;
            }

            public void runVideoHelper(String outputPathName) {
                  progressIndicator = null;
                  setVideoHelper(outputPathName);
            }

            /**
             * Helper method transfers an image provided in the parameter
             * to a file for this card and section. Resizes the image and
             * sets it in the rightPane.
             *
             * @param imageURL
             * @return true if successful
             */
            private boolean transferImageURL(String imageURL) {
                  try {
                        LOGGER.info("in transferImageURL");

                        arrayOfFMShapes.clear();
                        copyMediaFile(imageURL, 'C');
                        //setShapeFile(null);
                        return true;
                  } catch (Exception e) {
                        deleteMMcellAction();
                        LOGGER.warn("WARNING:  Unknown Exception transfering ImageURL {}", e.getMessage());
                        String errorMessage = " That's a drag. That didn't work." +
                            "\n Try dragging to the desktop first. " +
                            "\n then drag from the desk top";
                        FxNotify.notificationBlue("OUCH!!!!", errorMessage, Pos.CENTER, 7,
                            "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getWindow());
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
            private void copyMediaFile(String source, char mediaType) {
                  aviFileName = "";
                  LOGGER.info("copyMediaFile called, type: {}, source: {}", mediaType, source);
                  //         authcrypt.UserData data = new authcrypt.UserData();
                  //String currentCardCID = CreateFlash.getInstance().getCurrentCID();

                  //Rename the file. Get the ending of the file.
                  int idx = source.lastIndexOf('.');
                  String ending = source.substring(idx + 1);
                  DirectoryMgr dirMgr = new DirectoryMgr();
                  String mediaPath = DirectoryMgr.getMediaPath(mediaType);
                  LOGGER.debug("mediaPath: " + mediaPath);

                  if (FileOpsUtil.folderExists(new File(mediaPath))) {
                        LOGGER.debug("copyMediaFile folder exists");
                        if (mediaType == 'M') {
                              try {
                                    //String hash = FileNaming.getVideoHash(source);
                                    //FileNaming fileNaming = new FileNaming(hash, 'v', ending);
                                    setMediaFileName(FileNaming.getVideoName(cID, getQorA(), ending));
                                    //mediaFileName = ;
                                    String mediaURL = mediaPath + getMediaFileName();

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
                              Image image = new Image("File:" + source);
                              BufferedImage inputImage = SwingFXUtils.fromFXImage(image, null);
                              FileNaming fileName = new FileNaming(FileNaming.getImageHash(inputImage), 'i', "." + ending);
                              // Resize image if larger than 800 x 800
                              if (800 < image.getHeight() || 800 < image.getWidth()) {
                                    double scale = Fit.calcScale(image.getWidth(), image.getHeight(), 800, 800);
                                    LOGGER.debug("scale: " + scale);

                                    double scaledHeight = image.getHeight() * scale;
                                    double scaledWidth = image.getWidth() * scale;
                                    java.nio.file.Path targetImg = null;
                                    try {
                                          // create output image
                                          LOGGER.info("copying image by hand");
                                          setMediaFileName(fileName.getMediaFileName());
                                          //mediaFileName = fileName.getMediaFileName();
                                          shapesFileName = FileNaming.getShapesName(getMediaFileName());


                                          targetImg = Paths.get(mediaPath + getMediaFileName());

                                          LOGGER.debug("image getType: " + inputImage.getType()
                                              + "\n ht" + (int) scaledHeight
                                              + "\n wd" + (int) scaledWidth
                                              + "\n imgPath:" + targetImg
                                              + "\n ending: " + ending
                                          );
                                          // create output image
                                          BufferedImage outputImage = new BufferedImage((int) scaledWidth,
                                              (int) scaledHeight, inputImage.getType());
                                          Graphics2D g2d = outputImage.createGraphics();
                                          g2d.drawImage(inputImage, 0, 0, (int) scaledWidth, (int) scaledHeight, null);
                                          ImageIO.write(outputImage, ending, new File(targetImg.toUri()));
                                          g2d.dispose();
                                    } catch (IOException e) {
                                          LOGGER.error("Error copying image: {}", e.getMessage());
                                          //e.printStackTrace();
                                    }

                                    File check = new File(targetImg.toUri());
                                    if (!check.exists()) {
                                          LOGGER.warn("WARNING: Image file does not exist");
                                    }
                                    // test
                                    //Image testImage = new Image( "File: " + mediaPath + mediaURL);
                                    //LOGGER.info("Is image < 800 x 800? wd: {} ht: {}", testImage.getWidth() <= 800, testImage.getHeight() <= 800);
                              } else {
                                    // the image is smaller than 800x800
                                    setMediaFileName(fileName.getMediaFileName());
                                    //mediaFileName = fileName.getMediaFileName();
                                    shapesFileName = FileNaming.getShapesName(getMediaFileName());
                                    String mediaURL = mediaPath + getMediaFileName();

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
      } // END INNER CLASS DragAndDrop


      /*************************** END INNER CLASSES ***********************/


      /**
       * Disables buttons for this object. To disable all buttons for the
       * Card editor, call CreateFlash.DisableButtons();
       */
      public void disableEditorBtns() {
            LOGGER.debug("\n *~*~* disableEditorBtns called *~*~*");
            // disable drag and drop
            dNdIsdisabled = true;

            if (deleteMMCellBtn != null) {
                  deleteMMCellBtn.setDisable(true);
                  deleteTCellBtn.setDisable(true);
            }
            snapShotBtn.setDisable(true);
            drawpadBtn.setDisable(true);
            clearBtn.setDisable(true);
            if (rightPane != null) {
                  rightPane.setDisable(true);
            }
            cameraBtn.setDisable(true);
      }


      /* ------------------------------------------------------- **/


      /**
       * Enables buttons for this object. To enable all buttons for the
       * SectionEditor, call CreateFlash.EnableButtons();
       */
      public void enableEditorBtns() {
            dNdIsdisabled = false;
            if (deleteMMCellBtn != null) {
                  deleteMMCellBtn.setDisable(false);
                  deleteTCellBtn.setDisable(false);
            }
            clearBtn.setDisable(false);
            snapShotBtn.setDisable(false);
            drawpadBtn.setDisable(false);
            cameraBtn.setDisable(false);

            if (rightPane != null) {
                  rightPane.setDisable(false);
            }
      }



      /* ------------------------------------------------------- **/


      // *********** for testing methods ****************


      @FMAnnotations.DoNotDeployMethod
      public Point2D getSnapShotBtnXY() {

            Bounds bounds = snapShotBtn.getLayoutBounds();
            return snapShotBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getTextAreaXY() {
            Bounds bounds = stackL.getLayoutBounds();
            return tCell.getTextArea().localToScreen(bounds.getMinX() + 50, stackL.getLayoutY() + 40);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getClearTextBtnXY() {
            Bounds bounds = clearBtn.getLayoutBounds();
            return clearBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getRightPaneXY() {
            Bounds bounds = rightPane.getLayoutBounds();
            return rightPane.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public ArrayList getNodesFmRPane() {
            ArrayList<Node> shapeAry = new ArrayList<>(5);
            for (int i = 0; i < rightPane.getChildren().size(); i++) {
                  shapeAry.add(rightPane.getChildren().get(i));
            }
            return shapeAry;
      }

      @FMAnnotations.DoNotDeployMethod
      public Image getImage() {
            return this.image;
      }

      @FMAnnotations.DoNotDeployMethod
      public String getShapesFileNameTestMethod() {
            return shapesFileName;
      }
}

