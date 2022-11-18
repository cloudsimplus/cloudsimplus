/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Identifiable;

import java.util.ArrayList;
import java.util.List;

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
public class CloudletsTableBuilder extends TableBuilderAbstract<Cloudlet> {
    private static final String SECONDS = "Seconds";
    private static final String CPU_CORES = "CPU cores";
    private static final String ID = "ID";
	
	private String timeFormat = "%.1f";
	private String lengthFormat = "%d";
	private String idFormat = "%d";
	private String peFormat = "%d";

    /**
     * Instantiates a builder to print the list of Cloudlets using the a
     * default {@link MarkdownTable}.
     * To use a different {@link Table}, check the alternative constructors.
     *
     * @param list the list of Cloudlets to print
     */
    public CloudletsTableBuilder(final List<? extends Cloudlet> list) {
        super(list);
    }

    /**
     * Instantiates a builder to print the list of Cloudlets using the
     * given {@link Table}.
     *
     * @param list the list of Cloudlets to print
     * @param table the {@link Table} used to build the table with the Cloudlets data
     */
    public CloudletsTableBuilder(final List<? extends Cloudlet> list, final Table table) {
        super(list, table);
    }
    
    /**
     * Contains all columns that represent a time value
     * which may need to be formatted accordingly.
     */
    private List<TableColumn> timeColumnList = new ArrayList<TableColumn>();
    
    /**
     * Contains all columns that represent length values (MIs)
     * which may need to be formatted accordingly.
     */
    private List<TableColumn> lengthColumnList = new ArrayList<TableColumn>();
    
    /**
     * Contains all columns that represent ID values
     * which may need to be formatted accordingly.
     */
    private List<TableColumn> idColumnList = new ArrayList<TableColumn>();
    
    /**
     * Contains all columns that represent PE counts
     * which may need to be formatted accordingly.
     */
    private List<TableColumn> peColumnList = new ArrayList<TableColumn>();

    @Override
    protected void createTableColumns() {
        
        // Set up all table fields
        
        // Cloudlet
        final var cloudletCol = getTable().addColumn("Cloudlet", ID,getIDFormat());
        idColumnList.add(cloudletCol);
        addColDataFunction(cloudletCol, Identifiable::getId);
        
        // Status
        final var statusCol = getTable().addColumn("Status "); // 1 extra space to ensure proper formatting
        addColDataFunction(statusCol , cloudlet -> cloudlet.getStatus().name());
        
        // DC
        final var dcCol = getTable().addColumn("DC", ID,getIDFormat());
        idColumnList.add(dcCol);
        addColDataFunction(dcCol, cloudlet -> cloudlet.getVm().getHost().getDatacenter().getId());
        
        // Host
        final var hostCol = getTable().addColumn("Host", ID,getIDFormat());
        idColumnList.add(hostCol);
        addColDataFunction(hostCol, cloudlet -> cloudlet.getVm().getHost().getId());
        
        // Host PEs
        final var hostPEsCol = getTable().addColumn("Host PEs ", CPU_CORES,getPEFormat());
        peColumnList.add(hostPEsCol);
        addColDataFunction(hostPEsCol, cloudlet -> cloudlet.getVm().getHost().getWorkingPesNumber());
        
        // VM
        final var vmCol = getTable().addColumn("VM", ID,getIDFormat());
        idColumnList.add(vmCol);
        addColDataFunction(vmCol, cloudlet -> cloudlet.getVm().getId());
        
        // VM PEs
        final var vmPEsCol = getTable().addColumn("VM PEs   ", CPU_CORES,getPEFormat()); // 3 extra spaces to ensure proper formatting
        peColumnList.add(vmPEsCol);
        addColDataFunction(vmPEsCol, cloudlet -> cloudlet.getVm().getNumberOfPes());
        
        // CloudletLen
        final var cloudletLenCol = getTable().addColumn("CloudletLen", "MI",getLengthFormat());
        lengthColumnList.add(cloudletLenCol);
        addColDataFunction(cloudletLenCol, Cloudlet::getLength);
        
        // FinishedLen
        final var finishedLenCol = getTable().addColumn("FinishedLen", "MI",getLengthFormat());
        lengthColumnList.add(finishedLenCol);
        addColDataFunction(finishedLenCol, Cloudlet::getFinishedLengthSoFar);
        
        // CloudletPEs
        final var cloudletPEsCol = getTable().addColumn("CloudletPEs", CPU_CORES,getPEFormat());
        lengthColumnList.add(cloudletPEsCol);
        addColDataFunction(cloudletPEsCol, Cloudlet::getNumberOfPes);

        // StartTime
        final var startTimeCol = getTable().addColumn("StartTime", SECONDS,getTimeFormat());
        timeColumnList.add(startTimeCol);
        addColDataFunction(startTimeCol, Cloudlet::getExecStartTime);

        // FinishTime
        final var finishTimeCol = getTable().addColumn("FinishTime", SECONDS,getTimeFormat());
        timeColumnList.add(finishTimeCol);
        addColDataFunction(finishTimeCol, cl -> cl.getFinishTime());

        // ExecTime
        final var execTimeCol = getTable().addColumn("ExecTime", SECONDS,getTimeFormat());
        timeColumnList.add(execTimeCol);
        addColDataFunction(execTimeCol, cl ->  cl.getActualCpuTime());
    }
    
    public CloudletsTableBuilder setTimeFormat(String format) {
    	this.timeFormat = format;
    	return this;
    }
    
    public CloudletsTableBuilder setLengthFormat(String format) {
    	this.lengthFormat = format;
    	return this;
    }
    
    public CloudletsTableBuilder setPEFormat(String format) {
    	this.peFormat = format;
    	return this;
    }
    
    public CloudletsTableBuilder setIDFormat(String format) {
    	this.idFormat = format;
    	return this;
    }
    
    public String getTimeFormat() {
    	return this.timeFormat;
    }
    
    public String getLengthFormat() {
    	return this.lengthFormat;
    }
    
    public String getPEFormat() {
    	return this.peFormat;
    }
    
    public String getIDFormat() {
    	return this.idFormat;
    }

}
