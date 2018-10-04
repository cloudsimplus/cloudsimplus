.. java:import:: org.apache.commons.lang3 StringUtils

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Objects

HarddriveStorage
================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class HarddriveStorage implements FileStorage

   An implementation of a Hard Drive (HD) storage device. It simulates the behavior of a typical hard drive. The default values for this storage are those of a "\ `Maxtor DiamondMax 10 ATA <https://www.seagate.com/files/staticfiles/maxtor/en_us/documentation/data_sheets/diamondmax_10_data_sheet.pdf>`_\ " hard disk with the following parameters:

   ..

   * latency = 4.17 ms
   * avg seek time = 9 m/s
   * max transfer rate = 1064 Megabits/sec (133 MBytes/sec)

   :author: Uros Cibej, Anthony Sulistio, Manoel Campos da Silva Filho

Fields
------
DEF_LATENCY_SECS
^^^^^^^^^^^^^^^^

.. java:field:: public static final double DEF_LATENCY_SECS
   :outertype: HarddriveStorage

DEF_SEEK_TIME_SECS
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final double DEF_SEEK_TIME_SECS
   :outertype: HarddriveStorage

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

.. java:method:: @Override public double getLatency()
   :outertype: HarddriveStorage

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

getTransferTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTransferTime(String fileName)
   :outertype: HarddriveStorage

getTransferTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTransferTime(File file)
   :outertype: HarddriveStorage

getTransferTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTransferTime(int fileSize)
   :outertype: HarddriveStorage

getTransferTime
^^^^^^^^^^^^^^^

.. java:method:: protected final double getTransferTime(int fileSize, double speed)
   :outertype: HarddriveStorage

   Gets the time to transfer a file (in MBytes) according to a given transfer speed (in Mbits/sec).

   :param fileSize: the size of the file to compute the transfer time (in MBytes)
   :param speed: the speed (in MBits/sec) to compute the time to transfer the file
   :return: the transfer time in seconds

hasFile
^^^^^^^

.. java:method:: @Override public boolean hasFile(String fileName)
   :outertype: HarddriveStorage

hasPotentialAvailableSpace
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean hasPotentialAvailableSpace(int fileSize)
   :outertype: HarddriveStorage

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAmountAvailable(long amountToCheck)
   :outertype: HarddriveStorage

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAmountAvailable(double amountToCheck)
   :outertype: HarddriveStorage

isFull
^^^^^^

.. java:method:: @Override public boolean isFull()
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

.. java:method:: @Override public void setLatency(double latency)
   :outertype: HarddriveStorage

setMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setMaxTransferRate(double maxTransferRate)
   :outertype: HarddriveStorage

