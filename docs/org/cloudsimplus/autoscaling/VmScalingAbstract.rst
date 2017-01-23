.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Objects

.. java:import:: java.util.function Predicate

VmScalingAbstract
=================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public abstract class VmScalingAbstract implements VmScaling

   A base class for implementing \ :java:ref:`HorizontalVmScaling`\  and \ :java:ref:`VerticalVmScaling`\ .

   :author: Manoel Campos da Silva Filho

Fields
------
lastProcessingTime
^^^^^^^^^^^^^^^^^^

.. java:field:: protected double lastProcessingTime
   :outertype: VmScalingAbstract

   Last time the scheduler checked for VM overload.

Constructors
------------
VmScalingAbstract
^^^^^^^^^^^^^^^^^

.. java:constructor:: protected VmScalingAbstract()
   :outertype: VmScalingAbstract

Methods
-------
getOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Predicate<Vm> getOverloadPredicate()
   :outertype: VmScalingAbstract

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: VmScalingAbstract

isTimeToCheckOverload
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isTimeToCheckOverload(double time)
   :outertype: VmScalingAbstract

   Checks if it is time to evaluate the \ :java:ref:`getOverloadPredicate()`\  to check if the Vm is overloaded or not.

   :param time: current simulation time
   :return: true if the overload predicate has to be checked, false otherwise

requestUpScaling
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean requestUpScaling(double time)
   :outertype: VmScalingAbstract

   Performs the actual request to up scale the Vm. This method is automatically called by \ :java:ref:`requestUpScalingIfOverloaded(double)`\  when it is verified that the Vm is overloaded.

   :param time: current simulation time
   :return: true if the request was actually sent, false otherwise

requestUpScalingIfOverloaded
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean requestUpScalingIfOverloaded(double time)
   :outertype: VmScalingAbstract

setOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VmScaling setOverloadPredicate(Predicate<Vm> predicate)
   :outertype: VmScalingAbstract

setVm
^^^^^

.. java:method:: @Override public final VmScaling setVm(Vm vm)
   :outertype: VmScalingAbstract

