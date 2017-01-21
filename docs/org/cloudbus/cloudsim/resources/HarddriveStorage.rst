.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

HarddriveStorage
================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class HarddriveStorage implements FileStorage

   An implementation of a Hard Drive (HD) storage device. It simulates the behavior of a typical hard drive. The default values for this storage are those of a "Maxtor DiamonMax 10 ATA" hard disk with the following parameters:

   ..

   * latency = 4.17 ms
   * avg seek time = 9 m/s
   * max transfer rate = 133 MEGABYTE/sec

   :author: Uros Cibej, Anthony Sulistio

Constructors
------------
HarddriveStorage
^^^^^^^^^^^^^^^^

.. java:constructor:: public HarddriveStorage(String name, long capacity) throws IllegalArgumentException
   :outertype: HarddriveStorage

   Creates a new hard drive storage with a given name and capacity.

   :param name: the name of the new hard drive storage
   :param capacity: the capacity in MByte
   :throws IllegalArgumentException: when the name and the capacity are not valid

HarddriveStorage
^^^^^^^^^^^^^^^^

.. java:constructor:: public HarddriveStorage(long capacity) throws IllegalArgumentException
   :outertype: HarddriveStorage

   Creates a new hard drive storage with a given capacity. In this case the name of the storage is a default name.

   :param capacity: the capacity in MByte
   :throws IllegalArgumentException: when the name and the capacity are not valid

Methods
-------
addFile
^^^^^^^

.. java:method:: @Override public double addFile(File file)
   :outertype: HarddriveStorage

   {@inheritDoc}

   First, the method checks if there is enough space on the storage, then it checks if the file with the same name is already taken to avoid duplicate filenames.

   :param file: {@inheritDoc}
   :return: {@inheritDoc}

addFile
^^^^^^^

.. java:method:: @Override public double addFile(List<File> list)
   :outertype: HarddriveStorage

addReservedFile
^^^^^^^^^^^^^^^

.. java:method:: @Override public double addReservedFile(File file)
   :outertype: HarddriveStorage

contains
^^^^^^^^

.. java:method:: @Override public boolean contains(String fileName)
   :outertype: HarddriveStorage

contains
^^^^^^^^

.. java:method:: @Override public boolean contains(File file)
   :outertype: HarddriveStorage

deleteFile
^^^^^^^^^^

.. java:method:: @Override public File deleteFile(String fileName)
   :outertype: HarddriveStorage

deleteFile
^^^^^^^^^^

.. java:method:: @Override public double deleteFile(File file)
   :outertype: HarddriveStorage

getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: HarddriveStorage

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: HarddriveStorage

getAvgSeekTime
^^^^^^^^^^^^^^

.. java:method:: public double getAvgSeekTime()
   :outertype: HarddriveStorage

   Gets the average seek time of the hard drive in seconds.

   :return: the average seek time in seconds

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: HarddriveStorage

getFile
^^^^^^^

.. java:method:: @Override public File getFile(String fileName)
   :outertype: HarddriveStorage

getFileList
^^^^^^^^^^^

.. java:method:: @Override public List<File> getFileList()
   :outertype: HarddriveStorage

getFileNameList
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<String> getFileNameList()
   :outertype: HarddriveStorage

getLatency
^^^^^^^^^^

.. java:method:: public double getLatency()
   :outertype: HarddriveStorage

   Gets the latency of this hard drive in seconds.

   :return: the latency in seconds

getMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxTransferRate()
   :outertype: HarddriveStorage

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: HarddriveStorage

getNumStoredFile
^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumStoredFile()
   :outertype: HarddriveStorage

hasPotentialAvailableSpace
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean hasPotentialAvailableSpace(int fileSize)
   :outertype: HarddriveStorage

isFull
^^^^^^

.. java:method:: @Override public boolean isFull()
   :outertype: HarddriveStorage

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountAvailable(long amountToCheck)
   :outertype: HarddriveStorage

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountAvailable(double amountToCheck)
   :outertype: HarddriveStorage

renameFile
^^^^^^^^^^

.. java:method:: @Override public boolean renameFile(File file, String newName)
   :outertype: HarddriveStorage

reserveSpace
^^^^^^^^^^^^

.. java:method:: @Override public boolean reserveSpace(int fileSize)
   :outertype: HarddriveStorage

setAvgSeekTime
^^^^^^^^^^^^^^

.. java:method:: public boolean setAvgSeekTime(double seekTime)
   :outertype: HarddriveStorage

   Sets the average seek time of the storage in seconds.

   :param seekTime: the average seek time in seconds
   :return: \ ``true``\  if the values is greater than zero and was set successfully, \ ``false``\  otherwise

setAvgSeekTime
^^^^^^^^^^^^^^

.. java:method:: public boolean setAvgSeekTime(double seekTime, ContinuousDistribution gen)
   :outertype: HarddriveStorage

   Sets the average seek time and a new generator of seek times in seconds. The generator determines a randomized seek time.

   :param seekTime: the average seek time in seconds
   :param gen: the ContinuousGenerator which generates seek times
   :return: \ ``true``\  if the values is greater than zero and was set successfully, \ ``false``\  otherwise

setLatency
^^^^^^^^^^

.. java:method:: public boolean setLatency(double latency)
   :outertype: HarddriveStorage

   Sets the latency of this hard drive in seconds.

   :param latency: the new latency in seconds
   :return: \ ``true``\  if the setting succeeded, \ ``false``\  otherwise

setMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setMaxTransferRate(int rate)
   :outertype: HarddriveStorage

