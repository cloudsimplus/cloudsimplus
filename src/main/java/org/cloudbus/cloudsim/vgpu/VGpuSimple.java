package org.cloudbus.cloudsim.gp.vgpu;

import org.cloudbus.cloudsim.gp.vms.GpuVm;
import org.cloudbus.cloudsim.gp.resources.Gpu;
import org.cloudbus.cloudsim.gp.vms.GpuVmNull;
import org.cloudbus.cloudsim.gp.vms.GpuVmSimple;
import org.cloudbus.cloudsim.gp.resources.VGpuCore;
import org.cloudbus.cloudsim.gp.videocards.Videocard;
import org.cloudbus.cloudsim.gp.cloudlets.GpuCloudlet;
import org.cloudbus.cloudsim.gp.cloudlets.gputasks.GpuTask;
import org.cloudbus.cloudsim.gp.schedulers.gputask.GpuTaskScheduler;
import org.cloudbus.cloudsim.gp.schedulers.gputask.GpuTaskSchedulerTimeShared;

import org.gpucloudsimplus.listeners.VGpuVideocardEventInfo;
import org.gpucloudsimplus.listeners.VGpuGpuEventInfo;
import org.cloudsimplus.listeners.EventListener;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.resources.Bandwidth;
//import org.cloudbus.cloudsim.resources.Processor;
import org.cloudbus.cloudsim.schedulers.MipsShare;
import org.cloudbus.cloudsim.resources.ResourceManageable;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class VGpuSimple implements VGpu {

	private static long defaultGddramCapacity = 1024;
    private static long defaultBwCapacity = 100;
    
	private long id;
	private String type;
	private String tenancy;
	private int PCIeBw;
	
	private GpuVm gpuVm;
	
	private Ram gddram;
	private Bandwidth bw;
	private VGpuCore vGpuCore;

	private Gpu gpu;
	
	private long freeCoresNumber;
    private long expectedFreeCoresNumber;
	
	private final List<VGpuStateHistoryEntry> vGpuStateHistory;
	
	private VGpuResourceStats gpuUtilizationStats;
	//private HorizontalVmScaling horizontalScaling;
	
    private boolean failed;
    private boolean created;
	private boolean inMigration;

    private List<ResourceManageable> resources;

    private GpuTaskScheduler gpuTaskScheduler;
    
    //private double submissionDelay;
    
    private final List<EventListener<VGpuGpuEventInfo>> onMigrationStartListeners;
    private final List<EventListener<VGpuGpuEventInfo>> onMigrationFinishListeners;
    private final List<EventListener<VGpuGpuEventInfo>> onGpuAllocationListeners;
    private final List<EventListener<VGpuGpuEventInfo>> onGpuDeallocationListeners;
    private final List<EventListener<VGpuGpuEventInfo>> onUpdateProcessingListeners;
    
    private final List<EventListener<VGpuVideocardEventInfo>> onCreationFailureListeners;

    /*private VerticalVmScaling ramVerticalScaling;
    private VerticalVmScaling bwVerticalScaling;
    private VerticalVmScaling peVerticalScaling;*/

    private String description;
    private double startTime;//can use from gpuvm or must be initialize wherever gpuvms starttime initialized
    private double stopTime;//can use from gpuvm
    private double lastBusyTime;
    //private VmGroup group;
    //private double timeZone;
    private MipsShare allocatedMips;
    private MipsShare requestedMips;
	
    public VGpuSimple(final VGpu sourceVGpu) {
        this(sourceVGpu.getMips(), sourceVGpu.getNumberOfCores());
        this.setBw(sourceVGpu.getBw().getCapacity())
            .setGddram(sourceVGpu.getGddram().getCapacity());
            //.setSize(sourceVGpu.getStorage().getCapacity());
    }
    
    public VGpuSimple(final double mips, final long numberOfCores) {
        this(-1, "", "", -1, (long)mips, numberOfCores);
    }
    
    public VGpuSimple(final double mips, final long numberOfCores, 
    		final GpuTaskScheduler gpuTaskScheduler) {
        this(-1, "", "", -1, (long)mips, numberOfCores);
        setGpuTaskScheduler(gpuTaskScheduler);
    }
    
    public VGpuSimple(final long id, final String type, final String tenancy, 
			final int PCIeBw, final double mips, final long numberOfCores) {
        this(id, type, tenancy, PCIeBw,(long)mips, numberOfCores);
    }
    
    //gddram, bw, scheduler, 
	public VGpuSimple (final long id, final String type, final String tenancy, 
			final int PCIeBw, final long mips, final long numberOfCores) {
		setId(id);
		setType(type);
		setTenancy(tenancy);
		setPCIeBw(PCIeBw);
        this.resources = new ArrayList<>(4);
        this.onMigrationStartListeners = new ArrayList<>();
        this.onMigrationFinishListeners = new ArrayList<>();
        this.onGpuAllocationListeners = new ArrayList<>();
        this.onGpuDeallocationListeners = new ArrayList<>();
        this.onCreationFailureListeners = new ArrayList<>();
        this.onUpdateProcessingListeners = new ArrayList<>();
        this.vGpuStateHistory = new LinkedList<>();
        this.allocatedMips = new MipsShare();
        this.requestedMips = new MipsShare();
        
        this.vGpuCore = new VGpuCore(this, mips, numberOfCores);
        setMips(mips);                           
        setNumberOfCores(numberOfCores);
        
        mutableAttributesInit();
        
        freeCoresNumber = numberOfCores;
        expectedFreeCoresNumber = numberOfCores;
	}
	
	private void mutableAttributesInit () {
        this.description = "";
        this.startTime = -1;
        this.stopTime = -1;
        this.lastBusyTime = Double.MAX_VALUE;
        //setBroker(DatacenterBroker.NULL);
        //setSubmissionDelay(0);
        //setVmm("Xen");

        setInMigration(false);
        this.gpu = Gpu.NULL;
        setGpuTaskScheduler(new GpuTaskSchedulerTimeShared());

        //this.setHorizontalScaling(HorizontalVmScaling.NULL);
        //this.setRamVerticalScaling(VerticalVmScaling.NULL);
        //this.setBwVerticalScaling(VerticalVmScaling.NULL);
        //this.setPeVerticalScaling(VerticalVmScaling.NULL);

        //gpuUtilizationStats = VGpuResourceStats.NULL;

        setRam(new Ram(defaultGddramCapacity));
        setBw(new Bandwidth(defaultBwCapacity));
        //setStorage(new SimpleStorage(defaultStorageCapacity));
    }

	@Override
	public double updateGpuTaskProcessing (MipsShare mipsShare) {
		return updateGpuTaskProcessing (getSimulation().clock(), mipsShare);
	}
	
	@Override
	public double updateGpuTaskProcessing (double currentTime, MipsShare mipsShare) {
		requireNonNull(mipsShare);

        if (!gpuTaskScheduler.isEmpty()) {
            setLastBusyTime();
        }
        final double nextSimulationDelay = gpuTaskScheduler.updateProcessing(currentTime, 
        		mipsShare);
        notifyOnUpdateProcessingListeners();

        //gpuUtilizationStats.add(currentTime);
        //getBroker().requestIdleVmDestruction(this);
        if (nextSimulationDelay == Double.MAX_VALUE) {
            return nextSimulationDelay;
        }
        
        final double decimals = currentTime - (int) currentTime;
        return nextSimulationDelay - decimals < 0 ? nextSimulationDelay : nextSimulationDelay - decimals;
	}
	
	@Override
    public long getFreeCoresNumber () {
        return freeCoresNumber;
    }
	
	public VGpu setFreeCoresNumber (long freeCoresNumber) {
        if (freeCoresNumber < 0) {
            freeCoresNumber = 0;
        }
        this.freeCoresNumber = Math.min (freeCoresNumber, getNumberOfCores());
        return this;
    }
	
	@Override
    public long getExpectedFreeCoresNumber () {
        return expectedFreeCoresNumber;
    }
	
	public VGpu addExpectedFreeCoresNumber (final long coresToAdd) {
        return setExpectedFreeCoresNumber (expectedFreeCoresNumber + coresToAdd);
    }
	
	public VGpu removeExpectedFreeCoresNumber (final long coresToRemove) {
        return setExpectedFreeCoresNumber (expectedFreeCoresNumber - coresToRemove);
    }
	
	private VGpu setExpectedFreeCoresNumber(long expectedFreeCores) {
        if (expectedFreeCores < 0) {
            expectedFreeCores = 0;
        }
        this.expectedFreeCoresNumber = expectedFreeCores;
        return this;
    }
	
	@Override
	public double getCorePercentUtilization (double time) {
		return gpuTaskScheduler.getAllocatedGpuPercent(time);
	}

	@Override
    public double getCorePercentUtilization () {
		return getCorePercentUtilization(getSimulation().clock());
	}
	
	@Override
	public double getCorePercentRequested (double time) {
		return gpuTaskScheduler.getRequestedGpuPercent(time);
	}

	@Override
    public double getCorePercentRequested () {
		return getCorePercentRequested(getSimulation().clock());
	}
	
	@Override
    public double getGpuCoreUtilization (final double time) {
        return gpu.getExpectedRelativeGpuUtilization(this, getGpuPercentUtilization(time));
    }
	
	@Override
    public double getExpectedGpuCoreUtilization (final double vgpuCoreUtilizationPercent) {
        return gpu.getExpectedRelativeGpuUtilization(this, vgpuCoreUtilizationPercent);
    }
	
	@Override
    public double getGpuGddramUtilization () {
        return gpu.getRelativeGddramUtilization(this);
    }

    @Override
    public double getGpuBwUtilization () {
        return gpu.getRelativeBwUtilization(this);
    }

    @Override
    public double getTotalCoreMipsUtilization () {
        return getTotalCoreMipsUtilization(getSimulation().clock());
    }

    @Override
    public double getTotalCoreMipsUtilization (final double time) {
        return getCorePercentUtilization(time) * getTotalMipsCapacity();
    }
    
    @Override
    public double getTotalGpuMipsRequested () {
        return getCurrentRequestedMips().totalMips();
    }
    
    @Override
    public MipsShare getCurrentRequestedMips () {
        if (isCreated()) {
            return gpu.getVGpuScheduler().getRequestedMips(this);
        }

        return new MipsShare(vGpuCore.getCapacity(), vGpuCore.getMips());
    }
    
    @Override
    public long getCurrentRequestedBw () {
        if (!isCreated()) {
            return bw.getCapacity();
        }

        return (long) (gpuTaskScheduler.getCurrentRequestedBwPercentUtilization() * 
        		bw.getCapacity());
    }
    
    @Override
    public double getTotalMipsCapacity () {
        return getMips() * getNumberOfCores();
    }
    
    @Override
    public long getCurrentRequestedGddram () {
        if (!isCreated()) {
            return gddram.getCapacity();
        }

        return (long) (gpuTaskScheduler.getCurrentRequestedGddramPercentUtilization() * 
        		gddram.getCapacity());
    }
    
    @Override
    public double getStartTime () {
        return this.startTime;
    }
    
    @Override
    public VGpu setStartTime (final double startTime) {
        if (startTime < 0) {
            return this;
        }

        this.startTime = startTime;
        setLastBusyTime(startTime);
        return this;
    }
    
    @Override
    public double getStopTime () {
        return this.stopTime;
    }
    
    @Override
    public VGpu setStopTime (final double stopTime) {
        this.stopTime = Math.max(stopTime, -1);
        return this;
    }

    @Override
    public double getLastBusyTime () {
        return this.lastBusyTime;
    }
    
    public boolean hasStartedSomeGpuTask () {
        return lastBusyTime != Double.MAX_VALUE;
    }
    
    private void setLastBusyTime () {
        this.lastBusyTime = getSimulation().clock();
    }

    private void setLastBusyTime (final double time) {
        this.lastBusyTime = time;
    }
    
    @Override
    public double getTotalExecutionTime () {
        if (startTime < 0) {
            return 0;
        }

        return stopTime < 0 ? getSimulation().clock() - startTime : stopTime - startTime;
    }

    @Override
    public double getMips () {
        return vGpuCore.getMips();
    }
    
    protected final void setMips (final double mips) {
    	vGpuCore.setMips(mips);
    }
    
    @Override
    public long getNumberOfCores () {
        return vGpuCore.getCapacity();
    }
    
    private void setNumberOfCores (final long numberOfCores) {
    	vGpuCore.setCapacity(numberOfCores);
    }

    @Override
    public VGpuCore getVGpuCore () {
        return vGpuCore;
    }

    @Override
    public Ram getGddram () {
        return gddram;
    }
    
    private void setRam (final Ram ram) {
        this.gddram = requireNonNull(ram);
    }
    
    @Override
    public final VGpu setGddram (final long gddramCapacity) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("gddram capacity can just be changed when the vgpu was not created inside a videocard yet.");
        }

        setRam(new Ram(gddramCapacity));
        return this;
    }

    @Override
    public Bandwidth getBw () {
        return bw;
    }
    
    private void setBw (final Bandwidth bw) {
        this.bw = requireNonNull(bw);
    }

    @Override
    public final VGpu setBw (final long bwCapacity) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("Bandwidth capacity can just be changed when the vgpu was not created inside a videocard yet.");
        }
        setBw(new Bandwidth(bwCapacity));
        return this;
    }

    /*@Override
    public Resource getStorage() {
        return storage;
    }
    
    private void setStorage(final SimpleStorage storage) {
        this.storage = requireNonNull(storage);
    }

    @Override
    public final Vm setSize(final long size) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("Storage size can just be changed when the Vm was not created inside a Host yet.");
        }
        setStorage(new SimpleStorage(size));
        return this;
    }

    @Override
    public String getVmm() {
        return vmm;
    }
    
    protected final void setVmm(final String vmm) {
        this.vmm = vmm;
    }*/
    
    @Override
    public VGpu setGpu (final Gpu gpu) {
        if (Gpu.NULL.equals(requireNonNull(gpu)))  {
            setCreated(false);
        }

        this.gpu = gpu;
        return this;
    }

    @Override
    public Gpu getGpu () {
        return gpu;
    }
    
    @Override
    public GpuTaskScheduler getGpuTaskScheduler () {
        return gpuTaskScheduler;
    }

    @Override
    public final VGpu setGpuTaskScheduler (final GpuTaskScheduler gpuTaskScheduler) {
        requireNonNull(gpuTaskScheduler);
        if (isCreated()) {
            throw new UnsupportedOperationException("GpuTaskScheduler can just be changed when the vgpu was not created inside a Videocard yet.");
        }

        this.gpuTaskScheduler = gpuTaskScheduler;
        this.gpuTaskScheduler.setVGpu(this);
        return this;
    }
    
    @Override
    public boolean isInMigration () {
        return inMigration;
    }

    @Override
    public final void setInMigration (final boolean migrating) {
        this.inMigration = migrating;
    }
    
    public void updateMigrationStartListeners (final Gpu targetGpu) {
        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onMigrationStartListeners.size(); i++) {
            final EventListener<VGpuGpuEventInfo> listener = 
            		onMigrationStartListeners.get(i);
            listener.update(VGpuGpuEventInfo.of(listener, this, targetGpu));
        }
    }
    
    public void updateMigrationFinishListeners (final Gpu targetGpu) {
        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onMigrationFinishListeners.size(); i++) {
            final EventListener<VGpuGpuEventInfo> listener = 
            		onMigrationFinishListeners.get(i);
            listener.update(VGpuGpuEventInfo.of(listener, this, targetGpu));
        }
    }
    
    @Override
    public final boolean isCreated () {
        return created;
    }
    
    @Override
    public boolean isSuitableForGpuTask (final GpuTask gpuTask) {
        return getNumberOfCores() >= gpuTask.getNumberOfCores() &&
            gpuVm.getStorage().getAvailableResource() >= gpuTask.getFileSize();
    }
    
    @Override
    public void setCreated (final boolean created) {
        /*if(!this.created && created){
            setCreationTime();
        }*/

        this.created = created;
        this.setFailed(false);
    }
    
    @Override
    public List<VGpuStateHistoryEntry> getStateHistory () {
        return Collections.unmodifiableList(vGpuStateHistory);
    }
    
    @Override
    public void addStateHistoryEntry (final VGpuStateHistoryEntry entry) {
        if (!vGpuStateHistory.isEmpty()) {
            final VGpuStateHistoryEntry previousState = vGpuStateHistory.get(vGpuStateHistory.size() - 1);
            if (previousState.getTime() == entry.getTime()) {
            	vGpuStateHistory.set(vGpuStateHistory.size() - 1, entry);
                return;
            }
        }
        vGpuStateHistory.add(entry);
    }
    
    //@Override
    public List<ResourceManageable> getResources () {
        if (getSimulation().isRunning() && resources.isEmpty()) {
            resources = Arrays.asList(gddram, bw, vGpuCore);//storage
        }

        return Collections.unmodifiableList(resources);
    }
    
    //@Override
    public ResourceManageable getResource (Class<? extends ResourceManageable> resourceClass) {
        if(Pe.class.isAssignableFrom(resourceClass) || 
        		VGpuCore.class.isAssignableFrom(resourceClass)) {
            return vGpuCore;
        }

        //return Vm.super.getResource(resourceClass);
        return getResources()
                .stream()
                .filter(resource -> resource.isSubClassOf(resourceClass))
                .findFirst()
                .orElse(ResourceManageable.NULL);
    }

    @Override
    public VGpu addOnGpuAllocationListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        this.onGpuAllocationListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public VGpu addOnMigrationStartListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        onMigrationStartListeners.add(requireNonNull(listener));
        return this;
    }
    
    @Override
    public VGpu addOnMigrationFinishListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        onMigrationFinishListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public VGpu addOnGpuDeallocationListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onGpuDeallocationListeners.add(requireNonNull(listener));
        return this;
    }
    
    @Override
    public VGpu addOnCreationFailureListener(final EventListener<VGpuVideocardEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onCreationFailureListeners.add(requireNonNull(listener));
        return this;
    }
    
    @Override
    public VGpu addOnUpdateProcessingListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onUpdateProcessingListeners.add(requireNonNull(listener));
        return this;
    }
    
    /*@Override
    public boolean removeOnCreationFailureListener(final EventListener<VmDatacenterEventInfo> listener) {
        return onCreationFailureListeners.remove(requireNonNull(listener));
    }*/
    
    @Override
    public boolean removeOnUpdateProcessingListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        return onUpdateProcessingListeners.remove(requireNonNull(listener));
    }

    @Override
    public boolean removeOnGpuAllocationListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        return onGpuAllocationListeners.remove(requireNonNull(listener));
    }

    @Override
    public boolean removeOnGpuDeallocationListener(
    		final EventListener<VGpuGpuEventInfo> listener) {
        return onGpuDeallocationListeners.remove(requireNonNull(listener));
    }

    /*@Override
    public String toString() {
        final String desc = StringUtils.isBlank(description) ? "" : String.format(" (%s)", description);
        final String type = this instanceof VmGroup ? "VmGroup" : "Vm";
        return String.format("%s %d%s", type, getId(), desc);
    }*/

    @Override
    public int compareTo(final VGpu obj) {
        if(this.equals(requireNonNull(obj))) {
            return 0;
        }

        return Double.compare(getTotalMipsCapacity(), obj.getTotalMipsCapacity()) +
               Long.compare(this.getId(), obj.getId()) +
               this.getGpuVm().getBroker().compareTo(obj.getGpuVm().getBroker());
    }
    
    @Override
    public void setFailed(final boolean failed) {
        this.failed = failed;

        if(failed) {
            setGpuTasksToFailed();
        }
    }
    
    public void setGpuTasksToFailed() {
    	getGpuVm().getBroker().getCloudletWaitingList().stream()
    				.filter(cl -> getGpuVm().equals(cl.getVm()))
    		        .forEach(cl -> cl.setStatus(
    		        		GpuCloudlet.Status.FAILED_RESOURCE_UNAVAILABLE));

        /*getBroker().getCloudletWaitingList()
                   .stream()
                   .filter(cl -> this.equals(cl.getVm()))
                   .forEach(cl -> cl.setStatus(Cloudlet.Status.FAILED_RESOURCE_UNAVAILABLE));*/
    }
    
    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public boolean isWorking() {
        return !isFailed();
    }
    
    /*@Override
    public double getSubmissionDelay() {
        return this.submissionDelay;
    }

    @Override
    public final void setSubmissionDelay(final double submissionDelay) {
        if (submissionDelay < 0) {
            return;
        }

        this.submissionDelay = submissionDelay;
    }
    
    @Override
    public boolean isDelayed() {
        return submissionDelay > 0;
    }*/
    
    @Override
    public void notifyOnGpuAllocationListeners () {
        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onGpuAllocationListeners.size(); i++) {
            final EventListener<VGpuGpuEventInfo> listener = 
            		onGpuAllocationListeners.get(i);
            listener.update(VGpuGpuEventInfo.of(listener, this));
        }
    }

    @Override
    public void notifyOnGpuDeallocationListeners (final Gpu deallocatedGpu) {
        requireNonNull(deallocatedGpu);
        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onGpuDeallocationListeners.size(); i++) {
            final EventListener<VGpuGpuEventInfo> listener = 
            		onGpuDeallocationListeners.get(i);
            listener.update(VGpuGpuEventInfo.of(listener, this, deallocatedGpu));
        }
    }
    
    public void notifyOnUpdateProcessingListeners () {
        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onUpdateProcessingListeners.size(); i++) {
            final EventListener<VGpuGpuEventInfo> listener = 
            		onUpdateProcessingListeners.get(i);
            listener.update(VGpuGpuEventInfo.of(listener, this));
        }
    }
    
    @Override
    public void notifyOnCreationFailureListeners (final Videocard failedVideocard) {
        requireNonNull(failedVideocard);
        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onCreationFailureListeners.size(); i++) {
            final var listener = onCreationFailureListeners.get(i);
            listener.update(VGpuVideocardEventInfo.of(listener, this, failedVideocard));
        }
    }
    
    @Override
    public boolean removeOnMigrationStartListener (
    		final EventListener<VGpuGpuEventInfo> listener) {
        return onMigrationStartListeners.remove(requireNonNull(listener));
    }

    @Override
    public boolean removeOnMigrationFinishListener (
    		final EventListener<VGpuGpuEventInfo> listener) {
        return onMigrationFinishListeners.remove(requireNonNull(listener));
    }

    /*@Override
    public HorizontalVmScaling getHorizontalScaling() {
        return horizontalScaling;
    }

    @Override
    public final Vm setHorizontalScaling(final HorizontalVmScaling horizontalScaling) throws IllegalArgumentException {
        this.horizontalScaling = validateAndConfigureVmScaling(horizontalScaling);
        return this;
    }

    @Override
    public final Vm setRamVerticalScaling(final VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException {
        this.ramVerticalScaling = validateAndConfigureVmScaling(ramVerticalScaling);
        return this;
    }
    
    @Override
    public final Vm setBwVerticalScaling(final VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException {
        this.bwVerticalScaling = validateAndConfigureVmScaling(bwVerticalScaling);
        return this;
    }

    @Override
    public final Vm setPeVerticalScaling(final VerticalVmScaling peVerticalScaling) throws IllegalArgumentException {
        this.peVerticalScaling = validateAndConfigureVmScaling(peVerticalScaling);
        return this;
    }

    @Override
    public VerticalVmScaling getRamVerticalScaling() {
        return ramVerticalScaling;
    }

    @Override
    public VerticalVmScaling getBwVerticalScaling() {
        return bwVerticalScaling;
    }

    @Override
    public VerticalVmScaling getPeVerticalScaling() {
        return peVerticalScaling;
    }
    
    private <T extends VmScaling> T validateAndConfigureVmScaling (final T vmScaling) {
        requireNonNull(vmScaling);
        if (vmScaling.getVm() != null && vmScaling.getVm() != Vm.NULL && vmScaling.getVm() != this) {
            final String name = vmScaling.getClass().getSimpleName();
            throw new IllegalArgumentException(
                "The " + name + " given is already linked to a Vm. " +
                    "Each Vm must have its own " + name + " object or none at all. " +
                    "Another " + name + " has to be provided for this Vm.");
        }

        vmScaling.setVm(this);
        this.addOnUpdateProcessingListener(vmScaling::requestUpScalingIfPredicateMatches);
        return vmScaling;
    }
    
    @Override
    public VmGroup getGroup() {
        return group;
    }

    public void setGroup(final VmGroup group) {
        this.group = requireNonNull(group);
    }*/
    
    @Override
    public String getDescription () {
        return description;
    }

    @Override
    public VGpu setDescription (final String description) {
        this.description = description == null ? "" : description;
        return this;
    }
    
    /*@Override
    public VGpuResourceStats getCpuUtilizationStats () {
        return cpuUtilizationStats;
    }

    @Override
    public void enableUtilizationStats(){
        if(cpuUtilizationStats == null || cpuUtilizationStats == VmResourceStats.NULL) {
            this.cpuUtilizationStats = new VmResourceStats(this, vm -> vm.getCpuPercentUtilization(getSimulation().clock()));
        }
    }*/
    
    public static long getDefaultGddramCapacity () {
        return defaultGddramCapacity;
    }
    
    public static void setDefaultRamCapacity(final long defaultCapacity) {
        //AbstractMachine.validateCapacity(defaultCapacity);
        defaultGddramCapacity = defaultCapacity;
    }
    
    public static long getDefaultBwCapacity() {
        return defaultBwCapacity;
    }

    public static void setDefaultBwCapacity(final long defaultCapacity) {
        //AbstractMachine.validateCapacity(defaultCapacity);
        defaultBwCapacity = defaultCapacity;
    }
    
    /*public static long getDefaultStorageCapacity() {
        return defaultStorageCapacity;
    }
    
    public static void setDefaultStorageCapacity(final long defaultCapacity) {
        AbstractMachine.validateCapacity(defaultCapacity);
        defaultStorageCapacity = defaultCapacity;
    }*/
    
    /*@Override
    public double getTimeZone() {
        return timeZone;
    }

    @Override
    public CustomVGpu setTimeZone(final double timeZone) {
        this.timeZone = validateTimeZone(timeZone);
        return this;
    }*/
    
    public MipsShare getAllocatedMips () {
        return allocatedMips;
    }

    public void setAllocatedMips (final MipsShare allocatedMips) {
        this.allocatedMips = requireNonNull(allocatedMips);
    }

    public MipsShare getRequestedMips () {
        return requestedMips;
    }

    public void setRequestedMips (final MipsShare requestedMips) {
        this.requestedMips = requireNonNull(requestedMips);
    }
    
    @Override
    public void setId (final long id) {
        this.id = id;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public String getType () {
    	return type;
    }
    
    @Override
    public void setType (String type) {
    	this.type = type;
    }
    
    @Override
    public GpuVm getGpuVm () {
    	return gpuVm;
    }
    
    @Override
    public VGpu setGpuVm (GpuVm gpuVm) {
    	this.gpuVm = gpuVm;
    	if (!gpuVm.hasVGpu())
    		gpuVm.setVGpu(this);
    	return this;
    }
    
    @Override
    public int getPCIeBw () {
    	return PCIeBw;
    }
    
    @Override
	public void setPCIeBw (int PCIeBw) {
    	this.PCIeBw = PCIeBw;
    }
    
    @Override
    public String getTenancy () {
    	return tenancy;
    }
	
    @Override
	public void setTenancy (String tenancy) {
    	this.tenancy = tenancy;
    }
    
    @Override
    public Simulation getSimulation () {
    	return gpuVm.getSimulation();
    }
    
    @Override
    public double getGpuPercentUtilization (double time) {
    	return gpuTaskScheduler.getAllocatedGpuPercent(time);
    }

    @Override
    public double getGpuPercentUtilization () {
    	return getGpuPercentUtilization(getSimulation().clock());
    }

    @Override
    public boolean removeOnCreationFailureListener (final EventListener<VGpuVideocardEventInfo> listener) {
        return onCreationFailureListeners.remove(requireNonNull(listener));
    }

	@Override
	public VGpuResourceStats getGpuUtilizationStats () {
		return gpuUtilizationStats;
	}

	@Override
	public void enableUtilizationStats () {
		if(gpuUtilizationStats == null || gpuUtilizationStats == VGpuResourceStats.NULL) {
            this.gpuUtilizationStats = new VGpuResourceStats(this, 
            		vgpu -> vgpu.getGpuPercentUtilization(getSimulation().clock()));
        }
	}

	@Override
	public double getTotalGpuMipsUtilization () {
		return getTotalGpuMipsUtilization(getSimulation().clock());
	}

	@Override
	public double getTotalGpuMipsUtilization(double time) {
		return getGpuPercentUtilization(time) * getTotalMipsCapacity();
	}
}
