/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.co) All rights reserved.
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
import fileops.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import media.sound.SoundEffects;
import org.slf4j.Logger;
import type.DnDInterface;
import type.celltypes.*;
import type.draw.DrawObj;
import type.draw.shapes.FMRectangle;
//import FMTriangle;
import type.draw.shapes.GenericShape;
import flashmonkey.*;
//import jave2.videoconverter.*;
import javafx.scene.layout.*;
//import org.kordamp.ikonli.entypo.Entypo;
import org.kordamp.ikonli.fontawesome5.*;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.LoggerFactory;
import uicontrols.*;
import media.camera.CameraCapture;
import media.camera.WindowsCameraCapture;


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
public class SectionEditor implements DnDInterface {
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
      static final int MAX_DURATION = 60;


      // The array of FM shapes as opposed to the arrayOfBuilderShapes which are JavaShapes. FM
      // shapes have added variables and methods
      // do not change
      private ArrayList<GenericShape> arrayOfFMShapes = new ArrayList<>();

      // **** Variables ****
      public HBox sectionHBox;

      private Button clearBtn;
      private Button snapShotBtn;
      private Button drawpadBtn;
      private Button deleteTCellBtn;
      private Button deleteMMCellBtn;
      private Button addTextCellBtn;
      private Button HashTagBtn;
      private Button cameraBtn;
      private Button findBtn;

      private final ButtoniKonClazz FIND = new ButtoniKonClazz("FIND", "Search for images, videos, animations, and tools", FontAwesomeSolid.SEARCH, UIColors.FM_WHITE, ButtoniKonClazz.SIZE_24);
      private final ButtoniKonClazz CAMERA = new ButtoniKonClazz("", "Take a snapshot from your camera", FontAwesomeSolid.CAMERA, UIColors.EDITOR_BTNS, ButtoniKonClazz.SIZE_24);
      private final ButtoniKonClazz SNAPSHOT = new ButtoniKonClazz("", "Take a snapshot from your screen", "icon/24/snapshot_blue4.png", UIColors.EDITOR_BTNS, 0);
      private final ButtoniKonClazz DRAWPAD = new ButtoniKonClazz("", "Draw shapes only", FontAwesomeSolid.DRAW_POLYGON, UIColors.EDITOR_BTNS, ButtoniKonClazz.SIZE_24);
      private final ButtoniKonClazz CLEAR_TEXT = new ButtoniKonClazz("", "Clear text", FontAwesomeSolid.BACKSPACE, UIColors.EDITOR_BTNS, ButtoniKonClazz.SIZE_24);
      private final ButtoniKonClazz CLEAR_T_AREA = new ButtoniKonClazz("", "Remove text area", FontAwesomeSolid.MINUS_CIRCLE, UIColors.FOCUS_BLUE_OPAQUE, ButtoniKonClazz.SIZE_24);
      private final ButtoniKonClazz CLEAR_RIGHT = new ButtoniKonClazz("", "Remove right area", FontAwesomeSolid.MINUS_CIRCLE, UIColors.FOCUS_BLUE_OPAQUE,ButtoniKonClazz.SIZE_24);
      // upper section area.
      private final ButtoniKonClazz ADD_TEXT_CELL = new ButtoniKonClazz("", "Add a text cell", FontAwesomeSolid.FONT, UIColors.EDITOR_BTNS, ButtoniKonClazz.SIZE_16);
      private final ButtoniKonClazz HASHTAG = new ButtoniKonClazz("", "Add searchable keywords about this media", FontAwesomeSolid.HASHTAG, UIColors.EDITOR_BTNS, ButtoniKonClazz.SIZE_16);
      private final ButtoniKonClazz DELETE_T_CELL = new ButtoniKonClazz("", "Remove text cell", FontAwesomeSolid.TIMES_CIRCLE, UIColors.EDITOR_BTNS,ButtoniKonClazz.SIZE_16);
      private final ButtoniKonClazz DELETE_MM_CELL = new ButtoniKonClazz("", "Remove multi-media cell", FontAwesomeSolid.TIMES_CIRCLE, UIColors.EDITOR_BTNS,ButtoniKonClazz.SIZE_16);

      // Package Private
      char qOra;
      String cID;
      VBox txtVBox;
      ProgressIndicator progressIndicator;

      // *** Flags ***
      private boolean drawPadOpen;



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
      CellLayout sectionType = SingleCellType.TEXT; // double or single section
      private static DrawTools draw;
      private Image image;
      ImageView iView;
      private Pane rightPane; // pane showing shape and image
      private StackPane stackL;
      private StackPane stackR;

      // Public? it is called by 20 objects
      public TextCell tCell;
      private DragAndDrop dragAndDrop;
      // Audio, Video, Image
      // aka mediaFileName used
      // by getMediaFileNames[0]
      StringProperty aviFileNameProperty = null;
      // This objects shape File Name used
      // by getMediaFileNames[1]
      private StringProperty shapesFileNameProperty = null;

      /* ------------------------------------------------------- **/


      /* ------------------------------------------------------- **/


      private void initButtons() {
            this.drawpadBtn = DRAWPAD.get();
            this.snapShotBtn = SNAPSHOT.get();
            this.clearBtn = CLEAR_TEXT.get();
            this.cameraBtn = CAMERA.get();
      }


      SectionEditor() { /* empty */ }

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
            aviFileNameProperty = new SimpleStringProperty();
            shapesFileNameProperty = new SimpleStringProperty();
            rightPane = new Pane();

            this.tCell = new TextCell();
            this.sectionHBox = new HBox();
            this.stackL = new StackPane();
            this.stackR = new StackPane();
            this.txtVBox = new VBox( tCell.buildCell ( "", prompt, true, 0 ) );

            double w = FlashMonkeyMain.getPrimaryWindow().getWidth();
            this.txtVBox.setPrefWidth(w);

            // The stackpanes containing left and right items. Allows
            // delete buttons on the layer above the panes.
            stackL.getChildren().add(txtVBox);
            stackL.setAlignment(Pos.TOP_RIGHT);
            stackR.getChildren().add(this.rightPane);
            stackR.setAlignment(Pos.TOP_RIGHT);

            this.cID = cID;
            this.qOra = qOrA;

            // Get the sectionEditorButtons
            HBox buttonBox = new HBox();
            buttonBox.getChildren().add(sectionEditorButtons());
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setId("editorButtonBox");
            txtVBox.getChildren().add(buttonBox);

            sectionHBox.setSpacing(6);
            sectionHBox.setPadding(new Insets(4, 4, 4, 4));
            sectionHBox.setStyle("-fx-background-color: white; -fx-background-radius: 3");
            sectionHBox.setAlignment(Pos.BOTTOM_LEFT);

            // Drag and Drop capability/

            dragAndDrop = DragAndDrop.getInstance();
            DnDNode node = new DnDNode(this);
            registerDndOperations();

            //DnD dnd = new DnD();


            // Set the initial textCell size and container size
            double no = SceneCntl.calcCenterHt(40, 150, FlashMonkeyMain.getPrimaryWindow().getHeight());
            sectionHBox.setPrefHeight(no);
            txtVBox.setPrefHeight(no);
            tCell.getTextArea().setPrefHeight(no - 150);
            tCell.getTextCellVbox().setPrefHeight(no - 125);

            // Provide a responsive UI.
            // Responsive height
            FlashMonkeyMain.getPrimaryWindow().heightProperty().addListener((obs, oldVal, newVal) -> {
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
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  tCell.getTextArea().setText("");
      //            tCell.getTextArea().requestFocus();
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
            this.addTextCellBtn = ADD_TEXT_CELL.get();
            this.addTextCellBtn.setFocusTraversable(false);
            this.addTextCellBtn.setId("clrBtn");
            //this.addTextCellBtn.setTooltip(new Tooltip("Add a text area to the\n card"));
            // Action when textAreaBtn is pressed.
            this.addTextCellBtn.setOnAction((ActionEvent e) -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  addTCellAction();
            });

            this.HashTagBtn = HASHTAG.get();
            this.HashTagBtn.setFocusTraversable(false);
            this.HashTagBtn.setId("clrBtn");
            this.HashTagBtn.setOnAction(e -> {
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  hashTagsPopupEditor();
            });

            // Delete text cell & multi media cell buttons
            this.deleteTCellBtn = DELETE_T_CELL.get();
            this.deleteMMCellBtn = DELETE_MM_CELL.get();
            deleteTCellBtn.setFocusTraversable(false);
            deleteMMCellBtn.setFocusTraversable(false);
            deleteTCellBtn.setId("clrBtn");
            deleteMMCellBtn.setId("clrBtn");
            // Clear stackL from sectionHBox & remove
            // deleteMMCellBtn from right stackR
            this.deleteTCellBtn.setOnAction((ActionEvent e) -> {
                  CreateFlash.getInstance().setFlashListChanged(true);
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  toSingleMediaCellAction();
            });

            // Clear stackR from sectionHBox & remove
            this.deleteMMCellBtn.setOnAction((ActionEvent e) -> {
                  CreateFlash.getInstance().setFlashListChanged(true);
                  SoundEffects.PRESS_BUTTON_COMMON.play();
                  CreateFlash.getInstance().setFlashListChanged(true);
                  deleteMMcellAction();
            });

            HBox buttonBox = new HBox(2);
            buttonBox.setPadding(new Insets(0, 2, 0, 2));
            buttonBox.setStyle("-fx-background-color: #FFFFFF");
            buttonBox.setMaxWidth(225);
//            buttonBox.getChildren().addAll(this.findBtn, this.clearBtn, this.cameraBtn, this.snapShotBtn, this.drawpadBtn);
            buttonBox.getChildren().addAll( this.clearBtn, this.cameraBtn, this.snapShotBtn, this.drawpadBtn);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            return buttonBox;
      } // End Button settings

      /* ------------------------------------------------------- **/

      private void hashTagsPopupEditor() {
            HashTagEditorPopup hashTagEditor = HashTagEditorPopup.getInstance();
            hashTagEditor.popup(this);
      }



      /* ------------------------------------------------------- **/


      /**
       * Deletes the TextCell and replaces it with a single section
       * containing a Media or Image cell. Used by deleteTCellBtn and
       * when an existing card is read in for editing.
       */
      public void toSingleMediaCellAction() {

            LOGGER.info(" DeleteTCellButton pressed ");
            LOGGER.info("masterBox width setting to: " + this.sectionHBox.getWidth());

            //CreateFlash.getInstance().setFlashListChanged(true);

            this.sectionHBox.getChildren().clear();
            stackR.getChildren().clear();

            switch (this.sectionType.get()) {
                  // img and drawing or img only
                  case 'C':
                  case 'c': {
                        this.sectionType = SingleCellType.CANVAS;
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

                        // rescale shapes
                        if(arrayOfFMShapes.size() > 1) {
                              double rectWd = ((FMRectangle) arrayOfFMShapes.get(0)).getWd();
                              double rectHt = ((FMRectangle) arrayOfFMShapes.get(0)).getHt();
                              setShapesInExplode(this.arrayOfFMShapes, rectWd, rectHt,
                                      sectionHBox.widthProperty().get() - 90,
                                      sectionHBox.heightProperty().get() - 20);
                              //if (iView != null) {
                              // responsive height
                              AtomicLong newValHt = new AtomicLong();

                              sectionHBox.widthProperty().addListener((obs, oldval, newVal) -> {
                                    if (null != iView) {
                                          iView.setFitHeight(newValHt.get());
                                          iView.setFitWidth(newVal.doubleValue() - 90);
                                          setShapesInExplode(this.arrayOfFMShapes, rectWd, rectHt,
                                                  newVal.doubleValue() - 90, newValHt.get() - 20);
                                    }
                              });
                              // responsive height
                              sectionHBox.heightProperty().addListener((obs, oldval, newVal) -> {
                                    newValHt.set((long) newVal.doubleValue());
                              });
                        }

                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) ->
                                iView.setFitWidth(newVal.intValue() - 108));  //setPrefWidth(newVal.doubleValue() - 108));

                        // the initial display of the shapes
                        break;
                  }
                  // drawing
                  case 'D':
                  case 'd': {
                        LOGGER.info("\tsetting type to 'd'");
                        this.sectionType = SingleCellType.DRAWING;
                        // for media
                        this.rightPane.setMaxWidth(200);

                        break;
                  }
                  // video or sound
                  case 'M':
                  case 'm': {
                        this.sectionType = SingleCellType.AV;
                        LOGGER.info("\tsetting type to 'm'");
                        this.rightPane.setMaxWidth(this.sectionHBox.getWidth() - 90);
                  }
                  default: {
                        // Default, do nothing. This should not happen.
                  }
            }

            VBox btnBox = new VBox(2);
            btnBox.setPadding(new Insets(4));
//            btnBox.setId(xxxxxxxxxxxx);
            btnBox.getChildren().addAll(addTextCellBtn, HashTagBtn);
            stackR.getChildren().add(this.rightPane);
            this.sectionHBox.getChildren().addAll(btnBox, stackR);
      }


      /* ------------------------------------------------------- **/

      /**
       * Adds a text cell to the left pane.
       */
      private void addTCellAction() {

            LOGGER.info("\ntextAreaBtn pressed");

            //@TODO FINISH addTCell to include media ie video, and reset when addTCell button is clicked.

            CreateFlash.getInstance().setFlashListChanged(true);

            switch (this.sectionType.get()) {
                  case 'c': {
                        this.sectionType = DoubleCellType.CANVAS;
                        String path = image.getUrl();
                        image = null;
                        image = new Image("File:" + path);
                        setImageHelperForRPane(image);
                        break;
                  }
                  case 'd': {
                        this.sectionType = DoubleCellType.DRAWING;
                        break;
                  }
                  case 'm': {
                        this.sectionType = DoubleCellType.AV;
                        break;
                  }
                  default: {
                        // Default, do nothing. This should not happen.
                  }
            }

            this.stackR.getChildren().clear();
            this.stackR.getChildren().add(deleteMMCellBtn);
            this.rightPane.setMaxWidth(100);
            this.sectionHBox.getChildren().clear();
            this.sectionHBox.getChildren().addAll(stackL, stackR);
      }


      /* ------------------------------------------------------- **/


      /**
       * Clear stackR from sectionHBox {@code &} remove
       * deleteTCellbtn from stackL.
       */
      void deleteMMcellAction() {
            //SoundEffects.PRESS_BUTTON_COMMON.play();
            CreateFlash.getInstance().setFlashListChanged(true);
            clearMMCell();
      }

      private void clearMMCell() {
            this.sectionType = SingleCellType.TEXT;
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
            aviFileNameProperty.set("") ;
            aviFileNameProperty.set("");

            rightPane.getChildren().clear();
            stackR.getChildren().clear();

            tCell.getTextArea().setText("");
            setTextCellWidthFull();
            arrayOfFMShapes = new ArrayList<>();

            clearMMCell();
      }


      /* ***************************************************************************
                                    GETTERS
       *****************************************************************************/


      /**
       * Creates an array containing the mediaFileName, and shapesFileName,
       * and returns it.
       *
       * @return String array containing the shapes
       */
      public String[] getMediaNameArray() {
            LOGGER.debug(" **** called getMediaFileNames() before check ****" + "\nmediaFileName: " + aviFileNameProperty.get() + " | shapesFile: " + shapesFileNameProperty.get());
            //Thread.dumpStack();
            if (this.arrayOfFMShapes.size() > 1) {
                  final String[] str = {aviFileNameProperty.get(), shapesFileNameProperty.get()};
                  return str;
            } else {
                  final String[] s = {aviFileNameProperty.get()};
                  return s;
            }
      }

      public String getMediaFileName() {
            return aviFileNameProperty.get();
      }

      public String getShapesFileName() {
            return shapesFileNameProperty.get();
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
       * Returns the mediaType used as well as
       * the sectionType for this section. Section being
       * either a double or single cell section.
       *
       * @return Returns the media type.
       */
      public CellLayout getMediaType() {
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

      private DoubleProperty scale = new SimpleDoubleProperty(0);
      public double getScale() {
            return this.scale.get();
      }

      public DoubleProperty getScaleProperty() {
            return this.scale;
      }


      public void setScale(int cellWd, int cellHt) {
            double imgWd;
            double imgHt;

            if (iView != null) {
                  imgWd = iView.getImage().getWidth();
                  imgHt = iView.getImage().getHeight();
            } else {
                  imgWd = draw.getOverlayWd();
                  imgHt = draw.getOverlayHt();
            }
            scale.set(Fit.calcScale(imgWd, imgHt, cellWd, cellHt));
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
                  aviFileNameProperty.set(mediaName);
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
                  shapesFileNameProperty.set(name);
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


//      public boolean hasTextCell(char sectionType) {
//            return sectionType == 't'
//                    || sectionType == 'C'
//                    || sectionType == 'M'
//                    || sectionType == 'D';
//      }


      /* ------------------------------------------------------- **/


      /**
       * Sets an image in the rightPane for this section
       * from the BufferredImage in the parmeter.
       *
       * @param imgBuffer Expects a BufferedImage
       */
      private void setImageRightPane(BufferedImage imgBuffer) {
            image = SwingFXUtils.toFXImage(imgBuffer, null);
            setImageHelperForRPane(image);
      }


      /* ------------------------------------------------------- **/


      /**
       * Sets the TextCell width to allow the rightPane
       * in the HBox
       */
      public void setTextCellWdForMedia() {
            // DO NOT CHANGE THIS. Different between apple 126 and windows 138.
            double w = FlashMonkeyMain.getPrimaryWindow().getWidth() - 138;
            this.txtVBox.setPrefWidth(w);
      }


      /* ------------------------------------------------------- **/


      public void setTextCellWidthFull() {
            double w = FlashMonkeyMain.getPrimaryWindow().getWidth();
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
       * the delete cell buttons if media is present </p>
       * <p><b>NOTE:</b> Upper case is two cells, lower case is one.</p>
       *
       * @param mediaFileNames Expects the imageName, and shapesArrayName if either exist
       * @param mediaType      drawing only = 'D' 'd', Media = 'm' 'M', Canvas (image and drawing or image only) = 'c' or 'C'
       * @param qOrA           ..
       * @param cID            cardID, does not change.
       */
      public void setSectionMedia(String[] mediaFileNames, CellLayout mediaType, char qOrA, final String cID) {
            DirectoryMgr dirMgr = new DirectoryMgr();
            LOGGER.info(" setSectionMedia() called \n");
            this.sectionType = mediaType;

            double num = SceneCntl.calcCenterHt(40, 150, FlashMonkeyMain.getPrimaryWindow().getHeight());
            sectionHBox.setPrefHeight(num);
            txtVBox.setPrefHeight(num);
            tCell.getTextArea().setPrefHeight(num - 150);
            tCell.getTextCellVbox().setPrefHeight(num - 125);

            switch (mediaType.get()) {
                  // text
                  // 'T' is never used
                  case 't': {
                        double w = FlashMonkeyMain.getPrimaryWindow().getWidth() - 16;
                        this.txtVBox.setPrefWidth(w);
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) ->
                            txtVBox.setPrefWidth(newVal.doubleValue()));
                        break;
                  }
                  // Double Section image with or without shapes
                  case 'C': {
                        // For responsive text pane with the right pane.
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) ->
                            txtVBox.setPrefWidth(newVal.doubleValue() - 138));

                        imageCoordinatorDoubleSection(mediaFileNames);

                        break;
                  }
                  // Single cell section with image and or shapes
                  case 'c': {
                        // Responsive image cell
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) ->
                                txtVBox.setPrefWidth(newVal.doubleValue() - 108));

                        imageCoordinatorSingleSection(mediaFileNames);
                        break;
                  }
                  // Double section drawings
                  case 'D': {
                        // for responsive text pane with the right pane.
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) -> txtVBox.setPrefWidth(newVal.doubleValue() - 124));
                        // no break, let it fall through.
                  }
                  // single section Drawings only
                  case 'd': {
                        shapesFileNameProperty.set(mediaFileNames[1]);
                        LOGGER.debug(" Shapes Only .. This is a DrawPad");
                        this.arrayOfFMShapes.clear();
                        // use 'c' instead of 'd'
                        String path = DirectoryMgr.getMediaPath('c');
                        String shapesPath = path + shapesFileNameProperty.get();
                        File f = new File(shapesPath);
                        if (f.exists()) {
                              FileOpsShapes fo = new FileOpsShapes();
                              this.arrayOfFMShapes = fo.getListFromFile(mediaFileNames[1]);
                              double ht = ((FMRectangle) arrayOfFMShapes.get(0)).getWd();
                              double wd = ((FMRectangle) arrayOfFMShapes.get(0)).getHt();
                              addDrawRPane(this);
                              setShapesInRtPane(this.arrayOfFMShapes, wd, ht);
                              this.rightPane.setOnMouseClicked(e -> {
                                    CanvasEditorPopup edPopup = CanvasEditorPopup.getInstance();
                                    edPopup.init();
                                    String deckName = FlashCardOps.getInstance().getDeckLabelName();
                                    edPopup.popup(this.arrayOfFMShapes, this, mediaFileNames, deckName, cID);
                              });
                        } else {
                              LOGGER.warn("ERROR: Shapes file does not exist. Path: {}", f);
                        }

                        LOGGER.debug("mediaPath: " + path + ", shapesPathName: " + shapesFileNameProperty.get());
                        LOGGER.debug("mediaType: " + mediaType);

                        break;
                  }
                  // Double section media Video and Sound
                  case 'M': {
                        // For responsive text pane with the right pane.
                        CreateFlash.getInstance().getCFPCenter().widthProperty().addListener((obs, oldval, newVal) ->
                            txtVBox.setPrefWidth(newVal.doubleValue() - 108));
                  }
                  // Single section media
                  case 'm': {
                        // use 'c' instead of 'm'
                        String path = DirectoryMgr.getMediaPath('c');
                        String relativeImgPath = path + mediaFileNames[0];
                        LOGGER.debug("relativeImgPath: " + relativeImgPath + ", & mediaType: " + mediaType);
                        setMediaFileName(mediaFileNames[0]);
                        if(mediaType.get() == 'M') {
                              setVideoHelper(relativeImgPath, 100, 100);
                        }
                        else {
                              setVideoHelper(relativeImgPath, 200, 200);
                              toSingleMediaCellAction();
                        }
                        LOGGER.debug("media rightPane has content: " + rightPane.getChildren().isEmpty());

                        break;
                  }
                  // default is do nothing
            }
      }

      /* ------------------------------------------------------- **/

      private void imageCoordinatorSingleSection(String[] mediaFileNames) {
            setMediaFileName(mediaFileNames[0]);
            this.arrayOfFMShapes.clear();
            String path = DirectoryMgr.getMediaPath('c') + mediaFileNames[0];
            File f = new File(path);
            if (f.exists()) {
                  image = new Image("File:" + path);
                  // set shapes in right pane with image
                  FileOpsShapes fo = new FileOpsShapes();
                  if (mediaFileNames.length == 2) {
                        this.shapesFileNameProperty.set(mediaFileNames[1]);
                        this.arrayOfFMShapes = fo.getListFromFile(mediaFileNames[1]);
                        //the rectangle containing the original size
                        FMRectangle rect = ((FMRectangle) this.arrayOfFMShapes.get(0));
                        double origHt = rect.getHt();
                        double origWd = rect.getWd();
                  }
                  toSingleMediaCellAction();

                  this.rightPane.setOnMouseClicked(e -> {
                        SoundEffects.PRESS_BUTTON_COMMON.play();
                        rightPaneAction(mediaFileNames);
                  });
            }
            else {
                  LOGGER.warn("ERROR: SectionEditor setSectionMedia(...) image file does not exist. ");
            }
      }

      /* ------------------------------------------------------- **/

      private void imageCoordinatorDoubleSection(String[] mediaFileNames) {
            setMediaFileName(mediaFileNames[0]);
            this.arrayOfFMShapes.clear();
            String path = DirectoryMgr.getMediaPath('c') + mediaFileNames[0];
            File f = new File(path);
            if (f.exists()) {
                  image = new Image("File:" + path);
                  // set shapes in right pane with image
                  setImageHelperForRPane(image);

                  FileOpsShapes fo = new FileOpsShapes();
                  if (mediaFileNames.length == 2) {
                        this.shapesFileNameProperty.set(mediaFileNames[1]);
                        this.arrayOfFMShapes = fo.getListFromFile(mediaFileNames[1]);
                        //the rectangle containing the original size
                        FMRectangle rect = ((FMRectangle) this.arrayOfFMShapes.get(0));
                        double origHt = rect.getHt();
                        double origWd = rect.getWd();

                        // for double cell, when image and shapes are in Rt pane.
                        // Note uses the original pane width for ratio.
                        setShapesInRtPane(this.arrayOfFMShapes, origWd, origHt);
                  }

                  this.rightPane.setOnMouseClicked(e -> {
                        SoundEffects.PRESS_BUTTON_COMMON.play();
                        rightPaneAction(mediaFileNames);
                  });
            }
            else {
                  LOGGER.warn("ERROR: SectionEditor setSectionMedia(...) image file does not exist. ");
            }

      }

      /* ------------------------------------------------------- **/

      /**
       * Provides the rightPane clickOn action that creates a popup with the image
       * and shapeToolPane/popup.
       *
       * @param mediaFileNames ..
       */
      void rightPaneAction(String[] mediaFileNames) {
            String[] mediaFiles;
            if(mediaFileNames == null || mediaFileNames.length < 1) {
                  mediaFiles = getMediaNameArray();
            } else {
                  mediaFiles = mediaFileNames;
            }
            if(mediaFiles != null && mediaFiles.length > 0) {
                  minimizeFullScreen();
                  LOGGER.debug("rightPane action called");
                  CanvasEditorPopup edPopup = CanvasEditorPopup.getInstance();
                  edPopup.init();
                  String deckName = FlashCardOps.getInstance().getDeckLabelName();
                  edPopup.popup(this.arrayOfFMShapes, this, mediaFiles, deckName, cID);
                  CreateFlash.getInstance().setFlashListChanged(true);
            }
      }


      /* ------------------------------------------------------- **/


      /**
       * Adds an image to the rightPane, adds the rightPane deleteButton
       * and Adds the leftPane deleteButton.
       *
       * @param image ..
       */
      public void setImageHelperForRPane(Image image) {
            setImageHelperWSize(image, 100, 100);
      }

      private void setImageHelperWSize(Image image, double paneWd, double paneHt) {
            LOGGER.debug("does image exist, check width: {}", image.getWidth());

            this.sectionHBox.getChildren().clear();
            this.stackR.getChildren().clear();
            // Set the size of the ImageView pane
            this.iView = Fit.viewResize(image, paneWd, paneHt);
//            iView.setRotate(90);
            setTextCellWdForMedia();
            LOGGER.debug("if imageView contains an image: Check if width is a number: {}", iView.getImage().getWidth());
            // sets the image in the right
            setViewInRPane(iView);
            // sets the textVBox and delete button in the left stackPane
            addDeleteToLPane(this.stackL);
            // Adds the delete button to the right stackPane
            addDeleteToRPane(this.stackR);

            this.sectionHBox.getChildren().addAll(this.stackL, this.stackR);
      }

      /* ------------------------------------------------------- **/


      /**
       * Sets iView (photo) in rightPane. Checks and clears rightPane if not empty
       *
       * @param iView ..
       */
      private void setViewInRPane(ImageView iView) {
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


      /**
       * Adds video or audio to the rightPane, adds the rightPane deleteButton
       * and Adds the leftPane deleteButton.
       *
       * @param relativePath ..
       */
      void setVideoHelper(String relativePath, double wd, double ht) {
            this.sectionHBox.getChildren().clear();
            this.stackR.getChildren().clear();

            AVCell avCell = new AVCell();
            rightPane = avCell.buildCell(wd, ht, relativePath);
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
                  //this.stackL.setMinWidth();
                  this.stackR.setMaxWidth(100);
                  this.rightPane.setMaxWidth(100);
                  this.rightPane.setMinWidth(100);
                  this.rightPane.setMinHeight(100);
            }

            scale = Fit.calcScale(origWd, origHt, 100, 100);

            LOGGER.debug(" Scale: " + scale);

            for (int i = 1; i < fmShapes.size(); i++) {
                  this.rightPane.getChildren().add(fmShapes.get(i).getScaledShape(scale));
            }
      }


      /* ------------------------------------------------------- **/


      public void setShapesInExplode(ArrayList<GenericShape> fmShapes, double origWd, double origHt, double newWd, double newHt) {
            LOGGER.info("setShapesInRtPane called ");
            double scale;

            LOGGER.debug("newWd: {}, newHt: {}", newWd, newHt);

            this.rightPane.getChildren().clear();
            if (iView != null) {
                  this.rightPane.getChildren().add(iView);
            } else {
                  this.rightPane.setMinWidth(100);
                  this.rightPane.setMinHeight(100);
            }

            scale = Fit.calcScale(origWd, origHt, newWd, newHt);


            LOGGER.debug(" Scale: " + scale);

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
            SoundEffects.PRESS_BUTTON_COMMON.play();
            this.sectionType = DoubleCellType.CANVAS;
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
                  String message = "\n  Something went wrong. I didn't find a web-cam." +
                          "\n  If you have one, check your computer settings \n and try again. ";
                  String emojiPath = "image/Flash_hmm_75.png";
                  FxNotify.notificationError("Oooph!", message, Pos.CENTER, 20,
                      emojiPath, FlashMonkeyMain.getPrimaryWindow());
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
            SoundEffects.PRESS_BUTTON_COMMON.play();
            minimizeFullScreen();
            this.sectionType = DoubleCellType.CANVAS;
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
                        setImageRightPane(SnapShot.getInstance().getImgBuffer());
                        final String imgHash = FileNaming.getImageHash(SnapShot.getInstance().getImgBuffer());
                        final FileNaming fileNaming = new FileNaming(imgHash, 'i', ".png");
                        //Atomic reference for lambda
                        imgFileName.set(fileNaming.getMediaFileName());

                        // store the imageName in the mediaFileName.
                        //mediaFileName = imgFileName.get();
                        setMediaFileName(imgFileName.get());

                        // check that folder exists, if not it will
                        // be created.
                        FileOpsUtil.folderExists(new File(path));
                        FlashCardOps.getInstance().saveImage(imgFileName.get(), image, "png", 'C');
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
                        String shapesFileName = FileNaming.getShapesName(imgFileName.get());
                        drawObj.setFileName(shapesFileName);
                        shapesFileNameProperty.set(shapesFileName);
                        // and finally build the drawtools
                        draw.buildDrawTools(drawObj, this);
                        //draw.popUpTools();
                        drawObj.clearDrawObj();
                        // prevent unecessary work/ Are we editing or creating/ & initially
                        // we are creating
            //            cfp.setFlashListChanged(true);
                  }
                  drawObj.clearDrawObj();
            });


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
            SoundEffects.PRESS_BUTTON_COMMON.play();
            this.rightPane.setMinWidth(100);
            this.rightPane.setMaxWidth(100);
            this.rightPane.setMinHeight(100);

            LOGGER.info("*** drawpad action called ***");
            this.sectionType = DoubleCellType.DRAWING;
            this.arrayOfFMShapes.clear();
            aviFileNameProperty = null;
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

            String deckName = FlashCardOps.getInstance().getDeckLabelName();
            String cID = CreateFlash.getInstance().getCurrentCID();
            setShapeFileName(FileNaming.getShapesName(deckName, cID));

            // check that folder exists, if not it will
            // be created.
            FileOpsUtil.folderExists(new File(mediaPath));

            minimizeFullScreen();
            // Flag for detecting if this instance of SectionEditor has a drawpad open.
            drawPadOpen = true;
            draw = DrawTools.getInstance();
            draw.buildDrawTools(minX, minY, shapesFileNameProperty.get(), this);

            this.rightPane.getChildren().clear();
            this.rightPane.setOnMouseClicked(e -> {
                  rightPaneAction(getMediaNameArray());
            });
            // set drawing in rightPane of sectionEditor
            addDrawRPane(this);

            CreateFlash.getInstance().setFlashListChanged(true);
      }

      /**
       * Disables buttons for this object. To disable all buttons for the
       * Card editor, call CreateFlash.DisableButtons();
       */
      public void disableEditorBtns() {
            LOGGER.debug("\n *~*~* disableEditorBtns called *~*~*");
            // disable drag and drop
            dragAndDrop.setDisabled(true);

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
            dragAndDrop.setDisabled(false);
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


      void saveImageFromImageTool(Image img, String mime) {
            String deckName = FlashCardOps.getInstance().getDeckLabelName();
            // Save the image to the FMCanvas Folder
            // and rename it.
            String imgNewName = dragAndDrop.saveImage(img, mime, deckName);
            setMediaFileName(imgNewName);
      }


      /**
       * The drag and drop handler for this card and section.
       */
      @Override
      public void registerDndOperations() {
            txtVBox.setOnDragOver(e -> DragAndDrop.getInstance().dragOver(e));

            txtVBox.setOnDragDropped(e -> {
                  arrayOfFMShapes.clear();
                  dragAndDrop.dragDropped(e, progressIndicator, cID, qOra, new DnDNode(this));
                  aviFileNameProperty.set(dragAndDrop.getAVIFileNameProperty().getValue());
                  String[] mediaFileNames = dragAndDrop.setMediaFileNames(new DnDNode(this));
                  if(dragAndDrop.isVid()) {
                        // builds from a file
                        if(!dragAndDrop.isJave()) {
                              setVideoHelper(dragAndDrop.getAviURLProperty().get(), 100, 100);
                        }
                        } else {
                        // builds from an image
                        setImageHelperForRPane(dragAndDrop.getImage());
                  }
                  rightPane.setOnMouseClicked(m -> {
                        rightPaneAction(mediaFileNames);
                  });
                  e.consume();
            });
      }

      /**
       * May set a thread to handle long-running video processing.
       * Ensure usage is thread safe.
       */
//      @Override
//      public void setupValueChangedListeners(DnDNode node) {
//            isImgXferCompletedProperty().addListener((obs, oldVal, newVal) -> {
//                  // - Tightly coupled with DragNDrop due to long-running video convert task, and
//                  // displaying the progressIndicator and video in the rightPane.
//                  // - Change listeners get pinged every time it is used, created, or reset.
//                  // prevents unnecessary work.
//                  if(oldVal.equals(newVal)) { return; }
//
//                  node.editor.iView = null;
//                  if (newVal.intValue() == 1) {
//
//                        System.out.println("\n\n\nValue changed in Section Editor \n\n\n");
//
//                        if (isImage()) {
//                              node.editor.getArrayOfFMShapes().clear();
//                              node.editor.sectionType = DoubleCellType.CANVAS;
//                              String cID = CreateFlash.getInstance().getCurrentCID();
//                              String deckName = FlashCardOps.getInstance().getDeckLabelName();
//                              // Why is there a shapesfilename in drag and drop?
//                              // shapesFileName = FileNaming.getShapesName(deckName, cID);
//                              node.editor.setMediaFileName(getAVIFileNameProperty().get());
//                              // Sets the image in the rPane from
//                              // the image created in transferImage(...)
//                              node.editor.setImageHelperForRPane(image);
//
//                        } else if (isVid()) {
//                              node.editor.sectionType = DoubleCellType.AV;
//                              if (isJave()) {
//                                    isJaveCompleteProperty().addListener((i, j, k) -> {
//                                          if (k.intValue() == 1) {
//                                                runVideoHelper(getAVIFileNameProperty().get(), node);
//                                          } else {
//                                                // failed
//                                          }
//                                    });
//                                    node.editor.setMediaFileName(FileNaming.getVideoName(node.editor.cID, node.editor.getQorA(), "mp4"));
//
//                                    Thread thread = new Thread(getTask());
//                                    thread.start();
//
//                              } else {
//                                    node.editor.setMediaFileName(getAVIFileNameProperty().get());
//                              }
//                              // sets the video in the rPane
//                              // to the dragboard file
//                              // videoURL is from the dragboard.
//                              node.editor.setVideoHelper(getVidUrl(), 100, 100);
//                        }
//                  } else {
//                        // It failed.
//                        node.editor.deleteMMcellAction();
//
//                        FxNotify.notificationError("OUCH!!!!", getErrorMsg(), Pos.CENTER, 7,
//                                "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getPrimaryWindow());
//                  }
//            });
//
//      }


      /* ------------------------------------------------------- **/


      // *********** for testing methods ****************


      @FMAnnotations.DoNotDeployMethod
      public Point2D getSnapShotBtnXY() {

            Bounds bounds = snapShotBtn.getLayoutBounds();
            return snapShotBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getDrawpadBtnXY() {

            Bounds bounds = drawpadBtn.getLayoutBounds();
            return drawpadBtn.localToScreen(bounds.getMinX() + 10, bounds.getMinY() + 10);
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
            return shapesFileNameProperty.get();
      }
}

