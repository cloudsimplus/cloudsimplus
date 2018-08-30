/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.examples.autoscaling;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Processor;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScalingSimple;
import org.cloudsimplus.autoscaling.resources.ResourceScaling;
import org.cloudsimplus.autoscaling.resources.ResourceScalingGradual;
import org.cloudsimplus.autoscaling.resources.ResourceScalingInstantaneous;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;

/**
 * An example that scales VM PEs up or down, according to the arrival of Cloudlets.
 * A {@link VerticalVmScaling}
 * is set to each {@link #createListOfScalableVms(int) initially created VM}.
 * Every VM will check at {@link #SCHEDULING_INTERVAL specific time intervals}
 * if its PEs {@link #upperCpuUtilizationThreshold(Vm) are over or underloaded},
 * according to a <b>dynamic computed utilization threshold</b>.
 * Then it requests such PEs to be up or down scaled.
 *
 * <p>The example uses the CloudSim Plus {@link EventListener} feature
 * to enable monitoring the simulation and dynamically create objects such as Cloudlets and VMs at runtime.
 * It relies on
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html">Java 8 Method References</a>
 * to set a method to be called for {@link Simulation#addOnClockTickListener(EventListener) onClockTick events}.
 * It enables getting notifications when the simulation clock advances, then creating and submitting new cloudlets.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.2.1
 * @see VerticalVmCpuScalingExample
 */
public class VerticalVmCpuScalingDynamicThreshold {
    /**
     * The interval in which the Datacenter will schedule events.
     * As lower is this interval, sooner the processing of VMs and Cloudlets
     * is updated and you will get more notifications about the simulation execution.
     * However, it can affect the simulation performance.
     *
     * <p>For this example, a large schedule interval such as 15 will make that just
     * at every 15 seconds the processing of VMs is updated. If a VM is overloaded, just
     * after this time the creation of a new one will be requested
     * by the VM's {@link HorizontalVmScaling Horizontal Scaling} mechanism.</p>
     *
     * <p>If this interval is defined using a small value, you may get
     * more dynamically created VMs than expected. Accordingly, this value
     * has to be trade-off.
     * For more details, see {@link Datacenter#getSchedulingInterval()}.</p>
    */
    private static final int SCHEDULING_INTERVAL = 1;
    private static final int HOSTS = 1;

    private static final int HOST_PES = 32;
    private static final int VMS = 1;
    private static final int VM_PES = 14;
    private static final int VM_RAM = 1200;
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private static final int CLOUDLETS = 10;
    private static final int CLOUDLETS_INITIAL_LENGTH = 20_000;

    private int createsVms;

    public static void main(String[] args) {
        new VerticalVmCpuScalingDynamicThreshold();
    }

    /**
     * Default constructor that builds the simulation scenario and starts the simulation.
     */
    private VerticalVmCpuScalingDynamicThreshold() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        hostList = new ArrayList<>(HOSTS);
        vmList = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(CLOUDLETS);

        simulation = new CloudSim();
        simulation.addOnClockTickListener(this::onClockTickListener);

        createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList.addAll(createListOfScalableVms(VMS));

        createCloudletListsWithDifferentDelays();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        printSimulationResults();
    }

    /**
     * Shows updates every time the simulation clock advances.
     * @param evt information about the event happened (that for this Listener is just the simulation time)
     */
    private void onClockTickListener(EventInfo evt) {
        vmList.forEach(vm -> {
            System.out.printf(
                "\t\tTime %6.1f: Vm %d CPU Usage: %6.2f%% (%2d vCPUs. Running Cloudlets: #%02d) Upper Threshold: %.2f History Entries: %d\n",
                evt.getTime(), vm.getId(), vm.getCpuPercentUsage()*100.0,
                vm.getNumberOfPes(),
                vm.getCloudletScheduler().getCloudletExecList().size(),
                vm.getPeVerticalScaling().getUpperThresholdFunction().apply(vm),
                vm.getUtilizationHistory().getHistory().size());
        });
    }

    private void printSimulationResults() {
        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        final Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        final Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
        finishedCloudlets.sort(sortByVmId.thenComparing(sortByStartTime));

        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private void createDatacenter() {
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }

        Datacenter dc0 = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        final long ram = 20000; //in Megabytes
        final long bw = 100000; //in Megabytes
        final long storage = 10000000; //in Megabites/s
        final int id = hostList.size();
        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    /**
     * Creates a list of initial VMs in which each VM is able to scale vertically
     * when it is over or underloaded.
     *
     * @param numberOfVms number of VMs to create
     * @return the list of scalable VMs
     * @see #createVerticalPeScaling()
     */
    private List<Vm> createListOfScalableVms(final int numberOfVms) {
        List<Vm> newList = new ArrayList<>(numberOfVms);
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm = createVm();
            vm.setPeVerticalScaling(createVerticalPeScaling());
            newList.add(vm);
        }

        return newList;
    }

    /**
     * Creates a Vm object, enabling it to store
     * CPU utilization history which is used
     * to compute a dynamic threshold for CPU vertical scaling.
     *
     * @return the created Vm
     */
    private Vm createVm() {
        final int id = createsVms++;

        final Vm vm = new VmSimple(id, 1000, VM_PES)
            .setRam(VM_RAM).setBw(1000).setSize(10000)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());

        vm.getUtilizationHistory().enable();
        return vm;
    }

    /**
     * Creates a {@link VerticalVmScaling} for scaling VM's CPU when it's under or overloaded.
     *
     * <p>Realize the lower and upper thresholds are defined inside this method by using
     * references to the methods {@link #lowerCpuUtilizationThreshold(Vm)}
     * and {@link #upperCpuUtilizationThreshold(Vm)}.
     * These methods enable defining thresholds in a dynamic way
     * and even different thresholds for distinct VMs.
     * Therefore, it's a powerful mechanism.
     * </p>
     *
     * <p>
     * However, if you are defining thresholds in a static way,
     * and they are the same for all VMs, you can use a Lambda Expression
     * like below, for instance, instead of creating a new method that just returns a constant value:<br>
     * {@code verticalCpuScaling.setLowerThresholdFunction(vm -> 0.4);}
     * </p>
     *
     * @see #createListOfScalableVms(int)
     */
    private VerticalVmScaling createVerticalPeScaling() {
        //The percentage in which the number of PEs has to be scaled
        final double scalingFactor = 0.1;
        VerticalVmScalingSimple verticalCpuScaling = new VerticalVmScalingSimple(Processor.class, scalingFactor);

        /* By uncommenting the line below, you will see that, instead of gradually
         * increasing or decreasing the number of PEs, when the scaling object detects
         * the CPU usage is above or below the defined thresholds,
         * it will automatically calculate the number of PEs to add/remove to
         * move the VM from the over or underload condition.
        */
        //verticalCpuScaling.setResourceScaling(new ResourceScalingInstantaneous());

        /** Different from the commented line above, the line below implements a ResourceScaling using a Lambda Expression.
         * It is just an example which scales the resource twice the amount defined by the scaling factor
         * defined in the constructor.
         *
         * Realize that if the setResourceScaling method is not called, a ResourceScalingGradual will be used,
         * which scales the resource according to the scaling factor.
         * The lower and upper thresholds after this line can also be defined using a Lambda Expression.
         *
         * So, here we are defining our own {@link ResourceScaling} instead of
         * using the available ones such as the {@link ResourceScalingGradual}
         * or {@link ResourceScalingInstantaneous}.
         */
        verticalCpuScaling.setResourceScaling(vs -> 2*vs.getScalingFactor()*vs.getAllocatedResource());

        verticalCpuScaling.setLowerThresholdFunction(this::lowerCpuUtilizationThreshold);
        verticalCpuScaling.setUpperThresholdFunction(this::upperCpuUtilizationThreshold);

        return verticalCpuScaling;
    }

    /**
     * Defines the minimum CPU utilization percentage that indicates a Vm is underloaded.
     * This function is using a statically defined threshold, but it would be defined
     * a dynamic threshold based on any condition you want.
     * A reference to this method is assigned to each Vertical VM Scaling created.
     *
     * @param vm the VM to check if its CPU is underloaded.
     *        <b>The parameter is not being used internally, which means the same
     *        threshold is used for any Vm.</b>
     * @return the lower CPU utilization threshold
     * @see #createVerticalPeScaling()
     */
    private double lowerCpuUtilizationThreshold(Vm vm) {
        return 0.4;
    }

    /**
     * Defines a dynamic CPU utilization threshold that indicates a Vm is overloaded.
     * Such a threshold is the maximum CPU a VM can use before requesting vertical CPU scaling.
     * A reference to this method is assigned to each Vertical VM Scaling created.
     *
     * <p>The dynamic upper threshold is defined as 20% above the mean (mean * 1.2),
     * if there are at least 10 CPU utilization history entries.
     * That means if the CPU utilization of a VM is 20% above its mean
     * CPU utilization, it indicates the VM is overloaded.
     * If there aren't enough history entries,
     * it defines a static threshold as 70% of CPU utilization.</p>
     *
     * @param vm the VM to check if its CPU is overloaded.
     *        The parameter is not being used internally, that means the same
     *        threshold is used for any Vm.
     * @return the upper dynamic CPU utilization threshold
     * @see #createVerticalPeScaling()
     */
    private double upperCpuUtilizationThreshold(Vm vm) {
        final List<Double> history = vm.getUtilizationHistory().getHistory().values().stream().collect(Collectors.toList());
        return history.size() > 10 ? MathUtil.median(history) * 1.2 : 0.7;
    }

    /**
     * Creates lists of Cloudlets to be submitted to the broker with different delays,
     * simulating their arrivals at different times.
     * Adds all created Cloudlets to the {@link #cloudletList}.
     */
    private void createCloudletListsWithDifferentDelays() {
        final int initialCloudletsNumber = (int)(CLOUDLETS/2.5);
        final int remainingCloudletsNumber = CLOUDLETS-initialCloudletsNumber;
        //Creates a List of Cloudlets that will start running immediately when the simulation starts
        for (int i = 0; i < initialCloudletsNumber; i++) {
            cloudletList.add(createCloudlet(CLOUDLETS_INITIAL_LENGTH+(i*1000), 2));
        }

        /*
         * Creates several Cloudlets, increasing the arrival delay and decreasing
         * the length of each one.
         * The progressing delay enables CPU usage to increase gradually along the arrival of
         * new Cloudlets (triggering CPU up scaling at some point in time).
         *
         * The decreasing length enables Cloudlets to finish in different times,
         * to gradually reduce CPU usage (triggering CPU down scaling at some point in time).
         *
         * Check the logs to understand how the scaling is working.
        */
        for (int i = 1; i <= remainingCloudletsNumber; i++) {
            cloudletList.add(createCloudlet(CLOUDLETS_INITIAL_LENGTH*2/i, 1,i*2));
        }
    }

    /**
     * Creates a single Cloudlet with no delay, which means the Cloudlet arrival time will
     * be zero (exactly when the simulation starts).
     *
     * @param length the Cloudlet length
     * @param numberOfPes the number of PEs the Cloudlets requires
     * @return the created Cloudlet
     */
    private Cloudlet createCloudlet(final long length, final int numberOfPes) {
        return createCloudlet(length, numberOfPes, 0);
    }

    /**
     * Creates a single Cloudlet.
     *
     * @param length the length of the cloudlet to create.
     * @param numberOfPes the number of PEs the Cloudlets requires.
     * @param delay the delay that defines the arrival time of the Cloudlet at the Cloud infrastructure.
     * @return the created Cloudlet
     */
    private Cloudlet createCloudlet(final long length, final int numberOfPes, final double delay) {
        /*
        Since a VM PE isn't used by two Cloudlets at the same time,
        the Cloudlet can used 100% of that CPU capacity at the time
        it is running. Even if a CloudletSchedulerTimeShared is used
        to share the same VM PE among multiple Cloudlets,
        just one Cloudlet uses the PE at a time.
        Then it is preempted to enable other Cloudlets to use such a VM PE.
         */
        final UtilizationModel utilizationCpu = new UtilizationModelFull();

        /**
         * Since BW e RAM are shared resources that don't enable preemption,
         * two Cloudlets can't use the same portion of such resources at the same time
         * (unless virtual memory is enabled, but such a feature is not available in simulation).
         * This way, the total capacity of such resources is being evenly split among created Cloudlets.
         * If there are 10 Cloudlets, each one will use just 10% of such resources.
         * This value can be defined in different ways, as you want. For instance, some Cloudlets
         * can require more resources than other ones.
         * To enable that, you would need to instantiate specific {@link UtilizationModelDynamic} for each Cloudlet,
         * use a {@link UtilizationModelStochastic} to define resource usage randomly,
         * or use any other {@link UtilizationModel} implementation.
        */
        final UtilizationModel utilizationModelDynamic = new UtilizationModelDynamic(1.0/CLOUDLETS);
        Cloudlet cl = new CloudletSimple(length, numberOfPes);
        cl.setFileSize(1024)
            .setOutputSize(1024)
            .setUtilizationModelBw(utilizationModelDynamic)
            .setUtilizationModelRam(utilizationModelDynamic)
            .setUtilizationModelCpu(utilizationCpu)
            .setSubmissionDelay(delay);
        return cl;
    }
}
