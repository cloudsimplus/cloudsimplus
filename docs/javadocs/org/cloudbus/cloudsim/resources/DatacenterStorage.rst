.. java:import:: org.apache.commons.lang3 StringUtils

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.util DataCloudTags

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

DatacenterStorage
=================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class DatacenterStorage

   Implements the storage logic for a Datacenter. It keeps a list of storage devices \ `Disk Array <https://en.wikipedia.org/wiki/Disk_array>`_\ , as well as all basic storage related operations. This disk array can be, for instance, a list of \ :java:ref:`HarddriveStorage`\  or \ :java:ref:`SanStorage`\ .

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Abderrahman Lahiaouni

Constructors
------------
DatacenterStorage
^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterStorage()
   :outertype: DatacenterStorage

   Creates a DatacenterStorage with an empty \ :java:ref:`storage list <getStorageList()>`\ .

DatacenterStorage
^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterStorage(List<FileStorage> storageList)
   :outertype: DatacenterStorage

   Creates a DatacenterStorage with a given \ :java:ref:`storage list <getStorageList()>`\ .

   :param storageList: the storage list to set

Methods
-------
addFile
^^^^^^^

.. java:method:: public int addFile(File file)
   :outertype: DatacenterStorage

   Adds a file to the first storage device that has enough capacity

   :param file: the file to add
   :return: a tag from \ :java:ref:`DataCloudTags`\  informing the result of the operation

contains
^^^^^^^^

.. java:method:: public boolean contains(File file)
   :outertype: DatacenterStorage

   Checks whether the storageList has the given file.

   :param file: a file to be searched
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

contains
^^^^^^^^

.. java:method:: public boolean contains(String fileName)
   :outertype: DatacenterStorage

   Checks whether the storageList has the given file.

   :param fileName: a file name to be searched
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

getDatacenter
^^^^^^^^^^^^^

.. java:method:: public Datacenter getDatacenter()
   :outertype: DatacenterStorage

getStorageList
^^^^^^^^^^^^^^

.. java:method:: public List<FileStorage> getStorageList()
   :outertype: DatacenterStorage

   Gets the list of storage devices of the Datacenter, which is like a \ `Disk Array <https://en.wikipedia.org/wiki/Disk_array>`_\ .

predictFileTransferTime
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double predictFileTransferTime(List<String> requiredFiles)
   :outertype: DatacenterStorage

   Predict the total time to transfer a list of files.

   :param requiredFiles: the files to be transferred
   :return: the total predicted time to transfer the files

setAllFilesOfAllStoragesToThisDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setAllFilesOfAllStoragesToThisDatacenter()
   :outertype: DatacenterStorage

   Assigns all files of all storage devices to this Datacenter.

setDatacenter
^^^^^^^^^^^^^

.. java:method:: public void setDatacenter(Datacenter datacenter)
   :outertype: DatacenterStorage

setStorageList
^^^^^^^^^^^^^^

.. java:method:: public final DatacenterStorage setStorageList(List<FileStorage> storageList)
   :outertype: DatacenterStorage

   Sets the list of storage devices of the Datacenter, which is like a \ `Disk Array <https://en.wikipedia.org/wiki/Disk_array>`_\ .

   :param storageList: the new storage list

