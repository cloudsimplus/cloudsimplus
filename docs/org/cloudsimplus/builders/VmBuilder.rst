.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletSchedulerSpaceShared

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms VmSimple

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: org.cloudsimplus.listeners VmDatacenterEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

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

getBw
^^^^^

.. java:method:: public long getBw()
   :outertype: VmBuilder

getCloudletSchedulerClass
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public CloudletScheduler getCloudletSchedulerClass()
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

.. java:method:: public int getRam()
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

setBw
^^^^^

.. java:method:: public VmBuilder setBw(long defaultBW)
   :outertype: VmBuilder

setCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public VmBuilder setCloudletScheduler(CloudletScheduler defaultCloudletScheduler)
   :outertype: VmBuilder

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

