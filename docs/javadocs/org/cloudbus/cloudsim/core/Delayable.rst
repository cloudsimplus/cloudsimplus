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

   Gets the delay (in seconds) that a \ :java:ref:`DatacenterBroker`\  has to include when submitting the object, in order that it will be assigned to a VM only after this delay has expired.

   :return: the submission delay

setSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method::  void setSubmissionDelay(double submissionDelay)
   :outertype: Delayable

   Sets the delay (in seconds) that a \ :java:ref:`DatacenterBroker`\  has to include when submitting the object, in order that it will be assigned to a VM only after this delay has expired. The delay should be greater or equal to zero.

   :param submissionDelay: the amount of seconds from the current simulation time that the object will wait to be submitted

