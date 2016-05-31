/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetDatacenterBroker;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkDatacenter;
import org.cloudbus.cloudsim.network.datacenter.NetworkVm;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 *
 * @author raysaoliveira
 */
public class NetworkVmsExampleWithMetrics extends NetworkVmsExampleAppCloudletAbstract {

    public NetworkVmsExampleWithMetrics() {
        super();
        /*
         Total Cost Price
         */
        NetworkDatacenter datacenter = this.getDatacenter();
        List<NetworkVm> vm = this.getVmlist();
        NetDatacenterBroker broker = this.getBroker();
        TotalCostPrice(datacenter, vm, broker);

        /* 
         AppCloudlet app = this.getAppCloudlet();
         List<NetworkCloudlet> createCloudlets = createNetworkCloudlets(app, vm);
         List<NetworkCloudlet> cloudlet = app.getNetworkCloudletList();
         Iterator iterator = cloudlet.iterator(); iterator.hasNext();
             for(NetworkCloudlet cloudlet1: cloudlet){
                NetworkCloudlet cloud = cloudlet1.getCloudlet();
                ResponseTimeCloudlet(cloud);        
         }
        
         /*
         *** CPU TIME ***
         double timeCpu = 0;
         List<AppCloudlet> appCloudletList = this.getAppCloudletList();
         System.out.println("PASSEI");
         for (AppCloudlet app : appCloudletList) {
             List<NetworkCloudlet> networkCloudlet = app.getNetworkCloudletList();
             for (NetworkCloudlet networkC : networkCloudlet) {
                networkC.getActualCPUTime(datacenter.getId());
             }
         }
         // System.out.println("******** Time CPU:" + timeCpu);
         //throughput
         // EdgeSwitch ed = new EdgeSwitch("SwitchEdge1", datacenter0);
         //pegar o dowlink BW do edge, pois as Vms estao conectadas nele
         */
    }

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("TESTE1");
        NetworkVmsExampleWithMetrics networkVmsExampleWithMetrics = new NetworkVmsExampleWithMetrics();

    }
    
    

    private void TotalCostPrice(NetworkDatacenter datacenter, List<NetworkVm> vm, NetDatacenterBroker broker) {

        double memoryDataCenterVm, totalCost = 0;
        double bwDataCenterVm, miDataCenterVm, storageDataCenterVm;
        int numberOfVms = datacenter.getCharacteristics().getHostList().size() * MAX_VMS_PER_HOST;
        for (NetworkVm vms : vm) {
            memoryDataCenterVm = ((datacenter.getCharacteristics().getCostPerMem()) * vms.getRam() * numberOfVms);
            bwDataCenterVm = ((datacenter.getCharacteristics().getCostPerBw()) * vms.getBw() * numberOfVms);
            miDataCenterVm = ((datacenter.getCharacteristics().getCostPerMi()) * vms.getMips() * numberOfVms);
            storageDataCenterVm = ((datacenter.getCharacteristics().getCostPerStorage()) * vms.getSize() * numberOfVms);

            totalCost = memoryDataCenterVm + bwDataCenterVm + miDataCenterVm + storageDataCenterVm;
        }
        System.out.println("* Total Cost Price ******: " + totalCost);
    }
    /*
     private void ResponseTimeCloudlet(NetworkCloudlet cloudlet) {
        
     double rt = cloudlet.getFinishTime() - cloudlet.getSubmissionTime();
     System.out.println("***** Tempo de resposta CLOUDLETS - " + rt);

     } */

    @Override
    protected List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app, List<NetworkVm> vmList) {
        System.out.println("TESTE2");
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(vmList.size());
        System.out.println("TESTE3");
        int currentNetworkCloudletId = 0;
        for (NetworkVm vm : vmList) {
            System.out.println("TESTE4");
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
