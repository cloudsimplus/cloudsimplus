.. java:import:: org.cloudbus.cloudsim.datacenters.power PowerDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.vms.power PowerVm

PowerVmAllocationPolicy
=======================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public interface PowerVmAllocationPolicy extends VmAllocationPolicy

   An interface to be implemented by each class that represents a policy used by a \ :java:ref:`PowerDatacenter`\  to choose a \ :java:ref:`PowerHost`\  to place or migrate a given \ :java:ref:`PowerVm`\  considering the Host power consumption.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Methods
-------
findHostForVm
^^^^^^^^^^^^^

.. java:method::  PowerHost findHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicy

   Finds the first host that has enough resources to host a given VM.

   :param vm: the vm to find a host for it
   :return: the first host found that can host the VM or \ :java:ref:`PowerHost.NULL`\  if no suitable Host was found for Vm

