.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

PowerModel
==========

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public interface PowerModel

   The PowerModel interface needs to be implemented in order to provide a model of power consumption of hosts, depending on utilization of a critical system component, such as CPU. The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`PowerModel.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`PowerModel`\  variables.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Fields
------
NULL
^^^^

.. java:field::  PowerModel NULL
   :outertype: PowerModel

   A property that implements the Null Object Design Pattern for \ :java:ref:`PowerHost`\  objects.

Methods
-------
getHost
^^^^^^^

.. java:method::  PowerHost getHost()
   :outertype: PowerModel

getPower
^^^^^^^^

.. java:method::  double getPower(double utilization) throws IllegalArgumentException
   :outertype: PowerModel

   Gets power consumption of the Power Model, according to the utilization percentage of a critical resource, such as CPU.

   :param utilization: the utilization percentage (between [0 and 1]) of a resource that is critical for power consumption.
   :throws IllegalArgumentException: when the utilization percentage is not between [0 and 1]
   :return: the power consumption

setHost
^^^^^^^

.. java:method::  void setHost(PowerHost host)
   :outertype: PowerModel

