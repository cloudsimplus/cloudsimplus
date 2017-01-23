.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

VmAllocationPolicyAbstract
==========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public abstract class VmAllocationPolicyAbstract implements VmAllocationPolicy

   An abstract class that represents the policy used by a \ :java:ref:`Datacenter`\  to choose a \ :java:ref:`Host`\  to place or migrate or migrate a given \ :java:ref:`Vm`\ . It supports two-stage commit of reservation of hosts: first, we reserve the host and, once committed by the user, it is effectively allocated to he/she.

   Each \ :java:ref:`Datacenter`\  has to have its own instance of a class that extends this class.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
VmAllocationPolicyAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyAbstract()
   :outertype: VmAllocationPolicyAbstract

   Creates a new VmAllocationPolicy object.

Methods
-------
addUsedPes
^^^^^^^^^^

.. java:method:: protected void addUsedPes(Vm vm)
   :outertype: VmAllocationPolicyAbstract

   Adds number used PEs for a Vm to the map between each VM and the number of PEs used.

   :param vm: the VM to add the number of used PEs to the map

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: VmAllocationPolicyAbstract

getHostFreePesMap
^^^^^^^^^^^^^^^^^

.. java:method:: protected final Map<Host, Integer> getHostFreePesMap()
   :outertype: VmAllocationPolicyAbstract

   Gets a map with the number of free PEs for each host from \ :java:ref:`getHostList()`\ .

   :return: a Map where each key is a host and each value is the number of free PEs of that host.

getHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> List<T> getHostList()
   :outertype: VmAllocationPolicyAbstract

getUsedPes
^^^^^^^^^^

.. java:method:: protected Map<Vm, Integer> getUsedPes()
   :outertype: VmAllocationPolicyAbstract

   Gets the map between each VM and the number of PEs used. The map key is a VM and the value is the number of used Pes for that VM.

   :return: the used PEs map

getVmHostMap
^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Host> getVmHostMap()
   :outertype: VmAllocationPolicyAbstract

   Gets the map between a VM and its allocated host. The map key is a VM UID and the value is the allocated host for that VM.

   :return: the VM map

mapVmToPm
^^^^^^^^^

.. java:method:: protected void mapVmToPm(Vm vm, Host host)
   :outertype: VmAllocationPolicyAbstract

   Register the allocation of a given Host to a Vm. It maps the placement of the Vm into the given Host.

   :param vm: the placed Vm
   :param host: the Host where the Vm has just been placed

removeUsedPes
^^^^^^^^^^^^^

.. java:method:: protected int removeUsedPes(Vm vm)
   :outertype: VmAllocationPolicyAbstract

   Removes the used PEs for a Vm from the map between each VM and the number of PEs used.

   :return: the used PEs number

scaleVmVertically
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean scaleVmVertically(VerticalVmScaling scaling)
   :outertype: VmAllocationPolicyAbstract

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public final void setDatacenter(Datacenter datacenter)
   :outertype: VmAllocationPolicyAbstract

   Sets the Datacenter associated to the Allocation Policy

   :param datacenter: the Datacenter to set

setHostFreePesMap
^^^^^^^^^^^^^^^^^

.. java:method:: protected final VmAllocationPolicy setHostFreePesMap(Map<Host, Integer> hostFreePesMap)
   :outertype: VmAllocationPolicyAbstract

   Sets the Host free PEs Map.

   :param hostFreePesMap: the new Host free PEs map

setUsedPes
^^^^^^^^^^

.. java:method:: protected final void setUsedPes(Map<Vm, Integer> usedPes)
   :outertype: VmAllocationPolicyAbstract

   Sets the used pes.

   :param usedPes: the used pes

setVmTable
^^^^^^^^^^

.. java:method:: protected final void setVmTable(Map<Vm, Host> vmTable)
   :outertype: VmAllocationPolicyAbstract

   Sets the vm table.

   :param vmTable: the vm table

unmapVmFromPm
^^^^^^^^^^^^^

.. java:method:: protected Host unmapVmFromPm(Vm vm)
   :outertype: VmAllocationPolicyAbstract

   Unregister the allocation of a Host to a given Vm, unmapping the Vm to the Host where it was. The method has to be called when a Vm is moved/removed from a Host.

   :param vm: the moved/removed Vm
   :return: the Host where the Vm was removed/moved from or \ :java:ref:`Host.NULL`\  if the Vm wasn't associated to a Host

