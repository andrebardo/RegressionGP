/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import genetic_programming.Configuration;
import genetic_programming.ExpressionTreeFitness;
import genetic_programming.GPChromosome;
import genetic_programming.GeneticAlgorithm;
import java.util.ArrayList;
import syntax_tree.Context;
import syntax_tree.functions.Add;
import syntax_tree.functions.Constant;
import syntax_tree.functions.Function;
import syntax_tree.functions.Sub;
import syntax_tree.functions.Variable;
import util.DataLoader;

/**
 *
 * @author Kurumin
 */
public class main {
    
    public static void main (String args[]){
        DataLoader trainData = new DataLoader(Configuration.INPUT_SIZE, Configuration.IDEAL_SIZE);
        trainData.load(Configuration.TRAINING_FILE_NAME, false); // carrega os dados de treinamento
        
        Context context = Configuration.buildContext(); // constroi um contexto base
        ExpressionTreeFitness expFitness = new ExpressionTreeFitness(trainData.getData()); // cria a funcao de avaliacao
        GeneticAlgorithm ga = new GeneticAlgorithm();
        
        ga.evolve(context, expFitness);
        
        GPChromosome bestChromo = ga.getBestChromo();
        double bestFitness = ga.getBestFit();
        
        System.out.println("Melhor solução encontrada:\n"+bestChromo.getSyntaxTree().print()+" -> "+String.format("%.4f", bestFitness));
    }
    
    public static Context buildContext(){
        ArrayList<String> variables = new ArrayList<>();
        ArrayList<Function> functions = new ArrayList<>();
        
        variables.add("x1");
        
        functions.add(new Add());
        functions.add(new Sub());
        functions.add(new Constant());
        functions.add(new Variable());
        
        return new Context(functions, variables);
    }
}
