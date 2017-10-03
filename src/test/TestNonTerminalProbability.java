/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import genetic_programming.Configuration;

/**
 *
 * @author Kurumin
 */
public class TestNonTerminalProbability {
    public static void main(String[] args){
        for(int i = 7; i > 0; i--){
            double prob = 1 - ((double)i/(double)Configuration.TREE_DEPTH);
            System.out.println(prob);
        }
    }
}
