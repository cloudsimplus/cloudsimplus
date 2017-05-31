/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
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
package org.cloudsimplus.builders.tables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * Builds a table for printing simulation results from a list of Cloudlets.
 * It defines a set of default columns but new ones can be added
 * dynamically using the {@code addColumn()} methods.
 *
 * <p>The basic usage of the class is by calling its constructor,
 * giving a list of Cloudlets to be printed, and then
 * calling the {@link #build()} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudletsTableBuilder {
    private static final String SECONDS = "Seconds";
    private TableBuilder table;
    private List<? extends Cloudlet> cloudletList;
    /**
     * A Map containing a function that receives a Cloudlet and returns
     * the data to be printed from that Cloudlet to the associated column
     * of the table to be printed.
     */
    private Map<TableColumn, Function<Cloudlet, Object>> columnsDataFunctions;

    /**
     * Creates new helper object to print the list of cloudlets using the a
     * default {@link TextTableBuilder}.
     * To use a different {@link TableBuilder}, use the
     * {@link #setTable(TableBuilder)} method.
     *
     * @param list the list of Cloudlets that the data will be included into the table to be printed
     */
    public CloudletsTableBuilder(final List<? extends Cloudlet> list){
        Objects.requireNonNull(list);
        setTable(new TextTableBuilder());
        setCloudletList(list);
        columnsDataFunctions = new HashMap<>();
    }

    public CloudletsTableBuilder setTitle(String title){
        Objects.requireNonNull(title);
        table.setTitle(title);
        return this;
    }

    /**
     * Builds the table with the data of the Cloudlet list and shows the results.
     */
    public void build(){
        if(table.getTitle().isEmpty()){
            table.setTitle("SIMULATION RESULTS");
        }

        createTableColumns();
        cloudletList.forEach(cloudlet -> addDataToRow(cloudlet, table.newRow()));
        table.print();
    }

    /**
     * Dynamically adds a column to the end of the table to be built.
     * @param col the column to add
     * @param dataFunction a function that receives a Cloudlet and returns the data to be printed for the added column
     * @return
     */
    public CloudletsTableBuilder addColumn(final TableColumn col, Function<Cloudlet, Object> dataFunction){
        return addColumn(table.getColumns().size(), col, dataFunction);
    }

    /**
     * Dynamically adds a column to a specific position into the table to be built.
     * @param index the position to insert the column.
     * @param col the column to add
     * @param dataFunction a function that receives a Cloudlet and returns the data to be printed for the added column
     * @return
     */
    public CloudletsTableBuilder addColumn(final int index, final TableColumn col, Function<Cloudlet, Object> dataFunction){
        Objects.requireNonNull(col);
        Objects.requireNonNull(dataFunction);

        col.setTable(table);
        table.addColumn(index, col);
        columnsDataFunctions.put(col, dataFunction);
        return this;
    }

    /**
     * Creates the columns of the table and define how the data for those columns
     * will be got from a Cloudlet.
     */
    protected void createTableColumns() {
        final String ID = "ID";
        columnsDataFunctions.put(table.addColumn("Cloudlet", ID), c -> c.getId());
        columnsDataFunctions.put(table.addColumn("Status "), c -> c.getStatus().name());
        columnsDataFunctions.put(table.addColumn("DC", ID), c -> c.getVm().getHost().getDatacenter().getId());
        columnsDataFunctions.put(table.addColumn("Host", ID), c -> c.getVm().getHost().getId());
        columnsDataFunctions.put(table.addColumn("Host PEs ", "CPU cores"), c -> c.getVm().getHost().getNumberOfWorkingPes());
        columnsDataFunctions.put(table.addColumn("VM", ID), c -> c.getVm().getId());
        columnsDataFunctions.put(table.addColumn("VM PEs   ", "CPU cores"), c -> c.getVm().getNumberOfPes());
        columnsDataFunctions.put(table.addColumn("CloudletLen", "MI"), c -> c.getLength());
        columnsDataFunctions.put(table.addColumn("CloudletPEs", "CPU cores"), c -> c.getNumberOfPes());

        TableColumn col = table.addColumn("StartTime", SECONDS).setFormat("%d");
        columnsDataFunctions.put(col, c -> (long)c.getExecStartTime());

        col = table.addColumn("FinishTime", SECONDS).setFormat("%d");
        columnsDataFunctions.put(col, c -> (long)c.getFinishTime());

        col = table.addColumn("ExecTime", SECONDS).setFormat("%d");
        columnsDataFunctions.put(col, c -> (long)c.getActualCpuTime());
    }

    /**
     * Add data to a row of the table being generated.
     * @param cloudlet The cloudlet to get to data to show in the row of the table
     * @param row The row to be added the data to
     */
    protected void addDataToRow(Cloudlet cloudlet, List<Object> row) {
        table.getColumns()
            .stream()
            .forEach(col -> row.add(columnsDataFunctions.get(col).apply(cloudlet)));
    }

    /**
     * Sets the {@link TableBuilder} used to build the table with Cloudlet Data.
     * The default table builder is {@link TextTableBuilder}.
     * @param table the  {@link TableBuilder} to set
     * @return
     */
    public final CloudletsTableBuilder setTable(TableBuilder table) {
        Objects.requireNonNull(table);
        this.table = table;
        return this;
    }

    protected final CloudletsTableBuilder setCloudletList(List<? extends Cloudlet> cloudletList) {
        Objects.requireNonNull(cloudletList);
        this.cloudletList = cloudletList;
        return this;
    }

    protected TableBuilder getTable() {
        return table;
    }
}
