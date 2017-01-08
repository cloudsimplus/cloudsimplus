.. java:import:: java.util Comparator

.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.vms Vm

VmList
======

.. java:package:: org.cloudbus.cloudsim.lists
   :noindex:

.. java:type:: public final class VmList

   VmList is a collection of operations on lists of VMs.

   :author: Anton Beloglazov

Methods
-------
getById
^^^^^^^

.. java:method:: public static <T extends Vm> T getById(List<T> vmList, int id)
   :outertype: VmList

   Gets a \ :java:ref:`Vm`\  with a given id.

   :param <T>: the class of VMs inside the list
   :param id: ID of required VM
   :param vmList: list of existing VMs
   :return: a Vm with the given ID or \ :java:ref:`Vm.NULL`\  if not found

getByIdAndUserId
^^^^^^^^^^^^^^^^

.. java:method:: public static <T extends Vm> T getByIdAndUserId(List<T> vmList, int id, int userId)
   :outertype: VmList

   Gets a \ :java:ref:`Vm`\  with a given id and owned by a given user.

   :param <T>: The generic type
   :param vmList: list of existing VMs
   :param id: ID of required VM
   :param userId: the user ID of the VM's owner
   :return: VmSimple with the given ID, $null if not found

sortByCpuUtilization
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void sortByCpuUtilization(List<? extends Vm> vmList, double currentSimulationTime)
   :outertype: VmList

   Sort a given list of VMs by descending order of CPU utilization.

   :param vmList: the vm list to be sorted
   :param currentSimulationTime: the current simulation time to get the current CPU utilization for each Vm

