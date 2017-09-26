/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic_programming;

import syntax_tree.Context;
import syntax_tree.Expression;

/**
 *
 * @author Kurumin
 */
public interface Fitness {
    
    double fitness(Expression expression, Context context);
    
}
