package type.testtypes;

import flashmonkey.*;
import fmtree.FMTWalker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import type.cardtypes.CardLayout;
import type.celltypes.CellLayout;
import type.celltypes.SingleCellType;
import type.tools.calculator.*;

import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import type.sectiontype.GenericSection;
import uicontrols.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * MathCard is a unique test type. It is interactive in nature.
 * <p>
 * - INTENT:
 * - Allows EncryptedUser to input formula
 * - Ensures compliance with PERMDAS, division, multiplication, signs
 * etc.. during card use.
 * Provides advice upon an incorrect answer and uses
 * logic by calculating incorrect answers when attempting
 * to understand the users mistakes. Then provides the EncryptedUser
 * advice on their error. And or what to check.
 * -Pops up advice about the possible errors. IE
 * PERMDAS error. and provides correction. As a possibility
 * may provide additional problems to assist with correcting
 * the error.
 * - Card may input random number values into fields and test the EncryptedUser
 * on the card.
 * - Ensures compliance with PERMDAS, Signs, Multiplication, Division, Addition,
 * subtraction, etc...
 * <p>
 * - Most likely use is that the tool/cards are developed by students, TA's or the
 * professor for the class.
 * <p>
 * - FITB provides blanks for response.
 * <p>
 * * Note that the Flow for the cards are that... The problem appears to the EncryptedUser
 * with the formula, Random generated numbers for the problem(Not implemented), and ... the
 * EncryptedUser fills in the answer blanks in the problem TextAreas. If answered incorrectly,
 * The response field appears after the EncryptedUser has provided an answer.
 * <p>
 * - To add additional operators add them in the appropriate class or the Operator class.
 * Add them as an enum. Include its type in the getOperator() switch in DijkstraParser. Provide
 * its priority and ensure that it is correct.
 *
 * @author Lowell Stadelman
 */
public class MathCard extends TestTypeBase implements GenericTestType<MathCard> {

      private static MathCard CLASS_INSTANCE;

      // Parses the input from the user
      // Informs the user when an error occurs.
      protected DijkstraParser parser;


      // The GenericSection object
      private GenericSection genSection;
      /*
       * Panes
       **/
      // Contains the entire card
      protected GridPane gPane;
      // Contains the upper Section
      private HBox upperHBox;
      // Contains the lower Section
      private final GridPane lowerGridP = new GridPane();
      // Contains the interactive indicator over the question
      private Pane interActiveQPane;
      private StackPane uStackPane;

      // Contains the answer field
      private TextField userAnsField;

      // Answer Button
      private Button selectAnsButton;
      // The Mathmatical expression from/for the question
      protected String expression;
      // Problem as executed by DijkstraParser
      private static String[] ansComponents;
      // Controls unique to this CardType
      final ButtoniKonClazz CHECK_BUTTON = new ButtoniKonClazz("CALCULATOR", "Check for formatting errors,\n and check the answer.", FontAwesomeSolid.CALCULATOR,UIColors.FM_WHITE, ButtoniKonClazz.SIZE_24);
      private Button checkButton;
      private long millis;

      /**
       * Default No-Args Constructor
       */
      private MathCard() {
            setScore(2);
      }

      public static synchronized MathCard getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new MathCard();
            }
            return CLASS_INSTANCE;
      }


      /**
       * Builds the Test Editor Pane for this card
       *
       * @param flashList
       * @param p The formula for the math/Algebra/Trig/Calc problem
       * @param r The response when the EncryptedUser.EncryptedUser gets the answer wrong or correct.
       * @return Returns the Vbox with problem card and response card.
       */
      @Override
      public VBox getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor p, SectionEditor r, Pane pane) {
            setButtons();
            // Instantiate vBox and "set spacing" !important!!!
            VBox vBox = new VBox(2);
            StackPane sPane = new StackPane();
            p.sectionHBox.setPrefHeight(SceneCntl.calcCellHt());
            // Set prompt in Question/upperBox
            p.setPrompt("Enter Math Formula");
            r.setPrompt("Enter an explanation on how to solve this problem. Use the 'CHECK ANSWER' button to check for format mistakes and " +
                    " ensure the answer is what is expected. " +
                    " \n\nThis area is displayed in the answer during Q&A Mode. During Test mode, it is shown along with " +
                    " how FlashMonkey solved the problem when the user has entered an incorrect answer. " +
                    " Video, images with annotations may be used in this area to assist with or provide an in depth explanation.");

            sPane.getChildren().add(p.sectionHBox);
            vBox.getChildren().addAll(sPane, r.sectionHBox);
            vBox.getChildren().add(checkButton);

            this.checkButton.setOnAction(e -> {
                  expression = p.getText();
                  calcButtonAction(expression, p, r);
            });

            return vBox;
      }

      /* ------------------------------------------------- **/

      private void setButtons() {
            this.checkButton = CHECK_BUTTON.get();
            this.checkButton.setId("blueButtonRndBdr");
      }


      /* ------------------------------------------------- **/

      /**
       * The main card pane during "test" operations.
       *
       * @param cc
       * @param genCard
       * @return
       */
      @Override
      public GridPane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {

            millis = System.currentTimeMillis();
            // Clear the Operator answer components arrayList
            Operator.clearAnsComponents();

            genSection = GenericSection.getInstance();
            gPane = new GridPane();
            gPane.setVgap(2);

            // The Interactive Question pane.
            // Contains line pointer.
            uStackPane = new StackPane();
            uStackPane.setAlignment(Pos.TOP_LEFT);
            interActiveQPane = new Pane();
            interActiveQPane.setStyle("-fx-background-color: TRANSPARENT");
            interActiveQPane.setMaxWidth(ReadFlash.getInstance().getMasterBPane().getWidth() - 110);
            interActiveQPane.setMaxHeight(100);

            // Answer button in Test Mode
            if (selectAnsButton == null) {
                  selectAnsButton = ButtoniKon.getAnsSelect();
            } else {
                  selectAnsButton = ButtoniKon.getJustAns(selectAnsButton, "SELECT");
            }

            selectAnsButton.setOnAction(e -> ansButtonAction(cc, parentPane));

            // The String expression displayed to the EncryptedUser.EncryptedUser
            // Stored in the currentCard.
            expression = cc.getQText();

            upperHBox = genSection.sectionFactory(expression, cc.getQType(), 3, true, 0, cc.getQFiles());
            uStackPane.getChildren().addAll(upperHBox, interActiveQPane);

            upperHBox.heightProperty().addListener( (o, h, n) -> {
                  uStackPane.setMinHeight(n.doubleValue());
                  uStackPane.setMaxHeight(n.doubleValue());
            });


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
            HBox lowerHBox = genSection.sectionFactory("", SingleCellType.TEXT, 3, true, 0, null);

            StackPane lstackP = new StackPane();
            lstackP.setAlignment(Pos.CENTER);
            lstackP.getChildren().addAll(lowerHBox, lowerGridP);

            gPane.getChildren().clear();



            gPane.addRow(2, uStackPane); // upperHBox
            gPane.addRow(3, lstackP);

            // Transition for Question, Right & end button click
            FMTransition.setQRight(FMTransition.transitionFmRight(uStackPane));
            // Transition for Question, left & start button click
            FMTransition.setQLeft(FMTransition.transitionFmLeft(uStackPane));
            // Transition for Answer
            FMTransition.setAWaitTop(FMTransition.waitTransFmTop(lstackP, 0, 300, 350));
            FMTransition.getQRight().play();

            return gPane;
      } // END Test READ PANE




      /*------------------------------------------------------------**
       *                          GETTERS
       **------------------------------------------------------------**/

      /**
       * Sets 11th bit (2048), all other bits are 0
       *
       * @return bitSet. Used when parsing
       * cards from flashList
       */
      @Override
      public int getTestType() {
            // 2048
            return 0b0000100000000000;
      }

      @Override
      public int getSeconds() {
            long now = System.currentTimeMillis();
            return (int) (now - millis) / 1000;
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
       *
       * @return
       */
      @Override
      public CardLayout getCardLayout() {
            return CardLayout.DOUBLE_HORIZ; // double horizontal
      }

      /* ------------------------------------------------- **/

      @Override
      public GenericTestType getTest() {
            return new MathCard();
      }

      @Override
      public Button[] getAnsButtons() {
            return new Button[]{selectAnsButton};
      }

      @Override
      public Button getAnsButton() {
            return this.selectAnsButton;
      }

      @Override
      public boolean isDisabled() {
            return false;
      }

      @Override
      public void nextAnsButtAction() {
            // stub
      }

      @Override
      public void prevAnsButtAction() {
            // stub
      }

      @Override
      public void reset() {
            // ansComponents = new String[0];
            if (parser != null) {
                  parser.clearStructures();
            }
      }

      @Override
      public void resetSelectAnsButton() {
            selectAnsButton = ButtoniKon.getAnsSelect();
      }


      /*------------------------------------------------------------**
       *                        BUTTON ACTIONS
       **------------------------------------------------------------**/

      @Override
      public void ansButtonAction() { /* empty */ }

      @Override
      public void changed() {
            ReadFlash.getInstance().isChanged();
      }


      public void ansButtonAction(FlashCardMM cc, final Pane parentPane) {
            String response = userAnsField.getText();
            if (response.length() == 0) {
                  userAnsField.setPromptText("Enter a valid response.");
                  return;
            }
            changed();
            // The users response

            // Solves the expression
            parser = new DijkstraParser(expression);
            final double lowerHt = parentPane.getHeight();
            final double progress = ReadFlash.getInstance().getProgress();

            final FlashCardMM currentCard = (FlashCardMM) FMTWalker.getInstance().getCurrentNode().getData();
            final FlashCardOps fo = FlashCardOps.getInstance();
            final ReadFlash rf = ReadFlash.getInstance();
            rf.getProgGauge().moveNeedle(500, rf.incProg());
            // card from the arrayList - Used to update data in the ArrayList Card
            FlashCardMM listCard = fo.getFlashList().get(currentCard.getANumber());
            listCard.setSeconds(getSeconds());


            if (response.contains("/") && expression.contains("/")) {
                  // It's a fraction
                  userAnsField.setEditable(false);
                  selectAnsButton.setDisable(true);
                  ExpNode exp = Operator.getLast();
                  String[] parts = response.split("/");
                  String answer = exp.getExpSolved();
                  String[] correctParts = answer.split(" ");
                  try {
                        if (Double.parseDouble(parts[0]) == Double.parseDouble(correctParts[1])
                            && Double.parseDouble(parts[1]) == Double.parseDouble(correctParts[3])) {
                              userAnsField.setId("right_border");
                              rf.new RightAns(currentCard, this);
                        } else {
                              responseWrong(cc, lowerHt);
                              rf.new WrongAns(currentCard, this);
                        }
                  } catch (NumberFormatException e) {
                        responseWrong(cc, lowerHt);
                        rf.new WrongAns(currentCard, this);
                  }
            } else {
                  userAnsField.setEditable(false);
                  selectAnsButton.setDisable(true);
                  // Get the correct answer from the expression
                  double correctAnsDbl = parser.getResult();
                  // Display a colored border around the users answer box.

                  // for invalid inputs use try catch
                  try {
                        if((response.toLowerCase().equals("infinity") && correctAnsDbl == Double.POSITIVE_INFINITY)
                        || (response.toLowerCase().equals("-infinity") && correctAnsDbl == Double.NEGATIVE_INFINITY)) {
                              userAnsField.setId("right_border");
                              rf.new RightAns(currentCard, this);
                        } else {
                              double num = round(correctAnsDbl, 4);
                              double responseDbl = Double.parseDouble(response);
                              double ans = round(responseDbl, 4);

                              if (ans == num) {
                                    userAnsField.setId("right_border");
                                    rf.new RightAns(currentCard, this);
                                    //ansField.setStyle( "-fx-border-color: " + UIColors.HIGHLIGHT_GREEN + "; -fx-border-width: 5;");
                              } else { // Answer is wrong
                                    responseWrong(cc, lowerHt);
                                    rf.new WrongAns(currentCard, this);
                              }
                        }
                  } catch (NumberFormatException e) {
                        responseWrong(cc, lowerHt);
                        rf.new WrongAns(currentCard, this);
                  }
            }

            if (progress >= FMTWalker.getInstance().getCount()) {
                  ReadFlash.getInstance().endGame();
            }
      }

      /* ------------------------------------------------- **/

      private double round(double value, int places) {
            BigDecimal dec = new BigDecimal(Double.toString(value));
            dec = dec.setScale(places, RoundingMode.HALF_UP);
            return dec.doubleValue();
      }

      /* ------------------------------------------------- **/


      // Calc button, EncryptedUser may test input for answer, Tests input for incorrect
      // answer and outputs error message.
      private void calcButtonAction(String expression, SectionEditor uEditor, SectionEditor lEditor) {
            // Parses the expresssion
            parser = new DijkstraParser(expression);

            if (parser.isInvalidInput()) {
                  // If there is an error.
                  String style = "-fx-font-family: Arial; -fx-font-size: 15px; -fx-font-weight: 600;";
                  // Insert a temporary error message into the
                  // text area
                  String msg01;
                  String error = parser.getErrorStr();
                  if(parser.isLetterError()) {
                        msg01 = "I cannot solve for " + error;
                  } else {
                        msg01 = parser.getErrorMessage();
                  }

                  Text errorTxt = new Text(error);
                  errorTxt.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: 800;");
                  errorTxt.setFill(Color.RED);
                  FMAlerts alert = new FMAlerts();

                  int start = (parser.getErrorStrStartIdx());
                  // subtract one from start;
                  start--;
                  // Prevent less than 0 for the 1st element problem.
                  start = Math.max(start, 0);
                  String firstStr = expression.substring(0, start);
                  Text firstTxt = new Text(firstStr);
                  int end = parser.getErrorStrEndIdx();
                  // add one to the end
                  end++;
                  //end = end < expression.length() ? end : expression.length() - 1;
                  Text secondTxt;
                  if(end < expression.length() - 1) {
                        String second = expression.substring(end);
                        secondTxt = new Text(second);
                  } else {
                        secondTxt = new Text();
                  }
                  firstTxt.setStyle(style);
                  secondTxt.setStyle(style);
                  HBox expBox = new HBox(2);
                  expBox.getChildren().addAll(firstTxt, errorTxt, secondTxt);
                  FMAlerts alerts = new FMAlerts();
                  VBox box = alerts.alertPane(msg01, expBox);
                  alert.mathErrorAlert("Format Error", box);
            } else {
                  StringBuilder sb = new StringBuilder(lEditor.getText());
                  sb.append("\nresult = ");
                  sb.append(parser.getResult());
                  sb.append("\n");
                  lEditor.setText(sb.toString());
            }
            parser.clearErrors();
      }

      private String errorExpression(String expression, String errorStr) {
            return expression.replace(errorStr, " <b>" + errorStr + "<\\b> ");
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
            upperStack.setAlignment(Pos.TOP_LEFT);

            HBox uHBox = genSection.sectionFactory("", cc.getQType(), 2, true, 0, cc.getQFiles());
            HBox iq = interActiveQuestion(DijkstraParser.getWriterList());
            iq.setMaxWidth(ReadFlash.getInstance().getMasterBPane().getWidth() - 100);
            iq.setPadding(new Insets(15));
            upperStack.getChildren().addAll(uHBox, iq);
            upperHBox.getChildren().clear();
            upperHBox.getChildren().add(upperStack);
            upperHBox.setStyle("-fx-background-color: WHITE");
            // the lower section containing the list section that
            // the user's mouse is hovering over.

            vBox.getChildren().add(new TextField("Hover over the sub-answer"));
            vBox.getChildren().add(getResponseBox());
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
       *
       * @return
       */
      protected VBox getAnswerBox() {
            HBox box = new HBox(2);
            userAnsField = new TextField();
            userAnsField.setEditable(true);
            userAnsField.setPrefColumnCount(20);
            box.getChildren().addAll(userAnsField, selectAnsButton);

            box.setPadding(new Insets(80, 0, 0, 0));
            VBox vB = new VBox(2);
            vB.getChildren().add(box);

            return vB;
      }

      /* ------------------------------------------------- **/

      // Used by lambda in getResponseBox
      private HBox expHBox = new HBox();
      private int prevIdx = 0;
      private final Line line = new Line();

      //Rectangle rect = new Rectangle(18 , 22);
      ArrayList<TextField> textFAry;

      /**
       * Provides the interactive step by step evaluation of the math
       * problem that is set in the lower pane.
       *
       * @return returns a ScrollPane with an HBox containing the evaluations
       * in ordered form.
       */
      private ScrollPane getResponseBox() {
            VBox vBox = new VBox(2);
            vBox.setStyle("-fx-background-color: WHITE");
            ScrollPane scrollPane = new ScrollPane();

            while ( ! Operator.hasOperators()) {
                  // expression node
                  ExpNode exp = Operator.poll();
                  TextField tf = new TextField(exp.getExpSolved());
                  tf.setStyle("-fx-border-color: TRANSPARENT; -fx-border-width: 2px;-fx-background-color: TRANSPARENT; -fx-text-fill: BLACK");
                  tf.setEditable(false);
                  //tf.setMaxWidth(exp.getExpSolved().length() + 4);
                  tf.setOnMouseEntered(e -> {
                        tf.setStyle("-fx-border-color:" + UIColors.HIGHLIGHT_ORANGE + "; -fx-border-width: 2px;-fx-background-color: TRANSPARENT; -fx-text-fill: BLACK");
                        setIActiveStyle(prevIdx, exp.getIndex());
                        prevIdx = exp.getIndex();
                  });
                  tf.setOnMouseExited(e -> {
                        mouseExitedExpBox();
                        tf.setStyle("-fx-border-color: TRANSPARENT; -fx-border-width: 2px;-fx-background-color: TRANSPARENT; -fx-text-fill: BLACK");
                  });
                  vBox.getChildren().add(tf);
            }


            scrollPane.setContent(vBox);
            scrollPane.setStyle("-fx-background-color: WHITE");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToWidth(true);
            return scrollPane;
      }

      /* --------------------------------------------------------------------
       *                           INTERACTIVE QUESTION
       * --------------------------------------------------------------------- */


      /**
       * Shows the Interactive Circles  when the users response is answered incorrectly. It
       * adds a circle/rectangle over the operator when the EncryptedUser selects an answer part in
       * the response box.
       *
       * @param prevIdx
       * @param expIdx
       */
      private void setIActiveStyle(int prevIdx, int expIdx) {
            interActiveQPane.getChildren().remove(line);

            line.setStroke(Color.web(UIColors.HIGHLIGHT_ORANGE));
            line.setStrokeWidth(4);
            TextField tef = textFAry.get(expIdx);
            double startX = tef.getLayoutX();
            double endX = startX + tef.getWidth();
            double y = tef.getLayoutY() + (tef.getHeight() + 2);
            line.setStartX(startX);
            line.setEndX(endX);
            line.setStartY(y);//.setCenterY(y);
            line.setEndY(y);
            // Set the line in the userPane. The problem UI field.
            interActiveQPane.getChildren().add(line);

      }

      /* ------------------------------------------------- **/

      private void mouseExitedExpBox() {
            interActiveQPane.getChildren().remove(line);
      }

      /* ------------------------------------------------- **/

      private HBox interActiveQuestion(ArrayList expList) {
            // expression HBox
            expHBox = new HBox();
            textFAry = new ArrayList<>(10);
            DecimalFormat f = new DecimalFormat("#.####");

            int length;
            for (int i = 0; i < expList.size(); i++) {
                  Object ob = expList.get(i);
                  // if object is a double
                  TextField tf;
                  String s;
                  if (ob.getClass().isInstance(1.1)) {
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
