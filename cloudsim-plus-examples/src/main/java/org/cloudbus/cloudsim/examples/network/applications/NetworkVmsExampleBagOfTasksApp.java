package org.cloudbus.cloudsim.examples.network.applications;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.CloudletSendTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletReceiveTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletTask;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example of "Bag of Tasks" application that is compounded by
 * 3 {@link NetworkCloudlet}, where 2 of them send data to the first created one,
 * that waits to data be received.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleBagOfTasksApp extends NetworkVmExampleAbstract {

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
     * @TODO @author manoelcampos It isn't adding packets to send.
     * See {@link CloudletSendTask#addPacket(Cloudlet, long)}
     * @param broker the broker that the cloudlets will belong to
     * @return
     */
    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker){
        final int NETCLOUDLETS_FOR_EACH_APP = 3;
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(NETCLOUDLETS_FOR_EACH_APP+1);
        List<NetworkVm> selectedVms = randomlySelectVmsForApp(broker, NETCLOUDLETS_FOR_EACH_APP+1);
        //basically, each task runs the simulation and then data is consolidated in one task
        long memory = 1000;
        long networkCloudletLength = 10000;
        int taskStageId=0;
        int currentCloudletId = -1;
        for(int i = 0; i < NETCLOUDLETS_FOR_EACH_APP; i++){
            currentCloudletId++;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            NetworkCloudlet cloudlet =
                    new NetworkCloudlet(
                            currentCloudletId,
                            networkCloudletLength,
                            NETCLOUDLET_PES_NUMBER);
            cloudlet
                    .setMemory(memory)
                    .setFileSize(NETCLOUDLET_FILE_SIZE)
                    .setOutputSize(NETCLOUDLET_OUTPUT_SIZE)
                    .setUtilizationModel(utilizationModel)
                    .setVm(selectedVms.get(i));

            //compute and send data to node 0
            CloudletTask task;
            task = new CloudletExecutionTask(taskStageId++, networkCloudletLength);
            task.setMemory(memory);
            cloudlet.addTask(task);

            //NetworkCloudlet 0 wait data from other cloudlets, while the other cloudlets send data
            if (i==0){
                for(int j=1; j < NETCLOUDLETS_FOR_EACH_APP; j++) {
                    task = new CloudletReceiveTask(taskStageId++, selectedVms.get(j+1));
                    task.setMemory(memory);
                    cloudlet.addTask(task);
                }
            } else {
                task = new CloudletSendTask(taskStageId++);
                task.setMemory(memory);
                cloudlet.addTask(task);
            }

            networkCloudletList.add(cloudlet);
        }

        return networkCloudletList;
    }

}
