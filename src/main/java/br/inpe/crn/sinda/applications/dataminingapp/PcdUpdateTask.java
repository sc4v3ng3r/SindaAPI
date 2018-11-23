/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import br.inpe.crn.sinda.model.PcdHistory;
import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.model.PcdData;
import br.inpe.crn.sinda.model.PcdType;
import br.inpe.crn.sinda.network.QueryParameters;
import br.inpe.crn.sinda.network.SindaWebpageFetcher;
import br.inpe.crn.sinda.parser.SindaPcdParser;
import br.inpe.crn.sinda.utility.DateTimeUtils;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;

/**
 *
 * @author scavenger
 */
public class PcdUpdateTask implements Runnable {

    private volatile int m_currentState = STATE_RUNNING;
    public static final int STATE_STOP = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_DONE =3;
    private List<PcdHistory> m_pcdList;
    private SindaWebpageFetcher m_webPageFetcher = new SindaWebpageFetcher();
    private SindaPcdParser m_parser = new SindaPcdParser();
    private final SimpleDateFormat m_dateTimeFormat = DateTimeUtils.getInstance(DateTimeUtils.DATE_TIME_FORMAT);
    private PcdDataDownloadHistory m_history = PcdDataDownloadHistory.getInstance();
    private final ObjectMapper m_mapper = new ObjectMapper();
    private final ObjectWriter m_writer = m_mapper.writer(new DefaultPrettyPrinter());
    private final File UPDATE_DIRECTORY = new File("update");
    private UpdateTaskDone m_listener;
    private ExtractorLogger m_logger = ExtractorLogger.getInstance();

    public PcdUpdateTask(List<PcdHistory> pcdList, UpdateTaskDone listener) {
        this.m_pcdList = pcdList;
        m_listener = listener;
    }

    public synchronized void stop() {
        m_currentState = STATE_STOP;

    }

    @Override
    public void run() {
        ListIterator<PcdHistory> iterator = m_pcdList.listIterator();
        Calendar calendar = Calendar.getInstance();
        List<PcdData> allUpdatedData = new ArrayList<>();
        PcdHistory pcdHistory;
        Pcd pcd;
        Long pcdId = -1L;
        m_currentState = STATE_RUNNING;

        writeLog("UPDATE TASK STARTING");
        while (m_currentState == STATE_RUNNING) {

            if (iterator.hasNext()) {
                try {
                    pcdHistory = iterator.next();
                    pcd = new Pcd();
                    pcdId = Long.parseLong(pcdHistory.getId());

                    // Obter a data atual da Pcd no Site do sinda,
                    Document htmlDoc = m_webPageFetcher.fetchPcdInfoPage(pcdId, true);
                    m_parser.parsePcdInfo(htmlDoc, pcd);

                    Date localLastUpdate = m_dateTimeFormat.parse(pcdHistory.getLastUpdate());
                    //System.out.println(pcdId + " LAST LOCAL UPDATE: " + m_dateTimeFormat.format(localLastUpdate) );
                    writeLog(pcdId + " LAST LOCAL UPDATE: " + localLastUpdate);

                    Date webLastUpdate = pcd.getPeriodoFinal();
                    writeLog(pcdId + " LAST WEB UPDATE: " + m_dateTimeFormat.format(webLastUpdate));
                    //System.out.println(pcdId + " LAST WEB UPDATE: " + m_dateTimeFormat.format(webLastUpdate));

                    while (localLastUpdate.getTime() < webLastUpdate.getTime()) {
                       // System.out.println("UPDATING PCD: " + pcdId);
                        writeLog("UPDATING PCD: " + pcdId);
                        calendar.setTime(localLastUpdate);
                        calendar.add(Calendar.DATE, 1);

                        Date queryPi = calendar.getTime();
                        calendar.add(Calendar.YEAR, 1);
                        Date queryPf = calendar.getTime(); // um ano depois!

                        QueryParameters param = new QueryParameters.QueryParametersBuilder()
                                .id(pcdId)
                                .dataInicial(queryPi)
                                .dataFinal(queryPf)
                                .build();

                        Document dataPage = m_webPageFetcher.fetchPcdDataTablePage(param, true);
                        List<PcdData> dataList = m_parser.parsePcdDataTable(dataPage);
                        writeLog("getted data " + dataList.size());

                        allUpdatedData.addAll(dataList);
                        localLastUpdate = queryPf;

                    } // end while

                    //salva dos dados JSON de update!
                    if (!allUpdatedData.isEmpty()) {
                        if (!UPDATE_DIRECTORY.exists()) {
                            UPDATE_DIRECTORY.mkdir();
                        }

                        File updatedFile = new File(UPDATE_DIRECTORY, pcdHistory.getId() + "-" + m_dateTimeFormat.format(System.currentTimeMillis() + ".json"));

                        try {
                            m_writer.writeValue(updatedFile, allUpdatedData);
                            allUpdatedData.clear();
                            pcdHistory.setLastUpdate(m_dateTimeFormat.format(webLastUpdate));
                            writeLog("writing update PCD ID: " + pcdId);
                            m_history.add(pcdHistory);
                        } 
                        
                        catch (IOException ex) {
                            Logger.getLogger(PcdUpdateTask.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //salva o last update no pc_history
                        
                    }

                } 
                
                catch (ParseException ex) {
                    writeLog("FAIL AT PARSING " + pcdId + " LAST UPDATE ");
                    //Logger.getLogger(PcdUpdateTask.class.getName()).log(Level.SEVERE, null, ex);
                }

            } 
            
            else {
                m_currentState = STATE_DONE;
               // System.out.println("DONE TASK UPDATE");
            }
            
            
        }

        if (m_currentState != STATE_STOP && m_listener != null) {
            m_currentState = STATE_STOP;
            m_listener.taskDone(true);
            writeLog("ENDING!!");
            return;
        }

        writeLog("ABORTED!!!!");
        //System.out.println("ABORTED!");
        if (m_listener != null) {
            m_listener.taskAborted();
        }
    }

    private void writeLog(String data) {
        String dataToWrite = m_dateTimeFormat.format(System.currentTimeMillis())
                + " " + Thread.currentThread().getName() + " " + data;

        m_logger.write(dataToWrite);
    }

    public interface UpdateTaskDone {

        void taskDone(boolean status);

        void taskAborted();
    }

}
