/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.network;

/**
 *  Essa classe contém as constantes dos nomes usados nos parâmetros do consulta das requisições ao website sinda. 
 *  Essa requisições são feitas para obter-se os dados relacionados as PCDs.
 *  Todos os nomes e valores foram extraídos através da engenharia reversa de 
 *  requisições realizadas no mesmo site.
 *  @author scavenger
 */
public class SindaRequestParameters  {

    /**
     * Representa o id de uma PCD
     * String QUERY_PARAM_ID = "id"
     */
    public static final String QUERY_PARAM_ID = "id";
    public static final String QUERY_PARAM_ANO_INICIAL = "ano_inicial";
    public static final String QUERY_PARAM_ANO_FINAL = "ano_final";
    public static final String QUERY_PARAM_DIA_INICIAL = "dia_inicial";
    public static final String QUERY_PARAM_DIA_FINAL = "dia_final";
    public static final String QUERY_PARAM_MES_INICIAL = "mes_inicial";
    public static final String QUERY_PARAM_MES_FINAL = "mes_final";
    public static final String QUERY_PARAM_SUBMIT = "submit";
    public static final String QUERY_PARAM_SUBIMIT_VALUE = "Gerar+Dados";
    public static final String QUERY_PARAM_FORM_ID = "form_id";
    public static final String QUERY_PARAM_FORM_ID_VALUE = "902634";
 
}
