/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package com.liteoc.logic.expressionTree;

import com.liteoc.exception.OpenClinicaSystemException;

/**
 * @author Krikor Krumlian
 * 
 */
public class EqualityOpNode extends ExpressionNode {
    Operator op; // The operator.
    ExpressionNode left; // The expression for its left operand.
    ExpressionNode right; // The expression for its right operand.

    EqualityOpNode(Operator op, ExpressionNode left, ExpressionNode right) {
        // Construct a BinOpNode containing the specified data.
        assert op == Operator.EQUAL || op == Operator.NOT_EQUAL || op == Operator.CONTAINS;
        assert left != null && right != null;
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    String testCalculate() throws OpenClinicaSystemException {
        String x = null;
        String y = null;
        String l = left.testValue();
        String r = right.testValue();
        try {
            Float fx = Float.valueOf(l);
            Float fy = Float.valueOf(r);
            x = fx.toString();
            y = fy.toString();
        } catch (NumberFormatException nfe) {
            // Don't do anything cause we were just testing above.
        }
        if (x == null && y == null) {
            x = String.valueOf(l);
            y = String.valueOf(r);
        }
        return calc(x, y);
    }

    @Override
    String calculate() throws OpenClinicaSystemException {
        String x = null;
        String y = null;
        String l = left.value();
        String r = right.value();
        try {
            Float fx = Float.valueOf(l);
            Float fy = Float.valueOf(r);
            x = fx.toString();
            y = fy.toString();
        } catch (NumberFormatException nfe) {
            // Don't do anything cause we were just testing above.
        }
        if (x == null && y == null) {
            x = String.valueOf(l);
            y = String.valueOf(r);
        }
        return calc(x, y);

    }

    private String calc(String x, String y) throws OpenClinicaSystemException {
        switch (op) {
        case EQUAL:
            return String.valueOf(x.equals(y));
        case NOT_EQUAL:
            return String.valueOf(!x.equals(y));
        case CONTAINS:
            return String.valueOf(x.contains(y));
        default:
            throw new OpenClinicaSystemException("OCRERR_0002", new Object[] { left.value(), right.value(), op.toString() });
        }
    }

    @Override
    void printStackCommands() {
        // To evalute the expression on a stack machine, first do
        // whatever is necessary to evaluate the left operand, leaving
        // the answer on the stack. Then do the same thing for the
        // second operand. Then apply the operator (which means popping
        // the operands, applying the operator, and pushing the result).
        left.printStackCommands();
        right.printStackCommands();
        logger.info("  Operator " + op);
    }
}