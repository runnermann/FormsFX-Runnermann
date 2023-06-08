package type.tools.calculator;

import queue.ArrayQueue;

import java.util.Stack;

public interface OperatorInterface {

      String getSymbol();

      int getPriority();

      void stackAction(Object previous, ExpNode op, ArrayQueue outQueue, Stack<ExpNode> opStack, int index);

      double execute(ExpNode exp, double x, double value);

      String getStrExpr(String x, String y, OperatorInterface op);

      boolean isUnaryOp();

      boolean isOpenEnclosure();

      boolean isCloseEnclosure();

      /**
       * For open enclosures, a match would be the
       * closing enclosure for the same type.
       * @param operatorSymbol The operator
       * @return
       */
      public boolean isMatch(char operatorSymbol);
}
