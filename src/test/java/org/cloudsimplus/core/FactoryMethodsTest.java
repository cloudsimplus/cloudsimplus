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

import org.cloudsimplus.cloudlets.CloudletAbstract;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.HostAbstract;
import org.cloudsimplus.vms.VmAbstract;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks that the implementations of [org.cloudsimplus.vms.Vm], [org.cloudsimplus.hosts.Host],
/// [org.cloudsimplus.cloudlets.Cloudlet] and [Datacenter] are created by `of()` factory methods,
/// which return the interface type, instead of public constructors.
/// That makes user code get references of the interface type,
/// which don't have the methods that only the framework must call.
///
/// The classes are found by scanning the compiled framework classes,
/// so that new subclasses are checked too.
/// Static methods aren't overridden, so if a subclass doesn't declare its own `of()`,
/// calling it through the subclass name would create an object of the parent class.
class FactoryMethodsTest {
    private static final String FACTORY_NAME = "of";
    private static final String CLASS_EXTENSION = ".class";
    private static final List<Class<?>> BASE_TYPES =
        List.of(VmAbstract.class, HostAbstract.class, CloudletAbstract.class, Datacenter.class);

    @ParameterizedTest
    @MethodSource("implementationClasses")
    void hasNoPublicConstructors(final Class<?> implClass) {
        final var publicConstructors = stream(implClass.getConstructors()).toList();
        assertTrue(publicConstructors.isEmpty(), () -> implClass.getSimpleName() + " has public constructors: " + publicConstructors);
    }

    @ParameterizedTest
    @MethodSource("implementationClasses")
    void declaresOneFactoryForEachProtectedConstructor(final Class<?> implClass) {
        final Set<List<Class<?>>> constructorParams = stream(implClass.getDeclaredConstructors())
            .filter(constructor -> Modifier.isProtected(constructor.getModifiers()))
            .map(Constructor::getParameterTypes)
            .map(List::of)
            .collect(toSet());
        final Set<List<Class<?>>> factoryParams = factories(implClass)
            .map(Method::getParameterTypes)
            .map(List::of)
            .collect(toSet());

        assertFalse(constructorParams.isEmpty(), () -> implClass.getSimpleName() + " has no protected constructors");
        assertEquals(constructorParams, factoryParams, () -> implClass.getSimpleName() + " factories don't match its protected constructors");
    }

    @ParameterizedTest
    @MethodSource("implementationClasses")
    void factoriesReturnASupertypeOfTheClass(final Class<?> implClass) {
        factories(implClass).forEach(factory ->
            assertTrue(factory.getReturnType().isAssignableFrom(implClass),
                () -> "%s.%s returns %s".formatted(implClass.getSimpleName(), factory, factory.getReturnType())));
    }

    /// {@return the public static `of()` methods declared in a given class (not the inherited ones)}
    /// @param implClass the class to get the factories from
    private static Stream<Method> factories(final Class<?> implClass) {
        return stream(implClass.getDeclaredMethods())
            .filter(method -> method.getName().equals(FACTORY_NAME))
            .filter(method -> Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()));
    }

    /// {@return the public concrete classes of the framework that implement one of the [#BASE_TYPES]}
    static Stream<Class<?>> implementationClasses() {
        final Path classesDir = classesDir();
        try (var files = Files.walk(classesDir)) {
            final var classes = files
                .map(file -> classesDir.relativize(file).toString())
                // ignores inner classes and module-info/package-info files
                .filter(name -> name.endsWith(CLASS_EXTENSION) && !name.contains("$") && !name.contains("-info"))
                .map(FactoryMethodsTest::loadClass)
                .filter(FactoryMethodsTest::isImplementationClass)
                .toList();
            assertFalse(classes.isEmpty(), "No implementation class was found in " + classesDir);
            return classes.stream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static boolean isImplementationClass(final Class<?> klass) {
        final int modifiers = klass.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !klass.isInterface() &&
               BASE_TYPES.stream().anyMatch(type -> type.isAssignableFrom(klass));
    }

    /// {@return the directory with the compiled framework classes (not the test classes)}
    private static Path classesDir() {
        try {
            final Path classFile = Path.of(VmSimple.class.getResource(VmSimple.class.getSimpleName() + CLASS_EXTENSION).toURI());
            final int packageDepth = VmSimple.class.getPackageName().split("\\.").length;
            Path dir = classFile.getParent();
            for (int i = 0; i < packageDepth; i++) {
                dir = dir.getParent();
            }

            return dir;
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /// Loads a class without initializing it.
    /// @param relativePath the path of the class file, relative to the classes directory
    private static Class<?> loadClass(final String relativePath) {
        final String className = relativePath.replace(CLASS_EXTENSION, "").replace('/', '.').replace('\\', '.');
        try {
            return Class.forName(className, false, FactoryMethodsTest.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
