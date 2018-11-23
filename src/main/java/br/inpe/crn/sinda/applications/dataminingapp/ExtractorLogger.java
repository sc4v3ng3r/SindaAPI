/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;

/**
 *
 * @author scavenger
 */
public class ExtractorLogger {
    private static ExtractorLogger INSTANCE = null;
    private final File m_file = new File("tasks-log.txt");
    private PrintWriter m_writer = null;
    private String[] m_logStack;
    private int m_stackMAX_SIZE = 50;
    private int m_stackCounter = 0;
    
    public static final synchronized ExtractorLogger getInstance(){
        if (INSTANCE == null)
            INSTANCE = new ExtractorLogger();
        return INSTANCE;
    }

    private ExtractorLogger() {
        m_logStack = new String[m_stackMAX_SIZE];
        m_stackCounter = m_stackMAX_SIZE-1;
        
        try {
            if (!m_file.exists())
                m_file.createNewFile();
            m_writer = new PrintWriter(m_file);
        } 
         catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(ExtractorLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        catch(IOException ex){
              java.util.logging.Logger.getLogger(ExtractorLogger.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public String getlastLogs(int number){
        if (number > m_stackCounter){
            number = m_stackCounter;
        }
        String data = "";
        for(int i=0;i< number; i++){
            if (m_logStack[i] ==null)
                continue;
            
            data+= m_logStack[i] + "\n";
        }
        
        return data;
    }
    public synchronized void write(String data){
        if (m_stackCounter < 0){
            m_stackCounter = (m_stackMAX_SIZE-1);
        }
        m_logStack[m_stackCounter] = data;
        m_stackCounter-=1;
        
        m_writer.println(data);
        m_writer.flush();
    }
   
}
