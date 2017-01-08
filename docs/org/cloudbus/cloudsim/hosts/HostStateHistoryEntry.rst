HostStateHistoryEntry
=====================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class HostStateHistoryEntry

   Stores historic data about a host.

   :author: Anton Beloglazov

Constructors
------------
HostStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive)
   :outertype: HostStateHistoryEntry

   Instantiates a new host state history entry.

   :param time: the time
   :param allocatedMips: the allocated mips
   :param requestedMips: the requested mips
   :param isActive: the is active

Methods
-------
getAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method:: public double getAllocatedMips()
   :outertype: HostStateHistoryEntry

   Gets the allocated mips.

   :return: the allocated mips

getRequestedMips
^^^^^^^^^^^^^^^^

.. java:method:: public double getRequestedMips()
   :outertype: HostStateHistoryEntry

   Gets the requested mips.

   :return: the requested mips

getTime
^^^^^^^

.. java:method:: public double getTime()
   :outertype: HostStateHistoryEntry

   Gets the time.

   :return: the time

isActive
^^^^^^^^

.. java:method:: public boolean isActive()
   :outertype: HostStateHistoryEntry

   Checks if is active.

   :return: true, if is active

setActive
^^^^^^^^^

.. java:method:: public void setActive(boolean isActive)
   :outertype: HostStateHistoryEntry

   Sets the active.

   :param isActive: the new active

setAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method:: protected void setAllocatedMips(double allocatedMips)
   :outertype: HostStateHistoryEntry

   Sets the allocated mips.

   :param allocatedMips: the new allocated mips

setRequestedMips
^^^^^^^^^^^^^^^^

.. java:method:: protected void setRequestedMips(double requestedMips)
   :outertype: HostStateHistoryEntry

   Sets the requested mips.

   :param requestedMips: the new requested mips

setTime
^^^^^^^

.. java:method:: protected void setTime(double time)
   :outertype: HostStateHistoryEntry

   Sets the time.

   :param time: the new time

