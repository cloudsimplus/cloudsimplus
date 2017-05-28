.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Comparator

.. java:import:: java.util Optional

.. java:import:: java.util.stream Stream

PowerVmAllocationPolicyMigrationBestFitStaticThreshold
======================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public class PowerVmAllocationPolicyMigrationBestFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold

   A \ :java:ref:`VmAllocationPolicy`\  that uses a Static CPU utilization Threshold (THR) to detect host \ :java:ref:`under <getUnderUtilizationThreshold()>`\  and \ :java:ref:`getOverUtilizationThreshold(PowerHost)`\  over} utilization.

   It's a Best Fit policy which selects the Host having the least used amount of CPU MIPS to place a given VM, \ **disregarding energy consumption**\ .

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
PowerVmAllocationPolicyMigrationBestFitStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicyMigrationBestFitStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold)
   :outertype: PowerVmAllocationPolicyMigrationBestFitStaticThreshold

Methods
-------
findHostForVmInternal
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected Optional<PowerHost> findHostForVmInternal(Vm vm, Stream<PowerHost> hostStream)
   :outertype: PowerVmAllocationPolicyMigrationBestFitStaticThreshold

   Gets the Host having the least available MIPS capacity (max used MIPS).

   This method is ignoring the additional filtering performed by the super class. This way, Host selection is performed ignoring energy consumption. However, all the basic filters defined in the super class are ensured, since this method is called just after they are applied.

   :param vm: {@inheritDoc}
   :param hostStream: {@inheritDoc}
   :return: {@inheritDoc}

