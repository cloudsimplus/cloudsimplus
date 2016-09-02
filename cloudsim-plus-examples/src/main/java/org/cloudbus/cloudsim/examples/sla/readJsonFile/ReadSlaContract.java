/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.examples.sla.readJsonFile;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class read the sla contract in json format. 
 * The sla contract is divided into two classes: metrics and violations. 
 * @author raysaoliveira
 */
public class ReadSlaContract {

    private static final String FILE_NAME = "/Users/raysaoliveira/Desktop/TeseMestradoEngInformatica/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudbus/cloudsim/examples/sla/readJsonFile/SlaMetric.json";

    private static final String FILE_NAME2 = "/Users/raysaoliveira/Desktop/TeseMestradoEngInformatica/cloudsim-plus/cloudsim-plus-examples/src/main/java/org/cloudbus/cloudsim/examples/sla/readJsonFile/SlaViolations.json";

    public static void main(String[] args) {
        Gson gson = new Gson();
      
        try {
            System.out.println("\n Metrics \n");
            Metrics[] metric = gson.fromJson(new FileReader(FILE_NAME), Metrics[].class);
            for (Metrics m : metric) {
                System.out.println("Metric: " + m.getMetricName() + "  Value: " + m.getValue() + "  Unit: " + m.getUnit());
            }
            
            System.out.println("\n Violations \n");
            Violations[] violations = gson.fromJson(new FileReader(FILE_NAME2), Violations[].class);
            for (Violations v : violations) {
                System.out.println("Metric name violation: " + v.getMetricNameViolation() + "  Max: " + v.getMax() + "  Min: " + v.getMin());
            }

        } catch (FileNotFoundException ex) {
            System.out.println("File not foudn: " + FILE_NAME);
            System.out.println("File not foudn: " + FILE_NAME2);

        }
    }
}
