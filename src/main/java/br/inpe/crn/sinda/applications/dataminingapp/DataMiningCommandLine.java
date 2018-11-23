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
    private static ExtractorLogger m_logger = ExtractorLogger.getInstance();

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
                    try {
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
                                    break;

                                default:
                                    System.out.println(process + " opção inválida");
                                    break;
                            }
                        }

                    } catch (Exception ex) {
                        System.out.println("Dgite uma tarefa!");
                    }
                    break; // end case start!

                case "stop":
                    try {
                        process = iterator.next();

                        if (process.compareTo("data-mining") == 0) {
                            dataMiningProcessManager.stop();
                        } else if (process.compareTo("data-update") == 0) {
                            pcdsUpdateManager.stop();
                        }
                        break;

                    } catch (Exception ex) {
                    }

                case "show":
                    try {
                        String key = iterator.next();

                        switch (key) {
                            case "settings":
                                System.out.println(m_settings.toString());
                                break;
                        }
                    } catch (Exception ex) {
                    }

                    break;

                case "set":
                    try {
                        String key = iterator.next();
                        String value = "";
                        switch (key) {

                            case "ftp-server":
                                value = iterator.next();
                                m_settings.setFTP_SERVER(value);
                                break;

                            case "ftp-user-login":
                                value = iterator.next();
                                m_settings.setFTP_USER(value);
                                break;

                            case "ftp-user-password":
                                value = iterator.next();
                                m_settings.setFTP_PASSWORD(value);
                                break;

                            case "ftp-port":
                                value = iterator.next();
                                m_settings.setFTP_PORT(Integer.parseInt(value));
                                break;

                            default:
                                break;
                        }
                    } catch (Exception ex) {

                    }

                    /*sub-commands*/
                    break;

                case "log":
                    try {
                        String size = iterator.next();
                        if (size == null) {
                            System.out.println("Digite o numero de linhas que deseja imprimir");
                            return;
                        }
                        try {
                            int total = Integer.parseInt(size);
                            String logs = m_logger.tail2(total);

                            if (logs.isEmpty()) {
                                System.out.println("Log vazio...");
                            } else {
                                System.out.println("");
                                System.out.println(logs);
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Parametro inválido");
                        }
                        break;

                    } catch (Exception ex) {
                    }
                default:
                    return;

            }
        }
    }

    private static void help() {
        String help = "    ========================= SINDA EXTRACTOR PROGRAM =========================\n"
                + "       help:   Exibe a ajuda\n"
                + "       exit:     Fecha as tarefas que estão sendo executadas & fecha o programa [TEM UM LEVE DELAY DEVIDO AS TREADS]\n"
                + "       start <tarefa>:   Inicia uma determinada tarefa:   <data-mining>     <data-update>       <full-mining>\n"
                + "         Exemplo: start data-mining : Para inicializar a mineração\n"
                + "       stop <tarefa>:    Para uma determinada tarefa:    <data-mining>     <data-update>       <full-mining>\n"
                + "       status: Exibe o estado atual. Qual tarefa está sendo executada [NÃO ESTÁ FUNCIONANDO]\n "
                + "        show: Comando utilizado para exibir atuais configurações. Seus sub-parametros são:\n"
                + "               [*] show settings: Exibe o estado atual das configurações dafunçao de mineração de dados\n"
                + "       set: Comando para realizar configurações no arquivo do data-mining\n"
                + "            Suas sub-opções são:\n"
                + "                [*] set ftp-user-login <userlogin>\n"
                + "                [*] set ftp-user-password <password>\n"
                + "                [*] set ftp-server <server ftp>\n"
                + "                [*] set ftp-port <port> <userlogin>\n"
                + "       log <N >: Exibe as N mais recentes linhas de log. Util para acompanhar o que as threads estão fazendo.\n"
                + "  ==================================================================================================\n";

        System.out.println(help);
    }
}
