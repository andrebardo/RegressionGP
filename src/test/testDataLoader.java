/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

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
