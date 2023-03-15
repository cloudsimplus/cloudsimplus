package org.cloudsimplus.cloudlets;

import org.cloudsimplus.utilizationmodels.UtilizationModelStochastic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    public void setUp() {
        cloudlet = new CloudletSimple(0, CloudletTestUtil.CLOUDLET_LENGTH, CloudletSimpleTest.PES_NUMBER);
        cloudlet.setFileSize(CloudletTestUtil.CLOUDLET_FILE_SIZE)
            .setOutputSize(CloudletTestUtil.CLOUDLET_OUTPUT_SIZE)
            .setUtilizationModelCpu(new UtilizationModelStochastic());
        cloudlet.setUtilizationModelRam(new UtilizationModelStochastic());
        cloudlet.setUtilizationModelBw(new UtilizationModelStochastic());
    }

    @Test
    public void testAddRequiredFile() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        final String files[] = {FILE1, FILE2};
        for (final String file : files) {
            assertTrue(cloudlet.addRequiredFile(file), "file should be added");  //file doesn't previously added
            assertFalse(cloudlet.addRequiredFile(file), "file shouldn't be added"); //file already added
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

    @Test()
    public void testRequiredFiles1() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        assertThrows(NullPointerException.class, () ->  cloudlet.setRequiredFiles(null));
        assertNotNull(cloudlet.getRequiredFiles());
    }

    @Test
    public void testRequiredFiles2() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        final String files[] = {FILE1, FILE2, FILE3};

        for (final String file : files) {
            cloudlet.addRequiredFile(file);
        }

        assertTrue(cloudlet.hasRequiresFiles()); //it has required files
    }

}
