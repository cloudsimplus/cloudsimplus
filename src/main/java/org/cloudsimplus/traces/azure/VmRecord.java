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
package org.cloudsimplus.traces.azure;

/**
 * Class representing a VM record. It follows the schema of the 
 * <i>AzureTracesForPacking2020</i> dataset presented in:
 * 
 * https://github.com/Azure/AzurePublicDataset/blob/master/AzureTracesForPacking2020.md
 * 
 * @see VmTypeRecord
 * @see TracesParser
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 */
public class VmRecord {

	/**
	 * A unique id
	 */
	final private long vmId;
	
	/**
	 * The tenand id associated with this VM
	 */
	final private long tenantId;
	
	/**
	 * The VM type id associated with this VM
	 */
	final private long typeId;
	
	/**
	 * The priority of this VM
	 */
	final private long priority;
	
	/**
	 * The start time of the VM
	 */
	final private long startTime;
	
	/**
	 * The end time of the VM
	 */
	final private long endTime;
	
	/**
	 * Constructor to initialize the record.
	 * Parameters are self explanatory. 
	 */
	public VmRecord(long vmId, long tenantId, long typeId,
				    long priority, long startTime, long endTime) {
		
		this.vmId      = vmId;
		this.tenantId  = tenantId;
		this.typeId    = typeId;
		this.priority  = priority;
		this.startTime = startTime;
		this.endTime   = endTime;	
	}


	/**
	 * Returns the unique id
	 */
	public long getVmId() {
		return this.vmId;
	}

	/**
	 * Returns the tenand id associated with this VM
	 */
	public long getTenantId() {
		return this.tenantId;
	}

	/**
	 * Returns the VM type id associated with this VM
	 */
	public long getTypeId() {
		return this.typeId;
	}

	/**
	 * Returns the priority of this VM
	 */
	public long getPriority() {
		return this.priority;
	}

	/**
	 * Returns the start time of the VM
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * Returns the end time of the VM
	 */
	public long getEndTime() {
		return this.endTime;
	}
}
