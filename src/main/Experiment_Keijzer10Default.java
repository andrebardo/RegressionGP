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
import syntax_tree.Context;
import util.DataLoader;
import util.DataWriter;
import util.MathUtils;

/**
 *
 * @author Kurumin
 */
public class Experiment_Keijzer10Default {

    private static final int NUM_EXEC = 30;
    private static final String EXPERIMENT_NAME = "keijzer-10_default";
    private static final boolean VERBOSE = true; // print?
    
    private static void setupExperiment(){
        Configuration.POPULATION_SIZE = 100;
        Configuration.TOURNAMENT_SIZE = 2;
        Configuration.ELITISM_SIZE = 1;
        Configuration.MAX_GENERATION = 100;

        Configuration.CROSSING_RATE = 0.90; // Pc
        Configuration.MUTATION_RATE = 0.05; // Pm

        Configuration.TREE_DEPTH = 7;

        Configuration.TRAINING_FILE_NAME = "keijzer-10-train";
        Configuration.TESTING_FILE_NAME = "keijzer-10-test";
        Configuration.OUTPUT_DIR_NAME = "output";
        Configuration.INPUT_SIZE = 2; // <- Precisa ser configurado
        Configuration.IDEAL_SIZE = 1; // <- Precisa ser configurado
    }

    public static void main(String args[]) {
        
        setupExperiment();
        
        ArrayList<ArrayList<Double>> listOfBestFitness = new ArrayList<>();
        ArrayList<ArrayList<Double>> listOfMeanFitness = new ArrayList<>();
        ArrayList<ArrayList<Double>> listOfWorstFitness = new ArrayList<>();
        ArrayList<ArrayList<Double>> listOfRepeatPercent = new ArrayList<>();
        ArrayList<ArrayList<Double>> listOfLowerPercent = new ArrayList<>();
        ArrayList<ArrayList<Double>> listOfHigherPercent = new ArrayList<>();

        DataLoader trainData = new DataLoader(Configuration.INPUT_SIZE, Configuration.IDEAL_SIZE);
        trainData.load(Configuration.TRAINING_FILE_NAME, false); // carrega os dados de treinamento
        Context context = Configuration.buildContext(); // constroi um contexto base
        ExpressionTreeFitness expFitness = new ExpressionTreeFitness(trainData.getData()); // cria a funcao de avaliacao

        for (int i = 0; i < NUM_EXEC; i++) {
            System.out.println("Exec " + (i + 1) + "/" + (NUM_EXEC));
            GeneticAlgorithm ga = new GeneticAlgorithm();

            ga.evolve(context, expFitness, VERBOSE);

            listOfBestFitness.add(ga.getBestFitness());
            listOfMeanFitness.add(ga.getMeanFitness());
            listOfWorstFitness.add(ga.getWorstFitness());
            listOfRepeatPercent.add(ga.getRepeatPerGen());
            listOfLowerPercent.add(ga.getLowFitPercent());
            listOfHigherPercent.add(ga.getHighFitPercent());
        }

        ArrayList<ArrayList<Double>> bestPerGen = new ArrayList();
        ArrayList<ArrayList<Double>> meanPerGen = new ArrayList();
        ArrayList<ArrayList<Double>> worstPerGen = new ArrayList();
        ArrayList<ArrayList<Double>> repeatPerGen = new ArrayList();
        ArrayList<ArrayList<Double>> lowerPerGen = new ArrayList();
        ArrayList<ArrayList<Double>> higherPerGen = new ArrayList();
        for (int gen = 0; gen < Configuration.MAX_GENERATION; gen++) {
            ArrayList<Double> bestOfExec = new ArrayList();
            ArrayList<Double> meanOfExec = new ArrayList();
            ArrayList<Double> worstOfExec = new ArrayList();
            ArrayList<Double> repeatOfExec = new ArrayList();
            ArrayList<Double> lowerOfExec = new ArrayList();
            ArrayList<Double> higherOfExec = new ArrayList();

            for (int exec = 0; exec < NUM_EXEC; exec++) {
                bestOfExec.add(listOfBestFitness.get(exec).get(gen));
                meanOfExec.add(listOfMeanFitness.get(exec).get(gen));
                worstOfExec.add(listOfWorstFitness.get(exec).get(gen));
                repeatOfExec.add(listOfRepeatPercent.get(exec).get(gen));
                lowerOfExec.add(listOfLowerPercent.get(exec).get(gen));
                higherOfExec.add(listOfHigherPercent.get(exec).get(gen));
            }

            bestPerGen.add(bestOfExec);
            meanPerGen.add(meanOfExec);
            worstPerGen.add(worstOfExec);
            
            repeatPerGen.add(repeatOfExec);
            lowerPerGen.add(lowerOfExec);
            higherPerGen.add(higherOfExec);
        }

        ArrayList<Double> meanBestFitPerGen = new ArrayList<>();
        ArrayList<Double> bestFitnessStdDevPerGen = new ArrayList<>();

        ArrayList<Double> meanMeanFitPerGen = new ArrayList<>();
        ArrayList<Double> meanFitnessStdDevPerGen = new ArrayList<>();

        ArrayList<Double> meanWorstFitPerGen = new ArrayList<>();
        ArrayList<Double> worstFitnessStdDevPerGen = new ArrayList<>();
        
        ArrayList<Double> meanRepeatPerGen = new ArrayList<>();
        ArrayList<Double> repeatStdDevPerGen = new ArrayList<>();
        
        ArrayList<Double> meanLowerPerGen = new ArrayList<>();
        ArrayList<Double> lowerStdDevPerGen = new ArrayList<>();
        
        ArrayList<Double> meanHigherPerGen = new ArrayList<>();
        ArrayList<Double> higherStdDevPerGen = new ArrayList<>();
        
        for (int gen = 0; gen < Configuration.MAX_GENERATION; gen++) {
            meanBestFitPerGen.add(MathUtils.mean(bestPerGen.get(gen)));
            bestFitnessStdDevPerGen.add(MathUtils.stdDev(bestPerGen.get(gen)));

            meanMeanFitPerGen.add(MathUtils.mean(meanPerGen.get(gen)));
            meanFitnessStdDevPerGen.add(MathUtils.stdDev(meanPerGen.get(gen)));

            meanWorstFitPerGen.add(MathUtils.mean(worstPerGen.get(gen)));
            worstFitnessStdDevPerGen.add(MathUtils.stdDev(worstPerGen.get(gen)));
            
            meanRepeatPerGen.add(MathUtils.mean(repeatPerGen.get(gen)));
            repeatStdDevPerGen.add(MathUtils.stdDev(repeatPerGen.get(gen)));
            
            meanLowerPerGen.add(MathUtils.mean(lowerPerGen.get(gen)));
            lowerStdDevPerGen.add(MathUtils.stdDev(lowerPerGen.get(gen)));
            
            meanHigherPerGen.add(MathUtils.mean(higherPerGen.get(gen)));
            higherStdDevPerGen.add(MathUtils.stdDev(higherPerGen.get(gen)));
        }

        DataWriter.write(EXPERIMENT_NAME+"_data_per_generation",
                meanBestFitPerGen, bestFitnessStdDevPerGen,
                meanMeanFitPerGen, meanFitnessStdDevPerGen,
                meanWorstFitPerGen, worstFitnessStdDevPerGen,
                meanRepeatPerGen, repeatStdDevPerGen,
                meanLowerPerGen, lowerStdDevPerGen,
                meanHigherPerGen, higherStdDevPerGen);
    }

}
