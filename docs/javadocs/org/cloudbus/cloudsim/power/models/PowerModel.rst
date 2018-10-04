.. java:import:: org.cloudbus.cloudsim.hosts Host

PowerModel
==========

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public interface PowerModel extends PowerAware

   Provides a model for power consumption of hosts, depending on utilization of a critical system component, such as CPU. This is the fundamental class to enable power-aware Hosts.
   However, a Host just provides power usage data if a PowerModel is set using the
   . The power consumption data is return in Watt-Second (Ws), which is just in a different scale than the usual Kilowatt-Hour (kWh).

   The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`PowerModel.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`PowerModel`\  variables.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  PowerModel NULL
   :outertype: PowerModel

   A property that implements the Null Object Design Pattern for \ :java:ref:`Host`\  objects.

Methods
-------
getEnergyLinearInterpolation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time)
   :outertype: PowerModel

   Gets an \ **estimation**\  of energy consumption using linear interpolation of the utilization change. \ **It's required to set a  in order to get power usage data.**\

   :param fromUtilization: the initial utilization percentage
   :param toUtilization: the final utilization percentage
   :param time: the time span between the initial and final utilization to compute the energy consumption
   :return: the \ **estimated**\  energy consumption

getHost
^^^^^^^

.. java:method::  Host getHost()
   :outertype: PowerModel

getMaxPower
^^^^^^^^^^^

.. java:method::  double getMaxPower()
   :outertype: PowerModel

   Gets the max power that can be consumed by the host in Watt-Second (Ws).

   :return: the max consumption power in Watt-Second (Ws)

getPower
^^^^^^^^

.. java:method::  double getPower(double utilization) throws IllegalArgumentException
   :outertype: PowerModel

   Gets power consumption in Watt-Second (Ws) of the Power Model, according to the utilization percentage of a critical resource, such as CPU.

   \ **The power consumption data is just available while the host is active.**\

   :param utilization: the utilization percentage (between [0 and 1]) of a resource that is critical for power consumption.
   :throws IllegalArgumentException: when the utilization percentage is not between [0 and 1]
   :return: the power consumption in Watt-Second (Ws)

setHost
^^^^^^^

.. java:method::  void setHost(Host host)
   :outertype: PowerModel

