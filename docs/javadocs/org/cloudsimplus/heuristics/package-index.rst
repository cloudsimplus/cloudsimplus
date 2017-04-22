org.cloudsimplus.heuristics
===========================

Provides a set of interfaces and classes to develop heuristics to find sub-optimal solutions for problems, considering some utility function that has to be minimized or maximized. Such a function is also called a fitness function and as higher is the fitness better the found solution is.

Different heuristics include \ `Simulated Annealing <http://en.wikipedia.org/wiki/Simulated_annealing>`_\ , \ `Tabu Search <http://en.wikipedia.org/wiki/Tabu_search>`_\  and \ `Ant Colony Optimization <http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms>`_\ .

The first introduced heuristic is the \ :java:ref:`org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing`\  that is used by a \ :java:ref:`org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic`\  to map Cloudlets to VMs.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudsimplus.heuristics

.. toctree::
   :maxdepth: 1

   CloudletToVmMappingHeuristic
   CloudletToVmMappingHeuristicNull
   CloudletToVmMappingSimulatedAnnealing
   CloudletToVmMappingSolution
   Heuristic
   HeuristicAbstract
   HeuristicNull
   HeuristicSolution
   HeuristicSolutionNull
   SimulatedAnnealing

