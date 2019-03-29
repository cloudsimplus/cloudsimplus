.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

.. java:import:: java.util Objects

VmSelectionPolicyRandomSelection
================================

.. java:package:: org.cloudbus.cloudsim.selectionpolicies
   :noindex:

.. java:type:: public class VmSelectionPolicyRandomSelection implements VmSelectionPolicy

   A VM selection policy that randomly select VMs to migrate from a host. It uses a uniform Pseudo Random Number Generator (PRNG) as default to select VMs. If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
VmSelectionPolicyRandomSelection
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSelectionPolicyRandomSelection()
   :outertype: VmSelectionPolicyRandomSelection

   Creates a PowerVmSelectionPolicyRandomSelection using a uniform Pseudo Random Number Generator (PRNG) as default to select VMs to migrate.

VmSelectionPolicyRandomSelection
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSelectionPolicyRandomSelection(ContinuousDistribution rand)
   :outertype: VmSelectionPolicyRandomSelection

   Creates a PowerVmSelectionPolicyRandomSelection using a given Pseudo Random Number Generator (PRNG) to select VMs to migrate.

   :param rand: a Pseudo Random Number Generator (PRNG) to randomly select VMs to migrate.

Methods
-------
getVmToMigrate
^^^^^^^^^^^^^^

.. java:method:: @Override public Vm getVmToMigrate(Host host)
   :outertype: VmSelectionPolicyRandomSelection

