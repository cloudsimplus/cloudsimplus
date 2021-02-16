package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;

/**
 * A class that stores information about the suitability of
 * a {@link Host} for placing a {@link Vm}.
 * It provides fine-grained information to indicates if the Host is suitable in storage, ram,
 * bandwidth and number of PEs required by the given Vm.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.2
 */
public final class HostSuitability {
    private final Vm vm;
    private boolean forStorage;
    private boolean forRam;
    private boolean forBw;
    private boolean forPes;

    HostSuitability(final Vm vm){
        this.vm = Objects.requireNonNull(vm);
    }

    /** Checks if the Host has storage suitability for the size of the VM. */
    public boolean forStorage() {
        return forStorage;
    }

    /** Sets if the Host has disk suitability for storing the VM.
     * @param forStorage true to indicate it's suitable according to VM's size requirements, false otherwise
     * */
    HostSuitability setForStorage(final boolean forStorage) {
        this.forStorage = forStorage;
        return this;
    }

    /** Checks if the Host has RAM suitability for running the VM. */
    public boolean forRam() {
        return forRam;
    }

    /** Sets if the Host has RAM suitability for running the VM.
     * @param forRam true to indicate it's suitable according to VM's RAM requirements, false otherwise
     * */
    HostSuitability setForRam(final boolean forRam) {
        this.forRam = forRam;
        return this;
    }

    /** Checks if the Host has bandwidth suitability for running the VM. */
    public boolean forBw() {
        return forBw;
    }

    /** Sets if the Host has bandwidth suitability for running the VM.
     * @param forBw true to indicate it's suitable according to VM's BW requirements, false otherwise
     * */
    HostSuitability setForBw(final boolean forBw) {
        this.forBw = forBw;
        return this;
    }

    /** Checks if the Host has {@link Pe} suitability for running the VM. */
    public boolean forPes() {
        return forPes;
    }

    /** Sets if the Host has {@link Pe} suitability for running the VM.
     * @param forPes true to indicate it's suitable according to VM's number of PEs requirements, false otherwise
     * */
    HostSuitability setForPes(final boolean forPes) {
        this.forPes = forPes;
        return this;
    }

    /**
     * Checks if the Host is totally suitable or not for the given Vm
     * in terms of required storage, ram, bandwidth and number of PEs.
     * @return
     */
    public boolean fully(){
        return forStorage && forRam && forBw && forPes;
    }
}
