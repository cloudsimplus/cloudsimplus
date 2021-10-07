package org.cloudbus.cloudsim.examples.network.applications;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.network.NetworkVm;

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
    private static final long TASK_MEMORY = 1000; // in Megabytes
    private static final long CLOUDLET_LENGTH = 1;

    /**
     * Starts the execution of the example.
     * @param args
     */
    public static void main(String[] args) {
        new NetworkVmsExampleBagOfTasksApp();
    }

    private NetworkVmsExampleBagOfTasksApp(){
        super();
    }

    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker){
        final int CLOUDLETS_BY_APP = 2;
        final List<NetworkCloudlet> cloudletList = new ArrayList<>(CLOUDLETS_BY_APP+1);
        //basically, each task runs the simulation and then data is consolidated in one task

        for(int i = 0; i < CLOUDLETS_BY_APP; i++){
            final UtilizationModel utilizationModel = new UtilizationModelFull();
            final var cloudlet = new NetworkCloudlet(i, CLOUDLET_LENGTH, CLOUDLET_PES);
            final NetworkVm vm = getVmList().get(i);
            cloudlet
                    .setMemory(TASK_MEMORY)
                    .setFileSize(CLOUDLET_FILE_SIZE)
                    .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                    .setUtilizationModel(utilizationModel)
                    .setVm(vm)
                    .setBroker(vm.getBroker());
            cloudletList.add(cloudlet);
        }

        createTasksForNetworkCloudlets(cloudletList);

        return cloudletList;
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
            else addSendTask(cloudlet, networkCloudletList.get(0));
        }
    }
}
