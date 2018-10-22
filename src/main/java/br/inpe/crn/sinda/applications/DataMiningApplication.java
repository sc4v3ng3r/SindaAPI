/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications;

import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.network.SindaDataFetcher;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author scavenger
 * 
 * UTILIZAR OS ITERATORS NO PROCESSO DE CRIACAO DE SUB-LISTAS
 * 
 */
public class DataMiningApplication {

    public static final String DIR = "files";
    public static final File m_filesDirectory = new File(DIR);
    
    private static final PcdDataMiningTask.TaskListener listener = new PcdDataMiningTask.TaskListener() {

        @Override
        public void taskFinished(List<Pcd> pcdList) {
            System.out.println("DONE!");
        }

    };

    public static final void main(String args[]) {
        if (!m_filesDirectory.exists()) {
            m_filesDirectory.mkdir();
        }

        SindaDataFetcher dataFetcher = new SindaDataFetcher();

        List<Pcd> pcdList = dataFetcher.getPcdListWithMinimumInfo();

        // divisao de listas!
        int numberOfParts = 4;
        List< List<Pcd> > myLists = splitInSublists( numberOfParts, pcdList );

        
        ExecutorService executor = Executors.newFixedThreadPool( numberOfParts );
        ListIterator< List<Pcd> > iterator = myLists.listIterator();
       
        while (iterator.hasNext()) {
            List<Pcd> list = iterator.next();
            iterator.remove();
            /*
                Criando uma nova sublista aqui e passando esta para a thread, evita o problema de concorrencia
                e a existencia de memoria leaks, como tambem melhora o trabalho do collector garbage la na frente
                quando os dados em memoria ja foram salvos em disco e procisamos liberar a memoria principal.
            */
            PcdDataMiningTask task = new PcdDataMiningTask( new ArrayList<>(list),  listener);
            executor.execute( task );
        }
        
        
        System.out.println("executor shutting down!");
        executor.shutdown();
        pcdList.clear();
    }

    /* UTILIZAR ITERATOR NA LOGICA DESSE METODO,
       JA MELHORA O DESEMPENHO
     */
    public static List< List<Pcd>> splitInSublists(int numberOfSublists, List<Pcd> list) {
        List< List<Pcd> > listOfLists = new ArrayList<>();
        int portion = (list.size() / numberOfSublists);
        int startIndex = 0;
        int endIndex = portion;
        int total = 0;

        System.out.println( "TOTAL ITENS: " + list.size() );
        System.out.println( "ITENS POR SUBLIST: " + portion );

        for (int i = 0; i < numberOfSublists; i++) {
            List<Pcd> ref = list.subList(startIndex, endIndex);
            total += ref.size();

            listOfLists.add(ref);
            System.out.println("NEW SUBLIST WITH " + startIndex + " and " + (endIndex - 1));
            
            startIndex = endIndex;
            endIndex += (portion + 1);

            if ( endIndex > list.size() ) {
                endIndex = list.size();
            }
            
        }
        System.out.println("TOTAL: " + total);
        return listOfLists;
    }

}
