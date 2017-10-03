/** *****************************************************************************
 * Copyright 2012 Yuriy Lagodiuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************** */
package genetic_programming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import syntax_tree.Context;
import syntax_tree.Expression;
import syntax_tree.SyntaxTreeUtils;
import syntax_tree.functions.Function;

public class GPChromosome implements Comparable{

    private Expression syntaxTree;

    private Context context;

    private Fitness fitnessFunction;

    private Random random = new Random();
    
    private double fitnessValue;
    
    public GPChromosome(Context context, Fitness fitnessFunction, Expression syntaxTree) {
        this.context = context;
        this.fitnessFunction = fitnessFunction;
        this.syntaxTree = syntaxTree;
        this.fitnessValue = Double.MAX_VALUE;
    }
    
    public GPChromosome(Context context, Fitness fitnessFunction, Expression syntaxTree, double value) {
        this(context, fitnessFunction, syntaxTree);
        this.fitnessValue = value;
    }

    public List<GPChromosome> crossover(GPChromosome anotherChromosome) {
        List<GPChromosome> ret = new ArrayList<GPChromosome>(2);

        GPChromosome thisClone = new GPChromosome(this.context, this.fitnessFunction, this.syntaxTree.clone());
        GPChromosome anotherClone = new GPChromosome(this.context, this.fitnessFunction, anotherChromosome.syntaxTree.clone());

        Expression thisRandomNode = this.getRandomNode(thisClone.syntaxTree);
        Expression anotherRandomNode = this.getRandomNode(anotherClone.syntaxTree);

        Expression thisRandomSubTreeClone = thisRandomNode.clone();
        Expression anotherRandomSubTreeClone = anotherRandomNode.clone();

        this.swapNode(thisRandomNode, anotherRandomSubTreeClone);
        this.swapNode(anotherRandomNode, thisRandomSubTreeClone);

        ret.add(thisClone);
        ret.add(anotherClone);

        thisClone.optimizeTree();
        anotherClone.optimizeTree();

        return ret;
    }

    public GPChromosome mutate() {
        GPChromosome ret = new GPChromosome(this.context, this.fitnessFunction, this.syntaxTree.clone());

        int type = this.random.nextInt(7);
        //System.out.println("Mutação tipo "+type);
        switch (type) {
            case 0:
                ret.mutateByRandomChangeOfFunction();
                break;
            case 1:
                ret.mutateByRandomChangeOfChild();
                break;
            case 2:
                ret.mutateByRandomChangeOfNodeToChild();
                break;
            case 3:
                ret.mutateByReverseOfChildsList();
                break;
            case 4:
                ret.mutateByRootGrowth();
                break;
            case 5:
                ret.syntaxTree = SyntaxTreeUtils.createValidTree(Configuration.TREE_DEPTH, this.context);
                break;
            case 6:
                ret.mutateByReplaceEntireTreeWithAnySubTree();
                break;
        }

        ret.optimizeTree();
        return ret;
    }

    private void mutateByReplaceEntireTreeWithAnySubTree() {
        this.syntaxTree = this.getRandomNode(this.syntaxTree);
    }

    private void mutateByRootGrowth() {
        Function function = this.context.getRandomNonTerminalFunction(); // cria uma function não terminal aleatoria
        Expression newRoot = new Expression(function); // transforma a function numa subarvore
        newRoot.addChild(this.syntaxTree); // adiciona a raiz da arvore como filha da function
        for (int i = 1; i < function.argumentsCount(); i++) {
            newRoot.addChild(SyntaxTreeUtils.createTree(0, this.context)); // se houver mais argumentos, preenche com nós terminais aleatórios
        }
        for (int i = 0; i < function.argumentsCount(); i++) {
            newRoot.addCoefficient(this.context.getRandomValue()); // se houver coeficientes, adiciona aleatoriamente
        }
        this.syntaxTree = newRoot; // agora a function passa a ser a raiz da arvore
    }

    private void mutateByRandomChangeOfFunction() {
        Expression mutatingNode = this.getRandomNode(this.syntaxTree);

        Function oldFunction = mutatingNode.getFunction();
        Function newFunction = null;

        // trying to avoid case, when newFunction == oldFunction
        // hope, that in one of 3 iterations - we'll get
        // newFunction which != oldFunction
        for (int i = 0; i < 3; i++) {
            if (this.random.nextDouble() > 0.5) {
                newFunction = this.context.getRandomNonTerminalFunction();
            } else {
                newFunction = this.context.getRandomTerminalFunction();
            }

            if (newFunction != oldFunction) {
                break;
            }
        }

        mutatingNode.setFunction(newFunction);

        if (newFunction.isVariable()) {
            mutatingNode.setVariable(this.context.getRandomVariableName());
        }

        int functionArgumentsCount = newFunction.argumentsCount();
        int mutatingNodeChildsCount = mutatingNode.getChilds().size();

        if (functionArgumentsCount > mutatingNodeChildsCount) {
            for (int i = 0; i < ((functionArgumentsCount - mutatingNodeChildsCount) + 1); i++) {
                mutatingNode.getChilds().add(SyntaxTreeUtils.createTree(1, this.context));
            }
        } else if (functionArgumentsCount < mutatingNodeChildsCount) {
            List<Expression> subList = new ArrayList<Expression>(functionArgumentsCount);
            for (int i = 0; i < functionArgumentsCount; i++) {
                subList.add(mutatingNode.getChilds().get(i));
            }
            mutatingNode.setChilds(subList);
        }

        int functionCoefficientsCount = newFunction.coefficientsCount();
        int mutatingNodeCoefficientsCount = mutatingNode.getCoefficientsOfNode().size();
        if (functionCoefficientsCount > mutatingNodeCoefficientsCount) {
            for (int i = 0; i < ((functionCoefficientsCount - mutatingNodeCoefficientsCount) + 1); i++) {
                mutatingNode.addCoefficient(this.context.getRandomValue());
            }
        } else if (functionCoefficientsCount < mutatingNodeCoefficientsCount) {
            List<Double> subList = new ArrayList<Double>(functionCoefficientsCount);
            for (int i = 0; i < functionCoefficientsCount; i++) {
                subList.add(mutatingNode.getCoefficientsOfNode().get(i));
            }
            mutatingNode.setCoefficientsOfNode(subList);
        }
    }

    private void mutateByReverseOfChildsList() {
        Expression mutatingNode = this.getRandomNode(this.syntaxTree);
        Function mutatingNodeFunction = mutatingNode.getFunction();

        if ((mutatingNode.getChilds().size() > 1)
                && (!mutatingNodeFunction.isCommutative())) {

            Collections.reverse(mutatingNode.getChilds());

        } else {
            this.mutateByRandomChangeOfFunction();
        }
    }

    private void mutateByRandomChangeOfChild() {
        Expression mutatingNode = this.getRandomNode(this.syntaxTree);

        if (!mutatingNode.getChilds().isEmpty()) {

            int indx = this.random.nextInt(mutatingNode.getChilds().size());

            mutatingNode.getChilds().set(indx, SyntaxTreeUtils.createTree(1, this.context));

        } else {
            this.mutateByRandomChangeOfFunction();
        }
    }

    private void mutateByRandomChangeOfNodeToChild() {
        Expression mutatingNode = this.getRandomNode(this.syntaxTree);

        if (!mutatingNode.getChilds().isEmpty()) {

            int indx = this.random.nextInt(mutatingNode.getChilds().size());

            Expression child = mutatingNode.getChilds().get(indx);

            this.swapNode(mutatingNode, child.clone());

        } else {
            this.mutateByRandomChangeOfFunction();
        }
    }

    private Expression getRandomNode(Expression tree) {
        List<Expression> allNodesOfTree = tree.getAllNodesAsList();
        int allNodesOfTreeCount = allNodesOfTree.size();
        int indx = this.random.nextInt(allNodesOfTreeCount);
        return allNodesOfTree.get(indx);
    }

    private void swapNode(Expression oldNode, Expression newNode) {
        oldNode.setChilds(newNode.getChilds());
        oldNode.setFunction(newNode.getFunction());
        oldNode.setCoefficientsOfNode(newNode.getCoefficientsOfNode());
        oldNode.setVariable(newNode.getVariable());
    }

    public void optimizeTree() {
        //this.optimizeTree(70);
        this.optimizeTree(0);
    }

    public void optimizeTree(int iterations) {

        SyntaxTreeUtils.cutTree(this.syntaxTree, this.context, Configuration.TREE_DEPTH);
        SyntaxTreeUtils.simplifyTree(this.syntaxTree, this.context);
        
        if(!SyntaxTreeUtils.hasVariableNode(this.syntaxTree)){
            this.syntaxTree = SyntaxTreeUtils.createValidTree(Configuration.TREE_DEPTH, context);
            optimizeTree(0);
        }
        
        /*
        List<Double> coefficientsOfTree = this.syntaxTree.getCoefficientsOfTree();
        
        if (coefficientsOfTree.size() > 0) {
            CoefficientsChromosome initialChromosome = new CoefficientsChromosome(coefficientsOfTree, 0.6, 0.8);
            Population<CoefficientsChromosome> population = new Population<CoefficientsChromosome>();
            for (int i = 0; i < 5; i++) {
                population.addChromosome(initialChromosome.mutate());
            }
            population.addChromosome(initialChromosome);

            Fitness<CoefficientsChromosome, Double> fit = new CoefficientsFitness();

            GeneticAlgorithm<CoefficientsChromosome, Double> env = new GeneticAlgorithm<GPChromosome.CoefficientsChromosome, Double>(population, fit);

            env.evolve(iterations);

            List<Double> optimizedCoefficients = env.getBest().getCoefficients();

            this.syntaxTree.setCoefficientsOfTree(optimizedCoefficients);
        }*/
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Expression getSyntaxTree() {
        return this.syntaxTree;
    }
    
    
    /*
    private class CoefficientsChromosome implements Chromosome<CoefficientsChromosome>, Cloneable {

        private double pMutation;

        private double pCrossover;

        private List<Double> coefficients;

        public CoefficientsChromosome(List<Double> coefficients, double pMutation, double pCrossover) {
            this.coefficients = coefficients;
            this.pMutation = pMutation;
            this.pCrossover = pCrossover;
        }

        @Override
        public List<CoefficientsChromosome> crossover(CoefficientsChromosome anotherChromosome) {
            List<CoefficientsChromosome> ret = new ArrayList<GPChromosome.CoefficientsChromosome>(2);

            CoefficientsChromosome thisClone = this.clone();
            CoefficientsChromosome anotherClone = anotherChromosome.clone();

            for (int i = 0; i < thisClone.coefficients.size(); i++) {
                if (GPChromosome.this.random.nextDouble() > this.pCrossover) {
                    thisClone.coefficients.set(i, anotherChromosome.coefficients.get(i));
                    anotherClone.coefficients.set(i, this.coefficients.get(i));
                }
            }
            ret.add(thisClone);
            ret.add(anotherClone);

            return ret;
        }

        @Override
        public CoefficientsChromosome mutate() {
            CoefficientsChromosome ret = this.clone();
            for (int i = 0; i < ret.coefficients.size(); i++) {
                if (GPChromosome.this.random.nextDouble() > this.pMutation) {
                    double coeff = ret.coefficients.get(i);
                    coeff += GPChromosome.this.context.getRandomMutationValue();
                    ret.coefficients.set(i, coeff);
                }
            }
            return ret;
        }

        @Override
        protected CoefficientsChromosome clone() {
            List<Double> ret = new ArrayList<Double>(this.coefficients.size());
            for (double d : this.coefficients) {
                ret.add(d);
            }
            return new CoefficientsChromosome(ret, this.pMutation, this.pCrossover);
        }

        public List<Double> getCoefficients() {
            return this.coefficients;
        }

    }

    private class CoefficientsFitness implements Fitness<CoefficientsChromosome, Double> {

        @Override
        public Double calculate(CoefficientsChromosome chromosome) {
            GPChromosome.this.syntaxTree.setCoefficientsOfTree(chromosome.getCoefficients());
            return GPChromosome.this.fitnessFunction.calculate(GPChromosome.this);
        }

    }*/

    public Fitness getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(Fitness fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    @Override
    public int compareTo(Object t) {
        GPChromosome comp = (GPChromosome) t;
        return Double.compare(this.fitnessValue, comp.getFitnessValue());
    }
}
