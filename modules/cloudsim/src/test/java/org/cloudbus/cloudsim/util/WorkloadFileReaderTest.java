package org.cloudbus.cloudsim.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkloadFileReaderTest {
    private static final String SWF_FILE = "LCG.swf";
    private static final String ZIP_FILE = "two-workload-files.zip";
    private static final int NUMBER_OF_JOGS_AT_SWF_LCG_FILE = 188041;
    
    /**
     * Number of jobs of the NASA file inside the zip archive.
     */
    private static final int NUMBER_OF_JOGS_AT_SWF_NASA_FILE = 18239;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void readGz() throws FileNotFoundException {
	readFile(SWF_FILE+".gz", NUMBER_OF_JOGS_AT_SWF_LCG_FILE);
    }
    
    @Test
    public void readSwf() throws FileNotFoundException {
	readFile(SWF_FILE, NUMBER_OF_JOGS_AT_SWF_LCG_FILE);
    }

    @Test
    public void readZipWithTwoSwfFiles() throws FileNotFoundException {
	readFile(ZIP_FILE, 
                NUMBER_OF_JOGS_AT_SWF_LCG_FILE+NUMBER_OF_JOGS_AT_SWF_NASA_FILE);
    }
    
    private void readFile(String fileNameWithoutPath, int numberOfJobs) throws FileNotFoundException {
        WorkloadModel r = new WorkloadFileReader("src"
                + File.separator
                + "test"
                + File.separator
                + fileNameWithoutPath, 1);
        long milisecs = System.currentTimeMillis();
        List<Cloudlet> cloudletlist = r.generateWorkload();
        double seconds = (System.currentTimeMillis() - milisecs)/1000.0;
        assertEquals(numberOfJobs, cloudletlist.size());
        System.out.printf(
                "Time taken to read the file %s: %.2f seconds\n", 
                fileNameWithoutPath, seconds);
        
        for (Cloudlet cloudlet : cloudletlist) {
            assertTrue(cloudlet.getCloudletLength() > 0);
        }
    }
    
    
}
