.. java:import:: java.io BufferedReader

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io FileReader

.. java:import:: java.net URL

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

.. java:method:: public static BufferedReader getBufferedReader(Class klass, String resourceName) throws FileNotFoundException
   :outertype: ResourceLoader

   Gets a \ :java:ref:`BufferedReader`\  to read a resource (a file or sub-directory inside the resources directory) from its absolute path.

   :param klass: a class from the project that will be used just to assist in getting the path of the given resource
   :param resourceName: the name of the resource to get a \ :java:ref:`BufferedReader`\  for it
   :throws FileNotFoundException: when the file doesn't exist
   :return: a \ :java:ref:`BufferedReader`\  to read the resource

getFileReader
^^^^^^^^^^^^^

.. java:method:: public static FileReader getFileReader(Class klass, String resourceName) throws FileNotFoundException
   :outertype: ResourceLoader

   Gets a \ :java:ref:`FileReader`\  to read a resource (a file or sub-directory inside the resources directory) from its absolute path.

   :param klass: a class from the project that will be used just to assist in getting the path of the given resource
   :param resourceName: the name of the resource to get a \ :java:ref:`FileReader`\  for it
   :throws FileNotFoundException: when the file doesn't exist
   :return: a \ :java:ref:`FileReader`\  to read the resource

getResourcePath
^^^^^^^^^^^^^^^

.. java:method:: public static String getResourcePath(Class klass, String name)
   :outertype: ResourceLoader

   Gets the absolute path of a resource (a file or sub-directory) inside the resources directory.

   :param klass: a class from the project which will be used just to assist in getting the path of the given resource. It can can any class inside the project where a resource you are trying to get from the resources directory
   :param name: the name of the resource to get its path (that can be a file or a sub-directory inside the resources directory)
   :return: the absolute path of the resource

