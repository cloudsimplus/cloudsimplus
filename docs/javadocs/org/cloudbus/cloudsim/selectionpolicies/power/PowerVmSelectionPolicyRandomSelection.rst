.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

PowerVmSelectionPolicyRandomSelection
=====================================

.. java:package:: org.cloudbus.cloudsim.selectionpolicies.power
   :noindex:

.. java:type:: public class PowerVmSelectionPolicyRandomSelection extends PowerVmSelectionPolicy

   A VM selection policy that randomly select VMs to migrate from a host. If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerVmSelectionPolicyRandomSelection
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmSelectionPolicyRandomSelection()
   :outertype: PowerVmSelectionPolicyRandomSelection

Methods
-------
getVmToMigrate
^^^^^^^^^^^^^^

.. java:method:: @Override public Vm getVmToMigrate(Host host)
   :outertype: PowerVmSelectionPolicyRandomSelection

