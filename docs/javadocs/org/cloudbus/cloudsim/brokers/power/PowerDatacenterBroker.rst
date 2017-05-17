.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

PowerDatacenterBroker
=====================

.. java:package:: org.cloudbus.cloudsim.brokers.power
   :noindex:

.. java:type:: public class PowerDatacenterBroker extends DatacenterBrokerSimple

   A power-aware \ :java:ref:`DatacenterBrokerSimple`\ . If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerDatacenterBroker
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerDatacenterBroker(CloudSim simulation)
   :outertype: PowerDatacenterBroker

   Instantiates a new PowerDatacenterBroker.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

Methods
-------
processVmCreateResponseFromDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean processVmCreateResponseFromDatacenter(SimEvent ev)
   :outertype: PowerDatacenterBroker

