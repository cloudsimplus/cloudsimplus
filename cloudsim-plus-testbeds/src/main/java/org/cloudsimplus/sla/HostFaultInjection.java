/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * This class shows how to generate a fault. In this case the fault is in the
 * host.
 *
 * Notes: This class does not work with the injection fault in time 0. Only
 * works after the creation of cloudlets and VMs, because when destroying the
 * VM, the cloudlet is not returned to be chosen by another VM that did not
 * fail, thus causing error in the simulator.
 *
 * @author raysaoliveira
 *
 */
public class HostFaultInjection extends SimEntity {

    private Host host;
    private ContinuousDistribution numberOfFailedPesRandom;
    private boolean failed;
    private ContinuousDistribution delayForFailureOfHostRandom;

    /**
     * Creates a fault injection mechanism for a host that will generate
     * failures with a delay and number of failed PEs generated using a Uniform
     * Pseudo Random Number Generator (PRNG) for each one.
     *
     * @param name name of the fault injection
     * @see
     * #setDelayForFailureOfHostRandom(org.cloudbus.cloudsim.distributions.ContinuousDistribution)
     * @see
     * #setNumberOfFailedPesRandom(org.cloudbus.cloudsim.distributions.ContinuousDistribution)
     */
    public HostFaultInjection(String name) {
        super(name);
        this.numberOfFailedPesRandom = new UniformDistr();
        this.delayForFailureOfHostRandom = new UniformDistr();
        this.failed = false;
    }

    @Override
    public void startEntity() {
        double delay = delayForFailureOfHostRandom.sample() + 1;
        Log.printLine(getName() + " is starting...");
        schedule(getId(), 2, CloudSimTags.HOST_FAILURE);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.HOST_FAILURE:
                generateFailure();
                break;

            default:
                Log.printLine(getName() + ": unknown event type");
                break;
        }
    }

    @Override
    public void shutdownEntity() {
        Log.printLine(getName() + ": is shutting down...");
    }

    /**
     * Generates a failure on Host' PEs or not,
     * according to the number of PEs to be set to failed,
     * returned by the {@link #numberOfFailedPesRandom} PRNG.
     *
     * @return <tt>true</tt> if the failure was generated, <tt>false</tt> otherwise
     */
    public final boolean generateFailure() {
        int numberOfFailedPes = generateNumberOfFailedPes();

        this.failed = numberOfFailedPes > 0;
        for (int i = 0; i < numberOfFailedPes; i++) {
            host.getPeList().get(i).setStatus(Pe.Status.FAILED);
            Log.printLine(CloudSim.clock() + " ---> Host " + id + " FAILURE...\n");
        }
        
        if (numberOfFailedPes == host.getPeList().size()) {
            setVmsToFailedWhenHostIsFailed();
        }
        
        return this.failed;
    }

    /**
     * Generates a number of PEs that will fail for the host using the
     * {@link #numberOfFailedPesRandom},
     *
     * @return the generated number of failed PEs for the host
     */
    public int generateNumberOfFailedPes() {
        /*the random generator return values from [0 to 1]
         and multiplying by the number of PEs we get a number between
         0 and numbero of PEs*/
        return (int) (numberOfFailedPesRandom.sample() * host.getPeList().size() + 1);
    }

    /**
     * Checks if the the host is failed and sets all its Vm' to failed.
     */
    public void setVmsToFailedWhenHostIsFailed() {
        if (!this.isFailed()) {
            return;
        }

        for (Vm vm : host.getVmList()) {
            vm.setFailed(true);
            /*
             As the broker is expected to request vm creation and destruction,
             it is set here as the sender of the vm destroy request.
             */
            CloudSim.sendNow(
                    vm.getUserId(), host.getDatacenter().getId(),
                    CloudSimTags.VM_DESTROY, vm);
        }
    }

    /**
     * @return the failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Gets the host in which a failure may happen.
     *
     * @return
     */
    public Host getHost() {
        return host;
    }

    /**
     * Sets the host in which failure may happen.
     *
     * @param host the host to set
     */
    public void setHost(Host host) {
        this.host = host;
    }

    /**
     * Gets the pseudo random number generator (PRNG) 
     * that is used to define the number of PEs to be
     * set to failed for the related host. The PRNG returns
     * values between [0 and 1] 
     *
     * @return
     */
    public ContinuousDistribution getNumberOfFailedPesRandom() {
        return numberOfFailedPesRandom;
    }

    /**
     * Sets the pseudo random number generator (PRNG) 
     * that is used to define the number of PEs to be
     * set to failed for the related host. The PRNG must return
     * values between [0 and 1]
     *
     * @param numberOfFailedPesRandom the numberOfFailedPesRandom to set
     */
    public void setNumberOfFailedPesRandom(ContinuousDistribution numberOfFailedPesRandom) {
        this.numberOfFailedPesRandom = numberOfFailedPesRandom;
    }

    /**
     * Sets the pseudo delayForFailureOfHostRandom number generator returning
     * values between [min - max] setted in the
     * class{@link ExampleUsingFaultInjector}. This value will be used to sets
     * the delay time the host will fail.
     *
     * @param delayForFailureOfHostRandom the delayForFailureOfHostRandom to set
     */
    public void setDelayForFailureOfHostRandom(ContinuousDistribution delayForFailureOfHostRandom) {
        this.delayForFailureOfHostRandom = delayForFailureOfHostRandom;
    }

    /**
     * Gets the pseudo delayForFailureOfHostRandom number generator returning
     * values between [min - max] setted in the
     * class{@link ExampleUsingFaultInjector}. This value will be used to sets
     * the delay time the host will fail.
     *
     * @return the delayForFailureOfHostRandom
     */
    public ContinuousDistribution getDelayForFailureOfHostRandom() {
        return delayForFailureOfHostRandom;
    }

}
