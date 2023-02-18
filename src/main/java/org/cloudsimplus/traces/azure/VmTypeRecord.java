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
 * Class representing a VM type record. It follows the schema of the 
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

public class VmTypeRecord {

	/**
	 * A unique id for the VM type record
	 */
	final private long id;
	
	/**
	 * The VM type id of the record
	 */
	final private long vmTypeId;
	
	/**
	 * The machine id associated to this VM type record
	 */
	final private long machineId;
	
	/**
	 * The core allocation ratio
	 */
	final private double core;
	
	/**
	 * The RAM allocation ratio
	 */
	final private double memory;
	
	/**
	 * The HDD allocation ratio
	 */
	final private double hdd;
	
	/**
	 * The SSD allocation ratio
	 */
	final private double ssd; 
	
	/**
	 * The bandwidth allocation ratio
	 */
	final private double nic;
	
	/**
	 * Constructor to initialize the record.
	 * Parameters are self explanatory.
	 */
	public VmTypeRecord(long id, long vmTypeId, long machineId, double core,
					    double memory, double hdd, double ssd, double nic) {
		
		this.id        = id;
		this.vmTypeId  = vmTypeId;
		this.machineId = machineId;
		this.core      = core;
		this.memory    = memory;
		this.hdd       = hdd;
		this.ssd       = ssd;
		this.nic       = nic;
	}

	/**
	 * Returns the unique id for the VM type record
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Returns the VM type id of the record
	 */
	public long getVmTypeId() {
		return this.vmTypeId;
	}

	/**
	 * Returns the machine id associated to this VM type record
	 */
	public long getMachineId() {
		return this.machineId;
	}

	/**
	 * Returns the core allocation ratio
	 */
	public double getCore() {
		return this.core;
	}

	/**
	 * Returns the RAM allocation ratio
	 */
	public double getMemory() {
		return this.memory;
	}

	/**
	 * Returns the HDD allocation ratio
	 */
	public double getHdd() {
		return this.hdd;
	}

	/**
	 * Returns the SSD allocation ratio
	 */
	public double getSsd() {
		return this.ssd;
	}

	/**
	 * Returns the bandwidth allocation ratio
	 */
	public double getNic() {
		return this.nic;
	}
}
