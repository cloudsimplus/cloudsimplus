.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.hosts HostDynamicWorkloadSimple

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

PowerHostSimple
===============

.. java:package:: org.cloudbus.cloudsim.hosts.power
   :noindex:

.. java:type:: public class PowerHostSimple extends HostDynamicWorkloadSimple implements PowerHost

   A power-aware host which defines power consumption based on a \ :java:ref:`PowerModel`\ .

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012  <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerHostSimple
^^^^^^^^^^^^^^^

.. java:constructor:: public PowerHostSimple(long ram, long bw, long storage, List<Pe> peList)
   :outertype: PowerHostSimple

   Creates a PowerHost with the given parameters.

   :param ram: the RAM capacity in Megabytes
   :param bw: the Bandwidth (BW) capacity in Megabits/s
   :param storage: the storage capacity in Megabytes
   :param peList: the host's \ :java:ref:`Pe`\  list

PowerHostSimple
^^^^^^^^^^^^^^^

.. java:constructor:: @Deprecated public PowerHostSimple(int id, ResourceProvisioner ramProvisioner, ResourceProvisioner bwProvisioner, long storage, List<Pe> peList, VmScheduler vmScheduler, PowerModel powerModel)
   :outertype: PowerHostSimple

   Creates a PowerHost with the given parameters.

   :param id: the id of the host
   :param ramProvisioner: the ram provisioner with capacity in MEGABYTE
   :param bwProvisioner: the bw provisioner with capacity in Megabits/s
   :param storage: the storage capacity in MEGABYTE
   :param peList: the host's PEs list
   :param vmScheduler: the VM scheduler
   :param powerModel: the model of power consumption

Methods
-------
getEnergyLinearInterpolation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time)
   :outertype: PowerHostSimple

   Gets the energy consumption using linear interpolation of the utilization change.

   :param fromUtilization: the initial utilization percentage
   :param toUtilization: the final utilization percentage
   :param time: the time
   :return: the energy

getMaxPower
^^^^^^^^^^^

.. java:method:: @Override public double getMaxPower()
   :outertype: PowerHostSimple

   Gets the max power that can be consumed by the host.

   :return: the max consumption power

getPower
^^^^^^^^

.. java:method:: @Override public double getPower()
   :outertype: PowerHostSimple

getPower
^^^^^^^^

.. java:method:: protected double getPower(double utilization)
   :outertype: PowerHostSimple

   Gets the amount of power the Host consumes considering a given utilization percentage. For this moment it only computes the power consumed by PEs.

   :param utilization: the utilization percentage (between [0 and 1]) of a resource that is critical for power consumption
   :return: the power consumption

getPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public PowerModel getPowerModel()
   :outertype: PowerHostSimple

setPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public final PowerHost setPowerModel(PowerModel powerModel)
   :outertype: PowerHostSimple

