.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms UtilizationHistory

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Objects

VmSelectionPolicyMaximumCorrelation
===================================

.. java:package:: org.cloudbus.cloudsim.selectionpolicies
   :noindex:

.. java:type:: public class VmSelectionPolicyMaximumCorrelation implements VmSelectionPolicy

   A VM selection policy that selects for migration the VM with the Maximum Correlation Coefficient (MCC) among a list of migratable VMs.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
VmSelectionPolicyMaximumCorrelation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSelectionPolicyMaximumCorrelation(VmSelectionPolicy fallbackPolicy)
   :outertype: VmSelectionPolicyMaximumCorrelation

   Instantiates a new PowerVmSelectionPolicyMaximumCorrelation.

   :param fallbackPolicy: the fallback policy

Methods
-------
getFallbackPolicy
^^^^^^^^^^^^^^^^^

.. java:method:: public VmSelectionPolicy getFallbackPolicy()
   :outertype: VmSelectionPolicyMaximumCorrelation

   Gets the fallback VM selection policy to be used when the Maximum Correlation policy doesn't have data to be computed.

   :return: the fallback policy

getVmToMigrate
^^^^^^^^^^^^^^

.. java:method:: @Override public Vm getVmToMigrate(Host host)
   :outertype: VmSelectionPolicyMaximumCorrelation

setFallbackPolicy
^^^^^^^^^^^^^^^^^

.. java:method:: public final void setFallbackPolicy(VmSelectionPolicy fallbackPolicy)
   :outertype: VmSelectionPolicyMaximumCorrelation

   Sets the fallback VM selection policy to be used when the Maximum Correlation policy doesn't have data to be computed.

   :param fallbackPolicy: the new fallback policy

