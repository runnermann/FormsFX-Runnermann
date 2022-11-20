package type.testtypes;

import flashmonkey.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.apache.http.MethodNotSupportedException;
import type.cardtypes.GenericCard;
import type.celleditors.SectionEditor;
import type.tools.calculator.DijkstraParser;
import type.tools.calculator.ExpNode;
import type.tools.calculator.Graph;
import type.tools.calculator.OperatorInterface;
import uicontrols.ButtoniKon;
import uicontrols.FxNotify;
import uicontrols.SceneCntl;
import uicontrols.UIColors;

import java.awt.*;
import java.util.ArrayList;
import java.util.EmptyStackException;


// scilab jlatex math
//import org.scilab.forge.jlatexmath.TeXConstants;
//import org.scilab.forge.jlatexmath.TeXFormula;
//import org.scilab.forge.jlatexmath.TeXIcon;


/**
 * - To add additional operators to this class's capabilities,
 * add them in the appropriate class, or the Operator class.
 * Add them as an enum. Include it's type in the getOperator() switch.
 * <p>
 * //@todo Replace methods that are not parallelable and use ParallelStream for 4x effiency
 * <p>
 * example:
 * String[] myArray = { "this", "is", "a", "sentence" };
 * String result = Arrays.ParallelStream(myArray)
 * .reduce("", (a,b) lamda a + b);
 * see https://www.sitepoint.com/java-8-streams-filter-map-reduce/ and  rice university parallel processing
 * <p>
 * Mutable reduction @ https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Reduction
 * <p>
 * * Mutable reduction
 * * A mutable reduction operation accumulates input elements into a mutable result container,
 * * such as a Collection or StringBuilder, as it processes the elements in the stream.
 * *
 * * If we wanted to take a stream of strings and concatenate them into a single long string, we could achieve this with ordinary reduction:
 * *
 * * String concatenated = strings.reduce("", String::concat)
 *
 * @author Lowell Stadelman
 */
public class GraphCard extends TestTypeBase implements GenericTestType<GraphCard> //extends GraphCard {
{
      private static GraphCard CLASS_INSTANCE;

      private static int graphXLow;
      private static int graphXHigh;
      private static int graphYLow;
      private static int graphYHigh;
      private static Graph graphPane;
      protected String expression;
      private static DijkstraParser parser;
      //private static SectionEditor upperEditor;
      private final Button calcButton = new Button("Calc");

      private final Color[] colors = {Color.BLUEVIOLET, Color.FUCHSIA, Color.GREENYELLOW, Color.DEEPSKYBLUE, Color.MEDIUMPURPLE,
          Color.ORANGERED, Color.CRIMSON, Color.ALICEBLUE, Color.CYAN, Color.DEEPPINK};

      //private static final double START_HT = 300;
      //private static final double START_WD = 400;

      //private final String FTCLatex = "\\int_{a}^{b} f'(x)dx = f(b) - f(a)";
      //private final String fafbLatexBase = "f(b) - f(a) = ";
      //private final String integralLatexBase = "\\int_{a}^{b} f'(x)dx = ";

      //private static DecimalFormat percentFormat = new DecimalFormat("0.#");
      //private static DecimalFormat coordinateFormat = new DecimalFormat("0.####");


      /*-------------------------------------------------------------**
       *                        CONSTRUCTORS                          *
       **------------------------------------------------------------**/


      private GraphCard() {
            setScore(2);
      }

      public static synchronized GraphCard getInstance() {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new GraphCard();
            }
            return CLASS_INSTANCE;
      }

      private void init() {
            int centerX = 150;
            int centerY = 100;
            parser = new DijkstraParser();
            graphPane = new Graph(0.0, 0.0);
           // upperEditor = new SectionEditor();
      }


      /*-------------------------------------------------------------**
       *                          PANES
       **------------------------------------------------------------**/


      /**
       * Builds the Test Editor View for this card. Contains the panes that are in
       * this view.
       *
       * @param flashList
       * @param p         The formula for the math/Algebra/Trig/Calc problem
       * @param r         The response when the EncryptedUser.EncryptedUser gets the answer wrong or correct.
       * @return Returns the Vbox with problem card and response card.
       */
      //   @Override
      public VBox getTEditorPane(ArrayList<FlashCardMM> flashList, SectionEditor p, SectionEditor r, Pane parentPane) {
            // the vBox that contains the editor sections
            VBox viewBox = new VBox(2);
            viewBox.setSpacing(2);
            StackPane gridStack = new StackPane();
            init();
            //upperEditor = p;
            double ht = SceneCntl.getAppBox().getHt() / 3;
            p.sectionHBox.setPrefHeight(ht);
            r.sectionHBox.setPrefHeight(ht);

            p.setPrompt("Function of the line ex:\n\t y = x ^ 2  \n\t y = 1 + x ^ 3 \n up to 10 functions will be displayed");
            r.setPrompt("This area is displayed in the answer during Question and Answer \"FlashCard\" sessions");

            viewBox.getChildren().clear();

            DoubleProperty gPaneWdProperty = new SimpleDoubleProperty(parentPane.getBoundsInLocal().getWidth());
            DoubleProperty gPaneHtProperty = new SimpleDoubleProperty(parentPane.getBoundsInLocal().getHeight());

            // Set the graph lines and numbers to the stack
            // sets the ht & wd of graph
            VBox localVBox = new VBox();

            // bind with ? to a size the graphPane
            Pane graphP = Graph.createGraphPane(p.sectionHBox, gPaneWdProperty, gPaneHtProperty);
            gridStack.getChildren().add(graphP);


            viewBox.setSpacing(4);
            viewBox.setAlignment(Pos.CENTER);
            viewBox.getChildren().add(gridStack);
            viewBox.getChildren().add(userSettingsBox());
            viewBox.getChildren().addAll(p.sectionHBox, r.sectionHBox, calcButton);

            graphP.setOnMouseClicked((e) -> graphClickAction());
            // ------------old below------------ //


            calcButton.setOnAction(e -> {
                  expression = p.getText();
                  // creates grid, & line, and makes responsive,
                  // parentPane used for resizing
                  gridStack.getChildren().add(calcButtonAction(expression, parentPane, gPaneWdProperty, gPaneHtProperty, Graph.getOriginXY(), colors));
            });

            // LatexPane
            // parentVBox.getChildren().add(getLaTexPane());
            // parentVBox.getChildren().clear();


            return viewBox;
      }


      /**
       * The main card pane during testing
       *
       * @param cc
       * @param genCard
       * @return
       */
      //   @Override
      public GridPane getTReadPane(FlashCardMM cc, GenericCard genCard, Pane parentPane) {
            GridPane parentGrid = new GridPane();
            StackPane stack = new StackPane();
            GridPane localGPane = new GridPane();

            init();

            DoubleProperty wdProperty = new SimpleDoubleProperty(parentPane.getBoundsInLocal().getWidth());
            DoubleProperty htProperty = new SimpleDoubleProperty(parentPane.getBoundsInLocal().getHeight());

            // Set the graph lines and numbers to the stack
            // sets the ht & wd of graph
            stack.getChildren().add(Graph.createGraphPane(localGPane, wdProperty, htProperty));
            stack.setOnMouseClicked((e) -> graphClickAction());
            // Get the reference to the originXY

            localGPane.setVgap(4);
            localGPane.setAlignment(Pos.CENTER);
            Point2D originXY = Graph.getOriginXY();
            // The graph's boundaries
            graphXHigh = (int) Graph.getXHighLow().getX();
            graphXLow = (int) Graph.getYHighLow().getY();
            graphYHigh = (int) Graph.getYHighLow().getX();
            // the top of the graph
            graphYLow = (int) Graph.getYHighLow().getY();

            // display expressions on the graph
            expression = cc.getQText();
            String[] exps = splitExps(expression);
            for (int i = 0; i < exps.length; i++) {
                  stack.getChildren().add(createLinePane(localGPane, exps[i],
                      wdProperty, htProperty, originXY, colors[i]));
            }

            localGPane.addRow(0, stack);
            localGPane.addRow(1, userSettingsBox());
            parentGrid.getChildren().add(0, localGPane);

            return parentGrid;
      }


      @Override
      public boolean isDisabled() {
            return false;
      }



      /*------------------------------------------------------------**
       *                          SETTERS
       **------------------------------------------------------------**/

      public void setGraphXHigh(int high) {
            graphXHigh = high;
      }

      public void setGraphXLow(int low) {
            graphXLow = low;
      }

      /*------------------------------------------------------------**
       *                          GETTERS
       **------------------------------------------------------------**/

      /**
       * Sets 12th bit, or equal to  all other bits are 0
       *
       * @return bitSet. Used when parsing
       * cards from flashList
       * Not compatible ith Multi-Choice
       */
      @Override
      public int getTestType() {
            // 4096
            return 0b0001000000000000;
      }

      @Override
      public String getName() {
            return "Graph Card";
      }


      /**
       * Provides the card layout in a char.
       * layout: 'S' = single card layout,
       * 'D' = double horizontal card, and
       * 'd' = double vertical card
       *
       * @return
       */
      @Override
      public char getCardLayout() {
            return 'D';
      }


      @Override
      public GenericTestType getTest() {
            return new GraphCard();
      }

      /**
       * Returns the array of ansButtons, An array
       * should include the prevAnsButton and nextAnsButton (if they are needed)
       * and always should include the answerButton. Used
       * in placement in the south/bottom pane
       *
       * @return
       */
      @Override
      public Button[] getAnsButtons() {
            return new Button[0];
      }

      /**
       * The ReadFlash class expects this method to return the implementation
       * from the TestType for its answerButton. An answerButton should provide
       * the testTypes expected behavior, format changes, when the EncryptedUser
       * clicks on the AnswerButton. Include correct and incorrect behavior as
       * a minimum.
       */
      @Override
      public Button getAnsButton() {
            return null;
      }

      @Override
      public void changed() {
            ReadFlash.getInstance().isChanged();
      }

      @Override
      public void ansButtonAction() {
            changed();
            /* stub */
      }

      @Override
      public void prevAnsButtAction() {

      }

      @Override
      public void nextAnsButtAction() {

      }


      /**
       * Returns the Graph X High
       *
       * @return
       */
      public int getGraphXHigh() {
            return graphXHigh;
      }

      /**
       * Returns the Graph Low X
       *
       * @return
       */
      public int getGraphXLow() {
            return graphXLow;
      }


      /*** OTHERS ***/

      @Override
      public void reset() {
            //graphPane = buildGraph(200, 200, graphPane);
      }

      @Override
      public void resetSelectAnsButton() {
            //answerButton = ButtoniKon.getAnsSelect();
            throw new UnsupportedOperationException("This method is not implemented for GraphCard");
      }


      /*******************************************************************************************************************
       *                                                                                                                 *
       *                                            Graph Card Specific                                                  *
       *                                                                                                                 *
       ******************************************************************************************************************/


      //@Override
      //protected VBox getAnswerBox() {
      //    return new VBox(2);
      //}

      // Latex Math example -- REMOVE --
      private Pane getLaTexPane() {

            Pane latexPane = new Pane();

            String latex = "x = \\frac{ -b\\pm \\sqrt{-b^2 -4ac}}{2a}";
/*
        String latex = "\\begin{array}{l}";
        latex += "\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\";
        latex += "\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\";
        latex += "\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\\\";
        latex += "\\int_0^\\infty{x^{2n} e^{-a x^2}\\,dx} = \\frac{2n-1}{2a} \\int_0^\\infty{x^{2(n-1)} e^{-a x^2}\\,dx} = \\frac{(2n-1)!!}{2^{n+1}} \\sqrt{\\frac{\\pi}{a^{2n+1}}}\\\\";
        latex += "\\int_a^b{f(x)\\,dx} = (b - a) \\sum\\limits_{n = 1}^\\infty  {\\sum\\limits_{m = 1}^{2^n  - 1} {\\left( { - 1} \\right)^{m + 1} } } 2^{ - n} f(a + m\\left( {b - a} \\right)2^{-n} )\\\\";
        latex += "\\int_{-\\pi}^{\\pi} \\sin(\\alpha x) \\sin^n(\\beta x) dx = \\textstyle{\\left \\{ \\begin{array}{cc} (-1)^{(n+1)/2} (-1)^m \\frac{2 \\pi}{2^n} \\binom{n}{m} & n \\mbox{ odd},\\ \\alpha = \\beta (2m-n) \\\\ 0 & \\mbox{otherwise} \\\\ \\end{array} \\right .}\\\\";
        latex += "L = \\int_a^b \\sqrt{ \\left|\\sum_{i,j=1}^ng_{ij}(\\gamma(t))\\left(\\frac{d}{dt}x^i\\circ\\gamma(t)\\right)\\left(\\frac{d}{dt}x^j\\circ\\gamma(t)\\right)\\right|}\\,dt\\\\";
        latex += "\\begin{array}{rl} s &= \\int_a^b\\left\\|\\frac{d}{dt}\\vec{r}\\,(u(t),v(t))\\right\\|\\,dt \\\\ &= \\int_a^b \\sqrt{u'(t)^2\\,\\vec{r}_u\\cdot\\vec{r}_u + 2u'(t)v'(t)\\, \\vec{r}_u\\cdot\\vec{r}_v+ v'(t)^2\\,\\vec{r}_v\\cdot\\vec{r}_v}\\,\\,\\, dt. \\end{array}\\\\";
        latex += "\\end{array}";
*/

            //String latexStr = fafbLatexBase + coordinateFormat.format(3)+" - "+coordinateFormat.format(2)+" = "+coordinateFormat.format(1);
            //String integralLatex = integralLatexBase+coordinateFormat.format(integral);
            String latexStr = "x = \\frac{ -b\\pm \\sqrt{-b^2 -4ac}}{2a}";

            //       latexPane.getChildren().add(buildLatexImage(latexStr));
            latexPane.setMinHeight(100);
            latexPane.setMinWidth(300);
            latexPane.setStyle("-fx-background-color: WHITE");
            return latexPane;
      }

 /*   private static ImageView buildLatexImage(String latex) {
        TeXFormula formula = new TeXFormula(latex);
        //TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(20).build();

        //BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);


        //icon. paintIcon(null, , 0, 0);
        java.awt.Image awtImage = formula.createBufferedImage(TeXConstants.STYLE_TEXT, 24, java.awt.Color.BLACK, null);
        Image fxImage = SwingFXUtils.toFXImage((BufferedImage) awtImage, null);
        ImageView imageView = new ImageView(fxImage);

        return imageView;
    }

  */


      /**
       * The box with fields to set the graph view
       *
       * @return
       */
      private HBox userSettingsBox() {

            HBox box = new HBox(4);
            TextField xLow = new TextField();
            xLow.setPrefColumnCount(4);

            xLow.requestFocus();
            TextField xHigh = new TextField();
            xHigh.setPrefColumnCount(4);
            TextField yLow = new TextField();
            yLow.setPrefColumnCount(4);
            TextField yHigh = new TextField();
            yHigh.setPrefColumnCount(4);
            xLow.setId("graph_settings_border");
            xLow.setPromptText("xLow");
            xHigh.setId("graph_settings_border");
            xHigh.setPromptText("xHigh");
            yLow.setId("graph_settings_border");
            yLow.setPromptText("yLow");
            yHigh.setId("graph_settings_border");
            yHigh.setPromptText("yHigh");
            box.getChildren().clear();
            box.getChildren().addAll(xLow, xHigh, yLow, yHigh);
            return box;
      }


      /**
       * Splits a text area with multiple expressions into seperate expressions.
       * Searches for the new line or "\n".
       *
       * @param exps
       * @return
       */
      private String[] splitExps(String exps) {
            //reset();
            return exps.split("\n");
      }


      /*******************************************************************************************************************
       *                                                                                                                 *
       *                                            EXPRESSION CALC                                                      *
       *                                                                                                                 *
       ******************************************************************************************************************/

      /**
       * Recieves the expression and inserts it as a line into an existing graphView.
       *
       * @param parentPane The pane this line will be in
       * @param exp        The expression of the line ie y = x ^ 2
       * @param wdProperty ...
       * @param htProperty ...
       * @param originXY   The origin of the graph
       * @param color      The color for this line
       * @return A pane containing the lines created by the function
       */
      public static Pane createLinePane(Pane parentPane, String exp, DoubleProperty wdProperty, DoubleProperty htProperty, Point2D originXY, Color color) {

            // @TODO finish making line responsive.

            FunctionLine funcPane = new FunctionLine();
            funcPane.functionPathActions(exp, originXY, color, wdProperty, htProperty);
            parentPane.widthProperty().addListener((obs, oldval, newVal) -> {
                  funcPane.getChildren().clear();
                  wdProperty.setValue(newVal.doubleValue());
                  funcPane.functionPathActions(exp, originXY, color, wdProperty, htProperty);
            });
            parentPane.heightProperty().addListener((obs, oldval, newVal) -> {
                  funcPane.getChildren().clear();
                  htProperty.setValue(newVal.doubleValue());
                  funcPane.functionPathActions(exp, originXY, color, wdProperty, htProperty);
            });


            return funcPane;
      }


      private static class FunctionLine extends Pane {

            private void functionPathActions(String exp, Point2D originXY, Color color, DoubleProperty wdProperty, DoubleProperty htProperty) {
                  SVGPath path = new SVGPath();
                  // clip the graph edges to prevent any bleed over
                  double ht = htProperty.getValue();
                  double wd = wdProperty.getValue();
                  Rectangle clip = new Rectangle(wd, ht);

                  FMToolTip fmTip = new FMToolTip(this);
                  path.setOnMouseEntered(e -> {
                        fmTip.getFMTooltip(e, exp, color);
                        fmTip.show();
                  });
                  path.setOnMouseExited(e -> {
                        fmTip.remove();
                  });

                  this.setOnMouseMoved(e -> {
                        try {
                              fmTip.update(e, path);
                        } catch (IndexOutOfBoundsException f) {
                              // do nothing
                        }
                  });

                  path.setFill(Color.TRANSPARENT);
                  // The individual color for the line
                  path.setStroke(color);
                  path.setContent(graphFunction(exp, (int) Graph.getFunctionXStartEnd().getX(), (int) Graph.getFunctionXStartEnd().getY(), graphYLow, graphYHigh, originXY));
                  this.getChildren().add(path);


                  this.setClip(clip);
            }


            /**
             * Creates an svgPath string from a "y =" function.
             * Expects that y = is not included in the expression.
             * Executes y = x as per expression. X begins at xStart and
             * ends at xEnd. Calculations beyond yLowLim or yHighLim are
             * excluded. YlowLim and yHighLim are the high and low boundaries.
             * Low being the top or 0  and high being the bottom or 300 by default.
             * Boundaries are the height and width in pixels divided by 10 or the height
             * in pixels between each tickmark on the x and y axis.
             * <p>
             * convert to a parallel method using memoization and Futures. See Parallel programming 2.1 and 2.2 in particular
             * Blase Pascal's triangle. Memoization is particularly used in Functional Programming where F(X) is a function. The
             * problem being that in a sequential program, sequential operation are not an issue, however in parallel programming,
             * where a mathematical problem is broken into batches the problem is not so simple. Using FUTURES, the problem then
             * is stored, assuming there is a data structure available, in the data structure.
             * The problem is not executed until it is called on by FUTURES.GET. Then the tasks are executed recursively in parallel.
             *
             * @param expression The expression of the line.
             * @param xStart     The graph's start x numerical from left to right or negative to positive if applies.
             * @param xEnd       The graph's end X
             * @param yLowLim    upper pane boundary, 0 by default
             * @param yHighLim   Lower pane boundary, 300 by default
             * @return
             */
            private String graphFunction(String expression, int xStart, int xEnd, int yLowLim, int yHighLim, Point2D originXY) {
                  // offsets to adjust line to graph
                  int xOffset = -4;
                  int yOffset = -14;
                  // clip the line offset
                  int upOffset = -100;

                  //System.out.println("\n\nxStart " + xStart + ", xEnd " + xEnd + "\n\n");
                  //System.out.println("\n\nyLowLim " + yLowLim + " yHighLim " + yHighLim + " \n\n");

                  int length = xEnd - xStart;
                  length *= 5;
                  //System.out.println(" length: " + length);

                  double y;
                  double x = xStart;

                  // the converted coordinates
                  double xCoord;
                  double yCoord;

                  // Builds the string used to build the SVG.
                  StringBuilder sb = new StringBuilder();

                  sb.append("M");
                  double origX = originXY.getX();
                  double origY = originXY.getY() / 2;

                  for (int i = xStart; i < length; i++) {

                        int index = 0;
                        x += .2;
                        // Evaluate the expression and multiply
                        // the result by -1. (display is opposite)
                        y = evaluate(expression, x) * -1;

                        yCoord = (y * 20) + origY + yOffset;
                        xCoord = (x * 20) + origX + xOffset;

                        // If the line goes out of bounds. Stop graphing it
                        // and start over with a new line when it re-enters
                        if (yCoord < yHighLim && yCoord > yLowLim + upOffset) {
                              //        if (yCoord < 10000 && yCoord > -10000) {
                              sb.append(xCoord + " " + yCoord);

                              if (i < length - 1) {
                                    sb.append(", L");
                              }
                        } else if (sb.toString().charAt(sb.length() - 1) == 'L') {
                              index = sb.lastIndexOf("L");

                              sb.replace(index, index + 1, "M");
                        } else {
                              //System.out.println(" out of bounds Y " + y + ",  yCoord: " + yCoord);
                        }
                  }
                  // For the hanging 'M' added but without a point.
                  // Prevents errors
                  if (sb.toString().charAt(sb.length() - 1) == 'M') {
                        int index = sb.lastIndexOf("M");
                        sb.replace(index, index + 1, "");
                  }

                  return sb.toString();
            }

            /**
             * Helper to graphFunction, evaluates an expression and replaces
             * "x" in the expression with the parameter x
             * for this iteration.
             *
             * @param expression
             * @param x
             * @return
             */
            private static double evaluate(String expression, double x) {

                  ArrayList subExpList;

                  try {
                        subExpList = parse(expression, x);
                        parser.parseIntoRPN(subExpList);
                  } catch (Exception e) {
                        // Handle operator bad input exception in GraphCard line 401
                  }

                  if (DijkstraParser.isInvalidInput()) {
                        // Insert a temporary error message into the
                        // text area
                        StringBuilder sb = new StringBuilder();
                        sb.append(expression);
                        sb.append("\n\n " + parser.getErrorMessage());

                        FxNotify.notification("What !?!", sb.toString(), Pos.CENTER, 20,
                            "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getPrimaryWindow());

                        return 0;
                  } else {
                        return parser.execute(DijkstraParser.getOutQueue());
                  }
            }

            /**
             * A Graph Specific method. Replaces DijkstraParser.parseIntoRPN()
             * This method parses the expression into sub-expressions and replaces the value for x
             * with the value in arguement "x".
             *
             * @param express The string expression
             * @param x       the value for "x"
             * @return returns an ArrayList of elements
             */
            private static ArrayList parse(String express, double x) throws Exception {

                  // Remove "y =" from the expression
                  String expMinus = express.substring(3);
                  expMinus = expMinus.trim(); // clean whitespace from start
                  String element;
                  String[] elements = expMinus.split(" ");
                  ArrayList subExpList = new ArrayList(10);
                  String strX = "";
                  String strY = "";

                  // Evaluate each sub-element and determine if it's an "x", an
                  // operator, or a double.
                  for (int i = 0; i < elements.length; i++) {
                        element = elements[i];
                        // Expression length,
                        // used to determine if number or op
                        int length = element.length();
                        char c;

                        // classify the element as a number or operator
                        // and add it to the subExpList.

                        // Check 2nd char in case it's a negative number
                        if (length > 1) {
                              c = element.charAt(length - 1);
                        } else {
                              c = element.charAt(0);
                        }

                        // Classify, seperate, and add to subExpList
                        if (Character.isDigit(c)) {
                              double num = Double.parseDouble(element);
                              subExpList.add(num);

                        } else if (c == 'x') {

                              if (element.charAt(0) == '-') {
                                    subExpList.add(-x);

                              } else {

                                    subExpList.add(x);
                              }

                        } else { // it's an operator

                              OperatorInterface operator = DijkstraParser.getOperator(element);

                              if (i < 0) {
                                    System.err.println("ERROR: There are not enough numbers for the " + operator.getSymbol() +
                                        " operation. \nCheck the format of the expression.");
                                    if (operator.getPriority() != 0) {
                                          throw new EmptyStackException();
                                    }
                              }

                              if (operator.isUnaryOp()) { // sqrt, abs,
                                    // for printing values prior to execution
                                    strX = elements[i + 1];
                              } else if (i > 1 && i < elements.length - 1) {
                                    // for printing values prior to execution
                                    strX = elements[i - 1];
                                    strY = elements[i + 1];
                              } else {
                                    if (operator.getPriority() != 0) {
                                          //                        throw new EmptyStackException();
                                    }
                              }

                              // for display when answered incorrectly
                              String strOrigSubExp = parser.getStrExpr(strX, strY, operator);
                              ExpNode exp = new ExpNode(operator, strOrigSubExp, i);

                              // add the expression to the sub-expession list
                              subExpList.add(exp);
                        }
                  }
                  return subExpList;
            }
      }


      /*******************************************************************************************************************
       *                                                                                                                 *
       *                                                  BUTTON ACTIONS                                                 *
       *                                                                                                                 *
       ******************************************************************************************************************/


      /**
       * Create the table of x and y =
       *
       * @param expression
       */
      //@Override
      private Pane calcButtonAction(String expression, Pane parentPane, DoubleProperty wdProperty, DoubleProperty htProperty, Point2D originXY, Color[] colors) {
            Pane pane = new Pane();
            reset();
            // split multiple expressions
            String[] exps = splitExps(expression);
            // display expressions on the graph
            for (int i = 0; i < exps.length; i++) {
                  //Pane parentPane, String exp, DoubleProperty wdProperty, DoubleProperty htProperty, Point2D originXY, Color color)
                  pane.getChildren().add(createLinePane(parentPane, exps[i], wdProperty, htProperty, originXY, colors[i]));
            }
            return pane;
      }

      /**
       * Creates a large graph 3/4ths the screen size
       */
      private void graphClickAction() {

            LargeGraphPane lgPane = new LargeGraphPane();
            Stage graphStage = new Stage();
            // determin screen size and build graph to it's size minus the width of the main app.
            // make the pane panable and zoomable.

            //System.out.printf("*** graphClickAction called ***");

            lgPane.start(graphStage);
      }


      /**------------------------------------------------------------**
       *                        SUPPORT CLASSES
       **------------------------------------------------------------**/


      /**
       * Associates the Expression string with the line for display in pop-up
       */
    /*
    class LineObject {

        private SVGPath path;
        private String exp;
        private Tooltip tip;// = new Tooltip(lo.getExpression());

        LineObject( String expression) {
            this.path = new SVGPath();
            this.exp = expression;
            this.tip = new Tooltip(this.exp);

            //tooltipStartTiming(tip, 50);
            //tooltipHideTiming(tip, 6000);
            tip.setStyle("-fx-background-color: TRANSPARENT; -fx-text-fill: RGBA(255, 255, 255, .7);");

            path.setOnMouseEntered(e -> {

                Tooltip.install(this.path, this.tip);
                System.out.println("TOOLTIP: " + exp + " should show");
            });


        lo.getSVGPath().setOnMouseExited(e -> {
            tooltipHideTiming(tip, 4000);
            tooltipStartTiming(tip, 1000);
        });

        }

        SVGPath getSVGPath() {
            return this.path;
        }

        String getExpression() {
            return this.exp;
        }
    }
    */

      /** ------------------------------------------------- **/

      /**
       * INNER CLASS
       * Creates a large garph area 3/4 the size of the screen
       */
      public class LargeGraphPane extends Application {

            @Override
            public void start(Stage graphStage) {

                  Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
                  DoubleProperty htProperty = new SimpleDoubleProperty(screensize.getHeight() - 40);
                  double height = htProperty.getValue();

                  Graph largeGraph = new Graph(height, height);

                  // set dimensions of graphStage
                  graphStage.setWidth(height);
                  graphStage.setHeight(height);

                  // set pane
                  Pane pane = new Pane();

                  // split multiple expressions
                  String[] exps = splitExps(expression);
                  // display expressions on the graph
//            for(String e : exps) {
//                pane = createLinePane(
//                        pane,
//                        e,
//                        htProperty,
//                        htProperty);
//            }

                  Scene graphScene = new Scene(pane, height, height, Color.web(UIColors.FM_WHITE));
                  graphScene.getStylesheets().add("css/mainStyle.css");
                  graphStage.setScene(graphScene);
                  graphStage.show();
            }
      }


}
