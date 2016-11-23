/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class read the sla agreements in json format. 
 * The sla agreements is divided into two classes: 
 * metrics and violations.
 *
 * @author raysaoliveira
 */
public class ReadSlaAgreements {

    private int value;
    private String name, unit;

    private int valueResponseTime;
    private String nameMetricRT;

    private static final String FILE_NAME = "/Users/raysaoliveira/Desktop/TeseMestradoEngInformatica/cloudsim-plus/cloudsim-plus-testbeds/src/main/java/org/cloudsimplus/sla/readJsonFile/SlaMetric.json";

    private static final String FILE_NAME2 = "/Users/raysaoliveira/Desktop/TeseMestradoEngInformatica/cloudsim-plus/cloudsim-plus-testbeds/src/main/java/org/cloudsimplus/sla/readJsonFile/SlaViolations.json";

    public static void main(String[] args) {
        Gson gson = new Gson();
        ReadSlaAgreements sla = new ReadSlaAgreements();

        try {
            Metrics[] metric = gson.fromJson(new FileReader(FILE_NAME), Metrics[].class);

            for (Metrics m : metric) {
                System.out.println(m.toString());
                sla.name = m.getMetricName();
                sla.value = m.getValue();
                sla.unit = m.getUnit();
                Metrics metrics = new Metrics(sla.name, sla.value, sla.unit);

               //System.out.println("Metric: " + sla.name + "  Value: " + sla.value );
                if (m.getMetricName().trim().equals("responseTime")) {
                    sla.nameMetricRT = m.getMetricName();
                    sla.valueResponseTime = m.getValue();
                }
            }

            System.out.println("\n Violations \n");
            Violations[] violations = gson.fromJson(new FileReader(FILE_NAME2), Violations[].class);
            for (Violations v : violations) {
                // System.out.println("Metric name violation: " + v.getMetricNameViolation() + "  Max: " + v.getMax() + "  Min: " + v.getMin());
            }

        } catch (FileNotFoundException ex) {
            System.out.println("File not foudn: " + FILE_NAME);
            System.out.println("File not foudn: " + FILE_NAME2);

        }
        System.out.println(" -> Value: " + sla.getValueResponseTime() + " name: " + sla.getNameMetricRT());
    }

   

    /**
     * @return the Value
     */
    public int getValue() {
        return value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the valueResponseTime
     */
    public int getValueResponseTime() {
        return valueResponseTime;
    }

    /**
     * @return the nameMetricRT
     */
    public String getNameMetricRT() {
        return nameMetricRT;
    }

}
