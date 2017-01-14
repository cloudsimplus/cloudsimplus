.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util.function Predicate

.. java:import:: java.util.function Supplier

VmScaling
=========

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public interface VmScaling

   An interface to allow implementing \ `horizontal and vertical scaling <https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling>`_\  of VMs.

   :author: Manoel Campos da Silva Filho

Fields
------
FALSE_PREDICATE
^^^^^^^^^^^^^^^

.. java:field::  Predicate<Vm> FALSE_PREDICATE
   :outertype: VmScaling

   A \ :java:ref:`Predicate`\  that always returns false independently of any condition.

NULL
^^^^

.. java:field::  VmScaling NULL
   :outertype: VmScaling

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`HorizontalVmScaling`\  objects.

Methods
-------
getOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method::  Predicate<Vm> getOverloadPredicate()
   :outertype: VmScaling

   Gets a \ :java:ref:`Predicate`\  that defines when \ :java:ref:`Vm <getVm()>`\  is overloaded or not, that will make the Vm's broker to dynamically create a new Vm to balance the load of new arrived Cloudlets.

   **See also:** :java:ref:`.setOverloadPredicate(Predicate)`

getVm
^^^^^

.. java:method::  Vm getVm()
   :outertype: VmScaling

   Gets the \ :java:ref:`Vm`\  that this Load Balancer is linked to.

scaleIfOverloaded
^^^^^^^^^^^^^^^^^

.. java:method::  void scaleIfOverloaded(double time)
   :outertype: VmScaling

   Performs the horizontal or vertical scale if the Vm is overloaded. The type of scale depends on implementing classes.

   :param time: current simulation time

setOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method::  HorizontalVmScaling setOverloadPredicate(Predicate<Vm> predicate)
   :outertype: VmScaling

   Sets a \ :java:ref:`Predicate`\  that defines when \ :java:ref:`Vm <getVm()>`\  is overloaded or not, that will make the Vm's broker to dynamically create a new Vm to balance the load of new arrived Cloudlets.

   :param predicate: a predicate that checks certain conditions to define that the Load Balancer's \ :java:ref:`Vm <getVm()>`\  is over utilized. The predicate receives the Vm to allow the predicate to define the over utilization condition. Such a condition can be defined, for instance, based on Vm's \ :java:ref:`Vm.getTotalUtilizationOfCpu(double)`\  CPU usage}.

setVm
^^^^^

.. java:method::  VmScaling setVm(Vm vm)
   :outertype: VmScaling

   Sets a \ :java:ref:`Vm`\  to this Load Balancer. The broker will call this Load Balancer in order to balance load when its Vm is over utilized.

   :param vm: the Vm to set

