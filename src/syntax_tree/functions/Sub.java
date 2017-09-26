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
public class Sub implements Function {

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
        return 2;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isCommutative() {
        return false;
    }

    @Override
    public double eval(Expression expression, Context context) {
        List<Expression> childs = expression.getChilds();
        double left = childs.get(0).eval(context);
        double right = childs.get(1).eval(context);
        return (left - right);
    }

    @Override
    public String print(Expression expression) {
        List<Expression> childs = expression.getChilds();
        String left = childs.get(0).print();
        String right = childs.get(1).print();

        return String.format("(%s - %s)", left, right);
    }
}
