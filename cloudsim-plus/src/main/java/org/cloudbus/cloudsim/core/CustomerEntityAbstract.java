package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.Datacenter;

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

    /**
     * @see #getBroker()
     */
    private DatacenterBroker broker;

    /** @see #getLastTriedDatacenter() */
    private Datacenter lastTriedDatacenter;

    protected CustomerEntityAbstract(){
        lastTriedDatacenter = Datacenter.NULL;
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
    public Simulation getSimulation() {
        return broker.getSimulation();
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + broker.hashCode();
        return result;
    }

    @Override
    public void setLastTriedDatacenter(final Datacenter lastTriedDatacenter) {
        this.lastTriedDatacenter = lastTriedDatacenter;
    }

    @Override
    public Datacenter getLastTriedDatacenter() {
        return lastTriedDatacenter;
    }
}
