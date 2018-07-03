.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util Objects

.. java:import:: java.util.function Predicate

.. java:import:: java.util.function Supplier

HorizontalVmScalingSimple
=========================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public class HorizontalVmScalingSimple extends VmScalingAbstract implements HorizontalVmScaling

   A \ :java:ref:`HorizontalVmScaling`\  implementation that allows defining the condition to identify an overloaded VM, based on any desired criteria, such as current RAM, CPU and/or Bandwidth utilization. A \ :java:ref:`DatacenterBroker`\  monitors the VMs that have an HorizontalVmScaling object in order to create or destroy VMs on demand.

   The overload condition has to be defined by providing a \ :java:ref:`Predicate`\  using the \ :java:ref:`setOverloadPredicate(Predicate)`\  method. Check the \ :java:ref:`HorizontalVmScaling`\  documentation for details on how to enable horizontal down scaling using the \ :java:ref:`DatacenterBroker`\ .

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`HorizontalVmScaling`

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

getVmSupplier
^^^^^^^^^^^^^

.. java:method:: @Override public Supplier<Vm> getVmSupplier()
   :outertype: HorizontalVmScalingSimple

requestUpScaling
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean requestUpScaling(double time)
   :outertype: HorizontalVmScalingSimple

requestUpScalingIfPredicateMatches
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt)
   :outertype: HorizontalVmScalingSimple

setOverloadPredicate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmScaling setOverloadPredicate(Predicate<Vm> predicate)
   :outertype: HorizontalVmScalingSimple

setVmSupplier
^^^^^^^^^^^^^

.. java:method:: @Override public final HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier)
   :outertype: HorizontalVmScalingSimple

