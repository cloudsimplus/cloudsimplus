package org.cloudbus.cloudsim.examples.network.applications;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.network.NetworkVm;

import java.util.Arrays;
import java.util.List;

/**
 * An example of a Workflow Distributed Application that is compounded by
 * 3 {@link NetworkCloudlet}, each one having different stages
 * such as sending, receiving or processing data.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleWorkflowApp extends NetworkVmExampleAbstract {
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
            System.out.printf(
                "Created NetworkCloudlet %d for Application %d\n",
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
