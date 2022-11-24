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

import java.util.List;

import static java.util.Objects.requireNonNull;

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
    public static final String DEF_FORMAT = "%d";

    private static final String SECONDS = "Seconds";
    private static final String CPU_CORES = "CPU cores";
    private static final String ID = "ID";
    private static final String MI = "MI";

    /** @see #setTimeFormat(String) */
    private String timeFormat = "%.1f";

    /** @see #setIdFormat(String) */
    private String idFormat = DEF_FORMAT;
    /** @see #setLengthFormat(String) */
    private String lengthFormat = DEF_FORMAT;
    /** @see #setPeFormat(String) */
    private String peFormat = DEF_FORMAT;

    /**
     * Instantiates a builder to print the list of Cloudlets using the
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

    @Override
    protected void createTableColumns() {
        addColumn(getTable().newColumn("Cloudlet", ID), Identifiable::getId);

        // 1 extra space to ensure proper formatting
        addColumn(getTable().newColumn(" Status") , cloudlet -> cloudlet.getStatus().name());

        addColumn(getTable().newColumn("DC", ID, idFormat), cloudlet -> cloudlet.getVm().getHost().getDatacenter().getId());

        addColumn(getTable().newColumn("Host", ID, idFormat), cloudlet -> cloudlet.getVm().getHost().getId());
        addColumn(getTable().newColumn("Host PEs ", CPU_CORES, peFormat), cloudlet -> cloudlet.getVm().getHost().getWorkingPesNumber());

        addColumn(getTable().newColumn("VM", ID, idFormat), cloudlet -> cloudlet.getVm().getId());

        // 3 extra spaces to ensure proper formatting
        addColumn(getTable().newColumn("   VM PEs", CPU_CORES, peFormat), cloudlet -> cloudlet.getVm().getNumberOfPes());
        addColumn(getTable().newColumn("CloudletLen", MI, lengthFormat), Cloudlet::getLength);
        addColumn(getTable().newColumn("FinishedLen", MI, lengthFormat), Cloudlet::getFinishedLengthSoFar);
        addColumn(getTable().newColumn("CloudletPEs", CPU_CORES, peFormat), Cloudlet::getNumberOfPes);
        addColumn(getTable().newColumn("StartTime", SECONDS, timeFormat), Cloudlet::getExecStartTime);
        addColumn(getTable().newColumn("FinishTime", SECONDS, timeFormat), Cloudlet::getFinishTime);
        addColumn(getTable().newColumn("ExecTime", SECONDS, timeFormat), Cloudlet::getActualCpuTime);
    }

    /**
     * Gets the format for time columns.
     */
    public String getTimeFormat() {
        return timeFormat;
    }

    /**
     * Sets the format for time columns.
     */
    public CloudletsTableBuilder setTimeFormat(final String timeFormat) {
        this.timeFormat = requireNonNull(timeFormat);
        return this;
    }

    /**
     * Gets the format for cloudlet length columns.
     */
    public String getLengthFormat() {
        return lengthFormat;
    }

    /**
     * Sets the format for cloudlet length columns.
     */
    public CloudletsTableBuilder setLengthFormat(final String lengthFormat) {
        this.lengthFormat = requireNonNull(lengthFormat);
        return this;
    }

    /**
     * Gets the format for ID columns.
     */
    public String getIdFormat() {
        return idFormat;
    }

    /**
     * Sets the format for ID columns.
     */
    public CloudletsTableBuilder setIdFormat(final String idFormat) {
        this.idFormat = requireNonNull(idFormat);
        return this;
    }

    /**
     * Gets the format for columns indicating number of PEs.
     */
    public String getPeFormat() {
        return peFormat;
    }

    /**
     * Sets the format for columns indicating number of PEs.
     */
    public CloudletsTableBuilder setPeFormat(final String peFormat) {
        this.peFormat = requireNonNull(peFormat);
        return this;
    }
}
