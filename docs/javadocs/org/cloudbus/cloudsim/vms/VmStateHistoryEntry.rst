VmStateHistoryEntry
===================

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: public class VmStateHistoryEntry

   Historic data about requests and allocation of MIPS for a given VM over the time.

   :author: Anton Beloglazov

Constructors
------------
VmStateHistoryEntry
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean inMigration)
   :outertype: VmStateHistoryEntry

   Instantiates a new VmStateHistoryEntry

   :param time: the time
   :param allocatedMips: the allocated mips
   :param requestedMips: the requested mips
   :param inMigration: the is in migration

Methods
-------
equals
^^^^^^

.. java:method:: @Override public boolean equals(Object obj)
   :outertype: VmStateHistoryEntry

getAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method:: public double getAllocatedMips()
   :outertype: VmStateHistoryEntry

   Gets the allocated mips.

   :return: the allocated mips

getRequestedMips
^^^^^^^^^^^^^^^^

.. java:method:: public double getRequestedMips()
   :outertype: VmStateHistoryEntry

   Gets the requested mips.

   :return: the requested mips

getTime
^^^^^^^

.. java:method:: public double getTime()
   :outertype: VmStateHistoryEntry

   Gets the time.

   :return: the time

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: VmStateHistoryEntry

isInMigration
^^^^^^^^^^^^^

.. java:method:: public boolean isInMigration()
   :outertype: VmStateHistoryEntry

   Checks if the Vm is in migration for the current history.

   :return: true if the Vm is in migration, false otherwise

setAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method:: protected final void setAllocatedMips(double allocatedMips)
   :outertype: VmStateHistoryEntry

   Sets the allocated mips.

   :param allocatedMips: the new allocated mips

setInMigration
^^^^^^^^^^^^^^

.. java:method:: protected final void setInMigration(boolean inMigration)
   :outertype: VmStateHistoryEntry

   Defines if the Vm is in migration for the current history.

   :param inMigration: true if the Vm is in migration, false otherwise

setRequestedMips
^^^^^^^^^^^^^^^^

.. java:method:: protected final void setRequestedMips(double requestedMips)
   :outertype: VmStateHistoryEntry

   Sets the requested mips.

   :param requestedMips: the new requested mips

setTime
^^^^^^^

.. java:method:: protected final void setTime(double time)
   :outertype: VmStateHistoryEntry

   Sets the time.

   :param time: the new time

