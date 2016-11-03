package org.cloudbus.cloudsim.util;

import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;

/**
 * A class to help printing simulation results for a list of cloudlets.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class CloudletsTableBuilderHelper {
    private TableBuilder printer; 
    private List<? extends Cloudlet> cloudletList;

    /**
     * Creates new helper object and prints the list of cloudlets using the given printer.
     * 
     * @param printer the printer to be used to generate the table to be printed
     * @param list the list of Cloudlets that the data will be included into the table to be printed
     */    
    public CloudletsTableBuilderHelper(final TableBuilder printer, final List<? extends Cloudlet> list){
        this.setPrinter(printer)
            .setCloudletList(list)
            .buildTable();        
    }
    
    protected void buildTable(){
        if(printer.getTitle().isEmpty()){
            printer.setTitle("SIMULATION RESULTS");
        }
        
        createTableColumns();
        cloudletList.stream().forEach(cloudlet -> addDataToRow(cloudlet, printer.newRow()));
        printer.print();
    }

    /**
     * Add data to a row of the table being generated.
     * @param cloudlet The cloudlet to get to data to show in the row of the table
     * @param row The row to be added the data to
     */
    protected void addDataToRow(Cloudlet cloudlet, List<Object> row) {
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

    protected void createTableColumns() {
        printer.addColumn("Cloudlet").setSubTitle("ID");
        printer.addColumn("Status ");
        printer.addColumn("DC").setSubTitle("ID");
        printer.addColumn("VM").setSubTitle("ID");
        printer.addColumn("CloudletLen").setSubTitle("MI");
        printer.addColumn("CloudletPEs").setSubTitle("CPU cores");
        printer.addColumn("StartTime").setFormat("%d").setSubTitle("Seconds");
        printer.addColumn("FinishTime").setFormat("%d").setSubTitle("Seconds");
        printer.addColumn("ExecTime").setFormat("%.0f").setSubTitle("Seconds");
    }

    protected final CloudletsTableBuilderHelper setPrinter(TableBuilder printer) {
        this.printer = printer;
        return this;
    }

    protected CloudletsTableBuilderHelper setCloudletList(List<? extends Cloudlet> cloudletList) {
        this.cloudletList = cloudletList;
        return this;
    }

    protected TableBuilder getPrinter() {
        return printer;
    }
}
