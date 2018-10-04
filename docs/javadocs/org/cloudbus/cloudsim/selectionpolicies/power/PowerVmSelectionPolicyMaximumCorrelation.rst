.. java:import:: org.apache.commons.math3.linear Array2DRowRealMatrix

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.util MathUtil

.. java:import:: org.cloudbus.cloudsim.vms UtilizationHistory

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util Map

PowerVmSelectionPolicyMaximumCorrelation
========================================

.. java:package:: org.cloudbus.cloudsim.selectionpolicies.power
   :noindex:

.. java:type:: public class PowerVmSelectionPolicyMaximumCorrelation extends PowerVmSelectionPolicy

   A VM selection policy that selects for migration the VM with the Maximum Correlation Coefficient (MCC) among a list of migratable VMs.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerVmSelectionPolicyMaximumCorrelation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmSelectionPolicyMaximumCorrelation(PowerVmSelectionPolicy fallbackPolicy)
   :outertype: PowerVmSelectionPolicyMaximumCorrelation

   Instantiates a new PowerVmSelectionPolicyMaximumCorrelation.

   :param fallbackPolicy: the fallback policy

Methods
-------
getCorrelationCoefficients
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> getCorrelationCoefficients(double[][] data)
   :outertype: PowerVmSelectionPolicyMaximumCorrelation

   Gets the correlation coefficients.

   :param data: the data
   :return: the correlation coefficients

getFallbackPolicy
^^^^^^^^^^^^^^^^^

.. java:method:: public PowerVmSelectionPolicy getFallbackPolicy()
   :outertype: PowerVmSelectionPolicyMaximumCorrelation

   Gets the fallback policy.

   :return: the fallback policy

getMinUtilizationHistorySize
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected int getMinUtilizationHistorySize(List<Vm> vmList)
   :outertype: PowerVmSelectionPolicyMaximumCorrelation

   Gets the min CPU utilization percentage history size between a list of VMs.

   :param vmList: the VM list
   :return: the min CPU utilization percentage history size of the VM list

getUtilizationMatrix
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double[][] getUtilizationMatrix(List<Vm> vmList)
   :outertype: PowerVmSelectionPolicyMaximumCorrelation

   Gets the CPU utilization percentage matrix for a given list of VMs.

   :param vmList: the VM list
   :return: the CPU utilization percentage matrix, where each line i is a VM and each column j is a CPU utilization percentage history for that VM.

getVmToMigrate
^^^^^^^^^^^^^^

.. java:method:: @Override public Vm getVmToMigrate(Host host)
   :outertype: PowerVmSelectionPolicyMaximumCorrelation

setFallbackPolicy
^^^^^^^^^^^^^^^^^

.. java:method:: public final void setFallbackPolicy(PowerVmSelectionPolicy fallbackPolicy)
   :outertype: PowerVmSelectionPolicyMaximumCorrelation

   Sets the fallback policy.

   :param fallbackPolicy: the new fallback policy

