.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.resources Bandwidth

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Ram

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Objects

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

   Creates a VerticalVmScaling.

   :param resourceClassToScale: the class of Vm resource that this scaling object will request up or down scaling (such as \ :java:ref:`Ram`\ .class, \ :java:ref:`Bandwidth`\ .class or \ :java:ref:`Pe`\ .class).
   :param scalingFactor: the factor that will be used to scale a Vm resource up or down, whether if such a resource is over or underloaded, according to the defined predicates (a percentage value in scale from 0 to 1). In the case of up scaling, the value 1 will scale the resource in 100%, doubling its capacity.

Methods
-------
getResourceClassToScale
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Class<? extends ResourceManageable> getResourceClassToScale()
   :outertype: VerticalVmScalingSimple

getScalingFactor
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getScalingFactor()
   :outertype: VerticalVmScalingSimple

requestUpScaling
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean requestUpScaling(double time)
   :outertype: VerticalVmScalingSimple

setResourceClassToScale
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> resourceClassToScale)
   :outertype: VerticalVmScalingSimple

setScalingFactor
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VerticalVmScaling setScalingFactor(double scalingFactor)
   :outertype: VerticalVmScalingSimple

