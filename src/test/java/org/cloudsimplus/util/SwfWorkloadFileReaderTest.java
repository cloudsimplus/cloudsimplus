/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.util;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.traces.SwfWorkloadFileReader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwfWorkloadFileReaderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwfWorkloadFileReaderTest.class.getSimpleName());
    private static final String SWF_FILE = "LCG.swf";
    private static final int    SWF_FILE_JOBS = 188041;
    private static final String ZIP_FILE = "NASA-iPSC-1993-3.1-cln.swf.zip";

    /**
     * Number of jobs of the NASA file inside the zip archive.
     */
    private static final int ZIP_FILE_JOBS = 18239;

    @Test
    public void readGz() {
	    assertTrue(assertCreatedCloudletsFromTrace(SWF_FILE+".gz", SWF_FILE_JOBS));
    }

    @Test
    public void readSwf() {
	    assertTrue(assertCreatedCloudletsFromTrace(SWF_FILE, SWF_FILE_JOBS));
    }

    @Test
    public void readZipSwfFiles() {
	    assertTrue(assertCreatedCloudletsFromTrace(ZIP_FILE, ZIP_FILE_JOBS));
    }

    private boolean assertCreatedCloudletsFromTrace(final String fileNameWithoutPath, final int jobsNumber) {
        final SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(fileNameWithoutPath, 1);
        final long millisecs = System.currentTimeMillis();
        final List<Cloudlet> cloudletList = reader.generateWorkload();
        final double seconds = (System.currentTimeMillis() - millisecs)/1000.0;
        assertEquals(jobsNumber, cloudletList.size());
        LOGGER.info("Time taken to read the file {}: {} seconds", fileNameWithoutPath, seconds);

        for (final Cloudlet cloudlet : cloudletList) {
            assertTrue(cloudlet.getLength() > 0);
        }

        return true;
    }
}
