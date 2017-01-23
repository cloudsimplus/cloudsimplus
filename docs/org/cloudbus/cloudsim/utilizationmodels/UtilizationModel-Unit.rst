.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.vms Vm

UtilizationModel.Unit
=====================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type::  enum Unit
   :outertype: UtilizationModel

   Defines the unit of the resource utilization.

Enum Constants
--------------
ABSOLUTE
^^^^^^^^

.. java:field:: public static final UtilizationModel.Unit ABSOLUTE
   :outertype: UtilizationModel.Unit

   Indicate that the resource utilization is defined in absolute values.

PERCENTAGE
^^^^^^^^^^

.. java:field:: public static final UtilizationModel.Unit PERCENTAGE
   :outertype: UtilizationModel.Unit

   Indicate that the resource utilization is defined in percentage values in scale from 0 to 1 (where 1 is 100%).

