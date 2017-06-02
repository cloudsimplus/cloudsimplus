package org.cloudbus.cloudsim.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

/**
 * Loads a resource file/directory that is contained inside the directory of a given class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class ResourceLoader {

    /**
     * A private constructor to avoid class instantiation.
     */
    private ResourceLoader(){}

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
    public static String getResourcePath(Class klass, String name) {
        final URL resource = klass.getClassLoader().getResource(name);
        final String folder = (resource == null ? "" : resource.getPath());
        return (folder == null ? "" : folder);
    }

    /**
     * Gets a {@link FileReader} to read a resource (a file or sub-directory inside the resources directory)
     * from its absolute path.
     *
     * @param klass a class from the project that will be used just to assist in getting the path of the given resource
     * @param resourceName the name of the resource to get a {@link FileReader} for it
     * @return a {@link FileReader} to read the resource
     * @throws FileNotFoundException when the file doesn't exist
     */
    public static FileReader getFileReader(Class klass, String resourceName) throws FileNotFoundException {
        return new FileReader(ResourceLoader.getResourcePath(klass, resourceName));
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
    public static BufferedReader getBufferedReader(Class klass, String resourceName) throws FileNotFoundException {
        return new BufferedReader(getFileReader(klass, resourceName));
    }

}
