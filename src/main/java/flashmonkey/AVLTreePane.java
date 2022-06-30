package flashmonkey;

import fileops.DirectoryMgr;
import fmannotations.FMAnnotations;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import fmtree.*;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.testtypes.GenericTestType;
import type.testtypes.QandA;
import type.testtypes.TestList;
import uicontrols.UIColors;

import java.io.File;
import java.util.ArrayList;

import static flashmonkey.ReadFlash.GEN_CARD;

/**
 * Graphical User Interface class using JavaFX 8
 * Provides an interactive graphical representation of an AVL-Tree that may be used to navigate through data.
 * Once the tree is initially built, new nodes in the tree will fade/transition into their prospective positions.
 * EncryptedUser interaction
 * - Users may click on the circle/nodes to select data.
 * - When the EncryptedUser.EncryptedUser hovers over a node, it will show the data in a dark tool-tip pop-up.
 * The tree navigation represents data as it is organized by it's priority within the tree. Higher priorities are shifted
 * left, and lower priorities are shifted right.
 * The AVL-Tree GUI Assists the EncryptedUser.EncryptedUser to easily find specific data in the iOOily javaFX
 * platform. When the EncryptedUser.EncryptedUser has not performed well, the node/circle uses a red highlight, if the EncryptedUser.EncryptedUser did well, the node/
 * circle is represented in green. If the EncryptedUser.EncryptedUser has not engaged with the data it is represented in dark silver.
 *
 * @author Lowell Stadelman
 */
public class AVLTreePane<E extends Comparable<E>> extends Pane {

      private static final Logger LOGGER = LoggerFactory.getLogger(AVLTreePane.class);
      //private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AVLTreePane.class);


      private static final double UNSELECTED = 20; // size of circle
      private static final double SELECTED = 29;
      private final double vGap = 50;
      protected String message = " ... ";
      private int highlighted = Integer.MAX_VALUE;
      // Flag, if true, the added node fades into screen when added to the tree.
      private boolean transition = false;
      // array of ints used to compare if a node
      // uses the fade transition/SequentialTransition
      private int[] newNums = new int[3];
      // Animation object
      private SequentialTransition seq;
      private static final ArrayList<Circle> circleArray = new ArrayList<>();


      /**
       * default constructor
       */
      public AVLTreePane() {
            // blank
      }

      /**
       * sets the message used to display in the AVL-TreePane
       *
       * @param msg
       */
      protected void setMessage(String msg) {
            getChildren().add(new Text(20, 20, msg));
      }

      public void setHighlighted(FlashCardMM fc) {
            this.highlighted = fc.getANumber();
      }

      public void clearHighlighted() {
            this.highlighted = Integer.MAX_VALUE;
      }


      /**
       * Call this method if the Fade Transition is needed for this build iteration of the tree.
       * Call setTransition(boolean transition) to set it to true before calling this method.
       *
       * @param newNums The wrong nums for this iteration
       */
      protected void displayTree(int[] newNums) {

            int log2 = (int) Math.ceil(Math.log(16) * 2);
            int wd = log2 * 100;
            this.transition = true;

            this.newNums = newNums;
            seq = new SequentialTransition();

            FlashMonkeyMain.sceneTree.getWindow().setWidth(wd + 100);
            FlashMonkeyMain.sceneTree.getWindow().setHeight(wd / 2);
            this.setHeight(wd + 200);
            this.getChildren().clear();


            if (FMTWalker.getInstance().getData() != null) {
                  displayTree(FMTWalker.getInstance().getRoot(), wd / 2, vGap, wd / 4);
                  seq.play();
            }
      }


      /**
       * Call this method if transition is not needed for this build of the tree.
       */
      public void displayTree() {
            int log2 = (int) Math.ceil(Math.log(FMTWalker.getCount()) * 2);
            this.transition = false;

            int wd = (log2 * 100) + 200;

            FlashMonkeyMain.sceneTree.getWindow().setWidth(wd);
            FlashMonkeyMain.sceneTree.getWindow().setHeight(wd / 2.5 + 50);
            this.setHeight(wd);
            this.getChildren().clear();

            if (FMTWalker.getInstance().getData() != null) {
                  displayTree(FMTWalker.getInstance().getRoot(), wd / 2, vGap, wd / 4);
            }
      }


      private Circle circle;
      private double prevX = 0;
      private double prevY = 0;

      /**
       * Adds the node in the parameter to the display tree.
       *
       * @param node
       * @param x
       * @param y
       * @param hGap
       */
      private void displayTree(final FMTWalker.Node node, double x, double y, double hGap) {
            /**   attempting to create a single entity for the line and the circle. **/
            // Draw line from parent to left node
            final FMTree.Node root = node;

            if (root.left != null) {
                  int currentNum = ((FlashCardMM) root.left.getData()).getCNumber();

                  // If transition is true and currentNum matches one of newWrongNums[i]
                  if (transition && (currentNum == newNums[0] || currentNum == newNums[1] || currentNum == newNums[2])) {
                        // do nothing
                  } else {
                        Line l = new Line(x - hGap, y + vGap, x, y);
                        l.setStroke(Color.web("#818181"));
                        getChildren().add(l);
                  }

                  prevX = x;
                  prevY = y;
                  displayTree(root.left, x - hGap, y + vGap, hGap / 2);
            }
            // Draw line from parent to right node
            if (root.right != null) {
                  int currentNum = ((FlashCardMM) root.right.getData()).getCNumber();
                  // If transition is true and currentNum matches one of newWrongNums[i]
                  if (transition && (currentNum == newNums[0] || currentNum == newNums[1] || currentNum == newNums[2])) {
                        // do nothing
                  } else {

                        Line r = new Line(x + hGap, y + vGap, x, y);
                        r.setStroke(Color.web("#818181"));
                        getChildren().add(r);
                  }

                  prevX = x;
                  prevY = y;
                  displayTree(root.right, x + hGap, y + vGap, hGap / 2);
            }
            // If node is selected make it larger and set
            // a larger stroke width
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // NOTE!!! USING REFERENCE NOT .equals(...)
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (root == FMTWalker.getCurrentNode()) {
                  circle = new Circle(x, y, SELECTED);
                  circle.setStrokeWidth(2);
                  circleArray.add(circle);
            } else if (((FlashCardMM) root.getData()).getANumber() == highlighted) {
                  circle = new Circle(x, y, UNSELECTED);
                  circleArray.add(circle);
                  //circleArray.add(circle);
                  circle.setStrokeWidth(2);
                  circle.setStroke(Color.web("#9276AC"));
                  //circle.setFill(Color.web("#8146B6"));

                  ScaleTransition st = new ScaleTransition(Duration.millis(1000), circle);
                  st.setByX(.5);
                  st.setByY(.5);
                  st.setCycleCount(Animation.INDEFINITE);
                  st.setAutoReverse(true);

                  st.play();
            }
            // If node is not selected make it normal size
            else {
                  circle = new Circle(x, y, UNSELECTED);
                  circleArray.add(circle);
                  circle.setStrokeWidth(2);
                  //circleArray.add(circle);
            }

            circle = createCircle(circle, root);

            String str = "";
            Text text1;
            if (((FlashCardMM) root.getData()).getQType() == 't' && ((FlashCardMM) root.getData()).getQText().isEmpty()) {
                  str = " ! \n" + (((FlashCardMM) root.getData()).getANumber() + 1);

                  text1 = new Text(x - (str.length()), y - 4, str);
                  //text1.setStyle("-fx-text-fill: ");
                  ScaleTransition st = new ScaleTransition(Duration.millis(1500), circle);
                  st.setByX(.25);
                  st.setByY(.25);
                  st.setCycleCount(Animation.INDEFINITE);
                  st.setAutoReverse(true);

                  st.play();
            } else {
                  str = Integer.toString(((FlashCardMM) root.getData()).getANumber() + 1);
                  text1 = new Text(x - (str.length() * 7.5 / 2), y + 4, str);
            }


            // Fade in nodes. The nodes that are added to the tree after the EncryptedUser.EncryptedUser
            // has answered a question incorrectly.
            if (transition) {
                  int currentNum = ((FlashCardMM) root.getData()).getCNumber();

                  if (currentNum % 10 != 0) {
                        // If this node is one of the two/three wrong nodes from this iteration
                        if (currentNum == newNums[0] || currentNum == newNums[1] || currentNum == newNums[2]) {
                              Shape s = circleLine(x, y, prevX, prevY, root);
                              FadeTransition ft = new FadeTransition(new Duration(250), s);
                              ft.setDelay(new Duration(100));
                              ft.setFromValue(0.0);
                              ft.setToValue(1.0);
                              ft.setCycleCount(1);
                              ft.setAutoReverse(false);
                              // Add text last and separately
                              Text text = new Text(x - 4, y + 4, str);
                              getChildren().addAll(s, text);

                              seq.getChildren().add(ft);
                        } else {
                              getChildren().addAll(circle, text1);
                        }
                  } else {
                        getChildren().addAll(circle, text1);
                  }
            } else {
                  getChildren().addAll(circle, text1);
            }
      }


      /**
       * Creates interactive circles displayed in the AVLTreePane.
       *
       * @param node
       * @return
       */
      private Circle createCircle(Circle circle, FMTree.Node node) {
            LOGGER.debug("CreateCircle Called");

            circle.setOnMouseEntered((MouseEvent event) -> {
                  FlashCardMM currentCard = (FlashCardMM) node.getData();

                  String text = currentCard.getQText();
                  Tooltip qTip = new Tooltip();
                  qTip.setPrefSize(200, 45);
                  qTip.setContentDisplay(ContentDisplay.RIGHT);

                  String[] imagePaths = currentCard.getQFiles();
                  char c = currentCard.getQType();

                  Image image = null;
                  if (c == 'c' || c == 'C') {
                        image = new Image("File:" + DirectoryMgr.getMediaPath('C') + imagePaths[0]);
                        ImageView iView = new ImageView(image);
                        iView.setPreserveRatio(true);
                        iView.setFitHeight(40);
                        iView.setSmooth(true);
                        qTip.setGraphic(iView);
                        // Video or Audio
                  } else if (c == 'm' || c == 'M') {
                        Image playImg = new Image("File:src/image/vidCamera.png");
                        ImageView iView = new ImageView(playImg);
                        iView.setSmooth(true);
                        iView.setFitHeight(40);
                        iView.setPreserveRatio(true);
                        String mediaPathStr = DirectoryMgr.getMediaPath('M') + imagePaths[0];
                        File file = new File(mediaPathStr);
                        if (file.exists()) {
                              Media media = new Media(file.toURI().toString());
                              MediaPlayer mediaP = new MediaPlayer(media);
                              MediaView mView = new MediaView(mediaP);
                              mView.setFitHeight(40);
                              mView.setPreserveRatio(true);
                              mView.setSmooth(true);
                              StackPane pane = new StackPane(mView, iView);
                              pane.setAlignment(Pos.CENTER);

                              qTip.setGraphic(pane);
                        } else {
                              LOGGER.warn("No media File on click! I did nothing.");
                        }
                  }

                  if ( ! text.isEmpty()) {
                        qTip.setText(text);
                  } else {
                        qTip.setText("This flashcard has no content");
                  }
                  qTip.setWrapText(true);
                  qTip.setTextOverrun(OverrunStyle.ELLIPSIS);
                  Tooltip.install(circle, qTip);
            });
            // Actions on mouse click
            circle.setOnMouseClicked((MouseEvent event) -> {
                  if ( FlashMonkeyMain.isInEditorMode() ) {
                        this.creatorFlashTreeOnClick(node);
                  } else {
                        this.readFlashTreeOnClick( node);
                  }
            }); //   *** END SET ON MOUSE CLICK ***

            circle.setFill(fillColor(node));
            circle.setStroke(lineColor(node));

            return circle;
      }

      /**
       * Action for AVLTreePane on click when in CreateFlash mode
       * @param node
       */
      private void creatorFlashTreeOnClick( FMTree.Node node) {

            CreateFlash cfp = CreateFlash.getInstance();
            //LOGGER.debug("currentCard: \n{}",   (FlashCardMM) node.getData());
            FlashCardMM selectedCard = (FlashCardMM) node.getData();
            // because we reset the tree on delete, and buildTree uses references
            // when comparing two nodes, we must set the current node to the
            // new list. hahahahahahaaaaaa!
            int idx = selectedCard.getANumber();

            boolean moveOK = cfp.cardOnExitActions(true );
            if ( moveOK ) {
                  // Set the treeWalker current node to the new reference for
                  // the selected node.
                  selectedCard = (FlashCardMM) CreateFlash.getInstance().getCreatorList().get(idx);

                  FlashCardOps fco = FlashCardOps.getInstance();
                  fco.getTreeWalker().setCurrentNode(selectedCard);

                  // SET THE SECTIONS TO THE CURRENT CARDS DATA
                  CreateFlash.getInstance().setListIdx(idx);
                  CreateFlash.getInstance().setSectionEditors(selectedCard);

                  // Display the tree
                  displayTree();
            }
            // do nothing. Should be handled by popup
      }


      /**
       * Action for AVLTreePane on  click when in ReadFlash mode.
       * @param node
       */
      private void readFlashTreeOnClick( FMTree.Node node) {
            ReadFlash rf = ReadFlash.getInstance();
            FlashCardMM oldCC = (FlashCardMM) FMTWalker.getInstance().getData();
            int oldNum = oldCC.getCNumber();
            FMTWalker.getInstance().setCurrentNode(node);
            // Display the tree
            displayTree();
            FlashCardMM currentCard = (FlashCardMM) node.getData();
            GenericTestType test = TestList.selectTest(currentCard.getTestType());
            ReadFlash.ansQButtonSet(currentCard.getIsRight(), test);
            rf.buttonDisplay(FMTWalker.getCurrentNode());
            ReadFlash.rpCenter.getChildren().clear();
            if (rf.getMode() == 't') {
                  ReadFlash.rpCenter.getChildren().add(test.getTReadPane(currentCard, GEN_CARD, ReadFlash.rpCenter));
            } else {
                  ReadFlash.rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(currentCard, GEN_CARD, ReadFlash.rpCenter));
            }
            /// create transitions based on greater or less than
            /// previous card.
            if (oldNum < currentCard.getCNumber()) {
                  FMTransition.getQRight().play();
            } else {
                  FMTransition.getQLeft().play();
            }
            if (FMTransition.getAWaitTop() != null) {
                  FMTransition.getAWaitTop().play();
            }
      }



      /**
       * Sets the color of the line according to past history of the card.
       * Compares numRight with numSeen or in some cases with score.
       *
       * @param node
       * @return
       */
      private Color lineColor(FMTWalker.Node node) {
            FlashCardMM currentCard = (FlashCardMM) node.getData();
            int numSeen = currentCard.getNumSeen();
            int numRt = currentCard.getNumRight();

            // node is empty
            if (currentCard.getQType() == 't' && currentCard.getQText().isEmpty()) {
                  return Color.web(UIColors.HIGHLIGHT_YELLOW);
            }
            if (node == FMTWalker.getInstance().getCurrentNode()) // node currently viewed.
            {
                  return Color.web("#039ED3"); // blue
            }
            if (numSeen == 0) {
                  return Color.web(UIColors.FM_WHITE);
            }
            if (numRt == numSeen) // answers are correct more than not
            {
                  return Color.web("#3AFF66"); // FM green  #188A07
            }
            if ( numRt < numSeen )  // rarely answers correctly
            {
                  return Color.web("#D80519"); // FM dark red #8D060A
            } else  // question is neither good nore bad.
            {
                  return Color.web("#747474"); // grey
            }
      }

      /**
       * Sets the color of the fill according to this sessions status
       *
       * @param node
       * @return Returns White if not seen, red if incorrect, and green if correct.
       */
      private Color fillColor(FMTWalker.Node node) {
            FlashCardMM currentCard = (FlashCardMM) node.getData();

            LOGGER.debug("fillColor: is isRight() negitive for card/node <{}>, <{}> ", currentCard.getCNumber(), currentCard.getIsRight() < 0);

            if (Float.floatToIntBits(currentCard.getIsRight()) >>> 32 == 1) // Optimized to check the MSB, if it is 1 it is negitive
            {
                  return Color.web("#c0392b"); // FM red #D80519
            } else if (currentCard.getIsRight() == 1) {
                  return Color.web("#05D835"); // FM green 188A07
            } else if (currentCard.getIsRight() == 3) {
                  return Color.web("#E8E8E8"); // grey RGB 232 232 232
            } else if (Integer.lowestOneBit(currentCard.getCNumber()) == 1)  // check the least significant bit for 1. It's less than 10
            {
                  return Color.web("#D88F05"); // FM Orange #D88F05
            } else {
                  return Color.web("#FEFFFE");  // non selected unvisited -FM off white #F6EB79
            }
      }

      /**
       * Creates a line and circle union. The line starts at the parent circle's XY location
       * and ends at this node circles center xy. The circle contains the node as a clickable
       * node.
       *
       * @param centerX The location of the circle X position
       * @param centerY The location of the circle Y position
       * @param parentX The parent x pos
       * @param parentY the parent Y pos
       * @param node    the node for this circle
       * @return Returns the circle as a Shape
       */
      private Shape circleLine(double centerX, double centerY, double parentX, double parentY, FMTree.Node node) {

            Line line = new Line(parentX, parentY, centerX, centerY);

            Circle c = new Circle(centerX, centerY, UNSELECTED);

            Shape s = Shape.union(line, c);

            s.setFill(fillColor(node));
            s.setStroke(lineColor(node));
            return s;
      }

      @FMAnnotations.DoNotDeployMethod
      public Point2D getCircleXY(int nodeIdx) {
            //LOGGER.setLevel(Level.DEBUG);
            LOGGER.debug("circleArray.size: {}, Idx: {}", circleArray.size(), nodeIdx);
            if (nodeIdx < circleArray.size()) {
                  Circle c = circleArray.get(nodeIdx);
                  return new Point2D(c.getCenterX() + 8, c.getCenterY() + 16);
            }
            return null;
      }

}
