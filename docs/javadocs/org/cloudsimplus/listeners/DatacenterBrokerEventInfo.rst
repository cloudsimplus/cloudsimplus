.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

DatacenterBrokerEventInfo
=========================

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface DatacenterBrokerEventInfo extends EventInfo

   An interface that represent data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`DatacenterBroker`\ .

   :author: Manoel Campos da Silva Filho

Methods
-------
getDatacenterBroker
^^^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterBroker getDatacenterBroker()
   :outertype: DatacenterBrokerEventInfo

   Gets the \ :java:ref:`DatacenterBroker`\  for which the event happened.

of
^^

.. java:method:: static DatacenterBrokerEventInfo of(EventListener<? extends EventInfo> listener, DatacenterBroker broker)
   :outertype: DatacenterBrokerEventInfo

   Gets a \ ``DatacenterBrokerEventInfo``\  instance from the given parameters.

   :param listener: the listener to be notified about the event
   :param broker: the \ :java:ref:`DatacenterBroker`\  where the event happened

