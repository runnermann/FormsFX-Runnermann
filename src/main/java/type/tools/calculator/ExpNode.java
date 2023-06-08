package type.tools.calculator;

/**
 * This class contains a sub expression that is related to a single binomial operator.
 * It provides an object that can be referenced by the interactive calculator.
 */
public class ExpNode {

    private String expOrigin; // The values prior to any executions
    private String expSolved; // The values at the time the expression is executed
    private OperatorInterface op; // The operator type i.e "*" or "{" etc...
    private int idx; // index
    private int expressionIdx = 0;


    public ExpNode() {
        // no args constructor
    }

    /**
     * Contains a reference to this operator and its sub expression.
     * @param o This operator
     * @param origExp The sub expression of this operator
     * @param index The index in the string of the operator in this node.
     */
    public ExpNode(OperatorInterface o, String origExp, int index) {
        this.op = o;
        this.expOrigin = origExp;
        this.idx = index;
    }

    public String getExpOrigin() {
        return this.expOrigin;
    }

    public String getExpSolved() {
        return this.expSolved;
    }

    public OperatorInterface getOp() {
        return this.op;
    }

    /**
     * Returns the operator index.
     * @return
     */
    public int getIndex() { return this.idx; }

    public void setExpOrigin(String expOrigin) {
        this.expOrigin = expOrigin;
    }

    public void setExpSolved(String expSolved) {
        this.expSolved = expSolved;
    }

    public void setOp(Operator op) {
        this.op = op;
    }

    public void setIndex(int index) { this.idx = index; }

    public String toString() {
        return this.op.getSymbol();
    }

    /**
     * This must be set when the operator is created.
     * @param idx
     */

    public void setExpressionEndIdx(int idx) {
        expressionIdx = idx;
    }


    public int getExpressionStartIdx() {
        return expressionIdx;
    }
}
