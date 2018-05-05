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

.. java:constructor:: public PowerModelSimple(double maxPower, double staticPowerPercent, UnaryOperator<Double> powerIncrementFunction)
   :outertype: PowerModelSimple

   Instantiates a PowerModelSimple.

   :param maxPower: the max power that can be consumed in Watt-Second (Ws).
   :param staticPowerPercent: the static power usage percentage between 0 and 1.
   :param powerIncrementFunction: a function that defines how the power consumption increases along the time. This function receives the utilization percentage in scale from 0 to 100 and returns a factor representing how the power consumption will increase for the given utilization percentage. The function return is again a percentage value between [0 and 1].

Methods
-------
getConstant
^^^^^^^^^^^

.. java:method:: protected double getConstant()
   :outertype: PowerModelSimple

   Gets the constant which represents the power consumption for each fraction of resource used in Watt-Second (Ws).

   :return: the power consumption constant in Watt-Second (Ws)

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

   Gets the static power consumption in Watt-Second (Ws) that is not dependent of resource usage, according to the \ :java:ref:`getStaticPowerPercent()`\ . It is the amount of energy consumed even when the host is idle.

   :return: the static power usage in Watt-Second (Ws)

getStaticPowerPercent
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getStaticPowerPercent()
   :outertype: PowerModelSimple

   Gets the static power consumption percentage (between 0 and 1) that is not dependent of resource usage. It is the amount of energy consumed even when the host is idle.

   :return: the static power consumption percentage (between 0 and 1)

