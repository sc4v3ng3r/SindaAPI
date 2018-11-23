/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import static br.inpe.crn.sinda.applications.dataminingapp.DataMiningApplication.splitInSublists;
import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.network.SindaDataFetcher;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author scavenger
 */
public class MiningProcessManager {

    List<PcdDataMiningTask> m_taskList = new ArrayList<>();
    
    public final String FILES_DIRECTORY = "files";
    private boolean isRunning = false;
    public final File m_filesDirectory = new File(FILES_DIRECTORY);
    private static final PcdDataMiningTask.TaskListener listener = new PcdDataMiningTask.TaskListener() {
        
        @Override
        public void taskFinished(List<Pcd> pcdList) {
            System.out.println("DONE!");
        }

    };

    private void changeTasksToState(int state) {
        for (PcdDataMiningTask task : m_taskList) {
            task.setState(state);
        }
    }

    public boolean isRunning(){
        return isRunning;
    }
    
    public void pause() {
        changeTasksToState(PcdDataMiningTask.STATE_PAUSE);
    }

    public boolean start() {
        if (!isRunning) {

            DataMiningApplicationSettings settings = DataMiningApplicationSettings.getInstance();
            settings.saveData();
            settings = null;
            if (!m_filesDirectory.exists()) {
                m_filesDirectory.mkdir();
            }

            SindaDataFetcher dataFetcher = new SindaDataFetcher();
            
            List<Pcd> pcdList = removingReadyPcds(dataFetcher.getPcdListWithMinimumInfo());

            // verificar quais pcds ja foram registradas em arquivo e elimina-las da lista
            // divisao de listas!
            int numberOfParts = 4;
            List< List<Pcd>> myLists = splitInSublists(numberOfParts, pcdList);

            ExecutorService executor = Executors.newFixedThreadPool(numberOfParts);
            ListIterator< List<Pcd>> iterator = myLists.listIterator();

            while ( iterator.hasNext() ) {
                List<Pcd> list = iterator.next();
                iterator.remove();
               
                /*
                Criando uma nova sublista aqui e passando esta para a thread, evita o problema de concorrencia
                e a existencia de memoria leaks, como tambem melhora o trabalho do collector garbage la na frente
                quando os dados em memoria ja foram salvos em disco e procisamos liberar a memoria principal.
                 */
                
                PcdDataMiningTask task = new PcdDataMiningTask(new ArrayList<>(list), listener);
                m_taskList.add( task );
                executor.execute( task );
            }

            //System.out.println("executor shutting down!");
            executor.shutdown();
            pcdList.clear();
            isRunning = true;
            return isRunning;
        }
        
        return !isRunning;
    }

    public void resume() {
        changeTasksToState(PcdDataMiningTask.STATE_RUNNING);
    }

    public void stop() {
        changeTasksToState( PcdDataMiningTask.STATE_STOP);
        m_taskList.clear();
        isRunning = false;
    }

    public static List<Pcd> removingReadyPcds(List<Pcd> sindaPcdList) {
        Collections.sort(sindaPcdList, new Comparator<Pcd>() {
            @Override
            public int compare(Pcd t, Pcd t1) {
                if (t.getId() < t1.getId()) {
                    return -1;
                }
                if (t.getId() > t1.getId()) {
                    return 1;
                }

                return 0;
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        List<String> idsList = PcdDataDownloadHistory.getInstance().m_pcdIdsSaved;
         
        if ((idsList != null) && (!idsList.isEmpty())) {
            ListIterator<Pcd> pcdsIterator = sindaPcdList.listIterator();

            while (pcdsIterator.hasNext()) {
                Pcd currentPcd = pcdsIterator.next();

                for (String currentId : idsList) {
                    if (String.valueOf(currentPcd.getId()).compareTo(currentId) == 0) {
                        pcdsIterator.remove();
                        break;
                    }
                }
            }
        } 

        return sindaPcdList;
    }

    public static List< List<Pcd>> splitInSublists(int numberOfSublists, List<Pcd> list) {
        List< List<Pcd> > listOfLists = new ArrayList<>();
        int portion = (list.size() / numberOfSublists);
        int startIndex = 0;
        int endIndex = portion;
        int total = 0;

        //System.out.println( "TOTAL ITENS: " + list.size() );
        //System.out.println( "ITENS POR SUBLIST: " + portion );

        for (int i = 0; i < numberOfSublists; i++) {
            List<Pcd> ref = list.subList(startIndex, endIndex);
            total += ref.size();

            listOfLists.add(ref);
           // System.out.println("NEW SUBLIST WITH " + startIndex + " and " + (endIndex - 1));
            
            startIndex = endIndex;
            endIndex += (portion + 1);

            if ( endIndex > list.size() ) {
                endIndex = list.size();
            }
            
        }
        
        //System.out.println("TOTAL: " + total);
        return listOfLists;
    }
    
}
