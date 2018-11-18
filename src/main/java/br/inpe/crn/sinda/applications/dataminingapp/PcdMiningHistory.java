/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scavenger
 * Essa classe permite registrar & recuperar o historico das PCD's que foram
 * ja mineradas do site do sinda.
 */
public class PcdMiningHistory {
    private static PcdMiningHistory INSTANCE = null;
    private static final String FILE = "pcd_history.txt";
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
    
    public synchronized void save(String id) {
        BufferedWriter bfWriter;
        try {
            bfWriter = new BufferedWriter(  new FileWriter(m_file, true) );
            bfWriter.append(id);
            bfWriter.newLine();
            /*
            m_printWriter = new PrintWriter( m_bfWriter);
            m_printWriter.println( id );
            System.out.println("HISTORY ADDED " + id);
            m_printWriter.close();
            */
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
                System.out.println("READING PCD HISTORY " + id);
                m_pcdIdsSaved.add(id);
            }
            
            Collections.sort(m_pcdIdsSaved);
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
