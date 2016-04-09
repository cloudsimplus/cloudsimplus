/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.List;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * WorkflowApp is an example of AppCloudlet having three communicating tasks.
 * Task A and B sends the data (packet) while Task C receives them.
 *
 * <br/>Please refer to following publication for more details:<br/>
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 */
public class WorkflowApp extends AppCloudlet {

    public WorkflowApp(int type, int appId, double deadline, int numberOfVms, int userId) {
        super(type, appId, deadline, numberOfVms, userId);
        execTime = 100;
        this.numberOfVMs = 3;
    }

    /**
     * If the method completely overrides the parent method, a interface or
     * abstract class should be used. The method constant values for cloudlet
     * attributes that have to be defined by the user. Thus, that class appears
     * to be another example class, such as the {@link AppCloudlet}. If this is
     * the intention, it would be inside the examples package. At the
     * constructor, there are other hard-coded values.
     *
     * @param vmIdList
     */
    @Override
    public void createCloudletList(List<Integer> vmIdList) {
        long fileSize = NetworkConstants.FILE_SIZE;
        long outputSize = NetworkConstants.OUTPUT_SIZE;
        int memory = 100;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        int i = 0;
        // Task A
        NetworkCloudlet cl = new NetworkCloudlet(
                NetworkConstants.currentCloudletId,
                0,
                1,
                fileSize,
                outputSize,
                memory,
                utilizationModel,
                utilizationModel,
                utilizationModel);
        cl.numberOfStages = 2;
        NetworkConstants.currentCloudletId++;
        cl.setUserId(userId);
        cl.submittime = CloudSim.clock();
        cl.currentStageNum = -1;
        cl.setVmId(vmIdList.get(i));

        // first stage: big computation
        cl.stages.add(new 
            TaskStage(0, TaskStage.Stage.EXECUTION, 0, 1000 * 0.8, memory, 
                    vmIdList.get(0), cl.getId()));
        cl.stages.add(
                new TaskStage(1, TaskStage.Stage.WAIT_SEND, 1000, 0, memory, 
                    vmIdList.get(2), cl.getId() + 2));
        networkCloudletList.add(cl);
        i++;
        // Task B
        NetworkCloudlet clb = new NetworkCloudlet(
                NetworkConstants.currentCloudletId,
                0, 1, fileSize, outputSize, memory,
                utilizationModel, utilizationModel, utilizationModel);
        clb.numberOfStages = 2;
        NetworkConstants.currentCloudletId++;
        clb.setUserId(userId);
        clb.submittime = CloudSim.clock();
        clb.currentStageNum = -1;
        clb.setVmId(vmIdList.get(i));

        // first stage: big computation
        clb.stages.add(new TaskStage(
                0, TaskStage.Stage.EXECUTION,
                0, 1000 * 0.8, memory, vmIdList.get(1), clb.getId()));
        clb.stages.add(
                new TaskStage(1, TaskStage.Stage.WAIT_SEND, 1000, 0, 
                        memory, vmIdList.get(2), clb.getId() + 1));
        networkCloudletList.add(clb);
        i++;

        // Task C
        NetworkCloudlet clc = new NetworkCloudlet(
                NetworkConstants.currentCloudletId,
                0,
                1,
                fileSize,
                outputSize,
                memory,
                utilizationModel,
                utilizationModel,
                utilizationModel);
        clc.numberOfStages = 2;
        NetworkConstants.currentCloudletId++;
        clc.setUserId(userId);
        clc.submittime = CloudSim.clock();
        clc.currentStageNum = -1;
        clc.setVmId(vmIdList.get(i));

        // first stage: big computation
        clc.stages.add(
                new TaskStage(0, TaskStage.Stage.WAIT_RECV, 1000, 0, memory, 
                    vmIdList.get(0), cl.getId()));
        clc.stages.add(
                new TaskStage(1, TaskStage.Stage.WAIT_RECV, 1000, 0, memory, 
                    vmIdList.get(1), cl.getId() + 1));
        clc.stages.add(new TaskStage(
                1, TaskStage.Stage.EXECUTION, 0, 1000 * 0.8,
                memory, vmIdList.get(0), clc.getId()));

        networkCloudletList.add(clc);
    }
}
