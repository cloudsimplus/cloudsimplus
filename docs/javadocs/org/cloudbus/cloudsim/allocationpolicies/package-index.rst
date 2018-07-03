org.cloudbus.cloudsim.allocationpolicies
========================================

Provides classes that implement policies for a \ :java:ref:`org.cloudbus.cloudsim.datacenters.Datacenter`\  to select a Host to \ **place**\  or \ **migrate**\  a VM, based on some criteria defined by each class. Different policies can follow approaches such as best-fit, worst-fit and so on.

\ **Each Datacenter must have its own instance of a .**\  The most basic implementation is provided by the class \ :java:ref:`org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple`\ . Only classes that implement the \ :java:ref:`org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigration`\  interface are able to perform VM migration.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.allocationpolicies

.. toctree::
   :maxdepth: 1

   VmAllocationPolicy
   VmAllocationPolicyAbstract
   VmAllocationPolicyBestFit
   VmAllocationPolicyFirstFit
   VmAllocationPolicyNull
   VmAllocationPolicySimple

