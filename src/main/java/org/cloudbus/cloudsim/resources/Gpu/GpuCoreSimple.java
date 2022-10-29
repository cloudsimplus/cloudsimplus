package org.cloudbus.cloudsim.gp.resources;

import org.cloudbus.cloudsim.gp.provisioners.CoreProvisioner;
import org.cloudbus.cloudsim.gp.provisioners.CoreProvisionerSimple;
import org.cloudbus.cloudsim.resources.ResourceManageableAbstract;

import java.util.Objects;

public class GpuCoreSimple extends ResourceManageableAbstract implements GpuCore {

	private static double defaultMips = 1000;
    private long id;
    private Status status;
    private CoreProvisioner coreProvisioner;

    public GpuCoreSimple () {
        this(GpuCoreSimple.defaultMips);
    }

    public GpuCoreSimple (final double mipsCapacity) {
        this(mipsCapacity, new CoreProvisionerSimple());
    }

    public GpuCoreSimple(final double mipsCapacity, final CoreProvisioner coreProvisioner) {
        super((long)mipsCapacity, "Unit");
        setId(-1);
        setCoreProvisioner(coreProvisioner);

        // when created it should be set to FREE, i.e. available for use.
        setStatus(Status.FREE);
    }

    public GpuCoreSimple (final int id, final double mipsCapacity, 
    		final CoreProvisioner coreProvisioner) {
        this(mipsCapacity, coreProvisioner);
        this.setId(id);
    }

    public static double getDefaultMips () {
        return defaultMips;
    }

    public static void setDefaultMips (final double defaultMips) {
    	GpuCoreSimple.defaultMips = defaultMips;
    }

    @Override
    public final void setId (final long id) {
        this.id = id;
    }

    @Override
    public long getId () {
        return id;
    }

    @Override
    public Status getStatus () {
        return status;
    }

    @Override
    public final boolean setStatus (final Status status) {
        this.status = status;
        return true;
    }

    @Override
    public boolean setCapacity (final double mipsCapacity) {
        return setCapacity((long)mipsCapacity);
    }

    @Override
    public final GpuCore setCoreProvisioner(final CoreProvisioner coreProvisioner) {
        this.coreProvisioner = Objects.requireNonNull(coreProvisioner);
        this.coreProvisioner.setCore(this);
        return this;
    }

    @Override
    public CoreProvisioner getCoreProvisioner () {
        return coreProvisioner;
    }

    //@Override
    public String toString() {
        return String.format("%s %d: %s", getClass().getSimpleName(), id, status);
    }

    @Override
    public boolean isWorking() {
        return !isFailed();
    }

    @Override
    public boolean isFailed() {
        return Status.FAILED.equals(status);
    }

    @Override
    public boolean isFree() {
        return Status.FREE.equals(status);
    }

    @Override
    public boolean isBusy() {
        return Status.BUSY.equals(status);
    }
}
