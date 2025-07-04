/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.allocationpolicies;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostAbstract;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Processor;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.util.Conversion;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toSet;

/**
 * An abstract class that represents the policy
 * used by a {@link Datacenter} to choose a {@link Host} to place or migrate
 * a given {@link Vm}.
 *
 * <p>Each {@link Datacenter} must have its own instance of a {@link VmAllocationPolicy}.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
@Accessors(makeFinal = false) @Getter @Setter
public non-sealed abstract class VmAllocationPolicyAbstract implements VmAllocationPolicy {
    /**
     * WARNING: the function should not be called directly because it may be null.
     * Use the {@link #findHostForVm(Vm)} instead.
     *
     * @see #setFindHostForVmFunction(BiFunction)
     */
    private BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction;

    /** @see #getDatacenter() */
    @NonNull
    private Datacenter datacenter;

    /** @see #getHostCountForParallelSearch() */
    private int hostCountForParallelSearch;

    /**
     * Creates a VmAllocationPolicy.
     */
    public VmAllocationPolicyAbstract() {
        //Passing null makes the class to use the default function to find a host for a VM.
        this(null);
    }

    /**
     * Creates a VmAllocationPolicy, changing the {@link BiFunction} to select a Host for a Vm.
     *
     * @param findHostForVmFunction a {@link BiFunction} to select a Host for a given Vm.
     * @see VmAllocationPolicy#setFindHostForVmFunction(BiFunction)
     */
    public VmAllocationPolicyAbstract(final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) {
        setDatacenter(Datacenter.NULL);
        setFindHostForVmFunction(findHostForVmFunction);
        this.hostCountForParallelSearch = DEF_HOST_COUNT_PARALLEL_SEARCH;
    }

    @Override
    public final <T extends Host> List<T> getHostList() {
        return datacenter.getHostList();
    }

    @Override
    public boolean scaleVmVertically(final VerticalVmScaling scaling) {
        if (scaling.isVmUnderloaded()) {
            return downScaleVmVertically(scaling);
        }

        if (scaling.isVmOverloaded()) {
            return upScaleVmVertically(scaling);
        }

        return false;
    }

    /**
     * Performs the upscaling of Vm resource associated to a given scaling object.
     *
     * @param scaling the Vm scaling object
     * @return true if the Vm was overloaded and the upscaling was performed, false otherwise
     */
    private boolean upScaleVmVertically(final VerticalVmScaling scaling) {
        return isRequestingCpuScaling(scaling) ? scaleVmPesUpOrDown(scaling) : upScaleVmNonCpuResource(scaling);
    }

    /**
     * Performs the down scaling of Vm resource associated to a given scaling object.
     *
     * @param scaling the Vm scaling object
     * @return true if the downscaling was performed, false otherwise
     */
    private boolean downScaleVmVertically(final VerticalVmScaling scaling) {
        return isRequestingCpuScaling(scaling) ? scaleVmPesUpOrDown(scaling) : downScaleVmNonCpuResource(scaling);
    }

    /**
     * Performs the up or down scaling of Vm {@link Pe}s,
     * depending on if the VM is under or overloaded.
     *
     * @param scaling the Vm scaling object
     * @return true if the scaling was performed, false otherwise
     * @see #upScaleVmVertically(VerticalVmScaling)
     */
    private boolean scaleVmPesUpOrDown(final VerticalVmScaling scaling) {
        final double pesNumberForScaling = scaling.getResourceAmountToScale();
        if (pesNumberForScaling == 0) {
            return false;
        }

        final boolean isVmUnderloaded = scaling.isVmUnderloaded();
        // Avoids trying to downscale the number of vPEs to zero
        if(isVmUnderloaded && scaling.getVm().getPesNumber() == pesNumberForScaling) {
            scaling.logDownscaleToZeroNotAllowed();
            return false;
        }

        if (scaling.isVmOverloaded() && isNotHostPesSuitableToUpScaleVm(scaling)) {
            scaling.logResourceUnavailable();
            return false;
        }

        final Vm vm = scaling.getVm();
        vm.getHost().getVmScheduler().deallocatePesFromVm(vm);
        final int signal = isVmUnderloaded ? -1 : 1;
        // Removes or adds some capacity from/to the resource, respectively if the VM is under or overloaded
        vm.getProcessor().sumCapacity((long) pesNumberForScaling * signal);

        vm.getHost().getVmScheduler().allocatePesForVm(vm);
        return true;
    }

    private boolean isNotHostPesSuitableToUpScaleVm(final VerticalVmScaling scaling) {
        final Vm vm = scaling.getVm();
        final long pesCountForScaling = (long)scaling.getResourceAmountToScale();
        final MipsShare additionalVmMips = new MipsShare(pesCountForScaling, vm.getMips());
        return !vm.getHost().getVmScheduler().isSuitableForVm(vm, additionalVmMips);
    }

    /**
     * Checks if the scaling object is in charge of scaling CPU resource.
     *
     * @param scaling the Vm scaling object
     * @return true if the scaling is for CPU, false if it is
     * for any other kind of resource
     */
    private boolean isRequestingCpuScaling(final VerticalVmScaling scaling) {
        return Processor.class.equals(scaling.getResourceClass());
    }

    /**
     * Performs the upscaling of a Vm resource that is anything else than CPU.
     *
     * @param scaling the Vm scaling object
     * @return true if the upscaling was performed, false otherwise
     * @see #scaleVmPesUpOrDown(VerticalVmScaling)
     * @see #upScaleVmVertically(VerticalVmScaling)
     */
    private boolean upScaleVmNonCpuResource(final VerticalVmScaling scaling) {
        return scaling.allocateResourceForVm();
    }

    /**
     * Performs the downscaling of a Vm resource that is anything else than CPU.
     *
     * @param scaling the Vm scaling object
     * @return true if the downscaling was performed, false otherwise
     * @see #downScaleVmVertically(VerticalVmScaling)
     */
    private boolean downScaleVmNonCpuResource(final VerticalVmScaling scaling) {
        final var resourceManageableClass = scaling.getResourceClass();
        final var vmResource = scaling.getVm().getResource(resourceManageableClass);
        final double amountToDeallocate = scaling.getResourceAmountToScale();
        if(amountToDeallocate == 0)
            return false;

        final var resourceProvisioner = scaling.getVm().getHost().getProvisioner(resourceManageableClass);
        final double newTotalVmResource = vmResource.getCapacity() - amountToDeallocate;
        if (resourceProvisioner.allocateResourceForVm(scaling.getVm(), newTotalVmResource)) {
            LOGGER.info(
                "{}: {}: {} {} of {} deallocated from {}: new capacity is {} {}. Current resource usage is {}%",
                scaling.getVm().getSimulation().clockStr(),
                scaling.getClass().getSimpleName(),
                (long) amountToDeallocate, vmResource.getUnit(), resourceManageableClass.getSimpleName(),
                scaling.getVm(), vmResource.getCapacity(), vmResource.getUnit(),
                vmResource.getPercentUtilization() * 100);
            return true;
        }

        LOGGER.error(
            "{}: {}: {} requested to reduce {} capacity by {} but an unexpected error occurred and the resource was not resized",
            scaling.getVm().getSimulation().clockStr(),
            scaling.getClass().getSimpleName(),
            scaling.getVm(),
            resourceManageableClass.getSimpleName(), (long) amountToDeallocate);
        return false;

    }

    @Override
    public final Set<HostSuitability> allocateHostForVm(@NonNull final List<Vm> vmList) {
        if (datacenterHasNoHosts())
            return Collections.emptySet();

        return allocateHostForVmInternal(vmList);
    }

    /**
     * If you override this method, you must call {@link #allocateHostForVm(Vm, Host)}
     * for each suitable Host you have found that want to create a VM.
     * @see #allocateHostForVm(List)
     * @see Host#getSuitabilityFor(Vm)
     */
    protected Set<HostSuitability> allocateHostForVmInternal(final @NonNull List<Vm> vmList) {
        return vmList.stream().map(this::allocateHostForVm).collect(toSet());
    }

    /**
     * Allocates the host with less PEs in use for a given VM.
     * {@inheritDoc}
     */
    @Override
    public HostSuitability allocateHostForVm(final Vm vm) {
        if (datacenterHasNoHosts(vm)) {
            return new HostSuitability(vm, "Datacenter has no host.");
        }

        if (vm.isCreated()) {
            return new HostSuitability(vm, "VM is already created");
        }

        final var optionalHost = findHostForVm(vm);
        if (optionalHost.filter(Host::isActive).isPresent()) {
            return allocateHostForVm(vm, optionalHost.get());
        }

        LOGGER.warn(
            "{}: {}: No suitable host found for {} in {}",
            vm.getSimulation().clockStr(), getClass().getSimpleName(), vm, datacenter);
        return new HostSuitability(vm, "No suitable host found");
    }

    /**
     * @return true if the datacenter linked to this VmAllocationPolicy is empty (has no hosts);
     *         false otherwise.
     */
    private boolean datacenterHasNoHosts() {
        return datacenterHasNoHosts(null);
    }

    /**
     * {@return true if the datacenter linked to this VmAllocationPolicy is empty (has no hosts); false otherwise}
     * @param vm a Vm requested to be created or null if there is no Vm creation request
     */
    private boolean datacenterHasNoHosts(@Nullable final Vm vm) {
        final var vmStr = vm == null ? "Vm" : vm;
        if (getHostList().isEmpty()) {
            LOGGER.error(
                "{}: {}: There is no Hosts in {} for requesting {} creation.",
                datacenter.getSimulation().clockStr(), getClass().getSimpleName(), datacenter, vmStr);
            return true;
        }

        return false;
    }

    @Override
    public HostSuitability allocateHostForVm(final Vm vm, final Host host) {
        if(vm instanceof VmGroup vmGroup){
            return createVmsFromGroup(vmGroup, host);
        }

        return createVm(vm, host);
    }

    private HostSuitability createVmsFromGroup(final VmGroup vmGroup, final Host host) {
        int createdVms = 0;
        final var hostSuitabilityForVmGroup = new HostSuitability(host, vmGroup);
        for (final Vm vm : vmGroup.getVmList()) {
            final var hostSuitability = createVm(vm, host);
            hostSuitabilityForVmGroup.setSuitability(hostSuitability);
            createdVms += Conversion.boolToInt(hostSuitability.fully());
        }

        vmGroup.setCreated(createdVms > 0);
        if(vmGroup.isCreated()) {
            vmGroup.setHost(host);
        }

        return hostSuitabilityForVmGroup;
    }

    private HostSuitability createVm(final Vm vm, final Host host) {
        final var suitability = host.createVm(vm);
        if (suitability.fully()) {
            LOGGER.info(
                "{}: {}: {} has been allocated to {}",
                vm.getSimulation().clockStr(), getClass().getSimpleName(), vm, host);
        } else {
            LOGGER.error(
                "{}: {} Creation of {} on {} failed due to {}.",
                vm.getSimulation().clockStr(), getClass().getSimpleName(), vm, host, suitability);
        }

        return suitability;
    }

    @Override
    public void deallocateHostForVm(final Vm vm) {
        ((HostAbstract)vm.getHost()).destroyVm(vm);
    }

    /**
     * {@inheritDoc}
     * The default implementation of such a Function is provided by the method {@link #findHostForVm(Vm)}.
     *
     * @param findHostForVmFunction {@inheritDoc}.
     *                              Passing null will cause the usage of the default method to find a Host for a VM.
     * @see #defaultFindHostForVm(Vm)
     */
    @Override
    public final VmAllocationPolicy setFindHostForVmFunction(final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) {
        this.findHostForVmFunction = findHostForVmFunction;
        return this;
    }

    @Override
    public final Optional<Host> findHostForVm(final Vm vm) {
        final var optionalHost = findHostForVmFunction == null ? defaultFindHostForVm(vm) : findHostForVmFunction.apply(this, vm);
        //If the selected Host is not active, activate it (if it's already active, setActive has no effect)
        return optionalHost.map(host -> host.setActive(true));
    }

    /**
     * Provides the default implementation of the policy
     * to find a suitable Host for a given VM.
     *
     * @param vm the VM to find a suitable Host to
     * @return an {@link Optional} containing a suitable Host to place the VM;
     *         or an empty {@link Optional} if no suitable Host was found
     * @see #setFindHostForVmFunction(BiFunction)
     */
    protected abstract Optional<Host> defaultFindHostForVm(Vm vm);

    @Override
    public Map<Vm, Host> getOptimizedAllocationMap(final List<? extends Vm> vmList) {
        /*
         * This method implementation doesn't perform any
         * VM placement optimization and, in fact, has no effect.
         * Classes implementing the {@link VmAllocationPolicyMigration}
         * provide actual implementations for this method that subclasses can override.
         */
        return Collections.emptyMap();
    }

    @Override
    public boolean isVmMigrationSupported() {
        return false;
    }
}

