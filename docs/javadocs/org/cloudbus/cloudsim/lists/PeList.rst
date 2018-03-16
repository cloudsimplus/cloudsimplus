.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

PeList
======

.. java:package:: org.cloudbus.cloudsim.lists
   :noindex:

.. java:type:: public final class PeList

   PeList is a collection of operations on lists of PEs.

   :author: Anton Beloglazov

Methods
-------
getById
^^^^^^^

.. java:method:: public static <T extends Pe> T getById(List<T> peList, int id)
   :outertype: PeList

   Gets a \ :java:ref:`Pe`\  with a given id.

   :param <T>:
   :param peList: the PE list where to get a given PE
   :param id: the id of the PE to be get
   :return: the PE with the given id or null if not found

getFreePe
^^^^^^^^^

.. java:method:: public static <T extends Pe> T getFreePe(List<T> peList)
   :outertype: PeList

   Gets the first \ ``FREE``\  PE.

   :param <T>: the class of PEs inside the List
   :param peList: the PE list
   :return: the first free PE or null if not found

getMaxUtilization
^^^^^^^^^^^^^^^^^

.. java:method:: public static double getMaxUtilization(List<? extends Pe> peList)
   :outertype: PeList

   Gets the max utilization percentage (between [0 and 1]) among all PEs.

   :param peList: the pe list
   :return: the max utilization percentage (between [0 and 1])

getMaxUtilizationAmongVmsPes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static double getMaxUtilizationAmongVmsPes(List<? extends Pe> peList, Vm vm)
   :outertype: PeList

   Gets the max utilization percentage among all PEs allocated to a VM.

   :param vm: the vm to get the maximum utilization percentage
   :param peList: the pe list
   :return: the max utilization percentage

getMips
^^^^^^^

.. java:method:: public static long getMips(List<? extends Pe> peList, int id)
   :outertype: PeList

   Gets MIPS Rating of a PE with a given ID.

   :param peList: the PE list where to get a given PE
   :param id: the id of the PE to be get
   :return: the MIPS rating of the PE or -1 if the PE was not found

getNumberOfBusyPes
^^^^^^^^^^^^^^^^^^

.. java:method:: public static int getNumberOfBusyPes(List<? extends Pe> peList)
   :outertype: PeList

   Gets the number of \ ``BUSY``\  PEs.

   :param peList: the PE list
   :return: number of busy PEs

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method:: public static int getNumberOfFreePes(List<? extends Pe> peList)
   :outertype: PeList

   Gets the number of \ ``FREE``\  (non-busy) PEs.

   :param peList: the PE list
   :return: number of free PEs

getTotalMips
^^^^^^^^^^^^

.. java:method:: public static long getTotalMips(List<? extends Pe> peList)
   :outertype: PeList

   Gets total MIPS capacity for a list of PEs.

   :param peList: the pe list
   :return: the total MIPS capacity

setPeStatus
^^^^^^^^^^^

.. java:method:: public static boolean setPeStatus(List<? extends Pe> peList, int id, Pe.Status status)
   :outertype: PeList

   Sets a PE status.

   :param status: the new PE status
   :param id: the id of the PE to be set
   :param peList: the PE list
   :return: \ ``true``\  if the PE status has been changed, \ ``false``\  otherwise (PE id might not be exist)

setStatusFailed
^^^^^^^^^^^^^^^

.. java:method:: public static void setStatusFailed(List<? extends Pe> peList, int hostId, boolean failed)
   :outertype: PeList

   Sets the status of PEs of a host to FAILED or FREE. NOTE: \ ``hostId``\  are used for debugging purposes, which is \ **ON**\  by default. Use \ :java:ref:`setStatusFailed(List,boolean)`\  if you do not want this information.

   :param peList: the host's PE list to be set as failed or free
   :param hostId: the id of the host
   :param failed: true if the host's PEs have to be set as FAILED, false if they have to be set as FREE.

   **See also:** :java:ref:`.setStatusFailed(java.util.List,boolean)`

setStatusFailed
^^^^^^^^^^^^^^^

.. java:method:: public static <T extends Pe> void setStatusFailed(List<T> peList, boolean failed)
   :outertype: PeList

   Sets the status of PEs of a host to FAILED or FREE.

   :param <T>: the generic type
   :param peList: the host's PE list to be set as failed or free
   :param failed: true if the host's PEs have to be set as FAILED, false if they have to be set as FREE.

