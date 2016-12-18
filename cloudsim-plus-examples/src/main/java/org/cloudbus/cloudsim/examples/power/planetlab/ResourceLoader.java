package org.cloudbus.cloudsim.examples.power.planetlab;

import java.net.URL;

/**
 * Loads a resource file/directory that is contained inside a given directory of a class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class ResourceLoader {
    /**
     * Gets the path of a folder where a given class is
     * @param klass the class to get its folder path
     * @param name the name of the sub-folder inside the class folder
     * @return the absolute path of the sub-folder inside the class folder
     */
    static String getResourceFolder(Class klass, String name) {
        URL resource = klass.getClassLoader().getResource(name);
        String folder = (resource == null ? "" : resource.getPath());
        return (folder == null ? "" : folder);
    }
}
