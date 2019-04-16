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
     */
    Resource getBw();

    /**
     * Gets the machine memory resource in Megabytes.
     *
     * @return the machine memory
     */
    Resource getRam();

    /**
     * Gets the storage device of the machine with capacity in Megabytes.
     *
     * @return the machine storage device
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


    /**
     * Checks if the Machine has been idle for a given amount of time (in seconds).
     * @param time the time interval to check if the Machine has been idle (in seconds).
     *             If time is zero, it will be checked if the Machine is currently idle.
     *             If it's negative, even if the Machine is idle, it's considered
     *             that it isn't idle enough. This is useful if you don't want to perform
     *             any operation when the machine becomes idle (for instance,
     *             if idle machines might be shut down and a negative value is given,
     *             they won't).
     * @return true if the Machine has been idle as long as the given time,
     *         false if it's active of isn't idle long enough
     */
    default boolean isIdleEnough(final double time) {
        if(time < 0) {
            return false;
        }

        return getIdleInterval() >= time;
    }

    /**
     * Gets the interval interval the Machine has been idle.
     * @return the idle time interval (in seconds) or 0 if the Machine is not idle
     */
    default double getIdleInterval() {
        return getSimulation().clock() - getLastBusyTime();
    }

    /**
     * Gets the last time the Machine was running some process.
     * @return the last busy time (in seconds)
     */
    double getLastBusyTime();

    /**
     * Checks if the Machine is currently idle.
     * @return true if the Machine currently idle, false otherwise
     */
    default boolean isIdle(){
        return getIdleInterval() > 0;
    }

    /**
     * Validates a capacity for a machine resource.
     * @param capacity the capacity to check
     */
    static void validateCapacity(final double capacity){
        if(capacity <= 0){
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
    }


}
