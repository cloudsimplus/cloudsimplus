package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.vms.Vm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ProcessorTest {
    private static final double PE_MIPS = 1000;
    private static final int NUMBER_OF_PES = 2;

    @Test
    public void testGetTotalMipsCapacity() {
        final Processor instance = createDefaultProcessor();
        final double expResult = PE_MIPS * NUMBER_OF_PES;
        assertEquals(expResult, instance.getTotalMips());
    }

    /**
     * Creates a processing using the default values.
     * @return the created processor
     */
    private static Processor createDefaultProcessor() {
        return new Processor(Vm.NULL, PE_MIPS, NUMBER_OF_PES);
    }

    @Test
    public void testGetCapacity() {
        final Processor instance = createDefaultProcessor();
        final double expResult = PE_MIPS;
        assertEquals(expResult, instance.getMips());
    }

    @Test
    public void testGetNumberOfPesFromDefaultConstructor() {
        final Processor instance = createDefaultProcessor();
        final int expResult = NUMBER_OF_PES;
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testSetNumberOfPes() {
        final int expResult = NUMBER_OF_PES*2;
        final Processor instance = createDefaultProcessor();
        instance.setCapacity(expResult);
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testSetCapacity() {
        final long expResult = (long)PE_MIPS*2;
        final Processor instance = createDefaultProcessor();
        instance.setMips(expResult);
        assertEquals(expResult, instance.getMips());
    }

}
