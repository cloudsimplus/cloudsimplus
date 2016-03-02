package org.cloudbus.cloudsim.examples.util;

import org.cloudbus.cloudsim.util.TableBuilder;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;

/**
 * A class to help printing simulation results.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class ResultsHelper {
    public static void print(final TableBuilder printer, final List<Cloudlet> list){
        if(printer.getTitle().isEmpty()){
            printer.setTitle("OUTPUT");
        }
        
        printer
            .addColumn("CloudletID")
            .addColumn("STATUS ")
            .addColumn("DatacenterID")
            .addColumn("VmID")
            .addColumn("CloudletLen")
            .addColumn("CloudletPEs")
            .addColumn("ExecTime",  "%d")
            .addColumn("StartTime",  "%d")
            .addColumn("FinishTime", "%d");
        for (Cloudlet cloudlet: list) {
            List<Object> row = printer.newRow();
            row.add(cloudlet.getCloudletId());
            row.add(cloudlet.getStatus().name());
            row.add(cloudlet.getResourceId());
            row.add(cloudlet.getVmId());
            row.add(cloudlet.getCloudletLength());
            row.add(cloudlet.getNumberOfPes());
            row.add((int)cloudlet.getActualCPUTime());
            row.add((int)cloudlet.getExecStartTime());
            row.add((int)cloudlet.getFinishTime());
        }
        
        printer.print();

    }
}
