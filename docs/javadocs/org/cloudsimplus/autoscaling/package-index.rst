org.cloudsimplus.autoscaling
============================

Provides classes to enable \ `horizontal and vertical scaling <https://en.wikipedia.org/wiki/Scalability>`_\  of VMs in order to, respectively, adapt resource requirements to current workload and to balance load across different VMs.

These scaling mechanisms define a \ :java:ref:`java.util.function.Predicate`\  that define the condition to fire the scaling mechanism. The \ :java:ref:`org.cloudbus.cloudsim.brokers.DatacenterBroker`\  that the VM belongs to is accountable to evaluate the predicate and then request the scaling mechanism to act.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudsimplus.autoscaling

.. toctree::
   :maxdepth: 1

   HorizontalVmScaling
   HorizontalVmScalingNull
   HorizontalVmScalingSimple
   VerticalVmScaling
   VerticalVmScalingNull
   VerticalVmScalingSimple
   VmScaling
   VmScalingAbstract
   VmScalingNull

