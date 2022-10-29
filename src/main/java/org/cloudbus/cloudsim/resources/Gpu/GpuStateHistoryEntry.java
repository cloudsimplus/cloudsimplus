package org.cloudbus.cloudsim.gp.resources;


public class GpuStateHistoryEntry {
	

	private double time;
	private double allocatedMips;
	private double requestedMips; 
	private boolean active;
	public GpuStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean active) {
		this.time = time;
		this.allocatedMips = allocatedMips;
		this.requestedMips = requestedMips; 
		this.active = active;
	}
	
	public double time () {
		return time;
	}

	public double allocatedMips () {
		return allocatedMips;
	}

	public double requestedMips () {
		return requestedMips;
	}

	public double percentUsage () {
		return requestedMips > 0 ? allocatedMips / requestedMips : 0;
	}

	public boolean active () {
		return active;
	}

	@Override
	public String toString () {
		final String msg = "Time: %6.1f | Requested: %10.0f MIPS | Allocated: %10.0f MIPS | Used: %3.0f%% Gpu Active: %s%n";
		return String.format(msg, time, requestedMips, allocatedMips, percentUsage() * 100, active);
	}
}

