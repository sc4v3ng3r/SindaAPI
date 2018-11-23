/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.model.PcdHistory;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author scavenger
 */
public class PcdsUpdateProcessManager implements PcdUpdateTask.UpdateTaskDone {
   
    private PcdDataDownloadHistory m_dataHistory = PcdDataDownloadHistory.getInstance();
    private List<PcdHistory> m_historyList;
    private List<PcdUpdateTask> m_taskList = new ArrayList();
    private boolean isRunning = false; 
    private int m_taskCounter = 0;
    
    public PcdsUpdateProcessManager(){
        m_historyList = m_dataHistory.getAllHistory();
      //  System.out.println("HISTORY READED " + m_historyList.size() );
    }
    
    public  boolean start(){
        //System.out.println("STARTING UPDATE DATA PCD");
        if (isRunning)
            return false;
            
           // System.out.println("DATA UPDATE STARTED!");
            List<List<PcdHistory>> lists = splitInSublists(4, m_historyList);
            ExecutorService executor = Executors.newFixedThreadPool(4);
             
            ListIterator< List<PcdHistory>> iterator = lists.listIterator();

            while ( iterator.hasNext() ) {
                List<PcdHistory> list = iterator.next();
                iterator.remove();
                PcdUpdateTask task = new PcdUpdateTask(new ArrayList<>(list), this );
                m_taskList.add( task );
                executor.execute( task );
            }

            //System.out.println("executor shutting down!");
            executor.shutdown();
            lists.clear();

            isRunning = true;
            return isRunning;
    }
    
    
    public void stop(){
        if (isRunning){
            for(PcdUpdateTask task: m_taskList)
                task.stop();
        }
         isRunning = false;
    }
    
    public boolean isRunning(){ return isRunning; }
    
    private List< List<PcdHistory>> splitInSublists(int numberOfSublists, List<PcdHistory> list) {
        List< List<PcdHistory> > listOfLists = new ArrayList<>();
        int portion = (list.size() / numberOfSublists);
        int startIndex = 0;
        int endIndex = portion;
        int total = 0;

        //System.out.println( "TOTAL ITENS: " + list.size() );
        //System.out.println( "ITENS POR SUBLIST: " + portion );

        for (int i = 0; i < numberOfSublists; i++) {
            List<PcdHistory> ref = list.subList(startIndex, endIndex);
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

    @Override
    public void taskDone(boolean status) {
        if (status){
            if (m_taskCounter<4)
                m_taskCounter+=1;
        }
        
        else m_taskCounter = 0;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void taskAborted() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
