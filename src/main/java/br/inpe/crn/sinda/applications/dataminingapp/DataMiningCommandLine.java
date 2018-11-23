/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

/**
 *
 * @author scavenger
 */
public class DataMiningCommandLine {

    private static final String INVALID_COMMAND = "Comando inválido";
    private static Scanner m_scanner = new Scanner(System.in);
    private static MiningProcessManager dataMiningProcessManager = new MiningProcessManager();
    private static PcdsUpdateProcessManager pcdsUpdateManager = new PcdsUpdateProcessManager();
    private static DataMiningApplicationSettings m_settings = DataMiningApplicationSettings.getInstance();
    private static ExtractorLogger m_logger =ExtractorLogger.getInstance();
    
    public static final void main(String args[]) {

        String command = "";
        System.out.println("Digite help para ajuda.\n");
        do {
            command = readCommand();
            parseCommands(command);

        } while ((command.compareTo("exit")) != 0);

    }

    private static String readCommand() {
       
        System.out.print("SindaExtractor >> ");
        return m_scanner.nextLine();
    }

    private static void parseCommands(String command) {
        List<String> commands = Arrays.asList(command.split(" "));
        String process;
        ListIterator<String> iterator = commands.listIterator();

        while (iterator.hasNext()) {

            switch (iterator.next()) {

                case "help":
                    help();
                    break;

                case "exit":
                    if (dataMiningProcessManager.isRunning()) {
                        dataMiningProcessManager.stop();
                    }
                    pcdsUpdateManager.stop();
                    // Se a atualizacao estar rodando pare!
                    break;

                case "start":
                    process = iterator.next();
                    if ((process != null) && (!process.isEmpty())) {
                        switch (process) {
                            case "data-mining":
                                System.out.println("data mining");
                                if (dataMiningProcessManager.start()) {
                                    System.out.println("Sinda data-mining inicializado!");
                                } else {
                                    System.out.println("Sinda data-mining ja foi inicializado!");
                                }
                                break;

                            case "data-update":
                                System.out.println("dataUpdate");
                                pcdsUpdateManager.start();
                                System.out.println("Sinda data-mining inicializado!");

                                /*
                              else {
                                 System.out.println("Sinda data-mining ja foi inicializado!");
                                }*/
                                break;
                        }
                    }

                    break; // end case start!

                case "stop":
                    process = iterator.next();
                    if (process.compareTo("data-mining") == 0) {
                        dataMiningProcessManager.stop();
                    } else if (process.compareTo("data-update") == 0) {
                        pcdsUpdateManager.stop();
                    }
                    break;

                case "show":
                    break;

                case "set":
                    /*sub-commands*/
                    break;

                case "logs":
                    String size = iterator.next();
                    if(size == null)
                        return;
                    try{
                        int total = Integer.parseInt(size);
                       String logs =  m_logger.getlastLogs(total);
                       
                       if(logs.isEmpty())
                            System.out.println("Log vazio...");
                       else {
                           System.out.println("");
                           System.out.println(logs );
                       }
                    } 
                    catch(NumberFormatException ex){
                        System.out.println("Parametro inválido");
                     }
                    break;
                    
                default:
                   // System.out.println(DataMiningCommandLine.INVALID_COMMAND);
                    return;
            }
        }

        
        /*
         for(String s: commands)
            System.out.println("YOU TYPED COMMANDS: " + s);
         */
    }
    
    private static void help(){
        String help ="    ========================= SINDA EXTRACTOR PROGRAM =========================\n"
                +               "       help:   Exibe a ajuda\n"
                +               "       exit:     Fecha as tarefas que estão sendo executadas & fecha o programa\n"
                +               "       start <tarefa>:   Inicia uma determinada tarefa:   <data-mining>     <data-update>       <full-mining>\n"
                +               "       stop <tarefa>:    Para uma determinada tarefa:    <data-mining>     <data-update>       <full-mining>\n"
                +               "       status: Exibe o estado atual. Qual tarefa está sendo executada\n"
                +               "       logs <N >: Exibe as N mais recentes linhas de log. O máximo padrão é N = 50;";
        
        System.out.println(help);
    }
}
