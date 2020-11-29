package flashmonkey;

import fileops.DirectoryMgr;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
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
import type.testtypes.TestList;
import uicontrols.UIColors;

import java.io.File;

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
 *     @author Lowell Stadelman
 */
public class AVLTreePane<E extends Comparable<E>> extends Pane {
    
    //private static final Logger LOGGER = LoggerFactory.getLogger(AVLTreePane.class);
    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AVLTreePane.class);
    
    
    private static final double UNSELECTED = 20; // size of circle
    private static final double SELECTED = 29;
    private double vGap = 50;
    protected String message = " ... ";
    private int highlighted = Integer.MAX_VALUE;
    // Flag, if true, the added node fades into screen when added to the tree.
    private boolean transition = false;
    // array of ints used to compare if a node
    // uses the fade transition/SequentialTransition
    int[] newNums = new int[3];
    // Animation object
    SequentialTransition seq;


    /**
     * default constructor
     */
    public AVLTreePane()
    {
        // blank
    }

    /**
     * sets the message used to display in the AVL-TreePane
     * @param msg
     */
    protected void setMessage(String msg)
    {
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
     *  Call setTransition(boolean transition) to set it to true before calling this method.
     * @param newNums The wrong nums for this iteration
     */
    protected void displayTree( int[] newNums)
    {

        int log2 = (int) Math.ceil( Math.log(16) * 2 );
        int wd = log2 * 100;
        this.transition = true;

        this.newNums = newNums;
        seq = new SequentialTransition();

        FlashMonkeyMain.sceneTree.getWindow().setWidth(wd + 100);
        FlashMonkeyMain.sceneTree.getWindow().setHeight(wd / 2);
        this.setHeight(wd + 200);
        this.getChildren().clear();
        
        
        if (FMTWalker.getInstance().getData() != null)
        {
            displayTree( FMTWalker.getInstance().getRoot(), wd / 2, vGap, wd / 4);
            seq.play();
        }
    }


    /**
     * Call this method if transition is not needed for this build of the tree.
     */
    public void displayTree() {
        int log2 = (int) Math.ceil( Math.log(FMTWalker.getCount()) * 2 );
        this.transition = false;

        int wd = (log2 * 100) + 200;

        FlashMonkeyMain.sceneTree.getWindow().setWidth(wd);
        FlashMonkeyMain.sceneTree.getWindow().setHeight(wd / 2.5 + 50);
        this.setHeight(wd);
        this.getChildren().clear();

        if (FMTWalker.getInstance().getData() != null) {
            displayTree( FMTWalker.getInstance().getRoot(), wd / 2, vGap, wd / 4);
        }
    }


    private Circle circle;
    private double prevX = 0;
    private double prevY = 0;
    /**
     * Adds the node in the parameter to the display tree.
     * @param node
     * @param x
     * @param y
     * @param hGap
     */
    private void displayTree(FMTWalker.Node node, double x, double y, double hGap) {
        /**   attempting to create a single entity for the line and the circle. **/
        // Draw line from parent to left node
        if (node.left != null) {
            int currentNum = ((FlashCardMM) node.left.getData()).getCNumber();

            // If transition is true and currentNum matches one of newWrongNums[i]
            if (transition && (currentNum == newNums[0] || currentNum == newNums[1] || currentNum == newNums[2]))
            {
                // do nothing
            } else {
                Line l = new Line(x - hGap, y + vGap, x, y);
                l.setStroke(Color.web("#818181"));
                getChildren().add(l);
            }

            prevX = x;
            prevY = y;
            displayTree(node.left, x - hGap, y + vGap, hGap / 2);
        }
        // Draw line from parent to right node
        if (node.right != null) {
            int currentNum = ((FlashCardMM) node.right.getData()).getCNumber();
            // If transition is true and currentNum matches one of newWrongNums[i]
            if (transition && (currentNum == newNums[0] || currentNum == newNums[1] || currentNum == newNums[2]))
            {
                // do nothing
            } else {

                Line r = new Line(x + hGap, y + vGap, x, y);
                r.setStroke(Color.web("#818181"));
                getChildren().add(r);
            }

            prevX = x;
            prevY = y;
            displayTree(node.right, x + hGap, y + vGap, hGap / 2);
        }
        // If node is selected make it larger and set
        // a larger stroke width
        if( node == FMTWalker.getCurrentNode()) {
            circle = new Circle(x, y, SELECTED);
            circle.setStrokeWidth(2);
        }
        else if(((FlashCardMM) node.getData()).getANumber() == highlighted) {
            circle = new Circle(x, y, UNSELECTED);
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
            circle.setStrokeWidth(2);
        }

        circle = createCircle(circle, node);
    
        String str = "";
        Text text1;
        if (((FlashCardMM)node.getData()).getQType() == 't' && ((FlashCardMM)node.getData()).getQText().isEmpty()) {
            str = " ! \n" + Integer.toString(((FlashCardMM)node.getData()).getANumber() + 1);
            
            text1 = new Text(x - (str.length() ), y - 4, str);
            //text1.setStyle("-fx-text-fill: ");
            ScaleTransition st = new ScaleTransition(Duration.millis(1500), circle);
            st.setByX(.25);
            st.setByY(.25);
            st.setCycleCount(Animation.INDEFINITE);
            st.setAutoReverse(true);
    
            st.play();
        } else {
            str = Integer.toString(((FlashCardMM)node.getData()).getANumber() + 1);
            text1 = new Text(x - (str.length() * 7.5 / 2), y + 4, str);
        }
        

        // Fade in nodes. The nodes that are added to the tree after the EncryptedUser.EncryptedUser
        // has answered a question incorrectly.
        if( transition ) {
            int currentNum = ((FlashCardMM) node.getData()).getCNumber();

            if (currentNum % 10 != 0) {
                // If this node is one of the two/three wrong nodes from this iteration
                if (currentNum == newNums[0] || currentNum == newNums[1] || currentNum == newNums[2]) {
                    Shape s = circleLine(x, y, prevX, prevY, node);
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
                }
                else {
                    getChildren().addAll(circle, text1);
                }
            }
            else {
                 getChildren().addAll(circle, text1);
            }
        }
        else {
            getChildren().addAll(circle, text1);
        }
    }


    /**
     * Creates interactive circles displayed in the AVLTreePane.
     * @param node
     * @return
     */
    private Circle createCircle(Circle circle, FMTree.Node node)
    {
        
        LOGGER.debug("CreateCircle Called");
        
        circle.setOnMouseEntered((MouseEvent event) ->
        {
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
            }
            else if (c == 'm' || c == 'M') {
                Image playImg = new Image("File:src/image/vidCamera.png");
                ImageView iView = new ImageView(playImg);
                iView.setSmooth(true);
                iView.setFitHeight(40);
                iView.setPreserveRatio(true);
                String mediaPathStr = DirectoryMgr.getMediaPath('M') + imagePaths[0];
                File file = new File(mediaPathStr);
                if(file.exists()) {
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

            if( ! text.isEmpty()) {
                qTip.setText(text);
            } else {
                qTip.setText("This flashcard has no content");
            }
            qTip.setWrapText(true);
            qTip.setTextOverrun(OverrunStyle.ELLIPSIS);
            Tooltip.install(circle, qTip);
        });

        circle.setOnMouseClicked( (MouseEvent event) -> {
            CreateFlash cfp = CreateFlash.getInstance();
            boolean ret = cfp.saveCurrentCardExt();
            if(cfp.isntStarted() || !ret)
            {
                ReadFlash rf = ReadFlash.getInstance();
                FMTWalker.setCurrentNode(node);
    
                // Display the tree
                displayTree();
                FlashCardMM currentCard = (FlashCardMM) node.getData();

                GenericTestType test = TestList.selectTest(currentCard.getTestType());
                // If UI is in editor mode sets the card clicked on in the editor
                if (FlashMonkeyMain.isInEditorMode()) {
                    
                    CreateFlash.getInstance().getEditorL().resetSection();
                    CreateFlash.getInstance().getEditorU().resetSection();
                    CreateFlash.getInstance().setListIdx(currentCard.getANumber());
                    CreateFlash.getInstance().setUIFields(currentCard);
                // Else set the UI to the study mode
                }
                else {
                    rf.ansQButtonSet(currentCard.getIsRight(), test);
                    rf.buttonTreeDisplay(FMTWalker.getCurrentNode());
                    ReadFlash.rpCenter.getChildren().clear();
                    ReadFlash.rpCenter.getChildren().add( test.getTReadPane(currentCard, GEN_CARD, CreateFlash.getInstance().getMasterPane()));
                }
            }
            // else
                // ???? an error is displayed to the user.

        }); //   *** END SET ON MOUSE CLICK ***

        circle.setFill(fillColor(node));
        circle.setStroke(lineColor(node));

        return circle;
    }
    
    /** 
     * Sets the color of the line according to past history of the card.
     * Compares numRight with numSeen or in some cases with score. 
     * @param node
     * @return 
     */
    private Color lineColor(FMTWalker.Node node)
    {
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
        if (numSeen == 0)
        {
            return Color.web(UIColors.FM_WHITE);
        }
        if (numRt == numSeen ) // answers are correct more than not
        {
            return Color.web("#3AFF66"); // FM green  #188A07
        }
        if (numRt < numSeen )  // rarely answers correctly
        {
            return Color.web("#D80519"); // FM dark red #8D060A
        }
        else  // question is neither good nore bad. 
        {
            return Color.web("#747474"); // grey
        }
    }
    
    /**
     * Sets the color of the fill according to this sessions status
     * @param node
     * @return Returns White if not seen, red if incorrect, and green if correct.
     */
    private Color fillColor(FMTWalker.Node node)
    {
        FlashCardMM currentCard = (FlashCardMM) node.getData();
    
        LOGGER.debug("fillColor: is isRight() negitive for card/node <{}>, <{}> ", currentCard.getCNumber(), currentCard.getIsRight() < 0);
        
        if( Float.floatToIntBits(currentCard.getIsRight()) >>> 32 == 1) // Optimized to check the MSB, if it is 1 it is negitive
        {
            return Color.web("#c0392b"); // FM red #D80519
        }
        else if(currentCard.getIsRight() == 1)
        {
            return Color.web("#05D835"); // FM green 188A07
        }
        else if(currentCard.getIsRight() == 3)
        {
            return Color.web("#E8E8E8"); // grey RGB 232 232 232
        }
        else if (Integer.lowestOneBit(currentCard.getCNumber()) == 1)  // check the least significant bit for 1. It's less than 10
        {
            return Color.web("#D88F05"); // FM Orange #D88F05
        }
        else
        {
            return Color.web("#FEFFFE");  // non selected unvisited -FM off white #F6EB79
        }
    }

    /**
     * Creates a line and circle union. The line starts at the parent circle's XY location
     * and ends at this node circles center xy. The circle contains the node as a clickable
     * node.
     * @param centerX The location of the circle X position
     * @param centerY The location of the circle Y position
     * @param parentX The parent x pos
     * @param parentY the parent Y pos
     * @param node the node for this circle
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
}
