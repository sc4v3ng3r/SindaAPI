/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author scavenger
 */
public class FtpClient {
    private FTPClient m_client;
    private DataMiningApplicationSettings m_setttings = DataMiningApplicationSettings.getInstance();
    private ExtractorLogger m_logger = ExtractorLogger.getInstance();
    private boolean isInRightDirectory = false;

    public FtpClient() {
        m_client = new FTPClient();
        
        try {
            m_client.connect(m_setttings.getFTP_SERVER() , m_setttings.getFTP_PORT());
            m_client.login(m_setttings.getFTP_USER(),  m_setttings.getFTP_PASSWORD());
            m_client.enterLocalPassiveMode();
            m_client.setFileType(FTP.BINARY_FILE_TYPE );
        } 
        
        catch(Exception ex){
            System.out.println(ex);
        }
    }
    
    public boolean ftpDirecotryExists(String ftpDirectory){
        boolean flag =false;
        try {
            if (m_client.cwd(ftpDirectory) == 550 )
                return flag;
        } catch (IOException ex) {
            Logger.getLogger(FtpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public boolean createFTPDirectory(String ftpDirectory){
        if ( ftpDirecotryExists(ftpDirectory) ){
            return false;
        } else {
            try {
                m_client.mkd(ftpDirectory);
            } catch (IOException ex) {
                Logger.getLogger(FtpClient.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }
    
    public synchronized boolean saveData(File fileToSave, String ftpDirectory){
        boolean done = false;
        try {
            
            if (!isInRightDirectory){
                m_client.cwd(ftpDirectory);
                isInRightDirectory = true;
            }
            
            String remoteFileName = fileToSave.getName();
            InputStream input = new FileInputStream(fileToSave);
            done = m_client.storeFile(remoteFileName, input);
            input.close();
           
        }
        
        catch (IOException ex) {
            m_logger.write( ex.toString() );
            //Logger.getLogger(FtpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return done;
    }
    
    public boolean closeConnection(){
        boolean flag = false;
        try {
            flag = m_client.logout();
        } catch (IOException ex) {
            //Logger.getLogger(FtpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return flag;
    }
}
