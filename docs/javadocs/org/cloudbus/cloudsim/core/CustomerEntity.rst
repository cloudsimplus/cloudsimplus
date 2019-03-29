.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.vms Vm

CustomerEntity
==============

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface CustomerEntity extends UniquelyIdentifiable, ChangeableId, Delayable

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

getSimulation
^^^^^^^^^^^^^

.. java:method::  Simulation getSimulation()
   :outertype: CustomerEntity

   Gets the CloudSim instance that represents the simulation the Entity is related to.

setBroker
^^^^^^^^^

.. java:method::  void setBroker(DatacenterBroker broker)
   :outertype: CustomerEntity

   Sets a \ :java:ref:`DatacenterBroker`\  that represents the owner of this object.

   :param broker: the \ :java:ref:`DatacenterBroker`\  to set

