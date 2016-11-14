package org.cloudsimplus.util.tablebuilder;

import org.cloudbus.cloudsim.Cloudlet;

import java.util.List;

/**
 * A helper class to print cloudlets results as a table, including the Cloudlet priority value.
 *
 * @author Manoel Campos da Silva Filho
 */
public class PriorityCloudletsTableBuilderHelper extends CloudletsTableBuilderHelper {
    public PriorityCloudletsTableBuilderHelper(List<? extends Cloudlet> list) {
        super(list);
    }

    @Override
    protected void createTableColumns() {
        super.createTableColumns();
        getPrinter().addColumn("Priority");
    }

    @Override
    protected void addDataToRow(Cloudlet cloudlet, List<Object> row) {
        super.addDataToRow(cloudlet, row);
        row.add(cloudlet.getPriority());
    }
}
