/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.network;

import java.util.Collection;
import java.util.Date;
import br.inpe.crn.sinda.utility.DateTimeUtils;
import java.text.SimpleDateFormat;

/**
 *
 * @author scavenger
 */
public class QueryParameters {

    private String id;
    private String anoInicial;
    private String anoFinal;
    private String diaInicial;
    private String diaFinal;
    private String mesInicial;
    private String mesFinal;
    private Date dataInicial;
    private Date dataFinal;
    private static final byte INITIAL = 0x01;
    private static final byte FINAL = 0x02;
    
    private final SimpleDateFormat m_dayFormat = DateTimeUtils.getInstance( DateTimeUtils.DAY_FORMAT);
    private final SimpleDateFormat m_monthFormat = DateTimeUtils.getInstance(DateTimeUtils.MONTH_FORMAT );
    private final SimpleDateFormat m_yearFormat =  DateTimeUtils.getInstance(DateTimeUtils.YEAR_FORMAT);
    private final SimpleDateFormat m_dateTimeFormat = DateTimeUtils.getInstance(DateTimeUtils.DATE_TIME_FORMAT);

    private QueryParameters(QueryParametersBuilder builder) {
        id = builder.id;

        if ( (builder.dataInicial != null) && (builder.dataFinal != null)) {
            dataInicial = builder.dataInicial;
            formatingDate(dataInicial, INITIAL);
            dataFinal = builder.dataFinal;
            formatingDate(dataFinal, FINAL);
        } 
        
        else {
            anoInicial = builder.anoInicial;
            anoFinal = builder.anoFinal;
            diaInicial = builder.diaInicial;
            diaFinal = builder.diaFinal;
            mesInicial = builder.mesInicial;
            mesFinal = builder.mesFinal;
        }

    }

    public String getId() {
        return id;
    }

    public String getAnoInicial() {
        return anoInicial;
    }

    public String getAnoFinal() {
        return anoFinal;
    }

    public String getDiaInicial() {
        return diaInicial;
    }

    public String getDiaFinal() {
        return diaFinal;
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    public void setDiaInicial(String diaInicial) {
        this.diaInicial = diaInicial;
    }

    public void setDiaFinal(String diaFinal) {
        this.diaFinal = diaFinal;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
        formatingDate(dataInicial, INITIAL);
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
        formatingDate(dataFinal, FINAL);
    }

    private void formatingDate(final Date date, final byte moment) {
        switch (moment) {
            case INITIAL:
                setDiaInicial(m_dayFormat.format(date));
                setMesInicial(m_monthFormat.format(date));
                setAnoInicial(m_yearFormat.format(date));
                break;

            case FINAL:
                setDiaFinal(m_dayFormat.format(date));
                setMesFinal(m_monthFormat.format(date));
                setAnoFinal(m_yearFormat.format(date));
                break;
        }

    }

    public static class QueryParametersBuilder {

        private String id = "";
        private String anoInicial = "";
        private String anoFinal = "";
        private String diaInicial = "";
        private String diaFinal = "";
        private String mesInicial = "";
        private String mesFinal = "";
        private Date dataInicial;
        private Date dataFinal;

        public QueryParametersBuilder() {
        }

        public QueryParametersBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public QueryParametersBuilder id(final long id) {
            this.id = String.valueOf(id);
            return this;
        }

        public QueryParametersBuilder anoInicial(final String anoInicial) {
            this.anoInicial = anoInicial;
            return this;
        }

        public QueryParametersBuilder dataInicial(final Date dataInicial) {
            this.dataInicial = dataInicial;
            return this;
        }

        public QueryParametersBuilder dataFinal(final Date dataFinal) {
            this.dataFinal = dataFinal;
            return this;
        }

        public QueryParametersBuilder anoFinal(final String anoFinal) {
            this.anoFinal = anoFinal;
            return this;
        }

        public QueryParametersBuilder diaInicial(final String diaInicial) {
            this.diaInicial = diaInicial;
            return this;
        }

        public QueryParametersBuilder diaFinal(final String diaFinal) {
            this.diaFinal = diaFinal;
            return this;
        }

        public QueryParametersBuilder mesInicial(final String mesInicial) {
            this.mesInicial = mesInicial;
            return this;
        }

        public QueryParametersBuilder mesFinal(final String mesFinal) {
            this.mesFinal = mesFinal;
            return this;
        }

        public QueryParameters build() {

            if (id == null || id.isEmpty()) {
                throw new IllegalStateException("NEEDS id PARAMETER TO BUILD");
            } else if ((dataInicial != null) && (dataFinal != null)) {
                return new QueryParameters(this);
            } else if (this.anoFinal == null || anoFinal.isEmpty()) {
                throw new IllegalStateException("NEEDS ano_final PARAMETER TO BUILD");
            } else if (anoInicial == null || anoInicial.isEmpty()) {
                throw new IllegalStateException("NEEDS ano_inicial PARAMETER TO BUILD");
            } else if (diaFinal == null || diaFinal.isEmpty()) {
                throw new IllegalStateException("NEEDS dia_final PARAMETER TO BUILD");
            } else if (diaInicial == null || diaInicial.isEmpty()) {
                throw new IllegalStateException("NEEDS dia_inicial PARAMETER TO BUILD");
            } else if (mesFinal == null || mesFinal.isEmpty()) {
                throw new IllegalStateException("NEEDS mes_final PARAMETER TO BUILD");
            } else if (mesInicial == null || mesInicial.isEmpty()) {
                throw new IllegalStateException("NEEDS mes_inicial PARAMETER TO BUILD");
            } else {
                return new QueryParameters(this);
            }
        }
    }

}
