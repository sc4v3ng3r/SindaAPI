/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import br.inpe.crn.sinda.model.Pcd;
import br.inpe.crn.sinda.model.PcdData;
import br.inpe.crn.sinda.model.PcdType;
import br.inpe.crn.sinda.network.QueryParameters;
import br.inpe.crn.sinda.network.SindaWebpageFetcher;
import br.inpe.crn.sinda.utility.DateTimeUtils;

/**
 * Esta classe tem como objetivo interpretar as páginas web do sistema SINDA e
 * transformar seus dados para o modelo JAVA.
 *
 * @author scavenger
 */
public class SindaPcdParser {

    private final SindaWebpageFetcher m_fetcher = new SindaWebpageFetcher();
    private final SimpleDateFormat m_dayFormat = DateTimeUtils.getInstance(DateTimeUtils.DAY_FORMAT);
    private final SimpleDateFormat m_monthFormat = DateTimeUtils.getInstance(DateTimeUtils.MONTH_FORMAT);
    private final SimpleDateFormat m_yearFormat = DateTimeUtils.getInstance(DateTimeUtils.YEAR_FORMAT);
    private final SimpleDateFormat m_dateTimeFormat = DateTimeUtils.getInstance(DateTimeUtils.DATE_TIME_FORMAT);
    private QueryParameters m_parameters = null;
    private final QueryParameters.QueryParametersBuilder m_builder = new QueryParameters.QueryParametersBuilder();

    /**
     * Este método interpreta a página Html das listas de PCD's do site do Sinda
     * e retorna a lista das PCD's todas com os dados básicos configurados. Os
     * dados básicos são: Id, Estação e UF
     *
     * @param htmlDoc página html do sinda para ser interpretada.
     * @return List<Pcd> Lista com todas PCDs disponíveis no Sinda.
     */
    public List<Pcd> parseToPcdList(Document htmlDoc) {

        ArrayList<Pcd> pcdList = new ArrayList<>();

        try {
            Element selectElement = htmlDoc.getElementById("select");
            ListIterator<Element> iterator = selectElement.children().listIterator();

            String[] dataString;
            Pcd.PcdBuilder builder = new Pcd.PcdBuilder();

            while (iterator.hasNext()) {
                Element option = iterator.next();
                dataString = option.text().split("-");// { [0] ID, [1] UF, [2] STATION }

                if ((dataString != null) || (dataString.length > 0)) {
                    builder.id(Long.parseLong(dataString[0]));
                    builder.uf(dataString[1]);
                    builder.estacao(dataString[2]);
                }
                pcdList.add(builder.build());

            }

        } catch (Exception ex) {
            //System.out.println("ERROR at SindaPcdParser::parseToPcdList(Document htmlDoc) | htmlDoc is invalid or null!");
        }

        return pcdList;
    }

    /**
     * Esse interpreta a página do sinda que possui informações de localidade e
     * período de funcionamento de uma determinada PCD. Ele retorna a PCD com
     * seus dados completos caso eles existam na página.
     * Obtem:
     *  Periodo Inicial,
     *  Perido FInal,
     * Altitude,
     * Latitude,
     * Longitude,
     * Municipio
     * @param htmlDoc Página html do sinda para ser interpretada.
     * @param pcd PCD que terá seus dados preenchidos.
     * @return retorna a pcd que entrou como parâmetro com os dados preenchidos
     * caso estejam disponíveis no sistema Sinda.
     */
    public Pcd parsePcdInfo(final Document htmlDoc, Pcd pcd) {
        Element input = htmlDoc.getElementsByTag("form").first();
        List<String> periodos = obterPeriodos(htmlDoc);

        if (periodos.size() == 2) {
            try {
                pcd.setPeriodoInicial( m_dateTimeFormat.parse( periodos.get(0) ) );
                pcd.setPeriodoFinal(  m_dateTimeFormat.parse(  periodos.get(1)  ) );
            }
            catch (ParseException ex) {
                Logger.getLogger(SindaPcdParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Inicial: , Final: Municipio: Rio Branco/ AC, Latitude: -9.952, Longitude: -67.857, Altitude: 185
            /*
         = {
            0 "Inicial: " 
            1 "Final: Municipio: Rio Branco/ AC"
            2 "Latitude: -xxxxx"
            3 "Longitude: xxxtyyyy"
            4 "Altitude: xxxxx"
        };
             */
            //System.out.println(input.ownText());
            String dataInfo[] = input.ownText().trim().split(",");

            if (dataInfo.length == 5) {
                String municipio = dataInfo[1].substring(18).trim();
                String latitude = dataInfo[2].split(":")[1].trim();
                String longitude = dataInfo[3].split(":")[1].trim();
                String altitude = " ";
                
                try {
                    altitude = dataInfo[4].split(":")[1].trim();
                } 
                
                catch (ArrayIndexOutOfBoundsException ex) {
                   // System.out.println("PCD ID: " + pcd.getId() + " " + pcd.getUf() + " " + pcd.getEstacao() + " NAO TEM ALTITUDE DEFINIDA ");
                }

                pcd.setMunicipio(municipio);
                pcd.setAltitude(altitude);
                pcd.setLatitude(latitude);
                pcd.setLongitude(longitude);

                //System.out.println( pcd );
            } else {
                pcd.setMunicipio("");
                pcd.setAltitude("");
                pcd.setLatitude("");
                pcd.setLongitude("");
                //System.out.println("PCD " + pcd.getId() + " BUGADA!");
            }

        }
        return pcd;
    }

    /**
     * Interpreta uma página web do sistema sinda com uma tabela de dados de uma
     * determinada PCD retorna um List<String> contendo a descrição dos sensores
     * ou "índices" da PCD ATUAL.
     *
     * @param htmlDoc
     * @return
     */
    public List<String> parsePcdSensorsDescription(final Document htmlDoc) {
        List<String> indexes = new ArrayList<>();
        Element tableBody = htmlDoc.getElementsByTag("tbody").first();

        Iterator<Element> rowsIterator = tableBody.getElementsByTag("tr").iterator();

        if (rowsIterator.hasNext()) {
            indexes = gettingColumnsDescription(rowsIterator.next());
        }

        return indexes;
    }

    /**
     * Interpreta uma página web do sistema sinda com uma tabela de dados de uma
     * determinada PCD e retorna um List<PcdData> com os dados extraídos da
     * página. A ordem dos dados é extraída na mesma ordem dos índices dos
     * contadores da PCD
     *
     * @param htmlDoc página web com a tabela de dados
     * @return um List<PcdData>
     */
    public PcdType parsePcdType(final Document tableDoc) {
        List<String> indexes;
        PcdType type = null;
       
        if (tableDoc == null) {
            Logger.getLogger(SindaWebpageFetcher.class.getName() + "   "
                    + Thread.currentThread().getName() + "  parsePcdType::NULL DATA TABLE!");
            //System.out.println("");
        } 
        
        else {
            Element tableBody = tableDoc.getElementsByTag("tbody").first();
            Iterator<Element> rowsIterator = tableBody.getElementsByTag("tr").iterator();
            //int indexCounter;
            if ( rowsIterator.hasNext() ) {
                indexes = gettingColumnsDescription(rowsIterator.next());

                if ( !indexes.isEmpty() ) {
                     type = new PcdType();
                    // colocando os indices em uma unica string. os split podera ser feito com um '#'
                    for (String sensor : indexes) {
                        type.addSensor( sensor );
                    }
                }
            }
        }
        return type;
    }

    /*AINDA ESTAR OBTENDO AS COLUMNAS,  POREM ESTA IGNORANDO..*/
    public List<PcdData> parsePcdDataTable(final Document htmlDoc) {
       // System.out.println(Thread.currentThread().getName() + "  PARSING TABLE...  ");
        List<PcdData> dataList = new ArrayList<>();
        List<String> indexes;

        // AQUI DA NULL POINTER!
        if (htmlDoc == null) {
            Logger.getLogger(SindaWebpageFetcher.class.getName() + "   "
                    + Thread.currentThread().getName() + "  parsePcdDataTable::NULL DATA TABLE!");
            //System.out.println("");
        } else {
            Element tableBody = htmlDoc.getElementsByTag("tbody").first();
            Iterator<Element> rowsIterator = tableBody.getElementsByTag("tr").iterator();
            PcdData.PcdDataBuilder pcdBuilder = new PcdData.PcdDataBuilder();

            //int indexCounter;
            if (rowsIterator.hasNext()) {
                indexes = gettingColumnsDescription(rowsIterator.next());

                if (!indexes.isEmpty()) {
                    String pcdDataColumns = "";
                    // colocando os indices em uma unica string. os split podera ser feito com um '#'
                    for (String index : indexes) {
                        pcdDataColumns += index + "#";
                    }

                    // obtendo os valores de cada linha(TUPLAS) da tabela de dados. 
                    while (rowsIterator.hasNext()) {
                        Element row = rowsIterator.next();
                        Iterator<Element> dataValuesIterator = row.getElementsByTag("td").iterator();

                        String dataStringValues = "";

                        while (dataValuesIterator.hasNext()) {
                            String value = dataValuesIterator.next().text();
                            if (value.isEmpty()) {
                                value = " ";
                            }
                            dataStringValues += value + "#";
                        }

                        String date = dataStringValues.split("#")[0];
                        Date collectDate = null;

                        try {
                            collectDate = m_dateTimeFormat.parse(date);
                        } 
                        catch (Exception ex) {
                            collectDate = new Date();
                        }

                        PcdData pcdData = pcdBuilder
                                .data(dataStringValues)
                                //.dataColumns(pcdDataColumns)
                                .dataHoraColeta(collectDate)
                                .build();

                        dataList.add(pcdData);
                    }// fim da obtencao dos valores
                }
            }

            dataList.remove(dataList.size() - 1);

        }

        return dataList;
    }

    /**
     * Retorna uma lista de strings com a descrição das colunas dos sensores de
     * uma PCD
     *
     * @param indexesRow
     * @return
     */
    private List<String> gettingColumnsDescription(Element indexesRow) {
        List<String> indexesList = new ArrayList<>();

        Iterator<Element> it = indexesRow.getElementsByTag("b").iterator();
        while (it.hasNext()) {
            indexesList.add(it.next().ownText());
        }

        return indexesList;
    }

    /**
     * Obtém ambos os períodos: Inicial & Final de uma PCD. Caso os períodos não
     * sejam fornecidos pelo site do sinda a lista de períodos retorna vazia.
     *
     * @param htmlDoc Pagina html de onde será extraído os dados de períodos
     * @return Lista com os períodos. [0] Período inicial [1] Período final
     *
     */
    private List<String> obterPeriodos(final Document htmlDoc) {

        ListIterator<Element> it = htmlDoc.getElementsByTag("b").listIterator(); // obtem ambas tags <b>
        List<String> periodos = new ArrayList();

        while (it.hasNext()) {
            String periodo = it.next().text();
            //System.out.println("ITERATOR GET: " + periodo);
            periodos.add(periodo);
        }
        return periodos;
    }

}
