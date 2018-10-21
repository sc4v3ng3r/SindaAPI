/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.exemplos;

import java.util.List;
import org.jsoup.nodes.Document;
import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.model.PcdData;
import br.inpe.crn.sinda.network.QueryParameters;
import br.inpe.crn.sinda.network.SindaDataFetcher;
import br.inpe.crn.sinda.network.SindaWebpageFetcher;
import br.inpe.crn.sinda.parser.SindaPcdParser;

/**
 *
 * @author scavenger
 */
public class ProgramaExemplo {
    
    public static void main(String args[]){
        /*
        SindaWebpageFetcher fecher = new SindaWebpageFetcher();
        SindaPcdParser parser = new SindaPcdParser();
        
        /*
        Document pcdListWebPage = fecher.fetchPcdListPage();
    
        List<Pcd> pcdList =  parser.parseToPcdList(pcdListWebPage);
        Pcd pcd = pcdList.get(0);
        System.out.println( pcd );
        
       Document pcdInfoWebPage =  fecher.fetchPcdInfoPage( pcd.getId() , true );
       
       parser.parsePcdInfo(pcdInfoWebPage, pcd);
        System.out.println(pcd);
        
        QueryParameters param = new QueryParameters.
                QueryParametersBuilder()
                .id( pcd.getId())
                .diaInicial("14")
                .mesInicial("08")
                .anoInicial("2001")
                .diaFinal("14")
                .mesFinal("08")
                .anoFinal("2002")
                .build();
        
        
        Document htmlTable = fecher.fetchPcdDataTablePage(param);
        List<PcdData> dataList = parser.parsePcdDataTable(htmlTable);
   
        for(PcdData data : dataList ){
            System.out.println( data);
        }
        
        System.out.println("TOTAL: " + dataList.size());
        
        SindaDataFetcher dataFetcher = new SindaDataFetcher();
        
        List<Pcd> pcdList = dataFetcher.getPcdListWithFullInfo();
        for(Pcd pcd: pcdList){
            System.out.println( pcd );
        }*/
    }
    
}
