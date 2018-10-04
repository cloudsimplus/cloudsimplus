.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.heuristics CloudletToVmMappingHeuristic

.. java:import:: org.cloudsimplus.heuristics CloudletToVmMappingSolution

.. java:import:: org.cloudsimplus.heuristics Heuristic

.. java:import:: java.util.stream Collectors

DatacenterBrokerHeuristic
=========================

.. java:package:: org.cloudbus.cloudsim.brokers
   :noindex:

.. java:type:: public class DatacenterBrokerHeuristic extends DatacenterBrokerSimple

   A simple implementation of \ :java:ref:`DatacenterBroker`\  that uses some heuristic to get a suboptimal mapping among submitted cloudlets and Vm's. Such heuristic can be, for instance, the \ :java:ref:`org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing`\  that implements a Simulated Annealing algorithm. The Broker then places the submitted Vm's at the first Datacenter found. If there isn't capacity in that one, it will try the other ones.

   :author: Manoel Campos da Silva Filho

Constructors
------------
DatacenterBrokerHeuristic
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterBrokerHeuristic(CloudSim simulation)
   :outertype: DatacenterBrokerHeuristic

   Creates a new DatacenterBroker object.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

   **See also:** :java:ref:`.setHeuristic(CloudletToVmMappingHeuristic)`

Methods
-------
defaultVmMapper
^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm defaultVmMapper(Cloudlet cloudlet)
   :outertype: DatacenterBrokerHeuristic

getHeuristic
^^^^^^^^^^^^

.. java:method:: public Heuristic<CloudletToVmMappingSolution> getHeuristic()
   :outertype: DatacenterBrokerHeuristic

   :return: the heuristic used to find a sub-optimal mapping between Cloudlets and Vm's

requestDatacentersToCreateWaitingCloudlets
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void requestDatacentersToCreateWaitingCloudlets()
   :outertype: DatacenterBrokerHeuristic

setHeuristic
^^^^^^^^^^^^

.. java:method:: public DatacenterBrokerHeuristic setHeuristic(CloudletToVmMappingHeuristic heuristic)
   :outertype: DatacenterBrokerHeuristic

   Sets a heuristic to be used to find a sub-optimal mapping between Cloudlets and Vm's. The list of Cloudlets and Vm's to be used by the heuristic
   will be set automatically by the DatacenterBroker. Accordingly,
   the developer don't have to set these lists manually,
   once they will be overridden.

   The time taken to find a suboptimal mapping of Cloudlets to Vm's depends on the heuristic parameters that have to be set carefully.

   :param heuristic: the heuristic to be set
   :return: the DatacenterBrokerHeuristic instance

