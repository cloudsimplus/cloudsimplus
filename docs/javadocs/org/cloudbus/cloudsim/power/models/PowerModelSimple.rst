.. java:import:: java.util Objects

.. java:import:: java.util.function UnaryOperator

PowerModelSimple
================

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public class PowerModelSimple extends PowerModelAbstract

   A power model where the power consumption is defined by a \ :java:ref:`UnaryOperator`\  function given as parameter to the constructor. This way, the user can define how the power consumption increases along the time without requiring to create a new class for it.

   However, specific classes that implement well known models are provided, such as \ :java:ref:`PowerModelLinear`\ , \ :java:ref:`PowerModelSquare`\ , \ :java:ref:`PowerModelCubic`\  and \ :java:ref:`PowerModelSqrt`\ .

   :author: Manoel Campos da Silva Filho

Constructors
------------
PowerModelSimple
^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerModelSimple(double maxPower, double staticPowerPercent, UnaryOperator<Double> powerFunction)
   :outertype: PowerModelSimple

   Instantiates a PowerModelSimple.

   :param maxPower: the max power that can be supplied in Watts (W).
   :param staticPowerPercent: the static power supply percentage between [0 and 1].
   :param powerFunction: A function defining how the power supply is computed based on the CPU utilization. When called, this function receives the CPU utilization percentage in scale from [0 to 100] and must return the base power supply for that CPU utilization. The function is only accountable to compute the base power supply because the total power depends on other factors such as the \ :java:ref:`static power <getStaticPower()>`\  supplied by the Host, independent of its CPU usage.

Methods
-------
getConstant
^^^^^^^^^^^

.. java:method:: protected double getConstant()
   :outertype: PowerModelSimple

   Gets the constant which represents the power supply for each fraction of resource used in Watts (W).

   :return: the power supply constant in Watts (W)

getMaxPower
^^^^^^^^^^^

.. java:method:: @Override public double getMaxPower()
   :outertype: PowerModelSimple

getPowerInternal
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double getPowerInternal(double utilization) throws IllegalArgumentException
   :outertype: PowerModelSimple

getStaticPower
^^^^^^^^^^^^^^

.. java:method:: public final double getStaticPower()
   :outertype: PowerModelSimple

   Gets the static power supply in Watts that is not dependent of resource usage, according to the \ :java:ref:`getStaticPowerPercent()`\ . It is the amount of power supplied even when the host is idle.

   :return: the static power supply in Watts (W)

getStaticPowerPercent
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getStaticPowerPercent()
   :outertype: PowerModelSimple

   Gets the static power supply percentage (between 0 and 1) that is not dependent of resource usage. It is the percentage of power supplied even when the host is idle.

   :return: the static power supply percentage (between 0 and 1)

