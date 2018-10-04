.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

UniquelyIdentifiable
====================

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface UniquelyIdentifiable extends Identifiable

   An interface for objects that have an Unique Identifier (UID) that is compounded by a \ :java:ref:`DatacenterBroker`\  ID and the object ID.

   :author: Manoel Campos da Silva Filho

Methods
-------
getUid
^^^^^^

.. java:method:: static String getUid(long brokerId, long id)
   :outertype: UniquelyIdentifiable

   Generates an Unique Identifier (UID).

   :param brokerId: the id of the \ :java:ref:`DatacenterBroker`\  (user)
   :param id: the object id
   :return: the generated UID

getUid
^^^^^^

.. java:method::  String getUid()
   :outertype: UniquelyIdentifiable

   Gets the Unique Identifier (UID) for the VM, that is compounded by the id of a \ :java:ref:`DatacenterBroker`\  (representing the User) and the object id.

