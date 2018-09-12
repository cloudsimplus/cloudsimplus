package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;
import org.cloudsimplus.heuristics.Heuristic;

import java.util.stream.Collectors;

/**
 * <p>A simple implementation of {@link DatacenterBroker} that uses some heuristic
 * to get a suboptimal mapping among submitted cloudlets and Vm's.
 * Such heuristic can be, for instance, the {@link org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing}
 * that implements a Simulated Annealing algorithm.
 * The Broker then places the submitted Vm's at the first Datacenter found.
 * If there isn't capacity in that one, it will try the other ones.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBrokerHeuristic extends DatacenterBrokerSimple {
    /**
     * @see #getHeuristic()
     */
    private CloudletToVmMappingHeuristic heuristic;

    /**
     * Creates a new DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @post $none
     * @see #setHeuristic(CloudletToVmMappingHeuristic)
     */
    public DatacenterBrokerHeuristic(final CloudSim simulation) {
        super(simulation);
        setVmMapper(this::defaultVmMapper);
        heuristic = CloudletToVmMappingHeuristic.NULL;
    }

    @Override
    protected void requestDatacentersToCreateWaitingCloudlets() {
        setupAndStartHeuristic();
        super.requestDatacentersToCreateWaitingCloudlets();
    }

    /**
     * Setups the heuristic parameters and starts the heuristic to find a suboptimal mapping for Cloudlets and Vm's.
     */
    private void setupAndStartHeuristic() {
        heuristic.setVmList(getVmExecList());
        heuristic.setCloudletList(
	        getCloudletWaitingList().stream()
                        .filter(cloudlet -> !cloudlet.isBindToVm())
                        .collect(Collectors.toList()));
        /*
        Starts the heuristic to get a sub-optimal solution
        for the Cloudlets to Vm's mapping.
        Depending on the heuristic parameters, it may take a while
        to get a solution.
        */
        LOGGER.info(
                "{} started the heuristic to get a suboptimal solution for mapping Cloudlets to Vm's running {} neighborhood searches by iteration.{}{}",
                this, heuristic.getNeighborhoodSearchesByIteration(),
                System.lineSeparator(),
                "Please wait... It may take a while, depending on heuristic parameters and number of Cloudlets and Vm's.");

	    final CloudletToVmMappingSolution solution = heuristic.solve();
        LOGGER.info(
                "{} finished the solution find for mapping Cloudlets to Vm's in {} seconds with a solution cost of {}",
                this, heuristic.getSolveTime(), solution.getCost());
    }

    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        /*
         * Defines a fallback vm in the case the heuristic solution
         * didn't assign a Vm to the given cloudlet.
         */
        final Vm fallbackVm = super.defaultVmMapper(cloudlet);

        //If user didn't bind this cloudlet and it has not been executed yet,
        //gets the Vm for the Cloudlet from the heuristic solution.
        return heuristic.getBestSolutionSoFar().getResult().getOrDefault(cloudlet, fallbackVm);
    }

    /**
     *
     * @return the heuristic used to find a sub-optimal mapping between
     * Cloudlets and Vm's
     */
    public Heuristic<CloudletToVmMappingSolution> getHeuristic() {
        return heuristic;
    }

    /**
     * <p>Sets a heuristic to be used to find a sub-optimal mapping between
     * Cloudlets and Vm's. <b>The list of Cloudlets and Vm's to be used by the heuristic
     * will be set automatically by the DatacenterBroker. Accordingly,
     * the developer don't have to set these lists manually,
     * once they will be overridden.</b></p>
     *
     * <p>The time taken to find a suboptimal mapping of Cloudlets to Vm's
     * depends on the heuristic parameters that have to be set carefully.</p>
     *
     * @param heuristic the heuristic to be set
     * @return the DatacenterBrokerHeuristic instance
     */
    public DatacenterBrokerHeuristic setHeuristic(final CloudletToVmMappingHeuristic heuristic) {
        this.heuristic = heuristic;
        return this;
    }

}
