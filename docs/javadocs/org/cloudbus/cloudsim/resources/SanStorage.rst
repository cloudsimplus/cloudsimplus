SanStorage
==========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class SanStorage extends HarddriveStorage

   SanStorage represents a Storage Area Network (SAN) composed of a set of hard disks connected in a LAN. Capacity of individual disks are abstracted, thus only the overall capacity of the SAN is considered.

   \ ``WARNING``\ : This class is not yet fully functional. Effects of network contention are not considered in the simulation. So, time for file transfer is underestimated in the presence of high network load.

   :author: Rodrigo N. Calheiros, Manoel Campos da Silva Filho

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

   Gets the bandwidth of the SAN network (in Megabits/s).

   :return: the bandwidth (in Megabits/s)

getNetworkLatency
^^^^^^^^^^^^^^^^^

.. java:method:: public double getNetworkLatency()
   :outertype: SanStorage

   Gets the SAN's network latency (in seconds).

   :return: the SAN's network latency (in seconds)

getTransferTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTransferTime(int fileSize)
   :outertype: SanStorage

   {@inheritDoc} The network latency is added to the transfer time.

   :param fileSize: {@inheritDoc}
   :return: {@inheritDoc}

setBandwidth
^^^^^^^^^^^^

.. java:method:: public final void setBandwidth(double bandwidth)
   :outertype: SanStorage

   Sets the bandwidth of the SAN network (in Megabits/s).

   :param bandwidth: the bandwidth to set (in Megabits/s)
   :throws IllegalArgumentException: when the bandwidth is lower or equal to zero

setNetworkLatency
^^^^^^^^^^^^^^^^^

.. java:method:: public final void setNetworkLatency(double networkLatency)
   :outertype: SanStorage

   Sets the latency of the SAN network (in seconds).

   :param networkLatency: the latency to set (in seconds)
   :throws IllegalArgumentException: when the latency is lower or equal to zero

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: SanStorage

