/*
 */
package hostFaultInjection;

import static hostFaultInjection.HostFaultInjectionRunner.CLOUDLETS;
import static hostFaultInjection.HostFaultInjectionRunner.VMS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.PoissonDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.testbeds.SimulationExperiment;

/**
 *
 * @author raysaoliveira
 */
public final class HostFaultInjectionExperiment extends SimulationExperiment {

    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 300;

    private static final int HOST_PES = 8;
    private static final long HOST_RAM = 500000; //host memory (MEGABYTE)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;
    private List<Host> hostList;

    private static final int VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MEGABYTE)
    private static final int VM_RAM = 10000; //vm memory (MEGABYTE)
    private static final long VM_BW = 100000;
    private static final int VM_PES = 2; //number of cpus

    private static final int CLOUDLET_PES = 2;
    private static final long CLOUDLET_LENGHT = 4800_000_000L;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * Number of Hosts to create for each Datacenter. The number of elements in
     * this array defines the number of Datacenters to be created.
     */
    private static final int HOSTS = 10;
    private static final int BROKERS = 2;


    private HostFaultInjection fault;

    private DatacenterBrokerSimple broker;

    public HostFaultInjectionExperiment() {
        super();
        setNumBrokersToCreate(BROKERS);
        setAfterScenarioBuild(exp -> createFaultInjectionForHosts(getDatacenter0()));
    }

    public double getAvailability() {
        return fault.availability() * 100;
    }

    @Override
    protected List<Vm> createVms() {
        List<Vm> list = new ArrayList<>(VMS);
        final int id = getVmList().size();
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm(id+i);
            vm.setBroker(broker);
            list.add(vm);
        }
        return list;

    }

    public Vm createVm(int id) {
        Vm vm = new VmSimple(id, VM_MIPS, VM_PES);
        vm
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
                .setBroker(broker)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates the number of Cloudlets defined in {@link HostFaultInjectionRunner#CLOUDLETS} and submits
     * them to the given broker.
     *
     * @param broker the broker to submit cloudlets to
     * @return the List of created Cloudlets
     */
    public Cloudlet createCloudlet(int id, DatacenterBroker broker) {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet c
                = new CloudletSimple(id, CLOUDLET_LENGHT, CLOUDLET_PES)
                        .setFileSize(CLOUDLET_FILESIZE)
                        .setOutputSize(CLOUDLET_OUTPUTSIZE)
                        .setBroker(broker)
                        .setUtilizationModel(utilizationModel);
        return c;
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        final int id = getCloudletList().size();
        for (int i = 0; i < CLOUDLETS; i++) {
            list.add(createCloudlet(id+i, broker));
        }

        return list;
    }

    @Override
    protected DatacenterSimple createDatacenter() {
        DatacenterSimple dc = super.createDatacenter();
        dc.setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    @Override
    protected List<Host> createHosts() {
        hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }
        return hostList;

    }

    /**
     * Creates a Host.
     *
     * @return
     */
    public Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(8000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        return new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

    public List<Pe> createPeList(int numberOfPEs, long mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for (int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return list;
    }

    /**
     * Creates the fault injection for host
     *
     * @param datacenter
     */
    private void createFaultInjectionForHosts(Datacenter datacenter) {
        //final long seed = System.currentTimeMillis();
        final long seed = 83787384734L;
        /*The average number of failures expected to happen each minute
        in a Poisson Process, which is also called event rate or rate parameter.*/
        final double meanFailureNumberPerMinute = 0.0009;
        PoissonDistr poisson = new PoissonDistr(meanFailureNumberPerMinute, seed);

        fault = new HostFaultInjection(datacenter, poisson);

        for (DatacenterBroker broker : getBrokerList()) {
            Vm lastVmFromBroker = broker.getWaitingVm(broker.getVmsWaitingList().size() - 1);
            fault.addVmCloner(broker, this::cloneVm);
            fault.addCloudletsCloner(broker, this::cloneCloudlets);
        }

        Log.printFormattedLine(
                "\tFault Injection created for %s.\n\tMean Number of Failures per Minute: %.6f (1 failure expected at each %.2f minutes).",
                datacenter, meanFailureNumberPerMinute, poisson.getInterarrivalMeanTime());
    }

    /**
     * Clones a VM by creating another one with the same configurations of a
     * given VM.
     *
     * @param vm the VM to be cloned
     * @return the cloned (new) VM.
     *
     * @see
     * #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private Vm cloneVm(Vm vm) {
        Vm clone = new VmSimple((long) vm.getMips(), (int) vm.getNumberOfPes());
        /*It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the vm to its clone,
        since the clone ID will be 10 times the id of its
        source VM.*/
        clone.setId(vm.getId() * 10);
        clone.setDescription("Clone of VM " + vm.getId());
        clone
                .setSize(vm.getStorage().getCapacity())
                .setBw(vm.getBw().getCapacity())
                .setRam(vm.getBw().getCapacity())
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        Log.printFormattedLine("\n\n#Cloning VM %d from Host %d\n\tMips %.2f Number of Pes: %d ",
                vm.getId(), vm.getHost().getId(), clone.getMips(), clone.getNumberOfPes());

        return clone;
    }

    /**
     * Clones each Cloudlet associated to a given VM. The method is called when
     * a VM is destroyed due to a Host failure and a snapshot from that VM (a
     * clone) is started into another Host. In this case, all the Cloudlets
     * which were running inside the destroyed VM will be recreated from scratch
     * into the VM clone, re-starting their execution from the beginning.
     *
     * @param sourceVm the VM to clone its Cloudlets
     * @return the List of cloned Cloudlets.
     * @see
     * #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private List<Cloudlet> cloneCloudlets(Vm sourceVm) {
        final List<Cloudlet> sourceVmCloudlets = sourceVm.getCloudletScheduler().getCloudletList();
        final List<Cloudlet> clonedCloudlets = new ArrayList<>(sourceVmCloudlets.size());
        for (Cloudlet cl : sourceVmCloudlets) {
            clonedCloudlets.add(cloneCloudlet(cl, cl.getLength()-cl.getFinishedLengthSoFar()));
        }

        return clonedCloudlets;
    }

    /**
     * Creates a clone from a given Cloudlet.
     *
     * @param source the Cloudlet to be cloned.
     * @param length
     * @return the cloned (new) cloudlet
     */
    private Cloudlet cloneCloudlet(Cloudlet source, long length) {
        Cloudlet clone
                = new CloudletSimple(length, (int) source.getNumberOfPes());
        /*It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the cloudlet to its clone,
        since the clone ID will be 10 times the id of its
        source cloudlet.*/
        clone.setId(source.getId() * 10);
        clone
                .setUtilizationModelBw(source.getUtilizationModelBw())
                .setUtilizationModelCpu(source.getUtilizationModelCpu())
                .setUtilizationModelRam(source.getUtilizationModelRam());
        return clone;
    }

    @Override
    public void printResults() {
        for(DatacenterBroker broker: getBrokerList()){
            new CloudletsTableBuilder(broker.getCloudletsFinishedList()).build();
        }
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudSim());
    }

    public double getRatioVmsPerHost(){
        double vmsize = getVmList().size();
        double hostsize = getHostList().size();
        return vmsize / hostsize;
    }

    /**
     * A main method just for test purposes.
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        HostFaultInjectionExperiment exp
                = new HostFaultInjectionExperiment();
        exp.setVerbose(true);
        exp.run();
        System.out.println("# Ratio VMS per HOST: " + exp.getRatioVmsPerHost());
        System.out.println("\n# Number of Host faults: " + exp.fault.getNumberOfHostFaults());
        System.out.println("# Number of VM faults (VMs destroyed): " + exp.fault.getNumberOfDestroyedVms());
        Log.printFormattedLine("# Time that the simulations finished: %.2f minutes", exp.getCloudSim().clockInMinutes());
        Log.printFormattedLine("# VMs MTTR: %.2f minutes", exp.fault.meanTimeToRepairVmFaultsInMinutes());
        Log.printFormattedLine("# VMs MTBF: %.2f minutes", exp.fault.meanTimeBetweenVmFaultsInMinutes());
        Log.printFormattedLine("# Hosts MTBF: %.2f minutes", exp.fault.meanTimeBetweenHostFaultsInMinutes());
        Log.printFormattedLine("# Availability: %.2f%%", exp.fault.availability()*100);

    }
}
