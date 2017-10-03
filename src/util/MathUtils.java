/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Kurumin
 */
public class MathUtils {
    
    public static Double mean(List list){
        Double avg = 0.0;
        for (Iterator it = list.iterator(); it.hasNext();) {
            Double element = (Double) it.next();
            avg += element;
        }
        avg = avg / (double) list.size();
        return avg;
    }
    
    public static Double variance(List list){
        Double variance;
        double mean = mean(list);
        double temp = 0;
        for (Iterator it = list.iterator(); it.hasNext();) {
            Double element = (Double) it.next();
            temp += (element-mean)*(element-mean);
        }
            
        variance = temp/(list.size()-1);
        if(Double.isNaN(variance)){
            System.err.println("NaN "+list.size());
        }
        return variance;
    }
    
    public static Double stdDev(List list){
        return Math.sqrt(variance(list));
    }
}
