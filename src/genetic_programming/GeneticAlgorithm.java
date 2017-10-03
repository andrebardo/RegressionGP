/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic_programming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import syntax_tree.Context;
import syntax_tree.Expression;
import syntax_tree.SyntaxTreeUtils;

/**
 *
 * @author Kurumin
 */
public class GeneticAlgorithm {

    private ArrayList<Double> bestFitness;      // fitness do melhor invidivuo a cada geração
    private ArrayList<Double> meanFitness;      // fitness médio a cada geração
    private ArrayList<Double> worstFitness;     // fitness do pior invidivuo a cada geração
    private ArrayList<Double> repeatPerGen;     // percentual de individuos repetidos a cada geração
    private ArrayList<Double> highFitPercent;   // percentual de indivíduos piores que a média
    private ArrayList<Double> lowFitPercent;    // percentual de indivíduos melhores que a média

    private HashMap<String, Integer> repeatMap; // map para contar quantos são repetidos

    //private ArrayList<Integer> eliteRank;       // ordenação da elite
    //private ArrayList<Double> eliteFitness;     // fitness do conjunto elite
    private ArrayList<GPChromosome> eliteChromo;// conjunto elite

    private GPChromosome bestChromo;            // melhor global
    private double bestFit;                     // melhor global

    public GeneticAlgorithm() {
        this.bestFitness = new ArrayList<>();
        this.meanFitness = new ArrayList<>();
        this.worstFitness = new ArrayList<>();

        this.highFitPercent = new ArrayList<>();
        this.lowFitPercent = new ArrayList<>();
        this.repeatPerGen = new ArrayList<>();
        this.repeatMap = new HashMap<>();

        this.eliteChromo = new ArrayList<>();
        //this.eliteFitness = new ArrayList<>();  
        //this.eliteRank = new ArrayList<>();

        this.bestFit = Double.MAX_VALUE;
    }

    public void evolve(Context context, Fitness fitnessFunction, boolean print) {

        ArrayList<GPChromosome> newPop = (ArrayList<GPChromosome>) createInitialPopulation(context, fitnessFunction);
        bestChromo = newPop.get(0);
        evaluate(newPop, print); // primeira avaliação ocorre fora do loop
        
        for (int generation = 0; generation < Configuration.MAX_GENERATION; generation++) {
            //ArrayList<Double> fitness = (ArrayList<Double>) evaluate(newPop);
            if (print) {
                System.out.println("\nGeração " + generation + " de " + Configuration.MAX_GENERATION + ":");
                //print(newPop, generation);
            }
            ArrayList<GPChromosome> selectedPop = (ArrayList<GPChromosome>) tournamentSelection(newPop); // seleção
            ArrayList<GPChromosome> crossingPop = (ArrayList<GPChromosome>) crossover(selectedPop); // cruzamento
            ArrayList<GPChromosome> mutationPop = (ArrayList<GPChromosome>) mutation(crossingPop); // mutação
            evaluate(mutationPop, print); // avaliaçao
            elitism(newPop, print, generation); // elitismo (configura conjunto elite)
            newPop = (ArrayList<GPChromosome>) replacement(selectedPop, mutationPop, print); // substituição
            statistics(generation, newPop, print);
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

    private void evaluate(List<GPChromosome> population, boolean print) {
        
        for (int i = 0; i < population.size(); i++) {
            GPChromosome chromo = population.get(i);
            Fitness function = chromo.getFitnessFunction();
            Expression tree = chromo.getSyntaxTree();
            Context context = chromo.getContext();
            double value = function.fitness(tree, context); // calculo da função objetivo

            // Individuos que resultem em NaN ou Infinity serão refinados até que um valor válido seja encontrado
            // O refinamento nada mais é que aplicar uma mutação iterativamente até encontrar um valor válido
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                chromo = refinement(chromo, value);
                tree = chromo.getSyntaxTree();
                value = function.fitness(tree, context);
            }
            chromo.setFitnessValue(value);     
        }
    }

    private GPChromosome refinement(GPChromosome chromo, double value) {
        while (Double.isNaN(value) || Double.isInfinite(value)) {
            chromo = chromo.mutate();
            Fitness function = chromo.getFitnessFunction();
            Expression tree = chromo.getSyntaxTree();
            Context context = chromo.getContext();
            value = function.fitness(tree, context); // calculo da função objetivo
        }
        return chromo;
    }

    // só há necessidade de tratar o conjunto elite se ele for maior que 1
    // caso contrário, já se tem bestFit e bestChromo
    private void elitism(List<GPChromosome> population, boolean print, int gen) {
        if (Configuration.ELITISM_SIZE > 1) {
            Collections.sort(population); // ordena a população 
            eliteChromo.clear();
            for (int i = 0; i < Configuration.ELITISM_SIZE; i++) {
                eliteChromo.add(population.get(i));
            }
            if (print) {
                System.out.println("\nElite " + gen + ":");
                print(eliteChromo, /*eliteFitness,*/ gen);
            }
        }
    }
    
    private GPChromosome cloneChromosome(GPChromosome chromo){
        return new GPChromosome(
                chromo.getContext(),
                chromo.getFitnessFunction(),
                chromo.getSyntaxTree().clone(),
                chromo.getFitnessValue());
    }

    private List<GPChromosome> tournamentSelection(List<GPChromosome> population/*, List<Double> fitness*/) {
        ArrayList<GPChromosome> selected = new ArrayList<>(Configuration.POPULATION_SIZE);
        Random random = new Random(System.currentTimeMillis());

        while (selected.size() < Configuration.POPULATION_SIZE) {

            int index = random.nextInt(Configuration.POPULATION_SIZE);
            GPChromosome winner = cloneChromosome(population.get(index));
            double best = population.get(index).getFitnessValue();

            for (int i = 1; i < Configuration.TOURNAMENT_SIZE; i++) {
                index = random.nextInt(Configuration.POPULATION_SIZE);
                if (population.get(index).getFitnessValue() < best) {
                    winner = cloneChromosome(population.get(index));
                    best = population.get(index).getFitnessValue();
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
                mutants.add(cloneChromosome(population.get(i).mutate()));
            } else {
                mutants.add(cloneChromosome(population.get(i)));
            }
        }
        return mutants;
    }

    private void statistics(int gen, ArrayList<GPChromosome> pop, boolean print) {
        if (print) {
            System.out.println("\nEstatísticas " + gen);
        }
        
        repeatMap = new HashMap<>();
        double localBest = Double.MAX_VALUE;
        double worstFit = 0;
        double mean = 0;
        
        for(int i = 0; i < Configuration.POPULATION_SIZE; i++){
            double value = pop.get(i).getFitnessValue();
            if (value < localBest) {
                localBest = value;
                if (value < this.bestFit) { // melhor geral
                    this.bestChromo = new GPChromosome(
                            pop.get(i).getContext(),
                            pop.get(i).getFitnessFunction(),
                            pop.get(i).getSyntaxTree().clone(),
                            value);
                    this.bestFit = value;
                }
            }
            mean += value / (double) pop.size();
            if (value > worstFit) {
                worstFit = value;
            }
            // Calcula o número de individuos repetidos
            if (repeatMap.containsKey(pop.get(i).getSyntaxTree().print())) {
                repeatMap.put(pop.get(i).getSyntaxTree().print(), repeatMap.get(pop.get(i).getSyntaxTree().print()) + 1);
            } else {
                repeatMap.put(pop.get(i).getSyntaxTree().print(), 1);
            }
            if(print){
                System.out.println(i + " -> " + pop.get(i).getSyntaxTree().print() + " -> " + String.format("%.4f", pop.get(i).getFitnessValue()));
            }
        }
        bestFitness.add(localBest); 
        worstFitness.add(worstFit);
        meanFitness.add(mean);
        //System.out.println(String.format("Melhor Fitness: %.4f \nAvg Fitness: %.4f \nPior Fitness: %.4f", localBest, mean, worstFit));
        
        int repeat = 0;
        for (Entry<String, Integer> entry : repeatMap.entrySet()) {
            if (entry.getValue() > 1) {
                repeat++;
            }
        }
        double percent = 100 * repeat / (double) Configuration.POPULATION_SIZE;
        this.repeatPerGen.add(percent);
        if (print) {
            System.out.println("Percentual de diversidade: " + String.format("%.2f%%", percent));
            System.out.println("Melhor fitness:   " + String.format("%.4f", this.bestFitness.get(gen)));
            System.out.println("Pior fitness:     " + String.format("%.4f", this.worstFitness.get(gen)));
            System.out.println("Média dos filhos: " + String.format("%.4f", this.meanFitness.get(gen)));
        }
        if (gen > 0) {
            // o enunciado pede para comparar com os pais
            // nesse caso é preciso comparar com a geração anterior
            double meanFather = this.meanFitness.get(gen - 1);
            int low = 0, high = 0;
            for (int i = 0; i < pop.size(); i++) {
                if (pop.get(i).getFitnessValue() < meanFather) {
                    low++;
                } else if (pop.get(i).getFitnessValue() > meanFather) {
                    high++;
                }
            }
            double highPercent = 100 * high / (double) Configuration.POPULATION_SIZE;
            double lowPercent = 100 * low / (double) Configuration.POPULATION_SIZE;
            if (print) {
                System.out.println("Média dos pais:  " + String.format("%.4f", meanFather));
                System.out.println("Percentual de indivíduos melhores que a média dos pais: " + String.format("%.2f%%", lowPercent));
                System.out.println("Percentual de indivíduos piores que a média dos pais: " + String.format("%.2f%%", highPercent));
            }
            this.lowFitPercent.add(lowPercent);
            this.highFitPercent.add(highPercent);
        } else {
            this.lowFitPercent.add(0.0);
            this.highFitPercent.add(0.0);
        }
    }

    private List<GPChromosome> replacement(List<GPChromosome> fathers, List<GPChromosome> sons, boolean print) {
        ArrayList<GPChromosome> nextGeneration = new ArrayList<>(Configuration.POPULATION_SIZE);
        Random random = new Random(System.currentTimeMillis());
        // 100% replacement
        //nextGeneration = (ArrayList<GPChromosome>) newPop;
        
        // Tolerance Based Replacement
        double percentOfSons = 0;
        for(int i = 0; i < fathers.size(); i++){
            // se o fitness do filho por pior que 1.5x o pai, o filho é rejeitado
            if(sons.get(i).getFitnessValue() <= (fathers.get(i).getFitnessValue() * Configuration.TOLERANCE)){
                nextGeneration.add(cloneChromosome(sons.get(i)));
                percentOfSons++;
            }
            else
                nextGeneration.add(cloneChromosome(fathers.get(i)));
        }
        percentOfSons = 100 * percentOfSons / (double) fathers.size();
        if(print) System.out.println("Filhos Fisicamente Removidos: "+String.format("%.2f%%", (100-percentOfSons)));
        
        // Elite Replacement
        if (Configuration.ELITISM_SIZE == 1) {
            nextGeneration.remove(random.nextInt(nextGeneration.size()));
            nextGeneration.add(bestChromo);
        }
        if (Configuration.ELITISM_SIZE > 1) {
            for (int i = 0; i < Configuration.ELITISM_SIZE; i++) {
                nextGeneration.remove(random.nextInt(nextGeneration.size()));
            }
            for (int i = 0; i < Configuration.ELITISM_SIZE; i++) {
                nextGeneration.add(eliteChromo.get(i));
            }
        }

        return nextGeneration;
    }

    private void print(List<GPChromosome> pop, /*List<Double> fitness,*/ int gen) {
        //
        for (int i = 0; i < pop.size(); i++) {
            System.out.println(i + " -> " + pop.get(i).getSyntaxTree().print() + " -> " + String.format("%.4f", pop.get(i).getFitnessValue()));
        }
        //double curGenBest = bestFitness.get(gen);
        //System.out.println("Overall Best: "+bestChromo.getSyntaxTree().print()+" -> "+bestFit+ " (Current Generation best -> "+curGenBest+")");
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

    public ArrayList<Double> getRepeatPerGen() {
        return repeatPerGen;
    }

    public void setRepeatPerGen(ArrayList<Double> repeatPerGen) {
        this.repeatPerGen = repeatPerGen;
    }

    public ArrayList<Double> getHighFitPercent() {
        return highFitPercent;
    }

    public void setHighFitPercent(ArrayList<Double> highFitPercent) {
        this.highFitPercent = highFitPercent;
    }

    public ArrayList<Double> getLowFitPercent() {
        return lowFitPercent;
    }

    public void setLowFitPercent(ArrayList<Double> lowFitPercent) {
        this.lowFitPercent = lowFitPercent;
    }

}
