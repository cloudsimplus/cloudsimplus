.. java:import:: org.cloudbus.cloudsim.hosts HostDynamicWorkload

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.resources Pe

PowerHost
=========

.. java:package:: org.cloudbus.cloudsim.hosts.power
   :noindex:

.. java:type:: public interface PowerHost extends HostDynamicWorkload

   An interface to be implemented by power-aware Host classes, defining power consumption based on a \ :java:ref:`PowerModel`\ .

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  PowerHost NULL
   :outertype: PowerHost

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`PowerHost`\  objects.

Methods
-------
getEnergyLinearInterpolation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time)
   :outertype: PowerHost

   Gets the energy consumption using linear interpolation of the utilization change.

   :param fromUtilization: the initial utilization percentage
   :param toUtilization: the final utilization percentage
   :param time: the time
   :return: the energy

getMaxPower
^^^^^^^^^^^

.. java:method::  double getMaxPower()
   :outertype: PowerHost

   Gets the max power that can be consumed by the host.

   :return: the max power

getPower
^^^^^^^^

.. java:method::  double getPower()
   :outertype: PowerHost

   Gets the current power consumption of the host. For this moment, it only computes the power consumed by \ :java:ref:`Pe`\ s.

   :return: the power consumption

getPowerModel
^^^^^^^^^^^^^

.. java:method::  PowerModel getPowerModel()
   :outertype: PowerHost

   Gets the power model used by the host to define how it consumes power.

   :return: the power model

setPowerModel
^^^^^^^^^^^^^

.. java:method::  PowerHost setPowerModel(PowerModel powerModel)
   :outertype: PowerHost

   Sets the power model.

   :param powerModel: the new power model

