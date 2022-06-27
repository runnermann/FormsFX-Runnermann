package type.tools.calculator;

import queue.ArrayQueue;

import java.text.DecimalFormat;
import java.util.Stack;

/**
 * The Operator class provides the actions for an operator based on Java's calculation
 * capabilities. The class creates the "Operator Stack" in Dijkstra's method. The class
 * holds the Answer components which are the Expression Nodes 'ExpNode' that is displayed
 * in WrongAnswer. The 'AnsStack' is isolated but may be accessed through the queue methods
 * contained in this class.
 */
public enum Operator implements OperatorInterface {


      /**
       * Enum assigning priority and execution to operators
       */


      // ------------- LOWEST priority -------------


      ADD("+", 1) {
            @Override
            public double execute(ExpNode exp, double x, double y) {
                  String res = formatToString("ADD: ", this.getSymbol(), x, y, (x + y));
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return x + y;
            }
      },
      SUBTRACT("-", 1) {
            @Override
            public double execute(ExpNode exp, double x, double y) {
                  String res = formatToString("SUBTRACT: ", this.getSymbol(), x, y, (x - y));
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return x - y;
            }
      },


      // ------------- MEDIUM priority -------------


      MULTIPLY("*", 2) {
            @Override
            public double execute(ExpNode exp, double x, double y) {
                  String res = formatToString("MULTIPLY: ", this.getSymbol(), x, y, (x * y));
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return x * y;
            }
      },
      DIVIDE("/", 2) {
            @Override
            public double execute(ExpNode exp, double x, double y) {
                  String res = formatToString("DIVIDE: ", this.getSymbol(), x, y, (x / y));
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);

                  return x / y;
            }
      },
      MODULUS("%", 2) {
            @Override
            public double execute(ExpNode exp, double x, double y) {
                  String res = formatToString("DIVIDE: ", this.getSymbol(), x, y, (x / y));
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return x % y;
            }
      },
      ABS("abs", 3) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {
                  double result;
                  result = StrictMath.abs(x);
                  //exp.setExpSolved("ABSOLUTE VALUE: " + this.getSymbol() + " " + x + " = " + result);
                  String res = setFormUnary("ABSOLUTE VALUE: ", this.getSymbol(), x, result);
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);

                  return result;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      LOG("log", 3) {
            // for small roots log1p is closer to truth but using log().
            @Override
            public double execute(ExpNode exp, double base, double exponent) {

                  double result;
                  result = Math.log(exponent) / Math.log(base);
                  exp.setExpSolved("LOG: " + this.getSymbol() + " base " + base + " log exp: " + exponent + " = " + result);
                  ansComponents.offer(exp);
                  return result;
            }
      },
      POWER("^", 3) {
            @Override
            public double execute(ExpNode exp, double x, double exponent) {
                  String res = formatToString("EXPONENT: ", this.getSymbol(), x, exponent, Math.pow(x, exponent));
                  exp.setExpSolved(res);
                  return Math.pow(x, exponent);
            }
      },
      ROOT("root", 3) {
            @Override
            public double execute(ExpNode exp, double root, double x) {

                  if (x < 0) {
                        return 0;
                  }

                  // convert root to integer
                  int intRoot = (int) root;

                  switch (intRoot) {

                        case 0: {
                              return 0;
                        }
                        case 1: {
                              return x;
                        }
                        case 2: { // square root
                              String res = setFormUnary("SQRT: ", this.getSymbol(), x, Math.sqrt(x));
                              exp.setExpSolved(res);
                              ansComponents.offer(exp);
                              return StrictMath.sqrt(x);
                        }
                        default: {

                              // @todo Need to handle negative roots < 2 in Operator root

                              double ans = StrictMath.pow(x, (1 / root));
                              double ansCeil = Math.ceil(ans);

                              //System.out.println("ansCiel: " + ansCeil + " - ciel: " + ans + " = " + (ansCeil - ans));

                              if ((root > 2 && (ansCeil - ans < .00000001)) || (root < -1 && (ansCeil - ans > .09))) {
                                    //exp.setExpSolved("ROOT true: " + root + " " + this.getSymbol() + " " + x + " = " + ansCeil);
                                    String res = setFormUnary("ROOT true: " + root + " ", this.getSymbol(), x, ansCeil);
                                    exp.setExpSolved(res);

                                    ansComponents.offer(exp);
                                    return ansCeil;
                              } else {
                                    //exp.setExpSolved("ROOT false: " + root + " " + this.getSymbol() + " " + x + " = " + ans);
                                    String res = setFormUnary("ROOT false: " + root + " ", this.getSymbol(), x, ans);
                                    exp.setExpSolved(res);
                                    ansComponents.offer(exp);
                                    return ans;
                              }
                        }
                  }
            }
      },
      SQRT("sqrt", 3) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {
                  // ROOT from SQRT is the reverse of other operators
                  // return ROOT.execute(exp, root, x);
                  if (x <= 0) {
                        return 0;
                  }

                  double solved = StrictMath.sqrt(x);
                  //exp.setExpSolved("SQRT: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("SQRT: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  //System.out.println("exp: " + exp);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },


      // ------------- COS SIN TAN & SIMILAR FUNCTIONS (Priority 2 ) -------------


      COS("cos", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  double radians = Math.toRadians(x);
                  double solved = StrictMath.cos(radians);
                  //exp.setExpSolved("COS: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("COS: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  //System.out.println("exp: " + exp);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      COSH("cosh", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  double solved = StrictMath.cosh(x);
                  //exp.setExpSolved("COSH: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("COSH: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  //System.out.println("exp: " + exp);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      ACOS("acos", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  //double radians = Math.toRadians(x);
                  double solved = StrictMath.acos(x);
                  //exp.setExpSolved("ACOS: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("ACOS: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  //System.out.println("exp: " + exp);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      SIN("sin", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  double radians = Math.toRadians(x);
                  double solved = StrictMath.sin(radians);

                  String res = setFormUnary("SIN: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);

                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      SINH("sinh", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  //double radians = Math.toRadians(x);
                  double solved = StrictMath.sinh(x);
                  //exp.setExpSolved("SINH: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("SINH: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      ASIN("asin", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  //double radians = Math.toRadians(x);
                  double solved = StrictMath.asin(x);
                  //exp.setExpSolved("ASIN: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("ASIN: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);

                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      TAN("tan", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  double radians = Math.toRadians(x);
                  double solved = StrictMath.tan(radians);
                  //exp.setExpSolved("TAN: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("TAN: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      TANH("tanh", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  //double radians = Math.toRadians(x);
                  double solved = StrictMath.tanh(x);
                  //exp.setExpSolved("TANH: " + this.getSymbol() + " " + x + " = " + solved)
                  String res = setFormUnary("TANH: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      ATAN("atan", 2) {
            @Override
            public double execute(ExpNode exp, double x, double notUsed) {

                  //double radians = Math.toRadians(x);
                  double solved = StrictMath.atan(x);
                  //exp.setExpSolved("ATAN: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("ATAN: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return solved;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      },
      ATAN2("atan2", 2) {
            @Override
            public double execute(ExpNode exp, double x, double y) {

                  //double radians = Math.toRadians(x);
                  double solved = StrictMath.atan2(x, y);
                  //exp.setExpSolved("ATAN2: " + this.getSymbol() + " " + x + " = " + solved);
                  String res = setFormUnary("ATAN2: ", this.getSymbol(), x, solved);
                  exp.setExpSolved(res);
                  ansComponents.offer(exp);
                  return solved;
            }
      },
      DEFAULT("def", 8) {
            @Override
            public double execute(ExpNode exp, double notUsed, double notUsedEither) {

                  System.err.println(" *** Called DEFAULT.execute() ***");

                  return 0;
            }

            @Override
            public boolean isUnaryOp() {
                  return true;
            }
      };

// --------------------------------- --------------------------------- //
// END OF CONSTANTS
// Common fields and methods
// --------------------------------- --------------------------------- //

      private final String symbol;
      private final int priority;
      // Ensure this is independent of any using class. Watch that
      // any getters do not have this reference.
      private static final ArrayQueue<ExpNode> ansComponents = new ArrayQueue<>(4);

      Operator(String sym, int p) {
            symbol = sym;
            priority = p;
      }

      @Override
      public String getSymbol() {
            return this.symbol;
      }

      @Override
      public int getPriority() {
            return this.priority;
      }

      @Override
      public abstract double execute(ExpNode exp, double x, double y);


      /**
       * if this Operator has a lower or equal priority than the operator on
       * the top of the opStack. Pop the operator on the opStack
       * and push it onto the outQueue until this operator can be
       * pushed onto the opStack. (this operator is higher than the
       * operator on the opStack)
       *
       * @param outQueue ..
       * @param opStack  ..
       */
      public void stackAction(Object previous, ExpNode op, ArrayQueue outQueue, Stack<ExpNode> opStack, int index) {
            // index not used.
            while (!opStack.isEmpty() && opStack.peek().getOp().getPriority() >= this.priority) {
                  outQueue.offer(opStack.pop()); // pushing the enum onto the numStack
            }
            opStack.push(op);
      }

      public String formatToString(String startStr, String symbol, double x, double y, double result) {
            DecimalFormat f = new DecimalFormat("#.####");
            return String.format("%-11s", startStr) + f.format(x) + " " + symbol + " " + f.format(y) + " = " + f.format(result);
      }

      /**
       * Sets the form of the expression string for unary operators
       *
       * @param startStr
       * @param symbol
       * @param x
       * @param result
       * @return
       */
      public String setFormUnary(String startStr, String symbol, double x, double result) {
            DecimalFormat f = new DecimalFormat("#.####");
            return String.format("%-11s", startStr) + f.format(x) + " " + symbol + " = " + f.format(result);
            //return String.format(startStr + FORMAT_N + symbol + FORMAT_N + FORMAT_RESULT, x, result);
      }

      public static void clearAnsComponents() {
            ansComponents.clear();
      }

      public static boolean hasOperators() {
            return ansComponents == null || ansComponents.isEmpty();
      }

      public static ExpNode getLast() {
            return ansComponents.getLast();
      }

      public static ExpNode poll() {
            return ansComponents.poll();
      }


      @Override
      public boolean isUnaryOp() {
            return false;
      }

      @Override
      public String toString() {
            return this.getSymbol();
      }

      @Override
      public String getStrExpr(String x, String y, OperatorInterface op) {
            return x + " " + op + " " + y;
      }
}