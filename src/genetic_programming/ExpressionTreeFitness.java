/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic_programming;

import java.util.List;
import org.encog.ml.data.MLDataPair;
import syntax_tree.Context;
import syntax_tree.Expression;

/**
 *
 * @author Kurumin
 */
public class ExpressionTreeFitness implements Fitness{
    
    private List<MLDataPair> data; // dados contendo os pares (input, ideal)

    public ExpressionTreeFitness(List<MLDataPair> data) {
        this.data = data;
    }
    
    @Override
    public double fitness(Expression expression, Context context) {
        double diff = 0;
        for (MLDataPair pair : data) { // é feita uma avaliacao para cada par
            double[] input = pair.getInputArray(); // conjunto de entradas (x1, x2, x3, ..., xn)
            double idealValue = pair.getIdealArray()[0]; // o valor ideal y deste par (só tem um)
            for(int i = 0; i < input.length; i++){
                String variableName = "x"+(i+1); // o contexto, do jeito que foi implementado, pede um nome String
		Double variableValue = input[i]; // pega o valor da variavel
                context.setVariable(variableName, variableValue); // configura cada variavel no contexto
            }
            double actualValue = expression.eval(context); // avalia a expressao no contexto configurado
            diff += quadrado(actualValue - idealValue);
        }
        
        diff *= 1.0 / (double)data.size(); // * 1/N, onde N é o número de exemplos fornecidos
        
        return Math.sqrt(diff);
    }
    
    public double quadrado(double x){
        return x*x;
    }
    
}
