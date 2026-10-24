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
package org.cloudsimplus.cloudlets;

import org.cloudsimplus.utilizationmodels.UtilizationModelStochastic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// @author	Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since	CloudSim Plus 1.3.0
public class CloudletSimpleFilesTest {
    private static final String FILE1 = "file1.txt";
    private static final String FILE2 = "file2.txt";
    private static final String FILE3 = "file3.txt";
    private static final String FILE_INEXISTENT = "file-inexistent.txt";

    private CloudletSimple cloudlet;

    @BeforeEach
    public void setUp() {
        cloudlet = new CloudletSimple(0, CloudletTestUtil.CLOUDLET_LENGTH, CloudletSimpleTest.CORES_NUMBER);
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

    @Test
    public void testAddRequiredFileAfterSetImmutableList() {
        cloudlet.setRequiredFiles(List.of(FILE1));
        assertTrue(cloudlet.addRequiredFile(FILE2));
        assertEquals(List.of(FILE1, FILE2), cloudlet.getRequiredFiles());
    }

    @Test
    public void testSetRequiredFilesCopiesGivenList() {
        final var files = new ArrayList<>(List.of(FILE1));
        cloudlet.setRequiredFiles(files);
        files.add(FILE2);
        assertEquals(List.of(FILE1), cloudlet.getRequiredFiles());
    }

    @Test
    public void testDeleteRequiredFileWhenNoFiles() {
        assertFalse(cloudlet.deleteRequiredFile(FILE1));
        assertFalse(cloudlet.hasRequiresFiles());
    }

    @Test
    public void testAddRequiredFileAfterDeletingAllFiles() {
        cloudlet.addRequiredFile(FILE1);
        cloudlet.deleteRequiredFile(FILE1);
        assertTrue(cloudlet.addRequiredFile(FILE2));
        assertEquals(List.of(FILE2), cloudlet.getRequiredFiles());
    }

    @Test
    public void testGetRequiredFilesIsReadOnly() {
        cloudlet.addRequiredFile(FILE1);
        assertThrows(UnsupportedOperationException.class, () -> cloudlet.getRequiredFiles().add(FILE2));
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
