.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

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

   Gets the current power supply in Watts (w).

   :return: the power supply in Watts (w)

   **See also:** :java:ref:`.getPowerInKWatts()`

getPowerInKWatts
^^^^^^^^^^^^^^^^

.. java:method::  double getPowerInKWatts()
   :outertype: PowerAware

   Gets the current power supply in Kilowatts (kW).

   :return: the power supply Kilowatts (kW)

   **See also:** :java:ref:`.getPower()`

wattsSecToKWattsHour
^^^^^^^^^^^^^^^^^^^^

.. java:method:: static double wattsSecToKWattsHour(double power)
   :outertype: PowerAware

   Converts energy consumption from Watts-Second to Kilowatt-hour (kWh).

   :param power: the value in Watts-Second
   :return: the value converted to Kilowatt-hour (kWh)

