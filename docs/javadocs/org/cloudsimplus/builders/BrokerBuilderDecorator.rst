.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: java.util List

.. java:import:: java.util Objects

BrokerBuilderDecorator
======================

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class BrokerBuilderDecorator implements BrokerBuilderInterface

   A class that implements the Decorator Design Pattern in order to include features in a existing class. It is used to ensure that specific methods are called only after a given method is called.

   For instance, the methods \ :java:ref:`getVmBuilder()`\  and \ :java:ref:`getCloudletBuilder()`\  can only be called after some \ :java:ref:`DatacenterBrokerSimple`\  was created by calling the method \ :java:ref:`createBroker()`\ . By this way, after the method is called, it returns an instance of this decorator that allow chained call to the specific decorator methods as the following example:

   ..

   * \ :java:ref:`createBroker() <createBroker()>`\ .\ :java:ref:`getVmBuilder() <getVmBuilder()>`\

   :author: Manoel Campos da Silva Filho

Constructors
------------
BrokerBuilderDecorator
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public BrokerBuilderDecorator(BrokerBuilder builder, DatacenterBrokerSimple broker)
   :outertype: BrokerBuilderDecorator

Methods
-------
createBroker
^^^^^^^^^^^^

.. java:method:: @Override public BrokerBuilderDecorator createBroker()
   :outertype: BrokerBuilderDecorator

findBroker
^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker findBroker(int id)
   :outertype: BrokerBuilderDecorator

get
^^^

.. java:method:: @Override public DatacenterBroker get(int index)
   :outertype: BrokerBuilderDecorator

getBroker
^^^^^^^^^

.. java:method:: public DatacenterBroker getBroker()
   :outertype: BrokerBuilderDecorator

   :return: the latest created broker

getBrokers
^^^^^^^^^^

.. java:method:: @Override public List<DatacenterBroker> getBrokers()
   :outertype: BrokerBuilderDecorator

getCloudletBuilder
^^^^^^^^^^^^^^^^^^

.. java:method:: public CloudletBuilder getCloudletBuilder()
   :outertype: BrokerBuilderDecorator

   :return: the CloudletBuilder in charge of creating Cloudlets to the latest DatacenterBroker created by this BrokerBuilder

getVmBuilder
^^^^^^^^^^^^

.. java:method:: public VmBuilder getVmBuilder()
   :outertype: BrokerBuilderDecorator

   :return: the VmBuilder in charge of creating VMs to the latest DatacenterBroker created by this BrokerBuilder

