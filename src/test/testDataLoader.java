/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;
import util.DataLoader;


public class testDataLoader {

    public static void main(String[] args)  {    
        String filename = "keijzer-10-train"; // <- Nome do Arquivo
        
        int inputSize = 2; // <- Precisa ser configurado
        int idealSize = 1; // <- Precisa ser configurado
        
        DataLoader trainData = new DataLoader(inputSize, idealSize);
        trainData.load(filename, true);
    }
    
}
