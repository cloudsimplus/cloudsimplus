/// Provides a set of interfaces and classes to develop heuristics to
/// find suboptimal solutions for problems, considering some
/// utility function that has to be minimized or maximized.
/// Such a function is also called a fitness function, and as the higher the fitness,
/// the better the found solution is.
///
/// Different heuristics include
/// [Simulated Annealing](http://en.wikipedia.org/wiki/Simulated_annealing),
/// [Tabu Search](http://en.wikipedia.org/wiki/Tabu_search) and
/// [Ant Colony Optimization](http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms).
///
/// The first introduced heuristic is the [org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing]
/// that is used by a [org.cloudsimplus.brokers.DatacenterBrokerHeuristic]
/// to map Cloudlets to VMs.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.heuristics;
