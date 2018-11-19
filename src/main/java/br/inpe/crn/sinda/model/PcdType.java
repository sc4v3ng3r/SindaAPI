/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author scavenger
 */

@Entity
public class PcdType implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Ignore
    private String sensores = "";
    private String descricao = "";

    
    protected PcdType(PcdTypeBuilder builder ){
        this.id = builder.id;
        this.sensores = builder.sensores;
        this.descricao = builder.descricao;
    
    }
    
    public PcdType(){}
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonGetter
    public String getSensores() {
        return sensores;
    }

    @JsonIgnore
    public List<String> getSensoresList(){
        List<String> list = new ArrayList<>();
        
        if (sensores!= null){
            String[] sensoresVet = this.sensores.split("#");
            list.addAll( Arrays.asList(sensoresVet) );
        }
        
        return list;
    }
    
    public void setSensores(String sensores) {
        this.sensores = sensores;
    }
    
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void addSensor(String sensor){
        this.sensores+= sensor+"#";
    }
    @Override
    public boolean equals(Object obj) {
        boolean flag = false;
        
        if (obj instanceof PcdType){
            PcdType pcdType = (PcdType) obj;
            flag = ( this.descricao.compareTo( pcdType.descricao ) == 0 );
            flag = ( this.sensores.compareTo( pcdType.sensores )==0);
        }
        
        return flag; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "PCD TYPE  " + "ID: " + id + " SENSORS: " + sensores;
    }
    
    
    
    
    public class PcdTypeBuilder {
         private long id;
        private String sensores;
        private String descricao = "";
    
        public PcdTypeBuilder(){ }
        
        public PcdTypeBuilder id(final long id){
           this.id = id;
           return this;
        }
        
        public PcdTypeBuilder sensores(final String sensores){
            this.sensores = sensores;
            return this;
        }
        
        public PcdTypeBuilder descricao(final String descricao){
            this.descricao = descricao;
            return this;
        }
       
        public PcdType build(){
            
            /*
            * TEM QUE VALIZAR OS DADOS!!
            */
            return new PcdType(this);
        }
        
    }
    
}
