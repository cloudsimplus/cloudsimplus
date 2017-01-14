.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts HostDynamicWorkload

.. java:import:: org.cloudbus.cloudsim.hosts HostStateHistoryEntry

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

PowerHost
=========

.. java:package:: org.cloudbus.cloudsim.hosts.power
   :noindex:

.. java:type:: public interface PowerHost extends HostDynamicWorkload

   An interface to be implemented by power-aware Host classes. The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`PowerHost.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`PowerHost`\  variables.

   :author: Manoel Campos da Silva Filho

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

   Gets the power consumption of the host. For this moment it only computes the power consumed by PEs.

   :return: the power consumption

getPowerModel
^^^^^^^^^^^^^

.. java:method::  PowerModel getPowerModel()
   :outertype: PowerHost

   Gets the power model.

   :return: the power model

setPowerModel
^^^^^^^^^^^^^

.. java:method::  PowerHost setPowerModel(PowerModel powerModel)
   :outertype: PowerHost

   Sets the power model.

   :param powerModel: the new power model

