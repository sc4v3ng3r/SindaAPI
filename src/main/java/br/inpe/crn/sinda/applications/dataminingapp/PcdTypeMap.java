/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import br.inpe.crn.sinda.model.PcdType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scavenger
 * 
 * nao estar carregando os tipos ja registrados!
 */
public class PcdTypeMap {
    private static PcdTypeMap m_instance = null;
    
    private HashMap<String, PcdType > m_map = null;
    private List<PcdType> m_typesToSave = new ArrayList<>();
    private ObjectMapper m_mapper = new ObjectMapper();
    private ObjectWriter m_writer = m_mapper.writer(new DefaultPrettyPrinter() );
    private static final String FILE = "pcd_types.json";
    private final File m_file = new File(FILE);
    private FileWriter m_fileWriter = null;
    private DataMiningApplicationSettings m_settings = DataMiningApplicationSettings.getInstance();
    
    public static final synchronized PcdTypeMap getInstance() {
        if (m_instance == null)
            m_instance = new PcdTypeMap();
        
        return m_instance;
        
    }
    
    private PcdTypeMap(){
      m_map = new HashMap<>();
        try {
            m_fileWriter  = new FileWriter(m_file, true);
            if (m_file.exists()){
                loadData();
            }
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
        int idCounter =  m_settings.getPCD_TYPE_ID_COUNTER();
        type.setId( idCounter);
        m_map.put(type.getSensores(), type);
        saveData();
        m_settings.setPCD_TYPE_ID_COUNTER(idCounter+1);
    }
    
    private void saveData(){     
        try{
             m_file.delete();
             m_writer.writeValue(m_file, m_map);
             
        }
        
        catch(IOException ex){
             Logger.getLogger(PcdTypeMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadData(){
        if (m_file.exists()){
            try{
            byte[] data = Files.readAllBytes(m_file.toPath());
            
            if (data!=null && data.length > 0){
                  m_map = m_mapper.readValue(m_file, new TypeReference<HashMap<String,PcdType>>() {} );
                  System.out.println( " MAP READED SIE: " + m_map.size());
                   Iterator<String> keys = m_map.keySet().iterator();
                   while(keys.hasNext()){
                       System.out.println("KEY READED : " + keys.next());
                    }
            }
              
        }
        catch (Exception ex){
            Logger.getLogger(PcdTypeMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        }
        
        
       
        /* try{
            
            
            if ( (data!=null) && data.length > 0){
                m_map = m_mapper.readValue(data, HashMap.class);
                 Iterator keysIt = m_map.keySet().iterator();
                 while(keysIt.hasNext()){
                     String key = (String) keysIt.next();
                     System.out.println( m_map.get(key));
                 }
            }
            
        }
        catch(IOException ex){
            clear();
             Logger.getLogger(PcdTypeMap.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
    }
    public synchronized void clear(){
        m_map.clear();
        m_typesToSave.clear();
    }
    
    public synchronized int size(){
        return m_map.size();
    }
}
