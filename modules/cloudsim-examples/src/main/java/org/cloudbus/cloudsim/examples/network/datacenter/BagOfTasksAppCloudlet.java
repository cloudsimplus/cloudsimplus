package org.cloudbus.cloudsim.examples.network.datacenter;

/**
 * BagOfTasksAppCloudlet is an example of AppCloudlet having three non-communicating tasks. 
 * 
 * <br/>Please refer to following publication for more details:<br/>
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud
 * Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud
 * Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 *
 * 
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 */

import java.util.List;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkConstants;
import org.cloudbus.cloudsim.network.datacenter.TaskStage;

public final class BagOfTasksAppCloudlet extends AppCloudlet {
    public static final int EXEC_TIME = 100;

    public BagOfTasksAppCloudlet(int type, int appID, double deadline, int userId) {
        super(type, appID, deadline, 0, userId);
        this.numberOfVMs=this.generateNumberOfVms();
        this.execTime=EXEC_TIME/this.numberOfVMs;
    }

    @Override
    public void createCloudletList(List<Integer> vmIdList){
        //basically, each task runs the simulation and then data is consolidated in one task
        long memory = 1000;
        long fileSize = NetworkConstants.FILE_SIZE;
        long outputSize = NetworkConstants.OUTPUT_SIZE;
        int pesNumber = NetworkConstants.PES_NUMBER;
        int taskStageId=0;
        int t=NetworkConstants.currentCloudletId;
        for(int i=0;i<numberOfVMs;i++){
            UtilizationModel utilizationModel = new UtilizationModelFull();
            NetworkCloudlet cl = 
                    new NetworkCloudlet(
                            NetworkConstants.currentCloudletId, 
                            EXEC_TIME/numberOfVMs, pesNumber, fileSize, outputSize, 
                            memory, utilizationModel, utilizationModel, utilizationModel);
            NetworkConstants.currentCloudletId++;
            cl.setUserId(userId);
            cl.submittime=CloudSim.clock();
            cl.currentStageNum=-1;
            cl.setVmId(vmIdList.get(i));
            //compute and send data to node 0
            cl.stages.add(
                    new TaskStage(taskStageId++, TaskStage.Stage.EXECUTION, 
                            NetworkConstants.COMMUNICATION_LENGTH,
                            EXEC_TIME/numberOfVMs,  memory, 
                            vmIdList.get(0),cl.getId()));

            //0 has an extra stage of waiting for results; others send
            if (i==0){
                for(int j=1;j<numberOfVMs;j++)
                    cl.stages.add(
                            new TaskStage(taskStageId++, TaskStage.Stage.WAIT_RECV, 
                                    NetworkConstants.COMMUNICATION_LENGTH, 0, 
                                    memory, vmIdList.get(j), cl.getId()+j));
            } else {
                cl.stages.add(new TaskStage(taskStageId++, TaskStage.Stage.WAIT_SEND, 
                        NetworkConstants.COMMUNICATION_LENGTH, 0, 
                        memory, vmIdList.get(0), t));
            }

            networkCloudletList.add(cl);    			
        }	
    }

    /**
     * The number of VMs that the BagOfTasksAppCloudlet can use
     * is being defined according to the deadline value.
     * 
     * @return the number of VMs the BagOfTasksAppCloudlet can use.
     */
    public int generateNumberOfVms(){
        if(this.deadline > EXEC_TIME/2.0)
            return 2;
        
        return 4;
    }


	
}
