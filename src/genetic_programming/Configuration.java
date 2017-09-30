/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic_programming;

/**
 *
 * @author Kurumin
 */
public class Configuration {
    
    public static final int POPULATION_SIZE = 10;
    public static final int TOURNAMENT_SIZE = 2;
    public static final int MAX_GENERATION = 100;
    
    public static final double CROSSING_RATE = 0.90;
    public static final double MUTATION_RATE = 0.05;
    
    public static final int TREE_DEPTH = 7;
    
    public static final String TRAINING_FILE_NAME = "keijzer-7-train";
    public static final String TESTING_FILE_NAME = "keijzer-7-test";
    public static final int INPUT_SIZE = 1; // <- Precisa ser configurado
    public static final int IDEAL_SIZE = 1; // <- Precisa ser configurado
    
}
