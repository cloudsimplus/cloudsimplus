.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

VmScaling
=========

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public interface VmScaling

   An interface to allow implementing \ `horizontal and vertical scaling <https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling>`_\  of \ :java:ref:`Vm`\ s.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  VmScaling NULL
   :outertype: VmScaling

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`VmScaling`\  objects.

Methods
-------
getVm
^^^^^

.. java:method::  Vm getVm()
   :outertype: VmScaling

   Gets the \ :java:ref:`Vm`\  that this Load Balancer is linked to.

requestUpScalingIfPredicateMatches
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt)
   :outertype: VmScaling

   Requests the Vm to be scaled up or down if it is over or underloaded, respectively. The scaling request will be sent to the \ :java:ref:`DatacenterBroker`\  only if the under or overload condition is met, that depends of the implementation of the scaling mechanisms.

   The Vm to which this scaling object is related to, creates an \ :java:ref:`UpdateProcessingListener <Vm.addOnUpdateProcessingListener(EventListener)>`\  that will call this method to check if it time to perform an down or up scaling, every time the Vm processing is updated.

   :param evt: event information, including the current simulation time and the VM to be scaled
   :return: true if the Vm is over or underloaded and up or down scaling request was sent to the broker, false otherwise

setVm
^^^^^

.. java:method::  VmScaling setVm(Vm vm)
   :outertype: VmScaling

   Sets a \ :java:ref:`Vm`\  to this Load Balancer. The broker will call this Load Balancer in order to balance load when its Vm is over utilized.

   When the VmScaling is assigned to a Vm, the Vm sets itself to the VmScaling object, creating an association between the two objects.

   :param vm: the Vm to set

