.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

ResourceScalingGradual
======================

.. java:package:: org.cloudsimplus.autoscaling.resources
   :noindex:

.. java:type:: public class ResourceScalingGradual implements ResourceScaling

   A \ :java:ref:`ResourceScaling`\  for which the capacity of the resource to be scaled will be gradually resized according to the defined \ :java:ref:`scaling factor <VerticalVmScaling.getScalingFactor()>`\ . This scaling type may not automatically move a Vm from an under or overload state, since it will increase or decrease the resource capacity the specified fraction at a time.

   This gradual resize may give the opportunity for the Vm workload to return to the normal state, without requiring further scaling. However, if the workload doesn't return quickly to the normal and expected state, that may cause longer SLA violation time.

   \ **This is the default type of scaling in case one is not defined.**\

   :author: Manoel Campos da Silva Filho

Methods
-------
getResourceAmountToScale
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getResourceAmountToScale(VerticalVmScaling vmScaling)
   :outertype: ResourceScalingGradual

