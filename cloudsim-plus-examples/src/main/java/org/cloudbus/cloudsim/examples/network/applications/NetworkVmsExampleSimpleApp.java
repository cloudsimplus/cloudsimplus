package org.cloudbus.cloudsim.examples.network.applications;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletTask;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

import java.util.ArrayList;
import java.util.List;

/**
 * An example of a simple fictitious distributed application that is compounded by
 * 3 {@link NetworkCloudlet}s with 3 {@link CloudletExecutionTask}s each one.
 *
 * <b>The cloudlets just process data, not performing network communication</b>,
 * running in a similar way of a regular {@link Cloudlet}.
 * The difference is that the processing is split among the {@link NetworkCloudlet}'s {@link CloudletTask tasks},
 * simulating a distributed application.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleSimpleApp extends NetworkVmExampleAbstract {
    private static final int NETCLOUDLETS_FOR_EACH_APP = 3;
    private static final int NUMBER_OF_NETCLOUDLET_EXECUTION_TASKS = 3;

    private int currentNetworkCloudletId = -1;

    public NetworkVmsExampleSimpleApp(){
        super();
    }

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExampleSimpleApp();
    }

    /**
     * {@inheritDoc}
     * A {@link NetworkCloudlet} will be created and assigned for each VM in the given VM list.
     *
     * @param broker {@inheritDoc }
     * @return {@inheritDoc }
     */
    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker) {
        final List<NetworkCloudlet> networkCloudletList = new ArrayList<>(NETCLOUDLETS_FOR_EACH_APP);
        final long networkCloudletLengthMI = 1;
        final long fileSize = 300;
        final long outputSize = 300;
        final long memory = 256;
        final int pesNumber = 4;
        for (int i = 0; i < NETCLOUDLETS_FOR_EACH_APP; i++) {
            NetworkCloudlet cloudlet = new NetworkCloudlet(
                    ++currentNetworkCloudletId, networkCloudletLengthMI, pesNumber);
            cloudlet.setMemory(memory)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModel(new UtilizationModelFull());

            createNetworkCloudletExecutionTasks(cloudlet);
            networkCloudletList.add(cloudlet);
        }

        return networkCloudletList;
    }

    /**
     * Creates a list of {@link CloudletExecutionTask}'s for a given {@link NetworkCloudlet}.
     *
     * @param netCloudlet the {@link NetworkCloudlet} that the tasks will be added to
     */
    private void createNetworkCloudletExecutionTasks(NetworkCloudlet netCloudlet) {
        CloudletTask task;
        final long taskLengthMI = 15000;
        for(int i = 0; i < NUMBER_OF_NETCLOUDLET_EXECUTION_TASKS; i++){
            task = new CloudletExecutionTask(i, taskLengthMI);
            task.setMemory(netCloudlet.getMemory());
            netCloudlet.addTask(task);
        }
    }

}
