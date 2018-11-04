/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

import static br.inpe.crn.sinda.applications.dataminingapp.DataMiningApplication.m_filesDirectory;
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
public class PcdDataMiningTask implements Runnable {
        private List<Pcd> m_pcdList;
        private TaskListener m_listener;
        
        private final SindaWebpageFetcher m_webPageFetcher = new SindaWebpageFetcher();
        private final SindaPcdParser m_parser = new SindaPcdParser();
        
        private final ObjectMapper m_mapper = new ObjectMapper();
        private final ObjectWriter m_writer = m_mapper.writer(new DefaultPrettyPrinter());
        
        public PcdDataMiningTask(List<Pcd> m_pcdList, final TaskListener listener) {
            this.m_pcdList = m_pcdList;
            m_listener = listener;
        }

        @Override
        public void run() {
            
            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = DateTimeUtils.getInstance(DateTimeUtils.DATE_FORMAT_FOR_FILE);

            ListIterator<Pcd> it = m_pcdList.listIterator();
            
            while (it.hasNext() ) {
                Pcd pcd = it.next();
                System.out.println("Thread: " + Thread.currentThread().getName() + " GETTING DATA FOR PCD: " + pcd.getId());
                
                /*Obtem a pagina da web do Sinda que contem
                informações sobre a PCD. EX: latitude, longitude, altitude, periodo inicial & final...*/
                Document pcdInfoWebpage = m_webPageFetcher.fetchPcdInfoPage(pcd.getId(), true);
                
                m_parser.parsePcdInfo(pcdInfoWebpage, pcd); // interpratamos os dados da pagina e o salvamos no objeto java pcd.
                
                System.out.println("PCD  " + pcd.getId() + " [DATA INFO OK!] " + Thread.currentThread().getName());

                // ja realizar a subrotina de buscar os dados!
                // write in JSON FILE!
                List<PcdData> dataList = queryAllPcdDataHistory(pcd);
                
                if (!dataList.isEmpty()) {
                    System.out.println("PCD  " + pcd.getId() + " [QUERYING DATA OK!] " + Thread.currentThread().getName());
                    pcd.setData(dataList);
                }

                String filename = m_filesDirectory.getPath() + File.separator + String.valueOf(pcd.getId()) + "-"
                        + (pcd.getEstacao().replace(" ", "_").replace("/", "_")) // USAR UMA EXPRESSAO RELUGAR!
                        + "-" + pcd.getUf() + "-" + dateFormat.format(currentDate) + ".json";
                
                try {
                    System.out.println("Thread: " + Thread.currentThread().getName() + " writing file: " + filename);
                    m_writer.writeValue(new File(filename), pcd);
                } 
                
                catch (IOException ex) {
                    Logger.getLogger(DataMiningApplication.class.getName()).log(Level.SEVERE, null,  ex);
                }
                
                  // removemos o tem PCD com seus dados da lista, para o collecotr garbage ficar de olho nele e remove-lo da memoria
                it.remove();
            }

            m_listener.taskFinished(m_pcdList);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        } // end of run

        private List<PcdData> queryAllPcdDataHistory(Pcd pcd) {
            SimpleDateFormat fmt = DateTimeUtils.getInstance(DateTimeUtils.DATE_TIME_FORMAT);
            
            boolean typeFlag = false;
            //ReentrantLock locker = new ReentrantLock(true);
            PcdTypeMap typeMap = PcdTypeMap.getInstance();
            Date periodoInicial = pcd.getPeriodoInicial();
            Date periodoFinal = pcd.getPeriodoFinal();
            ArrayList<PcdData> dataList = new ArrayList<>();

            if ( (periodoInicial != null) && (periodoFinal != null) ) {
                System.out.println( Thread.currentThread().getName() + "::PCD PERIODO |Pi: " + fmt.format(periodoInicial)
                        + " |PF:  " + fmt.format(periodoFinal));

                Calendar calendar;
                calendar = Calendar.getInstance();
                calendar.setTime( periodoInicial );

                Date queryPi = periodoInicial;
                calendar.add(Calendar.YEAR, 1);
                Date queryPf = calendar.getTime();
                
                QueryParameters param = new QueryParameters.QueryParametersBuilder()
                        .id( pcd.getId() )
                        .dataInicial( queryPi )
                        .dataFinal( queryPf )
                        .build();
                
                do {
                    
                    System.out.println(Thread.currentThread().getName() + "::queringData |Pi: " + fmt.format(queryPi)
                            + " |PF:  " + fmt.format(queryPf));

                    Document dataTableHtmlDoc = m_webPageFetcher.fetchPcdDataTablePage( param, true );
                    
                    /*So ENTRA AQUI UMA UNICA VEZ!!!!*/
                    if (!typeFlag){
                         PcdType type = m_parser.parsePcdType(dataTableHtmlDoc);
                         if (type != null){
                            if (!typeMap.exist( type.getSensores())){
                                typeMap.add(type);
                                pcd.setTipo(type);
                                System.out.println("Thread " + Thread.currentThread().getName() + " ADDING TYPE 0" + type );
                            } 
                            else {
                                System.out.println("Thread " + Thread.currentThread().getName() + " EXIST TYPE " + type );
                                pcd.setTipo( typeMap.get( type.getSensores() ) );
                            }
                        }
                         typeFlag = true;
                    }
                  
                    dataList.addAll(0, m_parser.parsePcdDataTable( dataTableHtmlDoc ) );
                  
                    // salto no tempo!
                    calendar.setTime(queryPf);
                    calendar.add(Calendar.DATE, 1);
                    queryPi = calendar.getTime();
                   
                    calendar.add(Calendar.YEAR, 1);
                    queryPf = calendar.getTime();

                    param.setDataInicial(queryPi);
                    param.setDataFinal(queryPf);

                    //System.out.println("COMPARANDO " + fmt.format( queryPi) + " <= " + fmt.format(periodoFinal));
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