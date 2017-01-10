PowerModelCubic
===============

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public class PowerModelCubic implements PowerModel

   Implements a power model where the power consumption is the cube of the resource usage. If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov, Anton Beloglazov

Constructors
------------
PowerModelCubic
^^^^^^^^^^^^^^^

.. java:constructor:: public PowerModelCubic(double maxPower, double staticPowerPercent)
   :outertype: PowerModelCubic

   Instantiates a new power model cubic.

   :param maxPower: the max power
   :param staticPowerPercent: the static power percent

Methods
-------
getConstant
^^^^^^^^^^^

.. java:method:: protected double getConstant()
   :outertype: PowerModelCubic

   Gets the constant.

   :return: the constant

getMaxPower
^^^^^^^^^^^

.. java:method:: protected double getMaxPower()
   :outertype: PowerModelCubic

   Gets the max power.

   :return: the max power

getPower
^^^^^^^^

.. java:method:: @Override public double getPower(double utilization) throws IllegalArgumentException
   :outertype: PowerModelCubic

getStaticPower
^^^^^^^^^^^^^^

.. java:method:: protected double getStaticPower()
   :outertype: PowerModelCubic

   Gets the static power.

   :return: the static power

setConstant
^^^^^^^^^^^

.. java:method:: protected void setConstant(double constant)
   :outertype: PowerModelCubic

   Sets the constant.

   :param constant: the new constant

setMaxPower
^^^^^^^^^^^

.. java:method:: protected void setMaxPower(double maxPower)
   :outertype: PowerModelCubic

   Sets the max power.

   :param maxPower: the new max power

setStaticPower
^^^^^^^^^^^^^^

.. java:method:: protected void setStaticPower(double staticPower)
   :outertype: PowerModelCubic

   Sets the static power.

   :param staticPower: the new static power

