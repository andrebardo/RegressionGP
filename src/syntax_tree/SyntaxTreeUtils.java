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
package syntax_tree;

import genetic_programming.Configuration;
import java.util.List;
import syntax_tree.functions.Function;

public class SyntaxTreeUtils {
    
    public static Expression createValidTree(int depth, Context context){
        Expression expr;
        do{
            expr = createTree(depth, context);
        }while(!hasVariableNode(expr));
        return expr;
    }
    
    public static Expression createTree(int depth, Context context) {
        double prob = 1 - ((double)depth/(double)Configuration.TREE_DEPTH);
        if (depth > 0) {
            Function f;
            if (Math.random() >= prob) {
                f = context.getRandomNonTerminalFunction();
            } else {
                f = context.getRandomTerminalFunction();
            }

            Expression expr = new Expression(f);

            if (f.argumentsCount() > 0) {
                for (int i = 0; i < f.argumentsCount(); i++) {
                    Expression child = createTree(depth - 1, context);
                    expr.addChild(child);
                }
            } else {
                if (f.isVariable()) {
                    String varName = context.getRandomVariableName();
                    expr.setVariable(varName);
                }
            }

            for (int i = 0; i < f.coefficientsCount(); i++) {
                expr.addCoefficient(context.getRandomValue());
            }

            return expr;

        } else {
            Function f = context.getRandomTerminalFunction();
            Expression expr = new Expression(f);

            if (f.isVariable()) {
                String varName = context.getRandomVariableName();
                expr.setVariable(varName);
            }

            for (int i = 0; i < f.coefficientsCount(); i++) {
                expr.addCoefficient(context.getRandomValue());
            }

            return expr;

        }
    }

    public static void simplifyTree(Expression tree, Context context) {
        if (hasVariableNode(tree)) {
            for (Expression child : tree.getChilds()) {
                simplifyTree(child, context);
            }
        } else {
            double value = tree.eval(context);
            tree.addCoefficient(value);
            tree.removeChilds();
            List<Function> terminalFunctions = context.getTerminalFunctions();
            for (Function f : terminalFunctions) {
                if (f.isNumber()) {
                    tree.setFunction(f);
                    break;
                }
            }
        }
    }

    public static void cutTree(Expression tree, Context context, int depth) {
        if (depth > 0) {
            for (Expression child : tree.getChilds()) {
                cutTree(child, context, depth - 1);
            }
        } else {
            tree.removeChilds();
            tree.removeCoefficients();
            Function func = context.getRandomTerminalFunction();
            tree.setFunction(func);
            if (func.isVariable()) {
                tree.setVariable(context.getRandomVariableName());
            } else {
                tree.addCoefficient(context.getRandomValue());
            }
        }
    }

    public static boolean hasVariableNode(Expression tree) {
        boolean ret = false;

        if (tree.getFunction().isVariable()) {
            ret = true;
        } else {
            for (Expression child : tree.getChilds()) {
                ret = hasVariableNode(child);
                if (ret) {
                    break;
                }
            }
        }

        return ret;
    }
    
    public static boolean compareExpressions(Expression a, Expression b){
        return  compareFunctions(a.getFunction(), b.getFunction()) &&
                compareChilds(a.getChilds(), b.getChilds()) &&
                compareCoefficients(a.getCoefficientsOfNode(), b.getCoefficientsOfNode()) &&
                compareVariables(a.getVariable(), b.getVariable());
    }
    
    public static boolean compareFunctions(Function a, Function b){
        return a.getClass().equals(b.getClass());
    }
    
    public static boolean compareCoefficients(List<Double> a, List<Double> b){
        boolean equals = true;
        if(a.size() == b.size()){
            for(int i = 0; i < a.size(); i++){
                if(a.get(i).doubleValue() != b.get(i).doubleValue())
                    return false;
            }
        } else {
            return false;
        }
        return equals;
    }
 
    public static boolean compareVariables(String a, String b){
        if(a == null && b == null)
            return true;
        else if(a != null && b != null)
            return a.compareTo(b) == 0;
        else return false;
    }
    
    public static boolean compareChilds(List<Expression> a, List<Expression> b){
        boolean equals = true;
        if(a.size() == b.size()){
            for(int i = 0; i < a.size(); i++){
                if(!compareExpressions(a.get(i), b.get(i)))
                    return false;
            }
        } else {
            return false;
        }
        return equals;
    }
}
