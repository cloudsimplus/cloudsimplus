.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

VmScalingNull
=============

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: final class VmScalingNull implements VmScaling

   A class that implements the Null Object Design Pattern for \ :java:ref:`VmScaling`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmScaling.NULL`

Methods
-------
getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: VmScalingNull

requestUpScalingIfPredicateMatches
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt)
   :outertype: VmScalingNull

setVm
^^^^^

.. java:method:: @Override public VmScaling setVm(Vm vm)
   :outertype: VmScalingNull

