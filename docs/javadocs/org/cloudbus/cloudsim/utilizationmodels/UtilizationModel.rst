.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.vms Vm

UtilizationModel
================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public interface UtilizationModel

   The UtilizationModel interface needs to be implemented in order to provide a fine-grained control over resource usage by a Cloudlet. It also implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`UtilizationModel.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`UtilizationModel`\  variables.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  UtilizationModel NULL
   :outertype: UtilizationModel

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`UtilizationModel`\  objects using a Lambda Expression.

Methods
-------
getSimulation
^^^^^^^^^^^^^

.. java:method::  Simulation getSimulation()
   :outertype: UtilizationModel

   Gets the simulation that this UtilizationModel belongs to.

getUnit
^^^^^^^

.. java:method::  Unit getUnit()
   :outertype: UtilizationModel

   Gets the \ :java:ref:`Unit`\  in which the resource utilization is defined.

getUtilization
^^^^^^^^^^^^^^

.. java:method::  double getUtilization(double time)
   :outertype: UtilizationModel

   Gets the \ **expected**\  utilization of resource at a given simulation time. Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   It is an expected usage value because the actual  resource usage
   depends on the available  resource.

   :param time: the time to get the resource usage.
   :return: the resource utilization at the given time

   **See also:** :java:ref:`.getUnit()`

getUtilization
^^^^^^^^^^^^^^

.. java:method::  double getUtilization()
   :outertype: UtilizationModel

   Gets the \ **expected**\  utilization of resource at the current simulation time. Such a value can be a percentage in scale from [0 to 1] or an absolute value, depending on the \ :java:ref:`getUnit()`\ .

   It is an expected usage value because the actual  resource usage
   depends on the available  resource.

   :return: the current resource utilization

   **See also:** :java:ref:`.getUnit()`

setSimulation
^^^^^^^^^^^^^

.. java:method::  UtilizationModel setSimulation(Simulation simulation)
   :outertype: UtilizationModel

   Sets the simulation that this UtilizationModel belongs to.

   :param simulation: the Simulation instance to set

