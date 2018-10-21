/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import br.inpe.crn.sinda.utility.DateTimeUtils;
import org.jsoup.Connection;

/**
 *
 * @author scavenger
 */
/**
 * Esta classe tem como objetivo servir de interface para obter, pegar as
 * páginas web do sistema Sinda.
 *
 * @author scavenger
 */
public class SindaWebpageFetcher {

    private Document m_lastFetchedDocument;
    private int timeout = 120 * DateTimeUtils.SECONDS;
    //private int timeoutCounter = 0;

    private final String userAgent = "SindaWebpageFetcher";
    private Connection m_infoPageConnection = Jsoup.connect(SindaURLs.PCD_INFO_URL);
    private Connection m_dataQueryConnection = 
            Jsoup.connect(SindaURLs.PCD_DATA_QUERY_URL)
            .ignoreContentType(true)
                        .maxBodySize(10 * 1024 * 1024)
                        .timeout(timeout)
                        .headers(SindaRequestHeaders.DEFAULT_HEADERS);

    public SindaWebpageFetcher() {

    }

    /**
     *
     * @param timeout valor em segundos!
     */
    public SindaWebpageFetcher(int timeout) {
        this.timeout = timeout * DateTimeUtils.SECONDS;
    }

    /**
     * Obtém o tempo limite das conexões em segundos
     *
     * @return tempo limite em segundos
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * *
     * retorna o ultimo documento html obtido por essa instancia de Fetcher.
     *
     * @return Documento de página html
     */
    public Document getLastFetchedDocument() {
        return m_lastFetchedDocument;
    }

    /**
     * Esse Método obtém a página html do sistema sinda que contém a lista de
     * todas as PCDs
     *
     * @return Document Documento html da página
     * http://sinda.crn.inpe.br/PCD/SITE/novo/site/historico/index.php
     */
    public Document fetchPcdListPage() {
        try {
            m_lastFetchedDocument = Jsoup.connect(SindaURLs.PCD_LIST_URL)
                    .timeout(timeout)
                    .get();
        } catch (SocketTimeoutException timeout) {
            System.out.println(SindaWebpageFetcher.class.getName() + "::fetchPcdListPage( TIMEOUT");
        } catch (IOException ex) {
            Logger.getLogger(SindaWebpageFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_lastFetchedDocument;
    }

    /**
     *
     * @param pcdId ID da PCD
     * @param retryTimeout se true, continua indefinidamente refazer a conexão
     * com a página caso o tempo da conexão expire.
     * @return Document Documento html da página
     * http://sinda.crn.inpe.br/PCD/SITE/novo/site/historico/passo2.php
     */
    public Document fetchPcdInfoPage(long pcdId, boolean retryTimeout) {
        int timeOutCounter = 0;
        do {
            try {
                //OBS uma requisicao dessa eh feita para cada SENSOR / PCD
                //System.out.println("SindaWebpageFetcher::fetchPcdInfoPage " + Thread.currentThread().getName());
                m_lastFetchedDocument
                        = //Jsoup.connect(SindaURLs.PCD_INFO_URL)
                        m_infoPageConnection
                                .data(SindaRequestParameters.QUERY_PARAM_ID, String.valueOf(pcdId))
                                .userAgent(userAgent)
                                .timeout(timeout)
                                .post();

                retryTimeout = false;

                //System.out.println("SindaWebpageFetcher::fetchPcdInfoPage [DONE] " + Thread.currentThread().getName());
            } catch (SocketTimeoutException timeoutEx) {
                System.out.println(SindaWebpageFetcher.class.getName()
                        + "fetchPcdInfoPage::" + timeoutEx.getMessage());
                timeOutCounter += 1;

                if (retryTimeout) {
                    System.out.println("RETRIYNG time: " + timeOutCounter + "!!!");
                } else {
                    System.out.println("ABORTING!!!");
                }

            } catch (IOException ex) {
                Logger.getLogger(SindaWebpageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                retryTimeout = false;
            }

        } while (retryTimeout);

        return m_lastFetchedDocument;
    }

    /**
     * Obtém a página web do sistema SINDA que contém a tabela de dados de uma
     * determinada PCD.
     *
     * @param param Parametros de consulta utilizados para construção da tabela
     * de dados.
     * @return Um Document, página web do sistema sinda que contém a tabela de
     * dados.
     */
    public Document fetchPcdDataTablePage(final QueryParameters param, boolean retryTimeout) {
        Document dataTablePage = null;
        do {
            try {
                
                dataTablePage = //Jsoup.connect(SindaURLs.PCD_DATA_QUERY_URL)
                        m_dataQueryConnection        
                        .header(SindaRequestHeaders.PROPERTY_CONTENT_LENGTH, String.valueOf(getQueryParamBytes(param)))
                        .data(getParamToMap(param))
                        .post();
                
                if (dataTablePage != null) {
                    System.out.println(Thread.currentThread().getName() + " TOTAL TABLE LINES RESULTS  " + dataTablePage.getElementsByTag("tr").size());
                } 
                
                else {
                    System.out.println(Thread.currentThread().getName() + " EMPTY TABLE");
                }
                retryTimeout = false;

            } 
            
            catch (SocketTimeoutException ex) {
                // acontece java.io.IOException: Underlying input stream returned zero bytes
                // HEAP SPACE 
                Logger.getLogger(SindaWebpageFetcher.class.getName()).log(Level.SEVERE, null, ex);
               // retryTimeout = true;
            } 
            
            catch (IOException ex) {
                Logger.getLogger(SindaWebpageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                retryTimeout = false;
            }
            // OCASIONA NULL POINTER EXCEPTION

        } while (retryTimeout);

        return dataTablePage;
    }

    /**
     * @param con
     */
    private void setDefaultRequestHeaders(HttpURLConnection con) {

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_ACCEPT,
                SindaRequestHeaders.VALUE_ACCEPT);

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_ACCEPT_ENCODING,
                SindaRequestHeaders.VALUE_ACCEPT_ENCODING);

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_ACCEPT_LANGUAGE,
                SindaRequestHeaders.VALUE_ACCEPT_LANGUAGE);

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_CHARSET,
                SindaRequestHeaders.VALUE_CHARSET);

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_CONNECTION,
                SindaRequestHeaders.VALUE_CONNECTION);

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_CONTENT_TYPE,
                SindaRequestHeaders.VALUE_CONTENT_TYPE);

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_COOKIE,
                SindaRequestHeaders.VALUE_COOKIE);

        con.setRequestProperty(SindaRequestHeaders.PROPERTY_UPGRADE_INSECURE_REQUESTS,
                SindaRequestHeaders.VALUE_UPGRADE_INSECURE_REQUESTS);

    }

    /**
     *
     * @param param
     * @return
     */
    private StringBuilder getQueryParamStringBuilder(final QueryParameters param) {
        final String EQUALS = "=";
        final String AND = "&";

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_ANO_FINAL + EQUALS);
        strBuilder.append(param.getAnoFinal());
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_ANO_INICIAL + EQUALS);
        strBuilder.append(param.getAnoInicial());
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_DIA_FINAL + EQUALS);
        strBuilder.append(param.getDiaFinal());
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_DIA_INICIAL + EQUALS);
        strBuilder.append(param.getDiaInicial());
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_FORM_ID + EQUALS);
        strBuilder.append(SindaRequestParameters.QUERY_PARAM_FORM_ID_VALUE);
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_ID + EQUALS);
        strBuilder.append(param.getId());
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_MES_FINAL + EQUALS);
        strBuilder.append(param.getMesFinal());
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_MES_INICIAL + EQUALS);
        strBuilder.append(param.getMesInicial());
        strBuilder.append(AND);

        strBuilder.append(SindaRequestParameters.QUERY_PARAM_SUBMIT + EQUALS);
        strBuilder.append(SindaRequestParameters.QUERY_PARAM_SUBIMIT_VALUE);
        return strBuilder;
    }

    /**
     *
     * @param param
     * @return
     */
    private byte[] getQueryParamBytes(final QueryParameters param) {
        return getQueryParamStringBuilder(param).toString().getBytes();
    }

    /**
     *
     * @param param
     * @return
     */
    private Map<String, String> getParamToMap(final QueryParameters param) {
        Map<String, String> map = new HashMap<>();
        map.put(SindaRequestParameters.QUERY_PARAM_ANO_FINAL, param.getAnoFinal());
        map.put(SindaRequestParameters.QUERY_PARAM_ANO_INICIAL, param.getAnoInicial());
        map.put(SindaRequestParameters.QUERY_PARAM_DIA_FINAL, param.getDiaFinal());
        map.put(SindaRequestParameters.QUERY_PARAM_DIA_INICIAL, param.getDiaInicial());
        map.put(SindaRequestParameters.QUERY_PARAM_FORM_ID, SindaRequestParameters.QUERY_PARAM_FORM_ID_VALUE);
        map.put(SindaRequestParameters.QUERY_PARAM_ID, param.getId());
        map.put(SindaRequestParameters.QUERY_PARAM_MES_FINAL, param.getMesFinal());
        map.put(SindaRequestParameters.QUERY_PARAM_MES_INICIAL, param.getMesInicial());
        map.put(SindaRequestParameters.QUERY_PARAM_SUBMIT, SindaRequestParameters.QUERY_PARAM_SUBIMIT_VALUE);

        return map;
    }

    // tanks
    //https://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url
}
