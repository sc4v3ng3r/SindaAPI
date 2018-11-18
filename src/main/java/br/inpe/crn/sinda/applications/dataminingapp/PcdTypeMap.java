/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import br.inpe.crn.sinda.model.PcdType;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scavenger
 */
public class PcdTypeMap {
    private static PcdTypeMap m_instance = null;
    private HashMap<String, PcdType > m_map = null;
    private static long id_counter = 0;
    
    private ObjectMapper m_mapper = new ObjectMapper();
    private ObjectWriter m_writer = m_mapper.writer(new DefaultPrettyPrinter() );
    private static final String FILE = "pcd_types.json";
    private final File m_file = new File(FILE);
    private FileWriter m_fileWriter = null;
    
    public static final synchronized PcdTypeMap getInstance() {
        if (m_instance == null)
            m_instance = new PcdTypeMap();
        
        return m_instance;
        
    }
    
    private PcdTypeMap(){
      m_map = new HashMap<>();
        try {
            m_fileWriter  = new FileWriter(m_file, true);
            // load sensors counter !
        } catch (IOException ex) {
            Logger.getLogger(PcdTypeMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public synchronized boolean exist(final String sensors){
        return m_map.containsKey( sensors );
    }
    
    public synchronized PcdType get(final String key){
        return m_map.get(key);
    }
    
    public synchronized void  add( PcdType type){
        type.setId(id_counter);
        m_map.put(type.getSensores(), type);
        saveData(type);
        id_counter+=1;
    }
    
    private void saveData(PcdType type){
         
         try {
           SequenceWriter  seqWriter = m_writer.writeValuesAsArray( m_fileWriter );
            seqWriter.write(  type);
            seqWriter.flush();
        } 
         catch (IOException ex) {
            Logger.getLogger(PcdTypeMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public synchronized void clear(){
        m_map.clear();
    }
    
    public synchronized int size(){
        return m_map.size();
    }
    
    
}
