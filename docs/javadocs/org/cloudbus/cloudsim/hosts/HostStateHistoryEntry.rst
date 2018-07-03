HostStateHistoryEntry
=====================

.. java:package:: org.cloudbus.cloudsim.hosts
   :noindex:

.. java:type:: public final class HostStateHistoryEntry

   Keeps historic CPU utilization data about a host.

   :author: Anton Beloglazov

Constructors
------------
HostStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean active)
   :outertype: HostStateHistoryEntry

   Instantiates a host state history entry.

   :param time: the time the data in this history entry is related to
   :param allocatedMips: the total MIPS allocated from all PEs of the Host, to running VMs, at the recorded time
   :param requestedMips: the total MIPS requested by running VMs to all PEs of the Host at the recorded time
   :param active: if the Host is active at the given time

Methods
-------
getAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method:: public double getAllocatedMips()
   :outertype: HostStateHistoryEntry

   Gets the total MIPS allocated from all PEs of the Host, to running VMs, at the recorded time.

   :return: the allocated mips

getPercentUsage
^^^^^^^^^^^^^^^

.. java:method:: public double getPercentUsage()
   :outertype: HostStateHistoryEntry

   Gets the percentage (in scale from 0 to 1) of allocated MIPS from the total requested.

getRequestedMips
^^^^^^^^^^^^^^^^

.. java:method:: public double getRequestedMips()
   :outertype: HostStateHistoryEntry

   Gets the total MIPS requested by running VMs to all PEs of the Host at the recorded time.

   :return: the requested mips

getTime
^^^^^^^

.. java:method:: public double getTime()
   :outertype: HostStateHistoryEntry

   Gets the time the data in this history entry is related to.

isActive
^^^^^^^^

.. java:method:: public boolean isActive()
   :outertype: HostStateHistoryEntry

   Checks if the Host is/was active at the recorded time.

   :return: true if is active, false otherwise

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: HostStateHistoryEntry

