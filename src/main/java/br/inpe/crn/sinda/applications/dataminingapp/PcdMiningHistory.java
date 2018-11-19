/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author scavenger
 * Essa classe permite registrar & recuperar o historico das PCD's que foram
 * ja mineradas do site do sinda.
 */
public class PcdMiningHistory {
    private static PcdMiningHistory INSTANCE = null;
    private static final String FILE = "pcd_history.txt";
    private static final String FILE_JSON ="pcd_history.json";
 
    //private ObjectMapper m_mapper = new ObjectMapper();
    //private ObjectWriter m_writer = m_mapper.writer(new DefaultPrettyPrinter() );
   
    private final File m_file = new File(FILE);    
    List<String> m_pcdIdsSaved;
    
    private PcdMiningHistory(){
        m_pcdIdsSaved = new ArrayList<>();
          if (m_file.exists()){
                load();   
        } 
        
    }
    
    public static final synchronized PcdMiningHistory getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PcdMiningHistory();
        }
        return INSTANCE;
    }
    
    public synchronized void saveToJson(PcdHistory history){
    
    }
    
    private void loadInJson(){
    
    }
    
    public synchronized void save(String id) {
        BufferedWriter bfWriter;
        try {
            bfWriter = new BufferedWriter(  new FileWriter(m_file, true) );
            bfWriter.append(id);
            bfWriter.newLine();
            
            bfWriter.flush();
            bfWriter.close();
            System.out.println(Thread.currentThread().getName() + "  ----- ADD TO HISTORY PCD: " + id  + "------");
            m_pcdIdsSaved.add(id);
            Collections.sort(m_pcdIdsSaved);
            
        }
         
        catch( Exception ex ){
            Logger.getLogger(PcdMiningHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         finally {
            bfWriter = null;
                //m_printWriter = null;  
         }
    }
    
    private void load(){
      
        BufferedReader bfReader;
        try {
            bfReader = new BufferedReader(  new FileReader(FILE) );
            
            String id;
            while( (id = bfReader.readLine()) !=null ) {
                //System.out.println("READING PCD HISTORY " + id);
                m_pcdIdsSaved.add(id);
            }
            
            Collections.sort(m_pcdIdsSaved);
            System.out.println("\n\n\n--------- AFTER LOAD AND SORT --------- ");
            for(String it: m_pcdIdsSaved){
                System.out.println(it);
           }
            System.out.println("-----------------------------------------------------------\n\n\n");
                    
            bfReader.close();
        } 
        catch (Exception ex){
             Logger.getLogger(PcdMiningHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        finally {
            bfReader = null;
        }
    }
    
    public List<String> getIdsList(){
        return m_pcdIdsSaved;
    }
       
}
