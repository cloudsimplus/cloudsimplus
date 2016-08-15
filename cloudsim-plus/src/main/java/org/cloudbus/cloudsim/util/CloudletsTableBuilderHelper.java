package org.cloudbus.cloudsim.util;

import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;

/**
 * A class to help printing simulation results for a list of cloudlets.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class CloudletsTableBuilderHelper {
    public static void print(final TableBuilder printer, final List<? extends Cloudlet> list){
        if(printer.getTitle().isEmpty()){
            printer.setTitle("OUTPUT");
        }
        
        printer.addColumn("CloudletID");
        printer.addColumn("STATUS ");
        printer.addColumn("DatacenterID");
        printer.addColumn("VmID");
        printer.addColumn("CloudletLen").setSubTitle("MI");
        printer.addColumn("CloudletPEs");
        printer.addColumn("StartTime").setFormat("%d").setSubTitle("Seconds");
        printer.addColumn("FinishTime").setFormat("%d").setSubTitle("Seconds");
        printer.addColumn("ExecTime").setFormat("%.0f").setSubTitle("Seconds");
        
        List<Object> row;
        for (Cloudlet cloudlet: list) {
            row = printer.newRow();
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
