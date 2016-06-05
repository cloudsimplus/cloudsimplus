package org.cloudbus.cloudsim.network.datacenter;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class NetworkCloudletTest {
    private static final long LENGTH=10000;
    private static final int PES=1; 
    private static final int FILE_SIZE = 100;
    private static final int OUTPUT_SIZE = 100;
    private static final int RAM = 512;
    private NetworkCloudlet instance;

    @Before
    public void setUp(){
        instance = createNetworkCloudlet(0);
    }
    
    @Test
    public void testCurrentStageNum() {
        assertEquals(-1, instance.getCurrentTaskNum(), 0.0);
    }

    private NetworkCloudlet createNetworkCloudlet(int id) {
        return new NetworkCloudlet(
                id, LENGTH, PES, FILE_SIZE, OUTPUT_SIZE, RAM, 
                UtilizationModel.NULL, 
                UtilizationModel.NULL, 
                UtilizationModel.NULL);
    }
    
}
