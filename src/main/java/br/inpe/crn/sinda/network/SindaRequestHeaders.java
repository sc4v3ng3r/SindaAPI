/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.network;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author scavenger
 */
public class SindaRequestHeaders {
    
    public static final String PROPERTY_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String VALUE_ACCEPT_ENCODING = "gzip, deflate";
    
    public static final String PROPERTY_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String VALUE_ACCEPT_LANGUAGE = "en-US,en;q=0.5";
    
    public static final String PROPERTY_ACCEPT = "Accept";
    public static final String VALUE_ACCEPT = "text/html,application/xhtml+xm…plication/xml;q=0.9,*/*;q=0.8";
    
    public static final String PROPERTY_CHARSET = "charset";
    public static final String VALUE_CHARSET = "UTF-8";
    
    public static final String PROPERTY_CONNECTION = "Connection";
    public static final String VALUE_CONNECTION = "keep-alive";
    
    public static final String PROPERTY_CONTENT_TYPE = "Content-Type";
    public static final String VALUE_CONTENT_TYPE = "application/x-www-form-urlencoded";
    
    public static final String PROPERTY_CONTENT_LENGTH = "Content-Length";
    
    public static final String PROPERTY_COOKIE = "Cookie";
    public static final String VALUE_COOKIE = "_ga=GA1.2.1110815325.153537842…id=GA1.2.827305906.1535590385";
    
    public static final String PROPERTY_UPGRADE_INSECURE_REQUESTS = "Upgrade-Insecure-Requests";
    public static final String VALUE_UPGRADE_INSECURE_REQUESTS = "1";
    
    public static final HashMap<String, String> DEFAULT_HEADERS; 
    
    static {
        DEFAULT_HEADERS = new HashMap<>();
        DEFAULT_HEADERS.put(PROPERTY_ACCEPT_ENCODING, VALUE_ACCEPT_ENCODING );
        DEFAULT_HEADERS.put(PROPERTY_ACCEPT_LANGUAGE, VALUE_ACCEPT_LANGUAGE);
        DEFAULT_HEADERS.put(PROPERTY_ACCEPT, VALUE_ACCEPT);
        DEFAULT_HEADERS.put(PROPERTY_CHARSET, VALUE_CHARSET);
        DEFAULT_HEADERS.put(PROPERTY_CONNECTION, VALUE_CONNECTION);
        DEFAULT_HEADERS.put(PROPERTY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
        DEFAULT_HEADERS.put(PROPERTY_COOKIE ,VALUE_COOKIE);
        DEFAULT_HEADERS.put(PROPERTY_UPGRADE_INSECURE_REQUESTS, VALUE_UPGRADE_INSECURE_REQUESTS);
    }
    
}
