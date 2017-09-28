/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import genetic_programming.ExpressionTreeFitness;
import genetic_programming.GPChromosome;
import java.util.ArrayList;
import java.util.List;
import syntax_tree.Context;
import syntax_tree.Expression;
import syntax_tree.SyntaxTreeUtils;
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
public class testExpressionTreeFitness {
    
    public static void main(String args[]){
        String filename = "keijzer-7-train"; // <- Nome do Arquivo
        
        int inputSize = 1; // <- Precisa ser configurado
        int idealSize = 1; // <- Precisa ser configurado
        int treeDepth = 7; // <- Parametro dado no enunciado
        
        DataLoader trainData = new DataLoader(inputSize, idealSize);
        trainData.load(filename, true); // carrega os dados de treinamento
 
        Context context = buildContext(); // constroi um contexto base
        Expression exp1 = SyntaxTreeUtils.createTree(treeDepth, context); // gera uma expressao aleatoria
        Expression exp2 = SyntaxTreeUtils.createTree(treeDepth, context); // gera outra expressao aleatoria
        System.out.println("Expressão 1 criada: "+exp1.print());
        System.out.println("Expressão 2 criada: "+exp2.print());
        
        ExpressionTreeFitness expFitness = new ExpressionTreeFitness(trainData.getData()); // cria a funcao de avaliacao
        double fitness1 = expFitness.fitness(exp1, context); // avalia a expressao 1
        double fitness2 = expFitness.fitness(exp2, context); // avalia a expressao 2
        System.out.println("Fitness 1: "+fitness1);
        System.out.println("Fitness 2: "+fitness2);
        
        GPChromosome chromo1 = new GPChromosome(context, expFitness, exp1);
        GPChromosome chromo2 = new GPChromosome(context, expFitness, exp2);
        List<GPChromosome> filhos = chromo1.crossover(chromo2);
        System.out.println(filhos.size());
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
