/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regressaogp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;


public class RegressaoGP {

    public static void main(String[] args) throws IOException {
        File root = new File(".");
        File data = new File(root, "\\data\\keijzer-10-train.csv"); // <- Nome do Arquivo
        
        int inputSize = 2; // <- Precisa ser configurado
        int idealSize = 1; // <- Precisa ser configurado

        if(data.exists()){
            System.out.println("Lendo "+data.getCanonicalPath()+"...");
            BasicMLDataSet dataSet = new BasicMLDataSet(
                    EncogUtility.loadCSV2Memory(
                            data.getCanonicalPath(), inputSize, idealSize,
                            false, CSVFormat.DECIMAL_POINT, false)); // Carrega o arquivo csv para o formato DataSet
            System.out.println("Entradas lidas: "+dataSet.size()+".");
            ArrayList<MLDataPair> list = (ArrayList<MLDataPair>) dataSet.getData(); // Converte o DataSet para uma List de pares input-ideal
            for(MLDataPair pair: list){
                System.out.println("Input: "+Arrays.toString(pair.getInputArray())+" / "
                                 + "Ideal: "+Arrays.toString(pair.getIdealArray()));
            }
        }
        else{
            System.out.println(data.getCanonicalPath()+" não existe.");
        }
    }
    
}
