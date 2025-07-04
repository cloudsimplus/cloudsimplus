package org.cloudsimplus.vms;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VmScaling;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.CustomerEntityAbstract;
import org.cloudsimplus.core.Machine;
import org.cloudsimplus.core.Startable;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.utilizationmodels.BootModel;

import java.util.*;

/**
 * A base class for implementing {@link Vm}s.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 8.3.0
 */
@Accessors(makeFinal = false) @Getter
public non-sealed abstract class VmAbstract extends CustomerEntityAbstract implements Vm {
    /**
     * Gets the Default RAM capacity (in MB) for creating VMs.
     * This value is used when the RAM capacity is not given in a VM constructor.
     */
    private static long defaultRamCapacity = 1024;

    /**
     * Gets the Default Bandwidth capacity (in Mbps) for creating VMs.
     * This value is used when the BW capacity is not given in a VM constructor.
     */
    private static long defaultBwCapacity = 100;

    /**
     * Gets the Default Storage capacity (in MB) for creating VMs.
     * This value is used when the Storage capacity is not given in a VM constructor.
     */
    private static long defaultStorageCapacity = 1024;

    protected final Processor processor;
    /**
     * @see #getStateHistory()
     */
    protected final List<VmStateHistoryEntry> stateHistory;
    protected final List<EventListener<VmHostEventInfo>> onMigrationStartListeners;
    protected final List<EventListener<VmHostEventInfo>> onMigrationFinishListeners;
    protected final List<EventListener<VmHostEventInfo>> onHostAllocationListeners;
    protected final List<EventListener<VmHostEventInfo>> onHostDeallocationListeners;
    protected final List<EventListener<VmHostEventInfo>> onUpdateProcessingListeners;
    protected final List<EventListener<VmDatacenterEventInfo>> onCreationFailureListeners;

    @Setter @NonNull
    private BootModel bootModel;
    protected List<ResourceManageable> resources;

    @Setter
    protected String description;
    protected long freePesNumber;
    protected long expectedFreePesNumber;
    @Setter  @NonNull
    protected MipsShare allocatedMips;
    @Setter  @NonNull
    protected MipsShare requestedMips;
    @Setter
    private String vmm;
    private Host host;
    private double timeZone;
    private double submissionDelay;
    @Setter @NonNull
    private VmGroup group;
    private boolean failed;
    private SimpleStorage storage;
    private VmRam ram;
    private Bandwidth bw;
    @NonNull
    private CloudletScheduler cloudletScheduler;
    private boolean created;
    @Setter
    private boolean inMigration;
    private HorizontalVmScaling horizontalScaling;
    private VerticalVmScaling ramVerticalScaling;
    private VerticalVmScaling bwVerticalScaling;
    private VerticalVmScaling peVerticalScaling;
    private VmResourceStats cpuUtilizationStats;

    /**
     * A copy constructor that creates a VM based on the configuration of another one.
     * The created VM will have the same MIPS capacity, number of PEs,
     * BW, RAM and size of the given VM, but a default {@link CloudletScheduler} and no {@link DatacenterBroker}.
     * @param sourceVm the VM to be cloned
     */
    public VmAbstract(final Vm sourceVm) {
        this(-1, (long)sourceVm.getMips(), sourceVm.getPesNumber());
        this.setBw(sourceVm.getBw().getCapacity())
            .setRam(sourceVm.getRam().getCapacity())
            .setSize(sourceVm.getStorage().getCapacity());
    }

    protected VmAbstract(final long id, final long mipsCapacity, final long pesNumber) {
        this(id, mipsCapacity, pesNumber, new CloudletSchedulerTimeShared());
    }

    protected VmAbstract(final long id, final long mipsCapacity, final long pesNumber, final CloudletScheduler cloudletScheduler) {
        super();
        setId(id);
        this.processor = new Processor(this, pesNumber, mipsCapacity);
        setMips(mipsCapacity);
        setPesNumber(pesNumber);
        setCloudletScheduler(cloudletScheduler);

        this.resources = new ArrayList<>(4);
        this.bootModel = BootModel.NULL;

        // Initializes the number of free PEs as the number of VM PEs
        this.freePesNumber = pesNumber;
        this.expectedFreePesNumber = pesNumber;

        this.allocatedMips = new MipsShare();
        this.requestedMips = new MipsShare();
        this.stateHistory = new LinkedList<>();
        this.onMigrationStartListeners = new ArrayList<>();
        this.onMigrationFinishListeners = new ArrayList<>();
        this.onHostAllocationListeners = new ArrayList<>();
        this.onHostDeallocationListeners = new ArrayList<>();
        this.onUpdateProcessingListeners = new ArrayList<>();
        this.onCreationFailureListeners = new ArrayList<>();
        mutableAttributesInit();
    }

    /**
     * Sets the Default RAM capacity (in MB) for creating VMs.
     * This value is used when the RAM capacity is not given in a VM constructor.
     */
    public static void setDefaultRamCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultRamCapacity = defaultCapacity;
    }

    /**
     * Sets the Default Bandwidth capacity (in Mbps) for creating VMs.
     * This value is used when the BW capacity is not given in a VM constructor.
     */
    public static void setDefaultBwCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultBwCapacity = defaultCapacity;
    }

    /**
     * Sets the Default Storage capacity (in MB) for creating VMs.
     * This value is used when the Storage capacity is not given in a VM constructor.
     */
    public static void setDefaultStorageCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultStorageCapacity = defaultCapacity;
    }

    /**
     * Accepts a Vm or {@code List<Vm>}. If a Vm is given, it is returned.
     * If a List is given, returns the first VM in that List.
     * @param vmOrList a single Vm object or a {@code List<Vm>}
     * @return the Vm object or the first Vm inside the List (according to the given parameter)
     * @param <T> the type of the parameter, which may be a Vm or {@code List<Vm>}
     */
    public static <T> VmAbstract getFirstVm(final T vmOrList) {
        return isVmList(vmOrList) ? ((List<VmAbstract>) vmOrList).get(0) : (VmAbstract) vmOrList;
    }

    /**
     * Accepts a Vm or {@code List<Vm>} and return a {@code List<Vm>}
     * @param vmOrList a single Vm object or a {@code List<Vm>}
     * @return a List with 1 Vm object if a Vm instance is given; or a {@code List<Vm>} if a List is given
     * @param <T> the type of the parameter, which may be a Vm or {@code List<Vm>}
     */
    public static <T> List<Vm> getList(final T vmOrList) {
        return isVmList(vmOrList) ? ((List<Vm>) vmOrList) : List.of((Vm) vmOrList);
    }

    /**
     * Checks if an object is a Vm or {@code List<Vm>}.
     * @param vmOrList a Vm {@code List<Vm>}
     * @return true if it's a {@code List<Vm>}, false otherwise
     * @param <T> the type of the parameter, which may be a Vm or {@code List<Vm>}
     */
    public static <T> boolean isVmList(final T vmOrList) {
        return vmOrList instanceof List<?>;
    }

    /**
     * Accepts a single Vm object or a {@code List<Vm>} and returns the number of Vms in the given object.
     * @param vmOrList a Vm {@code List<Vm>}
     * @return 1 if the given object is a single Vm object, {@link List#size()} if it's a List.
     * @param <T> the type of the parameter, which may be a Vm or {@code List<Vm>}
     */
    public static <T> int getVmCount(final T vmOrList) {
        return isVmList(vmOrList) ? ((List<Vm>)vmOrList).size() : 1;
    }

    protected void mutableAttributesInit() {
        this.description = "";
        setBroker(DatacenterBroker.NULL);
        setSubmissionDelay(0);
        setVmm("Xen");

        setInMigration(false);
        this.host = Host.NULL;
        setCloudletScheduler(new CloudletSchedulerTimeShared());

        this.setHorizontalScaling(HorizontalVmScaling.NULL);
        this.setRamVerticalScaling(VerticalVmScaling.NULL);
        this.setBwVerticalScaling(VerticalVmScaling.NULL);
        this.setPeVerticalScaling(VerticalVmScaling.NULL);

        cpuUtilizationStats = VmResourceStats.NULL;

        setRam(new VmRam(this, defaultRamCapacity));
        setBw(new Bandwidth(defaultBwCapacity));
        setStorage(new SimpleStorage(defaultStorageCapacity));
    }

    @Override
    public double updateProcessing(final MipsShare mipsShare) {
        return updateProcessing(getSimulation().clock(), mipsShare);
    }

    @Override
    public double updateProcessing(final double currentTime, @NonNull final MipsShare mipsShare) {
        if (!cloudletScheduler.isEmpty() || isStartingUp()) {
            setLastBusyTime(getSimulation().clock());
        }

        if(isStartupDelayed() && currentTime == getStartupCompletionTime())
            LOGGER.info("{}: {}: {} has completed booting up.", getSimulation().clockStr(), getClass().getSimpleName(), this);

        return isShuttingDown() ? Double.MAX_VALUE : processing(currentTime, mipsShare);
    }

    private double processing(final double currentTime, final MipsShare mipsShare) {
        notifyOnUpdateProcessingListeners();
        return isStartingUp() ? bootProcessing() : cloudletsProcessing(currentTime, mipsShare);
    }

    /**
     * Process the VM boot up.
     * @return the remaining startup time that indicates how longer the boot process will take
     * so that the vm processing is updated after that time.
     */
    private double bootProcessing() {
        final var dc = host.getDatacenter();
        final double nextProcessingTime = dc.getSchedulingInterval() > -1 ? dc.getSchedulingInterval() : getRemainingStartupTime();
        dc.schedule(nextProcessingTime, CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
        return nextProcessingTime;
    }

    private double cloudletsProcessing(final double currentTime, final MipsShare mipsShare) {
        final double nextSimulationDelay = cloudletScheduler.updateProcessing(currentTime, mipsShare);

        cpuUtilizationStats.add(currentTime);
        getBroker().requestIdleVmDestruction(this);
        if (nextSimulationDelay == Double.MAX_VALUE) {
            return nextSimulationDelay;
        }

        /* If the current time is some value with the decimals greater than x.0
         * (such as 45.1) and the next event delay is any integer number such as 5,
         * then the next simulation time would be 50.1.
         * At time 50.1 the utilization will be reduced due to the completion of the Cloudlet.
         * At time 50.0 the Cloudlet is still running, so there is some CPU utilization.
         * But since the next update would be only at time 50.1, the utilization
         * at time 50.0 wouldn't be collected to enable knowing the exact time
         * before the utilization drops.
         * The condition and computation below is used to ensure VM processing occurs
         * at time 50 and 50.1.
         */
        final double decimals = currentTime - (int) currentTime;
        return nextSimulationDelay - decimals < 0 ? nextSimulationDelay : nextSimulationDelay - decimals;
    }

    /**
     * Sets the current number of free PEs.
     *
     * @return the new free pes number
     */
    public Vm setFreePesNumber(long freePesNumber) {
        if (freePesNumber < 0) {
            freePesNumber = 0;
        }
        this.freePesNumber = Math.min(freePesNumber, getPesNumber());
        return this;
    }

    /**
     * Adds a given number of expected free PEs to the total number of expected free PEs.
     * This value is updated as cloudlets are assigned to VMs but not submitted to the broker for running yet.
     *
     * @param pesToAdd the number of expected free PEs to add
     */
    public Vm addExpectedFreePesNumber(final long pesToAdd) {
        return setExpectedFreePesNumber(expectedFreePesNumber + pesToAdd);
    }

    /**
     * Adds a given number of expected free PEs to the total number of expected free PEs.
     * This value is updated as cloudlets are assigned to VMs but not submitted to the broker for running yet.
     *
     * @param pesToRemove the number of expected free PEs to remove
     */
    public Vm removeExpectedFreePesNumber(final long pesToRemove) {
        return setExpectedFreePesNumber(expectedFreePesNumber - pesToRemove);
    }

    /**
     * Sets the expected free PEs number before the VM starts executing.
     *
     * @param expectedFreePes the expected free PEs number to set
     */
    private Vm setExpectedFreePesNumber(final long expectedFreePes) {
        this.expectedFreePesNumber = Math.max(expectedFreePes, 0);
        return this;
    }

    @Override
    public double getCpuPercentUtilization() {
        return getCpuPercentUtilization(getSimulation().clock());
    }

    @Override
    public double getCpuPercentUtilization(final double time) {
        return isStartingUp() ? bootModel.getCpuPercentUtilization() : cloudletScheduler.getAllocatedCpuPercent(time);
    }

    @Override
    public double getCpuPercentRequested() {
        return getCpuPercentRequested(getSimulation().clock());
    }

    @Override
    public double getCpuPercentRequested(final double time) {
        return cloudletScheduler.getRequestedCpuPercent(time);
    }

    @Override
    public double getHostCpuUtilization(final double time) {
        return host.getExpectedRelativeCpuUtilization(this, getCpuPercentUtilization(time));
    }

    @Override
    public double getExpectedHostCpuUtilization(final double vmCpuUtilizationPercent) {
        return host.getExpectedRelativeCpuUtilization(this, vmCpuUtilizationPercent);
    }

    @Override
    public double getHostRamUtilization() {
        return host.getRelativeRamUtilization(this);
    }

    @Override
    public double getHostBwUtilization() {
        return host.getRelativeBwUtilization(this);
    }

    @Override
    public double getTotalCpuMipsUtilization() {
        return getTotalCpuMipsUtilization(getSimulation().clock());
    }

    @Override
    public double getTotalCpuMipsUtilization(final double time) {
        return getCpuPercentUtilization(time) * getTotalMipsCapacity();
    }

    @Override
    public double getTotalCpuMipsRequested() {
        return getCurrentRequestedMips().totalMips();
    }

    @Override
    public MipsShare getCurrentRequestedMips() {
        // TODO This method is confusing, since there is a getRequestedMips() (created with lombok)
        if (isCreated()) {
            return isStartingUp() ? new MipsShare(processor, bootModel) : host.getVmScheduler().getRequestedMips(this);
        }

        return new MipsShare(processor);
    }

    @Override
    public long getCurrentRequestedBw() {
        if (isCreated()) {
            return (long)(cloudletScheduler.getCurrentRequestedBwPercentUtilization() * bw.getCapacity());
        }

        return bw.getCapacity();
    }

    @Override
    public double getTotalMipsCapacity() {
        return getMips() * getPesNumber();
    }

    @Override
    public long getCurrentRequestedRam() {
        if (isCreated()) {
            final double percent = isStartingUp() ? bootModel.getRamPercentUtilization() : cloudletScheduler.getCurrentRequestedRamPercentUtilization();
            return (long)(percent * ram.getCapacity());
        }

        return ram.getCapacity();
    }

    @Override
    protected void onStart(final double time) {/**/}

    @Override
    protected void onFinish(final double time) {
        notifyOnHostDeallocationListeners(host);
    }

    /**
     * Checks if the VM has ever started some Cloudlet.
     * @return true if the VM has started some Cloudlet, false otherwise
     */
    public boolean hasStartedSomeCloudlet() {
        return getLastBusyTime() > NOT_ASSIGNED;
    }

    @Override
    public double getMips() {
        return processor.getMips();
    }

    /**
     * Sets the individual MIPS capacity of any VM's PE, considering that all
     * PEs have the same capacity.
     *
     * @param mips the new mips for every VM's PE
     */
    protected final void setMips(final double mips) {
        processor.setMips(mips);
    }

    @Override
    public long getPesNumber() {
        return processor.getCapacity();
    }

    protected void setPesNumber(final long pesNumber) {
        processor.setCapacity(pesNumber);
    }

    /**
     * Sets a new {@link Ram} resource for the Vm.
     *
     * @param ram the Ram resource to set
     */
    private void setRam(@NonNull final VmRam ram) {
        this.ram = ram;
    }

    @Override
    public final Vm setRam(final long ramCapacity) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("RAM capacity can just be changed when the Vm was not created inside a Host yet.");
        }

        setRam(new VmRam(this, ramCapacity));
        return this;
    }

    /**
     * Sets a new {@link Bandwidth} resource for the Vm.
     *
     * @param bw the Bandwidth resource to set
     */
    private void setBw(@NonNull final Bandwidth bw) {
        this.bw = bw;
    }

    @Override
    public final Vm setBw(final long bwCapacity) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("Bandwidth capacity can just be changed when the Vm was not created inside a Host yet.");
        }
        setBw(new Bandwidth(bwCapacity));
        return this;
    }

    /**
     * Sets a new {@link SimpleStorage} resource for the Vm.
     *
     * @param storage the storage resource to set
     */
    protected void setStorage(@NonNull final SimpleStorage storage) {
        this.storage = storage;
    }

    @Override
    public final Vm setSize(final long size) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("Storage size can just be changed when the Vm was not created inside a Host yet.");
        }
        setStorage(new SimpleStorage(size));
        return this;
    }

    /**
     * Sets the PM that hosts the VM.
     *
     * @param host Host to run the VM
     * @return this Vm
     */
    public Vm setHost(@NonNull final Host host) {
        this.host = host;
        return this;
    }

    @Override
    public final Vm setCloudletScheduler(@NonNull final CloudletScheduler cloudletScheduler) {
        if (isCreated()) {
            throw new UnsupportedOperationException("CloudletScheduler can just be changed when the Vm was not created inside a Host yet.");
        }

        this.cloudletScheduler = cloudletScheduler;
        this.cloudletScheduler.setVm(this);
        return this;
    }

    /**
     * Notifies the listeners when the VM starts migration to a target Host.
     *
     * @param targetHost the Host the VM is migrating to
     */
    public void updateMigrationStartListeners(final Host targetHost) {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onMigrationStartListeners.size(); i++) {
            final var listener = onMigrationStartListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this, targetHost));
        }
    }

    /**
     * Notifies the listeners when the VM finishes migration to a target Host.
     *
     * @param targetHost the Host the VM has just migrated to
     */
    public void updateMigrationFinishListeners(final Host targetHost) {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onMigrationFinishListeners.size(); i++) {
            final var listener = onMigrationFinishListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this, targetHost));
        }
    }

    @Override
    public boolean isSuitableForCloudlet(final Cloudlet cloudlet) {
        return getPesNumber() >= cloudlet.getPesNumber() &&
            storage.getAvailableResource() >= cloudlet.getFileSize();
    }

    /**
     * Changes the created status of the Vm inside the Host.
     *
     * @param requestCreation true to indicate the VM was created inside the Host; false otherwise
     * @see #isCreated()
     */
    public void setCreated(final boolean requestCreation) {
        if (requestCreation && !this.created) {
            setCreationTime();
            setStartTime(getSimulation().clock());
            setShutdownBeginTime(NOT_ASSIGNED);
            this.setFailed(false);
            validateAndConfigureVmScaling(peVerticalScaling);
            validateAndConfigureVmScaling(ramVerticalScaling);
            validateAndConfigureVmScaling(bwVerticalScaling);
            validateAndConfigureVmScaling(horizontalScaling);
        }

        this.created = requestCreation;
    }

    @Override
    public Startable setStartTime(final double startTime) {
        cloudletScheduler.setPreviousTime(startTime);
        return super.setStartTime(startTime);
    }

    @Override
    public List<VmStateHistoryEntry> getStateHistory() {
        /*
         * @TODO Instead of using a list, this attribute would be a map, where the
         *       key can be the history time and the value the history itself. This
         *       way, if one wants to get the history for a given time, he/she doesn't
         *       have to iterate over the entire list to find the desired entry.
         */
        return Collections.unmodifiableList(stateHistory);
    }

    @Override
    public void addStateHistoryEntry(final VmStateHistoryEntry entry) {
        if (!stateHistory.isEmpty()) {
            final VmStateHistoryEntry previousState = stateHistory.get(stateHistory.size() - 1);
            if (previousState.getTime() == entry.getTime()) {
                stateHistory.set(stateHistory.size() - 1, entry);
                return;
            }
        }
        stateHistory.add(entry);
    }

    @Override
    public List<ResourceManageable> getResources() {
        if (getSimulation().isRunning() && resources.isEmpty()) {
            resources = Arrays.asList(ram, bw, storage, processor);
        }

        return Collections.unmodifiableList(resources);
    }

    @Override
    public ResourceManageable getResource(Class<? extends ResourceManageable> resourceClass) {
        if (Pe.class.isAssignableFrom(resourceClass) || Processor.class.isAssignableFrom(resourceClass)) {
            return processor;
        }

        return Vm.super.getResource(resourceClass);
    }

    @Override
    public Vm addOnHostAllocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        this.onHostAllocationListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnMigrationStartListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        onMigrationStartListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnMigrationFinishListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        onMigrationFinishListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnHostDeallocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onHostDeallocationListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnCreationFailureListener(@NonNull final EventListener<VmDatacenterEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onCreationFailureListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnUpdateProcessingListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onUpdateProcessingListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnCreationFailureListener(@NonNull final EventListener<VmDatacenterEventInfo> listener) {
        return onCreationFailureListeners.remove(listener);
    }

    @Override
    public boolean removeOnUpdateProcessingListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public boolean removeOnHostAllocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onHostAllocationListeners.remove(listener);
    }

    @Override
    public boolean removeOnHostDeallocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onHostDeallocationListeners.remove(listener);
    }

    @Override
    public void setFailed(final boolean failed) {
        this.failed = failed;

        if (failed) {
            setCloudletsToFailed();
        }
    }

    public void setCloudletsToFailed() {
        getBroker().getCloudletWaitingList()
            .stream()
            .filter(cl -> this.equals(cl.getVm()))
            .forEach(cl -> cl.setStatus(Cloudlet.Status.FAILED_RESOURCE_UNAVAILABLE));
    }

    @Override
    public boolean isWorking() {
        return !isFailed();
    }

    @Override
    public final void setSubmissionDelay(final double submissionDelay) {
        this.submissionDelay = MathUtil.nonNegative(submissionDelay, "submissionDelay");
    }

    @Override
    public boolean isSubmissionDelayed() {
        return submissionDelay > 0;
    }

    @Override
    public void notifyOnHostAllocationListeners() {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onHostAllocationListeners.size(); i++) {
            final var listener = onHostAllocationListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this));
        }
    }

    @Override
    public void notifyOnHostDeallocationListeners(@NonNull final Host deallocatedHost) {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onHostDeallocationListeners.size(); i++) {
            final var listener = onHostDeallocationListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this, deallocatedHost));
        }
    }

    /**
     * Notifies all registered listeners when the processing of the Vm is updated in its {@link Host}.
     */
    public void notifyOnUpdateProcessingListeners() {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onUpdateProcessingListeners.size(); i++) {
            final var listener = onUpdateProcessingListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this));
        }
    }

    @Override
    public void notifyOnCreationFailureListeners(@NonNull final Datacenter failedDatacenter) {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onCreationFailureListeners.size(); i++) {
            final var listener = onCreationFailureListeners.get(i);
            listener.update(VmDatacenterEventInfo.of(listener, this, failedDatacenter));
        }
    }

    @Override
    public boolean removeOnMigrationStartListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onMigrationStartListeners.remove(listener);
    }

    @Override
    public boolean removeOnMigrationFinishListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onMigrationFinishListeners.remove(listener);
    }

    @Override
    public final Vm setHorizontalScaling(final HorizontalVmScaling horizontalScaling) throws IllegalArgumentException {
        this.horizontalScaling = horizontalScaling;
        return this;
    }

    @Override
    public final Vm setRamVerticalScaling(final VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException {
        this.ramVerticalScaling = ramVerticalScaling;
        return this;
    }

    @Override
    public final Vm setBwVerticalScaling(final VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException {
        this.bwVerticalScaling = bwVerticalScaling;
        return this;
    }

    @Override
    public final Vm setPeVerticalScaling(final VerticalVmScaling peVerticalScaling) throws IllegalArgumentException {
        this.peVerticalScaling = peVerticalScaling;
        return this;
    }

    private void validateAndConfigureVmScaling(@NonNull final VmScaling vmScaling) {
        if (vmScaling.getVm() != null && vmScaling.getVm() != NULL && vmScaling.getVm() != this) {
            final var name = vmScaling.getClass().getSimpleName();
            final var msg = "The %1$s given is already linked to a Vm. " +
                            "Each Vm must have its own %1$s object or none at all. " +
                            "Another %1$s has to be provided for this Vm.";
            throw new IllegalArgumentException(msg.formatted(name));
        }

        vmScaling.setVm(this);
        this.addOnUpdateProcessingListener(vmScaling::requestUpScalingIfPredicateMatches);
    }

    @Override
    public void enableUtilizationStats() {
        if (cpuUtilizationStats == null || cpuUtilizationStats == VmResourceStats.NULL) {
            this.cpuUtilizationStats = new VmResourceStats(this, vm -> vm.getCpuPercentUtilization(getSimulation().clock()));
        }
    }

    @Override
    public Vm setTimeZone(final double timeZone) {
        this.timeZone = validateTimeZone(timeZone);
        return this;
    }

    @Override
    public boolean isFinished() {
        return !created;
    }

    public Ram getRam() {
        return this.ram;
    }

    public Vm setDescription(final String description) {
        this.description = description;
        return this;
    }

    public Vm setVmm(final String vmm) {
        this.vmm = vmm;
        return this;
    }

    public Vm setGroup(final VmGroup group) {
        this.group = group;
        return this;
    }

    public Vm setInMigration(final boolean inMigration) {
        this.inMigration = inMigration;
        return this;
    }

    public Vm setAllocatedMips(final MipsShare allocatedMips) {
        this.allocatedMips = allocatedMips;
        return this;
    }

    public Vm setRequestedMips(final MipsShare requestedMips) {
        this.requestedMips = requestedMips;
        return this;
    }

    @Override
    public void shutdown() {
        final var lifeTimeMsg = this.isLifeTimeReached() ? " after reaching defined lifetime" : "";
        final var shutDownMsg = this.isShutDownDelayed()
                                        ? "expected to finish in %.2f seconds".formatted(this.getShutDownDelay())
                                        : "will finish immediately (since no Vm shutDownDelay was set)";
        this.setShutdownBeginTime(getSimulation().clock());
        LOGGER.info(
            "{}: {}: Requesting {} destruction on {}{}. Shutdown {}.",
            getSimulation().clockStr(), getClass().getSimpleName(), this, this.host, lifeTimeMsg, shutDownMsg);
        final var dc = host.getDatacenter();
        dc.schedule(dc, this.getShutDownDelay(), CloudSimTag.VM_DESTROY, this);
    }
}
