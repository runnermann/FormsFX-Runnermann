package type.tools.calculator;


import flashmonkey.CreateFlash;
import org.slf4j.LoggerFactory;
import queue.ArrayQueue;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**********************************************************************************************************************
 *
 *
 * Parses an expression based on Edsgar W. Dijkstra's Shunting Yard
 * algorithm. The class prioritizes operators and sub-expressions ensuring that the
 * expression maintains PERMDAS order. Allows the EncryptedUser to use pipes for
 * Absolute Value when entering a string expression.
 *
 * *** NOTE:
 * !!! To print the problem for the EncryptedUser in a simple and understandable format, We first create an object for each element
 *  *   that can be referenced and display a result to the EncryptedUser. Otherwise if we printed the results in the order they
 *  *   are executed, although they are in left to right PERMDAS order, a 9 * 9 may not provide a good hint if there isn't
 *  *   a 9 * 9 in the original string. ie in the problem, 9 * 3 ^ 2 - 2 -{ 3 ^ 2 ( 5 + 1 ) } } / ...
 *  *   9 * 9 does not appear in the original string. Neither would 2 * -1 which is used to calculate the results of the
 *  *   2 -{ ...}. To create an easy to understand display when the EncryptedUser gets the answer wrong, due to ordering, or to
 *  *   help the EncryptedUser understand the order that is used to solve problems in this calculator, the expression is
 *  *   parsed into a expNode (Expression Node) to be used as a reference. The reference may be used later for displaying and creating
 *  *   behaviors that assist with understanding.
 *
 *  NOT THREAD SAFE.
 *
 * Algorithm:
 *   - Determine if the equation is balanced… based on parantheseis , brackets, and braces. Simply go through the
 *   expression and place opening enclosures on a stack, and closing enclosures remove opening enclosures.
 *
 *   When a string expression is read (parsed) from beginning to end it is fed onto a queue in PERMDAS order using
 *   Dijkstra's Shunting Yard algorithm to reorder the expression from in-Fix notation, to end-fix notation.
 *   As the expression is parsed, each token is either an operator or a number.
 *
 * -	If the token is a number, it is offered (placed) to the outQueue and waits for the next token
 * -	If the token is an operator,
 *      - It checks the operator on the top of the opStack.
 *          - If the operator on the top of the stack is higher than its precedence. The operator on the top of the
 *          opStack is pooped from the opStack and offered to the outQueue until it can be placed on the opStack.
 *          - If the operator is an open enclosure it is always placed on the opStack.
 *              - If it is a negitive enclosure, a -1 is offered to the outQueue and a multiply is pushed onto the
 *              opQueue with the same rules for precedence.
 *          - If the operator is a closing enclosure, it pops the operators from the opStack until it has pooped the
 *          opening enclosure
 *
 *  After the string is read, the outQueue is evaluated in postFix order.
 * - If an element of the outQueue is a number, it is placed on an evaluation stack.
 * - If an element is an operator, the evaluation stack is popped and the first number
 *   becomes Y and the second becomes X and the expression is evaluated in X then Y order.
 *   The result is pushed back onto the evaluation stack. The remaining number on the evaluation
 *   stack is the result.
 *
 *
 *      -	Set priority
 *          o	Immidiate = 3
 *          	Power = execute immidiatley –
 *              •	Hold any processing if this ref is needed until it’s finished
 *              •	Pop last token, set it to left integer
 *              •	Get next token, (maybe place it on the stack), remove it from the original string/structure
 *              •	Evaluate and place on the stack
 *          	Rigth brackets, paran, brace: " ) " or " ] " or " } " and pipe " | " if absBool is true.
 *              •	Create new thread, hold any processing if this ref is needed
 *              •	Execute operators from the top of the stack until the next left bracket, paran, brace " ( " or " [ " or " { "
 *          	Pipe “ | “ Absolute Value
 *              •	If parser encounters a pipe for Absolute Value it checks the absValueBool.
 *                  o	If it is true it evaluates the operators and values until it reaches the first AbsValue pipe.
 *                  And enters the evaluation result.
 *                  o	Set absValueBool to false
 *              •	Else it is false it sets absValueBool to true
 *          o   High = 2
 *              	Multiply, Divide, and modulus * / %
 *          o	Low = 1
 *              	Add and subtract + -
 *          o	Wild Stop
 *              	Left paran, bracket, brace " ( " or " [ " or " { "
 *                  •	As stated above, Acts as a stop for previous right brackets
 *                  •	When evaluated
 *                      o	If preceeded by an integer, it is multiplied
 *                      o	If preceeded by an operator, it defaults to the operator
 *                      o	If preceeded by a "-" it is multiplied by negative 1
 *              	1st Pipe / Absolute value left \" | \"
 *                  *   As stated above, acts as a stop for previous right pipe
 *
 **********************************************************************************************************************/


public class DijkstraParser {

      private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CreateFlash.class);
      //private static final Logger LOGGER = LoggerFactory.getLogger(CreateFlash.class);

      private double result;
      // Combination of numbers and ExpNodes
      private static ArrayQueue outQueue;
      // ExpNodes
      private static Stack<ExpNode> opStack;
      // Combination of numbers and ExpNodes
      private static ArrayList subExpList;
      // Message if input is illegal
      private static String message;
      // Flag for invalid input
      private static boolean invalidInputBool;
      // Size of subExpList
      private static int size;
      // The location, we guess of the idx of the
      // expression.
      private static int expressionIdx;
      // The string that is an error
      private static int errorEndIdx;
      private static int errorStartIdx;
      private static String errorStr;
      private static boolean letterErrorBool;


      /**
       * default constructor
       */
      public DijkstraParser() {
            outQueue = new ArrayQueue();
            opStack = new Stack<>();
            size = 0;
            //invalidInput = true;
      }

      public DijkstraParser(String expression) {
            outQueue = new ArrayQueue();
            subExpList = new ArrayList();
            opStack = new Stack<>();
            result = evaluate(expression);
            size = 0;
            //invalidInput = true;
      }

      /** GETTERS **/

      /**
       * @return The queue
       */
      public static ArrayQueue getOutQueue() {
            return outQueue;
      }

      /**
       * @return The stack
       */
      public static Stack<ExpNode> getOpStack() {
            return opStack;
      }

      /**
       * @return returns the result
       */
      public double getResult() {
            return this.result;
      }

      /**
       * @return returns the list
       */
      public static ArrayList getWriterList() {
            return subExpList;
      }

      public static int getListSize() {
            return size;
      }


      /** METHODS **/


      /**
       * Evaluates the String expression in the argument
       *
       * @param expression The expression expression to be evaluated
       * @return Returns the result
       */
      private double evaluate(String expression) {
            errorStr = "";
            try {
                  if (parseStrExpression(expression)) {
                        parseIntoRPN(subExpList, expression);
                        return execute(outQueue, expression);
                  }
            } catch (EmptyStackException e) {
                  invalidInputBool = true;
                  errorStr = errorStr.length() == 0 ? "ERROR!  " : errorStr;
                  System.err.println("Exception: Line 190 DijkstraParser");
                  //e.printStackTrace();
                  clearStructures();
            } catch (IllegalStateException e) {
                  invalidInputBool = true;
                  clearStructures();
                  errorStr = errorStr.length() == 0 ? "ERROR!  " : errorStr;
            } catch (Exception e) {
                  invalidInputBool = true;
                  //errorStr = "UNKNOWN ERROR: Exception in DijkstraParser.";
                  System.err.println("Exception: Line 197 DijkstraParser");
                  e.printStackTrace();

                  clearStructures();
            }
            return 0;
      }

      /**
       * Executes an endfix expression given in a queue
       *
       * @param outQ ..
       * @return returns the solution
       */
      public double execute(ArrayQueue outQ, String expression) throws EmptyStackException, Exception {
            Stack<Double> resultStack = new Stack<>();

            double x = 0;
            double y = 0;
            ExpNode exp = null;
            while ( !outQ.isEmpty()) {
                  try {
                        if (outQ.peek().getClass().isInstance(1.1)) {
                              // queue element is a number
                              resultStack.push((double) outQ.poll());
                        } else {
                              // queue element is an operator
                              if ( ! ((ExpNode) outQ.peek()).getOp().isUnaryOp() ) {
                                    exp = (ExpNode) outQ.poll();
                                    y = resultStack.pop();
                                    x = resultStack.pop();
                                    double result = exp.getOp().execute(exp, x, y);
                                    resultStack.push(result);
                              } else {
                                    x = resultStack.pop();
                                    exp = (ExpNode) outQ.poll();
                                    double result = exp.getOp().execute(exp, x, 0);
                                    resultStack.push(result);
                              }
                        }
                  } catch (Exception e) {
                        invalidInputBool = true;
                        int endIdx = exp.getExpressionStartIdx() + exp.getOp().getSymbol().length();
                        //endIdx = endIdx < expression.length() ? endIdx : expression.length() - 1;
                        errorStr = exp.getOp().getSymbol();
                        errorMessageIsBrack();
                        errorStartIdx = exp.getExpressionStartIdx();
                        errorEndIdx = endIdx;
                        return 0;
                  }
            }

            if (resultStack.size() == 1) {
                  return resultStack.pop();

            } else {
                  invalidInputBool = true;
                  errorStr = "UNKNOWN ERROR: IDK?";
                  message = "";
                  clearStructures();
                  System.err.println("\nERROR: DijkstraParser resultStack has more than one" +
                          "\n answer left in the stack \n");
                  return 99999999;
            }
      }

      /**
       * Parses the String expression, and
       * of String elements containing each element of the
       * expression.
       *
       * @param paramExpression The string expression
       * @return true if succeeded
       * @throws Exception if operator input is invalid.
       * @throws EmptyStackException if there is a problem with the order of the brackets, braces, etc...
       */
      public boolean parseStrExpression(String paramExpression) throws EmptyStackException, Exception {

            // To be informative to the user, we must guess where to insert the error.
            // This is a best effort and is thrown off by the user if there is extra white
            // space. We must guess, since we've split the strings using white spaces.
            // StrIdx represents the end of the string index counting the chars of the
            // element and the white space. Note that the index is incremented for
            // white space at the end of the loop so that the inserted strIdx is correct
            // for the param for operator and ExpNode.
            int strIdx = 0;
            // clean whitespace from start and extra white spaces. Causes a problem
            // with location of the error, but user friendly when we don't have errors
            // due to extra white spaces.
            String expression = paramExpression.trim();
            // clean repeated white spaces
            expression = expression.replaceAll("\\s+", " ");
            String element;
            String[] elements = expression.split(" ");

            for (int i = 0; i < elements.length; i++) {
                  element = elements[i];
                  // So we can show where the error occurred,
                  // prevent downstream array overrun errors.
                  int guardLength = paramExpression.length();

                  strIdx = strIdx < guardLength ? strIdx : guardLength - 1;
                  // Set the class variable
                  expressionIdx = strIdx;
                  // trim the element afterwards
                  // element.trim();
                  int length = element.length();
                  char c;
                  // classify the element as a number or operator
                  // and add it to the subExpList.
                  // - Check if it's a number and account for negative numbers
                  if (Character.isDigit(element.charAt(0)) || length > 1 && Character.isDigit(element.charAt(1)) ) {
                        try{
                              double num = Double.parseDouble(element);

                              subExpList.add(num);
                              System.out.println("subExpList.add: " + num);
                              size++;
                        } catch (NumberFormatException e) {

                              System.err.println("NumberFormatException. line 285. I'm still running.");

                              letterErrorBool = Utility.isAsciiLetter(element.charAt(0));
                              errorStr = "" + element;
                              // The element's start idx in the expression
                              errorStartIdx = strIdx;
                              // the element's end idx in the expression
                              errorEndIdx = strIdx + element.length();
                              invalidInputBool = true;
                              errorMessageIsFormating(element);
                              clearStructures();
                              return false;
                        }
                  } else { // it's an operator, or it will fail
                        c = element.charAt(0);

                        /*
                         * Add the binomial operator and its values (in an unexecuted state)
                         * as strings into the ExpNode expOrigin
                         */
                        String x = "";
                        String y = "";
                        OperatorInterface operator = getOperator(element);

                        if(invalidInputBool) {
                              clearStructures();
                              System.err.println("invalidInputBool was triggered. line 306" );
                              return false;
                        }

                        if (operator.getPriority() == 8) { // DEFAULT OPERATOR
                              System.err.println("parseStrExp is return false !!! line 311");
                              return false;
                        }
                        if (i < 0) {
                              System.err.println("ERROR: There are not enough numbers for the " + operator.getSymbol() +
                                      " operation. \nCheck the format of the expression. line 316");
                              if (operator.getPriority() != 0) {
                                    System.out.println("Throwing empty stack exception line 319 DijkstraParser");
                                    throw new EmptyStackException();
                              }
                        }
                        if (operator.isUnaryOp()) { // sqrt, abs,
                              // for printing values prior to execution
                              x = elements[i + 1];

                              System.out.println("op " + operator + "is unary, x: " + x);

                        } else if (i > 1 && i < elements.length - 1) {
                              // for printing values prior to execution
                              x = elements[i - 1];
                              y = elements[i + 1];
                        } else {
                              // keep going

                        }

                        // for display when answered incorrectly
                        String strOrigSubExp = operator.getStrExpr(x, y, operator);
                        ExpNode exp = new ExpNode(operator, strOrigSubExp, i);
                        exp.setExpressionEndIdx(strIdx);

                        // add the expression to the sub-expression list
                        subExpList.add(exp);

                        System.out.println("subExpList.add: " + exp);

                        size++;
                  }
                  // Increase the strIdx by element length
                  // and add 1 for white space.
                  strIdx += element.length() + 1;
            }
            return true; // The number of elements in subExpList
      }


      /**
       * Parses an arrayList of numbers and operators into RPN order.
       *
       * @param subExpressList ...
       * @throws Exception ..
       */
      public void parseIntoRPN(ArrayList subExpressList, String expression) throws EmptyStackException, IllegalStateException, Exception {

            // - Work on parsing larger doubles including other real numbers.
            System.out.println("\n\n ~*~*~ parseIntoRPN ~*~*~");

            outQueue.clear();
            opStack.clear();

            /*
             * SpecialCase! Set prevObject to + to prevent MULTIPLY from being added to the opStack
             * at the start.
             **/
            OperatorInterface operator = getOperator("+");
            ExpNode exp = new ExpNode(operator, "", 0);

            Object prevObject = exp;
            BracketError bracketError = new BracketError();

            for (int i = 0; i < subExpressList.size(); i++) {
                  Object ob = subExpressList.get(i);
                  // if object is a double
                  if (ob.getClass().isInstance(1.1)) {

                        outQueue.offer(ob);
                        // Set the prevObject to the number
                        prevObject = ob;
                  } else {
                        // Build the outQueue in RPN order using the Shunting yard algorithm.
                        // Calls methods and values from enums using the OperatorInterface. :)
                        exp = (ExpNode) ob;
                        operator = exp.getOp();
                        // The reOrdering call
                        operator.stackAction(prevObject, exp, outQueue, opStack, i);
                        if(operator.isCloseEnclosure() || operator.isOpenEnclosure()) {
                              bracketError.matchCheckNHandler(exp, expression);
                        }
                        // Set the prevObject to the operator
                        prevObject = exp;
                  }
            }
            bracketError.stackNotEmptyCheckNHandler(expression);
            while (!opStack.isEmpty()) {
                  outQueue.offer(opStack.pop());
            }
      }


      private class BracketError {

            private Stack<ExpNode> bracketStack;

            private BracketError() {
                  bracketStack = new Stack<>();
            }

            private void matchCheckNHandler(ExpNode expNode, String expression) throws EmptyStackException {
                  OperatorInterface op = expNode.getOp();
                  if (op.isOpenEnclosure()) {
                        bracketStack.push(expNode);
                  } else {
                        ExpNode stackNode = null;
                        // bracketStack not empty error
                        if(bracketStack.size() > 0) {
                              stackNode = bracketStack.pop();
                              if (!stackNode.getOp().isMatch(op.getSymbol().charAt(0))) {
                                    int startIdx = stackNode.getExpressionStartIdx();
                                    int endIdx = expNode.getIndex() + expNode.getOp().getSymbol().length();
                                    //endIdx =  endIdx < expression.length() ? endIdx : expression.length() - 1;
                                    errorHandler(expression, startIdx, endIdx);
                              }
                        } else {
                              // bracketStack is empty error
                              int startIdx = expNode.getExpressionStartIdx();
                              int endIdx = expNode.getExpressionStartIdx() + expNode.getOp().getSymbol().length();
                              errorHandler(expression, expNode.getExpressionStartIdx(), endIdx);
                              throw new EmptyStackException();
                        }
                  }
            }

            private void stackNotEmptyCheckNHandler(String expression) throws IllegalStateException {
                  if(bracketStack.size() > 0) {
                        // error!
                        ExpNode stackNode = bracketStack.pop();
                        int startIdx = stackNode.getExpressionStartIdx();
                        int endIdx = startIdx + expression.length();
                        errorHandler(expression, startIdx, endIdx);
                        throw new IllegalStateException("The bracketStack should be empty");
                  }
                  // else all is good
            }

            /**
             * Handle messaging for stack not empty and stack is empty errors.
             * @param expression The expression the user has given.
             * @param startIdx The error elements start idx of the Expression
             * @param endIdx The error elements end idx of the Expression
             */
            private void errorHandler(String expression, int startIdx, int endIdx) {
                  invalidInputBool = true;
                  endIdx = endIdx < expression.length() ? endIdx : expression.length() -1;
                  errorStr = expression.substring(startIdx, endIdx + 1);
                  errorMessageIsBrack();
                  errorStartIdx = startIdx;
                  errorEndIdx = endIdx;
                  clearStructures();
            }
      }

      /*
       * Flag is set when a pipe (absolute value symbol) is opened and indicates
       * that the next pipe is the closing pipe.
       * Explanation. Open and closing pipes are identical. A true flag indicates
       * that the openPipe has been processed and the next pipe is the closing
       * abs value operation.
       */
      private static boolean pipeOpen = false;

      /**
       * Returns the Operator object based on the string provided in
       * the parameter.
       *
       * @param stOperator ..
       * @return ..
       * @throws Exception ..
       */
      public OperatorInterface getOperator(String stOperator) throws Exception {

            /** Priority from 0 to 4. Higher number == higher priority **/
            invalidInputBool = false;

            switch (stOperator) {
                  case "–": // cut and paste from ms word.
                  case "-": {
                        return Operator.SUBTRACT; // priority 1
                  }
                  case "+": {
                        return Operator.ADD; // priority 1
                  }
                  case "*": {
                        return Operator.MULTIPLY; // priority 2
                  }
                  case "/": {
                        return Operator.DIVIDE; // priority 2
                  }
                  case "%": {
                        return Operator.MODULUS; // priority 2
                  }
                  case "^": {
                        return Operator.POWER; // priority 3
                  }
                  case "cos": {
                        return Operator.COS; // priority 2
                  }
                  case "acos": {
                        return Operator.ACOS;
                  }
                  case "cosh": {
                        return Operator.COSH;
                  }
                  case "sin": {
                        return Operator.SIN; // priority 2
                  }
                  case "asin": {
                        return Operator.ASIN;
                  }
                  case "sinh": {
                        return Operator.SINH;
                  }
                  case "tan": {
                        return Operator.TAN; // priority 2
                  }
                  case "tanh": {
                        return Operator.TANH;
                  }
                  case "atan": {
                        return Operator.ATAN;
                  }
                  case "atan2": {
                        return Operator.ATAN2;
                  }
                  case "sqrt": {
                        return Operator.SQRT; // priority 3
                  }
                  case "root": {
                        return Operator.ROOT; // priority 3
                  }
                  case "log": {
                        return Operator.LOG; // priority 3???
                  }
                  case "abs": {
                        return Operator.ABS;
                  }

                  /** --------------- Open enclosures --------------- **/

                  case "[": {
                        return Open_Enclosure.OPEN_BRACE; // priority 0
                  }
                  case "{": {
                        return Open_Enclosure.OPEN_BRACK; // priority 0
                  }
                  case "(": {
                        return Open_Enclosure.OPEN_PARAN; // priority 0
                  }
                  case "-[": {
                        return Open_Enclosure.OPEN_BRACE_NEG; // priority 0
                  }
                  case "-{": {
                        return Open_Enclosure.OPEN_BRACK_NEG; // priority 0
                  }
                  case "-(": {
                        return Open_Enclosure.OPEN_PARAN_NEG; // priority 0
                  }
                  case "-|": {
                        // -1 * abs value
                        // Set pipeOpen for the closing pipe.
                        // "-|" is always an "open_pipe_negitive"
                        pipeOpen = true;
                        return Open_Enclosure.OPEN_PIPE_NEG; // priority 0
                  }
                  // NOTE that '|' for an open pipe is handled in close enclosures.


                  /** --------------- close enclosures --------------- **/

                  case "]": {
                        return Close_Enclosure.CLOSE_BRACE; // priority 4
                  }
                  case "}": {
                        return Close_Enclosure.CLOSE_BRACK; // priority 4
                  }
                  case ")": {
                        return Close_Enclosure.CLOSE_PARAN; // priority 4
                  }
                  case "|": {
                        // Need to check how many pipes exist already since they all look the same
                        if (pipeOpen) {
                              pipeOpen = false;
                              return Close_Enclosure.CLOSE_PIPE; // priority 4
                        } else {
                              pipeOpen = true;
                              return Open_Enclosure.OPEN_PIPE;
                        }
                  }
                  default: {
                        // Default is filtered out in DijkstraParser parseStrExpression()
                        letterErrorBool = Utility.isAsciiLetter(stOperator.charAt(0));
                        invalidInputBool = true;
                        errorStr = stOperator;

                        errorStartIdx = expressionIdx;
                        errorEndIdx = expressionIdx + stOperator.length();
                        errorMessageIsFormating(stOperator);
                        clearStructures();
                        return null;
                  }
            }
      }

      private static void errorMessageIsFormating(String stOperator) {
            message = "ERROR: Input not recognized. \nEnsure there is a space between the operator and the" +
                    "\n value. \nEXAMPLE: { 2 * 4 -( 3 - 1 ) } / { | ( 4 - sqrt 9 ) - 2 | }"
                    + "\n\nPlease replace \"" + stOperator + "\" with valid input.";
      }

      private static void errorMessageIsBrack() {
            message = "ERROR: There is an error in bracket {}, brace [], parenthesis(), or Absolute Value Pipe || ordering";
      }

      /**
       * Returns an invalid input message set by getOperator()
       *
       * @return error message
       */
      public String getErrorMessage() {
            return message;
      }

      public int getErrorStrEndIdx() {
            return errorEndIdx;
      }

      public int getErrorStrStartIdx() {
            return errorStartIdx;
      }

      public String getErrorStr() {
            return errorStr;
      }

      public boolean isLetterError() {
            return letterErrorBool;
      }

      /**
       * Returns the invalidInput flag
       *
       * @return if invalid, true
       */
      public boolean isInvalidInput() {
            return invalidInputBool;
      }

      /**
       * Clears the data structures used in this
       * class. Call this method before beginning
       * a new operation
       */
      public void clearStructures() {

            if (!outQueue.isEmpty()) {
                  outQueue.clear();
            }

            if (!opStack.isEmpty()) {
                  opStack.clear();
            }

            if (!subExpList.isEmpty()) {
                  subExpList.clear();
            }
      }

      public void clearErrors() {
            errorStr = "";
            errorEndIdx = 0;
            errorStartIdx = 0;
      }

}
