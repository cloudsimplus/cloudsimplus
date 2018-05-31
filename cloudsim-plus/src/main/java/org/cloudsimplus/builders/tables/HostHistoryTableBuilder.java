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

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;

/**
 * Builds a table for printing {@link HostStateHistoryEntry} entries from the
 * {@link Host#getStateHistory()}.
 * It defines a set of default columns but new ones can be added
 * dynamically using the {@code addColumn()} methods.
 *
 * <p>The basic usage of the class is by calling its constructor,
 * giving a Host to print its history, and then
 * calling the {@link #build()} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.3.2
 */
public class HostHistoryTableBuilder extends TableBuilderAbstract<HostStateHistoryEntry>{
    private final Host host;

    /**
     * Instantiates a builder to print the history of a Host using the a
     * default {@link TextTable}.
     * To use a different {@link Table}, check the alternative constructors.
     *
     * @param host the Host to get the history to print
     */
    public HostHistoryTableBuilder(final Host host) {
        super(host.getStateHistory());
        this.host = host;
    }

    /**
     * Instantiates a builder to print the history of a Host using the a
     * given {@link Table}.
     *
     * @param host the Host to get the history to print
     * @param table the {@link Table} used to build the table with the Cloudlets data
     */
    public HostHistoryTableBuilder(final Host host, final Table table) {
        this(host);
        this.setTable(table);
    }

    @Override
    protected void createTableColumns() {
        TableColumn col = getTable().addColumn("Time ").setFormat("%5.0f");
        addColumnDataFunction(col, HostStateHistoryEntry::getTime);

        col = getTable().addColumn("Requested").setFormat("%9.0f");
        addColumnDataFunction(col, HostStateHistoryEntry::getRequestedMips);

        col = getTable().addColumn("Allocated").setFormat("%9.0f");
        addColumnDataFunction(col, HostStateHistoryEntry::getAllocatedMips);

        col = getTable().addColumn("Used").setFormat("%3.0f%%");
        addColumnDataFunction(col, history -> history.getPercentUsage()*100);

        addColumnDataFunction(getTable().addColumn("Host Active"), HostStateHistoryEntry::isActive);

        col = getTable().addColumn("Host Total MIPS").setFormat("%9.0f");
        addColumnDataFunction(col, history -> host.getTotalMipsCapacity());

        col = getTable().addColumn("Host Total Usage").setFormat("%5.1f%%");
        addColumnDataFunction(col, history -> history.getAllocatedMips()/host.getTotalMipsCapacity()*100);
    }
}
