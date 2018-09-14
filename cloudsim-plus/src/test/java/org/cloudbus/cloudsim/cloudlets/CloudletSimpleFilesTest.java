package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.cloudbus.cloudsim.cloudlets.CloudletSimpleTest.PES_NUMBER;
import static org.junit.Assert.*;

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

    @Before
    public void setUp() {
        cloudlet = new CloudletSimple(0, CloudletTestUtil.CLOUDLET_LENGTH, PES_NUMBER);
        cloudlet.setFileSize(CloudletTestUtil.CLOUDLET_FILE_SIZE)
            .setOutputSize(CloudletTestUtil.CLOUDLET_OUTPUT_SIZE)
            .setUtilizationModelCpu(new UtilizationModelStochastic())
            .setUtilizationModelRam(new UtilizationModelStochastic())
            .setUtilizationModelBw(new UtilizationModelStochastic());
    }

    @Test
    public void testAddRequiredFile() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        final String files[] = {FILE1, FILE2};
        for (final String file : files) {
            assertTrue("Method file should be added",
                cloudlet.addRequiredFile(file));  //file doesn't previously added
            assertFalse("Method file shouldn't be added",
                cloudlet.addRequiredFile(file)); //file already added
        }
    }

    @Test
    public void testDeleteRequiredFile() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        final String files[] = {FILE1, FILE2, FILE3};
        for (final String file : files) {
            cloudlet.addRequiredFile(file);
        }

        assertFalse(cloudlet.deleteRequiredFile(FILE_INEXISTENT));
        for (final String file : files) {
            assertTrue(cloudlet.deleteRequiredFile(file));
            assertFalse(cloudlet.deleteRequiredFile(file)); //already deleted
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
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        cloudlet.setRequiredFiles(null);
        assertNotNull(cloudlet.getRequiredFiles());
    }

    @Test
    public void testRequiredFiles2() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        final String files[] = {FILE1, FILE2, FILE3};

        for (final String file : files) {
            cloudlet.addRequiredFile(file);
        }

        assertTrue(cloudlet.requiresFiles()); //it has required files
    }

}
