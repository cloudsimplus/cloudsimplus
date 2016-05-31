package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.Arrays;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkVm;
import org.cloudbus.cloudsim.network.datacenter.TaskStage;
import org.cloudbus.cloudsim.network.datacenter.TaskStage.Stage;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example of a Workflow {@link AppCloudlet}'s that are composed of 
 * 3 {@link NetworkCloudlet}, each one having different stages
 * such as sending, receiving or processing data.
 * 
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleWorkflowAppCloudlet extends NetworkVmsExampleAppCloudletAbstract {
    public NetworkVmsExampleWorkflowAppCloudlet(){
        super();
    }

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExampleWorkflowAppCloudlet();
    }
    
    /**
     * Create a list of NetworkCloudlets that together represent
     * the sub-applications of a Workflow AppCloudlet.
     *
     * @param appCloudlet
     * @param selectedVms List of VMs where the NetworkCloudlets will be executed
     * @return the list of created NetworkCloudlets
     */
    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(
            AppCloudlet appCloudlet, List<NetworkVm> selectedVms) {
        NetworkCloudlet networkCloudletList[] = new NetworkCloudlet[3];
        for(int i = 0; i < networkCloudletList.length; i++){         
            networkCloudletList[i] = createNetworkCloudlet(i, appCloudlet, selectedVms.get(i));
        }

        //Task A (id 0)
        addExecutionTask(networkCloudletList[0], selectedVms.get(0));
        addSendOrReceiveTask(networkCloudletList[0], Stage.WAIT_SEND, networkCloudletList[2]);

        //Task B (id 1)
        addExecutionTask(networkCloudletList[1], selectedVms.get(1));
        addSendOrReceiveTask(networkCloudletList[1], Stage.WAIT_SEND,  networkCloudletList[2]);
        
        //Task C (id 2)
        addSendOrReceiveTask(networkCloudletList[2], Stage.WAIT_RECV, networkCloudletList[0]);
        addSendOrReceiveTask(networkCloudletList[2], Stage.WAIT_RECV, networkCloudletList[1]);
        addExecutionTask(networkCloudletList[2], selectedVms.get(0));

        return Arrays.asList(networkCloudletList);
    }

    /**
     * Adds an send or receive task to list of tasks of the given {@link NetworkCloudlet}.
     * 
     * @param sourceNetCloudlet the {@link NetworkCloudlet} to add the task
     * @param stage The stage to set to the created task
     * @param destinationNetCloudlet the destination where to send or from which is 
     * expected to receive data
     */
    private void addSendOrReceiveTask(
            NetworkCloudlet sourceNetCloudlet, TaskStage.Stage stage,
            NetworkCloudlet destinationNetCloudlet) {        
        TaskStage task = new TaskStage(
                sourceNetCloudlet.getStages().size(), stage, 1000, 0,  NETCLOUDLET_RAM,
                sourceNetCloudlet.getVmId(), destinationNetCloudlet.getId());
        sourceNetCloudlet.getStages().add(task);
    }

    /**
     * Adds an execution task to list of tasks of the given {@link NetworkCloudlet}.
     * 
     * @param netCloudlet the {@link NetworkCloudlet} to add the task
     * @param vm the VM where to send or from which to receive data
     */
    private static void addExecutionTask(NetworkCloudlet netCloudlet, NetworkVm vm) {
        TaskStage stage = new TaskStage(
                netCloudlet.getStages().size(), 
                TaskStage.Stage.EXECUTION, 0, 1000 * 0.8, NETCLOUDLET_RAM,
                vm.getId(), netCloudlet.getId());
        netCloudlet.getStages().add(stage);
    }

    /**
     * Creates a {@link NetworkCloudlet} for the given {@link AppCloudlet}.
     * 
     * @param networkCloudletId the id of the {@link NetworkCloudlet} to be created
     * @param appCloudlet the {@link AppCloudlet} that will own the created {@link NetworkCloudlet)
     * @param vm the VM that will run the created {@link NetworkCloudlet)
     * @return 
     */
    private NetworkCloudlet createNetworkCloudlet(int networkCloudletId, AppCloudlet appCloudlet, NetworkVm vm) {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        NetworkCloudlet netCloudlet = new NetworkCloudlet(
                networkCloudletId, 0, 1, 
                NETCLOUDLET_FILE_SIZE, NETCLOUDLET_OUTPUT_SIZE, NETCLOUDLET_RAM,
                utilizationModel, utilizationModel, utilizationModel);
        netCloudlet.setAppCloudlet(appCloudlet);
        netCloudlet.setNumberOfStages(2);
        netCloudlet.setUserId(getBroker().getId());
        netCloudlet.submittime = CloudSim.clock();
        netCloudlet.setVmId(vm.getId());
        return netCloudlet;
    }

   

}
