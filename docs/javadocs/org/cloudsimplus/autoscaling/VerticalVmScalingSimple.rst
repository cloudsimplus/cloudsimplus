.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling.resources ResourceScalingGradual

.. java:import:: org.cloudsimplus.autoscaling.resources ResourceScaling

.. java:import:: java.util Objects

.. java:import:: java.util.function Function

VerticalVmScalingSimple
=======================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public class VerticalVmScalingSimple extends VmScalingAbstract implements VerticalVmScaling

   A \ :java:ref:`VerticalVmScaling`\  implementation that allows a \ :java:ref:`DatacenterBroker`\  to perform on demand up or down scaling for some VM resource such as RAM, CPU or Bandwidth.

   For each resource that is required to be scaled, a distinct VerticalVmScaling instance must assigned to the VM to be scaled.

   :author: Manoel Campos da Silva Filho

Constructors
------------
VerticalVmScalingSimple
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VerticalVmScalingSimple(Class<? extends ResourceManageable> resourceClassToScale, double scalingFactor)
   :outertype: VerticalVmScalingSimple

   Creates a VerticalVmScalingSimple with a \ :java:ref:`ResourceScaling`\  scaling type.

   :param resourceClassToScale: the class of Vm resource that this scaling object will request up or down scaling (such as \ :java:ref:`Ram`\ .class, \ :java:ref:`Bandwidth`\ .class or \ :java:ref:`Processor`\ .class).
   :param scalingFactor: the factor that will be used to scale a Vm resource up or down, whether if such a resource is over or underloaded, according to the defined predicates (a percentage value in scale from 0 to 1). In the case of up scaling, the value 1 will scale the resource in 100%, doubling its capacity.

   **See also:** :java:ref:`VerticalVmScaling.setResourceScaling(ResourceScaling)`

Methods
-------
getLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getLowerThresholdFunction()
   :outertype: VerticalVmScalingSimple

getResourceAmountToScale
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getResourceAmountToScale()
   :outertype: VerticalVmScalingSimple

getResourceClassToScale
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Class<? extends ResourceManageable> getResourceClassToScale()
   :outertype: VerticalVmScalingSimple

getResourceUsageThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getResourceUsageThresholdFunction()
   :outertype: VerticalVmScalingSimple

getScalingFactor
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getScalingFactor()
   :outertype: VerticalVmScalingSimple

getUpperThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getUpperThresholdFunction()
   :outertype: VerticalVmScalingSimple

getVmResourceToScale
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Resource getVmResourceToScale()
   :outertype: VerticalVmScalingSimple

isVmOverloaded
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isVmOverloaded()
   :outertype: VerticalVmScalingSimple

isVmUnderloaded
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isVmUnderloaded()
   :outertype: VerticalVmScalingSimple

requestScaling
^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean requestScaling(double time)
   :outertype: VerticalVmScalingSimple

requestScalingIfPredicateMatch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean requestScalingIfPredicateMatch(double time)
   :outertype: VerticalVmScalingSimple

setLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction)
   :outertype: VerticalVmScalingSimple

setResourceClassToScale
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> resourceClassToScale)
   :outertype: VerticalVmScalingSimple

setResourceScaling
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling)
   :outertype: VerticalVmScalingSimple

setScalingFactor
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setScalingFactor(double scalingFactor)
   :outertype: VerticalVmScalingSimple

setUpperThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction)
   :outertype: VerticalVmScalingSimple

