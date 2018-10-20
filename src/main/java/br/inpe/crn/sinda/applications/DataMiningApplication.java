/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications;

import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.model.PcdData;
import br.inpe.crn.sinda.network.QueryParameters;
import br.inpe.crn.sinda.network.SindaDataFetcher;
import br.inpe.crn.sinda.network.SindaWebpageFetcher;
import br.inpe.crn.sinda.parser.SindaPcdParser;
import br.inpe.crn.sinda.utility.DateTimeUtils;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;

/**
 *
 * @author scavenger
 */
public class DataMiningApplication {

    public static final String DIR = "files";
    public static final File m_filesDirectory = new File(DIR);

    private static final PcdGettingDataDetailsTask.TaskListener listeter = new PcdGettingDataDetailsTask.TaskListener() {

        @Override
        public void taskFinished(List<Pcd> pcdList) {
            System.out.println("DONE!");
            //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    };

    public static final void main(String args[]) {
        if (!m_filesDirectory.exists()) {
            m_filesDirectory.mkdir();
        }

        //if ( )
        SindaDataFetcher dataFetcher = new SindaDataFetcher();

        // a ideia sera dividir essa lista em 3 ou 4 partes e separar o trabalho de mineracao em 3 ou 4 threads
        List<Pcd> pcdList = dataFetcher.getPcdListWithMinimumInfo();

        int numberOfParts = 4;
        List< List<Pcd>> myLists = splitInSublists(numberOfParts, pcdList);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfParts);
        for (int i = 0; i < numberOfParts; i++) {
            PcdGettingDataDetailsTask task = new PcdGettingDataDetailsTask(myLists.get(i), listeter);
            executor.execute(task);
        }

        executor.shutdown();
    }

    static class PcdGettingDataDetailsTask implements Runnable {

        private List<Pcd> m_pcdList;
        private TaskListener m_listener;
        private SindaWebpageFetcher m_webPageFetcher = new SindaWebpageFetcher();
        private SindaPcdParser m_parser = new SindaPcdParser();
        private ObjectMapper m_mapper;

        public PcdGettingDataDetailsTask(List<Pcd> m_pcdList, final TaskListener listener) {
            this.m_pcdList = m_pcdList;
            m_listener = listener;
        }

        @Override
        public void run() {
            m_mapper = new ObjectMapper();
            ObjectWriter writer = m_mapper.writer(new DefaultPrettyPrinter());

            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = DateTimeUtils.getInstance(DateTimeUtils.DATE_FORMAT_FOR_FILE);

            for (int i = 0; i < m_pcdList.size(); i++) {
                Pcd pcd = m_pcdList.get(i);
                System.out.println("Thread: " + Thread.currentThread().getName() + " GETTING DATA FOR PCD: " + pcd.getId());
                Document webPage = m_webPageFetcher.fetchPcdInfoPage(pcd.getId(), true);
                m_parser.parsePcdInfo(webPage, pcd);
                System.out.println("PCD  " + pcd.getId() + " [DATA INFO OK!] " + Thread.currentThread().getName());

                // ja realizar a subrotina de buscar os dados!
                // write in JSON FILE!
                List<PcdData> dataList = queringData(pcd);
                if (!dataList.isEmpty()) {
                    System.out.println("PCD  " + pcd.getId() + " [DATA OK!] " + Thread.currentThread().getName());
                    pcd.setData(dataList);
                }

                String filename = m_filesDirectory.getPath() + File.separator + String.valueOf(pcd.getId()) + "-" + (pcd.getEstacao().replace(" ", "_"))
                        + "-" + pcd.getUf() + "-" + dateFormat.format(currentDate) + ".json";

                try {

                    System.out.println("Thread: " + Thread.currentThread().getName() + " writing file: " + filename);
                    writer.writeValue(new File(filename), pcd);
                } catch (IOException ex) {
                    Logger.getLogger(DataMiningApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            m_listener.taskFinished(m_pcdList);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private List<PcdData> queringData(Pcd pcd) {
            Date periodoInicial = pcd.getPeriodoInicial();
            Date periodoFinal = pcd.getPeriodoFinal();
            Calendar calendar;
            List<PcdData> dataList = new ArrayList<>();

            if ((periodoInicial != null) && (periodoFinal != null)) {
                calendar = Calendar.getInstance();
                calendar.setTime(periodoInicial);

                Date queryPi = periodoInicial;
                calendar.add(Calendar.YEAR, 1);
                Date queryPf = calendar.getTime();
                QueryParameters param = new QueryParameters.QueryParametersBuilder()
                        .id(pcd.getId())
                        .dataInicial(queryPi)
                        .dataFinal(queryPf)
                        .build();
                
                do {
                    Document dataDocument = m_webPageFetcher.fetchPcdDataTablePage(param);
                    dataList.addAll( m_parser.parsePcdDataTable(dataDocument) );
                    
                    calendar.setTime(queryPf);
                    calendar.add(Calendar.DATE, 1);
                    queryPi = calendar.getTime();
                    calendar.add(Calendar.YEAR, 1);
                    queryPf = calendar.getTime();

                    param.setDataInicial(queryPi);
                    param.setDataFinal(queryPf);

                } while (queryPi.compareTo(periodoFinal) <= 0);

            }
            return dataList;
        }

        public interface TaskListener {

            void taskFinished(List<Pcd> pcdList);
            // .. taskCanceled
            // .. taskError
        }
    }

    public static List< List<Pcd>> splitInSublists(int numberOfSublists, List<Pcd> list) {
        List< List<Pcd>> listOfLists = new ArrayList<>();
        int portion = (list.size() / numberOfSublists);
        int startIndex = 0;
        int endIndex = portion;
        int total = 0;

        System.out.println("TOTAL ITENS: " + list.size());
        System.out.println("ITENS POR SUBLIST: " + portion);

        for (int i = 0; i < numberOfSublists; i++) {
            List<Pcd> ref = list.subList(startIndex, endIndex);
            total += ref.size();

            listOfLists.add(ref);
            System.out.println("NEW SUBLIST WITH " + startIndex + " and " + (endIndex - 1));
            startIndex = endIndex;
            endIndex += (portion + 1);

            if (endIndex > list.size()) {
                endIndex = list.size();
            }
        }

        System.out.println("TOTAL: " + total);
        return listOfLists;
    }

}
