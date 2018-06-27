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
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example of "Bag of Tasks" application that is compounded by
 * 3 {@link NetworkCloudlet}, where 2 of them send data to the first created one,
 * that waits the data to be received.
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
        final int NETCLOUDLETS_FOR_EACH_APP = 3;
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(NETCLOUDLETS_FOR_EACH_APP+1);
        List<NetworkVm> selectedVms = randomlySelectVmsForApp(broker, NETCLOUDLETS_FOR_EACH_APP+1);
        //basically, each task runs the simulation and then data is consolidated in one task

        int taskStageId=0;
        int currentCloudletId = -1;
        for(int i = 0; i < NETCLOUDLETS_FOR_EACH_APP; i++){
            currentCloudletId++;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            NetworkCloudlet cloudlet =
                    new NetworkCloudlet(
                            currentCloudletId,
                        NETWORK_CLOUDLET_LENGTH,
                            NETCLOUDLET_PES_NUMBER);
            cloudlet
                    .setMemory(CLOUDLET_TASK_MEMORY)
                    .setFileSize(NETCLOUDLET_FILE_SIZE)
                    .setOutputSize(NETCLOUDLET_OUTPUT_SIZE)
                    .setUtilizationModel(utilizationModel)
                    .setVm(selectedVms.get(i));

            cloudlet.addTask(createExecutionTask(taskStageId++));

            //NetworkCloudlet 0 waits data from other cloudlets, while the other cloudlets send data
            if (i==0){
                for(int j=1; j < NETCLOUDLETS_FOR_EACH_APP; j++) {
                    CloudletReceiveTask task = createReceiveTask(taskStageId++, selectedVms.get(j+1));
                    cloudlet.addTask(task);
                }
            } else {
                CloudletSendTask task = createSendTask(taskStageId++);
                cloudlet.addTask(task);
                task.addPacket(networkCloudletList.get(0), 1000);
                task.addPacket(networkCloudletList.get(0), 2000);
            }

            networkCloudletList.add(cloudlet);
        }

        return networkCloudletList;
    }

    private CloudletSendTask createSendTask(final int taskId) {
        CloudletSendTask task = new CloudletSendTask(taskId);
        task.setMemory(CLOUDLET_TASK_MEMORY);
        return task;
    }

    private CloudletReceiveTask createReceiveTask(final int taskId, final Vm vm) {
        CloudletReceiveTask task = new CloudletReceiveTask(taskId, vm);
        task.setMemory(CLOUDLET_TASK_MEMORY);
        return task;
    }

    private CloudletTask createExecutionTask(final int taskId) {
        final CloudletTask task = new CloudletExecutionTask(taskId, NETWORK_CLOUDLET_LENGTH);
        task.setMemory(CLOUDLET_TASK_MEMORY);
        return task;
    }

}
