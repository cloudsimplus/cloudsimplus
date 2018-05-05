org.cloudbus.cloudsim.datacenters
=================================

Provides \ :java:ref:`org.cloudbus.cloudsim.datacenters.Datacenter`\  implementations, that represents a physical Cloud Datacenter and contains a set of \ :java:ref:`org.cloudbus.cloudsim.hosts.Host`\  that together provide the basic cloud infrastructure.

Each Datacenter has attributes that define its characteristics, such as the costs to use different physical resources from Hosts. These attributes are defined by a \ :java:ref:`org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics`\  object.

For each created Datacenter, a \ :java:ref:`org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy`\  instance must be defined. This object decides which PM will host each \ :java:ref:`org.cloudbus.cloudsim.vms.Vm`\ . The most basic VmAllocationPolicy is the \ :java:ref:`org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple`\ .

\ **All datacenter implementations are natively power-ware.**\  Specific implementations can also be network-aware, enabling the simulation of network communication. There are specifc networtk-aware versions for Hosts and VMs and a single kindle of such objects must be used for a simulation. For instance a network-aware simulation must use \ :java:ref:`org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter`\ , \ :java:ref:`org.cloudbus.cloudsim.hosts.network.NetworkHost`\ , \ :java:ref:`org.cloudbus.cloudsim.vms.network.NetworkVm`\  and \ :java:ref:`org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet`\ .

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.datacenters

.. toctree::
   :maxdepth: 1

   Datacenter
   DatacenterCharacteristics
   DatacenterCharacteristicsNull
   DatacenterCharacteristicsSimple
   DatacenterNull
   DatacenterSimple

