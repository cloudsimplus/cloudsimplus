.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.vms Vm

Delayable
=========

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface Delayable

   Defines methods for an object that its execution can be delayed by some time when it is submitted to a to a \ :java:ref:`Datacenter`\  by a \ :java:ref:`DatacenterBroker`\ .

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Vm`, :java:ref:`Cloudlet`

Methods
-------
getSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method::  double getSubmissionDelay()
   :outertype: Delayable

   Gets the time (in seconds) that a \ :java:ref:`DatacenterBroker`\  will wait to request the creation of the object. This is a relative time from the current simulation time.

   :return: the submission delay (in seconds)

setSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method::  void setSubmissionDelay(double submissionDelay)
   :outertype: Delayable

   Sets the time (in seconds) that a \ :java:ref:`DatacenterBroker`\  will wait to request the creation of the object. This is a relative time from the current simulation time.

   :param submissionDelay: the amount of seconds from the current simulation time that the object will wait to be submitted

