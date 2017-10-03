/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import genetic_programming.Configuration;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kurumin
 */
public class DataWriter {
    
    public static void write(String filename,
            List<Double> bestFit,  List<Double> stdBestFit,
            List<Double> meanFit,  List<Double> stdMeanFit,
            List<Double> worstFit, List<Double> stdWorstFit,
            List<Double> repeated, List<Double> stdRepeated,
            List<Double> lowerThan, List<Double> stdLower,
            List<Double> higherThan, List<Double> stdHigher){
        ArrayList<String> buffer = new ArrayList<>();
        buffer.add("Gen\t Best\t B_std_dev\t Mean\t M_std_dev\t Worst\t W_std_dev\t Repeated\t R_std_dev\t Lower\t L_std_dev\t Higher\t H_std_dev");
        for(int i = 0; i < bestFit.size(); i++){
            String line = String.format("%s\t %.4f\t %.4f\t %.4f\t %.4f\t %.4f\t %.4f\t %.4f%%\t %.4f%%\t %.4f%%\t %.4f%%\t %.4f%%\t %.4f%%", i,
                    bestFit.get(i),  stdBestFit.get(i),
                    meanFit.get(i),  stdMeanFit.get(i),
                    worstFit.get(i), stdWorstFit.get(i),
                    repeated.get(i), stdRepeated.get(i),
                    lowerThan.get(i),stdLower.get(i),
                    higherThan.get(i), stdHigher.get(i));
            buffer.add(line);
        }
        
        write(filename, buffer); 
    }
    
    private static void write(String filename, ArrayList<String> buffer){
        File root = new File(".");
        File file = new File(root, "\\data\\"+Configuration.OUTPUT_DIR_NAME+"\\" + filename + ".txt"); // <- Nome do Arquivo
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for(int i = 0; i < buffer.size(); i++){
                bw.write(buffer.get(i));
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(DataWriter.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
