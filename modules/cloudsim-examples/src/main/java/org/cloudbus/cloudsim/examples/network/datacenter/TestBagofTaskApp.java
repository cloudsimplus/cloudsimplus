package org.cloudbus.cloudsim.examples.network.datacenter;

/**
 * BagofTaskApp is an example of AppCloudlet having three non-communicating tasks. 
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

import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkConstants;
import org.cloudbus.cloudsim.network.datacenter.TaskStage;

/**
 * See the todo in the class documentation.
 * 
 * @todo This class appear to be only for tests.
 * It has some strange code. See the TODOs inside it.
 * It in fact is not being used and should be deleted.
 * 
 */
public class TestBagofTaskApp extends AppCloudlet {

	public TestBagofTaskApp(int type, int appID, double deadline, 	int numbervm, int userId) {
		super(type, appID, deadline, numbervm,userId);
		
                /*@todo There is something strange here. This getter isn't
                in fact a getter. It computes a value basd on the execTime,
                that is strange too (how can be seen in the todo below).
                Further, the given parameter is being ignored, so, it shound't
                exist.*/
		this.numbervm=this.getnumvm();
                
                /*@todo There is something strange here.
                The attribute is exeTime but the getter is getExecTime (see: exec not exe).
                The getter always returns 100, not the value stored
                in the attribute, that is not used anywhere. There is no getter in the
                super class.
                */
		this.exeTime=getExecTime()/this.numbervm;
	}
	
	@Override
    public void createCloudletList(List<Integer> vmIdList){
		//basically, each task runs the simulation and then data is consolidated in one task
		int executionTime = getExecTime();
		long memory = 1000;
		long fileSize = NetworkConstants.FILE_SIZE;
		long outputSize = NetworkConstants.OUTPUT_SIZE;
		int pesNumber = NetworkConstants.PES_NUMBER;
		int stgId=0;
		int t=NetworkConstants.currentCloudletId;
	  	for(int i=0;i<numbervm;i++){
    		UtilizationModel utilizationModel = new UtilizationModelFull();
    		NetworkCloudlet cl = new NetworkCloudlet(NetworkConstants.currentCloudletId, executionTime/numbervm, pesNumber, fileSize, outputSize, memory, utilizationModel, utilizationModel, utilizationModel);
    		NetworkConstants.currentCloudletId++;
    		cl.setUserId(userId);
			cl.submittime=CloudSim.clock();
    		cl.currStagenum=-1;
			cl.setVmId(vmIdList.get(i));
			//compute and send data to node 0
    		cl.stages.add(new TaskStage(NetworkConstants.EXECUTION, NetworkConstants.COMMUNICATION_LENGTH, executionTime/numbervm, stgId++, memory, vmIdList.get(0),cl.getCloudletId()));
    		
    		//0 has an extra stage of waiting for results; others send
    		if (i==0){
    			for(int j=1;j<numbervm;j++)
    				cl.stages.add(new TaskStage(NetworkConstants.WAIT_RECV, NetworkConstants.COMMUNICATION_LENGTH, 0, stgId++, memory, vmIdList.get(j),cl.getCloudletId()+j));
    		} else {
    			cl.stages.add(new TaskStage(NetworkConstants.WAIT_SEND, NetworkConstants.COMMUNICATION_LENGTH, 0, stgId++, memory, vmIdList.get(0),t));
    		}
    		
    		clist.add(cl);    			
    	}	
	}
	
	/**
	 * One can generate number of VMs for each application based on deadline
	 * @return
	 */
	public int getnumvm(){
		double exetime=getExecTime()/2;//for two vms
		if(this.deadline>exetime)
			return 2;
		else if(this.deadline>(exetime/4)) return 4;
			
		return 4;
	}
	
	private int getExecTime() {
		//use exec constraints 
		
		return 100;
	}

	
}
