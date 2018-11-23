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


    public static final synchronized ExtractorLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExtractorLogger();
        }
        return INSTANCE;
    }

    private ExtractorLogger() {

        try {
            if (!m_file.exists()) {
                m_file.createNewFile();
            }
            m_writer = new PrintWriter(m_file);
        } catch (FileNotFoundException ex) {
            // java.util.logging.Logger.getLogger(ExtractorLogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //java.util.logging.Logger.getLogger(ExtractorLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void write(String data) {
        m_writer.println(data);
        m_writer.flush();
    }

    public synchronized String tail2( int lines) {
        java.io.RandomAccessFile fileHandler = null;
        try {
            fileHandler
                    = new java.io.RandomAccessFile(m_file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer < fileLength) {
                        line = line + 1;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1) {
                        line = line + 1;
                    }
                }
                if (line >= lines) {
                    break;
                }
                sb.append((char) readByte);
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } 
        
        catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } 
        catch (java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
        
        finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
