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

    public void evolve(Context context, Fitness fitnessFunction) {

        ArrayList<GPChromosome> newPop = (ArrayList<GPChromosome>) createInitialPopulation(context, fitnessFunction);

        for (int generation = 0; generation < Configuration.MAX_GENERATION; generation++) {
            ArrayList<Double> fitness = (ArrayList<Double>) evaluate(newPop);
            print(newPop, fitness);

            ArrayList<GPChromosome> selectedPop = (ArrayList<GPChromosome>) tournamentSelection(newPop, fitness);
            ArrayList<GPChromosome> crossingPop = (ArrayList<GPChromosome>) crossover(selectedPop);
            ArrayList<GPChromosome> mutationPop = (ArrayList<GPChromosome>) mutation(crossingPop);
            newPop = (ArrayList<GPChromosome>) replacement(newPop, mutationPop);
        }
    }

    private List<GPChromosome> createInitialPopulation(Context context, Fitness expFitness) {
        ArrayList<GPChromosome> population = new ArrayList<>();
        for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
            Expression exp = SyntaxTreeUtils.createTree(Configuration.TREE_DEPTH, context);
            GPChromosome chromo = new GPChromosome(context, expFitness, exp);
            population.add(chromo);
        }
        return population;
    }

    private List<Double> evaluate(List<GPChromosome> population) {
        ArrayList<Double> fitness = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            GPChromosome chromo = population.get(i);
            Fitness function = chromo.getFitnessFunction();
            Expression tree = chromo.getSyntaxTree();
            Context context = chromo.getContext();
            double value = function.fitness(tree, context);
            fitness.add(value);
        }
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
                population.get(i).mutate();
            }

            mutants.add(population.get(i));
        }
        return mutants;
    }

    private List<GPChromosome> replacement(List<GPChromosome> oldPop, List<GPChromosome> newPop) {
        ArrayList<GPChromosome> nextGeneration = new ArrayList<>(Configuration.POPULATION_SIZE);

        // 100% replacement
        nextGeneration = (ArrayList<GPChromosome>) newPop;

        return nextGeneration;
    }

    private void print(List<GPChromosome> pop, List<Double> fitness) {
        System.out.println("\nNova População:");
        for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
            System.out.println(pop.get(i).getSyntaxTree().print() + " -> " + fitness.get(i));
        }
    }

}
