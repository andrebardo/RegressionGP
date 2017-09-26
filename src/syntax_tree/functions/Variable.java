/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax_tree.functions;

import java.util.LinkedList;
import java.util.List;
import syntax_tree.Context;
import syntax_tree.Expression;

/**
 *
 * @author Kurumin
 */
public class Variable implements Function {

    private int coefficientsCount = 0;

    @Override
    public int coefficientsCount() {
        return this.coefficientsCount;
    }

    @Override
    public List<Double> getCoefficients(Expression expression) {
        return new LinkedList<Double>();
    }

    @Override
    public void setCoefficients(Expression expression, List<Double> coefficients, int startIndex) {
        expression.removeCoefficients();
    }

    @Override
    public int argumentsCount() {
        return 0;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isCommutative() {
        return true;
    }

    @Override
    public double eval(Expression expression, Context context) {
        return context.lookupVariable(expression.getVariable());
    }

    @Override
    public String print(Expression expression) {
        return expression.getVariable();
    }
}
