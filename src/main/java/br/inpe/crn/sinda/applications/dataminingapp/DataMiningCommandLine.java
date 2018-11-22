/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import java.util.Scanner;

/**
 *
 * @author scavenger
 */
public class DataMiningCommandLine {

    private static final String INVALID_COMMAND = "Comando inválido";
    private static Scanner m_scanner = new Scanner(System.in);

    public static final void main(String args[]) {

        String command = "";
        
        do {
           
            command = readCommand();
            parseCommands(command);
        
        } while ((command.compareTo("exit")) != 0) ;

    }

    private static String readCommand() {
        System.out.print("SindaExtractor >> ");
        return m_scanner.nextLine();
    }
    
    private static void parseCommands(String command){
        
        System.out.println("YOU TYPED: " + command);
    } 
}
