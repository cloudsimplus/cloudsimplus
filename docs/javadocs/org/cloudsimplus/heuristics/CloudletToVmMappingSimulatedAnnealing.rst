.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

CloudletToVmMappingSimulatedAnnealing
=====================================

.. java:package:: org.cloudsimplus.heuristics
   :noindex:

.. java:type:: public class CloudletToVmMappingSimulatedAnnealing extends SimulatedAnnealing<CloudletToVmMappingSolution> implements CloudletToVmMappingHeuristic

   A heuristic that uses \ `Simulated Annealing <http://en.wikipedia.org/wiki/Simulated_annealing>`_\  to find a sub-optimal mapping among a set of Cloudlets and VMs in order to reduce the number of idle or overloaded Vm Pe's.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CloudletToVmMappingSimulatedAnnealing
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletToVmMappingSimulatedAnnealing(double initialTemperature, ContinuousDistribution random)
   :outertype: CloudletToVmMappingSimulatedAnnealing

   Creates a new Simulated Annealing Heuristic for solving Cloudlets to Vm's mapping.

   :param initialTemperature: the system initial temperature
   :param random: a random number generator

   **See also:** :java:ref:`.setColdTemperature(double)`, :java:ref:`.setCoolingRate(double)`

Methods
-------
createNeighbor
^^^^^^^^^^^^^^

.. java:method:: @Override public CloudletToVmMappingSolution createNeighbor(CloudletToVmMappingSolution source)
   :outertype: CloudletToVmMappingSimulatedAnnealing

getCloudletList
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> getCloudletList()
   :outertype: CloudletToVmMappingSimulatedAnnealing

getInitialSolution
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public CloudletToVmMappingSolution getInitialSolution()
   :outertype: CloudletToVmMappingSimulatedAnnealing

getVmList
^^^^^^^^^

.. java:method:: @Override public List<Vm> getVmList()
   :outertype: CloudletToVmMappingSimulatedAnnealing

setCloudletList
^^^^^^^^^^^^^^^

.. java:method:: @Override public void setCloudletList(List<Cloudlet> cloudletList)
   :outertype: CloudletToVmMappingSimulatedAnnealing

setVmList
^^^^^^^^^

.. java:method:: @Override public void setVmList(List<Vm> vmList)
   :outertype: CloudletToVmMappingSimulatedAnnealing

