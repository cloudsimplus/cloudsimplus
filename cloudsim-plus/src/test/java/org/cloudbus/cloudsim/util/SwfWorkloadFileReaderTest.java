package org.cloudbus.cloudsim.util;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwfWorkloadFileReaderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwfWorkloadFileReaderTest.class.getSimpleName());
    private static final int JOBS_AT_SWF_LCG_FILE = 188041;
    private static final String SWF_FILE = "LCG.swf";
    private static final String ZIP_FILE = "two-workload-files.zip";

    /**
     * Number of jobs of the NASA file inside the zip archive.
     */
    private static final int JOBS_AT_SWF_NASA_FILE = 18239;

    @Test
    public void readGz() {
	    readFile(SWF_FILE+".gz", JOBS_AT_SWF_LCG_FILE);
    }

    @Test
    public void readSwf() {
	    readFile(SWF_FILE, JOBS_AT_SWF_LCG_FILE);
    }

    @Test
    public void readZipWithTwoSwfFiles() {
	    readFile(ZIP_FILE, JOBS_AT_SWF_LCG_FILE + JOBS_AT_SWF_NASA_FILE);
    }

    private void readFile(String fileNameWithoutPath, int numberOfJobs) {
        final SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(fileNameWithoutPath, 1);
        final long milisecs = System.currentTimeMillis();
        final List<Cloudlet> cloudletlist = reader.generateWorkload();
        final double seconds = (System.currentTimeMillis() - milisecs)/1000.0;
        assertEquals(numberOfJobs, cloudletlist.size());
        LOGGER.info(
            "Time taken to read the file {}: {} seconds",
            fileNameWithoutPath, seconds);

        for (final Cloudlet cloudlet : cloudletlist) {
            assertTrue(cloudlet.getLength() > 0);
        }
    }
}
