package org.cloudsimplus.datacenters;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.function.Consumer;

/**
 * A class that provides a set of methods to datacenter the {@link Datacenter} class
 * using {@link Mockito}. Each method in this class provides a datacenter for a
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
        this.datacenter = Mockito.mock(Datacenter.class);
        this.characteristics = Mockito.mock(DatacenterCharacteristics.class);
    }

    public static Datacenter createMock(final Consumer<DatacenterMocker> consumer) {
        final DatacenterMocker mocker = new DatacenterMocker();
        consumer.accept(mocker);
        return mocker.datacenter;
    }

    public OngoingStubbing<DatacenterCharacteristics> getCharacteristics() {
        return Mockito.when(datacenter.getCharacteristics()).thenReturn(characteristics);
    }

    public OngoingStubbing<Double> getCostPerBw(final double cost) {
        return Mockito.when(characteristics.getCostPerBw()).thenReturn(cost);
    }

    public OngoingStubbing<Double> getCostPerSecond(final double cost) {
        return Mockito.when(characteristics.getCostPerSecond()).thenReturn(cost);
    }

    public OngoingStubbing<Double> getCostPerMem(final double cost) {
        return Mockito.when(characteristics.getCostPerMem()).thenReturn(cost);
    }

    public OngoingStubbing<Double> getCostPerStorage(final double cost) {
        return Mockito.when(characteristics.getCostPerStorage()).thenReturn(cost);
    }
}
