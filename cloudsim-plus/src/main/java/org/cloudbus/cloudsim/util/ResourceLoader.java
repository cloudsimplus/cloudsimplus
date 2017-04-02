package org.cloudbus.cloudsim.util;

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
     * @param klass a class from the project that will be used just to assist in getting the path of the given resource
     * @param name the name of the resource to get its path
     *             (that can be a file or a sub-directory inside the resources directory)
     * @return the absolute path of the resource
     */
    public static String getResourcePath(Class klass, String name) {
        final URL resource = klass.getClassLoader().getResource(name);
        final String folder = (resource == null ? "" : resource.getPath());
        return (folder == null ? "" : folder);
    }

}
