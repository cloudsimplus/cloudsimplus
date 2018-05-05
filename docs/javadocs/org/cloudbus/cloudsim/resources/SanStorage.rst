SanStorage
==========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class SanStorage extends HarddriveStorage

   SanStorage represents a Storage Area Network (SAN) composed of a set of hard disks connected in a LAN. Capacity of individual disks are abstracted, thus only the overall capacity of the SAN is considered.

   \ ``WARNING``\ : This class is not yet fully functional. Effects of network contention are not considered in the simulation. So, time for file transfer is underestimated in the presence of high network load.

   :author: Rodrigo N. Calheiros

Constructors
------------
SanStorage
^^^^^^^^^^

.. java:constructor:: public SanStorage(long capacity, double bandwidth, double networkLatency) throws IllegalArgumentException
   :outertype: SanStorage

   Creates a new SAN with a given capacity, latency, and bandwidth of the network connection.

   :param capacity: Total storage capacity of the SAN
   :param bandwidth: Network bandwidth (in Megabits/s)
   :param networkLatency: Network latency (in seconds)
   :throws IllegalArgumentException: when the name and the capacity are not valid

SanStorage
^^^^^^^^^^

.. java:constructor:: public SanStorage(String name, long capacity, double bandwidth, double networkLatency)
   :outertype: SanStorage

   Creates a new SAN with a given capacity, latency, and bandwidth of the network connection and with a specific name.

   :param name: the name of the new storage device
   :param capacity: Storage device capacity
   :param bandwidth: Network bandwidth (in Megabits/s)
   :param networkLatency: Network latency (in seconds)
   :throws IllegalArgumentException: when the name and the capacity are not valid

Methods
-------
addFile
^^^^^^^

.. java:method:: @Override public double addFile(File file)
   :outertype: SanStorage

addReservedFile
^^^^^^^^^^^^^^^

.. java:method:: @Override public double addReservedFile(File file)
   :outertype: SanStorage

deleteFile
^^^^^^^^^^

.. java:method:: @Override public double deleteFile(File file)
   :outertype: SanStorage

getBandwidth
^^^^^^^^^^^^

.. java:method:: public double getBandwidth()
   :outertype: SanStorage

   Get the bandwidth of the SAN network.

   :return: the bandwidth

getMaxTransferRate
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxTransferRate()
   :outertype: SanStorage

   Gets the maximum transfer rate of the SAN in MByte/sec. It is defined as the minimum value between the disk rate and the SAN bandwidth. Even the bandwidth being faster the the disk rate, the max transfer rate is limited by the disk speed.

   :return: the max transfer in MEGABYTE/sec

getNetworkLatency
^^^^^^^^^^^^^^^^^

.. java:method:: public double getNetworkLatency()
   :outertype: SanStorage

   Gets the SAN's network latency.

   :return: the SAN's network latency

