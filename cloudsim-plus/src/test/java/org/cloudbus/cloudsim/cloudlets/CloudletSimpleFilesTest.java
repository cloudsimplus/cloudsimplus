package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.cloudbus.cloudsim.cloudlets.CloudletSimpleTest.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * @author	Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since	CloudSim Plus 1.3.0
 */
public class CloudletSimpleFilesTest {
    private static final String FILE1 = "file1.txt";
    private static final String FILE2 = "file2.txt";
    private static final String FILE3 = "file3.txt";
    private static final String FILE_INEXISTENT = "file-inexistent.txt";

    private CloudletSimple cloudlet;
    private UtilizationModel utilizationModelCpu;
    private UtilizationModel utilizationModelRam;
    private UtilizationModel utilizationModelBw;

    @Before
    public void setUp() {
        utilizationModelCpu = new UtilizationModelStochastic();
        utilizationModelRam = new UtilizationModelStochastic();
        utilizationModelBw = new UtilizationModelStochastic();
        cloudlet = new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER);
        cloudlet.setFileSize(CLOUDLET_FILE_SIZE)
            .setOutputSize(CLOUDLET_OUTPUT_SIZE)
            .setUtilizationModelCpu(utilizationModelCpu)
            .setUtilizationModelRam(utilizationModelRam)
            .setUtilizationModelBw(utilizationModelBw);
    }

    @Test
    public void testAddRequiredFile() {
        final CloudletSimple c = createCloudlet();
        final String files[] = {FILE1, FILE2};
        for (final String file : files) {
            assertTrue("Method file should be added",
                c.addRequiredFile(file));  //file doesn't previously added
            assertFalse("Method file shouldn't be added",
                c.addRequiredFile(file)); //file already added
        }
    }

    @Test
    public void testDeleteRequiredFile() {
        final CloudletSimple c = createCloudlet();
        final String files[] = {FILE1, FILE2, FILE3};
        for (final String file : files) {
            c.addRequiredFile(file);
        }

        assertFalse(c.deleteRequiredFile(FILE_INEXISTENT));
        for (final String file : files) {
            assertTrue(c.deleteRequiredFile(file));
            assertFalse(c.deleteRequiredFile(file)); //already deleted
        }
    }

    @Test
    public void testSetRequiredFiles0() {
        final List<String> files = new ArrayList<>();
        files.add(FILE1);
        cloudlet.setRequiredFiles(files);
        assertEquals(files, cloudlet.getRequiredFiles());
    }

    @Test(expected = NullPointerException.class)
    public void testRequiredFiles1() {
        final CloudletSimple c = createCloudlet();
        c.setRequiredFiles(null);
        assertNotNull(c.getRequiredFiles());
    }

    @Test
    public void testRequiredFiles2() {
        final CloudletSimple c = createCloudlet();
        final String files[] = {FILE1, FILE2, FILE3};

        for (final String file : files) {
            c.addRequiredFile(file);
        }

        assertTrue(c.requiresFiles()); //it has required files
    }

}
