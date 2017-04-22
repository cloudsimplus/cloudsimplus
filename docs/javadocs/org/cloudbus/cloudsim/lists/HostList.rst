.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

HostList
========

.. java:package:: org.cloudbus.cloudsim.lists
   :noindex:

.. java:type:: public final class HostList

   HostList is a collection of operations on lists of hosts (PMs).

   :author: Anton Beloglazov

Methods
-------
getById
^^^^^^^

.. java:method:: public static <T extends Host> T getById(List<T> hostList, int id)
   :outertype: HostList

   Gets a \ :java:ref:`Host`\  with a given id.

   :param <T>: the generic type
   :param hostList: the list of existing hosts
   :param id: the host ID
   :return: a Host with the given ID or $null if not found

getHostWithFreePe
^^^^^^^^^^^^^^^^^

.. java:method:: public static <T extends Host> T getHostWithFreePe(List<T> hostList)
   :outertype: HostList

   Gets the first host with free PEs.

   :param <T>: the class of Hosts inside the List
   :param hostList: the list of existing hosts
   :return: a Host object or \ ``null``\  if not found

getHostWithFreePe
^^^^^^^^^^^^^^^^^

.. java:method:: public static <T extends Host> T getHostWithFreePe(List<T> hostList, int pesNumber)
   :outertype: HostList

   Gets the first Host with a specified number of free PEs.

   :param <T>: the class of Hosts inside the List
   :param hostList: the list of existing hosts
   :param pesNumber: the pes number
   :return: a Host object or \ ``null``\  if not found

getNumberOfBusyPes
^^^^^^^^^^^^^^^^^^

.. java:method:: public static int getNumberOfBusyPes(List<? extends Host> hostList)
   :outertype: HostList

   Gets the total number of \ ``BUSY``\  PEs for all Hosts.

   :param hostList: the list of existing hosts
   :return: total number of busy PEs

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method:: public static int getNumberOfFreePes(List<? extends Host> hostList)
   :outertype: HostList

   Gets the total number of \ ``FREE``\  (non-busy) PEs for all Hosts.

   :param hostList: the list of existing hosts
   :return: total number of free PEs

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: public static int getNumberOfPes(List<? extends Host> hostList)
   :outertype: HostList

   Gets the total number of PEs for all Hosts.

   :param hostList: the list of existing hosts
   :return: total number of PEs for all PMs

setPeStatus
^^^^^^^^^^^

.. java:method:: public static boolean setPeStatus(List<? extends Host> hostList, Pe.Status status, int hostId, int peId)
   :outertype: HostList

   Sets the status of a particular PE on a given Host.

   :param hostList: the list of existing hosts
   :param status: the new PE status
   :param hostId: the host id
   :param peId: the id of the PE to set the status
   :return: \ ``true``\  if the PE status has changed, \ ``false``\  otherwise (host id or PE id might not be exist)

