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

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions PoissonProcess

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Pe.Status

HostFaultInjection
==================

.. java:package:: org.cloudsimplus.faultinjection
   :noindex:

.. java:type:: public class HostFaultInjection extends CloudSimEntity

   Generates random failures for the \ :java:ref:`Pe`\ 's of a specific \ :java:ref:`Host`\ . The events happens in the following order:

   ..

   #. a failure is injected randomly;
   #. a delay is defined for the failure to actually happen;
   #. the number of PEs to fail is randomly generated.

   :author: raysaoliveira

   **See also:** :java:ref:`https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/`

Constructors
------------
HostFaultInjection
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostFaultInjection(Host host, double meanFailuresPerMinute, long seed)
   :outertype: HostFaultInjection

   Creates a fault injection mechanism for a host that will inject failures with a delay and number of failed PEs generated using a given Pseudo Random Number Generator (PRNG).

   :param host: the Host to which failures may be randomly generated
   :param meanFailuresPerMinute: the average number of failures expected to happen each minute.
   :param seed: the seed to initialize the internal uniform random number generator

   **See also:** :java:ref:`.setMaxFailureDelay(double)`

Methods
-------
getFailedVmsCount
^^^^^^^^^^^^^^^^^

.. java:method:: public long getFailedVmsCount()
   :outertype: HostFaultInjection

   Gets the number of VMs that are completely failed (which all their PEs were removed due to Host PEs failure).

getHost
^^^^^^^

.. java:method:: public Host getHost()
   :outertype: HostFaultInjection

   Gets the host in which a failure may happen.

getMaxFailureDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaxFailureDelay()
   :outertype: HostFaultInjection

   Gets the maximum delay after a fault be injected that it will in fact happen (in seconds).

   This is used to define, randomly the actual time, after the time the fault was injected, that it will happen. For instance, if a fault is generated and a value 5 is chosen between 0 and the maximum delay, the fault will actually happen just after 5 seconds it was generated.

   :return: the maximum failure delay (in seconds)

getMeanFailuresPerMinute
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMeanFailuresPerMinute()
   :outertype: HostFaultInjection

   Gets the average number of failures expected to happen each minute.

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

setHost
^^^^^^^

.. java:method:: protected final void setHost(Host host)
   :outertype: HostFaultInjection

   Sets the host in which failure may happen.

   :param host: the host to set

setMaxFailureDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setMaxFailureDelay(double maxFailureDelay)
   :outertype: HostFaultInjection

   Sets the maximum delay after a fault be injected that it will in fact happen (default is 0).

   This is used to define, randomly the actual time, after the time the fault was injected, that it will happen. For instance, if a fault is generated and a value 5 is chosen between 0 and the maximum delay, the fault will actually happen just after 5 seconds it was generated.

   :param maxFailureDelay: the max delay to set (in seconds)

setVmCloner
^^^^^^^^^^^

.. java:method:: public void setVmCloner(UnaryOperator<Vm> vmCloner)
   :outertype: HostFaultInjection

   Sets a \ :java:ref:`UnaryOperator`\  that creates a clone of a \ :java:ref:`Vm`\  when all Host PEs fail or all VM's PEs are deallocated because they have failed.

   The \ :java:ref:`UnaryOperator`\  is a \ :java:ref:`Function`\  that receives a \ :java:ref:`Vm`\  and returns a clone of it. When all PEs of the VM fail, this vmCloner \ :java:ref:`Function`\  is used to create a copy of the VM to be submitted to another Host. It is like a VM snapshot in a real cloud infrastructure, which will be started into another host in order to recovery from a failure.

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

