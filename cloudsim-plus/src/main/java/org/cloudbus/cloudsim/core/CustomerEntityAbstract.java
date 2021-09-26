package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.util.TimeUtil;

import static java.util.Objects.requireNonNull;

/**
 * A base class for {@link CustomerEntity} implementations.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.3
 */
public abstract class CustomerEntityAbstract implements CustomerEntity {
    /**
     * @see #getId()
     */
    private long id;

    /** @see #getArrivedTime() */
    private double arrivedTime;
    /** @see #getCreationTime() */
    private double creationTime;

    /**
     * @see #getBroker()
     */
    private DatacenterBroker broker;

    /** @see #getLastTriedDatacenter() */
    private Datacenter lastTriedDatacenter;

    protected CustomerEntityAbstract(){
        lastTriedDatacenter = Datacenter.NULL;
        creationTime = -1;
    }

    @Override
    public final void setBroker(final DatacenterBroker broker) {
        this.broker = requireNonNull(broker);
    }

    @Override
    public DatacenterBroker getBroker() {
        return broker;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public final void setId(final long id) {
        this.id = id;
    }

    @Override
    public String getUid() {
        return UniquelyIdentifiable.getUid(broker.getId(), id);
    }

    @Override
    public double getArrivedTime() {
        return arrivedTime;
    }

    @Override
    public CustomerEntity setArrivedTime(final double time) {
        this.arrivedTime = TimeUtil.validateTime("Arrived time", time);
        return this;
    }

    @Override
    public double getCreationTime() {
        return creationTime;
    }

    public CustomerEntity setCreationTime() {
        setCreationTime(getSimulation().clock());
        return this;
    }

    public CustomerEntity setCreationTime(final double time) {
        this.creationTime = TimeUtil.validateTime("Creation time", time);
        return this;
    }

    @Override
    public double getWaitTime() {
        return creationTime < 0 ? getSimulation().clock() - arrivedTime : creationTime - arrivedTime;
    }

    @Override
    public Simulation getSimulation() {
        return broker.getSimulation();
    }

    @Override
    public void setLastTriedDatacenter(final Datacenter lastTriedDatacenter) {
        this.lastTriedDatacenter = lastTriedDatacenter;
    }

    @Override
    public Datacenter getLastTriedDatacenter() {
        return lastTriedDatacenter;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final var that = (CustomerEntityAbstract) obj;
        return this.getId() == that.getId() && this.getBroker().equals(that.getBroker());
    }

    @Override
    public final int hashCode() {
        int result = broker.hashCode();
        result = 31 * result + Long.hashCode(id);
        return result;
    }
}
