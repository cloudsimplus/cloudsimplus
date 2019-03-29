.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: java.util List

BrokerBuilderInterface
======================

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public interface BrokerBuilderInterface extends Builder

   An interface to classes that build \ :java:ref:`DatacenterBrokerSimple`\  objects.

   :author: Manoel Campos da Silva Filho

Methods
-------
create
^^^^^^

.. java:method::  BrokerBuilderDecorator create()
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

