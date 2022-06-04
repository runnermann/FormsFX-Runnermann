package type.testtypes;

import flashmonkey.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import type.tools.calculator.*;
//import type.tools.calculator.Expression;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import type.sectiontype.GenericSection;
import uicontrols.FxNotify;
import uicontrols.SceneCntl;
import uicontrols.UIColors;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * MathCard is a unique test type. It is interactive in nature.
 *
 * - INTENT:
 *  - Allows EncryptedUser to input formula
 *  - Ensures compliance with PERMDAS, division, multiplication, signs
 *      etc.. during card use.
 *      Provides advice upon an incorrect answer and uses
 *      logic by calculating incorrect answers when attempting
 *      to understand the users mistakes. Then provides the EncryptedUser
 *      advice on their error. And or what to check.
 *      -Pops up advice about the possible errors. IE
 *      PERMDAS error. and provides correction. As a possibility
 *      may provide additional problems to assist with correcting
 *      the error.
 *      - Card may input random number values into fields and test the EncryptedUser
 *      on the card.
 *  - Ensures compliance with PERMDAS, Signs, Multiplication, Division, Addition,
 *      subtraction, etc...
 *
 *  - Most likely use is that the tool/cards are developed by students, TA's or the
 *  professor for the class.
 *
 *  - FITB provides blanks for response.
 *
 *  * Note that the Flow for the cards are that... The problem appears to the EncryptedUser
 *  with the formula, Random generated numbers for the problem(Not implemented), and ... the
 *  EncryptedUser fills in the answer blanks in the problem TextAreas. If answered incorrectly,
 *  The response field appears after the EncryptedUser has provided an answer.
 *
 *  - To add additional operators add them in the appropriate class or the Operator class.
 *  Add them as an enum. Include its type in the getOperator() switch in DijkstraParser. Provide
 *  its priority and ensure that it is correct.
 *
 *  @author Lowell Stadelman
 */
public class MathCard implements GenericTestType<MathCard> {

    private static MathCard CLASS_INSTANCE;

    protected DijkstraParser parser;

    /** Panes **/
    // Contains the entire card
    protected GridPane gPane;// vBox;
    // Contains the upper Section
    private HBox upperHBox;
    // contains the lower Section
    private GridPane lowerGridP = new GridPane();
    // Contains the interactive indicator over the question
    private Pane uPane;
    // Contains the answer field
    private TextField userAnsField;
    // The GenericSection object
    private GenericSection genSection;
    // Answer Button
    private Button answerButton;
    // The Mathmatical expression from/for the question
    protected String expression;
    // Problem as executed by DijkstraParser
    private static String[] ansComponents;
    // Buttons
    Button calcButton = new Button("Calc");
    //final ButtoniKonClazz CALC_BUTTON = new ButtoniKonClazz("FIND", "Search for images, videos, animations, and tools", Entypo.MAGNIFYING_GLASS,UIColors.FM_WHITE);

    /**
     * Default No-Args Constructor
     */
    private MathCard() { /* empty constructor */}

    public static synchronized MathCard getInstance() {
        if(CLASS_INSTANCE == null) {
            CLASS_INSTANCE = new MathCard();
        }
        return CLASS_INSTANCE;
    }
    

    /**
     * Builds the Test Editor Pane for this card
     * @param flashList
     * @param p The formula for the math/Algebra/Trig/Calc problem
     * @param r The response when the EncryptedUser.EncryptedUser gets the answer wrong or correct.
     * @return Returns the Vbox with problem card and response card.
     */
    @Override
    public VBox getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor p, SectionEditor r, Pane pane)
    {
        // Instantiate vBox and "set spacing" !important!!!
        VBox vBox = new VBox(2);
        StackPane sPane = new StackPane();
        p.sectionHBox.setPrefHeight(SceneCntl.calcCellHt());
        // Set prompt in Question/upperBox
        p.setPrompt("Enter Math Formula, student entries go into textFields");
        r.setPrompt("Enter the response if answered incorrectly");

        sPane.getChildren().add(p.sectionHBox);
        vBox.getChildren().addAll(sPane, r.sectionHBox);
        vBox.getChildren().add(calcButton);

        calcButton.setOnAction(e -> {
            // @TODO
            expression = p.getPlainText();
            calcButtonAction(expression, p, r);
        });

        return vBox;
    }


    /* ------------------------------------------------- **/

    /**
     * The main card pane during "test" operations.
     * @param cc
     * @param genCard
     * @return
     */
    @Override
    public GridPane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {

        // Clear the Operator answer components arrayList
        if(Operator.hasOperators()) {
            Operator.clearAnsComponents();
        }

        genSection = GenericSection.getInstance();
        gPane = new GridPane();
        gPane.setVgap(2);

        // The Interactive Question pane.
        // Contains circle pointer.
        uPane = new Pane();
        uPane.setMinHeight(SceneCntl.calcCellHt());
        uPane.setMaxHeight(SceneCntl.calcCellHt());
        // Answer button in Test Mode
        answerButton = new Button("answer");
        answerButton.setOnAction(e -> ansButtonAction(cc, parentPane));

        // The String expression displayed to the EncryptedUser.EncryptedUser
        // Stored in the currentCard.
        expression = cc.getQText();

        double lowerHt = parentPane.getHeight() - 60;
        upperHBox = genSection.sectionFactory(expression, cc.getQType(), 2, false, lowerHt, cc.getQFiles());
        uPane.getChildren().add(upperHBox);

        /* ----------------------- **/

        ReadFlash.getInstance().setShowAnsNavBtns(false);

        /*
         *  1) Create area for answer... Determine if it's images, video's etc for the question
         *      - Completed - Create section for question... IE TextArea.
         *      - Completed - Finish area for users response
         *      - Completed - Create visual cartisian coordinate graph for visualizing graphed equations/solutions.
         *      - In the answer area... Develop responses based on the users answer.. IE by attempting to show
         *      their errors. Or... Perhaps this is a third section that transitions from the bottom?
         */
        // Initially when the user sees the question
        // the lowerVBox contains the users response text field
        // contained in the lowerVBox. Contains the answer button,
        // and the user response field

        VBox lowerVBox = getAnswerBox();
        lowerVBox.setStyle("-fx-background-color: WHITE");
        // After the user answers. If they are wrong, show the interactive
        // list breaking down the problem, along with any multi-media showing
        // how to fix the problem. Note that the button and
        // text field in the lowerStack are cleared by answerWrong().
        lowerGridP.add(lowerVBox, 0, 0, 2, 1);

        gPane.getChildren().clear();
        gPane.addRow(3, lowerGridP );
        gPane.addRow(2, uPane); // upperHBox
        // Transition for Question, Right & end button click
        FMTransition.setQRight( FMTransition.transitionFmRight(uPane));
        // Transition for Question, left & start button click
        FMTransition.setQLeft( FMTransition.transitionFmLeft(uPane));
        // Transition for Answer
        FMTransition.setAWaitTop( FMTransition.waitTransFmTop(lowerVBox, 30, 300, 300));
        FMTransition.getQRight().play();
        FMTransition.getAWaitTop().play();

        return gPane;
    } // END Test READ PANE




    /*------------------------------------------------------------**
     *                          GETTERS
     **------------------------------------------------------------**/

    /**
     * Sets 11th bit (2048), all other bits are 0
     * @return bitSet. Used when parsing
     * cards from flashList
     */
    @Override
    public int getTestType() {
        // 2048
        return 0b0000100000000000;
    }

    @Override
    public String getName() {
        return "Math Card";
    }

    /* ------------------------------------------------- **/

    /**
     * This card is a double horizontal layout
     * // Maybe it should be a single ??? And cards flow from
     * Quiz to response. ???
     * @return
     */
    @Override
    public char getCardLayout()
    {
        return 'D'; // double horizontal
    }

    /* ------------------------------------------------- **/

    @Override
    public GenericTestType getTest() {
        return new MathCard();
    }

    @Override
    public Button[] getAnsButtons() {
        return new Button[] {answerButton};
    }

    @Override
    public Button getAnsButton() {
        return answerButton;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void nextAnsButtAction()
    {
        // stub
    }

    @Override
    public void prevAnsButtAction()
    {
        // stub
    }

    @Override
    public void reset() {
        // ansComponents = new String[0];
        if(parser != null) {
            parser.clearStructures();
        }
    }


    /*------------------------------------------------------------**
     *                        BUTTON ACTIONS
     **------------------------------------------------------------**/

    @Override
    public void ansButtonAction() { /* empty */ }


    public void ansButtonAction(FlashCardMM cc, final Pane parentPane) {
        // The users response
        String response = userAnsField.getText();
        // Solves the expression
        parser = new DijkstraParser(expression);
        double lowerHt = parentPane.getHeight();

        if( response.isEmpty() ) {
            userAnsField.setPromptText("Enter a valid response." );
        } else if(response.contains("/") && expression.contains("/")) {
            // It's a fraction

            userAnsField.setEditable(false);
            answerButton.setDisable(true);
            ExpNode exp = Operator.getLast();
            String[] parts = response.split("/");
            String answer = exp.getExpSolved();
            String[] correctParts = answer.split(" ");

            if (Double.parseDouble(parts[0]) == Double.parseDouble(correctParts[1])
                    && Double.parseDouble(parts[1]) == Double.parseDouble(correctParts[3])) {
                userAnsField.setId("right_border");
            } else {
                responseWrong( cc, lowerHt );
            }
        } else {
            userAnsField.setEditable(false);
            answerButton.setDisable(true);

            // Get the correct answer from the expression
            double correctAnsDbl = parser.getResult();
            // Display a colored border around the users answer box.
            // for invalid inputs use try catch
            try {
                double responseDbl = Double.parseDouble(response);
                if (responseDbl == correctAnsDbl) {
                    userAnsField.setId("right_border");
                    //ansField.setStyle( "-fx-border-color: " + UIColors.HIGHLIGHT_GREEN + "; -fx-border-width: 5;");
                } else { // Answer is wrong
                    responseWrong( cc, lowerHt );
                }
            } catch (NumberFormatException e) {
                responseWrong( cc, lowerHt );
            }
        }
    }

    /* ------------------------------------------------- **/


    // Calc button, EncryptedUser may test input for answer, Tests input for incorrect
    // answer and outputs error message.
    private void calcButtonAction(String expression, SectionEditor uEditor, SectionEditor lEditor) {
        // Parses the expresssion
        parser = new DijkstraParser(expression);

        if(parser.isInvalidInput()) {
            // Insert a temporary error message into the
            // text area

            StringBuilder sb = new StringBuilder();
            sb.append(expression);
            sb.append("\n\n " + parser.getErrorMessage());
            FxNotify.notificationDark("", " Hmmmm! " + sb.toString(), Pos.CENTER, 15,
                    "emojis/flash_headexplosion_60.png", FlashMonkeyMain.getWindow());

        }
        else {
            // @TODO change to getStyledTExt if needed
            StringBuilder sb = new StringBuilder(lEditor.getPlainText());
            sb.append("\n\nRESPONSE = ");
            sb.append(parser.getResult());
            sb.append("\n");
            lEditor.setText(sb.toString());
        }
    }

    /* --------------------------------------------------------------------
     *                    DIJKSTRA EXPRESSION LIST
     * --------------------------------------------------------------------- */



    /**
     * Actions when the EncryptedUser provides the incorrect answer. Clears the
     * upperBox and replaces with the interActiveQuestion. Each segment of
     * the expression is then displayed -contained in TextFields. In the lowerBox,
     * An ordered list of the operations as they were executed is displayed.
     * The executed operations (in the lowerBox) are contained in TextAreas and are clickable.
     * Each ordered (executed) operation contains a reference to it's operator in the interActiveQuestion.
     * The operator in the interActiveQuestion is highlighted with a circle that is displayed
     * in a StackPane above the interActiveQuestion. Thus relating to the EncryptedUser which operation
     * produced which results in a PERMDAS order of operations.
     */
    private void responseWrong(FlashCardMM cc, double lowerHt) {
        VBox vBox = new VBox(2);
        vBox.setMaxWidth(250);
        vBox.setMinWidth(250);
        // The upper section of the interactive that indicates
        // the math operator that is used.
        StackPane upperStack = new StackPane();
        HBox uHBox = genSection.sectionFactory("", cc.getQType(), 2, false, lowerHt, cc.getQFiles());
        HBox iq = interActiveQuestion(parser.getWriterList());
        iq.setPadding(new Insets(15));
        upperStack.getChildren().addAll( uHBox, iq);
        upperHBox.getChildren().clear();
        upperHBox.getChildren().add(upperStack);
        upperHBox.setStyle("-fx-background-color: WHITE");
        // the lower section containing the list section that
        // the user's mouse is hovering over.

        vBox.getChildren().add(new TextField("Solve L -> R using PERMDAS"));
        vBox.getChildren().add(getResponseBox(parser.getWriterList()));
        vBox.setAlignment(Pos.TOP_LEFT);

        lowerGridP.getChildren().clear();
        lowerGridP.setStyle("-fx-background-color: WHITE");

        // Contains the image and the white section as a text area behind the answer list.
        StackPane lowerStack = new StackPane();
        lowerStack.setAlignment(Pos.TOP_LEFT);
        Pane pane = genSection.sectionFactory("", cc.getAType(), 2, true, 0, cc.getAFiles());
        lowerStack.getChildren().add(pane);
        lowerStack.getChildren().add(vBox);
        lowerGridP.getChildren().add(lowerStack);
    }




    /* ------------------------------------------------- **/

    /**
     * Set at the start of the read session.
     * The users response/answer box
     * @return
     */
   protected VBox getAnswerBox() {
            HBox box = new HBox(2);
            userAnsField = new TextField();
            userAnsField.setEditable(true);
            userAnsField.setPrefColumnCount(20);
            box.getChildren().addAll(userAnsField, answerButton);

            box.setPadding(new Insets(80, 0, 0, 0));
            VBox vB = new VBox(2);
            vB.getChildren().add(box);

            return vB;
    }

    /* ------------------------------------------------- **/

    // Used by lambda in getResponseBox
    private HBox expHBox = new HBox();
    private int prevIdx = 0;
    private Circle circle = new Circle(8);
    //Rectangle rect = new Rectangle(18 , 22);
    ArrayList<TextField> textFAry;
    /**
     * Provides the step by step evaluation of the math
     * problem
     * @return returns a ScrollPane with an HBox containing the evaluations
     * in ordered form.
     */
    private ScrollPane getResponseBox(ArrayList expList) {
            VBox vBox = new VBox(2);
            ScrollPane scrollPane = new ScrollPane();

            while( ! Operator.hasOperators()) {
                // expression node
                ExpNode exp = Operator.poll();
                TextField tf = new TextField(exp.getExpSolved());
                tf.setStyle("-fx-border-color: TRANSPARENT; -fx-border-width: 2px;-fx-background-color: TRANSPARENT; -fx-text-fill: BLACK");
                tf.setEditable(false);
                //tf.setMaxWidth(exp.getExpSolved().length() + 4);
                tf.setOnMouseEntered(e -> {
                    tf.setStyle("-fx-border-color:" + UIColors.HIGHLIGHT_ORANGE + "; -fx-border-width: 2px;-fx-background-color: TRANSPARENT; -fx-text-fill: BLACK");
                    setIActiveStyle( prevIdx, exp.getIndex());
                    prevIdx = exp.getIndex();
                });
                tf.setOnMouseExited(e -> {
                    mouseExitedExpBox();
                    tf.setStyle("-fx-border-color: TRANSPARENT; -fx-border-width: 2px;-fx-background-color: TRANSPARENT; -fx-text-fill: BLACK");
                });
                vBox.getChildren().add(tf);
                //vBox.setMaxWidth(exp.getExpSolved().length() + 4);
            }
            scrollPane.setContent(vBox);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToWidth(true);
            return scrollPane;
    }

    /* --------------------------------------------------------------------
    *                           INTERACTIVE QUESTION
    * --------------------------------------------------------------------- */


    /**
     * Sets the Interactive problem when the users response is answered incorrectly. It
     * adds a circle/rectangle over the operator when the EncryptedUser selects an answer part in
     * the response box.
     * @param prevIdx
     * @param expIdx
     */
    private void setIActiveStyle(int prevIdx, int expIdx) {
        uPane.getChildren().remove(circle);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.web(UIColors.HIGHLIGHT_ORANGE));
        circle.setStrokeWidth(2);
        TextField tef = textFAry.get(expIdx);
        double x = tef.getLayoutX() + (tef.getWidth() / 2);
        double y = tef.getLayoutY() + (tef.getHeight() / 2);
        circle.setCenterX(x);
        circle.setCenterY(y);
        //System.out.println("Circle center is x & y " + circle.getCenterX() + " " + circle.getCenterY() );
        // Set the circle in the userPane. The problem UI field.
        uPane.getChildren().add(circle);

    }

    /* ------------------------------------------------- **/

    private void mouseExitedExpBox() {
        uPane.getChildren().remove(circle);
    }

    /* ------------------------------------------------- **/

    private HBox interActiveQuestion(ArrayList expList) {
        // expression HBox
        expHBox = new HBox();
        textFAry = new ArrayList<>(10);
        DecimalFormat f = new DecimalFormat("#.####");

        int length;
        for(int i = 0; i < expList.size(); i++) {
            Object ob = expList.get(i);
            // if object is a double
            TextField tf;
            String s;
            if(ob.getClass().isInstance(1.1)) {
                s = f.format(expList.get(i));
                length = s.length();
                tf = new TextField(s);
            } else {
                s = expList.get(i).toString();
                tf = new TextField(s);
                length = s.length();
            }
            tf.setMaxWidth(length * 10);
            tf.setMinWidth(length * 10);
            tf.setStyle("-fx-stroke-width: 0; -fx-stroke: TRANSPARENT; -fx-background-color: TRANSPARENT;");
            tf.setPadding(Insets.EMPTY);
            tf.alignmentProperty().setValue(Pos.CENTER);

            textFAry.add(tf);
            expHBox.getChildren().add(tf);
        }

        return expHBox;
    }
}
