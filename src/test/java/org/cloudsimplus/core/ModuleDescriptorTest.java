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
package org.cloudsimplus.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks if the `module-info.java` is in sync with the project packages.
/// It's easy to forget updating that file when a package is created,
/// which makes the package inaccessible to projects that use CloudSim Plus as a Java module.
///
/// Tests run on the classpath (where the module descriptor is ignored),
/// so the test reads the compiled `module-info.class` directly.
class ModuleDescriptorTest {
    private static final String MODULE_INFO_CLASS = "module-info.class";

    /// The packages that Gson accesses by reflection to create objects from JSON files.
    private static final Set<String> PACKAGES_OPENED_TO_GSON = Set.of("org.cloudsimplus.slametrics", "org.cloudsimplus.vmtemplates");

    private static Path classesDir;
    private static ModuleDescriptor descriptor;

    @BeforeAll
    static void readModuleDescriptor() throws URISyntaxException, IOException {
        classesDir = Path.of(CloudSimPlus.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        try (InputStream input = Files.newInputStream(classesDir.resolve(MODULE_INFO_CLASS))) {
            descriptor = ModuleDescriptor.read(input);
        }
    }

    @Test
    void allPackagesAreExported() throws IOException {
        final Set<String> exported = descriptor.exports().stream()
                                               .map(ModuleDescriptor.Exports::source)
                                               .collect(Collectors.toCollection(TreeSet::new));
        assertEquals(packagesWithClasses(), exported, "module-info.java must export all packages");
    }

    @Test
    void packagesUsedByGsonAreOpened() {
        final Set<String> openedToGson = descriptor.opens().stream()
                                                   .filter(opens -> opens.targets().contains("com.google.gson"))
                                                   .map(ModuleDescriptor.Opens::source)
                                                   .collect(Collectors.toSet());
        assertTrue(openedToGson.containsAll(PACKAGES_OPENED_TO_GSON), "Packages opened to Gson: " + openedToGson);
    }

    /// {@return the name of the packages that have at least one compiled class}
    private static Set<String> packagesWithClasses() throws IOException {
        try (Stream<Path> files = Files.walk(classesDir)) {
            return files.filter(file -> file.toString().endsWith(".class"))
                        .filter(file -> !file.endsWith(MODULE_INFO_CLASS))
                        .map(file -> classesDir.relativize(file.getParent()).toString().replace(file.getFileSystem().getSeparator(), "."))
                        .collect(Collectors.toCollection(TreeSet::new));
        }
    }
}
