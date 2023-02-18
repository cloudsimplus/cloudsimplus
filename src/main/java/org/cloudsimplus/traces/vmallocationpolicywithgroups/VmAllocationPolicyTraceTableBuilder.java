/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2023 IBM Research.
 *     Author: Pavlos Maniotis
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
package org.cloudsimplus.traces.vmallocationpolicywithgroups;

import java.io.PrintStream;
import java.util.List;

import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyRequestStatus;
import org.cloudsimplus.builders.tables.CsvTable;
import org.cloudsimplus.builders.tables.TableBuilderAbstract;
import org.cloudsimplus.traces.azure.TracesStatisticsManager;

/**
 * A class for creating trace files with {@link VmAllocationPolicyTraceRecord}.
 * It extends {@link TableBuilderAbstract}. The trace files are printed in the 
 * form of csv table.
 * 
 * @see VmAllocationPolicyTraceRecord
 * @see TracesStatisticsManager
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 */
public class VmAllocationPolicyTraceTableBuilder extends TableBuilderAbstract<VmAllocationPolicyTraceRecord> {

	/**
	 * Takes as input a list with the {@link VmAllocationPolicyTraceRecord}s 
	 * and a {@link PrintStream} which is used to print them. 
	 * 
	 * @param records the list with the records for the trace file
	 * @param out a {@link PrintStream} to print the statistics
	 */
	public VmAllocationPolicyTraceTableBuilder (final List<VmAllocationPolicyTraceRecord> records, final PrintStream out) {
		
		super(records);
		
		final CsvTable csvTable = new CsvTable();
		csvTable.setPrintStream(out);
		csvTable.setColumnSeparator(",");
		this.setTable(csvTable);
		
		if(records.isEmpty())
			return;

		this.createColumns();
		
		this.setObjectList(records);
	}
	
    /**
     * {@inheritDoc}} 
     */
	@Override
	protected void createTableColumns() {		

	}
	
	/**
	 * creates the columns for the recorded data 
	 */
	private void createColumns() {
		addColumn(getTable().newColumn("Id"),                       record -> record.getId());
		addColumn(getTable().newColumn("Type"),                     record -> record.getRequestType());
		addColumn(getTable().newColumn("Status"),                   record -> record.getRequestStatus());
        addColumn(getTable().newColumn("VMs"),                      record -> record.getNumOfVms());
		addColumn(getTable().newColumn("Arrival Time (days)"),      record -> (double) record.getArrivalTime() / 3600d / 24d);
		addColumn(getTable().newColumn("Departure Time (days)"),    record -> (double) record.getDepartureTime() / 3600d / 24d);
		addColumn(getTable().newColumn("Arrival Time (sec)"),       record -> record.getArrivalTime());
		addColumn(getTable().newColumn("Departure Time (sec)"),     record -> record.getDepartureTime());
		addColumn(getTable().newColumn("Requested Lifetime (sec)"), record -> record.getRequestedLifetime());
		addColumn(getTable().newColumn("Simulated Lifetime (sec)"), record -> record.getRequestStatus() == VmAllocationPolicyRequestStatus.SUCCESS ? record.getDepartureTime() - record.getArrivalTime() : -1);
		addColumn(getTable().newColumn("Lifetime difference (%)"),  record -> record.getRequestStatus() == VmAllocationPolicyRequestStatus.SUCCESS ? this.calculatePercentageDifference(record.getRequestedLifetime(), record.getDepartureTime() - record.getArrivalTime()) : -1);
		addColumn(getTable().newColumn("Total Cores"),              record -> record.getNumOfCores() * record.getNumOfVms());
		addColumn(getTable().newColumn("Cores/Vm"),                 record -> record.getNumOfCores());
		addColumn(getTable().newColumn("Ram/Vm (MiB)"),             record -> record.getRamMiB());
		addColumn(getTable().newColumn("Bw/Vm (Mbps)"),             record -> record.getBwMbps());
		addColumn(getTable().newColumn("Storage/Vm (MiB)"),         record -> record.getStorageMiB());
        addColumn(getTable().newColumn("Ideal Switches"),           record -> record.getIdealNumOfSwitches());
        addColumn(getTable().newColumn("Switches"),                 record -> record.getNumOfSwitches());
        addColumn(getTable().newColumn("Ideal Hosts"),              record -> record.getIdealNumOfHosts());
        addColumn(getTable().newColumn("Hosts"),                    record -> record.getNumOfHosts());
        addColumn(getTable().newColumn("Scope"),                    record -> record.getScope());
        addColumn(getTable().newColumn("Affinity type"),            record -> record.getAffinityType());
        addColumn(getTable().newColumn("Enforcement"),              record -> record.getEnforcement());
	}
	
	/**
	 * Calculates the percentage difference between two values
	 */
	private double calculatePercentageDifference (double initialValue, double finalValue) {
		
		return 100 * ((finalValue - initialValue) / Math.abs(initialValue));
	}
}
