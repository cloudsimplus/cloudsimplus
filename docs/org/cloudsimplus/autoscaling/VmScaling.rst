.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util.function Predicate

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

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`VmScaling`\  objects.

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

requestUpScalingIfOverloaded
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean requestUpScalingIfOverloaded(double time)
   :outertype: VmScaling

   Requests a \ :java:ref:`horizontal <HorizontalVmScaling>`\  or \ :java:ref:`vertical <VerticalVmScaling>`\  scale if the Vm is overloaded. The type of scale depends on implementing classes. The scaling request will be sent to the broker only if the \ :java:ref:`getOverloadPredicate()`\  returns true.

   :param time: current simulation time
   :return: true if the Vm is overloaded and and up scaling request was sent to the broker, false otherwise

setOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method::  VmScaling setOverloadPredicate(Predicate<Vm> predicate)
   :outertype: VmScaling

   Sets a \ :java:ref:`Predicate`\  that defines when \ :java:ref:`Vm <getVm()>`\  is overloaded or not, that will make the Vm's broker to dynamically create a new Vm to balance the load of new arrived Cloudlets.

   :param predicate: a predicate that checks certain conditions to define that the Load Balancer's \ :java:ref:`Vm <getVm()>`\  is over utilized. The predicate receives the Vm to allow the predicate to define the over utilization condition. Such a condition can be defined, for instance, based on Vm's \ :java:ref:`Vm.getCpuPercentUse(double)`\  CPU usage}.

setVm
^^^^^

.. java:method::  VmScaling setVm(Vm vm)
   :outertype: VmScaling

   Sets a \ :java:ref:`Vm`\  to this Load Balancer. The broker will call this Load Balancer in order to balance load when its Vm is over utilized.

   When the VmScaling is assigned to a Vm, the Vm sets itself to the VmScaling object, creating an association between the two objects.

   :param vm: the Vm to set

