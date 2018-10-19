/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author scavenger
 */
@Entity
public class PcdData {
    
    @Id
    private long id;
    
    private Date dataHoraColeta;
    private String data;
    private String dataColumns;

    // necessario para o hibernate
    public PcdData() {}

    public PcdData(PcdDataBuilder builder){
        this.id = builder.id;
        this.dataHoraColeta = builder.dataHoraColeta;
        this.data = builder.data;
        this.dataColumns = builder.dataColumns;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDataHoraColeta() {
        return dataHoraColeta;
    }

    public void setDataHoraColeta(Date dataHoraColeta) {
        this.dataHoraColeta = dataHoraColeta;
    }

    public String getData() {
        return data;
    }

    public void setData(String pcdData) {
        this.data = pcdData;
    }

    public String getDataColumns() {
        return dataColumns;
    }

    public void setDataColumns(String dataColumns) {
        this.dataColumns = dataColumns;
    }

    @Override
    public String toString() {
        return /*"ID: " + this.id + " + " Hora coleta: " + this.dataHoraColeta +*/
                "\n data columns: " + this.dataColumns
                +"\n DATA: " + this.data;
    }
    
    public static class PcdDataBuilder{
        private long id;
        private Date dataHoraColeta;
        private String data;
        private String dataColumns;
        
        public PcdDataBuilder id(final long id){
            this.id = id;
            return this;
        }
        
        public PcdDataBuilder dataHoraColeta(final Date date){
            this.dataHoraColeta = date;
            return this;
        }
        
        public PcdDataBuilder data(final String data){
            this.data = data;
            return this;
        }
        
        public PcdDataBuilder dataColumns(final String dataColumns){
            this.dataColumns = dataColumns;
            return this;
        }
        
        public PcdData build(){
            /*
            if (this.dataHoraColeta == null ||
                    (data == null || data.isEmpty())){
                throw new IllegalStateException("DATA and TIME CANNOT BE EMPTY FIELDS");
            }
            
            if (this.dataColumns == null ||
                    this.dataColumns.isEmpty() ){
                throw new IllegalStateException("data columns CANNOT BE EMPTY FIELD");
            }           */ 
            return new PcdData(this);
        }
        
    }
    
}
