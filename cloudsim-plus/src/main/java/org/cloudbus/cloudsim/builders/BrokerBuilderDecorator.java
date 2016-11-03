package org.cloudbus.cloudsim.builders;

import java.util.List;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;

/**
 * <p>A class that implements the Decorator Design Pattern in order to
 * include functionalities in a existing class.
 * It is used to ensure that specific methods are called only after
 * a given method is called.</p>
 *
 * For instance, the methods {@link #getVmBuilder()} and
 * {@link #getCloudletBuilder()} can only be called after
 * some {@link DatacenterBrokerSimple} was created by calling
 * the method {@link #createBroker()}.<br>
 * By this way, after the method is called, it returns
 * an instance of this decorator that allow
 * chained call to the specific decorator methods
 * as the following example:
 * <ul><li>{@link #createBroker() createBroker()}.{@link #getVmBuilder() getVmBuilder()}</li></ul>
 *
 * @author Manoel Campos da Silva Filho
 */
public class BrokerBuilderDecorator implements BrokerBuilderInterface {
    private final BrokerBuilder builder;
    private final VmBuilder vmBuilder;
    private final CloudletBuilder cloudletBuilder;
    private final DatacenterBroker broker;

    public BrokerBuilderDecorator(final BrokerBuilder builder, final DatacenterBrokerSimple broker) {
        if(builder == null)
           throw new RuntimeException("The builder parameter cannot be null.");
        if(broker == null)
           throw new RuntimeException("The broker parameter cannot be null.");
        this.builder = builder;
        this.broker = broker;

        this.vmBuilder = new VmBuilder(broker);
        this.cloudletBuilder = new CloudletBuilder(this, broker);
    }

    @Override
    public BrokerBuilderDecorator createBroker() {
        return builder.createBroker();
    }

    @Override
    public DatacenterBroker findBroker(int id) throws RuntimeException {
        return builder.findBroker(id);
    }

    @Override
    public List<DatacenterBroker> getBrokers() {
        return builder.getBrokers();
    }

    @Override
    public DatacenterBroker get(int index) {
       return builder.get(index);
    }

    /**
     * @return the VmBuilder in charge of creating VMs
     * to the latest DatacenterBroker created by this BrokerBuilder
     */
    public VmBuilder getVmBuilder() {
        return vmBuilder;
    }

    /**
     * @return the CloudletBuilder in charge of creating Cloudlets
     * to the latest DatacenterBroker created by this BrokerBuilder
     */
    public CloudletBuilder getCloudletBuilder() {
        return cloudletBuilder;
    }

    /**
     * @return the latest created broker
     */
    public DatacenterBroker getBroker() {
        return broker;
    }

}
