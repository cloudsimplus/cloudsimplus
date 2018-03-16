package org.cloudbus.cloudsim.examples.power.util;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigration;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.power.PowerDatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkload;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkloadSimple;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmStateHistoryEntry;
import org.cloudbus.cloudsim.vms.power.PowerVm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.*;

/**
 * A class to help creating simulation objects for the power-aware simulation examples, printing simulation data
 * and collecting some metrics.
 * <p>
 * <p>If you are using any algorithms, policies or workload included in the power package, please cite
 * the following paper:
 * <br>
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * </p>
 *
 * @author Anton Beloglazov
 */
public final class Helper {
    /**
     * Total percentage of SLA violations.
     */
    public static final String SLA_METRIC_OVERALL_PERCENT = "overall";
    /**
     * Average percentage of SLA violations.
     */
    public static final String SLA_METRIC_AVERAGE_PERCENT = "average";
    /**
     * Total percentage of SLA violations due to VM migration.
     */
    public static final String SLA_METRIC_UNDERALLOCATED_MIGRATION = "underallocated_migration";
    private final String experimentName;
    private final boolean outputInCsv;
    private final String outputFolder;
    private final Simulation simulation;

    /**
     * Stores data to be printed to the console or outputed to a CSV file.
     */
    private StringBuilder buffer;

    /**
     * Creates an Helper object.
     *
     * @param simulation     the {@link Simulation} the Helper is associated to
     * @param experimentName the experiment name
     * @param outputInCsv    if the output should be save into a CSV file
     * @param outputFolder   the output folder
     */
    public Helper(final Simulation simulation, final String experimentName, final boolean outputInCsv, final String outputFolder) {
        this.simulation = simulation;
        this.experimentName = experimentName;
        this.outputInCsv = outputInCsv;
        this.outputFolder = outputFolder;
    }

    /**
     * Creates the vm list.
     *
     * @param broker    the broker
     * @param vmsNumber the vms number
     * @return the list< vm>
     */
    public static List<Vm> createVmList(DatacenterBroker broker, int vmsNumber) {
        List<Vm> vms = new ArrayList<>(vmsNumber);
        for (int i = 0; i < vmsNumber; i++) {
            int vmType = i / (int) Math.ceil((double) vmsNumber / Constants.VM_TYPES);
            CloudletScheduler scheduler = new CloudletSchedulerTimeShared();

            PowerVm vm = new PowerVm(i, Constants.VM_MIPS[vmType], Constants.VM_PES[vmType]);
            vm.setRam(Constants.VM_RAM[vmType])
                .setBw(Constants.VM_BW)
                .setSize(Constants.VM_SIZE)
                .setCloudletScheduler(scheduler);
            vms.add(vm);
        }

        return vms;
    }

    /**
     * Creates the host list.
     *
     * @param hostsNumber the hosts number
     * @return the list< power host>
     */
    public static List<PowerHost> createHostList(int hostsNumber) {
        List<PowerHost> hostList = new ArrayList<>(hostsNumber);
        for (int i = 0; i < hostsNumber; i++) {
            int hostType = i % Constants.HOST_TYPES;

            List<Pe> peList = new ArrayList<>();
            for (int j = 0; j < Constants.HOST_PES[hostType]; j++) {
                peList.add(new PeSimple(Constants.HOST_MIPS[hostType], new PeProvisionerSimple()));
            }

            PowerHost host = new PowerHostUtilizationHistory(
                Constants.HOST_RAM[hostType], Constants.HOST_BW, Constants.HOST_STORAGE, peList);
            host.setPowerModel(Constants.HOST_POWER[hostType])
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeSharedOverSubscription());
            hostList.add(host);
        }
        return hostList;
    }

    /**
     * Creates the broker.
     *
     * @return the Datacenter broker
     */
    public static DatacenterBroker createBroker(CloudSim simulation) {
       return new PowerDatacenterBroker(simulation);
    }

    /**
     * Creates the Datacenter.
     *
     * @param datacenterClass    the Datacenter class
     * @param hostList           the host list
     * @param vmAllocationPolicy the vm allocation policy
     * @return the power Datacenter
     * @throws Exception the exception
     */
    public Datacenter createDatacenter(
        Class<? extends Datacenter> datacenterClass,
        List<PowerHost> hostList,
        VmAllocationPolicy vmAllocationPolicy) throws Exception {
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this resource
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        Constructor<? extends Datacenter> construct = datacenterClass.getConstructor(CloudSim.class,
            DatacenterCharacteristics.class,
            VmAllocationPolicy.class);

        Datacenter datacenter = construct.newInstance(simulation, characteristics, vmAllocationPolicy);
        return datacenter.setSchedulingInterval(Constants.SCHEDULING_INTERVAL);
    }

    /**
     * Gets the times before host shutdown.
     *
     * @param hosts the hosts
     * @return the times before host shutdown
     */
    public List<Double> getTimesBeforeHostShutdown(List<HostSimple> hosts) {
        List<Double> timeBeforeShutdown;
        timeBeforeShutdown = new LinkedList<>();
        for (HostSimple host : hosts) {
            boolean previousIsActive = true;
            double lastTimeSwitchedOn = 0;
            for (HostStateHistoryEntry entry : ((HostDynamicWorkloadSimple) host).getStateHistory()) {
                if (previousIsActive && !entry.isActive()) {
                    timeBeforeShutdown.add(entry.getTime() - lastTimeSwitchedOn);
                }
                if (!previousIsActive && entry.isActive()) {
                    lastTimeSwitchedOn = entry.getTime();
                }
                previousIsActive = entry.isActive();
            }
        }
        return timeBeforeShutdown;
    }

    /**
     * Gets the times before vm migration.
     *
     * @param vms the vms
     * @return the times before vm migration
     */
    public List<Double> getTimesBeforeVmMigration(List<Vm> vms) {
        List<Double> timeBeforeVmMigration = new LinkedList<>();
        for (Vm vm : vms) {
            boolean previousIsInMigration = false;
            double lastTimeMigrationFinished = 0;
            for (VmStateHistoryEntry entry : vm.getStateHistory()) {
                if (previousIsInMigration && !entry.isInMigration()) {
                    timeBeforeVmMigration.add(entry.getTime() - lastTimeMigrationFinished);
                }
                if (!previousIsInMigration && entry.isInMigration()) {
                    lastTimeMigrationFinished = entry.getTime();
                }
                previousIsInMigration = entry.isInMigration();
            }
        }
        return timeBeforeVmMigration;
    }

    /**
     * Prints the results.
     *
     * @param datacenter the Datacenter
     * @param vmList     the List of VMs to get results from
     * @param lastClock  the clock at the end of the simulation
     */
    public void printResults(final PowerDatacenter datacenter, final List<Vm> vmList, final double lastClock) {
        Log.enable();
        final List<HostSimple> hosts = datacenter.getHostList();
        final Map<String, Double> slaMetrics = getSlaMetrics(vmList);

        final double averageSlaViolationPercent = slaMetrics.get(SLA_METRIC_AVERAGE_PERCENT);
        final double percentSlaDegradationDueMigration = slaMetrics.get(SLA_METRIC_UNDERALLOCATED_MIGRATION);
        final double percentSlaViolationTimeForActiveHosts = getSlaViolationTimePercentageForHosts(hosts);
        //@todo It's not clear the difference between the two variables below
        final double overallSlaViolationPercent = slaMetrics.get(SLA_METRIC_OVERALL_PERCENT);
        final double slaViolationsPercent = percentSlaViolationTimeForActiveHosts * percentSlaDegradationDueMigration;

        final List<Double> timeBeforeHostShutdown = getTimesBeforeHostShutdown(hosts);
        final double meanTimeBeforeHostShutdown = timeBeforeHostShutdown.isEmpty() ? Double.NaN : MathUtil.mean(timeBeforeHostShutdown);
        final double stDevTimeBeforeHostShutdown = timeBeforeHostShutdown.isEmpty() ? Double.NaN : MathUtil.stDev(timeBeforeHostShutdown);

        final List<Double> timeBeforeVmMigration = getTimesBeforeVmMigration(vmList);
        final double meanTimeBeforeVmMigration = timeBeforeVmMigration.isEmpty() ? Double.NaN : MathUtil.mean(timeBeforeVmMigration);
        final double stDevTimeBeforeVmMigration = timeBeforeVmMigration.isEmpty() ? Double.NaN : MathUtil.stDev(timeBeforeVmMigration);

        createOutputFoldersIfCsvOutput(outputInCsv, outputFolder);
        Log.enable();
        Log.printLine();
        final String delimiter = ",";
        this.buffer = new StringBuilder();
        addStringToBuffer("Experiment name", "%s", experimentName);
        addStringToBuffer("Number of Hosts", "%d", hosts.size());
        addStringToBuffer("Number of VMs", "%d", vmList.size());
        addStringToBuffer("Total simulation time", "%.2f", lastClock, "sec ("+lastClock/60+" min)");
        addStringToBuffer("Energy consumption", "%.2f", datacenter.getPowerInKWattsHour(), "kWh");
        addStringToBuffer("Number of VM migrations", "%d", datacenter.getMigrationCount());

        addStringToBuffer("SLA violations", "%.5f%%", slaViolationsPercent * 100);
        addStringToBuffer("SLA performance degradation due to migration", "%.5f%%", percentSlaDegradationDueMigration * 100);
        addStringToBuffer("SLA violation time for active hosts", "%.5f%%", percentSlaViolationTimeForActiveHosts * 100);
        addStringToBuffer("Overall SLA violation", "%.5f%%", overallSlaViolationPercent * 100);
        addStringToBuffer("Average SLA violation", "%.5f%%", averageSlaViolationPercent * 100);

        addStringToBuffer("Number of host shutdowns", "%d", timeBeforeHostShutdown.size());
        addStringToBuffer("Mean time before a host shutdown", "%.2f", meanTimeBeforeHostShutdown, "sec");
        addStringToBuffer("StDev time before a host shutdown", "%.2f", stDevTimeBeforeHostShutdown, "sec");
        addStringToBuffer("Mean time before a VM migration", "%.2f", meanTimeBeforeVmMigration, "sec");
        addStringToBuffer("StDev time before a VM migration", "%.2f", stDevTimeBeforeVmMigration, "sec");

        PowerVmAllocationPolicyMigration vap = getPowerVmAllocationPolicy(datacenter);
        this.buffer.append("\n");

        if (outputInCsv) {
            if (!PowerVmAllocationPolicyMigration.NULL.equals(vap)) {
                writeMetricHistoryToFile(hosts, vap, outputFolder + "/metrics/" + experimentName + "_metric");
            }

            writeBufferToFile(outputFolder + "/stats/" + experimentName + "_stats.csv");
            writeDataToFile(timeBeforeHostShutdown, outputFolder + "/time_before_host_shutdown/"
                + experimentName + "_time_before_host_shutdown.csv");
            writeDataToFile(timeBeforeVmMigration, outputFolder + "/time_before_vm_migration/"
                + experimentName + "_time_before_vm_migration.csv");
        } else Log.print(buffer.toString());
        this.buffer = null;

        Log.printLine();
        Log.disable();
    }

    /**
     * Adds a string to the {@link #buffer} so that it can be outputed to a CSV file or directly printed to console.
     *
     * @param label  the label that will be printed before the value if the format is not CSV
     * @param format the format to be used to print the value.
     * @param value  the value to be printed
     */
    private <T> void addStringToBuffer(final String label, String format, final T value) {
        addStringToBuffer(label, format, value, "");
    }

    /**
     * Adds a string to the {@link #buffer} so that it can be outputed to a CSV file or directly printed to console.
     *
     * @param label  the label that will be printed before the value if the format is not CSV
     * @param format the format to be used to print the value.
     * @param value  the value to be printed
     * @param suffix the suffix that will be printed in the end of the line if the format is not CSV
     */
    private <T> void addStringToBuffer(final String label, String format, final T value, String suffix) {
        final String delimiter = outputInCsv ? "," : "\n";
        suffix = outputInCsv ? "" : " " + suffix;
        format = outputInCsv ? format : label + ": " + format;
        final String line = String.format(format, value) + suffix + delimiter;
        buffer.append(line);
    }

    private void createOutputFoldersIfCsvOutput(boolean outputInCsv, String outputFolder) {
        if (!outputInCsv) {
            return;
        }

        final File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File folder1 = new File(outputFolder + "/stats");
        if (!folder1.exists()) {
            folder1.mkdir();
        }
        File folder2 = new File(outputFolder + "/time_before_host_shutdown");
        if (!folder2.exists()) {
            folder2.mkdir();
        }
        File folder3 = new File(outputFolder + "/time_before_vm_migration");
        if (!folder3.exists()) {
            folder3.mkdir();
        }
        File folder4 = new File(outputFolder + "/metrics");
        if (!folder4.exists()) {
            folder4.mkdir();
        }
    }

    /**
     * Try to get the {@link PowerVmAllocationPolicyMigration} from the given Datacenter.
     *
     * @param datacenter the Datacenter to get the PowerVmAllocationPolicyMigration
     * @return the PowerVmAllocationPolicyMigration or {@link PowerVmAllocationPolicyMigration#NULL}
     * if the associated policy is not an {@link PowerVmAllocationPolicyMigration}
     */
    private PowerVmAllocationPolicyMigration getPowerVmAllocationPolicy(PowerDatacenter datacenter) {
        if (datacenter.getVmAllocationPolicy() instanceof PowerVmAllocationPolicyMigration) {
            return (PowerVmAllocationPolicyMigration) datacenter
                .getVmAllocationPolicy();
        }

        return PowerVmAllocationPolicyMigration.NULL;
    }

    /**
     * Gets the percentage of SLA violation time for the given list of Hosts,
     * considering host the times when the Hosts were active.
     * It is the percentage of violation time according
     * to the total time hosts were active.
     *
     * @param hosts the hosts to compute SLA violation time mean
     * @return SLA violation time mean
     */
    protected double getSlaViolationTimePercentageForHosts(final List<HostSimple> hosts) {
        double totalHostsActiveTime = 0;
        double totalHostsSlaViolationTime = 0;

        for (HostSimple host : hosts) {
            HostSlaMetrics sla = new HostSlaMetrics((HostDynamicWorkload) host);
            totalHostsActiveTime += sla.totalActiveTime;
            totalHostsSlaViolationTime += sla.totalSlaViolationTime;
        }

        return totalHostsSlaViolationTime / totalHostsActiveTime;
    }

    /**
     * Gets a map of SLA metric names and the corresponding values from a list of VMs,.
     *
     * @param vms the vms
     * @return the sla metrics map, compounded of metric names and their values.
     */
    protected Map<String, Double> getSlaMetrics(List<Vm> vms) {
        final Map<String, Double> metrics = new HashMap<>(3);
        final List<Double> slaViolation = new LinkedList<>();
        double totalAllocatedMips = 0;
        double totalRequestedMips = 0;
        double totalUnderAllocatedMipsDueToMigration = 0;

        for (Vm vm : vms) {
            VmSlaMetrics sla = new VmSlaMetrics(vm);
            slaViolation.addAll(sla.slaViolation);
            totalAllocatedMips += sla.totalAllocatedMips;
            totalRequestedMips += sla.totalRequestedMips;
            totalUnderAllocatedMipsDueToMigration += sla.underAllocatedMipsDueToMigration;
        }

        metrics.put(SLA_METRIC_OVERALL_PERCENT, (totalRequestedMips - totalAllocatedMips) / totalRequestedMips);
        metrics.put(SLA_METRIC_AVERAGE_PERCENT, slaViolation.isEmpty() ? 0.0 : MathUtil.mean(slaViolation));
        metrics.put(SLA_METRIC_UNDERALLOCATED_MIGRATION, totalUnderAllocatedMipsDueToMigration / totalRequestedMips);

        return metrics;
    }

    /**
     * Write the data from a given list of numbers to a file,
     * one value per line.
     *
     * @param data       the list of numbers to be saved to the file
     * @param outputPath the path of the file to save the data
     */
    public void writeDataToFile(List<? extends Number> data, String outputPath) {
        File file = new File(outputPath);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(0);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Number value : data) {
                writer.write(value.toString() + "\n");
            }
            writer.close();
            System.out.format("File %s created.\n", outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Write the data from the {@link #buffer} to a file
     *
     * @param outputPath the path of the file to save the data
     */
    public void writeBufferToFile(String outputPath) {
        File file = new File(outputPath);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(0);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(buffer.toString());
            writer.close();
            System.out.format("File %s created.\n", outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Write metric history to a file.
     *
     * @param hosts              the hosts
     * @param vmAllocationPolicy the vm allocation policy
     * @param outputPath         the output path
     */
    public void writeMetricHistoryToFile(
        List<? extends HostSimple> hosts,
        PowerVmAllocationPolicyMigration vmAllocationPolicy,
        String outputPath) {
        for (int j = 0; j < 10; j++) {
            HostSimple host = hosts.get(j);

            if (!vmAllocationPolicy.getTimeHistory().containsKey(host)) {
                continue;
            }
            File file = new File(outputPath + "_" + host.getId() + ".csv");
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                List<Double> timeData = vmAllocationPolicy.getTimeHistory().get(host);
                List<Double> utilizationData = vmAllocationPolicy.getUtilizationHistory().get(host);
                List<Double> metricData = vmAllocationPolicy.getMetricHistory().get(host);

                for (int i = 0; i < timeData.size(); i++) {
                    writer.write(String.format(
                        "%.2f,%.2f,%.2f\n",
                        timeData.get(i),
                        utilizationData.get(i),
                        metricData.get(i)));
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    public void printCloudletList(List<CloudletSimple> list) {
        int size = list.size();

        String indent = "\t";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Resource ID" + indent + "VM ID" + indent
            + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (CloudletSimple cloudlet : list) {
            Log.print(indent + cloudlet.getId());

            if (cloudlet.getStatus() == CloudletSimple.Status.SUCCESS) {
                Log.printLine(indent + "SUCCESS" + indent + indent + cloudlet.getLastDatacenter() + indent
                    + cloudlet.getVm() + indent + dft.format(cloudlet.getActualCpuTime()) + indent
                    + dft.format(cloudlet.getExecStartTime()) + indent + indent
                    + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

    /**
     * Prints the metric history.
     *
     * @param hosts              the hosts
     * @param vmAllocationPolicy the vm allocation policy
     */
    public void printMetricHistory(
        List<? extends HostSimple> hosts,
        PowerVmAllocationPolicyMigrationAbstract vmAllocationPolicy) {
        for (int i = 0; i < 10; i++) {
            HostSimple host = hosts.get(i);

            Log.printLine("Host #" + host.getId());
            Log.printLine("Time:");
            if (!vmAllocationPolicy.getTimeHistory().containsKey(host)) {
                continue;
            }
            for (Double time : vmAllocationPolicy.getTimeHistory().get(host)) {
                Log.printFormatted("%.2f, ", time);
            }
            Log.printLine();

            for (Double utilization : vmAllocationPolicy.getUtilizationHistory().get(host)) {
                Log.printFormatted("%.2f, ", utilization);
            }
            Log.printLine();

            for (Double metric : vmAllocationPolicy.getMetricHistory().get(host)) {
                Log.printFormatted("%.2f, ", metric);
            }
            Log.printLine();
        }
    }


    /**
     * An inner class to store data related to a given VM
     * which is used to compute SLA metrics.
     */
    private class VmSlaMetrics {
        Vm vm;
        double totalAllocatedMips;
        double totalRequestedMips;
        double underAllocatedMipsDueToMigration;
        double previousAllocatedMips;
        double previousRequestedMips;
        private double previousTime;
        boolean previousIsInMigration;
        final List<Double> slaViolation;

        VmSlaMetrics(Vm vm) {
            this.vm = vm;
            previousTime = -1;
            slaViolation = new ArrayList<>();
            compute();
        }

        /**
         * Computes some SLA metrics for the VM, based on
         * its MIPS history.
         */
        private void compute() {
            for (final VmStateHistoryEntry entry : vm.getStateHistory()) {
                if (previousTime != -1) {
                    double timeDiff = entry.getTime() - previousTime;
                    totalAllocatedMips += previousAllocatedMips * timeDiff;
                    totalRequestedMips += previousRequestedMips * timeDiff;

                    if (previousAllocatedMips < previousRequestedMips) {
                        slaViolation.add((previousRequestedMips - previousAllocatedMips) / previousRequestedMips);
                        if (previousIsInMigration) {
                            underAllocatedMipsDueToMigration +=
                                (previousRequestedMips - previousAllocatedMips) * timeDiff;
                        }
                    }
                }

                previousAllocatedMips = entry.getAllocatedMips();
                previousRequestedMips = entry.getRequestedMips();
                previousTime = entry.getTime();
                previousIsInMigration = entry.isInMigration();
            }
        }
    }

    /**
     * An inner class to store data related to a given Host
     * which is used to compute SLA metrics.
     */
    private class HostSlaMetrics {
        HostDynamicWorkload host;
        private double previousTime;
        double previousAllocatedMips;
        double previousRequestedMips;
        double totalSlaViolationTime;
        double totalActiveTime;
        boolean previousIsActive;

        /**
         * Creates a Host SLA Object to compute SLA metrics for the given Host.
         * @param host the Host to compute SLA metrics.
         */
        HostSlaMetrics(HostDynamicWorkload host) {
            this.host = host;
            previousTime = -1;
            previousIsActive = true;
            compute();
        }

        /**
         * Computes some SLA metrics for the Host, based on its MIPS history,
         * considering just the times when the Host was active.
         */
        private void compute() {
            for (HostStateHistoryEntry entry : host.getStateHistory()) {
                if (previousTime != -1 && previousIsActive) {
                    final double timeDiff = entry.getTime() - previousTime;
                    totalActiveTime += timeDiff;
                    if (previousAllocatedMips < previousRequestedMips) {
                        totalSlaViolationTime += timeDiff;
                    }
                }

                previousRequestedMips = entry.getRequestedMips();
                previousAllocatedMips = entry.getAllocatedMips();
                previousTime = entry.getTime();
                previousIsActive = entry.isActive();
            }
        }

    }

}
