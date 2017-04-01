package org.cloudbus.cloudsim.datacenters;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

import java.util.function.Consumer;

/**
 * A class that provides a set of methods to dc the {@link Datacenter} class
 * using {@link EasyMock}. Each method in this class provides a dc for a
 * method with the same name in the Datacenter class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class DatacenterMocker {
    /**
     * The created Datacenter dc object.
     */
    private final Datacenter dc;
    private final DatacenterCharacteristics c;

    private DatacenterMocker() {
        this.dc = EasyMock.createMock(Datacenter.class);
        this.c = EasyMock.createMock(DatacenterCharacteristics.class);
    }

    public static Datacenter createMock(Consumer<DatacenterMocker> consumer) {
        DatacenterMocker mocker = new DatacenterMocker();
        consumer.accept(mocker);
        DatacenterMocker.replay(mocker.c);
        DatacenterMocker.replay(mocker.dc);
        return mocker.dc;
    }

    public IExpectationSetters<DatacenterCharacteristics> getCharacteristics() {
        return EasyMock.expect(dc.getCharacteristics()).andReturn(c);
    }

    public IExpectationSetters<Double> getCostPerBw(double cost) {
        return EasyMock.expect(c.getCostPerBw()).andReturn(cost);
    }

    public IExpectationSetters<Double> getCostPerSecond(double cost) {
        return EasyMock.expect(c.getCostPerSecond()).andReturn(cost);
    }

    public IExpectationSetters<Double> getCostPerMem(double cost) {
        return EasyMock.expect(c.getCostPerMem()).andReturn(cost);
    }

    public IExpectationSetters<Double> getCostPerStorage(double cost) {
        return EasyMock.expect(c.getCostPerStorage()).andReturn(cost);
    }

    private static <T extends Object> void replay(T mock) {
        EasyMock.replay(mock);
    }

    public static <T extends Object> void verify(T mock) {
        EasyMock.verify(mock);
    }


}
