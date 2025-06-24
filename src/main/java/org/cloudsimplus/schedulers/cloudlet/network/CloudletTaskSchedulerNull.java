package org.cloudsimplus.schedulers.cloudlet.network;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.vms.Vm;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link CloudletTaskScheduler} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletTaskScheduler#NULL
 */
final class CloudletTaskSchedulerNull implements CloudletTaskScheduler {
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public CloudletTaskScheduler setVm(Vm vm) { return this; }
    @Override public void clearVmPacketsToSend() {/**/}
    @Override public List<VmPacket> getVmPacketsToSend() {
        return Collections.emptyList();
    }
    @Override public boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt) {
        return false;
    }
    @Override public void processCloudletTasks(Cloudlet cloudlet, long partialFinishedMI) {/**/}
    @Override public boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet) {
        return true;
    }
}
