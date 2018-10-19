/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import br.inpe.crn.sinda.utility.DateTimeUtils;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author scavenger
 */
@Entity
public class Pcd {
    
    @Id
    private long id;
    private String uf;
    private String estacao;
    private String municipio;
    private String altitude;
    private String latitude;
    private String longitude;
    private Date periodoInicial;
    private Date periodoFinal;
    
    private List<PcdData> data;
    
    // colocaremos a lsita de sensores aqui?? vai diminuir a redundancia
   //TEM Q SALVAR COMO UMA STRING UNICA SEPARADA POR "#"
    private List<String> sensores = new ArrayList<>();
    
    public Pcd(){}

    public Pcd( PcdBuilder builder ){
        this.id = builder.id;
        this.uf = builder.uf;
        this.estacao = builder.estacao;
        this.municipio = builder.municipio;
        this.altitude = builder.altitude;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.periodoInicial = builder.periodoInicial;
        this.periodoFinal = builder.periodoFinal;
        this.data = builder.data;
        this.sensores = builder.sensores;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getEstacao() {
        return estacao;
    }
    
    public List<String> getSensores(){
        return this.sensores;
    }

    public void setEstacao(String estacao) {
        this.estacao = estacao;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setSensores(List<String> sensores){
        this.sensores = sensores;
    }
    
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getPeriodoInicial() {
        return periodoInicial;
    }

    public void setPeriodoInicial(Date periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    public Date getPeriodoFinal() {
        return periodoFinal;
    }

    public void setPeriodoFinal(Date periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    public List<PcdData> getData() {
        return data;
    }

    public void setData(List<PcdData> data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        String stringData;
        
                stringData = "ID: " + this.id + 
                " UF: " + this.uf + 
                " ESTACAO: " + this.estacao +
                " MUNICIPIO: " + this.municipio +
                " LATITUDE: " + this.latitude +
                " LONGITUDE: " + this.longitude +
                " ALTITUDE: " + this.altitude; 
                
                /*if ( (this.periodoInicial != null)  && ( this.periodoFinal !=null) ){
                    stringData += " INICIO: " + DateTimeUtils.SIMPLE_DATE_TIME_FORMAT.format( this.periodoInicial)  +
                    " FINAL: " + DateTimeUtils.SIMPLE_DATE_TIME_FORMAT.format(this.periodoFinal);
                }*/
                return stringData;
                
    }
    
    public static class PcdBuilder {
        private long id;
        private String uf;
        private String estacao;
        private String municipio;
        private String altitude;
        private String latitude;
        private String longitude;
        private Date periodoInicial;
        private Date periodoFinal;
        private List<PcdData> data;
        private List<String> sensores;
    
        public PcdBuilder id(final long id){
            this.id = id;
            return this;
        }
        
        public PcdBuilder uf(final String uf){
            this.uf = uf;
            return this;
        }
        
        public PcdBuilder estacao(final String estacao){
            this.estacao = estacao;
            return this;
        }
        
        public PcdBuilder municipio(final String municipio){
            this.municipio = municipio;
            return this;
        }
        
        public PcdBuilder altitude(final String altitude){
            this.altitude = altitude;
            return this;
        }
        
        public PcdBuilder latitude(final String latitude){
            this.latitude = latitude;
            return this;
        }
        
        public PcdBuilder longitude(final String longitude){
            this.longitude = longitude;
            return this;
        }
        
        public PcdBuilder peridoInicial(final Date inicio){
            this.periodoInicial = inicio;
            return this;
        }
        
        public PcdBuilder periodoFinal(final Date periodoFinal){
            this.periodoFinal = periodoFinal;
            return this;
        }
        
        public PcdBuilder data(final List<PcdData> data){
            this.data = data;
            return this;
        }
    
        public PcdBuilder sensores(final List<String> sensores){
            this.sensores = sensores;
            return this;
        }
        
        public Pcd build() {
            //COLOCAR AS REGRAS DE EXECCAO!
             /*
            if ( (this.altitude == null || this.altitude.isEmpty())   ||
                 (this.longitude == null || this.longitude.isEmpty()) ||
                 (this.latitude == null || this.latitude.isEmpty()) ) {
                
                throw new IllegalStateException("CANT BUILD A PCD DATA!");
            }
                */
            return new Pcd(this);
        }
    }
    
}
