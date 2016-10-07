package org.cloudbus.cloudsim.brokers;

import java.util.stream.Collectors;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;
import org.cloudsimplus.heuristics.Heuristic;

/**
 * <p>A simple implementation of {@link DatacenterBroker} that uses some heuristic
 * to get a suboptimal mapping among submitted cloudlets and Vm's.
 * Such heuristic can be, for instance, the {@link org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing}
 * that implements a Simulated Annealing algorithm.
 * The Broker then places the submitted Vm's at the first datacenter found.
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
     * @param name name to be associated with this entity
     * @throws IllegalArgumentException when the entity name is invalid
     * @pre name != null
     * @post $none
     * @see #setHeuristic(CloudletToVmMappingHeuristic)
     */
    public DatacenterBrokerHeuristic(String name) {
        super(name);
        heuristic = CloudletToVmMappingHeuristic.NULL;
    }

    @Override
    protected void requestDatacentersToCreateWaitingCloudlets() {
        setupAndStartHeuristic();
        super.requestDatacentersToCreateWaitingCloudlets();
    }

    /**
     * Setup the heuristic parameters that the Broker are in charge of
     * and start the heuristic to find a suboptimal mapping for Cloudlets and Vm's.
     */
    private void setupAndStartHeuristic() {
        heuristic.setVmList(getVmsCreatedList());
        heuristic.setCloudletList(
	        getCloudletsWaitingList().stream()
                        .filter(c-> !c.isBoundedToVm())
                        .collect(Collectors.toList()));
        /*
        Starts the heuristic to get a sub-optimal solution
        for the Cloudlets to Vm's mapping.
        Depending on the heuristic parameters, it may take a while
        to get a solution.
        */
        Log.printFormattedLine(
                "\n# Broker %d started the heuristic to get a suboptimal solution for mapping Cloudlets to Vm's running %d neighborhood searches by iteration",
                getId(), heuristic.getNumberOfNeighborhoodSearchesByIteration());
        Log.printLine("Please wait... It may take a while, depending on heuristic parameters and number of Cloudlets and Vm's.");
	    CloudletToVmMappingSolution solution = heuristic.solve();
        Log.printFormattedLine(
                "# Broker %d finished the solution find for mapping Cloudlets to Vm's in %.2f seconds with a solution cost of %.2f\n",
                getId(), heuristic.getSolveTime(), solution.getCost());
    }

    @Override
    public Vm selectVmForWaitingCloudlet(Cloudlet cloudlet) {
        if (cloudlet.isBoundedToVm()) {
            // submit to the specific vm
            return VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
        }

        /*
         * Defines a fallback vm in the case the heuristic solution
         * didn't assign a Vm to the given cloudlet.
         */
        Vm fallbackVm = VmList.getById(getVmsCreatedList(), getNextVmIndex());

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
     */
    public void setHeuristic(CloudletToVmMappingHeuristic heuristic) {
        this.heuristic = heuristic;
    }

}
