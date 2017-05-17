.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.function Function

.. java:import:: java.util.function UnaryOperator

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.distributions PoissonDistr

HostFaultInjection
==================

.. java:package:: org.cloudsimplus.faultinjection
   :noindex:

.. java:type:: public class HostFaultInjection extends CloudSimEntity

   Generates random failures for the \ :java:ref:`Pe`\ 's of \ :java:ref:`Host`\ s inside a given \ :java:ref:`Datacenter`\ . The events happens in the following order:

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

   :author: raysaoliveira

   **See also:** \ `SAP Blog: Availability vs Reliability <https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/>`_\

Constructors
------------
HostFaultInjection
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostFaultInjection(Datacenter datacenter, ContinuousDistribution failureArrivalTimesGenerator)
   :outertype: HostFaultInjection

   Creates a fault injection mechanism for the Hosts of a given \ :java:ref:`Datacenter`\ . The failures are randomly injected according to the given mean of failures to be generated per \ **minute**\ , which is also called \ **event rate**\  or \ **rate parameter**\ .

   :param datacenter: the Datacenter to which failures will be randomly injected for its Hosts
   :param failureArrivalTimesGenerator: a Pseudo Random Number Generator which generates the times that Hosts failures will occur. \ **The values returned by the generator will be considered to be minutes**\ . Frequently it is used a \ :java:ref:`PoissonDistr`\  to generate failure arrivals, but any \ :java:ref:`ContinuousDistribution`\  can be used.

Methods
-------
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

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent ev)
   :outertype: HostFaultInjection

setCloudletsCloner
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setCloudletsCloner(Function<Vm, List<Cloudlet>> cloudletsCloner)
   :outertype: HostFaultInjection

   Sets a \ :java:ref:`Function`\  that creates a clone of all Cloudlets which were running inside a given failed \ :java:ref:`Vm`\ .

   Such a Function is used to re-create and re-submit those Cloudlets to a clone of the failed VM. In this case, all the Cloudlets are recreated from scratch into the cloned VM, re-starting their execution from the beginning. Since a snapshot (clone) of the failed VM will be started into another Host, the Cloudlets Cloner Function will recreated all Cloudlets, simulating the restart of applications into this new VM instance.

   :param cloudletsCloner: the cloudlets cloner \ :java:ref:`Function`\  to set

   **See also:** :java:ref:`.setVmCloner(java.util.function.UnaryOperator)`

setDatacenter
^^^^^^^^^^^^^

.. java:method:: protected final void setDatacenter(Datacenter datacenter)
   :outertype: HostFaultInjection

   Sets the datacenter in which failures will be injected.

   :param datacenter: the datacenter to set

setVmCloner
^^^^^^^^^^^

.. java:method:: public void setVmCloner(UnaryOperator<Vm> vmCloner)
   :outertype: HostFaultInjection

   Sets a \ :java:ref:`UnaryOperator`\  that creates a clone of a \ :java:ref:`Vm`\  when all Host PEs fail or all VM's PEs are deallocated because they have failed.

   The \ :java:ref:`UnaryOperator`\  is a \ :java:ref:`Function`\  that receives a \ :java:ref:`Vm`\  and returns a clone of it. When all PEs of the VM fail, this vmCloner \ :java:ref:`Function`\  is used to create a copy of the VM to be submitted to another Host. It is like a VM snapshot in a real cloud infrastructure, which will be started into another datacenter in order to recovery from a failure.

   :param vmCloner: the VM cloner \ :java:ref:`Function`\  to set

   **See also:** :java:ref:`.setCloudletsCloner(java.util.function.Function)`

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: HostFaultInjection

startEntity
^^^^^^^^^^^

.. java:method:: @Override protected void startEntity()
   :outertype: HostFaultInjection

