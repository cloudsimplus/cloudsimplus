.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Objects

.. java:import:: java.util.function Predicate

.. java:import:: java.util.function Supplier

HorizontalVmScalingSimple
=========================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class HorizontalVmScalingSimple implements HorizontalVmScaling

   A \ :java:ref:`HorizontalVmScaling`\  implementation that allows defining that the VMs from a given \ :java:ref:`DatacenterBroker`\  are overloaded or not based on the overall resource utilization of all such VMs.

   The condition in fact hsa to be defined by the user of this class, by providing a \ :java:ref:`Predicate`\  using the \ :java:ref:`setOverloadPredicate(Predicate)`\  method.

   :author: Manoel Campos da Silva Filho

Constructors
------------
HorizontalVmScalingSimple
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HorizontalVmScalingSimple()
   :outertype: HorizontalVmScalingSimple

Methods
-------
getOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Predicate<Vm> getOverloadPredicate()
   :outertype: HorizontalVmScalingSimple

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: HorizontalVmScalingSimple

getVmSupplier
^^^^^^^^^^^^^

.. java:method:: @Override public Supplier<Vm> getVmSupplier()
   :outertype: HorizontalVmScalingSimple

scaleIfOverloaded
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void scaleIfOverloaded(double time)
   :outertype: HorizontalVmScalingSimple

   {@inheritDoc}

   The method will check the need to create a new
   VM at the time interval defined by the .
   A VM creation request is only sent when the VM is overloaded and
   new Cloudlets were submitted to the broker.

   :param time: {@inheritDoc}

setOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final HorizontalVmScaling setOverloadPredicate(Predicate<Vm> predicate)
   :outertype: HorizontalVmScalingSimple

setVm
^^^^^

.. java:method:: @Override public VmScaling setVm(Vm vm)
   :outertype: HorizontalVmScalingSimple

setVmSupplier
^^^^^^^^^^^^^

.. java:method:: @Override public final VmScaling setVmSupplier(Supplier<Vm> supplier)
   :outertype: HorizontalVmScalingSimple

