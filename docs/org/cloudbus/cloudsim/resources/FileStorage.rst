.. java:import:: java.util List

FileStorage
===========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface FileStorage extends Resource

   An interface which defines the desired functionality of a storage system in a Data Cloud that performs operations on a file system, such as file inclusion, exclusion and renaming. Classes that implement this interface should simulate the characteristics of different storage systems by setting the capacity of the storage and the maximum transfer rate. The transfer rate defines the time required to execute some common operations on the storage, e.g. storing a file, getting a file and deleting a file.

   :author: Uros Cibej, Anthony Sulistio, Manoel Campos da Silva Filho

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

getMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxTransferRate()
   :outertype: FileStorage

   Gets the maximum transfer rate of the storage in MByte/sec.

   :return: the maximum transfer rate in MEGABYTE/sec

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

hasPotentialAvailableSpace
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean hasPotentialAvailableSpace(int fileSize)
   :outertype: FileStorage

   Checks whether there is enough space on the storage for a certain file

   :param fileSize: to size of the file intended to be stored on the device
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

   :param fileSize: the size to be reserved in MEGABYTE
   :return: \ ``true``\  if reservation succeeded, \ ``false``\  otherwise

setMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method::  boolean setMaxTransferRate(int rate)
   :outertype: FileStorage

   Sets the maximum transfer rate of this storage system in MByte/sec.

   :param rate: the maximum transfer rate in MEGABYTE/sec
   :return: \ ``true``\  if the values is greater than zero and was set successfully, \ ``false``\  otherwise

