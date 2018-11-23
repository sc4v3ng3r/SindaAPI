/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import br.inpe.crn.sinda.network.SindaURLs;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author scavenger
 */
public class DataMiningApplicationSettings {
    String PCD_LIST_URL = SindaURLs.PCD_LIST_URL;
    String PCD_QUERY_URL = SindaURLs.PCD_INFO_URL;
    String PCD_DATA_URL = SindaURLs.PCD_DATA_QUERY_URL;
    int PCD_TYPE_ID_COUNTER=0;
    
    String BOIA_LIST_URL="";
    String BOIA_QUERY_URL="";
    String BOIA_DATA_URL ="";
    
    int BOIA_TYPE_ID_COUNTER=0;
    boolean DATA_MINING_STAGE;
    
    //ftp settings
    private String FTP_SERVER ="";
    private String FTP_USER = "";
    private String FTP_PASSWORD = "";
    private int FTP_PORT = 21;
    
    @Ignore
    private ObjectMapper m_mapper = new ObjectMapper();
    @Ignore
    private ObjectWriter m_writer = m_mapper.writer(new DefaultPrettyPrinter() );
    @Ignore
    private static final String FILE_SETTINGS_NAME = "settings.json";
    @Ignore
    private static final File m_file = new File(FILE_SETTINGS_NAME);
    //private FileWriter m_fileWriter = null;

    private static DataMiningApplicationSettings INSTANCE;
    
    public static synchronized DataMiningApplicationSettings getInstance(){
            
            if(INSTANCE == null && (m_file.exists()))
                INSTANCE = loadData();
                //INSTANCE = new DataMiningApplicationSettings();
            else if (INSTANCE == null)
                INSTANCE = new DataMiningApplicationSettings();
            
        return INSTANCE;
    }
    
    private DataMiningApplicationSettings(){
        saveData();
    }
    
    public String getPCD_LIST_URL() {
        return PCD_LIST_URL;
    }

    public synchronized void setPCD_LIST_URL(String PCD_LIST_URL) {
        this.PCD_LIST_URL = PCD_LIST_URL;
        saveData();
    }

    public String getPCD_QUERY_URL() {
        return PCD_QUERY_URL;
    }

    public synchronized void setPCD_QUERY_URL(String PCD_QUERY_URL) {
        this.PCD_QUERY_URL = PCD_QUERY_URL;
        saveData();
    }

    public String getPCD_DATA_URL() {
        return PCD_DATA_URL;
    }

    public synchronized void setPCD_DATA_URL(String PCD_DATA_URL) {
        this.PCD_DATA_URL = PCD_DATA_URL;
        saveData();
    }

    public int getPCD_TYPE_ID_COUNTER() {
        return PCD_TYPE_ID_COUNTER;
   
    }

    public synchronized void setPCD_TYPE_ID_COUNTER(int PCD_TYPE_ID_COUNTER) {
        this.PCD_TYPE_ID_COUNTER = PCD_TYPE_ID_COUNTER;
        saveData();
    }

    public String getBOIA_LIST_URL() {
        return BOIA_LIST_URL;
    }

    public synchronized void setBOIA_LIST_URL(String BOIA_LIST_URLD) {
        this.BOIA_LIST_URL = BOIA_LIST_URLD;
        saveData();
    }

    public String getFTP_SERVER() {
        return FTP_SERVER;
    }

    public void setFTP_SERVER(String FTP_SERVER) {
        this.FTP_SERVER = FTP_SERVER;
        saveData();
    }

    
    public  String getBOIA_QUERY_URL() {
        return BOIA_QUERY_URL;
    }

    public synchronized void setBOIA_QUERY_URL(String BOIA_QUERY_URL) {
        this.BOIA_QUERY_URL = BOIA_QUERY_URL;
        saveData();
    }

    public String getBOIA_DATA_URL() {
        return BOIA_DATA_URL;
    }

    public synchronized void setBOIA_DATA_URL(String BOIA_DATA_URL) {
        this.BOIA_DATA_URL = BOIA_DATA_URL;
        saveData();
    }

    public int getBOIA_TYPE_ID_COUNTER() {
        return BOIA_TYPE_ID_COUNTER;
    }

    public synchronized void setBOIA_TYPE_ID_COUNTER(int BOIA_TYPE_ID_COUNTER) {
        this.BOIA_TYPE_ID_COUNTER = BOIA_TYPE_ID_COUNTER;
        saveData();
    }

    public String getFTP_USER() {
        return FTP_USER;
    }

    public void setFTP_USER(String FTP_USER) {
        this.FTP_USER = FTP_USER;
        saveData();
    }

    public String getFTP_PASSWORD() {
        return FTP_PASSWORD;
    }

    public void setFTP_PASSWORD(String FTP_PASSWORD) {
        this.FTP_PASSWORD = FTP_PASSWORD;
        saveData();
    }

    public int getFTP_PORT() {
        return FTP_PORT;
    }

    public void setFTP_PORT(int FTP_PORT) {
        this.FTP_PORT = FTP_PORT;
        saveData();
    }
    
    
    public  void saveData (){
        
        try {
            m_writer.writeValue(m_file, this);
            
        } catch (IOException ex) {
            Logger.getLogger(DataMiningApplicationSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    } 
    
    private  static DataMiningApplicationSettings loadData(){
        DataMiningApplicationSettings TEST = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (m_file.exists()){
                TEST =  mapper.readValue(m_file, DataMiningApplicationSettings.class);
            } 
            
            else{
            
            }
            
        } catch (IOException ex) {
            Logger.getLogger(DataMiningApplicationSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return TEST;
    }

    @Override
    public String toString() {
        /*
             String PCD_LIST_URL = SindaURLs.PCD_LIST_URL;
    String PCD_QUERY_URL = SindaURLs.PCD_INFO_URL;
    String PCD_DATA_URL = SindaURLs.PCD_DATA_QUERY_URL;
    int PCD_TYPE_ID_COUNTER=0;
    
    String BOIA_LIST_URL="";
    String BOIA_QUERY_URL="";
    String BOIA_DATA_URL ="";
    
    int BOIA_TYPE_ID_COUNTER=0;
    boolean DATA_MINING_STAGE;
    
    //ftp settings
    private String FTP_SERVER ="localhost";
    private String FTP_USER = "scavenger";
    private String FTP_PASSWORD = "sc4v3ng3rhacker";
    private int FTP_PORT = 21;
        */
           return "\n PCD_LIST_URL: " + PCD_LIST_URL + "\n"
                   +   " PCD_QUERY_URL: " + PCD_QUERY_URL + "\n"
                   +    " PCD_DATA_URL: " +  PCD_DATA_URL + "\n"
                   + " PCD_TYPE_ID_COUNTER: " + PCD_TYPE_ID_COUNTER + "\n"
                    + " BOIA_LIST_URL: " + BOIA_LIST_URL + "\n"
                   + " BOIA_QUERY_URL: " + BOIA_QUERY_URL + "\n"
                   + " BOIA_DATA_URL: " + BOIA_DATA_URL + "\n"
                   + " BOIA_TYPE_ID_COUNTER: "  + BOIA_TYPE_ID_COUNTER  + "\n"
                   + " FTP_SERVER: " + FTP_SERVER + "\n"
                   + " FTP_USER: " + FTP_USER + "\n"
                   + " FTP_PASSWORD: " + FTP_PASSWORD + "\n"
                   + " FTP_PORT: " + FTP_PORT + "\n";
        //  return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}