package type.tools.calculator;

import flashmonkey.FMToolTip;
import flashmonkey.FlashMonkeyMain;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import uicontrols.FxNotify;
import uicontrols.UIColors;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.stream.Stream;

/**
 * This class provides the Graph background with Axis, numbers and horizontal and vertical lines.
 * The inner private static class extends Pane.
 */
public class Graph {

      // For number of grid lines
      private static int countX;
      private static int countY;
      // For X & Y axis lines
      private static double centerX;
      private static double centerY;
      private static double startXPt;
      private static double endXPt;


      //private DijkstraParser djParser;
      // Invalid flag is set when DijkstraParser has been given
      // invalid input. When this flag is true, show a
      // message to the EncryptedUser.
      //private boolean invalidFlag;
      //private String message;

      /**
       * Full constructor. Sets the center of the pane
       *
       * @param centX ..
       * @param centY ..
       */
      public Graph(double centX, double centY) {

            centerX = centX;
            centerY = centY;
            //this.djParser = new DijkstraParser();
      }

      /**
       * Sets the centerX of the pane
       *
       * @param centX ..
       */
      public void setCenterX(double centX) {
            centerX = centX;
      }

      /**
       * Sets the center Y of the pane.
       *
       * @param centY ..
       */
      public void setCenterY(double centY) {
            centerY = centY;
      }


      /*******************************************************************************************************************
       *                                                                                                                 *
       *                                                  GRID AND AXIS                                                  *
       *                                                                                                                 *
       ******************************************************************************************************************/

      public static Pane createGraphPane(Pane parentPane, DoubleProperty wdProperty, DoubleProperty htProperty) {

            FMGraphPane graphPane = new FMGraphPane();
            //FunctionPane fPane = new FunctionPane();
            graphPane.setStyle("-fx-background-color:" + UIColors.FM_GREY);
            parentPane.widthProperty().addListener((obs, oldval, newVal) -> {
                  graphPane.getChildren().clear();
                  wdProperty.setValue(newVal.doubleValue());
                  graphPane.createGrid(wdProperty, htProperty);
                  graphPane.createXYAxis(wdProperty, htProperty);
                  graphPane.axisNums(wdProperty, htProperty);
            });
            parentPane.heightProperty().addListener((obs, oldval, newVal) -> {
                  graphPane.getChildren().clear();
                  // subtract height for user adjustment box
                  htProperty.setValue(newVal.doubleValue() - 30);
                  graphPane.createGrid(wdProperty, htProperty);
                  graphPane.createXYAxis(wdProperty, htProperty);
                  graphPane.axisNums(wdProperty, htProperty);
            });

            htProperty.setValue(htProperty.getValue() - 30);
            graphPane.createGrid(wdProperty, htProperty);
            graphPane.createXYAxis(wdProperty, htProperty);
            graphPane.axisNums(wdProperty, htProperty);

            return graphPane;
      }

      public static Point2D getOriginXY() {
            return FMGraphPane.originXY;
      }

      /**
       * The graphs high X and high Y.
       *
       * @return getX() returns the highX for the graph. Same for Y.
       */
      public static Point2D getXHighLow() {
            return FMGraphPane.xHighLow;
      }

      /**
       * The graphs low x and low y
       *
       * @return getX() returns the low x for the graph, same for y
       */
      public static Point2D getYHighLow() {
            return FMGraphPane.yHighLow;
      }

      public static Point2D getFunctionXStartEnd() {
            return FMGraphPane.funcXStartEnd;
      }


      /**
       * Calculates the Origin, the graph lines, and the x & y axis.
       * Extends Pane.
       */
      private static class FMGraphPane extends Pane {
            Color axisColor = Color.BLACK;
            Color graphLines = Color.web(UIColors.GRAPH_BGND);
            // The origin of the graph
            static Point2D originXY;// = new Point2D(0,0);
            // getX() returns high, getY() returns low
            static Point2D xHighLow;
            // getX() returns high, getY() returns low
            static Point2D yHighLow;
            // getX() returns high, getY() returns low
            static Point2D funcXStartEnd;

            /**
             * Call to Create the graphPane and the x and y axis
             *
             * @param wdProperty
             * @param htProperty
             */
            private void createXYAxis(DoubleProperty wdProperty, DoubleProperty htProperty) {

                  double ht = htProperty.getValue();
                  double wd = wdProperty.getValue();

                  // if user has set centerY and centerX
                  double originY = centerY <= 0.0 ? ht / 2 : centerY;
                  double originX = centerX <= 0.0 ? wd / 2 : centerX;
                  originXY = new Point2D(originX, originY);

                  Line xAxis = new Line(0, originY, wd, originY);
                  xAxis.setStroke(axisColor);
                  Line yAxis = new Line(originX, 0, originX, ht);
                  yAxis.setStroke(axisColor);

                  this.getChildren().addAll(xAxis, yAxis);
            }

            /**
             * Creates the grid lines parallel to x and y axis
             *
             * @param htProperty height
             * @param wdProperty wd
             * @return A pane contianing the grid
             */
            private void createGrid(DoubleProperty wdProperty, DoubleProperty htProperty) {

                  double ht = htProperty.getValue();
                  //ht = ht <= 0.0 ? startHt : ht;
                  double wd = wdProperty.getValue();
                  //wd = wd <= 0.0 ? startHt : wd;

                  //System.err.println("*!*!*!* called createGrid() *!*!*!*");

                  countX = 0;
                  countY = 0;
                  // Zero is 1/2 of remainder for x and y
                  int zeroWd = (int) (wd % 40) / 2;
                  int zeroHt = (int) (ht % 40) / 2;

                  // vertical grid lines
                  for (int i = zeroWd; i < wd + 1; i += 20) {
                        Line vertL = new Line(i, 0, i, ht);
                        vertL.setStrokeWidth(.25);
                        vertL.setStroke(graphLines);
                        this.getChildren().add(vertL);
                        countX++;
                  }
                  // horizontal grid lines
                  for (int i = zeroHt; i < ht + 1; i += 20) {
                        Line horzL = new Line(0, i, wd, i);
                        horzL.setStrokeWidth(.25);
                        horzL.setStroke(graphLines);
                        this.getChildren().add(horzL);
                        countY++;
                  }
            }

            /**
             * rovides the pane with nums
             *
             * @param wdProperty wd
             * @param htProperty ht
             */
            private void axisNums(DoubleProperty wdProperty, DoubleProperty htProperty) {

                  double ht = htProperty.getValue();
                  double wd = wdProperty.getValue();

                  double zeroY = originXY.getY();
                  double zeroX = originXY.getX();

                  double yPixel, yPixStart;
                  double xPixel, xPixStart;
                  yPixel = yPixStart = (int) (ht % 40) / 2;
                  xPixel = xPixStart = (int) (wd % 40) / 2;
                  yPixel += 9.5;
                  //xPixel += ;

                  /*   Y Axis Count  **/
                  // Count down from ( countY - 1/2 ), a positive number,
                  // to ( 0 - 1/2 of countY ) a negitive number.
                  int iStart = countY / 2; // upper Y or 0
                  int iEnd = 0 - iStart; // lower Y
                  // provides the x low and high. This is correct!
                  //                xHighLow = new Point2D(iStart, iEnd);
                  // Provides nums along y axis
                  for (int i = iStart; i >= iEnd; i--) {
                        Text number = new Text(zeroX + 3, yPixel, Integer.toString(i));
                        number.setId("graphNumber");
                        //number.setFont(Font.loadFont("File:font/Raleway-Medium.ttf", 16));
                        this.getChildren().add(number);
                        yPixel += 20;
                  }

                  /* X Axis Count **/
                  // Count from the left to right for X numbers
                  // from 0 - countX / 2 (a negative number, to countX / 2 (A positive number)
                  iStart = 0 - countX / 2;
                  iEnd = countX / 2;
                  funcXStartEnd = new Point2D(iStart, iEnd);


                  // Provides nums along X axis
                  for (int i = iStart; i < iEnd + 1; i++) {
                        if (i != 0) {
                              Text number = new Text(xPixel, zeroY + 10, Integer.toString(i));
                              number.setId("graphNumber");
                              this.getChildren().add(number);
                        }
                        xPixel += 20;
                  }
                  yHighLow = new Point2D(yPixel, yPixStart);
                  xHighLow = new Point2D(xPixel, xPixStart);
            }
      }

//    private static class FunctionPane extends Pane {
//
//        private static DijkstraParser parser;
//
//        private void functionPathActions(String exp, Point2D originXY, Color color, DoubleProperty wdProperty, DoubleProperty htProperty) {
//            SVGPath path = new SVGPath();
//            // clip the graph edges to prevent any bleed over
//            double ht = htProperty.getValue();
//            double wd = wdProperty.getValue();
//            Rectangle clip = new Rectangle(wd, ht);
//
//            FMToolTip fmTip = new FMToolTip(this);
//            path.setOnMouseEntered(e -> {
//                fmTip.getFMTooltip(e, exp, color);
//                fmTip.show();
//            });
//            path.setOnMouseExited(e -> {
//                fmTip.remove();
//            });
//
//            this.setOnMouseMoved(e -> {
//                try {
//                    fmTip.update(e, path);
//                } catch (IndexOutOfBoundsException f) {
//                    // do nothing
//                }
//            });
//
//            path.setFill(Color.TRANSPARENT);
//            // The individual color for the line
//            path.setStroke(color);
//            path.setContent(graphFunction(exp, getFunctionXStartEnd().getX(), (int) getFunctionXStartEnd().getY(), getYHighLow().getY(), getYHighLow().getX(), originXY));
//            this.getChildren().add(path);
//
//
//            this.setClip(clip);
//
//        }
//
//
//        /**
//         * Creates an svgPath string from a "y =" function.
//         * Expects that y = is not included in the expression.
//         * Executes y = x as per expression. X begins at xStart and
//         * ends at xEnd. Calculations beyond yLowLim or yHighLim are
//         * excluded. YlowLim and yHighLim are the high and low boundaries.
//         * Low being the top or 0  and high being the bottom or 300 by default.
//         * Boundaries are the height and width in pixels divided by 10 or the height
//         * in pixels between each tickmark on the x and y axis.
//         * <p>
//         * convert to a parallel method using memoization and Futures. See Parallel programming 2.1 and 2.2 in particular
//         * Blase Pascal's triangle. Memoization is particularly used in Functional Programming where F(X) is a function. The
//         * problem being that in a sequential program, sequential operation are not an issue, however in parallel programming,
//         * where a mathematical problem is broken into batches the problem is not so simple. Using FUTURES, the problem then
//         * is stored, assuming there is a data structure available, in the data structure.
//         * The problem is not executed until it is called on by FUTURES.GET. Then the tasks are executed recursively in parallel.
//         *
//         * @param expression The expression of the line.
//         * @param xStart     The graph's start x numerical from left to right or negative to positive if applies.
//         * @param xEnd       The graph's end X
//         * @param yLowLim    upper pane boundary, 0 by default
//         * @param yHighLim   Lower pane boundary, 300 by default
//         * @return
//         */
//        private String graphFunction(String expression, IntegerProperty xStart, IntegerProperty xEnd, DoubleProperty yLowLim, DoubleProperty yHighLim, Point2D originXY) {
//
//            System.out.println(" *!*!* IN graphFunction() !*!*!");
//            // offsets to adjust line to graph
//            int xOffset = -4;
//            int yOffset = -14;
//            // clip the line offset
//            int upOffset = -100;
//
//            System.out.println("\n\nxStart " + xStart + ", xEnd " + xEnd + "\n\n");
//            System.out.println("\n\nyLowLim " + yLowLim + " yHighLim " + yHighLim + " \n\n");
//
//            double r = xEnd.getValue() - xStart.getValue();
//            int length = 5 * (int) r;
//            System.out.println(" length: " + length);
//
//            double y;
//            double x = xStart.getValue();
//
//            // the converted coordinates
//            double xCoord;
//            double yCoord;
//
//            // Builds the string used to build the SVG.
//            StringBuilder sb = new StringBuilder();
//
//            sb.append("M");
//            double origX = originXY.getX();
//            double origY = originXY.getY() / 2;
//
//            for (int i = xStart.getValue(); i < length; i++) {
//
//                int index = 0;
//                x += .2;
//                // Evaluate the expression and multiply
//                // the result by -1. (display is opposite)
//                y = evaluate(expression, x) * -1;
//
//                yCoord = (y * 20) + origY + yOffset;
//                xCoord = (x * 20) + origX + xOffset;
//
//                // If the line goes out of bounds. Stop graphing it
//                // and start over with a new line when it re-enters
//                if (yCoord < yHighLim.getValue() && yCoord > yLowLim.getValue() + upOffset) {
//                    //        if (yCoord < 10000 && yCoord > -10000) {
//                    sb.append(xCoord + " " + yCoord);
//
//                    if (i < length - 1) {
//                        sb.append(", L");
//                    }
//                } else if (sb.toString().charAt(sb.length() - 1) == 'L') {
//                    index = sb.lastIndexOf("L");
//
//                    sb.replace(index, index + 1, "M");
//                } else {
//                    System.out.println(" out of bounds Y " + y + ",  yCoord: " + yCoord);
//                }
//            }
//            // For the hanging 'M' added but without a point.
//            // Prevents errors
//            if (sb.toString().charAt(sb.length() - 1) == 'M') {
//                int index = sb.lastIndexOf("M");
//                sb.replace(index, index + 1, "");
//            }
//
//            return sb.toString();
//        }
//
//        /**
//         * Helper to graphFunction, evaluates an expression and replaces
//         * "x" in the expression with the parameter x
//         * for this iteration.
//         *
//         * @param expression
//         * @param x
//         * @return
//         */
//        private static double evaluate(String expression, double x) {
//
//            ArrayList subExpList;
//
//            try {
//                subExpList = parse(expression, x);
//                parser.parseIntoRPN(subExpList);
//            } catch (Exception e) {
//                // Handle operator bad input exception in GraphCard line 401
//            }
//
//            if (parser.isInvalidInput()) {
//
//                System.out.println(" Invalid input: " + parser.isInvalidInput());
//                // Insert a temporary error message into the
//                // text area
//                StringBuilder sb = new StringBuilder();
//                sb.append(expression);
//                sb.append("\n\n " + parser.getErrorMessage());
//
//                FxNotify.notificationDark("What !?!", sb.toString(), Pos.CENTER, 20,
//                        "emojis/Flash_headexplosion_60.png", FlashMonkeyMain.getWindow());
//
//                return 0;
//            } else {
//                return parser.execute(DijkstraParser.getOutQueue());
//            }
//        }
//
//        /**
//         * A Graph Specific method. Replaces DijkstraParser.parseIntoRPN()
//         * This method parses the expression into sub-expressions and replaces the value for x
//         * with the value in arguement "x".
//         *
//         * @param express The string expression
//         * @param x       the value for "x"
//         * @return returns an ArrayList of elements
//         */
//        private static ArrayList parse(String express, double x) throws Exception {
//
//            // Remove "y =" from the expression
//            String expMinus = express.substring(3);
//            expMinus = expMinus.trim(); // clean whitespace from start
//            String element;
//            String[] elements = expMinus.split(" ");
//            ArrayList subExpList = new ArrayList(10);
//            String strX = "";
//            String strY = "";
//
//            // Evaluate each sub-element and determine if it's an "x", an
//            // operator, or a double.
//            for (int i = 0; i < elements.length; i++) {
//                element = elements[i];
//                // Expression length,
//                // used to determine if number or op
//                int length = element.length();
//                char c;
//
//                // classify the element as a number or operator
//                // and add it to the subExpList.
//
//                // Check 2nd char in case it's a negative number
//                if (length > 1) {
//                    c = element.charAt(length - 1);
//                } else {
//                    c = element.charAt(0);
//                }
//
//                // Classify, seperate, and add to subExpList
//                if (Character.isDigit(c)) {
//                    double num = Double.parseDouble(element);
//                    subExpList.add(num);
//
//                } else if (c == 'x') {
//
//                    if (element.charAt(0) == '-') {
//                        subExpList.add(-x);
//
//                    } else {
//
//                        subExpList.add(x);
//                    }
//
//                } else { // it's an operator
//
//                    OperatorInterface operator = DijkstraParser.getOperator(element);
//
//                    if (i < 0) {
//                        System.err.println("ERROR: There are not enough numbers for the " + operator.getSymbol() +
//                                " operation. \nCheck the format of the expression.");
//                        if (operator.getPriority() != 0) {
//                            throw new EmptyStackException();
//                        }
//                    }
//
//                    if (operator.isUnaryOp()) { // sqrt, abs,
//                        // for printing values prior to execution
//                        strX = elements[i + 1];
//                    } else if (i > 1 && i < elements.length - 1) {
//                        // for printing values prior to execution
//                        strX = elements[i - 1];
//                        strY = elements[i + 1];
//                    } else {
//                        if (operator.getPriority() != 0) {
//                            //                        throw new EmptyStackException();
//                        }
//                    }
//
//                    // for display when answered incorrectly
//                    String strOrigSubExp = parser.getStrExpr(strX, strY, operator);
//                    ExpNode exp = new ExpNode(operator, strOrigSubExp, i);
//
//                    // add the expression to the sub-expession list
//                    subExpList.add(exp);
//                }
//            }
//            return subExpList;
//        }
//    }

}
