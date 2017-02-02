.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: java.util Objects

.. java:import:: java.util.function BiFunction

UtilizationModelDynamic
=======================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelDynamic extends UtilizationModelAbstract

   A Cloudlet \ :java:ref:`UtilizationModel`\  that allows to increases the utilization of the related resource along the simulation time. It accepts a Lambda Expression that defines how the utilization increment must behave. By this way, the class enables the developer to define such a behaviour when instantiating objects of this class.

   For instance, it is possible to use the class to arithmetically or geometrically increment resource usage, but any kind of increment as logarithmic or exponential is possible. For more details, see the \ :java:ref:`setUtilizationIncrementFunction(BiFunction)`\ .

   :author: Manoel Campos da Silva Filho

Constructors
------------
UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic()
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic with no initial utilization and resource utilization unit defined in \ :java:ref:`Unit.PERCENTAGE`\ .

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(Unit unit)
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic with no initial utilization and resource utilization \ :java:ref:`Unit`\  be defined according to the given parameter.

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(double currentUtilization)
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic that the initial resource utilization will be defined according to the given parameter and the \ :java:ref:`Unit`\  will be set as \ :java:ref:`Unit.PERCENTAGE`\ .

   :param currentUtilization: the initial percentage of resource utilization

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(Unit unit, double currentUtilization)
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic that the initial resource utilization and the \ :java:ref:`Unit`\  will be defined according to the given parameters.

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)
   :param currentUtilization: the initial of resource utilization, that the unit depends on the \ ``unit``\  parameter

Methods
-------
getInitialUtilization
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getInitialUtilization()
   :outertype: UtilizationModelDynamic

   Gets the initial utilization of resource that cloudlets using this UtilizationModel will require when they start to execute.

   Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   :return: the initial utilization

getMaxResourceUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaxResourceUtilization()
   :outertype: UtilizationModelDynamic

   Gets the maximum amount of resource that will be used.

   Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   :return: the maximum resource utilization

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelDynamic

getUtilizationIncrementFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public BiFunction<Double, Double, Double> getUtilizationIncrementFunction()
   :outertype: UtilizationModelDynamic

   Gets the function that defines how the resource utilization will be incremented along the time.

   :return: the utilization increment function

   **See also:** :java:ref:`.setUtilizationIncrementFunction(BiFunction)`

setInitialUtilization
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final UtilizationModelDynamic setInitialUtilization(double currentUtilization)
   :outertype: UtilizationModelDynamic

   Sets the initial utilization of resource that cloudlets using this UtilizationModel will require when they start to execute.

   Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   :param currentUtilization: initial resource utilization

setMaxResourceUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final UtilizationModelDynamic setMaxResourceUtilization(double maxResourceUsagePercentage)
   :outertype: UtilizationModelDynamic

   Sets the maximum amount of resource of resource that will be used.

   Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   :param maxResourceUsagePercentage: the maximum resource usage

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel setSimulation(Simulation simulation)
   :outertype: UtilizationModelDynamic

setUtilizationIncrementFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final UtilizationModelDynamic setUtilizationIncrementFunction(BiFunction<Double, Double, Double> utilizationUpdateFunction)
   :outertype: UtilizationModelDynamic

   Sets the function that defines how the resource utilization will be incremented along the time.

   Such a function must be one with two \ ``Double``\  parameters, that when called internally by this UtilizationModel will receive the \ ``timeSpan``\  and the \ ``currentUtilization``\ , that respectively represents the time interval that has passed since the last time the \ :java:ref:`getUtilization(double)`\  method was called and the \ :java:ref:`initial resource utilization <getInitialUtilization()>`\  (that may be a percentage or absolute value, depending on the \ :java:ref:`getUnit()`\ ).

   Such parameters that will be passed to the Lambda function given to this setter must be used by the developer to define how the utilization will be incremented. For instance, to define an arithmetic increment, a Lambda function to be given to this setter could be as below:

   \ ``(timeSpan, currentUtilization) -> currentUtilization + (0.1 * timeSpan)``\

   Considering that the UtilizationModel \ :java:ref:`Unit`\  was defined in \ :java:ref:`Unit.PERCENTAGE`\ , such an Lambda Expression will increment the usage in 10% for each second that has passed since the last time the \ :java:ref:`getUtilization(double)`\  was called.

   The value returned by the given Lambda Expression will be automatically validated to avoid negative utilization or utilization over 100% (when the UtilizationModel \ :java:ref:`unit <getUnit()>`\  is defined in percentage).

   Defining a geometric progression for the resource utilization is as simple as changing the plus signal to a multiplication signal.

   :param utilizationUpdateFunction: the utilization increment function to set

