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
package org.cloudsimplus.builders.tables.vmstatistics;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudsimplus.builders.tables.CsvTable;
import org.cloudsimplus.builders.tables.TableBuilderAbstract;
import org.cloudsimplus.traces.azure.VmTypeRecord;

/**
 * A class for creating histograms about the VM types, i.e., {@link VmTypeRecord}.
 * It extends {@link TableBuilderAbstract}. The histograms are printed in the form of csv table.
 * <br><br>
 * The format of the csv table is as follows:
 * <pre>
 * 
 *   Count, Percentage, Cores, RAM (MiB), BW (Mbps), Storage (MiB)
 *     X,        X,       X,       X,         X,          X
 *     .         .        .        .          .           .
 *     .         .        .        .          .           .
 *     .         .        .        .          .           .
 * </pre>
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 */
public class VmTypeRecordsTableBuilder extends TableBuilderAbstract<Map.Entry<String, Long>> {
	
	/**
	 * The total number of VM types in the histogram
	 */
	final private long totalNumOfVmTypes;
	
	/**
	 * Takes as input a list with the histogram data in the form of {@link Entry Entries}
	 * and a {@link PrintStream} which is used to print them. 
	 * 
	 * @param vmTypes the list with the data for the histogram
	 * @param out the {@link PrintStream} to print the statistics
	 */
	public VmTypeRecordsTableBuilder (final List<Map.Entry<String, Long>> vmTypes, final PrintStream out) {

		super(vmTypes);
		
		CsvTable csvTable = new CsvTable();
		csvTable.setPrintStream(out);
		csvTable.setColumnSeparator(",");
		this.setTable(csvTable);
		
		this.totalNumOfVmTypes = vmTypes.stream().mapToLong(i -> i.getValue()).sum();
		
		if(vmTypes.isEmpty())
			return;
		
		this.createColumns();
						
		final Comparator<Map.Entry<String, Long>> comparator = Entry.<String, Long>comparingByValue().reversed();	
		
		vmTypes.sort(comparator);

		
		this.setObjectList(vmTypes);
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
	private void createColumns () {
		addColumn(getTable().newColumn("Count"),         vmType -> vmType.getValue());
		addColumn(getTable().newColumn("Percentage"),    vmType -> this.calculatePercentage(vmType) );
		addColumn(getTable().newColumn("Cores"),         vmType -> vmType.getKey().split(",", -1)[0]);
        addColumn(getTable().newColumn("RAM (MiB)"),     vmType -> vmType.getKey().split(",", -1)[1]);
        addColumn(getTable().newColumn("BW (Mbps)"),     vmType -> vmType.getKey().split(",", -1)[2]);
        addColumn(getTable().newColumn("Storage (MiB)"), vmType -> vmType.getKey().split(",", -1)[3]);
	}
	
	/**
	 * Calculates the percentage of a specific histogram {@link Entry}
	 * 
	 * @param vmType the entry for which to calculate the percentage
	 * @return the calculated percentage
	 */
	private double calculatePercentage(final Map.Entry<String, Long> vmType) {
		
		return ((double) Math.round((double) vmType.getValue() / (double) this.totalNumOfVmTypes * 100d * 100d)) / 100d;
	}
}
