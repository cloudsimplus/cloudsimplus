org.cloudbus.cloudsim.selectionpolicies.power
=============================================

Provides \ :java:ref:`org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy`\  implementations that define policies to be used by a \ :java:ref:`org.cloudbus.cloudsim.hosts.Host`\  to select a \ :java:ref:`org.cloudbus.cloudsim.vms.Vm`\  to migrate from a list of VMs.

The order in which VMs are migrated may impact positive or negatively some SLA metric. For instance, migrating VMs that are requiring more bandwidth may reduce network congestion after such VMs are migrated and will make more bandwidth available that will reduce the migration time for subsequent migrating VMs.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.selectionpolicies.power

.. toctree::
   :maxdepth: 1

   PowerVmSelectionPolicy
   PowerVmSelectionPolicyMaximumCorrelation
   PowerVmSelectionPolicyMinimumMigrationTime
   PowerVmSelectionPolicyMinimumUtilization
   PowerVmSelectionPolicyRandomSelection

