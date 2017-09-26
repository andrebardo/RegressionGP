/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax_tree.functions;

import java.util.List;

/**
 *
 * @author Kurumin
 */
import java.util.List;
import syntax_tree.Context;
import syntax_tree.Expression;

public interface Function {

    double eval(Expression expression, Context context); // Calcula o resultado da expressao em um dado contexto

    int argumentsCount();  // Numero de parametros que a expressao precisa

    boolean isVariable();

    boolean isNumber();

    boolean isCommutative(); // https://pt.wikipedia.org/wiki/Comutatividade

    String print(Expression expression);

    List<Double> getCoefficients(Expression expression);

    void setCoefficients(Expression expression, List<Double> coefficients, int startIndex);

    int coefficientsCount();

}
