/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inpe.crn.sinda.applications.dataminingapp;

/**
 *
 * @author scavenger
 */
public class PcdHistory {
    private String id;
    private String lastUpdate;

    
    public PcdHistory(){}
    
    public PcdHistory(String id, String lastUpdate){
        setId(id);
        setLastUpdate(lastUpdate);
    }
    
    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public final void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    
}
