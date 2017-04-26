package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.Resourceful;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Represents either a: (i) Physical Machine (PM) which implements the interface {@link Host};
 * or (ii) Virtual Machine (VM), which implements the interface {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public interface Machine extends ChangeableId, Resourceful {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link Machine}
     * objects.
     */
    Machine NULL = new MachineNull();

    /**
     * Gets the machine bandwidth (bw) capacity in Megabits/s.
     *
     * @return the machine bw capacity
     * @pre $none
     * @post $result > 0
     */
    Resource getBw();

    /**
     * Gets the machine memory resource in Megabytes.
     *
     * @return the machine memory
     * @pre $none
     * @post $result > 0
     */
    Resource getRam();

    /**
     * Gets the storage device of the machine with capacity in Megabytes.
     *
     * @return the machine storage device
     * @pre $none
     * @post $result >= 0
     */
    Resource getStorage();

    /**
     * Gets the overall number of {@link Pe}s the machine has,
     * that include PEs of all statuses, including failed PEs.
     *
     * @return the machine's number of PEs
     */
    long getNumberOfPes();

    /**
     * Gets the individual MIPS capacity of any machine's {@link Pe}, considering that all
     * PEs have the same capacity.
     *
     * @return the MIPS capacity of a single {@link Pe}
     */
    double getMips();

    /**
     * Gets total MIPS capacity of all PEs of the machine.
     *
     * @return the total MIPS of all PEs
     */
    double getTotalMipsCapacity();

    /**
     * Gets the CloudSim instance that represents the simulation the Entity is related to.
     * @return
     */
    Simulation getSimulation();
}
