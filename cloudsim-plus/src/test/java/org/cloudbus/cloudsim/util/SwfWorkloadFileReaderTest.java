package org.cloudbus.cloudsim.util;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SwfWorkloadFileReaderTest {
    private static final String SWF_FILE = "LCG.swf";
    private static final String ZIP_FILE = "two-workload-files.zip";
    private static final int JOBS_AT_SWF_LCG_FILE = 188041;

    /**
     * Number of jobs of the NASA file inside the zip archive.
     */
    private static final int JOBS_AT_SWF_NASA_FILE = 18239;

    @Test
    public void readGz() throws IOException {
	    readFile(SWF_FILE+".gz", JOBS_AT_SWF_LCG_FILE);
    }

    @Test
    public void readSwf() throws IOException {
	    readFile(SWF_FILE, JOBS_AT_SWF_LCG_FILE);
    }

    @Test
    public void readZipWithTwoSwfFiles() throws IOException {
	    readFile(ZIP_FILE,
                JOBS_AT_SWF_LCG_FILE + JOBS_AT_SWF_NASA_FILE);
    }

    private void readFile(String fileNameWithoutPath, int numberOfJobs) throws IOException {
        final SwfWorkloadFileReader r = new SwfWorkloadFileReader("src"
                + File.separator
                + "test"
                + File.separator
                + fileNameWithoutPath, 1);
        final long milisecs = System.currentTimeMillis();
        final List<Cloudlet> cloudletlist = r.generateWorkload();
        final double seconds = (System.currentTimeMillis() - milisecs)/1000.0;
        assertEquals(numberOfJobs, cloudletlist.size());
        System.out.printf(
                "Time taken to read the file %s: %.2f seconds\n",
                fileNameWithoutPath, seconds);

        for (final Cloudlet cloudlet : cloudletlist) {
            assertTrue(cloudlet.getLength() > 0);
        }
    }
}
