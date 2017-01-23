.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util.function Predicate

.. java:import:: java.util.function Supplier

HorizontalVmScaling
===================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public interface HorizontalVmScaling extends VmScaling

   A Vm \ `Horizontal Scaling <https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling>`_\  mechanism used by a \ :java:ref:`DatacenterBroker`\  to dynamically create or destroy VMs according to the arrival or termination of Cloudlets, in order to enable load balancing.

   Since Cloudlets can be created and submitted to a broker in runtime, the number of arrived Cloudlets can be to much to existing VMs, requiring the creation of new VMs to balance the load. Accordingly, as Cloudlets terminates, some created VMs may not be required anymore and should be destroyed.

   A HorizontalVmScaling implementation performs such up or down scaling by creating or destroying VMs are needed.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  HorizontalVmScaling NULL
   :outertype: HorizontalVmScaling

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`HorizontalVmScaling`\  objects.

Methods
-------
getVmSupplier
^^^^^^^^^^^^^

.. java:method::  Supplier<Vm> getVmSupplier()
   :outertype: HorizontalVmScaling

   Gets a \ :java:ref:`Supplier`\  that will be used to create VMs when the Load Balancer detects that the current Broker's VMs are overloaded.

requestUpScalingIfOverloaded
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  boolean requestUpScalingIfOverloaded(double time)
   :outertype: HorizontalVmScaling

   Requests a horizontal scale if the Vm is overloaded, according to the \ :java:ref:`getOverloadPredicate()`\  predicate. The scaling is performed by creating a new Vm using the \ :java:ref:`getVmSupplier()`\  method and submitting it to the broker.

   The time interval in which it will be checked if the Vm is overloaded depends on the \ :java:ref:`Datacenter.getSchedulingInterval()`\  value. Make sure to set such a value to enable the periodic overload verification.

   The method will check the need to create a new
   VM at the time interval defined by the .
   A VM creation request is only sent when the VM is overloaded and
   new Cloudlets were submitted to the broker.

   :param time: current simulation time
   :return: {@inheritDoc}

setVmSupplier
^^^^^^^^^^^^^

.. java:method::  HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier)
   :outertype: HorizontalVmScaling

   Sets a \ :java:ref:`Supplier`\  that will be used to create VMs when the Load Balancer detects that the Broker's VMs are overloaded.

   :param supplier: the supplier to set

