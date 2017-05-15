.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

ResourceScaling
===============

.. java:package:: org.cloudsimplus.autoscaling.resources
   :noindex:

.. java:type:: @FunctionalInterface public interface ResourceScaling

   A \ :java:ref:`FunctionalInterface`\  to define how the capacity of the resource to be scaled by a \ :java:ref:`VerticalVmScaling`\  will be resized, according to the defined \ :java:ref:`scaling factor <VerticalVmScaling.getScalingFactor()>`\ .

   The interval in which the under and overload conditions are checked is defined by the \ :java:ref:`Datacenter.getSchedulingInterval()`\ . This way, during one interval and another, there may be some SLA violation if the resource is overloaded between these intervals.

   There are some implementations of this functional interface such as \ :java:ref:`ResourceScalingGradual`\  and \ :java:ref:`ResourceScalingInstantaneous`\ . New ones can be defined using Lambda Expressions.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`ResourceScalingGradual`, :java:ref:`ResourceScalingInstantaneous`

Fields
------
NULL
^^^^

.. java:field::  ResourceScaling NULL
   :outertype: ResourceScaling

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`ResourceScaling`\  objects.

Methods
-------
getResourceAmountToScale
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getResourceAmountToScale(VerticalVmScaling vmScaling)
   :outertype: ResourceScaling

   Computes the amount of resource to scale up or down, depending if the resource is over or underloaded, respectively.

   :param vmScaling: the \ :java:ref:`VerticalVmScaling`\  object that is in charge to scale a resource.

