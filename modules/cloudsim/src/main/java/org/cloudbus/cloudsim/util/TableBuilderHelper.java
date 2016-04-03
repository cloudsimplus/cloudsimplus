package org.cloudbus.cloudsim.util;

import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;

/**
 * A class to help printing simulation results.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class TableBuilderHelper {
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
            .addColumn("StartTime",  "%d")
            .addColumn("FinishTime", "%d")
            .addColumn("ExecTime",  "%.0f");
        for (Cloudlet cloudlet: list) {
            List<Object> row = printer.newRow();
            row.add(cloudlet.getId());
            row.add(cloudlet.getStatus().name());
            row.add(cloudlet.getDatacenterId());
            row.add(cloudlet.getVmId());
            row.add(cloudlet.getCloudletLength());
            row.add(cloudlet.getNumberOfPes());
            row.add((int)cloudlet.getExecStartTime());
            row.add((int)cloudlet.getFinishTime());
            row.add(cloudlet.getActualCPUTime());
        }
        
        printer.print();
    }
}
