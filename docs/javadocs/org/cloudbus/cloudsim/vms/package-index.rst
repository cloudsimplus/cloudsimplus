org.cloudbus.cloudsim.vms
=========================

Provides implementations of Virtual Machines (\ :java:ref:`org.cloudbus.cloudsim.vms.Vm`\ ) which are a software package that emulate the architecture of a physical machine. Each VM is executed by a Host and it is used to run applications (\ :java:ref:`org.cloudbus.cloudsim.cloudlets.Cloudlet`\ ). Both VMs and Cloudlets are owned by a specific cloud customer (represented by a \ :java:ref:`org.cloudbus.cloudsim.brokers.DatacenterBroker`\ ).

As each VM can run several Cloudlets, the scheduling of such Cloudlets on the VM's CPU cores (\ :java:ref:`org.cloudbus.cloudsim.resources.Pe`\ ) is defined by a \ :java:ref:`org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler`\ .

The most basic Vm implementation is the \ :java:ref:`org.cloudbus.cloudsim.vms.VmSimple`\ .

Specific Vm implementations can be power- or network-aware, enabling the simulation of power consumption and network communication. For more information see \ :java:ref:`org.cloudbus.cloudsim.datacenters`\  package documentation.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.vms

.. toctree::
   :maxdepth: 1

   UtilizationHistory
   UtilizationHistoryNull
   Vm
   VmNull
   VmSimple
   VmStateHistoryEntry
   VmUtilizationHistory

