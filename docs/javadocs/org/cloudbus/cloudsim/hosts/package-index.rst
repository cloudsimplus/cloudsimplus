org.cloudbus.cloudsim.hosts
===========================

Provides \ :java:ref:`org.cloudbus.cloudsim.hosts.Host`\  implementations that represent a Physical Machine (PM) is used to run \ :java:ref:`org.cloudbus.cloudsim.vms.Vm`\  from different cloud customers (represented by a \ :java:ref:`org.cloudbus.cloudsim.brokers.DatacenterBroker`\ ).

As each Host can run several VMs, the scheduling of such VMs on the Host's CPU cores (\ :java:ref:`org.cloudbus.cloudsim.resources.Pe`\ ) is defined by a \ :java:ref:`org.cloudbus.cloudsim.schedulers.vm.VmScheduler`\ .

The most basic Host is the \ :java:ref:`org.cloudbus.cloudsim.hosts.HostSimple`\ .

Specific Host implementations can be power- or network-aware, enabling the simulation of power consumption and network communication. For more information see \ :java:ref:`org.cloudbus.cloudsim.datacenters`\  package documentation.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.hosts

.. toctree::
   :maxdepth: 1

   Host
   HostNull
   HostSimple
   HostStateHistoryEntry

