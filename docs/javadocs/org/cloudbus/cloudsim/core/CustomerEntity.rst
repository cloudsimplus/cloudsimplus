.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

CustomerEntity
==============

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface CustomerEntity extends ChangeableId, Delayable

   Represents an object that is owned by a \ :java:ref:`DatacenterBroker`\ , namely \ :java:ref:`Vm`\  and \ :java:ref:`Cloudlet`\ .

   :author: raysaoliveira

Methods
-------
getBroker
^^^^^^^^^

.. java:method::  DatacenterBroker getBroker()
   :outertype: CustomerEntity

   Gets the \ :java:ref:`DatacenterBroker`\  that represents the owner of this object.

   :return: the broker or  if a broker has not been set yet

setBroker
^^^^^^^^^

.. java:method::  CustomerEntity setBroker(DatacenterBroker broker)
   :outertype: CustomerEntity

   Sets a \ :java:ref:`DatacenterBroker`\  that represents the owner of this object.

   :param broker: the \ :java:ref:`DatacenterBroker`\  to set

