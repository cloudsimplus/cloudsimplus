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
package org.cloudbus.cloudsim.resources.utilization;

import org.cloudsimplus.traces.azure.TracesStatisticsManager;

/**
 * A class for creating timestamped records of the resources utilization for the data center
 * as a whole. The records include the timestamp, the number of allocated cores, the allocated 
 * RAM, the allocated bandwidth, and the allocated amount of storage. 
 * See also {@link TracesStatisticsManager}. 
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 */
public class ResourcesUtilizationRecord {

	/**
	 * The timestamp of the record
	 */
	final private double timeStamp;
	
	/**
	 * The number of allocated cores (i.e., Pes) for the data center
	 */
	final private long allocatedCores;
	
	/**
	 * The amount of allocated RAM
	 */
	final private long allocatedRamMiB;

	/**
	 * The amount of allocated bandwidth
	 */
	final private long allocatedBwMbps;
	
	/**
	 * The amount of allocated storage
	 */
	final private long allocatedStorageMiB;
	
	/**
	 * Constructor to initialize the record.
	 * Input parameters are self explanatory 
	 */
	public ResourcesUtilizationRecord(double timeStamp, long allocatedCores,
			long allocatedRamMiB, long allocatedBwMbps, long allocatedStorageMiB) {
		
		this.timeStamp = timeStamp;
		this.allocatedCores = allocatedCores;
		this.allocatedRamMiB = allocatedRamMiB;
		this.allocatedBwMbps = allocatedBwMbps;
		this.allocatedStorageMiB = allocatedStorageMiB;
	}

	/**
	 * Returns the timestamp of the record
	 */
	public double getTimeStamp() {
		return timeStamp;
	}


	/**
	 * Returns the number of allocated cores of the record
	 */
	public long getAllocatedCores() {
		return allocatedCores;
	}

	/**
	 * Returns the allocated RAM of the record
	 */
	public long getAllocatedRamMiB() {
		return allocatedRamMiB;
	}

	/**
	 * Returns the allocated bandwidth of the record
	 */
	public long getAllocatedBwMbps() {
		return allocatedBwMbps;
	}

	/**
	 * Returns the allocated storage of the record
	 */
	public long getAllocatedStorageMiB() {
		return allocatedStorageMiB;
	}
}
