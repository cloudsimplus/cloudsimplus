package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.CloudletExecutionTask;
import org.cloudbus.cloudsim.network.datacenter.CloudletTask;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkVm;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example of a simple {@link AppCloudlet}'s that is composed of
 * 3 {@link NetworkCloudlet}'s of 2 {@link CloudletExecutionTask}'s each one.
 * The cloudlets just process data, not performing network communication, 
 * running in a similar way of a regular {@link org.cloudbus.cloudsim.Cloudlet}.
 * The difference is that the processing is splitted among the NetworkCloudlet's tasks.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleSimpleAppCloudlet extends NetworkVmsExampleAppCloudletAbstract {

    public NetworkVmsExampleSimpleAppCloudlet(){
        super();
    }

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExampleSimpleAppCloudlet();
    }

    /**
     * Creates a list of NetworkCloudlets that together represents the distributed
     * processes of a AppCloudlet.
     * A NetworkCloudlet will created and assigned for each VM in the given VM list.
     *
     * @param app
     * @return the list of created NetworkCloudlets
     */
    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app) {
        final int NETCLOUDLETS_FOR_EACH_APP = 3;
        List<NetworkVm> selectedVms = randomlySelectVmsForAppCloudlet(getBroker(), NETCLOUDLETS_FOR_EACH_APP);
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(NETCLOUDLETS_FOR_EACH_APP);
        int currentNetworkCloudletId = 0;
        for (Vm vm: selectedVms) {
            long networkCloudletLengthMI = 1;
            long fileSize = 300;
            long outputSize = 300;
            long memory = 256;
            long taskLengthMI = 15000;
            int pesNumber = 4;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            NetworkCloudlet netCloudlet = new NetworkCloudlet(
                    currentNetworkCloudletId,
                    networkCloudletLengthMI, pesNumber, fileSize, outputSize, memory,
                    utilizationModel, utilizationModel, utilizationModel);
            netCloudlet.setAppCloudlet(app);
            // setting the owner of these Cloudlets
            netCloudlet.setUserId(getBroker().getId());
            CloudletTask task;
            for(int i = 0; i < 2; i++){
                task = new CloudletExecutionTask(i, taskLengthMI);
                task.setMemory(memory);
                netCloudlet.addTask(task);
            }
            networkCloudletList.add(netCloudlet);
            currentNetworkCloudletId++;
        }

        return networkCloudletList;
    }

}
