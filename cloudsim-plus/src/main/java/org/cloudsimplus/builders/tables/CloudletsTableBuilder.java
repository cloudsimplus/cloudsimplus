/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
    private static final String TIME_FORMAT = "%d";
    private static final String SECONDS = "Seconds";
    private static final String CPU_CORES = "CPU cores";

    /**
     * Instantiates a builder to print the list of Cloudlets using the a
     * default {@link TextTable}.
     * To use a different {@link Table}, check the alternative constructors.
     *
     * @param list the list of Cloudlets to print
     */
    public CloudletsTableBuilder(final List<? extends Cloudlet> list) {
        super(list);
    }

    /**
     * Instantiates a builder to print the list of Cloudlets using the a
     * given {@link Table}.
     *
     * @param list the list of Cloudlets to print
     * @param table the {@link Table} used to build the table with the Cloudlets data
     */
    public CloudletsTableBuilder(final List<? extends Cloudlet> list, final Table table) {
        super(list, table);
    }

    @Override
    protected void createTableColumns() {
        final String ID = "ID";
        addColumnDataFunction(getTable().addColumn("Cloudlet", ID), Identifiable::getId);
        addColumnDataFunction(getTable().addColumn("Status "), cloudlet -> cloudlet.getStatus().name());
        addColumnDataFunction(getTable().addColumn("DC", ID), cloudlet -> cloudlet.getVm().getHost().getDatacenter().getId());
        addColumnDataFunction(getTable().addColumn("Host", ID), cloudlet -> cloudlet.getVm().getHost().getId());
        addColumnDataFunction(getTable().addColumn("Host PEs ", CPU_CORES), cloudlet -> cloudlet.getVm().getHost().getNumberOfWorkingPes());
        addColumnDataFunction(getTable().addColumn("VM", ID), cloudlet -> cloudlet.getVm().getId());
        addColumnDataFunction(getTable().addColumn("VM PEs   ", CPU_CORES), cloudlet -> cloudlet.getVm().getNumberOfPes());
        addColumnDataFunction(getTable().addColumn("CloudletLen", "MI"), Cloudlet::getLength);
        addColumnDataFunction(getTable().addColumn("CloudletPEs", CPU_CORES), Cloudlet::getNumberOfPes);

        TableColumn col = getTable().addColumn("StartTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, cloudlet -> (long)cloudlet.getExecStartTime());

        col = getTable().addColumn("FinishTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, cloudlet -> (long)cloudlet.getFinishTime());

        col = getTable().addColumn("ExecTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, cloudlet -> (long)Math.ceil(cloudlet.getActualCpuTime()));
    }
}
