package br.inpe.crn.sinda.applications;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author scavenger
 */

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class TableGenerator {

    public static void main(String[] args) {
        Configuration conf = new AnnotationConfiguration();
        conf.configure(); // read hibernat.cfg file
        SchemaExport se = new SchemaExport(conf);
        se.create(true, true); // tables creation

    }
}
