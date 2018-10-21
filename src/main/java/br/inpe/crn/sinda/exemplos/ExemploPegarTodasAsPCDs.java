/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.exemplos;

import java.util.List;
import org.jsoup.nodes.Document;
import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.network.SindaWebpageFetcher;
import br.inpe.crn.sinda.parser.SindaPcdParser;

/**
 *
 * @author scavenger
 */
public class ExemploPegarTodasAsPCDs {
    /**
     * @param args 
     */
   public static void main(String args[]){
       /*
       SindaWebpageFetcher pageFetcher = new SindaWebpageFetcher();
       SindaPcdParser parser = new SindaPcdParser();
       
       Document pcdHTMLpage = pageFetcher.fetchPcdListPage();
       
       List<Pcd> pcdList = parser.parseToPcdList(pcdHTMLpage);
       
       for(Pcd pcd: pcdList)
           System.out.println( pcd );
       
       System.out.println("TOTAL PCDs: " + pcdList.size() );
     
       
       // Se quiser a PCD SEM INFORMACOES NULAS
       Pcd pcdQualquer = pcdList.get(0);
       
       Document pcdInfoWebPage =  pageFetcher.fetchPcdInfoPage( pcdQualquer.getId(), true);
       
       parser.parsePcdInfo( pcdInfoWebPage, pcdQualquer);
       System.out.println("PCD COM TODAS AS INFOS");
       System.out.println(pcdQualquer);
       */
   }
}
