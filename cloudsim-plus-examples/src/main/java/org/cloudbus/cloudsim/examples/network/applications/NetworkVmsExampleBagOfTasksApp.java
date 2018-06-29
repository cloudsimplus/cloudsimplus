package org.cloudbus.cloudsim.examples.network.applications;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

import java.util.ArrayList;
import java.util.List;

/**
 * An example of "Bag of Tasks" distributed application that is compounded by
 * 3 {@link NetworkCloudlet}, where 2 of them send data to the first created one,
 * which waits the data to be received.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleBagOfTasksApp extends NetworkVmExampleAbstract {
    private static final long CLOUDLET_TASK_MEMORY = 1000;
    private static final long NETWORK_CLOUDLET_LENGTH = 1;

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExampleBagOfTasksApp();
    }

    public NetworkVmsExampleBagOfTasksApp(){
        super();
    }

    /**
     * @param broker the broker that the cloudlets will belong to
     * @return
     */
    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker){
        final int NETCLOUDLETS_FOR_EACH_APP = 2;
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(NETCLOUDLETS_FOR_EACH_APP+1);
        //basically, each task runs the simulation and then data is consolidated in one task

        for(int i = 0; i < NETCLOUDLETS_FOR_EACH_APP; i++){
            UtilizationModel utilizationModel = new UtilizationModelFull();
            NetworkCloudlet cloudlet = new NetworkCloudlet(i, NETWORK_CLOUDLET_LENGTH, NETCLOUDLET_PES_NUMBER);
            cloudlet
                    .setMemory(CLOUDLET_TASK_MEMORY)
                    .setFileSize(NETCLOUDLET_FILE_SIZE)
                    .setOutputSize(NETCLOUDLET_OUTPUT_SIZE)
                    .setUtilizationModel(utilizationModel)
                    .setVm(getVmList().get(i));
            networkCloudletList.add(cloudlet);
        }

        createTasksForNetworkCloudlets(networkCloudletList);

        return networkCloudletList;
    }

    private void createTasksForNetworkCloudlets(final List<NetworkCloudlet> networkCloudletList) {
        for (NetworkCloudlet cloudlet : networkCloudletList) {
            addExecutionTask(cloudlet);

            //NetworkCloudlet 0 waits data from other Cloudlets
            if (cloudlet.getId()==0){
                /*
                If there are a total of N Cloudlets, since the first one receives packets
                from all the other ones, this for creates the tasks for the first Cloudlet
                to wait packets from N-1 other Cloudlets.
                 */
                for(int j=1; j < networkCloudletList.size(); j++) {
                    addReceiveTask(cloudlet, networkCloudletList.get(j));
                }
            }
            //The other NetworkCloudlets send data to the first one
            else {
                addSendTask(cloudlet, networkCloudletList.get(0));
            }
        }
    }
}
