package org.cloudbus.cloudsim.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CalculatorTest {
    
    @Test
    public void testAdd() {
        System.out.println("add");
        assertEquals(3D, new Calculator<>(0D).add(2D, 1D), 0D);
        assertEquals(3F, new Calculator<>(0F).add(2F, 1F), 0F);
        assertEquals(3L, new Calculator<>(0L).add(2L, 1L), 0L);
        assertEquals(3, new Calculator<>(0).add(2, 1), 0);

        short shortRes=3, shortA=2, shortB=1, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).add(shortA, shortB), shortZero);
        
        byte byteRes=3, byteA=2, byteB=1, byteZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).add(byteA, byteB), byteZero);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_nulValue() {
        new Calculator<>(0D).add(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNegative_nulValue() {
        new Calculator<>(0D).isNegative(null);
    }

    @Test
    public void testSubtract() {
        System.out.println("subtract");
        assertEquals(1D, new Calculator<>(0D).subtract(2D, 1D), 0D);
        assertEquals(1F, new Calculator<>(0F).subtract(2F, 1F), 0F);
        assertEquals(1L, new Calculator<>(0L).subtract(2L, 1L), 0L);
        assertEquals(1, new Calculator<>(0).subtract(2, 1), 0);

        short shortRes=1, shortA=2, shortB=1, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).subtract(shortA, shortB), shortZero);
        
        byte byteRes=1, byteA=2, byteB=1, byteZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).subtract(byteA, byteB), byteZero);
    }

    @Test
    public void testMultiple() {
        System.out.println("multiple");
        assertEquals(6D, new Calculator<>(0D).multiple(2D, 3D), 0D);
        assertEquals(6F, new Calculator<>(0F).multiple(2F, 3F), 0F);
        assertEquals(6L, new Calculator<>(0L).multiple(2L, 3L), 0L);
        assertEquals(6, new Calculator<>(0).multiple(2, 3), 0);

        short shortRes=6, shortA=2, shortB=3, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).multiple(shortA, shortB), shortZero);
        
        byte byteRes=6, byteA=2, byteB=3, byteZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).multiple(byteA, byteB), byteZero);
    }

    @Test
    public void testDivide() {
        System.out.println("divide");
        assertEquals(6D, new Calculator<>(0D).divide(12D, 2D), 0D);
        assertEquals(6F, new Calculator<>(0F).divide(12F, 2F), 0F);
        assertEquals(6L, new Calculator<>(0L).divide(12L, 2L), 0L);
        assertEquals(6, new Calculator<>(0).divide(12, 2), 0);

        short shortRes=6, shortA=12, shortB=2, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).divide(shortA, shortB), shortZero);
        
        byte byteRes=6, byteA=12, byteB=2, byteZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).divide(byteA, byteB), byteZero);
    }

    @Test
    public void testMod() {
        System.out.println("mod");
        assertEquals(0D, new Calculator<>(0D).mod(12D, 2D), 0D);
        assertEquals(0F, new Calculator<>(0F).mod(12F, 2F), 0F);
        assertEquals(0L, new Calculator<>(0L).mod(12L, 2L), 0L);
        assertEquals(0, new Calculator<>(0).mod(12, 2), 0);

        short shortRes=0, shortA=12, shortB=2, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).mod(shortA, shortB), shortZero);
        
        byte byteRes=0, byteA=12, byteB=2, byteZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).mod(byteA, byteB), byteZero);
    }

    @Test
    public void testMin() {
        System.out.println("min");
        assertEquals(2D, new Calculator<>(0D).min(12D, 2D), 0D);
        assertEquals(2F, new Calculator<>(0F).min(12F, 2F), 0F);
        assertEquals(2L, new Calculator<>(0L).min(12L, 2L), 0L);
        assertEquals(2, new Calculator<>(0).min(12, 2), 0);

        short shortRes=2, shortA=12, shortB=2, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).min(shortA, shortB), shortZero);
        
        byte byteRes=2, byteA=12, byteB=2, byteZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).min(byteA, byteB), byteZero);
        
        assertEquals(2D, new Calculator<>(0D).min(2D, 12D), 0D);
        assertEquals(2F, new Calculator<>(0F).min(2F, 12F), 0F);
        assertEquals(2L, new Calculator<>(0L).min(2L, 12L), 0L);
        assertEquals(2, new Calculator<>(0).min(2, 12), 0);

        assertEquals(shortRes, new Calculator<>(shortZero).min(shortB, shortA), shortZero);
        assertEquals(byteRes, new Calculator<>(byteZero).min(byteB, byteA), byteZero);        
    }

    @Test
    public void testMax_maxAtFirstParam() {
        System.out.println("max");
        assertEquals(12D, new Calculator<>(0D).max(12D, 2D), 0D);
        assertEquals(12F, new Calculator<>(0F).max(12F, 2F), 0F);
        assertEquals(12L, new Calculator<>(0L).max(12L, 2L), 0L);
        assertEquals(12, new Calculator<>(0).max(12, 2), 0);

        short shortRes=12, shortA=12, shortB=2, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).max(shortA, shortB), shortZero);
    }

    @Test
    public void testMax_maxAtSecondParam() {
        byte byteRes=12, byteA=12, byteB=2, byteZero=0;
        short shortRes=12, shortA=12, shortB=2, shortZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).max(byteA, byteB), byteZero);
        assertEquals(12D, new Calculator<>(0D).max(2D, 12D), 0D);
        assertEquals(12F, new Calculator<>(0F).max(2F, 12F), 0F);
        assertEquals(12L, new Calculator<>(0L).max(2L, 12L), 0L);
        assertEquals(12, new Calculator<>(0).max(2, 12), 0);
        assertEquals(shortRes, new Calculator<>(shortZero).max(shortB, shortA), shortZero);
        assertEquals(byteRes, new Calculator<>(byteZero).max(byteB, byteA), byteZero);        
    }

    @Test
    public void testAbs_negativeValues() {
        System.out.println("abs");
        assertEquals(12D, new Calculator<>(0D).abs(-12D), 0D);
        assertEquals(12F, new Calculator<>(0F).abs(-12F), 0F);
        assertEquals(12L, new Calculator<>(0L).abs(-12L), 0L);
        assertEquals(12, new Calculator<>(0).abs(-12), 0);

        short shortRes=12, shortA=-12, shortZero=0;
        assertEquals(shortRes, new Calculator<>(shortZero).abs(shortA), shortZero);
        
        byte byteRes=12, byteA=-12, byteZero=0;
        assertEquals(byteRes, new Calculator<>(byteZero).abs(byteA), byteZero);
    }

    @Test
    public void testAbs_positiveValues() {
        assertEquals(12D, new Calculator<>(0D).abs(12D), 0D);
        assertEquals(12F, new Calculator<>(0F).abs(12F), 0F);
        assertEquals(12L, new Calculator<>(0L).abs(12L), 0L);
        assertEquals(12, new Calculator<>(0).abs(12), 0);

        short shortRes = 12, shortA=12, shortZero = 0;
        assertEquals(shortRes, new Calculator<>(shortZero).abs(shortA), shortZero);
        
        byte byteRes = 12, byteA=12, byteZero = 0;
        assertEquals(byteRes, new Calculator<>(byteZero).abs(byteA), byteZero);
    }

    @Test
    public void testIsNegativeOrZero_positiveValues() {
        System.out.println("testIsNegativeOrZero_positiveValues");
        
        assertFalse(new Calculator<>(0D).isNegativeOrZero(1D));
        assertFalse(new Calculator<>(0F).isNegativeOrZero(1F));
        assertFalse(new Calculator<>(0L).isNegativeOrZero(1L));
        assertFalse(new Calculator<>(0).isNegativeOrZero(1));
        
        short shortA=1;
        assertFalse(new Calculator<>(shortA).isNegativeOrZero(shortA));
        
        byte byteA=1;
        assertFalse(new Calculator<>(byteA).isNegativeOrZero(byteA));
    }

    @Test
    public void testIsNegativeOrZero_zeroValues() {
        assertTrue(new Calculator<>(0D).isNegativeOrZero(0D));
        assertTrue(new Calculator<>(0F).isNegativeOrZero(0F));
        assertTrue(new Calculator<>(0L).isNegativeOrZero(0L));
        assertTrue(new Calculator<>(0).isNegativeOrZero(0));
        byte byteZero = 0;
        assertTrue(new Calculator<>(byteZero).isNegativeOrZero(byteZero));
        short shortZero = 0;
        assertTrue(new Calculator<>(shortZero).isNegativeOrZero(shortZero));
    }

    @Test
    public void testIsNegativeOrZero_negativeValues() {
        assertTrue(new Calculator<>(0D).isNegativeOrZero(-12D));
        assertTrue(new Calculator<>(0F).isNegativeOrZero(-12F));
        assertTrue(new Calculator<>(0L).isNegativeOrZero(-12L));
        assertTrue(new Calculator<>(0).isNegativeOrZero(-12));
        
        short shortA=-12;
        assertTrue(new Calculator<>(shortA).isNegativeOrZero(shortA));
        
        byte byteA=-12;
        assertTrue(new Calculator<>(byteA).isNegativeOrZero(byteA));
        
    }

    @Test
    public void testIsNegative_zeroValues() {
        System.out.println("isNegative");
        assertFalse(new Calculator<>(0D).isNegative(0D));
        assertFalse(new Calculator<>(0F).isNegative(0F));
        assertFalse(new Calculator<>(0L).isNegative(0L));
        assertFalse(new Calculator<>(0).isNegative(0));
        
        short shortA=0;
        assertFalse(new Calculator<>(shortA).isNegative(shortA));
        
        byte byteA=0;
        assertFalse(new Calculator<>(byteA).isNegative(byteA));
        
    }

    @Test
    public void testIsNegative_positiveValues() {
        assertFalse(new Calculator<>(0D).isNegative(1D));
        assertFalse(new Calculator<>(0F).isNegative(1F));
        assertFalse(new Calculator<>(0L).isNegative(1L));
        assertFalse(new Calculator<>(0).isNegative(1));
        short shortA=1;
        assertFalse(new Calculator<>(shortA).isNegative(shortA));
        
        byte byteA=1;
        assertFalse(new Calculator<>(byteA).isNegative(byteA));
    }

    @Test
    public void testIsNegative_negativeValues() {
        assertTrue(new Calculator<>(0D).isNegative(-12D));
        assertTrue(new Calculator<>(0F).isNegative(-12F));
        assertTrue(new Calculator<>(0L).isNegative(-12L));
        assertTrue(new Calculator<>(0).isNegative(-12));
        short shortA=-12;
        assertTrue(new Calculator<>(shortA).isNegative(shortA));
        
        byte byteA=-12;
        assertTrue(new Calculator<>(byteA).isNegative(byteA));
    }

    @Test
    public void testGetZero() {
        System.out.println("getZero");
        assertEquals(0D, new Calculator<>(0D).getZero(), 0D);
        assertEquals(0F, new Calculator<>(0F).getZero(), 0F);
        assertEquals(0L, new Calculator<>(0L).getZero(), 0L);
        assertEquals(0, new Calculator<>(0).getZero(), 0);

        short shortZero = 0;
        assertEquals(shortZero, new Calculator<>(shortZero).getZero(), shortZero);
        byte byteZero = 0;
        assertEquals(byteZero, new Calculator<>(byteZero).getZero(), byteZero);
    }
    
}
