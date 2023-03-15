package org.cloudsimplus.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.util.MathUtil;

/**
 * A base class for {@link CustomerEntity} implementations.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.3
 */
@Accessors @Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class CustomerEntityAbstract implements CustomerEntity {
    @EqualsAndHashCode.Include
    private long id;

    @NonNull
    @EqualsAndHashCode.Include
    private DatacenterBroker broker;

    private double arrivedTime;
    private double creationTime;

    private Datacenter lastTriedDatacenter;

    protected CustomerEntityAbstract(){
        lastTriedDatacenter = Datacenter.NULL;
        creationTime = -1;
    }

    @Override
    public String getUid() {
        return UniquelyIdentifiable.getUid(broker.getId(), id);
    }

    @Override
    public void setArrivedTime(final double time) {
        this.arrivedTime = MathUtil.nonNegative(time, "Arrived time");
    }

    public void setCreationTime() {
        setCreationTime(getSimulation().clock());
    }

    public void setCreationTime(final double time) {
        this.creationTime = MathUtil.nonNegative(time, "Creation time");
    }

    @Override
    public double getWaitTime() {
        return creationTime < 0 ? getSimulation().clock() - arrivedTime : creationTime - arrivedTime;
    }

    @Override
    public Simulation getSimulation() {
        return broker.getSimulation();
    }
}
