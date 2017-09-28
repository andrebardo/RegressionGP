/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax_tree.functions;

import java.util.List;
import syntax_tree.Context;
import syntax_tree.Expression;

/**
 *
 * @author Kurumin
 */
public class Constant implements Function {

    private int coefficientsCount = 1;

    @Override
    public int argumentsCount() {
        return 0;
    }

    @Override
    public int coefficientsCount() {
        return this.coefficientsCount;
    }

    @Override
    public List<Double> getCoefficients(Expression expression) {
        return expression.getCoefficientsOfNode().subList(0, this.coefficientsCount);
    }

    @Override
    public void setCoefficients(Expression expression, List<Double> coefficients, int startIndex) {
        expression.removeCoefficients();
        for (int i = 0; i < this.coefficientsCount; i++) {
            expression.addCoefficient(coefficients.get(startIndex + i));
        }
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean isCommutative() {
        return true;
    }

    @Override
    public double eval(Expression expression, Context context) {
        return expression.getCoefficientsOfNode().get(0);
    }

    @Override
    public String print(Expression expression) {
        double retVal = expression.getCoefficientsOfNode().get(0);
        String retStr = null;
        if (retVal < 0) {
            retStr = String.format("(%.2f)", retVal);
        } else {
            retStr = String.format("%.2f", retVal);
        }
        return retStr;
    }
}
