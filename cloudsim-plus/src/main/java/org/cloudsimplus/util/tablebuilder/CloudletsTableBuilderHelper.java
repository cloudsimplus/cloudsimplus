/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.util.tablebuilder;

import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A class to help printing simulation results for a list of cloudlets.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudletsTableBuilderHelper {
    private TableBuilder printer;
    private List<? extends Cloudlet> cloudletList;

    /**
     * Creates new helper object to print the list of cloudlets using the a
     * default {@link TextTableBuilder}.
     * To use a different {@link TableBuilder}, use the
     * {@link #setPrinter(TableBuilder)} method.
     *
     * @param list the list of Cloudlets that the data will be included into the table to be printed
     */
    public CloudletsTableBuilderHelper(final List<? extends Cloudlet> list){
        this.setPrinter(new TextTableBuilder()).setCloudletList(list);
    }

    public CloudletsTableBuilderHelper setTitle(String title){
        printer.setTitle(title);
        return this;
    }

    /**
     * Builds the table with the data of the Cloudlet list and shows the results.
     */
    public void build(){
        if(printer.getTitle().isEmpty()){
            printer.setTitle("SIMULATION RESULTS");
        }

        createTableColumns();
        cloudletList.forEach(cloudlet -> addDataToRow(cloudlet, printer.newRow()));
        printer.print();
    }


    protected void createTableColumns() {
        printer.addColumn("Cloudlet").setSubTitle("ID");
        printer.addColumn("Status ");
        printer.addColumn("DC").setSubTitle("ID");
        printer.addColumn("Host").setSubTitle("ID");
        printer.addColumn("VM").setSubTitle("ID");
        printer.addColumn("CloudletLen").setSubTitle("MI");
        printer.addColumn("CloudletPEs").setSubTitle("CPU cores");
        printer.addColumn("StartTime").setFormat("%d").setSubTitle("Seconds");
        printer.addColumn("FinishTime").setFormat("%d").setSubTitle("Seconds");
        printer.addColumn("ExecTime").setFormat("%.0f").setSubTitle("Seconds");
    }

    /**
     * Add data to a row of the table being generated.
     * @param cloudlet The cloudlet to get to data to show in the row of the table
     * @param row The row to be added the data to
     */
    protected void addDataToRow(Cloudlet cloudlet, List<Object> row) {
        Vm vm = cloudlet.getVm();
        Host host = vm.getHost();
        Datacenter datacenter = host.getDatacenter();

        row.add(cloudlet.getId());
        row.add(cloudlet.getStatus().name());
        row.add(datacenter.getId());
        row.add(host.getId());
        row.add(vm.getId());
        row.add(cloudlet.getLength());
        row.add(cloudlet.getNumberOfPes());
        row.add((int)cloudlet.getExecStartTime());
        row.add((int)cloudlet.getFinishTime());
        row.add(cloudlet.getActualCpuTime());
    }

    public final CloudletsTableBuilderHelper setPrinter(TableBuilder printer) {
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
