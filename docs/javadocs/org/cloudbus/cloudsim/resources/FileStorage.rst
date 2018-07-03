.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.network.switches Switch

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

FileStorage
===========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface FileStorage extends Resource

   An interface which defines the desired functionality of a storage system in a Data Cloud that performs operations on a file system, such as file inclusion, exclusion and renaming. Classes that implement this interface should simulate the characteristics of different storage systems by setting the capacity of the storage and the maximum transfer rate. The transfer rate defines the time required to execute some common operations on the storage, e.g. storing a file, getting a file and deleting a file.

   :author: Uros Cibej, Anthony Sulistio, Manoel Campos da Silva Filho

Fields
------
FILE_NOT_FOUND
^^^^^^^^^^^^^^

.. java:field::  int FILE_NOT_FOUND
   :outertype: FileStorage

Methods
-------
addFile
^^^^^^^

.. java:method::  double addFile(File file)
   :outertype: FileStorage

   Adds a file to the storage. The time taken (in seconds) for adding the specified file can also be found using \ :java:ref:`File.getTransactionTime()`\ .

   :param file: the file to be added
   :return: the time taken (in seconds) for adding the specified file or zero if there isn't available storage space.

addFile
^^^^^^^

.. java:method::  double addFile(List<File> list)
   :outertype: FileStorage

   Adds a set of files to the storage. The time taken (in seconds) for adding each file can also be found using \ :java:ref:`File.getTransactionTime()`\ .

   :param list: the files to be added
   :return: the time taken (in seconds) for adding the specified file or zero if the file is invalid or there isn't available storage space.

addReservedFile
^^^^^^^^^^^^^^^

.. java:method::  double addReservedFile(File file)
   :outertype: FileStorage

   Adds a file for which the space has already been reserved. The time taken (in seconds) for adding the specified file can also be found using \ :java:ref:`File.getTransactionTime()`\ .

   :param file: the file to be added
   :return: the time (in seconds) required to add the file

contains
^^^^^^^^

.. java:method::  boolean contains(String fileName)
   :outertype: FileStorage

   Checks whether a file exists in the storage or not.

   :param fileName: the name of the file we are looking for
   :return: \ ``true``\  if the file is in the storage, \ ``false``\  otherwise

contains
^^^^^^^^

.. java:method::  boolean contains(File file)
   :outertype: FileStorage

   Checks whether a file is stored in the storage or not.

   :param file: the file we are looking for
   :return: \ ``true``\  if the file is in the storage, \ ``false``\  otherwise

deleteFile
^^^^^^^^^^

.. java:method::  File deleteFile(String fileName)
   :outertype: FileStorage

   Removes a file from the storage. The time taken (in seconds) for deleting the specified file can be found using \ :java:ref:`File.getTransactionTime()`\ .

   :param fileName: the name of the file to be removed
   :return: the deleted file.

deleteFile
^^^^^^^^^^

.. java:method::  double deleteFile(File file)
   :outertype: FileStorage

   Removes a file from the storage. The time taken (in seconds) for deleting the specified file can also be found using \ :java:ref:`File.getTransactionTime()`\ .

   :param file: the file to be removed
   :return: the time taken (in seconds) for deleting the specified file

getFile
^^^^^^^

.. java:method::  File getFile(String fileName)
   :outertype: FileStorage

   Gets the file with the specified name. The time taken (in seconds) for getting the specified file can also be found using \ :java:ref:`File.getTransactionTime()`\ .

   :param fileName: the name of the needed file
   :return: the file with the specified filename; null if not found

getFileList
^^^^^^^^^^^

.. java:method::  List<File> getFileList()
   :outertype: FileStorage

   Gets a \ **read-only**\  list with all files stored on the device.

   :return: a List of files

getFileNameList
^^^^^^^^^^^^^^^

.. java:method::  List<String> getFileNameList()
   :outertype: FileStorage

   Gets a \ **read-only**\  list with the names of all files stored on the device.

   :return: a List of file names

getLatency
^^^^^^^^^^

.. java:method::  double getLatency()
   :outertype: FileStorage

   Gets the latency of this hard drive in seconds.

   :return: the latency in seconds

getMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxTransferRate()
   :outertype: FileStorage

   Gets the maximum local transfer rate of the storage in \ **Mbits/sec**\ , i.e., the physical device reading speed.

   :return: the maximum transfer rate in Mbits/sec

   **See also:** :java:ref:`.setMaxTransferRate(double)`

getName
^^^^^^^

.. java:method::  String getName()
   :outertype: FileStorage

   :return: the name of the storage device

getNumStoredFile
^^^^^^^^^^^^^^^^

.. java:method::  int getNumStoredFile()
   :outertype: FileStorage

   Gets the number of files stored on this device.

   :return: the number of stored files

getTransferTime
^^^^^^^^^^^^^^^

.. java:method::  double getTransferTime(String fileName)
   :outertype: FileStorage

   Gets the transfer time of a given file.

   :param fileName: the name of the file to compute the transfer time (where its size is defined in MByte)
   :return: the transfer time in seconds or \ :java:ref:`FILE_NOT_FOUND`\  if the file was not found in this storage device

getTransferTime
^^^^^^^^^^^^^^^

.. java:method::  double getTransferTime(File file)
   :outertype: FileStorage

   Gets the transfer time of a given file.

   :param file: the file to compute the transfer time (where its size is defined in MByte)
   :return: the transfer time in seconds

getTransferTime
^^^^^^^^^^^^^^^

.. java:method::  double getTransferTime(int fileSize)
   :outertype: FileStorage

   Gets the transfer time of a given file.

   :param fileSize: the size of the file to compute the transfer time (in MByte)
   :return: the transfer time in seconds

hasFile
^^^^^^^

.. java:method::  boolean hasFile(String fileName)
   :outertype: FileStorage

   Checks if the storage device has a specific file.

   :param fileName: the name of the file to check if it's contained in this storage device.
   :return: true if the storage device has the file, false otherwise.

hasPotentialAvailableSpace
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean hasPotentialAvailableSpace(int fileSize)
   :outertype: FileStorage

   Checks whether there is enough space on the storage for a certain file

   :param fileSize: size of the file intended to be stored on the device (in MByte)
   :return: \ ``true``\  if enough space available, \ ``false``\  otherwise

renameFile
^^^^^^^^^^

.. java:method::  boolean renameFile(File file, String newName)
   :outertype: FileStorage

   Renames a file on the storage. The time taken (in seconds) for renaming the specified file can also be found using \ :java:ref:`File.getTransactionTime()`\ .

   :param file: the file we would like to rename
   :param newName: the new name of the file
   :return: \ ``true``\  if the renaming succeeded, \ ``false``\  otherwise

reserveSpace
^^^^^^^^^^^^

.. java:method::  boolean reserveSpace(int fileSize)
   :outertype: FileStorage

   Makes reservation of space on the storage to store a file.

   :param fileSize: the size to be reserved (in MByte)
   :return: \ ``true``\  if reservation succeeded, \ ``false``\  otherwise

setLatency
^^^^^^^^^^

.. java:method::  void setLatency(double latency)
   :outertype: FileStorage

   Sets the latency of this hard drive in seconds.

   :param latency: the new latency in seconds
   :throws IllegalArgumentException: if the value is lower than 0

setMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method::  void setMaxTransferRate(double maxTransferRate)
   :outertype: FileStorage

   Sets the maximum transfer rate of this storage system in \ **Mbits/sec**\ , i.e., the physical device reading speed.

   Despite disk transfer rate is usually defined in MBytes/sec, it's being used Mbits/sec everywhere to avoid confusions, since \ :java:ref:`Host`\ , \ :java:ref:`Vm`\ , \ :java:ref:`Switch`\  and \ :java:ref:`SanStorage`\  use such a data unit.

   :param maxTransferRate: the maximum transfer rate in Mbits/sec
   :throws IllegalArgumentException: if the value is lower than 1

