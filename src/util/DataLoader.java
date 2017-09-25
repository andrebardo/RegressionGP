/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

/**
 *
 * @author Kurumin
 */
public class DataLoader {

    private int inputSize; // <- Precisa ser configurado
    private int idealSize; // <- Precisa ser configurado
    private File file;
    private ArrayList<MLDataPair> data;

    public DataLoader(int inputSize, int idealSize) {
        this.inputSize = inputSize;
        this.idealSize = idealSize;
    }

    public void load(String filename, boolean print) {
        File root = new File(".");
        file = new File(root, "\\data\\" + filename + ".csv"); // <- Nome do Arquivo 
        try {
            if (file.exists()) {
                BasicMLDataSet dataSet = new BasicMLDataSet(
                        EncogUtility.loadCSV2Memory(
                                file.getCanonicalPath(), inputSize, idealSize,
                                false, CSVFormat.DECIMAL_POINT, false)); // Carrega o arquivo csv para o formato DataSet

                data = (ArrayList<MLDataPair>) dataSet.getData(); // Converte o DataSet para uma List de pares input-ideal
                if (print) { // Se print for true, imprime cada linha do arquivo
                    System.out.println("Lendo " + file.getCanonicalPath() + "...");
                    System.out.println("Entradas lidas: " + dataSet.size() + ".");
                    for (MLDataPair pair : data) {
                        System.out.println("Input: " + Arrays.toString(pair.getInputArray()) + " / "
                                         + "Ideal: " + Arrays.toString(pair.getIdealArray()));
                    }
                }
            } else {
                System.out.println(file.getCanonicalPath() + " não existe.");
            }
        } catch (IOException ex) {
            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getInputSize() {
        return inputSize;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    }

    public int getIdealSize() {
        return idealSize;
    }

    public void setIdealSize(int idealSize) {
        this.idealSize = idealSize;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ArrayList<MLDataPair> getData() {
        return data;
    }

    public void setData(ArrayList<MLDataPair> data) {
        this.data = data;
    }
}
