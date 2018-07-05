.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util.function Function

.. java:import:: java.util.function UnaryOperator

VmClonerSimple
==============

.. java:package:: org.cloudsimplus.faultinjection
   :noindex:

.. java:type:: public class VmClonerSimple implements VmCloner

   A basic implementation of a \ :java:ref:`VmCloner`\ .

   :author: raysaoliveira

Constructors
------------
VmClonerSimple
^^^^^^^^^^^^^^

.. java:constructor:: public VmClonerSimple(UnaryOperator<Vm> vmClonerFunction, Function<Vm, List<Cloudlet>> cloudletsClonerFunction)
   :outertype: VmClonerSimple

   Creates a \ :java:ref:`Vm`\  cloner which makes the maximum of 1 Vm clone.

   :param vmClonerFunction: the \ :java:ref:`UnaryOperator`\  to be used to clone \ :java:ref:`Vm`\ s.
   :param cloudletsClonerFunction: the \ :java:ref:`Function`\  to be used to clone Vm's \ :java:ref:`Cloudlet`\ s.

   **See also:** :java:ref:`.setMaxClonesNumber(int)`

Methods
-------
clone
^^^^^

.. java:method:: @Override public Map.Entry<Vm, List<Cloudlet>> clone(Vm sourceVm)
   :outertype: VmClonerSimple

getClonedVmsNumber
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getClonedVmsNumber()
   :outertype: VmClonerSimple

getMaxClonesNumber
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getMaxClonesNumber()
   :outertype: VmClonerSimple

isMaxClonesNumberReached
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isMaxClonesNumberReached()
   :outertype: VmClonerSimple

setCloudletsClonerFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VmCloner setCloudletsClonerFunction(Function<Vm, List<Cloudlet>> cloudletsClonerFunction)
   :outertype: VmClonerSimple

setMaxClonesNumber
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmCloner setMaxClonesNumber(int maxClonesNumber)
   :outertype: VmClonerSimple

setVmClonerFunction
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final VmCloner setVmClonerFunction(UnaryOperator<Vm> vmClonerFunction)
   :outertype: VmClonerSimple

