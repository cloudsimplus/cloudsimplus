package org.cloudbus.cloudsim.examples.network.applications;

import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.cloudlets.network.CloudletSendTask;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.cloudlets.network.CloudletTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletReceiveTask;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example of a "Workflow Application" that is compounded by
 * 3 {@link NetworkCloudlet}, each one having different stages
 * such as sending, receiving or processing data.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 *
 * @todo @author manoelcampos The example isn't working yet.
 * It freezes after the cloudlets creation.
 * Maybe the problem is in the NetworkCloudletSpaceSharedScheduler class.
 */
public class NetworkVmsExampleWorkflowApp extends NetworkVmExampleAbstract {
    private static final long PACKET_DATA_LENGTH_IN_BYTES = 1000;
    private static final long NUMBER_OF_PACKETS_TO_SEND = 100;
    private int currentNetworkCloudletId = -1;

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExampleWorkflowApp();
    }

    public NetworkVmsExampleWorkflowApp(){
        super();
    }

    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker) {
        NetworkCloudlet networkCloudletList[] = new NetworkCloudlet[3];
        List<NetworkVm> selectedVms =
                randomlySelectVmsForApp(broker, networkCloudletList.length);

        for(int i = 0; i < networkCloudletList.length; i++){
            networkCloudletList[i] =
                    createNetworkCloudlet(selectedVms.get(i), broker);
            Log.printFormattedLine(
                "Created NetworkCloudlet %d for Application %d",
                networkCloudletList[i].getId(), broker.getId());
        }

        //NetworkCloudlet 0 Tasks
        addExecutionTask(networkCloudletList[0]);
        addSendTask(networkCloudletList[0], networkCloudletList[2]);

        //NetworkCloudlet 1 Tasks
        addExecutionTask(networkCloudletList[1]);
        addSendTask(networkCloudletList[1], networkCloudletList[2]);

        //NetworkCloudlet 2 Tasks
        addReceiveTask(networkCloudletList[2], networkCloudletList[0]);
        addReceiveTask(networkCloudletList[2], networkCloudletList[1]);
        addExecutionTask(networkCloudletList[2]);

        return Arrays.asList(networkCloudletList);
    }

    /**
     * Adds a send task to list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param sourceCloudlet the {@link NetworkCloudlet} to add the task to
     * @param destinationCloudlet the destination where to send or from which is
     * expected to receive data
     */
    private void addSendTask(
            NetworkCloudlet sourceCloudlet,
            NetworkCloudlet destinationCloudlet) {

        CloudletSendTask task = new CloudletSendTask(sourceCloudlet.getTasks().size());
        task.setMemory(NETCLOUDLET_RAM);
        sourceCloudlet.addTask(task);
        for(int i = 0; i < NUMBER_OF_PACKETS_TO_SEND; i++) {
            task.addPacket(destinationCloudlet, PACKET_DATA_LENGTH_IN_BYTES);
        }
    }

    /**
     * Adds a receive task to list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} that the task will belong to
     * @param sourceCloudlet the cloudlet where it is expected to receive packets from
     */
    private void addReceiveTask(NetworkCloudlet cloudlet, NetworkCloudlet sourceCloudlet) {
        CloudletReceiveTask task = new CloudletReceiveTask(
                cloudlet.getTasks().size(), sourceCloudlet.getVm());
        task.setMemory(NETCLOUDLET_RAM);
        task.setNumberOfExpectedPacketsToReceive(NUMBER_OF_PACKETS_TO_SEND);
        cloudlet.addTask(task);
    }

    /**
     * Adds an execution task to list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param netCloudlet the {@link NetworkCloudlet} to add the task
     */
    private static void addExecutionTask(NetworkCloudlet netCloudlet) {
        /**
         * @todo @author manoelcampos It's strange to define the time of the execution task.
         * It would be defined the length instead. In this case, the execution time will
         * depend on the MIPS of the PE where the task is being executed.
         */
        CloudletTask task = new CloudletExecutionTask(
                netCloudlet.getTasks().size(), NETCLOUDLET_EXECUTION_TASK_LENGTH);
        task.setMemory(NETCLOUDLET_RAM);
        netCloudlet.addTask(task);
    }

    /**
     * Creates a {@link NetworkCloudlet}.
     *
     * @param vm the VM that will run the created {@link NetworkCloudlet)
     * @param broker the broker that will own the create NetworkCloudlet
     * @return
     */
    private NetworkCloudlet createNetworkCloudlet(NetworkVm vm, DatacenterBroker broker) {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        NetworkCloudlet cloudlet = new NetworkCloudlet(++currentNetworkCloudletId, 1, NETCLOUDLET_PES_NUMBER);
        cloudlet
                .setMemory(NETCLOUDLET_RAM)
                .setFileSize(NETCLOUDLET_FILE_SIZE)
                .setOutputSize(NETCLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel)
                .setVm(vm);

        return cloudlet;
    }



}
