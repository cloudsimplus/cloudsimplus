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

import lombok.SneakyThrows;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class that loads a resource file/directory that is contained inside
 * the directory of a given class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class ResourceLoader {

    /**
     * A private constructor to avoid class instantiation.
     */
    private ResourceLoader(){/**/}

    /**
     * Instantiates a {@link BufferedReader} to read a file
     * (a file or subdirectory inside the resources' directory) from its path.
     *
     * @param filePath the path of the resource to get a {@link BufferedReader} for it
     * @param klass a class from the project that will be used just to assist in getting the path of the given resource
     * @return a {@link BufferedReader} to read the resource
     * @throws FileNotFoundException when the file doesn't exist
     */
    public static BufferedReader newBufferedReader(final String filePath, final Class klass) {
        return new BufferedReader(newInputStreamReader(filePath, klass));
    }

    /**
     * Instantiates a {@link InputStreamReader} to read a file,
     * trying to load the file from a jar file, in case the user is running simulations from a jar package.
     * If it can't get a reader directly, the simulation is not being executed from a jar package,
     * so try to load the file from a directory in the filesystem.
     *
     * @param filePath the path of the file to get a reader for it
     * @param klass a class from the project that will be used just to assist in getting the path of the given resource
     * @return a {@link InputStreamReader} to read the resource
     * @throws UncheckedIOException when the file cannot be accessed (such as when it doesn't exist)
     */
    public static InputStreamReader newInputStreamReader(final String filePath, final Class klass) {
        return new InputStreamReader(newInputStream(filePath, klass));
    }

    /**
     * Instantiates a {@link InputStreamReader} to read a file <b>outside</b> the resource directory.
     *
     * @param filePath the path to the file
     * @return a {@link InputStreamReader} to read the resource
     * @throws UncheckedIOException when the file doesn't exist or can't be accessed
     */
    public static InputStreamReader newInputStreamReader(final String filePath) {
        try {
            return new InputStreamReader(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Instantiates a {@link InputStream} to read a file,
     * trying to load the file from a jar file, in case the user is running simulations from a jar package.
     * If it can't get a reader directly, the simulation is not being executed from a jar package,
     * so try to load the file from a directory in the filesystem.
     *
     * @param filePath the path of the file to get a reader for it
     * @param klass a class from the project that will be used just to assist in getting the path of the given resource
     * @return a {@link InputStreamReader} to read the resource
     * @throws UncheckedIOException when the file doesn't exist or can't be accessed
     */
    public static InputStream newInputStream(final String filePath, final Class klass) {
        // Try to load the resource from the resource directory in the filesystem
        var inputStream = klass.getClassLoader().getResourceAsStream(File.separator+filePath);
        if(inputStream != null){
            return inputStream;
        }

        // Try to load the resource from a jar file
        inputStream = klass.getResourceAsStream(File.separator+filePath);
        if(inputStream != null){
            return inputStream;
        }

        // Try to load the resource from anywhere else than the resource directory
        try {
            return Files.newInputStream(Paths.get(filePath));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Gets the absolute path of a resource (a file or subdirectory) inside the resources' directory.
     *
     * @param klass a class from the project which will be used just to assist in getting the path
     *              of the given resource. It can be any class inside the project
     *              where a resource you are trying to get from the resources directory
     * @param name the name of the resource to get its path
     *             (that can be a file or a subdirectory inside the resources' directory)
     * @return the absolute path of the resource
     */
    public static String getResourcePath(final Class klass, final String name) {
        final URL url = getResourceUrl(klass, name);
        return url == null || url.getPath() == null ? "" : url.getFile();
    }

    /**
     * Gets the {@link URL} of a resource (a file or subdirectory) inside the resources' directory.
     *
     * @param klass a class from the project which will be used just to assist in getting the path
     *              of the given resource. It can be any class inside the project
     *              where a resource you are trying to get from the resources directory
     * @param name the name of the resource to get its path
     *             (that can be a file or a subdirectory inside the resources' directory)
     * @return the {@link URL} of the resource
     */
    public static URL getResourceUrl(final Class klass, final String name) {
        return klass.getClassLoader().getResource(name);
    }

    /**
     * Gets the list of files contained inside a given resource directory.
     *
     * @param klass a class from the project which will be used just to assist in getting the path
     *              of the given resource. It can be any class inside the project
     *              where a resource you are trying to get from the resources directory
     * @param resourceDir the name of the resource directory to get the list of files from
     * @return the file name list
     */
    @SneakyThrows(IOException.class)
    public static List<String> getResourceList(final Class klass, final String resourceDir){
        final var uri = getResourceUri(klass, resourceDir);
        final Path fullPath = uriToPath(resourceDir, uri);
        try(var pathStream = Files.walk(fullPath, 1)) {
            final var fileNameList =
                     pathStream.map(path -> resourceDir + File.separator + path.getFileName().toString())
                     .collect(Collectors.toList());

            // Removes the first element which is the name of the containing directory
            if(!fileNameList.isEmpty()) {
                fileNameList.remove(0);
            }

            return fileNameList;
        }
    }

    private static URI getResourceUri(final Class klass, final String resourceDir) {
        try {
            return getResourceUrl(klass, resourceDir).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SneakyThrows(IOException.class)
    private static Path uriToPath(final String resourceDir, final URI uri){
        if (uri.getScheme().equals("jar")) {
            try (var fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                return fileSystem.getPath(resourceDir);
            }
        }

        return Paths.get(uri);
    }
}
