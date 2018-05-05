.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.resources Pe

PowerAware
==========

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public interface PowerAware

   An interface for power-aware components such as \ :java:ref:`Datacenter`\  and \ :java:ref:`PowerModel`\ .

   :author: Manoel Campos da Silva Filho

Methods
-------
getPower
^^^^^^^^

.. java:method::  double getPower()
   :outertype: PowerAware

   Gets the current power consumption in Watt-Second (Ws). For this moment, it only computes the power consumed by \ :java:ref:`Pe`\ s.

   :return: the power consumption in Watt-Second (Ws)

   **See also:** :java:ref:`.getPowerInKWattsHour()`

getPowerInKWattsHour
^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getPowerInKWattsHour()
   :outertype: PowerAware

   Gets the current power consumption in Kilowatt-hour (kWh). For this moment, it only computes the power consumed by \ :java:ref:`Pe`\ s.

   :return: the power consumption Kilowatt-hour (kWh)

   **See also:** :java:ref:`.getPower()`

wattsSecToKWattsHour
^^^^^^^^^^^^^^^^^^^^

.. java:method:: static double wattsSecToKWattsHour(double power)
   :outertype: PowerAware

   Converts from Watts-Second to Kilowatt-hour (kWh).

   :param power: the value in Watts-Second
   :return: the value converted to Kilowatt-hour (kWh)

