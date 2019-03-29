.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletSimple

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelFull

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners CloudletVmEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.function BiFunction

CloudletBuilder
===============

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class CloudletBuilder implements Builder

   A Builder class to create \ :java:ref:`Cloudlet`\  objects.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CloudletBuilder
^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletBuilder(BrokerBuilderDecorator brokerBuilder, DatacenterBrokerSimple broker)
   :outertype: CloudletBuilder

Methods
-------
create
^^^^^^

.. java:method:: public CloudletBuilder create(int amount, int initialId)
   :outertype: CloudletBuilder

create
^^^^^^

.. java:method:: public CloudletBuilder create(int amount)
   :outertype: CloudletBuilder

createAndSubmit
^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder createAndSubmit(int amount)
   :outertype: CloudletBuilder

createAndSubmit
^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder createAndSubmit(int amount, int initialId)
   :outertype: CloudletBuilder

getBrokerBuilder
^^^^^^^^^^^^^^^^

.. java:method:: public BrokerBuilderDecorator getBrokerBuilder()
   :outertype: CloudletBuilder

getCloudlets
^^^^^^^^^^^^

.. java:method:: public List<Cloudlet> getCloudlets()
   :outertype: CloudletBuilder

getFileSize
^^^^^^^^^^^

.. java:method:: public long getFileSize()
   :outertype: CloudletBuilder

getLength
^^^^^^^^^

.. java:method:: public long getLength()
   :outertype: CloudletBuilder

getOutputSize
^^^^^^^^^^^^^

.. java:method:: public long getOutputSize()
   :outertype: CloudletBuilder

getPes
^^^^^^

.. java:method:: public int getPes()
   :outertype: CloudletBuilder

setCloudletCreationFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setCloudletCreationFunction(BiFunction<Long, Integer, Cloudlet> cloudletCreationFunction)
   :outertype: CloudletBuilder

   Sets a \ :java:ref:`BiFunction`\  used to create Cloudlets. It must length of the Cloudlet (in MI) and the number of PEs it will require.

   :param cloudletCreationFunction:

setFileSize
^^^^^^^^^^^

.. java:method:: public CloudletBuilder setFileSize(long defaultFileSize)
   :outertype: CloudletBuilder

setLength
^^^^^^^^^

.. java:method:: public CloudletBuilder setLength(long defaultLength)
   :outertype: CloudletBuilder

setOnCloudletFinishEventListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder setOnCloudletFinishEventListener(EventListener<CloudletVmEventInfo> defaultOnCloudletFinishEventListener)
   :outertype: CloudletBuilder

setOutputSize
^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder setOutputSize(long defaultOutputSize)
   :outertype: CloudletBuilder

setPEs
^^^^^^

.. java:method:: public CloudletBuilder setPEs(int defaultPEs)
   :outertype: CloudletBuilder

setRequiredFiles
^^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder setRequiredFiles(List<String> requiredFiles)
   :outertype: CloudletBuilder

setUtilizationModelBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder setUtilizationModelBw(UtilizationModel utilizationModelBw)
   :outertype: CloudletBuilder

setUtilizationModelCpu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder setUtilizationModelCpu(UtilizationModel utilizationModelCpu)
   :outertype: CloudletBuilder

setUtilizationModelCpuRamAndBw
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final CloudletBuilder setUtilizationModelCpuRamAndBw(UtilizationModel utilizationModel)
   :outertype: CloudletBuilder

   Sets the same utilization model for CPU, RAM and BW. By this way, at a time t, every one of the 3 resources will use the same percentage of its capacity.

   :param utilizationModel: the utilization model to set

setUtilizationModelRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder setUtilizationModelRam(UtilizationModel utilizationModelRam)
   :outertype: CloudletBuilder

setVm
^^^^^

.. java:method:: public CloudletBuilder setVm(Vm defaultVm)
   :outertype: CloudletBuilder

submitCloudlets
^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder submitCloudlets()
   :outertype: CloudletBuilder

   Submits the list of created cloudlets to the latest created broker.

   :return: the CloudletBuilder instance

