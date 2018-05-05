.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: java.util Objects

PowerModelAbstract
==================

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public abstract class PowerModelAbstract implements PowerModel

   An abstract implementation of a \ :java:ref:`PowerModel`\ .

   :author: raysaoliveira

Methods
-------
getEnergyLinearInterpolation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time)
   :outertype: PowerModelAbstract

getHost
^^^^^^^

.. java:method:: @Override public Host getHost()
   :outertype: PowerModelAbstract

getPower
^^^^^^^^

.. java:method:: @Override public double getPower()
   :outertype: PowerModelAbstract

getPower
^^^^^^^^

.. java:method:: @Override public final double getPower(double utilization) throws IllegalArgumentException
   :outertype: PowerModelAbstract

getPowerInternal
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract double getPowerInternal(double utilization) throws IllegalArgumentException
   :outertype: PowerModelAbstract

   An internal method to be implemented by sub classes to get the power consumption for the current CPU utilization.

   The basic parameter validation is performed by the \ :java:ref:`getPower(double)`\  method.

   :param utilization: the utilization percentage (between [0 and 1]) of a resource that is critical for power consumption.
   :throws IllegalArgumentException: when the utilization percentage is not between [0 and 1]
   :return: the power consumption

setHost
^^^^^^^

.. java:method:: @Override public final void setHost(Host host)
   :outertype: PowerModelAbstract

