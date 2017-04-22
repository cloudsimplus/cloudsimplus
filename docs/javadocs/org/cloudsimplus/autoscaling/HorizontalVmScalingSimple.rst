.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Objects

.. java:import:: java.util.function Predicate

.. java:import:: java.util.function Supplier

HorizontalVmScalingSimple
=========================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public class HorizontalVmScalingSimple extends VmScalingAbstract implements HorizontalVmScaling

   A \ :java:ref:`HorizontalVmScaling`\  implementation that allows defining the conditions to identify an under or overloaded VM, based on any desired criteria, such as current RAM, CPU and/or Bandwidth utilization. A \ :java:ref:`DatacenterBroker`\  thus monitors the VMs that have an HorizontalVmScaling object in order to create or destroy VMs on demand.

   Thes conditions in fact have to be defined by the user of this class, by providing \ :java:ref:`Predicate`\ s using the \ :java:ref:`setUnderloadPredicate(Predicate)`\  and \ :java:ref:`setOverloadPredicate(Predicate)`\  methods.

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

getUnderloadPredicate
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Predicate<Vm> getUnderloadPredicate()
   :outertype: HorizontalVmScalingSimple

getVmSupplier
^^^^^^^^^^^^^

.. java:method:: @Override public Supplier<Vm> getVmSupplier()
   :outertype: HorizontalVmScalingSimple

requestScaling
^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean requestScaling(double time)
   :outertype: HorizontalVmScalingSimple

requestScalingIfPredicateMatch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean requestScalingIfPredicateMatch(double time)
   :outertype: HorizontalVmScalingSimple

setOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmScaling setOverloadPredicate(Predicate<Vm> predicate)
   :outertype: HorizontalVmScalingSimple

setUnderloadPredicate
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmScaling setUnderloadPredicate(Predicate<Vm> predicate)
   :outertype: HorizontalVmScalingSimple

setVmSupplier
^^^^^^^^^^^^^

.. java:method:: @Override public final HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier)
   :outertype: HorizontalVmScalingSimple

