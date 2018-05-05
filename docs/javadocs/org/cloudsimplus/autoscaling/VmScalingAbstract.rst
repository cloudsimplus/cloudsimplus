.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Objects

VmScalingAbstract
=================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public abstract class VmScalingAbstract implements VmScaling

   A base class for implementing \ :java:ref:`HorizontalVmScaling`\  and \ :java:ref:`VerticalVmScaling`\ .

   :author: Manoel Campos da Silva Filho

Constructors
------------
VmScalingAbstract
^^^^^^^^^^^^^^^^^

.. java:constructor:: protected VmScalingAbstract()
   :outertype: VmScalingAbstract

Methods
-------
getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: VmScalingAbstract

isTimeToCheckPredicate
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isTimeToCheckPredicate(double time)
   :outertype: VmScalingAbstract

   Checks if it is time to evaluate weather the Vm is under or overloaded.

   :param time: current simulation time
   :return: true if it's time to check weather the Vm is over and underloaded, false otherwise

requestUpScaling
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean requestUpScaling(double time)
   :outertype: VmScalingAbstract

   Performs the actual request to scale the Vm up or down, depending if it is over or underloaded, respectively. This method is automatically called by \ :java:ref:`VmScaling.requestUpScalingIfPredicateMatches(org.cloudsimplus.listeners.VmHostEventInfo)`\  when it is verified that the Vm is over or underloaded.

   :param time: current simulation time
   :return: true if the request was actually sent, false otherwise

setLastProcessingTime
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setLastProcessingTime(double lastProcessingTime)
   :outertype: VmScalingAbstract

   Sets the last time the scheduler checked for VM overload.

   :param lastProcessingTime: the processing time to set

setVm
^^^^^

.. java:method:: @Override public final VmScaling setVm(Vm vm)
   :outertype: VmScalingAbstract

