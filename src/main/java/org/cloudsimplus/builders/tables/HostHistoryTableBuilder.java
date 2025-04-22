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

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostStateHistoryEntry;

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
    private static final String MIPS = "MIPS";
    private final Host host;

    /**
     * Instantiates a builder to print the history of a Host using the default {@link TextTable}.
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
        final var col1 = getTable().newColumn("Time ", "Secs", "%5.0f");
        addColumn(col1, HostStateHistoryEntry::time);

        final String format = "%9.0f";
        final var col2 = getTable().newColumn("Total Requested", MIPS, format);
        addColumn(col2, HostStateHistoryEntry::requestedMips);

        final var col3 = getTable().newColumn("Total Allocated", MIPS, format);
        addColumn(col3, HostStateHistoryEntry::allocatedMips);

        final var col4 = getTable().newColumn("Used ", "", "%3.0f%%");
        addColumn(col4, history -> history.percentUsage()*100);

        addColumn(getTable().newColumn("Host Active"), HostStateHistoryEntry::active);

        final var col5 = getTable().newColumn("Host Total MIPS", "", format);
        addColumn(col5, history -> host.getTotalMipsCapacity());

        final var col6 = getTable().newColumn("Host Total Usage", "", "%5.1f%%");
        addColumn(col6, history -> history.allocatedMips()/host.getTotalMipsCapacity()*100);
    }
}
