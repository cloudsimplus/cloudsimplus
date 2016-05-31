package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkVm;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example of a simple {@link AppCloudlet}'s that are composed of 
 * different {@link NetworkCloudlet} that in fact just process data, running 
 * like a regular Cloudlet.
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
     * @param selectedVms List of VMs where the NetworkCloudlets will be executed
     * @return the list of created NetworkCloudlets
     */
    @Override
    public List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app, List<NetworkVm> selectedVms) {
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(selectedVms.size());
        int currentNetworkCloudletId = 0;    
        for (Vm vm: selectedVms) {
            long length = 4;
            long fileSize = 300;
            long outputSize = 300;
            long memory = 256;
            int pesNumber = 4;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            NetworkCloudlet netCloudlet = new NetworkCloudlet(
                    currentNetworkCloudletId,
                    length, pesNumber, fileSize, outputSize, memory,
                    utilizationModel, utilizationModel, utilizationModel);
            netCloudlet.setAppCloudlet(app);
            // setting the owner of these Cloudlets
            netCloudlet.setUserId(getBroker().getId());
            netCloudlet.submittime = CloudSim.clock();
            networkCloudletList.add(netCloudlet);
            currentNetworkCloudletId++;
        }
        
        return networkCloudletList;
    }

  
   
}
