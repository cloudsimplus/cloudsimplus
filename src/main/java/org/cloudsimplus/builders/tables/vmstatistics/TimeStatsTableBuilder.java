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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CsvTable;
import org.cloudsimplus.builders.tables.TableBuilderAbstract;

/**
 * A class for calculating the Min, Avg, Max, Variance, and Stddev statistics on a list of 
 * values. It extends {@link TableBuilderAbstract}. The statistics are printed in the 
 * form of csv table. The values can be timestamps or duration of certain types of events, 
 * e.g., the {@link Vm} arrival times or the {@link Vm} lifetimes.
 * <br><br>
 * The format of the csv table is as follows:
 * 
 * <pre>   
 * Unit,    Min, Avg, Max, Variance, Stddev
 * Days,     X,   X,   X,      X,       X
 * Hours,    X,   X,   X,      X,       X
 * Minutes,  X,   X,   X,      X,       X
 * Seconds,  X,   X,   X,      X,       X
 * </pre>
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 */
public class TimeStatsTableBuilder extends TableBuilderAbstract<Long> {
		
	/**
	 * the minimum value
	 */
	private long   min = 0;
	
	/**
	 * the mean value
	 */
	private double mean = 0;
	
	/**
	 * the maximum value
	 */
	private long   max = 0;
	
	/**
	 * the variance 
	 */
	private double variance = 0;
	
	/**
	 * the standard deviation
	 */
	private double stddev = 0;
	
	/**
	 * Creates four rows for the supported time units which are (a) days, (b) hours, 
	 * (c) minutes, and (d) seconds.
	 * 
	 * @param timesSec the list with the values to calculate the statistics 
	 * @param out a {@link PrintStream} to print the statistics
	 */
	public TimeStatsTableBuilder (final List<Long> timesSec, final PrintStream out) {

		super(timesSec);
		
		final CsvTable csvTable = new CsvTable();
		csvTable.setPrintStream(out);
		csvTable.setColumnSeparator(",");
		this.setTable(csvTable);
		
		if(timesSec.isEmpty())
			return;		
		
		this.createColumns();
		
		this.calculateStats(timesSec);
		
		List<Long> units = new ArrayList<Long>();
		
		// Hack to print data in different units
		units.add( (long) 1 ); // days
		units.add( (long) 2 ); // hours
		units.add( (long) 3 ); // minutes
		units.add( (long) 4 ); // seconds
		
		this.setObjectList(units);
	}
	
    /**
     * {@inheritDoc}} 
     */
	@Override
	protected void createTableColumns() {
        
	}
	
	/**
	 * creates the columns with the supported statistics 
	 */
	private void createColumns() {
		addColumn(getTable().newColumn("Unit"),     i -> this.label(i) );
		addColumn(getTable().newColumn("Min"),      i -> this.scaleToUnit(this.min, i) );
		addColumn(getTable().newColumn("Avg"),      i -> this.scaleToUnit(this.mean, i) );
        addColumn(getTable().newColumn("Max"),      i -> this.scaleToUnit(this.max, i) );
        addColumn(getTable().newColumn("Variance"), i -> this.scaleToUnit(this.variance, i) );
        addColumn(getTable().newColumn("Stddev"),   i -> this.scaleToUnit(this.stddev, i) );
	}
	
	/**
	 * calculates the min, mean, max, variance and stddev statistics
	 * 
	 * @param timeStats the list with the values to calculate the statistics
	 */
	private void calculateStats(final List<Long> timeStats) {
			
			this.min = 
					timeStats.stream()
					.min(Comparator.comparing(Long::valueOf))
					.get();
			
			this.mean = 
					 timeStats.stream()
					.mapToDouble(Long::valueOf)
					.average().getAsDouble();
			
			this.max = 
					timeStats
					.stream()
					.max(Comparator.comparing(Long::valueOf))
					.get();
			
			this.variance = 
					 timeStats.stream()
					.mapToDouble(i -> (double) i - this.mean)
					.map(i -> i * i)
					.map(i -> i).average().getAsDouble();
			
			this.stddev = Math.sqrt(this.variance);
	}
	
	
	/**
	 * Prints the label for the corresponding time unit depending on the input parameter
	 * 
	 * @param unit 1 for days, 2 for hours, 3 for minutes and 4 for seconds
	 * @return the corresponding label 
	 */
	private String label (final long unit) {
		
		if (unit == 1)
			return "Days";
		else if (unit == 2)
			return "Hours";
		else if (unit == 3)
			return "Minutes";
		else
			return "Seconds";
	}	
	
	/**
	 * Takes as input a value in seconds and converts it to a different unit.
	 * 
	 * @param timeSec the value in seconds
	 * @param unit 1 for days, 2 for hours, 3 for minutes and 4 for seconds
	 */
	private double scaleToUnit (final double timeSec, final long unit) {
		
		if (unit == 1)
			return (double) Math.round(timeSec / 86_400d * 100d) / 100d; // Days
		else if (unit == 2)
			return (double) Math.round(timeSec /  3_600d * 100d) / 100d; // Hours
		else if (unit == 3)
			return (double) Math.round(timeSec /     60d * 100d) / 100d; // Minutes
		else
			return (double) Math.round(timeSec);                         // Seconds
	}	
}
