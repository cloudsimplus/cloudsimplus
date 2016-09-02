package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;
import static org.cloudbus.cloudsim.Cloudlet.NOT_ASSIGNED;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * A base class for {@link Cloudlet} implementations.
 * 
 * @author Manoel Campos da Silva Filho
 */
public abstract class AbstractCloudlet implements Cloudlet {
    /**
     * The list of every {@link Datacenter} where the cloudlet has been executed. In case
     * it starts and finishes executing in a single datacenter, without
     * being migrated, this list will have only one item.
     */
    private final List<ExecutionInDatacenterInfo> executionInDatacenterInfoList;
    
    /**
     * The index of the last Datacenter where the cloudlet was executed. If the
     * cloudlet is migrated during its execution, this index is updated. The
     * value {@link #NOT_ASSIGNED} indicates the cloudlet has not been executed yet.
     */
    private int lastExecutedDatacenterIndex;

    protected AbstractCloudlet(){
	// Normally, a Cloudlet is only executed on a resource without being
        // migrated to others. Hence, to reduce memory consumption, set the
        // size of this ArrayList to be less than the default one.
        executionInDatacenterInfoList = new ArrayList<>(2);
        lastExecutedDatacenterIndex = NOT_ASSIGNED;
    }
    
    @Override
    public double registerArrivalOfCloudletIntoDatacenter() {
        if (!isAssignedToDatacenter()) {
            return NOT_ASSIGNED;
        }

        final ExecutionInDatacenterInfo dcInfo = executionInDatacenterInfoList.get(lastExecutedDatacenterIndex);
        dcInfo.arrivalTime = CloudSim.clock();

        return dcInfo.arrivalTime;
    }
    
    @Override
    public boolean isAssignedToDatacenter() {
        return getLastExecutedDatacenterIndex() > NOT_ASSIGNED;
    }    

    protected int getLastExecutedDatacenterIndex() {
        return lastExecutedDatacenterIndex;
    }

    protected void setLastExecutedDatacenterIndex(int lastExecutedDatacenterIndex) {
        this.lastExecutedDatacenterIndex = lastExecutedDatacenterIndex;
    }
    
    protected List<ExecutionInDatacenterInfo> getExecutionInDatacenterInfoList() {
        return executionInDatacenterInfoList;
    }
    
    /**
     * Internal class that keeps track of Cloudlet's movement in different
     * {@link Datacenter Datacenters}. Each time a cloudlet is run on a given Datacenter, the cloudlet's
     * execution history on each Datacenter is registered at {@link Cloudlet#executionInDatacenterInfoList}
     */
    protected static class ExecutionInDatacenterInfo {
        /**
         * Cloudlet's submission (arrival) time to a Datacenter.
         */
        public double arrivalTime = 0.0;

        /**
         * The time this Cloudlet resides in a Datacenter (from arrival time
         * until departure time, that may include waiting time).
         */
        public double wallClockTime = 0.0;

        /**
         * The total time the Cloudlet spent being executed in a Datacenter.
         */
        public double actualCPUTime = 0.0;

        /**
         * Cost per second a Datacenter charge to execute this Cloudlet.
         */
        public double costPerSec = 0.0;

        /**
         * Cloudlet's length finished so far (in MI).
         */
        public long finishedSoFar = 0;

        /**
         * a Datacenter id.
         */
        public int datacenterId = NOT_ASSIGNED;

        /**
         * The Datacenter name.
         */
        public String name = "";
    }    
}
