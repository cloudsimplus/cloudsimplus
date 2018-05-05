.. java:import:: org.cloudbus.cloudsim.resources Resource

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling.resources ResourceScaling

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util.function Function

VerticalVmScalingNull
=====================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: final class VerticalVmScalingNull implements VerticalVmScaling

   A class that implements the Null Object Design Pattern for \ :java:ref:`VerticalVmScaling`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VerticalVmScaling.NULL`

Methods
-------
getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: VerticalVmScalingNull

getLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getLowerThresholdFunction()
   :outertype: VerticalVmScalingNull

getResource
^^^^^^^^^^^

.. java:method:: @Override public Resource getResource()
   :outertype: VerticalVmScalingNull

getResourceAmountToScale
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getResourceAmountToScale()
   :outertype: VerticalVmScalingNull

getResourceClass
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Class<? extends ResourceManageable> getResourceClass()
   :outertype: VerticalVmScalingNull

getResourceUsageThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getResourceUsageThresholdFunction()
   :outertype: VerticalVmScalingNull

getScalingFactor
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getScalingFactor()
   :outertype: VerticalVmScalingNull

getUpperThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getUpperThresholdFunction()
   :outertype: VerticalVmScalingNull

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: VerticalVmScalingNull

isVmOverloaded
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isVmOverloaded()
   :outertype: VerticalVmScalingNull

isVmUnderloaded
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isVmUnderloaded()
   :outertype: VerticalVmScalingNull

requestUpScalingIfPredicateMatches
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt)
   :outertype: VerticalVmScalingNull

setLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction)
   :outertype: VerticalVmScalingNull

setResourceClass
^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling setResourceClass(Class<? extends ResourceManageable> resourceClass)
   :outertype: VerticalVmScalingNull

setResourceScaling
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling)
   :outertype: VerticalVmScalingNull

setScalingFactor
^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling setScalingFactor(double scalingFactor)
   :outertype: VerticalVmScalingNull

setUpperThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction)
   :outertype: VerticalVmScalingNull

setVm
^^^^^

.. java:method:: @Override public VmScaling setVm(Vm vm)
   :outertype: VerticalVmScalingNull

