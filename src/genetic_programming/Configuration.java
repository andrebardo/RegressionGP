/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic_programming;

import java.util.ArrayList;
import syntax_tree.Context;
import syntax_tree.functions.Add;
import syntax_tree.functions.Constant;
import syntax_tree.functions.Cos;
import syntax_tree.functions.Div;
import syntax_tree.functions.Function;
import syntax_tree.functions.Ln;
import syntax_tree.functions.Mult;
import syntax_tree.functions.Pow;
import syntax_tree.functions.Sin;
import syntax_tree.functions.Sqrt;
import syntax_tree.functions.Sub;
import syntax_tree.functions.Variable;

/**
 *
 * @author Kurumin
 */
public class Configuration {
    
    public static final int POPULATION_SIZE = 100;
    public static final int TOURNAMENT_SIZE = 2;
    public static final int ELITISM_SIZE = 0;
    public static final int MAX_GENERATION = 100;
    
    public static final double CROSSING_RATE = 0.90; // Pc
    public static final double MUTATION_RATE = 0.10; // Pm
    
    public static final int TREE_DEPTH = 7;
    
    public static final String TRAINING_FILE_NAME = "keijzer-7-train";
    public static final String TESTING_FILE_NAME = "keijzer-7-test";
    public static final int INPUT_SIZE = 1; // <- Precisa ser configurado
    public static final int IDEAL_SIZE = 1; // <- Precisa ser configurado
    
    public static Context buildContext(){
        ArrayList<String> variables = new ArrayList<>();
        ArrayList<Function> functions = new ArrayList<>();
        
        for(int i = 0; i < INPUT_SIZE; i++){
            variables.add("x"+(i+1));
        }
  
        functions.add(new Add());
        functions.add(new Sub());
        functions.add(new Mult());
        functions.add(new Div());
        functions.add(new Sqrt());
        functions.add(new Pow());
        functions.add(new Sin());
        functions.add(new Cos());
        functions.add(new Ln());
        functions.add(new Constant());
        functions.add(new Variable());
        
        return new Context(functions, variables);
    } 
}
