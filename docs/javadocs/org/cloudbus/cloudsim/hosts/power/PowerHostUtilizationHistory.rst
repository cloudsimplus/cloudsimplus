.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.hosts HostDynamicWorkloadSimple

.. java:import:: org.cloudbus.cloudsim.vms.power PowerVm

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.util MathUtil

PowerHostUtilizationHistory
===========================

.. java:package:: org.cloudbus.cloudsim.hosts.power
   :noindex:

.. java:type:: public class PowerHostUtilizationHistory extends PowerHostSimple

   A \ :java:ref:`PowerHost`\  that stores its CPU utilization percentage history. The history is used by VM allocation and selection policies.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerHostUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerHostUtilizationHistory(long ram, long bw, long storage, List<Pe> peList)
   :outertype: PowerHostUtilizationHistory

   Creates a PowerHostUtilizationHistory.

   :param ram: the RAM capacity in Megabytes
   :param bw: the Bandwidth (BW) capacity in Megabits/s
   :param storage: the storage capacity in Megabytes
   :param peList: the host's \ :java:ref:`Pe`\  list

PowerHostUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: @Deprecated public PowerHostUtilizationHistory(int id, ResourceProvisioner ramProvisioner, ResourceProvisioner bwProvisioner, long storage, List<Pe> peList, VmScheduler vmScheduler, PowerModel powerModel)
   :outertype: PowerHostUtilizationHistory

   Creates a PowerHostUtilizationHistory with the given parameters.

   :param id: the host id
   :param ramProvisioner: the ram provisioner with capacity in MEGABYTE
   :param bwProvisioner: the bw provisioner with capacity in Megabits/s
   :param storage: the storage capacity in MEGABYTE
   :param peList: the host's PEs list
   :param vmScheduler: the vm scheduler
   :param powerModel: the power consumption model

Methods
-------
getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double[] getUtilizationHistory()
   :outertype: PowerHostUtilizationHistory

   Gets the host CPU utilization percentage history (between [0 and 1], where 1 is 100%).

