.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: java.util Objects

.. java:import:: java.util.function Function

UtilizationModelDynamic
=======================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelDynamic extends UtilizationModelAbstract

   A Cloudlet \ :java:ref:`UtilizationModel`\  that allows to increase the utilization of the related resource along the simulation time. It accepts a Lambda Expression that defines how the utilization increment must behave. By this way, the class enables the developer to define such a behaviour when instantiating objects of this class.

   For instance, it is possible to use the class to arithmetically or geometrically increment resource usage, but any kind of increment as logarithmic or exponential is possible. For more details, see the \ :java:ref:`setUtilizationUpdateFunction(Function)`\ .

   :author: Manoel Campos da Silva Filho

Constructors
------------
UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic()
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic with no initial utilization and resource utilization unit defined in \ :java:ref:`Unit.PERCENTAGE`\ .

   The utilization will not be dynamically incremented
   until an increment function is defined by the .

   **See also:** :java:ref:`.setUtilizationUpdateFunction(Function)`

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(Unit unit)
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic with no initial utilization and resource utilization \ :java:ref:`Unit`\  be defined according to the given parameter.

   The utilization will not be dynamically incremented
   until that an increment function is defined by the .

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(double initialUtilizationPercent)
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic that the initial resource utilization will be defined according to the given parameter and the \ :java:ref:`Unit`\  will be set as \ :java:ref:`Unit.PERCENTAGE`\ .

   The utilization will not be dynamically incremented
   until that an increment function is defined by the .

   :param initialUtilizationPercent: the initial percentage of resource utilization

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(Unit unit, double initialUtilization)
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic that the initial resource utilization and the \ :java:ref:`Unit`\  will be defined according to the given parameters.

   The utilization will not be dynamically incremented
   until that an increment function is defined by the .

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)
   :param initialUtilization: the initial resource utilization, that the unit depends on the \ ``unit``\  parameter

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(Unit unit, double initialUtilization, double maxResourceUtilization)
   :outertype: UtilizationModelDynamic

   Creates a UtilizationModelDynamic that the initial resource utilization, max resource utilization and the \ :java:ref:`Unit`\  will be defined according to the given parameters.

   The utilization will not be dynamically incremented
   until that an increment function is defined by the .

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)
   :param initialUtilization: the initial resource utilization, that the unit depends on the \ ``unit``\  parameter
   :param maxResourceUtilization: the maximum resource utilization

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: @SuppressWarnings protected UtilizationModelDynamic(UtilizationModelDynamic source)
   :outertype: UtilizationModelDynamic

   A copy constructor that creates a read-only UtilizationModelDynamic based on a source object.

   :param source: the source UtilizationModelDynamic to create an instance from

UtilizationModelDynamic
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelDynamic(UtilizationModelDynamic source, double initialUtilization)
   :outertype: UtilizationModelDynamic

   A copy constructor that creates a UtilizationModelDynamic based on a source object.

   :param source: the source UtilizationModelDynamic to create an instance from
   :param initialUtilization: the initial resource utilization (in the same unit of the given UtilizationModelDynamic instance)

Methods
-------
getMaxResourceUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaxResourceUtilization()
   :outertype: UtilizationModelDynamic

   Gets the maximum amount of resource that will be used.

   Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   :return: the maximum resource utilization

getTimeSpan
^^^^^^^^^^^

.. java:method:: public double getTimeSpan()
   :outertype: UtilizationModelDynamic

   Gets the time difference from the current simulation time to the last time the resource utilization was updated.

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelDynamic

   {@inheritDoc}

   It will automatically increment the \ :java:ref:`getUtilization()`\  by applying the \ :java:ref:`increment function <setUtilizationUpdateFunction(Function)>`\ .

   :param time: {@inheritDoc}
   :return: {@inheritDoc}

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization()
   :outertype: UtilizationModelDynamic

setMaxResourceUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final UtilizationModelDynamic setMaxResourceUtilization(double maxResourceUsagePercentage)
   :outertype: UtilizationModelDynamic

   Sets the maximum amount of resource that will be used.

   Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   :param maxResourceUsagePercentage: the maximum resource usage

setUtilizationUpdateFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final UtilizationModelDynamic setUtilizationUpdateFunction(Function<UtilizationModelDynamic, Double> utilizationUpdateFunction)
   :outertype: UtilizationModelDynamic

   Sets the function defining how the resource utilization will be incremented or decremented along the time.

   Such a function must require one \ :java:ref:`UtilizationModelDynamic`\  parameter and return the new resource utilization. When this function is called internally by this \ ``UtilizationModel``\ , it receives a read-only \ :java:ref:`UtilizationModelDynamic`\  instance and allow the developer using this \ ``UtilizationModel``\  to define how the utilization must be updated.

   For instance, to define an arithmetic increment, a Lambda function to be given to this setter could be defined as below:

   \ ``um -> um.getUtilization() + um.getTimeSpan()*0.1``\

   Considering the \ ``UtilizationModel``\  \ :java:ref:`Unit`\  was defined in \ :java:ref:`Unit.PERCENTAGE`\ , such a Lambda Expression will increment the usage in 10% for each second that has passed since the last time the utilization was computed.

   The value returned by the given Lambda Expression will be automatically validated to avoid negative utilization or utilization over 100% (when the \ ``UtilizationModel``\  \ :java:ref:`unit <getUnit()>`\  is defined in percentage). The function would be defined to decrement the utilization along the time, by just changing the plus to a minus signal.

   Defining a geometric progression for the resource utilization is as simple as changing the plus signal to a multiplication signal.

   :param utilizationUpdateFunction: the utilization increment function to set, that will receive the UtilizationModel instance and must return the new utilization value based on the previous utilization.

