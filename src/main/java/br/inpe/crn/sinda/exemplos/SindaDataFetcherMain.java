/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.exemplos;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jsoup.nodes.Document;
import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.model.PcdData;
import br.inpe.crn.sinda.network.QueryParameters;
import br.inpe.crn.sinda.network.SindaWebpageFetcher;
import br.inpe.crn.sinda.parser.SindaPcdParser;

/**
 *
 * @author scavenger
 */
public class SindaDataFetcherMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // objeto pegador de paginas do site do sinda!
        SindaWebpageFetcher fetcher = new SindaWebpageFetcher();
        // objeto interpretador de dados das paginas do site sinda!
        SindaPcdParser parser = new SindaPcdParser();
        Document sindaPcdListWebpage = fetcher.fetchPcdListPage();

        // obtenho a lista de todas as pcds com os dados basicos
        List<Pcd> pcdList = parser.parseToPcdList(sindaPcdListWebpage);

         Pcd myPcd = pcdList.get(  0 );
         Calendar calendar = Calendar.getInstance();
           
         calendar.set( Calendar.DAY_OF_MONTH, 14);
         calendar.set( Calendar.MONTH,  Calendar.AUGUST);
         calendar.set( Calendar.YEAR, 2001);
         
         Date dataInicial = calendar.getTime();
         
         System.out.println("TEMPO INICIAL: " + dataInicial);
         
         calendar.add(Calendar.YEAR, 1);
         Date dataFinal = calendar.getTime();
         System.out.println("TEMPO FINAL: " + dataFinal);
         
         QueryParameters query = new QueryParameters.QueryParametersBuilder()
                 .id( myPcd.getId() )
                 .dataInicial(dataInicial)
                 .dataFinal(dataFinal)
                 .build(); 
         
         List<PcdData> dataList =  parser.parsePcdDataTable( fetcher.fetchPcdDataTablePage(query) );
        
         
         for(PcdData data: dataList){
             System.out.println( data );
         }
    
         System.out.println("LIST SIZE: " + dataList.size() );
         /*
         System.out.println("\n ---- ANO SEGUINTE ----");
         
         query.setAnoInicial("2002");
         query.setAnoFinal("2003");
         
         dataList =  parser.parsePcdDataTable( fetcher.fetchPcdDataTablePage(query) );
         
         for(PcdData data: dataList){
             System.out.println( data );
         }
*/
         
    }

   
}
