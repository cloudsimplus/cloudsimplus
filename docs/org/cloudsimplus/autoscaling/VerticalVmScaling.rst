.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

VerticalVmScaling
=================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public interface VerticalVmScaling extends VmScaling

   A Vm \ `Vertical Scaling <https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling>`_\  mechanism used by a \ :java:ref:`DatacenterBroker`\  to dynamically scale VM resources up or down, according to the current resource usage. For each resource that is supposed to be scaled, such as RAM, CPU and Bandwidth, a different VerticalVmScaling instance should be provided.

   :author: Manoel Campos da Silva Filho

Methods
-------
getScalingFactor
^^^^^^^^^^^^^^^^

.. java:method::  double getScalingFactor()
   :outertype: VerticalVmScaling

   Gets the factor that will be used to scale a Vm resource up or down, whether if such a resource is over or underloaded, according to the defined predicates.

   This is a percentage value in scale from 0 to 1. Every time the VM needs to be scaled up or down, this factor will be applied to increase or reduce a specific VM allocated resource.

   :return: the scaling factor

   **See also:** :java:ref:`.getOverloadPredicate()`

scaleIfOverloaded
^^^^^^^^^^^^^^^^^

.. java:method:: @Override  void scaleIfOverloaded(double time)
   :outertype: VerticalVmScaling

   Performs the vertical scale if the Vm is overloaded, according to the \ :java:ref:`getOverloadPredicate()`\  predicate, increasing the Vm resource to which the scaling object is linked to (that may be RAM, CPU, BW, etc), by the factor defined a scaling factor.

   The time interval in which it will be checked if the Vm is overloaded depends on the \ :java:ref:`Datacenter.getSchedulingInterval()`\  value. Make sure to set such a value to enable the periodic overload verification.

   :param time: current simulation time

   **See also:** :java:ref:`.getScalingFactor()`

setScalingFactor
^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling setScalingFactor(double scalingFactor)
   :outertype: VerticalVmScaling

   Sets the factor that will be used to scale a Vm resource up or down, whether if such a resource is over or underloaded, according to the defined predicates.

   This is a percentage value in scale from 0 to 1. Every time the VM needs to be scaled up or down, this factor will be applied to increase or reduce a specific VM allocated resource.

   :param scalingFactor: the scaling factor to set

   **See also:** :java:ref:`.getOverloadPredicate()`

