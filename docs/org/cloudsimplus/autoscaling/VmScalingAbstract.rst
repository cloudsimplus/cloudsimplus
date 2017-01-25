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

getUnderloadPredicate
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Predicate<Vm> getUnderloadPredicate()
   :outertype: VmScalingAbstract

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: VmScalingAbstract

isTimeToCheckPredicate
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isTimeToCheckPredicate(double time)
   :outertype: VmScalingAbstract

   Checks if it is time to evaluate the \ :java:ref:`getOverloadPredicate()`\  and \ :java:ref:`getUnderloadPredicate()`\  to check if the Vm is over or underloaded, respectively.

   :param time: current simulation time
   :return: true if the over and underload predicate has to be checked, false otherwise

requestScaling
^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean requestScaling(double time)
   :outertype: VmScalingAbstract

   Performs the actual request to scale the Vm up or down, depending if it is over or underloaded, respectively. This method is automatically called by \ :java:ref:`requestScalingIfPredicateMatch(double)`\  when it is verified that the Vm is over or underloaded.

   :param time: current simulation time
   :return: true if the request was actually sent, false otherwise

requestScalingIfPredicateMatch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean requestScalingIfPredicateMatch(double time)
   :outertype: VmScalingAbstract

setOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VmScaling setOverloadPredicate(Predicate<Vm> predicate)
   :outertype: VmScalingAbstract

setUnderloadPredicate
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VmScaling setUnderloadPredicate(Predicate<Vm> predicate)
   :outertype: VmScalingAbstract

setVm
^^^^^

.. java:method:: @Override public final VmScaling setVm(Vm vm)
   :outertype: VmScalingAbstract

