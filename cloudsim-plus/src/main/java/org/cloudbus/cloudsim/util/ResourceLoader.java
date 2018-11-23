package org.cloudbus.cloudsim.util;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An utility class that loads a resource file/directory that is contained inside
 * the directory of a given class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class ResourceLoader {

    /**
     * A private constructor to avoid class instantiation.
     */
    private ResourceLoader(){}

    /**
     * Gets a {@link FileReader}
     * @param filePath the path to the file
     * @return the {@link FileReader} instance.
     */
    public static FileReader getFileReader(final String filePath){
        try {
            return new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Gets the absolute path of a resource (a file or sub-directory) inside the resources directory.
     *
     * @param klass a class from the project which will be used just to assist in getting the path
     *              of the given resource. It can can any class inside the project
     *              where a resource you are trying to get from the resources directory
     * @param name the name of the resource to get its path
     *             (that can be a file or a sub-directory inside the resources directory)
     * @return the absolute path of the resource
     */
    public static String getResourcePath(final Class klass, final String name) {
        final URL resource = getResourceUrl(klass, name);
        return resource == null || resource.getPath() == null ? "" : resource.getFile();
    }

    /**
     * Gets the {@link URL} of a resource (a file or sub-directory) inside the resources directory.
     *
     * @param klass a class from the project which will be used just to assist in getting the path
     *              of the given resource. It can can any class inside the project
     *              where a resource you are trying to get from the resources directory
     * @param name the name of the resource to get its path
     *             (that can be a file or a sub-directory inside the resources directory)
     * @return the {@link URL} of the resource
     */
    public static URL getResourceUrl(final Class klass, final String name) {
        return klass.getClassLoader().getResource(name);
    }

    /**
     * Gets the list of files contained inside a given resource directory.
     *
     * @param klass a class from the project which will be used just to assist in getting the path
     *              of the given resource. It can can any class inside the project
     *              where a resource you are trying to get from the resources directory
     * @param resourceDir the name of the resource directory to get the list of files from
     * @return
     */
    public static List<String> getResourceList(final Class klass, final String resourceDir){
        final URI uri;
        try {
            uri = getResourceUrl(klass, resourceDir).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            final Path fullPath = uriToPath(resourceDir, uri);
            final List<String> list =
                Files.walk(fullPath, 1)
                     .map(path -> resourceDir + "/" + path.getFileName().toString())
                     .collect(Collectors.toList());

            //Removes the first element which is the name of the containing directory
            if(!list.isEmpty()) {
                list.remove(0);
            }

            return list;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Path uriToPath(final String resourceDir, final URI uri) throws IOException {
        if (uri.getScheme().equals("jar")) {
            final FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            return fileSystem.getPath(resourceDir);
        }

        return Paths.get(uri);
    }

    /**
     * Gets a {@link BufferedReader} to read a resource (a file or sub-directory inside the resources directory)
     * from its absolute path.
     *
     * @param klass a class from the project that will be used just to assist in getting the path of the given resource
     * @param resourceName the name of the resource to get a {@link BufferedReader} for it
     * @return a {@link BufferedReader} to read the resource
     * @throws FileNotFoundException when the file doesn't exist
     */
    public static BufferedReader getBufferedReader(final Class klass, final String resourceName) {
        return new BufferedReader(new InputStreamReader(getInputStream(resourceName, klass)));
    }

    /**
     * Try to load the resource from a jar file, in case the user is running simulations
     * from a jar instead of directly from the IDE.
     * If the input is null, the simulation is not being executed from a jar file,
     * so try to load the resource from a directory in the filesystem.
     *
     * @param resourceName the name of the resource to get a {@link BufferedReader} for it
     * @param klass a class from the project that will be used just to assist in getting the path of the given resource
     * @return a {@link InputStream} to read the resource
     * @throws UncheckedIOException when the file cannot be accessed (such as when it doesn't exist)
     */
    public static InputStream getInputStream(final String resourceName, final Class klass) {
        //Try to load the resource from the resource directory in the filesystem
        InputStream input = klass.getClassLoader().getResourceAsStream("/"+resourceName);
        if(input != null){
            return input;
        }

        //Try to load the resource from a jar file
        input = klass.getResourceAsStream("/"+resourceName);
        if(input != null){
            return input;
        }

        //Try to load the resource from anywhere else than the resource directory
        try {
            return new FileInputStream(resourceName);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
}
