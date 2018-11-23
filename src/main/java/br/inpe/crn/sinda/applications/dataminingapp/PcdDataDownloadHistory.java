/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import br.inpe.crn.sinda.model.PcdHistory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scavenger Essa classe permite registrar & recuperar o historico das
 * PCD's que foram ja mineradas do site do sinda.
 */
public class PcdDataDownloadHistory {

    private static PcdDataDownloadHistory INSTANCE = null;
    private static final String FILE = "pcd_history.txt";
    private static final String FILE_JSON = "pcd_history.json";
    private final File m_file = new File(FILE_JSON);
    private ObjectMapper m_mapper;
    private ObjectWriter m_writer;

    private HashMap<String, PcdHistory> m_map;
    List<String> m_pcdIdsSaved;

    private PcdDataDownloadHistory() {
        m_mapper = new ObjectMapper();
        m_writer = m_mapper.writer(new DefaultPrettyPrinter());

        m_pcdIdsSaved = new ArrayList<>();
        m_map = new HashMap<>();

        if (m_file.exists()) {
            load();
        }

    }

    public static final synchronized PcdDataDownloadHistory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PcdDataDownloadHistory();
        }
        return INSTANCE;
    }

    public synchronized void saveToJson() {
        try {
            if (m_file.exists()) {
                m_file.delete();
            }

            m_writer.writeValue(m_file, m_map);

        } catch (IOException ex) {
            Logger.getLogger(PcdTypeMap.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void load() {
        if (m_file.exists()) {
            try {
                byte[] data = Files.readAllBytes(m_file.toPath());

                if (data != null && data.length > 0) {
                    m_map = m_mapper.readValue(m_file, new TypeReference<HashMap<String, PcdHistory>>() {
                    });
                    
                   // System.out.println(" MAP READED SIE: " + m_map.size());
                    Iterator<String> keys = m_map.keySet().iterator();
                  
                    while (keys.hasNext()) {
                        //String key =  keys.next();
                        m_pcdIdsSaved.add(  keys.next() );
                        //System.out.println("PCD KEY READED : " + key);
                    }
                }
            } 
            
            catch (IOException ex) {
                Logger.getLogger(PcdTypeMap.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public synchronized void add(PcdHistory history) {
        m_map.put(history.getId(), history);
        saveToJson();
    }

    public  synchronized PcdHistory getHistory(String pcdId){
        return m_map.get(pcdId);
    }
    
    public synchronized void remove(String pcdId){ m_map.remove(pcdId); }
    
    public synchronized List<String> getIdsList() {
        return m_pcdIdsSaved;
    }
    
    public List<PcdHistory> getAllHistory(){
        return new ArrayList<>(m_map.values());
    }
}
