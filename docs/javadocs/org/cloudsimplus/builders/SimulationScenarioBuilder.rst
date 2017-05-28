.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.core CloudSim

SimulationScenarioBuilder
=========================

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class SimulationScenarioBuilder

   An builder to help getting instance of other CloudSim object builders.

   :author: Manoel Campos da Silva Filho

Constructors
------------
SimulationScenarioBuilder
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SimulationScenarioBuilder(CloudSim simulation)
   :outertype: SimulationScenarioBuilder

Methods
-------
getBrokerBuilder
^^^^^^^^^^^^^^^^

.. java:method:: public BrokerBuilder getBrokerBuilder()
   :outertype: SimulationScenarioBuilder

getDatacenterBuilder
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder getDatacenterBuilder()
   :outertype: SimulationScenarioBuilder

getFirstHostFromFirstDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Host getFirstHostFromFirstDatacenter()
   :outertype: SimulationScenarioBuilder

getFirstVmFromFirstBroker
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Vm getFirstVmFromFirstBroker()
   :outertype: SimulationScenarioBuilder

getHostOfDatacenter
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Host getHostOfDatacenter(int hostIndex, int datacenterIndex)
   :outertype: SimulationScenarioBuilder

getSimulation
^^^^^^^^^^^^^

.. java:method:: public CloudSim getSimulation()
   :outertype: SimulationScenarioBuilder

getVmFromBroker
^^^^^^^^^^^^^^^

.. java:method:: public Vm getVmFromBroker(int vmIndex, int brokerIndex)
   :outertype: SimulationScenarioBuilder

