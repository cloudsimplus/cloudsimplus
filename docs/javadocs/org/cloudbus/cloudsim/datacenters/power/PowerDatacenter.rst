.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Map.Entry

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterCharacteristics

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterSimple

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostSimple

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.core.predicates PredicateType

.. java:import:: org.cloudbus.cloudsim.resources FileStorage

.. java:import:: org.cloudbus.cloudsim.util Log

PowerDatacenter
===============

.. java:package:: org.cloudbus.cloudsim.datacenters.power
   :noindex:

.. java:type:: public class PowerDatacenter extends DatacenterSimple

   PowerDatacenter is a class that enables simulation of power-aware data centers. If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerDatacenter
^^^^^^^^^^^^^^^

.. java:constructor:: public PowerDatacenter(CloudSim simulation, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy)
   :outertype: PowerDatacenter

   Creates a PowerDatacenter.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param characteristics: the characteristics of the Datacenter to be created
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts

PowerDatacenter
^^^^^^^^^^^^^^^

.. java:constructor:: @Deprecated public PowerDatacenter(CloudSim simulation, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<FileStorage> storageList, double schedulingInterval)
   :outertype: PowerDatacenter

   Creates a PowerDatacenter with the given parameters.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param characteristics: the characteristics of the Datacenter to be created
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts
   :param storageList: a List of storage elements, for data simulation
   :param schedulingInterval: the scheduling delay to process each Datacenter received event

Methods
-------
getLastCloudletProcessingTime
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getLastCloudletProcessingTime()
   :outertype: PowerDatacenter

   Gets the last time submitted cloudlets were processed.

   :return: true, if is cloudlet submitted

getMigrationCount
^^^^^^^^^^^^^^^^^

.. java:method:: public int getMigrationCount()
   :outertype: PowerDatacenter

   Gets the migration count.

   :return: the migration count

getPower
^^^^^^^^

.. java:method:: public double getPower()
   :outertype: PowerDatacenter

   Gets the power.

   :return: the power

incrementMigrationCount
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void incrementMigrationCount()
   :outertype: PowerDatacenter

   Increment migration count.

isInMigration
^^^^^^^^^^^^^

.. java:method:: protected boolean isInMigration()
   :outertype: PowerDatacenter

   Checks if PowerDatacenter has any VM in migration.

isMigrationsEnabled
^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isMigrationsEnabled()
   :outertype: PowerDatacenter

   Checks if migrations are enabled.

   :return: true, if migrations are enable; false otherwise

processCloudletSubmit
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void processCloudletSubmit(SimEvent ev, boolean ack)
   :outertype: PowerDatacenter

processVmMigrate
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void processVmMigrate(SimEvent ev, boolean ack)
   :outertype: PowerDatacenter

removeFinishedVmsFromEveryHost
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void removeFinishedVmsFromEveryHost()
   :outertype: PowerDatacenter

setLastCloudletProcessingTime
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setLastCloudletProcessingTime(double lastCloudletProcessingTime)
   :outertype: PowerDatacenter

   Sets the last time submitted cloudlets were processed.

   :param lastCloudletProcessingTime: the new cloudlet submitted

setMigrationCount
^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setMigrationCount(int migrationCount)
   :outertype: PowerDatacenter

   Sets the migration count.

   :param migrationCount: the new migration count

setMigrationsEnabled
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final PowerDatacenter setMigrationsEnabled(boolean enable)
   :outertype: PowerDatacenter

   Enable or disable migrations.

   :param enable: true to enable migrations; false to disable

setPower
^^^^^^^^

.. java:method:: protected final void setPower(double power)
   :outertype: PowerDatacenter

   Sets the power.

   :param power: the new power

updateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double updateCloudletProcessing()
   :outertype: PowerDatacenter

updateVmsProcessingOfAllHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double updateVmsProcessingOfAllHosts()
   :outertype: PowerDatacenter

