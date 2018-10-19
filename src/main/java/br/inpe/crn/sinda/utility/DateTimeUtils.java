/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.utility;

import java.text.SimpleDateFormat;

/**
 *
 * @author scavenger
 */
public class DateTimeUtils {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //public static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT);
    
    public static final String DAY_FORMAT = "dd";
    //public static final  SimpleDateFormat SIMPLE_DATE_DAY_FORMAT = new SimpleDateFormat(DAY_FORMAT);
    
    public static final String MONTH_FORMAT  = "MM";
    //public static final  SimpleDateFormat SIMPLE_MONTH_DATE_FORMAT = new SimpleDateFormat(MONTH_FORMAT);
    
    public static final String YEAR_FORMAT = "yyyy";
   // public static final  SimpleDateFormat SIMPLE_YEAR_DATE_FORMAT = new SimpleDateFormat(YEAR_FORMAT);
    
    public static final int SECONDS = 1000;
    
    public static final synchronized SimpleDateFormat getInstance(final String FORMAT){
        return new SimpleDateFormat(FORMAT);
    }
}