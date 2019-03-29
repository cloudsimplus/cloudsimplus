.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms VmSimple

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners VmDatacenterEventInfo

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Supplier

VmBuilder
=========

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class VmBuilder implements Builder

   A Builder class to create \ :java:ref:`Vm`\  objects using the default values defined in \ :java:ref:`Vm`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmSimple.setDefaultRamCapacity(long)`, :java:ref:`VmSimple.setDefaultBwCapacity(long)`, :java:ref:`VmSimple.setDefaultStorageCapacity(long)`

Constructors
------------
VmBuilder
^^^^^^^^^

.. java:constructor:: public VmBuilder(DatacenterBrokerSimple broker)
   :outertype: VmBuilder

Methods
-------
createAndSubmit
^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder createAndSubmit()
   :outertype: VmBuilder

   Creates and submits one VM to its broker.

createAndSubmit
^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder createAndSubmit(int amount)
   :outertype: VmBuilder

   Creates and submits a list of VM to its broker.

getMips
^^^^^^^

.. java:method:: public double getMips()
   :outertype: VmBuilder

getPes
^^^^^^

.. java:method:: public long getPes()
   :outertype: VmBuilder

getVmById
^^^^^^^^^

.. java:method:: public Vm getVmById(int id)
   :outertype: VmBuilder

getVms
^^^^^^

.. java:method:: public List<Vm> getVms()
   :outertype: VmBuilder

setCloudletSchedulerSupplier
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setCloudletSchedulerSupplier(Supplier<CloudletScheduler> cloudletSchedulerSupplier)
   :outertype: VmBuilder

setMips
^^^^^^^

.. java:method:: public VmBuilder setMips(double defaultMIPS)
   :outertype: VmBuilder

setOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnHostAllocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmBuilder

setOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnHostDeallocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmBuilder

setOnUpdateVmProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnUpdateVmProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmBuilder

setOnVmCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnVmCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: VmBuilder

setPes
^^^^^^

.. java:method:: public VmBuilder setPes(long defaultPEs)
   :outertype: VmBuilder

setVmCreationFunction
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setVmCreationFunction(BiFunction<Double, Long, Vm> vmCreationFunction)
   :outertype: VmBuilder

   Sets a \ :java:ref:`BiFunction`\  used to create VMs. It must receive the MIPS capacity of each \ :java:ref:`Pe`\  and the number of PEs for the VM it will create.

   :param vmCreationFunction:

