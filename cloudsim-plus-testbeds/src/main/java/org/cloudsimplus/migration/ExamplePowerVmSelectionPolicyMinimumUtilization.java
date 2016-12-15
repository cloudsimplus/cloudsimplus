/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.migration;

import java.util.List;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.power.PowerVm;

/**
 *
 * @author raysaoliveira
 */
public class ExamplePowerVmSelectionPolicyMinimumUtilization extends PowerVmSelectionPolicy {

    @Override
    public Vm getVmToMigrate(PowerHost host) {
        List<PowerVm> migratableVms = getMigratableVms(host);
        if (migratableVms.isEmpty()) {
            return Vm.NULL;
        }

        Vm vmToMigrate = Vm.NULL;
        double minMetric = Double.MAX_VALUE;
        for (Vm vm : migratableVms) {
            if (vm.isInMigration()) {
                continue;
            }
            double metric = vm.getTotalUtilizationOfCpuMips(host.getSimulation().clock()) / vm.getMips();
            if (metric < minMetric) {
                minMetric = metric;
                vmToMigrate = vm;
            }
        }
        return vmToMigrate;
    }
}
