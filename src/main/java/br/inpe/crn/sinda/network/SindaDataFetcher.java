/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.network;

import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.parser.SindaPcdParser;
import java.util.List;
import org.jsoup.nodes.Document;

/**
 *
 * @author scavenger
 */
public class SindaDataFetcher {
    private final SindaWebpageFetcher m_webpageFetcher;
    private final SindaPcdParser m_parser;
    
    
    public SindaDataFetcher(){
        
        m_webpageFetcher = new SindaWebpageFetcher();
        m_parser = new SindaPcdParser();
        
    }
    
    public List<Pcd> getPcdListWithMinimumInfo(){
        return m_parser.parseToPcdList( m_webpageFetcher.fetchPcdListPage() );
    }
    
    public List<Pcd> getPcdListWithNormalInfo(){
        
       List<Pcd> pcdList = getPcdListWithMinimumInfo();
       
       for (Pcd pcd: pcdList){
           Document page =  m_webpageFetcher.fetchPcdInfoPage( pcd.getId(), true);
           m_parser.parsePcdInfo(page, pcd);
       }
       
       return pcdList;
    }
    
    
    public List<Pcd> getPcdListWithFullInfo(){
        List<Pcd> pcdList =  getPcdListWithMinimumInfo();
        
        for(Pcd pcd: pcdList){
            System.out.println("Getting basic info pf: " + pcd.getId() );
            Document infoWebpage = m_webpageFetcher.fetchPcdInfoPage(pcd.getId(),  true);
            m_parser.parsePcdInfo(infoWebpage, pcd);
            System.out.println("DONE!!!");
        }
        
        return pcdList;
    }
}   
