Storage
=======

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public final class Storage extends ResourceManageableAbstract

   A simple storage that just manages the device capacity and raw allocated (used) space. It doesn't deals with files neither with file system operations such as file inclusion or deletion. Such a class allows managing the Storage capacity and allocation.

   :author: Manoel Campos da Silva Filho

Constructors
------------
Storage
^^^^^^^

.. java:constructor:: public Storage(long capacity)
   :outertype: Storage

   Creates a new Storage device.

   :param capacity: the storage capacity in Megabytes

