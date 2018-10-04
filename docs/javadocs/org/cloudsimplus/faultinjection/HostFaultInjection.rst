.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicyAbstract

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSimEntity

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core Machine

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions PoissonDistr

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util.function BinaryOperator

.. java:import:: java.util.function Function

.. java:import:: java.util.stream Stream

HostFaultInjection
==================

.. java:package:: org.cloudsimplus.faultinjection
   :noindex:

.. java:type:: public class HostFaultInjection extends CloudSimEntity

   Generates random failures for the \ :java:ref:`Pe`\ 's of \ :java:ref:`Host`\ s inside a given \ :java:ref:`Datacenter`\ . A Fault Injection object usually has to be created after the VMs are created, to make it easier to define a function to be used to clone failed VMs. The events happens in the following order:

   ..

   #. a time to inject a Host failure is generated using a given Random Number Generator;
   #. a Host is randomly selected to fail at that time using an internal Uniform Random Number Generator with the same seed of the given generator;
   #. the number of Host PEs to fail is randomly generated using the internal generator;
   #. failed physical PEs are removed from affected VMs, VMs with no remaining PEs and destroying and clones of them are submitted to the \ :java:ref:`DatacenterBroker`\  of the failed VMs;
   #. another failure is scheduled for a future time using the given generator;
   #. the process repeats until the end of the simulation.

   When Host's PEs fail, if there are more available PEs than the required by its running VMs, no VM will be affected.

   Considering that X is the number of failed PEs and it is lower than the total available PEs. In this case, the X PEs will be removed cyclically, 1 by 1, from running VMs. This way, some VMs may continue running with less PEs than they requested initially. On the other hand, if after the failure the number of Host working PEs is lower than the required to run all VMs, some VMs will be destroyed.

   If all PEs are removed from a VM, it is automatically destroyed and a snapshot (clone) from it is taken and submitted to the broker, so that the clone can start executing into another host. In this case, all the cloudlets which were running inside the VM yet, will be cloned to and restart executing from the beginning.

   If a cloudlet running inside a VM which was affected by a PE failure requires Y PEs but the VMs doesn't have such PEs anymore, the Cloudlet will continue executing, but it will spend more time to finish. For instance, if a Cloudlet requires 2 PEs but after the failure the VM was left with just 1 PE, the Cloudlet will spend the double of the time to finish.

   \ **NOTES:**\

   ..

   * Host PEs failures may happen after all its VMs have finished executing. This way, the presented simulation results may show that the number of PEs into a Host is lower than the required by its VMs. In this case, the VMs shown in the results finished executing before some failures have happened. Analysing the logs is easy to confirm that.
   * Failures inter-arrivals are defined in minutes, since seconds is a too small time unit to define such value. Furthermore, it doesn't make sense to define the number of failures per second. This way, the generator of failure arrival times given to the constructor considers the time in minutes, despite the simulation time unit is seconds. Since commonly Cloudlets just take some seconds to finish, mainly in simulation examples, failures may happen just after the cloudlets have finished. This way, one usually should make sure that Cloudlets' length are large enough to allow failures to happen before they end.

   For more details, check \ `Raysa Oliveira's Master Thesis (only in Portuguese) <http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf>`_\ .

   :author: raysaoliveira

   **See also:** \ `SAP Blog: Availability vs Reliability <https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/>`_\

Constructors
------------
HostFaultInjection
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostFaultInjection(Datacenter datacenter)
   :outertype: HostFaultInjection

   Creates a fault injection mechanism for the Hosts of a given \ :java:ref:`Datacenter`\ . The Hosts failures are randomly injected according to a \ :java:ref:`UniformDistr`\  pseudo random number generator, which indicates the mean of failures to be generated per \ **hour**\ , (which is also called \ **event rate**\  or \ **rate parameter**\ ).

   :param datacenter: the Datacenter to which failures will be randomly injected for its Hosts

   **See also:** :java:ref:`.HostFaultInjection(Datacenter,ContinuousDistribution)`

HostFaultInjection
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostFaultInjection(Datacenter datacenter, ContinuousDistribution faultArrivalHoursGenerator)
   :outertype: HostFaultInjection

   Creates a fault injection mechanism for the Hosts of a given \ :java:ref:`Datacenter`\ . The Hosts failures are randomly injected according to the given pseudo random number generator, that indicates the mean of failures to be generated per \ **minute**\ , (which is also called \ **event rate**\  or \ **rate parameter**\ ).

   :param datacenter: the Datacenter to which failures will be randomly injected for its Hosts
   :param faultArrivalHoursGenerator: a Pseudo Random Number Generator which generates the times Hosts failures will occur (in hours). \ **The values returned by the generator will be considered to be hours**\ . Frequently it is used a \ :java:ref:`PoissonDistr`\  to generate failure arrivals, but any \ :java:ref:`ContinuousDistribution`\  can be used.

Methods
-------
addVmCloner
^^^^^^^^^^^

.. java:method:: public void addVmCloner(DatacenterBroker broker, VmCloner cloner)
   :outertype: HostFaultInjection

   Adds a \ :java:ref:`VmCloner`\  that creates a clone for the last failed \ :java:ref:`Vm`\  belonging to a given broker, when all VMs of that broker have failed.

   This is optional. If a \ :java:ref:`VmCloner`\  is not set, VMs will not be recovered from failures.

   :param broker: the broker to set the VM cloner Function to
   :param cloner: the \ :java:ref:`VmCloner`\  to set

availability
^^^^^^^^^^^^

.. java:method:: public double availability()
   :outertype: HostFaultInjection

   Gets the Datacenter's availability as a percentage value between 0 to 1, based on VMs' downtime (the times VMs took to be repaired).

availability
^^^^^^^^^^^^

.. java:method:: public double availability(DatacenterBroker broker)
   :outertype: HostFaultInjection

   Gets the availability for a given broker as a percentage value between 0 to 1, based on VMs' downtime (the times VMs took to be repaired).

   :param broker: the broker to get the availability of its VMs

generateHostFault
^^^^^^^^^^^^^^^^^

.. java:method:: public void generateHostFault(Host host)
   :outertype: HostFaultInjection

   Generates a fault for all PEs of a Host.

   :param host: the Host to generate the fault to.

generateHostFault
^^^^^^^^^^^^^^^^^

.. java:method:: public void generateHostFault(Host host, long numberOfPesToFail)
   :outertype: HostFaultInjection

   Generates a fault for a given number of random PEs of a Host.

   :param host: the Host to generate the fault to.
   :param numberOfPesToFail: number of PEs that must fail

getDatacenter
^^^^^^^^^^^^^

.. java:method:: public Datacenter getDatacenter()
   :outertype: HostFaultInjection

   Gets the datacenter in which failures will be injected.

getLastFailedHost
^^^^^^^^^^^^^^^^^

.. java:method:: public Host getLastFailedHost()
   :outertype: HostFaultInjection

   Gets the last Host for which a failure was injected.

   :return: the last failed Host or \ :java:ref:`Host.NULL`\  if not Host has failed yet.

getMaxTimeToFailInHours
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaxTimeToFailInHours()
   :outertype: HostFaultInjection

   Gets the maximum time to generate a failure (in hours). After that time, no failure will be generated.

   **See also:** :java:ref:`.getMaxTimeToFailInSecs()`

getNumberOfFaults
^^^^^^^^^^^^^^^^^

.. java:method:: public long getNumberOfFaults()
   :outertype: HostFaultInjection

   Gets the total number of faults which affected all VMs from any broker.

getNumberOfFaults
^^^^^^^^^^^^^^^^^

.. java:method:: public long getNumberOfFaults(DatacenterBroker broker)
   :outertype: HostFaultInjection

   Gets the total number of Host faults which affected all VMs from a given broker or VMs from all existing brokers.

   :param broker: the broker to get the number of Host faults affecting its VMs or null whether is to be counted Host faults affecting VMs from any broker

getNumberOfHostFaults
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfHostFaults()
   :outertype: HostFaultInjection

   Gets the total number of faults happened for existing hosts. This isn't the total number of failed hosts because one host may fail multiple times.

getRandomRecoveryTimeForVmInSecs
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getRandomRecoveryTimeForVmInSecs()
   :outertype: HostFaultInjection

   Gets a Pseudo Random Number used to give a recovery time (in seconds) for each VM that was failed.

meanTimeBetweenHostFaultsInMinutes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double meanTimeBetweenHostFaultsInMinutes()
   :outertype: HostFaultInjection

   Computes the current Mean Time Between host Failures (MTBF) in minutes. Since Hosts don't actually recover from failures, there aren't recovery time to make easier the computation of MTBF for Host as it is directly computed for VMs.

   :return: the current mean time (in minutes) between Host failures (MTBF) or zero if no failures have happened yet

   **See also:** :java:ref:`.meanTimeBetweenVmFaultsInMinutes()`

meanTimeBetweenVmFaultsInMinutes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double meanTimeBetweenVmFaultsInMinutes()
   :outertype: HostFaultInjection

   Computes the current Mean Time Between host Failures (MTBF) in minutes, which affected VMs from any broker for the entire Datacenter. It uses a straightforward way to compute the MTBF. Since it's stored the VM recovery times, it's possible to use such values to make easier the MTBF computation, different from the Hosts MTBF.

   :return: the current Mean Time Between host Failures (MTBF) in minutes or zero if no VM was destroyed due to Host failure

   **See also:** :java:ref:`.meanTimeBetweenHostFaultsInMinutes()`

meanTimeBetweenVmFaultsInMinutes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double meanTimeBetweenVmFaultsInMinutes(DatacenterBroker broker)
   :outertype: HostFaultInjection

   Computes the current Mean Time Between host Failures (MTBF) in minutes, which affected VMs from a given broker. It uses a straightforward way to compute the MTBF. Since it's stored the VM recovery times, it's possible to use such values to make easier the MTBF computation, different from the Hosts MTBF.

   :param broker: the broker to get the MTBF for
   :return: the current mean time (in minutes) between Host failures (MTBF) or zero if no VM was destroyed due to Host failure

   **See also:** :java:ref:`.meanTimeBetweenHostFaultsInMinutes()`

meanTimeToRepairVmFaultsInMinutes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double meanTimeToRepairVmFaultsInMinutes()
   :outertype: HostFaultInjection

   Computes the current Mean Time To Repair failures of VMs in minutes (MTTR) in the Datacenter, for all existing brokers.

   :return: the MTTR (in minutes) or zero if no VM was destroyed due to Host failure

meanTimeToRepairVmFaultsInMinutes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double meanTimeToRepairVmFaultsInMinutes(DatacenterBroker broker)
   :outertype: HostFaultInjection

   Computes the current Mean Time To Repair Failures of VMs in minutes (MTTR) belonging to given broker. If a null broker is given, computes the MTTR of all VMs for all existing brokers.

   :param broker: the broker to get the MTTR for or null if the MTTR is to be computed for all brokers
   :return: the current MTTR (in minutes) or zero if no VM was destroyed due to Host failure

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: HostFaultInjection

setDatacenter
^^^^^^^^^^^^^

.. java:method:: protected final void setDatacenter(Datacenter datacenter)
   :outertype: HostFaultInjection

   Sets the datacenter in which failures will be injected.

   :param datacenter: the datacenter to set

setMaxTimeToFailInHours
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setMaxTimeToFailInHours(double maxTimeToFailInHours)
   :outertype: HostFaultInjection

   Sets the maximum time to generate a failure (in hours). After that time, no failure will be generated.

   :param maxTimeToFailInHours: the maximum time to set (in hours)

startEntity
^^^^^^^^^^^

.. java:method:: @Override protected void startEntity()
   :outertype: HostFaultInjection

