package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.TaskStage;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example of a Bag of Tasks {@link AppCloudlet}'s that are composed of 
 * 3 {@link NetworkCloudlet}, where 2 of them send data to the first created one,
 * that waits to data be received.
 * 
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleBagOfTasksAppCloudlet extends NetworkVmsExampleAppCloudletAbstract {

    public NetworkVmsExampleBagOfTasksAppCloudlet(){
        super();
    }

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExampleBagOfTasksAppCloudlet();
    }
    
    /**
     * Creates a list of NetworkCloudlets that together represents the distributed
     * processes of a Bag of Tasks AppCloudlet.
     *
     * @param app
     * @param vmList List of VMs where the NetworkCloudlets will be executed
     * @return the list of created NetworkCloudlets
     */
    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app, List<Vm> vmList){
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(NUMBER_OF_NETCLOUDLET_FOR_EACH_APPCLOUDLET);
        //basically, each task runs the simulation and then data is consolidated in one task
        long memory = 1000;
        long networkCloudletLength = 10000;
        int taskStageId=0;
        int currentCloudletId = -1;
        int t=currentCloudletId;
        for(int i = 0; i < NUMBER_OF_NETCLOUDLET_FOR_EACH_APPCLOUDLET; i++){
            currentCloudletId++;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            NetworkCloudlet netCloudlet = 
                    new NetworkCloudlet(
                            currentCloudletId, 
                            networkCloudletLength, 
                            NETCLOUDLET_PES_NUMBER, 
                            NETCLOUDLET_FILE_SIZE, 
                            NETCLOUDLET_OUTPUT_SIZE, 
                            memory, utilizationModel, utilizationModel, utilizationModel);
            netCloudlet.setAppCloudlet(app);
            netCloudlet.setUserId(getBroker().getId());
            netCloudlet.submittime = CloudSim.clock();
            netCloudlet.setVmId(vmList.get(i).getId());
            //compute and send data to node 0
            netCloudlet.getStages().add(new TaskStage(taskStageId++, TaskStage.Stage.EXECUTION, 
                            NETCLOUDLET_TASK_COMMUNICATION_LENGTH,
                            networkCloudletLength,  memory, 
                            vmList.get(0).getId(),netCloudlet.getId()));

            //0 has an extra stage of waiting for results; others send
            if (i==0){
                for(int j=1; j < NUMBER_OF_NETCLOUDLET_FOR_EACH_APPCLOUDLET; j++) {
                    netCloudlet.getStages().add(new TaskStage(taskStageId++, TaskStage.Stage.WAIT_RECV, 
                                NETCLOUDLET_TASK_COMMUNICATION_LENGTH, 0, 
                                memory, vmList.get(j).getId(), netCloudlet.getId()+j));
                }
            } else {
                netCloudlet.getStages().add(new TaskStage(taskStageId++, TaskStage.Stage.WAIT_SEND, 
                        NETCLOUDLET_TASK_COMMUNICATION_LENGTH, 0, 
                        memory, vmList.get(0).getId(), t));
            }

            networkCloudletList.add(netCloudlet);    			
        }
        
        return networkCloudletList;
    }


}
