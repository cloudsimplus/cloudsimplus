package org.cloudbus.cloudsim.datacenters;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

import java.util.function.Consumer;

/**
 * A class that provides a set of methods to datacenter the {@link Datacenter} class
 * using {@link EasyMock}. Each method in this class provides a datacenter for a
 * method with the same name in the Datacenter class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class DatacenterMocker {
    /**
     * The created Datacenter datacenter object.
     */
    private final Datacenter datacenter;
    private final DatacenterCharacteristics characteristics;

    private DatacenterMocker() {
        this.datacenter = EasyMock.createMock(Datacenter.class);
        this.characteristics = EasyMock.createMock(DatacenterCharacteristics.class);
    }

    public static Datacenter createMock(final Consumer<DatacenterMocker> consumer) {
        final DatacenterMocker mocker = new DatacenterMocker();
        consumer.accept(mocker);
        DatacenterMocker.replay(mocker.characteristics);
        DatacenterMocker.replay(mocker.datacenter);
        return mocker.datacenter;
    }

    public IExpectationSetters<DatacenterCharacteristics> getCharacteristics() {
        return EasyMock.expect(datacenter.getCharacteristics()).andReturn(characteristics);
    }

    public IExpectationSetters<Double> getCostPerBw(final double cost) {
        return EasyMock.expect(characteristics.getCostPerBw()).andReturn(cost);
    }

    public IExpectationSetters<Double> getCostPerSecond(final double cost) {
        return EasyMock.expect(characteristics.getCostPerSecond()).andReturn(cost);
    }

    public IExpectationSetters<Double> getCostPerMem(final double cost) {
        return EasyMock.expect(characteristics.getCostPerMem()).andReturn(cost);
    }

    public IExpectationSetters<Double> getCostPerStorage(final double cost) {
        return EasyMock.expect(characteristics.getCostPerStorage()).andReturn(cost);
    }

    private static <T extends Object> void replay(final T mock) {
        EasyMock.replay(mock);
    }

    public static <T extends Object> void verify(final T mock) {
        EasyMock.verify(mock);
    }


}
