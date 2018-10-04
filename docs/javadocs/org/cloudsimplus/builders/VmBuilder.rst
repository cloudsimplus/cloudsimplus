.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms VmSimple

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners VmDatacenterEventInfo

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.function Supplier

VmBuilder
=========

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class VmBuilder

   A Builder class to create \ :java:ref:`Vm`\  objects.

   :author: Manoel Campos da Silva Filho

Constructors
------------
VmBuilder
^^^^^^^^^

.. java:constructor:: public VmBuilder(DatacenterBrokerSimple broker)
   :outertype: VmBuilder

Methods
-------
createAndSubmitOneVm
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder createAndSubmitOneVm()
   :outertype: VmBuilder

createAndSubmitVms
^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder createAndSubmitVms(int amount)
   :outertype: VmBuilder

getBandwidth
^^^^^^^^^^^^

.. java:method:: public long getBandwidth()
   :outertype: VmBuilder

getMips
^^^^^^^

.. java:method:: public double getMips()
   :outertype: VmBuilder

getOnUpdateVmProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public EventListener<VmHostEventInfo> getOnUpdateVmProcessingListener()
   :outertype: VmBuilder

getPes
^^^^^^

.. java:method:: public int getPes()
   :outertype: VmBuilder

getRam
^^^^^^

.. java:method:: public long getRam()
   :outertype: VmBuilder

getSize
^^^^^^^

.. java:method:: public long getSize()
   :outertype: VmBuilder

getVmById
^^^^^^^^^

.. java:method:: public Vm getVmById(int id)
   :outertype: VmBuilder

getVms
^^^^^^

.. java:method:: public List<Vm> getVms()
   :outertype: VmBuilder

setBandwidth
^^^^^^^^^^^^

.. java:method:: public VmBuilder setBandwidth(long defaultBW)
   :outertype: VmBuilder

setCloudletSchedulerSupplier
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setCloudletSchedulerSupplier(Supplier<CloudletScheduler> cloudletSchedulerSupplier)
   :outertype: VmBuilder

   Sets a \ :java:ref:`Supplier`\  that is accountable to create CloudletScheduler for requested VMs.

   :param cloudletSchedulerSupplier: the CloudletScheduler Supplier to set

setMips
^^^^^^^

.. java:method:: public VmBuilder setMips(double defaultMIPS)
   :outertype: VmBuilder

setOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnHostAllocationListener(EventListener<VmHostEventInfo> onHostAllocationListener)
   :outertype: VmBuilder

setOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnHostDeallocationListener(EventListener<VmHostEventInfo> onHostDeallocationListener)
   :outertype: VmBuilder

setOnUpdateVmProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnUpdateVmProcessingListener(EventListener<VmHostEventInfo> onUpdateVmProcessing)
   :outertype: VmBuilder

setOnVmCreationFilatureListenerForAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setOnVmCreationFilatureListenerForAllVms(EventListener<VmDatacenterEventInfo> onVmCreationFailureListener)
   :outertype: VmBuilder

setPes
^^^^^^

.. java:method:: public VmBuilder setPes(int defaultPEs)
   :outertype: VmBuilder

setRam
^^^^^^

.. java:method:: public VmBuilder setRam(int defaultRAM)
   :outertype: VmBuilder

setSize
^^^^^^^

.. java:method:: public VmBuilder setSize(long defaultSize)
   :outertype: VmBuilder

