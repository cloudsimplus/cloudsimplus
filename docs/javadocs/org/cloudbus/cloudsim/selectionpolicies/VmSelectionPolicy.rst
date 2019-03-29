.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

VmSelectionPolicy
=================

.. java:package:: org.cloudbus.cloudsim.selectionpolicies
   :noindex:

.. java:type:: public interface VmSelectionPolicy

   An interface to be used to implement VM selection policies for a list of migratable VMs. The selection is defined by sub classes. If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Fields
------
NULL
^^^^

.. java:field::  VmSelectionPolicy NULL
   :outertype: VmSelectionPolicy

Methods
-------
getVmToMigrate
^^^^^^^^^^^^^^

.. java:method::  Vm getVmToMigrate(Host host)
   :outertype: VmSelectionPolicy

   Gets a VM to migrate from a given host.

   :param host: the host to get a Vm to migrate from
   :return: the vm to migrate or \ :java:ref:`Vm.NULL`\  if there is not Vm to migrate

