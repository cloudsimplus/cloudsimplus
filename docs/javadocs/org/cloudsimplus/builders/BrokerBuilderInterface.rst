.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

BrokerBuilderInterface
======================

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public interface BrokerBuilderInterface

   An interface to classes that build \ :java:ref:`DatacenterBrokerSimple`\  objects.

   :author: Manoel Campos da Silva Filho

Methods
-------
createBroker
^^^^^^^^^^^^

.. java:method::  BrokerBuilderDecorator createBroker()
   :outertype: BrokerBuilderInterface

findBroker
^^^^^^^^^^

.. java:method::  DatacenterBroker findBroker(int id)
   :outertype: BrokerBuilderInterface

get
^^^

.. java:method::  DatacenterBroker get(int index)
   :outertype: BrokerBuilderInterface

getBrokers
^^^^^^^^^^

.. java:method::  List<DatacenterBroker> getBrokers()
   :outertype: BrokerBuilderInterface

