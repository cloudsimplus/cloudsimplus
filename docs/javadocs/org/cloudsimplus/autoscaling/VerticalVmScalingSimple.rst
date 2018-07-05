.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling.resources ResourceScaling

.. java:import:: org.cloudsimplus.autoscaling.resources ResourceScalingGradual

.. java:import:: org.cloudsimplus.autoscaling.resources ResourceScalingInstantaneous

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util Objects

.. java:import:: java.util.function Function

VerticalVmScalingSimple
=======================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public class VerticalVmScalingSimple extends VmScalingAbstract implements VerticalVmScaling

   A \ :java:ref:`VerticalVmScaling`\  implementation which allows a \ :java:ref:`DatacenterBroker`\  to perform on demand up or down scaling for some \ :java:ref:`Vm`\  resource, such as \ :java:ref:`Ram`\ , \ :java:ref:`Pe`\  or \ :java:ref:`Bandwidth`\ .

   For each resource that is required to be scaled, a distinct \ :java:ref:`VerticalVmScaling`\  instance must be assigned to the VM to be scaled.

   :author: Manoel Campos da Silva Filho

Constructors
------------
VerticalVmScalingSimple
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VerticalVmScalingSimple(Class<? extends ResourceManageable> resourceClassToScale, double scalingFactor)
   :outertype: VerticalVmScalingSimple

   Creates a VerticalVmScalingSimple with a \ :java:ref:`ResourceScalingGradual`\  scaling type.

   :param resourceClassToScale: the class of Vm resource that this scaling object will request up or down scaling (such as \ :java:ref:`Ram`\ .class, \ :java:ref:`Bandwidth`\ .class or \ :java:ref:`Processor`\ .class).
   :param scalingFactor: the factor (a percentage value in scale from 0 to 1) that will be used to scale a Vm resource up or down, whether such a resource is over or underloaded, according to the defined predicates. In the case of up scaling, the value 1 will scale the resource in 100%, doubling its capacity.

   **See also:** :java:ref:`VerticalVmScaling.setResourceScaling(ResourceScaling)`

Methods
-------
getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: VerticalVmScalingSimple

getLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getLowerThresholdFunction()
   :outertype: VerticalVmScalingSimple

getResource
^^^^^^^^^^^

.. java:method:: @Override public Resource getResource()
   :outertype: VerticalVmScalingSimple

getResourceAmountToScale
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getResourceAmountToScale()
   :outertype: VerticalVmScalingSimple

   {@inheritDoc}

   If a \ :java:ref:`ResourceScaling`\  implementation such as \ :java:ref:`ResourceScalingGradual`\  or \ :java:ref:`ResourceScalingInstantaneous`\  are used, it will rely on the \ :java:ref:`getScalingFactor()`\  to compute the amount of resource to scale. Other implementations may use the scaling factor by it is up to them.

   \ **NOTE:**\  The return of this method is rounded up to avoid
   values between ]0 and 1[. For instance, up scaling the number of CPUs in 0.5 means that half of a CPU should be added to the VM. Since number of CPUs is an integer value, this 0.5 will be converted to zero, causing no effect. For other resources such as RAM, adding 0.5 MB has not practical advantages either. This way, the value is always rounded up.

   :return: {@inheritDoc}

getResourceClass
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Class<? extends ResourceManageable> getResourceClass()
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

isVmOverloaded
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isVmOverloaded()
   :outertype: VerticalVmScalingSimple

isVmUnderloaded
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isVmUnderloaded()
   :outertype: VerticalVmScalingSimple

requestUpScaling
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean requestUpScaling(double time)
   :outertype: VerticalVmScalingSimple

requestUpScalingIfPredicateMatches
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt)
   :outertype: VerticalVmScalingSimple

setLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction)
   :outertype: VerticalVmScalingSimple

setResourceClass
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setResourceClass(Class<? extends ResourceManageable> resourceClass)
   :outertype: VerticalVmScalingSimple

setResourceScaling
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling)
   :outertype: VerticalVmScalingSimple

   {@inheritDoc}

   This class's constructors define a \ :java:ref:`ResourceScalingGradual`\  as the default \ :java:ref:`ResourceScaling`\ .

   :param resourceScaling: {@inheritDoc}
   :return: {@inheritDoc}

setScalingFactor
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setScalingFactor(double scalingFactor)
   :outertype: VerticalVmScalingSimple

setUpperThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction)
   :outertype: VerticalVmScalingSimple

