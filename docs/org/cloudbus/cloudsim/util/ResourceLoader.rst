.. java:import:: java.net URL

ResourceLoader
==============

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public final class ResourceLoader

   Loads a resource file/directory that is contained inside the directory of a given class.

   :author: Manoel Campos da Silva Filho

Methods
-------
getResourcePath
^^^^^^^^^^^^^^^

.. java:method:: public static String getResourcePath(Class klass, String name)
   :outertype: ResourceLoader

   Gets the absolute path of a resource (a file or sub-directory) inside the resources directory.

   :param klass: a class from the project that will be used just to assist in getting the path of the given resource
   :param name: the name of the resource to get its path (that can be a file or a sub-directory inside the resources directory)
   :return: the absolute path of the resource

