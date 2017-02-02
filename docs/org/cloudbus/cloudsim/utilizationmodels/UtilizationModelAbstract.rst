.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: java.util Objects

UtilizationModelAbstract
========================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public abstract class UtilizationModelAbstract implements UtilizationModel

   An abstract implementation of \ :java:ref:`UtilizationModel`\ .

   :author: Manoel Campos da Silva Filho

Fields
------
ALMOST_ZERO
^^^^^^^^^^^

.. java:field:: public static final double ALMOST_ZERO
   :outertype: UtilizationModelAbstract

   A constant which indicates that values lower or equal to this value will be considered as zero.

Constructors
------------
UtilizationModelAbstract
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelAbstract()
   :outertype: UtilizationModelAbstract

UtilizationModelAbstract
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelAbstract(Unit unit)
   :outertype: UtilizationModelAbstract

Methods
-------
getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: UtilizationModelAbstract

getUnit
^^^^^^^

.. java:method:: @Override public Unit getUnit()
   :outertype: UtilizationModelAbstract

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization()
   :outertype: UtilizationModelAbstract

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel setSimulation(Simulation simulation)
   :outertype: UtilizationModelAbstract

setUnit
^^^^^^^

.. java:method:: protected final UtilizationModel setUnit(Unit unit)
   :outertype: UtilizationModelAbstract

   Sets the \ :java:ref:`Unit`\  in which the resource utilization is defined.

   :param unit: \ :java:ref:`Unit`\  to set

validateUtilizationField
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void validateUtilizationField(String fieldName, double fieldValue)
   :outertype: UtilizationModelAbstract

   Checks if a given field has a valid value, considering that the minimum value is zero.

   :param fieldName: the name of the field to display at the Exception when the value is invalid
   :param fieldValue: the current value of the field

validateUtilizationField
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void validateUtilizationField(String fieldName, double fieldValue, double minValue)
   :outertype: UtilizationModelAbstract

