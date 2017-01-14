.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

Builder
=======

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public abstract class Builder

   An abstract Builder for creation of CloudSim objects, such as \ :java:ref:`Datacenter`\ , \ :java:ref:`Host`\ , \ :java:ref:`Vm`\  \ :java:ref:`DatacenterBroker`\  and \ :java:ref:`Cloudlet`\ . The builders helps in the creation of such objects, by allowing to set standard attribute's values in order to create several objects with the same characteristics.

   :author: Manoel Campos da Silva Filho

Methods
-------
validateAmount
^^^^^^^^^^^^^^

.. java:method:: public void validateAmount(double amount)
   :outertype: Builder

