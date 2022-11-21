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

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;

/**
 * Builds a table for printing {@link HostStateHistoryEntry} entries from the
 * {@link Host#getStateHistory()}.
 * It defines a set of default columns but new ones can be added
 * dynamically using the {@code newColumn()} methods.
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

    @Override
    protected void createTableColumns() {
        TableColumn col = getTable().newColumn("Time ").setFormat("%5.0f");
        mapColDataFunction(col, HostStateHistoryEntry::time);

        final String format = "%9.0f";
        col = getTable().newColumn("Requested").setFormat(format);
        mapColDataFunction(col, HostStateHistoryEntry::requestedMips);

        col = getTable().newColumn("Allocated").setFormat(format);
        mapColDataFunction(col, HostStateHistoryEntry::allocatedMips);

        col = getTable().newColumn("Used").setFormat("%3.0f%%");
        mapColDataFunction(col, history -> history.percentUsage()*100);

        mapColDataFunction(getTable().newColumn("Host Active"), HostStateHistoryEntry::active);

        col = getTable().newColumn("Host Total MIPS").setFormat(format);
        mapColDataFunction(col, history -> host.getTotalMipsCapacity());

        col = getTable().newColumn("Host Total Usage").setFormat("%5.1f%%");
        mapColDataFunction(col, history -> history.allocatedMips()/host.getTotalMipsCapacity()*100);
    }
}
