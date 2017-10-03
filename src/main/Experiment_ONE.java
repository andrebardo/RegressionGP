/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import genetic_programming.Configuration;
import genetic_programming.ExpressionTreeFitness;
import genetic_programming.GeneticAlgorithm;
import java.util.ArrayList;
import java.util.List;
import syntax_tree.Context;
import util.DataLoader;
import util.DataWriter;
import util.MathUtils;

/**
 *
 * @author Kurumin
 */
public class Experiment_ONE {

    private static final int NUM_EXEC = 30;
    private static final String EXPERIMENT_NAME = "ONE";
    
    private static void setupExperiment(){
        Configuration.POPULATION_SIZE = 100;
        Configuration.TOURNAMENT_SIZE = 2;
        Configuration.ELITISM_SIZE = 1;
        Configuration.MAX_GENERATION = 100;

        Configuration.CROSSING_RATE = 0.90; // Pc
        Configuration.MUTATION_RATE = 0.05; // Pm

        Configuration.TREE_DEPTH = 7;

        Configuration.TRAINING_FILE_NAME = "keijzer-7-train";
        Configuration.TESTING_FILE_NAME = "keijzer-7-test";
        Configuration.OUTPUT_DIR_NAME = "output";
        Configuration.INPUT_SIZE = 1; // <- Precisa ser configurado
        Configuration.IDEAL_SIZE = 1; // <- Precisa ser configurado
    }

    public static void main(String args[]) {
        
        setupExperiment();
        
        ArrayList<ArrayList<Double>> listOfBestFitness = new ArrayList<>();
        ArrayList<ArrayList<Double>> listOfMeanFitness = new ArrayList<>();
        ArrayList<ArrayList<Double>> listOfWorstFitness = new ArrayList<>();

        DataLoader trainData = new DataLoader(Configuration.INPUT_SIZE, Configuration.IDEAL_SIZE);
        trainData.load(Configuration.TRAINING_FILE_NAME, false); // carrega os dados de treinamento
        Context context = Configuration.buildContext(); // constroi um contexto base
        ExpressionTreeFitness expFitness = new ExpressionTreeFitness(trainData.getData()); // cria a funcao de avaliacao

        for (int i = 0; i < NUM_EXEC; i++) {
            System.out.println("Exec " + (i + 1) + "/" + (NUM_EXEC));
            GeneticAlgorithm ga = new GeneticAlgorithm();

            ga.evolve(context, expFitness, false);

            listOfBestFitness.add(ga.getBestFitness());
            listOfMeanFitness.add(ga.getMeanFitness());
            listOfWorstFitness.add(ga.getWorstFitness());
        }

        ArrayList<ArrayList<Double>> bestPerGen = new ArrayList();
        ArrayList<ArrayList<Double>> meanPerGen = new ArrayList();
        ArrayList<ArrayList<Double>> worstPerGen = new ArrayList();
        for (int gen = 0; gen < Configuration.MAX_GENERATION; gen++) {
            ArrayList<Double> bestOfExec = new ArrayList();
            ArrayList<Double> meanOfExec = new ArrayList();
            ArrayList<Double> worstOfExec = new ArrayList();

            for (int exec = 0; exec < NUM_EXEC; exec++) {
                bestOfExec.add(listOfBestFitness.get(exec).get(gen));
                meanOfExec.add(listOfMeanFitness.get(exec).get(gen));
                worstOfExec.add(listOfWorstFitness.get(exec).get(gen));
            }

            bestPerGen.add(bestOfExec);
            meanPerGen.add(meanOfExec);
            worstPerGen.add(worstOfExec);
        }

        ArrayList<Double> meanBestFitPerGen = new ArrayList<>();
        ArrayList<Double> bestFitnessStdDevPerGen = new ArrayList<>();

        ArrayList<Double> meanMeanFitPerGen = new ArrayList<>();
        ArrayList<Double> meanFitnessStdDevPerGen = new ArrayList<>();

        ArrayList<Double> meanWorstFitPerGen = new ArrayList<>();
        ArrayList<Double> worstFitnessStdDevPerGen = new ArrayList<>();
        
        for (int gen = 0; gen < Configuration.MAX_GENERATION; gen++) {
            meanBestFitPerGen.add(MathUtils.mean(bestPerGen.get(gen)));
            bestFitnessStdDevPerGen.add(MathUtils.stdDev(bestPerGen.get(gen)));

            meanMeanFitPerGen.add(MathUtils.mean(meanPerGen.get(gen)));
            meanFitnessStdDevPerGen.add(MathUtils.stdDev(meanPerGen.get(gen)));

            meanWorstFitPerGen.add(MathUtils.mean(worstPerGen.get(gen)));
            worstFitnessStdDevPerGen.add(MathUtils.stdDev(worstPerGen.get(gen)));
        }

        DataWriter.write(EXPERIMENT_NAME+"_fitness_per_generation",
                meanBestFitPerGen, bestFitnessStdDevPerGen,
                meanMeanFitPerGen, meanFitnessStdDevPerGen,
                meanWorstFitPerGen, worstFitnessStdDevPerGen);
    }

}
