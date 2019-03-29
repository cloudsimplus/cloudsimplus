.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util NoSuchElementException

BrokerBuilder
=============

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class BrokerBuilder implements BrokerBuilderInterface

   A Builder class to createBroker \ :java:ref:`DatacenterBrokerSimple`\  objects.

   :author: Manoel Campos da Silva Filho

Constructors
------------
BrokerBuilder
^^^^^^^^^^^^^

.. java:constructor:: public BrokerBuilder(SimulationScenarioBuilder scenario)
   :outertype: BrokerBuilder

Methods
-------
create
^^^^^^

.. java:method:: @Override public BrokerBuilderDecorator create()
   :outertype: BrokerBuilder

findBroker
^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker findBroker(int id)
   :outertype: BrokerBuilder

get
^^^

.. java:method:: @Override public DatacenterBroker get(int index)
   :outertype: BrokerBuilder

getBrokers
^^^^^^^^^^

.. java:method:: @Override public List<DatacenterBroker> getBrokers()
   :outertype: BrokerBuilder

