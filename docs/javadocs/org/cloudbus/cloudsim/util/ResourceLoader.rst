.. java:import:: java.net URI

.. java:import:: java.net URISyntaxException

.. java:import:: java.net URL

.. java:import:: java.nio.file FileSystem

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util.stream Stream

ResourceLoader
==============

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class ResourceLoader

   Loads a resource file/directory that is contained inside the directory of a given class.

   :author: Manoel Campos da Silva Filho

Methods
-------
getBufferedReader
^^^^^^^^^^^^^^^^^

.. java:method:: public static BufferedReader getBufferedReader(Class klass, String resourceName)
   :outertype: ResourceLoader

   Gets a \ :java:ref:`BufferedReader`\  to read a resource (a file or sub-directory inside the resources directory) from its absolute path.

   :param klass: a class from the project that will be used just to assist in getting the path of the given resource
   :param resourceName: the name of the resource to get a \ :java:ref:`BufferedReader`\  for it
   :throws FileNotFoundException: when the file doesn't exist
   :return: a \ :java:ref:`BufferedReader`\  to read the resource

getFileReader
^^^^^^^^^^^^^

.. java:method:: public static FileReader getFileReader(String filePath)
   :outertype: ResourceLoader

   Gets a \ :java:ref:`FileReader`\

   :param filePath: the path to the file
   :return: the \ :java:ref:`FileReader`\  instance.

getInputStream
^^^^^^^^^^^^^^

.. java:method:: public static InputStream getInputStream(Class klass, String resourceName)
   :outertype: ResourceLoader

   Try to load the resource from a jar file, in case the user is running simulations from a jar instead of directly from the IDE. If the input is null, the simulation is not being executed from a jar file, so try to load the resource from a directory in the filesystem.

   :param klass: a class from the project that will be used just to assist in getting the path of the given resource
   :param resourceName: the name of the resource to get a \ :java:ref:`BufferedReader`\  for it
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)
   :return: a \ :java:ref:`InputStream`\  to read the resource

getResourceList
^^^^^^^^^^^^^^^

.. java:method:: public static List<String> getResourceList(Class klass, String resourceDir)
   :outertype: ResourceLoader

   Gets the list of files contained inside a given resource directory.

   :param klass: a class from the project which will be used just to assist in getting the path of the given resource. It can can any class inside the project where a resource you are trying to get from the resources directory
   :param resourceDir: the name of the resource directory to get the list of files from

getResourcePath
^^^^^^^^^^^^^^^

.. java:method:: public static String getResourcePath(Class klass, String name)
   :outertype: ResourceLoader

   Gets the absolute path of a resource (a file or sub-directory) inside the resources directory.

   :param klass: a class from the project which will be used just to assist in getting the path of the given resource. It can can any class inside the project where a resource you are trying to get from the resources directory
   :param name: the name of the resource to get its path (that can be a file or a sub-directory inside the resources directory)
   :return: the absolute path of the resource

getResourceUrl
^^^^^^^^^^^^^^

.. java:method:: public static URL getResourceUrl(Class klass, String name)
   :outertype: ResourceLoader

   Gets the \ :java:ref:`URL`\  of a resource (a file or sub-directory) inside the resources directory.

   :param klass: a class from the project which will be used just to assist in getting the path of the given resource. It can can any class inside the project where a resource you are trying to get from the resources directory
   :param name: the name of the resource to get its path (that can be a file or a sub-directory inside the resources directory)
   :return: the \ :java:ref:`URL`\  of the resource

