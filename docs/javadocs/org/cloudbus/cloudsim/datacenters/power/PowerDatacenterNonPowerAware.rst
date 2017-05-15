.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Map.Entry

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterCharacteristics

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostSimple

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core.predicates PredicateType

.. java:import:: org.cloudbus.cloudsim.resources FileStorage

PowerDatacenterNonPowerAware
============================

.. java:package:: org.cloudbus.cloudsim.datacenters.power
   :noindex:

.. java:type:: public class PowerDatacenterNonPowerAware extends PowerDatacenter

   PowerDatacenterNonPowerAware is a class that represents a \ **non-power**\  aware data center in the context of power-aware simulations. If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerDatacenterNonPowerAware
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerDatacenterNonPowerAware(CloudSim simulation, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy)
   :outertype: PowerDatacenterNonPowerAware

   Creates a Datacenter.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param characteristics: the Datacenter characteristics
   :param vmAllocationPolicy: the vm provisioner

PowerDatacenterNonPowerAware
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: @Deprecated public PowerDatacenterNonPowerAware(CloudSim simulation, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<FileStorage> storageList, double schedulingInterval)
   :outertype: PowerDatacenterNonPowerAware

   Creates a Datacenter with the given parameters.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param characteristics: the Datacenter characteristics
   :param vmAllocationPolicy: the vm provisioner
   :param storageList: the storage list
   :param schedulingInterval: the scheduling interval

Methods
-------
updateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double updateCloudletProcessing()
   :outertype: PowerDatacenterNonPowerAware

