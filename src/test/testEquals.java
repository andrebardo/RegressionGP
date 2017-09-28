/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.List;
import syntax_tree.Expression;
import syntax_tree.SyntaxTreeUtils;
import syntax_tree.functions.Add;
import syntax_tree.functions.Constant;
import syntax_tree.functions.Sub;
import syntax_tree.functions.Variable;

/**
 *
 * @author Kurumin
 */
public class testEquals {
    
    public static void main (String args[]){
        Expression add1 = new Expression(new Add());
        Expression add2 = new Expression(new Add());
        System.out.println("Add1 == Add2: "+SyntaxTreeUtils.compareExpressions(add1, add2));
        
        Expression var1 = new Expression(new Variable());
        Expression var2 = new Expression(new Variable());
        System.out.println("Var1 == Var2 (null, null): "+SyntaxTreeUtils.compareExpressions(var1, var2));
        var1.setVariable("x1");
        System.out.println("Var1 == Var2 (x, null): "+SyntaxTreeUtils.compareExpressions(var1, var2));
        var2.setVariable("x1");
        System.out.println("Var1 == Var2 (x, x): "+SyntaxTreeUtils.compareExpressions(var1, var2));
        var2.setVariable("y");
        System.out.println("Var1 == Var2 (x, y): "+SyntaxTreeUtils.compareExpressions(var1, var2));
        
        Expression con1 = new Expression(new Constant());
        Expression con2 = new Expression(new Constant());
        System.out.println("Con1 == Con2 (null, null): "+SyntaxTreeUtils.compareExpressions(con1, con2));
        con1.addCoefficient(1.0);
        System.out.println("Con1 == Con2 (1.0, null): "+SyntaxTreeUtils.compareExpressions(con1, con2));
        con2.addCoefficient(1.0);
        System.out.println("Con1 == Con2 (1.0, 1.0): "+SyntaxTreeUtils.compareExpressions(con1, con2));
        //compareCoefficients(con1.getCoefficientsOfNode(), con2.getCoefficientsOfNode());
        
        add1.addChild(con1);
        add1.addChild(con2);
        System.out.println(add1.print()+" == add2: "+SyntaxTreeUtils.compareExpressions(add1, add2));
        add2.addChild(con1);
        add2.addChild(con1);
        System.out.println(add1.print()+" == "+add2.print()+": "+SyntaxTreeUtils.compareExpressions(add1, add2));
        con2.removeCoefficients();
        con2.addCoefficient(2.0);
        System.out.println(add1.print()+" == "+add2.print()+": "+SyntaxTreeUtils.compareExpressions(add1, add2));
        
        add1.removeChilds();
        add1.addChild(con1);
        add1.addChild(var1);
        System.out.println(add1.print()+" == "+add2.print()+": "+SyntaxTreeUtils.compareExpressions(add1, add2));
        add2.removeChilds();
        add2.addChild(var1);
        add2.addChild(con1);
        System.out.println(add1.print()+" == "+add2.print()+": "+SyntaxTreeUtils.compareExpressions(add1, add2));
        add2.removeChilds();
        add2.addChild(con1);
        add2.addChild(var1);
        System.out.println(add1.print()+" == "+add2.print()+": "+SyntaxTreeUtils.compareExpressions(add1, add2));
        add2.removeChilds();
        add2.addChild(con1);
        add2.addChild(var2);
        System.out.println(add1.print()+" == "+add2.print()+": "+SyntaxTreeUtils.compareExpressions(add1, add2));
    }
    
    /*public static boolean compareCoefficients(List<Double> a, List<Double> b){
        boolean equals = true;
        if(a.size() == b.size()){
            System.out.println("Equal size: "+a.size()+", "+b.size());
            for(int i = 0; i < a.size(); i++){
                if(a.get(i).doubleValue() != b.get(i).doubleValue()){
                    System.err.println("Diff values: "+a.get(i)+", "+b.get(i));
                    return false;
                }
            }
        } else {
            System.err.println("Diff size: "+a.size()+", "+b.size());
            return false;
        }
        return equals;
    }*/
    
}
