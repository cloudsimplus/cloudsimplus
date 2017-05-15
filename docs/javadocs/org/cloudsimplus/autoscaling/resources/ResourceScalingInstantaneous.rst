.. java:import:: org.cloudbus.cloudsim.util MathUtil

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: java.util.function Function

ResourceScalingInstantaneous
============================

.. java:package:: org.cloudsimplus.autoscaling.resources
   :noindex:

.. java:type:: public class ResourceScalingInstantaneous implements ResourceScaling

   A \ :java:ref:`ResourceScaling`\  for which the capacity of the resource to be scaled will be instantaneously resized to move the Vm from the under or overload state. This way, the SLA violation time will be reduced.

   This scaling type will resize the resource capacity in the following way:

   ..

   * in underload conditions: it decreases the resource capacity to be equal to the current load of the resource being scaled;
   * in overload conditions: it increases the resource capacity to be equal to the current load of the resource being scaled.

   Finally it adds an extra amount of resource, defined by the \ :java:ref:`scaling factor <VerticalVmScaling.getScalingFactor()>`\ , for safety. This extra amount added is to enable the resource usage to grow up to the scaling factor without needing to resize the resource again. If it grows up to the scaling factor, a new up scaling request will be sent.

   If the scaling factor for this type of scaling is zero, it means that the scaling object
   will always resize the resource to the exact amount that is being used.

   :author: Manoel Campos da Silva Filho

Methods
-------
getResourceAmountToScale
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getResourceAmountToScale(VerticalVmScaling vmScaling)
   :outertype: ResourceScalingInstantaneous

