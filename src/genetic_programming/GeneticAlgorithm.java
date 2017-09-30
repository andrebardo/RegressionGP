/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic_programming;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import syntax_tree.Context;
import syntax_tree.Expression;
import syntax_tree.SyntaxTreeUtils;

/**
 *
 * @author Kurumin
 */
public class GeneticAlgorithm {

    private ArrayList<Double> bestFitness;
    private ArrayList<Double> meanFitness;
    private ArrayList<Double> worstFitness;
    
    private ArrayList<Double> eliteFitness;
    private ArrayList<GPChromosome> eliteChromo;
    private double eliteThreshold;
    
    private GPChromosome bestChromo;
    private double bestFit;

    public GeneticAlgorithm() {
        this.bestFitness = new ArrayList<>();
        this.meanFitness = new ArrayList<>();
        this.worstFitness = new ArrayList<>();
        
        this.eliteChromo = new ArrayList<>();
        this.eliteFitness = new ArrayList<>();
        this.eliteThreshold = Double.MAX_VALUE;
        
        this.bestFit = Double.MAX_VALUE;
    }

    public void evolve(Context context, Fitness fitnessFunction) {

        ArrayList<GPChromosome> newPop = (ArrayList<GPChromosome>) createInitialPopulation(context, fitnessFunction);
        bestChromo = newPop.get(0);
        
        for (int generation = 0; generation < Configuration.MAX_GENERATION; generation++) {
            ArrayList<Double> fitness = (ArrayList<Double>) evaluate(newPop);
            print(newPop, fitness, generation);

            ArrayList<GPChromosome> selectedPop = (ArrayList<GPChromosome>) tournamentSelection(newPop, fitness);
            ArrayList<GPChromosome> crossingPop = (ArrayList<GPChromosome>) crossover(selectedPop);
            ArrayList<GPChromosome> mutationPop = (ArrayList<GPChromosome>) mutation(crossingPop);
            newPop = (ArrayList<GPChromosome>) replacement(newPop, mutationPop);
        }
    }

    private List<GPChromosome> createInitialPopulation(Context context, Fitness expFitness) {
        ArrayList<GPChromosome> population = new ArrayList<>();
        for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
            Expression exp = SyntaxTreeUtils.createValidTree(Configuration.TREE_DEPTH, context);
            GPChromosome chromo = new GPChromosome(context, expFitness, exp);
            population.add(chromo);
        }
        return population;
    }

    private List<Double> evaluate(List<GPChromosome> population) {
        double bestFit = Double.MAX_VALUE;
        double worstFit = 0;
        double mean = 0;
        
        ArrayList<Double> fitness = new ArrayList<>();

        for (int i = 0; i < population.size(); i++) {
            GPChromosome chromo = population.get(i);
            Fitness function = chromo.getFitnessFunction();
            Expression tree = chromo.getSyntaxTree();
            Context context = chromo.getContext();
            double value = function.fitness(tree, context);
            fitness.add(value);

            if (value < bestFit) {
                bestFit = value;
                if(value < this.bestFit){
                    bestChromo = new GPChromosome(chromo.getContext(),
                                                  chromo.getFitnessFunction(),
                                                  chromo.getSyntaxTree().clone());
                    this.bestFit = value;
                }
            }
            if (value > worstFit) {
                worstFit = value;
            }
            mean += value;

            if (Configuration.ELITISM_SIZE > 1) {
                if (eliteFitness.size() < Configuration.ELITISM_SIZE) {
                    if (eliteFitness.isEmpty()) {
                        eliteFitness.add(value);
                        eliteChromo.add(chromo);
                    } else {
                        boolean insert = false;
                        for (int j = 0; j < eliteFitness.size(); j++) {
                            if (value < eliteFitness.get(j)) {
                                eliteFitness.add(j, value);
                                eliteChromo.add(j, chromo);
                                insert = true;
                                break;
                            }
                        }
                        if (!insert) {
                            eliteFitness.add(value);
                            eliteChromo.add(chromo);
                        }
                    }
                    eliteThreshold = eliteFitness.get(0);
                } else {
                    if (value < eliteThreshold) {
                        eliteFitness.remove(0);
                        eliteChromo.remove(0);
                        for (int j = 0; j < eliteFitness.size(); j++) {
                            if (value > eliteFitness.get(j)) {
                                eliteFitness.add(j, value);
                                eliteChromo.add(j, chromo);
                                break;
                            }
                        }
                        eliteThreshold = eliteFitness.get(0);
                    }
                }
            }
        }
        bestFitness.add(bestFit);
        mean = mean / (double) population.size();
        meanFitness.add(mean);
        worstFitness.add(worstFit);
        return fitness;
    }

    private List<GPChromosome> tournamentSelection(List<GPChromosome> population, List<Double> fitness) {
        ArrayList<GPChromosome> selected = new ArrayList<>(Configuration.POPULATION_SIZE);
        Random random = new Random(System.currentTimeMillis());

        while (selected.size() < Configuration.POPULATION_SIZE) {

            int index = random.nextInt(Configuration.POPULATION_SIZE);
            GPChromosome winner = population.get(index);
            double best = fitness.get(index);

            for (int i = 1; i < Configuration.TOURNAMENT_SIZE; i++) {
                index = random.nextInt(Configuration.POPULATION_SIZE);
                if (fitness.get(index) < best) {
                    winner = population.get(index);
                    best = fitness.get(index);
                }
            }
            selected.add(winner);
        }
        return selected;
    }

    private List<GPChromosome> crossover(List<GPChromosome> population) {
        ArrayList<GPChromosome> sons = new ArrayList<>(Configuration.POPULATION_SIZE);
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < Configuration.POPULATION_SIZE; i += 2) {
            GPChromosome father = population.get(i);
            GPChromosome mother = population.get(i + 1);

            if (random.nextDouble() <= Configuration.CROSSING_RATE) {
                sons.addAll(father.crossover(mother));
            } else {
                sons.add(father);
                sons.add(mother);
            }
        }
        return sons;
    }

    private List<GPChromosome> mutation(List<GPChromosome> population) {
        ArrayList<GPChromosome> mutants = new ArrayList<>(Configuration.POPULATION_SIZE);
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {

            if (random.nextDouble() <= Configuration.MUTATION_RATE) {
                //System.out.print("\nMutation at "+i);
                mutants.add(population.get(i).mutate());
            }
            else{
                mutants.add(population.get(i));
            }
        }
        return mutants;
    }

    private List<GPChromosome> replacement(List<GPChromosome> oldPop, List<GPChromosome> newPop) {
        ArrayList<GPChromosome> nextGeneration = new ArrayList<>(Configuration.POPULATION_SIZE);
        Random random = new Random(System.currentTimeMillis());
        // 100% replacement
        nextGeneration = (ArrayList<GPChromosome>) newPop;
        
        if(Configuration.ELITISM_SIZE == 1){
            nextGeneration.remove(random.nextInt(nextGeneration.size()));
            nextGeneration.add(bestChromo);
        }
        if(Configuration.ELITISM_SIZE > 1){
            for(int i = 0; i < Configuration.ELITISM_SIZE; i++)
                nextGeneration.remove(random.nextInt(nextGeneration.size()));
            for(int i = 0; i < Configuration.ELITISM_SIZE; i++)
                nextGeneration.add(eliteChromo.get(i));
        }

        return nextGeneration;
    }

    private void print(List<GPChromosome> pop, List<Double> fitness, int gen) {
        System.out.println("\nNova Geração ("+gen+" de "+Configuration.MAX_GENERATION+"):");
        for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
            System.out.println(i+" -> "+pop.get(i).getSyntaxTree().print() + " -> " + fitness.get(i));
        }
        double curGenBest = bestFitness.get(gen);
        System.out.println("Overall Best: "+bestChromo.getSyntaxTree().print()+" -> "+bestFit+ " (Current Generation best -> "+curGenBest+")");
    }

    public ArrayList<Double> getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(ArrayList<Double> bestFitness) {
        this.bestFitness = bestFitness;
    }

    public ArrayList<Double> getMeanFitness() {
        return meanFitness;
    }

    public void setMeanFitness(ArrayList<Double> meanFitness) {
        this.meanFitness = meanFitness;
    }

    public ArrayList<Double> getWorstFitness() {
        return worstFitness;
    }

    public void setWorstFitness(ArrayList<Double> worstFitness) {
        this.worstFitness = worstFitness;
    }

    public GPChromosome getBestChromo() {
        return bestChromo;
    }

    public void setBestChromo(GPChromosome bestChromo) {
        this.bestChromo = bestChromo;
    }

    public double getBestFit() {
        return bestFit;
    }

    public void setBestFit(double bestFit) {
        this.bestFit = bestFit;
    }
    
}
