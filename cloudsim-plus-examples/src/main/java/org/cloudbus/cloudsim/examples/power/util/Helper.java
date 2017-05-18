package org.cloudbus.cloudsim.examples.power.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigration;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkloadSimple;
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.vms.VmStateHistoryEntry;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
import org.cloudbus.cloudsim.brokers.power.PowerDatacenterBroker;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A class to help creating simulation objects for the power-aware simulation examples, printing simulation data
 * and collecting some metrics.
 *
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
	 * Creates the vm list.
	 *
	 * @param broker the broker
	 * @param vmsNumber the vms number
	 *
	 * @return the list< vm>
	 */
	public static List<Vm> createVmList(DatacenterBroker broker, int vmsNumber) {
		List<Vm> vms = new ArrayList<>(vmsNumber);
		for (int i = 0; i < vmsNumber; i++) {
			int vmType = i / (int) Math.ceil((double) vmsNumber / Constants.VM_TYPES);
            CloudletScheduler scheduler = new CloudletSchedulerTimeShared(); //Constants.VM_MIPS[vmType], Constants.VM_PES[vmType]

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
	 *
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
		DatacenterBroker broker = null;
		try {
			broker = new PowerDatacenterBroker(simulation);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return broker;
	}

	/**
	 * Creates the Datacenter.
	 *
	 * @param datacenterClass the Datacenter class
	 * @param hostList the host list
	 * @param vmAllocationPolicy the vm allocation policy
	 *
	 * @return the power Datacenter
	 *
	 * @throws Exception the exception
	 */
	public static Datacenter createDatacenter(
	    CloudSim simulation,
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
	public static List<Double> getTimesBeforeHostShutdown(List<HostSimple> hosts) {
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
	public static List<Double> getTimesBeforeVmMigration(List<Vm> vms) {
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
     * @param vmList the List of VMs to get results from
	 * @param lastClock the last clock
	 * @param experimentName the experiment name
	 * @param outputInCsv the output in csv
	 * @param outputFolder the output folder
	 */
	public static void printResults(
			PowerDatacenter datacenter,
			List<Vm> vmList,
			double lastClock,
			String experimentName,
			boolean outputInCsv,
			String outputFolder)
    {
		Log.enable();
		final List<HostSimple> hosts = datacenter.getHostList();
        final Map<String, Double> slaMetrics = getSlaMetrics(vmList);

        final double slaOverall = slaMetrics.get("overall");
        final double slaAverage = slaMetrics.get("average");
        final double slaDegradationDueToMigration = slaMetrics.get("underallocated_migration");
        final double slaTimePerActiveHost = getSlaTimePerActiveHost(hosts);
        final double sla = slaTimePerActiveHost * slaDegradationDueToMigration;

		final List<Double> timeBeforeHostShutdown = getTimesBeforeHostShutdown(hosts);
        final double meanTimeBeforeHostShutdown  = timeBeforeHostShutdown.isEmpty() ? Double.NaN : MathUtil.mean(timeBeforeHostShutdown);
		final double stDevTimeBeforeHostShutdown = timeBeforeHostShutdown.isEmpty() ? Double.NaN : MathUtil.stDev(timeBeforeHostShutdown);

		final List<Double> timeBeforeVmMigration = getTimesBeforeVmMigration(vmList);
		final double meanTimeBeforeVmMigration  = timeBeforeVmMigration.isEmpty() ? Double.NaN : MathUtil.mean(timeBeforeVmMigration);
		final double stDevTimeBeforeVmMigration = timeBeforeVmMigration.isEmpty() ? Double.NaN : MathUtil.stDev(timeBeforeVmMigration);

		if (outputInCsv) {
            createOutputFolders(outputFolder);
            final StringBuilder data = new StringBuilder();
			final String delimiter = ",";

			data.append(experimentName).append(delimiter);
			data.append(parseExperimentName(experimentName));
			data.append(String.format("%d", hosts.size())).append(delimiter);
			data.append(String.format("%d", vmList.size())).append(delimiter);
			data.append(String.format("%.2f", lastClock)).append(delimiter);
			data.append(String.format("%.5f", datacenter.getPowerInKWattsHour())).append(delimiter);
			data.append(String.format("%d", datacenter.getMigrationCount())).append(delimiter);
			data.append(String.format("%.10f", sla)).append(delimiter);
			data.append(String.format("%.10f", slaTimePerActiveHost)).append(delimiter);
			data.append(String.format("%.10f", slaDegradationDueToMigration)).append(delimiter);
			data.append(String.format("%.10f", slaOverall)).append(delimiter);
			data.append(String.format("%.10f", slaAverage)).append(delimiter);
			data.append(String.format("%d", timeBeforeHostShutdown.size())).append(delimiter);
			data.append(String.format("%.2f", meanTimeBeforeHostShutdown)).append(delimiter);
			data.append(String.format("%.2f", stDevTimeBeforeHostShutdown)).append(delimiter);
			data.append(String.format("%.2f", meanTimeBeforeVmMigration)).append(delimiter);
			data.append(String.format("%.2f", stDevTimeBeforeVmMigration)).append(delimiter);

            PowerVmAllocationPolicyMigration vap = getPowerVmAllocationPolicy(datacenter);
            if (!PowerVmAllocationPolicyMigration.NULL.equals(vap)) {
                data.append(String.format("%.5f", MathUtil.mean(vap.getExecutionTimeHistoryVmSelection()))).append(delimiter);
				data.append(String.format("%.5f", MathUtil.stDev(vap.getExecutionTimeHistoryVmSelection()))).append(delimiter);
				data.append(String.format("%.5f", MathUtil.mean(vap.getExecutionTimeHistoryHostSelection()))).append(delimiter);
				data.append(String.format("%.5f", MathUtil.stDev(vap.getExecutionTimeHistoryHostSelection()))).append(delimiter);
				data.append(String.format("%.5f", MathUtil.mean(vap.getExecutionTimeHistoryVmReallocation()))).append(delimiter);
				data.append(String.format("%.5f", MathUtil.stDev(vap.getExecutionTimeHistoryVmReallocation()))).append(delimiter);
				data.append(String.format("%.5f", MathUtil.mean(vap.getExecutionTimeHistoryTotal()))).append(delimiter);
				data.append(String.format("%.5f", MathUtil.stDev(vap.getExecutionTimeHistoryTotal()))).append(delimiter);
				writeMetricHistory(hosts, vap, outputFolder + "/metrics/" + experimentName + "_metric");
			}
			data.append("\n");

			writeDataRow(data.toString(), outputFolder + "/stats/" + experimentName + "_stats.csv");
			writeDataColumn(timeBeforeHostShutdown, outputFolder + "/time_before_host_shutdown/"
					+ experimentName + "_time_before_host_shutdown.csv");
			writeDataColumn(timeBeforeVmMigration, outputFolder + "/time_before_vm_migration/"
					+ experimentName + "_time_before_vm_migration.csv");

		} else {
			Log.enable();
			Log.printLine();
			Log.printFormattedLine("Experiment name: %s", experimentName);
			Log.printFormattedLine("Number of hosts: %d", hosts.size());
			Log.printFormattedLine("Number of VMs: %d", vmList.size());
			Log.printFormattedLine("Total simulation time: %.2f sec", lastClock);
			Log.printFormattedLine("Energy consumption: %.2f kWh", datacenter.getPowerInKWattsHour());
			Log.printFormattedLine("Number of VM migrations: %d", datacenter.getMigrationCount());
			Log.printFormattedLine("SLA: %.5f%%", sla * 100);
			Log.printFormattedLine("SLA perf degradation due to migration: %.2f%%", slaDegradationDueToMigration * 100);
			Log.printFormattedLine("SLA time per active host: %.2f%%", slaTimePerActiveHost * 100);
			Log.printFormattedLine("Overall SLA violation: %.2f%%", slaOverall * 100);
			Log.printFormattedLine("Average SLA violation: %.2f%%", slaAverage * 100);
			Log.printFormattedLine("Number of host shutdowns: %d", timeBeforeHostShutdown.size());
			Log.printFormattedLine("Mean time before a host shutdown: %.2f sec", meanTimeBeforeHostShutdown);
			Log.printFormattedLine("StDev time before a host shutdown: %.2f sec", stDevTimeBeforeHostShutdown);
			Log.printFormattedLine("Mean time before a VM migration: %.2f sec", meanTimeBeforeVmMigration);
			Log.printFormattedLine("StDev time before a VM migration: %.2f sec", stDevTimeBeforeVmMigration);

            PowerVmAllocationPolicyMigration vap = getPowerVmAllocationPolicy(datacenter);
			if (!PowerVmAllocationPolicyMigration.NULL.equals(vap)) {
                Log.printFormattedLine("Execution time - VM selection mean: %.5f sec", MathUtil.mean(vap.getExecutionTimeHistoryVmSelection()));
				Log.printFormattedLine("Execution time - VM selection stDev: %.5f sec", MathUtil.stDev(vap.getExecutionTimeHistoryVmSelection()));
				Log.printFormattedLine("Execution time - host selection mean: %.5f sec", MathUtil.mean(vap.getExecutionTimeHistoryHostSelection()));
				Log.printFormattedLine("Execution time - host selection stDev: %.5f sec", MathUtil.stDev(vap.getExecutionTimeHistoryHostSelection()));
				Log.printFormattedLine("Execution time - VM reallocation mean: %.5f sec", MathUtil.mean(vap.getExecutionTimeHistoryVmReallocation()));
				Log.printFormattedLine("Execution time - VM reallocation stDev: %.5f sec", MathUtil.stDev(vap.getExecutionTimeHistoryVmReallocation()));
				Log.printFormattedLine("Execution time - total mean: %.5f sec", MathUtil.mean(vap.getExecutionTimeHistoryTotal()));
				Log.printFormattedLine("Execution time - total stDev: %.5f sec", MathUtil.stDev(vap.getExecutionTimeHistoryTotal()));
			}
			Log.printLine();
		}

		Log.disable();
	}

    private static void createOutputFolders(String outputFolder) {
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
     * @param datacenter the Datacenter to get the PowerVmAllocationPolicyMigration
     * @return the PowerVmAllocationPolicyMigration or {@link PowerVmAllocationPolicyMigration#NULL}
     * if the associated policy is not an {@link PowerVmAllocationPolicyMigration}
     */
    private static PowerVmAllocationPolicyMigration getPowerVmAllocationPolicy(PowerDatacenter datacenter) {
        if(datacenter.getVmAllocationPolicy() instanceof PowerVmAllocationPolicyMigration){
            return (PowerVmAllocationPolicyMigration) datacenter
                .getVmAllocationPolicy();
        }

        return PowerVmAllocationPolicyMigration.NULL;
    }

    /**
	 * Parses the experiment name.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static String parseExperimentName(String name) {
		Scanner scanner = new Scanner(name);
		StringBuilder csvName = new StringBuilder();
		scanner.useDelimiter("_");
		for (int i = 0; i < 4; i++) {
			if (scanner.hasNext()) {
				csvName.append(scanner.next()).append(",");
			} else {
				csvName.append(",");
			}
		}
		scanner.close();
		return csvName.toString();
	}

	/**
	 * Gets the sla time per active host.
	 *
	 * @param hosts the hosts
	 * @return the sla time per active host
	 */
	protected static double getSlaTimePerActiveHost(List<HostSimple> hosts) {
		double slaViolationTimePerHost = 0;
		double totalTime = 0;

		for (HostSimple _host : hosts) {
			HostDynamicWorkloadSimple host = (HostDynamicWorkloadSimple) _host;
			double previousTime = -1;
			double previousAllocated = 0;
			double previousRequested = 0;
			boolean previousIsActive = true;

			for (HostStateHistoryEntry entry : host.getStateHistory()) {
				if (previousTime != -1 && previousIsActive) {
					double timeDiff = entry.getTime() - previousTime;
					totalTime += timeDiff;
					if (previousAllocated < previousRequested) {
						slaViolationTimePerHost += timeDiff;
					}
				}

				previousAllocated = entry.getAllocatedMips();
				previousRequested = entry.getRequestedMips();
				previousTime = entry.getTime();
				previousIsActive = entry.isActive();
			}
		}

		return slaViolationTimePerHost / totalTime;
	}

	/**
	 * Gets the sla time per host.
	 *
	 * @param hosts the hosts
	 * @return the sla time per host
	 */
	protected static double getSlaTimePerHost(List<HostSimple> hosts) {
		double slaViolationTimePerHost = 0;
		double totalTime = 0;

		for (HostSimple _host : hosts) {
			HostDynamicWorkloadSimple host = (HostDynamicWorkloadSimple) _host;
			double previousTime = -1;
			double previousAllocated = 0;
			double previousRequested = 0;

			for (HostStateHistoryEntry entry : host.getStateHistory()) {
				if (previousTime != -1) {
					double timeDiff = entry.getTime() - previousTime;
					totalTime += timeDiff;
					if (previousAllocated < previousRequested) {
						slaViolationTimePerHost += timeDiff;
					}
				}

				previousAllocated = entry.getAllocatedMips();
				previousRequested = entry.getRequestedMips();
				previousTime = entry.getTime();
			}
		}

		return slaViolationTimePerHost / totalTime;
	}

	/**
	 * Gets the sla metrics for a list of VMs.
	 *
	 * @param vms the vms
	 * @return the sla metrics map, compounded of metric names and their values.
	 */
	protected static Map<String, Double> getSlaMetrics(List<Vm> vms) {
		Map<String, Double> metrics = new HashMap<>(3);
		List<Double> slaViolation = new LinkedList<>();
		double totalAllocated = 0;
		double totalRequested = 0;
		double totalUnderAllocatedDueToMigration = 0;

		for (Vm vm : vms) {
			double vmTotalAllocated = 0;
			double vmTotalRequested = 0;
			double vmUnderAllocatedDueToMigration = 0;
			double previousTime = -1;
			double previousAllocated = 0;
			double previousRequested = 0;
			boolean previousIsInMigration = false;

			for (VmStateHistoryEntry entry : vm.getStateHistory()) {
				if (previousTime != -1) {
					double timeDiff = entry.getTime() - previousTime;
					vmTotalAllocated += previousAllocated * timeDiff;
					vmTotalRequested += previousRequested * timeDiff;

					if (previousAllocated < previousRequested) {
						slaViolation.add((previousRequested - previousAllocated) / previousRequested);
						if (previousIsInMigration) {
							vmUnderAllocatedDueToMigration += (previousRequested - previousAllocated)
									* timeDiff;
						}
					}
				}

				previousAllocated = entry.getAllocatedMips();
				previousRequested = entry.getRequestedMips();
				previousTime = entry.getTime();
				previousIsInMigration = entry.isInMigration();
			}

			totalAllocated += vmTotalAllocated;
			totalRequested += vmTotalRequested;
			totalUnderAllocatedDueToMigration += vmUnderAllocatedDueToMigration;
		}

		metrics.put("overall", (totalRequested - totalAllocated) / totalRequested);
		if (slaViolation.isEmpty()) {
			metrics.put("average", 0.);
		} else {
			metrics.put("average", MathUtil.mean(slaViolation));
		}
		metrics.put("underallocated_migration", totalUnderAllocatedDueToMigration / totalRequested);
		// metrics.put("sla_time_per_vm_with_migration", slaViolationTimePerVmWithMigration /
		// totalTime);
		// metrics.put("sla_time_per_vm_without_migration", slaViolationTimePerVmWithoutMigration /
		// totalTime);

		return metrics;
	}

	/**
	 * Write data column.
	 *
	 * @param data the data
	 * @param outputPath the output path
	 */
	public static void writeDataColumn(List<? extends Number> data, String outputPath) {
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
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Write data row.
	 *
	 * @param data the data
	 * @param outputPath the output path
	 */
	public static void writeDataRow(String data, String outputPath) {
		File file = new File(outputPath);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Write metric history.
	 *
	 * @param hosts the hosts
	 * @param vmAllocationPolicy the vm allocation policy
	 * @param outputPath the output path
	 */
	public static void writeMetricHistory(
			List<? extends HostSimple> hosts,
			PowerVmAllocationPolicyMigration vmAllocationPolicy,
			String outputPath) {
		// for (HostSimple host : hosts) {
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
	public static void printCloudletList(List<CloudletSimple> list) {
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
	 * @param hosts the hosts
	 * @param vmAllocationPolicy the vm allocation policy
	 */
	public static void printMetricHistory(
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
     * An private constructor to avoid class instantiation.
     */
	private Helper(){}
}
